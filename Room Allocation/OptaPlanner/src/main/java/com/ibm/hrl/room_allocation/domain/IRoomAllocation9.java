package com.ibm.hrl.room_allocation.domain;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

public interface IRoomAllocation9 {
	public List<OfficeType> getOfficeTypeList();

	public List<RoomAvailability> getRoomList();

	public List<? extends IEmployee> getEmployeeList();

	public HardSoftScore getScore();

	public Stream<String> describeSolution();

	public void writeCSV(PrintStream s);
}