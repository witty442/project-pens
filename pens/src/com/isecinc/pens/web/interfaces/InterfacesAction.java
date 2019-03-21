package com.isecinc.pens.web.interfaces;

import java.math.BigDecimal;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.AppversionVerify;
import util.ControlCode;
import util.MonitorSales;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Trip;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.MonitorItemDetailBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ConvertUtils;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.ExportManager;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.inf.manager.UpdateSalesManager;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MUser;

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
		InterfacesForm conversionForm = (InterfacesForm) form;
		String returnText = "prepare";
		try {
			
			//for test
			/*User user = new User();
			com.isecinc.core.bean.References ref = new com.isecinc.core.bean.References("AD","AD","AD");
			user.setUserName("ADMIN");
			user.setRole(ref);
			request.getSession().setAttribute("user",user);*/
			
			conversionForm.setMonitorBean(new MonitorBean());
			
			//clear Task running for next run
			InterfaceDAO dao = new InterfaceDAO();
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT);
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT);
			
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
		logger.debug("Interfaces Prepare Form without ID");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		String returnText = "prepare";
		//Connection conn = null;
		try {
			
			interfacesForm.setResults(null);
			//for test
			/*
			 *  SOMBOON   VAN
				WANCHAI   DD
				SOMCHAI   TT
			 */
			/*User user = new User();
		
			user.setUserName("SOMCHAI");
	        user.setPassword("1234");
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			user = new LoginProcess().login(user.getUserName(), user.getPassword(), conn);
			
			request.getSession().setAttribute("user",user);*/
			
			interfacesForm.setMonitorBean(new MonitorBean());
			
			//clear Task running for next run
			InterfaceDAO dao = new InterfaceDAO();
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT);
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
		}finally{
			//conn.close();
		}
		return returnText;
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Interfaces Search Current Action");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		InterfaceDAO dao = new InterfaceDAO();
		try {
			String timeInUse =interfacesForm.getMonitorBean().getTimeInUse();
			logger.info("TimeInUse:"+timeInUse);
			
			InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
			if(request.getAttribute("searchKey") != null){
				criteria.setSearchKey((String)request.getAttribute("searchKey"));
			}
			interfacesForm.setCriteria(criteria);
			/** Set Condition Search **/
			MonitorBean[] results = dao.findMonitorList(user);
			
			if (results != null && results.length > 0) {
				interfacesForm.getCriteria().setSearchResult(results.length);
				interfacesForm.setResults(results);
				criteria.setMonitorBean(results[0]);
				interfacesForm.setCriteria(criteria);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}

			interfacesForm.getMonitorBean().setTimeInUse(timeInUse);
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
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
	public ActionForward syschronizeFromOracle(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Import :syschronizeFromOracle");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User userLogin = (User) request.getSession().getAttribute("user");
		ImportManager importManager =  new ImportManager();
		User userRequest = new User();
		boolean canRunBatch = false;
		try {
			//clear Task running for next run
			InterfaceDAO dao = new InterfaceDAO();
			String status = dao.findControlMonitor(Constants.TYPE_IMPORT);
			logger.info("status["+status+"]");
			if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
			    canRunBatch = true;
			}
		
			//CheckBok choose ReImport Update Transaction
			String reimportUpdateTransChk = request.getParameter("reimportUpdateTransChk");
			logger.debug("reimportUpdateTransChk:"+reimportUpdateTransChk);
			if( !Utils.isNull(reimportUpdateTransChk).equals("")){
				//Update Control Code Fore run process
				ControlCode.updateControlCode("BatchImportWorker", "reimportUpdateTrans", "Y");
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
				     
				MonitorBean m = importManager.importMain(userLogin,userRequest,requestTable,request,interfacesForm.getMonitorBean().isImportAll(),requestTableTransType);
				interfacesForm.setMonitorBean(m);
				
				/** Set for Progress Bar Popup **/
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
	 * Export To Txt
	 */
	public ActionForward syschronizeToOracle(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Export :syschronizeToOracle");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User userLogin = (User) request.getSession().getAttribute("user");
		ExportManager exportManager =  new ExportManager();
		User userRequest = new User();
		String isImport = "N";
		try {
			if(request.getParameter("import") != null){
				isImport = (String)request.getParameter("import");
			}
			
			boolean canRunBatch = false;
			InterfaceDAO dao = new InterfaceDAO();
			String status = dao.findControlMonitor(Constants.TYPE_EXPORT);
			logger.info("status["+status+"]");
			if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
			    canRunBatch = true;
			}
					
			if(canRunBatch){
				
				logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
				
	            String requestTable = "";
	            String requestTableTransType = "";
			    if( !Utils.isNull(interfacesForm.getMonitorBean().getRequestExportTable()).equals("")){
					String[] exportArray = interfacesForm.getMonitorBean().getRequestExportTable().split("\\|");
					requestTable = exportArray[0];
					requestTableTransType = exportArray[1];
					
					/** Case Admin Update By Request Table Replace UserId*/
					String whereClause = "AND USER_NAME LIKE '%"+interfacesForm.getMonitorBean().getRequestExportUserName()+"%'";
					if(Utils.isNull(interfacesForm.getMonitorBean().getRequestExportUserName()).equals("")){
						whereClause = "AND USER_NAME LIKE '%ADMIN%'";
					}
					User[] results = new MUser().search(whereClause);
					if(results != null && results.length >0){
						userRequest = results[0];/** Replace UserID for Serach File in FTP Server */
					}
				}
			    
			    logger.debug("requestUpdateSalesTable:"+interfacesForm.getMonitorBean().getRequestExportTable());
			    logger.debug("UserId Request:"+userRequest.getId()+",UserName Request:"+userRequest.getRole());
	
				MonitorBean m = exportManager.exportMain(userLogin,userRequest, requestTable,requestTableTransType, request);
				interfacesForm.setMonitorBean(m);
				/** Set from Progress Popup **/
				request.setAttribute("action", "submited");
				request.setAttribute("id", m.getTransactionId());
				//request.setAttribute("import", isImport);
				
				/** Check For ProgressBar  **/
				 if( !Utils.isNull(interfacesForm.getMonitorBean().getRequestExportTable()).equals("")){
				     request.setAttribute("transaction_count", "1");
				 }else{
					 request.setAttribute("transaction_count", "2"); 
				 }
 
				logger.debug("getTransactionId:"+m.getTransactionId());
				
			}else{
				request.setAttribute("Message","กำลังส่งข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("success");
	}
	
	/**
	 * Export To Txt
	 */
	public ActionForward clearControl(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearControl");
		try {
		    String sql = "delete from monitor; delete from monitor_item;delete from monitor_item_detail;";
		    String log = Utils.excUpdate(sql);
		    logger.debug("log:"+log);
			request.setAttribute("Message","Clear Control Import Export Success กรุณา ดึงข้อมูล หรือ Export ใหม่ ");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("success");
	}
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward getLog(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("getLog");
		User user = (User) request.getSession().getAttribute("user");
		EnvProperties env = EnvProperties.getInstance();
		String pathFull = "";
		String htmlCode = "";
		String fileName = "";
		try {
			 fileName = request.getParameter("fileName")+"-"+user.getUserName()+".log";
			 String transType = request.getParameter("transType");
			 if(Constants.TRANSACTION_MASTER_TYPE.equals(transType)){
				 pathFull =  env.getProperty("path.master.sales.in.result")+fileName;
			 }else if(Constants.TRANSACTION_TRANS_TYPE.equals(transType)){
				 pathFull =  env.getProperty("path.transaction.sales.in.result")+fileName;
			 }else if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(transType)){
				// pathFull =  env.getProperty("path.transaction.sales.in.result")+fileName;
				//get from table t_temp_import_trans
				 fileName = request.getParameter("fileName");
			 }
			 
			 if(Constants.TRANSACTION_MASTER_TYPE.equals(transType)
				|| Constants.TRANSACTION_TRANS_TYPE.equals(transType)){
				 logger.debug("fileName:"+fileName);
				 logger.debug("transaType:"+transType);
				 logger.debug("pathFull:"+pathFull);
				 
				 FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			     //DISPLAY CSV FILE 
				// ftpManager.getDownloadFTPFileByName(pathFull,response);
				
				 String dataLog = ftpManager.getDownloadFTPFileByName(pathFull);
				// logger.debug("DataLOG:"+dataLog);
				  htmlCode = ConvertUtils.genHTMLCode(dataLog).toString(); 
			 }else{
				 //Case TRANSACTION_UTS_TRANS_TYPE
				 //get Logs from table t_temp_import_trans
				 String dataLog = InterfaceDAO.getUpdateTransErrLogs(fileName).toString();
				 if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(transType)){
					 htmlCode = ConvertUtils.genHTMLCodeUpdateTransLogs(dataLog).toString();
				 }
			 }
			 
			 //logger.debug("htmlCode:"+htmlCode);
		     request.setAttribute("DATA_LOGS", htmlCode);
		     request.setAttribute("LOGS_NAME",fileName );
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("getLog");
	}
	
	
	public ActionForward getTextFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("getLog");
		User user = (User) request.getSession().getAttribute("user");
		int i = 0;
		EnvProperties env = EnvProperties.getInstance();
		String pathFull = "";
		try {
			 String fileName = request.getParameter("fileName");;
			 String transType = request.getParameter("transType");
			 String type = request.getParameter("type");
			 String status = request.getParameter("status");
			 if(Constants.TRANSACTION_MASTER_TYPE.equals(transType)){
				 if(type.equals(Constants.TYPE_EXPORT)){
					 if(fileName.indexOf("TRIP") != -1 
					   || fileName.indexOf("AUTHEN") != -1
					   || fileName.indexOf("SALES_INVENTORY") != -1){
						 pathFull =  env.getProperty("path.master.sales.in")+fileName;
					 }else{
				         pathFull =  env.getProperty("path.master.sales.out")+fileName;
					 }
				 }else{
					 //import 
					 if(fileName.indexOf("TRIP") != -1 
					   || fileName.indexOf("CUST") != -1
					   || fileName.indexOf("CUSTADDR") != -1
					   || fileName.indexOf("CUSTCONTACT") != -1
							 ){
						 if(status.equals("1")){ //success
							 pathFull =  env.getProperty("path.master.sales.in.processed")+fileName; 
						 }else{
							 pathFull =  env.getProperty("path.master.sales.in")+fileName; 
						 }
					 }else{
					    pathFull =  env.getProperty("path.master.sales.in")+fileName; 
					 }
				 }
			 }else if(Constants.TRANSACTION_TRANS_TYPE.equals(transType)){
				 if(type.equals(Constants.TYPE_EXPORT)){
					 pathFull =  env.getProperty("path.transaction.sales.out")+fileName; 
				 }else{
					 if((Constants.STATUS_SUCCESS+"").equals(status)){
					     pathFull =  env.getProperty("path.transaction.sales.in.processed")+fileName;
					 }else{
						 pathFull =  env.getProperty("path.transaction.sales.in")+fileName; 
					 }
				 }
			 }else if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(transType)){
				 if(type.equals(Constants.TYPE_EXPORT)){
					 pathFull =  env.getProperty("path.transaction.sales.out")+fileName; 
				 }else{
					 if((Constants.STATUS_SUCCESS+"").equals(status)){
					     pathFull =  env.getProperty("path.transaction.sales.in.processed")+fileName;
					 }else{
						 pathFull =  env.getProperty("path.transaction.sales.in")+fileName; 
					 }
				 }
			 }
			 logger.debug("fileName:"+fileName);
			 logger.debug("transaType:"+transType);
			 logger.debug("pathFull:"+pathFull);
			 
			 FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
		     //DISPLAY CSV FILE 
			 ftpManager.getDownloadFTPFileByName(pathFull,response);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("getLog");
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
		Trip trip = null;
		
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
