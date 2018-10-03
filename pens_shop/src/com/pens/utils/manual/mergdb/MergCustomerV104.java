package com.pens.utils.manual.mergdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MergCustomerV104 {

	private static Logger logger = Logger.getLogger("PENS");
	static String userId = "100021083";// ID V104 พิทัก
	static String souceSchema = "pens_v105";
	static String destSchema = "pens";
	static String provinceId = "103,118"; //กาญจนบุรี 103 //นครปฐม 118
	
	static Connection connSource = null;
	static Connection connDest = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		startMerg();
	}
	
	public  static Connection getConnectionDest(){		
		try {	
			if(connDest != null){
				return connDest;
			}
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/pens?useUnicode=true&amp;characterEncoding=UTF-8";
			String username = "pens";
			String password = "pens";
			
			logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			 connDest = DriverManager.getConnection(url,username,password);	
			logger.debug("Connection:"+connDest);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return connDest;	
	}
	
	public  static Connection getConnectionSource(){		
		try {	
			if(connSource != null){
				return connSource;
			}
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/pens_v105?useUnicode=true&amp;characterEncoding=UTF-8";
			String username = "pens";
			String password = "pens";
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			 connSource = DriverManager.getConnection(url,username,password);	
			//logger.debug("Connection:"+connSource);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return connSource;	
	}
	
	public static void startMerg(){

		try{	
			logger.debug("----Start----");
			
			connSource = getConnectionSource();
			connDest = getConnectionDest();
			
			// Find Customer in DB V307 
			List custList = findCustomerOld(connSource);
			
			if(custList != null && custList.size() > 0){
				for(int i=0;i<custList.size();i++){
					CustMergBean b =(CustMergBean)custList.get(i);
					//Insert new customer 
					insertMCustomer(connDest,b.getOldCustId(),b.getNewCustId());
					
					List addressList = findAddressOld(connSource,b.getOldCustId());
					if(addressList != null && addressList.size() >0 ){
						for(int a=0;a<addressList.size();a++){
							CustMergBean aa =(CustMergBean)addressList.get(a);
					        insertMAddress(connDest,b.getNewCustId(),aa.getOldAddressId(),aa.getNewAddressId());
						}
					}
					
					List contactList = findContactOld(connSource,b.getOldCustId());
					if(contactList != null && contactList.size() >0 ){
						for(int a=0;a<contactList.size();a++){
							CustMergBean aa =(CustMergBean)contactList.get(a);
					        insertMContact(connDest,b.getNewCustId(),aa.getOldContactId(),aa.getNewContactId());
						}
					}
				}
			}
			
			logger.debug("----Success----");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(connSource != null){
					connSource.close();connSource=null;
				}
				if(connDest != null){
					connDest.close();connDest=null;
				}
			}catch(Exception e){}
		}
	}
	
	//Kan 103 ,nakonpatom 118
	public static List<CustMergBean> findCustomerOld(Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<CustMergBean> custList = new ArrayList<CustMergBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select  *  from "+souceSchema+".m_customer  c ,"+souceSchema+".m_address  a where 1=1 \n");
			sql.append(" and c.customer_id = a.customer_id \n");
			sql.append(" and a.purpose ='S' \n");
			sql.append(" and a.province_id in ("+provinceId+") \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				CustMergBean m = new CustMergBean();
				m.setOldCustId(rs.getString("customer_id"));
				m.setNewCustId(String.valueOf(getNextValue("m_customer")));
				
				custList.add(m);
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return custList;
	}

	public static List<CustMergBean> findAddressOld(Connection conn,String oldCustomerId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<CustMergBean> custList = new ArrayList<CustMergBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select  *  from "+souceSchema+".m_address where customer_id ="+oldCustomerId+" \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				CustMergBean m = new CustMergBean();
				m.setOldAddressId(rs.getString("address_id"));
				m.setNewAddressId(String.valueOf(getNextValue("m_address")));
				
				custList.add(m);
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return custList;
	}
	
	public static List<CustMergBean> findContactOld(Connection conn,String oldCustomerId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<CustMergBean> custList = new ArrayList<CustMergBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select  *  from "+souceSchema+".m_contact where customer_id ="+oldCustomerId+" \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				CustMergBean m = new CustMergBean();
				m.setOldContactId(rs.getString("contact_id"));
				m.setNewContactId(String.valueOf(getNextValue("m_contact")));
				
				custList.add(m);
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return custList;
	}
	
	public static void insertMCustomer(Connection conn ,String oldCustomerId,String newCustomerId) throws Exception {
		PreparedStatement ps = null;
		try {
			logger.debug("Insert M_CUSTOMER");
			
			StringBuffer sql = new StringBuffer("");
			sql.append("\n INSERT INTO "+destSchema+".m_customer ");
			sql.append("\n   ( SELECT  ");
			sql.append("\n    "+newCustomerId+" As CUSTOMER_ID, REFERENCE_ID, CODE,  ");
			sql.append("\n    NAME, NAME2, CUSTOMER_TYPE, TAX_NO,  ");
			sql.append("\n    TERRITORY, WEBSITE, BUSINESS_TYPE,  ");
			sql.append("\n    ISACTIVE, "+userId+" as  USER_ID, PERSON_ID_NO,  ");
			sql.append("\n    EMAIL, CREDIT_CHECK, REGISTER_DATE,  ");
			sql.append("\n    FIRST_DELIVERLY_DATE, PAYMENT_TERM, VAT_CODE,  ");
			sql.append("\n    CREDIT_LIMIT, BIRTHDAY, OCCUPATION,  ");
			sql.append("\n    SHIPPING_METHOD, ORDER_DATE, SHIPPING_DATE,  ");
			sql.append("\n    SHIPPING_ROUTE, MONHTLY_INCOME, CHOLESTEROL,  ");
			sql.append("\n    MEMBER_LEVEL, RECOMMENDED_BY, CANCEL_REASON,  ");
			sql.append("\n    MEMBER_TYPE, 2125 as PRODUCT_CATEGORY_ID, CREATED,  ");
			sql.append("\n    "+userId+" as CREATED_BY, UPDATED, "+userId+" as UPDATED_BY,  ");
			sql.append("\n    ORDER_AMOUNT_PERIOD, PAYMENT_METHOD, PARENT_CUSTOMER_ID,  ");
			sql.append("\n    TRANSIT_NAME, RECOMMENDED_TYPE, RECOMMENDED_ID,  ");
			sql.append("\n    SHIPPING_TIME, ROUND_TRIP, EXPIRED_DATE,  ");
			sql.append("\n    AGE_MONTH, INTERFACES, PARTY_TYPE,  ");
			sql.append("\n    EXPORTED, DELIVERY_GROUP, isvip,  ");
			sql.append("\n    SHIPPING_TIME_TO, CREDITCARD_EXPIRED ");
			sql.append("\n    FROM "+souceSchema+".m_customer where customer_id ="+oldCustomerId);
	        sql.append("\n ) ");
 
			ps = conn.prepareStatement(sql.toString());
			int ch =ps.executeUpdate();
			
			logger.debug("sql:"+newCustomerId+"["+ch+"]");
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	public static void insertMAddress(Connection conn ,String newCustomerId,String oldAddressId,String newAddressId) throws Exception {
		PreparedStatement ps = null;
		try {
			logger.debug("Insert M_ADDRESS");
			
			StringBuffer sql = new StringBuffer("");
			sql.append("\n INSERT INTO "+destSchema+".m_address ");
			sql.append("\n (SELECT "+newAddressId+" as ADDRESS_ID, ISACTIVE, LINE1,  ");
			sql.append("\n  LINE2, LINE3, LINE4,  ");
			sql.append("\n  POSTAL_CODE, COUNTRY, PROVINCE_ID, "); 
			sql.append("\n  PURPOSE, DISTRICT_ID, "+newCustomerId+" as CUSTOMER_ID,  ");
			sql.append("\n  CREATED, "+userId+" AS CREATED_BY, UPDATED,  ");
			sql.append("\n  "+userId+" AS UPDATED_BY, PROVINCE_NAME, null as REFERENCE_ID FROM "+souceSchema+".m_address where address_id = "+oldAddressId+") ");

			ps = conn.prepareStatement(sql.toString());
			int ch = ps.executeUpdate();
			
			logger.debug("sql:"+newAddressId+"["+ch+"]");
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	public static void insertMContact(Connection conn ,String newCustomerId,String oldContactId,String newContactId) throws Exception {
		PreparedStatement ps = null;
		try {
			logger.debug("Insert M_CONTACT");
			
			StringBuffer sql = new StringBuffer("");
			
			sql.append("\n INSERT INTO "+destSchema+".m_contact ");
			sql.append("\n   (SELECT "+newContactId+" as CONTACT_ID, "+newCustomerId+" as CUSTOMER_ID, CONTACT_TO,  ");
			sql.append("\n    PHONE, FAX, ISACTIVE,  ");
			sql.append("\n    RELATION, CREATED, "+userId+" as CREATED_BY,  ");
			sql.append("\n    UPDATED, "+userId+" as UPDATED_BY, PHONE2,  ");
			sql.append("\n    MOBILE, MOBILE2, null as REFERENCE_ID,  ");
			sql.append("\n    phone_sub1, phone_sub2  from "+souceSchema+".m_contact where contact_id ="+oldContactId+") ");

			ps = conn.prepareStatement(sql.toString());
			int ch = ps.executeUpdate();
			
			logger.debug("sql:"+newContactId+"["+ch+"]");
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	public static Integer getNextValue(String tableName) throws Exception {
		Integer nextValue = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			conn = getConnectionDest();
			StringBuilder sql = new StringBuilder();
			// Get Last Value
			sql.delete(0, sql.length());
			sql.append("SELECT NextValue FROM c_sequence ");
			sql.append("WHERE NAME = '" + tableName + "' ");
			stmt = conn.createStatement();
			//logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getInt("nextValue");
			} else {
				sql.delete(0, sql.length());
				sql.append("INSERT INTO c_sequence(NAME,STARTNO,NEXTVALUE) ");
				sql.append("VALUES('" + tableName + "',1,1)");
				//logger.debug(sql.toString());
				stmt.execute(sql.toString());
				nextValue = 1;
			}
			// Update Value
			sql.delete(0, sql.length());
			sql.append("UPDATE c_sequence SET NextValue = NextValue + 1 ");
			sql.append("WHERE NAME = '" + tableName + "' ");
			//logger.debug(sql.toString());
			stmt.execute(sql.toString());
		
		} catch (Exception e) {
			logger.debug(e.toString());
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			
		}
		return nextValue;
	}
}
