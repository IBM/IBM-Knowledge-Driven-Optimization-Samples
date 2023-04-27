package com.ibm.hrl.room_allocation.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Employee implements IEmployee {

	@PlanningId
	private String eid;
	private String officeType;
	private String teamLead;
	private boolean teamLeadIsIndependent;
	private String area;
	@PlanningVariable(valueRangeProviderRefs = "floorRange")
	private Floor floor;

	// No-arg constructor required for OptaPlanner
	public Employee() {
	}

	public Employee(String eid, String officeType, String teamLead, boolean teamLeadIsIndependent, String area) {
		this.setEid(eid);
		this.setOfficeType(officeType);
		this.setTeamLead(teamLead);
		this.setTeamLeadIsIndependent(teamLeadIsIndependent);
		this.setArea(area);
	}

	public Employee(String eid, String officeType, String teamLead, boolean teamLeadIsIndependent, String area,
			int floor) {
		this.setEid(eid);
		this.setOfficeType(officeType);
		this.setTeamLead(teamLead);
		this.setTeamLeadIsIndependent(teamLeadIsIndependent);
		this.setArea(area);
		this.setFloor(new Floor(floor));
	}

	@Override
	public String toString() {
		String assignment = (getFloor() == null) ? "" : (" >> " + getFloor().getNumber());
		return "Employee" + "(" + getEid() + ", office-type=" + getOfficeType() + ", lead=" + getTeamLead() + ", ind.lead="
				+ isTeamLeadIsIndependent() + ", area=" + getArea() + ")" + assignment;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getOfficeType() {
		return officeType;
	}

	public void setOfficeType(String officeType) {
		this.officeType = officeType;
	}

	public String getTeamLead() {
		return teamLead;
	}

	public void setTeamLead(String teamLead) {
		this.teamLead = teamLead;
	}

	public boolean isTeamLeadIsIndependent() {
		return teamLeadIsIndependent;
	}

	public void setTeamLeadIsIndependent(boolean teamLeadIsIndependent) {
		this.teamLeadIsIndependent = teamLeadIsIndependent;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}
}
