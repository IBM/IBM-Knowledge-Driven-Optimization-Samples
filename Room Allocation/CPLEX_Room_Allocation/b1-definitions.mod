/*********************************************
 * OPL 12.10.0.0 Model
 * Author: YISHAIFELDMAN
 * Creation Date: 30 Sep 2020 at 15:19:02
 *********************************************/

subject to {
  forall (a in areas, f in floors) 
    area_utilization[a][f] == sum (e in employees : e.is_team_lead == 1 && e.area == a) alloc_tuple[<e.number,f>];
// N.B. Can't use dvar alloc inside condition!!
//    area_utilization[a][f] == sum (e in employees : e.is_team_lead == 1 && e.area == a && alloc_tuple[<e.number,f>]) 1;  // illegal

// Version for full array:
//      area_utilization[a][f] == sum (e in employees : e.is_team_lead == 1 && e.area == a) alloc[e.number][f];

  forall (a in areas, f in floors) {
    // N.B. important for area_utilization to be integer, otherwise problem with ==0  
  	area_utilization[a][f] == 0 => cost_penalty[a][f] == 0.; 
  	area_utilization[a][f] >= 0.1 => cost_penalty[a][f] == 1.; 
  }

  forall (ot in office_types, f in floors)
    occupancy[ot][f] == sum (e in employees : office_type_by_employee_type[e.type] == ot.type_name) alloc_tuple[<e.number,f>];

  forall (ot in office_types, f in floors) {
    // N.B. can't use ceil function, non-linear!
    assigned_offices_f[ot][f] == occupancy[ot][f] / ot.max_occupancy;
    assigned_offices[ot][f] >= assigned_offices_f[ot][f];
    assigned_offices[ot][f] <= assigned_offices_f[ot][f] + 0.999;  // N.B need epsilon
  }
  
  total_cost == sum(ot in office_types, f in floors) assigned_offices[ot][f] * ot.cost;
}
