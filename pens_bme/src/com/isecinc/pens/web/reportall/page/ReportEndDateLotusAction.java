package com.isecinc.pens.web.reportall.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.GenStockOnhandRepTempTask;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.sql.ReportEndDateLotusSQL;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ReportEndDateLotusAction extends I_Action {

	
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "reportAll";
		ReportAllForm reportAllForm = (ReportAllForm) form;
		ReportAllBean bean = new ReportAllBean();
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 request.getSession().setAttribute("BATCH_TASK_RESULT",null);
				 reportAllForm.setResults(null);
				
				 bean = new ReportAllBean();
				 bean.setDispHaveQty("true");
				 
				 /** get parameter from Gen AutoOrder Page **/
				 if( !Utils.isNull(request.getParameter("storeCode")).equals("")){
					 bean.setPensCustCodeFrom(Utils.isNull(request.getParameter("storeCode")));
					 bean.setPensCustNameFrom(GeneralDAO.getStoreName(bean.getPensCustCodeFrom()));
				 }
				 if( !Utils.isNull(request.getParameter("orderDate")).equals("")){
					 bean.setSalesDate(Utils.isNull(request.getParameter("orderDate")));
				 }
				 bean.setCurrentDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				 
				 reportAllForm.setBean(bean);
				 reportAllForm.setPageName(Utils.isNull(request.getParameter("pageName")));
				 
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
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "reports";
	}

	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
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
				//logger.debug("results:"+summaryForm.getResults());
				ImportDAO importDAO = new ImportDAO();
				Master m = importDAO.getStoreName("Store", aForm.getBean().getPensCustCodeFrom());
				if(m != null)
				   aForm.getBean().setPensCustNameFrom(m.getPensDesc());
			}else{
				//set for display by page
				request.getSession().setAttribute("summary" ,null);
				request.getSession().setAttribute("BATCH_TASK_RESULT",null);//Clear BatchTask Result
				 
				//Validate asOfdate Must over than max End Date
				String[] resultsValidate = ControlConstantsDB.canGenEndDateLotus(aForm.getBean().getPensCustCodeFrom(),aForm.getBean().getSalesDate());
				if(resultsValidate[0].equals("false")){
					aForm.setResults(null);
					request.setAttribute("Message", "กรุณากรอก วันที่ขาย (As Of) มากกว่า วันที่ปิดสต๊อกล่าสุด ");
				}else{
					List<ReportAllBean> results = null;
					ReportAllBean sumBean = searchReportEndDateLotusAllStore(aForm.getBean(),user);
					
					results = sumBean.getItemsList();
					if (results != null  && results.size() >0) {
						aForm.setResults(results);
						request.getSession().setAttribute("summary", sumBean.getSummary());
						
						//logger.debug("results:"+summaryForm.getResults());
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", aForm.getBean().getPensCustCodeFrom());
						if(m != null)
							aForm.getBean().setPensCustNameFrom(m.getPensDesc());
						
					} else {
						aForm.setResults(null);
						request.getSession().setAttribute("summary", null);
						request.setAttribute("Message", "ไม่พบข่อมูล");
					}
					
					//By StoreCode ONLY ,All Store Not Set
					if( !Utils.isNull(aForm.getBean().getPensCustCodeFrom()).equals("ALL")
						&& Utils.isNull(aForm.getBean().getPensCustCodeFrom()).indexOf(",") == -1){
						aForm.getBean().setEndDate(getEndDateStock(aForm.getBean().getPensCustCodeFrom()));
						aForm.getBean().setEndSaleDate(getEndSaleDateLotus(aForm.getBean().getPensCustCodeFrom(),aForm.getBean().getSalesDate()) );
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "reportAll";
	}
	 public ReportAllBean searchReportEndDateLotusAllStore(ReportAllBean c,User user) throws Exception{
	   Statement stmt = null;
		ResultSet rst = null;
		List<ReportAllBean> pos = new ArrayList<ReportAllBean>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		double BEGINING_QTY = 0;
		double sale_in_qty = 0;
		double sale_return_qty = 0;
		double sale_out_qty = 0;
		double ADJUST_QTY = 0;
		double STOCK_SHORT_QTY = 0;
		double onhand_qty = 0;
		double onhand_amt = 0;
		String key = "";
		Map<String, ReportAllBean> rowMap = new HashMap<String, ReportAllBean>();
		ReportAllBean item = null;
		ReportAllBean prevItem = null;
		List<StoreBean> storeList = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			logger.debug("test "+Utils.isNull(c.getPensCustCodeFrom())+":"+Utils.isNull(c.getPensCustCodeFrom()).indexOf(","));
			
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL") 
					&& Utils.isNull(c.getPensCustCodeFrom()).indexOf(",") == -1){
				//Get By Store
				storeList =  new ArrayList<StoreBean>();
				StoreBean storeBean = new StoreBean();
				storeBean.setStoreCode(c.getPensCustCodeFrom());
				storeList.add(storeBean);
			}else if( Utils.isNull(c.getPensCustCodeFrom()).indexOf(",") != -1){ //select multi store
				logger.debug("Multi storeCode");
				String[] storeCodeArr = Utils.isNull(c.getPensCustCodeFrom()).split("\\,");
				StoreBean m = new StoreBean();
				StoreBean storeB = null;
				storeList = new ArrayList<StoreBean>();
				for(int i=0;i<storeCodeArr.length;i++){
					m = new StoreBean();
					m.setStoreCode(storeCodeArr[i]);
					//get StoreName
					storeB = StoreDAO.getStoreName(conn,"Store",m.getStoreCode(),"LOTUS");
					if(storeB != null){
						m.setStoreName(storeB.getStoreName());
					}
					storeList.add(m);
				}
			}else{
				//Get AllStore Lotus
				storeList = StoreDAO.getStoreList(conn, PickConstants.STORE_TYPE_LOTUS_CODE);
			}
			
			if(storeList != null && storeList.size() >0){
			for(int i=0;i<storeList.size();i++){
				//Loop Step by Store Code
				StoreBean storeBean = storeList.get(i);
				c.setPensCustCodeFrom(storeBean.getStoreCode());
				
				sql = ReportEndDateLotusSQL.genSQL(conn, c, user, c.getSummaryType(),"ReportEndDate");
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					item = new ReportAllBean();
					if("PensItem".equalsIgnoreCase(c.getSummaryType())){
					    key = rst.getString("CUSTOMER_CODE")+rst.getString("group_type")+rst.getString("pens_item");
					}else{
						key = rst.getString("CUSTOMER_CODE")+rst.getString("group_type");
					}
					
					item.setStoreCode(rst.getString("CUSTOMER_CODE"));
					item.setStoreName(rst.getString("customer_desc"));
					if("PensItem".equalsIgnoreCase(c.getSummaryType())){
					   item.setPensItem(rst.getString("pens_item"));
					}
				
					item.setGroup(rst.getString("group_type"));
					item.setBeginingQty(Utils.decimalFormat(rst.getDouble("BEGINING_QTY"),Utils.format_current_no_disgit));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price_bf"),Utils.format_current_2_disgit));
					item.setOnhandAmt(Utils.decimalFormat(rst.getDouble("onhand_amt"),Utils.format_current_2_disgit));
					 
					if(rowMap.get(key) != null){
						//get prev row + new row
						prevItem = rowMap.get(key);
						prevItem.setBeginingQty(prevItem.getBeginingQty()+item.getBeginingQty());
						
						rowMap.put(key, prevItem);
					}else{
						rowMap.put(key, item);
					}
					
					//Sum All Row
					BEGINING_QTY += rst.getDouble("BEGINING_QTY");
					sale_in_qty += rst.getDouble("sale_in_qty");
					sale_return_qty += rst.getDouble("sale_return_qty");
					sale_out_qty += rst.getDouble("sale_out_qty");
					ADJUST_QTY += rst.getDouble("ADJUST_QTY");
					STOCK_SHORT_QTY += rst.getDouble("STOCK_SHORT_QTY");
					onhand_qty += rst.getDouble("onhand_qty");
					onhand_amt += rst.getDouble("onhand_amt");

				}//while
				
			}//for
			}//if
			//add Summary Row
			item = new ReportAllBean();
			item.setStoreCode("");
			if("PensItem".equalsIgnoreCase(c.getSummaryType())){
			   item.setPensItem("");
			}
			item.setGroup("");
			item.setBeginingQty(Utils.decimalFormat(BEGINING_QTY,Utils.format_current_no_disgit));
			item.setSaleInQty(Utils.decimalFormat(sale_in_qty,Utils.format_current_no_disgit));
			item.setSaleReturnQty(Utils.decimalFormat(sale_return_qty,Utils.format_current_no_disgit));
			item.setSaleOutQty(Utils.decimalFormat(sale_out_qty,Utils.format_current_no_disgit));
			item.setAdjustQty(Utils.decimalFormat(ADJUST_QTY,Utils.format_current_no_disgit));
			item.setStockShortQty(Utils.decimalFormat(STOCK_SHORT_QTY,Utils.format_current_no_disgit));
			item.setOnhandQty(Utils.decimalFormat(onhand_qty,Utils.format_current_no_disgit));
			item.setOnhandAmt(Utils.decimalFormat(onhand_amt,Utils.format_current_2_disgit));
			
			logger.debug("**query success***");
			//convert Map to List
			pos = new ArrayList<ReportAllBean>(rowMap.values());
			logger.debug("**Start sort***");
			//Sort by StoreCode,GroupCode
			 Collections.sort(pos, ReportAllBean.Comparators.STORE_CODE_GROUP_ASC);
			 
			c.setSummary(item);
			c.setItemsList(pos);
			
			logger.debug("** success***");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return c;
    }
	 
	public ActionForward genMonthEnd(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genMonthEnd");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String storeType = "";
		String yearMonth = "";
		String[] results = new String[3];
		try{
			storeType = Utils.isNull(request.getParameter("storeType"));
			if("lotus".equalsIgnoreCase(storeType)){
				
				//results = GenerateMonthEndLotus.generateMonthLotus(reportsForm.getBean(), user);
				if("".equals(results[2])){
				  request.setAttribute("Message", "Generate Month End เรียบร้อยแล้ว");
				}else{
				  request.setAttribute("Message", "ไม่สามารถ Generate Month End ได้ \n "+results[2]);
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	
	public ActionForward genEndDate(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genMonthEnd");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String storeType = "";
		String yearMonth = "";
		String[] results = new String[3];
		try{
			storeType = Utils.isNull(request.getParameter("storeType"));
			if("lotus".equalsIgnoreCase(storeType)){
				
				/*results = GenerateEndDateLotus.generateEndDateLotus(summaryForm.getOnhandSummary(), user);
				if("".equals(results[2])){
				  request.setAttribute("Message", "Generate End Date เรียบร้อยแล้ว");
				}else{
				  request.setAttribute("Message", "ไม่สามารถ Generate End Date ได้ \n "+results[2]);
				}*/
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			logger.debug("PageAction:"+reportAllForm.getPageName());
			fileName ="Report BME EndDate Stock Lotus.xls";
		    
		    if(reportAllForm.getResults() != null && reportAllForm.getResults().size() > 0){
				htmlTable = genReportEndDateLotusHTML(request,reportAllForm,user);	
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
		     }else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
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

	/** For batch popup TaskName: GenStockOnhandTempTask **/
	/** Prepare parameter **/
	public ActionForward genStockOnhandRepTemp(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("submit genStockOnhandRepTemp");
		ReportAllForm aForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			batchParaMap.put(GenStockOnhandRepTempTask.PARAM_ASOF_DATE, aForm.getBean().getSalesDate());
			batchParaMap.put(GenStockOnhandRepTempTask.PARAM_STORE_CODE, aForm.getBean().getPensCustCodeFrom());
			
			logger.debug("storeCode:"+aForm.getBean().getPensCustCodeFrom());
			logger.debug("asOfDate:"+aForm.getBean().getSalesDate());
			
			request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
			request.setAttribute("BATCH_TASK_NAME",BatchTaskConstants.GEN_STOCK_ONHAND_REP_TEMP);//set to popup page to BatchTask
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
		}
		return mapping.findForward("reportAll");
	}
	
	/** For batch popup TaskName: GenStockOnhandTempTask **/
	/** Prepare parameter **/
	public ActionForward genReportCompareEndDateLotus(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("submit genReportCompareEndDateLotus");
		ReportAllForm aForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			batchParaMap.put(GenStockOnhandRepTempTask.PARAM_ASOF_DATE, aForm.getBean().getSalesDate());
			batchParaMap.put(GenStockOnhandRepTempTask.PARAM_STORE_CODE, aForm.getBean().getPensCustCodeFrom());
			
			logger.debug("storeCode:"+aForm.getBean().getPensCustCodeFrom());
			logger.debug("asOfDate:"+aForm.getBean().getSalesDate());
			
			request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
			request.setAttribute("BATCH_TASK_NAME",BatchTaskConstants.GEN_REPORT_COMP_ENDDATE_LOTUS);//set to popup page to BatchTask
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
		}
		return mapping.findForward("reportAll");
	}
	
	/** For batch popup after Task success**/
	/** For display BatchTask Result **/
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		ReportAllForm aForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("searchBatch :pageName["+pageName+"]");
	         aForm.setResults(null);
	         
			 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
			 logger.debug("batchTaskForm result size:"+batchTaskForm.getResults().length);
			 
			 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
			 
			 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		try {
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
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
	
	public StringBuffer genReportEndDateLotusHTML(HttpServletRequest request,ReportAllForm aForm,User user){
		StringBuffer h = new StringBuffer("");
		int colspan = 13;
		String page = aForm.getPageName();
		List<ReportAllBean> list = aForm.getResults();
		try{
			logger.debug("SummaryType:"+aForm.getBean().getSummaryType());
			
			if("GroupCode".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				colspan = 12;
			}
			//Head Style
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงานปิดรอบ LOTUS(EndDate)</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+aForm.getBean().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+aForm.getBean().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Pens Item From:"+aForm.getBean().getPensItemFrom()+"  Pens Item To:"+aForm.getBean().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Group:"+aForm.getBean().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >ปิดสต๊อกล่าสุดวันที่:"+aForm.getBean().getEndDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>รหัสสาขา</th> \n");
				  h.append("<th>ชื่อสาขา</th> \n");
				  if("PensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				     h.append("<th>PensItem</th> \n");
				  }
				  h.append("<th>Group</th> \n");
				  h.append("<th>Begining Qty</th> \n");
				  h.append("<th>Sale In Qty</th> \n");
				  h.append("<th>Sale Return Qty </th> \n");
				  h.append("<th>Sales Out Qty </th> \n");
				  h.append("<th>Adjust Qty</th> \n");
				  h.append("<th>Stock short </th> \n");
				  h.append("<th>Onhand Qty </th> \n");
				  h.append("<th>Price List </th> \n");
				  h.append("<th>Amount </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					ReportAllBean s = (ReportAllBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
					     h.append("<td>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getBeginingQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getAdjustQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getStockShortQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getOnhandQty()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getRetailPriceBF()+"</td> \n");
					  h.append("<td class='currency'>"+s.getOnhandAmt()+"</td> \n");
					h.append("</tr>");
				}
				
				ReportAllBean s = (ReportAllBean)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  if("PensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				     h.append("<td></td> \n");
				  }
				  h.append("<td class='colum_head'>Total</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getBeginingQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getSaleInQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getSaleReturnQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getSaleOutQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getAdjustQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getStockShortQty()+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+s.getOnhandQty()+"</td> \n");
				  h.append("<td class='currency_bold'></td> \n");
				  h.append("<td class='currency_bold'>"+s.getOnhandAmt()+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	public static String getEndDateStock(String storeCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		String r = "";
		try {
			//Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select distinct max(ending_date) as max_ending_date FROM PENSBME_ENDDATE_STOCK WHERE 1=1 ");
			sql.append("\n and store_code ='"+storeCode+"'");
		
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				r = DateUtil.stringValue(rst.getDate("max_ending_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			}
			
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		
	}
	public static String getEndSaleDateLotus(String storeCode,String asOfDate) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		String r = "";
		try {
			Date asofDateTemp = DateUtil.parse(asOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			sql.append("\n select distinct max(sales_date) as max_ending_date FROM PENSBME_SALES_FROM_LOTUS WHERE 1=1 ");
			sql.append("\n and pens_cust_code ='"+storeCode+"'");
		
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				if(rst.getDate("max_ending_date").before(asofDateTemp)){
				   r = DateUtil.stringValue(rst.getDate("max_ending_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				}else{
				   r = asOfDate;
				}
			}
			
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static String getEndDateStockTemp(String storeCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		String r = "";
		try {
			//Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n select distinct max(ending_date) as max_ending_date FROM PENSBME_ENDDATE_STOCK_TEMP WHERE 1=1 ");
			sql.append("\n and store_code ='"+storeCode+"'");
		
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				r = DateUtil.stringValue(rst.getDate("max_ending_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			}
			
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		
	}
 
}
