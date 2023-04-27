package com.ibm.hrl.room_allocation.domain;

import javafx.util.Pair;

public class RoomAvailability {
	private int availability;
	private int floor;
	private String officeType;

	public RoomAvailability(int floor, String officeType, int availability) {
		this.floor = floor;
		this.officeType = officeType;
		this.availability = availability;
	}

	public int getAvailability() {
		return availability;
	}

	public int getFloor() {
		return floor;
	}

	public String getOfficeType() {
		return officeType;
	}

	public String toString() {
		return "Rooms on floor " + floor + " of type " + officeType + ": " + availability;
	}

	public Pair<Integer, String> getKey() {
		return new Pair<Integer, String>(floor, officeType);
	}
}