package com.isecinc.pens.web.report.salesanalyst;

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
import com.isecinc.pens.report.salesanalyst.ProfileProcess;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.report.salesanalyst.SAGenerate;
import com.isecinc.pens.report.salesanalyst.SAInitial;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.SecurityHelper;
import com.isecinc.pens.report.salesanalyst.helper.Utils;


/**
  WITTY
  SalesAnalystReportAction
 * 
 */
public class SAReportAction extends I_Action {
	protected Logger logger = Logger.getLogger("PENS");
	
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SalesAnalystReportAction Prepare Form action:"+request.getParameter("action"));
		SAReportForm formBean = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "prepare";
		try {
			logger.debug("Clear Session");
			request.getSession().setAttribute("RESULT",null);
			//Display User Role Info\
			request.getSession().setAttribute("USER_ROLE_INFO", SecurityHelper.genHtmlUserRoleInfo(user));	
			
			SABean saBean = new SABean();
			saBean.setIncludePos("Y");
			
			formBean.setSalesBean(saBean);
			
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
		SAReportForm formBean = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "prepare";
		try {
			logger.debug("Clear Session");
			request.getSession().setAttribute("RESULT",null);
			//Display User Role Info\
			request.getSession().setAttribute("USER_ROLE_INFO", SecurityHelper.genHtmlUserRoleInfo(user));	
			
			SABean saBean = new SABean();
			saBean.setIncludePos("Y");
			
			formBean.setSalesBean(saBean);

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		
		return returnText;
	}
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SAReportForm formBean = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		Connection conn = null;
		String htmlCode = "";
		String sql = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			
			logger.debug("search");
			
		    String order_by_name = request.getParameter("order_by_name");
		    String order_type = request.getParameter("order_type");

		    if (order_by_name!=null) formBean.getSalesBean().setOrder_by_name(order_by_name);
		    if (order_type!=null) formBean.getSalesBean().setOrder_type(order_type);
		    
		    List<StringBuffer> resultList = SAGenerate.genReport(conn,request.getContextPath(),user,formBean.getSalesBean());
		    if(resultList != null && resultList.size()> 0){
		       htmlCode = ((StringBuffer)resultList.get(0)).toString();
			   sql = ((StringBuffer)resultList.get(1)).toString();
		    }
		    
			//logger.debug("htmlCode: \n"+htmlCode);
			SAInitial.getInstance().setMaxOrderedDateTime(conn);
			String maxDate = SAInitial.getInstance().getMaxOrderedDate();
			String maxTime = SAInitial.getInstance().getMaxOrderedTime();
			
			request.getSession().setAttribute("maxOrderedDate", maxDate);
			request.getSession().setAttribute("maxOrderedTime", maxTime);
			
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
		SAReportForm formBean = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String htmlCode ="";
		try {	
			logger.debug("export");
            if( request.getSession().getAttribute("RESULT") != null	){
            	htmlCode = Utils.isNull(request.getSession().getAttribute("RESULT"));
            	
            }
		    if( !Utils.isNull(htmlCode).equals("")){
		    	/** Gen Header  Report Excel  **/
		    	String columnCount = request.getParameter("columnCount");
		    	String condDisp1 = Utils.isNull(request.getParameter("condDisp1"));
		    	String condDisp2 = Utils.isNull(request.getParameter("condDisp2"));
		    	String condDisp3 = Utils.isNull(request.getParameter("condDisp3"));
		        String condDisp4 = Utils.isNull(request.getParameter("condDisp4"));
		        String condDisp5 = Utils.isNull(request.getParameter("condDisp5"));
		        
		    	String headerHtml  = SAGenerate.genHeaderReportExportExcel( user,formBean.getSalesBean(),columnCount,condDisp1,condDisp2,condDisp3,condDisp4,condDisp5).toString();
                //logger.debug("header html:\n "+headerHtml);
                
		    	StringBuffer EXCEL_HEADER = new StringBuffer("");
	
		    	EXCEL_HEADER.append("<style> \n");
		    	EXCEL_HEADER.append(" table {\n");
		    	EXCEL_HEADER.append(" border-collapse: collapse; \n");
		    	EXCEL_HEADER.append(" }\n");
				EXCEL_HEADER.append(" table, th, td{ \n");
				EXCEL_HEADER.append("   border: 1px solid black; \n");
				EXCEL_HEADER.append(" } \n");
				EXCEL_HEADER.append("</style> \n");
				
		    	/** Replace Tag Html Img = img_XX **/
		    	htmlCode = htmlCode.replaceAll("<img", "<img_xx");
		    	headerHtml += EXCEL_HEADER.toString()+ htmlCode;
		    		
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
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}

	public ActionForward getSQL(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		try {	
			
			logger.debug("getSQL");
			String sql = "";
			if( request.getSession().getAttribute("RESULT_SQL") != null	){
            	sql = Utils.isNull(request.getSession().getAttribute("RESULT_SQL"));
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
			
			SAReportForm formBean = (SAReportForm) form;
			User user = (User) request.getSession().getAttribute("user");
			boolean status = ProfileProcess.saveProfile(user, formBean.getSalesBean());
			
			if(status){
			   request.setAttribute("Message", "บันทึกข้อมูลรูปแแบบการค้นหา  Profile "+formBean.getSalesBean().getProfileId() +"เรียบร้อยแล้ว");
			}else{
			   request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลรูปแแบบการค้นหา  Profile "+formBean.getSalesBean().getProfileId() +"โปรดตรวจสอบข้อมูล");
			}
			
			request.getSession().setAttribute("RESULT", null);	
			request.getSession().setAttribute("RESULT_SQL", null);	
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}

	
	public ActionForward changeProfile(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		try {	
			logger.debug("changeProfile");
			
			SAReportForm formBean = (SAReportForm) form;
			User user = (User) request.getSession().getAttribute("user");
			SABean saBean = ProfileProcess.getProfile(user.getId()+"", formBean.getSalesBean().getProfileId());
			
			
			formBean.setSalesBean(saBean);
			
			/*if(status){
			   request.setAttribute("Message", "บันทึกข้อมูลรูปแแบบการค้นหา  Profile "+formBean.getSalesBean().getProfileId() +"เรียบร้อยแล้ว");
			}else{
			   request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลรูปแแบบการค้นหา  Profile "+formBean.getSalesBean().getProfileId() +"โปรดตรวจสอบข้อมูล");
			}*/
			request.getSession().setAttribute("RESULT", null);	
			request.getSession().setAttribute("RESULT_SQL", null);	
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
