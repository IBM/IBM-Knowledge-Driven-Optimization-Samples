package com.ibm.hrl.room_allocation.domain;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;

class UndoTeamChangeMove extends AbstractMove<IRoomAllocation9> {
	private final Map<String, Optional<Floor>> restore;
	private AbstractMove<IRoomAllocation9> originalMove;

	public UndoTeamChangeMove(AbstractMove<IRoomAllocation9> originalMove, Map<String, Optional<Floor>> current) {
		this.originalMove = originalMove;
		this.restore = current;
	}

	@Override
	public boolean isMoveDoable(ScoreDirector<IRoomAllocation9> scoreDirector) {
		return !restore.isEmpty();
	}

	@Override
	protected AbstractMove<IRoomAllocation9> createUndoMove(ScoreDirector<IRoomAllocation9> scoreDirector) {
		return originalMove;
	}

	@Override
	protected void doMoveOnGenuineVariables(ScoreDirector<IRoomAllocation9> scoreDirector) {
		RoomAllocation9AssignmentClass problem = (RoomAllocation9AssignmentClass) scoreDirector.getWorkingSolution();
		Map<String, IFloorAssignment> assignment_map = problem.assignment_map;
		for (Entry<String, Optional<Floor>> e : restore.entrySet()) {
			String employee = e.getKey();
			Optional<Floor> maybeTarget = e.getValue();
			IFloorAssignment assignment = scoreDirector.lookUpWorkingObject(assignment_map.get(employee));
			Floor current_assignment = assignment.getFloor();
			Floor target = null;
			if (maybeTarget.isPresent())
				target = maybeTarget.get();
			if (target != current_assignment) {
				scoreDirector.beforeVariableChanged(assignment, "floor");
				assignment.setFloor(target);
				scoreDirector.afterVariableChanged(assignment, "floor");
			}
		}
	}
}