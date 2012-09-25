package com.isecinc.pens.web.salestarget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.SalesTarget;
import com.isecinc.pens.bean.SalesTargetProduct;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MSalesTarget;
import com.isecinc.pens.model.MSalesTargetProduct;

/**
 * Sales Target Action Class
 * 
 * @author Aneak.t
 * @version $Id: SalesTargetAction.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class SalesTargetAction extends I_Action {

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Sales Target Prepare Form");
		SalesTargetForm salesTargetForm = (SalesTargetForm) form;
		SalesTargetProduct[] salesTargetProduct = null;
		SalesTarget salesTarget = null;
		String whereCause = "";

		try {

			whereCause += " AND SALES_TARGET_ID = " + id;
			salesTargetProduct = new MSalesTargetProduct().search(whereCause);
			salesTarget = new MSalesTarget().find(id);

			if (salesTargetProduct == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}

			if (salesTargetProduct != null) {
				salesTargetForm.getCriteria().setSearchResult(salesTargetProduct.length);
				salesTargetForm.setSalesTargetProduct(salesTargetProduct);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			salesTargetForm.setSalesTarget(salesTarget);
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
		SalesTargetForm salesTargetForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");

		try {
			SalesTargetCriteria criteria = getSearchCriteria(request, salesTargetForm.getCriteria(), this.getClass()
					.toString());
			salesTargetForm.setCriteria(criteria);
			String whereCause = "";
			whereCause += " AND YEAR = " + salesTargetForm.getSalesTarget().getYear();
			whereCause += " AND USER_ID = " + user.getId();
			whereCause += " ORDER BY YEAR ";

			SalesTarget[] results = new MSalesTarget().search(whereCause);
			salesTargetForm.setResults(results);

			if (results != null) {
				salesTargetForm.getCriteria().setSearchResult(results.length);
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

	@Override
	protected void setNewCriteria(ActionForm form) {
		SalesTargetForm salesTargetForm = (SalesTargetForm) form;
		salesTargetForm.setCriteria(new SalesTargetCriteria());
	}

	@Override
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
