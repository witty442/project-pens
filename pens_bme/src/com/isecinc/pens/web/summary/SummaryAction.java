package com.isecinc.pens.web.summary;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.DiffStockSummary;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.summary.process.GenerateMonthEndLotus;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.GenStockOnhandRepTempTask;
import com.isecinc.pens.web.summary.report.OpenBillRobinsonReportAction;
import com.isecinc.pens.web.summary.report.ReportOnhandAsOfKingAction;
import com.isecinc.pens.web.summary.report.ReportOnhandAsOfRobinsonAction;
import com.isecinc.pens.web.summary.report.ReportOnhandBigCOracleAction;
import com.isecinc.pens.web.summary.report.ReportOnhandSizeColorBigCAction;
import com.isecinc.pens.web.summary.report.ReportOnhandSizeColorKingAction;
import com.isecinc.pens.web.summary.report.ReportOnhandSizeColorLotusAction;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SummaryAction extends I_Action {

	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		SummaryForm summaryForm = (SummaryForm) form;
		OnhandSummary oh = new OnhandSummary();
		try {
			 logger.debug("prepare");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 request.getSession().setAttribute("resultsTrans", null);
				 request.getSession().setAttribute("summaryTrans",null);
				 request.getSession().setAttribute("HeadSummary",null);
				 summaryForm.setDataHTML(null);
				 summaryForm.setEndDate("");
				 summaryForm.setEndSaleDate("");
				 summaryForm.setResults(null);
				 summaryForm.setResultsTrans(null);
				 summaryForm.setOnhandSummaryResults(null);
				 
				 summaryForm.setOnhandSummary(oh);
				 summaryForm.setOnhandSummaryResults(null);
				 summaryForm.setOnhandSummaryLotusResults(null);
				 
				 summaryForm.setTransactionSummary(new TransactionSummary());
				 summaryForm.setPhysicalSummaryResults(null);
				 summaryForm.setPhysicalSummary(new PhysicalSummary());
				 
				 summaryForm.setDiffStockSummaryLists(null);
				 summaryForm.setDiffStockSummary(new DiffStockSummary());
				 
				 summaryForm.setOnhandBigCResults(null);
				 summaryForm.setOnhandSummaryMTTDetailResults(null);
				 summaryForm.setOnhandSummarySizeColorBigCResults(null);
				 
				 //Default display have qty
				 if("sizeColorBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))
					|| "sizeColorLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))
					|| "sizeColorKing".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))
					|| "onhandBigCSP".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))
					|| "reportEndDateLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))
					|| "OnhandMTTDetail".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))
					|| "OnhandBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))
						 ){
					 
					 oh = new OnhandSummary();
					 oh.setDispHaveQty("true");
					 summaryForm.setOnhandSummary(oh);
				 }
				 
				 //for test 
				/* OnhandSummary bean = new OnhandSummary();
				 bean.setAsOfDate("01/02/2562");
				 summaryForm.setBean(bean);*/
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
		SummaryForm summaryForm = (SummaryForm) form;
		try {
			logger.debug("prepare 2");
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
		SummaryForm summaryForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		logger.debug("page["+Utils.isNull(request.getParameter("page"))+"]");
	
		try {
			 String queryStr= request.getQueryString();
			 if(queryStr.indexOf("d-") != -1){
			 	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
			 	System.out.println("queryStr:"+queryStr);
			 }
			 
			//Case link page in display no search again
			logger.debug("currentPage:"+request.getParameter(queryStr));
			if(request.getParameter(queryStr) != null){
				
				/*ImportDAO importDAO = new ImportDAO();
				Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
				if(m != null){
				  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
				}*/
				
				/*logger.debug("summaryForm.getOnhandSummary() :"+summaryForm.getOnhandSummary());
				logger.debug("summaryForm.getTransactionSummary() :"+summaryForm.getTransactionSummary());
				logger.debug("summaryForm.getDiffStockSummary() :"+summaryForm.getDiffStockSummary());
				logger.debug("summaryForm.getPhysicalSummary() :"+summaryForm.getPhysicalSummary());*/
				
				Object HeadSummaryObj = (Object)request.getSession(true).getAttribute("HeadSummary");
				
				
				// Store Value Case Click page 
				if(HeadSummaryObj instanceof OnhandSummary){
					OnhandSummary HeadSummary = (OnhandSummary)HeadSummaryObj;
					
					/*logger.debug("HeadSummary.getPensCustNameFrom():"+HeadSummary.getPensCustNameFrom());
					logger.debug("HeadSummary.getInitDate():"+HeadSummary.getInitDate());
					logger.debug("HeadSummary.getCustNo():"+HeadSummary.getCustNo());*/
					
					summaryForm.getOnhandSummary().setPensCustNameFrom(HeadSummary.getPensCustNameFrom());
					summaryForm.getOnhandSummary().setInitDate(HeadSummary.getInitDate());
					summaryForm.getOnhandSummary().setCustNo(HeadSummary.getCustNo());
					
				}else if(HeadSummaryObj instanceof TransactionSummary){
				
					TransactionSummary HeadSummary = (TransactionSummary)HeadSummaryObj;
					logger.debug("getTransactionSummary.getPensCustNameFrom():"+HeadSummary.getPensCustNameFrom());
					summaryForm.getTransactionSummary().setPensCustNameFrom(HeadSummary.getPensCustNameFrom());
					
				}else if(HeadSummaryObj instanceof DiffStockSummary){
					DiffStockSummary HeadSummary = (DiffStockSummary)HeadSummaryObj;
					logger.debug("getDiffStockSummary.getPensCustNameFrom():"+HeadSummary.getPensCustNameFrom());
					summaryForm.getDiffStockSummary().setPensCustNameFrom(HeadSummary.getPensCustNameFrom());
					
				}else if(HeadSummaryObj instanceof PhysicalSummary){
					
					PhysicalSummary HeadSummary = (PhysicalSummary)HeadSummaryObj;
					logger.debug("getPhysicalSummary.getPensCustNameFrom():"+HeadSummary.getPensCustNameFrom());
					summaryForm.getPhysicalSummary().setPensCustNameFrom(HeadSummary.getPensCustNameFrom());
				}
			}else{
				
				if("onhand".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					List<OnhandSummary> results = new SummaryDAO().search(summaryForm.getOnhandSummary(),user);
					if (results != null  && results.size() >0) {
						summaryForm.setOnhandSummaryResults(results);
						
						OnhandSummary cc = (OnhandSummary)results.get(0);
						summaryForm.getOnhandSummary().setAsOfDate(cc.getAsOfDate());
						summaryForm.getOnhandSummary().setFileName(cc.getFileName());
						
						//logger.debug("results:"+summaryForm.getOnhandSummaryResults());
						
					} else {
						summaryForm.setOnhandSummaryResults(null);
						request.setAttribute("Message", "��辺������");
					}
					
				}else if("onhandLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					//set for display by page
					request.getSession().setAttribute("summary",null);
					summaryForm.setPage("onhandLotus");
					List<OnhandSummary> results = null;
					OnhandSummary re = new SummaryDAO().searchOnhandLotus(summaryForm,summaryForm.getOnhandSummary(),user);
					if(re != null){
						results = re.getItemsList();
						request.getSession().setAttribute("summary",re.getSummary());
					}else{
						request.getSession().setAttribute("summary",null);
					}
					
					if (results != null  && results.size() >0) {
						summaryForm.setResults(results);
						
						//logger.debug("results:"+summaryForm.getResults());
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
						
					} else {
						summaryForm.setResults(null);
						request.setAttribute("Message", "��辺������");
					}
				}else if("onhandTops".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					//set for display by page
					request.getSession().setAttribute("summary",null);
					summaryForm.setPage("onhandTops");
					List<OnhandSummary> results = null;
					OnhandSummary re = new SummaryDAO().searchOnhandTops(summaryForm,summaryForm.getOnhandSummary(),user);
					if(re != null){
						results = re.getItemsList();
						request.getSession().setAttribute("summary",re.getSummary());
					}else{
						request.getSession().setAttribute("summary",null);
					}
					
					if (results != null  && results.size() >0) {
						summaryForm.setResults(results);
						
						//logger.debug("results:"+summaryForm.getResults());
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
						
					} else {
						summaryForm.setResults(null);
						request.setAttribute("Message", "��辺������");
					}
					
				/**  Onhand as of robinson OLD Version **/
				}else if("onhandAsOf_Robinson".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					//set for display by page
					request.getSession().setAttribute("summary",null);
					summaryForm.setPage("onhandAsOf_Robinson");
					List<OnhandSummary> results = null;
					OnhandSummary re = new SummaryDAO().searchOnhandAsOf_Robinson(summaryForm,summaryForm.getOnhandSummary(),user);
					if(re != null){
						results = re.getItemsList();
						request.getSession().setAttribute("summary",re.getSummary());
					}else{
						request.getSession().setAttribute("summary",null);
					}
					
					if (results != null  && results.size() >0) {
						summaryForm.setResults(results);
						
						//logger.debug("results:"+summaryForm.getResults());
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
						
					} else {
						summaryForm.setResults(null);
						request.setAttribute("Message", "��辺������");
					}
			    /** Onhand as of robinson new version**/
				}else if("onhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					summaryForm = ReportOnhandAsOfRobinsonAction.process(request,user, summaryForm);
					
				}else if("openBillRobinsonReport".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					summaryForm.setDataHTML(null);
					summaryForm.getBean().setErrorList(null);
					summaryForm = OpenBillRobinsonReportAction.process(request,user, summaryForm,"html");
					//validate error
					if(summaryForm.getBean().getErrorList() != null && summaryForm.getBean().getErrorList().size() >0){
						 //gen Error Item display
						 request.setAttribute("Message", "���������١��ͧ ��辺������ Pens Item � MASTER TABLE PENSBME_ITEMBY_GP");
						 summaryForm.setDataHTML(OpenBillRobinsonReportAction.genErrorListTable(summaryForm.getBean().getErrorList()));
					 }else{
						 if(summaryForm.getDataHTML()==null ||(summaryForm.getDataHTML() != null && summaryForm.getDataHTML().toString().length()==0)){
							 request.setAttribute("Message", "��辺������");
						 }
					 }
				}else if("ReportStockWacoalLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					//Validate Initial Date
					Date asOfDate = DateUtil.parse(summaryForm.getOnhandSummary().getSalesDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
					Date initDate = new SummaryDAO().searchInitDateWacoalStock(summaryForm.getOnhandSummary().getPensCustCodeFrom());
					
					logger.debug("initDate:"+initDate);
					logger.debug("asOfDate:"+asOfDate);
					request.getSession().setAttribute("summary",null);
					
					boolean pass = true;
					if(initDate !=null){
						if(asOfDate.before(initDate)){
							summaryForm.setResults(null);
							request.setAttribute("Message", "�ѹ��� as of ��ͧ�ҡ������ҡѺ�ѹ���Ѻʵ�͡��駵�");
							pass = false;
						}
					}
					if(pass){
						List<OnhandSummary> results = null;
						 OnhandSummary re = null;
						if("ALL".equalsIgnoreCase(summaryForm.getOnhandSummary().getPensCustCodeFrom())){
							re = new SummaryDAO().searchReportStockWacoalLotusAllBranch(summaryForm,user,asOfDate);
						}else{
						    re = new SummaryDAO().searchReportStockWacoalLotusOneBranch(summaryForm,user,initDate,asOfDate);
						}
						if(re != null){
							results = re.getItemsList();	
							request.getSession().setAttribute("summary",re.getSummary());
						}
						if (results != null  && results.size() >0) {
							summaryForm.setResults(results);
							summaryForm.getOnhandSummary().setInitDate(DateUtil.stringValue(initDate,DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
							//get Branch Name
							GeneralDAO DAO = new GeneralDAO();
							summaryForm.getOnhandSummary().setPensCustNameFrom(DAO.getBranchName(summaryForm.getOnhandSummary().getPensCustCodeFrom()));
							summaryForm.getOnhandSummary().setInitDate(DateUtil.stringValue(initDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
						} else {
							summaryForm.setResults(null);
							summaryForm.getOnhandSummary().setInitDate(DateUtil.stringValue(initDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
							request.setAttribute("Message", "��辺������");
						}
					}	
				}else if("monthEndLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					List<OnhandSummary> results = new SummaryDAO().searchOnhandMonthEndLotus(summaryForm,summaryForm.getOnhandSummary(),user);
					summaryForm.setPage("monthEndLotus");
					
					if (results != null  && results.size() >0) {
						summaryForm.setResults(results);

						//logger.debug("results:"+summaryForm.getOnhandSummaryLotusResults());
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
					
					} else {
						summaryForm.setResults(null);
						request.setAttribute("Message", "��辺������");
					}
				}else if("bmeTrans".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					List<OnhandSummary> results = new SummaryDAO().searchBmeTrans(summaryForm.getOnhandSummary(),user);
					if (results != null  && results.size() >0) {
						summaryForm.setOnhandSummaryBmeTransResults(results);
						
						//logger.debug("results:"+summaryForm.getOnhandSummaryLotusResults());
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
						
					} else {
						summaryForm.setOnhandSummaryBmeTransResults(null);
						request.setAttribute("Message", "��辺������");
					}
					
				}else if("onhandMTT".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					request.getSession().setAttribute("summary" ,null);
					//Validate Initial Date
					Date asOfDate = DateUtil.parse(summaryForm.getOnhandSummary().getSalesDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					Date initDate = new SummaryDAO().searchInitDateMTT(summaryForm.getOnhandSummary().getPensCustCodeFrom());
					
					logger.debug("initDate:"+initDate);
					logger.debug("asOfDate:"+asOfDate);
					summaryForm.setPage("onhandMTT");
				
					boolean pass = true;
					if(initDate !=null){
						if(asOfDate.before(initDate)){
							summaryForm.setResults(null);
							request.setAttribute("Message", "�ѹ��� as of ��ͧ�ҡ������ҡѺ�ѹ���Ѻʵ�͡��駵�");
							pass = false;
						}
					}
					if(pass){
						List<OnhandSummary> results = null;
						OnhandSummary r = new SummaryDAO().searchOnhandMTT(summaryForm.getOnhandSummary(),initDate,user,summaryForm.getSummaryType());
						results = r.getItemsList();
						if (results != null  && results.size() >0) {
							summaryForm.setResults(results);
							request.getSession().setAttribute("summary" ,r.getSummary());
							summaryForm.getOnhandSummary().setInitDate(DateUtil.stringValue(initDate,DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
							
							ImportDAO importDAO = new ImportDAO();
							Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
							if(m != null){
							  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
							  summaryForm.getOnhandSummary().setCustNo(m.getInterfaceValue());
							}
                          
						} else {
							summaryForm.setResults(null);
							request.setAttribute("Message", "��辺������");
						}
					}
				}else if("onhandMTTDetail".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					//Validate Initial Date
					Date asOfDate = DateUtil.parse(summaryForm.getOnhandSummary().getSalesDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					Date initDate = new SummaryDAO().searchInitDateMTT(summaryForm.getOnhandSummary().getPensCustCodeFrom());
					
					logger.debug("initDate:"+initDate);
					logger.debug("asOfDate:"+asOfDate);
					
					boolean pass = true;
					if(initDate !=null){
						if(asOfDate.before(initDate)){
							summaryForm.setOnhandSummaryMTTDetailResults(null);
							request.setAttribute("Message", "�ѹ��� as of ��ͧ�ҡ������ҡѺ�ѹ���Ѻʵ�͡��駵�");
							pass = false;
						}
					}
					if(pass){
						List<OnhandSummary> results = null;
						results = new SummaryDAO().searchOnhandMTTDetail(summaryForm.getOnhandSummary(),initDate,user);
						
						if (results != null  && results.size() >0) {
							summaryForm.setOnhandSummaryMTTDetailResults(results);
							summaryForm.getOnhandSummary().setInitDate(DateUtil.stringValue(initDate,DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
							
							ImportDAO importDAO = new ImportDAO();
							Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
							if(m != null)
							  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
							
						} else {
							summaryForm.setOnhandSummaryMTTDetailResults(null);
							request.setAttribute("Message", "��辺������");
						}
					}
				
				}else if("onhandBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					summaryForm.setPage("onhandBigC");
					List<OnhandSummary> results = null;
					OnhandSummary re = new SummaryDAO().searchOnhandBigC_ASOF(summaryForm.getOnhandSummary(),user);
					if(re != null){
						results = re.getItemsList();
						request.getSession().setAttribute("summary",re.getSummary());
					}else{
						request.getSession().setAttribute("summary",null);
					}
					
					if (results != null  && results.size() >0) {
						summaryForm.setResults(results);
						summaryForm.setSummary(re);
						//logger.debug("results:"+summaryForm.getResults());
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
						if(m != null)
						   summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
						
					} else {
						summaryForm.setResults(null);
						request.setAttribute("Message", "��辺������");
					}
				}else if("onhandLotusPeriod".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					List<OnhandSummary> results = new SummaryDAO().searchOnhandLotusPeriod(summaryForm.getOnhandSummary(),user);
					if (results != null  && results.size() >0) {
						summaryForm.setOnhandSummaryLotusPeriodResults(results);
						
						OnhandSummary cc = (OnhandSummary)results.get(0);
						
						//logger.debug("results:"+summaryForm.getOnhandSummaryLotusPeriodResults());
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
						
					} else {
						summaryForm.setOnhandSummaryLotusResults(null);
						request.setAttribute("Message", "��辺������");
					}
				}else if("physical".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					List<PhysicalSummary> results = new SummaryDAO().search(summaryForm.getPhysicalSummary(),user);
					if (results != null && results.size() >0) {
						summaryForm.setPhysicalSummaryResults(results);
						
						PhysicalSummary cc = (PhysicalSummary)results.get(0);
						summaryForm.getOnhandSummary().setFileName(cc.getFileName());
						
						//logger.debug("results:"+summaryForm.getPhysicalSummaryResults());
						
					} else {
						summaryForm.setPhysicalSummaryResults(null);
						request.setAttribute("Message", "��辺������");
					}
						
				}else if("lotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					request.getSession().setAttribute("summary" ,null);
					summaryForm.setPage(Utils.isNull(request.getParameter("page")));
					
					List<TransactionSummary> results = null;
					TransactionSummary re = new SummaryDAO().search(summaryForm.getTransactionSummary(),user,"lotus");
					results = re.getItemsList();
					if (results != null  && results.size() >0) {
						request.getSession().setAttribute("summaryTrans", re.getSummary());
						summaryForm.setResultsTrans(results);
						//logger.debug("results:"+summaryForm.getLotusSummaryResults());
						
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getTransactionSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getTransactionSummary().setPensCustNameFrom(m.getPensDesc());
					} else {
						summaryForm.setResultsTrans(null);
						request.getSession().setAttribute("summaryTrans", null);
						request.setAttribute("Message", "��辺������");
					}
					
				}else if("bigc".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					request.getSession().setAttribute("summary" ,null);
					summaryForm.setPage(Utils.isNull(request.getParameter("page")));
					
					List<TransactionSummary> results = null;
					TransactionSummary re = new SummaryDAO().search(summaryForm.getTransactionSummary(),user,"bigc");
					results = re.getItemsList();
					if (results != null  && results.size() >0) {
						request.getSession().setAttribute("summaryTrans", re.getSummary());
						summaryForm.setResultsTrans(results);
						//logger.debug("results:"+summaryForm.getBigcSummaryResults());
						
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getTransactionSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getTransactionSummary().setPensCustNameFrom(m.getPensDesc());
					} else {
						summaryForm.setResultsTrans(null);
						request.getSession().setAttribute("summaryTrans", null);
						request.setAttribute("Message", "��辺������");
					}	
				}else if("bigc_temp".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					request.getSession().setAttribute("summary" ,null);
					summaryForm.setPage(Utils.isNull(request.getParameter("page")));
					
					List<TransactionSummary> results = null;
					TransactionSummary re = new SummaryDAO().search(summaryForm.getTransactionSummary(),user,"bigc_temp");
					results = re.getItemsList();
					if (results != null  && results.size() >0) {
						request.getSession().setAttribute("summaryTrans", re.getSummary());
						summaryForm.setResultsTrans(results);
						//logger.debug("results:"+summaryForm.getBigcSummaryResults());
						
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getTransactionSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getTransactionSummary().setPensCustNameFrom(m.getPensDesc());
					} else {
						summaryForm.setResultsTrans(null);
						request.getSession().setAttribute("summaryTrans", null);
						request.setAttribute("Message", "��辺������");
					}	
				}else if("tops".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					request.getSession().setAttribute("summary" ,null);
					summaryForm.setPage(Utils.isNull(request.getParameter("page")));
					
					List<TransactionSummary> results = null;
					TransactionSummary re = new SummaryDAO().search(summaryForm.getTransactionSummary(),user,"tops");
					results = re.getItemsList();
					if (results != null  && results.size() >0) {
						request.getSession().setAttribute("summaryTrans", re.getSummary());
						summaryForm.setResultsTrans(results);
						//logger.debug("results:"+summaryForm.getTopsSummaryResults());
						
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getTransactionSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getTransactionSummary().setPensCustNameFrom(m.getPensDesc());
					} else {
						summaryForm.setResultsTrans(null);
						request.getSession().setAttribute("summaryTrans",null);
						request.setAttribute("Message", "��辺������");
					}	
		        }else if("king".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
		        	request.getSession().setAttribute("summary" ,null);
					summaryForm.setPage(Utils.isNull(request.getParameter("page")));
					
					List<TransactionSummary> results = null;
					TransactionSummary re = new SummaryDAO().search(summaryForm.getTransactionSummary(),user,"king");
					results = re.getItemsList();
					if (results != null  && results.size() >0) {
						summaryForm.setResultsTrans(results);
						request.getSession().setAttribute("summaryTrans", re.getSummary());
						
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getTransactionSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getTransactionSummary().setPensCustNameFrom(m.getPensDesc());
						
						logger.debug("custname:"+summaryForm.getTransactionSummary().getPensCustNameFrom());
					} else {
						summaryForm.setResultsTrans(null);
						request.getSession().setAttribute("summaryTrans",null);
						request.setAttribute("Message", "��辺������");
					}	
					
				}else if("diff_stock".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					List<DiffStockSummary> results = new SummaryDAO().searchDiffStock(summaryForm.getDiffStockSummary(),user);
					if (results != null  && results.size() >0) {
						summaryForm.setDiffStockSummaryLists(results);
						//logger.debug("results:"+summaryForm.getDiffStockSummaryLists());
						
					} else {
						summaryForm.setDiffStockSummaryLists(null);
						request.setAttribute("Message", "��辺������");
					}
					
				}else if("sumByGroupCode".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					//Wait
					List<TransactionSummary> results = new SummaryDAO().searchByGroupCode(summaryForm.getTransactionSummary(),user,"lotus");
					if (results != null  && results.size() >0) {
						summaryForm.setResultsTrans(results);
						//logger.debug("results:"+summaryForm.getLotusSummaryResults());
						
					} else {
						summaryForm.setResultsTrans(null);
						request.setAttribute("Message", "��辺������");
					}

				}else if("onhandBigCSP".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					//Validate Initial Date
					Date asOfDate = DateUtil.parse(summaryForm.getOnhandSummary().getSalesDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					Date initDate = new SummaryDAO().searchInitDateBigC(summaryForm.getOnhandSummary().getPensCustCodeFrom());
					
					logger.debug("initDate:"+initDate);
					logger.debug("asOfDate:"+asOfDate);
					logger.debug("haveDispQty:"+summaryForm.getOnhandSummary().getDispHaveQty());
					//set for display by page
					summaryForm.setPage("onhandBigCSP");
					
					boolean pass = true;
					if(initDate !=null){
						if(asOfDate.before(initDate)){
							summaryForm.setOnhandSummarySizeColorBigCResults(null);
							request.setAttribute("Message", "�ѹ��� as of ��ͧ�ҡ������ҡѺ�ѹ���Ѻʵ�͡��駵�");
							pass = false;
						}
					}
					if(pass){
						List<OnhandSummary> results = null;
						results = new SummaryDAO().searchOnhandBigC_SP(summaryForm,summaryForm.getOnhandSummary(),initDate);
						
						if (results != null  && results.size() >0) {
							summaryForm.setResults(results);
							summaryForm.getOnhandSummary().setInitDate(DateUtil.stringValue(initDate,DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
							
							ImportDAO importDAO = new ImportDAO();
							Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
							if(m != null)
							  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
						
						} else {
							summaryForm.setResults(null);
							request.setAttribute("Message", "��辺������");
						}
					}
				}else if("sizeColorLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					summaryForm = ReportOnhandSizeColorLotusAction.process(request,user, summaryForm);
					
				}else if("sizeColorBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					summaryForm = ReportOnhandSizeColorBigCAction.process(request,user, summaryForm);
				
				/** Duty-free */
				}else if("onhandAsOfKing".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					summaryForm = ReportOnhandAsOfKingAction.process(request,user, summaryForm);
					
				}else if("sizeColorKing".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					summaryForm = ReportOnhandSizeColorKingAction.process(request,user, summaryForm);
					
				}else if("onhandBigCOracle".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					summaryForm = ReportOnhandBigCOracleAction.process(request,user, summaryForm);
				}
				
				// set param on screen
				if( !Utils.isNull(summaryForm.getOnhandSummary().getPensCustNameFrom()).equals("") ){
					logger.debug("set OnhandSummary");
				    OnhandSummary HeadSummary = new OnhandSummary();
	                HeadSummary.setInitDate(summaryForm.getOnhandSummary().getInitDate());
	                HeadSummary.setCustNo(summaryForm.getOnhandSummary().getCustNo());
	                HeadSummary.setPensCustNameFrom(summaryForm.getOnhandSummary().getPensCustNameFrom());
				    request.getSession(true).setAttribute("HeadSummary",HeadSummary);
				    
				}else if( !Utils.isNull(summaryForm.getTransactionSummary().getPensCustNameFrom()).equals("") ){
					logger.debug("set TransactionSummary");
					 TransactionSummary HeadSummary = new TransactionSummary();
		             HeadSummary.setPensCustNameFrom(summaryForm.getTransactionSummary().getPensCustNameFrom());
					 request.getSession(true).setAttribute("HeadSummary",HeadSummary);
					 
				}else if( !Utils.isNull(summaryForm.getDiffStockSummary().getPensCustNameFrom()).equals("") ){
					logger.debug("set DiffStockSummary");
					 DiffStockSummary HeadSummary = new DiffStockSummary();
		             HeadSummary.setPensCustNameFrom(summaryForm.getDiffStockSummary().getPensCustNameFrom());
					 request.getSession(true).setAttribute("HeadSummary",HeadSummary);
					 
				}else if( !Utils.isNull(summaryForm.getPhysicalSummary().getPensCustNameFrom()).equals("") ){
					logger.debug("set PhysicalSummary");
					 PhysicalSummary HeadSummary = new PhysicalSummary();
		             HeadSummary.setPensCustNameFrom(summaryForm.getPhysicalSummary().getPensCustNameFrom());
					 request.getSession(true).setAttribute("HeadSummary",HeadSummary);
				}
				
			}//Case Click Link	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "search";
	}
	public ActionForward genMonthEnd(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genMonthEnd");
		SummaryForm summaryForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String storeType = "";
		String yearMonth = "";
		String[] results = new String[3];
		try{
			storeType = Utils.isNull(request.getParameter("storeType"));
			if("lotus".equalsIgnoreCase(storeType)){
				
				results = GenerateMonthEndLotus.generateMonthLotus(summaryForm.getOnhandSummary(), user);
				if("".equals(results[2])){
				  request.setAttribute("Message", "Generate Month End ���º��������");
				}else{
				  request.setAttribute("Message", "�������ö Generate Month End �� \n "+results[2]);
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("export");
	}
	
	public ActionForward genEndDate(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genMonthEnd");
		SummaryForm summaryForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String storeType = "";
		String yearMonth = "";
		String[] results = new String[3];
		try{
			storeType = Utils.isNull(request.getParameter("storeType"));
			if("lotus".equalsIgnoreCase(storeType)){
				
				/*results = GenerateEndDateLotus.generateEndDateLotus(summaryForm.getOnhandSummary(), user);
				if("".equals(results[2])){
				  request.setAttribute("Message", "Generate End Date ���º��������");
				}else{
				  request.setAttribute("Message", "�������ö Generate End Date �� \n "+results[2]);
				}*/
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("export");
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		SummaryForm summaryForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		SummaryExport export = new SummaryExport();
		try {
			
			logger.debug("PageAction:"+request.getParameter("page"));
			
			/** Onhand **/
			if("onhand".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="ReportStockonhandBme.xls";
				logger.debug("summaryForm.getOnhandSummaryResults().size():"+summaryForm.getOnhandSummaryResults().size() );
				if(summaryForm.getOnhandSummaryResults() != null && summaryForm.getOnhandSummaryResults().size() > 0){
					htmlTable = export.genOnhandHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("ReportStockWacoalLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="ReportStockWacoalLotus.xls";
				if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genReportStockWacoalLotusHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("onhandLotusPeriod".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="ReportBmeStockon-handatLotus(Period for Acc).xls";
			     if(summaryForm.getOnhandSummaryLotusResults() != null && summaryForm.getOnhandSummaryLotusResults().size() > 0){
					htmlTable = export.genOnhandLotusPeriodHTML(request,summaryForm,user,"lotus");	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("onhandLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand at Lotus(As of)_"+summaryForm.getSummaryType()+".xls";
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandLotusHTML(Utils.isNull(request.getParameter("page")),request,summaryForm,user,summaryForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("onhandTops".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand at Tops(As of)_"+summaryForm.getSummaryType()+".xls";
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandTopsHTML(Utils.isNull(request.getParameter("page")),request,summaryForm,user,summaryForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("onhandAsOf_Robinson".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand at Robinson(As of)_"+summaryForm.getSummaryType()+".xls";
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandRobinsonHTML(Utils.isNull(request.getParameter("page")),request,summaryForm,user,summaryForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("monthEndLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock Month End Lotus.xls";
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genMonthEndLotusHTML(Utils.isNull(request.getParameter("page")),request,summaryForm,user,summaryForm.getResults());	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("reportEndDateLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme End Date Stock Lotus.xls";
				 
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genReportEndDateLotusHTML(Utils.isNull(request.getParameter("page")),request,summaryForm,user,summaryForm.getResults());	
				 }else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				 }
			}else if("bmeTrans".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Transaction Bme.xls";
				 
			     if(summaryForm.getOnhandSummaryBmeTransResults() != null && summaryForm.getOnhandSummaryBmeTransResults().size() > 0){
					htmlTable = export.genBmeTransHTML(Utils.isNull(request.getParameter("page")),request,summaryForm,user,summaryForm.getOnhandSummaryBmeTransResults());	
				 }else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				 }
			}else if("onhandMTT".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand MTT.xls";
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandMTTHTML(request,summaryForm,user);	
				 }else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				 }
			}else if("onhandMTTDetail".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand MTT Detail.xls";
			     if(summaryForm.getOnhandSummaryMTTDetailResults() != null && summaryForm.getOnhandSummaryMTTDetailResults().size() > 0){
					htmlTable = export.genOnhandMTTDetailHTML(request,summaryForm,user);	
				 }else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				 }
			}else if("onhandAsOfKing".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand DutyFree.xls";
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandAsOfKingHTML(request,summaryForm,user);	
				 }else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				 }
			}else if("onhandAsOfRobinson".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand Robinson.xls";
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandAsOfRobinsonHTML(request,summaryForm,user);	
				 }else{
					 request.setAttribute("Message", "��辺������");
						return mapping.findForward("export");
					 }
			}else if("sizeColorKing".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Stock on-hand at Dutyfree ��-��� (���ʵ�͡����).xls";
			     if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandSizeColorKingHTML(request,summaryForm,user);	
				 }else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				 }
			}else if("onhandBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="Report Bme Stock on-hand at BigC(As of).xls";
				if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandBigCHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("lotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName="ReportSalesDetailBmeLOTUS.xls";
				
				if(summaryForm.getResultsTrans() != null && summaryForm.getResultsTrans().size() > 0){
					htmlTable = export.genLotusHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("bigc".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="ReportSalesDetailBmeBigC.xls";
				if(summaryForm.getResultsTrans() != null && summaryForm.getResultsTrans().size() > 0){
					htmlTable = export.genBigCHTML(request,summaryForm,user,"");	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("bigc_temp".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="ReportSalesDetailBmeBigC_TEMP.xls";
				if(summaryForm.getResultsTrans() != null && summaryForm.getResultsTrans().size() > 0){
					htmlTable = export.genBigCHTML(request,summaryForm,user,"(����ѹ-TEMP)");	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("tops".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName="ReportSalesDetailBmeTops.xls";
				if(summaryForm.getResultsTrans() != null && summaryForm.getResultsTrans().size() > 0){
					htmlTable = export.genTopsHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("king".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName="ReportSalesDetailBmeKingPower.xls";
				if(summaryForm.getResultsTrans() != null && summaryForm.getResultsTrans().size() > 0){
					htmlTable = export.genKingHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("physical".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				if(summaryForm.getPhysicalSummaryResults() != null && summaryForm.getPhysicalSummaryResults().size() > 0){
					htmlTable = export.genPhysicalHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("diff_stock".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				if(summaryForm.getDiffStockSummaryLists() != null && summaryForm.getDiffStockSummaryLists().size() > 0){
					htmlTable = export.genDiffStockHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("sizeColorBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genBigCSizeColorHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("onhandBigCSP".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName="Report Stock Onhand at BigC (For SP)_"+summaryForm.getSummaryType()+".xls";
				if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genOnhandBigCSPHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("sizeColorLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName="Report Stock onhand at LOTUS Size-Color(Init New Stock)_"+Utils.isNull(summaryForm.getSummaryType())+".xls";
				if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = export.genLotusSizeColorHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("onhandBigCOracle".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName="Report Stock onhand BigC at Oracle.xls";
				if(summaryForm.getResults() != null && summaryForm.getResults().size() > 0){
					htmlTable = ReportOnhandBigCOracleAction.genExcel(request,summaryForm);
				}else{
					request.setAttribute("Message", "��辺������");
					return mapping.findForward("export");
				}
			}else if("openBillRobinsonReport".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 summaryForm.setDataHTML(null);
				 summaryForm.getBean().setErrorList(null);
				 summaryForm = OpenBillRobinsonReportAction.process(request,user, summaryForm,"EXCEL");
				
				 //validate error
				 if(summaryForm.getBean().getErrorList() != null && summaryForm.getBean().getErrorList().size() >0){
					 logger.debug("Export error");
					 //gen Error Item display
					 request.setAttribute("Message", "���������١��ͧ ��辺������ Pens Item � MASTER TABLE PENSBME_ITEMBY_GP");
					 summaryForm.setDataHTML(OpenBillRobinsonReportAction.genErrorListTable(summaryForm.getBean().getErrorList()));
				 }else{
					// logger.debug("data HTML:"+summaryForm.getDataHTML()+":"+summaryForm.getDataHTML().toString().length());
					 if(summaryForm.getDataHTML()==null ||(summaryForm.getDataHTML() != null && summaryForm.getDataHTML().toString().length()==0)){
						 request.setAttribute("Message", "��辺������");
					 }else{
						  htmlTable = summaryForm.getDataHTML();
					 }
				 }
			}
			
	        //logger.debug("fileName:"+fileName);
	        //fileName = Utils.toUnicodeChar(fileName);
	        //logger.debug("fileName:"+fileName);
	        //"data.xls";
			if( (summaryForm.getDataHTML() != null && summaryForm.getDataHTML().toString().length()>0)
				|| (htmlTable != null && htmlTable.length() > 0)){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    
			    out.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("export");
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

	/** For batch popup **/
	public ActionForward genStockOnhandTemp(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genStockOnhandTemp");
		SummaryForm aForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			batchParaMap.put(GenStockOnhandRepTempTask.PARAM_ASOF_DATE, aForm.getOnhandSummary().getAsOfDate());
			batchParaMap.put(GenStockOnhandRepTempTask.PARAM_STORE_CODE, aForm.getOnhandSummary().getPensCustCodeFrom());
			
			logger.debug("storeCode:"+aForm.getOnhandSummary().getPensCustCodeFrom());
			
			request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
			request.setAttribute("action","submitedGenStockOnhandTemp");//set to popup page to BatchTask
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
		}
		return mapping.findForward("export");
	}
	/** For batch popup after Task success**/
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		SummaryForm aForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		//String pageName = aForm.getPageName(); 
		try {
			//logger.debug("searchBatch :pageName["+pageName+"]");
	
			 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
			 logger.debug("batchTaskForm result size:"+batchTaskForm.getResults().length);
			 
			 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
			 
			 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
			 logger.debug("fileName:"+batchTaskForm.getMonitorItem().getFileName());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{	
		}
		return mapping.findForward("search");
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		SummaryForm summaryForm = (SummaryForm) form;
		try {
			
			 	
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
