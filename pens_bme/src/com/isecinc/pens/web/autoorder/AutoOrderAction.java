package com.isecinc.pens.web.autoorder;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.GenAutoOrderRepTask;
import com.isecinc.pens.web.batchtask.task.GenStockOnhandRepTempTask;
import com.isecinc.pens.web.order.OrderForm;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.summary.SummaryExport;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.ControlLockPage;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.EnvQuartzProperties;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * AutoOrderAction
 * 
 * @author witty:17/10/2019
 * @version 
 * 
 */
public class AutoOrderAction extends I_Action {

	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		AutoOrderForm aForm = (AutoOrderForm) form;
		String action = Utils.isNull(request.getParameter("action"));
		try {
			if("new".equalsIgnoreCase(action)){
				//Clear session
				request.getSession().removeAttribute("BATCH_TASK_RESULT");
			    aForm.setResults(null);
				 
				//prepare parameter
				AutoOrderBean bean = new AutoOrderBean();
				bean.setOrderDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				bean.setDispNoZero("true");
				
				/** get parameter from Gen Report Page **/
				 if( !Utils.isNull(request.getParameter("storeCode")).equals("")){
					 bean.setStoreCode(Utils.isNull(request.getParameter("storeCode")));
					 bean.setStoreName(GeneralDAO.getStoreName(bean.getStoreCode()));
				 }
				 if( !Utils.isNull(request.getParameter("orderDate")).equals("")){
					 bean.setOrderDate(Utils.isNull(request.getParameter("orderDate")));
				 }
				//for test
				//bean.setStoreCode("020047-26");
				//bean.setStoreCode("020049-15");
				
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
	   return mapping.findForward("search");
	}
	
	/** For batch popup TaskName: GenStockOnhandTempTask **/
	/** Prepare parameter **/
	public ActionForward genStockOnhandRepTemp(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("submit genStockOnhandRepTemp");
		AutoOrderForm aForm = (AutoOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			batchParaMap.put(GenStockOnhandRepTempTask.PARAM_ASOF_DATE, aForm.getBean().getOrderDate());
			batchParaMap.put(GenStockOnhandRepTempTask.PARAM_STORE_CODE, aForm.getBean().getStoreCode());
			
			logger.debug("storeCode:"+aForm.getBean().getStoreCode());
			logger.debug("asOfDate:"+aForm.getBean().getOrderDate());
			
			request.getSession().setAttribute(BatchTaskConstants.BATCH_PARAM_MAP,batchParaMap);
			request.setAttribute("BATCH_TASK_NAME",BatchTaskConstants.GEN_STOCK_ONHAND_REP_TEMP);//set to popup page to BatchTask
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
		}
		return mapping.findForward("search");
	}
	
	/** For batch popup TaskName: GenAutoOrderRepTask **/
	/** Prepare parameter **/
	public ActionForward genAutoOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genAutoOrder");
		AutoOrderForm aForm = (AutoOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			batchParaMap.put(GenAutoOrderRepTask.PARAM_ORDER_DATE, aForm.getBean().getOrderDate());
			batchParaMap.put(GenAutoOrderRepTask.PARAM_STORE_CODE, aForm.getBean().getStoreCode());
			
			logger.debug("storeCode:"+aForm.getBean().getStoreCode());
			logger.debug("asOfDate:"+aForm.getBean().getOrderDate());
			
			/** Lock Order Bme all Time Gen Auto Order **/
			ControlLockPage.controlLockPage(user, "Order", "Y");
			
			request.getSession().setAttribute(BatchTaskConstants.BATCH_PARAM_MAP,batchParaMap);
			request.setAttribute("BATCH_TASK_NAME",BatchTaskConstants.GEN_AUTO_ORDER_REP);//set to popup page to BatchTask
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
	   return mapping.findForward("search");
	}
	
	 public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		AutoOrderForm aForm = (AutoOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 //searchBatch
			 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
			 
			 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
			
			 //Case after GenAutoOrderRep search display to screen 
			 logger.debug("BatchTaskName:"+batchTaskForm.getResults()[0].getName());
			 if(batchTaskForm.getResults()[0].getName().equalsIgnoreCase("GenAutoOrderRep")){
				 //enable save and confirm button
				 aForm.getBean().setCanSave(true);
				 //search and display 
				 search(mapping, form, request, response); 
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	 protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		 logger.debug("searchBatch");
		 AutoOrderForm aForm = (AutoOrderForm) form;
		 User user = (User) request.getSession().getAttribute("user");
		 try{
			 aForm.setResults(null);
			 aForm.getBean().setCanSave(false);
			 
			 List<AutoOrderBean> dataList = AutoOrderDAO.searchAutoOrderRep(aForm.getBean());
			 if(dataList != null && dataList.size() >0){
			    aForm.setResults(dataList);
			    //validate Order Rep is status= Confirm  no edit
			    AutoOrderBean item = dataList.get(0);
			    if( !"CONFIRM".equalsIgnoreCase(item.getStatus())){
			    	 aForm.getBean().setCanSave(true);
			    }
			 }else{
				 request.setAttribute("Message", "ไม่พบข้อมูล");
			 }
		 }catch(Exception e){
			 logger.error(e.getMessage(),e);
		 }
		 return "search";
	}
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		AutoOrderForm aForm = (AutoOrderForm) form;
		StringBuffer h = null;
		String fileName ="data.xls";
		double totalQty = 0,totalAmount=0;
		try {
			 List<AutoOrderBean> dataList = AutoOrderDAO.searchAutoOrderRep(aForm.getBean());
			 if(dataList != null && dataList.size() >0){
                h = new StringBuffer();
                h.append(ExcelHeader.EXCEL_HEADER);
                for(int i=0;i<dataList.size();i++){
                	AutoOrderBean item = dataList.get(i);
                	if(i==0){
                		h.append("<table align='center' border='1' cellpadding='3' cellspacing='0'>");
                		h.append("<tr>");
                		h.append("<th >No</th>");
                		h.append("<th >Group</th>");
                		h.append("<th >Size/ Color</th>");
                		h.append("<th >Item</th>");
                		h.append("<th >WC Onhand - ยอด Order ที่คีย์ไว้</th>");
                		h.append("<th >ราคาขาย ปลีกก่อน VAT</th>");
                		h.append("<th >Stock หน้าร้าน</th>");
                		h.append("<th >ยอดขาย ย้อนหลัง</th>");
                		h.append("<th >Recommend ให้เติมเต็ม</th>");
                		h.append("<th >ยอดที่ต้องการเติมเต็ม</th>");
                		h.append("<th >Status</th>");
                		h.append("<th >Min/Max</th>");
                		h.append("</tr> ");
                	}
                	//Gen Row
                	h.append("<tr>");
                	h.append("<td class='num'> "+(i+1)+"</td> ");
                	h.append("<td class='text'> "+item.getGroupCode()+"</td> ");
                	h.append("<td class='text'> "+item.getSizeColor()+"</td> ");
                	h.append("<td class='text'> "+item.getPensItem()+"</td> ");
                	h.append("<td class='num'> "+item.getWacoalOnhandQty()+"</td> ");
                	h.append("<td class='num'> "+item.getRetailPriceBF()+"</td> ");
                	h.append("<td class='num'> "+item.getShopOnhandQty()+"</td> ");
                	h.append("<td class='num'> "+item.getSalesQty()+"</td> ");
                	h.append("<td class='num'> "+item.getRecommendQty()+"</td> ");
                	h.append("<td class='num'> "+item.getOrderQty()+"</td> ");
                	h.append("<td class='text'> "+item.getStatus()+"</td> ");
                	h.append("<td class='text'> "+item.getMinQty()+"/"+item.getMaxQty()+"</td> ");
                	h.append("</tr> ");
                	
                	//summary
                	totalQty += Utils.convertStrToDouble(item.getOrderQty());
                	totalAmount += (Utils.convertStrToDouble(item.getOrderQty()) * Utils.convertStrToDouble(item.getRetailPriceBF()));
                }//for
                //Summary
                h.append("<tr>");
            	h.append("<td class='num'> </td> ");
            	h.append("<td class='text'> </td> ");
            	h.append("<td class='text'> </td> ");
            	h.append("<td class='text'> </td> ");
            	h.append("<td class='num'></td> ");
            	h.append("<td class='num'> </td> ");
            	h.append("<td class='num'> </td> ");
            	h.append("<td class='num'> </td> ");
            	h.append("<td class='colum_head' align='right'> Total</td> ");
            	h.append("<td class='num_currency_bold'> "+Utils.decimalFormat(totalQty,Utils.format_current_no_disgit)+"</td> ");
            	h.append("<td class='num_currency_bold' colspan='2'> "+Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit)+"</td> ");
            	h.append("</tr> ");
                h.append("</table>");
			 }
			 //Export to OutputStream
		     java.io.OutputStream out = response.getOutputStream();
			 response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			 response.setContentType("application/vnd.ms-excel");
			 Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			 w.write(h.toString());
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
    
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    AutoOrderForm aForm = (AutoOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		AutoOrderBean item = null;
		List<AutoOrderBean> dataList = new ArrayList<AutoOrderBean>();
		try{
			String[] groupCodeArr = request.getParameterValues("groupCode");
			String[] pensItemArr = request.getParameterValues("pensItem");
			String[] orderQtyArr = request.getParameterValues("orderQty");
			conn = DBConnection.getInstance().getConnectionApps();
			conn.setAutoCommit(false);
			for(int i=0;i<groupCodeArr.length;i++){
				item = new AutoOrderBean();
				item.setGroupCode(groupCodeArr[i]);
				item.setPensItem(pensItemArr[i]);
				item.setOrderQty(orderQtyArr[i]);
				dataList.add(item);
			}
			aForm.getBean().setUserName(user.getUserName());
			//insert OrderRep
			AutoOrderDAO.updateOrderRep(conn,aForm.getBean(), dataList);
			
			conn.commit();
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			//search refresh data
			dataList = AutoOrderDAO.searchAutoOrderRep(aForm.getBean());
			if(dataList != null && dataList.size() >0){
			    aForm.setResults(dataList);
			    //validate Order Rep is status= Confirm  no edit
			    item = dataList.get(0);
			    if( !"CONFIRM".equalsIgnoreCase(item.getStatus())){
			    	 aForm.getBean().setCanSave(true);
			    }
			}
		}catch(Exception e){
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้ \n"+e.getMessage());
		}finally{
			conn.close();
		}
		return "search";
	}
	public ActionForward deleteOrderRep(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("deleteOrderRep");
		AutoOrderForm aForm = (AutoOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn =DBConnection.getInstance().getConnectionApps();
			aForm.getBean().setUserName(user.getUserName());
			//validate data can Confirm
			String canR = AutoOrderDAO.canConfirmOrderRepToOder(conn, aForm.getBean().getOrderDate(),aForm.getBean().getStoreCode());
			if(canR.equalsIgnoreCase("0")){
				request.setAttribute("Message", "ข้อมูลร้านนี้ ถูก Confirm to Order ไปแล้ว  ไม่สามารถลบข้อมูลได้");
				return mapping.findForward("search");
			}
			if(canR.equalsIgnoreCase("1")){
				
				aForm.getBean().setUserName(user.getUserName());
		
				//delete OrderRep
				AutoOrderDAO.deleteOrderRep(conn,aForm.getBean().getStoreCode(),aForm.getBean().getOrderDate());
				
				request.setAttribute("Message", "ยืนยัน ลบข้อมูล Order เติมเต็มเรียบร้อยแล้ว ");
				
			    aForm.getBean().setCanSave(false);
			    aForm.setResults(null);
			    request.getSession().removeAttribute("BATCH_TASK_RESULT");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			conn.close();
		}
	   return mapping.findForward("search");
	}
	
	public ActionForward confirmTempRepToOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmTempRepToOrder");
		AutoOrderForm aForm = (AutoOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn =DBConnection.getInstance().getConnectionApps();
			aForm.getBean().setUserName(user.getUserName());
			//validate data can Confirm
			String canR = AutoOrderDAO.canConfirmOrderRepToOder(conn, aForm.getBean().getOrderDate(),aForm.getBean().getStoreCode());
			if(canR.equalsIgnoreCase("0")){
				request.setAttribute("Message", "ข้อมูลร้านนี้ ถูก Confirm to Order ไปแล้ว");
				return mapping.findForward("search");
			}else if(canR.equalsIgnoreCase("-1")){
				request.setAttribute("Message", "ไม่พบข้อมูล  ที่จะ Confirm to Order");
				return mapping.findForward("search");
			}
			if(canR.equalsIgnoreCase("1")){
				//Update Order Rep
				List<AutoOrderBean> dataList = new ArrayList<AutoOrderBean>();
				String[] groupCodeArr = request.getParameterValues("groupCode");
				String[] pensItemArr = request.getParameterValues("pensItem");
				String[] orderQtyArr = request.getParameterValues("orderQty");
				conn = DBConnection.getInstance().getConnectionApps();
				conn.setAutoCommit(false);
				for(int i=0;i<groupCodeArr.length;i++){
					AutoOrderBean item = new AutoOrderBean();
					item.setGroupCode(groupCodeArr[i]);
					item.setPensItem(pensItemArr[i]);
					item.setOrderQty(orderQtyArr[i]);
					dataList.add(item);
				}
				aForm.getBean().setUserName(user.getUserName());
				AutoOrderDAO.updateOrderRep(conn,aForm.getBean(), dataList);
				
				//set status Order REP =CONFIRM
				AutoOrderDAO.confirmOrderRep(conn,aForm.getBean());
				
				//Gen to PENSBME_ORDER
				AutoOrderDAO.genOrderRepToBMEOrder(conn, aForm.getBean());
				
				//delete stock onhand Temp
				AutoOrderDAO.deleteStockOnhandTemp(conn, "");
				
				request.setAttribute("Message", "ยืนยัน Generate Order เรียบร้อยแล้ว ");
				
			    aForm.getBean().setCanSave(false);
			    aForm.setResults(null);
			    request.getSession().removeAttribute("BATCH_TASK_RESULT");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			conn.close();
		}
	   return mapping.findForward("search");
	}
	public ActionForward lockOrderPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("lockOrderPage");
		AutoOrderForm aForm = (AutoOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String lock = Utils.isNull(request.getParameter("lock"));
			ControlLockPage.controlLockPage(user, "Order", lock);
			if(lock.equals("Y")){
				request.setAttribute("Message", "Lock BME Order Page Success");
			}else{
				request.setAttribute("Message", "UnLock BME Order Page Success");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			
		}
	   return mapping.findForward("search");
	}
	
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "process";
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare2");
		return "search";
	}
	
	@Override
	protected void setNewCriteria(ActionForm form) {
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
