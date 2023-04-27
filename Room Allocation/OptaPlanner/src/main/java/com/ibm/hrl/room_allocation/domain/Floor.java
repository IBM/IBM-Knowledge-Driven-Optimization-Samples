package com.ibm.hrl.room_allocation.domain;

import java.util.Objects;

public class Floor {

    private int number;

    public Floor(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Floor " + Integer.toString(number);
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public int getNumber() {
        return number;
    }

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Floor other = (Floor) obj;
		return number == other.number;
	}
}
