package com.isecinc.pens.web.role;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBConnection;
import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.Role;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MRole;
import com.isecinc.pens.report.salesanalyst.SAConstants;

/**
 * User Action Class
 * 
 * @author Witty
 * 
 */
public class RoleAction extends I_Action {


	
	/**
	 * Prepare
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RoleForm roleForm = (RoleForm) form;
		try {
			if(request.getParameter("action") != null){
				request.getSession().removeAttribute("ROLE_LIST");
				request.getSession().removeAttribute("ROLE_DETAIL_LIST");
			}

		} catch (Exception e) {
			request.setAttribute("Message",  e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RoleForm roleForm = (RoleForm) form;
		try {
			if(request.getParameter("action") != null){
				request.getSession().removeAttribute("ROLE_LIST");
				request.getSession().removeAttribute("ROLE_DETAIL_LIST");
			}

		} catch (Exception e) {
			request.setAttribute("Message",  e.getMessage());
			throw e;
		}
		return "prepare";
	}
	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RoleForm roleForm = (RoleForm) form;
		try {
            request.getSession().setAttribute("ROLE_LIST",MRole.findRoleList(roleForm.getRole()));
            
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", e.getMessage());
			throw e;
		}
		return "search";
	}

	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		RoleForm roleForm = (RoleForm) form;
		try {
			//User userActive = (User) request.getSession(true).getAttribute("user");
			conn = DBConnection.getInstance().getConnection();
			// Begin Transaction
			conn.setAutoCommit(false);
			//
			request.setAttribute("Message", SystemMessages.getCaption("SaveSucess", Locale.getDefault()));
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {}
			request.setAttribute("Message", e.getMessage());
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "prepare";
	}
	
	public ActionForward searchRole(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("addRole");
		RoleForm formBean = (RoleForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		try {	
			if(Utils.isNull(request.getParameter("roleDup")).equals("true")){
				request.getSession().setAttribute("ROLE_DETAIL_LIST",null);
			}else{
				List roleDetailList = MRole.findRoleListByRole(formBean.getRole());
				
				if(roleDetailList != null && roleDetailList.size() ==0){
					roleDetailList = new ArrayList<Role>();
					
			    	Role roleItem = new Role();
			        roleItem.setIndex(4);
			        roleItem.setRoleColumnAccess("Brand");
			        roleItem.setRoleDataAccess("ALL");
			        roleItem.setRoleDataAccessDesc(SAConstants.MSG_ALL_TH);
				    roleDetailList.add(roleItem);
				    
				    roleItem = new Role();
			        roleItem.setIndex(3);
			        roleItem.setRoleColumnAccess("Division");
			        roleItem.setRoleDataAccess("ALL");
			        roleItem.setRoleDataAccessDesc(SAConstants.MSG_ALL_TH);
				    roleDetailList.add(roleItem);
				    
				    roleItem = new Role();
			        roleItem.setIndex(2);
			        roleItem.setRoleColumnAccess("Sales_Channel");
			        roleItem.setRoleDataAccess("ALL");
			        roleItem.setRoleDataAccessDesc(SAConstants.MSG_ALL_TH);
				    roleDetailList.add(roleItem);
				    
				    roleItem = new Role();
			        roleItem.setIndex(1);
			        roleItem.setRoleColumnAccess("Customer_Category");
			        roleItem.setRoleDataAccess("ALL");
			        roleItem.setRoleDataAccessDesc(SAConstants.MSG_ALL_TH);
				    roleDetailList.add(roleItem);
				    
				    roleItem = new Role();
			        roleItem.setIndex(5);
			        roleItem.setRoleColumnAccess("Salesrep_id");
			        roleItem.setRoleDataAccess("ALL");
			        roleItem.setRoleDataAccessDesc(SAConstants.MSG_ALL_TH);
				    roleDetailList.add(roleItem);
				     
				}
				request.getSession().setAttribute("ROLE_DETAIL_LIST",roleDetailList);
			}

		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("add");
	}
	
	
	public ActionForward viewRole(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("ViewRole");
		RoleForm formBean = (RoleForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		try {	
			String roleIdParam = Utils.isNull(request.getParameter("roleId"));
			formBean.getRole().setRoleId(roleIdParam);
			formBean.setMode("View");
			
		    request.getSession().setAttribute("ROLE_DETAIL_LIST",MRole.findRoleListByRole(formBean.getRole()));
		    
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("view");
	}
	
	public ActionForward addRole(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("addRole");
		RoleForm formBean = (RoleForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		try {	
			request.getSession().setAttribute("ROLE_DETAIL_LIST",null);
			formBean.setMode("CreateNewRecord");
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("add");
	}

	
	public ActionForward editRole(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("editRole");
		RoleForm formBean = (RoleForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		try {	
			String roleIdParam = Utils.isNull(request.getParameter("roleId"));
			String roleNameParam = Utils.isNull(request.getParameter("roleName"));
			logger.debug("roleIdSession:"+Utils.isNull(formBean.getRole().getRoleId()));
			
			if(Utils.isNull(formBean.getRole().getRoleId()).equals("")){
			    formBean.getRole().setRoleId(roleIdParam);
			   
			}
			formBean.setMode("Edit");
			List roleList = MRole.findRoleListByRole(formBean.getRole());
		    request.getSession().setAttribute("ROLE_DETAIL_LIST",roleList);
		 
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward addRowTable(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("addRowTable");
		RoleForm formBean = (RoleForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		List<Role> roleDetailList = null;
		try {	
		    if(request.getSession().getAttribute("ROLE_DETAIL_LIST") != null){
		       roleDetailList  = (List<Role>) request.getSession().getAttribute("ROLE_DETAIL_LIST");
		    }
		    
		    if(roleDetailList != null && roleDetailList.size() > 0){	
		    	
		    	/** set value to Session **/
		    	for(int i=0;i<roleDetailList.size();i++){
		    		Role roleItem = (Role)roleDetailList.get(i);
		    		String[] roleColumnAccess = request.getParameterValues("roleColumnAccess");
		    		String[] roleDataAccess = request.getParameterValues("roleDataAccess");
		    		String[] roleDataAccessDesc = request.getParameterValues("roleDataAccessDesc");
		    		
		    		roleItem.setRoleColumnAccess(roleColumnAccess[roleItem.getIndex()-1]);
		    		roleItem.setRoleDataAccess(roleDataAccess[roleItem.getIndex()-1]);
		    		roleItem.setRoleDataAccessDesc(roleDataAccessDesc[roleItem.getIndex()-1]);
		    		
		    		roleDetailList.set(i, roleItem);
		    	}
		    	
