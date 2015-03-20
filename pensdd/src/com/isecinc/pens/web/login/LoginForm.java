package com.isecinc.pens.web.login;

import org.apache.struts.action.ActionForm;

/**
 * LoginForm Class
 * 
 * @author Atiz.b
 * @version $Id: LoginForm.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 */
public class LoginForm extends ActionForm {

	private static final long serialVersionUID = 2233309770774217200L;

	/** Labels */
	private String codeLabel;
	private String passwordLabel;

	/** Objects */
	private String userName;
	private String password;

	/** Initial Form */
	protected void init() {}

	public String getCodeLabel() {
		return codeLabel;
	}

	public String getPasswordLabel() {
		return passwordLabel;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
