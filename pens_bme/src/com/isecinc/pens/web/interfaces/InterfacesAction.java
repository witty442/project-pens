package com.isecinc.pens.web.interfaces;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.bean.InterfaceBean;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.MonitorItemDetailBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.inf.manager.ProcessManager;
import com.isecinc.pens.inf.manager.process.GenerateHISHER;
import com.isecinc.pens.inf.manager.process.GenerateOrderExcel;
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
		logger.debug("Interfaces Prepare Form without ID");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		String returnText = "prepare";
		InterfaceDAO dao = new InterfaceDAO();
		try {
			 
			logger.debug("pageName:"+Utils.isNull(request.getParameter("pageName")) +",pageAction:"+Utils.isNull(request.getParameter("pageAction")));
			
			if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_HISHER)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				//default value
				InterfaceBean bean =new InterfaceBean();
				bean.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
				bean.setCustGroupDesc(PickConstants.STORE_TYPE_HISHER_CODE+" "+PickConstants.getStoreGroupName(PickConstants.STORE_TYPE_HISHER_CODE));
				bean.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    bean.setTextFileName("");
				bean.setOutputPath("");//Gen
				
				interfacesForm.setBean(bean);
				logger.debug("transaDate:"+interfacesForm.getBean().getTransactionDate());
				
				
				interfacesForm.setMonitorBean(new MonitorBean());
				interfacesForm.setMonitorItemBeanResult(new MonitorItemBean());
				interfacesForm.setMonitorItemList(null);
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_HISHER);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BILL_ICC)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){

				interfacesForm.setMonitorBean(new MonitorBean());
				interfacesForm.setMonitorItemBeanResult(new MonitorItemBean());
				interfacesForm.setMonitorItemList(null);
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_BILL_ICC);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BMESCAN)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				
				interfacesForm.setMonitorBean(new MonitorBean());
				interfacesForm.setMonitorItemBeanResult(new MonitorItemBean());
				interfacesForm.setMonitorItemList(null);
				
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_BMESCAN);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_EXPORT_BILL_ICC)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				
				interfacesForm.setMonitorBean(new MonitorBean());
				interfacesForm.setMonitorItemBeanResult(new MonitorItemBean());
				interfacesForm.setMonitorItemList(null);
				
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT_BILL_ICC);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ORDER_EXCEL)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				
				//default value
				InterfaceBean bean =new InterfaceBean();
				bean.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
				bean.setCustGroupDesc(PickConstants.STORE_TYPE_HISHER_CODE+" "+PickConstants.getStoreGroupName(PickConstants.STORE_TYPE_HISHER_CODE));
				bean.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    bean.setTextFileName("");
				bean.setOutputPath("");//Gen
				
				interfacesForm.setBean(bean);
				logger.debug("transaDate:"+interfacesForm.getBean().getTransactionDate());
				
				interfacesForm.setMonitorBean(new MonitorBean());
				interfacesForm.setMonitorItemBeanResult(new MonitorItemBean());
				interfacesForm.setMonitorItemList(null);
				
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_ORDER_EXCEL);
				
			}else 	if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ITEM_MASTER_HISHER)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				//default value
				InterfaceBean bean =new InterfaceBean();
				bean.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
				bean.setCustGroupDesc(PickConstants.STORE_TYPE_HISHER_CODE+" "+PickConstants.getStoreGroupName(PickConstants.STORE_TYPE_HISHER_CODE));
				bean.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    bean.setTextFileName("");
				bean.setOutputPath("");//Gen
				
				interfacesForm.setBean(bean);
				logger.debug("transaDate:"+interfacesForm.getBean().getTransactionDate());
				
				
				interfacesForm.setMonitorBean(new MonitorBean());
				interfacesForm.setMonitorItemBeanResult(new MonitorItemBean());
				interfacesForm.setMonitorItemList(null);
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_ITEM_MASTER_HISHER);
			}
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
		String type = "";
		try {
			String timeInUse =interfacesForm.getMonitorBean().getTimeInUse();
			logger.info("TimeInUse:"+timeInUse);
			
			logger.info("pageName:"+Utils.isNull(request.getParameter("pageName")));
			
			if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_HISHER)){
				type = Constants.TYPE_GEN_HISHER;
				
				InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
				if(request.getAttribute("searchKey") != null){
					criteria.setSearchKey((String)request.getAttribute("searchKey"));
				}
				interfacesForm.setCriteria(criteria);
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,type);
				
				if (results != null && results.length > 0) {
					interfacesForm.getCriteria().setSearchResult(results.length);
					interfacesForm.setResults(results);
					criteria.setMonitorBean(new MonitorBean());
					interfacesForm.setCriteria(criteria);
					
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					
				
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BILL_ICC)){
				type = Constants.TYPE_IMPORT_BILL_ICC;
				
				InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
				if(request.getAttribute("searchKey") != null){
					criteria.setSearchKey((String)request.getAttribute("searchKey"));
				}
				interfacesForm.setCriteria(criteria);
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,type);
				
				if (results != null && results.length > 0) {
					interfacesForm.getCriteria().setSearchResult(results.length);
					interfacesForm.setResults(results);
					criteria.setMonitorBean(new MonitorBean());
					interfacesForm.setCriteria(criteria);
					
					//Search interfaceResult (monitorItem)
					interfacesForm.setMonitorItemList(dao.findMonitorItemList(user,results[0]));
				
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
			}else 	if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BMESCAN)){
				type = Constants.TYPE_IMPORT;
				
				InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
				if(request.getAttribute("searchKey") != null){
					criteria.setSearchKey((String)request.getAttribute("searchKey"));
				}
				interfacesForm.setCriteria(criteria);
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorList(user,type);
				
				if (results != null && results.length > 0) {
					interfacesForm.getCriteria().setSearchResult(results.length);
					interfacesForm.setResults(results);
					criteria.setMonitorBean(new MonitorBean());
					interfacesForm.setCriteria(criteria);
					
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_EXPORT_BILL_ICC)){
				type = Constants.TYPE_EXPORT_BILL_ICC;
				
				InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
				if(request.getAttribute("searchKey") != null){
					criteria.setSearchKey((String)request.getAttribute("searchKey"));
				}
				interfacesForm.setCriteria(criteria);
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,type);
				
				if (results != null && results.length > 0) {
					interfacesForm.getCriteria().setSearchResult(results.length);
					interfacesForm.setResults(results);
					criteria.setMonitorBean(new MonitorBean());
					interfacesForm.setCriteria(criteria);
					
					//Search interfaceResult (monitorItem)
					interfacesForm.setMonitorItemList(dao.findMonitorItemList(user,results[0]));
				
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ORDER_EXCEL)){
					type = Constants.TYPE_GEN_ORDER_EXCEL;
					
					InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
					if(request.getAttribute("searchKey") != null){
						criteria.setSearchKey((String)request.getAttribute("searchKey"));
					}
					interfacesForm.setCriteria(criteria);
					/** Set Condition Search **/
					MonitorBean[] results = dao.findMonitorListNew(user,type);
					
					if (results != null && results.length > 0) {
						interfacesForm.getCriteria().setSearchResult(results.length);
						interfacesForm.setResults(results);
						criteria.setMonitorBean(new MonitorBean());
						interfacesForm.setCriteria(criteria);
						
						//Search interfaceResult (monitorItem)
						MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
						interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
						
					
					} else {
						request.setAttribute("Message", "Data not found");
					}
					
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ITEM_MASTER_HISHER)){
				type = Constants.TYPE_GEN_ITEM_MASTER_HISHER;
				
				InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
				if(request.getAttribute("searchKey") != null){
					criteria.setSearchKey((String)request.getAttribute("searchKey"));
				}
				interfacesForm.setCriteria(criteria);
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,type);
				
				if (results != null && results.length > 0) {
					interfacesForm.getCriteria().setSearchResult(results.length);
					interfacesForm.setResults(results);
					criteria.setMonitorBean(new MonitorBean());
					interfacesForm.setCriteria(criteria);
					
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					
				
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
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
	
	 
	public ActionForward runBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		
		logger.debug("IrunBatch");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User userLogin = (User) request.getSession().getAttribute("user");
		ProcessManager processManager =  new ProcessManager();
		InterfaceDAO dao = new InterfaceDAO();
		boolean canRunBatch = false;
		String textFileName = "";
		EnvProperties env = EnvProperties.getInstance();
		try {
			
			if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_HISHER)){
				String status = dao.findControlMonitor(Constants.TYPE_GEN_HISHER);
				
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName("Generate His &Her");
					monitorModel.setType(Constants.TYPE_GEN_HISHER);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Gen FileName **/
					textFileName = InterfaceUtils.getHisHerTextFileName(interfacesForm.getBean().getTransactionDate());//Gen
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					logger.debug("Output path:"+interfacesForm.getBean().getOutputPath());
					
					batchParamMap.put(GenerateHISHER.PARAM_OUTPUT_PATH,"");
					batchParamMap.put(GenerateHISHER.PARAM_FILE_NAME,textFileName);
					batchParamMap.put(GenerateHISHER.PARAM_CUST_GROUP,interfacesForm.getBean().getCustGroup());
					batchParamMap.put(GenerateHISHER.PARAM_TRANS_DATE, interfacesForm.getBean().getTransactionDate());
					
					monitorModel.setBatchParamMap(batchParamMap);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());
					
					interfacesForm.getBean().setTextFileName(textFileName);
					interfacesForm.getBean().setOutputPath(	env.getProperty("path.icc.hisher.export.txt"));
					
				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BILL_ICC)){
				String status = dao.findControlMonitor(Constants.TYPE_IMPORT_BILL_ICC);
				
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName("Import Bill ICC");
					monitorModel.setType(Constants.TYPE_IMPORT_BILL_ICC);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Gen FileName **/
					textFileName = InterfaceUtils.getHisHerTextFileName(interfacesForm.getBean().getTransactionDate());//Gen
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					logger.debug("Output path:"+interfacesForm.getBean().getOutputPath());
					batchParamMap.put(GenerateHISHER.PARAM_TRANS_DATE, interfacesForm.getBean().getTransactionDate());
					
					monitorModel.setBatchParamMap(batchParamMap);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());

				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_EXPORT_BILL_ICC)){
				String status = dao.findControlMonitor(Constants.TYPE_EXPORT_BILL_ICC);
				
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName("Export Bill ICC");
					monitorModel.setType(Constants.TYPE_EXPORT_BILL_ICC);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Gen FileName **/
					textFileName = InterfaceUtils.getHisHerTextFileName(interfacesForm.getBean().getTransactionDate());//Gen
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					batchParamMap.put(GenerateHISHER.PARAM_TRANS_DATE, interfacesForm.getBean().getTransactionDate());
					
					monitorModel.setBatchParamMap(batchParamMap);
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());

				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ORDER_EXCEL)){
				String status = dao.findControlMonitor(Constants.TYPE_GEN_ORDER_EXCEL);
				String realPathTemps = BeanParameter.getTempPath();
				logger.debug("realPathTemps"+realPathTemps);
				
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName("Generate Order Excel His&Her");
					monitorModel.setType(Constants.TYPE_GEN_ORDER_EXCEL);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Gen FileName **/
					textFileName = InterfaceUtils.getHisHerExcelFileName(interfacesForm.getBean().getTransactionDate());//Gen
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					logger.debug("Output path:"+interfacesForm.getBean().getOutputPath());
					
					batchParamMap.put(GenerateOrderExcel.PARAM_OUTPUT_PATH,"");
					batchParamMap.put(GenerateOrderExcel.PARAM_FILE_NAME,textFileName);
					batchParamMap.put(GenerateOrderExcel.PARAM_CUST_GROUP,interfacesForm.getBean().getCustGroup());
					batchParamMap.put(GenerateOrderExcel.PARAM_TRANS_DATE, interfacesForm.getBean().getTransactionDate());
					batchParamMap.put(GenerateOrderExcel.PARAM_REAL_PATH_TEMP,realPathTemps);
					
					monitorModel.setBatchParamMap(batchParamMap);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());
					
					interfacesForm.getBean().setTextFileName(textFileName);
					interfacesForm.getBean().setOutputPath(env.getProperty("path.icc.hisher.export.orderexcel"));
					
				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ITEM_MASTER_HISHER)){
				String status = dao.findControlMonitor(Constants.TYPE_GEN_ITEM_MASTER_HISHER);
				
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName("Generate Item Master His &Her to ICC");
					monitorModel.setType(Constants.TYPE_GEN_ITEM_MASTER_HISHER);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Gen FileName **/
					textFileName = InterfaceUtils.getHisHerItemMasterTextFileName(interfacesForm.getBean().getTransactionDate());//Gen
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					logger.debug("Output path:"+interfacesForm.getBean().getOutputPath());
					
					batchParamMap.put(GenerateHISHER.PARAM_OUTPUT_PATH,"");
					batchParamMap.put(GenerateHISHER.PARAM_FILE_NAME,textFileName);
					batchParamMap.put(GenerateHISHER.PARAM_CUST_GROUP,interfacesForm.getBean().getCustGroup());
					batchParamMap.put(GenerateHISHER.PARAM_TRANS_DATE, interfacesForm.getBean().getTransactionDate());
					
					monitorModel.setBatchParamMap(batchParamMap);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());
					
					interfacesForm.getBean().setTextFileName(textFileName);
					interfacesForm.getBean().setOutputPath(	env.getProperty("path.icc.hisher.export.master.txt"));
					
				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
				
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
