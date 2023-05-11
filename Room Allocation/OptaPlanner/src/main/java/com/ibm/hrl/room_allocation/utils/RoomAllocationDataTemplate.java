package com.ibm.hrl.room_allocation.utils;

import com.ibm.hrl.room_allocation.domain.Employee;
import com.ibm.hrl.room_allocation.domain.IEmployee;
import com.ibm.hrl.room_allocation.domain.IRoomAllocation9;
import com.ibm.hrl.room_allocation.domain.OfficeType;
import com.ibm.hrl.room_allocation.domain.RoomAllocation9Employee;
import com.ibm.hrl.room_allocation.domain.RoomAvailability;

import java.util.ArrayList;
import java.util.List;

public class RoomAllocationDataTemplate {
    public static IRoomAllocation9 generateData() {
        List<IEmployee> employees = new ArrayList<>();
        employees.add(new Employee("number1", "office-type1", "team-lead1", false, "area1"));
        // ...

        List<OfficeType> officeTypes = new ArrayList<>();
        officeTypes.add(new OfficeType("office-type1", 2, 874));
        // ...

        List<RoomAvailability> rooms = new ArrayList<>();
        rooms.add(new RoomAvailability(1, "office-type1", 33));
        // ...

        return new RoomAllocation9Employee(employees, officeTypes, rooms);
    }
}
