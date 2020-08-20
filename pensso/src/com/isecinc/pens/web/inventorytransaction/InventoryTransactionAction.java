package com.isecinc.pens.web.inventorytransaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.InventoryTransaction;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MInventoryTransaction;
import com.pens.util.DateToolsUtil;

/**
 * Inventory Transaction Action Class
 * 
 * @author Aneak.t
 * @version $Id: InventoryTransactionAction.java,v 1.0 11/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InventoryTransactionAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Product Prepare Form");
		InventoryTransactionForm inventoryTransactionForm = (InventoryTransactionForm) form;
		InventoryTransaction inventoryTransaction = null;

		try {
			inventoryTransaction = new MInventoryTransaction().find(id);
			if (inventoryTransaction == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			inventoryTransactionForm.setInventoryTransaction(inventoryTransaction);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}

		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		InventoryTransactionForm inventoryTransactionForm = (InventoryTransactionForm) form;

		try {
			InventoryTransactionCriteria criteria = getSearchCriteria(request, inventoryTransactionForm.getCriteria(),
					this.getClass().toString());
			inventoryTransactionForm.setCriteria(criteria);
			String whereCause = "";
			if (inventoryTransactionForm.getInventoryTransaction().getMovementDateFrom() != null
					&& !inventoryTransactionForm.getInventoryTransaction().getMovementDateFrom().equals("")) {
				whereCause += " AND MOVEMENT_DATE >= '"
						+ DateToolsUtil.convertToTimeStamp(inventoryTransactionForm.getInventoryTransaction().getMovementDateFrom()) + "'";
			}
			if (inventoryTransactionForm.getInventoryTransaction().getMovementDateTo() != null
					&& !inventoryTransactionForm.getInventoryTransaction().getMovementDateTo().equals("")) {
				whereCause += " AND MOVEMENT_DATE <= '"
						+ DateToolsUtil.convertToTimeStamp(inventoryTransactionForm.getInventoryTransaction().getMovementDateTo()) + "'";
			}
			if (inventoryTransactionForm.getInventoryTransaction().getTransactionType() != null
					&& !inventoryTransactionForm.getInventoryTransaction().getTransactionType().equals("")) {
				whereCause += " AND TRANSACTION_TYPE = '"
						+ inventoryTransactionForm.getInventoryTransaction().getTransactionType() + "'";
			}
			whereCause += " ORDER BY MOVEMENT_DATE DESC ";

			InventoryTransaction[] results = new MInventoryTransaction().search(whereCause);
			inventoryTransactionForm.setResults(results);

			if (results != null) {
				inventoryTransactionForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		InventoryTransactionForm inventoryTransactionForm = (InventoryTransactionForm) form;
		inventoryTransactionForm.setCriteria(new InventoryTransactionCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
