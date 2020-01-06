package com.isecinc.pens.web.sa;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.SAEmpBean;
import com.isecinc.pens.bean.SAReportBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SAEmpDAO;
import com.isecinc.pens.dao.SAReportDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SAReportAction extends I_Action {

	public static int pageSize = 90;
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		SAReportForm aForm = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		SAReportBean bean = new SAReportBean();
		logger.debug("prepare");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			String page = Utils.isNull(request.getParameter("page"));
			logger.debug("page:"+page);
			
			if(page.equals("saStatementReport")){
				if("new".equals(action) ){
					//Clear list session
					request.getSession().setAttribute("SA_STATEMENT_REPORT_LIST", null);
					//init session
					List<PopupForm> billTypeList2 = new ArrayList<PopupForm>();
					PopupForm ref2 = new PopupForm("",""); 
					billTypeList2.add(ref2);
					billTypeList2.addAll(GeneralDAO.getMasterListByRefCode(new PopupForm(),"","Region"));
					request.getSession().setAttribute("empRegionList",billTypeList2);
			
					List<PopupForm> billTypeList3 = new ArrayList<PopupForm>();
					PopupForm ref3 = new PopupForm("",""); 
					billTypeList3.add(ref3);
					billTypeList3.addAll(GeneralDAO.getMasterListByRefCode(new PopupForm(),"","Group_store"));
					request.getSession().setAttribute("groupStoreList",billTypeList3);
				
					SAReportBean ad = new SAReportBean();
					//init MM
					List<PopupForm> monthList = new ArrayList<PopupForm>();
					Calendar c = Calendar.getInstance();
			    	int count =0;
					for(int i=11;i>=0;i--){
						count++;
						c.set(Calendar.DAY_OF_MONTH, 1);
					    c.set(Calendar.MONTH, i);
					    PopupForm p = new PopupForm();
					    p.setCode(DateUtil.stringValue(c.getTime(), DateUtil.DD_MM_YYYY_WITHOUT_SLASH));
					    p.setDesc(DateUtil.stringValue(c.getTime(), DateUtil.MMM_YY,Utils.local_th));
					    monthList.add(p);
					}
					request.getSession().setAttribute("monthList", monthList);
					
					//Set current Month
					c = Calendar.getInstance();
					c.set(Calendar.DAY_OF_MONTH, 1);
				    ad.setMonth(DateUtil.stringValue(c.getTime(), DateUtil.DD_MM_YYYY_WITHOUT_SLASH));
				    
				    logger.debug("currentMonth:"+ad.getMonth());
					
					aForm.setBean(ad);
				}	
				
			}else if(page.equals("saOrisoftReport")){
				if("new".equals(action) ){
					//Clear list session
					request.getSession().setAttribute("SA_ORISOFT_REPORT_LIST", null);
					
					SAReportBean ad = new SAReportBean();
					//init MM
					List<PopupForm> monthList = new ArrayList<PopupForm>();
					Calendar c = Calendar.getInstance();
			    	int count =0;
					for(int i=11;i>=0;i--){
						count++;
						c.set(Calendar.DAY_OF_MONTH, 1);
					    c.set(Calendar.MONTH, i);
					    PopupForm p = new PopupForm();
					    p.setCode(DateUtil.stringValue(c.getTime(), "MMyyyy"));
					    p.setDesc(DateUtil.stringValue(c.getTime(), DateUtil.MMM_YY,Utils.local_th));
					    monthList.add(p);
					}
					request.getSession().setAttribute("monthList", monthList);
					
					//Set current Month
					c = Calendar.getInstance();
				    ad.setMonth(DateUtil.stringValue(c.getTime(),"MMyyyy"));
				    
				    logger.debug("currentMonth:"+ad.getMonth());
					
					aForm.setBean(ad);
				}	
			}else if(page.equals("saDeptReport")){
				if("new".equals(action) ){
					//Clear list session
					request.getSession().setAttribute("SA_DEPT_REPORT_LIST", null);
					
					List<PopupForm> billTypeList1 = new ArrayList();
					PopupForm ref1 = new PopupForm("",""); 
					billTypeList1.add(ref1);
					billTypeList1.addAll(GeneralDAO.getMasterListByRefCode(new PopupForm(),"","EMPtype"));
					request.getSession().setAttribute("empTypeList",billTypeList1);
					
					List<PopupForm> billTypeList3 = new ArrayList<PopupForm>();
					PopupForm ref3 = new PopupForm("",""); 
					billTypeList3.add(ref3);
					billTypeList3.addAll(GeneralDAO.getMasterListByRefCode(new PopupForm(),"","Group_store"));
					request.getSession().setAttribute("groupStoreList",billTypeList3);
					
					aForm.setBean(new SAReportBean());
				}	
			}else if(page.equals("saDamageReport")){
				if("new".equals(action) ){
					//Clear list session
					request.getSession().setAttribute("SA_DAMAGE_REPORT_LIST", null);
					
					List<PopupForm> billTypeList3 = new ArrayList<PopupForm>();
					PopupForm ref3 = new PopupForm("",""); 
					billTypeList3.add(ref3);
					billTypeList3.addAll(GeneralDAO.getMasterListByRefCode(new PopupForm(),"","Group_store"));
					request.getSession().setAttribute("groupStoreList",billTypeList3);
					
					List<PopupForm> billTypeList1 = new ArrayList();
					billTypeList1.add(new PopupForm("",""));
					billTypeList1.add(new PopupForm("BME","BME"));
					billTypeList1.add(new PopupForm("WACOAL","WACOAL"));
					request.getSession().setAttribute("typeList",billTypeList1);
					
					aForm.setBean(new SAReportBean());
				}	
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
		SAReportForm summaryForm = (SAReportForm) form;
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
		SAReportForm aForm = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			String page = Utils.isNull(request.getParameter("page"));
			logger.debug("page:"+page);
			
			if(page.equals("saStatementReport")){
				SAReportBean result = SAReportDAO.searchEmployeeList(aForm.getBean());
				if(result != null){
					List<SAReportBean> items = result.getItems();
					request.getSession().setAttribute("SA_STATEMENT_REPORT_LIST", items);
					if(items != null && items.size()>0){
						
					}else{
						request.setAttribute("Message", "ไม่พบข้อมูล");
					}
				}
			}else if(page.equals("saOrisoftReport")){
				SAReportBean result = SAReportDAO.searchData4OrisoftReport(aForm.getBean());
				if(result != null){
					List<SAReportBean> items = result.getItems();
					request.getSession().setAttribute("SA_ORISOFT_REPORT_LIST", items);
					if(items != null && items.size()>0){
						
					}else{
						request.setAttribute("Message", "ไม่พบข้อมูล");
					}
				}
			}else if(page.equals("saDeptReport")){
				SAReportBean result = SAReportDAO.searchEmployeeDeptList(aForm.getBean());
				if(result != null){
					List<SAReportBean> items = result.getItems();
					request.getSession().setAttribute("SA_DEPT_REPORT_LIST", items);
					if(items != null && items.size()>0){
						
					}else{
						request.setAttribute("Message", "ไม่พบข้อมูล");
					}
				}
			}else if(page.equals("saDamageReport")){
				SAReportBean result = SAReportDAO.searchDamagetList(aForm.getBean());
				if(result != null){
					List<SAReportBean> items = result.getItems();
					request.getSession().setAttribute("SA_DAMAGE_REPORT_LIST", items);
					if(items != null && items.size()>0){
						
					}else{
						request.setAttribute("Message", "ไม่พบข้อมูล");
					}
				}
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

	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		SAReportForm aForm = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		boolean found = false;
		try {
			logger.debug("PageAction:"+request.getParameter("page"));
			/** Onhand **/
			if("saStatementReport".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				SAReportBean cri = new SAReportBean();
				cri.setAsOfDate(aForm.getBean().getAsOfDate());
				cri.setEmpId(Utils.isNull(request.getParameter("empId")));
				
				SAEmpBean empBean = SAEmpDAO.getEmp(cri.getEmpId());
				if(empBean != null){
					cri.setName(empBean.getName());
					cri.setSurname(empBean.getSurName());
					cri.setGroupStore(empBean.getGroupStore());
					cri.setBranch(empBean.getBranch());
					cri.setFullName(cri.getName()+" "+cri.getSurname());
				}
				
				htmlTable = SAExportExcel.genSAStatementReport(cri,user);
				if( !"".equals(htmlTable.toString())){
					found = true;
				}
			}
			
			if(found){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportToExcelAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		SAReportForm aForm = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		boolean found = false;
		try {
			
			logger.debug("PageAction:"+request.getParameter("page"));
			SAReportBean cri = new SAReportBean();
			cri.setMonth(aForm.getBean().getMonth());
			cri.setAsOfDate(aForm.getBean().getAsOfDate());
			
			/** Onhand **/
			if("saStatementReport".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				htmlTable = SAExportExcel.genSAStatementAllReport(cri,user);
				if( !"".equals(htmlTable.toString())){
					found = true;
				}
			}else if("saOrisoftReport".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				
				htmlTable = SAExportExcel.genSAOrisoftReport(aForm.getBean(),user);
				if( !"".equals(htmlTable.toString())){
					found = true;
				}
			}else if(Utils.isNull(request.getParameter("page")).equals("saDeptReport")){
				 if(request.getSession().getAttribute("SA_DEPT_REPORT_LIST") != null){
					 found = true;
					 List<SAReportBean> dataList =(List)request.getSession().getAttribute("SA_DEPT_REPORT_LIST");
					 htmlTable =SAExportExcel.genSADeptReport(aForm.getBean(),dataList,user);
				 }
			}
			if(found){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SAReportForm aForm = (SAReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
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
		return "search";
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		SAReportForm aForm = (SAReportForm) form;
		SAReportBean bean = new SAReportBean();
		try {
			if("saStatementReport".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				request.getSession().setAttribute("SA_STATEMENT_REPORT_LIST",null);

				//Set current Month
				Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_MONTH, 1);
			    bean.setMonth(DateUtil.stringValue(c.getTime(), DateUtil.DD_MM_YYYY_WITHOUT_SLASH));
			}else 	if("saOrisoftReport".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				request.getSession().setAttribute("SA_ORISOFT_REPORT_LIST",null);

				//Set current Month
				Calendar c = Calendar.getInstance();
			    bean.setMonth(DateUtil.stringValue(c.getTime(), "MMyyyy"));
			}
			
			aForm.setBean(bean);
			aForm.setMode("add");
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
