package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.DiffStockSummary;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.sql.ReportEndDateLotusSQL;
import com.isecinc.pens.sql.ReportMonthEndLotusSQL;
import com.isecinc.pens.sql.ReportOnhandAsOfKingSQL;
import com.isecinc.pens.sql.ReportOnhandAsOf_Robinson_SQL;
import com.isecinc.pens.sql.ReportOnhandBigC_ASOF_SQL;
import com.isecinc.pens.sql.ReportOnhandBigC_SPSQL;
import com.isecinc.pens.sql.ReportOnhandLotus_SQL;
import com.isecinc.pens.sql.ReportOnhandMTTDetailSQL;
import com.isecinc.pens.sql.ReportOnhandMTTSQL;
import com.isecinc.pens.sql.ReportOnhandTops_SQL;
import com.isecinc.pens.sql.ReportSizeColorBigC_SQL;
import com.isecinc.pens.sql.ReportSizeColorLotus_SQL;
import com.isecinc.pens.sql.ReportStockWacoalLotus_SQL;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class SummaryDAO {

	private static Logger logger = Logger.getLogger("PENS");
	
	  public List<OnhandSummary> search(OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String tableName ="";
			try {
				conn = DBConnection.getInstance().getConnection();
				
				if(Utils.isNull(c.getLocation()).equalsIgnoreCase("StockStore")){
					tableName = "PENSBME_ONHAND_BME";
				}else if(Utils.isNull(c.getLocation()).equalsIgnoreCase("StockFriday")){
					tableName = "PENSBME_ONHAND_BME_FRIDAY";
				}else if(Utils.isNull(c.getLocation()).equalsIgnoreCase("StockOShopping")){
					tableName = "PENSBME_ONHAND_BME_OSHOPPING";
				}else if(Utils.isNull(c.getLocation()).equalsIgnoreCase("Stock7Catalog")){
					tableName = "PENSBME_ONHAND_BME_7CATALOG";
				}else if(Utils.isNull(c.getLocation()).equalsIgnoreCase("StockTVDirect")){
					tableName = "PENSBME_ONHAND_BME_TVDIRECT";
				}
				
				sql.delete(0, sql.length());
				sql.append("\n  SELECT h.*  from "+tableName+" h ");
				sql.append("\n  where 1=1  ");
			
				if( !Utils.isNull(c.getItemCodeFrom()).equals("") && !Utils.isNull(c.getItemCodeTo()).equals("")){
					sql.append(" and item >='"+c.getItemCodeFrom()+"' \n");
					sql.append(" and item <='"+c.getItemCodeTo()+"' \n");
				}
				if( Utils.isNull(c.getItemCodeFrom()).equals("") || Utils.isNull(c.getItemCodeTo()).equals("")){
					if(!Utils.isNull(c.getItemCodeFrom()).equals("")){
						sql.append(" and item ='"+c.getItemCodeFrom()+"' \n");	
					}
					if(!Utils.isNull(c.getItemCodeTo()).equals("")){
						sql.append(" and item ='"+c.getItemCodeTo()+"' \n");
					}
				}
				
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append(" and pens_item >='"+c.getPensItemFrom()+"' \n");
					sql.append(" and pens_item <='"+c.getPensItemTo()+"' \n");
				}
				if( Utils.isNull(c.getPensItemFrom()).equals("") || Utils.isNull(c.getPensItemTo()).equals("")){
					if(!Utils.isNull(c.getPensItemFrom()).equals("")){
						sql.append(" and pens_item ='"+c.getPensItemFrom()+"' \n");	
					}
					if(!Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append(" and pens_item ='"+c.getPensItemTo()+"' \n");
					}
				}
				
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append(" and group_item in ("+Utils.converToTextSqlIn(c.getGroup())+") \n");
				}
				if( !Utils.isNull(c.getStatus()).equals("")){
					sql.append(" and status ='"+Utils.isNull(c.getStatus())+"' \n");
				}
				
				if( "".equals(Utils.isNull(c.getDispZeroStock()))){
					sql.append(" AND Onhand_QTY <> 0  \n");
				}
				
				sql.append("\n  ORDER BY h.group_item  ,h.item asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					item.setAsOfDate(Utils.stringValue(rst.getDate("as_of_date"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setMaterialMaster(rst.getString("Material_master"));
					item.setBarcode(rst.getString("BARCODE"));
					item.setOnhandQty(rst.getString("Onhand_QTY"));
					
					item.setWholePriceBF(Utils.decimalFormat(rst.getDouble("Whole_Price_BF"),Utils.format_current_2_disgit)+" ");
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("Retail_Price_BF"),Utils.format_current_2_disgit)+" ");
					
					item.setItem(rst.getString("item"));
					item.setItemDesc(rst.getString("item_desc"));
					item.setFileName(rst.getString("file_name"));
					item.setGroup(rst.getString("group_item"));
					item.setStatus(rst.getString("STATUS"));
					item.setMessage(rst.getString("MESSAGE"));
					
					item.setPensItem(rst.getString("PENS_ITEM"));
					
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
	  public List<PhysicalSummary> search(PhysicalSummary c,User user) throws Exception{
		    Statement stmt = null;
			ResultSet rst = null;
			List<PhysicalSummary> pos = new ArrayList<PhysicalSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				
				sql.delete(0, sql.length());
				sql.append("\n  SELECT p.*,m.pens_desc as cust_name from PENSBME_PHYSICAL_COUNT p ,PENSBME_MST_REFERENCE m");
				sql.append("\n  where 1=1  and p.cust_code = m.pens_value and m.reference_code ='Store'");
			

				if( !Utils.isNull(c.getCountDateFrom()).equals("") && !Utils.isNull(c.getCountDateTo()).equals("")){
					
					Date salesDateFromParam = Utils.parse(c.getCountDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					Date salesDateToParam = Utils.parse(c.getCountDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
					String dateFrom = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					String dateTo = Utils.stringValue(salesDateToParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(p.count_date) >= to_date('"+dateFrom+"','dd/mm/yyyy') \n");
					sql.append(" and trunc(p.count_date) <= to_date('"+dateTo+"','dd/mm/yyyy') \n");
				}
				if( Utils.isNull(c.getCountDateFrom()).equals("") || Utils.isNull(c.getCountDateTo()).equals("")){
					
					if( !Utils.isNull(c.getCountDateFrom()).equals("") ){
						Date salesDateFromParam = Utils.parse(c.getCountDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						String dateFrom = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
						sql.append(" and trunc(p.count_date) = to_date('"+dateFrom+"','dd/mm/yyyy') \n");
					}
					
					if(!Utils.isNull(c.getCountDateTo()).equals("")){
						Date salesDateToParam = Utils.parse(c.getCountDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						String dateTo = Utils.stringValue(salesDateToParam, Utils.DD_MM_YYYY_WITH_SLASH);
						sql.append(" and trunc(p.count_date) = to_date('"+dateTo+"','dd/mm/yyyy') \n");
					}
				}
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")&& !Utils.isNull(c.getPensCustCodeTo()).equals("")){
					sql.append(" and p.cust_code >='"+c.getPensCustCodeFrom()+"' \n");
					sql.append(" and p.cust_code <='"+c.getPensCustCodeTo()+"' \n");
				}
				if( Utils.isNull(c.getPensCustCodeFrom()).equals("") || Utils.isNull(c.getPensCustCodeTo()).equals("")){
					if(!Utils.isNull(c.getPensCustCodeFrom()).equals("")){
						sql.append(" and p.cust_code ='"+c.getPensCustCodeFrom()+"' \n");
					}
					if(!Utils.isNull(c.getPensCustCodeTo()).equals("")){
						sql.append(" and p.cust_code ='"+c.getPensCustCodeTo()+"' \n");
					}
				}
				
				if(!Utils.isNull(c.getFileName()).equals("")){
					sql.append(" and p.file_name LIKE '%"+c.getFileName()+"%' \n");
				}
				
				sql.append("\n  ORDER BY p.item asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					PhysicalSummary item = new PhysicalSummary();
					
					item.setItem(rst.getString("item"));
					item.setBarcode(rst.getString("BARCODE"));
					item.setPensCustCode(rst.getString("cust_code"));
					item.setPensCustName(rst.getString("cust_name"));
					item.setCountDate(Utils.stringValue(rst.getDate("count_date"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setFileName(rst.getString("file_name"));
					item.setCreateDate(Utils.stringValue(rst.getDate("create_date"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
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

	  public TransactionSummary search(TransactionSummary c,User user,String type) throws Exception{
		   TransactionSummary re = new TransactionSummary();
		   List<TransactionSummary> summaryList = new ArrayList<TransactionSummary>();
	    	Connection conn = null;
	    	try{
	    		conn = DBConnection.getInstance().getConnection();
	    		if("lotus".equalsIgnoreCase(type)){
	    		   re = searchLotus(c,conn,user);
	    		}else if("bigc".equalsIgnoreCase(type)){
	    		   summaryList = searchBigC(c,conn,user);	
	    		   re.setItemsList(summaryList);
	    		}else if("bigc_temp".equalsIgnoreCase(type)){
		    	   summaryList = searchBigC_TEMP(c,conn,user);	
		    	   re.setItemsList(summaryList);
	    		}else if("tops".equalsIgnoreCase(type)){
		    	   summaryList = searchTops(c,conn,user);	
		    	   re.setItemsList(summaryList);
	    		}else if("king".equalsIgnoreCase(type)){
			       re = searchKing(c,conn,user);	
			     
			    }
	    		
	    	}catch(Exception e){
	    		logger.error(e.getMessage(),e);
	    	}finally{
	    		if(conn != null){
	    			conn.close();conn= null;
	    		}
	    	}
	    	return re;
	    }
	  
	  public List<TransactionSummary> searchByGroupCode(TransactionSummary c,User user,String type) throws Exception{
	    	List<TransactionSummary> summaryList = new ArrayList<TransactionSummary>();
	    	
	    	Connection conn = null;
	    	try{
	    		conn = DBConnection.getInstance().getConnection();
	    		summaryList = searchByGroupCode(c,conn,user);
	    	}catch(Exception e){
	    		logger.error(e.getMessage(),e);
	    	}finally{
	    		if(conn != null){
	    			conn.close();conn= null;
	    		}
	    	}
	    	return summaryList;
	    }
	
	  public List<TransactionSummary> searchByGroupCode(TransactionSummary c,Connection conn,User user) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
			StringBuilder sql = new StringBuilder();
			boolean salesDateFlag = false;
			Date salesDateFromParam = null;
			Date salesDateToParam = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT * from PENSBME_SALES_FROM_LOTUS ");
				sql.append("\n  where 1=1  ");
			

				if( !Utils.isNull(c.getSalesDateFrom()).equals("")&& !Utils.isNull(c.getSalesDateTo()).equals("")){
					salesDateFlag = true;
					
					salesDateFromParam = Utils.parse(c.getSalesDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					salesDateToParam = Utils.parse(c.getSalesDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
					String dateFrom = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					String dateTo = Utils.stringValue(salesDateToParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(sales_date) >= to_date('"+dateFrom+"','dd/mm/yyyy') \n");
					sql.append(" and trunc(sales_date) <= to_date('"+dateTo+"','dd/mm/yyyy') \n");
				}
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
					sql.append(" and pens_cust_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") \n");
				}
				
				if(!Utils.isNull(c.getFileName()).equals("")){
					sql.append(" and file_name LIKE '%"+c.getFileName()+"%' \n");
				}
				
				sql.append("\n  ORDER BY sales_date ,pens_cust_code,style_no asc \n");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				logger.debug("salesDateFromParam:"+salesDateFromParam);
				logger.debug("salesDateToParam:"+salesDateToParam);
				
				if(salesDateFlag){
					//ps.setDate(1, new java.sql.Date(salesDateFromParam.getTime()));
					//ps.setDate(2, new java.sql.Date(salesDateToParam.getTime()));
				}
				rst = ps.executeQuery(sql.toString());
				
				while (rst.next()) {
					TransactionSummary item = new TransactionSummary();
					item.setVendor(rst.getString("VENDOR"));
					item.setName(rst.getString("NAME"));
					item.setApType(rst.getString("AP_TYPE"));
					item.setLeaseVendorType(rst.getString("LEASE_VENDOR_TYPE"));
					item.setStoreNo(rst.getString("STORE_NO"));
					item.setStoreName(rst.getString("STORE_NAME"));
					item.setStyleNo(rst.getString("STYLE_NO"));
					item.setDescription(rst.getString("DESCRIPTION"));
					item.setCol(rst.getString("COL"));
					item.setSizeType(rst.getString("SIZE_TYPE"));
					item.setSizes(rst.getString("SIZES"));
					item.setSalesDate(Utils.stringValue(rst.getDate("SALES_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setQty(Utils.decimalFormat(rst.getDouble("QTY"),Utils.format_current_2_disgit));
					item.setGrossSales(Utils.decimalFormat(rst.getDouble("GROSS_SALES"),Utils.format_current_2_disgit));
					item.setReturnAmt(Utils.decimalFormat(rst.getDouble("RETURN_AMT"),Utils.format_current_2_disgit));
					item.setNetSalesInclVat(Utils.decimalFormat(rst.getDouble("NET_SALES_INCL_VAT"),Utils.format_current_2_disgit));
					item.setVatAmt(Utils.decimalFormat(rst.getDouble("VAT_AMT"),Utils.format_current_2_disgit));
					item.setNetSalesExcVat(Utils.decimalFormat(rst.getDouble("NET_SALES_EXC_VAT"),Utils.format_current_2_disgit));
					item.setGpPercent(Utils.decimalFormat(rst.getDouble("GP_PERCENT"),Utils.format_current_2_disgit));
					item.setGpAmount(Utils.decimalFormat(rst.getDouble("GP_AMOUNT"),Utils.format_current_2_disgit));
					item.setVatOnGpAmount(Utils.decimalFormat(rst.getDouble("VAT_ON_GP_AMOUNT"),Utils.format_current_2_disgit));
					item.setGpAmountInclVat(Utils.decimalFormat(rst.getDouble("GP_AMOUNT_INCL_VAT"),Utils.format_current_2_disgit));
					item.setApAmount(Utils.decimalFormat(rst.getDouble("AP_AMOUNT"),Utils.format_current_2_disgit));
					item.setTotalVatAmt(Utils.decimalFormat(rst.getDouble("TOTAL_VAT_AMT"),Utils.format_current_2_disgit));
					item.setApAmountInclVat(Utils.decimalFormat(rst.getDouble("AP_AMOUNT_INCL_VAT"),Utils.format_current_2_disgit));

					item.setPensCustCode(rst.getString("PENS_CUST_CODE"));
					item.setPensCustDesc(rst.getString("PENS_CUST_DESC"));
					item.setPensGroup(rst.getString("PENS_GROUP"));
					item.setPensGroupType(rst.getString("PENS_GROUP_TYPE"));
					item.setSalesYear(rst.getString("SALES_YEAR"));
					item.setSalesMonth(rst.getString("SALES_MONTH"));
					item.setFileName(rst.getString("File_name"));

					item.setCreateDate(Utils.stringValue(rst.getDate("CREATE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setCreateUser(rst.getString("create_user"));
					
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	  
	  public TransactionSummary searchLotus(TransactionSummary c,Connection conn,User user) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
			StringBuilder sql = new StringBuilder();
			boolean salesDateFlag = false;
			Date salesDateFromParam = null;
			Date salesDateToParam = null;
			TransactionSummary re = new TransactionSummary();
			int totalQty = 0;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT * from PENSBME_SALES_FROM_LOTUS ");
				sql.append("\n  where 1=1  ");
			
				if( !Utils.isNull(c.getSalesDateFrom()).equals("")&& !Utils.isNull(c.getSalesDateTo()).equals("")){
					salesDateFlag = true;
					
					salesDateFromParam = Utils.parse(c.getSalesDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					salesDateToParam = Utils.parse(c.getSalesDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
					String dateFrom = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					String dateTo = Utils.stringValue(salesDateToParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(sales_date) >= to_date('"+dateFrom+"','dd/mm/yyyy') \n");
					sql.append(" and trunc(sales_date) <= to_date('"+dateTo+"','dd/mm/yyyy') \n");
				}
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
					sql.append(" and pens_cust_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") \n");
				}
				
				if(!Utils.isNull(c.getFileName()).equals("")){
					sql.append(" and file_name LIKE '%"+c.getFileName()+"%' \n");
				}
				
				sql.append("\n  ORDER BY sales_date ,pens_cust_code,style_no asc \n");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				logger.debug("salesDateFromParam:"+salesDateFromParam);
				logger.debug("salesDateToParam:"+salesDateToParam);
				
				if(salesDateFlag){
					//ps.setDate(1, new java.sql.Date(salesDateFromParam.getTime()));
					//ps.setDate(2, new java.sql.Date(salesDateToParam.getTime()));
				}
				rst = ps.executeQuery(sql.toString());
				
				while (rst.next()) {
					TransactionSummary item = new TransactionSummary();
					item.setVendor(rst.getString("VENDOR"));
					item.setName(rst.getString("NAME"));
					item.setApType(rst.getString("AP_TYPE"));
					item.setLeaseVendorType(rst.getString("LEASE_VENDOR_TYPE"));
					item.setStoreNo(rst.getString("STORE_NO"));
					item.setStoreName(rst.getString("STORE_NAME"));
					item.setStyleNo(rst.getString("STYLE_NO"));
					item.setDescription(rst.getString("DESCRIPTION"));
					item.setCol(rst.getString("COL"));
					item.setSizeType(rst.getString("SIZE_TYPE"));
					item.setSizes(rst.getString("SIZES"));
					item.setSalesDate(Utils.stringValue(rst.getDate("SALES_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setQty(Utils.decimalFormat(rst.getDouble("QTY"),Utils.format_current_2_disgit));
					item.setGrossSales(Utils.decimalFormat(rst.getDouble("GROSS_SALES"),Utils.format_current_2_disgit));
					item.setReturnAmt(Utils.decimalFormat(rst.getDouble("RETURN_AMT"),Utils.format_current_2_disgit));
					item.setNetSalesInclVat(Utils.decimalFormat(rst.getDouble("NET_SALES_INCL_VAT"),Utils.format_current_2_disgit));
					item.setVatAmt(Utils.decimalFormat(rst.getDouble("VAT_AMT"),Utils.format_current_2_disgit));
					item.setNetSalesExcVat(Utils.decimalFormat(rst.getDouble("NET_SALES_EXC_VAT"),Utils.format_current_2_disgit));
					item.setGpPercent(Utils.decimalFormat(rst.getDouble("GP_PERCENT"),Utils.format_current_2_disgit));
					item.setGpAmount(Utils.decimalFormat(rst.getDouble("GP_AMOUNT"),Utils.format_current_2_disgit));
					item.setVatOnGpAmount(Utils.decimalFormat(rst.getDouble("VAT_ON_GP_AMOUNT"),Utils.format_current_2_disgit));
					item.setGpAmountInclVat(Utils.decimalFormat(rst.getDouble("GP_AMOUNT_INCL_VAT"),Utils.format_current_2_disgit));
					item.setApAmount(Utils.decimalFormat(rst.getDouble("AP_AMOUNT"),Utils.format_current_2_disgit));
					item.setTotalVatAmt(Utils.decimalFormat(rst.getDouble("TOTAL_VAT_AMT"),Utils.format_current_2_disgit));
					item.setApAmountInclVat(Utils.decimalFormat(rst.getDouble("AP_AMOUNT_INCL_VAT"),Utils.format_current_2_disgit));

					item.setPensCustCode(rst.getString("PENS_CUST_CODE"));
					item.setPensCustDesc(rst.getString("PENS_CUST_DESC"));
					item.setPensGroup(rst.getString("PENS_GROUP"));
					item.setPensGroupType(rst.getString("PENS_GROUP_TYPE"));
					item.setSalesYear(rst.getString("SALES_YEAR"));
					item.setSalesMonth(rst.getString("SALES_MONTH"));
					item.setFileName(rst.getString("File_name"));

					item.setCreateDate(Utils.stringValue(rst.getDate("CREATE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setCreateUser(rst.getString("create_user"));
					
					totalQty += Utils.convertStrToDouble(item.getQty());
					
					pos.add(item);
					
				}//while
                
				TransactionSummary sum = new TransactionSummary();
				sum.setQty(totalQty+"");
				
				re.setItemsList(pos);
				re.setSummary(sum);
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
			return re;
		}
	  
	  /**
	   * 
	   * @param c
	   * @param conn
	   * @param user
	   * @return
	   * @throws Exception
	   */
	  public List<TransactionSummary> searchBigC(TransactionSummary c,Connection conn,User user) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
			StringBuilder sql = new StringBuilder();
			boolean salesDateFlag = false;
			Date salesDateFromParam = null;
			Date salesDateToParam = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT * from PENSBME_SALES_FROM_BIGC ");
				sql.append("\n  where 1=1  ");
			

				if( !Utils.isNull(c.getSalesDateFrom()).equals("")&& !Utils.isNull(c.getSalesDateTo()).equals("")){
					salesDateFlag = true;
					
					salesDateFromParam = Utils.parse(c.getSalesDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					salesDateToParam = Utils.parse(c.getSalesDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
					String dateFrom = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					String dateTo = Utils.stringValue(salesDateToParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(sales_date) >= to_date('"+dateFrom+"','dd/mm/yyyy') \n");
					sql.append(" and trunc(sales_date) <= to_date('"+dateTo+"','dd/mm/yyyy') \n");
				}
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
					sql.append(" and pens_cust_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") \n");
				}
				
				if(!Utils.isNull(c.getFileName()).equals("")){
					sql.append(" and file_name LIKE '%"+c.getFileName()+"%' \n");
				}
				
				sql.append("\n  ORDER BY sales_date ,pens_cust_code,style_no asc \n");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				logger.debug("salesDateFromParam:"+salesDateFromParam);
				logger.debug("salesDateToParam:"+salesDateToParam);
				
				if(salesDateFlag){
					//ps.setDate(1, new java.sql.Date(salesDateFromParam.getTime()));
					//ps.setDate(2, new java.sql.Date(salesDateToParam.getTime()));
				}
				rst = ps.executeQuery(sql.toString());
				
				while (rst.next()) {
					TransactionSummary item = new TransactionSummary();
					item.setVendor(rst.getString("VENDOR"));
					item.setName(rst.getString("NAME"));
					item.setBarcode(rst.getString("BARCODE"));
					item.setStoreNo(rst.getString("STORE_NO"));
					item.setStoreName(rst.getString("STORE_NAME"));
					item.setStyleNo(rst.getString("STYLE_NO"));
					item.setDescription(rst.getString("DESCRIPTION"));
					
					item.setSalesDate(Utils.stringValue(rst.getDate("SALES_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setQty(Utils.decimalFormat(rst.getDouble("QTY"),Utils.format_current_2_disgit));
					item.setGpPercent(Utils.decimalFormat(rst.getDouble("GP_PERCENT"),Utils.format_current_2_disgit));
					item.setTotalWholePriceBF(Utils.decimalFormat(rst.getDouble("TOTAL_WHOLE_PRICE_BF"),Utils.format_current_2_disgit));
				
					item.setPensItem(rst.getString("PENS_ITEM"));
					item.setPensCustCode(rst.getString("PENS_CUST_CODE"));
					item.setPensCustDesc(rst.getString("PENS_CUST_DESC"));
					item.setPensGroup(rst.getString("PENS_GROUP"));
					item.setPensGroupType(rst.getString("PENS_GROUP_TYPE"));
					item.setSalesYear(rst.getString("SALES_YEAR"));
					item.setSalesMonth(rst.getString("SALES_MONTH"));
					item.setFileName(rst.getString("File_name"));

					item.setCreateDate(Utils.stringValue(rst.getDate("CREATE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setCreateUser(rst.getString("create_user"));
					//Whole_Price_BF NUMBER(11,2),
					//Retail_Price_BF NUMBER(11,2),
					item.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"),Utils.format_current_2_disgit));
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"),Utils.format_current_2_disgit));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	  /**
	   * 
	   * @param c
	   * @param conn
	   * @param user
	   * @return
	   * @throws Exception
	   */
	  public List<TransactionSummary> searchBigC_TEMP(TransactionSummary c,Connection conn,User user) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
			StringBuilder sql = new StringBuilder();
			boolean salesDateFlag = false;
			Date salesDateFromParam = null;
			Date salesDateToParam = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT * from PENSBME_SALES_FROM_BIGC_TEMP");
				sql.append("\n  where 1=1  ");
				if( !Utils.isNull(c.getSalesDateFrom()).equals("")&& !Utils.isNull(c.getSalesDateTo()).equals("")){
					salesDateFlag = true;
					
					salesDateFromParam = Utils.parse(c.getSalesDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					salesDateToParam = Utils.parse(c.getSalesDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
					String dateFrom = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					String dateTo = Utils.stringValue(salesDateToParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(sales_date) >= to_date('"+dateFrom+"','dd/mm/yyyy') \n");
					sql.append(" and trunc(sales_date) <= to_date('"+dateTo+"','dd/mm/yyyy') \n");
				}
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
					sql.append(" and pens_cust_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") \n");
				}
				
				if(!Utils.isNull(c.getFileName()).equals("")){
					sql.append(" and file_name LIKE '%"+c.getFileName()+"%' \n");
				}
				
				sql.append("\n  ORDER BY sales_date ,pens_cust_code,style_no asc \n");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				logger.debug("salesDateFromParam:"+salesDateFromParam);
				logger.debug("salesDateToParam:"+salesDateToParam);
				
				if(salesDateFlag){
					//ps.setDate(1, new java.sql.Date(salesDateFromParam.getTime()));
					//ps.setDate(2, new java.sql.Date(salesDateToParam.getTime()));
				}
				rst = ps.executeQuery(sql.toString());
				
				while (rst.next()) {
					TransactionSummary item = new TransactionSummary();
					item.setVendor(rst.getString("VENDOR"));
					item.setName(rst.getString("NAME"));
					item.setBarcode(rst.getString("BARCODE"));
					item.setStoreNo(rst.getString("STORE_NO"));
					item.setStoreName(rst.getString("STORE_NAME"));
					item.setStyleNo(rst.getString("STYLE_NO"));
					item.setDescription(rst.getString("DESCRIPTION"));
					
					item.setSalesDate(Utils.stringValue(rst.getDate("SALES_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setQty(Utils.decimalFormat(rst.getDouble("QTY"),Utils.format_current_2_disgit));
					item.setGpPercent(Utils.decimalFormat(rst.getDouble("GP_PERCENT"),Utils.format_current_2_disgit));
					item.setTotalWholePriceBF(Utils.decimalFormat(rst.getDouble("TOTAL_WHOLE_PRICE_BF"),Utils.format_current_2_disgit));
				
					item.setPensItem(rst.getString("PENS_ITEM"));
					item.setPensCustCode(rst.getString("PENS_CUST_CODE"));
					item.setPensCustDesc(rst.getString("PENS_CUST_DESC"));
					item.setPensGroup(rst.getString("PENS_GROUP"));
					item.setPensGroupType(rst.getString("PENS_GROUP_TYPE"));
					item.setSalesYear(rst.getString("SALES_YEAR"));
					item.setSalesMonth(rst.getString("SALES_MONTH"));
					item.setFileName(rst.getString("File_name"));

					item.setCreateDate(Utils.stringValue(rst.getDate("CREATE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setCreateUser(rst.getString("create_user"));
					//Whole_Price_BF NUMBER(11,2),
					//Retail_Price_BF NUMBER(11,2),
					item.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"),Utils.format_current_2_disgit));
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"),Utils.format_current_2_disgit));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	  public List<TransactionSummary> searchTops(TransactionSummary c,Connection conn,User user) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
			StringBuilder sql = new StringBuilder();
			boolean salesDateFlag = false;
			Date salesDateFromParam = null;
			Date salesDateToParam = null;
			try {
				sql.append("\n  SELECT * from PENSBME_SALES_FROM_TOPS");
				sql.append("\n  where 1=1  ");
				if( !Utils.isNull(c.getSalesDateFrom()).equals("")&& !Utils.isNull(c.getSalesDateTo()).equals("")){
					salesDateFlag = true;
					
					salesDateFromParam = Utils.parse(c.getSalesDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					salesDateToParam = Utils.parse(c.getSalesDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
					String dateFrom = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					String dateTo = Utils.stringValue(salesDateToParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(sales_date) >= to_date('"+dateFrom+"','dd/mm/yyyy') \n");
					sql.append(" and trunc(sales_date) <= to_date('"+dateTo+"','dd/mm/yyyy') \n");
				}
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
					sql.append(" and pens_cust_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") \n");
				}
				
				if(!Utils.isNull(c.getFileName()).equals("")){
					sql.append(" and file_name LIKE '%"+c.getFileName()+"%' \n");
				}
				
				sql.append("\n  ORDER BY sales_date ,pens_cust_code asc \n");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				logger.debug("salesDateFromParam:"+salesDateFromParam);
				logger.debug("salesDateToParam:"+salesDateToParam);
				
				rst = ps.executeQuery(sql.toString());
				while (rst.next()) {
					TransactionSummary item = new TransactionSummary();
					
					item.setSalesDate(Utils.stringValue(rst.getDate("SALES_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setStoreNo(Utils.isNull(rst.getString("STORE_NO")));
					item.setStoreName(Utils.isNull(rst.getString("STORE_NAME")));
					
					item.setBranchName(Utils.isNull(rst.getString("BRANCH_NAME")));
					item.setGroupNo(Utils.isNull(rst.getString("GROUP_NO")));
					item.setGroupName(Utils.isNull(rst.getString("GROUP_NAME")));
					item.setDept(Utils.isNull(rst.getString("DEPT")));
					item.setDeptName(Utils.isNull(rst.getString("DEPT_NAME")));
					item.setItem(Utils.isNull(rst.getString("ITEM")));
					item.setItemDesc(Utils.isNull(rst.getString("ITEM_DESC")));
					item.setUnitCost(Utils.decimalFormat(rst.getDouble("UNIT_COST"),Utils.format_current_2_disgit));
					item.setRetailPrice(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE"),Utils.format_current_2_disgit));
					
					item.setQty(Utils.decimalFormat(rst.getDouble("QTY"),Utils.format_current_2_disgit));
					item.setGpPercent(Utils.decimalFormat(rst.getDouble("GP_PERCENT"),Utils.format_current_2_disgit));
					item.setNetSalesExcVat(Utils.decimalFormat(rst.getDouble("NET_SALES_EXCL_VAT"),Utils.format_current_2_disgit));
					item.setNetSalesInclVat(Utils.decimalFormat(rst.getDouble("NET_SALES_INCL_VAT"),Utils.format_current_2_disgit));
					item.setGpAmount(Utils.decimalFormat(rst.getDouble("GP_AMOUNT"),Utils.format_current_2_disgit));
					
					item.setGrossSales(Utils.decimalFormat(rst.getDouble("GROSS_SALES"),Utils.format_current_2_disgit));
					item.setDiscount(Utils.decimalFormat(rst.getDouble("DISCOUNT"),Utils.format_current_2_disgit));
					item.setCusReturn(Utils.decimalFormat(rst.getDouble("CUS_RETURN"),Utils.format_current_2_disgit));
					
					item.setDiscountCusReturn(Utils.decimalFormat(rst.getDouble("DISCOUNT_CUS_RETURN"),Utils.format_current_2_disgit));
					item.setNetCusReturn(Utils.decimalFormat(rst.getDouble("NET_CUS_RETURN"),Utils.format_current_2_disgit));
					item.setCogs(Utils.decimalFormat(rst.getDouble("COGS"),Utils.format_current_2_disgit));
					
					item.setTotalWholePriceBF(Utils.decimalFormat(rst.getDouble("TOTAL_WHOLE_PRICE_BF"),Utils.format_current_2_disgit));
				
					item.setPensItem(rst.getString("PENS_ITEM"));
					item.setPensCustCode(rst.getString("PENS_CUST_CODE"));
					item.setPensCustDesc(rst.getString("PENS_CUST_DESC"));
					item.setPensGroup(rst.getString("PENS_GROUP"));
					item.setPensGroupType(rst.getString("PENS_GROUP_TYPE"));
					item.setSalesYear(rst.getString("SALES_YEAR"));
					item.setSalesMonth(rst.getString("SALES_MONTH"));
					item.setFileName(rst.getString("File_name"));

					item.setCreateDate(Utils.stringValue(rst.getDate("CREATE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setCreateUser(rst.getString("create_user"));
					//Whole_Price_BF NUMBER(11,2),
					//Retail_Price_BF NUMBER(11,2),
					item.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"),Utils.format_current_2_disgit));
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"),Utils.format_current_2_disgit));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	  
	  public TransactionSummary searchKing(TransactionSummary c,Connection conn,User user) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
			StringBuilder sql = new StringBuilder();
			boolean salesDateFlag = false;
			Date salesDateFromParam = null;
			Date salesDateToParam = null;
			TransactionSummary re = new TransactionSummary();
			int totalQty = 0;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT h.* " );
				sql.append("\n  ,(SELECT pens_desc from PENSBME_MST_REFERENCE m where h.cust_no = m.pens_value and m.reference_code ='Store') as store_name " );
				sql.append("\n from PENSBME_SALES_FROM_KING h");
				sql.append("\n  where 1=1  ");
				if( !Utils.isNull(c.getSalesDateFrom()).equals("")&& !Utils.isNull(c.getSalesDateTo()).equals("")){
					salesDateFlag = true;
					
					salesDateFromParam = Utils.parse(c.getSalesDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					salesDateToParam = Utils.parse(c.getSalesDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
					String dateFrom = Utils.stringValue(salesDateFromParam, Utils.DD_MM_YYYY_WITH_SLASH);
					String dateTo = Utils.stringValue(salesDateToParam, Utils.DD_MM_YYYY_WITH_SLASH);
					
					sql.append(" and trunc(sale_date) >= to_date('"+dateFrom+"','dd/mm/yyyy') \n");
					sql.append(" and trunc(sale_date) <= to_date('"+dateTo+"','dd/mm/yyyy') \n");
				}
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
					sql.append(" and CUST_NO IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") \n");
				}
				
				if(!Utils.isNull(c.getFileName()).equals("")){
					sql.append(" and file_name LIKE '%"+c.getFileName()+"%' \n");
				}
				
				sql.append("\n  ORDER BY sale_date ,CUST_GROUP asc \n");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				logger.debug("salesDateFromParam:"+salesDateFromParam);
				logger.debug("salesDateToParam:"+salesDateToParam);
				
				rst = ps.executeQuery(sql.toString());
				while (rst.next()) {
					TransactionSummary item = new TransactionSummary();
					
					item.setSalesDate(Utils.stringValue(rst.getDate("SALE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setCustGroup(Utils.isNull(rst.getString("CUST_GROUP")));
					item.setStoreNo(Utils.isNull(rst.getString("CUST_NO")));
					item.setStoreName(Utils.isNull(rst.getString("STORE_NAME")));
					item.setPensItem(rst.getString("PENS_ITEM"));
					item.setFileName(rst.getString("File_name"));
					item.setGroupCode(rst.getString("group_code"));
					item.setCreateDate(Utils.stringValue(rst.getDate("CREATE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
					item.setCreateUser(rst.getString("create_user"));
					
					item.setKingCode(rst.getString("code"));
					item.setKingDescription(rst.getString("description"));
					item.setKingReference(rst.getString("reference"));
					item.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_2_disgit));
					item.setKingUnitPrice(Utils.decimalFormat(rst.getDouble("unit_price"),Utils.format_current_2_disgit));
					item.setKingUnitCost(Utils.decimalFormat(rst.getDouble("unit_cost"),Utils.format_current_2_disgit));
					item.setKingAmount(Utils.decimalFormat(rst.getDouble("amount"),Utils.format_current_2_disgit));
					item.setKingCostAmt(Utils.decimalFormat(rst.getDouble("cost_amt"),Utils.format_current_2_disgit));
				
					totalQty += Utils.convertStrToDouble(item.getQty());
					
					pos.add(item);
					
				}//while

				TransactionSummary sum = new TransactionSummary();
				sum.setQty(totalQty+"");
				
				re.setItemsList(pos);
				re.setSummary(sum);
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
			return re;
		}
	  
	  
	  public List<Master> searchMasterList(String referenceCode,String orderBy) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<Master> pos = new ArrayList<Master>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				
				/*if("store".equalsIgnoreCase(referenceCode)){
					sql.append("\n select A.* from( ");
					sql.append("\n   SELECT a.* , " );
					sql.append("\n   ( CASE WHEN a.pens_value ='Dummy' THEN 0 ");
					sql.append("\n     ELSE  CAST(REPLACE(a.pens_value,'-','0') as INTEGER) END) as store_no_order ");
					sql.append("\n   from PENSBME_MST_REFERENCE a "); 
					sql.append("\n   where 1=1 and a.reference_code ='Store'  ");
					sql.append("\n)A ");
					sql.append("\n ORDER BY  A.store_no_order ");
				}else{*/
				  sql.append("\n  SELECT * from PENSBME_MST_REFERENCE \n");
				  sql.append("\n  where 1=1 and reference_code ='"+referenceCode+"' \n");
				//}
				
				sql.append("\n  ORDER BY "+orderBy+" asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					Master item = new Master();
					item.setReferenceCode(rst.getString("reference_code"));
					item.setInterfaceValue(rst.getString("interface_value"));
					item.setInterfaceDesc(rst.getString("interface_desc"));
					item.setPensValue(rst.getString("pens_value"));
					item.setPensDesc(rst.getString("pens_desc"));
					item.setPensDesc2(rst.getString("pens_desc2"));
					item.setPensDesc3(rst.getString("pens_desc3"));
					item.setCreateUser(rst.getString("create_user"));
					item.setSequence(rst.getString("sequence"));
					item.setStatus(rst.getString("status"));
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
	  
	  public static List<PopupForm> searchCustomerMaster(PopupForm c,String storeType,String operation) throws Exception {
		  return searchCustomerMaster(c, storeType, "", operation);
	  }
	  
	  public static List<PopupForm> searchCustomerMaster(PopupForm c,String storeType,String storeGroup,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			logger.debug("storeType:"+storeType);
			try {
				conn = DBConnection.getInstance().getConnection();
				
				sql.append("SELECT A.* FROM (\n");
				sql.append("  SELECT M.* , \n");
				sql.append("  (select max(M1.interface_desc) from pensbi.PENSBME_MST_REFERENCE M1 \n");
				sql.append("   where M1.reference_code = 'SubInv' and M1.pens_value =M.pens_value) as sub_inv \n");
				sql.append("  from PENSBME_MST_REFERENCE M \n");
				sql.append("  where 1=1 and reference_code ='Store' \n");
			
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				
				//StoreGroup
				if( !Utils.isNull(storeGroup).equals("")){
					sql.append(" and pens_value LIKE '"+storeGroup+"%' \n");
				}
				
				if( !Utils.isNull(storeType).equalsIgnoreCase("")){
					if(storeType.equalsIgnoreCase("lotus")){
						sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_LOTUS_CODE+"%' \n");
					}else if(storeType.equalsIgnoreCase("bigc")){
						sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_BIGC_CODE+"%' \n");
					}else if(storeType.equalsIgnoreCase("tops")){
						sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_TOPS_CODE+"%' \n");
					}else if(storeType.equalsIgnoreCase("MTT")){
						sql.append(" and ( pens_value LIKE '"+Constants.STORE_TYPE_MTT_CODE_1+"%' \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER+"%'  \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_HISHER_CODE+"%' \n" );
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_2+"%'  \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_3+"%'  \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_4+"%'  \n");
						sql.append("     OR pens_value LIKE '020092%'  \n");
						sql.append("     OR pens_value LIKE '020095%')  \n");
						
					}else if(storeType.equalsIgnoreCase("king")
							|| storeType.equalsIgnoreCase("DutyFree")){//Duty Free
						/*sql.append(" and (pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER+"%' \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_2+"%'  \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_3+"%'  \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_4+"%'  \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_5+"%' ) \n");*/
						
						sql.append(SQLHelper.genFilterByStoreType(conn, "DUTYFREE", "pens_value"));
					}else if(storeType.equalsIgnoreCase("Robinson")){
						sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_ROBINSON_CODE+"%' \n");
					}
				}
				sql.append("  and pens_desc4 ='N' \n");
				sql.append("  ORDER BY pens_value asc \n");
				sql.append(" )A WHERE 1=1 \n");
				//sub_inv
				if( !Utils.isNull(c.getSubInvSearch()).equals("")){
					sql.append(" and A.sub_inv ='"+c.getSubInvSearch()+"' \n");
				}
			
				logger.debug(" sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("pens_value")));
					item.setDesc(Utils.isNull(rst.getString("pens_desc")));
					item.setStoreNo(Utils.isNull(rst.getString("interface_value")));
					item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
					
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
	  
	  public static List<PopupForm> searchBranchMaster(PopupForm c,String storeType,String operation) throws Exception {
		  return searchBranchMasterModel(c, storeType, operation);
	  }
	  public static List<PopupForm> searchBranchMasterModel(PopupForm c,String storeType,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			logger.debug("storeType:"+storeType);
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* from pensbme_wacoal_store_mapping M");
				sql.append("\n  where 1=1  ");
			
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and branch_id ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and upper(branch_name) = '"+c.getDescSearch().toUpperCase()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and branch_id LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and upper(branch_name) LIKE '%"+c.getDescSearch().toUpperCase()+"%' \n");
					}
				}
				//storeType (LOTUS OR BIGC)
				if( !Utils.isNull(storeType).equals("")){
					sql.append(" and branch_name LIKE '"+storeType+"%' \n");
				}
		
				sql.append("\n  ORDER BY branch_id asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setBranchId(Utils.isNull(rst.getString("branch_id")));
					item.setBranchName(Utils.isNull(rst.getString("branch_name")));
					item.setStoreNo(Utils.isNull(rst.getString("store_no")));
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
	  
	  public List<PopupForm> searchGroupMaster(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n  SELECT distinct pens_desc2 from PENSBME_MST_REFERENCE \n");
				sql.append("\n  where 1=1 and reference_code ='LotusItem' \n");
			
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and pens_desc2 LIKE '%"+c.getCodeSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and pens_desc2 LIKE '%"+c.getDescSearch()+"%' \n");
				}
				
				sql.append("\n  ORDER BY pens_desc2 asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("pens_desc2"));
					item.setDesc(rst.getString("pens_desc2"));
					
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
	  public Date searchInitDateMTT(String custNo) throws Exception {
		  Connection conn = null;
		  try{
			   conn = DBConnection.getInstance().getConnection();
			   return searchInitDateMTTModel(conn,custNo);
		  }catch(Exception e){
			  throw e;
		  } finally {
			  conn.close();
		  }
	  }
	  public Date searchInitDateMTT(Connection conn,String custNo) throws Exception {
		  return searchInitDateMTTModel(conn,custNo);
	  }
	  public Date searchInitDateMTTModel(Connection conn,String custNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			Date initDate = null;
			StringBuilder sql = new StringBuilder();
			try {
				sql.delete(0, sql.length());
				sql.append("  SELECT distinct trunc(max(Count_stk_date)) as Count_stk_date from PENSBI.PENSBME_MTT_INIT_STK \n");
				sql.append("  where 1=1 and Cust_no ='"+custNo+"' \n");
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()) {
			       initDate = rst.getDate("Count_stk_date");
					
				}//if
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return initDate;
		}
	  public Date searchInitDateBigC(String custNo) throws Exception {
		  Connection conn = null;
		  try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchInitDateBigCModel(conn, custNo);
		  }catch(Exception e){
			  throw e;
		  }finally{
			  if(conn != null){
				  conn.close();conn=null;
			  }
		  }
	  }
	  
      public Date searchInitDateBigC(Connection conn,String custNo) throws Exception {
    	  return searchInitDateBigCModel(conn, custNo);
	  }
	  public Date searchInitDateBigCModel(Connection conn,String custNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			Date initDate = null;
			StringBuilder sql = new StringBuilder();
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct trunc(max(Count_stk_date)) as Count_stk_date from PENSBME_BIGC_INIT_STK ");
				sql.append("\n  where 1=1 and Cust_no ='"+custNo+"' ");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()) {
			       initDate = rst.getDate("Count_stk_date");
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return initDate;
		}
	  
	  public Date searchInitDateWacoalStock(Connection conn,String brandchId) throws Exception {
		 return searchInitDateWacoalStockModel(conn, brandchId);
	  }
	  public Date searchInitDateWacoalStock(String brandchId) throws Exception {
		  Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
		    return searchInitDateWacoalStockModel(conn, brandchId);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	  }
	  
	  public Date searchInitDateWacoalStockModel(Connection conn,String brandchId) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			Date initDate = null;
			StringBuilder sql = new StringBuilder();
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct trunc(max(check_date)) as Count_stk_date from PENSBME_INISTK_WACOAL \n");
				sql.append("\n  where 1=1 and branch_id ='"+brandchId+"' \n");
				
				//logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()) {
			       initDate = rst.getDate("Count_stk_date");
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return initDate;
		}
	  public Date searchInitDateLotus(Connection conn,String custNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			Date initDate = null;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT distinct trunc(max(Count_stk_date)) as Count_stk_date from PENSBME_LOTUS_INIT_STK \n");
				sql.append("\n  where 1=1 and Cust_no ='"+custNo+"' \n");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()) {
			       initDate = rst.getDate("Count_stk_date");
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					
				} catch (Exception e) {}
			}
			return initDate;
		}
	  
	  public List<PopupForm> searchProductFromORCL(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT ");
				sql.append("\n INVENTORY_ITEM_ID, ");
				sql.append("\n INVENTORY_ITEM_CODE, ");
				sql.append("\n INVENTORY_ITEM_DESC ");
				
				sql.append("\n FROM PENSBI.XXPENS_BI_MST_ITEM ");
				sql.append("\n WHERE INVENTORY_ITEM_DESC LIKE 'ME%' ");

				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and INVENTORY_ITEM_CODE LIKE '%"+c.getCodeSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and INVENTORY_ITEM_DESC LIKE '%"+c.getDescSearch()+"%' \n");
				}
				
				sql.append("\n  ORDER BY INVENTORY_ITEM_CODE asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("INVENTORY_ITEM_CODE"));
					item.setDesc(rst.getString("INVENTORY_ITEM_DESC"));
					
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
	  
	  public List<PopupForm> searchProductTops(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n SELECT pens_value,I.INVENTORY_ITEM_DESC ");
				sql.append("\n FROM PENSBI.PENSBME_MST_REFERENCE M ,PENSBI.XXPENS_BI_MST_ITEM I");
				sql.append("\n WHERE reference_code = 'TOPSitem'  ");
				sql.append("\n AND M.pens_value = I.inventory_item_code   ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append(" and I.INVENTORY_ITEM_DESC LIKE '%"+c.getDescSearch()+"%' \n");
				}
				sql.append("\n  ORDER BY M.pens_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("pens_value"));
					item.setDesc(rst.getString("INVENTORY_ITEM_DESC"));
					
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
	  public static List<PopupForm> searchProductFromBMEMaster(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n select distinct A.item, A.group_item, nvl(OH.whole_price_bf,0) as price from( ");
				
				sql.append("\n  select distinct M.pens_value as item ,M.pens_desc2 as group_item ");
				sql.append("\n  from pensbi.PENSBME_MST_REFERENCE M   ");
				sql.append("\n  where M.reference_code = 'LotusItem' ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					   sql.append("\n and M.pens_value LIKE '%"+c.getCodeSearch()+"%'");
				}
				
				sql.append("\n  union  ");
				
				sql.append("\n  select distinct M.pens_desc3 as item ,M.pens_desc2 as group_item ");
				sql.append("\n  from pensbi.PENSBME_MST_REFERENCE M   ");
				sql.append("\n  where M.reference_code = 'LotusItem' ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					   sql.append("\n and M.pens_desc3 LIKE '%"+c.getCodeSearch()+"%'");
				}
				sql.append("\n )A LEFT OUTER JOIN ");
				sql.append("\n ( ");
				sql.append("\n  select group_item,whole_price_bf   ");
				sql.append("\n  from pensbi.PENSBME_ONHAND_BME ");
				sql.append("\n ) OH ON A.group_item = OH.group_item ");
				sql.append("\n order by A.item asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("item"));
					item.setDesc(rst.getString("group_item"));
					item.setPrice(rst.getString("price"));
					
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
	  
	  public static List<PopupForm> searchProductFromMstByGroupCode(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n  select M.pens_value as pens_item  ");
				sql.append("\n ,NVL(( select max(whole_price_bf) from PENSBME_ONHAND_BME_LOCKED B where B.group_item =M.pens_desc2),0) as price ");
				sql.append("\n  from PENSBME_MST_REFERENCE M   ");
				sql.append("\n  where M.reference_code = 'LotusItem' ");
				sql.append("\n  and M.pens_desc2 ='"+c.getCodeSearch()+"'");
				sql.append("\n  order by M.pens_value asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				int no = 0;
				if (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("pens_item"));
					item.setDesc(rst.getString("pens_item"));
					item.setPrice(rst.getString("price"));
					
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
	  
	  public List<DiffStockSummary> searchDiffStock(DiffStockSummary c,User user) throws Exception {
		    Connection conn = null;
			Statement stmt = null;
			ResultSet rst = null;
			List<DiffStockSummary> pos = new ArrayList<DiffStockSummary>();
			StringBuilder sql = new StringBuilder();
			try {
				conn = DBConnection.getInstance().getConnection();

				sql.append("SELECT  \n");
				sql.append("O.customer_code, \n");
				sql.append("O.customer_desc, \n");
				sql.append("O.inventory_item_code, \n");
				sql.append("O.inventory_item_desc, \n");
				sql.append("O.inventory_item_id, \n");
				sql.append("O.order_consign, \n");
				sql.append("O.order_from_lotus, \n");
				sql.append("O.data_from_physical, \n");
				sql.append("( (O.order_consign - O.order_from_lotus ) - O.data_from_physical  ) as diff,  \n");     
				sql.append("(CASE WHEN O.order_consign - O.order_from_lotus = O.data_from_physical THEN 'No Diff' \n");
				sql.append("       WHEN O.order_consign - O.order_from_lotus > O.data_from_physical   THEN 'Short' \n");
				sql.append("       WHEN O.order_consign - O.order_from_lotus < O.data_from_physical   THEN 'Over' \n");
				sql.append("       ELSE '' \n");
				sql.append("       END ) as diff_msg \n");
				sql.append("FROM ( \n");
				sql.append("SELECT M.*,A.order_consign ,b.order_from_lotus ,c.data_from_physical FROM(  \n");
				sql.append("       SELECT  distinct \n");
				sql.append("      C.customer_id, \n");
				sql.append("	  C.customer_code,  \n");
				sql.append("	  R.pens_desc as customer_desc, \n");
				sql.append("	  V.inventory_item_id, \n");
				sql.append("	  P.inventory_item_code, \n");
				sql.append("	  P.inventory_item_desc \n");
				sql.append("	   \n");
				sql.append("	  FROM XXPENS_BI_SALES_ANALYSIS_V V  \n");
				sql.append("	  ,XXPENS_BI_MST_CUSTOMER C \n");
				sql.append("	  ,PENSBME_MST_REFERENCE R \n");
				sql.append("	  ,XXPENS_BI_MST_ITEM P \n");
				sql.append("	    WHERE 1=1  \n");
				sql.append("	    AND V.inventory_item_id = P.inventory_item_id \n");
				sql.append("	    AND V.customer_id = C.customer_id \n");
				sql.append("	    AND C.CUSTOMER_CODE = R.pens_value \n");
				sql.append("	    AND R.reference_code = 'Store' \n");
				sql.append("	    AND V.Customer_id IS NOT NULL  \n");
				sql.append("	    AND V.inventory_item_id IS NOT NULL \n");
				if( !"".equals(Utils.isNull(c.getAsOfDate()))){
					Date dateObj = Utils.parse(Utils.isNull(c.getAsOfDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String christDateStr = Utils.stringValue(dateObj, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
				    sql.append("	AND V.invoice_date <= to_date('"+christDateStr+"','dd/mm/yyyy') \n");
				}
				if( !"".equals(Utils.isNull(c.getPensCustCodeFrom()))){
				    sql.append("    AND C.CUSTOMER_CODE = '"+Utils.isNull(c.getPensCustCodeFrom())+"' \n");
				}
				if( !"".equals(Utils.isNull(c.getHaveQty()))){
					sql.append("	AND V.INVOICED_QTY <> 0  \n");
				}
				//sql.append("	    /* GROUP BY  C.customer_id ,C.customer_code ,R.pens_desc, \n");
				//sql.append("	     V.inventory_item_id,  P.inventory_item_code,P.inventory_item_desc */ \n");
				sql.append("  ) M \n");
				sql.append("LEFT OUTER JOIN  \n");
				sql.append(" (   \n");
				sql.append("	 SELECT  \n");
				sql.append("		 V.Customer_id  \n");
				sql.append("		,V.inventory_item_id \n");
				sql.append("		,(SUM(V.INVOICED_QTY)  -  SUM(V.RETURNED_QTY) ) as order_consign  \n");
				sql.append("		 FROM XXPENS_BI_SALES_ANALYSIS_V V  \n");
				sql.append("	     ,XXPENS_BI_MST_CUSTOMER C \n");
				sql.append("		 WHERE 1=1  \n");
				sql.append("		 AND V.Customer_id = C.Customer_id  \n");
				sql.append("		 AND V.Customer_id IS NOT NULL  \n");
				sql.append("         AND V.inventory_item_id IS NOT NULL \n");
				if( !"".equals(Utils.isNull(c.getAsOfDate()))){
					Date dateObj = Utils.parse(Utils.isNull(c.getAsOfDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String christDateStr = Utils.stringValue(dateObj, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
				    sql.append("	 AND V.invoice_date <= to_date('"+christDateStr+"','dd/mm/yyyy') \n");
				}
				if( !"".equals(Utils.isNull(c.getPensCustCodeFrom()))){
				    sql.append("	 AND C.CUSTOMER_CODE = '"+Utils.isNull(c.getPensCustCodeFrom())+"' \n");
				}
				
				sql.append("		 GROUP BY V.Customer_id  ,V.inventory_item_id \n");
				sql.append("  )A  \n");
				sql.append("ON M.Customer_id=A.Customer_id AND M.inventory_item_id = A.inventory_item_id \n");
				sql.append(" \n");
				sql.append("LEFT OUTER JOIN  \n");
				sql.append(" (   \n");
				sql.append("	 SELECT  \n");
				sql.append("		 C.Customer_id  \n");
				sql.append("		,P.inventory_item_id \n");
				sql.append("		,sum(V.qty) as order_from_lotus \n");
				sql.append("		FROM PENSBME_SALES_FROM_LOTUS V  \n");
				sql.append("	     , XXPENS_BI_MST_CUSTOMER C \n");
				sql.append("	      \n");
				sql.append("	     , XXPENS_BI_MST_ITEM P \n");
				sql.append("	    WHERE 1=1  \n");
				sql.append("	     \n");
				sql.append("	      \n");
				sql.append("	    AND V.PENS_ITEM = P.INVENTORY_ITEM_CODE \n");
				sql.append("	    AND C.CUSTOMER_CODE = V.pens_cust_code \n");
				if( !"".equals(Utils.isNull(c.getAsOfDate()))){
					Date dateObj = Utils.parse(Utils.isNull(c.getAsOfDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String christDateStr = Utils.stringValue(dateObj, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
				    sql.append("	AND V.sales_date <= to_date('"+christDateStr+"','dd/mm/yyyy') \n");
				}
				if( !"".equals(Utils.isNull(c.getPensCustCodeFrom()))){
				    sql.append("	AND C.CUSTOMER_CODE = '"+Utils.isNull(c.getPensCustCodeFrom())+"' \n");
				}
				
				sql.append("		GROUP BY C.Customer_id  ,P.inventory_item_id \n");
				sql.append("  )B \n");
				sql.append("ON M.Customer_id=B.Customer_id AND M.inventory_item_id = B.inventory_item_id \n");
				sql.append(" \n");
				sql.append("LEFT OUTER JOIN  \n");
				sql.append(" (   \n");
				sql.append("	 SELECT  \n");
				sql.append("		 C.Customer_id  \n");
				sql.append("		,P.inventory_item_id \n");
				sql.append("		,count(*) as data_from_physical \n");
				sql.append("		FROM PENSBME_PHYSICAL_COUNT V  \n");
				sql.append("	     , XXPENS_BI_MST_CUSTOMER C \n");
				sql.append("	     , XXPENS_BI_MST_ITEM P \n");
				sql.append("	    WHERE 1=1  \n");
				sql.append("	    AND V.ITEM = P.INVENTORY_ITEM_CODE \n");
				sql.append("	    AND V.cust_code = C.CUSTOMER_CODE \n");
				if( !"".equals(Utils.isNull(c.getAsOfDate()))){
					Date dateObj = Utils.parse(Utils.isNull(c.getAsOfDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String christDateStr = Utils.stringValue(dateObj, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
				    sql.append("	AND V.count_date <= to_date('"+christDateStr+"','dd/mm/yyyy') \n");
				}
				if( !"".equals(Utils.isNull(c.getPensCustCodeFrom()))){
				    sql.append("	AND C.CUSTOMER_CODE = '"+Utils.isNull(c.getPensCustCodeFrom())+"' \n");
				}
				
				sql.append("	    GROUP BY C.Customer_id  ,P.inventory_item_id \n");
				sql.append("  )C \n");
				sql.append("ON M.Customer_id=C.Customer_id AND M.inventory_item_id = C.inventory_item_id \n");
				sql.append(" \n");
				sql.append(") O \n");
				sql.append("ORDER BY O.customer_code, O.inventory_item_code asc \n");

				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					DiffStockSummary item = new DiffStockSummary();
					
					item.setCustCode(rst.getString("customer_code")+"-"+rst.getString("customer_desc"));
					item.setItem(rst.getString("inventory_item_code"));
					item.setDescription(rst.getString("inventory_item_desc"));
					item.setOrderConsign(Utils.decimalFormat(rst.getDouble("order_consign"),Utils.format_current_2_disgit));
					item.setOrderFromLotus(Utils.decimalFormat(rst.getDouble("order_from_lotus"),Utils.format_current_2_disgit));
					item.setDataFromPhysical(Utils.decimalFormat(rst.getDouble("data_from_physical"),Utils.format_current_2_disgit));
					item.setAdjust(Utils.decimalFormat(rst.getDouble("diff"),Utils.format_current_2_disgit));
					item.setDiff(Utils.decimalFormat(rst.getDouble("diff"),Utils.format_current_2_disgit));
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
	  
	 
	  public OnhandSummary searchReportStockWacoalLotus(SummaryForm f,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
		    double saleInQtyTemp = 0;
		    double saleReturnQtyTemp = 0;
		    double saleOutQtyTemp = 0;
		    double adjustQtyTemp =0;
		    double stockShortQtyTemp = 0;
		    double onhandQtyTemp = 0;
		    double onhand_amt = 0;
			try {
				conn = DBConnection.getInstance().getConnection();
                sql = ReportOnhandLotus_SQL.genSQL(conn, c, f.getSummaryType());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreName(rst.getString("customer_desc"));
					if("PensItem".equalsIgnoreCase(f.getSummaryType())){
					   item.setPensItem(rst.getString("pens_item"));
					}
					item.setGroup(rst.getString("group_type"));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price_bf"),Utils.format_current_2_disgit));
					item.setOnhandAmt(Utils.decimalFormat(rst.getDouble("onhand_amt"),Utils.format_current_2_disgit));
					 
					
					saleInQtyTemp += Utils.convertStrToDouble(item.getSaleInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(item.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(item.getSaleOutQty());
					adjustQtyTemp +=Utils.convertStrToDouble(item.getAdjustQty());
					stockShortQtyTemp +=Utils.convertStrToDouble(item.getStockShortQty());
					onhandQtyTemp +=Utils.convertStrToDouble(item.getOnhandQty());
					onhand_amt += rst.getDouble("onhand_amt");
							
					pos.add(item);
					
				}//while
				
				//Summary
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(f.getSummaryType())){
				   item.setPensItem("");
				}
				item.setGroup("");
				item.setSaleInQty(Utils.decimalFormat(saleInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
				item.setAdjustQty(Utils.decimalFormat(adjustQtyTemp,Utils.format_current_no_disgit));
				item.setStockShortQty(Utils.decimalFormat(stockShortQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandAmt(Utils.decimalFormat(onhand_amt,Utils.format_current_2_disgit));
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
	  
	  /** Compare by pens_group_item **/
	  public OnhandSummary searchOnhandLotus(SummaryForm f,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
		    double saleInQtyTemp = 0;
		    double saleReturnQtyTemp = 0;
		    double saleOutQtyTemp = 0;
		    double adjustQtyTemp =0;
		    double stockShortQtyTemp = 0;
		    double onhandQtyTemp = 0;
		    double onhand_amt = 0;
			try {
				conn = DBConnection.getInstance().getConnection();
                sql = ReportOnhandLotus_SQL.genSQL(conn, c, f.getSummaryType());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreName(rst.getString("customer_desc"));
					if("PensItem".equalsIgnoreCase(f.getSummaryType())){
					   item.setPensItem(rst.getString("pens_item"));
					}
					item.setGroup(rst.getString("group_type"));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price_bf"),Utils.format_current_2_disgit));
					item.setOnhandAmt(Utils.decimalFormat(rst.getDouble("onhand_amt"),Utils.format_current_2_disgit));
					 
					
					saleInQtyTemp += Utils.convertStrToDouble(item.getSaleInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(item.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(item.getSaleOutQty());
					adjustQtyTemp +=Utils.convertStrToDouble(item.getAdjustQty());
					stockShortQtyTemp +=Utils.convertStrToDouble(item.getStockShortQty());
					onhandQtyTemp +=Utils.convertStrToDouble(item.getOnhandQty());
					onhand_amt += rst.getDouble("onhand_amt");
							
					pos.add(item);
					
				}//while
				
				//Summary
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(f.getSummaryType())){
				   item.setPensItem("");
				}
				item.setGroup("");
				item.setSaleInQty(Utils.decimalFormat(saleInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
				item.setAdjustQty(Utils.decimalFormat(adjustQtyTemp,Utils.format_current_no_disgit));
				item.setStockShortQty(Utils.decimalFormat(stockShortQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandAmt(Utils.decimalFormat(onhand_amt,Utils.format_current_2_disgit));
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
	  
	  public OnhandSummary searchOnhandTops(SummaryForm f,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
		    double saleInQtyTemp = 0;
		    double saleReturnQtyTemp = 0;
		    double saleOutQtyTemp = 0;
		    double adjustQtyTemp =0;
		    double stockShortQtyTemp = 0;
		    double onhandQtyTemp = 0;
		    double onhand_amt = 0;
			try {
				conn = DBConnection.getInstance().getConnection();
               sql = ReportOnhandTops_SQL.genSQL(conn, c, f.getSummaryType());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreName(rst.getString("customer_desc"));
					if("PensItem".equalsIgnoreCase(f.getSummaryType())){
					   item.setPensItem(rst.getString("pens_item"));
					}
					item.setGroup(rst.getString("group_type"));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price_bf"),Utils.format_current_2_disgit));
					item.setOnhandAmt(Utils.decimalFormat(rst.getDouble("onhand_amt"),Utils.format_current_2_disgit));
					 
					
					saleInQtyTemp += Utils.convertStrToDouble(item.getSaleInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(item.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(item.getSaleOutQty());
					adjustQtyTemp +=Utils.convertStrToDouble(item.getAdjustQty());
					stockShortQtyTemp +=Utils.convertStrToDouble(item.getStockShortQty());
					onhandQtyTemp +=Utils.convertStrToDouble(item.getOnhandQty());
					onhand_amt += rst.getDouble("onhand_amt");
							
					pos.add(item);
					
				}//while
				
				//Summary
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(f.getSummaryType())){
				   item.setPensItem("");
				}
				item.setGroup("");
				item.setSaleInQty(Utils.decimalFormat(saleInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
				item.setAdjustQty(Utils.decimalFormat(adjustQtyTemp,Utils.format_current_no_disgit));
				item.setStockShortQty(Utils.decimalFormat(stockShortQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandAmt(Utils.decimalFormat(onhand_amt,Utils.format_current_2_disgit));
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
	  public OnhandSummary searchOnhandAsOf_Robinson(SummaryForm f,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
		    double saleInQtyTemp = 0;
		    double saleReturnQtyTemp = 0;
		    double saleOutQtyTemp = 0;
		    double adjustQtyTemp =0;
		    double stockShortQtyTemp = 0;
		    double onhandQtyTemp = 0;
		    double onhand_amt = 0;
			try {
				conn = DBConnection.getInstance().getConnection();
                sql = ReportOnhandAsOf_Robinson_SQL.genSQL(conn, c, f.getSummaryType());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreNo(rst.getString("store_no"));
					item.setStoreName(rst.getString("customer_desc"));
					if("PensItem".equalsIgnoreCase(f.getSummaryType())){
					   item.setPensItem(rst.getString("pens_item"));
					}
					item.setGroup(rst.getString("group_type"));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price_bf"),Utils.format_current_2_disgit));
					item.setOnhandAmt(Utils.decimalFormat(rst.getDouble("onhand_amt"),Utils.format_current_2_disgit));
					 
					
					saleInQtyTemp += Utils.convertStrToDouble(item.getSaleInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(item.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(item.getSaleOutQty());
					adjustQtyTemp +=Utils.convertStrToDouble(item.getAdjustQty());
					stockShortQtyTemp +=Utils.convertStrToDouble(item.getStockShortQty());
					onhandQtyTemp +=Utils.convertStrToDouble(item.getOnhandQty());
					onhand_amt += rst.getDouble("onhand_amt");
							
					pos.add(item);
					
				}//while
				
				//Summary
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(f.getSummaryType())){
				   item.setPensItem("");
				}
				item.setGroup("");
				item.setSaleInQty(Utils.decimalFormat(saleInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
				item.setAdjustQty(Utils.decimalFormat(adjustQtyTemp,Utils.format_current_no_disgit));
				item.setStockShortQty(Utils.decimalFormat(stockShortQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandAmt(Utils.decimalFormat(onhand_amt,Utils.format_current_2_disgit));
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
	  public OnhandSummary searchReportStockWacoalLotusAllBranch(SummaryForm f,User user,Date asOfDate) throws Exception{
		  Connection conn = null;
		  Statement stmt = null;
		  ResultSet rst = null;
		  StringBuilder sql = new StringBuilder();
		  double saleInitQtyTemp = 0;
		  double saleInQtyTemp = 0;
		  double saleReturnQtyTemp = 0;
		  double saleOutQtyTemp = 0;
		  double onhandQtyTemp = 0;
		  OnhandSummary c = f.getOnhandSummary();
		  List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
		  Date initDate = null;
		  try{
				conn = DBConnection.getInstance().getConnection();
				//get Store Code All 
				sql.append("\n SELECT DISTINCT AA.* FROM(");
			    sql.append("\n SELECT DISTINCT L.branch_id ");
				sql.append("\n FROM  PENSBME_INISTK_WACOAL L WHERE 1=1");
			    sql.append("\n UNION ALL");
			    sql.append("\n SELECT DISTINCT L.branch_id ");
				sql.append("\n FROM  PENSBME_WACOAL_SALEIN L");
				sql.append("\n WHERE  1=1 ");
				sql.append("\n UNION ALL");
			    sql.append("\n SELECT DISTINCT L.branch_id ");
				sql.append("\n FROM  PENSBME_WACOAL_SALEOUT L");
				sql.append("\n WHERE 1=1 ");
				sql.append("\n UNION ALL");
			    sql.append("\n SELECT DISTINCT L.branch_id");
				sql.append("\n FROM  PENSBME_WACOAL_RETURN L");
				sql.append("\n WHERE  1=1 ");
				
				sql.append("\n )AA order by AA.branch_id asc ");
				logger.debug("sql:"+sql.toString());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					initDate = new SummaryDAO().searchInitDateWacoalStock(conn,Utils.isNull(rst.getString("branch_id")));
					
					OnhandSummary onhandSummary = f.getOnhandSummary();
					onhandSummary.setPensCustCodeFrom(Utils.isNull(rst.getString("branch_id")));
					f.setOnhandSummary(onhandSummary);
					OnhandSummary itemStore = searchReportStockWacoalLotusModel(conn, f, user, initDate, asOfDate);
					pos.addAll(itemStore.getItemsList());
							
					saleInitQtyTemp += Utils.convertStrToDouble(itemStore.getInitSaleQty());
					saleInQtyTemp += Utils.convertStrToDouble(itemStore.getSaleInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(itemStore.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(itemStore.getSaleOutQty());
					onhandQtyTemp +=Utils.convertStrToDouble(itemStore.getOnhandQty());
				}//while
				
				//Summary
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				item.setGroup("");
				item.setInitDate("");
				item.setSaleInQty(Utils.decimalFormat(saleInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
				item.setInitSaleQty(Utils.decimalFormat(saleInitQtyTemp,Utils.format_current_no_disgit));
				c.setSummary(item);
                
				//set data list
				c.setItemsList(pos);
				
				return c;
		  }catch(Exception e){
			throw e;
		  }finally{
			  conn.close();
		  }
	  }
	  public OnhandSummary searchReportStockWacoalLotusOneBranch(SummaryForm f,User user,Date initDate,Date asOfDate) throws Exception{
		  Connection conn = null;
		  try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchReportStockWacoalLotusModel(conn, f, user, initDate, asOfDate);
		  }catch(Exception e){
			  throw e;
		  }finally{
			  conn.close();
		  }
	  }
	  
	  public OnhandSummary searchReportStockWacoalLotusModel(Connection conn,SummaryForm f,User user,Date initDate,Date asOfDate) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			double saleInitQtyTemp = 0;
		    double saleInQtyTemp = 0;
		    double saleReturnQtyTemp = 0;
		    double saleOutQtyTemp = 0;
		    double onhandQtyTemp = 0;
		    OnhandSummary c = f.getOnhandSummary();
		    String initDateStr = Utils.stringValue(initDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			try {
				logger.debug("storeCode:"+f.getOnhandSummary().getPensCustCodeFrom());
                sql = ReportStockWacoalLotus_SQL.genSQL(conn, c,initDate,asOfDate);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("branch_id"));
					item.setStoreName(rst.getString("branch_name"));
					item.setInitDate(initDateStr);
					item.setGroup(rst.getString("group_type"));
					item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("sale_init_qty"),Utils.format_current_no_disgit));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					 
					saleInitQtyTemp += Utils.convertStrToDouble(item.getInitSaleQty());
					saleInQtyTemp += Utils.convertStrToDouble(item.getSaleInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(item.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(item.getSaleOutQty());
					onhandQtyTemp +=Utils.convertStrToDouble(item.getOnhandQty());
	
					pos.add(item);
				}//while
				
				//Summary
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				item.setGroup("");
				item.setInitDate("");
				item.setSaleInQty(Utils.decimalFormat(saleInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
				item.setInitSaleQty(Utils.decimalFormat(saleInitQtyTemp,Utils.format_current_no_disgit));
				c.setSummary(item);
                
				//set data list
				c.setItemsList(pos);
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return c;
	    }
	  
	  public OnhandSummary searchReportEndDateLotus_BK(SummaryForm f,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
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
			try {
				conn = DBConnection.getInstance().getConnection();
				sql = ReportEndDateLotusSQL.genSQL(conn, c, user, f.getSummaryType(),"ReportEndDate");
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("CUSTOMER_CODE"));
					item.setStoreName(rst.getString("customer_desc"));
					if("PensItem".equalsIgnoreCase(f.getSummaryType())){
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
					 
					BEGINING_QTY += rst.getDouble("BEGINING_QTY");
					sale_in_qty += rst.getDouble("sale_in_qty");
					sale_return_qty += rst.getDouble("sale_return_qty");
					sale_out_qty += rst.getDouble("sale_out_qty");
					ADJUST_QTY += rst.getDouble("ADJUST_QTY");
					STOCK_SHORT_QTY += rst.getDouble("STOCK_SHORT_QTY");
					onhand_qty += rst.getDouble("onhand_qty");
					onhand_amt += rst.getDouble("onhand_amt");
					pos.add(item);
					
				}//while
				
				//add Summary Row
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(f.getSummaryType())){
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
	  
	  public OnhandSummary searchReportEndDateLotusAllStore(SummaryForm f,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
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
			Map<String, OnhandSummary> rowMap = new HashMap<String, OnhandSummary>();
			OnhandSummary item = null;
			OnhandSummary prevItem = null;
			List<StoreBean> storeList = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					//Get By Store
					storeList =  new ArrayList<StoreBean>();
					StoreBean storeBean = new StoreBean();
					storeBean.setStoreCode(c.getPensCustCodeFrom());
					storeList.add(storeBean);
				}else{
					//Get AllStore Lotus
					storeList = StoreDAO.getStoreList(conn, PickConstants.STORE_TYPE_LOTUS_CODE);
				}
				
				if(storeList != null && storeList.size() >0){
				for(int i=0;i<storeList.size();i++){
					//Loop Step by Store Code
					StoreBean storeBean = storeList.get(i);
					f.getOnhandSummary().setPensCustCodeFrom(storeBean.getStoreCode());
					
					sql = ReportEndDateLotusSQL.genSQL(conn, c, user, f.getSummaryType(),"ReportEndDate");
					
					stmt = conn.createStatement();
					rst = stmt.executeQuery(sql.toString());
					
					while (rst.next()) {
						item = new OnhandSummary();
						if("PensItem".equalsIgnoreCase(f.getSummaryType())){
						    key = rst.getString("CUSTOMER_CODE")+rst.getString("group_type")+rst.getString("pens_item");
						}else{
							key = rst.getString("CUSTOMER_CODE")+rst.getString("group_type");
						}
						
						item.setStoreCode(rst.getString("CUSTOMER_CODE"));
						item.setStoreName(rst.getString("customer_desc"));
						if("PensItem".equalsIgnoreCase(f.getSummaryType())){
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
				item = new OnhandSummary();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(f.getSummaryType())){
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
				pos = new ArrayList<OnhandSummary>(rowMap.values());
				//Sort by StoreCode,GroupCode
				 Collections.sort(pos, OnhandSummary.Comparators.STORE_CODE_GROUP_ASC);
				 
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
	  
	  public List<OnhandSummary> searchBmeTrans(OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String christSalesDateFromStr ="";
			String christSalesDateToStr ="";
			try {
				conn = DBConnection.getInstance().getConnection();
				
				//prepare parameter
				Date asofDateFrom = Utils.parse(c.getAsOfDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateFromStr = Utils.stringValue(asofDateFrom, Utils.DD_MM_YYYY_WITH_SLASH);
					
				Date asofDateTo = Utils.parse(c.getAsOfDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateToStr = Utils.stringValue(asofDateTo, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n SELECT A.* FROM(");
				sql.append("\n SELECT M.*");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
				sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
				sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
				sql.append("\n , NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) as ADJUST_QTY ");
				sql.append("\n , NVL(STOCK_SHORT.STOCK_SHORT_QTY,0) as STOCK_SHORT_QTY ");
				
				sql.append("\n , (NVL(SALE_IN.SALE_IN_QTY,0) " + //SaleIn
								"\n -(" +
								"\n    NVL(SALE_OUT.SALE_OUT_QTY,0)  " +//SaleOut
								"\n  + NVL(SALE_RETURN.SALE_RETURN_QTY,0) " +//Return
								"\n  )" +
								"\n  + ( NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) )" + //Adjust
								"\n  + NVL(STOCK_SHORT.STOCK_SHORT_QTY,0) " +//Short
						   "\n  ) ONHAND_QTY");
				
				sql.append("\n FROM(  ");
						sql.append("\n SELECT DISTINCT ");
						sql.append("\n C.customer_code,P.inventory_item_code as pens_item,   ");
						sql.append("\n (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = C.customer_code AND M.reference_code ='Store') as customer_desc, ");
						sql.append("\n substr(P.inventory_item_desc,0,6) as group_type ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");

		                sql.append("\n AND V.invoice_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
		                sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
		                 
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n UNION ");
						
						sql.append("\n SELECT distinct ");
						sql.append("\n J.store_code as customer_code,I.pens_item,  ");
						sql.append("\n (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = J.store_code AND M.reference_code ='Store') as customer_desc, ");
						sql.append("\n I.group_code as group_type ");
						
						sql.append("\n FROM PENSBME_PICK_JOB J   ");
						sql.append("\n ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND J.job_id = B.job_id  ");
						sql.append("\n AND B.job_id = I.job_id ");
						sql.append("\n AND B.box_no = I.box_no ");

						sql.append("\n AND J.close_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
	                    sql.append("\n AND J.close_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n UNION ");
					
						sql.append("\n SELECT distinct L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
						sql.append("\n (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = L.PENS_CUST_CODE AND M.reference_code ='Store') as customer_desc, ");
							sql.append("\n L.PENS_GROUP_TYPE as group_type ");
							sql.append("\n FROM ");
							sql.append("\n PENSBI.PENSBME_SALES_FROM_LOTUS L ");
							sql.append("\n WHERE 1=1 ");
		                    sql.append("\n AND L.sales_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
		                    sql.append("\n AND L.sales_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
		                     
							if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
								sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
							}
							if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
								sql.append("\n AND L.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
								sql.append("\n AND L.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
							}
							if( !Utils.isNull(c.getGroup()).equals("")){
								sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
							}
							
               sql.append("\n )M ");
       		sql.append("\n LEFT OUTER JOIN(	 ");
       				sql.append("\n SELECT ");
       				sql.append("\n L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
						sql.append("\n L.PENS_GROUP_TYPE as group_type, ");
						sql.append("\n NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
						sql.append("\n FROM ");
						sql.append("\n PENSBI.PENSBME_SALES_FROM_LOTUS L ");
						sql.append("\n WHERE 1=1 ");
	                    sql.append("\n AND L.sales_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
	                    sql.append("\n AND L.sales_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
	                     
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n  GROUP BY L.PENS_CUST_CODE,L.PENS_ITEM,L.PENS_GROUP_TYPE ");
						sql.append("\n )SALE_OUT ");
						sql.append("\n ON  M.customer_code = SALE_OUT.customer_code and M.pens_item = SALE_OUT.pens_item ");	 
						sql.append("\n AND M.group_type = SALE_OUT.group_type ");
						
				sql.append("\n LEFT OUTER JOIN( ");
						sql.append("\n SELECT  ");
						sql.append("\n C.customer_code ,P.inventory_item_code as pens_item,  ");
						sql.append("\n substr(P.inventory_item_desc,0,6) as group_type, ");
						sql.append("\n NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						
	                    sql.append("\n AND V.invoice_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
	                    sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
							
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_code,P.inventory_item_code,  ");
						sql.append("\n substr(P.inventory_item_desc,0,6) ");
				sql.append("\n )SALE_IN ");
				sql.append("\n ON  M.customer_code = SALE_IN.customer_code and M.pens_item = SALE_IN.pens_item ");
				sql.append("\n AND M.group_type  = SALE_IN.group_type ");
				sql.append("\n LEFT OUTER JOIN ( ");
				
					sql.append("\n SELECT  ");
					sql.append("\n A.customer_code,A.pens_item,A.group_type,  ");
					sql.append("\n NVL(SUM(A.SALE_RETURN_QTY),0)  as SALE_RETURN_QTY ");
					sql.append("\n FROM( ");
			        	sql.append("\n SELECT  ");
						sql.append("\n J.store_code as customer_code,I.pens_item,  ");
						sql.append("\n I.group_code as group_type, ");
						sql.append("\n COUNT(*) as SALE_RETURN_QTY ");
						
						sql.append("\n FROM PENSBME_PICK_JOB J   ");
						sql.append("\n ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND J.job_id = B.job_id  ");
						sql.append("\n AND B.job_id = I.job_id ");
						sql.append("\n AND B.box_no = I.box_no ");
						sql.append("\n AND I.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
						sql.append("\n AND J.close_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
	                    sql.append("\n AND J.close_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY ");
						sql.append("\n J.store_code ,I.pens_item ,I.group_code ");
				        
				     sql.append("\n )A ");
				     sql.append("\n GROUP BY A.customer_code,A.pens_item,A.group_type ");

				sql.append("\n )SALE_RETURN ");
				sql.append("\n  ON  M.customer_code = SALE_RETURN.customer_code and M.pens_item = SALE_RETURN.pens_item ");
				sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
				
				//Stock Issue
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n SELECT ");
				sql.append("\n L.store_code as customer_code,L.item_issue as pens_item,L.item_issue_desc as group_type, ");
				sql.append("\n (NVL(SUM(ITEM_ISSUE_QTY),0)*-1) AS ISSUE_QTY ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
				// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
                 sql.append("\n AND L.transaction_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
                 sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
                  
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.item_issue_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n  GROUP BY ");
				sql.append("\n  L.STORE_CODE,L.item_issue, ");
				sql.append("\n  L.item_issue_desc ");
				sql.append("\n )STOCK_ISSUE ");
				sql.append("\n ON  M.customer_code = STOCK_ISSUE.customer_code and M.pens_item = STOCK_ISSUE.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_ISSUE.group_type ");
				
				//Stock Receipt
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n SELECT ");
				sql.append("\n L.STORE_CODE as customer_code,L.item_receipt as pens_item,L.item_receipt_desc as group_type, ");
				sql.append("\n NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_ADJUST_INVENTORY L  WHERE 1=1 ");
				//L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");
                 sql.append("\n AND L.transaction_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
                 sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.item_receipt_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n  GROUP BY ");
				sql.append("\n  L.STORE_CODE,L.item_receipt, ");
				sql.append("\n  L.item_receipt_desc ");
				sql.append("\n )STOCK_RECEIPT ");
				sql.append("\n ON  M.customer_code = STOCK_RECEIPT.customer_code and M.pens_item = STOCK_RECEIPT.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_RECEIPT.group_type ");
				
				//STOCK_SHORT
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n SELECT ");
				sql.append("\n L.STORE_CODE as customer_code,L.item_adjust as pens_item,L.item_adjust_desc as group_type, ");
				sql.append("\n NVL(SUM(ITEM_ADJUST_QTY),0) AS STOCK_SHORT_QTY ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_ADJUST_SALES L  WHERE 1=1 ");	 
	
                 sql.append("\n AND L.transaction_date >= to_date('"+christSalesDateFromStr+"','dd/mm/yyyy')  ");
                 sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateToStr+"','dd/mm/yyyy')  ");
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.item_adjust_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n  GROUP BY ");
				sql.append("\n  L.STORE_CODE,L.item_adjust, ");
				sql.append("\n  L.item_adjust_desc ");
				sql.append("\n )STOCK_SHORT ");
				sql.append("\n ON  M.customer_code = STOCK_SHORT.customer_code and M.pens_item = STOCK_SHORT.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_SHORT.group_type ");
				
				sql.append("\n ) A ");
				sql.append("\n ORDER BY A.customer_code,A.group_type asc ");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					//item.setStoreName(rst.getString("customer_desc"));
					item.setPensItem(rst.getString("pens_item"));
					//item.setItemDesc(rst.getString("inventory_item_desc"));
					item.setGroup(rst.getString("group_type"));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					
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
	  
	  public OnhandSummary searchOnhandMTT(OnhandSummary c,Date initDate,User user,String summaryType) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			double initSaleQty = 0;
		    double saleInQtyTemp = 0;
		    double saleReturnQtyTemp = 0;
		    double saleOutQtyTemp = 0;
		   // double adjustQtyTemp =0;
		  //  double stockShortQtyTemp = 0;
		    double onhandQtyTemp = 0;
			try {
				conn = DBConnection.getInstance().getConnection();
		
				sql = ReportOnhandMTTSQL.genSQL(conn, c, user, initDate,summaryType);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setCustNo(rst.getString("cust_no"));
					item.setStoreName(rst.getString("customer_desc"));
					if(!"GroupCode".equalsIgnoreCase(summaryType)){	
					  item.setPensItem(rst.getString("pens_item"));
					}
					item.setGroup(rst.getString("group_type"));
					item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("init_sale_qty"),Utils.format_current_no_disgit));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					
					initSaleQty += Utils.convertStrToDouble(item.getInitSaleQty());
					saleInQtyTemp += Utils.convertStrToDouble(item.getSaleInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(item.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(item.getSaleOutQty());
					onhandQtyTemp +=Utils.convertStrToDouble(item.getOnhandQty());
					
					pos.add(item);
				}//while
				
				//Summary
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(summaryType)){
				   item.setPensItem("");
				}
				item.setGroup("");
				item.setInitSaleQty(Utils.decimalFormat(initSaleQty,Utils.format_current_no_disgit));
				item.setSaleInQty(Utils.decimalFormat(saleInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
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
	  
	  public List<OnhandSummary> searchOnhandMTTDetail(OnhandSummary c,Date initDate,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String storeName = "";
			String custNoOracle = "";
			try {
				conn = DBConnection.getInstance().getConnection();
				
				storeName = GeneralDAO.getStoreNameModel(conn, c.getPensCustCodeFrom());
		        custNoOracle = GeneralDAO.getCustNoOracleModel(conn, c.getPensCustCodeFrom());
				
				sql = ReportOnhandMTTDetailSQL.genSQL(conn, c, user, initDate);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(c.getPensCustCodeFrom());
					item.setCustNo(custNoOracle);
					item.setStoreName(storeName);
					
					item.setPensItem(rst.getString("pens_item"));
					item.setMaterialMaster(rst.getString("material_master"));
					item.setBarcode(rst.getString("barcode"));
					
					item.setGroup(rst.getString("group_type"));
					item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("init_sale_qty"),Utils.format_current_no_disgit));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
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
	  
	  
	  public OnhandSummary searchOnhandBigC_ASOF(OnhandSummary c,User user) throws Exception{
		    Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			double transInInitQtyTemp = 0;
			double transInQtyTemp = 0;
		    double saleReturnQtyTemp = 0;
		    double saleOutQtyTemp = 0;
		    double adjustQtyTemp =0;
		    double stockShortQtyTemp = 0;
		    double onhandQtyTemp = 0;
			try {
				conn = DBConnection.getInstance().getConnection();
				sql = ReportOnhandBigC_ASOF_SQL.genSQL(conn, c, user);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreName(rst.getString("customer_name"));
					item.setSubInv(rst.getString("sub_inv"));
					item.setPensItem(rst.getString("pens_item"));
					item.setGroup(rst.getString("group_type"));
					
					item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("INIT_QTY"),Utils.format_current_no_disgit));
					item.setTransInQty(Utils.decimalFormat(rst.getDouble("TRANS_IN_QTY"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("SALEOUT_SHORT_QTY"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("adjust_qty"),Utils.format_current_no_disgit));
					//item.setStockShortQty(Utils.decimalFormat(rst.getDouble("short_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					
					transInInitQtyTemp += Utils.convertStrToDouble(item.getInitSaleQty());
					transInQtyTemp += Utils.convertStrToDouble(item.getTransInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(item.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(item.getSaleOutQty());
					adjustQtyTemp +=Utils.convertStrToDouble(item.getAdjustQty());
					stockShortQtyTemp +=Utils.convertStrToDouble(item.getStockShortQty());
					onhandQtyTemp +=Utils.convertStrToDouble(item.getOnhandQty());
					
					pos.add(item);
					
				}//while
				
				//Summary
				OnhandSummary item = new OnhandSummary();
				item.setStoreCode("");
				item.setGroup("");
				item.setInitSaleQty(Utils.decimalFormat(transInInitQtyTemp,Utils.format_current_no_disgit));
				item.setTransInQty(Utils.decimalFormat(transInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
				item.setAdjustQty(Utils.decimalFormat(adjustQtyTemp,Utils.format_current_no_disgit));
				item.setStockShortQty(Utils.decimalFormat(stockShortQtyTemp,Utils.format_current_no_disgit));
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
	  
	  public List<OnhandSummary> searchOnhandBigC_SP(SummaryForm summaryForm, OnhandSummary c,Date initDate) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				
				sql = ReportOnhandBigC_SPSQL.genSQL(conn, c,initDate,summaryForm.getSummaryType());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					//logger.debug("query");
                    OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(Utils.isNull(rst.getString("store_code")));
					item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
					item.setStoreName(Utils.isNull(rst.getString("store_name")));
					if( "PensItem".equalsIgnoreCase(summaryForm.getSummaryType())){
					   item.setPensItem(rst.getString("pens_item"));
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
	  
	  public List<OnhandSummary> searchOnhandLotusPeriod(OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				
				//prepare parameter
				String christSalesDateStrFrom ="";
				String christSalesDateStrTo ="";
				
				if( !Utils.isNull(c.getAsOfDateFrom()).equals("")){
					Date d = Utils.parse(c.getAsOfDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStrFrom = Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH);
				}

				if( !Utils.isNull(c.getAsOfDateTo()).equals("")){
					Date d = Utils.parse(c.getAsOfDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStrTo = Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH);
				}
				
				sql.append("\n SELECT A.* FROM(");
				sql.append("\n SELECT M.* ");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
				sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
				sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) - (NVL(SALE_OUT.SALE_OUT_QTY,0)+NVL(SALE_RETURN.SALE_RETURN_QTY,0)) ONHAND_QTY");
				sql.append("\n FROM(  ");
						sql.append("\n SELECT DISTINCT ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n C.customer_desc, ");
						sql.append("\n substr(P.inventory_item_desc,0,6) as group_type ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047%'");
						
						if( !Utils.isNull(c.getAsOfDateFrom()).equals("") && !Utils.isNull(c.getAsOfDateTo()).equals("")){
		                    sql.append("\n AND V.invoice_date >= to_date('"+christSalesDateStrFrom+"','dd/mm/yyyy')  ");
		                    sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStrTo+"','dd/mm/yyyy')  ");
						}
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
               sql.append("\n )M ");
       		sql.append("\n LEFT OUTER JOIN(	 ");
       				sql.append("\n SELECT ");
       				sql.append("\n L.PENS_CUST_CODE as customer_code, ");
						sql.append("\n L.PENS_CUST_DESC as customer_desc, ");
						sql.append("\n L.PENS_GROUP_TYPE as group_type, ");
						sql.append("\n NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
						sql.append("\n FROM ");
						sql.append("\n PENSBI.PENSBME_SALES_FROM_LOTUS L ");
						sql.append("\n,( ");
								sql.append("\n select distinct pens_value,pens_desc2 from ");
								sql.append("\n PENSBI.PENSBME_MST_REFERENCE M ");
								sql.append("\n WHERE 1=1 ");
								sql.append("\n AND M.reference_code ='LotusItem' ");
						sql.append("\n ) M ");
						sql.append("\n WHERE 1=1 ");
						sql.append("\n AND M.pens_desc2 = L.pens_group_type ");
						
						if( !Utils.isNull(c.getAsOfDateFrom()).equals("") && !Utils.isNull(c.getAsOfDateTo()).equals("")){
		                    sql.append("\n AND L.sales_date >= to_date('"+christSalesDateStrFrom+"','dd/mm/yyyy')  ");
		                    sql.append("\n AND L.sales_date <= to_date('"+christSalesDateStrTo+"','dd/mm/yyyy')  ");
						}
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND M.pens_value >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND M.pens_value <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n  GROUP BY ");
						sql.append("\n  L.PENS_CUST_CODE, ");
						sql.append("\n  L.PENS_CUST_DESC, ");
						sql.append("\n  L.PENS_GROUP_TYPE ");
						sql.append("\n )SALE_OUT ");
						sql.append("\n ON  M.Customer_code = SALE_OUT.Customer_code ");	 
						sql.append("\n AND M.group_type = SALE_OUT.group_type ");
				sql.append("\n LEFT OUTER JOIN( ");
						sql.append("\n SELECT  ");
						sql.append("\n C.customer_id,  ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n substr(P.inventory_item_desc,0,6) as group_type, ");
						sql.append("\n NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047%'");
						
						if( !Utils.isNull(c.getAsOfDateFrom()).equals("") && !Utils.isNull(c.getAsOfDateTo()).equals("")){
		                    sql.append("\n AND V.invoice_date >= to_date('"+christSalesDateStrFrom+"','dd/mm/yyyy')  ");
		                    sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStrTo+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
							
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_id,  ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n substr(P.inventory_item_desc,0,6) ");
				sql.append("\n )SALE_IN ");
				sql.append("\n ON  M.Customer_code = SALE_IN.Customer_code ");
				sql.append("\n AND M.group_type  = SALE_IN.group_type ");
				sql.append("\n LEFT OUTER JOIN ( ");
						sql.append("\n SELECT  ");
						sql.append("\n C.customer_id,  ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n substr(P.inventory_item_desc,0,6) as group_type, ");
						sql.append("\n NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047%'");
						
						if( !Utils.isNull(c.getAsOfDateFrom()).equals("") && !Utils.isNull(c.getAsOfDateTo()).equals("")){
		                    sql.append("\n AND V.invoice_date >= to_date('"+christSalesDateStrFrom+"','dd/mm/yyyy')  ");
		                    sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStrTo+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_id,  ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n substr(P.inventory_item_desc,0,6)" );
				sql.append("\n )SALE_RETURN ");
				sql.append("\n  ON  M.Customer_code = SALE_RETURN.Customer_code ");
				sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
				sql.append("\n ) A ");
				sql.append("\n ORDER BY A.customer_code,A.group_type asc ");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreName(rst.getString("customer_desc"));
					//item.setItem(rst.getString("inventory_item_code"));
					//item.setItemDesc(rst.getString("inventory_item_desc"));
					item.setGroup(rst.getString("group_type"));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
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
	  
	  /** Compare by pens_item */
	  public List<OnhandSummary> searchOnhandLotusVersion1(OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				
				//prepare parameter
				String christSalesDateStr ="";
				if( !Utils.isNull(c.getSalesDate()).equals("")){
					Date d = Utils.parse(c.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStr = Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH);
				}
				sql.append("\n SELECT A.* FROM(");
				sql.append("\n SELECT M.* ");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
				sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
				sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) - (NVL(SALE_OUT.SALE_OUT_QTY,0)+NVL(SALE_RETURN.SALE_RETURN_QTY,0)) ONHAND_QTY");
				sql.append("\n FROM(  ");
						sql.append("\n SELECT DISTINCT ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n C.customer_desc, ");
						sql.append("\n substr(P.inventory_item_desc,0,6) as group_desc, ");
						sql.append("\n P.inventory_item_code,  ");
						sql.append("\n P.inventory_item_desc ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
		                    sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}

               sql.append("\n )M ");
       		sql.append("\n LEFT OUTER JOIN(	 ");
       				sql.append("\n SELECT ");
       				sql.append("\n L.PENS_CUST_CODE as customer_code, ");
						sql.append("\n L.PENS_CUST_DESC as customer_desc, ");
						sql.append("\n L.PENS_GROUP_TYPE as group_desc, ");
						sql.append("\n L.pens_item as inventory_item_code, ");
						sql.append("\n L.description as inventory_item_desc, ");
						sql.append("\n NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
						sql.append("\n FROM ");
						sql.append("\n PENSBI.PENSBME_SALES_FROM_LOTUS L ");
						sql.append("\n WHERE 1=1 ");
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND L.sales_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
							sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY ");
						sql.append("\n L.PENS_CUST_CODE, ");
						sql.append("\n L.PENS_CUST_DESC, ");
						sql.append("\n L.PENS_GROUP_TYPE, ");
						sql.append("\n L.pens_item, ");
						sql.append("\n L.description  ");
						sql.append("\n )SALE_OUT ");
						sql.append("\n ON  M.Customer_code = SALE_OUT.Customer_code ");
						sql.append("\n AND M.inventory_item_code = SALE_OUT.inventory_item_code		 ");	 
				sql.append("\n LEFT OUTER JOIN( ");
						sql.append("\n SELECT  ");
						sql.append("\n C.customer_id,  ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n V.inventory_item_id,  ");
						sql.append("\n P.inventory_item_code,  ");
						sql.append("\n P.inventory_item_desc , ");
						sql.append("\n NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
							
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_id,  ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n V.inventory_item_id,  ");
						sql.append("\n P.inventory_item_code,  ");
						sql.append("\n P.inventory_item_desc  ");
				sql.append("\n )SALE_IN ");
				sql.append("\n ON  M.Customer_code=SALE_IN.Customer_code ");
				sql.append("\n AND M.inventory_item_code = SALE_IN.inventory_item_code ");
				sql.append("\n LEFT OUTER JOIN ( ");
						sql.append("\n SELECT  ");
						sql.append("\n C.customer_id,  ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n V.inventory_item_id,  ");
						sql.append("\n P.inventory_item_code,  ");
						sql.append("\n P.inventory_item_desc , ");
						sql.append("\n NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_id,  ");
						sql.append("\n C.customer_code,   ");
						sql.append("\n V.inventory_item_id,  ");
						sql.append("\n P.inventory_item_code,  ");
					    sql.append("\n P.inventory_item_desc  ");
				sql.append("\n )SALE_RETURN ");
				sql.append("\n  ON  M.Customer_code=SALE_RETURN.Customer_code ");
				sql.append("\n  AND M.inventory_item_code = SALE_RETURN.inventory_item_code ");
				
				sql.append("\n ) A ");
				sql.append("\n ORDER BY A.customer_code,A.group_desc,A.inventory_item_code asc ");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreName(rst.getString("customer_desc"));
					item.setItem(rst.getString("inventory_item_code"));
					item.setItemDesc(rst.getString("inventory_item_desc"));
					item.setGroup(rst.getString("group_desc"));
					//item.setQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					
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
	  
	  /** Month End Report Lotus **/
	  public List<OnhandSummary> searchOnhandMonthEndLotus(SummaryForm form, OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				//Gen SQL
			    sql = ReportMonthEndLotusSQL.genSQL(conn,c,user,form.getSummaryType());
			    
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreName(rst.getString("customer_desc"));
					if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  item.setPensItem(rst.getString("pens_item"));
					}
					item.setGroup(rst.getString("group_type"));
					item.setBeginingQty(Utils.decimalFormat(rst.getDouble("begining_qty"),Utils.format_current_no_disgit));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					
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
