package com.ibm.hrl.room_allocation.domain;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FloorVariableListener implements VariableListener<IRoomAllocation9, FloorAssignment> {

	private IRoomAllocation9 solution;

	@Override
	public void resetWorkingSolution(ScoreDirector<IRoomAllocation9> scoreDirector) {
		VariableListener.super.resetWorkingSolution(scoreDirector);
		solution = scoreDirector.getWorkingSolution();
	}

	@Override
	public void beforeEntityAdded(ScoreDirector<IRoomAllocation9> scoreDirector, FloorAssignment entity) {
	}

	@Override
	public void afterEntityAdded(ScoreDirector<IRoomAllocation9> scoreDirector, FloorAssignment entity) {
	}

	@Override
	public void beforeVariableChanged(ScoreDirector<IRoomAllocation9> scoreDirector, FloorAssignment entity) {
	}

	@Override
	public void afterVariableChanged(ScoreDirector<IRoomAllocation9> scoreDirector, FloorAssignment entity) {
		RoomAllocation9AssignmentClass solution = (RoomAllocation9AssignmentClass) scoreDirector.getWorkingSolution();
		final String employee_id = entity.getEid();
		final Floor newFloor = entity.getFloor();
		ArrayList<String> agenda = new ArrayList<>(List.of(employee_id));
		Set<String> done = new HashSet<>();
		while (agenda.size() > 0) {
			String leader = agenda.remove(0);
			Set<String> members = solution.shadowVars.propagateFrom(leader);
			if (members != null)
				for (String member : members) {
					if (done.contains(member))
						continue;
					IFloorAssignment working_assignment = scoreDirector
							.lookUpWorkingObject(solution.assignment_map.get(member));
					// N.B. working_assignment.getFloor() could be null!
					if (working_assignment.getFloor() == newFloor)
						continue;
					scoreDirector.beforeVariableChanged(working_assignment, "floor");
					working_assignment.setFloor(newFloor);
					scoreDirector.afterVariableChanged(working_assignment, "floor");
					if (agenda.stream().noneMatch(member::equals))
						agenda.add(member);
				}
			done.add(leader);
		}
	}

	@Override
	public void beforeEntityRemoved(ScoreDirector<IRoomAllocation9> scoreDirector, FloorAssignment entity) {
	}

	@Override
	public void afterEntityRemoved(ScoreDirector<IRoomAllocation9> scoreDirector, FloorAssignment entity) {
	}

}
