package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.CustomerBillInfo;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.SequenceHelper;
import com.isecinc.pens.inf.helper.Utils;

public class CustomerBillInfoDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 /*public static CustomerBillInfo searchCustomerBillInfo(Connection conn,int customerBillId) throws Exception {
		 return searchCustomerBillInfoModel(conn, customerBillId);
	 }*/
	 /*public static CustomerBillInfo searchCustomerBillInfo(int customerBillId) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return searchCustomerBillInfoModel(conn, customerBillId);
		 }catch(Exception e){
			 throw e;
		 }finally{
			 if(conn != null){
				 conn.close();
			 }
		 }
	 }
	 public static CustomerBillInfo searchCustomerBillInfoModel(Connection conn,int customerBillId) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			CustomerBillInfo bean = null;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT M.* from m_customer_bill_info M");
				sql.append("\n  where customer_bill_id ="+customerBillId+"");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					bean = new CustomerBillInfo();
					bean.setCustomerBillId(rst.getInt("customer_bill_id"));
					bean.setCustomerName(Utils.isNull(rst.getString("customer_name")));
					bean.setAddressDesc(Utils.isNull(rst.getString("address_desc")));
					bean.setIdNo(Utils.isNull(rst.getString("id_no")));
					bean.setPassportNo(Utils.isNull(rst.getString("passport_no")));
					
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return bean;
		}*/
	 
	
	 
	 //Insert only no update
	 /*public static int saveCustomerBillInfo(Connection conn,CustomerBillInfo bean) throws Exception {
		 int customerBillId=0;
		 if(bean.getCustomerBillId() != 0){
			 if( !isCustomerBillIdExist(conn, bean.getCustomerBillId())){
				 customerBillId= insertCustomerBillInfo(conn, bean);
			 }
		 }else{
			 customerBillId= insertCustomerBillInfo(conn, bean);
		 }
		 return customerBillId;
	 }
	 
	 public static boolean isCustomerBillIdExist(Connection conn,int customerBillId) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			boolean exist = false;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT count(*) as c from m_customer_bill_info ");
				sql.append("\n  where customer_bill_id ="+customerBillId);
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					if(rst.getInt("c") >0){
						exist = true;
					}
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return exist;
		}
	 
	 public static int insertCustomerBillInfo(Connection conn,CustomerBillInfo bean) throws Exception {
			PreparedStatement ps = null;
			StringBuilder sql = new StringBuilder();
			int customerBillId = 0;
			int index =0;
			logger.debug("insertCustomerBillInfo");
			try {
				sql.append("\n insert into m_customer_bill_info");
				sql.append("\n (customer_bill_id,Customer_name ,address_desc,id_no,passport_no,created,created_by)");
				sql.append("\n values(?,?,?,?,?,?,?) \n");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				
				customerBillId = SequenceHelper.getNextValue("m_customer_bill_info");
				ps.setInt(++index, customerBillId);
				ps.setString(++index, bean.getCustomerName());
				ps.setString(++index, bean.getAddressDesc());
				ps.setString(++index, bean.getIdNo());
				ps.setString(++index, bean.getPassportNo());
				ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
				ps.setString(++index, bean.getCreatedBy());
				
				 ps.executeUpdate();
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					ps.close();
				} catch (Exception e) {}
			}
			return customerBillId;
		}
	*/
}
