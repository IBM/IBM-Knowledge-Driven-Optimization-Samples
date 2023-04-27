/*********************************************
 * OPL 12.10.0.0 Model
 * Author: YISHAIFELDMAN
 * Creation Date: 11 Oct 2020 at 12:31:03
 *********************************************/

execute PRINT_SOLUTION {
  var dfile = new IloOplOutputFile("output/room-allocation-b1-output.json");
  var isFirst=true 
  dfile.writeln("[")
  for (var assign in legal_assignments) {
    
  	if (0 == alloc_tuple[assign]){
  	  continue	
  	} 	  	
    var employee = employee_by_number[assign.employee_number]
    if (isFirst) {
    	isFirst = false    
    } else {        
        dfile.writeln(",")        
    } 
    dfile.writeln("{")       
    dfile.writeln("\"resource\": " + "\"" + assign.employee_number + "\",")
    dfile.writeln("\"activity\": " + "\"" + assign.floor + "\"")
    dfile.write("}")        
  }
  dfile.writeln("]")
  dfile.close();
  
  writeln("Done Printing Output")
}