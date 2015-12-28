package com.isecinc.pens.web.mc;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.ExcelHeader;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.bean.MCEmpBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.MCEmpDAO;
import com.isecinc.pens.dao.MCTimeDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.export.Excel;
import com.isecinc.pens.web.export.ExcelResultBean;
import com.isecinc.pens.web.export.ExcelStyle;
import com.isecinc.pens.web.export.ExportReturnWacoal;
import com.isecinc.pens.web.export.ExportTimeSheetGroup;


/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MCTimeAction extends I_Action {


	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		MCTimeForm aForm = (MCTimeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				MCBean ad = new MCBean();
			
				aForm.setBean(ad);
			}else if("back".equals(action)){
				
				aForm.setBean(MCTimeDAO.searchStaffTime(aForm.getBeanCriteria(),false));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MCTimeForm aForm = (MCTimeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		logger.debug("search");
		String msg = "";
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			 MCBean cri = aForm.getBean();
			 String staffMonth = Utils.isNull(cri.getStaffMonth());
             staffMonth = staffMonth.length()==1?"0"+staffMonth:staffMonth;
             cri.setStaffMonth(staffMonth);
             
			 aForm.setBean(MCTimeDAO.searchStaffTime(cri,false));
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
		return "search";
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		MCTimeForm aForm = (MCTimeForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		List<MCBean> resultList = new ArrayList<MCBean>();
		try {
			MCBean bean = new MCBean();
			
			conn = DBConnection.getInstance().getConnection();

            String empRefId = Utils.isNull(request.getParameter("empRefId"));
            String staffMonth = Utils.isNull(request.getParameter("staffMonth"));
                   staffMonth = staffMonth.length()==1?"0"+staffMonth:staffMonth;
            String staffYear = Utils.isNull(request.getParameter("staffYear"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit empRefId:"+empRefId+",action:"+action);
			 //Get employee Detail
			MCEmpBean empBean = new MCEmpBean();
			empBean.setEmpRefId(empRefId);
			empBean = MCEmpDAO.searchHeadModel(conn,empBean,false).getItems().get(0);
			
			if("add".equalsIgnoreCase(action)){
				
				//Calculate Max Day in Month
				String yearChrist = ""+(Integer.parseInt(staffYear)-543);
				String dateString = "01"+staffMonth+yearChrist;
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
				bean.setMaxDay(maxDayInMonth);
				
				MCBean item = new MCBean();
				for(int d=1;d<=maxDayInMonth;d++){
					item = new MCBean();
					String staffDate = "";
					if(d < 10 ){
						staffDate = "0"+d+"/"+staffMonth+"/"+staffYear;
					}else{
						staffDate =  d+"/"+staffMonth+"/"+staffYear;
					}
					item.setStaffDate(staffDate);
					item.setEmpId(bean.getEmpId());
					item.setEmpRefId(bean.getEmpRefId());
					item.setStartTime("");
					item.setEndTime("");
					item.setTotalTime("");
					item.setReasonLeave("");
					item.setNote("");
					
					if(Utils.isHoliday(item.getStaffDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)){
						item.setHoliday(true);
					}
					//logger.debug("staffDate:"+item.getStaffDate()+",isHoliday:"+Utils.isHoliday(item.getStaffDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					
					resultList.add(item);
				}
				
			}else{
				//Edit 
				MCBean c = new MCBean();
				c.setEmpRefId(empRefId);
				c.setStaffMonth(staffMonth);
				c.setStaffYear(staffYear);
				
				resultList = MCTimeDAO.searchStaffTimeDetail(c);
			}
			//set value bean
			bean.setEmpRefId(empBean.getEmpRefId());
			bean.setEmpId(empBean.getEmpId());
			bean.setName(empBean.getName());
			bean.setSurName(empBean.getSurName());
			bean.setFullName(empBean.getName()+" "+empBean.getSurName());
			bean.setRegionDesc(empBean.getRegionDesc());
			bean.setEmpTypeDesc(empBean.getEmpTypeDesc());
			bean.setStaffMonth(staffMonth);
			bean.setStaffYear(staffYear);
			
			aForm.setBean(bean);
			aForm.setResults(resultList);
			aForm.setMode(action);//Mode Edit ,Add
			
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
		MCTimeForm summaryForm = (MCTimeForm) form;
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
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MCTimeForm aForm = (MCTimeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int i = 0;
		List<MCBean> itemList = new ArrayList<MCBean>();
		try {
	       MCBean h = aForm.getBean();
	       h.setCreateUser(user.getUserName());
	       h.setUpdateUser(user.getUserName());
	       
			//Get Value
			String[] staffDate = request.getParameterValues("staffDate");
			String[] startTime = request.getParameterValues("startTime");
			String[] endTime = request.getParameterValues("endTime");
			String[] totalTime = request.getParameterValues("totalTime");
			String[] reasonLeave = request.getParameterValues("reasonLeave");
			String[] note = request.getParameterValues("note");
			
			for(i=0;i<staffDate.length;i++){
				MCBean itemBean = new MCBean();
				itemBean.setStaffDate(Utils.isNull(staffDate[i]));
				itemBean.setStartTime(Utils.isNull(startTime[i]));
				itemBean.setEndTime(Utils.isNull(endTime[i]));
				itemBean.setTotalTime(Utils.isNull(totalTime[i]));
				itemBean.setReasonLeave(Utils.isNull(reasonLeave[i]));
				itemBean.setNote(Utils.isNull(note[i]));
				
				logger.debug("reasonLeave[i]["+reasonLeave[i]+"]");
				itemList.add(itemBean);
			}
			
			MCTimeDAO.saveMCStaffTime(h,itemList);
			
			request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว \n");
			
			//search again
			List<MCBean> resultList = MCTimeDAO.searchStaffTimeDetail(aForm.getBean());
			aForm.setResults(resultList);
			
		} catch (Exception e) {
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	public ActionForward exportExcelGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcelGroup");
		MCTimeForm aForm = (MCTimeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			ExcelResultBean excelResultBean =  ExportTimeSheetGroup.genExportToExcel(user,aForm.getBean());
			if(excelResultBean.isFound()){
			    XSSFWorkbook xssfWorkbookDta =excelResultBean.getXssfWorkbook();
		
				response.setHeader("Content-Disposition", "attachment; filename=TimeSheet.xlsx");
				response.setContentType("application/vnd.ms-excel; charset=windows-874");
				java.io.OutputStream out = response.getOutputStream();
	
				xssfWorkbookDta.write(out);
	
			    out.flush();
			    out.close();
			 }else{
				 request.setAttribute("Message","ไม่พบข้อมูล");
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
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		MCTimeForm aForm = (MCTimeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			ExcelResultBean excelResultBean =  ExportTimeSheetGroup.genExportToExcel(user,aForm.getBean());
			if(excelResultBean.isFound()){
			    XSSFWorkbook xssfWorkbookDta =excelResultBean.getXssfWorkbook();
				response.setHeader("Content-Disposition", "attachment; filename=TimeSheet.xlsx");
				response.setContentType("application/vnd.ms-excel; charset=windows-874");
				java.io.OutputStream out = response.getOutputStream();
	
				xssfWorkbookDta.write(out);
	
			    out.flush();
			    out.close();
			 }else{
				 request.setAttribute("Message","ไม่พบข้อมูล");
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
	
	
	public ActionForward exportExcelBK(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		MCTimeForm aForm = (MCTimeForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="TimeSheet.xls";
		try {
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
		    String empRefId = Utils.isNull(request.getParameter("empRefId"));
	        String staffMonth = Utils.isNull(request.getParameter("staffMonth"));
	               staffMonth = staffMonth.length()==1?"0"+staffMonth:staffMonth;
	        String staffYear = Utils.isNull(request.getParameter("staffYear"));
	        
	        String totalTimeAll = Utils.isNull(request.getParameter("totalTimeAll"));
            
            logger.debug("empRefId:"+empRefId);
            logger.debug("staffMonth:"+staffMonth);
            logger.debug("staffYear:"+staffYear);
            
            MCBean c = new MCBean();
			c.setStaffYear(staffYear);
			c.setStaffMonth(staffMonth);
			c.setEmpRefId(empRefId);
			
			 //Get employee Detail
			MCEmpBean empBean = new MCEmpBean();
			empBean.setEmpRefId(empRefId);
			empBean = MCEmpDAO.searchHead(empBean,false).getItems().get(0);
			
			MCBean head = new MCBean();
			head.setEmpId(empBean.getEmpId());
			head.setName(empBean.getName());
			head.setSurName(empBean.getSurName());
			head.setStaffMonth(staffMonth);
			head.setStaffYear(staffYear);
			
			//get Data List
			List<MCBean> dataList = MCTimeDAO.searchStaffTimeDetail(c);
		
			htmlTable = genExportReport(head,dataList,totalTimeAll);
			
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
	
	private StringBuffer genExportReport(MCBean b,List<MCBean> dataList,String totalTimeAll){
		StringBuffer h = new StringBuffer("");
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
		    
			//Header
			h.append("<table border='1' width='100%'> \n");
			h.append("<tr> \n");
			 h.append("<td align='left' colspan='6'><b>รายงานบันทึกการลงเวลาเข้า-ออก งาน</b></td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			 h.append("<td align='left' colspan='6'><b>รหัสพนักงาน :"+b.getEmpId() +"   ชื่อ-สกุล :"+b.getName()+" "+b.getSurName()+"</b></td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			 h.append("<td align='left' colspan='6'><b>ประจำปี/เดือน :"+b.getStaffYear()+"/"+b.getStaffMonth()+"</b></td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
				
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td align='center'><b>วันที่</b></td> \n");
			h.append("<td align='center'><b>เวลาเข้า</b></td> \n");
			h.append("<td align='center'><b>เวลาออก</b></td> \n");
			h.append("<td align='center'><b>รวมเวลา (ชั่วโมง:นาที)</b></td> \n");
			h.append("<td align='center'><b>สาเหตุการลา</b></td> \n");
			h.append("<td align='center'><b>หมายเหตุ</b></td> \n");
			h.append("</tr> \n");
			if(dataList!= null && dataList.size() >0){
				for(int i=1;i<dataList.size();i++){
					MCBean item = dataList.get(i);
					
					h.append("<tr> \n");
					h.append("<td class='text'>"+item.getStaffDate()+"</td> \n");
					h.append("<td class='text'>"+item.getStartTime()+"</td> \n");
					h.append("<td class='text'>"+item.getEndTime()+"</td> \n");
					h.append("<td class='text'>"+item.getTotalTime()+"</td> \n");
					h.append("<td class='text'>"+item.getReasonLeave()+"</td> \n");
					h.append("<td class='text'>"+item.getNote()+"</td> \n");
					h.append("</tr>");
				}//for
			}//if
			
			h.append("<tr> \n");
			h.append("<td class='text'>&nbsp;</td> \n");
			h.append("<td class='text'>&nbsp;</td> \n");
			h.append("<td class='text'><b>รวมเวลา</b></td> \n");
			h.append("<td class='text'>"+totalTimeAll+"</td> \n");
			h.append("<td class='text'>&nbsp;</td> \n");
			h.append("<td class='text'>&nbsp;</td> \n");
			h.append("</tr>");
			
			h.append("</table> \n");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		MCTimeForm aForm = (MCTimeForm) form;
		try {
			aForm.setBean(new MCBean());
			aForm.setResultsSearch(null);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
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
