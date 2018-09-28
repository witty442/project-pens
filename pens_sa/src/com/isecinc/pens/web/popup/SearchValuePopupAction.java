package com.isecinc.pens.web.popup;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.salesanalyst.ConditionFilterBean;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.report.salesanalyst.SAInitial;


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
		Connection conn = null;
		try {
			request.getSession().setAttribute("code_session", "");
			request.getSession().setAttribute("desc_session", "");
			request.getSession().setAttribute("RESULT",null);
			formBean.setSalesBean(new SABean());
	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return returnText;
	}
	
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("SearchValuePopupAction Prepare Form 2");
		SearchValuePopupForm formBean = (SearchValuePopupForm) form;
		String returnText = "prepare";
		try {
			request.getSession().setAttribute("code_session", "");
			request.getSession().setAttribute("desc_session", "");
			
			 //DISPLAY Navigation
			 String currCondNo = Utils.isNull(request.getParameter("currCondNo"));
			 String currCondTypeValue = Utils.isNull(request.getParameter("currCondTypeValue"));
			 String currCondNameText = Utils.isNull((String)SAInitial.getInstance().GROUP_BY_MAP.get(request.getParameter("currCondTypeValue")));
			 String load = Utils.isNull(request.getParameter("load"));
			 
			 //set Filter Cond
			 String condType1 = Utils.isNull(request.getParameter("condType1"));
			 String condCode1 = Utils.isNull(request.getParameter("condCode1"));
			 String condValueDisp1 = Utils.isNull(request.getParameter("condValueDisp1"));
			 
			 if("1".equals(load)){
			   //condCode1 = new String(condCode1.getBytes("ISO8859_1"), "UTF-8");
			   //condValueDisp1 = new String(condValueDisp1.getBytes("ISO8859_1"), "UTF-8");
			 }
			 
			 String condType2 = Utils.isNull(request.getParameter("condType2"));
			 String condCode2 = Utils.isNull(request.getParameter("condCode2"));
			 String condValueDisp2 = Utils.isNull(request.getParameter("condValueDisp2"));
			 if("1".equals(load)){
			    //condCode2 = new String(condCode2.getBytes("ISO8859_1"), "UTF-8");
			    //condValueDisp2 = new String(condValueDisp2.getBytes("ISO8859_1"), "UTF-8");
			 }
			 
			 String condType3 = Utils.isNull(request.getParameter("condType3"));
			 String condCode3 = Utils.isNull(request.getParameter("condCode3"));
			 String condValueDisp3 = Utils.isNull(request.getParameter("condValueDisp3"));
			 if("1".equals(load)){
			   //condCode3 = new String(condCode3.getBytes("ISO8859_1"), "UTF-8");
			   //condValueDisp3 = new String(condValueDisp3.getBytes("ISO8859_1"), "UTF-8");
			 }
			 
			//Display Navigation
			 String curNavigation =Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue));  
			 String navigation ="";
			 
			 if(currCondNo.equals("1")){
			 	 navigation += ""+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue));  
			 }else if(currCondNo.equals("2")){
				 
				 String cDisp1 = formBean.getFilterBean()!=null?formBean.getFilterBean().getCondValueDisp1():condValueDisp1;
				 logger.debug("formBean.getFilterBean().getCondCode1():"+formBean.getFilterBean().getCondCode1());
				 logger.debug("formBean.getFilterBean().getCondValueDisp1():"+formBean.getFilterBean().getCondValueDisp1());
				 
			 	 //Cond 1
			 	 navigation += ""+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))+"["+cDisp1+"]" +"<br>";
			 	 //CurrCond
			 	 navigation += "#"+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue)) +"<br>"; 
			 	
			 }else if(currCondNo.equals("3")){
			 	//Cond 1
			 	 navigation += ""+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))+"["+condValueDisp1+"]" +"<br>";
			 	//Cond 2
			 	 navigation += "#"+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType2))+"["+condValueDisp2+"]" +"<br>";
			 	 //CurrCond
			 	 navigation += "#"+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue)) +"<br>"; 
			 }else if(currCondNo.equals("4")){
			 	//Cond 1
			 	 navigation += ""+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType1))+"["+condValueDisp1+"]" +"<br>";
			 	//Cond 2
			 	 navigation += "#"+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType2))+"["+condValueDisp2+"]" +"<br>";
			 	//Cond 3
			 	 navigation += "#"+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(condType3))+"["+condValueDisp3+"]" +"<br>";
			 	 //curCond
			 	 navigation += "#"+Utils.isNull((String)SAInitial.GROUP_BY_MAP.get(currCondTypeValue))+"<br>"; 
			 }
			 
	        //set Navigation
			formBean.setCurNavigation(curNavigation);
			formBean.setNavigation(navigation);
			 
			 //set action  
			 String action =  request.getParameter("action");
			 if(action != null){
				 request.getSession().setAttribute("VALUE_LIST",null);
			 }
			 
			 //Clear bean
			 ConditionFilterBean filterBean = new ConditionFilterBean();
			 filterBean.setCondType1(condType1);
			 filterBean.setCondCode1(condCode1);
			 filterBean.setCondValueDisp1(condValueDisp1);
			 
			 formBean.setFilterBean(filterBean);
			 
			 formBean.setSalesBean(new SABean());
			 
             //Clear parameters
			 request.getSession().setAttribute("codes", null);
			 request.getSession().setAttribute("keys", null);
			 request.getSession().setAttribute("descs", null);
			 
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
		String desc = forms.getSalesBean().getDesc();
		try {
			 String currCondType = Utils.isNull(request.getParameter("currCondType"));
			 String currCondNo = Utils.isNull(request.getParameter("currCondNo"));			 
			
			logger.debug("currCondNo:"+currCondNo+",currCondType:"+currCondType);
			
			/** Check Next page no query **/
			 String queryStr= request.getQueryString();
			 if(queryStr.indexOf("d-") != -1){
			 	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
			 	System.out.println("queryStr:"+queryStr);
			 }
			 
			//Case link page in display no search again
			logger.debug("currentPage:"+request.getParameter(queryStr));
			if(request.getParameter(queryStr) != null){
				logger.debug("No Query");
			}else{
				//Set old criteria data to session
				request.getSession().setAttribute("code_session", forms.getSalesBean().getCode());
				request.getSession().setAttribute("desc_session", forms.getSalesBean().getDesc());
				
				if (currCondNo.equalsIgnoreCase("2") || currCondNo.equalsIgnoreCase("3") || currCondNo.equalsIgnoreCase("4")){
				
					ConditionFilterBean filterBean = forms.getFilterBean();
					filterBean.setCurrCondNo(currCondNo);
					filterBean.setCurrCondType(currCondType);
					
					logger.debug("condType1:"+filterBean.getCondType1()+",condCode1:"+filterBean.getCondCode1());
					logger.debug("condType2:"+filterBean.getCondType2()+",condCode2:"+filterBean.getCondCode2());
					logger.debug("condType3:"+filterBean.getCondType3()+",condCode3:"+filterBean.getCondCode3());
					
				    request.getSession().setAttribute("VALUE_LIST", SAInitial.getInstance().getConditionValueListByParent(user,currCondType,forms.getSalesBean().getCode(),desc,filterBean));
				}else{
					request.getSession().setAttribute("VALUE_LIST", SAInitial.getInstance().getConditionValueList(request,currCondType,forms.getSalesBean().getCode(),desc));	
				}
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
