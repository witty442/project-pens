package com.isecinc.pens.model;

import static com.pens.util.ConvertNullUtil.convertToString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.util.Log;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.document.CustomerDocumentProcess;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateToolsUtil;
import com.pens.util.seq.SequenceProcessAll;

/**
 * MCustomer Class
 * 
 * @author Aneak.t
 * @version $Id: MCustomer.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 *          atiz.b : edit for new customer sequence
 * 
 */

public class MCustomer extends I_Model<Customer> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "pensso.m_customer";
	public static String COLUMN_ID = "Customer_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "CODE", "NAME", "NAME2", "CUSTOMER_TYPE", "TAX_NO", "TERRITORY", "WEBSITE",
			"BUSINESS_TYPE", "BIRTHDAY", "CREDIT_CHECK", "PAYMENT_TERM", "VAT_CODE", "PAYMENT_METHOD",
			"SHIPPING_METHOD", "USER_ID", "ISACTIVE", "CREATED_BY", "UPDATED_BY", "PARENT_CUSTOMER_ID", "PARTY_TYPE",
			"PRINT_TAX","PRINT_TYPE","PRINT_BRANCH_DESC","PRINT_HEAD_BRANCH_DESC","AIRPAY_FLAG","LOCATION","IMAGE_FILE_NAME",
			"IS_CHANGE","TRIP_DAY","TRIP_DAY2","TRIP_DAY3"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Customer find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Customer.class);
	}
	public Customer find(Connection conn,String id) throws Exception {
		return super.find(conn,id, TABLE_NAME, COLUMN_ID, Customer.class);
	}
	public Customer findByWhereCond(String whereSql) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return findByWhereCond(conn, whereSql);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public Customer findByWhereCond(Connection conn ,String whereSql) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Customer p = null;
		try{
			String sql ="\n select * from pensso.m_customer "+whereSql ;
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				p = new Customer(rst);
			}
			return p;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
	}
	public String getCustGroup(Connection conn,long customerId) throws Exception {
		return getCustGroupModel(conn, customerId);
	}
	public String getCustGroup(long customerId) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getCustGroupModel(conn, customerId);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();conn=null;
		}
	}
	public String getCustGroupModel(Connection conn,long customerId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String custGroup = "";
		try{
			String sql ="\n select cust_group from m_customer where customer_id="+customerId ;
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				custGroup = Utils.isNull(rst.getString("cust_group"));
			}
			return custGroup;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();rst=null;
				stmt.close();stmt=null;
			} catch (Exception e2) {}
		}
	}
	
	public String getProvinceGroupModel(Connection conn,long customerId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String provinceGroup = "";
		try{
			String sql ="\n select (select group_name from pensso.m_province_group g where g.group_id=p.group_id) as province_group"
					+"\n from pensso.m_customer m,pensso.m_address a ,pensso.m_province p"
					+"\n where a.province_id = p.province_id"
					+"\n and m.customer_id = a.customer_id"
					+"\n and a.purpose = 'S'"
					+"\n and m.customer_id="+customerId ;
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				provinceGroup = Utils.isNull(rst.getString("province_group"));
			}
			return provinceGroup;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();rst=null;
				stmt.close();stmt=null;
			} catch (Exception e2) {}
		}
	}
	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public Customer[] search(String whereCause) throws Exception {
		List<Customer> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Customer.class);
		if (pos.size() == 0) return null;
		Customer[] array = new Customer[pos.size()];
		array = pos.toArray(array);
		return array;
	}
	
	public boolean isCustHaveProductSpecial(Connection conn,String customerId) throws Exception {
        boolean isCustHaveProductSpecial = false;
		Statement stmt = null;
		ResultSet rst = null;
		try{
			String sql ="\n select count(*) as c from pensso.m_customer "+
			            "\n where customer_id = "+customerId+ " and code in(select code from pensso.m_customer_center)" ;
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				if( rst.getInt("c") >0){
					isCustHaveProductSpecial = true;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return isCustHaveProductSpecial;
	}
	
	public int getTotalRowCustomer(Connection conn,String whereCause,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		int totalRow = 0;
		try{
			String sql ="\n select count(*) total_row  from("+
			            "\n   select distinct c.* "+
			            "\n   from pensso.m_customer c,pensso.m_address a "+
			            "\n   where 1=1 and c.customer_id = a.customer_id " +
					    "\n   and a.purpose ='B' "+whereCause+
					    "\n  )A";
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				totalRow = rst.getInt("total_row");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return totalRow;
	}
	
	public List<Customer> getTripPage(Connection conn,String whereCause,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Map<Integer,Customer> tripMap = new HashMap<Integer,Customer>();
		List<Customer> list = null;
		Customer c = new Customer();
		try{
			String sql ="\n  select distinct trip_day,trip_day2,trip_day3 from pensso.m_customer c ,pensso.m_address a "
					   +"\n  where 1=1 and c.customer_id = a.customer_id " +
			            "\n  and a.purpose ='B' "+
					    "\n  "+whereCause+
			            "\n  order by trip_day,trip_day2,trip_day3 asc" ;
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				if(rst.getInt("trip_day") != 0){
				   c = new Customer();
				   c.setTripDay(rst.getString("trip_day"));
				   tripMap.put(rst.getInt("trip_day"), c);
				}
				if(rst.getInt("trip_day2") != 0){
				    c = new Customer();
				    c.setTripDay(rst.getString("trip_day2"));
				   tripMap.put(rst.getInt("trip_day2"), c);
				}
				if(rst.getInt("trip_day3") != 0){
					c = new Customer();
					c.setTripDay(rst.getString("trip_day3"));
				    tripMap.put(rst.getInt("trip_day3"), c);
				}
			}
			
			//Sort
			list = new ArrayList<Customer>(tripMap.values());
			Collections.sort(list, Customer.Comparators.TRIP_ASC);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return list;
	}
	
	public Customer getImageFileName(String customerId) throws Exception {

		Statement stmt = null;
		ResultSet rst = null;
		Customer c = null;
		Connection conn = null;
		try{
			String sql ="\n select name,name2, image_file_name from m_customer where customer_id ="+customerId;
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				c = new Customer();
				c.setName(Utils.isNull(rst.getString("name")));
				c.setName2(Utils.isNull(rst.getString("name2")));
				c.setImageFileName(Utils.isNull(rst.getString("image_file_name")));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
				conn.close();
			} catch (Exception e2) {}
		}
		
		return c;
	}
	
	public List<Customer> getCustomerLocation() throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Customer c = null;
		Connection conn = null;
		List<Customer> customerList = new ArrayList<Customer>();
		try{
			String sql ="\n select name,name2, location from m_customer where location is not null and location <> ''";
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				c = new Customer();
				c.setName(Utils.isNull(rst.getString("name")));
				c.setName2(Utils.isNull(rst.getString("name2")));
				c.setLocation(Utils.isNull(rst.getString("location")));
				customerList.add(c);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
				conn.close();
			} catch (Exception e2) {}
		}
		
		return customerList;
	}
	
	public Customer[] searchOpt(Connection conn,String whereCause,User user,int start,String dispTotalInvoice) throws Exception {
		return searchOptModel(conn,whereCause,user,start,dispTotalInvoice);
	}
	
    public Customer[] searchOpt(String whereCause,User user,int start,String dispTotalInvoice) throws Exception {
	   Connection conn = null;
	   try{
		   conn = new DBCPConnectionProvider().getConnection(conn);
		   return searchOptModel(conn,whereCause,user,start,dispTotalInvoice);
	   }catch(Exception e){
		   throw e;
	   }finally{
			conn.close();
	   }
	}
    
    public Customer[] searchOptByTrip(Connection conn,String whereCause,User user,String tripBySearchSqlIn,String dispTotalInvoice) throws Exception {
		return searchOptByTripModel(conn,whereCause,user,tripBySearchSqlIn,dispTotalInvoice);
	}
	
    public Customer[] searchOptByTrip(String whereCause,User user,String  tripBySearchSqlIn,String dispTotalInvoice) throws Exception {
	   Connection conn = null;
	   try{
		   conn = new DBCPConnectionProvider().getConnection(conn);
		   return searchOptByTripModel(conn,whereCause,user,tripBySearchSqlIn,dispTotalInvoice);
	   }catch(Exception e){
		   throw e;
	   }finally{
			conn.close();
	   }
	}
	
	/**
	 * 
	 * @param whereCause
	 * @return
	 * @throws Exception
	 * Tunnig Method By Wit
	 */
	private Customer[] searchOptModel(Connection conn,String whereCause,User user,int currPage,String dispTotalInvoice) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<Customer> custList = new ArrayList<Customer>();
		Customer[] array = null;
		String creditDateFix = "";
		String sql = "";
		Customer m = null;
		try {
			//Filter display Column
			String displayActionReceipt ="";
			String displayActionEditCust  ="";
			String role = user.getType();
			if( !role.equalsIgnoreCase(User.TT)){
				displayActionReceipt ="none";
			}
			if( role.equalsIgnoreCase(User.TT)){
				displayActionEditCust ="none";
				
			}
			if( !Utils.isNull(dispTotalInvoice).equals("")){
			   //Get CreditDateFix FROM C_REFERENCE
			   References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.CREDIT_DATE_FIX,InitialReferences.CREDIT_DATE_FIX);
			   creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
			   logger.debug("creditDateFix:"+creditDateFix);
			}
			   sql +="\n select M.* from (";
			   sql +="\n select A.* ,rownum as r__ from (";
			      sql += "select distinct c.*  \n";
			       sql+=" ,a.line1,a.line2,a.line3,a.line4 \n";
				   sql+=" ,(select d.name from pensso.m_district d where d.district_id = a.district_id) as district_name \n";
				   sql+=" ,a.province_name,a.postal_code, \n";
				   
                   sql+= " ( select count(*) as tot from pensso.t_order od where od.customer_id = c.customer_id)  as order_amount \n";
                   sql+= " from pensso.m_customer c, \n" ;
                   sql +="( \n";
                   sql+="   select distinct customer_id,district_id,line1,line2,line3,line4 ,province_name,postal_code \n";;
                   sql +="  from pensso.m_address where purpose ='B'\n";
                   sql +=")a \n";
                   sql+= " where 1=1 \n";
                   sql+= " and c.customer_id = a.customer_id \n";
			       sql+= whereCause;
			       sql+= "\n    order by c.code asc ";
			       sql+= "\n   )A ";
			       sql+= "\n    WHERE rownum < (("+currPage+" * "+50+") + 1 )  ";
			       sql+= "\n )M  ";
			       sql+= "\n  WHERE r__ >= ((("+currPage+"-1) * "+50+") + 1)  ";
		    
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			int start = Utils.calcStartNoInPage(currPage, 50);
			while(rst.next()){
				m = new Customer();
				m.setNo(start);
				// Mandatory
				m.setId(rst.getLong("CUSTOMER_ID"));
				m.setReferencesID(rst.getInt("REFERENCE_ID"));
				m.setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
				m.setCode(rst.getString("CODE").trim());
				m.setName(rst.getString("NAME").trim());
				m.setName2(ConvertNullUtil.convertToString(rst.getString("NAME2")).trim());
				m.setTaxNo(ConvertNullUtil.convertToString(rst.getString("TAX_NO")).trim());
				m.setWebsite(ConvertNullUtil.convertToString(rst.getString("WEBSITE")).trim());
				m.setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
				m.setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
				// System.out.println(rst.getString("PARENT_CUSTOMER_ID"));
				if (rst.getInt("PARENT_CUSTOMER_ID") != 0 && rst.getString("PARENT_CUSTOMER_ID") != null) {
					Customer c = new MCustomer().find(rst.getString("PARENT_CUSTOMER_ID"));
					m.setParentID(c.getId());
					m.setParentCode(c.getCode());
					m.setParentName((c.getName() + " " + c.getName2()).trim());
				}
				m.setBirthDay("");
				if (rst.getTimestamp("BIRTHDAY") != null) {
					m.setBirthDay(DateToolsUtil.convertToString(rst.getTimestamp("BIRTHDAY")));
				}
				m.setCreditCheck(ConvertNullUtil.convertToString(rst.getString("CREDIT_CHECK")).trim());
				m.setPaymentTerm(ConvertNullUtil.convertToString(rst.getString("PAYMENT_TERM")).trim());
				m.setVatCode(ConvertNullUtil.convertToString(rst.getString("VAT_CODE")).trim());
				m.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				m.setShippingMethod(ConvertNullUtil.convertToString(rst.getString("SHIPPING_METHOD")).trim());
				m.setShippingRoute(ConvertNullUtil.convertToString(rst.getString("SHIPPING_ROUTE")).trim());
				m.setTransitName(ConvertNullUtil.convertToString(rst.getString("TRANSIT_NAME")).trim());
				
				/** **/
				//logger.debug("user_id:"+Utils.isNull(rst.getString("user_id")));
				User u = new MUser().find(Utils.isNull(rst.getString("user_id")));
				//logger.debug("User:"+u);
				if(u != null){
				  u.activeRoleInfo();
				}
				m.setSalesRepresent(u);
				
				m.setCreditLimit(rst.getDouble("CREDIT_LIMIT"));
				m.setIsActive(rst.getString("ISACTIVE").trim());
				m.setInterfaces(rst.getString("INTERFACES").trim());
				m.setPartyType(ConvertNullUtil.convertToString(rst.getString("PARTY_TYPE")).trim());
				m.setExported(rst.getString("EXPORTED"));

				String addressSummary  = Utils.isNull(rst.getString("line1"))+" "+Utils.isNull(rst.getString("line2"))+" "+Utils.isNull(rst.getString("line3"));
				       addressSummary += " "+Utils.isNull(rst.getString("line4"))+" "+Utils.isNull(rst.getString("province_name"))+" "+Utils.isNull(rst.getString("postal_code"));
				m.setAddressSummary(addressSummary);
				
				// Total Invoice
				//double totalOrderAmt = rst.getDouble("total_order_amt");
				//double totalReceiptAmt = rst.getDouble("total_receipt_amt");
				//m.setTotalInvoice(totalOrderAmt-totalReceiptAmt);
				
				//m.setTotalInvoice(new MReceiptLine().lookCreditAmtBK(conn,m.getId()));
				if( !Utils.isNull(dispTotalInvoice).equals("")){
					logger.info("calcTotalInvoice");
				    m.setTotalInvoice(MReceiptSummary.lookCreditAmtByCustomerId(conn,u.getId(),m.getId()));
				}
				
				// Order Amount
				m.setOrderAmount(rst.getInt("order_amount"));

				// set display label
				m.setDisplayLabel();
				
				//Show or Display Column
				
				//Disp Column Edit Customer
				m.setDisplayActionEditCust(displayActionEditCust);//disp
				if( !Utils.isNull(Utils.isNull(rst.getString("image_file_name"))).equals("")){
				    m.setImageFileName(Utils.isNull(rst.getString("image_file_name")));
				}else{
					m.setImageFileName("");
				}
				
				//Can Edit Dust
				if(role.equalsIgnoreCase(User.ADMIN)){
					m.setCanActionEditCust(true);
				}else if(role.equalsIgnoreCase(User.VAN)){
					if (m.getOrderAmount()== 0){
					   if (!m.getExported().equalsIgnoreCase("Y")){
						  m.setCanActionEditCust(true);
						  m.setCanActionEditCust2(false);
					   }else{
						 // m.setDisplayActionEditCust("");
						   if( role.equalsIgnoreCase(User.VAN)){
						     m.setCanActionEditCust2(true);
						   }
					   }
					}else{
						if( role.equalsIgnoreCase(User.VAN)){
						  m.setCanActionEditCust2(true);
						}
					}
				}else if(role.equalsIgnoreCase(User.TT)){
					   m.setCanActionEditCust2(true);
				}
				
				//logger.debug("setDisplayActionEditCust:"+m.getDisplayActionEditCust());
				
				//displayActionReceipt
				m.setDisplayActionReceipt(displayActionReceipt);
				
				custList.add(m);
				
				start++;
			}
			
			//convert to Obj
			if(custList != null && custList.size() >0){
				array = new Customer[custList.size()];
				array = custList.toArray(array);
			}else{
				array = null;
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			
		}
		
		return array;
	}
	
