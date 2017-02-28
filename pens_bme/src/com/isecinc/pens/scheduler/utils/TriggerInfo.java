/*
 * Created on Jul 1, 2005
 * 
 */
package com.isecinc.pens.scheduler.utils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Pakornkit Jaikla
 *
 */
public class TriggerInfo implements Serializable{
	private int numberOfRepeat = 1;
	private String name;
	private Date startTime;
	private String repeatExpr;
	private String className;
	
	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className The className to set.
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the numberOfRepeat.
	 */
	public int getNumberOfRepeat() {
		return numberOfRepeat;
	}
	/**
	 * @param numberOfRepeat The numberOfRepeat to set.
	 */
	public void setNumberOfRepeat(int numberOfRepeat) {
		this.numberOfRepeat = numberOfRepeat;
	}
	/**
	 * @return Returns the repeatExpr.
	 */
	public String getRepeatExpr() {
		return repeatExpr;
	}
	/**
	 * @param repeatExpr The repeatExpr to set.
	 */
	public void setRepeatExpr(String repeatExpr) {
		this.repeatExpr = repeatExpr;
	}

	/**
	 * @return Returns the startTime.
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime The startTime to set.
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
