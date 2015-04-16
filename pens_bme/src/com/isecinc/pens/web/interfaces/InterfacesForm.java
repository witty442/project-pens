package com.isecinc.pens.web.interfaces;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.inf.bean.MonitorBean;
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

	
	

}
