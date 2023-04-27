package com.ibm.hrl.room_allocation.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

public interface IFloorAssignment {

	String toString();

	String getEid();

	void setEid(String eid);

	Floor getFloor();

	void setFloor(Floor floor);

}