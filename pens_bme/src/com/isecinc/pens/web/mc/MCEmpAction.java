package com.isecinc.pens.web.mc;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.isecinc.pens.bean.MCEmpBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.MCDAO;
import com.isecinc.pens.dao.MCEmpDAO;
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
public class MCEmpAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		MCEmpForm aForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				MCEmpBean ad = new MCEmpBean();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				aForm.setBean(MCEmpDAO.searchHead(aForm.getBeanCriteria(),false));
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
		MCEmpForm aForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setBean(MCEmpDAO.searchHead(aForm.getBean(),false));
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
	
	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareReport");
		MCEmpForm aForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				MCEmpBean ad = new MCEmpBean();
				
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchReport");
		MCEmpForm aForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			MCEmpBean b = aForm.getBean();
			//aForm.setBean(MCBeanDAO.searchHead(aForm.getBean()));
			//aForm.setResultsSearch(aForm.getBean().getItems());
			
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
		return mapping.findForward("detail");
	}
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		MCEmpForm aForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			String staffId = Utils.isNull(request.getParameter("staffId"));
            String monthTrip = Utils.isNull(request.getParameter("monthTrip"));
            String maxDayInMonth = Utils.isNull(request.getParameter("maxDayInMonth"));
            
            logger.debug("staffId:"+staffId);
           
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close(); 
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
		}
		return mapping.findForward("report");
	}
	
	private StringBuffer genExportReport(MCEmpBean b){
		StringBuffer h = new StringBuffer("");
		try{
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'@'; \n");
			h.append(" } \n");
			h.append("</style> \n");

			//Header
			h.append("<table border='1' width='100%'> \n");
			
			h.append("<tr> \n");
			//h.append("<td align='center' colspan='2'><b>รายละเอียดประจำ Trip ของเดือน "+monthYearFull+"</b></td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='2'>");
			h.append("<table> ");
				h.append("<tr> \n");
			//	h.append(" <td align='left' nowrap>&nbsp;"+b.getStaffType()+":&nbsp;"+b.getStaffId()+":&nbsp;"+b.getName()+" "+b.getSureName()+" </td>\n");
			//	h.append(" <td align='right' nowrap>&nbsp;Route:&nbsp;"+b.getMcRouteDesc()+"\n");
			//	h.append(" พื้นที่รับผิดชอบ:&nbsp;"+b.getMcAreaDesc()+"</td> \n");
				h.append("</tr> \n");
			h.append("</table> ");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			    //h.append("<td align='left' colspan='2'>หมายเหตุ:&nbsp;"+b.getRemark()+"</b></td> \n");
		    h.append("</tr> \n");	
		
			h.append("</table> \n");

			
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("<td align='center'><b>วันที่</b></td> \n");
				h.append("<td align='center'><b>รายละเอียด</b></td> \n");
				h.append("</tr> \n");
			
				h.append("</table> \n");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		MCEmpForm aForm = (MCEmpForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new MCEmpBean());

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
		MCEmpForm aForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		MCEmpBean bean = new MCEmpBean();
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String empRefId = Utils.isNull(request.getParameter("empRefId"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit empRefId:"+empRefId+",action:"+action);
			if("add".equalsIgnoreCase(action)){
				
			}else{
				MCEmpBean c = new MCEmpBean();
				c.setEmpRefId(empRefId);
				bean = MCEmpDAO.searchHead(c,false).getItems().get(0);
			}
			
			aForm.setBean(bean);
			aForm.setMode(action);//Mode Edit ,Add
			
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
		MCEmpForm summaryForm = (MCEmpForm) form;
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
		MCEmpForm orderForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		
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
		MCEmpForm aForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
		
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MCEmpBean h = aForm.getBean();
			logger.debug("mode:"+aForm.getMode());
			//head 
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Validate StaffId duplicate
			if("add".equalsIgnoreCase((aForm.getMode()))){
				logger.debug("insert:");
				if(!Utils.isNull(h.getEmpId()).equals("")){
					if( MCDAO.isDuplicateEmployeeId(conn, h.getEmpId(),h.getEmpRefId())){
						request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้  ข้อมูล รหัสพนักงาน ซ้ำ");
						h.setEmpId("");
					    aForm.setBean(h);
						return  "detail";
					}
				}
			    h = MCEmpDAO.insertMCEmpModel(conn, h);
			}else{
				logger.debug("update:");
				if(!Utils.isNull(h.getEmpId()).equals("") ){
					if( MCDAO.isDuplicateEmployeeId(conn, h.getEmpId(),h.getEmpRefId())){
						request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้  ข้อมูล รหัสพนักงาน ซ้ำ");
						h.setEmpId(h.getOrgEmpId());
					    aForm.setBean(h);
						return  "detail";
					}
				}
				  MCEmpDAO.updateMCEmpModel(conn, h);
				  
			}
		
			logger.debug("region:"+h.getRegion());
			//Search Again
			h.setEmpRouteName("");
			MCEmpBean bean = MCEmpDAO.searchHead(conn,h,true).getItems().get(0);
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
	
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		MCEmpForm aForm = (MCEmpForm) form;
		try {
			aForm.setResults(new ArrayList<MCEmpBean>());
			
			MCEmpBean ad = new MCEmpBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
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
		MCEmpForm reportForm = (MCEmpForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			MCEmpBean h = null;// MCBeanDAO.searchReport(reportForm.getBean());
			if(h != null){
				//Head
				//parameterMap.put("p_boxno", h.getBoxNo());
				//parameterMap.put("p_jobname", h.getJobId()+"-"+h.getName());
				//parameterMap.put("p_remark", Utils.isNull(h.getRemark()));
	
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
