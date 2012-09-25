package com.isecinc.pens.web.group;


import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.GroupRole;
import com.isecinc.pens.bean.Role;


/**
 *Witty
 * 
 */
public class GroupRoleForm extends I_Form {

	private static final long serialVersionUID = -366067722071055991L;

	private GroupRoleCriteria criteria = new GroupRoleCriteria();
	
	private String mode ;
	
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public GroupRole getGroupRole() {
		return criteria.getGroupRole();
	}

	public void setGroupRole(GroupRole groupRole) {
		criteria.setGroupRole(groupRole);
	}
	

	public GroupRoleCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(GroupRoleCriteria criteria) {
		this.criteria = criteria;
	}


}
