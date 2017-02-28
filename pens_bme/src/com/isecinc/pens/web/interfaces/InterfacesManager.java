package com.isecinc.pens.web.interfaces;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import util.BeanParameter;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.imports.excel.ImportExcelProcess;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.BatchProcessManager;
import com.isecinc.pens.inf.manager.process.GenerateHISHER;
import com.isecinc.pens.inf.manager.process.GenerateOrderExcel;
import com.isecinc.pens.inf.manager.process.ImportWacoalProcess;
import com.isecinc.pens.init.InitialMessages;

public class InterfacesManager {
	
protected static Logger logger = Logger.getLogger("PENS");


public ActionForward runBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		
		logger.debug("runBatch");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User userLogin = (User) request.getSession().getAttribute("user");
		BatchProcessManager processManager =  new BatchProcessManager();
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
					textFileName = InterfaceUtils.getHisHerTextFileName(interfacesForm.getBean().getProductType());//Gen
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					logger.debug("Output path:"+interfacesForm.getBean().getOutputPath());
					
					batchParamMap.put(GenerateHISHER.PARAM_OUTPUT_PATH,"");
					batchParamMap.put(GenerateHISHER.PARAM_FILE_NAME,textFileName);
					batchParamMap.put(GenerateHISHER.PARAM_CUST_GROUP,interfacesForm.getBean().getCustGroup());
					batchParamMap.put(GenerateHISHER.PARAM_TRANS_DATE, interfacesForm.getBean().getTransactionDate());
					batchParamMap.put(GenerateHISHER.PARAM_PRODUCT_TYPE, interfacesForm.getBean().getProductType());
					
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
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_WACOAL_STOCK)){
				String status = dao.findControlMonitor(Constants.TYPE_IMPORT_WACOAL_STOCK);
				String realPathTemps = BeanParameter.getTempPath();
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName(Constants.TYPE_IMPORT_WACOAL_STOCK);
					monitorModel.setType(Constants.TYPE_IMPORT_WACOAL_STOCK);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					batchParamMap.put(ImportWacoalProcess.PARAM_REAL_PATH_TEMP, realPathTemps);
					monitorModel.setBatchParamMap(batchParamMap);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());

				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN)){
				String status = dao.findControlMonitor(Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN);
				String realPathTemps = BeanParameter.getTempPath();
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName(Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN);
					monitorModel.setType(Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					batchParamMap.put(ImportWacoalProcess.PARAM_REAL_PATH_TEMP, realPathTemps);
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
					batchParamMap.put(GenerateHISHER.PARAM_PRODUCT_TYPE, interfacesForm.getBean().getProductType());
					
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
					batchParamMap.put(GenerateOrderExcel.PARAM_PRODUCT_TYPE, interfacesForm.getBean().getProductType());
					
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
					textFileName = InterfaceUtils.getHisHerItemMasterTextFileName(interfacesForm.getBean().getProductType(),interfacesForm.getBean().getTransactionDate());//Gen
				
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					logger.debug("Output path:"+interfacesForm.getBean().getOutputPath());
					
					batchParamMap.put(GenerateHISHER.PARAM_OUTPUT_PATH,"");
					batchParamMap.put(GenerateHISHER.PARAM_FILE_NAME,textFileName);
					batchParamMap.put(GenerateHISHER.PARAM_CUST_GROUP,interfacesForm.getBean().getCustGroup());
					batchParamMap.put(GenerateHISHER.PARAM_TRANS_DATE, interfacesForm.getBean().getTransactionDate());
					batchParamMap.put(GenerateHISHER.PARAM_PRODUCT_TYPE, interfacesForm.getBean().getProductType());
					
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
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_TRANSACTION_LOTUS)){
				String status = dao.findControlMonitor(Constants.TYPE_IMPORT_TRANSACTION_LOTUS);
				
				logger.info("status["+status+"]");
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName("Import transaction Lotus");
					monitorModel.setType(Constants.TYPE_IMPORT_TRANSACTION_LOTUS);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					monitorModel.setBatchParamMap(batchParamMap);
					/** Set FormFile **/
					FormFile dataFile = interfacesForm.getBean().getFormDataFile();
					monitorModel.setDataFile(dataFile);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());
					
					interfacesForm.getBean().setTextFileName(textFileName);
					interfacesForm.getBean().setOutputPath(	env.getProperty("path.icc.hisher.export.master.txt"));
					
				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_SALEOUT_WACOAL)){
				String status = dao.findControlMonitor(Constants.TYPE_IMPORT_SALEOUT_WACOAL);
				String realPathTemps = BeanParameter.getTempPath();
				logger.info("status["+status+"]");
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName(Constants.TYPE_IMPORT_SALEOUT_WACOAL);
					monitorModel.setType(Constants.TYPE_IMPORT_SALEOUT_WACOAL);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					batchParamMap.put(ImportExcelProcess.PARAM_REAL_PATH_TEMP,realPathTemps);
					
					monitorModel.setBatchParamMap(batchParamMap);
					/** Set FormFile **/
					FormFile dataFile = interfacesForm.getBean().getFormDataFile();
					monitorModel.setDataFile(dataFile);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());
					
					interfacesForm.getBean().setTextFileName(textFileName);
					interfacesForm.getBean().setOutputPath(	env.getProperty("path.icc.hisher.export.master.txt"));
					
				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS)){
				String status = dao.findControlMonitor(Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS);
				
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName("Generate Stock End Date Lotus");
					monitorModel.setType(Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					monitorModel.setBatchParamMap(batchParamMap);
					
					/** Set Param Batch Map Object**/
					Map<String, Object> batchParamMapObj = new HashMap<String, Object>();
					OnhandSummary onhandSummary = new OnhandSummary();
					onhandSummary.setPensCustCodeFrom( Utils.isNull(request.getParameter("customerCode")));
					onhandSummary.setSalesDate(Utils.isNull(request.getParameter("salesDate")));
					
					batchParamMapObj.put("ONHAND_SUMMARY", onhandSummary);
					
					monitorModel.setBatchParamMap(batchParamMap);
					monitorModel.setBatchParamMapObj(batchParamMapObj);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());
					
				}else{
					request.setAttribute("Message","กำลังดึงข้อมูลอยู่ กรุณารอสักครู่  โปรดตรวจสอบสถานะล่าสุด");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS)){
				String status = dao.findControlMonitor(Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS);
				
				logger.info("status["+status+"]");
				
				if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
				    canRunBatch = true;
				}
			
				if(canRunBatch){
					logger.debug("UserLogin:"+userLogin.getId()+", RoleLogin:"+userLogin.getType());
					/** Import Data */

					/** insert to monitor_interface **/
					MonitorBean monitorModel = new MonitorBean();
					monitorModel.setName("Generate Stock Report End Date Lotus");
					monitorModel.setType(Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS);
					monitorModel.setStatus(Constants.STATUS_START);
					monitorModel.setCreateUser(userLogin.getUserName());
					monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
					
					/** Set Param Batch Map **/
					Map<String, String> batchParamMap = new HashMap<String, String>();
					monitorModel.setBatchParamMap(batchParamMap);
					
					/** Set Param Batch Map Object**/
					Map<String, Object> batchParamMapObj = new HashMap<String, Object>();
					OnhandSummary onhandSummary = new OnhandSummary();
					onhandSummary.setPensCustCodeFrom( Utils.isNull(request.getParameter("customerCode")));
					onhandSummary.setSalesDate(Utils.isNull(request.getParameter("salesDate")));
					
					batchParamMapObj.put("ONHAND_SUMMARY", onhandSummary);
					
					monitorModel.setBatchParamMap(batchParamMap);
					monitorModel.setBatchParamMapObj(batchParamMapObj);
					
					MonitorBean m = processManager.createBatchTask(monitorModel,userLogin,request);
				   
					/** Set for Progress Bar Opoup **/
					request.setAttribute("action", "submited");
					request.setAttribute("id", m.getTransactionId());
					
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
}
