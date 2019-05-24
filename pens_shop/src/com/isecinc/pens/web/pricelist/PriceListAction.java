package com.isecinc.pens.web.pricelist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PriceList;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProductPrice;

/**
 * Price List Action Class
 * 
 * @author Aneak.t
 * @version $Id: PriceListAction.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class PriceListAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Price List Prepare Form");
		PriceListForm pricelistForm = (PriceListForm) form;
		PriceList pricelist = null;

		try {
			pricelist = new MPriceList().find(id);
			if (pricelist == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			pricelistForm.setPriceList(pricelist);

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
		logger.debug("Price List Search");
		PriceListForm pricelistForm = (PriceListForm) form;
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			PriceListCriteria criteria = getSearchCriteria(request, pricelistForm.getCriteria(), this.getClass()
					.toString());
			pricelistForm.setCriteria(criteria);
			String whereCause = "";
			if (pricelistForm.getPriceList().getName() != null
					&& !pricelistForm.getPriceList().getName().trim().equals("")) {
				whereCause += " AND NAME LIKE '%"
						+ pricelistForm.getPriceList().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (pricelistForm.getPriceList().getEffectiveDate() != null
					&& !pricelistForm.getPriceList().getEffectiveDate().trim().equals("")) {
				whereCause += " AND EFFECTIVE_DATE >= '"
						+ DateToolsUtil.convertToTimeStamp(pricelistForm.getPriceList().getEffectiveDate().trim())
						+ "' ";
			}
			if (pricelistForm.getPriceList().getEffectiveToDate() != null
					&& !pricelistForm.getPriceList().getEffectiveToDate().trim().equals("")) {
				whereCause += " AND EFFECTIVETO_DATE <= '"
						+ DateToolsUtil.convertToTimeStamp(pricelistForm.getPriceList().getEffectiveToDate().trim())
						+ "' ";
			}
			if (pricelistForm.getPriceList().getIsActive() != null
					&& !pricelistForm.getPriceList().getIsActive().equals("")) {
				whereCause += " AND ISACTIVE = '" + pricelistForm.getPriceList().getIsActive() + "' ";
			}
			//whereCause += " AND PRICE_LIST_TYPE = '" + user.getOrderType().getKey() + "' ";
			
			//Get from app config
			whereCause += " AND PRICELIST_ID = " + user.getConfig().getPricelistId() + " ";
			
			whereCause += " ORDER BY NAME ";

			logger.debug("whereClasue: \n"+whereCause);
			
			PriceList[] results = new MPriceList().search(whereCause);
			pricelistForm.setResults(results);
			if (results != null) {
				pricelistForm.getCriteria().setSearchResult(results.length);
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
	 * View Price
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward viewprice(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("view price");
		PriceListForm priceListForm = (PriceListForm) form;
		String pricelistId = (String) request.getParameter("id");
		PriceList priceList = null;

		try {
			priceList = new MPriceList().find(pricelistId);
			String whereCause = "";
			whereCause += "  and PRICELIST_ID = " + pricelistId;
			//whereCause += "  and date_format(effective_date,'%Y%m%d') <= date_format(current_timestamp,'%Y%m%d') ";
			//whereCause += "  and date_format(effectiveto_date,'%Y%m%d') >= date_format(current_timestamp,'%Y%m%d') ";
			whereCause += "  and isactive = 'Y' ";
			whereCause += " ORDER BY PRODUCT_ID, UOM_ID ";

			ProductPrice[] productPrices = new MProductPrice().search(whereCause);

			if (productPrices == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			priceList.setProductPrices(productPrices);
			priceListForm.setPriceList(priceList);

		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		}

		return mapping.findForward("viewprice");
	}

	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		PriceListForm pricelistForm = (PriceListForm) form;
		pricelistForm.setCriteria(new PriceListCriteria());
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
