package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.MemberProduct;
import com.isecinc.pens.process.SequenceProcess;

/**
 * MMemberProduct Class
 * 
 * @author Aneak.t
 * @version $Id: MMemberProduct.java,v 1.0 13/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MMemberProduct extends I_Model<MemberProduct> {

	private static final long serialVersionUID = 353010938349001204L;

	public static String TABLE_NAME = "m_member_product";
	public static String COLUMN_ID = "Member_Product_ID";

	// Column
	private String[] columns = { "MEMBER_PRODUCT_ID", "PRODUCT_ID", "CUSTOMER_ID", "ORDER_QTY", "UOM_ID", "CREATED_BY",
			"UPDATED_BY" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MemberProduct find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, MemberProduct.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public MemberProduct[] search(String whereCause) throws Exception {
		List<MemberProduct> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberProduct.class);
		if (pos.size() == 0) return null;
		MemberProduct[] array = new MemberProduct[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param product
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(MemberProduct memberProduct, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (memberProduct.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = memberProduct.getId();
		}
		Object[] values = { id, memberProduct.getProduct().getId(), memberProduct.getCustomerId(),
				memberProduct.getOrderQty(), memberProduct.getUomId(), activeUserID, activeUserID };
		if (super.save(TABLE_NAME, columns, values, memberProduct.getId(), conn)) {
			memberProduct.setId(id);
		}
		return true;
	}

	/**
	 * Delete
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String deleteId, Connection conn) throws Exception {
		return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
	}

	/**
	 * Delete All Lines
	 * 
	 * @param orderId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean deleteAllLines(int customerId, Connection conn) throws Exception {
		List<MemberProduct> pos = new ArrayList<MemberProduct>();
		String deleteId = "";
		try {
			String whereCause = " AND CUSTOMER_ID = " + customerId;
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberProduct.class);
			for (MemberProduct l : pos)
				deleteId += "," + l.getId();
			if (deleteId.length() > 0) {
				deleteId = deleteId.substring(1);
				return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Look Up
	 */
	public List<MemberProduct> lookUp(int customerId) {
		List<MemberProduct> pos = new ArrayList<MemberProduct>();
		try {
			String whereCause = " AND CUSTOMER_ID = " + customerId + " ORDER BY MEMBER_PRODUCT_ID ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberProduct.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
