package com.ibm.hrl.room_allocation.domain;

import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class ShadowEmployeeVariables implements IShadowEmployeeVariables {
	private Set<String> genuineVariables = new HashSet<>();
	private SetValuedMap<String, String> propagation = new HashSetValuedHashMap<>();

	/**
	 * If start is in a loop based on the next function, return any element of the loop. Otherwise, return the last
	 * element.
	 */
	public static String findTop(String start, Function<String, String> next) {
		String leading = next.apply(start);
		String previous = start;
		while (leading != null) {
			if (leading.equals(start))
				return start;
			start = next.apply(start);
			previous = leading;
			leading = next.apply(leading);
			if (leading == null)
				return previous;
			previous = leading;
			leading = next.apply(leading);
		}
		return previous;
	}

	public ShadowEmployeeVariables(Map<String, EmployeeInfo> employee_map) {
		Set<String> todo = new HashSet<>(employee_map.keySet());
		Set<String> toRemove = new HashSet<>();
		// This code is specific to the case of a single appearance of the solution mapping in the transformation
		// Step 1: all variables that don't satisfy the condition are genuine planning variables
		for (String emp : todo) {
			EmployeeInfo info = employee_map.get(emp);
			if (info.isTeamLeadIsIndependent()) {
				genuineVariables.add(emp);
				toRemove.add(emp);
			}
		}
		todo.removeAll(toRemove);

		// Step 2: Add one element from each loop
		while (!todo.isEmpty()) {
			String start = todo.iterator().next();
			todo.remove(start);
			Function<String, String> getTeamLead = emp -> employee_map.get(emp).getTeamLead();
			String topElement = findTop(start, getTeamLead);
			genuineVariables.add(topElement);
			Set<String> shadows = propagation.get(topElement);
			String current = start;
			do {
				todo.remove(current);
				if (!Objects.equals(current, topElement))
					shadows.add(current);
				current = getTeamLead.apply(current);
			} while (current != null && !current.equals(start) && shadows.contains(current));
		}
	}

	@Override
	public boolean isShadowVariable(String employee_id) {
		return !genuineVariables.contains(employee_id);
	}

	@Override
	public Set<String> propagateFrom(String employee_id) {
		return propagation.get(employee_id);
	}
}
