package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Inventory;
import com.isecinc.pens.bean.PriceList;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.User;

/**
 * MInventory Class
 * 
 * @author Aneak.t
 * @version $Id: MInventory.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MInventory extends I_Model<Inventory> {

	private static final long serialVersionUID = 5064577897335433201L;

	public static String TABLE_NAME = "m_inventory_onhand";
	public static String COLUMN_ID = "Inventory_Onhand_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Inventory find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Inventory.class);
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
	public Inventory[] search(String whereCause) throws Exception {
		List<Inventory> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Inventory.class);
		if (pos.size() == 0) return null;
		Inventory[] array = new Inventory[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Calculate
	 * 
	 * @param inventory
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public Inventory calculateRemainOnSold(Inventory inventory, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		try {
			stmt = conn.createStatement();
			String sql = "";
			sql = " select uom_id, sum(qty) qty ";
			sql += " from t_order_line ";
			sql += "where 1=1 ";
			sql += "  and ORDER_ID IN ( ";
			sql += "  select ORDER_ID  ";
			sql += "  from t_order  ";
			sql += "  where doc_status='SV' ";
			sql += "    and order_date = CURRENT_DATE ";
			sql += "    and user_id = " + inventory.getUser().getId();
			sql += "    and product_id = " + inventory.getProduct().getId();
			// Aneak.t
			sql += " and iscancel <> 'Y' ";

			sql += "    and order_type = '" + inventory.getUser().getOrderType().getKey() + "' ";
			sql += "  ) ";
			sql += "group by uom_id ";
			rst = stmt.executeQuery(sql);
			UOM baseUOM = null;
			UOM uom = null;
			while (rst.next()) {
				if (rst.getString("uom_id").trim().equalsIgnoreCase(inventory.getProduct().getUom().getId())) {
					// primary uom
					inventory.setSalesQty1(inventory.getSalesQty1() + rst.getDouble("qty"));
					inventory.setRemainQty1(inventory.getAvailableQty1() - inventory.getSalesQty1());
					if (baseUOM == null) baseUOM = new MUOM().find(rst.getString("uom_id"));
				} else {
					// sub uom
					inventory.setSalesQty2(inventory.getSalesQty2() + rst.getDouble("qty"));
					inventory.setRemainQty2(inventory.getAvailableQty2() - inventory.getSalesQty2());
					if (uom == null) uom = new MUOM().find(rst.getString("uom_id"));
				}
			}

			if (baseUOM == null) baseUOM = inventory.getProduct().getUom();
			if (uom == null) {
				PriceList ppl = new MPriceList().getCurrentPriceList(user.getOrderType().getKey());
				List<ProductPrice> ppls = new MProductPrice().lookUp(inventory.getProduct().getId(), ppl.getId());
				for (ProductPrice pp : ppls) {
					if (!pp.getUom().getId().equalsIgnoreCase(baseUOM.getId())) {
						uom = pp.getUom();
						break;
					}
				}
			}

			double capacity = new MUOMConversion().getCapacity(baseUOM, uom, inventory.getProduct());

			// re-calculate AVAILABLE/SALES/REMAIN
			// avail
			int avail1 = new Double(inventory.getAvailableQty1()).intValue();
			double a = inventory.getAvailableQty1() - avail1;
			double k = a * capacity;
			int avail2 = Integer.parseInt(new DecimalFormat("##0").format(k));
			inventory.setAvailableQty1(avail1);
			inventory.setAvailableQty2(inventory.getAvailableQty2() + avail2);

			// sales
			int sales1 = new Double(inventory.getSalesQty1()).intValue();
			a = inventory.getSalesQty1() - sales1;
			k = a * capacity;
			int sales2 = Integer.parseInt(new DecimalFormat("##0").format(k));
			inventory.setSalesQty1(sales1);
			inventory.setSalesQty2(inventory.getSalesQty2() + sales2);

			double totalAvail = (inventory.getAvailableQty1() * capacity) + inventory.getAvailableQty2();
			double totalSales = (inventory.getSalesQty1() * capacity) + inventory.getSalesQty2();
			double totalRemain = totalAvail - totalSales;

			inventory.setRemainQty1(new Double(totalRemain / capacity).intValue());
			inventory.setRemainQty2(totalRemain % capacity);

			// // remain
			// int remain1 = new Double(inventory.getRemainQty1()).intValue();
			// a = inventory.getRemainQty1() - remain1;
			// k = a * capacity;
			// int remain2 = Integer.parseInt(new DecimalFormat("##0").format(k));
			// inventory.setRemainQty1(remain1);
			// inventory.setRemainQty2(inventory.getRemainQty2() + remain2 + avail2);
			//
			// int useQty1 = 0;
			// double remainQty2 = 0;
			// double salesQty2 = 0;
			// if (inventory.getSalesQty2() > capacity) {
			// useQty1 = new Double(inventory.getSalesQty2() / capacity).intValue();
			// useQty1++;
			// salesQty2 = (inventory.getSalesQty2() % capacity);
			// if (salesQty2 == capacity) {
			// salesQty2 = 0;
			// useQty1--;
			// }
			// } else {
			// if (inventory.getSalesQty2() > inventory.getAvailableQty2()) {
			// useQty1 = new Double(inventory.getSalesQty2() / capacity).intValue();
			// useQty1++;
			// remainQty2 = capacity - (inventory.getSalesQty2() % capacity);
			// if (remainQty2 == capacity) {
			// remainQty2 = 0;
			// useQty1--;
			// }
			// inventory.setRemainQty1(inventory.getAvailableQty1() - inventory.getSalesQty1() - useQty1);
			// inventory.setRemainQty2(remainQty2);
			// }
			//
			// // else {
			// // if (inventory.getSalesQty2() != 0) {
			// // remainQty2 = capacity - inventory.getSalesQty2();
			// // useQty1 = 1;
			// // inventory.setRemainQty1(inventory.getAvailableQty1() - inventory.getSalesQty1() - useQty1);
			// // inventory.setRemainQty2(remainQty2);
			// // } else {
			// // inventory.setRemainQty1(inventory.getAvailableQty1() - inventory.getSalesQty1());
			// // inventory.setRemainQty2(inventory.getAvailableQty2() - inventory.getSalesQty2());
			// // }
			// // }
			// }
			// inventory.setRemainQty1(inventory.getAvailableQty1() - inventory.getSalesQty1() - useQty1);
			// inventory.setRemainQty2(inventory.getAvailableQty2() - inventory.getSalesQty2() - salesQty2);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		return inventory;
	}
}
