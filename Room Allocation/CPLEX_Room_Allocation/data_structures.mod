/*********************************************
 * OPL 12.10.0.0 Model
 * Author: YISHAIFELDMAN
 * Creation Date: 29 Sep 2020 at 11:36:51
 *********************************************/

// alt: mapping from emp# to rest of info (same for all keyed tuples)
 tuple Employee {
   key string number;
   string type;
   string team_lead;  // key of Employee
   int is_team_lead;  // boolean can only be used for dvars
   int is_independent;  // ditto
   int area;
 }
 
 
 tuple OfficeType {
   key string type_name;
   int max_occupancy;
   float cost;
 }
 
 {Employee} employees = ...;

 int number_of_employees = card(employees);

 {string} employee_types = {e.type | e in employees};
 
 {string} employee_numbers = {e.number | e in employees};

 Employee employee_by_number[employee_numbers] = [e.number:e | e in employees];
 
 {OfficeType} office_types = ...; 
 
 int number_of_office_types = card(office_types);
 
 {string} office_type_names = {ot.type_name | ot in office_types};
 
 string office_type_by_employee_type[employee_types] = ...;
 
 {int} areas = ...;
 
 int number_of_areas = card(areas);
  
 {string} floors = ...; 
 
 int number_of_floors = card(floors);
 
// range office_types_range = 1..number_of_office_types; 
 range floors_range = 0..number_of_floors-1; 
 
// int rooms[floors_range][office_types_range] = ...;
// FIXME: Use tuple as index 
 int rooms[floors][office_type_names] = ...;