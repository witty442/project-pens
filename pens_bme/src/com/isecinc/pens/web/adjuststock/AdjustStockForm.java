package com.isecinc.pens.web.adjuststock;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Order;

public class AdjustStockForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<AdjustStock> results = new ArrayList<AdjustStock>();
    private List<AdjustStock> resultsSearch = new ArrayList<AdjustStock>();
	private AdjustStockCriteria criteria = new AdjustStockCriteria();
    private boolean verify = false;
    private String mode;
    private AdjustStock adjustStockCriteria ;
    
    
    
	public AdjustStock getAdjustStockCriteria() {
		return adjustStockCriteria;
	}

	public void setAdjustStockCriteria(AdjustStock adjustStockCriteria) {
		this.adjustStockCriteria = adjustStockCriteria;
	}

	public List<AdjustStock> getResultsSearch() {
		return resultsSearch;
	}

	public void setResultsSearch(List<AdjustStock> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}


	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
		this.verify = verify;
	}

	public AdjustStockCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(AdjustStockCriteria criteria) {
		this.criteria = criteria;
	}

	public AdjustStock getAdjustStock() {
		return criteria.getAdjustStock();
	}

	public void setAdjustStock(AdjustStock order) {
		criteria.setAdjustStock(order);
	}

	public List<AdjustStock> getResults() {
		return results;
	}

	public void setResults(List<AdjustStock> results) {
		this.results = results;
	}
	
    
}
