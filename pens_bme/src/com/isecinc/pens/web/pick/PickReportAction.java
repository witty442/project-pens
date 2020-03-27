package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;
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
import com.isecinc.pens.bean.MTTBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.PickReportBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.MTTBeanDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.PickReportDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.order.OrderForm;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.shop.ShopBean;
import com.isecinc.pens.web.shop.ShopForm;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PickReportAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareReport");
		PickReportForm aForm = (PickReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResults(null);
				aForm.setSummary(null);
				
				PickReportBean ad = new PickReportBean();
				ad.setStatus("ISSUE");
				aForm.setBean(ad);
				
				//init session CustGroupList
				List<PopupForm> custGroupList = new ArrayList();
				PopupForm ref = new PopupForm("",""); 
				custGroupList.add(ref);
				custGroupList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
				request.getSession().setAttribute("custGroupList",custGroupList);
				
				//session for save Check by page 
				request.getSession().setAttribute("pick_report_codes", null);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	 public static ActionForward searchReport(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
			logger.debug("searchReport");
			PickReportForm aForm = (PickReportForm) form;
			User user = (User) request.getSession().getAttribute("user");
			String msg = "";
			int currPage = 1;
			boolean allRec = false;
			Connection conn = null;
			try {
				String action = Utils.isNull(request.getParameter("action"));
				logger.debug("action:"+action);
				
				conn = DBConnection.getInstance().getConnection();
				if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
					//session for save Check by page 
					request.getSession().setAttribute("pick_report_codes", null);
					
					//case  back
					if("back".equalsIgnoreCase(action)){
						aForm.setBean(aForm.getBeanCriteria());
					}
					//default currPage = 1
					aForm.setCurrPage(currPage);
					
					//get Total Record
					String issueReqNoAll = PickReportDAO.searchHeadTotalRecList(conn,aForm.getBean());
					String[] issueReqNoArr = issueReqNoAll.split("\\,");
					aForm.setTotalRecord(issueReqNoArr.length);
					aForm.setIssueReqNoAll(issueReqNoAll);
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
					List<PickReportBean> items = PickReportDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
					aForm.setResults(items);
					
					if(items.size() <=0){
					   request.setAttribute("Message", "ไม่พบข้อมูล");
					   aForm.setResults(null);
					}else{
						//currPage ==totalPage ->get Summary
						if(currPage==aForm.getTotalPage()){
						   //aForm.setSummary(PickReportDAO.searchHeadTotalSummary(conn, aForm.getBean()));
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
					List<PickReportBean> items = PickReportDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
					aForm.setResults(items);
					
					//currPage ==totalPage ->get Summary
					if(currPage==aForm.getTotalPage()){
					  // aForm.setSummary(PickReportDAO.searchHeadTotalSummary(conn, aForm.getBean()));
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
			return mapping.findForward("search");
	}
	
	 /**
		 * Prepare without ID
		 */
		protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			String forward = "prepare";
			PickReportForm aForm = (PickReportForm) form;
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

	
	public ActionForward exportReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportReport");
		PickReportForm aForm = (PickReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			String reportType = Utils.isNull(request.getParameter("reportType"));
			String chkAll = Utils.isNull(request.getParameter("chkAll"));
			logger.debug("chkAll:"+chkAll);
			
			//get select checkbox issueReqNo filter export 
			String issueReqNoAll = "";
			if( !Utils.isNull(chkAll).equals("")){
			   issueReqNoAll =  aForm.getIssueReqNoAll();
			}else{
			   issueReqNoAll =  Utils.isNull(request.getParameter("codes"));
			}
			
			logger.debug("issueReqNoAll:"+issueReqNoAll);
			if(issueReqNoAll.length()>0){
				issueReqNoAll = issueReqNoAll.substring(0,issueReqNoAll.length()-1);
			}
			if("Normal".equalsIgnoreCase(reportType)){
			    htmlTable = genExportReport(request,aForm,user,issueReqNoAll);
			}else if("Summary".equalsIgnoreCase(reportType)){
				htmlTable = genExportReportSummary(request,aForm,user,issueReqNoAll);
			}else if("Detail".equalsIgnoreCase(reportType)){
			    htmlTable = genExportReportDetail(request,aForm,user,issueReqNoAll);
			}else if("ExportBarcode".equalsIgnoreCase(reportType)){
			    htmlTable = genExportBarcodeDetail(request, aForm);
			}
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close(); 
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	
	public StringBuffer genExportBarcodeDetail(HttpServletRequest request,ActionForm form)  throws Exception {
		logger.debug("genExportBarcodeDetail");
		PickReportForm aForm = (PickReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer h = new StringBuffer("");
		Connection conn = null;
		int qty = 0;
		int r = 0;
		try {
			conn = DBConnection.getInstance().getConnection();
			String chkAll = Utils.isNull(request.getParameter("chkAll"));
			logger.debug("chkAll:"+chkAll);
			
			//get select checkbox issueReqNo filter export 
			String issueReqNoAll = "";
			if( !Utils.isNull(chkAll).equals("")){
			   issueReqNoAll =  aForm.getIssueReqNoAll();
			}else{
			   issueReqNoAll =  Utils.isNull(request.getParameter("codes"));
			}
			
			logger.debug("issueReqNoAll:"+issueReqNoAll);
			if(issueReqNoAll.length()>0){
				issueReqNoAll = issueReqNoAll.substring(0,issueReqNoAll.length()-1);
			}
			aForm.getBean().setIssueReqNoArr(issueReqNoAll);
			
			List<PickReportBean> dataList= PickReportDAO.searchExportBarcodeDetail(conn,aForm.getBean());
			if(dataList != null){
				//logger.debug("header:"+ExcelHeader.EXCEL_HEADER.toString());
				
				h.append(ExcelHeader.EXCEL_HEADER);
				h.append("<table border='1'>");
				h.append("<tr>");
			    h.append("<td>Barcode </td>");
			    h.append("<td>Material Master</td>");
			    h.append("<td>Pens Item</td>");
	            h.append("</tr>");    
	            
	            if(dataList != null && dataList.size() > 0){
	            	for(int i=0;i<dataList.size();i++){
	            		PickReportBean o = dataList.get(i);
	            		// Loop By Qty 
	            		qty = Integer.parseInt(o.getIssueQty());
	            		for(r=0;r<qty;r++){
		            		h.append("<tr> \n");    
		            		h.append("<td class='text'>"+o.getBarcode()+"</td>");
		  				    h.append("<td class='text'>"+o.getGroupCode()+"</td>");
		  				    h.append("<td class='text'>"+o.getPensItem()+"</td>");
		  				    h.append("</tr> "); 
	            		}//for 2
	            	}//for 1
	            }
	            h.append("</table> \n"); 
	        
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return h;
	}
	
	//Normal
	private StringBuffer genExportReport(HttpServletRequest request,PickReportForm form,User user,String issueReqNoArr) throws Exception{
		StringBuffer h = new StringBuffer("");
		String colSpan = "8";
		Connection conn = null;
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr class='colum_head'> \n");
			h.append("  <td align='left' colspan='"+colSpan+"'>รายงาน ข้อมูลการเบิกสินค้าจาก PICK</td> \n");
			h.append("</tr> \n");
			h.append("<tr class='colum_head'> \n");
			h.append("  <td align='left' colspan='"+colSpan+"' >รหัสร้านค้า:"+form.getBean().getStoreCode()+"&nbsp;"+Utils.isNull(form.getBean().getStoreName())+"</td> \n");
			h.append("</tr> \n");
			h.append("<tr class='colum_head'> \n");
			h.append("  <td align='left' colspan='"+colSpan+"' >From Issue Request date:"+form.getBean().getIssueReqDateFrom()+"  To Issue Request date::"+form.getBean().getIssueReqDateTo()+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
			
		   //search new
			conn = DBConnection.getInstance().getConnectionApps();
			
			List<PickReportBean> items = PickReportDAO.searchHeadList(conn,form.getBean(),true,0,pageSize);
		
			if(items != null){
				h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				  h.append("<td>No.</td> \n");
				  h.append("<td>Store Code</td> \n");
				  h.append("<td>Store Name</td> \n");
				  h.append("<td>Issue Req No</td> \n");
				  h.append("<td>Issue Date</td> \n");
				  h.append("<td>Qty</td> \n");
				  h.append("<td>User Request</td> \n");
				  h.append("<td>Remark</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<items.size();i++){
					PickReportBean s = (PickReportBean)items.get(i);
					h.append("<tr> \n");
					  h.append("<td class='num'>"+(i+1)+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getIssueReqNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getIssueReqDate()+"</td> \n");
					  h.append("<td class='num'>"+s.getIssueQty()+"</td> \n");
					  h.append("<td class='text'>"+s.getUserRequest()+"</td> \n");
					  h.append("<td class='text'>"+s.getRemark()+"</td> \n");
					h.append("</tr>");
				}//for
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
		return h;
	}
	private StringBuffer genExportReportSummary(HttpServletRequest request,PickReportForm form,User user,String issueReqNoArr) throws Exception{
		StringBuffer h = new StringBuffer("");
		String colSpan = "3";
		Connection conn = null;
		double totalQty = 0;
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
            h.append("<table border='1'> \n");
			h.append("<tr class='colum_head'> \n");
			h.append("  <td align='left' colspan='"+colSpan+"'>Summary By Item</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
		
		   //search new
			conn = DBConnection.getInstance().getConnectionApps();
			
			PickReportBean criBean = new PickReportBean();
			criBean.setIssueReqNoArr(issueReqNoArr);
			List<PickReportBean> items = PickReportDAO.searchReportSummaryList(conn,criBean);
			if(items != null){
				h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				h.append("<td>No.</td> \n");
				h.append("<td>Pens Item</td> \n");
				h.append("<td>Group Code</td> \n");
				h.append("<td>Issue Qty</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<items.size();i++){
					PickReportBean s = (PickReportBean)items.get(i);
					h.append("<tr> \n");
					  h.append("<td class='num'>"+(i+1)+"</td> \n");
					  h.append("<td class='num'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='num'>"+s.getGroupCode()+"</td> \n");
					  h.append("<td class='num'>"+s.getIssueQty()+"</td> \n");
					h.append("</tr>");
					totalQty += Utils.convertCurrentcyToInt(s.getIssueQty());
				}//for
				h.append("</table> \n");
				logger.debug("totalQty:"+totalQty);
				
				//Add Summary
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td class='colum_head' colspan='3' align='right'>ผลรวม </td> \n");
				  h.append("<td class='num_currency_bold'>"+Utils.decimalFormat(totalQty,Utils.format_current_no_disgit)+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
		return h;
	}
	private StringBuffer genExportReportDetail(HttpServletRequest request,PickReportForm form,User user,String issueReqNoArr) throws Exception{
		StringBuffer h = new StringBuffer("");
		String colSpan = "12";
		Connection conn = null;
		int totalQty = 0;
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr class='colum_head'> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>Summary by Detail</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
			
		   //search new
			conn = DBConnection.getInstance().getConnectionApps();
			
			PickReportBean criBean = new PickReportBean();
			criBean.setIssueReqNoArr(issueReqNoArr);
			List<PickReportBean> items = PickReportDAO.searchReportDetailList(conn,criBean);
			if(items != null){
				h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				  h.append("<td>No.</td> \n");
				  h.append("<td>เลขที่ใบเบิก</td> \n");
				  h.append("<td>วันที่ตัดเบิก</td> \n");
				  h.append("<td>รหัสร้านค้า</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Pens Item</td> \n");
				  h.append("<td>Material Master</td> \n");
				  h.append("<td>Group Code</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>BoxNo</td> \n");
				  h.append("<td>W/H</td> \n");
				  h.append("<td>Issue Qty</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<items.size();i++){
					PickReportBean s = (PickReportBean)items.get(i);
					h.append("<tr> \n");
					  h.append("<td class='num'>"+(i+1)+"</td> \n");
					  h.append("<td class='text'>"+s.getIssueReqNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getIssueReqDate()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='num'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='text'>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroupCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td class='text'>"+s.getBoxNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getWareHouse()+"</td> \n");
					  h.append("<td class='num'>"+s.getIssueQty()+"</td> \n");
					h.append("</tr>");
					
					totalQty += Utils.convertCurrentcyToInt(s.getIssueQty());
				}//for
				//Add Summary
				h.append("<tr> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='num'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='colum_head'>รวมจำนวน</td> \n");
				  h.append("<td class='num_currency_bold'>"+Utils.decimalFormat(totalQty,Utils.format_current_no_disgit)+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
		return h;
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PickReportForm summaryForm = (PickReportForm) form;
		try {
			logger.debug("prepare 2");
			
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
		PickReportForm orderForm = (PickReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			PickReportBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());

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
		PickReportForm aForm = (PickReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {

		} catch (Exception e) {
			conn.rollback();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			try {
				
			} catch (Exception e2) {}
			return "prepare";
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
		PickReportForm aForm = (PickReportForm) form;
		try {
			aForm.setResults(null);
			aForm.setSummary(null);
			aForm.setBean(new PickReportBean());
			
			PickReportBean ad = new PickReportBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setStatus("ISSUE");
			aForm.setBean(ad);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
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
		
		logger.debug("Search for report : " + this.getClass());
		PickReportForm reportForm = (PickReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
	
		} catch (Exception e) {
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		// return null;
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
