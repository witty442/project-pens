package com.isecinc.pens.web.product;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBCPConnectionProvider;

import com.isecinc.core.Database;
import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;

/**
 * Product Action Class
 * 
 * @author Aneak.t
 * @version $Id: ProductAction.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ProductAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Product Prepare Form");
		ProductForm productForm = (ProductForm) form;
		Product product = null;

		try {
			product = new MProduct().find(id);
			if (product == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			productForm.setProduct(product);
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
		ProductForm productForm = (ProductForm) form;
		try {
			User user = (User) request.getSession().getAttribute("user");

			ProductCriteria criteria = getSearchCriteria(request, productForm.getCriteria(), this.getClass().toString());
			productForm.setCriteria(criteria);
			String whereCause = "";
			if (productForm.getProduct().getCode() != null && !productForm.getProduct().getCode().trim().equals("")) {
				whereCause += " AND CODE LIKE '%"
						+ productForm.getProduct().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%' ";
			}
			if (productForm.getProduct().getName() != null && !productForm.getProduct().getName().trim().equals("")) {
				whereCause += " AND NAME LIKE '%"
						+ productForm.getProduct().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%' ";
			}
			if (productForm.getProduct().getProductCategory().getSegId1() != 0) {
				whereCause += " AND PRODUCT_CATEGORY_ID IN (";
				whereCause += " select product_category_id ";
				whereCause += " from m_product_category ";
				whereCause += " where isactive = 'Y' ";
				whereCause += "  and seg_id1 = " + productForm.getProduct().getProductCategory().getSegId1();
				if (productForm.getProduct().getProductCategory().getSegId2() != 0)
					whereCause += "  and seg_id2 = " + productForm.getProduct().getProductCategory().getSegId2();
				if (productForm.getProduct().getProductCategory().getSegId3() != 0)
					whereCause += "  and seg_id3 = " + productForm.getProduct().getProductCategory().getSegId3();
				if (productForm.getProduct().getProductCategory().getSegId4() != 0)
					whereCause += "  and seg_id4 = " + productForm.getProduct().getProductCategory().getSegId4();
				if (productForm.getProduct().getProductCategory().getSegId5() != 0)
					whereCause += "  and seg_id5 = " + productForm.getProduct().getProductCategory().getSegId5();
				whereCause += ")";
			}

			whereCause += " AND PRODUCT_ID IN (";
			whereCause += " select distinct(pp.product_id) ";
			whereCause += " from m_product_price pp , m_pricelist pl ";
			whereCause += " where pp.pricelist_id = pl.pricelist_id ";
			//whereCause += " and pl.price_list_type = '" + user.getOrderType().getKey() + "' ";
			whereCause += " and pl.pricelist_id = " + user.getConfig().getPricelistId() + " ";
			whereCause += " and pp.isactive = 'Y' ";
			whereCause += ") ";

			whereCause += " ORDER BY CODE ";

			logger.debug("sql:"+whereCause);
			
			Product[] results = new MProduct().search(whereCause);
			productForm.setResults(results);

			if (results != null) {
				productForm.getCriteria().setSearchResult(results.length);
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
		ProductForm productForm = (ProductForm) form;
		Product product = null;
		ProductPrice[] productPrices = null;
		String productId = (String) request.getParameter("id");
		List<ProductPrice> pos = null;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			User user = (User) request.getSession().getAttribute("user");
			product = new MProduct().find(productId);
			String sql = "SELECT * FROM " + MProductPrice.TABLE_NAME + " WHERE PRODUCT_ID = " + productId;
			sql += " AND pricelist_id in (select pricelist_id from m_pricelist where pricelist_id = '"
					+ user.getConfig().getPricelistId() + "') ";
			pos = Database.query(sql, null, ProductPrice.class, conn);
			productPrices = new ProductPrice[pos.size()];
			productPrices = pos.toArray(productPrices);

			if (productPrices == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			product.setProductPrices(productPrices);
			productForm.setProduct(product);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward("viewprice");
	}

	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		ProductForm productForm = (ProductForm) form;
		productForm.setCriteria(new ProductCriteria());
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
