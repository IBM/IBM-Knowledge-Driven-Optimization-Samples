package com.ibm.hrl.room_allocation;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.Duration;
import java.util.List;

import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.hrl.room_allocation.domain.EmpTeamFollowLeaderMove;
import com.ibm.hrl.room_allocation.domain.EmpTeamFollowLeaderMoveIteratorFactory;
import com.ibm.hrl.room_allocation.domain.Employee;
import com.ibm.hrl.room_allocation.domain.EmployeeInfo;
import com.ibm.hrl.room_allocation.domain.Floor;
import com.ibm.hrl.room_allocation.domain.FloorAssignment;
import com.ibm.hrl.room_allocation.domain.IRoomAllocation9;
import com.ibm.hrl.room_allocation.domain.RoomAllocation9AssignmentClass;
import com.ibm.hrl.room_allocation.domain.RoomAllocation9Employee;
import com.ibm.hrl.room_allocation.domain.ShadowFloorAssignment;
import com.ibm.hrl.room_allocation.domain.OfficeType;
import com.ibm.hrl.room_allocation.solver.RoomAllocationConstraintProvider;
import com.ibm.hrl.room_allocation.utils.RoomAllocationData;
import com.ibm.hrl.room_allocation.utils.RoomAllocationDataInflated;
import com.ibm.hrl.room_allocation.utils.RoomAllocationDataParametric;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class RoomAllocationMain {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoomAllocationMain.class);

	// *** made accessible as singleton
	public static IRoomAllocation9 problem;
	public static boolean inflated;
	public static boolean assignmentClass;

	public static Boolean customMoves;

	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers
				.newFor("RoomAllocationMain")
				.defaultFormatWidth(120)
				.terminalWidthDetection(false)
				.build()
				.description("Solve room-allocation problem");
		parser.addArgument("-i", "--inflated")
				.action(Arguments.storeTrue())
				.setDefault(false)
				.help("use room information consisting of number of seats rather than number of offices")
				.dest("inflated");
		parser.addArgument("-a", "--assignment-class")
				.action(Arguments.storeTrue())
				.setDefault(false)
				.help("use a separate class for assignments of employees to floors")
				.dest("assignment-class");
		parser.addArgument("-t", "--time")
				.type(Integer.class)
				.setDefault(30)
				.help("time limit (sec.)")
				.dest("time");
		// Not used, this is the default
//		parser.addArgument("-s", "--shadow-variables")
//				.action(Arguments.storeTrue())
//				.setDefault(false)
//				.help("use shadow variables to propagate assignments")
//				.dest("shadow-variables");
		parser.addArgument("-m", "--custom-moves")
				.action(Arguments.storeTrue())
				.setDefault(false)
				.help("use custom moves to propagate assignment")
				.dest("custom-moves");
		Namespace parsedArgs = null;
		try {
			parsedArgs = parser.parseArgs(args);
		} catch (ArgumentParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		inflated = parsedArgs.getBoolean("inflated");
		assignmentClass = parsedArgs.getBoolean("assignment-class");
		customMoves = parsedArgs.getBoolean("custom-moves");
		int time_limit = parsedArgs.getInt("time");

		Class<? extends IRoomAllocation9> problemClass = assignmentClass ? RoomAllocation9AssignmentClass.class
				: RoomAllocation9Employee.class;

		// Load the problem
		problem = inflated
				? RoomAllocationDataInflated.generateData()
				: (RoomAllocationDataParametric.generateData(
						assignmentClass ? EmployeeInfo.class : Employee.class,
						problemClass));
//		System.out.println("Problem: " + problem);

		if (customMoves) {
		}

		SolverConfig config = new SolverConfig()
				.withSolutionClass(problemClass);
		if (assignmentClass)
			if (customMoves) {
				// use custom moves for teams
				MoveIteratorFactoryConfig empTeamFollowLeaderMoveIteratorFactoryConfig = new MoveIteratorFactoryConfig();
				empTeamFollowLeaderMoveIteratorFactoryConfig.setMoveIteratorFactoryClass(EmpTeamFollowLeaderMoveIteratorFactory.class);

				config = config
						.withEntityClasses(FloorAssignment.class)
						.withPhaseList(List.of(
								new ConstructionHeuristicPhaseConfig(),
								new LocalSearchPhaseConfig()
										.withMoveSelectorConfig(new UnionMoveSelectorConfig(List.of(
												new ChangeMoveSelectorConfig(),
												new SwapMoveSelectorConfig(),
												empTeamFollowLeaderMoveIteratorFactoryConfig)))));
			} else
				// use shadow variables for teams
				config = config.withEntityClasses(FloorAssignment.class, ShadowFloorAssignment.class);
		else
			config = config.withEntityClasses(Employee.class);
		config = config
				.withConstraintProviderClass(RoomAllocationConstraintProvider.class)
				.withTerminationConfig(new TerminationConfig()
						.withBestScoreLimit("0hard/0soft")
						.withSpentLimit(Duration.ofSeconds(time_limit)));
		SolverFactory<IRoomAllocation9> solverFactory = SolverFactory.create(config);

		// Solve the problem
		Solver<IRoomAllocation9> solver = solverFactory.buildSolver();
		IRoomAllocation9 solution = solver.solve(problem);

		// Visualize the solution
//		solution.describeSolution().forEach(line -> LOGGER.info(line));
		try {
			String outputFile = inflated ? "d:/tmp/java-solution-inflated.csv" : "d:/tmp/java-solution.csv";
			solution.writeCSV(new PrintStream(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		LOGGER.info("Score: " + solution.getScore());
		ScoreManager<IRoomAllocation9, HardSoftScore> scoreManager = ScoreManager.create(solverFactory);
		System.out.println(scoreManager.explainScore(solution));
		LOGGER.info("Done.");
	}
}
