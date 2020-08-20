package com.isecinc.pens.bean;

import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.pens.util.ConvertNullUtil;

/**
 * Sub Inventory
 * 
 * @author atiz.b
 * @version $Id: SubInventory.java,v 1.0 29/10/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SubInventory extends I_PO {

	private static final long serialVersionUID = -2931447760756328817L;

	/**
	 * Derault Constructor
	 */
	public SubInventory() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public SubInventory(ResultSet rst) throws Exception {
		setId(rst.getInt("SUB_INVENTORY_ID"));
		setName(rst.getString("NAME"));
		setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
		setType(ConvertNullUtil.convertToString(rst.getString("TYPE")).trim());
		setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
		setActive(rst.getString("ISACTIVE").trim());

		setDisplayLabel();
	}

	/**
	 * Set Label
	 */
	protected void setDisplayLabel() throws Exception {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("Sub Inventory[%s] [%s]-[%s] Type[%s]", getId(), getName(), getDescription(), getType());
	}

	/** SUB_INVENTORY_ID */
	private int id;

	/** NAME */
	private String name;

	/** DESCRIPTION */
	private String description;

	/** TYPE */
	private String type;

	/** TERRITORY */
	private String territory;

	/** ISACTIVE */
	private String active;

	/** Transient SELECTED */
	private String selected = "N";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTerritory() {
		return territory;
	}

	public void setTerritory(String territory) {
		this.territory = territory;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

}
