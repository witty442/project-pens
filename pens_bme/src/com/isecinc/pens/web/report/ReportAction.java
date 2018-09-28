package com.isecinc.pens.web.report;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ReportAction extends I_Action {

	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		ReportForm reportForm = (ReportForm) form;
		try {
			 logger.debug("prepare");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("RESULT", null);
				 reportForm.setSalesBean(new SABean());
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
		ReportForm reportForm = (ReportForm) form;
		try {
			logger.debug("prepare 2");
			if("new".equalsIgnoreCase(request.getParameter("action"))){
				request.getSession().setAttribute("RESULT", null);
				reportForm.setSalesBean(new SABean());
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReportForm formBean = (ReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		Connection conn = null;
		String htmlCode = "";
		String sql = "";
		try {
			logger.debug("search");
			
			conn = DBConnection.getInstance().getConnection();
		    String order_by_name = request.getParameter("order_by_name");
		    String order_type = request.getParameter("order_type");

		    if (order_by_name!=null) formBean.getSalesBean().setOrder_by_name(order_by_name);
		    if (order_type!=null) formBean.getSalesBean().setOrder_type(order_type);
		    
		    List<StringBuffer> resultList = SAGenerate.genReport(conn,request.getContextPath(),user,formBean.getSalesBean());
		    if(resultList != null && resultList.size()> 0){
		       htmlCode = ((StringBuffer)resultList.get(0)).toString();
			   sql = ((StringBuffer)resultList.get(1)).toString();
		    }
		  
			if( !Utils.isNull(htmlCode).equals("")){
			    request.getSession().setAttribute("RESULT", htmlCode);
			    request.getSession().setAttribute("RESULT_SQL", sql);
			}else{
				logger.debug("Data not found");
			    request.setAttribute("Message", "ไม่พบข้อมูลที่ค้นหา");
			    request.getSession().setAttribute("RESULT", null);	
			    request.getSession().setAttribute("RESULT_SQL", null);	
			}
			
		} catch (Exception e) {
			request.getSession().setAttribute("RESULT", null);	
			request.getSession().setAttribute("RESULT_SQL", null);	
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
		return returnText;
	}

	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ReportForm formBean = (ReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String htmlCode = "";
		try {
			logger.debug("export");
            if( request.getSession().getAttribute("RESULT") != null	){
            	htmlCode = Utils.isNull(request.getSession().getAttribute("RESULT"));
            }
            
		    if( !Utils.isNull(htmlCode).equals("")){
		    	/** Gen Header  Report Excel  **/
		    	String columnCount = "6";// request.getParameter("columnCount");
		    	String condDisp1 = Utils.isNull(request.getParameter("condDisp1"));
		    	String condDisp2 = Utils.isNull(request.getParameter("condDisp2"));
		    	String condDisp3 = Utils.isNull(request.getParameter("condDisp3"));
		        String condDisp4 = Utils.isNull(request.getParameter("condDisp4"));
		        String condDisp5 = Utils.isNull(request.getParameter("condDisp5"));
		        
		    	String headerHtml  = SAGenerate.genHeaderReportExportExcel( user,formBean.getSalesBean(),columnCount,condDisp1,condDisp2,condDisp3,condDisp4,condDisp5).toString();
                //logger.debug("header html:\n "+headerHtml);
                
		    	/** Replace Tag Html Img = img_XX **/
		    	htmlCode = htmlCode.replaceAll("<img", "<img_xx");
		    	headerHtml += htmlCode;
		    		
		    	java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(headerHtml);
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูลที่ค้นหา");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("export");
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//Connection conn = null;
		//SummaryForm summaryForm = (SummaryForm) form;
		try {
			 
		} catch (Exception e) {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReportForm reportForm = (ReportForm) form;
		try {
			request.getSession().setAttribute("RESULT", null);
			 reportForm.setSalesBean(new SABean());
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
