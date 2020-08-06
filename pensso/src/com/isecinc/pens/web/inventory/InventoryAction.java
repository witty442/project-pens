package com.isecinc.pens.web.inventory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.Database;
import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Inventory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MInventory;
import com.pens.util.DBCPConnectionProvider;

/**
 * Inventory Action Class
 * 
 * @author Aneak.t
 * @version $Id: InventoryAction.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InventoryAction extends I_Action {
	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Product Prepare Form");
		InventoryForm inventoryForm = (InventoryForm) form;
		Inventory inventory = null;

		try {
			inventory = new MInventory().find(id);
			if (inventory == null) {
				request.setAttribute("Message", InitialMessages.getMessages()
						.get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			inventoryForm.setInventory(inventory);
		} catch (Exception e) {
			request.setAttribute("Message",
					InitialMessages.getMessages().get(Messages.FETAL_ERROR)
							.getDesc()
							+ e.getMessage());
			throw e;
		}

		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		InventoryForm inventoryForm = (InventoryForm) form;
		Connection conn = null;
		String sql = "";
		Inventory[] results = null;
		try {
			User user = (User) request.getSession().getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);

			InventoryCriteria criteria = getSearchCriteria(request,
					inventoryForm.getCriteria(), this.getClass().toString());
			inventoryForm.setCriteria(criteria);

			sql = " SELECT * FROM m_inventory_onhand INV, m_product PRD WHERE INV.INVENTORY_ITEM_ID = PRD.PRODUCT_ID ";
			if (inventoryForm.getInventory().getProduct().getCode() != null
					&& !inventoryForm.getInventory().getProduct().getCode()
							.trim().equals("")) {
				sql += "  AND PRD.CODE LIKE '%"
						+ inventoryForm.getInventory().getProduct().getCode()
								.trim().replace("\'", "\\\'")
								.replace("\"", "\\\"") + "%' ";
			}
			String productName = inventoryForm.getInventory().getProduct()
					.getName().trim();
			if (productName != null && !productName.equals("")) {
				sql += "  AND PRD.NAME LIKE '%"
						+ inventoryForm.getInventory().getProduct().getName()
								.trim().replace("\'", "\\\'")
								.replace("\"", "\\\"") + "%' ";
			}
			if (inventoryForm.getInventory().getProduct().getProductCategory()
					.getId() != 0)
				sql += "  AND PRODUCT_CATEGORY_ID = "
						+ inventoryForm.getInventory().getProduct()
								.getProductCategory().getId();
			if (inventoryForm.getInventory().getSubInventory().getId() != 0) {
				sql += "  AND INV.SUB_INVENTORY_ID = "
						+ inventoryForm.getInventory().getSubInventory()
								.getId();
			} else {
				sql += "  AND (INV.SUB_INVENTORY_ID IN (select sub_inventory_id from m_sales_inventory where user_id = "
						+ user.getId() + ") ";
				sql += "  OR INV.SUB_INVENTORY_ID IN (select sub_inventory_id from m_sub_inventory where name = '"
						+ user.getCode() + "')) ";
			}
			sql += " ORDER BY INV.SUB_INVENTORY_ID, PRD.CODE ";

			List<Inventory> pos = Database.query(sql, null, Inventory.class,
					conn);

			List<Inventory> newPOS = new ArrayList<Inventory>();
			Inventory inventory = null;
			if (pos.size() != 0) {
				int oldProductId = 0;
				for (Inventory inv : pos) {
					if (oldProductId != inv.getProduct().getId()) {
						if (oldProductId != 0)
							newPOS.add(inventory);
						inventory = new Inventory();
						inventory.setUser(inv.getUser());
						inventory.setProduct(inv.getProduct());
						inventory.setSubInventory(inv.getSubInventory());
						oldProductId = inv.getProduct().getId();
					}
					// check UOM
					if (inv.getUom()
							.getId()
							.equalsIgnoreCase(inv.getProduct().getUom().getId())) {
						// primary uom
						inventory.setUom1(inv.getUom());
						inventory.setAvailableQty1(inv.getAvailableQty());
					} else {
						// secondary uom
						inventory.setUom2(inv.getUom());
						inventory.setAvailableQty2(inv.getAvailableQty());
					}
				}
				// add last record
				if (inventory != null)
					newPOS.add(inventory);
				MInventory minv = new MInventory();
				for (Inventory inv : newPOS) {
					inv = minv.calculateRemainOnSold(inv, user, conn);
				}

				// Include Sales Product not in Inventory Into Result
				List<Inventory> includeInventory = addSalesProductNotInStockData(
						inventoryForm, user, conn);

				newPOS.addAll(includeInventory);

				Collections.sort(newPOS);
				
				results = new Inventory[newPOS.size()];
				results = newPOS.toArray(results);
				inventoryForm.setResults(results);
				inventoryForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages()
						.get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message",
					InitialMessages.getMessages().get(Messages.FETAL_ERROR)
							.getDesc()
							+ e.getMessage());
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
			}
		}
		return "search";
	}

	private List<Inventory> addSalesProductNotInStockData(
			InventoryForm inventoryForm, User user, Connection conn)
			throws Exception {
		String productCode = inventoryForm.getInventory().getProduct()
				.getCode();
		String productName = inventoryForm.getInventory().getProduct()
				.getName().trim();
		int productCategoryId = inventoryForm.getInventory().getProduct()
				.getProductCategory().getId();
		int subInventoryId = inventoryForm.getInventory().getSubInventory()
				.getId();

		// FIND Sub Inventory ID
		String sql = "SELECT SUB_INVENTORY_ID FROM M_SUB_INVENTORY WHERE 1=1 ";

		if (subInventoryId != 0) {
			sql += "  AND SUB_INVENTORY_ID = " + subInventoryId;
		} else {
			sql += "  AND (SUB_INVENTORY_ID IN (select sub_inventory_id from m_sales_inventory where user_id = "
					+ user.getId() + ") ";
			sql += "  OR SUB_INVENTORY_ID IN (select sub_inventory_id from m_sub_inventory where name = '"
					+ user.getCode() + "')) ";
		}

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next())
			subInventoryId = rs.getInt("SUB_INVENTORY_ID");

		sql = "SELECT 0 as INVENTORY_ONHAND_ID , od.USER_ID , ol.PRODUCT_ID as INVENTORY_ITEM_ID , ol.UOM_ID as UOM_ID , 0 as AVAILABLE_QTY ,"
				+ subInventoryId
				+ " as SUB_INVENTORY_ID "
				+ "FROM M_Product pd , T_ORDER_LINE ol , T_Order od "
				+ "WHERE pd.PRODUCT_ID = ol.PRODUCT_ID "
				+ "AND od.ORDER_ID = ol.ORDER_ID "
				+ "AND od.doc_status='SV' "
				+ "AND od.order_date = CURRENT_DATE "
				+ "AND od.user_id = "
				+ user.getId()
				+ " AND ol.iscancel <> 'Y' AND od.order_type = 'CS' "
				+ "AND ol.PRODUCT_ID NOT IN (SELECT io.INVENTORY_ITEM_ID FROM M_INVENTORY_ONHAND io WHERE io.Sub_Inventory_ID = "
				+ subInventoryId + ") ";

		if (!StringUtils.isEmpty(productCode))
			sql += "  AND pd.CODE LIKE '%"
					+ productCode.replace("\'", "\\\'").replace("\"", "\\\"")
					+ "%' ";

		if (!StringUtils.isEmpty(productName))
			sql += "  AND pd.NAME LIKE '%"
					+ productName.trim().replace("\'", "\\\'")
							.replace("\"", "\\\"") + "%' ";

		if (productCategoryId != 0)
			sql += "  AND pd.PRODUCT_CATEGORY_ID = " + productCategoryId;

		List<Inventory> pos = Database.query(sql, null, Inventory.class, conn);

		List<Inventory> newPOS = new ArrayList<Inventory>();
		Inventory inventory = null;
		if (pos.size() != 0) {
			int oldProductId = 0;
			for (Inventory inv : pos) {
				if (oldProductId != inv.getProduct().getId()) {
					if (oldProductId != 0)
						newPOS.add(inventory);
					inventory = new Inventory();
					inventory.setUser(inv.getUser());
					inventory.setProduct(inv.getProduct());
					inventory.setSubInventory(inv.getSubInventory());
					oldProductId = inv.getProduct().getId();
				}
				// check UOM
				if (inv.getUom().getId()
						.equalsIgnoreCase(inv.getProduct().getUom().getId())) {
					// primary uom
					inventory.setUom1(inv.getUom());
					inventory.setAvailableQty1(inv.getAvailableQty());
				} else {
					// secondary uom
					inventory.setUom2(inv.getUom());
					inventory.setAvailableQty2(inv.getAvailableQty());
				}
			}
			// add last record
			if (inventory != null)
				newPOS.add(inventory);
			MInventory minv = new MInventory();
			for (Inventory inv : newPOS) {
				inv = minv.calculateRemainOnSold(inv, user, conn);
			}
		}

		return newPOS;
	}

	/**
	 * Sub Inventory Search
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward searchSub(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		InventoryForm inventoryForm = (InventoryForm) form;
		Connection conn = null;
		String sql = "";
		Inventory[] results = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);

			InventoryCriteria criteria = getSearchCriteria(request,
					inventoryForm.getCriteria(), this.getClass().toString());
			inventoryForm.setCriteria(criteria);
			sql = " SELECT * FROM m_inventory_onhand INV, m_product PRD WHERE INV.INVENTORY_ITEM_ID = PRD.PRODUCT_ID ";
			if (inventoryForm.getInventory().getProduct().getCode() != null
					&& !inventoryForm.getInventory().getProduct().getCode()
							.equals("")) {
				sql += "  AND PRD.CODE LIKE '%"
						+ inventoryForm.getInventory().getProduct().getCode()
						+ "%' ";
			}
			String productName = inventoryForm.getInventory().getProduct()
					.getName();
			if (productName != null && !productName.equals("")) {
				if (productName.split(" ").length > 1) {
					sql += "  AND PRD.NAME LIKE '%" + productName.split(" ")[0]
							+ "%' ";
					sql += "  AND PRD.DESCRIPTION LIKE '%"
							+ productName.split(" ")[1] + "%' ";
				} else {
					sql += "  AND PRD.NAME LIKE '%"
							+ inventoryForm.getInventory().getProduct()
									.getName() + "%' ";
					sql += "  OR PRD.DESCRIPTION LIKE '%"
							+ inventoryForm.getInventory().getProduct()
									.getName() + "%' ";
				}
			}
			sql += "  AND INV.USER_ID IS NULL ";
			// sql += "  AND INV.SUB_INVENTORY_ID = "
			// + ((User)
			// request.getSession().getAttribute("user")).getSubInventoryId();
			sql += " ORDER BY INV.INVENTORY_ONHAND_ID ";

			List<Inventory> pos = Database.query(sql, null, Inventory.class,
					conn);
			if (pos.size() != 0) {
				results = new Inventory[pos.size()];
				results = pos.toArray(results);
				inventoryForm.setResults(results);
				inventoryForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages()
						.get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message",
					InitialMessages.getMessages().get(Messages.FETAL_ERROR)
							.getDesc()
							+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
			}
		}
		return mapping.findForward("sub-search");
	}

	/**
	 * Clear Form Sub
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward clearFormSub(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Clear Form " + this.getClass());
		String searchKey = (String) request.getSession(true).getAttribute(
				this.getClass().toString());
		if (searchKey != null) {
			request.getSession(true).removeAttribute(searchKey);
			request.removeAttribute(this.getClass().toString());
		}
		// Clear Criteria
		try {
			setNewCriteria(form);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("sub-search");
	}

	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		InventoryForm inventoryForm = (InventoryForm) form;
		inventoryForm.setCriteria(new InventoryCriteria());
	}

	@Override
	protected String save(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
