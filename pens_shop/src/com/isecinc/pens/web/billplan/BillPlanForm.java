package com.isecinc.pens.web.billplan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.BillPlan;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.OrderLine;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class BillPlanForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private BillPlanCriteria criteria = new BillPlanCriteria();

	private List<BillPlan> results;
	private List<BillPlan> lines = null;
	
	@SuppressWarnings("unchecked")
	public BillPlanForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new BillPlan();
			}
		};
		
		lines = LazyList.decorate(new ArrayList<BillPlan>(), factory);
	}
	
	public BillPlanCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(BillPlanCriteria criteria) {
		this.criteria = criteria;
	}

	public List<BillPlan> getResults() {
		return results;
	}

	public void setResults(List<BillPlan> results) {
		this.results = results;
	}

	public BillPlan getBillPlan() {
		return criteria.getBillPlan();
	}

	public void setBillPlan(BillPlan billPlan) {
		criteria.setBillPlan(billPlan);
	}
	
	public List<BillPlan> getLines() {
		return lines;
	}

	public void setLines(List<BillPlan> lines) {
		this.lines = lines;
	}
    
	
}
