package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;

/**
 * Product Category
 * 
 * @author Aneak.t
 * @version $Id: ProductCategory.java,v 1.0 06/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ProductCategory extends I_PO implements Serializable {

	private static final long serialVersionUID = -3406329561777118645L;

	public static int SEGMENT1 = 1;
	public static int SEGMENT2 = 2;
	public static int SEGMENT3 = 3;
	public static int SEGMENT4 = 4;
	public static int SEGMENT5 = 5;

	/**
	 * Default Constructor
	 */
	public ProductCategory() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public ProductCategory(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("PRODUCT_CATEGORY_ID"));
		setName(rst.getString("NAME"));
		setIsActive(rst.getString("ISACTIVE"));

		// set display label
		setDisplayLabel();

	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	public String toString() {
		return String.format("Product Category[%s]-%s", getId(), getName());
	}

	private int id;
	private String name;
	private String isActive;

	private int segId1;
	private String segName1;
	private int segId2;
	private String segName2;
	private int segId3;
	private String segName3;
	private int segId4;
	private String segName4;
	private int segId5;
	private String segName5;

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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public int getSegId1() {
		return segId1;
	}

	public void setSegId1(int segId1) {
		this.segId1 = segId1;
	}

	public String getSegName1() {
		return segName1;
	}

	public void setSegName1(String segName1) {
		this.segName1 = segName1;
	}

	public int getSegId2() {
		return segId2;
	}

	public void setSegId2(int segId2) {
		this.segId2 = segId2;
	}

	public String getSegName2() {
		return segName2;
	}

	public void setSegName2(String segName2) {
		this.segName2 = segName2;
	}

	public int getSegId3() {
		return segId3;
	}

	public void setSegId3(int segId3) {
		this.segId3 = segId3;
	}

	public String getSegName3() {
		return segName3;
	}

	public void setSegName3(String segName3) {
		this.segName3 = segName3;
	}

	public int getSegId4() {
		return segId4;
	}

	public void setSegId4(int segId4) {
		this.segId4 = segId4;
	}

	public String getSegName4() {
		return segName4;
	}

	public void setSegName4(String segName4) {
		this.segName4 = segName4;
	}

	public int getSegId5() {
		return segId5;
	}

	public void setSegId5(int segId5) {
		this.segId5 = segId5;
	}

	public String getSegName5() {
		return segName5;
	}

	public void setSegName5(String segName5) {
		this.segName5 = segName5;
	}

}
