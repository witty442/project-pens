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
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.document.CustomerDocumentProcess;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnection;
import com.pens.util.DateToolsUtil;
import com.pens.util.Utils;
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

	public static String TABLE_NAME = "m_customer";
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
	public Customer findOpt(String id) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Customer cust = null;
		Address address = null;
		Contact contact = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("\n select c.* ");
			sql.append("\n ,(select aa.address_id from pensonline.m_address aa where");
			sql.append("\n  c.customer_id = aa.customer_id and aa.purpose='B') as bill_to_address_id");
			sql.append("\n ,(select aa.address_id from pensonline.m_address aa where");
			sql.append("\n  c.customer_id = aa.customer_id and aa.purpose='S') as ship_to_address_id");
			sql.append("\n ,a.line1,a.line2,a.line3,a.line4,a.province_id,a.district_id ,a.postal_code");
			sql.append("\n ,t.contact_to,t.mobile ");
			sql.append("\n from pensonline.m_customer c ");
			sql.append("\n left join pensonline.m_address a on c.customer_id = a.customer_id and a.purpose='B'");
			sql.append("\n left join pensonline.m_contact t on c.customer_id = t.customer_id");
			sql.append("\n where c.customer_id = "+id);
			logger.debug(sql.toString());
			
			conn = DBConnection.getInstance().getConnection();
			ps =conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()) {
				cust = new Customer(rs);
				cust.setBillToAddressId(rs.getInt("bill_to_address_id"));
				cust.setShipToAddressId(rs.getInt("ship_to_address_id"));
				cust.setProvince(Utils.isNull(rs.getString("province_id")));
				cust.setDistrict(Utils.isNull(rs.getString("district_id")));
				
				address = new Address();
				address.setLine1(Utils.isNull(rs.getString("line1")));
				address.setLine2(Utils.isNull(rs.getString("line2")));
				address.setLine3(Utils.isNull(rs.getString("line3")));
				address.setLine4(Utils.isNull(rs.getString("line4")));
				address.setPostalCode(Utils.isNull(rs.getString("postal_code")));
				cust.setAddress(address);
				
				contact = new Contact();
				contact.setContactTo(Utils.isNull(rs.getString("contact_to")));
				contact.setMobile(Utils.isNull(rs.getString("mobile")));
				cust.setContact(contact);
			}
			return cust;
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
			ps.close();
			rs.close();
		}
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
			String sql ="\n select * from m_customer "+whereSql ;
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
	public String getCustGroup(long customerId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		String custGroup = "";
		try{
			String sql ="\n select cust_group from m_customer where customer_id="+customerId ;
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
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
				conn.close();conn=null;
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
			String sql ="\n select count(*) as c from m_customer "+
			            "\n where customer_id = "+customerId+ " and code in(select code from m_customer_center)" ;
			
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
			            "\n   select distinct m_customer.* from m_customer ,m_address  "+
					    "\n   where 1=1 and m_customer.customer_id = m_address.customer_id " +
					    "\n   and m_address.purpose ='B' "+whereCause+
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
			String sql ="\n  select distinct trip_day,trip_day2,trip_day3 from m_customer ,m_address  "
					   +"\n  where 1=1 and m_customer.customer_id = m_address.customer_id " +
			            "\n  and m_address.purpose ='B' "+
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
	
	public String genWhereSQL(Customer customer,User user) {
		String whereCause = "";
		if (customer.getTerritory() != null
				&& !customer.getTerritory().trim().equals("")) {
			whereCause += "\n AND m_customer.TERRITORY = '" + customer.getTerritory().trim() + "'";
		}
		if (customer.getCode() != null && !customer.getCode().trim().equals("")) {
			whereCause += "\n AND m_customer.CODE LIKE '%"
					+ customer.getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"")
					+ "%' ";
		}
		if (customer.getName() != null && !customer.getName().trim().equals("")) {
			whereCause += "\n AND m_customer.NAME LIKE '%"
					+ customer.getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
					+ "%' ";
		}
		if (customer.getIsActive() != null
				&& !customer.getIsActive().equals("")) {
			whereCause += "\n AND m_customer.ISACTIVE = '" + customer.getIsActive() + "'";
		}
		// WIT EDIT :04/08/2554 
		if(!User.ADMIN.equals(user.getType())){
		   whereCause += "\n AND m_customer.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
		   whereCause += "\n AND m_customer.USER_ID = " + user.getId();
		}
		
		if ( !"".equals(Utils.isNull(customer.getDistrict())) && !"0".equals(Utils.isNull(customer.getDistrict())) ){
			whereCause += "\n AND m_address.district_id = " + customer.getDistrict() + "";
		}
		
		if (customer.getSearchProvince() != 0) {
			whereCause += "\n AND m_customer.CUSTOMER_ID IN (select customer_id ";
			whereCause += "\n from m_address where province_id = " + customer.getSearchProvince()
					 + ")";
		}
		
		if (  !Utils.isNull(customer.getDispHaveTrip()).equals("") ) {
			whereCause +="\n AND ( m_customer.trip_day <> 0 or m_customer.trip_day2 <>0 or m_customer.trip_day3 <> 0) ";
		  
		}
		return whereCause;
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
	private Customer[] searchOptModel(Connection conn,String whereCause,User user,int start,String dispTotalInvoice) throws Exception {
		
		Statement stmt = null;
		ResultSet rst = null;
		List<Customer> custList = new ArrayList<Customer>();
		Customer[] array = null;
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
			
			String sql = "select distinct m_customer.*  \n";
			       sql+=" ,m_address.line1,m_address.line2,m_address.line3,m_address.line4 \n";
			       
				   sql+=" ,(select d.name from m_district d where d.district_id = m_address.district_id) as district_name \n";
				   sql+=" ,m_address.province_name,m_address.postal_code, \n";
			       sql+=" ad_user.CATEGORY,ad_user.ORGANIZATION,ad_user.START_DATE,ad_user.END_DATE, \n";
                   sql+=" ad_user.NAME,ad_user.SOURCE_NAME,ad_user.ID_CARD_NO,ad_user.USER_NAME,ad_user.PASSWORD, \n";
                   sql+=" ad_user.ROLE,ad_user.ISACTIVE,ad_user.CODE,ad_user.UPDATED,ad_user.UPDATED_BY,ad_user.TERRITORY, \n";
                   sql+= " ( select count(*) as tot from t_order od where od.customer_id = m_customer.customer_id)  as order_amount \n";
                  // sql+= " ( select sum(o.net_amount) net_amount from t_order o \n where o.doc_status = 'SV'  and o.CUSTOMER_ID = m_customer.CUSTOMER_ID ) as total_order_amt,\n";
               
                  // sql+= " ( select sum(r.receipt_amount) receipt_amount from t_receipt r \n where r.doc_status = 'SV' and r.CUSTOMER_ID = m_customer.CUSTOMER_ID) as total_receipt_amt \n";
                   sql+=",PRINT_TYPE ,PRINT_BRANCH_DESC,PRINT_HEAD_BRANCH_DESC,PRINT_TAX \n";
                   
                   sql+= " from m_customer  ,ad_user , \n" ;
                   sql +="( \n";
                   sql+="   select distinct customer_id,district_id,line1,line2,line3,line4 ,province_name,postal_code \n";;
                   sql +="  from m_address where purpose ='B'\n";
                   sql +=")m_address \n";
                   sql+= " where m_customer.user_id = ad_user.user_id \n";
                   sql+= " and m_customer.customer_id = m_address.customer_id \n";
			       sql+= whereCause;
			       
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				start++;
				Customer m = new Customer();
				m.setNo(start);
				// Mandatory
				m.setId(rst.getInt("CUSTOMER_ID"));
				m.setOracleCustId(rst.getLong("oracle_cust_id"));
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
				
				// Total Invoice
				//double totalOrderAmt = rst.getDouble("total_order_amt");
				//double totalReceiptAmt = rst.getDouble("total_receipt_amt");
				//m.setTotalInvoice(totalOrderAmt-totalReceiptAmt);
				
				//m.setTotalInvoice(new MReceiptLine().lookCreditAmtBK(conn,m.getId()));
				if( !Utils.isNull(dispTotalInvoice).equals("")){
					logger.info("calcTotalInvoice");
				   m.setTotalInvoice(MReceiptSummary.lookCreditAmtByCustomerId(conn,m.getId(),creditDateFix));
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
			
			String sql = "select distinct m_customer.*  \n";
			       sql+=" ,m_address.line1,m_address.line2,m_address.line3,m_address.line4 \n";
			       
				   sql+=" ,(select d.name from m_district d where d.district_id = m_address.district_id) as district_name \n";
				   sql+=" ,m_address.province_name,m_address.postal_code, \n";
			       sql+=" ad_user.CATEGORY,ad_user.ORGANIZATION,ad_user.START_DATE,ad_user.END_DATE, \n";
                   sql+=" ad_user.NAME,ad_user.SOURCE_NAME,ad_user.ID_CARD_NO,ad_user.USER_NAME,ad_user.PASSWORD, \n";
                   sql+=" ad_user.ROLE,ad_user.ISACTIVE,ad_user.CODE,ad_user.UPDATED,ad_user.UPDATED_BY,ad_user.TERRITORY, \n";
                   sql+= " ( select count(*) as tot from t_order od where od.customer_id = m_customer.customer_id)  as order_amount \n";
                  // sql+= " ( select sum(o.net_amount) net_amount from t_order o \n where o.doc_status = 'SV'  and o.CUSTOMER_ID = m_customer.CUSTOMER_ID ) as total_order_amt,\n";
               
                  // sql+= " ( select sum(r.receipt_amount) receipt_amount from t_receipt r \n where r.doc_status = 'SV' and r.CUSTOMER_ID = m_customer.CUSTOMER_ID) as total_receipt_amt \n";
                   sql+=",PRINT_TYPE ,PRINT_BRANCH_DESC,PRINT_HEAD_BRANCH_DESC,PRINT_TAX \n";
                   
                   sql+= " from m_customer  ,ad_user , \n" ;
                   sql +="( \n";
                   sql+="   select distinct customer_id,district_id,line1,line2,line3,line4 ,province_name,postal_code \n";;
                   sql +="  from m_address where purpose ='B'\n";
                   sql +=")m_address \n";
                   sql+= " where m_customer.user_id = ad_user.user_id \n";
                   sql+= " and m_customer.customer_id = m_address.customer_id \n";
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
				m.setId(rst.getInt("CUSTOMER_ID"));
				m.setOracleCustId(rst.getLong("oracle_cust_id"));
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
				    m.setTotalInvoice(MReceiptSummary.lookCreditAmtByCustomerId(conn,m.getId(),creditDateFix));
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
				m.setId(rst.getInt("CUSTOMER_ID"));
				m.setOracleCustId(rst.getLong("oracle_cust_id"));
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

		if (customer.getId() == 0) {
			//id = SequenceProcess.getNextValue(TABLE_NAME);
			id = SequenceProcessAll.getIns().getNextValue("m_customer.customer_id").intValue();
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
			        //"\n ,BUSINESS_TYPE='"+ConvertNullUtil.convertToString(customer.getBusinessType()).trim()+"'"+
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
