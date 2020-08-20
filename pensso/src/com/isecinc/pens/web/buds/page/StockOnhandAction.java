package com.isecinc.pens.web.buds.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.StockOnhandBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.buds.BudsAllBean;
import com.isecinc.pens.web.buds.BudsAllForm;
import com.pens.util.BeanParameter;
import com.pens.util.DBConnectionApps;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockOnhandAction extends I_Action {

	public static int pageSize = 60;
	
	public ActionForward prepareSearchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		BudsAllBean bean = new BudsAllBean();
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				bean.setStockOnhandBean(new StockOnhandBean());
				aForm.setBean(bean);
			 }
			 aForm.setPageName(Utils.isNull(request.getParameter("pageName")));
			 aForm.setSubPageName(Utils.isNull(request.getParameter("subPageName")));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean excel = false;
		Connection conn = null;
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "budsAll";
		BudsAllForm aForm = (BudsAllForm) form;
		BudsAllBean bean = new BudsAllBean();
		Connection conn = null;
		try {
			 
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
		return "";
	}

	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		boolean excel = false;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			conn = DBConnectionApps.getInstance().getConnection();
			
		    StockOnhandBean stockOnhandBean = StockOnhandDAO.searchStockOnhandReport(conn,aForm.getBean().getStockOnhandBean(),excel);
		   
		    if(stockOnhandBean != null && stockOnhandBean.getDataStrBuffer() != null){
			    request.setAttribute("budsAllForm_RESULTS", stockOnhandBean.getDataStrBuffer());
			    stockOnhandBean.setDataStrBuffer(null);//clear memory
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
				stockOnhandBean.setDataStrBuffer(null);//clear memory
			}
			aForm.getBean().setStockOnhandBean(stockOnhandBean);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			conn.close();
		}
		return "budsAll";
	}
	
	public ActionForward exportStockOnhandReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportStockOnhandReport");
		BudsAllForm aForm = (BudsAllForm) form;
		Connection conn = null;
		boolean excel = true;
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			
			aForm.getBean().getStockOnhandBean().setDataStrBuffer(null);
		    StockOnhandBean stockOnhandBean = StockOnhandDAO.searchStockOnhandReport(conn,aForm.getBean().getStockOnhandBean(),excel);
	
			if(stockOnhandBean != null && stockOnhandBean.getDataStrBuffer() != null){

				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(stockOnhandBean.getDataStrBuffer().toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    stockOnhandBean.setDataStrBuffer(null);//clear memory
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
				stockOnhandBean.setDataStrBuffer(null);//clear memory
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return mapping.findForward("budsAll");
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
