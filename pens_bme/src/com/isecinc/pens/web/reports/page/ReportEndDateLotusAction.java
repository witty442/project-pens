package com.isecinc.pens.web.reports.page;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.summary.process.GenerateStockEndDateLotus;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.GenStockOnhandTempTask;
import com.isecinc.pens.web.reports.ReportsBean;
import com.isecinc.pens.web.reports.ReportsForm;
import com.isecinc.pens.web.reports.sql.ReportEndDateLotusSQL;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

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
		String forward = "reports";
		ReportsForm reportsForm = (ReportsForm) form;
		ReportsBean bean = new ReportsBean();
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 reportsForm.setResults(null);
				
				 bean = new ReportsBean();
				 bean.setDispHaveQty("true");
				 reportsForm.setBean(bean);
				 reportsForm.setPageName(Utils.isNull(request.getParameter("pageName")));
				 
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
		ReportsForm reportsForm = (ReportsForm) form;
		User user = (User) request.getSession().getAttribute("user");
		logger.debug("page["+Utils.isNull(request.getParameter("page"))+"]");
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			
			//Validate asOfdate Must over than max End Date
			String[] resultsValidate = ControlConstantsDB.canGenEndDateLotus(reportsForm.getBean().getPensCustCodeFrom(),reportsForm.getBean().getSalesDate());
			if(resultsValidate[0].equals("false")){
				reportsForm.setResults(null);
				request.setAttribute("Message", "กรุณากรอก วันที่ขาย (As Of) มากกว่า วันที่ปิดสต๊อกล่าสุด ");
			}else{
				List<ReportsBean> results = null;
				ReportsBean sumBean = searchReportEndDateLotusAllStore(reportsForm.getBean(),user);
				
				results = sumBean.getItemsList();
				if (results != null  && results.size() >0) {
					reportsForm.setResults(results);
					request.getSession().setAttribute("summary", sumBean.getSummary());
					
					//logger.debug("results:"+summaryForm.getResults());
					ImportDAO importDAO = new ImportDAO();
					Master m = importDAO.getStoreName("Store", reportsForm.getBean().getPensCustCodeFrom());
					if(m != null)
						reportsForm.getBean().setPensCustNameFrom(m.getPensDesc());
					
				} else {
					reportsForm.setResults(null);
					request.getSession().setAttribute("summary", null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
				
				//By StoreCode ONLY ,All Store Not Set
				if( !Utils.isNull(reportsForm.getBean().getPensCustCodeFrom()).equals("ALL")
					&& Utils.isNull(reportsForm.getBean().getPensCustCodeFrom()).indexOf(",") == -1){
					reportsForm.getBean().setEndDate(GenerateStockEndDateLotus.getEndDateStock(reportsForm.getBean().getPensCustCodeFrom()));
					reportsForm.getBean().setEndSaleDate(GenerateStockEndDateLotus.getEndSaleDateLotus(reportsForm.getBean().getPensCustCodeFrom(),reportsForm.getBean().getSalesDate()) );
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "reports";
	}
	 public ReportsBean searchReportEndDateLotusAllStore(ReportsBean c,User user) throws Exception{
	   Statement stmt = null;
		ResultSet rst = null;
		List<ReportsBean> pos = new ArrayList<ReportsBean>();
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
		Map<String, ReportsBean> rowMap = new HashMap<String, ReportsBean>();
		ReportsBean item = null;
		ReportsBean prevItem = null;
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
					item = new ReportsBean();
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
			item = new ReportsBean();
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
			
			//convert Map to List
			pos = new ArrayList<ReportsBean>(rowMap.values());
			//Sort by StoreCode,GroupCode
			 Collections.sort(pos, ReportsBean.Comparators.STORE_CODE_GROUP_ASC);
			 
			c.setSummary(item);
			c.setItemsList(pos);
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
		ReportsForm reportsForm = (ReportsForm) form;
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
		return mapping.findForward("reports");
	}
	
	public ActionForward genEndDate(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("genMonthEnd");
		ReportsForm reportsForm = (ReportsForm) form;
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
		return mapping.findForward("reports");
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ReportsForm reportsForm = (ReportsForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			logger.debug("PageAction:"+request.getParameter("page"));
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reports");
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
		ReportsForm aForm = (ReportsForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Prepare Parameter to BatchTask
			Map<String, String> batchParaMap = new HashMap<String, String>();
			batchParaMap.put(GenStockOnhandTempTask.PARAM_ASOF_DATE, aForm.getBean().getAsOfDate());
			batchParaMap.put(GenStockOnhandTempTask.PARAM_STORE_CODE, aForm.getBean().getPensCustCodeFrom());
			
			logger.debug("storeCode:"+aForm.getBean().getPensCustCodeFrom());
			
			request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
			request.setAttribute("action","submitedGenStockOnhandTemp");//set to popup page to BatchTask
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
		}
		return mapping.findForward("reports");
	}
	/** For batch popup after Task success**/
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		ReportsForm aForm = (ReportsForm) form;
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
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("reports");
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReportsForm reportsForm = (ReportsForm) form;
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
