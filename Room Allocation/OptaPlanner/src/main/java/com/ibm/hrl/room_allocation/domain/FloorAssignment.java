package com.ibm.hrl.room_allocation.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class FloorAssignment implements IFloorAssignment {

	@PlanningId
	private String eid;
	@PlanningVariable(valueRangeProviderRefs = "floorRange")
	private Floor floor;

	// No-arg constructor required for OptaPlanner
	public FloorAssignment() {
	}

	public FloorAssignment(String eid) {
		this.setEid(eid);
	}

	public FloorAssignment(String eid, int floor) {
		this.setEid(eid);
		this.setFloor(new Floor(floor));
	}

	@Override
	public String toString() {
		String assignment = (getFloor() == null) ? "" : (" >> " + getFloor().getNumber());
		return "FloorAssignment" + "(" + getEid() + ")" + assignment;
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
