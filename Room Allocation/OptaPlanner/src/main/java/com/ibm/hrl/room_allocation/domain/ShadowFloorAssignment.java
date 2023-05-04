package com.ibm.hrl.room_allocation.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

@PlanningEntity
public class ShadowFloorAssignment implements IFloorAssignment {

	@PlanningId
	private String eid;
	@CustomShadowVariable(variableListenerClass = FloorVariableListener.class, sources = {
			@PlanningVariableReference(entityClass = FloorAssignment.class, variableName = "floor") })
	private Floor floor;

	// No-arg constructor required for OptaPlanner
	public ShadowFloorAssignment() {
	}

	public ShadowFloorAssignment(String eid) {
		this.setEid(eid);
	}

	public ShadowFloorAssignment(String eid, int floor) {
		this.setEid(eid);
		this.setFloor(new Floor(floor));
	}

	@Override
	public String toString() {
		String assignment = (getFloor() == null) ? "" : (" >> " + getFloor().getNumber());
		return "ShadowFloorAssignment" + "(" + getEid() + ")" + assignment;
	}

	@Override
	public String getEid() {
		return eid;
	}

	@Override
	public void setEid(String eid) {
		this.eid = eid;
	}

	@Override
	public Floor getFloor() {
		return floor;
	}

	@Override
	public void setFloor(Floor floor) {
		this.floor = floor;
	}
}
