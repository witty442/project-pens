package com.isecinc.pens.web.report.member;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.member.MemberExpirationReport;

public class MemberReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -8463911228603168679L;

	private MemberExpirationReport memberExpire = new MemberExpirationReport();
	
	public MemberExpirationReport getMemberExpirationReport() {
		return memberExpire;
	}

	public void setMemberExpirationReport(
			MemberExpirationReport memberExpire) {
		this.memberExpire = memberExpire;
	}

	private String fileType = SystemElements.PDF;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
