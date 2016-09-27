package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import util.Constants;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.DiffStockSummary;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.sql.ReportMonthEndLotusSQL;
import com.isecinc.pens.sql.ReportOnhandBigCSQL;
import com.isecinc.pens.sql.ReportOnhandBigC_SPSQL;
import com.isecinc.pens.sql.ReportOnhandLotus_SQL;
import com.isecinc.pens.sql.ReportOnhandMTTSQL;
import com.isecinc.pens.sql.ReportSizeColorBigCSQL;
import com.isecinc.pens.sql.ReportSizeColorLotus_SQL;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.summary.SummaryForm;

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

	  public List<TransactionSummary> search(TransactionSummary c,User user,String type) throws Exception{
	    	List<TransactionSummary> summaryList = new ArrayList<TransactionSummary>();
	    	
	    	Connection conn = null;
	    	try{
	    		conn = DBConnection.getInstance().getConnection();
	    		if("lotus".equalsIgnoreCase(type)){
	    		   summaryList = searchLotus(c,conn,user);
	    		}else if("bigc".equalsIgnoreCase(type)){
	    		   summaryList = searchBigC(c,conn,user);	
	    		}else if("tops".equalsIgnoreCase(type)){
		    	   summaryList = searchTops(c,conn,user);	
	    		}else if("king".equalsIgnoreCase(type)){
			       summaryList = searchKing(c,conn,user);	
			    }
	    	}catch(Exception e){
	    		logger.error(e.getMessage(),e);
	    	}finally{
	    		if(conn != null){
	    			conn.close();conn= null;
	    		}
	    	}
	    	return summaryList;
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
	  
	  public List<TransactionSummary> searchLotus(TransactionSummary c,Connection conn,User user) throws Exception {
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
	  
	  public List<TransactionSummary> searchTops(TransactionSummary c,Connection conn,User user) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
			StringBuilder sql = new StringBuilder();
			boolean salesDateFlag = false;
			Date salesDateFromParam = null;
			Date salesDateToParam = null;
			try {
				sql.delete(0, sql.length());
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
	  
	  public List<TransactionSummary> searchKing(TransactionSummary c,Connection conn,User user) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
			StringBuilder sql = new StringBuilder();
			boolean salesDateFlag = false;
			Date salesDateFromParam = null;
			Date salesDateToParam = null;
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
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* , \n");
				sql.append("\n  (select max(M1.interface_desc) from pensbi.PENSBME_MST_REFERENCE M1 ");
				sql.append("\n   where M1.reference_code = 'SubInv' and M1.pens_value =M.pens_value) as sub_inv ");
				
				sql.append("\n  from PENSBME_MST_REFERENCE M");
				
				sql.append("\n  where 1=1 and reference_code ='Store' ");
			
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
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_2+"%')  \n");
						
					}else if(storeType.equalsIgnoreCase("king")){
						sql.append(" and (pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER+"%' \n");
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_2+"%' ) \n");
					}
				}
				sql.append("\n  and pens_desc4 ='N' \n");
				sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("xx sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
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
	  
	  public List<PopupForm> searchGroupMaster(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
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
			Statement stmt = null;
			ResultSet rst = null;
			Date initDate = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct trunc(Count_stk_date) as Count_stk_date from PENSBME_MTT_INIT_STK \n");
				sql.append("\n  where 1=1 and Cust_no ='"+custNo+"' \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
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
					conn.close();
				} catch (Exception e) {}
			}
			return initDate;
		}
	  
	  public Date searchInitDateBigC(String custNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			Date initDate = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct trunc(max(Count_stk_date)) as Count_stk_date from PENSBME_BIGC_INIT_STK \n");
				sql.append("\n  where 1=1 and Cust_no ='"+custNo+"' \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
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
					conn.close();
				} catch (Exception e) {}
			}
			return initDate;
		}
	  
	  public Date searchInitDateLotus(String custNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			Date initDate = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT distinct trunc(max(Count_stk_date)) as Count_stk_date from PENSBME_LOTUS_INIT_STK \n");
				sql.append("\n  where 1=1 and Cust_no ='"+custNo+"' \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
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
					conn.close();
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
				
				sql.append("\n FROM ");
				sql.append("\n PENSBI.XXPENS_BI_MST_ITEM ");
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
	  
	  /** Compare by pens_group_item **/
	  public List<OnhandSummary> searchOnhandLotus(SummaryForm f,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
                sql = ReportOnhandLotus_SQL.genSQL(conn, c, f.getSummaryType());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					//item.setStoreName(rst.getString("customer_desc"));
					if("PensItem".equalsIgnoreCase(f.getSummaryType())){
					   item.setPensItem(rst.getString("pens_item"));
					}
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
	  
	  public List<OnhandSummary> searchReportEndDateLotus(SummaryForm f,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			Date asofDate = null;
			try {
				String christSalesDateStr ="";
				if( !Utils.isNull(c.getSalesDate()).equals("")){
					asofDate = Utils.parse(c.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStr = Utils.stringValue(asofDate, Utils.DD_MM_YYYY_WITH_SLASH);
				}

				conn = DBConnection.getInstance().getConnection();
				sql.append("\n SELECT  ");
				sql.append("\n  STORE_CODE,ENDING_DATE,GROUP_CODE,PENS_ITEM   ");
				sql.append("\n ,SALE_IN_QT,SALE_OUT_QTY,SALE_RETURN_QTY ");
				sql.append("\n ,ADJUST_QTY,SHORT_QTY,ENDING_QTY ");
				sql.append("\n FROM PENSBME_ENDDATE_STOCK V   ");
				sql.append("\n WHERE 1=1   ");
				
				//Lotus Only 020047
				sql.append("\n AND V.STORE_CODE LIKE '020047%'");
				
				if( !Utils.isNull(c.getSalesDate()).equals("")){
                    sql.append("\n AND V.ending_date == to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				    sql.append("\n AND V.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND V.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND V.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND V.GROUP_CODE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("STORE_CODE"));
					//item.setStoreName(rst.getString("customer_desc"));
					//if("PensItem".equalsIgnoreCase(f.getSummaryType())){
					   item.setPensItem(rst.getString("pens_item"));
					//}
					//item.setItemDesc(rst.getString("inventory_item_desc"));
					item.setGroup(rst.getString("group_code"));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("ending_qty"),Utils.format_current_no_disgit));
					
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
	  
	  public List<OnhandSummary> searchOnhandMTT(OnhandSummary c,Date initDate,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
		
				sql = ReportOnhandMTTSQL.genSQL(conn, c, user, initDate);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setCustNo(rst.getString("cust_no"));
					item.setStoreName(rst.getString("customer_desc"));
					
					item.setPensItem(rst.getString("pens_item"));
					
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
		        		
				//prepare parameter
				String christSalesDateStr ="";
				if( !Utils.isNull(c.getSalesDate()).equals("")){
					Date d = Utils.parse(c.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStr = Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH);
				}
				String initDateStr ="";
				if( initDate != null){
					initDateStr = Utils.stringValue(initDate, Utils.DD_MM_YYYY_WITH_SLASH);
				}
				
				sql.append("\n SELECT A.* FROM(");
				sql.append("\n SELECT M.*");
				
				sql.append("\n , NVL(INIT_MTT.INIT_SALE_QTY,0) AS INIT_SALE_QTY");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
				sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
				sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
				sql.append("\n , (NVL(INIT_MTT.INIT_SALE_QTY,0) + NVL(SALE_IN.SALE_IN_QTY,0)) - (NVL(SALE_OUT.SALE_OUT_QTY,0)+NVL(SALE_RETURN.SALE_RETURN_QTY,0)) ONHAND_QTY");
				
				sql.append("\n FROM(  ");
				   sql.append("\n SELECT DISTINCT AA.* FROM(");
					    sql.append("\n SELECT DISTINCT ");
						sql.append("\n M.pens_item, L.group_code as group_type, M.material_master,L.barcode ");
						sql.append("\n FROM  PENSBME_ORDER L");
						sql.append("\n ,( ");
						sql.append("\n   select pens_value as pens_item,");
						sql.append("\n   interface_value as material_master,interface_desc as barcode ");
						sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
						sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
						sql.append("\n   )M ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND L.barcode = M.barcode  ");
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND M.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND M.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
					    sql.append("\n UNION ALL");
					    
						sql.append("\n SELECT DISTINCT ");
						sql.append("\n L.pens_item, L.group_item as group_type, L.material_master,L.barcode ");
						sql.append("\n FROM  PENSBME_ONHAND_BME_LOCKED L ");
						sql.append("\n WHERE 1=1   ");
						
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.group_item IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n UNION ALL");
						
						sql.append("\n SELECT ");
      				    sql.append("\n DISTINCT L.PENS_ITEM,L.GROUP_CODE as group_type, L.material_master,L.barcode ");
						sql.append("\n FROM PENSBI.PENSBME_MTT_INIT_STK H,PENSBME_MTT_ONHAND_INIT_STK L");
						sql.append("\n WHERE 1=1 ");
						sql.append("\n AND H.cust_no = L.cust_no  ");
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.CUST_NO IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.GROUP_CODE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						
						sql.append("\n UNION ALL");
						
						sql.append("\n SELECT DISTINCT ");
						sql.append("\n  P.inventory_item_code as pens_item, substr(P.inventory_item_desc,0,6) as group_type ");
						sql.append("\n ,MI.material_master,MI.barcode ");
						sql.append("\n  FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n ,( ");
						sql.append("\n   select pens_value as pens_item,");
						sql.append("\n   interface_value as material_master,interface_desc as barcode ");
						sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
						sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
						sql.append("\n  )MI ");
						sql.append("\n ,( ");
						sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
						sql.append("\n   PENSBI.PENSBME_MST_REFERENCE M ");
						sql.append("\n   WHERE  ( pens_value like  '"+Constants.STORE_TYPE_MTT_CODE_1+"%' " );
						sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER+"%'");
						sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_HISHER_CODE+"%')");
						sql.append("\n   AND M.reference_code ='Store' ");
				        sql.append("\n  ) M ");
				  
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id ");
						sql.append("\n AND M.cust_no = C.customer_code  ");
						sql.append("\n AND P.inventory_item_code = MI.pens_item  ");
						
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						
						if(initDate != null){
							 sql.append("\n AND V.invoice_date > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND M.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
							
						sql.append("\n )AA");
						
              sql.append("\n )M ");
              sql.append("\n LEFT OUTER JOIN(	 ");
		            /** INIT MTT STOCK **/
	      		    sql.append("\n SELECT ");
					sql.append("\n L.PENS_ITEM, L.GROUP_CODE as group_type, L.material_master,L.barcode,");
					sql.append("\n SUM(QTY) AS INIT_SALE_QTY ");
					sql.append("\n FROM PENSBI.PENSBME_MTT_INIT_STK H,PENSBME_MTT_ONHAND_INIT_STK L");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n and H.cust_no = L.cust_no  ");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.CUST_NO IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.GROUP_CODE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n  GROUP BY L.PENS_ITEM,L.GROUP_CODE, L.material_master,L.barcode ");
					sql.append("\n )INIT_MTT ");
					sql.append("\n ON  M.pens_item = INIT_MTT.pens_item ");	 
					sql.append("\n AND M.group_type = INIT_MTT.group_type ");		
					sql.append("\n AND M.material_master = INIT_MTT.material_master AND M.barcode = INIT_MTT.barcode ");	
					
      		   sql.append("\n LEFT OUTER JOIN(	 ");
      				sql.append("\n SELECT ");
					sql.append("\n L.PENS_ITEM, ");
				    sql.append("\n L.GROUP_CODE as group_type, L.material_master,L.barcode , ");
					sql.append("\n NVL(COUNT(*),0) AS SALE_OUT_QTY ");
					sql.append("\n FROM PENSBI.PENSBME_SALES_OUT L");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n AND L.status <> '"+PickConstants.STATUS_CANCEL+"'");
						
						if(initDate != null){
							 sql.append("\n AND L.sale_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.CUST_NO IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.GROUP_CODE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n  GROUP BY ");
						sql.append("\n  L.PENS_ITEM, L.GROUP_CODE , L.material_master,L.barcode ");
						
						sql.append("\n ) SALE_OUT ");
						
						sql.append("\n ON  M.pens_item = SALE_OUT.pens_item ");	 
						sql.append("\n AND M.group_type = SALE_OUT.group_type ");
						sql.append("\n AND M.material_master = SALE_OUT.material_master AND M.barcode = SALE_OUT.barcode ");
						
				sql.append("\n LEFT OUTER JOIN( ");
				    sql.append("\n SELECT  ");
				    sql.append("\n A.pens_item, A.group_type,A.material_master,A.barcode,NVL(SUM(A.SALE_IN_QTY),0)  as SALE_IN_QTY ");
				    sql.append("\n FROM (  ");
							sql.append("\n SELECT  ");
							sql.append("\n MI.pens_item, M.group_code as group_type,MI.material_master,MI.barcode,");
							sql.append("\n NVL(SUM(QTY),0)  as SALE_IN_QTY ");
							sql.append("\n FROM PENSBME_ORDER M ");
							sql.append("\n ,( ");
							sql.append("\n   select pens_value as pens_item,");
							sql.append("\n   interface_value as material_master,interface_desc as barcode ");
							sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
							sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
							sql.append("\n  )MI ");
							sql.append("\n WHERE MI.BARCODE = M.BARCODE  ");
							if(initDate != null){
								 sql.append("\n AND M.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
								 sql.append("\n AND M.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
							}else{
								 sql.append("\n AND M.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
							}
							if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							    sql.append("\n AND M.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
							}
							if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
								sql.append("\n AND MI.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
								sql.append("\n AND MI.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
							}
							if( !Utils.isNull(c.getGroup()).equals("")){
								sql.append("\n AND M.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
							}
							sql.append("\n GROUP BY MI.pens_item,M.group_code,MI.material_master,MI.barcode ");
							
							sql.append("\n UNION ALL  ");
							
							sql.append("\n SELECT  ");
							sql.append("\n I.pens_item, I.group_code as group_type,I.material_master,MI.barcode, ");
							sql.append("\n NVL(COUNT(*),0)  as SALE_IN_QTY ");
							sql.append("\n FROM PENSBME_PICK_STOCK M ,PENSBI.PENSBME_PICK_STOCK_I I ");
							sql.append("\n ,( ");
							sql.append("\n   select pens_value as pens_item,");
							sql.append("\n   interface_value as material_master,interface_desc as barcode ");
							sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
							sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
							sql.append("\n  )MI ");
							sql.append("\n WHERE 1=1   ");
							sql.append("\n AND M.issue_req_no = I.issue_req_no ");
							sql.append("\n AND I.pens_item = MI.pens_item   ");
							sql.append("\n AND I.material_master = MI.material_master");
							sql.append("\n AND M.issue_req_status ='"+PickConstants.STATUS_ISSUED+"'");
							
							if(initDate != null){
								 sql.append("\n AND M.ISSUE_REQ_DATE  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
								 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
							}else{
								 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
							}
							if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							    sql.append("\n AND M.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
							}
							if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
								sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
								sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
							}
							if( !Utils.isNull(c.getGroup()).equals("")){
								sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
							}
							sql.append("\n GROUP BY I.pens_item,I.group_code,I.material_master,MI.barcode ");
							
							sql.append("\n UNION ALL  ");
							
							sql.append("\n SELECT  ");
							sql.append("\n I.pens_item, I.group_code as group_type,I.material_master,I.barcode, ");
							sql.append("\n NVL(SUM(I.ISSUE_QTY),0)  as SALE_IN_QTY ");
							sql.append("\n FROM PENSBME_STOCK_ISSUE M ,PENSBI.PENSBME_STOCK_ISSUE_ITEM I ");
							sql.append("\n WHERE 1=1   ");
							sql.append("\n AND M.issue_req_no = I.issue_req_no  ");
							sql.append("\n AND M.status ='"+PickConstants.STATUS_ISSUED+"'");
							
							if(initDate != null){
								 sql.append("\n AND M.ISSUE_REQ_DATE  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
								 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
							}else{
								 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
							}
							if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							    sql.append("\n AND M.customer_no IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
							}
							if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
								sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
								sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
							}
							if( !Utils.isNull(c.getGroup()).equals("")){
								sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
							}
							sql.append("\n GROUP BY I.pens_item,I.group_code,I.material_master,I.barcode ");
							
				  sql.append("\n ) A ");
				  sql.append("\n GROUP BY A.pens_item, A.group_type,A.material_master,A.barcode");
				sql.append("\n )SALE_IN ");
				
				sql.append("\n ON  M.pens_item = SALE_IN.pens_item ");
				sql.append("\n AND M.group_type  = SALE_IN.group_type ");
				sql.append("\n AND M.material_master = SALE_IN.material_master AND M.barcode = SALE_IN.barcode ");
				
				sql.append("\n LEFT OUTER JOIN ( ");
						sql.append("\n SELECT I.pens_item, ");
						sql.append("\n I.group_code as group_type,I.material_master,I.barcode ,");
						sql.append("\n NVL(COUNT(*),0) as SALE_RETURN_QTY ");
						sql.append("\n  FROM PENSBME_PICK_JOB J   ");
						sql.append("\n ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND J.job_id = B.job_id  ");
						sql.append("\n AND B.job_id = I.job_id ");
						sql.append("\n AND B.box_no = I.box_no ");
						sql.append("\n AND I.STATUS IN( '"+PickConstants.STATUS_RETURN+"' )");
						
						if(initDate != null){
							 sql.append("\n AND J.close_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND J.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND J.close_date   <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						
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
						sql.append("\n GROUP BY I.pens_item ,I.group_code ,I.material_master,I.barcode ");

				sql.append("\n )SALE_RETURN ");
				sql.append("\n  ON  M.pens_item = SALE_RETURN.pens_item ");
				sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
				sql.append("\n  AND M.material_master = SALE_RETURN.material_master AND M.barcode = SALE_RETURN.barcode ");
				
				sql.append("\n ) A ");
				sql.append("\n ORDER BY A.group_type,A.MATERIAL_MASTER,A.BARCODE asc ");
				
				logger.debug("sql:"+sql);
				
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
	  
	  public List<OnhandSummary> searchSizeColorBigCDetail(OnhandSummary c,Date initDate,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnection();
		     		
				sql = ReportSizeColorBigCSQL.genSQL(conn, c, initDate);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(Utils.isNull(rst.getString("store_code")));
					item.setSubInv(Utils.isNull(rst.getString("sub_inv")));
					item.setStoreName(Utils.isNull(rst.getString("store_name")));
					
					item.setPensItem(rst.getString("pens_item"));
					item.setMaterialMaster(rst.getString("material_master"));
					item.setBarcode(rst.getString("barcode"));
					
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
	  
	  public List<OnhandSummary> searchSizeColorLotusDetail(SummaryForm f,OnhandSummary c,Date initDate,User user) throws Exception{
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

	  
	  public List<OnhandSummary> searchOnhandLotus_OLD(OnhandSummary c,User user) throws Exception{
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
				sql.append("\n SELECT M.*");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
				sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
				sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) - (NVL(SALE_OUT.SALE_OUT_QTY,0)+NVL(SALE_RETURN.SALE_RETURN_QTY,0)) ONHAND_QTY");
				sql.append("\n FROM(  ");
						sql.append("\n SELECT DISTINCT ");
						sql.append("\n C.customer_code,   ");
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
						
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047%'");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
		                    sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
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
								 
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND L.sales_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
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
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
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
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
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
	  
	  public List<OnhandSummary> searchOnhandBigC(OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String cutOffDate = "01/08/2014";
			try {
				conn = DBConnection.getInstance().getConnection();
				
				sql = ReportOnhandBigCSQL.genSQL(conn, c, user,cutOffDate);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setStoreName(rst.getString("customer_name"));
					item.setSubInv(rst.getString("sub_inv"));
					item.setPensItem(rst.getString("pens_item"));
					
					item.setGroup(rst.getString("group_type"));
					item.setTransInQty(Utils.decimalFormat(rst.getDouble("TOTAL_TRANS_IN_QTY"),Utils.format_current_no_disgit));
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
