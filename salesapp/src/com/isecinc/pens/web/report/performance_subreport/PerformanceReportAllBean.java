package com.isecinc.pens.web.report.performance_subreport;

import java.util.List;

import com.isecinc.pens.report.performance.PerformanceReport;
import com.isecinc.pens.web.report.transfer.BankTransferReport;

public class PerformanceReportAllBean {
	
 List<PerformanceReport> sub1Report;
 List<BankTransferReport> sub2Report;
public List<PerformanceReport> getSub1Report() {
	return sub1Report;
}
public void setSub1Report(List<PerformanceReport> sub1Report) {
	this.sub1Report = sub1Report;
}
public List<BankTransferReport> getSub2Report() {
	return sub2Report;
}
public void setSub2Report(List<BankTransferReport> sub2Report) {
	this.sub2Report = sub2Report;
}
   


   
   
}
