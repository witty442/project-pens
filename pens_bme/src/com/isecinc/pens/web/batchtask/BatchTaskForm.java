package com.isecinc.pens.web.batchtask;

import java.util.List;
import java.util.Map;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.inf.bean.InterfaceBean;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;

/**
 * Trip Form
 * 
 * @author Witty.B
 * @version $Id: TripForm.java,v 1.0 19/10/2010 00:00:00 Witty.B Exp $
 * 
 */ 

public class BatchTaskForm extends I_Form {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4077513527723148160L;

	private InterfaceBean bean =null;
	private MonitorBean monitorBean = null;
	private List<MonitorItemBean> monitorItemList;
	private BatchTaskInfo taskInfo;
	private MonitorBean[] results = null;
	
	
	public MonitorBean[] getResults() {
		return results;
	}
	public void setResults(MonitorBean[] results) {
		this.results = results;
	}
	public BatchTaskInfo getTaskInfo() {
		return taskInfo;
	}
	public void setTaskInfo(BatchTaskInfo taskInfo) {
		this.taskInfo = taskInfo;
	}
	public InterfaceBean getBean() {
		return bean;
	}
	public void setBean(InterfaceBean bean) {
		this.bean = bean;
	}
	
	public MonitorBean getMonitorBean() {
		return monitorBean;
	}
	public void setMonitorBean(MonitorBean monitorBean) {
		this.monitorBean = monitorBean;
	}
	public List<MonitorItemBean> getMonitorItemList() {
		return monitorItemList;
	}
	public void setMonitorItemList(List<MonitorItemBean> monitorItemList) {
		this.monitorItemList = monitorItemList;
	}
	
	
}
