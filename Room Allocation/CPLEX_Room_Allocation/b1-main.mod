/*********************************************
 * OPL 12.10.0.0 Model
 * Author: YISHAIFELDMAN
 * Creation Date: 29 Sep 2020 at 18:18:35
 *********************************************/

execute {
 	cplex.tilim = 60.0;
}

include "data_structures.mod";
execute {
  writeln("Input Is Loaded");  
}
include "print/print-input.mod";


include "b1-variables.mod";
include "b1-objectives.mod";
include "b1-definitions.mod";
include "b1-constraints.mod";

execute {
//  writeln(area_utilization);
//  writeln(cost_penalty);
  writeln("Objective2 = ", total_cost);
  writeln("Done.");

//  var dfile = new IloOplOutputFile("output/debug.txt");
//  dfile.writeln("BOO!");
//  writeln(floors);
//  dfile.close();
}

include "print/print-b1-output.mod";
