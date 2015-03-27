package com.isecinc.pens.web.dataimport.member;

import com.isecinc.core.web.I_Criteria;

/**
 * Member Criteria
 * 
 * @author Aneak.t
 * @version $Id: MemberCriteria.java,v 1.0 11/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberCriteria extends I_Criteria{

	private static final long serialVersionUID = 8539839020113236576L;
	
	private Member member = new Member();

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
	
}
