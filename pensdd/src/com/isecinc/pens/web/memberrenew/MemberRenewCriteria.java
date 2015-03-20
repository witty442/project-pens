package com.isecinc.pens.web.memberrenew;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.MemberRenew;

/**
 * Member Renew Criteria
 * 
 * @author Aneak.t
 * @version $Id: MemberRenewCriteria.java,v 1.0 01/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberRenewCriteria extends I_Criteria{

	private static final long serialVersionUID = -6862902445041260485L;

	private MemberRenew memberRenew = new MemberRenew();

	public MemberRenew getMemberRenew() {
		return memberRenew;
	}

	public void setMemberRenew(MemberRenew memberRenew) {
		this.memberRenew = memberRenew;
	}
	
}
