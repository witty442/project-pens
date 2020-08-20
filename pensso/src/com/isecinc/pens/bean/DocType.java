package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.pens.util.ConvertNullUtil;

/**
 * DocType Class
 * 
 * @author Atiz.b
 * @version $Id: DocType.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class DocType extends I_PO implements Serializable {

	private static final long serialVersionUID = 5962021177844123534L;

	/**
	 * Default Constructor
	 */
	public DocType() {}

	/**
	 * Default Constructor
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public DocType(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("DOCTYPE_ID"));
		setName(rst.getString("NAME").trim());
		setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
		setActive(rst.getString("ISACTIVE"));

		// set display label
		setDisplayLabel();
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	/** ID */
	private int id;

	/** NAME */
	private String name;

	/** DESCRIPTION */
	private String description;

	/** ACTIVE */
	private String active;

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

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

}
