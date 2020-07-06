package com.isecinc.pens.web.report.performance_subreport;

import java.io.Serializable;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.performance.PerformanceReport;

/**
 */

public class PerformanceReportFormSubReport extends I_Form  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 793752194290373076L;
	private String fileType;
    private PerformanceReport bean =new PerformanceReport();
    
    
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public PerformanceReport getBean() {
		return bean;
	}
	public void setBean(PerformanceReport bean) {
		this.bean = bean;
	}
	
    
}
