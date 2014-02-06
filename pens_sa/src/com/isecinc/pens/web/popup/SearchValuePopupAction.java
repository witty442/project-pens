package com.isecinc.pens.web.popup;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.salesanalyst.ConditionFilterBean;
import com.isecinc.pens.report.salesanalyst.DisplayBean;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.report.salesanalyst.SAProcess;
import com.isecinc.pens.report.salesanalyst.helper.Utils;


/**
  WITTY
  SalesAnalystReportAction
 * 
 */
public class SearchValuePopupAction extends I_Action {
	protected Logger logger = Logger.getLogger("PENS");
	
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SearchValuePopupAction Prepare Form 1");
		SearchValuePopupForm formBean = (SearchValuePopupForm) form;
		String returnText = "prepare";
		try {
			request.getSession().setAttribute("RESULT",null);
			formBean.setSalesBean(new SABean());
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		return returnText;
	}
	
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SearchValuePopupAction Prepare Form 2");
		SearchValuePopupForm formBean = (SearchValuePopupForm) form;
		String returnText = "prepare";
		try {
			 String condNo = request.getParameter("condNo");
			 String condNameValue = request.getParameter("condNameValue");
			 String condNameText = request.getParameter("condNameText");
			
			 request.getSession().setAttribute("codes", null);
			 request.getSession().setAttribute("keys", null);
			 request.getSession().setAttribute("descs", null);
			 
			 String action =  request.getParameter("action");
			 if(action != null){
				 request.getSession().setAttribute("VALUE_LIST",null);
			 }
			 System.out.println("condNameText:"+condNameText);
			 
			 formBean.setSalesBean(new SABean());

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			throw e;
		}
		
		return returnText;
	}
	
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("SearchValuePopupAction Search Current Action");
		SearchValuePopupForm forms = (SearchValuePopupForm) form;
		String returnText = "search";
		User user = (User) request.getSession().getAttribute("user");
		try {
			String currCondType = Utils.isNull(request.getParameter("currCondType"));
			String currCondNo = Utils.isNull(request.getParameter("currCondNo"));
			
			/*request.getSession().setAttribute("codes", null);
			request.getSession().setAttribute("keys", null);
			request.getSession().setAttribute("descs", null);*/
			 
			logger.debug("currCondNo:"+currCondNo+",currCondNo:"+currCondNo);
			
			if (currCondNo.equalsIgnoreCase("2") || currCondNo.equalsIgnoreCase("3") || currCondNo.equalsIgnoreCase("4")){
				String condType1 = Utils.isNull(request.getParameter("condType1"));
				String condCode1 = Utils.isNull(request.getParameter("condCode1"));
				
				String condType2 = Utils.isNull(request.getParameter("condType2"));
				String condCode2 = Utils.isNull(request.getParameter("condCode2"));
				
				String condType3 = Utils.isNull(request.getParameter("condType3"));
				String condCode3 = Utils.isNull(request.getParameter("condCode3"));
				
				ConditionFilterBean filterBean = new ConditionFilterBean();
				filterBean.setCurrCondNo(currCondNo);
				filterBean.setCurrCondType(currCondType);
				filterBean.setCondType1(condType1);
				filterBean.setCondCode1(condCode1);
				
				filterBean.setCondType2(condType2);
				filterBean.setCondCode2(condCode2);
				
				filterBean.setCondType3(condType3);
				filterBean.setCondCode3(condCode3);
				
				logger.debug("condType1:"+condType1+",condCode1:"+condCode1);
				logger.debug("condType2:"+condType2+",condCode2:"+condCode2);
				logger.debug("condType3:"+condType3+",condCode3:"+condCode3);
				
			    request.getSession().setAttribute("VALUE_LIST", SAProcess.getInstance().getConditionValueListByParent(user,currCondType,forms.getSalesBean().getCode(),forms.getSalesBean().getDesc(),filterBean));
			}else{
				request.getSession().setAttribute("VALUE_LIST", SAProcess.getInstance().getConditionValueList(request,currCondType,forms.getSalesBean().getCode(),forms.getSalesBean().getDesc()));	
			}
			
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}
	
	/**
	 * Set New Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		
	}
	
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;

		try {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			request.setAttribute("type", SystemElements.ADMIN);
			
			
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "re-search";
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
