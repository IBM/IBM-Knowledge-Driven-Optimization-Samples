package com.ibm.hrl.room_allocation.domain;

import java.util.Set;

public interface IShadowEmployeeVariables {

	boolean isShadowVariable(String employee_id);

	Set<String> propagateFrom(String employee_id);

}