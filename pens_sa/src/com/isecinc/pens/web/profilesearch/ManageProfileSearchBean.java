package com.isecinc.pens.web.profilesearch;

import java.io.Serializable;

public class ManageProfileSearchBean implements Serializable{
 
   private static final long serialVersionUID = 2067478765106321206L;
   private int userId;
   private int profileId;
   private String profileName;
   private String reportName;
  
   
	public String getReportName() {
	return reportName;
}
public void setReportName(String reportName) {
	this.reportName = reportName;
}
	public int getProfileId() {
		return profileId;
	}
	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
  
}
