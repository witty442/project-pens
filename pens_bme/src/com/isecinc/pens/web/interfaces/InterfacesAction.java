package com.isecinc.pens.web.interfaces;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.MonitorItemDetailBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MUser;
import com.pens.util.Constants;
import com.pens.util.EnvProperties;
import com.pens.util.Utils;

/**
 * ConversionAction Class
 * 
 * @author Witty.B
 * @version $Id: ConversionAction.java,v 1.0 19/10/2010 00:00:00
 * 
 */

public class InterfacesAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Interfaces Prepare Form");
		String returnText = "prepare";
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		return returnText;
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	   return new InterfacesPrepareManager().prepare(form, request, response)	;
	}
 
	public ActionForward runBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		return new InterfacesManager().runBatch(mapping,form,request,response);
	}
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new InterfacesSearchManager().search(form, request, response);
	}
		
	/**
	 * Search
	 */
	public ActionForward searchDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Conversion Search Detail");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		InterfaceDAO dao = new InterfaceDAO();
		try {
			InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
			if(request.getAttribute("searchKey") != null){
				criteria.setSearchKey((String)request.getAttribute("searchKey"));
			}
			MonitorBean monitorBean = new MonitorBean();
			monitorBean.setMonitorId(new BigDecimal(request.getParameter("monitor_id")));
			criteria.setMonitorBean(monitorBean);
			interfacesForm.setCriteria(criteria);
			/** Set Condition Search **/
			MonitorBean[] results = dao.findMonitorDetailList(user,interfacesForm.getMonitorBean(),"");
			
			interfacesForm.setResults(results);

			if (results != null && results.length > 0) {
				interfacesForm.getCriteria().setSearchResult(results.length);
				
				interfacesForm.setResults(results);
				criteria.setMonitorBean((MonitorBean)results[0]);
				interfacesForm.setCriteria(criteria);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			
			if(request.getParameter("rf") == null){
				request.setAttribute("rf", "Y");
			}
			if(request.getParameter("sort") != null){
				request.setAttribute("sort", request.getParameter("sort"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	
	/**
	 * Import To DB
	 */
	public ActionForward importData(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Import :importData");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User userLogin = (User) request.getSession().getAttribute("user");
		ImportManager importManager =  new ImportManager();
		User userRequest = new User();
		try {
			boolean canRunBatch = false;
			InterfaceDAO dao = new InterfaceDAO();
			String status = dao.findControlMonitor(Constants.TYPE_IMPORT_BMESCAN);
			logger.info("status["+status+"]");
			
			if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
			    canRunBatch = true;
			}
		
			if(canRunBatch){
				logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
				/** Import Data */
				logger.debug("importAll:"+interfacesForm.getMonitorBean().isImportAll());
				 String requestTable = "";
		         String requestTableTransType = "";
		         
			     if( !Utils.isNull(interfacesForm.getMonitorBean().getRequestTable()).equals("")){
					String[] exportArray = interfacesForm.getMonitorBean().getRequestTable().split("\\|");
					requestTable = exportArray[0];
					requestTableTransType = exportArray[1];
					
					/** Case Admin Update By Request Table Replace UserId*/
					String whereClause = "AND USER_NAME LIKE '%"+interfacesForm.getMonitorBean().getRequestImportUserName()+"%'";
					if(Utils.isNull(interfacesForm.getMonitorBean().getRequestImportUserName()).equals("")){
						whereClause = "AND USER_NAME LIKE '%ADMIN%'";
					}
					User[] results = new MUser().search(whereClause);
					if(results != null && results.length >0){
						userRequest = results[0];
					}
				 }
			     
			    logger.debug("requestTable:"+interfacesForm.getMonitorBean().getRequestTable());
				logger.debug("User Request:"+userRequest.getId()+",UserName Request:"+userRequest.getRole());
				     
				MonitorBean m = importManager.importTxt(Constants.TYPE_IMPORT_BMESCAN,userLogin,userRequest,request,interfacesForm.getMonitorBean().isImportAll());
			   
				/** Set for Progress Bar Opoup **/
				request.setAttribute("action", "submited");
				request.setAttribute("id", m.getTransactionId());
				
			}else{
				request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("success");
	}
	
	
	/**
	 * Search
	 */
	public ActionForward downloadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("downloadFile");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		InterfaceDAO dao = new InterfaceDAO();
		EnvProperties env = EnvProperties.getInstance();
		try {
			String realPathTemps = env.getProperty("path.backup.icc.hisher.export.orderexcel");//BeanParameter.getTempPath();
			logger.debug("realPathTemps"+realPathTemps);
			
			logger.debug("monitorItemId:"+request.getParameter("monitorItemId"));
			MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBeanByPK(user,request.getParameter("monitorItemId"));
			
			String pathFull = realPathTemps+"/"+monitorItemBeanResult.getFileName();
			//Load Excel File
			FileInputStream file = new FileInputStream(new File(pathFull));
            //Create Workbook instance holding reference to .xlsx file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

        	response.setHeader("Content-Disposition", "attachment; filename="+monitorItemBeanResult.getFileName());
			response.setContentType("application/vnd.ms-excel; charset=windows-874");
			java.io.OutputStream out = response.getOutputStream();

			workbook.write(out);

		    out.flush();
		    out.close();
		    file.close();
		} catch (Exception e) {
			e.printStackTrace();
			
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("showItemExport");
	}
	
	/**
	 * Search
	 */
	public ActionForward showItemExport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("ShowItemExport");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		InterfaceDAO dao = new InterfaceDAO();
		try {
			logger.debug("tableName:"+request.getParameter("tableName"));
			MonitorItemDetailBean[] results = dao.findMonitorItemDetailBeanList(new BigDecimal(request.getParameter("monitorItemId")));
			
			interfacesForm.setResultsItemDetail(results);
		    MonitorBean m = new MonitorBean();
		    MonitorItemBean i = new MonitorItemBean();
		    i.setTableName(request.getParameter("tableName"));
		    m.setMonitorItemBean(i);
		    interfacesForm.setMonitorBean(m);
		} catch (Exception e) {
			e.printStackTrace();
			
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("showItemExport");
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		InterfacesForm tripForm = (InterfacesForm) form;
		try {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			request.setAttribute("type", SystemElements.ADMIN);
			request.setAttribute("searchKey", tripForm.getCriteria().getSearchKey());
			
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "re-search";
	}
	
	@Override
	protected void setNewCriteria(ActionForm form) {
		InterfacesForm tripForm = (InterfacesForm) form;
		tripForm.setCriteria(new InterfacesCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
