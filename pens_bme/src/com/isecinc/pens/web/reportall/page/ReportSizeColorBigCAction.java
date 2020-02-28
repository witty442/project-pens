package com.isecinc.pens.web.reportall.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.GenStockOnhandRepTempTask;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.sql.ReportSizeColorBigCSQL;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class ReportSizeColorBigCAction  extends I_Action{
	
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
		ReportAllForm reportAllForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		logger.debug("page["+Utils.isNull(request.getParameter("page"))+"]");
		try {
			//Clear session 
			request.getSession().setAttribute("summary",null);
			request.getSession().setAttribute("BATCH_TASK_RESULT",null);
			
			 String queryStr= request.getQueryString();
			 if(queryStr.indexOf("d-") != -1){
			 	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
			 	System.out.println("queryStr:"+queryStr);
			 }
			 
			//Case link page in display no search again
			logger.debug("currentPage:"+request.getParameter(queryStr));
			if(request.getParameter(queryStr) != null){
				
				Master m = new ImportDAO().getStoreName("Store", reportAllForm.getBean().getPensCustCodeFrom());
				if(m != null)
					reportAllForm.getBean().setPensCustNameFrom(m.getPensDesc());
			}else{
				reportAllForm = searchReport(request,user, reportAllForm);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "reportAll";
	}
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			logger.debug("PageName:"+reportAllForm.getPageName());
			if(reportAllForm.getResults() != null && reportAllForm.getResults().size() > 0){
				htmlTable = genBigCSizeColorHTML(request,reportAllForm,user);	
				
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
		return "view";
	}

	/** For batch popup TaskName: GenStockOnhandTempTask **/
	/** Prepare parameter **/
	public ActionForward genStockOnhandRepTemp(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("submit genStockOnhandTemp");
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
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
	public StringBuffer genBigCSizeColorHTML(HttpServletRequest request,ReportAllForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		String colSpan= "13";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รายงาน Stock B'me Stock onhand At BigC ( ระดับสี/ไซร์ ) </td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >จากวันที่ขาย:"+form.getBean().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >รหัสร้านค้า:"+form.getBean().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >วันที่ล่าสุดที่มีการตรวจนับสต็อก:"+form.getBean().getInitDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Pens Item From:"+form.getBean().getPensItemFrom()+"  Pens Item To:"+form.getBean().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'  >Group:"+form.getBean().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
			
			if(form.getResults() != null){
			    List<ReportAllBean> list = (List<ReportAllBean>)form.getResults();
			    
			    h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td>Sub Inv</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>PensItem</td> \n");
				  h.append("<td>Materila Master</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Initial Stock</td> \n");
				  h.append("<td>Trans In Qty</td> \n");
				  h.append("<td>Sale Out Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>Adjust Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					ReportAllBean s = (ReportAllBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getSubInv()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getBarcode())+"</td> \n");
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getTransInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getAdjustSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
				
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	public static ReportAllForm searchReport(HttpServletRequest request,User user,ReportAllForm aForm) throws Exception{
	Connection conn = null;
	List<StoreBean> storeList = null;
	ReportAllBean c = aForm.getBean();
	String storeCodeCheck = "";
	String[] storeCodeCheckArr = null;
	boolean pass = true;
	Statement stmt = null;
	ResultSet rst = null;
	StringBuilder sql = new StringBuilder();
	double BEGINING_QTY = 0;
	double trans_in_qty = 0;
	double sale_return_qty = 0;
	double sale_out_qty = 0;
	double ADJUST_QTY = 0;
	double onhand_qty = 0;
	String key = "";
	List<ReportAllBean> rowAllList = new ArrayList<ReportAllBean>();
	ReportAllBean item = null;
	ReportAllBean prevItem = null;
	List<ReportAllBean> pos = new ArrayList<ReportAllBean>();
	try{
	
		//Init Connection
		conn = DBConnection.getInstance().getConnection();
		
		//Check All Store 
		storeCodeCheck = Utils.isNull(c.getPensCustCodeFrom());
		storeCodeCheckArr = Utils.isNull(c.getPensCustCodeFrom()).split("\\,");
		
		//One StoreCode
		if( !Utils.isNull(storeCodeCheck).equals("ALL") && storeCodeCheckArr.length==1){
			//Get By Store
			storeList =  new ArrayList<StoreBean>();
			StoreBean storeBean = new StoreBean();
			storeBean.setStoreCode(c.getPensCustCodeFrom());
			storeList.add(storeBean);
			
			//Validate Initial Date
			Date asOfDate = DateUtil.parse(c.getSalesDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			Date initDate = new SummaryDAO().searchInitDateBigC(conn,c.getPensCustCodeFrom());
			
			logger.debug("initDate:"+initDate);
			logger.debug("asOfDate:"+asOfDate);

			if(initDate !=null){
				if(asOfDate.before(initDate)){
					aForm.setResults(null);
					request.setAttribute("Message", "วันที่ as of ต้องมากกว่าเท่ากับวันที่นับสต๊อกตั้งต้น");
					pass = false;
				}
			}
			if(pass){
				List<ReportAllBean> results = null;
				results = searchSizeColorBigCDetail(conn,aForm,initDate,user);
				
				if (results != null  && results.size() >0) {
					aForm.setResults(results);
					
					aForm.getBean().setInitDate(DateUtil.stringValue(initDate,DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					
					ImportDAO importDAO = new ImportDAO();
					Master m = importDAO.getStoreName("Store", aForm.getBean().getPensCustCodeFrom());
					if(m != null)
						aForm.getBean().setPensCustNameFrom(m.getPensDesc());
												
				} else {
					aForm.setResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
			}
		}else{
			//Get AllStore Lotus
			if(storeCodeCheck.indexOf("ALL") != -1){
				//All
				storeList = StoreDAO.getStoreList(conn, PickConstants.STORE_TYPE_BIGC_CODE);
			}else{
				//StoreCode more 1> 020047-1,020049-4
				storeList = StoreDAO.getStoreList(conn, PickConstants.STORE_TYPE_BIGC_CODE,SQLHelper.converToTextSqlIn(storeCodeCheck));
			}
			//Loop By StoreList
			if(storeList != null && storeList.size() >0){
				Date initDate = null;
				for(int i=0;i<storeList.size();i++){
					//Loop Step by Store Code
					StoreBean storeBean = storeList.get(i);
					c.setPensCustCodeFrom(storeBean.getStoreCode());
					//Get InitDate By StoreCode
					initDate = new SummaryDAO().searchInitDateBigC(conn,c.getPensCustCodeFrom());
				
					sql = ReportSizeColorBigCSQL.genSQL(conn, c, initDate);
					
					stmt = conn.createStatement();
					rst = stmt.executeQuery(sql.toString());
					while (rst.next()) {
						item = new ReportAllBean();
						
						item.setStoreCode(Utils.isNull(rst.getString("store_code")));
						item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
						item.setStoreName(Utils.isNull(rst.getString("store_name")));
						
						//if("PensItem".equalsIgnoreCase(summaryForm.getSummaryType()) || Utils.isNull(summaryForm.getSummaryType()).equals("") ){
						  item.setPensItem(rst.getString("pens_item"));
						  item.setMaterialMaster(rst.getString("material_master"));
						  item.setBarcode(rst.getString("barcode"));
						//}
						item.setGroup(rst.getString("group_type"));
						item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("init_sale_qty"),Utils.format_current_no_disgit));
						item.setTransInQty(Utils.decimalFormat(rst.getDouble("trans_in_qty"),Utils.format_current_no_disgit));
						item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
						item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
						item.setAdjustSaleQty(Utils.decimalFormat(rst.getDouble("SALE_ADJUST_QTY"),Utils.format_current_no_disgit));
						item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
	
						rowAllList.add(item);
						
						//Sum All Row
						BEGINING_QTY += rst.getDouble("init_sale_qty");
						trans_in_qty += rst.getDouble("trans_in_qty");
						sale_return_qty += rst.getDouble("sale_return_qty");
						sale_out_qty += rst.getDouble("sale_out_qty");
						ADJUST_QTY += rst.getDouble("SALE_ADJUST_QTY");
						onhand_qty += rst.getDouble("onhand_qty");

					}//while
				}//for
				//add Summary Row
				item = new ReportAllBean();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				   item.setPensItem("");
				}
				item.setGroup("");
				item.setInitSaleQty(Utils.decimalFormat(BEGINING_QTY,Utils.format_current_no_disgit));
				item.setTransInQty(Utils.decimalFormat(trans_in_qty,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(sale_return_qty,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(sale_out_qty,Utils.format_current_no_disgit));
				item.setAdjustQty(Utils.decimalFormat(ADJUST_QTY,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhand_qty,Utils.format_current_no_disgit));
				
				//convert Map to List
				//pos = new ArrayList<OnhandSummary>(rowMap.values());
				//Sort by StoreCode,GroupCode
				if("GroupCode".equalsIgnoreCase(aForm.getBean().getSummaryType())){
					//Collections.sort(rowAllList, OnhandSummary.Comparators.STORE_CODE_GROUP_ASC);
				}else{
					//Collections.sort(rowAllList, OnhandSummary.Comparators.STORE_CODE_GROUP_ASC);
				}
				 
				//c.setSummary(item);
				//c.setItemsList(pos);
			}//if
			
			if (rowAllList != null  && rowAllList.size() >0) {
				aForm.setResults(rowAllList);
				aForm.setSummary(item);
				aForm.getBean().setInitDate("");
				aForm.getBean().setPensCustCodeFrom("ALL");			
				aForm.getBean().setPensCustNameFrom("ALL");				
			} else {
				aForm.setResults(null);
				aForm.setSummary(null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
		}

	}catch(Exception e){
		logger.error(e.getMessage(),e);
	}finally{
		if(stmt != null){
			stmt.close();stmt=null;
		}
		if(rst != null){
			rst.close();rst=null;
		}
		if(conn != null){
			conn.close();conn=null;
		}
	}
	return aForm;
 }
	
	
 public static List<ReportAllBean> searchSizeColorBigCDetail(Connection conn ,ReportAllForm f,Date initDate,User user) throws Exception{
	   Statement stmt = null;
		ResultSet rst = null;
		List<ReportAllBean> pos = new ArrayList<ReportAllBean>();
		StringBuilder sql = new StringBuilder();
		//logger.debug("summaryType["+f.getSummaryType()+"]");
		try {
			sql = ReportSizeColorBigCSQL.genSQL(conn, f.getBean(), initDate);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				ReportAllBean item = new ReportAllBean();
				
				item.setStoreCode(Utils.isNull(rst.getString("store_code")));
				item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
				item.setStoreName(Utils.isNull(rst.getString("store_name")));
				
				//if("PensItem".equalsIgnoreCase(f.getSummaryType()) || Utils.isNull(f.getSummaryType()).equals("") ){
				  item.setPensItem(rst.getString("pens_item"));
				  item.setMaterialMaster(rst.getString("material_master"));
				  item.setBarcode(rst.getString("barcode"));
				//}
				item.setGroup(rst.getString("group_type"));
				item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("init_sale_qty"),Utils.format_current_no_disgit));
				item.setTransInQty(Utils.decimalFormat(rst.getDouble("trans_in_qty"),Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
				item.setAdjustSaleQty(Utils.decimalFormat(rst.getDouble("SALE_ADJUST_QTY"),Utils.format_current_no_disgit));
				
				item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
				
				pos.add(item);
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return pos;
    }

}
