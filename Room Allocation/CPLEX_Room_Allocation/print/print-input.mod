/*********************************************
 * OPL 12.10.0.0 Model
 * Author: YISHAIFELDMAN
 * Creation Date: 11 Oct 2020 at 12:28:27
 *********************************************/
 
execute LOG_INPUT {

  var dfile = new IloOplOutputFile("output/room-allocation-input.json")
 
  
  function key(key) {
  	 return  "\"" + key + "\": " 
  } 
  
  function aValue(val) {
     var t =  typeof(val)          
     var value = val
     if (t == "string") {
       value = "\""+ val + "\""     
     } 
     return value     
  }
  
  function writeTuples(scope, targets) {
    var isFirst=true
    for (var tar in targets) {
        if (isFirst) {
        isFirst = false    
        } else {        
            scope.writeln(",")        
        } 
        scope.writeln("{") 
        for (var i =0; i < tar.getNFields(); i++) {
            var f = tar.getFieldName(i);                       
            scope.write(key(f));
            scope.write(aValue(tar[f]))
            if (i+1 < tar.getNFields()) {
                scope.write(",")            
            }
            scope.writeln("")   
        }           
        scope.write("}")
    }
  }
  
  function writeSets(scope, targets) {
    var isFirst=true
    for (var tar in targets) {
        if (isFirst) {
        isFirst = false    
        } else {        
            scope.writeln(",")        
        } 
        scope.write(aValue(tar))
    }
  } 
  
  function writeKeyArrays(scope, targets) {
    var isFirst=true
    for (var tar in targets) {
        if (isFirst) {
        isFirst = false    
        } else {        
            scope.writeln(",")        
        } 
        scope.write(key(tar) + aValue(targets[tar]))  
    }
  }
  
  function writeOffice_type_by_employee_type(scope) {
  	scope.writeln(key("officeTypeByEmployee") + "{")
  	writeKeyArrays(scope, office_type_by_employee_type)
  	scope.writeln("\n},")       
  }
  
  function writeFloors(scope) {
	scope.writeln(key("floors") + "[")
	writeSets(scope, floors)
	scope.writeln("\n],")       
  }
	
  function writeAreas(scope) {
	scope.writeln(key("areas") + "[")
	writeSets(scope, areas)
	scope.writeln("\n],")       
  }
  
  function writeRooms(scope) {    
    scope.writeln(key("rooms") + "[")
    var isFirstFloor = true
    for (var floor in rooms) {              
        if (isFirstFloor) {
          isFirstFloor = false          
        } else {
          scope.writeln(",")            
        }
        scope.writeln("{")      
        scope.writeln(key("floor") + aValue(floor) + ",")
        scope.writeln(key("rooms") + "{")
        var isFirst = true
        for (var officetype in rooms[floor]) {
            if (!isFirst) {
                scope.writeln(",")          
            } else {
                isFirst = false          
            }                   
            scope.write(key(officetype) + aValue(rooms[floor][officetype]))    
        }
        scope.write("\n}}")
    }
    scope.writeln("\n],")   
    return scope
  } 
  
  function writeOfficeTypes(scope) {    
    scope.writeln(key("office_types") + "[")
    writeTuples(scope, office_types)
    scope.writeln("\n],") 
  }
  
  function writeEmployees(scope) {    
    scope.writeln(key("employees") + "[")
    writeTuples(scope, employees)
    scope.writeln("\n]")  
  }      
  
  dfile.writeln("{")
  writeOfficeTypes(dfile)
  dfile.writeln("\n\n")
  writeOffice_type_by_employee_type(dfile)
  dfile.writeln("\n\n")
  writeFloors(dfile)
  dfile.writeln("\n\n")
  writeAreas(dfile)
  dfile.writeln("\n\n")
  writeRooms(dfile)
  dfile.writeln("\n\n")
  writeEmployees(dfile)     	  
  dfile.writeln("}") 
  dfile.close();
  
  writeln("Done Print Input Data.")
}