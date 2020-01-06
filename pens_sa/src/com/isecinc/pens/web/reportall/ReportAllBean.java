package com.isecinc.pens.web.reportall;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.isecinc.pens.web.projectc.ProjectCBean;
public class ReportAllBean implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8389174957413702703L;

   private ProjectCBean projectCBean;

	public ProjectCBean getProjectCBean() {
		return projectCBean;
	}
	
	public void setProjectCBean(ProjectCBean projectCBean) {
		this.projectCBean = projectCBean;
	}
   
	 
  
}
