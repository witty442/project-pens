package com.isecinc.pens.web.role;


import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Role;


/**
 *Witty
 * 
 */
public class RoleForm extends I_Form {

	private static final long serialVersionUID = -366067722071055991L;

	private RoleCriteria criteria = new RoleCriteria();

	private Role[] results = null;
    
	private String mode ;
	
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Role getRole() {
		return criteria.getRole();
	}

	public void setRole(Role role) {
		criteria.setRole(role);
	}
	public Role[] getResults() {
		return results;
	}

	public void setResults(Role[] results) {
		this.results = results;
	}

	public RoleCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(RoleCriteria criteria) {
		this.criteria = criteria;
	}


}