private Customer[] searchOptByTripModel(Connection conn,String whereCause,User user,String tripBySearchSqlIn,String dispTotalInvoice) throws Exception {
		
		Statement stmt = null;
		ResultSet rst = null;
		List<Customer> custList = new ArrayList<Customer>();
		Customer[] array = null;
		int no = 0;
		String creditDateFix = "";
		try {
			//Filter display Column
			String displayActionReceipt ="";
			String displayActionEditCust  ="";
			String role = user.getType();
			if( !role.equalsIgnoreCase(User.TT)){
				displayActionReceipt ="none";
			}
			if( role.equalsIgnoreCase(User.TT)){
				displayActionEditCust ="none";
			}
			if( !Utils.isNull(dispTotalInvoice).equals("")){
				 //Get CreditDateFix FROM C_REFERENCE
				 References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.CREDIT_DATE_FIX,InitialReferences.CREDIT_DATE_FIX);
				 creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
				 logger.debug("creditDateFix:"+creditDateFix);
			}
			
			String sql = "select distinct c.*  \n";
			       sql+=" ,a.line1,a.line2,a.line3,a.line4 \n";
			       
				   sql+=" ,(select d.name from pensso.m_district d where d.district_id = a.district_id) as district_name \n";
				   sql+=" ,a.province_name,a.postal_code, \n";
			       sql+=" u.CATEGORY,u.ORGANIZATION,u.START_DATE,u.END_DATE, \n";
                   sql+=" u.NAME,u.SOURCE_NAME,u.ID_CARD_NO,u.USER_NAME,u.PASSWORD, \n";
                   sql+=" u.ROLE,u.ISACTIVE,u.CODE,u.UPDATED,u.UPDATED_BY,u.TERRITORY, \n";
                   sql+= " ( select count(*) as tot from pensso.t_order od where od.customer_id = c.customer_id)  as order_amount \n";
                  // sql+= " ( select sum(o.net_amount) net_amount from t_order o \n where o.doc_status = 'SV'  and o.CUSTOMER_ID = m_customer.CUSTOMER_ID ) as total_order_amt,\n";
               
                  // sql+= " ( select sum(r.receipt_amount) receipt_amount from t_receipt r \n where r.doc_status = 'SV' and r.CUSTOMER_ID = m_customer.CUSTOMER_ID) as total_receipt_amt \n";
                   sql+=",PRINT_TYPE ,PRINT_BRANCH_DESC,PRINT_HEAD_BRANCH_DESC,PRINT_TAX \n";
                   
                   sql+= " from pensso.m_customer c ,pensso.ad_user u, \n" ;
                   sql +="( \n";
                   sql+="   select distinct customer_id,district_id,line1,line2,line3,line4 ,province_name,postal_code \n";
                   sql +="  from pensso.m_address where purpose ='B'\n";
                   sql +=")a \n";
                   sql+= " where c.user_id = u.user_id \n";
                   sql+= " and c.customer_id = a.customer_id \n";
			       sql+= whereCause;
			       sql+="\n and (trip_day in("+tripBySearchSqlIn+")  or trip_day2 in("+tripBySearchSqlIn+") or trip_day3 in("+tripBySearchSqlIn+" ))\n";
			       
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				Customer m = new Customer();
				no++;
				m.setNo(no);
				// Mandatory
				m.setId(rst.getLong("CUSTOMER_ID"));
				m.setReferencesID(rst.getInt("REFERENCE_ID"));
				m.setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
				m.setCode(rst.getString("CODE").trim());
				m.setName(rst.getString("NAME").trim());
				m.setName2(ConvertNullUtil.convertToString(rst.getString("NAME2")).trim());
				m.setTaxNo(ConvertNullUtil.convertToString(rst.getString("TAX_NO")).trim());
				m.setWebsite(ConvertNullUtil.convertToString(rst.getString("WEBSITE")).trim());
				m.setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
				m.setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
				// System.out.println(rst.getString("PARENT_CUSTOMER_ID"));
				if (rst.getInt("PARENT_CUSTOMER_ID") != 0 && rst.getString("PARENT_CUSTOMER_ID") != null) {
					Customer c = new MCustomer().find(rst.getString("PARENT_CUSTOMER_ID"));
					m.setParentID(c.getId());
					m.setParentCode(c.getCode());
					m.setParentName((c.getName() + " " + c.getName2()).trim());
				}
				m.setBirthDay("");
				if (rst.getTimestamp("BIRTHDAY") != null) {
					m.setBirthDay(DateToolsUtil.convertToString(rst.getTimestamp("BIRTHDAY")));
				}
				m.setCreditCheck(ConvertNullUtil.convertToString(rst.getString("CREDIT_CHECK")).trim());
				m.setPaymentTerm(ConvertNullUtil.convertToString(rst.getString("PAYMENT_TERM")).trim());
				m.setVatCode(ConvertNullUtil.convertToString(rst.getString("VAT_CODE")).trim());
				m.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				m.setShippingMethod(ConvertNullUtil.convertToString(rst.getString("SHIPPING_METHOD")).trim());
				m.setShippingRoute(ConvertNullUtil.convertToString(rst.getString("SHIPPING_ROUTE")).trim());
				m.setTransitName(ConvertNullUtil.convertToString(rst.getString("TRANSIT_NAME")).trim());
				
				/** **/
				User u = new User();
				u.setId(rst.getInt("USER_ID"));
				u.setCode(rst.getString("CODE").trim());
				u.setName(rst.getString("NAME").trim());
				u.setType(convertToString(rst.getString("ROLE")).trim());
				u.setActive(rst.getString("ISACTIVE").trim());

				// oracle fields
				u.setCategory(convertToString(rst.getString("CATEGORY")).trim());
				u.setOrganization(convertToString(rst.getString("ORGANIZATION")).trim());
				u.setSourceName(convertToString(rst.getString("SOURCE_NAME")).trim());
				u.setIdCardNo(convertToString(rst.getString("ID_CARD_NO")).trim());
				u.setStartDate(convertToString(rst.getString("START_DATE")).trim());
				u.setEndDate(convertToString(rst.getString("END_DATE")).trim());
				u.setTerritory(convertToString(rst.getString("TERRITORY")).trim());

				// sales online fields
				u.setUserName(convertToString(rst.getString("USER_NAME")).trim());
				u.setPassword(convertToString(rst.getString("PASSWORD")).trim());
				u.setConfirmPassword(convertToString(rst.getString("PASSWORD")).trim());
				
				u.activeRoleInfo();
				m.setSalesRepresent(u);
				/** **/
				
				m.setCreditLimit(rst.getDouble("CREDIT_LIMIT"));
				m.setIsActive(rst.getString("ISACTIVE").trim());
				m.setInterfaces(rst.getString("INTERFACES").trim());
				m.setPartyType(ConvertNullUtil.convertToString(rst.getString("PARTY_TYPE")).trim());
				m.setExported(rst.getString("EXPORTED"));

				String addressSummary  = Utils.isNull(rst.getString("line1"))+" "+Utils.isNull(rst.getString("line2"))+" "+Utils.isNull(rst.getString("line3"));
				       addressSummary += " "+Utils.isNull(rst.getString("line4"))+" "+Utils.isNull(rst.getString("province_name"))+" "+Utils.isNull(rst.getString("postal_code"));
				m.setAddressSummary(addressSummary);
				
				if( !Utils.isNull(dispTotalInvoice).equals("")){
					logger.debug("Calc Total Invoice");
				    m.setTotalInvoice(MReceiptSummary.lookCreditAmtByCustomerId(conn,u.getId(),m.getId()));
				}
		
				// Order Amount
				m.setOrderAmount(rst.getInt("order_amount"));

				// set display label
				m.setDisplayLabel();
				
				//Show or Display Column
				
				//Disp Column Edit Customer
				m.setDisplayActionEditCust(displayActionEditCust);//disp
				if( !Utils.isNull(Utils.isNull(rst.getString("image_file_name"))).equals("")){
				    m.setImageFileName(Utils.isNull(rst.getString("image_file_name")));
				}else{
					m.setImageFileName("");
				}
				
				//Can Edit Dust
				if(role.equalsIgnoreCase(User.ADMIN)){
					m.setCanActionEditCust(true);
				}else if(role.equalsIgnoreCase(User.VAN)){
					if (m.getOrderAmount()== 0){
					   if (!m.getExported().equalsIgnoreCase("Y")){
						  m.setCanActionEditCust(true);
						  m.setCanActionEditCust2(false);
					   }else{
						 // m.setDisplayActionEditCust("");
						   if( role.equalsIgnoreCase(User.VAN)){
						     m.setCanActionEditCust2(true);
						   }
					   }
					}else{
						if( role.equalsIgnoreCase(User.VAN)){
						  m.setCanActionEditCust2(true);
						}
					}
				}else if(role.equalsIgnoreCase(User.TT)){
					   m.setCanActionEditCust2(true);
				}
				
				//logger.debug("setDisplayActionEditCust:"+m.getDisplayActionEditCust());
				
				//displayActionReceipt
				m.setDisplayActionReceipt(displayActionReceipt);
				
				custList.add(m);
			}
			
			//convert to Obj
			if(custList != null && custList.size() >0){
				array = new Customer[custList.size()];
				array = custList.toArray(array);
			}else{
				array = null;
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			
		}
		
		return array;
	}

	public Customer[] searchOptForStockCustomer(Connection conn,String whereCause,User user,int start) throws Exception {
		return searchOptForStockCustomerModel(conn,whereCause,user,start);
	}
	
    public Customer[] searchOptForStockCustomer(String whereCause,User user,int start) throws Exception {
	   Connection conn = null;
	   try{
		   conn = new DBCPConnectionProvider().getConnection(conn);
		   return searchOptForStockCustomerModel(conn,whereCause,user,start);
	   }catch(Exception e){
		   throw e;
	   }finally{
			conn.close();
	   }
	}
    
   private Customer[] searchOptForStockCustomerModel(Connection conn,String whereCause,User user,int start) throws Exception {
		
		Statement stmt = null;
		ResultSet rst = null;
		List<Customer> custList = new ArrayList<Customer>();
		Customer[] array = null;
		try {
			//Filter display Column
			String displayActionReceipt ="";
			String displayActionEditCust  ="";
			String role = user.getType();
			if( !role.equalsIgnoreCase(User.TT)){
				displayActionReceipt ="none";
			}
			if( role.equalsIgnoreCase(User.TT)){
				displayActionEditCust ="none";
			}
			
			String sql = "select distinct m_customer.*  \n";
			       sql+=" ,m_address.line1,m_address.line2,m_address.line3,m_address.line4 \n";
				   sql+=" ,(select d.name from m_district d where d.district_id = m_address.district_id) as district_name \n";
				   sql+=" ,m_address.province_name,m_address.postal_code, \n";
			       sql+=" ad_user.CATEGORY,ad_user.ORGANIZATION,ad_user.START_DATE,ad_user.END_DATE, \n";
                   sql+=" ad_user.NAME,ad_user.SOURCE_NAME,ad_user.ID_CARD_NO,ad_user.USER_NAME,ad_user.PASSWORD, \n";
                   sql+=" ad_user.ROLE,ad_user.ISACTIVE,ad_user.CODE,ad_user.UPDATED,ad_user.UPDATED_BY,ad_user.TERRITORY, \n";
                   sql+= " ( select count(*) as tot from t_order od where od.customer_id = m_customer.customer_id)  as order_amount \n";
                   sql+=",PRINT_TYPE ,PRINT_BRANCH_DESC,PRINT_HEAD_BRANCH_DESC,PRINT_TAX ";
                   sql+= "from m_customer ,m_address ,ad_user " ;
                   sql+=" where m_customer.user_id = ad_user.user_id \n";
                   sql+= "and m_customer.customer_id = m_address.customer_id  " ;
                   sql+= "and m_address.purpose ='B' \n";
			       sql+= whereCause;
			       
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				start++;
				Customer m = new Customer();
				m.setNo(start);
				// Mandatory
				m.setId(rst.getLong("CUSTOMER_ID"));
				m.setReferencesID(rst.getInt("REFERENCE_ID"));
				m.setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
				m.setCode(rst.getString("CODE").trim());
				m.setName(rst.getString("NAME").trim());
				m.setName2(ConvertNullUtil.convertToString(rst.getString("NAME2")).trim());
				m.setTaxNo(ConvertNullUtil.convertToString(rst.getString("TAX_NO")).trim());
				m.setWebsite(ConvertNullUtil.convertToString(rst.getString("WEBSITE")).trim());
				m.setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
				m.setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
				// System.out.println(rst.getString("PARENT_CUSTOMER_ID"));
				if (rst.getInt("PARENT_CUSTOMER_ID") != 0 && rst.getString("PARENT_CUSTOMER_ID") != null) {
					Customer c = new MCustomer().find(rst.getString("PARENT_CUSTOMER_ID"));
					m.setParentID(c.getId());
					m.setParentCode(c.getCode());
					m.setParentName((c.getName() + " " + c.getName2()).trim());
				}
				m.setBirthDay("");
				if (rst.getTimestamp("BIRTHDAY") != null) {
					m.setBirthDay(DateToolsUtil.convertToString(rst.getTimestamp("BIRTHDAY")));
				}
				m.setCreditCheck(ConvertNullUtil.convertToString(rst.getString("CREDIT_CHECK")).trim());
				m.setPaymentTerm(ConvertNullUtil.convertToString(rst.getString("PAYMENT_TERM")).trim());
				m.setVatCode(ConvertNullUtil.convertToString(rst.getString("VAT_CODE")).trim());
				m.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				m.setShippingMethod(ConvertNullUtil.convertToString(rst.getString("SHIPPING_METHOD")).trim());
				m.setShippingRoute(ConvertNullUtil.convertToString(rst.getString("SHIPPING_ROUTE")).trim());
				m.setTransitName(ConvertNullUtil.convertToString(rst.getString("TRANSIT_NAME")).trim());
				
				/** **/
				User u = new User();
				u.setId(rst.getInt("USER_ID"));
				u.setCode(rst.getString("CODE").trim());
				u.setName(rst.getString("NAME").trim());
				u.setType(convertToString(rst.getString("ROLE")).trim());
				u.setActive(rst.getString("ISACTIVE").trim());

				// oracle fields
				u.setCategory(convertToString(rst.getString("CATEGORY")).trim());
				u.setOrganization(convertToString(rst.getString("ORGANIZATION")).trim());
				u.setSourceName(convertToString(rst.getString("SOURCE_NAME")).trim());
				u.setIdCardNo(convertToString(rst.getString("ID_CARD_NO")).trim());
				u.setStartDate(convertToString(rst.getString("START_DATE")).trim());
				u.setEndDate(convertToString(rst.getString("END_DATE")).trim());
				u.setTerritory(convertToString(rst.getString("TERRITORY")).trim());

				// sales online fields
				u.setUserName(convertToString(rst.getString("USER_NAME")).trim());
				u.setPassword(convertToString(rst.getString("PASSWORD")).trim());
				u.setConfirmPassword(convertToString(rst.getString("PASSWORD")).trim());
				
				u.activeRoleInfo();
				m.setSalesRepresent(u);
				/** **/
				
				m.setCreditLimit(rst.getDouble("CREDIT_LIMIT"));
				m.setIsActive(rst.getString("ISACTIVE").trim());
				m.setInterfaces(rst.getString("INTERFACES").trim());
				m.setPartyType(ConvertNullUtil.convertToString(rst.getString("PARTY_TYPE")).trim());
				m.setExported(rst.getString("EXPORTED"));

				String addressSummary  = Utils.isNull(rst.getString("line1"))+" "+Utils.isNull(rst.getString("line2"))+" "+Utils.isNull(rst.getString("line3"));
				       addressSummary += " "+Utils.isNull(rst.getString("line4"))+" "+Utils.isNull(rst.getString("province_name"))+" "+Utils.isNull(rst.getString("postal_code"));
				m.setAddressSummary(addressSummary);
				
				// set display label
				m.setDisplayLabel();
				
				//Show or Display Column
				
				//Disp Column Edit Customer
				m.setDisplayActionEditCust(displayActionEditCust);//disp
				
				//Can Edit Dust
				if(role.equalsIgnoreCase(User.ADMIN)){
					m.setCanActionEditCust(true);
				}else if(role.equalsIgnoreCase(User.VAN)){
					if (m.getOrderAmount()== 0){
					   if (!m.getExported().equalsIgnoreCase("Y")){
						  m.setCanActionEditCust(true);
						  m.setCanActionEditCust2(false);
					   }else{
						 // m.setDisplayActionEditCust("");
						   if( role.equalsIgnoreCase(User.VAN)){
						     m.setCanActionEditCust2(true);
						   }
					   }
					}else{
						if( role.equalsIgnoreCase(User.VAN)){
						  m.setCanActionEditCust2(true);
						}
					}
				}
				
				//logger.debug("setDisplayActionEditCust:"+m.getDisplayActionEditCust());
				
				//displayActionReceipt
				m.setDisplayActionReceipt(displayActionReceipt);
				
				custList.add(m);
			}
			
			//convert to Obj
			if(custList != null && custList.size() >0){
				array = new Customer[custList.size()];
				array = custList.toArray(array);
			}else{
				array = null;
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			
		}
		
		return array;
	}
	/**
	 * Save
	 * 
	 * @param customer
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private long id = 0;

	public boolean save(Customer customer, int activeUserID,String salesCode, Connection conn) throws Exception {

		if (customer.getId() ==0) {
			id = SequenceProcessAll.getIns().getNextValue("m_customer").longValue();
			customer.setCode(new CustomerDocumentProcess().getNextDocumentNo(salesCode, activeUserID, conn));
		} else {
			id = customer.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "CODE", customer.getCode(), id, conn)) return false;

		Object[] values = { id, ConvertNullUtil.convertToString(customer.getCode()).trim(),
				ConvertNullUtil.convertToString(customer.getName()).trim(),
				ConvertNullUtil.convertToString(customer.getName2()).trim(),
				ConvertNullUtil.convertToString(customer.getCustomerType()).trim(),
				ConvertNullUtil.convertToString(customer.getTaxNo()).trim(),
				ConvertNullUtil.convertToString(customer.getTerritory()).trim(),
				ConvertNullUtil.convertToString(customer.getWebsite()).trim(),
				ConvertNullUtil.convertToString(customer.getBusinessType()).trim(),
				DateToolsUtil.convertToTimeStamp(customer.getBirthDay()), "N",
				ConvertNullUtil.convertToString(customer.getPaymentTerm()).trim(),
				ConvertNullUtil.convertToString(customer.getVatCode()).trim(),
				ConvertNullUtil.convertToString(customer.getPaymentMethod()).trim(),
				ConvertNullUtil.convertToString(customer.getShippingMethod()).trim(),
				activeUserID, customer.getIsActive() != null ? customer.getIsActive() : "N",
				activeUserID, activeUserID, customer.getParentID(),
				ConvertNullUtil.convertToString(customer.getPartyType()).trim(),
				customer.getPrintTax(),customer.getPrintType(),customer.getPrintBranchDesc(),customer.getPrintHeadBranchDesc(),
				Utils.isNull(customer.getAirpayFlag()),Utils.isNull(customer.getLocation()),
				Utils.isNull(customer.getImageFileName()),"Y",
				Utils.isNull(customer.getTripDay()),
				Utils.isNull(customer.getTripDay2()),
				Utils.isNull(customer.getTripDay3())
		      };
		if (super.save(TABLE_NAME, columns, values, customer.getId(), conn)) {
			customer.setId(id);
		}
		return true;
	}
	
	public boolean update(Customer customer, int activeUserID,String salesCode, Connection conn) throws Exception {
		PreparedStatement ps = null;
		try{
		
			
			String sql = "update m_customer set tax_no ='"+customer.getTaxNo()+"' " +
			        "\n ,print_type ='"+customer.getPrintType()+"',print_tax='"+customer.getPrintTax()+"'"+
			        "\n ,PRINT_BRANCH_DESC ='"+customer.getPrintBranchDesc()+"',PRINT_HEAD_BRANCH_DESC='"+customer.getPrintHeadBranchDesc()+"'"+
			        "\n ,airpay_flag ='"+customer.getAirpayFlag()+"' ,updated = sysdate() ,updated_by="+activeUserID+",location='"+customer.getLocation()+"'"+
			        "\n ,image_file_name='"+Utils.isNull(customer.getImageFileName())+"'"+
			        "\n ,NAME='"+ConvertNullUtil.convertToString(customer.getName()).trim()+"'"+
			        "\n ,NAME2='"+ConvertNullUtil.convertToString(customer.getName2()).trim()+"'"+
			        "\n ,WEBSITE='"+ConvertNullUtil.convertToString(customer.getWebsite()).trim()+"'"+
			        "\n ,BUSINESS_TYPE='"+ConvertNullUtil.convertToString(customer.getBusinessType()).trim()+"'"+
			        "\n ,BIRTHDAY=?"+
			        "\n ,PARENT_CUSTOMER_ID="+customer.getParentID()+""+
			        "\n ,business_type='"+ConvertNullUtil.convertToString(customer.getPartyType()).trim()+"',is_change='Y'"+
					"\n  where customer_id ="+ customer.getId();
			
			 logger.debug("sql:"+sql);
			 
			 ps = conn.prepareStatement(sql);
			 ps.setTimestamp(1, DateToolsUtil.convertToTimeStamp(customer.getBirthDay()));
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	
	public boolean updateImageFile(Customer customer, int activeUserID,String salesCode, Connection conn) throws Exception {
		PreparedStatement ps = null;
		try{
			String sql = "update m_customer set image_file_name='"+Utils.isNull(customer.getImageFileName())+"'"+
					"\n  where customer_id ="+ customer.getId();
			
			 logger.debug("sql:"+sql);
			 
			 ps = conn.prepareStatement(sql);
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	

	public boolean updateCredit(Customer customer, int activeUserID,String salesCode, Connection conn) throws Exception {
		PreparedStatement ps = null;
		try{
		
			String sql = "update m_customer set image_file_name='"+Utils.isNull(customer.getImageFileName())+"'"+
					"\n ,location='"+customer.getLocation()+"' ,is_change='Y' "+
					"\n  where customer_id ="+ customer.getId();
			
			 logger.debug("sql:"+sql);
			 
			 ps = conn.prepareStatement(sql);
			 
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	

	/**
	 * Get Invoice Amount
	 * 
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public double getInvoiceAmount(int customerId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		double orderAmt = 0;
		double paidAmt = 0;
		double creditNoteAmt = 0 ;
		double invoiceAmt = 0;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			String sql = "select sum(ROUND(o.net_amount,2)) net_amount ";
			sql += "from t_order o ";
			sql += "where o.doc_status = 'SV' ";
			// sql += "  and o.interfaces = 'Y' ";
			// sql += "  and o.ar_invoice_no is not null ";
			// sql += "  and o.ar_invoice_no <> '' ";
			sql += "  and o.CUSTOMER_ID = " + customerId;

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if (rst.next()) orderAmt = rst.getDouble("net_amount");

			sql = "SELECT SUM(rl.PAID_AMOUNT) paid_amount ";
			sql += "FROM T_RECEIPT_LINE rl , T_RECEIPT r ";
			sql += "WHERE rl.RECEIPT_ID = r.RECEIPT_ID AND r.doc_status = 'SV' ";
			sql += "  AND r.CUSTOMER_ID = " + customerId;
            
			//logger.debug("sql:"+sql);
			rst = stmt.executeQuery(sql);
			if (rst.next()) paidAmt = rst.getDouble("paid_amount");
			
			/* Reduce From Credit No*/
			/*sql = "SELECT SUM(t.TOTAL_AMOUNT) as creditNoteAmt FROM ( "+
					" SELECT cn.* FROM t_credit_note cn "+
					" WHERE NOT EXISTS ( SELECT 1 FROM t_order od , t_receipt rc, t_receipt_line rcl WHERE rc.receipt_id = rcl.receipt_id AND od.order_id = rcl.order_id AND rc.doc_status = 'SV'AND od.ar_invoice_no = cn.ar_invoice_no) "+
					" AND NOT EXISTS (SELECT 1 FROM t_receipt rc,t_receipt_cn rccn WHERE rc.receipt_id = rccn.receipt_id AND rc.Doc_Status = 'SV' AND rccn.credit_note_id = cn.credit_note_id) "+
					" AND cn.AR_Invoice_No IS NOT NULL AND cn.active='Y' "+
					" UNION "+
					" SELECT cn.* FROM t_credit_note cn WHERE cn.active='Y' AND cn.ar_invoice_no IS NULL "+
					" AND NOT EXISTS (SELECT 1 FROM t_receipt rc,t_receipt_cn rccn WHERE rc.receipt_id = rccn.receipt_id AND rc.Doc_Status = 'SV' AND rccn.credit_note_id = cn.credit_note_id ) "+
					") t WHERE t.CUSTOMER_ID = " + customerId;
			*/
			sql = "select sum(cn.total_amount) as creditNoteAmt from t_credit_note cn where NOT EXISTS ( SELECT 1 FROM T_ORDER od WHERE od.AR_INVOICE_NO = cn.AR_INVOICE_NO AND (od.PAYMENT = 'Y' OR od.DOC_STATUS = 'VO')) AND cn.active = 'Y' and cn.customer_id = "+customerId;
			
			rst = stmt.executeQuery(sql);
			if (rst.next()) creditNoteAmt = rst.getDouble("creditNoteAmt");
			
			Log.debug("Customer Credit Note Amount : "+creditNoteAmt);

			invoiceAmt = orderAmt - paidAmt + creditNoteAmt;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return invoiceAmt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
