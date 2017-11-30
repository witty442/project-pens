package com.isecinc.pens.web.pick;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Job;

public class JobForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;
    private List<Job> results = new ArrayList<Job>();
    private List<Job> resultsSearch = new ArrayList<Job>();

    private String mode;
    private Job Job ;
    private Job JobCriteria ;
    
    
	public Job getJobCriteria() {
		return JobCriteria;
	}
	public void setJobCriteria(Job jobCriteria) {
		JobCriteria = jobCriteria;
	}
	public List<Job> getResults() {
		return results;
	}
	public void setResults(List<Job> results) {
		this.results = results;
	}
	public List<Job> getResultsSearch() {
		return resultsSearch;
	}
	public void setResultsSearch(List<Job> resultsSearch) {
		this.resultsSearch = resultsSearch;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public Job getJob() {
		return Job;
	}
	public void setJob(Job job) {
		Job = job;
	}
    
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if(getJob() !=null)
			getJob().setDispAutoCN("");
	}
    
}
