package com.isecinc.pens.web.memberfollow;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.MemberFollow;

/**
 * MMemberFollow Criteria Class
 * 
 * @author Aneak.t
 * @version $Id: MemberFollowCriteria.java,v 1.0 14/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberFollowCriteria extends I_Criteria{

	private static final long serialVersionUID = -2138679795913120126L;

	private MemberFollow memberFollow = new MemberFollow();

	public MemberFollow getMemberFollow() {
		return memberFollow;
	}

	public void setMemberFollow(MemberFollow memberFollow) {
		this.memberFollow = memberFollow;
	}
	
}
