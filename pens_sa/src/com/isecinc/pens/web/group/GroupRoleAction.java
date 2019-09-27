package com.isecinc.pens.web.group;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.GroupRole;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MGroupRole;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * User Action Class
 * 
 * @author Witty
 * 
 */
public class GroupRoleAction extends I_Action {


	
	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupRoleForm formBean = (GroupRoleForm) form;
		logger.debug("prepare");
		try {
			if(request.getParameter("action") != null){
				formBean.setGroupRole(new GroupRole());
				request.getSession().setAttribute("GROUP_ROLE_LIST",MGroupRole.findGroupRoleList(formBean.getGroupRole()));
			}

		} catch (Exception e) {
			request.setAttribute("Message",  e.getMessage());
			throw e;
		}
		return "prepare";
	}

	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		GroupRoleForm formBean = (GroupRoleForm) form;
		logger.debug("prepare");
		try {
			if(request.getParameter("action") != null){
				formBean.setGroupRole(new GroupRole());
				request.getSession().setAttribute("GROUP_ROLE_LIST",MGroupRole.findGroupRoleList(formBean.getGroupRole()));
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
		GroupRoleForm formBean = (GroupRoleForm) form;
		try {
            request.getSession().setAttribute("GROUP_ROLE_LIST",MGroupRole.findGroupRoleList(formBean.getGroupRole()));
            
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",  e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("saveRowTable");
		GroupRoleForm formBean = (GroupRoleForm) form;
		List<GroupRole> roleDetailList = null;
		try {	
			formBean.setMode("edit");
		    if(request.getSession().getAttribute("GROUP_ROLE_LIST") != null){
		    	roleDetailList  = (List<GroupRole>) request.getSession().getAttribute("GROUP_ROLE_LIST");
		    	
		    	for(int i=0;i<roleDetailList.size();i++){
		    		GroupRole roleItem = (GroupRole)roleDetailList.get(i);
		    		
		    		String[] userGroupIds = request.getParameterValues("userGroupId");
		    		String[] userGroupNames = request.getParameterValues("userGroupName");
		    		String[] roleIds = request.getParameterValues("roleId");
		    		
		    		roleItem.setUserGroupId(Utils.isNull(userGroupIds[roleItem.getIndex()-1]));
		    		roleItem.setUserGroupName(Utils.isNull(userGroupNames[roleItem.getIndex()-1]));
		    		roleItem.setRoleId(Utils.isNull(roleIds[roleItem.getIndex()-1]));
		    		
		    		roleDetailList.set(i, roleItem);
		    	}
		    }
		    if(roleDetailList != null && roleDetailList.size() > 0){	
		    	MGroupRole.saveGroupRole(roleDetailList);
		    	
		    	/** Search **/
		    	request.getSession().setAttribute("GROUP_ROLE_LIST",MGroupRole.findGroupRoleList(formBean.getGroupRole()));
		    	
		    	request.setAttribute("Message", SystemMessages.getCaption("SaveSucess", Locale.getDefault()));
		    }
		   
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return "edit";
	}
	
	
	
	public ActionForward addRowTable(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("addRowTable");
		GroupRoleForm formBean = (GroupRoleForm) form;
		List<GroupRole> roleDetailList = null;
		try {	
			formBean.setMode("edit");
		    if(request.getSession().getAttribute("GROUP_ROLE_LIST") != null){
		    	roleDetailList  = (List<GroupRole>) request.getSession().getAttribute("GROUP_ROLE_LIST");
		    }
		    
		    if(roleDetailList != null && roleDetailList.size() > 0){	
		    	/** Add value to Session **/
		    	for(int i=0;i<roleDetailList.size();i++){
		    		GroupRole roleItem = (GroupRole)roleDetailList.get(i);
		    		String[] userGroupIds = request.getParameterValues("userGroupId");
		    		String[] userGroupNames = request.getParameterValues("userGroupName");
		    		String[] roleIds = request.getParameterValues("roleId");
		    			
		    		roleItem.setUserGroupId(Utils.isNull(userGroupIds[roleItem.getIndex()-1]));
		    		roleItem.setUserGroupName(Utils.isNull(userGroupNames[roleItem.getIndex()-1]));
		    		roleItem.setRoleId(Utils.isNull(roleIds[roleItem.getIndex()-1]));
		    		
		    		roleDetailList.set(i, roleItem);
		    	}
		    	
		    	//add new Role
		    	GroupRole groupRoleItem = new GroupRole();
		    	groupRoleItem = formBean.getGroupRole();
		        
		    	GroupRole lastRoleRow = (GroupRole)roleDetailList.get(roleDetailList.size()-1);
		        groupRoleItem.setIndex(lastRoleRow.getIndex()+1);
		        logger.debug("index:"+groupRoleItem.getIndex());
		        roleDetailList.add(groupRoleItem);
		    }else{
		    	roleDetailList = new ArrayList<GroupRole>();
		    	GroupRole groupRoleItem = new GroupRole();
		    	groupRoleItem.setUserGroupId(formBean.getGroupRole().getUserGroupId());
		    	groupRoleItem.setIndex(1);	
			    roleDetailList.add(groupRoleItem);
		    }
		    request.getSession().setAttribute("GROUP_ROLE_LIST",roleDetailList);
		    
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("edit");
	}
	
	public ActionForward removeRowTable(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("removeRowTable");
		GroupRoleForm formBean = (GroupRoleForm) form;
		List<GroupRole> newRoleDetailList = new ArrayList<GroupRole>();
		try {	
			formBean.setMode("edit");
		    if(request.getSession().getAttribute("GROUP_ROLE_LIST") != null){
		    	String[] ids = formBean.getIds();
		    	logger.debug("ids:"+ids.length);
		    	List<GroupRole> roleDetailList = (List<GroupRole>) request.getSession().getAttribute("GROUP_ROLE_LIST");
		    	
		    	/** Add value to Session **/
		    	for(int i=0;i<roleDetailList.size();i++){
		    		GroupRole roleItem = (GroupRole)roleDetailList.get(i);
		    		String[] userGroupIds = request.getParameterValues("userGroupId");
		    		String[] userGroupNames = request.getParameterValues("userGroupName");
		    		String[] roleIds = request.getParameterValues("roleId");
		    		
		    		roleItem.setUserGroupId(Utils.isNull(userGroupIds[roleItem.getIndex()-1]));
		    		roleItem.setUserGroupName(Utils.isNull(userGroupNames[roleItem.getIndex()-1]));
		    		roleItem.setRoleId(Utils.isNull(roleIds[roleItem.getIndex()-1]));
		    		
		    		roleDetailList.set(i, roleItem);
		    	}
		    	
		    	int index =0;
		    	for(int i=0;i< roleDetailList.size();i++){
		    		GroupRole roleItem =(GroupRole)roleDetailList.get(i);
		    	    if( !isChkRemoveRow(ids ,roleItem.getIndex() ) ){
		    	    	index++;
		    	    	roleItem.setIndex(index);
		    	    	newRoleDetailList.add(roleItem);
		    	    } 
		    	}//for
		    }//if
		    request.getSession().setAttribute("GROUP_ROLE_LIST",newRoleDetailList);
		    
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", e.toString());
		}
		return mapping.findForward("edit");
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
		GroupRoleForm roleForm = (GroupRoleForm) form;
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
			request.getSession().removeAttribute("GROUP_ROLE_LIST");
			request.getSession().removeAttribute("ROLE_LIST");
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("mainpage");
	}
	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		GroupRoleForm roleForm = (GroupRoleForm) form;
		roleForm.setCriteria(new GroupRoleCriteria());
	}

}
