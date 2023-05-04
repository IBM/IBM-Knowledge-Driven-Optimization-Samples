package com.ibm.hrl.room_allocation.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PlanningSolution
public class RoomAllocation9Employee implements IRoomAllocation9 {
	@ProblemFactCollectionProperty
	private List<OfficeType> officeTypes;
	@ProblemFactCollectionProperty
	private List<RoomAvailability> building;
	@PlanningEntityCollectionProperty
	private List<? extends IEmployee> employees;

	// computed fields
	@ProblemFactCollectionProperty
	@ValueRangeProvider(id = "floorRange")
	private List<Floor> floors;

	@PlanningScore
	private HardSoftScore score;

	// No-arg constructor required for OptaPlanner
	public RoomAllocation9Employee() {
	}

	public RoomAllocation9Employee(List<? extends IEmployee> employees, List<OfficeType> officeTypes,
	                               List<RoomAvailability> building) {
		this.officeTypes = officeTypes;
		this.building = building;
		this.employees = employees;

		// computed fields
		this.floors = building.stream().map(RoomAvailability::getFloor).distinct().map(Floor::new)
				.collect(Collectors.toList());
	}

	// ************************************************************************
	// Getters and setters
	// ************************************************************************

	@Override
	public List<OfficeType> getOfficeTypeList() {
		return officeTypes;
	}

	@Override
	public List<RoomAvailability> getRoomList() {
		return building;
	}

	@Override
	public List<? extends IEmployee> getEmployeeList() {
		return employees;
	}

	@Override
	public HardSoftScore getScore() {
		return score;
	}

	@Override
	public Stream<String> describeSolution() {
		return employees.stream().map(emp -> emp.getEid() + " -> " + emp.getFloor());
	}

	@Override
	public void writeCSV(PrintStream s) {
		s.println("EmployeeId,Floor");
		employees.forEach(emp -> s.println(emp.getEid() + "," + emp.getFloor().getNumber()));
	}
}
