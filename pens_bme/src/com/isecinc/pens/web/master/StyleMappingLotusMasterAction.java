package com.isecinc.pens.web.master;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.StyleMappingLotusMasterBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.StyleMappingLotusMasterDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StyleMappingLotusMasterAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		StyleMappingLotusMasterForm aForm = (StyleMappingLotusMasterForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equalsIgnoreCase(action)){
				aForm.setBean(new StyleMappingLotusMasterBean());
				aForm.setBeanOLD(null);
				aForm.setResults(null);
			}else if("back".equalsIgnoreCase(action)){
				aForm.setResults(StyleMappingLotusMasterDAO.searchStyleMappingLotusMaster(aForm.getBeanCriteria()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StyleMappingLotusMasterForm summaryForm = (StyleMappingLotusMasterForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StyleMappingLotusMasterForm aForm = (StyleMappingLotusMasterForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			List<StyleMappingLotusMasterBean> results = StyleMappingLotusMasterDAO.searchStyleMappingLotusMaster(aForm.getBean());
			if(results != null && results.size() >0){
				aForm.setResults(results);
				logger.debug("resutls:"+aForm.getResults().size());
			}else{
				aForm.setResults(null);
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			aForm.setBeanCriteria(aForm.getBean());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "search";
	}
	
	public ActionForward searchDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchDetail");
		StyleMappingLotusMasterForm aForm = (StyleMappingLotusMasterForm) form;
		try {	
			StyleMappingLotusMasterBean cri = new StyleMappingLotusMasterBean();
			cri.setStyleNo(Utils.isNull(request.getParameter("style")));
			cri.setGroupCode(Utils.isNull(request.getParameter("groupCode")));
			cri.setPensItem(Utils.isNull(request.getParameter("pensItem")));
			String mode =Utils.isNull(request.getParameter("mode"));
			
			if("edit".equalsIgnoreCase(mode)){
			   List<StyleMappingLotusMasterBean> results = StyleMappingLotusMasterDAO.searchStyleMappingLotusMaster(cri);
			   aForm.setBean(results.get(0));
			   
			   StyleMappingLotusMasterBean key = new StyleMappingLotusMasterBean();
			   key.setStyleNo(results.get(0).getStyleNo());
			   key.setGroupCode(results.get(0).getGroupCode());
			   key.setPensItem(results.get(0).getPensItem());
			   aForm.setBeanOLD(key);//set for update

			}else{
			   cri = new StyleMappingLotusMasterBean();
			   
			   aForm.setBean(cri);
			   aForm.setMode(mode);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		StyleMappingLotusMasterForm aForm = (StyleMappingLotusMasterForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		boolean foundError = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//logger.debug("1 OLD GROUP_CODE:"+Utils.isNull(aForm.getBeanOLD().getGroupCode()));
			
			StyleMappingLotusMasterBean data = aForm.getBean();
			data.setCreateUser(user.getUserName());
			data.setUpdateUser(user.getUserName());

			logger.debug("mode:"+aForm.getMode());
			if(foundError==false){
				//save
				if("add".equalsIgnoreCase(aForm.getMode())){ 
					//validate isExist
					StyleMappingLotusMasterBean oldBean = StyleMappingLotusMasterDAO.isExist(conn,data);
					if(oldBean == null){
					   //insert 
						StyleMappingLotusMasterDAO.insertNew(conn, data);
						request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว");
					}else{
					   //update 
						//StyleMappingLotusMasterDAO.update(conn,oldBean, data);
						request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ เนื่องจากมีข้อมูลนี้ในระบบแล้ว");
					}
				}else{
					//PriceListMasterBean oldBean = PriceListMasterDAO.isExist(conn,data);
					logger.debug("OLD GROUP_CODE:"+Utils.isNull(aForm.getBeanOLD().getGroupCode()));
					
					//update 
					StyleMappingLotusMasterDAO.update(conn,aForm.getBeanOLD(), data);
					request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว");
				}
				conn.commit();
			}else{
				conn.rollback();
				request.setAttribute("Message",msg);
			}
		} catch (Exception e) {
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return "detail";
		} finally {
			try {
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportToExcel");
		StyleMappingLotusMasterForm aForm = (StyleMappingLotusMasterForm) form;
		StringBuffer dataStr = new StringBuffer();
		try {
			//Get Data
			List<StyleMappingLotusMasterBean> results = StyleMappingLotusMasterDAO.searchStyleMappingLotusMaster(aForm.getBean());
			if(results != null && results.size() >0){
				dataStr.append("<table border='1'>");
				dataStr.append("<tr>");
				dataStr.append("<th>Group Code</th>");
				dataStr.append("<th>Article</th>");
				dataStr.append("<th>Pens Item</th>");
				dataStr.append("</tr>");
				for(int i=0;i<results.size();i++){
					StyleMappingLotusMasterBean bean = results.get(i);
					dataStr.append("<tr>");
					dataStr.append("<td>"+Utils.isNull(bean.getGroupCode())+"</td>");
					dataStr.append("<td>"+Utils.isNull(bean.getStyleNo())+"</td>");
					dataStr.append("<td>"+Utils.isNull(bean.getPensItem())+"</td>");
					dataStr.append("</tr>");
				}
				dataStr.append("</table>");
			
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+"data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(dataStr.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	public ActionForward clearSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		StyleMappingLotusMasterForm aForm = (StyleMappingLotusMasterForm) form;
		try {
			StyleMappingLotusMasterBean cri = new StyleMappingLotusMasterBean();
			  aForm.setResults(null);
			  aForm.setBeanOLD(null);
			  aForm.setBean(cri);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		StyleMappingLotusMasterForm aForm = (StyleMappingLotusMasterForm) form;
		try {
			StyleMappingLotusMasterBean cri = new StyleMappingLotusMasterBean();
			  aForm.setBeanOLD(null);
			  aForm.setBean(cri);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
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
