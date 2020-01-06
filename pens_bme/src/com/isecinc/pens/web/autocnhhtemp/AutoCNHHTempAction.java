package com.isecinc.pens.web.autocnhhtemp;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class AutoCNHHTempAction extends I_Action {

	public static int pageSize = 25;
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		AutoCNHHTempForm aForm = (AutoCNHHTempForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("prepare2 action["+action+"]");
			
			if("new".equals(action)){
				request.getSession().removeAttribute("BATCH_TASK_RESULT");
				aForm.setResultsSearch(null);
				AutoCNHHTempBean ad = new AutoCNHHTempBean();
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("import");
	}
	
	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("importExcel");
		AutoCNHHTempForm aForm = (AutoCNHHTempForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			
			request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
			request.getSession().setAttribute("DATA_FILE", aForm.getDataFile());
			request.setAttribute("action","submitedRunBatchTask");//set to popup page to BatchTask
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("import");
	}
	
	/** For batch popup after Task success**/
	/** For display BatchTask Result **/
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		AutoCNHHTempForm aForm = (AutoCNHHTempForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			logger.debug("searchBatch ");
	         aForm.setResults(null);
	         
			 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
			 logger.debug("batchTaskForm result size:"+batchTaskForm.getResults().length);
			 
			 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
			 
			 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("import");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
		
		}
		return "";
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
	
		return "detail";
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
