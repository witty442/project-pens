package com.isecinc.pens.web.master;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Master;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MasterForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private MasterCriteria criteria = new MasterCriteria();

	private List<Master> masterResults;
	private int masterResultsSize;
	
	
	public int getMasterResultsSize() {
		return masterResultsSize;
	}

	public void setMasterResultsSize(int masterResultsSize) {
		this.masterResultsSize = masterResultsSize;
	}

	public Master getMaster() {
		return criteria.getMaster();
	}

	public void setMaster(Master master) {
		criteria.setMaster(master);
	}

	public List<Master> getMasterResults() {
		return masterResults;
	}

	public void setMasterResults(List<Master> masterResults) {
		this.masterResults = masterResults;
	}

	public MasterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MasterCriteria criteria) {
		this.criteria = criteria;
	}
	
	

}
