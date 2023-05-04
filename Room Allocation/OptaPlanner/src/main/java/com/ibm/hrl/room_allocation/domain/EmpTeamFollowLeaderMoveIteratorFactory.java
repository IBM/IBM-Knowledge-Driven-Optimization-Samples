package com.ibm.hrl.room_allocation.domain;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;

/**
 * Create EmpTeamChangeMove objects for all teams
 */
public class EmpTeamFollowLeaderMoveIteratorFactory implements MoveIteratorFactory<IRoomAllocation9, EmpTeamFollowLeaderMove> {
	@Override
	public long getSize(ScoreDirector<IRoomAllocation9> scoreDirector) {
		RoomAllocation9AssignmentClass problem = (RoomAllocation9AssignmentClass) scoreDirector.getWorkingSolution();
		return problem.members.size();
	}

	@Override
	public Iterator<EmpTeamFollowLeaderMove> createOriginalMoveIterator(ScoreDirector<IRoomAllocation9> scoreDirector) {
		RoomAllocation9AssignmentClass problem = (RoomAllocation9AssignmentClass) scoreDirector.getWorkingSolution();
		Map<String, Set<String>> members = problem.members;
		return members.entrySet().stream().map(team -> new EmpTeamFollowLeaderMove(team.getValue(), team.getKey())).iterator();
	}

	@Override
	public Iterator<EmpTeamFollowLeaderMove> createRandomMoveIterator(ScoreDirector<IRoomAllocation9> scoreDirector,
			Random random) {
		RoomAllocation9AssignmentClass problem = (RoomAllocation9AssignmentClass) scoreDirector.getWorkingSolution();
		Map<String, Set<String>> members = problem.members;
		List<Entry<String, Set<String>>> entries = members.entrySet()
				.stream()
				.filter(entry -> scoreDirector.lookUpWorkingObject(problem.assignment_map.get(entry.getKey()).getFloor() != null))
				.collect(Collectors.toList());
		if (entries.isEmpty())
			return (Stream.<EmpTeamFollowLeaderMove>empty()).iterator();
		Iterator<EmpTeamFollowLeaderMove> result = Stream.generate(() -> random.nextInt(entries.size()))
				.map(index -> new EmpTeamFollowLeaderMove(
						entries.get(index).getValue(),
						entries.get(index).getKey()
				))
				.iterator();
		for (int i = 0; i < 10; i++)
			System.out.println("iter: " + result.next().toString());
		return result;
	}
}
