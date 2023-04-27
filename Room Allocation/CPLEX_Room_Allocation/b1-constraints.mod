/*********************************************
 * OPL 12.10.0.0 Model
 * Author: YISHAIFELDMAN
 * Creation Date: 30 Sep 2020 at 18:58:02
 *********************************************/

subject to {
  // constraint1: has_unique_assignment
  forall (en in employee_numbers) 
    (sum (f in floors) alloc_tuple[<en,f>]) == 1;
    
  // constraint2
  // Need employee_by_number to find employee by key field, can this be done without this additional data structure?
  forall (e in employees : employee_by_number[e.team_lead].is_independent == 0, // need to find team-lead object!!!
          f in floors)
      alloc_tuple[<e.number,f>] == alloc_tuple[<e.team_lead,f>];

  // constraint3
  forall (ot in office_types, f in floors)
    occupancy[ot][f] <= ot.max_occupancy * rooms[f][ot.type_name];
}