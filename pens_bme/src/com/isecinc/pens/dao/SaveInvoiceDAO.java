package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.bean.SaveInvoiceBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class SaveInvoiceDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 public static SaveInvoiceBean search(Connection conn,SaveInvoiceBean o,boolean getTrans ) throws Exception {
		  return searchModel(conn, o,getTrans);
		}
	   
	 public static List<References> getProductNameList() throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			List<References> statusList= new ArrayList<References>();
			Connection conn = null;
			try {
				sql.append("\n select distinct product_tname FROM pensbme_icc_head order by product_tname ");
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					statusList.add(new References(rst.getString("product_tname"), rst.getString("product_tname")));
					
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
			return statusList;
		}
	 
	   public static SaveInvoiceBean search(SaveInvoiceBean o ,boolean getTrans) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchModel(conn, o,getTrans);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	   
		public static SaveInvoiceBean searchModel(Connection conn,SaveInvoiceBean o ,boolean getTrans) throws Exception {
				PreparedStatement ps = null;
				ResultSet rs = null;
				StringBuilder sql = new StringBuilder();
				SaveInvoiceBean m = null;
				List<SaveInvoiceBean> items = new ArrayList<SaveInvoiceBean>();
				int r = 1;
				String billDate ="";
				double grandTotalQty = 0;
				double grandNetBVat = 0;
				try {
				   if( !Utils.isNull(o.getBILL_DATE()).equals("")){
					   billDate = o.getBILL_DATE().replaceAll("/", "");
				   }
				   sql.append("\n select h.* , m.pens_value as CUST_CODE, m.pens_desc2 as CUST_DESC" );
				   sql.append("\n ,d.total_qty,d.cost ,d.net_bvat ");
				   sql.append("\n from pensbme_icc_head h ,PENSBME_MST_REFERENCE m" );
				   sql.append("\n ,( select  d.bill_10 ,d.bus_code,d.dept_code,d.product_code,sum(d.total_qty) as total_qty ,sum(d.cost) as cost ");
				   sql.append("\n    ,sum(d.total_qty *d.cost) net_bvat from pensbme_icc_dlyr d"); 
				   sql.append("\n    group by d.bill_10 ,d.bus_code,d.dept_code,d.product_code" );
				   sql.append("\n  )d ");
				   sql.append("\n where 1=1 ");
				   sql.append("\n and d.bill_10= h.bill_10 ");
				   sql.append("\n and d.bus_code = h.bus_code" );
				   sql.append("\n and d.dept_code = h.dept_code" );
				   sql.append("\n and d.product_code = h.product_code ");
					  
				   sql.append("\n and m.reference_code = 'Store' and m.interface_value = h.CUST_ID ");
				   sql.append("\n and h.bill_date = '"+billDate+"'");
				   if( !Utils.isNull(o.getProductName()).equals(""))
				      sql.append("\n and h.product_tname ='"+o.getProductName()+"'");
				   sql.append("\n ");
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql.toString());
					rs = ps.executeQuery();
					
					while(rs.next()) {
					   m = new SaveInvoiceBean();
					   m.setCustCode(Utils.isNull(rs.getString("CUST_CODE"))); 
					   m.setCustDesc(Utils.isNull(rs.getString("CUST_DESC"))); 
					   
					   m.setORACLE_INVOICE_NO(Utils.isNull(rs.getString("ORACLE_INVOICE_NO")));     
					   m.setACTIVITY_CODE(Utils.isNull(rs.getString("ACTIVITY_CODE")));      
					   m.setBILL_10(Utils.isNull(rs.getString("BILL_10")));      
					   m.setCUST_ID(Utils.isNull(rs.getString("CUST_ID")));      
					   m.setSHIP_NO(Utils.isNull(rs.getString("SHIP_NO")));      
					   m.setBUS_CODE(Utils.isNull(rs.getString("BUS_CODE")));      
					   m.setDEPT_CODE(Utils.isNull(rs.getString("DEPT_CODE")));      
					   m.setPRODUCT_CODE(Utils.isNull(rs.getString("PRODUCT_CODE")));      
					   m.setSPD_CODE(Utils.isNull(rs.getString("SPD_CODE")));      
					   m.setPRODUCT_TNAME(Utils.isNull(rs.getString("PRODUCT_TNAME")));      
					   m.setBILL_DATE(Utils.isNull(rs.getString("BILL_DATE")));      
					   m.setSHIP_TO_ADDRESS(Utils.isNull(rs.getString("SHIP_TO_ADDRESS")));      
					   m.setSHIP_NAME(Utils.isNull(rs.getString("SHIP_NAME")));      
					   m.setTSC_ID(Utils.isNull(rs.getString("TSC_ID")));      
					   m.setTSC_NAME(Utils.isNull(rs.getString("TSC_NAME")));      
					   m.setSITE_ID(Utils.isNull(rs.getString("SITE_ID")));      
					   m.setNET_AMOUNT(Utils.isNull(rs.getString("NET_AMOUNT")));      
					   m.setTDH_GEN_NO(Utils.isNull(rs.getString("TDH_GEN_NO")));      
					   m.setPRODUCT_BARCODE(Utils.isNull(rs.getString("PRODUCT_BARCODE")));      
					   m.setBKK_UPC_FLAG(Utils.isNull(rs.getString("BKK_UPC_FLAG")));      
					   m.setBILL_NO(Utils.isNull(rs.getString("BILL_NO")));      
					   m.setVIA_TO_ADDRESS(Utils.isNull(rs.getString("VIA_TO_ADDRESS")));      
					   m.setVIA_NAME(Utils.isNull(rs.getString("VIA_NAME")));      
					   m.setCORNER_ID(Utils.isNull(rs.getString("CORNER_ID")));      
					   m.setCORNER_NAME(Utils.isNull(rs.getString("CORNER_NAME")));      
					   m.setDIS_SALE_PASS(Utils.isNull(rs.getString("DIS_SALE_PASS")));      
					   m.setDISC1_PERCENT(Utils.isNull(rs.getString("DISC1_PERCENT")));      
					   m.setDISC2_PERCENT(Utils.isNull(rs.getString("DISC2_PERCENT")));      
					   m.setDISC_BAHT(Utils.isNull(rs.getString("DISC_BAHT")));      
					   m.setZIPCODE(Utils.isNull(rs.getString("ZIPCODE")));      
					   m.setSHIP_PHONE(Utils.isNull(rs.getString("SHIP_PHONE")));      
					   m.setVIA_PHONE(Utils.isNull(rs.getString("VIA_PHONE")));      
					   m.setCASH_FLAG(Utils.isNull(rs.getString("CASH_FLAG")));      
					   m.setDELIVERY_DATE(Utils.isNull(rs.getString("DELIVERY_DATE")));      
					   m.setPO_NO(Utils.isNull(rs.getString("PO_NO")));      
					   m.setFROM_SYSTEM(Utils.isNull(rs.getString("FROM_SYSTEM")));      
					   m.setGROUP_NO_BILL_REPLACE(Utils.isNull(rs.getString("GROUP_NO_BILL_REPLACE")));      
					   m.setSORTER_ROUND(Utils.isNull(rs.getString("SORTER_ROUND")));      
					   m.setSORTER_BATCH(Utils.isNull(rs.getString("SORTER_BATCH")));      
					   m.setSORTER_CHUTE(Utils.isNull(rs.getString("SORTER_CHUTE")));      
					   
					   m.setTotalQty(Utils.decimalFormat(rs.getDouble("total_qty"), Utils.format_current_no_disgit));
					   m.setCost(Utils.decimalFormat(rs.getDouble("cost"), Utils.format_current_2_disgit));
					   m.setNetBVat(Utils.decimalFormat(rs.getDouble("net_bvat"), Utils.format_current_2_disgit));
					   
					   grandTotalQty +=rs.getDouble("total_qty");
					   grandNetBVat += rs.getDouble("net_bvat");
						
					   items.add(m);
					   r++;
					}//while
					
					//set Result 
					o.setGrandTotalQty(Utils.decimalFormat(grandTotalQty, Utils.format_current_no_disgit));
					o.setGrandNetBVat(Utils.decimalFormat(grandNetBVat, Utils.format_current_2_disgit));
					o.setItemList(items);
					 
				} catch (Exception e) {
					throw e;
				} finally {
					try {
						rs.close();
						ps.close();
					} catch (Exception e) {}
				}
			return o;
		}
		
		public static void updateORACLE_INVOICE_NO_ON_ICCHead(Connection conn,User user,String ORACLE_INVOICE_NO,String BILL_10,String BILL_DATE,String busCode ,String deptCode,String productCode) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateORACLE_INVOICE_NO_ON_ICCHead");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_ICC_HEAD SET  \n");
				sql.append(" ORACLE_INVOICE_NO =?  ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE BILL_10 =? and BILL_DATE = ? and BUS_CODE =? and DEPT_CODE =? and PRODUCT_CODE = ? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, ORACLE_INVOICE_NO);
				ps.setString(c++, user.getUserName());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, BILL_10);
				ps.setString(c++, BILL_DATE);
				ps.setString(c++, busCode);
				ps.setString(c++, deptCode);
				ps.setString(c++, productCode);
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	
}
