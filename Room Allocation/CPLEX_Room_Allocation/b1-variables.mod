/*********************************************
 * OPL 12.10.0.0 Model
 * Author: YISHAIFELDMAN
 * Creation Date: 30 Sep 2020 at 15:19:48
 *********************************************/

//dvar boolean alloc[employee_numbers][floors];  // replaced by allc_tuple below

tuple assignment {
  string employee_number;
  string floor;
}

// Can be read from input or computed in a different way if not all assignments are legal
{assignment} legal_assignments = {<en,f> | en in employee_numbers, f in floors};

dvar boolean alloc_tuple[legal_assignments];

dvar int area_utilization[areas][floors];
dvar float cost_penalty[areas][floors];

dvar int+ occupancy[office_types][floors];  // how many people are assigned to this type of offices on this floor
dvar float assigned_offices_f[office_types][floors];  // how many offices of this type have non-empty assignments on this floor as a fraction
dvar int assigned_offices[office_types][floors];  // how many offices of this type have non-empty assignments on this floor as an integer

//dvar float total_cost[office_types][floors];
dvar float total_cost;
