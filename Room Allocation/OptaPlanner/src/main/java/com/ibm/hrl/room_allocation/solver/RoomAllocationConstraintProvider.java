/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.hrl.room_allocation.solver;

import com.ibm.hrl.room_allocation.RoomAllocationMain;
import com.ibm.hrl.room_allocation.domain.Employee;
import com.ibm.hrl.room_allocation.domain.EmployeeInfo;
import com.ibm.hrl.room_allocation.domain.IFloorAssignment;
import com.ibm.hrl.room_allocation.domain.OfficeType;
import com.ibm.hrl.room_allocation.domain.RoomAvailability;
import org.javatuples.Quartet;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countBi;

public class RoomAllocationConstraintProvider implements ConstraintProvider {
	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
		return new Constraint[]{
				// Hard constraints
				RoomAllocationMain.inflated
						? availabilityConstraintForInflated(constraintFactory)
						: (RoomAllocationMain.assignmentClass
						? availabilityConstraintForAssignmentClass(constraintFactory)
						: availabilityConstraint(constraintFactory)),
				RoomAllocationMain.assignmentClass
						? sameFloorConstraintForAssignmentClass(constraintFactory)
						: sameFloorConstraint(constraintFactory)
				// Soft constraints
		};
	}

	// This formulation assumes that availability is the total number of seats, not
	// offices
	public Constraint availabilityConstraintForInflated(ConstraintFactory constraintFactory) {
		return constraintFactory.forEach(Employee.class)
				.groupBy(emp -> emp.getFloor().getNumber(), Employee::getOfficeType, count())
				.join(RoomAvailability.class,
						Joiners.equal((n, ot, s) -> n, RoomAvailability::getFloor),
						Joiners.equal((n, ot, s) -> ot, RoomAvailability::getOfficeType))
				.filter((n, ot, s, rec) -> s > rec.getAvailability())
				.penalize("Availability exceeded", HardSoftScore.ONE_HARD,
						(n, ot, s, rec) -> s - rec.getAvailability());
	}

	// This formulation supports the original data, in which availability is the total number of offices
	public Constraint availabilityConstraint(ConstraintFactory constraintFactory) {
		return constraintFactory.forEach(Employee.class)
				.groupBy(emp -> emp.getFloor().getNumber(), Employee::getOfficeType, count())
				.join(RoomAvailability.class,
						Joiners.equal((n, ot, s) -> n, RoomAvailability::getFloor),
						Joiners.equal((n, ot, s) -> ot, RoomAvailability::getOfficeType))
				.map(Quartet::with)
				// Quartet contains: employee-number, office-type, seat-requirement, RoomAvailability object
				.join(OfficeType.class,
						Joiners.equal(Quartet::getValue1, OfficeType::getTypeName))
				.filter((ra, otInfo) -> ra.getValue2() > ra.getValue3().getAvailability() * otInfo.getMaxOccupancy())
				.penalize("Availability exceeded", HardSoftScore.ONE_HARD,
						(ra, otInfo) -> ra.getValue2() - ra.getValue3().getAvailability() * otInfo.getMaxOccupancy());
	}

	// This formulation uses a separate Floor Assignment class;
	// it also supports the original data, in which availability is the total number of offices
	public Constraint availabilityConstraintForAssignmentClass(ConstraintFactory constraintFactory) {
		return constraintFactory.forEach(EmployeeInfo.class)
				.join(IFloorAssignment.class,
						Joiners.equal(EmployeeInfo::getEid, IFloorAssignment::getEid),
						Joiners.filtering((emp, assignment) -> assignment.getFloor() != null))
				.groupBy((emp, assignment) -> assignment.getFloor().getNumber(),
						(emp, assignment) -> emp.getOfficeType(), countBi())
				.join(RoomAvailability.class,
						Joiners.equal((n, ot, s) -> n, RoomAvailability::getFloor),
						Joiners.equal((n, ot, s) -> ot, RoomAvailability::getOfficeType))
				.map(Quartet::with)
				// Quartet contains: employee-number, office-type, seat-requirement, RoomAvailability object
				.join(OfficeType.class,
						Joiners.equal(Quartet::getValue1, OfficeType::getTypeName))
				.filter((ra, otInfo) -> ra.getValue2() > ra.getValue3().getAvailability() * otInfo.getMaxOccupancy())
				.penalize("Availability exceeded",
						HardSoftScore.ONE_HARD,
						(ra, otInfo) -> ra.getValue2() - ra.getValue3().getAvailability() * otInfo.getMaxOccupancy());
	}

	public Constraint sameFloorConstraint(ConstraintFactory constraintFactory) {
		return constraintFactory.forEach(Employee.class)
				.filter(emp -> !emp.isTeamLeadIsIndependent())
				.join(Employee.class, Joiners.equal(Employee::getTeamLead, Employee::getEid),
						Joiners.filtering((emp, lead) -> emp.getFloor().getNumber() != lead.getFloor().getNumber()))
				.penalize("Team on same floor", HardSoftScore.ONE_SOFT,
						(emp, lead) -> Math.abs(emp.getFloor().getNumber() - lead.getFloor().getNumber()));
	}

	public Constraint sameFloorConstraintForAssignmentClass(ConstraintFactory constraintFactory) {
		return constraintFactory.forEach(EmployeeInfo.class)
				.filter(emp -> !emp.isTeamLeadIsIndependent())
				.join(IFloorAssignment.class,
						Joiners.equal(EmployeeInfo::getEid, IFloorAssignment::getEid),
						Joiners.filtering((emp, assignment) -> assignment.getFloor() != null))
				.join(IFloorAssignment.class,
						Joiners.equal((emp, floor) -> emp.getTeamLead(), IFloorAssignment::getEid),
						Joiners.filtering((emp, floor, lead_floor) -> floor.getFloor() != null && lead_floor.getFloor() != null &&
								floor.getFloor().getNumber() != lead_floor.getFloor().getNumber()))
				.penalize("Team on same floor",
						HardSoftScore.ONE_SOFT,
						(emp, floor, lead_floor) -> Math
								.abs(floor.getFloor().getNumber() - lead_floor.getFloor().getNumber()));
	}
}
