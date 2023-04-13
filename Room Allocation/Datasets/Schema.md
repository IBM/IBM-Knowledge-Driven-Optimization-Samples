# Schema Description
A dataset for the room allocation problem contains four input files:

- **employee**: The list of employees and their attributes
- **office_availability**: How many offices of each type are available on each floor
- **office_type_by_employee_type**: Which type of office is suitable for each type of employee
- **office_type_info**: Information about each type of office

The Following sections contain the schema of each of these files.

## employee

This file has a row for each employee, where for each employee the following information is provided:

- **type**: The type of employee.
- **number**: A unique identifier of the employee
- **team_lead**: The employee number of this employee's team lead. In case this employee does not have a team lead (i.e., this is the head of the organization), the employee's team lead is the employee.
- **is_team_lead**: 1 if the employee is a team lead and 0 otherwise.
- **is_independant**: 1 if the employee is independant and zero otherwise.
- **area**: The area to which the employee belongs.

## Office Availability

- **floor**: The floor number
- **office_type**: The type of office.
- **rooms**: The number of rooms of this officetype on this floor.

Note that for each floor, there will be a row in this file for each type of office that exists on this floor. 

## office_type_by_employee

This will have as many rows as the types of employees, where each row has the following fields:
- **employee_type**: The type of employee.
- **office_type**: The appropriate office type for this employee type.

## office_type_info

The following information is provided for each office type:

- **max_occupancy**: The maximum number of employees that can be placed in this type of office.
- **type_name**: The office type.
-**cost**: The cost for each office of this type used

## solution

A placement solution contains a row for each employee, where each row contains the following: 

- **resource**: The employee number.
- **activity**: [^1]The floor in which this employee is placed.

[^1] The reason for these names for the fields is to ensure that the solution file format is suitable for a variety of assignment optimization problems