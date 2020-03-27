package com.isecinc.pens.web.importall.page;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.autocnhhtemp.AutoCNHHTempForm;
import com.isecinc.pens.web.autoorder.AutoOrderForm;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.GenStockOnhandRepTempTask;
import com.isecinc.pens.web.batchtask.task.ImportExcelPICG899ToG07Task;
import com.isecinc.pens.web.importall.ImportAllBean;
import com.isecinc.pens.web.importall.ImportAllForm;
import com.isecinc.pens.web.importall.bean.ImportExcelInvBillTBean;
import com.isecinc.pens.web.importall.bean.PageBean;

public class ImportFileSwitchItemAdjustStockAction {
	
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "importAll";
		ImportAllForm importAllForm = (ImportAllForm) form;
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 request.getSession().removeAttribute("BATCH_TASK_RESULT");
				 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
				 importAllForm.setResults(null);
				 importAllForm.setBean(new ImportAllBean());
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}

	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm aForm = (ImportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try{
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			request.getSession().setAttribute(BatchTaskConstants.BATCH_PARAM_MAP,batchParaMap);
			request.getSession().setAttribute(BatchTaskConstants.DATA_FILE,aForm.getDataFile());
			
			//set to Start BatchTaskName
			request.setAttribute("BATCH_TASK_NAME",BatchTaskConstants.IMPORT_EXCEL_SWITCH_ITEM_ADJStock);//set to popup page to BatchTask
			
	   }catch(Exception e){
		   request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
	   }
	   return mapping.findForward("importAll");
	}
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		ImportAllForm aForm = (ImportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 //searchBatch
			 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
			 
			 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
			
			 logger.debug("BatchTaskName:"+batchTaskForm.getResults()[0].getName());
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("importAll");
	}
	public ActionForward searchBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatchForm");
		ImportAllForm aForm = (ImportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
	         aForm.setResults(null);
	         
			 BatchTaskForm batchTaskForm = new BatchTaskDAO().searchBatchLastRun(user, BatchTaskConstants.IMPORT_EXCEL_SWITCH_ITEM_ADJStock);
			 if(batchTaskForm.getResults() != null && batchTaskForm.getResults().length >0){
				 logger.debug("batchTaskForm result size:"+batchTaskForm.getResults().length);
				 
				 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
				 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
				 
				 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
			 }else{
				 request.setAttribute("Message", "ไม่พบข้อมูล");
			 }
		} catch (Exception e) {
			
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("importAll");
	}
	public ActionForward clearBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearBatchForm");
		ImportAllForm aForm = (ImportAllForm) form;
		try {
			 request.getSession().removeAttribute("BATCH_TASK_RESULT");
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
		} catch (Exception e) {
			
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("importAll");
	}
	
}
