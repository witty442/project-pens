package com.isecinc.pens.web.report.analyst;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.report.analyst.bean.ABean;
import com.isecinc.pens.web.report.analyst.helper.SecurityHelper;
import com.isecinc.pens.web.report.analyst.process.AGenerate;
import com.isecinc.pens.web.report.analyst.process.AInitial;
import com.isecinc.pens.web.report.analyst.process.ProfileProcess;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;


/**
  WITTY
  SAReportAction
 * 
 */
public class AReportAction extends I_Action {
	protected Logger logger = Logger.getLogger("PENS");
	public static AInitial aInit = null;
	
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SalesAnalystReportAction Prepare have ID Form action:"+request.getParameter("action"));
		AReportForm formBean = (AReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "prepare";
		try {
			logger.debug("Clear Session");
			request.getSession().setAttribute("aReportForm_RESULT",null);
			request.getSession().setAttribute("aReportForm_SQL",null);
			//Display User Role Info\
			request.getSession().setAttribute("USER_ROLE_INFO", SecurityHelper.genHtmlUserRoleInfo(user));	
			
			ABean ABean = new ABean();
			ABean.setIncludePos("Y");
			
			formBean.setSalesBean(ABean);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		return returnText;
	}
	
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SalesAnalystReportAction Prepare Form action:"+request.getParameter("action"));
		AReportForm formBean = (AReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "prepare";
		try {
			if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
				logger.debug("Clear Session");
				request.getSession().setAttribute("aReportForm_RESULT",null);
				request.getSession().setAttribute("aReportForm_SQL",null);
				
				//Display User Role Info\
				request.getSession().setAttribute("USER_ROLE_INFO", SecurityHelper.genHtmlUserRoleInfo(user));	
				
				ABean ABean = new ABean();
				ABean.setIncludePos("Y");
				
				formBean.setSalesBean(ABean);
				formBean.setReportName(Utils.isNull(request.getParameter("reportName")));
				
				//set reportName by Page User choose
				logger.debug("AReport Prepare:reportName:"+formBean.getReportName());
				
				//clear parameter init
				request.getSession().removeAttribute("PARAM_INIT_"+Utils.isNull(request.getParameter("reportName")));
				 
				//init first 
				aInit = new AInitial().getAInit(request);
				
				logger.debug("TableViewName:"+aInit.TABLE_VIEW);
	
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		return returnText;
	}
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		AReportForm formBean = (AReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		Connection conn = null;
		String htmlCode = "";
		String sql = "";
		AGenerate aGenerate = null;
		try {
			logger.debug("search");
			aInit = new AInitial().getAInit(request);
			aGenerate = new AGenerate(aInit);
			logger.debug("AReport:tableName:"+aInit.TABLE_VIEW);
			
			conn = DBConnection.getInstance().getConnection();
			
		    String order_by_name = request.getParameter("order_by_name");
		    String order_type = request.getParameter("order_type");

		    if (order_by_name!=null) formBean.getSalesBean().setOrder_by_name(order_by_name);
		    if (order_type!=null) formBean.getSalesBean().setOrder_type(order_type);
		
		    List<StringBuffer> resultList = aGenerate.genReport(conn,request.getContextPath(),user,formBean.getSalesBean());
		    if(resultList != null && resultList.size()> 0){
		       htmlCode = ((StringBuffer)resultList.get(0)).toString();
			   sql = ((StringBuffer)resultList.get(1)).toString();
		    }
		    
			//logger.debug("htmlCode: \n"+htmlCode);
			aInit.setMaxOrderedDateTime(conn);
			String maxDate = aInit.getMaxOrderedDate();
			String maxTime = aInit.getMaxOrderedTime();
			
			request.getSession().setAttribute("maxOrderedDate", maxDate);
			request.getSession().setAttribute("maxOrderedTime", maxTime);
			
			if( !Utils.isNull(htmlCode).equals("")){
			    request.getSession().setAttribute("aReportForm_RESULT", htmlCode);
			    request.getSession().setAttribute("aReportForm_SQL", sql);
			}else{
				logger.debug("Data not found");
			    request.setAttribute("Message", "ไม่พบข้อมูลที่ค้นหา");
			    request.getSession().setAttribute("aReportForm_RESULT", null);	
			    request.getSession().setAttribute("aReportForm_SQL", null);	
			}
		} catch (Exception e) {
			request.getSession().setAttribute("aReportForm_RESULT", null);	
			request.getSession().setAttribute("aReportForm_SQL", null);	
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
		return returnText;
	}
	
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		AReportForm formBean = (AReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String htmlCode ="";
		AGenerate aGenerate = null;
		String reportName = Utils.isNull(request.getParameter("reportName"));
		try {	
			aGenerate = new AGenerate(aInit);
			
			logger.debug("export");
            if( request.getSession().getAttribute("aReportForm_RESULT") != null	){
            	htmlCode = Utils.isNull(request.getSession().getAttribute("aReportForm_RESULT"));
            	
            }
		    if( !Utils.isNull(htmlCode).equals("")){
		    	/** Gen Header  Report Excel  **/
		    	String columnCount = request.getParameter("columnCount");
		    	String condDisp1 = Utils.isNull(request.getParameter("condDisp1"));
		    	String condDisp2 = Utils.isNull(request.getParameter("condDisp2"));
		    	String condDisp3 = Utils.isNull(request.getParameter("condDisp3"));
		        String condDisp4 = Utils.isNull(request.getParameter("condDisp4"));
		        String condDisp5 = Utils.isNull(request.getParameter("condDisp5"));
		        
		    	String headerHtml  = aGenerate.genHeaderReportExportExcel(reportName,user,formBean.getSalesBean(),columnCount,condDisp1,condDisp2,condDisp3,condDisp4,condDisp5).toString();
                //logger.debug("header html:\n "+headerHtml);
                
		    	StringBuffer EXCEL_HEADER = new StringBuffer("");
	
		    	/*EXCEL_HEADER.append("<style> \n");
		    	EXCEL_HEADER.append(" table {\n");
		    	EXCEL_HEADER.append(" border-collapse: collapse; \n");
		    	EXCEL_HEADER.append(" }\n");
				EXCEL_HEADER.append(" table, th, td{ \n");
				EXCEL_HEADER.append("   border: 1px solid black; \n");
				EXCEL_HEADER.append(" } \n");
				EXCEL_HEADER.append("</style> \n");*/
		    	
		  
		    	EXCEL_HEADER = ExcelHeader.EXCEL_HEADER;
		    	
		    	EXCEL_HEADER.append("<style> \n");
		    	EXCEL_HEADER.append(" table, th, td{ \n");
				EXCEL_HEADER.append("  height: 50px; \n");
				EXCEL_HEADER.append(" } \n");
				EXCEL_HEADER.append("</style> \n");
		    	
				
		    	/** Replace Tag Html Img = img_XX **/
		    	htmlCode = htmlCode.replaceAll("<img", "<img_xx");
		    	htmlCode = htmlCode.replaceAll("<table","<table border='1'");
		    	headerHtml += EXCEL_HEADER.toString()+ htmlCode;
		    		
		    	java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				logger.debug(headerHtml);
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
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}

	public ActionForward getSQL(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		try {	
			
			logger.debug("getSQL");
			String sql = "";
			if( request.getSession().getAttribute("aReportForm_SQL") != null	){
            	sql = Utils.isNull(request.getSession().getAttribute("aReportForm_SQL"));
            }
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=sql.sql");
			response.setContentType("text/plain");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(sql);
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward saveProfile(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		try {	
			logger.debug("saveProfile");
			String reportName = Utils.isNull(request.getParameter("reportName"));
			
			AReportForm formBean = (AReportForm) form;
			formBean.getSalesBean().setReportName(reportName);
			
			User user = (User) request.getSession().getAttribute("user");
			boolean status = ProfileProcess.saveProfile(user, formBean.getSalesBean());
			ABean profileBean =  ProfileProcess.getProfile(user.getId()+"", formBean.getSalesBean().getProfileId(),formBean.getSalesBean().getReportName());
			
			if(status){
			   request.setAttribute("Message", "บันทึกข้อมูลรูปแแบบการค้นหา   "+profileBean.getProfileId()+"-"+profileBean.getProfileName() +" เรียบร้อยแล้ว");
			}else{
			   request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลรูปแแบบการค้นหา   "+profileBean.getProfileId()+"-"+profileBean.getProfileName() +" โปรดตรวจสอบข้อมูล");
			}
			
			request.getSession().setAttribute("aReportForm_RESULT", null);	
			request.getSession().setAttribute("aReportForm_SQL", null);	
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}

	
	public ActionForward changeProfile(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		try {	
			logger.debug("changeProfile");
			String reportName = Utils.isNull(request.getParameter("reportName"));
			
			AReportForm formBean = (AReportForm) form;
			User user = (User) request.getSession().getAttribute("user");
			ABean ABean = ProfileProcess.getProfile(user.getId()+"", formBean.getSalesBean().getProfileId(),reportName);
			
			
			formBean.setSalesBean(ABean);
			
			/*if(status){
			   request.setAttribute("Message", "บันทึกข้อมูลรูปแแบบการค้นหา  Profile "+formBean.getSalesBean().getProfileId() +"เรียบร้อยแล้ว");
			}else{
			   request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลรูปแแบบการค้นหา  Profile "+formBean.getSalesBean().getProfileId() +"โปรดตรวจสอบข้อมูล");
			}*/
			request.getSession().setAttribute("aReportForm_RESULT", null);	
			request.getSession().setAttribute("aReportForm_SQL", null);	
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}

	/**
	 * Set New Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		
	}
	
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "re-search";
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
