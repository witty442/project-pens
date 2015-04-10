package com.isecinc.pens.web.rt;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.RTBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.RTDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class RTAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	public static String STATUS_SAVE ="SV";
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				RTBean ad = new RTBean();
				ad.setNoPicRcv("true");
				ad.setCreateUser(user.getUserName());
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				RTBean cri  =aForm.getBeanCriteria();
				cri.setDocNo("");
				aForm.setBean(RTDAO.searchHead(cri,false));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			RTBean b = aForm.getBean();
			aForm.setBean(RTDAO.searchHead(aForm.getBean(),false));
			aForm.setResultsSearch(aForm.getBean().getItems());

			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "��辺������");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	

	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new RTBean());
			
			RTBean ad = new RTBean();
			ad.setCreateUser(user.getUserName());
			aForm.setBean(ad);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String docNo = Utils.isNull(request.getParameter("docNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare edit docNo:"+docNo +",mode:"+mode);
		
			RTBean c = new RTBean();
			c.setDocNo(docNo);
			if( !Utils.isNull(docNo).equals("")){
			  RTBean bean = RTDAO.searchHead(c,true).getItems().get(0);
			 
			  aForm.setBean(bean);
			}else{
			   c.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   c.setCanSave(true);
			   aForm.setBean(c);
			}
			
			aForm.setMode(mode);//Mode Edit
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RTForm summaryForm = (RTForm) form;
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
		RTForm orderForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			RTBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			//orderForm.setResults(aS.getItems());

			orderForm.setBean(aS);
			
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
		Connection conn = null;
		RTForm aForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			RTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(RTConstant.STATUS_OPEN);
			
			if( !Utils.isNull(h.getDocNo()).equals("")){
				RTDAO.updateRTNControlBySale(conn, h);
			}else{
				//Get DocNo
				h.setDocNo(RTDAO.genDocNo(new Date(),h.getCustGroup()));
				RTDAO.insertRTNControlBySale(conn, h);
			}
			
			//Search Again
			RTBean bean = RTDAO.searchHead(conn,h,true).getItems().get(0);
		
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "�ѹ�֡���������º��������");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","�������ö�ѹ�֡�������� \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
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
	
	public ActionForward cancelAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelAction");
		RTForm aForm = (RTForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			RTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(RTConstant.STATUS_CANCEL);
			RTDAO.updateStatusRTNControl(conn, h);
			
			//Search Again
			RTBean bean = RTDAO.searchHead(conn,h,true).getItems().get(0);
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "¡��ԡ��¡�����º��������");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward completeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("completeAction");
		RTForm aForm = (RTForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			RTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(RTConstant.STATUS_COMPLETE);
			RTDAO.updateStatusRTNControl(conn, h);
			
			//Search Again
			RTBean bean = RTDAO.searchHead(conn,h,true).getItems().get(0);
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "�ѹ�֡��¡�����º��������");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		RTForm aForm = (RTForm) form;
		try {
			aForm.setResults(new ArrayList<RTBean>());
			
			RTBean ad = new RTBean();
			//ad.setSaleDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			//ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward preparePic(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("preparePic");
		RTForm aForm = (RTForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String docNo = Utils.isNull(request.getParameter("docNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
		
			logger.debug("prepare PIC edit docNo:"+docNo +",mode:"+mode);
		
			RTBean c = new RTBean();
			c.setDocNo(docNo);
			if( !Utils.isNull(docNo).equals("")){
			  RTBean bean = RTDAO.searchHead(c,true).getItems().get(0);
			  if(bean.getStatus().equals(RTConstant.STATUS_COMPLETE)){
				  bean.setCanPicSave(true);
			  }
			  aForm.setBean(bean);
			}
			
			aForm.setMode(mode);//Mode Edit
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detailPic");
	}
	
	public ActionForward savePic(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("savePic");
		RTForm aForm = (RTForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			RTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			h.setStatus(RTConstant.STATUS_RECEIVED);
			
			RTDAO.updateRTNControlByPic(conn, h);
			
			//Search Again
			RTBean bean = RTDAO.searchHead(conn,h,true).getItems().get(0);
			if(bean.getStatus().equals(RTConstant.STATUS_COMPLETE)){
				 bean.setCanSave(true);
			}
		    aForm.setBean(bean);
			
			conn.commit();
			request.setAttribute("Message", "�ѹ�֡��¡�����º��������");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detailPic");
	}
	
	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report ");
		RTForm reportForm = (RTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		 
		try {
		
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {
				
			}
		}
		// return null;
		return null;
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
