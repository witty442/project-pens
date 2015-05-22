package com.isecinc.pens.web.rt;

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

import util.BeanParameter;
import util.BundleUtil;
import util.ExcelHeader;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.RTBean;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.RTDAO;
import com.isecinc.pens.dao.ReqPickStockDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.pick.ReqPickStockForm;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class RTAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	public static String STATUS_SAVE ="SV";
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				RTBean ad = new RTBean();
				ad.setNoPicRcv("true");
				ad.setCreateUser(user.getUserName());
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				RTBean cri  =aForm.getBeanCriteria();
				cri.setDocNo("");
				aForm.setBean(RTDAO.searchHead(cri,false));
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
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			RTBean b = aForm.getBean();
			aForm.setBean(RTDAO.searchHead(aForm.getBean(),false));
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
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new RTBean());
			
			RTBean ad = new RTBean();
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
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String docNo = Utils.isNull(request.getParameter("docNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare edit docNo:"+docNo +",mode:"+mode);
		
			RTBean c = new RTBean();
			if( !Utils.isNull(docNo).equals("")){
			   c.setDocNo(docNo);
			   RTBean bean = RTDAO.searchHead(c,true).getItems().get(0);
			   aForm.setBean(bean);
			}else{
			   c.setDocNo("");
			   c.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   c.setCanSave(true);
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
		RTForm summaryForm = (RTForm) form;
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
		RTForm orderForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			RTBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
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
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			RTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(RTConstant.STATUS_OPEN);
			
			if( !Utils.isNull(h.getDocNo()).equals("")){
				RTDAO.updateRTNControlBySale(conn, h);
			}else{
				//Get DocNo
				h.setDocNo(RTDAO.genDocNo(new Date(),h.getCustGroup()));
				RTDAO.insertRTNControlBySale(conn, h);
			}
			
			//Search Again
			
			RTBean result = RTDAO.searchHead(conn,h,true);
			logger.debug("Size:"+result.getItems().size());
			
			RTBean bean = result.getItems().get(0);
		
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
	
	public ActionForward cancelAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelAction");
		RTForm aForm = (RTForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			RTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(RTConstant.STATUS_CANCEL);
			RTDAO.updateStatusRTNControl(conn, h);
			
			//Search Again
			RTBean bean = RTDAO.searchHead(conn,h,true).getItems().get(0);
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
	
	public ActionForward completeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("completeAction");
		RTForm aForm = (RTForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			RTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(RTConstant.STATUS_COOMFIRM);
			RTDAO.updateStatusRTNControl(conn, h);
			
			//Search Again
			RTBean bean = RTDAO.searchHead(conn,h,true).getItems().get(0);
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
		RTForm aForm = (RTForm) form;
		try {
			aForm.setResults(new ArrayList<RTBean>());
			
			RTBean ad = new RTBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			//ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward preparePic(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("preparePic");
		RTForm aForm = (RTForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String docNo = Utils.isNull(request.getParameter("docNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare PIC edit docNo:"+docNo +",mode:"+mode);
		
			RTBean c = new RTBean();
			c.setDocNo(docNo);
			if( !Utils.isNull(docNo).equals("")){
			  RTBean bean = RTDAO.searchHead(c,true).getItems().get(0);
			  if(bean.getStatus().equals(RTConstant.STATUS_COOMFIRM) || bean.getStatus().equals(RTConstant.STATUS_RECEIVED)){
				bean.setCanPicSave(true);
			  }
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
		return mapping.findForward("detailPic");
	}
	
	public ActionForward savePic(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("savePic");
		RTForm aForm = (RTForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			RTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(RTConstant.STATUS_RECEIVED);
			
			logger.debug("remarkTeamPic:"+Utils.isNull(h.getRemarkTeamPic()));
			
			RTDAO.updateRTNControlByPic(conn, h);
			
			//Search Again
			RTBean bean = RTDAO.searchHead(conn,h,true).getItems().get(0);
			if(bean.getStatus().equals(RTConstant.STATUS_COOMFIRM) || bean.getStatus().equals(RTConstant.STATUS_RECEIVED)){
				 bean.setCanPicSave(true);
			}
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
		return mapping.findForward("detailPic");
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
		RTForm reportForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			conn = DBConnection.getInstance().getConnection();
			RTBean bean = RTDAO.searchHead(conn,reportForm.getBean(),true);
		   
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
		RTForm reportForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		StringBuffer h = new StringBuffer("");
		int colSpan = 6;
		try {
			//logger.debug("ReqPickStock:"+h);
			List<RTBean> resultList = reportForm.getResultsSearch();
				
			if(resultList != null && resultList.size()>0){
					
			  h.append(ExcelHeader.EXCEL_HEADER);
			  h.append("<table border='1'> \n");
			  h.append("<tr> \n");
					h.append("<th >Authorize return No</th >");
					h.append("<th >เล่มที่/เลขที่</th >");
					h.append("<th >วันที่บันทึก</th >");
					h.append("<th >กลุ่มร้านค้า</th >");
					h.append("<th >รหัสร้านค้า</th >");
					h.append("<th >ชื่อร้านค้า</th >");
					h.append("<th >RTN NO</th >");
					h.append("<th >จำนวนหีบใน RTN</th >");
					h.append("<th >จำนวนชิ้นใน RTN</th >");
					h.append("<th >วันที่ PIC รับสินค้า</th >");
					h.append("<th >จำนวนหีบ ที่ PIC รับ</th >");
					h.append("<th >จำนวนชิ้นที่ Scan จริง</th >");
					h.append("<th >Status</th >");
					h.append("<th >หมายเหตุ</th >");
					h.append("<th >ชื่อขนส่งที่ไปรับจากห้าง</th >");
					h.append("<th >วันที่นัดมาส่งของที่ PD</th >");
					h.append("<th >จำนวนหีบที่จัดส่ง</th >");
					h.append("<th >สิ่งอื่นที่ส่งมาเพิ่มเติม 1</th >");
					h.append("<th >สิ่งอื่นที่ส่งมาเพิ่มเติม 2</th >");
					h.append("<th >สิ่งอื่นที่ส่งมาเพิ่มเติม 3</th >");
					h.append("<th >สิ่งอื่นที่ส่งมาเพิ่มเติม 4</th >");
			h.append("</tr> \n");

			  for(int n=0;n<resultList.size();n++){
				RTBean mc = (RTBean)resultList.get(n);
					h.append("<tr> \n");
					h.append("<td>"+mc.getDocNo() +"</td>");
					h.append("<td>"+mc.getRefDoc() +"</td>");
					h.append("<td>"+mc.getDocDate()+"</td>");
				    h.append("<td>"+mc.getCustGroup() +":"+mc.getCustGroupName()+"</td>");
				    h.append("<td>"+mc.getStoreCode() +"</td>");
				    h.append("<td>"+mc.getStoreName()+"</td>");
					h.append("<td>"+mc.getRtnNo()+"</td>");
					h.append("<td>"+mc.getRtnQtyCTN()+"</td>");
					h.append("<td>"+mc.getRtnQtyEA()+"</td>");
					h.append("<td>"+mc.getPicRcvDate()+"</td>");
					h.append("<td>"+mc.getPicRcvQtyCTN()+"</td>");
					h.append("<td>"+mc.getPicRcvQtyEA()+"</td>");
					h.append("<td>"+mc.getStatusDesc()+"</td>"); 
					
					h.append("<td>"+mc.getRemark()+"</td>"); 
					h.append("<td>"+mc.getDeliveryBy()+"</td>"); 
					h.append("<td>"+mc.getDeliveryDate()+"</td>"); 
					h.append("<td>"+mc.getDeliveryQty()+"</td>");  
					h.append("<td>"+mc.getAttach1()+"</td>"); 
					h.append("<td>"+mc.getAttach2()+"</td>"); 
					h.append("<td>"+mc.getAttach3()+"</td>"); 
					h.append("<td>"+mc.getAttach4()+"</td>"); 
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
