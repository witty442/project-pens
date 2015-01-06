package com.isecinc.pens.web.mc;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
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
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.MCDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MCAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				MCBean ad = new MCBean();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				//aForm.setBean(MCBeanDAO.searchHead(aForm.getBeanCriteria()));
				//aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			MCBean b = aForm.getBean();
			aForm.setBean(MCDAO.searchHead(aForm.getBean()));
			aForm.setResultsSearch(aForm.getBean().getItems());
			
			//Calculate Max Day in Month
			String dateString = "01"+b.getMonthTrip();
			logger.debug("DateString:"+dateString);
			
			Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(Utils.parse(dateString, Utils.DD_MM_YYYY_WITHOUT_SLASH));  
	        calendar.add(Calendar.MONTH, 1);  
	        calendar.set(Calendar.DAY_OF_MONTH, 1);  
	        calendar.add(Calendar.DATE, -1);  
	        Date lastDayOfMonth = calendar.getTime(); 
	        
			Calendar cLastDay = Calendar.getInstance();
			cLastDay.setTime(lastDayOfMonth);
			int maxDayInMonth = cLastDay.get(Calendar.DAY_OF_MONTH);
			
			logger.debug("maxDayInMonth:"+maxDayInMonth);
			
			aForm.getBean().setMaxDay(maxDayInMonth);
			
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
		return mapping.findForward("search2");
	}
	
	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareReport");
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				MCBean ad = new MCBean();
				
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("report");
	}
	
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchReport");
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			MCBean b = aForm.getBean();
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
		return mapping.findForward("report");
	}
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		MCForm aForm = (MCForm) form;
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
            logger.debug("monthTrip:"+monthTrip);
            logger.debug("maxDayInMonth:"+maxDayInMonth);
            
            MCBean c = new MCBean();
			c.setMonthTrip(monthTrip);
			c.setStaffId(staffId);
			
			MCBean mc = MCDAO.searchHead(c).getItems().get(0);
			mc.setMaxDay(Integer.parseInt(maxDayInMonth));
			htmlTable = genExportReport(mc);
			
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
	
	private StringBuffer genExportReport(MCBean b){
		StringBuffer h = new StringBuffer("");
		try{
			
		    String monthYearFull = "";
		    String monthYear = "";
		    Map<String,String> dayMaps = b.getDaysMap();
		    int maxDayInMonth = b.getMaxDay();
		    
		    String dateString = "01"+b.getMonthTrip();//01012015
		    Date dateObj = Utils.parse(dateString, Utils.DD_MM_YYYY_WITHOUT_SLASH);
		    monthYearFull = Utils.stringValue(dateObj, Utils.MMMM_YYYY,Utils.local_th);
		    monthYear = Utils.stringValue(dateObj, Utils.MMM_YYYY,Utils.local_th);
		    
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td>&nbsp;</td> \n");
			h.append("<td align='left' colspan='4'>รายละเอียดประจำ Trip ของเดือน "+monthYearFull+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td>&nbsp;</td> \n");
			h.append("<td align='left'>MC:"+b.getName()+" "+b.getSureName()+"</td> \n");
			h.append("<td align='left' colspan='2'>พื้นที่รับผิดชอบ:"+b.getMcAreaDesc()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(dayMaps != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>&nbsp;</td> \n");
				  h.append("<td>วันที่</td> \n");
				  h.append("<td colspan='2'>รายละเอียด</td> \n");
				h.append("</tr> \n");
				
				for(int i=1;i<=maxDayInMonth;i++){
					String dayStr = ((i+"").length()==1?"0"+i:""+i)+"-"+monthYear;
					String key = ((i+"").length()==1?"0"+i:""+i)+""+b.getMonthTrip();
					String detail = Utils.isNull(dayMaps.get(key));
					
					h.append("<tr> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>"+dayStr+"&nbsp;</td> \n");
					  h.append("<td colspan='2'>"+detail+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		MCForm aForm = (MCForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new MCBean());
			
			MCBean ad = new MCBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
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
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String staffId = Utils.isNull(request.getParameter("staffId"));
            String monthTrip = Utils.isNull(request.getParameter("monthTrip"));
            String maxDayInMonth = Utils.isNull(request.getParameter("maxDayInMonth"));
            
            String mode = Utils.isNull(request.getParameter("mode"));
            
			if( !"".equals(staffId) && !"".equals(staffId)){
				logger.debug("prepare edit staffId:"+staffId);
				MCBean c = new MCBean();
				c.setMonthTrip(monthTrip);
				c.setStaffId(staffId);
				
				MCBean bean = MCDAO.searchHead(c).getItems().get(0);
				//aForm.setResults(bean.getItems());
				
				//step  01 what day of week
				Calendar cDay = Calendar.getInstance(Utils.local_th);
				cDay.setTime(Utils.parse("01"+monthTrip, Utils.DD_MM_YYYY_WITHOUT_SLASH));
				int startDayOfMonth = cDay.get(Calendar.DAY_OF_WEEK);
				bean.setStartDayOfMonth(startDayOfMonth);
				
				logger.debug("startDayOfMonth:"+startDayOfMonth);
				logger.debug("maxDayInMonth:"+maxDayInMonth);
				
				bean.setMaxDay(Utils.convertStrToInt(maxDayInMonth));
				
				
				aForm.setBean(bean);
				aForm.setMode(mode);//Mode Edit
			}
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
		MCForm summaryForm = (MCForm) form;
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
		MCForm orderForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			MCBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
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
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MCBean h = aForm.getBean();
	  
			//head 
			MCBean headTrans = new MCBean();
			headTrans.setMonthTrip(h.getMonthTrip());
			headTrans.setStaffId(h.getStaffId());
			headTrans.setMcArea(h.getMcArea());
			headTrans.setName(h.getName());
			headTrans.setSureName(h.getSureName());
			headTrans.setCreateUser(user.getUserName());
			headTrans.setUpdateUser(user.getUserName());
			
			int update = MCDAO.updateHeadModel(conn, headTrans);
			if(update==0){
			   MCDAO.saveHeadModel(conn, headTrans);
			}
			
			//get value
			for(int d=1;d<=h.getMaxDay();d++) {
				String key = String.valueOf(d).length()==1?"0"+d:String.valueOf(d);
				String detail = request.getParameter(key+h.getMonthTrip());
				
				MCBean item = new MCBean();
				item.setMonthTrip(h.getMonthTrip());
				item.setStaffId(h.getStaffId());
				item.setDay(key);
				item.setDetail(detail);
				item.setCreateUser(user.getUserName());
				item.setUpdateUser(user.getUserName());
				
				//save line DB
				update = MCDAO.updateItemModel(conn, item);
				if(update==0){
				   MCDAO.saveItemModel(conn, item);
				}
			}

			//Search Again
			MCBean bean = MCDAO.searchHead(conn,h).getItems().get(0);
		    bean.setStartDayOfMonth(h.getStartDayOfMonth());
		    bean.setMaxDay(h.getMaxDay());
		    
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
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
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		MCForm aForm = (MCForm) form;
		try {
			aForm.setResults(new ArrayList<MCBean>());
			
			MCBean ad = new MCBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setBean(ad);
			
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
		MCForm reportForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			MCBean h = null;// MCBeanDAO.searchReport(reportForm.getBean());
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
