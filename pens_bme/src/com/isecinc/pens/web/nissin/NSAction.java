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

import javax.management.DescriptorKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ExcelHeader;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.NSBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.NSDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.process.SequenceProcess;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class NSAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	public static String STATUS_SAVE ="SV";
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		NSForm aForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String page = Utils.isNull(request.getParameter("page"));
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				NSBean ad = new NSBean();
				ad.setNoPicRcv("true");
				ad.setCreateUser(user.getUserName());
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				NSBean cri  = aForm.getBeanCriteria();
				//cri.setDocNo("");
				aForm.setBean(NSDAO.searchHead(cri,false,page));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		NSForm aForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			String page = Utils.isNull(request.getParameter("page"));
			NSBean b = aForm.getBean();
			aForm.setBean(NSDAO.searchHead(aForm.getBean(),false,page));
			
			aForm.setResultsSearch(aForm.getBean().getItems());

			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	

	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		NSForm aForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResultsSearch(null);
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
			   NSBean bean = NSDAO.searchHead(c,true,page).getItems().get(0);
			   bean.setMode(mode);
			   
			   aForm.setBean(bean);
			}else{
				
			   c.setOrderId("");
			   c.setOrderDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   c.setCanSave(true);
			   c.setMode(mode);
			   aForm.setBean(c);
			}
			
			aForm.setMode(mode);//Mode Edit
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
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
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		NSForm orderForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			NSBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			//orderForm.setResults(aS.getItems());

			orderForm.setBean(aS);
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
			NSBean result = NSDAO.searchHead(conn,h,true,page);
			logger.debug("Size:"+result.getItems());
			
			NSBean bean = result.getItems().get(0);
		
		    aForm.setBean(bean);
			
			conn.commit();
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
			NSBean bean = NSDAO.searchHead(conn,h,true,page).getItems().get(0);
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
			NSBean bean = NSDAO.searchHead(conn,cri,true,page).getItems().get(0);
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
			  NSBean bean = NSDAO.searchHead(c,true,page).getItems().get(0);
			 
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
			NSBean bean = NSDAO.searchHead(conn,h,true,page).getItems().get(0);
			
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
			NSBean bean = NSDAO.searchHead(conn,repoNSForm.getBean(),true,page);
		   
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
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
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
		NSForm repoNSForm = (NSForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		StringBuffer h = new StringBuffer("");
		int colSpan = 6;
		try {
			//logger.debug("ReqPickStock:"+h);
			List<NSBean> resultList = repoNSForm.getResultsSearch();
				
			if(resultList != null && resultList.size()>0){
					
			  h.append(ExcelHeader.EXCEL_HEADER);
			  h.append("<table border='1'> \n");
			  h.append("<tr> \n");
				    h.append("<th>ID</th>\n");
					h.append("<th>Status</th>\n");
					h.append("<th>วันที่บันทึก</th>\n");
					h.append("<th>ประเภท</th>\n");
					h.append("<th>รหัสร้านค้า</th>\n");
					h.append("<th>ชื่อร้านค้า</th>\n");
					h.append("<th>ภาค</th>\n");
					h.append("<th>จังหวัด</th>\n");
					h.append("<th>ที่อยู่ Line1</th>\n");
					h.append("<th>ที่อยู่ Line2</th>\n");
					h.append("<th>เบอร์โทรศัพท์</th>\n");
					h.append("<th>Invoice No</th>\n");
					h.append("<th>Invoice Date</th>\n");
					h.append("<th>รหัส Sale</th>\n");
					
					h.append("<th>Cup72 (หีบ)</th>\n");
					h.append("<th>Cup72 (ถ้วย)</th>\n");
					h.append("<th>ซอง (หีบ)</th>\n");
					h.append("<th>ซอง  (ซอง)</th>\n");
					h.append("<th>Pooh72 (หีบ)</th>\n");
					h.append("<th>Pooh72 (ถ้วย)</th>\n");
					
					h.append("<th>Remark1</th>\n");
					h.append("<th>Pending reason</th>\n");
			h.append("</tr> \n");

			  for(int n=0;n<resultList.size();n++){
				NSBean mc = (NSBean)resultList.get(n);
					h.append("<tr> \n");
					h.append("<td>"+mc.getOrderId() +"</td>");
					h.append("<td>"+mc.getStatusDesc()+"</td>"); 
					h.append("<td>"+mc.getOrderDate() +"</td>");
					h.append("<td>"+mc.getCustomerType()+"</td>");
					h.append("<td>"+mc.getCustomerCode()+"</td>");
				    h.append("<td>"+mc.getCustomerName()+"</td>");
				    h.append("<td>"+mc.getChannelName()+"</td>");
				    h.append("<td>"+mc.getProvinceName()+"</td>");
				    h.append("<td>"+mc.getAddressLine1() +"</td>");
				    h.append("<td>"+mc.getAddressLine2()+"</td>");
					h.append("<td>"+mc.getPhone()+"</td>");
					h.append("<td>"+mc.getInvoiceNo()+"</td>");
					h.append("<td>"+mc.getInvoiceDate()+"</td>");
					h.append("<td>"+mc.getSaleCode()+"</td>");
					h.append("<td>"+mc.getCupQty()+"</td>");
					h.append("<td>"+mc.getCupNQty()+"</td>");
					h.append("<td>"+mc.getPacQty()+"</td>");
					h.append("<td>"+mc.getPacNQty()+"</td>");
					h.append("<td>"+mc.getPoohQty()+"</td>"); 
					h.append("<td>"+mc.getPoohNQty()+"</td>"); 
					
					h.append("<td>"+mc.getRemark()+"</td>"); 
					h.append("<td>"+mc.getPendingReason()+"</td>"); 
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
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
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
