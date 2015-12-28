package com.isecinc.pens.web.interfaces;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.inf.bean.InterfaceBean;
import com.isecinc.pens.inf.bean.MonitorBean;

/**
 * ConversionCriteria Criteria
 * 
 * @author Witty.B
 * @version $Id: ConversionCriteria,v 1.0 19/10/2010 00:00:00 Witty.B Exp $
 * 
 */


public class InterfacesCriteria extends I_Criteria{

	private static final long serialVersionUID = 714056610377438393L;

	private MonitorBean monitorBean = new MonitorBean();
	
	public MonitorBean getMonitorBean() {
		return monitorBean;
	}

	public void setMonitorBean(MonitorBean monitorBean) {
		this.monitorBean = monitorBean;
	}


}
