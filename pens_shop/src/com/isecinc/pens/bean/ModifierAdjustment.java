package com.isecinc.pens.bean;

import java.io.Serializable;

public class ModifierAdjustment implements Serializable{

	private static final long serialVersionUID = 648360296975104311L;

	private ModifierLine summary;
	private String adjustmentType;
	
	public ModifierLine getSummary() {
		return summary;
	}
	public void setSummary(ModifierLine summary) {
		this.summary = summary;
	}
	public String getAdjustmentType() {
		return adjustmentType;
	}
	public void setAdjustmentType(String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}
	
}
