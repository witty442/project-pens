package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.dataimports.bean.IMemberProductBean;

/**
 * ICustomer Class
 * 
 * @author Danai.K
 * @version $Id: ICustomer.java,v 1.0 20/12/2010 00:00:00 Danai.K Exp $
 */

public class IMemberProduct extends I_Model<IMemberProductBean> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "i_member_product";
	public static String COLUMN_ID = "IMEMBER_PRODUCT_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "ICUSTOMER_ID", "PRODUCT_ID", "ORDER_QTY", "UOM_ID", "IMPORTED",
			"IMPORTED_DETAIL",
			/* "ISACTIVE", *//* "CREATED", */"CREATED_BY", /* "UPDATED", */"UPDATED_BY" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public IMemberProductBean find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, IMemberProductBean.class);
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
	public IMemberProductBean[] search(String whereCause) throws Exception {
		List<IMemberProductBean> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, IMemberProductBean.class);
		if (pos.size() == 0) return null;
		IMemberProductBean[] array = new IMemberProductBean[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param memberProduct
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(IMemberProductBean memberProduct, int activeUserID, Connection conn) throws Exception {
		/*
		 * int id = 0; if (memberProduct.getId() == 0) { id = SequenceProcess.getNextValue(TABLE_NAME); } else { id =
		 * memberProduct.getId(); }
		 */
		int id = memberProduct.getId();

		Object[] values = { id, memberProduct.getCustomerId(), memberProduct.getProduct().getId(),
				memberProduct.getOrderQty(), memberProduct.getProduct().getUom().getId(),
				ConvertNullUtil.convertToString(memberProduct.getImported()),
				ConvertNullUtil.convertToString(memberProduct.getImportedDetail()),
				/* memberProduct.getIsActive(), *//* memberProduct.getCreated(), */activeUserID, /*
																								 * memberProduct.getUpdated
																								 * (),
																								 */activeUserID };

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, COLUMN_ID, memberProduct.getId() + "", 0, conn)) {
			// update
			super.save(TABLE_NAME, columns, values, memberProduct.getId(), conn);
		} else {
			// insert
			super.save(TABLE_NAME, columns, values, 0, conn);
		}

		return true;
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
		List<IMemberProductBean> pos = new ArrayList<IMemberProductBean>();
		String deleteId = "";
		try {
			String whereCause = " AND ICUSTOMER_ID = " + customerId;
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, IMemberProductBean.class);
			for (IMemberProductBean l : pos)
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
}
