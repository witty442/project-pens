package com.isecinc.pens.web.memberfollow;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.MemberFollow;

/**
 * Member Follow Form Class
 * 
 * @author Aneak.t
 * @version $Id: MemberFollowForm.java,v 1.0 14/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberFollowForm extends I_Form{

	private static final long serialVersionUID = 6681650577779632372L;

	private MemberFollowCriteria criteria = new MemberFollowCriteria();
	
	private MemberFollow[] results = null;

	private String isFirst = "N";
	
	public MemberFollowCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MemberFollowCriteria criteria) {
		this.criteria = criteria;
	}

	public MemberFollow getMemberFollow() {
		return criteria.getMemberFollow();
	}

	public void setMemberFollow(MemberFollow memberFollow) {
		criteria.setMemberFollow(memberFollow);
	}

	public String getIsFirst() {
		return isFirst;
	}

	public void setIsFirst(String isFirst) {
		this.isFirst = isFirst;
	}

	public MemberFollow[] getResults() {
		return results;
	}

	public void setResults(MemberFollow[] results) {
		this.results = results;
	}
	
}
