package com.isecinc.pens.web.popup;

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
import com.isecinc.pens.bean.ConfirmReturnWacoal;
import com.isecinc.pens.bean.ControlReturnReport;
import com.isecinc.pens.bean.ReqFinish;
import com.isecinc.pens.bean.ReturnBoxReport;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.ConfFinishDAO;
import com.isecinc.pens.dao.ConfirmReturnWacoalDAO;
import com.isecinc.pens.dao.ReqReturnWacoalDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.pick.ConfFinishForm;
import com.isecinc.pens.web.temp.TempForm;
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
public class PrintGroupCodeAction extends I_Action {


	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		TempForm aForm = (TempForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String requestNo = Utils.isNull(request.getParameter("requestNo"));
			if( !"".equals(requestNo)){
				ReqFinish r = new ReqFinish();
				r.setRequestNo(requestNo);
			    List<ReqFinish> groupCodeList = ConfFinishDAO.searchItemByGroupCode(conn,r);
			    request.getSession().setAttribute("results", groupCodeList);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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
		TempForm summaryForm = (TempForm) form;
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
		TempForm orderForm = (TempForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			
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
		TempForm aForm = (TempForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
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
		return "search";
	}
	
	public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("print : " + this.getClass());
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		List<ReqFinish> dataList = new ArrayList<ReqFinish>();
		HashMap parameterMap = new HashMap();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			String[] groupCodes = request.getParameterValues("groupCode");
			String[] qtys = request.getParameterValues("qty");
			
			if(groupCodes != null){
				for(int i=0;i<groupCodes.length;i++){
					int qty = Utils.convertStrToInt(qtys[i]);
					if(qty !=0){
						for(int a=0;a<qty;a++){
							ReqFinish req = new ReqFinish();
							req.setGroupCode(groupCodes[i]);
							dataList.add(req);
						}
					}
				}
			}
	        
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);
			
			if(dataList != null && dataList.size()> 0){
				
				//Gen Report
				String fileName = "group_code_box_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, dataList);
				
				//request.setAttribute("printSuccess", "printSuccess");
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"
					+ e.getMessage());
		} finally {
			try {
				if(conn !=null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return null;
	}
	
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		TempForm aForm = (TempForm) form;
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
