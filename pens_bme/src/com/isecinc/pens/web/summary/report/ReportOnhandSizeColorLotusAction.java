package com.isecinc.pens.web.summary.report;

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

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.sql.ReportSizeColorLotus_SQL;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class ReportOnhandSizeColorLotusAction {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static SummaryForm process(HttpServletRequest request,User user,SummaryForm summaryForm) throws Exception{
	Connection conn = null;
	List<StoreBean> storeList = null;
	OnhandSummary c = summaryForm.getOnhandSummary();
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
	List<OnhandSummary> rowAllList = new ArrayList<OnhandSummary>();
	OnhandSummary item = null;
	OnhandSummary prevItem = null;
	List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
	try{
		//set Page Name
		summaryForm.setPage("sizeColorLotus");
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
			Date initDate = new SummaryDAO().searchInitDateLotus(conn,c.getPensCustCodeFrom());
			
			logger.debug("initDate:"+initDate);
			logger.debug("asOfDate:"+asOfDate);

			if(initDate !=null){
				if(asOfDate.before(initDate)){
					summaryForm.setResults(null);
					request.setAttribute("Message", "วันที่ as of ต้องมากกว่าเท่ากับวันที่นับสต๊อกตั้งต้น");
					pass = false;
				}
			}
			if(pass){
				List<OnhandSummary> results = null;
				results = searchSizeColorLotusDetail(summaryForm,summaryForm.getOnhandSummary(),initDate,user);
				
				if (results != null  && results.size() >0) {
					summaryForm.setResults(results);
					summaryForm.getOnhandSummary().setInitDate(DateUtil.stringValue(initDate,DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					
					ImportDAO importDAO = new ImportDAO();
					Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
					if(m != null)
					   summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
												
				} else {
					summaryForm.setResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
			}
		}else{
			//Get AllStore Lotus
			if(storeCodeCheck.indexOf("ALL") != -1){
				//All
				storeList = StoreDAO.getStoreList(conn, PickConstants.STORE_TYPE_LOTUS_CODE);
			}else{
				//StoreCode more 1> 020047-1,020049-4
				storeList = StoreDAO.getStoreList(conn, PickConstants.STORE_TYPE_LOTUS_CODE,Utils.converToTextSqlIn(storeCodeCheck));
			}
			//Loop By StoreList
			if(storeList != null && storeList.size() >0){
				Date initDate = null;
				for(int i=0;i<storeList.size();i++){
					//Loop Step by Store Code
					StoreBean storeBean = storeList.get(i);
					c.setPensCustCodeFrom(storeBean.getStoreCode());
					//Get InitDate By StoreCode
					initDate = new SummaryDAO().searchInitDateLotus(conn,c.getPensCustCodeFrom());
					
					sql = ReportSizeColorLotus_SQL.genSQL(conn, c, initDate, summaryForm.getSummaryType());
					
					stmt = conn.createStatement();
					rst = stmt.executeQuery(sql.toString());
					while (rst.next()) {
						item = new OnhandSummary();
						
						item.setStoreCode(Utils.isNull(rst.getString("store_code")));
						item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
						item.setStoreName(Utils.isNull(rst.getString("store_name")));
						
						if("PensItem".equalsIgnoreCase(summaryForm.getSummaryType()) || Utils.isNull(summaryForm.getSummaryType()).equals("") ){
						  item.setPensItem(rst.getString("pens_item"));
						  item.setMaterialMaster(rst.getString("material_master"));
						  item.setBarcode(rst.getString("barcode"));
						}
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
				item = new OnhandSummary();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(summaryForm.getSummaryType())){
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
				if("GroupCode".equalsIgnoreCase(summaryForm.getSummaryType())){
					//Collections.sort(rowAllList, OnhandSummary.Comparators.STORE_CODE_GROUP_ASC);
				}else{
					//Collections.sort(rowAllList, OnhandSummary.Comparators.STORE_CODE_GROUP_ASC);
				}
				 
				//c.setSummary(item);
				//c.setItemsList(pos);
			}//if
			
			if (rowAllList != null  && rowAllList.size() >0) {
				summaryForm.setResults(rowAllList);
				summaryForm.setSummary(item);
				summaryForm.getOnhandSummary().setInitDate("");
				summaryForm.getOnhandSummary().setPensCustCodeFrom("ALL");			
				summaryForm.getOnhandSummary().setPensCustNameFrom("ALL");				
			} else {
				summaryForm.setResults(null);
				summaryForm.setSummary(null);
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
	return summaryForm;
 }
	
	
 public static List<OnhandSummary> searchSizeColorLotusDetail(SummaryForm f,OnhandSummary c,Date initDate,User user) throws Exception{
	   Statement stmt = null;
		ResultSet rst = null;
		List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		logger.debug("summaryType["+f.getSummaryType()+"]");
		try {
			conn = DBConnection.getInstance().getConnection();
	     		
			sql = ReportSizeColorLotus_SQL.genSQL(conn, c, initDate, f.getSummaryType());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			while (rst.next()) {
				OnhandSummary item = new OnhandSummary();
				
				item.setStoreCode(Utils.isNull(rst.getString("store_code")));
				item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
				item.setStoreName(Utils.isNull(rst.getString("store_name")));
				
				if("PensItem".equalsIgnoreCase(f.getSummaryType()) || Utils.isNull(f.getSummaryType()).equals("") ){
				  item.setPensItem(rst.getString("pens_item"));
				  item.setMaterialMaster(rst.getString("material_master"));
				  item.setBarcode(rst.getString("barcode"));
				}
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
				conn.close();
			} catch (Exception e) {}
		}
		return pos;
    }
	
}