		    	//add new Role
		        Role roleItem = new Role();
		        roleItem = formBean.getRole();
		        
		        Role lastRoleRow = (Role)roleDetailList.get(roleDetailList.size()-1);
		        roleItem.setIndex(lastRoleRow.getIndex()+1);
		        roleDetailList.add(roleItem);
		    }else{
		    	roleDetailList = new ArrayList<Role>();
		    	Role roleItem = new Role();
			    roleItem = formBean.getRole(); 
		        roleItem.setIndex(1);
			    roleDetailList.add(roleItem);
		    }
		    request.getSession().setAttribute("ROLE_DETAIL_LIST",roleDetailList);
		    
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward removeRowTable(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("removeRowTable");
		RoleForm formBean = (RoleForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		List<Role> newRoleDetailList = new ArrayList();
		try {	
		    if(request.getSession().getAttribute("ROLE_DETAIL_LIST") != null){
		    	String[] ids = formBean.getIds();
		    	logger.debug("ids:"+ids.length);
		    	List roleDetailList = (List<Role>) request.getSession().getAttribute("ROLE_DETAIL_LIST");
		    	
		    	/** set value to Session **/
		    	for(int i=0;i<roleDetailList.size();i++){
		    		Role roleItem = (Role)roleDetailList.get(i);
		    		String[] roleColumnAccess = request.getParameterValues("roleColumnAccess");
		    		String[] roleDataAccess = request.getParameterValues("roleDataAccess");		    		
                    String[] roleDataAccessDesc = request.getParameterValues("roleDataAccessDesc");
		    		
		    		roleItem.setRoleColumnAccess(roleColumnAccess[roleItem.getIndex()-1]);
		    		roleItem.setRoleDataAccess(roleDataAccess[roleItem.getIndex()-1]);
		    		roleItem.setRoleDataAccessDesc(roleDataAccessDesc[roleItem.getIndex()-1]);
		    		
		    		roleDetailList.set(i, roleItem);
		    	}
		    	
		    	int index =0;
		    	for(int i=0;i< roleDetailList.size();i++){
		    	    Role roleItem =(Role)roleDetailList.get(i);
		    	    if( !isChkRemoveRow(ids ,roleItem.getIndex() ) ){
		    	    	index++;
		    	    	roleItem.setIndex(index);
		    	    	newRoleDetailList.add(roleItem);
		    	    } 
		    	}//for
		    }//if
		    request.getSession().setAttribute("ROLE_DETAIL_LIST",newRoleDetailList);
		    
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward saveRowTable(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveRowTable");
		RoleForm formBean = (RoleForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		List<Role> roleDetailList = null;
		try {	
		    if(request.getSession().getAttribute("ROLE_DETAIL_LIST") != null){
		    	roleDetailList  = (List<Role>) request.getSession().getAttribute("ROLE_DETAIL_LIST");
		    	
		    	for(int i=0;i<roleDetailList.size();i++){
		    		Role roleItem = (Role)roleDetailList.get(i);
		    		String[] roleColumnAccess = request.getParameterValues("roleColumnAccess");
		    		String[] roleDataAccess = request.getParameterValues("roleDataAccess");
                    String[] roleDataAccessDesc = request.getParameterValues("roleDataAccessDesc");
		    		
		    		roleItem.setRoleColumnAccess(roleColumnAccess[roleItem.getIndex()-1]);
		    		roleItem.setRoleDataAccess(roleDataAccess[roleItem.getIndex()-1]);
		    		roleItem.setRoleDataAccessDesc(roleDataAccessDesc[roleItem.getIndex()-1]);
		    		
		    		roleDetailList.set(i, roleItem);
		    	}
		    }
		    
		    if(roleDetailList != null && roleDetailList.size() > 0){	
		    	
		    	int roleId = MRole.saveRole(formBean.getRole(), roleDetailList);
		    	formBean.getRole().setRoleId(roleId+"");
		    	editRole(mapping, formBean, request, response);
		    	request.setAttribute("Message", SystemMessages.getCaption("SaveSucess", Locale.getDefault()));
		    }
		   
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward clearList(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearList");
		RoleForm formBean = (RoleForm) form;
		try {	
		    request.getSession().setAttribute("ROLE_LIST",null);
		    
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("prepare");
	}
	
	private  boolean isChkRemoveRow(String[] ids ,int index){
		boolean b = false;
		 for(int n=0;n<ids.length;n++){
 	    	logger.debug("ids["+Utils.isNull(ids[n])+"] : index["+index+"]");
    	    if( Utils.isNull(ids[n]).equals(String.valueOf(index))){
    	    	b = true;
    	    	break;
    	    }
 	    }
		logger.debug("b:"+b);
		return b;
	}
	/**
	 * Change Active
	 */
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Connection conn = null;
		RoleForm roleForm = (RoleForm) form;
		try {
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = DBConnection.getInstance().getConnection();
			// Begin Transaction
			conn.setAutoCommit(false);
			//
			//new MUser().changeActive(roleForm.getStatus(), roleForm.getIds(), userActive.getId(), conn);
			// Commit Transaction
			conn.commit();
			//
			request.setAttribute("Message", SystemMessages.getCaption("SaveSucess", Locale.getDefault()));
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			throw e;
		} finally {
			conn.close();
		}
		return "re-search";
	}
	
	public ActionForward backToMain(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("backToMain");
		try {	
			request.getSession().removeAttribute("ROLE_DETAIL_LIST");
			request.getSession().removeAttribute("ROLE_LIST");
			request.getSession().removeAttribute("ROLE_DETAIL_LIST");
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("mainpage");
	}

	public ActionForward backToRolePage(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("backToRolePage");
		RoleForm roleForm = (RoleForm) form;
		try {
			 roleForm.setRole(new Role());
			 search(roleForm, request, response);
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("search");
	}
	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		RoleForm roleForm = (RoleForm) form;
		roleForm.setCriteria(new RoleCriteria());
	}

}
