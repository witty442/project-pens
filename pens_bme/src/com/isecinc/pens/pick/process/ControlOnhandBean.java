package com.isecinc.pens.pick.process;

import java.math.BigDecimal;

public class ControlOnhandBean {
  
	private BigDecimal taskId;
	private String taskDate;
	private String taskName;
	private String status;
	public BigDecimal getTaskId() {
		return taskId;
	}
	public void setTaskId(BigDecimal taskId) {
		this.taskId = taskId;
	}
	public String getTaskDate() {
		return taskDate;
	}
	public void setTaskDate(String taskDate) {
		this.taskDate = taskDate;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
    
}
