package com.isecinc.pens.web.db;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.db.backup.DBBackUpManager;
import com.isecinc.pens.db.backup.DBRestoreManager;
import com.isecinc.pens.init.InitialMessages;

/**
 * DBManagementAction Class
 * 
 * @author Witty.B
 * @version $Id: DBManagementAction ,v 1.0 19/10/2010 00:00:00
 * 
 */

public class DBManagementAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Prepare Form");
		DBManagementForm formBean = (DBManagementForm) form;
		String returnText = "prepare";
		try {
			
		} catch (Exception e) {
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
		logger.debug("Prepare Form without ID");
		DBManagementForm ManualUpdateAddressForm = (DBManagementForm) form;
		String returnText = "prepare";
		try {
			ManualUpdateAddressForm.setResults(null);
			
		} catch (Exception e) {
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
		DBManagementForm formBean = (DBManagementForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		try {
			
            request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
            
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}

	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DBManagementForm formBean = (DBManagementForm) form;
		try {
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()+ e.toString());	
		}
		return "prepare";
	}

	
	@Override
	protected void setNewCriteria(ActionForm form) {
		
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ActionForward backupDB(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("backupDB");
		try {
			  User userB =(User)request.getSession().getAttribute("user");
			  String[] pathFull = DBBackUpManager.processBackup(request,userB);
			  request.setAttribute("Message","Backup DB Success \n Path Backup ->"+pathFull[0]);
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare");
	}
	
	public ActionForward restoreDB(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("restoreDB");
		try {
			  String pathFull = DBRestoreManager.process(request);
			  request.setAttribute("Message","Restore DB Success \n Path Backup ->"+pathFull);
		} catch (Exception e) {
            logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare");
	}
	
	
}
