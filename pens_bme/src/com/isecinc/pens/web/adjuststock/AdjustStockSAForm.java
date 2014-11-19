package com.isecinc.pens.web.adjuststock;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStockSA;

public class AdjustStockSAForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<AdjustStockSA> results = new ArrayList<AdjustStockSA>();
    private List<AdjustStockSA> resultsSearch = new ArrayList<AdjustStockSA>();
    private boolean verify = false;
    private String mode;
    private AdjustStockSA adjustStockSA ;
    private AdjustStockSA adjustStockSACriteria ;
    
    private int resultsSize;
    private int resultsSearchSize;
    
    
	public int getResultsSearchSize() {
		return resultsSearch!=null?resultsSearch.size():0;
	}

	public void setResultsSearchSize(int resultsSearchSize) {
		this.resultsSearchSize = resultsSearchSize;
	}

	public int getResultsSize() {
		return results!=null?results.size():0;
	}

	public void setResultsSize(int resultsSize) {
		this.resultsSize = resultsSize;
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

	public List<AdjustStockSA> getResults() {
		return results;
	}

	public void setResults(List<AdjustStockSA> results) {
		this.results = results;
	}

	public List<AdjustStockSA> getResultsSearch() {
		return resultsSearch;
	}

	public void setResultsSearch(List<AdjustStockSA> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}

	public AdjustStockSA getAdjustStockSA() {
		return adjustStockSA;
	}

	public void setAdjustStockSA(AdjustStockSA adjustStockSA) {
		this.adjustStockSA = adjustStockSA;
	}

	public AdjustStockSA getAdjustStockSACriteria() {
		return adjustStockSACriteria;
	}

	public void setAdjustStockSACriteria(AdjustStockSA adjustStockSACriteria) {
		this.adjustStockSACriteria = adjustStockSACriteria;
	}

	
	
    
}
