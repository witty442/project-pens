package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;

public class BarcodeForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<Barcode> results = new ArrayList<Barcode>();
    private List<Barcode> resultsSearch = new ArrayList<Barcode>();
    private List<Barcode> resultsSearchPrev = new ArrayList<Barcode>();
    
    private String mode;
    private Barcode Job ;
    private Barcode JobCriteria ;
    
    
	public List<Barcode> getResultsSearchPrev() {
		return resultsSearchPrev;
	}
	public void setResultsSearchPrev(List<Barcode> resultsSearchPrev) {
		this.resultsSearchPrev = resultsSearchPrev;
	}
	public List<Barcode> getResults() {
		return results;
	}
	public void setResults(List<Barcode> results) {
		this.results = results;
	}
	public List<Barcode> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<Barcode> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public Barcode getJob() {
		return Job;
	}
	public void setJob(Barcode job) {
		Job = job;
	}
	public Barcode getJobCriteria() {
		return JobCriteria;
	}
	public void setJobCriteria(Barcode jobCriteria) {
		JobCriteria = jobCriteria;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		 //Initialize the property 
			if(Job !=null){
		     Job.setIncludeCancel("");
			}
	}
    
   
}
