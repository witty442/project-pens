package com.isecinc.pens.bean;

import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;

public class ModifierRelation extends I_PO {

	private static final long serialVersionUID = 8358128152880139426L;

	/**
	 * Default Constructor
	 * 
	 */
	public ModifierRelation() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public ModifierRelation(ResultSet rst) throws Exception {
		setId(rst.getInt("RELATION_MODIFIER_ID"));
		setModifierType(rst.getString("MODIFIER_TYPE").trim());
		setLineFromId(rst.getInt("MODIFIER_LINE_FROM_ID"));
		setLineToId(rst.getInt("MODIFIER_LINE_TO_ID"));
	}

	protected void setDisplayLabel() throws Exception {

	}

	private int id;
	private String modifierType;
	private int lineFromId;
	private int lineToId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModifierType() {
		return modifierType;
	}

	public void setModifierType(String modifierType) {
		this.modifierType = modifierType;
	}

	public int getLineFromId() {
		return lineFromId;
	}

	public void setLineFromId(int lineFromId) {
		this.lineFromId = lineFromId;
	}

	public int getLineToId() {
		return lineToId;
	}

	public void setLineToId(int lineToId) {
		this.lineToId = lineToId;
	}

}
