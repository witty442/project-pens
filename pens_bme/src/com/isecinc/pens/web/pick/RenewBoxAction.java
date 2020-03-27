package com.isecinc.pens.web.pick;

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
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.RenewBox;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.dao.OnhandDAO;
import com.isecinc.pens.dao.PickStockDAO;
import com.isecinc.pens.dao.RenewBoxDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class RenewBoxAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		RenewBoxForm f = (RenewBoxForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				f.setResultsSearch(null);
				
				f.setBean(new RenewBox());
			}else if("back".equals(action)){
				RenewBox re = RenewBoxDAO.searchHead(f.getBeanCriteria(),"");
				f.setBean(re);
				f.setResultsSearch(re.getItems());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		RenewBoxForm f = (RenewBoxForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			RenewBox re = RenewBoxDAO.searchHead(f.getBean(),"");
			f.setBean(re);
			f.setResultsSearch(f.getBean().getItems());
			if(f.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   f.setResultsSearch(null);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return mapping.findForward("search2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		RenewBoxForm f = (RenewBoxForm) form;
		try {
			f.setResultsSearch(null);
			f.setBean(new RenewBox());	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		RenewBoxForm f = (RenewBoxForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String mode =Utils.isNull(request.getParameter("mode"));
			conn = DBConnection.getInstance().getConnection();
			
			f.setBeanCriteria(new RenewBox());
			
			if("VIEW".equalsIgnoreCase(mode)){
				conn = DBConnection.getInstance().getConnection();
				
				//List<RenewBox> results =  RenewBoxDAO.searchBarcoceItemInBoxCasePickSomeItem(conn);
				//f.setResults(results);
				
			}else{
				//new 
				f.setBean(new RenewBox());
				List<RenewBox> results =  RenewBoxDAO.searchBarcoceItemInBoxCasePickSomeItem(conn,f.getBean());
			    f.setResults(results);

			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RenewBoxForm f = (RenewBoxForm) form;
		try {
			logger.debug("prepare 2");
			f.setBean(new RenewBox());
			
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
		RenewBoxForm f = (RenewBoxForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			  conn = DBConnection.getInstance().getConnection();
			  logger.debug("jobid:"+f.getBean().getJobId());
			  List<RenewBox> results =  RenewBoxDAO.searchBarcoceItemInBoxCasePickSomeItem(conn,f.getBean());
			  if(results != null && results.size() >0){
				  f.setResults(results);
			  }else{
				  f.setResults(null);
				  msg = "ไม่พบข้อมูล"; 
			  }
			  request.setAttribute("Message", msg);
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

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		RenewBoxForm f = (RenewBoxForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String boxNoOld = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//PickStock Head
			RenewBox h = f.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Set Items
			List<RenewBox> itemList = new ArrayList<RenewBox>();
		
			String[] lineId = request.getParameterValues("lineId");
			String[] boxNo = request.getParameterValues("boxNo");
			String[] jobId = request.getParameterValues("jobId");
			String[] qty = request.getParameterValues("qty");
			
			logger.debug("lineId:"+lineId.length);
			
			//add value to Results
			if(boxNo != null && boxNo.length > 0){
				for(int i=0;i<boxNo.length;i++){
					if( !Utils.isNull(lineId[i]).equals("")){
						RenewBox l = new RenewBox();
						 l.setLineId(i+1);
						 l.setBoxNo(Utils.isNull(boxNo[i]));
						 l.setJobId(Utils.isNull(jobId[i]));
						 l.setQty(qty[i]);
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 
						 boxNoOld +="'"+l.getBoxNo()+"',";
						 
						 itemList.add(l);
					}
				}
			}
			
			h.setItems(itemList);
			
			RenewBoxDAO.save(conn, h);
			
			conn.commit();
			
			request.setAttribute("Message","Gen New BoxNo เรียบร้อยแล้ว");
			
			//search agian
			if(boxNoOld.length()>0){
				boxNoOld = boxNoOld.substring(0,boxNoOld.length()-1);
			}
			
			//return to main search
			RenewBox re = RenewBoxDAO.searchHead(f.getBean(),boxNoOld);
			f.setBean(re);
			f.setResultsSearch(f.getBean().getItems());
			
		} catch (Exception e) {
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
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
		return "prepare2";
	}
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		RenewBoxForm f = (RenewBoxForm) form;
		try {
		//	aForm.setResults(new ArrayList<Barcode>());
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
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
		RenewBoxForm reportForm = (RenewBoxForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			Barcode b = new Barcode();
			b.setJobId(Utils.isNull(request.getParameter("jobId")));
			b.setBoxNo(Utils.isNull(request.getParameter("boxNo")));
			
			Barcode h = BarcodeDAO.searchReport(b);
			if(h != null){
				//Head
				parameterMap.put("p_boxno", h.getBoxNo());
				parameterMap.put("p_jobname", h.getJobId()+"-"+h.getName());
				parameterMap.put("p_remark", Utils.isNull(h.getRemark()));
	
				//Gen Report
				String fileName = "boxno_pdf_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, h.getItems());
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  พิมพ์รายการที่มีสถานะเป็น CLOSE เท่านั้น");
				return  mapping.findForward("prepare");
			}
		} catch (Exception e) {
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				// conn.close();
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
