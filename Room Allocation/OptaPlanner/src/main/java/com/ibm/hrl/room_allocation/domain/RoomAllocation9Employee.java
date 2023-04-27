package com.ibm.hrl.room_allocation.domain;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import javafx.util.Pair;

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

	// pre-computed fields
//	public Map<Pair<Integer, String>, Integer> f_building;

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
		this.floors = building.stream().map(rec -> rec.getFloor()).distinct().map(floor -> new Floor(floor))
				.collect(Collectors.toList());
//		this.f_building = building
//				.stream()
//				.collect(Collectors.toMap(rec->new Pair<>(rec.getFloor(), rec.getOfficeType()), RoomAvailability::getAvailability));
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
		employees.stream().forEach(emp -> s.println(emp.getEid() + "," + emp.getFloor().getNumber()));
	}

	// *** new for same-floor constraint in the representation (see RoomAllocationConstraintProvider2)
	// !!! Doesn't work! (no notification of change when lead's floor changes?)
	public Floor floor_of(Employee employee) {
		if (employee.isTeamLeadIsIndependent()) {
			Floor result = employee.getFloor();
			if (result != null)
				return result;
			return floors.get(0);
		}
		for (IEmployee lead : employees) {
			if (lead.getEid() == employee.getTeamLead()) {
				Floor result = lead.getFloor();
				if (result != null)
					return result;
				return floors.get(0);
			}
		}
		return floors.get(0);
	}
}
