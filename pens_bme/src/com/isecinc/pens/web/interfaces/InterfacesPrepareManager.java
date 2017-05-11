package com.isecinc.pens.web.interfaces;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.bean.InterfaceBean;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

public class InterfacesPrepareManager {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Interfaces Prepare Form without ID");
		InterfacesForm interfacesForm = (InterfacesForm) form;
		String returnText = "prepare";
		InterfaceDAO dao = new InterfaceDAO();
		try {
			logger.debug("pageName:"+Utils.isNull(request.getParameter("pageName")) +",pageAction:"+Utils.isNull(request.getParameter("pageAction")));
			
			//Clear Form
			interfacesForm.setMonitorBean(new MonitorBean());
			interfacesForm.setMonitorItemBeanResult(new MonitorItemBean());
			interfacesForm.setMonitorItemList(null);
			
			if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_HISHER)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				//default value
				InterfaceBean bean =new InterfaceBean();
				bean.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
				bean.setCustGroupDesc(PickConstants.STORE_TYPE_HISHER_CODE+" "+PickConstants.getStoreGroupName(PickConstants.STORE_TYPE_HISHER_CODE));
				bean.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    bean.setTextFileName("");
				bean.setOutputPath("");//Gen
				
				interfacesForm.setBean(bean);
				logger.debug("transaDate:"+interfacesForm.getBean().getTransactionDate());
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_HISHER);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BILL_ICC)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){

				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_BILL_ICC);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_WACOAL_STOCK)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){

				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_WACOAL_STOCK);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){

				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_BMESCAN)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_BMESCAN);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_EXPORT_BILL_ICC)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT_BILL_ICC);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ORDER_EXCEL)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				
				//default value
				InterfaceBean bean =new InterfaceBean();
				bean.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
				bean.setCustGroupDesc(PickConstants.STORE_TYPE_HISHER_CODE+" "+PickConstants.getStoreGroupName(PickConstants.STORE_TYPE_HISHER_CODE));
				bean.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    bean.setTextFileName("");
				bean.setOutputPath("");//Gen
				
				interfacesForm.setBean(bean);
				logger.debug("transaDate:"+interfacesForm.getBean().getTransactionDate());
				
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_ORDER_EXCEL);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_ITEM_MASTER_HISHER)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				//default value
				InterfaceBean bean =new InterfaceBean();
				bean.setCustGroup(PickConstants.STORE_TYPE_HISHER_CODE);
				bean.setCustGroupDesc(PickConstants.STORE_TYPE_HISHER_CODE+" "+PickConstants.getStoreGroupName(PickConstants.STORE_TYPE_HISHER_CODE));
				bean.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    bean.setTextFileName("");
				bean.setOutputPath("");//Gen
				
				interfacesForm.setBean(bean);
				logger.debug("transaDate:"+interfacesForm.getBean().getTransactionDate());
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_ITEM_MASTER_HISHER);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				//default value
				InterfaceBean bean =new InterfaceBean();
				bean.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			    bean.setTextFileName("");
				bean.setOutputPath("");//Gen
				
				interfacesForm.setBean(bean);
				logger.debug("transaDate:"+interfacesForm.getBean().getTransactionDate());
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS);
				
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_TRANSACTION_LOTUS)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				//default value
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_TRANSACTION_LOTUS);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_SALEOUT_WACOAL)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				//default value
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_SALEOUT_WACOAL);
				
			}else if(Utils.isNull(request.getParameter("pageName")).equalsIgnoreCase(Constants.TYPE_IMPORT_POS)
					&& Utils.isNull(request.getParameter("pageAction")).equalsIgnoreCase("NEW")){
				//default value
				
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_POS);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
		}finally{
			//conn.close();
		}
		return returnText;
	}
}
