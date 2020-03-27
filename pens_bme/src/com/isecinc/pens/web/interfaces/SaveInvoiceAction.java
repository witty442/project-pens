package com.isecinc.pens.web.interfaces;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.SaveInvoiceBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SaveInvoiceDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SaveInvoiceAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	public static String STATUS_SAVE ="SV";
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		SaveInvoiceForm aForm = (SaveInvoiceForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				SaveInvoiceBean ad = new SaveInvoiceBean();
				ad.setCreateUser(user.getUserName());
				
			    List<References> productTypeList = new ArrayList<References>();
			    productTypeList.add(new References("",""));
			    productTypeList.addAll(SaveInvoiceDAO.getProductNameList());
			    request.getSession().setAttribute("productNameList",productTypeList); 
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				SaveInvoiceBean cri  =aForm.getBeanCriteria();
				SaveInvoiceBean b = SaveInvoiceDAO.search(cri,false);
				aForm.setResultsSearch(b.getItemList());
	            aForm.setBean(b);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		SaveInvoiceForm aForm = (SaveInvoiceForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			SaveInvoiceBean b = aForm.getBean();
			b = SaveInvoiceDAO.search(aForm.getBean(),false);
			aForm.setResultsSearch(b.getItemList());
            aForm.setBean(b);
            
			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	

	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		SaveInvoiceForm aForm = (SaveInvoiceForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new SaveInvoiceBean());
			
			SaveInvoiceBean ad = new SaveInvoiceBean();
			ad.setCreateUser(user.getUserName());
			aForm.setBean(ad);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		SaveInvoiceForm aForm = (SaveInvoiceForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SaveInvoiceForm summaryForm = (SaveInvoiceForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "detail";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SaveInvoiceForm orderForm = (SaveInvoiceForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			SaveInvoiceBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			//orderForm.setResults(aS.getItems());

			orderForm.setBean(aS);
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SaveInvoiceForm aForm = (SaveInvoiceForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SaveInvoiceBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Items
			String[] oracle_invoice_no =request.getParameterValues("oracle_invoice_no");
			String[] bill_10 =request.getParameterValues("bill_10");
			String[] bill_date =request.getParameterValues("bill_date");
			String[] busCode =request.getParameterValues("busCode");
			String[] deptCode =request.getParameterValues("deptCode");
			String[] productCode =request.getParameterValues("productCode");
		    if(oracle_invoice_no.length >0){
		    	for(int i=0;i<oracle_invoice_no.length;i++){
		    		SaveInvoiceDAO.updateORACLE_INVOICE_NO_ON_ICCHead(conn, user, oracle_invoice_no[i], bill_10[i], bill_date[i],busCode[i],deptCode[i],productCode[i]);
		    	}
		    }
			
			//Search Again
            SaveInvoiceBean b  = SaveInvoiceDAO.search(conn,h,true);
		    aForm.setResultsSearch(b.getItemList());
		    aForm.setBean(b);
		    
			conn.commit();
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "search";
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		SaveInvoiceForm aForm = (SaveInvoiceForm) form;
		try {
			aForm.setResults(new ArrayList<SaveInvoiceBean>());
			
			SaveInvoiceBean ad = new SaveInvoiceBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			//ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {

	}
	
}
