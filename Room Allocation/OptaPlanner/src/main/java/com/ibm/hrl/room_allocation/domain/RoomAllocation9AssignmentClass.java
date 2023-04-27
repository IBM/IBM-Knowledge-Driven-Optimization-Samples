package com.ibm.hrl.room_allocation.domain;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.ibm.hrl.room_allocation.RoomAllocationMain;

import javafx.util.Pair;

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
	// number of leads z with at least one other member
	public Map<String, Set<String>> members;
//	public Map<Pair<Integer, String>, Integer> f_building;
	public IShadowEmployeeVariables shadowVars;

	// parameters
	// TODO: is it possible to remove dependence on static variable?
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

//		System.out.println(assignments);
//		System.out.println(assignment_map);
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
		floors = building.stream().map(rec -> rec.getFloor()).distinct().map(floor -> new Floor(floor))
				.collect(Collectors.toList());
		employee_map = employees.stream().collect(Collectors.toMap(EmployeeInfo::getEid, ei -> ei));
//		this.f_building = building
//				.stream()
//				.collect(Collectors.toMap(rec->new Pair<>(rec.getFloor(), rec.getOfficeType()), RoomAvailability::getAvailability));
//		System.out.println("Assignments: " + assignments);
		members = employees.stream()
				.filter(emp -> emp.getEid() != emp.getTeamLead())
				.filter(emp -> !emp.isTeamLeadIsIndependent())
				.collect(Collectors.groupingBy(EmployeeInfo::getTeamLead,
						Collectors.mapping(EmployeeInfo::getEid, Collectors.toSet())));
		removeEmptyValues(members);
//		System.out.println("Non-independent team leads: " + members.size());
	}

	public static <K, V extends Set<?>> void removeEmptyValues(Map<K, V> map) {
		List<K> toRemove = map.entrySet().stream()
				.filter(e -> e.getValue().isEmpty())
				.map(e -> e.getKey())
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
		assignments.stream().forEach(emp -> s.println(emp.getEid() + "," + emp.getFloor().getNumber()));
	}

	// *** new for same-floor constraint in the representation (see RoomAllocationConstraintProvider2)
	// !!! Doesn't work! (no notification of change when lead's floor changes?)
	public Floor floor_of(Employee employee) {
		if (employee.isTeamLeadIsIndependent()) {
			Floor result = employee.getFloor();
			if (result != null)
				return result;
			return getFloors().get(0);
		}
		for (IEmployee lead : employees) {
			if (lead.getEid() == employee.getTeamLead()) {
				Floor result = lead.getFloor();
				if (result != null)
					return result;
				return getFloors().get(0);
			}
		}
		return getFloors().get(0);
	}

	public List<Floor> getFloors() {
		return floors;
	}
}
