package com.isecinc.pens.web.buds.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.StockReturn;
import com.isecinc.pens.bean.StockReturnLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MStockReturn;
import com.isecinc.pens.web.buds.BudsAllBean;
import com.isecinc.pens.web.buds.BudsAllForm;
import com.isecinc.pens.web.buds.page.export.ConfPickingExport;
import com.isecinc.pens.web.stockreturn.StockReturnForm;
import com.pens.util.BeanParameter;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ConfPickingAction extends I_Action {

	public static int pageSize = 60;
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "budsAll";
		BudsAllForm aForm = (BudsAllForm) form;
		BudsAllBean bean = new BudsAllBean();
		Connection conn = null;
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"]subPageName["+request.getParameter("subPageName")+"]");
			 logger.debug("action["+request.getParameter("action")+"]");
			 conn = DBConnectionApps.getInstance().getConnection();
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().removeAttribute("_criteria");
				//CLEAR SESSION 
				request.getSession().removeAttribute("REGION_ALL");
				request.getSession().removeAttribute("PROVINCE_ALL");
				request.getSession().removeAttribute("AMPHUR_ALL");//for amphur check searchTransport
					
			    if("ConfPicking".equalsIgnoreCase(Utils.isNull(request.getParameter("subPageName"))) ){
				   bean.setConfPickingBean(new ConfPickingBean());
				   aForm.setBean(bean);
			    }else if("BudsConfPicking".equalsIgnoreCase(Utils.isNull(request.getParameter("subPageName"))) ){
			       bean.setConfPickingBean(new ConfPickingBean());
			       aForm.setBean(bean);
			    }	
			 }
			 aForm.setPageName(Utils.isNull(request.getParameter("pageName")));
			 aForm.setSubPageName(Utils.isNull(request.getParameter("subPageName")));
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			conn.close();
		}
		return forward;
	}
	public ActionForward prepareSearchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		BudsAllBean bean = new BudsAllBean();
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				ConfPickingBean confPickingBean = new ConfPickingBean();
				confPickingBean.setTransactionDateFrom(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				confPickingBean.setTransactionDateTo(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				bean.setConfPickingBean(confPickingBean);
				aForm.setBean(bean);
			 }
			 aForm.setPageName(Utils.isNull(request.getParameter("pageName")));
			 aForm.setSubPageName(Utils.isNull(request.getParameter("subPageName")));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "";
	}

	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		String subPageName = aForm.getSubPageName();
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnectionApps.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.getBean().setConfPickingBean((ConfPickingBean)request.getSession().getAttribute("_criteria"));
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				aForm.setPageSize(pageSize);
				
				//get Total Record
				aForm.setTotalRecord(ConfPickingDAO.searchTotalHead(conn,subPageName,aForm.getBean().getConfPickingBean()));
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
				ConfPickingBean confPickingBean = ConfPickingDAO.searchHead(conn,subPageName,aForm.getBean().getConfPickingBean(),false,allRec,currPage,pageSize);
				if(confPickingBean.getItemsList().size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.getBean().setConfPickingBean(confPickingBean);
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    ConfPickingBean confPickingBean = ConfPickingDAO.searchHead(conn,subPageName,aForm.getBean().getConfPickingBean(),false,allRec,currPage,pageSize);
				if(confPickingBean.getItemsList().size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.getBean().setConfPickingBean(confPickingBean);
				
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("viewDetail");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save criteria
			request.getSession().setAttribute("_criteria", aForm.getBean().getConfPickingBean());
			//CLEAR SESSION Criteria search Transport
			request.getSession().removeAttribute("REGION_ALL");
			request.getSession().removeAttribute("PROVINCE_ALL");
			request.getSession().removeAttribute("AMPHUR_ALL");
			
			String mode = Utils.isNull(request.getParameter("mode"));
		    String pickingNo = Utils.isNull(request.getParameter("pickingNo"));
		    logger.debug("pickingNo:"+pickingNo);
		    if("".equalsIgnoreCase(Utils.isNull(pickingNo))){
		    	ConfPickingBean confPickingBean = new ConfPickingBean();
		    	aForm.getBean().setConfPickingBean(confPickingBean);
		    }else{
		    	//Get detail t_picking_trans
		    	ConfPickingBean confPickingBean = ConfPickingDAO.searchPickingTrans(pickingNo);
		    	
		    	confPickingBean = ConfPickingDAO.searchPickingDetail(mode,aForm.getSubPageName(), confPickingBean, false, user);
				if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
					aForm.getBean().setConfPickingBean(confPickingBean);
				    request.setAttribute("budsAllForm_RESULTS", confPickingBean.getDataStrBuffer());
				    request.setAttribute("budsAllForm_total_RESULTS", confPickingBean.getRowTotalStrBuffer());
				    confPickingBean.setDataStrBuffer(null);//clear memory
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.getBean().setConfPickingBean(confPickingBean);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			
			ConfPickingBean confPickingBean = ConfPickingDAO.searchPickingDetail(budsAllForm.getMode(),budsAllForm.getSubPageName(), budsAllForm.getBean().getConfPickingBean(), false, user);
			
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				budsAllForm.getBean().setConfPickingBean(confPickingBean);
			    request.setAttribute("budsAllForm_RESULTS", confPickingBean.getDataStrBuffer());
			    request.setAttribute("budsAllForm_total_RESULTS", confPickingBean.getRowTotalStrBuffer());
			    confPickingBean.setDataStrBuffer(null);//clear memory
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "budsAll";
	}
	public ActionForward confirmPicking(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmPicking");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			Map<String, String> orderNoMap = new HashMap<String, String>();
			String[] orderNo = request.getParameterValues("orderNo");
			String orderNoTemp = "";
			for(int i=0;i<orderNo.length;i++){
				if(orderNoMap.get(Utils.isNull(orderNo[i]))==null){
					orderNoTemp +=Utils.isNull(orderNo[i])+",";
				}
				orderNoMap.put(Utils.isNull(orderNo[i]), Utils.isNull(orderNo[i]));
			}
			if(orderNoTemp.length() >0){
			   orderNoTemp = orderNoTemp.substring(0,orderNoTemp.length()-1);
			   budsAllForm.getBean().getConfPickingBean().setSelectedOrderNo(orderNoTemp);
			}
			
			//Confirm Picking
			ConfPickingDAO.confirmPicking(budsAllForm.getBean().getConfPickingBean(), user);
			request.setAttribute("Message", "ยืนยันชุดข้อมูลการจัดการ Picking เรียบร้อยแล้ว");
			
			//search 
			//Get detail t_picking_trans
	    	ConfPickingBean confPickingBean = ConfPickingDAO.searchPickingTrans(budsAllForm.getBean().getConfPickingBean().getPickingNo());
	    	
	    	confPickingBean = ConfPickingDAO.searchPickingDetail(budsAllForm.getMode(),budsAllForm.getSubPageName(), confPickingBean, false, user);
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				budsAllForm.getBean().setConfPickingBean(confPickingBean);
			    request.setAttribute("budsAllForm_RESULTS", confPickingBean.getDataStrBuffer());
			    request.setAttribute("budsAllForm_total_RESULTS", confPickingBean.getRowTotalStrBuffer());
			    confPickingBean.setDataStrBuffer(null);//clear memory
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward addOrderPickingManual(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("addOrderPickingManual");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			Map<String, String> orderNoMap = new HashMap<String, String>();
			String[] orderNo = request.getParameterValues("orderNo");
			String orderNoTemp = "";
			for(int i=0;i<orderNo.length;i++){
				if(orderNoMap.get(Utils.isNull(orderNo[i]))==null){
					orderNoTemp +=Utils.isNull(orderNo[i])+",";
				}
				orderNoMap.put(Utils.isNull(orderNo[i]), Utils.isNull(orderNo[i]));
			}
			if(orderNoTemp.length() >0){
			   orderNoTemp = orderNoTemp.substring(0,orderNoTemp.length()-1);
			   budsAllForm.getBean().getConfPickingBean().setSelectedOrderNo(orderNoTemp);
			}
			
			//Confirm Picking
			ConfPickingDAO.addOrderManualPicking(budsAllForm.getBean().getConfPickingBean(), user);
			request.setAttribute("Message", "เพิ่มรายการ Order เข้า Picking ["+budsAllForm.getBean().getConfPickingBean().getPickingNo()+"] เรียบร้อยแล้ว");
			
			//search 
			//Get detail t_picking_trans
	    	ConfPickingBean confPickingBean = ConfPickingDAO.searchPickingTrans(budsAllForm.getBean().getConfPickingBean().getPickingNo());
	    	
	    	confPickingBean = ConfPickingDAO.searchPickingDetail(budsAllForm.getMode(),budsAllForm.getSubPageName(), confPickingBean, false, user);
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				budsAllForm.getBean().setConfPickingBean(confPickingBean);
			    request.setAttribute("budsAllForm_RESULTS", confPickingBean.getDataStrBuffer());
			    request.setAttribute("budsAllForm_total_RESULTS", confPickingBean.getRowTotalStrBuffer());
			    confPickingBean.setDataStrBuffer(null);//clear memory
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward rejectOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("rejectOrder");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String orderNoTemp = "";
		try {
			//Get Parameter
			Map<String, String> orderNoMap = new HashMap<String, String>();
			String[] chkOrder = request.getParameterValues("chkOrder");
			for(int i=0;i<chkOrder.length;i++){
				if(orderNoMap.get(Utils.isNull(chkOrder[i]))==null){
					orderNoTemp +=Utils.isNull(chkOrder[i])+",";
				}
				orderNoMap.put(Utils.isNull(chkOrder[i]), Utils.isNull(chkOrder[i]));
			}
			if(orderNoTemp.length() >0){
			   orderNoTemp = orderNoTemp.substring(0,orderNoTemp.length()-1);
			   budsAllForm.getBean().getConfPickingBean().setSelectedOrderNo(orderNoTemp);
			}
			
			ConfPickingDAO.rejectPicking(budsAllForm.getBean().getConfPickingBean(), user);
			request.setAttribute("Message", "Reject Order เรียบร้อยแล้ว");
			
			//search 
			ConfPickingBean confPickingBean = budsAllForm.getBean().getConfPickingBean();
	    	confPickingBean = ConfPickingDAO.searchPickingDetail(budsAllForm.getMode(),budsAllForm.getSubPageName(), confPickingBean, false, user);
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				budsAllForm.getBean().setConfPickingBean(confPickingBean);
			    request.setAttribute("budsAllForm_RESULTS", confPickingBean.getDataStrBuffer());
			    request.setAttribute("budsAllForm_total_RESULTS", confPickingBean.getRowTotalStrBuffer());
			    confPickingBean.setDataStrBuffer(null);//clear memory
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	
	public ActionForward genInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genInvoice");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			Map<String, String> orderNoMap = new HashMap<String, String>();
			String[] orderNo = request.getParameterValues("orderNo");
			String orderNoTemp = "";
			for(int i=0;i<orderNo.length;i++){
				if(orderNoMap.get(Utils.isNull(orderNo[i]))==null){
					orderNoTemp +=Utils.isNull(orderNo[i])+",";
				}
				orderNoMap.put(Utils.isNull(orderNo[i]), Utils.isNull(orderNo[i]));
			}
			if(orderNoTemp.length() >0){
			   orderNoTemp = orderNoTemp.substring(0,orderNoTemp.length()-1);
			   budsAllForm.getBean().getConfPickingBean().setSelectedOrderNo(orderNoTemp);
			}
			
			//Gen Invoice update status = LOADING
			ConfPickingDAO.genInvoice(budsAllForm.getBean().getConfPickingBean(), user);
			
			request.setAttribute("Message", "Generate Invoice เรียบร้อยแล้ว");
		
			//search 
			ConfPickingBean confPickingBean = budsAllForm.getBean().getConfPickingBean();
	    	confPickingBean = ConfPickingDAO.searchPickingDetail(budsAllForm.getMode(),budsAllForm.getSubPageName(), confPickingBean, false, user);
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				budsAllForm.getBean().setConfPickingBean(confPickingBean);
			    request.setAttribute("budsAllForm_RESULTS", confPickingBean.getDataStrBuffer());
			    request.setAttribute("budsAllForm_total_RESULTS", confPickingBean.getRowTotalStrBuffer());
			    confPickingBean.setDataStrBuffer(null);//clear memory
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถ Generate Invoice ได้ \n"+e.toString());
		}
		return mapping.findForward("budsAll");
	}
	
	public ActionForward exportPickingList(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportPickingList");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = budsAllForm.getPageName();
		String subPageName = budsAllForm.getSubPageName();
		StringBuffer dataExcel = new StringBuffer();
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			budsAllForm.getBean().getConfPickingBean().setItemsList(null);
			
			ConfPickingBean confPickingBean = ConfPickingDAO.searchPickingDetail("view",subPageName, budsAllForm.getBean().getConfPickingBean(), true, user);
			
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				dataExcel.append(confPickingBean.getDataStrBuffer());
				dataExcel.append(confPickingBean.getRowTotalStrBuffer());
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(dataExcel.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    
			    //clear memory
			    confPickingBean.setDataStrBuffer(null);
			    confPickingBean.setRowTotalStrBuffer(null);
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	
	public ActionForward exportPickingListReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportPickingListReport");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = budsAllForm.getPageName();
		String subPageName = budsAllForm.getSubPageName();
		StringBuffer dataExcel = new StringBuffer();
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			budsAllForm.getBean().getConfPickingBean().setItemsList(null);
			
			List<ConfPickingBean> resultList = ConfPickingDAO.searchPickingReport(budsAllForm.getBean().getConfPickingBean(), user);
			ConfPickingBean confPickingBean = budsAllForm.getBean().getConfPickingBean();
			confPickingBean.setItemsList(resultList);
			
			logger.debug("resultList :"+resultList.size());
			//GenExcel Table
			dataExcel = ConfPickingExport.genPickingListReport(confPickingBean, true);
			
			if(dataExcel != null){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(dataExcel.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	
	public ActionForward exportSalesReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportSalesReport");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer dataExcel = new StringBuffer();
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			budsAllForm.getBean().getConfPickingBean().setItemsList(null);
			
			ConfPickingBean confPickingBean = ConfPickingExport.searchSalesReport(budsAllForm.getBean().getConfPickingBean(), true, user);
			
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				dataExcel.append(confPickingBean.getDataStrBuffer());
				//dataExcel.append(confPickingBean.getRowTotalStrBuffer());
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(dataExcel.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    
			   //clear memory
			    confPickingBean.setDataStrBuffer(null);
			    confPickingBean.setRowTotalStrBuffer(null);
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward exportSalesDetailReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportSalesDetailReport");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer dataExcel = new StringBuffer();
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			budsAllForm.getBean().getConfPickingBean().setItemsList(null);
			
			ConfPickingBean confPickingBean = ConfPickingExport.searchSalesDetailReport(budsAllForm.getBean().getConfPickingBean(), true, user);
			
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				dataExcel.append(confPickingBean.getDataStrBuffer());
				//dataExcel.append(confPickingBean.getRowTotalStrBuffer());
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(dataExcel.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    
			   //clear memory
			    confPickingBean.setDataStrBuffer(null);
			    confPickingBean.setRowTotalStrBuffer(null);
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
  //PickingList = Loading Report
  public ActionForward printPickingReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printReport");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		String fileNameExport  = "PickingReport.pdf";
		String fileType = SystemElements.PDF;
		try { 
			String reportType = Utils.isNull(request.getParameter("reportType"));
			if("excel".equalsIgnoreCase(reportType)){
				fileNameExport  = "PickingReport.xls";
				fileType = SystemElements.EXCEL;
			}
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
			ServletContext context = request.getSession().getServletContext();
	
			String fileName = "picking_report";
            String fileJasper = BeanParameter.getReportPath() + fileName;
          
            //init connection 
			conn = DBConnectionApps.getInstance().getConnection();
			
            //detail
			List<ConfPickingBean> resultList = ConfPickingDAO.searchPickingReport(aForm.getBean().getConfPickingBean(), user);
			logger.debug("resultList :"+resultList.size());
			
		   //set main parameter
            parameterMap.put("pickingNo",aForm.getBean().getConfPickingBean().getPickingNo());
            parameterMap.put("transactionDate",aForm.getBean().getConfPickingBean().getTransactionDate());
            parameterMap.put("userPrint",user.getName());
            parameterMap.put("printDate",DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,DateUtil.local_th));
            
            reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName,resultList ,fileNameExport);
		
            //update print picking count
            ConfPickingDAO.updatePrintCountPickingTrans(conn, aForm.getBean().getConfPickingBean(),"PrintPicking");
            
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn !=null){
				conn.close();conn= null;
			}
		}
		return null;
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			 
		} catch (Exception e) {
			request.setAttribute("Message",e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
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
