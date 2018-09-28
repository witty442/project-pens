package com.isecinc.pens.web.user;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.PD;
import com.isecinc.pens.bean.SubInventory;
import com.isecinc.pens.bean.User;
 
/**
 * User Form
 * 
 * @author Atiz.b
 * @version $Id: UserForm.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class UserForm extends I_Form {

	private static final long serialVersionUID = -366067722071055991L;

	private UserCriteria criteria = new UserCriteria();

	private User[] results = null;

	private List<SubInventory> subInventories = null;
	
	private List<PD> pdList = null;
	
	private String[] subinvids;

	public User getUser() {
		return criteria.getUser();
	}

	public void setUser(User user) {
		criteria.setUser(user);
	}

	public User[] getResults() {
		return results;
	}

	public void setResults(User[] results) {
		this.results = results;
	}

	public UserCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(UserCriteria criteria) {
		this.criteria = criteria;
	}

	public List<SubInventory> getSubInventories() {
		return subInventories;
	}

	public void setSubInventories(List<SubInventory> subInventories) {
		this.subInventories = subInventories;
	}

	public String[] getSubinvids() {
		return subinvids;
	}

	public void setSubinvids(String[] subinvids) {
		this.subinvids = subinvids;
	}

	public List<PD> getPdList() {
		return pdList;
	}

	public void setPdList(List<PD> pdList) {
		this.pdList = pdList;
	}
	

}
