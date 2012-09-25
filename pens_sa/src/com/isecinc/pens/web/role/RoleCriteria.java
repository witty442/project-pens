package com.isecinc.pens.web.role;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Role;

/**
 * User Criteria
 * Witty
 * 
 */
public class RoleCriteria extends I_Criteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3460983827667841344L;

	private Role role = new Role();

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	

}
