package com.ibm.hrl.room_allocation.domain;

import com.ibm.hrl.room_allocation.RoomAllocationMain;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PlanningSolution
public class RoomAllocation9AssignmentClass implements IRoomAllocation9 {
	@ProblemFactCollectionProperty
	private List<OfficeType> officeTypes;
	@ProblemFactCollectionProperty
	private List<RoomAvailability> building;
	@ProblemFactCollectionProperty
	private List<EmployeeInfo> employees;
	@PlanningEntityCollectionProperty
	private List<IFloorAssignment> assignments;

	// computed fields
	@ProblemFactCollectionProperty
	@ValueRangeProvider(id = "floorRange")
	private List<Floor> floors;
	public Map<String, EmployeeInfo> employee_map;

	@PlanningScore
	private HardSoftScore score;

	// pre-computed fields
	public Map<String, IFloorAssignment> assignment_map;
	public Map<String, Set<String>> members;
	public IShadowEmployeeVariables shadowVars;

	// parameters
	private boolean useCustomMoves = RoomAllocationMain.customMoves;

	// No-arg constructor required for OptaPlanner
	public RoomAllocation9AssignmentClass() {
	}

	public RoomAllocation9AssignmentClass(List<EmployeeInfo> employees, List<OfficeType> officeTypes,
	                                      List<RoomAvailability> building) {
		this.officeTypes = officeTypes;
		this.building = building;
		this.employees = employees;

		preCompute();

		shadowVars = new ShadowEmployeeVariables(employee_map);
		assignments = new ArrayList<>();
		initializeSolution();
	}

	private void initializeSolution() {
		for (IEmployee emp : employees) {
			if (!useCustomMoves && shadowVars.isShadowVariable(emp.getEid()))
				assignments.add(new ShadowFloorAssignment(emp.getEid()));
			else
				assignments.add(new FloorAssignment(emp.getEid()));
		}
		assignment_map = assignments.stream().collect(Collectors.toMap(IFloorAssignment::getEid, assignment -> assignment));
	}

	private void preCompute() {
		floors = building.stream().map(RoomAvailability::getFloor).distinct().map(Floor::new)
				.collect(Collectors.toList());
		employee_map = employees.stream().collect(Collectors.toMap(EmployeeInfo::getEid, ei -> ei));
		members = employees.stream()
				.filter(emp -> !Objects.equals(emp.getEid(), emp.getTeamLead()))
				.filter(emp -> !emp.isTeamLeadIsIndependent())
				.collect(Collectors.groupingBy(EmployeeInfo::getTeamLead,
						Collectors.mapping(EmployeeInfo::getEid, Collectors.toSet())));
		removeEmptyValues(members);
	}

	public static <K, V extends Set<?>> void removeEmptyValues(Map<K, V> map) {
		List<K> toRemove = map.entrySet().stream()
				.filter(e -> e.getValue().isEmpty())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		for (K k : toRemove) {
			map.remove(k);
		}
	}

	// ************************************************************************
	// Getters and setters
	// ************************************************************************

	public List<OfficeType> getOfficeTypeList() {
		return officeTypes;
	}

	public List<RoomAvailability> getRoomList() {
		return building;
	}

	public List<EmployeeInfo> getEmployeeList() {
		return employees;
	}

	public HardSoftScore getScore() {
		return score;
	}

	public List<IFloorAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(List<IFloorAssignment> assignments) {
		this.assignments = assignments;
	}

	public Stream<String> describeSolution() {
		return employees.stream().map(emp -> emp.getEid() + " -> " + emp.getFloor());
	}

	public void writeCSV(PrintStream s) {
		s.println("EmployeeId,Floor");
		assignments.forEach(emp -> s.println(emp.getEid() + "," + emp.getFloor().getNumber()));
	}

	public List<Floor> getFloors() {
		return floors;
	}
}
