package com.isecinc.pens.web.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.Constants;
import com.pens.util.Utils;

public class InterfacesSearchManager  {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Interfaces Search Current Action");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		InterfaceDAO dao = new InterfaceDAO();
		try {
			String timeInUse =interfacesForm.getMonitorBean().getTimeInUse();
			logger.info("TimeInUse:"+timeInUse);
			logger.info("pageName:"+Utils.isNull(request.getParameter("pageName")));
			
			/*
			InterfacesCriteria criteria = getSearchCriteria(request, interfacesForm.getCriteria(), this.getClass().toString());
			if(request.getAttribute("searchKey") != null){
				criteria.setSearchKey((String)request.getAttribute("searchKey"));
			}
			interfacesForm.setCriteria(criteria);*/
			
			if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_HISHER)){
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_GEN_HISHER);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
				
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BILL_ICC)){
				
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_IMPORT_BILL_ICC);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					
					//Search interfaceResult (monitorItem)
					interfacesForm.setMonitorItemList(dao.findMonitorItemList(user,results[0]));
				
				} else {
					request.setAttribute("Message", "Data not found");
				}
             }else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_WACOAL_STOCK)){
				
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_IMPORT_WACOAL_STOCK);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					
					//Search interfaceResult (monitorItem)
					interfacesForm.setMonitorItemList(dao.findMonitorItemList(user,results[0]));
					
					//Search MonitorItemBeanResult (monitorItem)
					interfacesForm.setMonitorItemBeanResult(dao.findMonitorItemBean(user,results[0]));
				
				} else {
					request.setAttribute("Message", "Data not found");
				}
             }else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN)){
 				
 				/** Set Condition Search **/
 				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN);
 				
 				if (results != null && results.length > 0) {
 					interfacesForm.setResults(results);
 					
 					//Search interfaceResult (monitorItem)
 					interfacesForm.setMonitorItemList(dao.findMonitorItemList(user,results[0],Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN));
 					
 				} else {
 					request.setAttribute("Message", "Data not found");
 				}
 				
			}else 	if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BMESCAN)){
				
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorList(user,Constants.TYPE_IMPORT);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_EXPORT_BILL_ICC)){
				
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_EXPORT_BILL_ICC);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					
					//Search interfaceResult (monitorItem)
					interfacesForm.setMonitorItemList(dao.findMonitorItemList(user,results[0]));
				
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ORDER_EXCEL)){

					/** Set Condition Search **/
					MonitorBean[] results = dao.findMonitorListNew(user, Constants.TYPE_GEN_ORDER_EXCEL);
					
					if (results != null && results.length > 0) {
						interfacesForm.setResults(results);
						
						//Search interfaceResult (monitorItem)
						MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
						interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					} else {
						request.setAttribute("Message", "Data not found");
					}
					
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ITEM_MASTER_HISHER)){
				
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_GEN_ITEM_MASTER_HISHER);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS)){
				
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					//logger.debug("")
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					
					request.setAttribute("Message", results[0].getErrorMsg());
				} else {
					request.setAttribute("Message", "Data not found");
				}
	
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS)){
			
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user, Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					//logger.debug("")
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					
					request.setAttribute("Message", results[0].getErrorMsg());
				} else {
					request.setAttribute("Message", "Data not found");
				}
             }else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_SALEOUT_WACOAL)){
				
				/** Set Condition Search **/
				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_IMPORT_SALEOUT_WACOAL);
				
				if (results != null && results.length > 0) {
					interfacesForm.setResults(results);
					
					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					//logger.debug("")
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					
					request.setAttribute("Message", results[0].getErrorMsg());
				} else {
					request.setAttribute("Message", "Data not found");
				}
				
             }else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_POS)){
 				
            	 /** Set Condition Search **/
 				MonitorBean[] results = dao.findMonitorListNew(user,Constants.TYPE_IMPORT_POS);
 				
 				if (results != null && results.length > 0) {
 					interfacesForm.setResults(results);
 					
 					//Search interfaceResult (monitorItem)
					MonitorItemBean monitorItemBeanResult = dao.findMonitorItemBean(user,results[0]);
					//logger.debug("")
					interfacesForm.setMonitorItemBeanResult(monitorItemBeanResult);
					
 				} else {
 					request.setAttribute("Message", "Data not found");
 				}
              }
			interfacesForm.getMonitorBean().setTimeInUse(timeInUse);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}
}
