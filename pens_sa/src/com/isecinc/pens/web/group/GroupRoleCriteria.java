package com.isecinc.pens.web.group;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.GroupRole;


/**
 * User Criteria
 * Witty
 * 
 */
public class GroupRoleCriteria extends I_Criteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3460983827667841344L;

	private GroupRole groupRole = new GroupRole();

	public GroupRole getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(GroupRole groupRole) {
		this.groupRole = groupRole;
	}

    
}
