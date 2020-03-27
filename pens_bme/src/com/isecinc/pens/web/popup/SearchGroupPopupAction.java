package com.isecinc.pens.web.popup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SearchGroupPopupAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		PopupForm popupForm = (PopupForm) form;
		try {
			 logger.debug("prepare action:"+request.getParameter("action"));
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.setAttribute("results", null);
				 popupForm.setCode("");
				 popupForm.setDesc("");
				 popupForm.setCodeSearch("");
				 popupForm.setDescSearch("");
				 popupForm.setNo(0);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
			 }
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PopupForm popupForm = (PopupForm) form;
		try {
			logger.debug("prepare 2 action:"+request.getParameter("action"));
			if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.setAttribute("results", null);
				 popupForm.setCode("");
				 popupForm.setDesc("");
				 popupForm.setNo(0);
				 
				 request.getSession().setAttribute("codes", null);
				 request.getSession().setAttribute("keys", null);
				 request.getSession().setAttribute("descs", null);
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PopupForm popupForm = (PopupForm) form;
		List<PopupForm> results = null;
		try {
			String storeType = Utils.isNull(request.getParameter("storeType"));
			String pageName = Utils.isNull(request.getParameter("pageName"));
			logger.debug("storeType:"+storeType);
			
			if("lockItemOrder".equalsIgnoreCase(pageName)){
				results = searchGroupCodeListByRefCode(popupForm,"LotusItem");
			}else{
				 if("tops".equalsIgnoreCase(storeType)){
					 results = searchGroupCodeListTypeTops(popupForm);
				 }else{
				     results = searchGroupCodeList(popupForm);
				 }
			}
			 if(results != null && results.size() >0){
				 request.setAttribute("GROUP_LIST", results);
			 }else{
				 request.setAttribute("Message", "ไม่พบข่อมูล");
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "search";
	}
	
  public static List<PopupForm> searchGroupCodeList(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<PopupForm> pos = new ArrayList<PopupForm>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			sql.append("  SELECT distinct material_master from pensbme_style_mapping where 1=1 \n");

			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append(" and material_master LIKE '%"+c.getCodeSearch()+"%' \n");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append(" and material_master LIKE '%"+c.getDescSearch()+"%' \n");
			}
			
			sql.append("  ORDER BY material_master asc \n");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			while (rst.next()) {
				PopupForm item = new PopupForm();
				no++;
				item.setNo(no);
				item.setCode(rst.getString("material_master"));
				item.setDesc(rst.getString("material_master"));
				pos.add(item);
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return pos;
	}

  public static List<PopupForm> searchGroupCodeListTypeTops(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<PopupForm> pos = new ArrayList<PopupForm>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			sql.append("  SELECT pens_desc2 from PENSBI.PENSBME_MST_REFERENCE WHERE reference_code = 'TOPSitem' \n");

			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append(" and pens_desc2 LIKE '%"+c.getCodeSearch()+"%' \n");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append(" and pens_desc2 LIKE '%"+c.getDescSearch()+"%' \n");
			}
			
			sql.append("  ORDER BY pens_desc2 asc \n");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			while (rst.next()) {
				PopupForm item = new PopupForm();
				no++;
				item.setNo(no);
				item.setCode(rst.getString("pens_desc2"));
				item.setDesc(rst.getString("pens_desc2"));
				pos.add(item);
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return pos;
	}
	
  public static List<PopupForm> searchGroupCodeListByRefCode(PopupForm c,String refCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<PopupForm> pos = new ArrayList<PopupForm>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			sql.append("  SELECT distinct pens_desc2 from PENSBI.PENSBME_MST_REFERENCE");
			sql.append("  WHERE reference_code = '"+refCode+"' \n");

			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append(" and pens_desc2 LIKE '%"+c.getCodeSearch()+"%' \n");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append(" and pens_desc2 LIKE '%"+c.getDescSearch()+"%' \n");
			}
			
			sql.append("  ORDER BY pens_desc2 asc \n");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			while (rst.next()) {
				PopupForm item = new PopupForm();
				no++;
				item.setNo(no);
				item.setCode(rst.getString("pens_desc2"));
				item.setDesc(rst.getString("pens_desc2"));
				pos.add(item);
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return pos;
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//Connection conn = null;
		//SummaryForm summaryForm = (SummaryForm) form;
		try {
			 
		} catch (Exception e) {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PopupForm summaryForm = (PopupForm) form;
		try {
			 request.getSession().setAttribute("results", null);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
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
