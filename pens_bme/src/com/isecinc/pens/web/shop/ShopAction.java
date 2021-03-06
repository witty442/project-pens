package com.isecinc.pens.web.shop;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.shop.sub.ShopBillDetailAction;
import com.isecinc.pens.web.shop.sub.ShopBillSummaryAction;
import com.isecinc.pens.web.shop.sub.ShopPromotionAction;
import com.isecinc.pens.web.shop.sub.ShopSaleOutAction;
import com.isecinc.pens.web.shop.sub.ShopStockOnhandAction;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ShopAction extends I_Action {

	/** Constants  */
	final public static String P_MAYA_SALEOUT = "MayaSaleOut";
	final public static String P_MAYA_STOCK_ONHAND = "MayaStockOnhand";
	final public static String P_SHOP_PROM = "ShopPromotion";//MasterC4
	final public static String P_SHOP_BILL_DETAIL = "ShopBillDetail";
	
	final public static String P_TM_SALEOUT = "TMSaleOut";
	final public static String P_TM_STOCK_ONHAND = "TMStockOnhand";
	final public static String P_CH_SALEOUT = "CHINASaleOut";
	final public static String P_CH_STOCK_ONHAND = "CHINAStockOnhand";
	final public static String P_SP_SALEOUT = "SPSaleOut";
	final public static String P_SP_STOCK_ONHAND = "SPStockOnhand";
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		ShopForm aForm = (ShopForm) form;
		String pageName =Utils.isNull(request.getParameter("pageName"));
		String action = Utils.isNull(request.getParameter("pageAction"));
		ShopBean bean = new ShopBean();
		try {
			 logger.debug("prepare action["+action+"] pagaName:"+pageName);
			 if("new".equalsIgnoreCase(action)){
				 /** MAYA **/
				 if(P_MAYA_SALEOUT.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_PENSHOP_CODE);
					 bean.setStartDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 bean.setEndDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 }else if(P_MAYA_STOCK_ONHAND.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_PENSHOP_CODE);
				 /** TERMINAL **/
				 }else if(P_TM_SALEOUT.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_TERMINAL_CODE);
					 bean.setStartDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 bean.setEndDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 }else if(P_TM_STOCK_ONHAND.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_TERMINAL_CODE);
				 /** CHINA **/
				 }else if(P_CH_SALEOUT.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_CHINA_CODE);
					 bean.setStartDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 bean.setEndDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 }else if(P_CH_STOCK_ONHAND.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_CHINA_CODE);
					 
				 }else if(P_SP_SALEOUT.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_SP_CODE);
					 bean.setStartDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					 bean.setEndDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 }else if(P_SP_STOCK_ONHAND.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_SP_CODE);
					 
				/** ShopBill **/
				 }else if(P_SHOP_PROM.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 forward = "searchPromotion"; 
				 }else if(P_SHOP_BILL_DETAIL.equalsIgnoreCase(pageName)){
					 bean = new ShopBean();
					 bean.setCustGroup(PickConstants.STORE_TYPE_PENSHOP_CODE);
				 }
				  aForm.setBean(bean);
				  aForm.setResults(null);
				 
				 logger.debug("custGroup:"+ aForm.getBean().getCustGroup());
				 logger.debug("startDate:"+ aForm.getBean().getStartDate());
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ShopForm mayaForm = (ShopForm) form;
		try {
			logger.debug("prepare 2");
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ShopForm aForm = (ShopForm) form;
		User user = (User) request.getSession().getAttribute("user");
		logger.debug("pageName["+Utils.isNull(request.getParameter("pageName"))+"]");
		String forward ="search";
		try {
			 String queryStr= request.getQueryString();
			 if(queryStr.indexOf("d-") != -1){
			 	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
			 	System.out.println("queryStr:"+queryStr);
			 }
			 
			//Case link page in display no search again
			logger.debug("currentPage:"+request.getParameter(queryStr));
			if(request.getParameter(queryStr) != null){
				
			}else{
				/****** Sales Out ******/
				 if(P_MAYA_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 aForm = ShopSaleOutAction.search(request, aForm,user);
					 
				 }else if(P_TM_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					aForm = ShopSaleOutAction.search(request, aForm,user);
					
				 }else if(P_CH_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					aForm = ShopSaleOutAction.search(request, aForm,user);
					
				 }else if(P_SP_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				    aForm = ShopSaleOutAction.search(request, aForm,user);
				    
				 /****** Stock Onhand ******/
				 }else if(P_MAYA_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 aForm = ShopStockOnhandAction.search(request, aForm,user);
					 
				 }else if(P_TM_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 aForm = ShopStockOnhandAction.search(request, aForm,user);
					 
				 }else if(P_CH_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 aForm = ShopStockOnhandAction.search(request, aForm,user);
					 
				 }else if(P_SP_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 aForm = ShopStockOnhandAction.search(request, aForm,user);
					 
				 /******* Shop Promotion *******/
				 }else if(P_SHOP_PROM.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){  
					 String action = Utils.isNull(request.getParameter("action"));
					 if("back".equalsIgnoreCase(action)){
						 aForm.setBean(aForm.getBeanCriteria());
					 }
					   
					 aForm = ShopPromotionAction.search(aForm,request,response);
					 
					 forward = "searchPromotion";
				 
				 }else if(P_SHOP_BILL_DETAIL.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){  
					 String action = Utils.isNull(request.getParameter("action"));
					 if("back".equalsIgnoreCase(action)){
						 aForm.setBean(aForm.getBeanCriteria());
					 }
					 
					 if("DETAIL".equalsIgnoreCase(aForm.getBean().getReportType())){
					    aForm = ShopBillDetailAction.search(aForm,request,response);
					 }else  if("SUMMARY".equalsIgnoreCase(aForm.getBean().getReportType())){
						aForm = ShopBillSummaryAction.search(aForm,request,response);
					 }
					 
					 forward = "search";
				 }
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return forward;
	}
	
	public ActionForward searchDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchDetail");
		ShopForm aForm = (ShopForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="searchDetail";
		try {
			 //set old criteria
			 aForm.setBeanCriteria(aForm.getBean());
			 
			 logger.debug("PageAction:"+request.getParameter("pageName"));
			 if(P_SHOP_PROM.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				aForm = ShopPromotionAction.searchPromotionDetail(aForm,request,response);
				forward ="detailPromotion";
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward(forward);
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ShopForm aForm = (ShopForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		
		try {
			logger.debug("PageAction:"+request.getParameter("pageName"));
			
			 if(P_MAYA_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Sale MAYA Shop.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					htmlTable = ShopSaleOutAction.exportSaleOutToExcel(request,aForm,user,aForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if(P_MAYA_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Stock-Onhand at MAYA.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					htmlTable = ShopStockOnhandAction.exportToExcel(request,aForm,user,aForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if(P_SHOP_BILL_DETAIL.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Shop Bill.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					 if("DETAIL".equalsIgnoreCase(aForm.getBean().getReportType())){
						 htmlTable = ShopBillDetailAction.exportToExcel(request,aForm,user);	
					 }else  if("SUMMARY".equalsIgnoreCase(aForm.getBean().getReportType())){
						// htmlTable = ShopBillSummaryAction.exportToExcel(request,aForm,user);	
					 }
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if(P_TM_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Sale TERMINAL Shop.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					htmlTable = ShopSaleOutAction.exportSaleOutToExcelTM(request,aForm,user,aForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if(P_TM_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Stock-Onhand Terminal21-Asok.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					htmlTable = ShopStockOnhandAction.exportToExcel(request,aForm,user,aForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
				
			}else if(P_CH_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Sale Im Chaina Shop.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					htmlTable = ShopSaleOutAction.exportSaleOutToExcel(request,aForm,user,aForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if(P_CH_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Stock-Onhand Im Chaina.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					htmlTable = ShopStockOnhandAction.exportToExcel(request,aForm,user,aForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if(P_SP_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Sale Siam Premium Outlet Shop.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					htmlTable = ShopSaleOutAction.exportSaleOutToExcel(request,aForm,user,aForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if(P_SP_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Stock-Onhand Siam Premium Outlet.xls";
				if(aForm.getResults() != null && aForm.getResults().size() > 0){
					htmlTable = ShopStockOnhandAction.exportToExcel(request,aForm,user,aForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}
			
	        //logger.debug("fileName:"+fileName);
	        //fileName = Utils.toUnicodeChar(fileName);
	        //logger.debug("fileName:"+fileName);
	        //"data.xls";
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
		
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ShopForm aForm = (ShopForm) form;
		String forward ="";
		try {
			 if(P_SHOP_PROM.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 aForm = ShopPromotionAction.save(form, request, response);
				 forward ="detailPromotion";
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			return forward;
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return forward;
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ShopForm summaryForm = (ShopForm) form;
		try {
			
			 	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
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
