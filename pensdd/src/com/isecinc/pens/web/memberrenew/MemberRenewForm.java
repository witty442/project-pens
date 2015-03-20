package com.isecinc.pens.web.memberrenew;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberRenew;

/**
 * Member Renew Form
 * 
 * @author Aneak.t
 * @version $Id: MemberRenewForm.java,v 1.0 01/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberRenewForm extends I_Form{

	private static final long serialVersionUID = 7154868818946722565L;
	
	private MemberRenewCriteria criteria = new MemberRenewCriteria();
	
	private MemberRenew[] results = null;
	
	private Member[] members = null;

	public MemberRenewCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MemberRenewCriteria criteria) {
		this.criteria = criteria;
	}

	public MemberRenew[] getResults() {
		return results;
	}

	public void setResults(MemberRenew[] results) {
		this.results = results;
	}

	public MemberRenew getMemberRenew() {
		return criteria.getMemberRenew();
	}

	public void setMemberRenew(MemberRenew memberRenew) {
		criteria.setMemberRenew(memberRenew);
	}

	public Member[] getMembers() {
		return members;
	}

	public void setMembers(Member[] members) {
		this.members = members;
	}

}
