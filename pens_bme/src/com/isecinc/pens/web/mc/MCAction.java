package com.isecinc.pens.web.mc;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
				aForm.setBean(MCDAO.searchHead(aForm.getBeanCriteria(),false));
				aForm.setResultsSearch(aForm.getBean().getItems());
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
			aForm.setBean(MCDAO.searchHead(aForm.getBean(),false));
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
	
	public ActionForward prepareMCStaffDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareMCStaffDetail");
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			 if("add".equals(action)){
			   MCBean bean = new MCBean();
			   bean.setActive("Y");//default
			   bean.setMode("add");
			   bean.setCanEdit(true);
			   aForm.setBean(bean);
					
			 }else if("edit".equals(action)){
			   conn = DBConnection.getInstance().getConnection();
			   String emRefId = Utils.isNull(request.getParameter("emRefId"));
				
			   MCBean bean = new MCBean();
			   bean.setEmpRefId(emRefId);
			   
			   MCBean results = MCDAO.searchStaff(bean);//find by emp_ref_id
			   bean = results.getItems().get(0);
			   bean.setMode("edit");
			   
			  //check can Edit
			   bean.setCanEdit(MCDAO.canEditStaff(conn, bean.getEmpRefId()));
			   aForm.setBean(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepareMCStaffDetail");
	}
	
	public ActionForward saveMCStaffDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.debug("saveMCStaffDetail");
		Connection conn = null;
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MCBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			//logger.debug("is_active:"+h.getActive());
			//Get Route Id from RouteName
			h.setMcRoute(MCDAO.getRouteIdByRouteName(conn, h.getMcRouteDesc(),h.getMcArea()));
			
            if("add".equals(h.getMode())){
            	int update = 0;
            	//case insert only
            	if( !Utils.isNull(h.getOrgEmpRefId()).equals("")){
            	   update =  MCDAO.updateMCStaffModelByOrgEmpRefId(conn, h);
                }else{
                   update =  MCDAO.updateMCStaffModelByEmpRefId(conn, h);
                }
            	logger.debug("update result:"+update);
            	
            	if(update ==0)
            	  MCDAO.insertMCStaffModel(conn, h);
            	
            }else  if("edit".equals(h.getMode())){
        	    MCDAO.updateMCStaffModelByOrgEmpRefId(conn, h);
            }
            
			//Search Again
            MCBean cri = new MCBean();
            cri.setEmpRefId(h.getEmpRefId());
            cri.setMcRouteDesc(h.getMcRouteDesc());
			MCBean bean = MCDAO.searchStaff(conn,cri).getItems().get(0);
			bean.setMode("edit");
			
			//check can Edit
			bean.setCanEdit(MCDAO.canEditStaff(conn, bean.getEmpRefId()));
		    aForm.setBean(bean);
		    
		    conn.commit();
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return mapping.findForward("prepareMCStaffDetail");
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepareMCStaffDetail");
	}
	
	public ActionForward prepareMCStaff(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareMCStaff");
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				MCBean ad = new MCBean();
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				aForm.setBean(MCDAO.searchStaff(aForm.getBeanCriteria()));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("prepareMCStaff");
	}
	
	public ActionForward searchMCStaff(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchMCStaff");
		MCForm aForm = (MCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			//save Criteria Search
			aForm.setBeanCriteria(aForm.getBean());
			
			aForm.setBean(MCDAO.searchStaff(aForm.getBean()));
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
		return mapping.findForward("prepareMCStaff");
	}
	
	public ActionForward clearMCStaff(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearMCStaff");
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
		return mapping.findForward("prepareMCStaff");
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
			c.setEmpId(staffId);
			
			MCBean mc = MCDAO.searchHead(c,true).getItems().get(0);
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
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'@'; \n");
			h.append(" } \n");
			h.append("</style> \n");
			
		    String monthYearFull = "";
		    String monthYear = "";
		    Map<String,String> dayMaps = b.getDaysMap();
		    int maxDayInMonth = b.getMaxDay();
		    
		    String dateString = "01"+b.getMonthTrip();//01012015
		    Date dateObj = Utils.parse(dateString, Utils.DD_MM_YYYY_WITHOUT_SLASH);
		    monthYearFull = Utils.stringValue(dateObj, Utils.MMMM_YYYY,Utils.local_th);
		    monthYear = Utils.stringValue(dateObj, Utils.MMM_YYYY,Utils.local_th);
		    
			//Header
			h.append("<table border='1' width='100%'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='center' colspan='2'><b>รายละเอียดประจำ Trip ของเดือน "+monthYearFull+"</b></td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='2'>");
			h.append("<table> ");
				h.append("<tr> \n");
				h.append(" <td align='left' nowrap>&nbsp;"+b.getEmpType()+":&nbsp;"+b.getEmpId()+":&nbsp;"+b.getName()+" "+b.getSurName()+" </td>\n");
				h.append(" <td align='right' nowrap>&nbsp;Route:&nbsp;"+b.getMcRouteDesc()+"\n");
				h.append(" พื้นที่รับผิดชอบ:&nbsp;"+b.getMcAreaDesc()+"</td> \n");
				h.append("</tr> \n");
			h.append("</table> ");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			    h.append("<td align='left' colspan='2'>หมายเหตุ:&nbsp;"+b.getRemark()+"</b></td> \n");
		    h.append("</tr> \n");	
		
			h.append("</table> \n");

			if(dayMaps != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("<td align='center'><b>วันที่</b></td> \n");
				h.append("<td align='center'><b>รายละเอียด</b></td> \n");
				h.append("</tr> \n");
				
				for(int i=1;i<=maxDayInMonth;i++){
					String dayStr = ((i+"").length()==1?"0"+i:""+i)+"-"+monthYear;
					String key = ((i+"").length()==1?"0"+i:""+i)+""+b.getMonthTrip();
					String detail = Utils.isNull(dayMaps.get(key));
					
					h.append("<tr> \n");
					h.append("<td class='text'>"+dayStr+"</td> \n");
					h.append("<td w>"+detail+"</td> \n");
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
			
            String empId = Utils.isNull(request.getParameter("empId"));
            String monthTrip = Utils.isNull(request.getParameter("monthTrip"));
            String maxDayInMonth = Utils.isNull(request.getParameter("maxDayInMonth")); 
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare edit empId:"+empId);
			MCBean c = new MCBean();
			c.setMonthTrip(monthTrip);
			c.setEmpId(empId);
			
			MCBean bean = MCDAO.searchHead(c,true).getItems().get(0);
			
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
			headTrans.setEmpRefId(h.getEmpRefId());
			headTrans.setMonthTrip(h.getMonthTrip());
			headTrans.setEmpId(h.getEmpId());
			headTrans.setEmpType(h.getEmpType());
			headTrans.setMcRoute(h.getMcRoute());
			headTrans.setMcArea(h.getMcArea());
			headTrans.setName(h.getName());
			headTrans.setRemark(h.getRemark());
			headTrans.setSurName(h.getSurName());
			headTrans.setCreateUser(user.getUserName());
			headTrans.setUpdateUser(user.getUserName());
			
			int update = MCDAO.updateMCTransModel(conn, headTrans);
			if(update==0){
			   MCDAO.insertMCTransModel(conn, headTrans);
			}
			
			//get value
			for(int d=1;d<=h.getMaxDay();d++) {
				String key = String.valueOf(d).length()==1?"0"+d:String.valueOf(d);
				String detail = request.getParameter(key+h.getMonthTrip());
				String dayOfWeek = request.getParameter("week_"+key+h.getMonthTrip());
				
				
				MCBean item = new MCBean();
				item.setEmpRefId(h.getEmpRefId());
				item.setMonthTrip(h.getMonthTrip());
				item.setEmpId(h.getEmpId());
				item.setDay(key);
				item.setDetail(detail);
				item.setDayOfWeek(dayOfWeek);
				item.setCreateUser(user.getUserName());
				item.setUpdateUser(user.getUserName());
				
				//save line DB
				update = MCDAO.updateMCTransDetailModel(conn, item);
				if(update==0){
				   MCDAO.insertMCTransDetail(conn, item);
				}
			}
			
			
			//Search Again
			MCBean bean = MCDAO.searchHead(conn,h,true).getItems().get(0);
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
	
	public ActionForward copyDataFromLastMonth(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("copyDataFromLastMonth");
		MCForm aForm = (MCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Step 1 Save Blank Data
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MCBean h = aForm.getBean();
	  
			//head 
			MCBean headTrans = new MCBean();
			headTrans.setEmpRefId(h.getEmpRefId());
			headTrans.setMonthTrip(h.getMonthTrip());
			headTrans.setEmpId(h.getEmpId());
			headTrans.setEmpType(h.getEmpType());
			headTrans.setMcRoute(h.getMcRoute());
			headTrans.setMcArea(h.getMcArea());
			headTrans.setName(h.getName());
			headTrans.setRemark(h.getRemark());
			headTrans.setSurName(h.getSurName());
			headTrans.setCreateUser(user.getUserName());
			headTrans.setUpdateUser(user.getUserName());
			
			int update = MCDAO.updateMCTransModel(conn, headTrans);
			if(update==0){
			   MCDAO.insertMCTransModel(conn, headTrans);
			}
			
			//get value
			for(int d=1;d<=h.getMaxDay();d++) {
				String key = String.valueOf(d).length()==1?"0"+d:String.valueOf(d);
				String detail = request.getParameter(key+h.getMonthTrip());
				String dayOfWeek = request.getParameter("week_"+key+h.getMonthTrip());
				
				
				MCBean item = new MCBean();
				item.setEmpRefId(h.getEmpRefId());
				item.setMonthTrip(h.getMonthTrip());
				item.setEmpId(h.getEmpId());
				item.setDay(key);
				item.setDetail(detail);
				item.setDayOfWeek(dayOfWeek);
				item.setCreateUser(user.getUserName());
				item.setUpdateUser(user.getUserName());
				
				//save line DB
				update = MCDAO.updateMCTransDetailModel(conn, item);
				if(update==0){
				   MCDAO.insertMCTransDetail(conn, item);
				}
			}
			
			//Step2 Update Trans Detail by day_of_week
			MCBean cri = aForm.getBean();
			String currentMonth = cri.getMonthTrip();//012015
			String prevMonth = ""+(Integer.parseInt(currentMonth.substring(0,2))-1);
			String prevMonthTrip = "";
			/** Case Month = 01 PrevMonth = 12(year-1)*/
			if(Integer.parseInt(currentMonth.substring(0,2))==1){
				prevMonthTrip = "12"+ (Integer.parseInt(currentMonth.substring(2,6))-1); 
			}else{
				prevMonthTrip = (prevMonth.length()==1?"0"+prevMonth:prevMonth)+currentMonth.substring(2,6); 
			}
			
			
			logger.debug("prevMonthTrip["+prevMonthTrip+"]");
			
			//Update Detail
			MCDAO.copyFromLastMonthModel(conn, cri.getEmpRefId(),cri.getEmpId(), currentMonth, prevMonthTrip);
			
			//Search Again
			MCBean bean = MCDAO.searchHead(conn,h,true).getItems().get(0);
		    bean.setStartDayOfMonth(h.getStartDayOfMonth());
		    bean.setMaxDay(h.getMaxDay());
		    
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "Copy ข้อมูลเรียบร้อยแล้ว");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
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
