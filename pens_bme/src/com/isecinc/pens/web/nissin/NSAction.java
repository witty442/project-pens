package com.isecinc.pens.web.nissin;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.NSBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.NSDAO;
import com.isecinc.pens.dao.constants.ConstantBean;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.shop.ShopBean;
import com.isecinc.pens.web.shop.ShopForm;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;
import com.pens.util.seq.SequenceProcess;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class NSAction extends I_Action {

	public static int pageSize = 50;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	public static String STATUS_SAVE ="SV";
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		NSForm aForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			String page = Utils.isNull(request.getParameter("page"));
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResults(null);
				NSBean ad = new NSBean();
				ad.setNoPicRcv("true");
				ad.setCreateUser(user.getUserName());
				aForm.setBean(ad);
				
				//prepare conn
				conn = DBConnection.getInstance().getConnection();
				
				//prepare session
				List<PopupForm> billTypeList = new ArrayList<PopupForm>();
				PopupForm ref = new PopupForm("",""); 
				billTypeList.add(ref);
				billTypeList.addAll(NSDAO.searchChannelList(new PopupForm(),""));
				request.getSession().setAttribute("channelList",billTypeList);
				
				//customerSubType
				List<ConstantBean> customerSubTypeList = new ArrayList<ConstantBean>();
				ConstantBean blank = new ConstantBean("","","",""); 
				customerSubTypeList.add(blank);
				customerSubTypeList.addAll(ControlConstantsDB.getCondList(ControlConstantsDB.NS_CUTSOMER_SUB_TYPE));
				request.getSession().setAttribute("customerSubTypeList",customerSubTypeList);
				
				//
				List<NSBean> dataList = new ArrayList<NSBean>();
				NSBean beanItem = new NSBean(); 
				dataList.add(beanItem);
				dataList.addAll(NSDAO.searchSalesZoneListModel(conn));
				request.getSession().setAttribute("SALES_ZONE_LIST",dataList);
				
			}else if("back".equals(action)){
				NSBean cri  = aForm.getBeanCriteria();
				aForm.setBean(cri);
				search(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
	}
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
			logger.debug("searchPromotion");
			NSForm aForm = (NSForm) form;
			User user = (User) request.getSession().getAttribute("user");
			String msg = "";
			int currPage = 1;
			boolean allRec = false;
			Connection conn = null;
			try {
				String action = Utils.isNull(request.getParameter("action"));
				logger.debug("action:"+action);
				String page = Utils.isNull(request.getParameter("page"));
				
				conn = DBConnection.getInstance().getConnection();
				if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
					//case  back
					if("back".equalsIgnoreCase(action)){
						aForm.setBean(aForm.getBeanCriteria());
					}
					//default currPage = 1
					aForm.setCurrPage(currPage);
					
					//get Total Record
					aForm.setTotalRecord(NSDAO.searchHeadTotalRecList(conn,aForm.getBean()));
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
					List<NSBean> items = NSDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize,page);
					aForm.setResults(items);
					
					if(items.size() <=0){
					   request.setAttribute("Message", "ไม่พบข้อมูล");
					   aForm.setResults(null);
					}else{
						if(aForm.getTotalPage()==aForm.getCurrPage()){
							aForm.setSummary(NSDAO.searchHeadSummary(conn, aForm.getBean()));
						}
					}
					
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
					List<NSBean> items = NSDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize,page);
					aForm.setResults(items);
					
					if(aForm.getTotalPage()==aForm.getCurrPage()){
						aForm.setSummary(NSDAO.searchHeadSummary(conn, aForm.getBean()));
					}
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
			return "search";
		}
	 
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		NSForm aForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResults(null);
			aForm.setBean(new NSBean());
			
			NSBean ad = new NSBean();
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
		NSForm aForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String page = Utils.isNull(request.getParameter("page"));
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String orderId = Utils.isNull(request.getParameter("orderId"));
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare edit docNo:"+orderId +",mode:"+mode);
		
			NSBean c = new NSBean();
			if( !Utils.isNull(orderId).equals("")){
			   c.setOrderId(orderId);
			   NSBean bean = NSDAO.searchNissinOrder(c,page);
			   bean.setMode(mode);
			   
			   aForm.setBean(bean);
			}else{
				
			   c.setOrderId("");
			   c.setOrderDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   c.setCanSave(true);
			   c.setMode(mode);
			   aForm.setBean(c);
			}
			
			aForm.setMode(mode);//Mode Edit
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
		NSForm summaryForm = (NSForm) form;
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
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		NSForm aForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String page = Utils.isNull(request.getParameter("page"));
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			NSBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(NSConstant.STATUS_OPEN);
			
			if( !Utils.isNull(h.getOrderId()).equals("")){
				NSDAO.updateNissinOrderByNissin(conn, h);
			}else{
				//Get DocNo
				h.setOrderId(String.valueOf(SequenceProcess.getNextValue("NISSIN_ORDER")));
				NSDAO.insertNissionOrderByNissin(conn, h);
			}
			
			//Search Again
			NSBean bean =NSDAO.searchNissinOrder(conn,h,page);
		
		    aForm.setBean(bean);
			
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
		return "detail";
	}
	
	@Deprecated
	public ActionForward cancelAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelAction");
		NSForm aForm = (NSForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String page = Utils.isNull(request.getParameter("page"));
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			NSBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(NSConstant.STATUS_CANCEL);
			NSDAO.updateStatusRTNControl(conn, h);
			
			//Search Again
			NSBean bean = NSDAO.searchNissinOrder(conn,h,page);
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "ยกเลิกรายการเรียบร้อยแล้ว");
			
		} catch (Exception e) {
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
	
	@Deprecated
	public ActionForward completeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("completeAction");
		NSForm aForm = (NSForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String page = Utils.isNull(request.getParameter("page"));
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			NSBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(NSConstant.STATUS_COMPLETE);
			NSDAO.updateStatusRTNControl(conn, h);
			
			//Search Again
			NSBean cri = new NSBean();
			cri.setOrderId(h.getOrderId());
			NSBean bean =  NSDAO.searchNissinOrder(conn,cri,page);//NSDAO.searchHead(conn,cri,true,page).getItems().get(0);
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "บันทึกรายการเรียบร้อยแล้ว");
			
		} catch (Exception e) {
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
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		NSForm aForm = (NSForm) form;
		try {
			aForm.setResults(new ArrayList<NSBean>());
			
			NSBean ad = new NSBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			//ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward preparePens(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("preparePic");
		NSForm aForm = (NSForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String page = Utils.isNull(request.getParameter("page"));
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String orderId = Utils.isNull(request.getParameter("orderId"));
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare PIC edit docNo:"+orderId +",mode:"+mode);
		
			NSBean c = new NSBean();
			c.setOrderId(orderId);
			if( !Utils.isNull(orderId).equals("")){
			  NSBean bean = NSDAO.searchNissinOrder(c,page);;//NSDAO.searchHead(c,true,page).getItems().get(0);
			 
			  aForm.setBean(bean);
			}
			
			aForm.setMode(mode);//Mode Edit
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detailPens");
	}
	
	public ActionForward savePens(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("savePens");
		NSForm aForm = (NSForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String page = Utils.isNull(request.getParameter("page"));
			String action = Utils.isNull(request.getParameter("action"));
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			NSBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			if("pending".equals(action)){
				h.setStatus(NSConstant.STATUS_PENDING);
			}else{
				h.setStatus(NSConstant.STATUS_COMPLETE);
				h.setPendingReason("");
			}
			
			NSDAO.updateNissinOrderByPens(conn, h);
			
			//Search Again
			NSBean bean =NSDAO.searchNissinOrder(conn,h,page);;// NSDAO.searchHead(conn,h,true,page).getItems().get(0);
			
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "บันทึกรายการเรียบร้อยแล้ว");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detailPens");
	}
	
	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report ");
		NSForm repoNSForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
			String page = Utils.isNull(request.getParameter("page"));
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			conn = DBConnection.getInstance().getConnection();
			NSBean bean = NSDAO.searchNissinOrder(conn,repoNSForm.getBean(),page);//NSDAO.searchHead(conn,repoNSForm.getBean(),true,page);
		   
			if(bean != null && bean.getItems() != null && bean.getItems().size() >0){
				//Gen Report
				String fileName = "rt_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, bean.getItems());
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล");
				return  mapping.findForward("prepare");
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {
				
			}
		}
		return null;
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel: ");
		Connection conn = null;
		NSForm repoNSForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer h = new StringBuffer("");
		int colSpan = 6;
		try {
			conn = DBConnection.getInstance().getConnection();
			//logger.debug("ReqPickStock:"+h);
			List<NSBean> resultList = NSDAO.searchHeadList(conn, repoNSForm.getBean(), true, 0, 0,"");
				
			if(resultList != null && resultList.size()>0){
					
			  h.append(ExcelHeader.EXCEL_HEADER);
			  h.append("<table border='1'> \n");
			  h.append("<tr> \n");
				    h.append("<th>ID</th>\n");
					h.append("<th>Status</th>\n");
					h.append("<th>Nissin Ordered Date</th>\n");
					h.append("<th>Pens Open Date</th>\n");
					h.append("<th>Type</th>\n");
					h.append("<th>Customer Code</th>\n");
					h.append("<th>Customer Name</th>\n");
					h.append("<th>Region</th>\n");
					h.append("<th>Province</th>\n");
					h.append("<th>Address Line1</th>\n");
					h.append("<th>Address Line2</th>\n");
					h.append("<th>Phone Number</th>\n");
					h.append("<th>Invoice No</th>\n");
					h.append("<th>Invoice Date</th>\n");
					h.append("<th>Complete Date</th>\n");
					h.append("<th>Sale Code</th>\n");
					
					h.append("<th>Cup72 (CTN)</th>\n");
					h.append("<th>Cup72 (CUP)</th>\n");
					
					h.append("<th>BAG6(CTN)</th>\n");
					h.append("<th>BAG6(BAG)</th>\n");
					
					h.append("<th>BAG10(CTN)</th>\n");
					h.append("<th>BAG10(BAG)</th>\n");
					
					h.append("<th>Pooh72 (CTN)</th>\n");
					h.append("<th>Pooh72 (CUP)</th>\n");
					
					h.append("<th>Remark1</th>\n");
					h.append("<th>Pending reason</th>\n");
			h.append("</tr> \n");

			  for(int n=0;n<resultList.size();n++){
				   NSBean mc = (NSBean)resultList.get(n);
					h.append("<tr> \n");
					h.append("<td>"+mc.getOrderId() +"</td>");
					h.append("<td class='text'>"+mc.getStatusDesc()+"</td>"); 
					h.append("<td class='text'>"+mc.getNissinOrderDate() +"</td>");
					h.append("<td class='text'>"+mc.getOrderDate() +"</td>");
					h.append("<td class='text'>"+mc.getCustomerType()+"</td>");
					h.append("<td class='text'>"+mc.getCustomerCode()+"</td>");
				    h.append("<td class='text'>"+mc.getCustomerName()+"</td>");
				    h.append("<td class='text'>"+mc.getChannelName()+"</td>");
				    h.append("<td class='text'>"+mc.getProvinceName()+"</td>");
				    h.append("<td class='text'>"+mc.getAddressLine1() +"</td>");
				    h.append("<td class='text'>"+mc.getAddressLine2()+"</td>");
					h.append("<td class='text'>"+mc.getPhone()+"</td>");
					h.append("<td class='text'>"+mc.getInvoiceNo()+"</td>");
					h.append("<td class='text'>"+mc.getInvoiceDate()+"</td>");
					h.append("<td class='text'>"+mc.getCompleteDate()+"</td>");
					h.append("<td>"+mc.getSaleCode()+"</td>");
					h.append("<td>"+mc.getCupQty()+"</td>");
					h.append("<td>"+mc.getCupNQty()+"</td>");
					h.append("<td>"+mc.getPac6CTNQty()+"</td>");
					h.append("<td>"+mc.getPac6Qty()+"</td>");
					h.append("<td>"+mc.getPac10CTNQty()+"</td>");
					h.append("<td>"+mc.getPac10Qty()+"</td>");
					h.append("<td>"+mc.getPoohQty()+"</td>"); 
					h.append("<td>"+mc.getPoohNQty()+"</td>"); 
					
					h.append("<td class='text'>"+mc.getRemark()+"</td>"); 
					h.append("<td class='text'>"+mc.getPendingReason()+"</td>"); 
				    h.append("</tr>");
				}
			
			    //Get Summary
			    NSBean summary = NSDAO.searchHeadSummary(conn, repoNSForm.getBean());
			    if(summary !=null){
			    	h.append("<tr>");
			    	h.append("<td class='colum_head' colspan='15'>Total</td>");
					h.append("<td class='num_currency_bold'>"+summary.getCupQty()+"</td>");
					h.append("<td class='num_currency_bold'>"+summary.getCupNQty()+"</td>");
					h.append("<td class='num_currency_bold'>"+summary.getPac6CTNQty()+"</td>");
					h.append("<td class='num_currency_bold'>"+summary.getPac6Qty()+"</td>");
					h.append("<td class='num_currency_bold'>"+summary.getPac10CTNQty()+"</td>");
					h.append("<td class='num_currency_bold'>"+summary.getPac10Qty()+"</td>");
					h.append("<td class='num_currency_bold'>"+summary.getPoohQty()+"</td>"); 
					h.append("<td class='num_currency_bold'>"+summary.getPoohNQty()+"</td>"); 
					h.append("<td colspan='2'></td>"); 
				    h.append("</tr>");
			    }
			    h.append("</table>");
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(h.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  ");
				return  mapping.findForward("search");
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;
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
