package com.ibm.hrl.room_allocation.domain;

public interface IEmployee {

	String getEid();

	Floor getFloor();

	boolean isTeamLeadIsIndependent();
}
