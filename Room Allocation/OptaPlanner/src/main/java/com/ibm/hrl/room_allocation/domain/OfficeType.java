package com.ibm.hrl.room_allocation.domain;

public class OfficeType {

	private String typeName;
	private int maxOccupancy;
	private double cost;

	public OfficeType(String typeName, int maxOccupancy, double cost) {
		this.typeName = typeName;
		this.maxOccupancy = maxOccupancy;
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "OfficeType: " + typeName + ", max-occupancy=" + maxOccupancy + ", cost=" + cost;
	}

	// ************************************************************************
	// Getters and setters
	// ************************************************************************

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getMaxOccupancy() {
		return maxOccupancy;
	}

	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

}
