package com.isecinc.pens.web.report.member;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.member.MemberExpirationReport;

public class MemberReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private MemberReportCriteria criteria = new MemberReportCriteria();

	public MemberReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MemberReportCriteria criteria) {
		this.criteria = criteria;
	}

	public MemberExpirationReport getMemberExpirationReport(){
		return criteria.getMemberExpirationReport();
	}

	public void setMemberExpirationReport(MemberExpirationReport memberExpiration){
		criteria.setMemberExpirationReport(memberExpiration);
	}
}
