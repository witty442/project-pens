package com.isecinc.pens.web.dataimports.member;

import org.apache.struts.upload.FormFile;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.web.member.MemberCriteria;

public class MemberImportForm extends I_Form {

	private static final long serialVersionUID = -3377115951015868391L;

	private String newImport;
	private FormFile memberFile;
	private MemberCriteria criteria = new MemberCriteria();
	private String checkAll;
	private String tempSelectedIds;
	private int nextYear;
	private int currentId;
	private String currentOrderLineRemain;
	private String currentStartNextYear;

	public String getNewImport() {
		return newImport;
	}
	public void setNewImport(String newImport) {
		this.newImport = newImport;
	}
	public FormFile getMemberFile() {
		return memberFile;
	}
	public void setMemberFile(FormFile memberFile) {
		this.memberFile = memberFile;
	}
	public MemberCriteria getCriteria() {
		return criteria;
	}
	public void setCriteria(MemberCriteria criteria) {
		this.criteria = criteria;
	}
	public String getCheckAll() {
		return checkAll;
	}
	public void setCheckAll(String checkAll) {
		this.checkAll = checkAll;
	}
	public String getTempSelectedIds() {
		return tempSelectedIds;
	}
	public void setTempSelectedIds(String tempSelectedIds) {
		this.tempSelectedIds = tempSelectedIds;
	}
	public int getNextYear() {
		return nextYear;
	}
	public void setNextYear(int nextYear) {
		this.nextYear = nextYear;
	}
	public int getCurrentId() {
		return currentId;
	}
	public void setCurrentId(int currentId) {
		this.currentId = currentId;
	}
	public String getCurrentOrderLineRemain() {
		return currentOrderLineRemain;
	}
	public void setCurrentOrderLineRemain(String currentOrderLineRemain) {
		this.currentOrderLineRemain = currentOrderLineRemain;
	}
	public String getCurrentStartNextYear() {
		return currentStartNextYear;
	}
	public void setCurrentStartNextYear(String currentStartNextYear) {
		this.currentStartNextYear = currentStartNextYear;
	}

}
