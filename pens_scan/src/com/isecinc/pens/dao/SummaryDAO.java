package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.Constants;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

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
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* , \n");
				sql.append("\n  (select M1.interface_desc from pensbi.PENSBME_MST_REFERENCE M1 ");
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
						sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_MTT_CODE_2+"%' ) \n");
					}
				}
				
				sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("sql:"+sql);
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
	  

	  

	
	  
}
