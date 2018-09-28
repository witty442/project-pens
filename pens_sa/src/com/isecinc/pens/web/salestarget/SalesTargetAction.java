package com.isecinc.pens.web.salestarget;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBConnection;
import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SalesTargetAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		SalesTargetForm aForm = (SalesTargetForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String pageName ="";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			pageName = Utils.isNull(request.getParameter("pageName"));
			if("new".equals(action)){
				pageName = Utils.isNull(request.getParameter("pageName"));
				request.getSession().setAttribute("RESULTS",null);
				SalesTargetBean sales = new SalesTargetBean();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
				//init Connection
				conn = DBConnection.getInstance().getConnection();
				
				if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){
					SalesTargetControlPage.prepareSearchMKT(request, conn, user,pageName);
					
				}else if (SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName) ){
					logger.debug("Prepare MT");
					SalesTargetControlPage.prepareSearchMT(request, conn, user,pageName);
					SalesTargetBean s = SalesTargetUtils.searchSalesrepCodeByUserName(conn,user);
					if(s != null){
						sales.setSalesrepId(s.getSalesrepId());
						sales.setSalesrepCode(s.getSalesrepCode());
					}
					
				}else if (SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName) ){
					SalesTargetControlPage.prepareSearchMTMGR(request, conn, user,pageName);
					
				}else if (SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName) ){
					SalesTargetControlPage.prepareSearchReportSalesTarget(request, conn, user,pageName);
				}else if (SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName) ){
					SalesTargetControlPage.prepareSearchReportSalesTargetAll(request, conn, user,pageName);
				}
				
				aForm.setBean(sales);
			}else if("back".equals(action)){
				pageName = aForm.getPageName();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				//Search By MKT
				if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){
				    SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMKT(aForm.getBeanCriteria(),user,pageName);
				    aForm.setBean(salesReuslt);
				    request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMKT(request,aForm.getBean(),user));
				   
				}else if (SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){
					SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMT(aForm.getBeanCriteria(),user,pageName);
				    aForm.setBean(salesReuslt);
				    request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMT(request,aForm.getBean(),user));
				    
				}else if (SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){
					SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMT(aForm.getBeanCriteria(),user,pageName);
				    aForm.setBean(salesReuslt);
				    request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMTMGR(request,aForm.getBean(),user));
				}
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean foundData = false;
		String pageName = aForm.getPageName();
		try {
			logger.debug("search Head :pageName["+pageName+"]");
			//save old criteria
			aForm.setBeanCriteria(SalesTargetControlPage.convertToCriteriaBean(pageName,aForm.getBean()));
			
			//Search By Role MKT(Marketing)
			if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){
				SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMKT(aForm.getBean(),user,pageName);
			    aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
				  request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMKT(request,aForm.getBean(),user));
				  foundData = true;
			   }
			 //Search By Role MT(sales)
			}else if (SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){
			   SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMT(aForm.getBean(),user,pageName);
			   aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
			     request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMT(request,aForm.getBean(),user));
			     foundData = true;
			  }
			  //Search By Role Manager MT(sales)
			}else if (SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){
			   SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMT(aForm.getBean(),user,pageName);
			   aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
			     request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMTMGR(request,aForm.getBean(),user));
			     foundData = true;
			  }
			  //Search Report
			}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){
				 StringBuffer resultHtmlTable = SalesTargetReport.searchReport(aForm.getBean());
				 if(resultHtmlTable != null){
					  request.getSession().setAttribute("RESULTS",resultHtmlTable);
					  foundData = true;
				 }
			// Search Report All 
			}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName)){
				 StringBuffer resultHtmlTable = SalesTargetReport.searchReportAll(aForm.getBean());
				 if(resultHtmlTable != null){
					  request.getSession().setAttribute("RESULTS",resultHtmlTable);
					  foundData = true;
				 }
			}
			
			if(foundData==false){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   request.getSession().setAttribute("RESULTS",null);
			}
			logger.debug("pageName:"+aForm.getPageName());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		SalesTargetForm aForm = (SalesTargetForm) form;
		String pageName = aForm.getPageName();
		//Search By MKT
		if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName) ){
		   return SalesTargetControlPage.prepareDetailMKT(form, request, response);
		}else if (SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName) ){
		   return SalesTargetControlPage.prepareDetailMT(form, request, response);
		}else if (SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){
		   return SalesTargetControlPage.prepareDetailMTMGR(form, request, response);
		}
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			h.setStatus(SalesTargetConstants.STATUS_OPEN);
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<SalesTargetBean> itemList = new ArrayList<SalesTargetBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] itemCode = request.getParameterValues("itemCode");
			String[] itemId = request.getParameterValues("itemId");
			String[] targetQty = request.getParameterValues("targetQty");
			String[] targetAmount = request.getParameterValues("targetAmount");
			String[] price = request.getParameterValues("price");
			String[] orderAmt12Month = request.getParameterValues("orderAmt12Month");
			String[] orderAmt3Month = request.getParameterValues("orderAmt3Month");
			String[] remark = request.getParameterValues("remark");
			String[] rejectReason = request.getParameterValues("rejectReason");
			String[] status = request.getParameterValues("status");
			
			logger.debug("itemCode:"+itemCode.length);
			
			//add value to Results
			if(itemCode != null && itemCode.length > 0){
				for(int i=0;i<itemCode.length;i++){
					if( !Utils.isNull(itemCode[i]).equals("") && !Utils.isNull(price[i]).equals("")){
						 SalesTargetBean l = new SalesTargetBean();
						 logger.debug("itemCode["+Utils.isNull(itemCode[i])+"]lineId:"+lineId[i]);
						 
						 l.setLineId(Utils.convertStrToLong(lineId[i],0));
						 l.setItemCode(Utils.isNull(itemCode[i]));
						 l.setItemId(Utils.isNull(itemId[i]));
						 l.setTargetQty(Utils.isNull(targetQty[i]));
						 l.setTargetAmount(Utils.isNull(targetAmount[i]));
						 l.setPrice(Utils.isNull(price[i]));
						 l.setOrderAmt12Month(Utils.isNull(orderAmt12Month[i]));
						 l.setOrderAmt3Month(Utils.isNull(orderAmt3Month[i]));
						 l.setRemark(Utils.isNull(remark[i]));
						 l.setRejectReason(Utils.isNull(rejectReason[i]));
						 l.setStatus(Utils.isNull(status[i]));
						
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 itemList.add(l);
					}//if
				}//for
			}//if
			//set items list
			h.setItems(itemList);
			//save
			SalesTargetDAO.save(h,user);
			
			//commit
			conn.commit();

			//search
			boolean getItems = true;
			h = SalesTargetDAO.searchSalesTarget(conn, h,getItems, user,aForm.getPageName());
			//get PriceListId
			h.setPriceListId(SalesTargetUtils.getPriceListId(conn, h.getSalesChannelNo(), h.getCustCatNo()));
			
			aForm.setBean(h);
			aForm.setResults(h.getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
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
		return "detail";
	}
	public ActionForward copyFromLastMonth(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("copyFromLastMonth By MKT");
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			SalesTargetBean bean = aForm.getBean();
			bean.setCreateUser(user.getUserName());
			bean.setUpdateUser(user.getUserName());
			
			String errorCode = SalesTargetCopy.copyFromLastMonth(user, bean,aForm.getPageName());
			if(errorCode.equalsIgnoreCase("DATA_CUR_EXIST_EXCEPTION")){
				request.setAttribute("Message","ไม่สามารถ Copy ได้ เนื่องจากมีการบันทึกข้อมูลบางส่วนไปแล้ว");
			}else if(errorCode.equalsIgnoreCase("DATA_PREV_NOT_FOUND")){
				request.setAttribute("Message","ไม่พบข้อมูลเดือนที่แล้ว");
			}else{
			   request.setAttribute("Message","Copy ข้อมูลเรียบร้อยแล้ว");
			   
			   //Search Data
			   SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMKT(aForm.getBean(),user,pageName);
			   aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() > 0){
				  request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMKT(request,aForm.getBean(),user));
			   }
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward deleteAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Delete All By Marketing");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			
			//delete all;
			SalesTargetDAO.deleteAllByMKT(conn, h);

			request.setAttribute("Message","ลบข้อมูล เรียบร้อยแล้ว");
			
			conn.commit();
			//reset
			h.setId(0);
			h.setTotalOrderAmt12Month("");
			h.setTotalOrderAmt3Month("");
			h.setTotalTargetAmount("");
			h.setTotalTargetQty("");
			h.setItems(null);
			h.setItemsList(null);
			logger.debug("priceListId:"+h.getPriceListId());
			aForm.setResults(new ArrayList<SalesTargetBean>());
			aForm.setBean(h);
			
		} catch (Exception e) {
			conn.rollback();
			
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward postToSales(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("postToSales By Marketing");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			h.setStatus(SalesTargetConstants.STATUS_POST);
			h.setUpdateUser(user.getUserName());
			
			//update status POST
			SalesTargetDAO.updateStatusHeadByMKT(conn, h);
			SalesTargetDAO.updateStatusItemByMKTByID(conn, h);
			
			request.setAttribute("Message","Post To Sales เรียบร้อยแล้ว");
			
			conn.commit();
			
			//search
			boolean getItems = true;
			h = SalesTargetDAO.searchSalesTarget(conn, h,getItems, user,aForm.getPageName());
			
			aForm.setBean(h);
			aForm.setResults(h.getItems());
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward salesAcceptToSalesManager(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("salesAcceptToSalesManager By MT(Sales)");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			h.setStatus(SalesTargetConstants.STATUS_ACCEPT);
			h.setUpdateUser(user.getUserName());
			
			//update status POST
			SalesTargetDAO.updateStatusHeadByMKT(conn, h);
			SalesTargetDAO.updateStatusItemByMKTByID(conn, h);
			
			request.setAttribute("Message","ได้ทำการ Accept เป้าหมายขาย เรียบร้อยแล้ว");
			
			conn.commit();
			
			//search
			boolean getItems = true;
			h = SalesTargetDAO.searchSalesTarget(conn, h,getItems, user,aForm.getPageName());
			
			aForm.setBean(h);
			aForm.setResults(h.getItems());
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward salesManagerFinish(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("salesManagerFinish By MT Manager");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean cri = new SalesTargetBean();
			cri = aForm.getBean();
			
			String[] ids = request.getParameterValues("ids");
			String[] status = request.getParameterValues("status");
			
			for(int i=0;i<ids.length;i++){
				if( !SalesTargetConstants.STATUS_FINISH.equals(Utils.isNull(status[i]))){
					SalesTargetBean h = new SalesTargetBean();
					h.setId(Utils.convertStrToLong(ids[i], 0));
					h.setStatus(SalesTargetConstants.STATUS_FINISH);
					h.setUpdateUser(user.getUserName());
					
					//update status FINISH
					SalesTargetDAO.updateStatusHeadByMKT(conn, h);
					SalesTargetDAO.updateStatusItemByMKTByID(conn, h);
				}
			}
			request.setAttribute("Message","ได้ทำการ อนุมัติเป้าหมายขาย เรียบร้อยแล้ว");
			
			conn.commit();
			
			//search head
			cri = SalesTargetDAO.searchTargetHeadByMT(cri,user,aForm.getPageName());
			aForm.setBean(cri);
		    //gen Results
			request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMTMGR(request,aForm.getBean(),user));
			
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		User user = (User) request.getSession().getAttribute("user");
		SalesTargetForm aForm = (SalesTargetForm) form;
		boolean foundData = false;
		StringBuffer resultHtmlTable = null;
		String pageName = aForm.getPageName();
		boolean excel = true;
		try {
			 //Search Report
			 if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){
				  resultHtmlTable = SalesTargetReport.searchReport(aForm.getBean(),excel);
				 if(resultHtmlTable != null){
					  foundData = true;
				 }
			 }else  if(SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){
				  resultHtmlTable = SalesTargetExport.genExportExcelByMT(request, aForm.getBean(), user) ;
				  if(resultHtmlTable != null){
					  foundData = true;
				 }	
			 }
			 
			  if(foundData){
					java.io.OutputStream out = response.getOutputStream();
					response.setHeader("Content-Disposition", "attachment; filename=data.xls");
					response.setContentType("application/vnd.ms-excel");
					
					Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
					w.write(resultHtmlTable.toString());
				    w.flush();
				    w.close();
		
				    out.flush();
				    out.close();
			  }else{
				  request.setAttribute("Message","");
			  }
         
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SalesTargetForm orderForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
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
