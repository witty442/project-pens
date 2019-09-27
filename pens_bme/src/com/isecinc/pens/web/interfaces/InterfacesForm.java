package com.isecinc.pens.web.interfaces;

import java.util.List;

import org.apache.struts.upload.FormFile;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.InterfaceBean;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.MonitorItemDetailBean;
import com.isecinc.pens.bean.MonitorItemResultBean;

/**
 * Trip Form
 * 
 * @author Witty.B
 * @version $Id: TripForm.java,v 1.0 19/10/2010 00:00:00 Witty.B Exp $
 * 
 */ 

public class InterfacesForm extends I_Form {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4077513527723148160L;

	private InterfacesCriteria criteria = new InterfacesCriteria();

	private MonitorBean[] results = null;
	
	private MonitorItemDetailBean[] resultsItemDetail = null;
    
	private String[] chkIndex;
	private InterfaceBean bean = new InterfaceBean();
	private InterfaceBean beanCriteria = new InterfaceBean();

	private MonitorItemBean monitorItemBeanResult = null;
	private List<MonitorItemBean> monitorItemList;
	
	private List<MonitorItemResultBean> salesInList;
	private List<MonitorItemResultBean> returnList;
	
	
	public List<MonitorItemResultBean> getSalesInList() {
		return salesInList;
	}

	public void setSalesInList(List<MonitorItemResultBean> salesInList) {
		this.salesInList = salesInList;
	}

	public List<MonitorItemResultBean> getReturnList() {
		return returnList;
	}

	public void setReturnList(List<MonitorItemResultBean> returnList) {
		this.returnList = returnList;
	}

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
