package com.isecinc.pens.web.interfaces;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.inf.bean.InterfaceBean;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.MonitorItemDetailBean;

/**
 * Trip Form
 * 
 * @author Witty.B
 * @version $Id: TripForm.java,v 1.0 19/10/2010 00:00:00 Witty.B Exp $
 * 
 */ 

public class InterfacesForm extends I_Form {

	private static final long serialVersionUID = 9066506758859129582L;

	private InterfacesCriteria criteria = new InterfacesCriteria();

	private MonitorBean[] results = null;
	
	private MonitorItemDetailBean[] resultsItemDetail = null;
    
	private String[] chkIndex;
	private InterfaceBean bean = new InterfaceBean();
	private InterfaceBean beanCriteria = new InterfaceBean();

	private MonitorItemBean monitorItemBeanResult = null;
	private List<MonitorItemBean> monitorItemList;
    
	
	public List<MonitorItemBean> getMonitorItemList() {
		return monitorItemList;
	}

	public void setMonitorItemList(List<MonitorItemBean> monitorItemList) {
		this.monitorItemList = monitorItemList;
	}

	public MonitorItemBean getMonitorItemBeanResult() {
		return monitorItemBeanResult;
	}

	public void setMonitorItemBeanResult(MonitorItemBean monitorItemBeanResult) {
		this.monitorItemBeanResult = monitorItemBeanResult;
	}

	public MonitorItemDetailBean[] getResultsItemDetail() {
		return resultsItemDetail;
	}

	public void setResultsItemDetail(MonitorItemDetailBean[] resultsItemDetail) {
		this.resultsItemDetail = resultsItemDetail;
	}

	public String[] getChkIndex() {
		return chkIndex;
	}

	public void setChkIndex(String[] chkIndex) {
		this.chkIndex = chkIndex;
	}

	public MonitorBean getMonitorBean() {
		return criteria.getMonitorBean();
	}

	public void setMonitorBean(MonitorBean monitorBean) {
		criteria.setMonitorBean(monitorBean);
	}

	public InterfacesCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(InterfacesCriteria criteria) {
		this.criteria = criteria;
	}

	public MonitorBean[] getResults() {
		return results;
	}

	public void setResults(MonitorBean[] results) {
		this.results = results;
	}

	public InterfaceBean getBean() {
		return bean;
	}

	public void setBean(InterfaceBean bean) {
		this.bean = bean;
	}

	public InterfaceBean getBeanCriteria() {
		return beanCriteria;
	}

	public void setBeanCriteria(InterfaceBean beanCriteria) {
		this.beanCriteria = beanCriteria;
	}
    
	
	

}
