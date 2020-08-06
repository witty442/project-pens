package com.isecinc.pens.web.buds.page;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.buds.BudsAllBean;
import com.isecinc.pens.web.buds.BudsAllForm;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class OrderEDIAction extends I_Action {

	
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "pickingAll";
		BudsAllForm budsAllForm = (BudsAllForm) form;
		BudsAllBean bean = new BudsAllBean();
		Connection conn = null;
		List<PopupBean> dataList = null;
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			 conn = DBConnectionApps.getInstance().getConnection();
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				//LOAD_NO_LIST
				dataList = new ArrayList<PopupBean>();
				dataList.add(new PopupBean("LOAD_NO_1","1"));
				dataList.add(new PopupBean("LOAD_NO_1","2"));
				request.getSession().setAttribute("LOAD_NO_LIST",dataList);
			
				bean.setConfPickingBean(new ConfPickingBean());
				budsAllForm.setBean(bean);
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			conn.close();
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "reportAll";
	}

	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			//PickingAllForm.getBean().getConfPickingBean().setItemsList(null);
			
			ConfPickingBean confPickingBean = null;//ConfPickingDAO.searchPickingDetail(request.getContextPath(), pickingAllForm.getBean().getConfPickingBean(), false, user);
			if(confPickingBean != null && confPickingBean.getDataStrBuffer() != null){
				budsAllForm.getBean().setConfPickingBean(confPickingBean);
			    request.setAttribute("pickingAllForm_RESULTS", confPickingBean.getDataStrBuffer());
			    confPickingBean.setDataStrBuffer(null);//clear memory
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "pickingAll";
	}
	public ActionForward confirmPicking(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmPicking");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			ConfPickingDAO.confirmPicking(budsAllForm.getBean().getConfPickingBean(), user);
			request.setAttribute("Message", "Confirm Picking เรียบร้อยแล้ว");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("pickingAll");
	}
	
	public ActionForward rejectPicking(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("rejectPicking");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			ConfPickingDAO.rejectPicking(budsAllForm.getBean().getConfPickingBean(), user);
			request.setAttribute("Message", "Reject Picking เรียบร้อยแล้ว");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("pickingAll");
	}
	
	/*public ActionForward finishPicking(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("finishPicking");
		PickingAllForm pickingAllForm = (PickingAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			ConfPickingDAO.finishPicking(pickingAllForm.getBean().getConfPickingBean(), user);
			request.setAttribute("Message", "Finish Picking เรียบร้อยแล้ว");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("pickingAll");
	}*/
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		BudsAllForm BudsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//set for display by page
			/*request.getSession().setAttribute("summary" ,null);
			PickingAllForm.getBean().getProjectCBean().setDataStrBuffer(null);
			PickingAllForm.getBean().getProjectCBean().setItemsList(null);
			
			ConfPickingBean ConfPickingBean = searchReportModel(request.getContextPath(), PickingAllForm.getBean().getProjectCBean(), true, user);
			if(ConfPickingBean != null && ConfPickingBean.getDataStrBuffer() != null){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(ConfPickingBean.getDataStrBuffer().toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}*/
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("pickingAll");
	}
		
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			 
		} catch (Exception e) {
			request.setAttribute("Message",e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
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
