package com.isecinc.pens.web.user;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.User;

/**
 * User Criteria
 * 
 * @author Atiz.b
 * @version $Id: UserCriteria.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class UserCriteria extends I_Criteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3460983827667841344L;

	private User user = new User();
   

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
