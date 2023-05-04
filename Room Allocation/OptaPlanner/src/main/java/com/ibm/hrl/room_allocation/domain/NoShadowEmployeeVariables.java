package com.ibm.hrl.room_allocation.domain;

import java.util.Set;

public class NoShadowEmployeeVariables implements IShadowEmployeeVariables {

	@Override
	public boolean isShadowVariable(String employee_id) {
		return false;
	}

	@Override
	public Set<String> propagateFrom(String employee_id) {
		return null;
	}

}
