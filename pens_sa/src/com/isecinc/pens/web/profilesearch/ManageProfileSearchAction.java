package com.isecinc.pens.web.profilesearch;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.report.analyst.bean.ABean;
import com.isecinc.pens.web.report.analyst.process.AInitial;
import com.isecinc.pens.web.report.analyst.process.ProfileProcess;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * User Action Class
 * 
 * @author Witty
 * 
 */
public class ManageProfileSearchAction extends I_Action {


	
	/**
	 * Prepare
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ManageProfileSearchForm aForm = (ManageProfileSearchForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			if( "new".equalsIgnoreCase(Utils.isNull(request.getParameter("action")))){
			   logger.debug("Prepare new");
			   conn = DBConnection.getInstance().getConnectionApps();
			   String reportName = Utils.isNull(request.getParameter("reportName"));
			   
			   String profileId = Utils.isNull(request.getParameter("profileId"));
			   ManageProfileSearchBean bean = new ManageProfileSearchBean();
			   bean.setProfileId(Utils.convertToInt(profileId));
			   bean.setProfileName(ProfileProcess.getProfileName(conn, user.getId(), Utils.convertToInt(profileId),reportName));
			   aForm.setBean(bean);
			}

		} catch (Exception e) {
			request.setAttribute("Message",  e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return "search";
	}

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ManageProfileSearchForm aForm = (ManageProfileSearchForm) form;
		try {
			
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
		ManageProfileSearchForm roleForm = (ManageProfileSearchForm) form;
		try {
           // request.getSession().setAttribute("ROLE_LIST",MRole.findRoleList(roleForm.getRole()));
            
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
		ManageProfileSearchForm aForm = (ManageProfileSearchForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String action = Utils.isNull(request.getParameter("action"));
		boolean status = true;
		try {
			conn = DBConnection.getInstance().getConnection();
			// Begin Transaction
			conn.setAutoCommit(false);
			
			String reportName = Utils.isNull(request.getParameter("reportName"));
			if("save".equalsIgnoreCase(action)){
				//update 
				aForm.getBean().setUserId(user.getId());
				aForm.getBean().setReportName(reportName);
			    status = ProfileProcess.updateProfileNameModel(conn, user, aForm.getBean());
			    if(status ==false){
			    	//insert new
					ABean saBean = new ABean();
					saBean.setUserId(user.getId()+"");
					saBean.setProfileName(aForm.getBean().getProfileName());
					saBean.setReportName(reportName);
					//getMax ProfileID by user ID
					int nextProfileId = ProfileProcess.getMaxProfileId(conn, user.getId(),reportName)+1;
					saBean.setProfileId(nextProfileId+"");
					
					status = ProfileProcess.insertProfileBlankModel(conn, user, saBean);
					
					//set display
					aForm.getBean().setUserId(user.getId());
					aForm.getBean().setProfileId(Utils.convertStrToInt(saBean.getProfileId()));
					aForm.getBean().setProfileName(saBean.getProfileName());
					aForm.getBean().setReportName(saBean.getReportName());
			    }
			}else{
				//insert new
				ABean saBean = new ABean();
				saBean.setUserId(user.getId()+"");
				saBean.setProfileName(aForm.getBean().getProfileName());
				saBean.setReportName(reportName);
				//getMax ProfileID by user ID
				int nextProfileId = ProfileProcess.getMaxProfileId(conn, user.getId(),reportName)+1;
				saBean.setProfileId(nextProfileId+"");
				
				status = ProfileProcess.insertProfileBlankModel(conn, user, saBean);
				
				//set display
				aForm.getBean().setUserId(user.getId());
				aForm.getBean().setProfileId(Utils.convertStrToInt(saBean.getProfileId()));
				aForm.getBean().setProfileName(saBean.getProfileName());
				aForm.getBean().setReportName(saBean.getReportName());
			}
			logger.debug("status:"+status);
			if(status){
				/** init profileList new update profile **/
				List<References> profileList = new ArrayList<References>();
				References r = new References("0","ไม่เลือก");
				profileList.add(r);
				//new Code
				profileList.addAll(AInitial.initProfileList(conn, user.getId(),reportName));
				request.getSession(true).setAttribute("profileList", profileList);
				
				request.setAttribute("save_success", "save_success");
			    request.setAttribute("Message", "บันทึกข้อมูลรูปแแบบการค้นหา  Profile "+aForm.getBean().getProfileId() +"เรียบร้อยแล้ว");
			}else{
			    request.setAttribute("Message", "ไม่สามารถ บันทึกข้อมูลรูปแแบบการค้นหา  Profile "+aForm.getBean().getProfileId() +"โปรดตรวจสอบข้อมูล");
			}
			
			conn.commit();
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
		return "search";
	}
	
	/**
	 * Change Active
	 */
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Connection conn = null;
		try {
			
		} catch (Exception e) {
			try {
			
			} catch (Exception ex) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			throw e;
		} finally {
			conn.close();
		}
		return "re-search";
	}
	

	
	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		ManageProfileSearchForm roleForm = (ManageProfileSearchForm) form;
		roleForm.setCriteria(new MangeProfileSearchCriteria());
	}

}
