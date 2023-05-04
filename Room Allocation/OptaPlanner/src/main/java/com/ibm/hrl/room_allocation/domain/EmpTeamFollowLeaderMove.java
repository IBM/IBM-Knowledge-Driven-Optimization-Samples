package com.ibm.hrl.room_allocation.domain;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;

public class EmpTeamFollowLeaderMove extends AbstractMove<IRoomAllocation9> {
	private final Set<String> employees;
	private final String leader;

	public EmpTeamFollowLeaderMove(Set<String> employees, String leader) {
		super();
		this.employees = employees;
		this.leader = leader;
	}

	@Override
	public boolean isMoveDoable(ScoreDirector<IRoomAllocation9> scoreDirector) {
		RoomAllocation9AssignmentClass problem = (RoomAllocation9AssignmentClass) scoreDirector.getWorkingSolution();
		return employees.stream()
				.anyMatch(emp -> !scoreDirector.lookUpWorkingObject(problem.assignment_map.get(emp)).getFloor().equals(leader));
	}

	@Override
	protected AbstractMove<IRoomAllocation9> createUndoMove(ScoreDirector<IRoomAllocation9> scoreDirector) {
		RoomAllocation9AssignmentClass problem = (RoomAllocation9AssignmentClass) scoreDirector.getWorkingSolution();
		Map<String, IFloorAssignment> assignment_map = problem.assignment_map;
		Map<String, Optional<Floor>> current = employees.stream()
				.collect(Collectors.toMap(Function.identity(),
						emp -> Optional.ofNullable(assignment_map.get(emp).getFloor())));
		System.out.println("current: " + current);
		return new UndoTeamChangeMove(this, current);
	}

	@Override
	protected void doMoveOnGenuineVariables(ScoreDirector<IRoomAllocation9> scoreDirector) {
		RoomAllocation9AssignmentClass problem = (RoomAllocation9AssignmentClass) scoreDirector.getWorkingSolution();
		Map<String, IFloorAssignment> assignment_map = problem.assignment_map;
		Floor target = scoreDirector.lookUpWorkingObject(problem.assignment_map.get(leader)).getFloor();
		for (String employee : employees) {
			IFloorAssignment assignment = scoreDirector.lookUpWorkingObject(assignment_map.get(employee));
			Floor current_assignment = assignment.getFloor();
			if (!target.equals(current_assignment)) {
				scoreDirector.beforeVariableChanged(assignment, "floor");
				assignment.setFloor(target);
				scoreDirector.afterVariableChanged(assignment, "floor");
			}
		}
	}
}