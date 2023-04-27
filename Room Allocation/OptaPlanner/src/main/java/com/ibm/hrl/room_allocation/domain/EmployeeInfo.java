package com.ibm.hrl.room_allocation.domain;

// This class contains employee information without the assigned floor; assignments are given separately by FloorAssignments
public class EmployeeInfo implements IEmployee {
	private String eid;
	private String officeType;
	private String teamLead;
	private boolean teamLeadIsIndependent;
	private String area;

	public EmployeeInfo(String eid, String officeType, String teamLead, boolean teamLeadIsIndependent, String area) {
		this.setEid(eid);
		this.setOfficeType(officeType);
		this.setTeamLead(teamLead);
		this.setTeamLeadIsIndependent(teamLeadIsIndependent);
		this.setArea(area);
	}

	@Override
	public String toString() {
		return "EmployeeInfo" + "(" + getEid() + ", office-type=" + getOfficeType() + ", lead=" + getTeamLead()
				+ ", ind.lead=" + isTeamLeadIsIndependent() + ", area=" + getArea() + ")";
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

	@Override
	public Floor getFloor() {
		// Not used, required by interface
		return null;
	}
}
