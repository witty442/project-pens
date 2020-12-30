package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CustomerNissin;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnectionApps;
import com.pens.util.seq.SequenceProcessAll;

/**
 * MCustomerNissin Class
 * 
 * @author Wittaya
 * @version 1.0 
 * 
 */

public class MCustomerNissin extends I_Model<CustomerNissin> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "pensso.m_customer_nis";
	public static String COLUMN_ID = "Customer_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "NAME", "CUSTOMER_TYPE", "MOBILE",
			"ADDRESS_LINE1", "ADDRESS_LINE2", "ADDRESS_LINE3", "DISTRICT_ID","PROVINCE_ID", "CREATED_BY"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CustomerNissin find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, CustomerNissin.class);
	}
	public CustomerNissin find(Connection conn,String id) throws Exception {
		return super.find(conn,id, TABLE_NAME, COLUMN_ID, CustomerNissin.class);
	}
	public CustomerNissin findByWhereCond(String whereSql) throws Exception {
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
	public CustomerNissin findByWhereCond(Connection conn ,String whereSql) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		CustomerNissin p = null;
		try{
			String sql ="\n select * from pensso.m_customer_nis "+whereSql ;
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				p = new CustomerNissin(rst);
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
	public CustomerNissin findOpt(long customerId) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			return findOpt(conn, customerId);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	public CustomerNissin findOpt(Connection conn ,long customerId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		CustomerNissin p = null;
		try{
			String sql  ="\n select c.* ";
			       sql +="\n ,(select name from pensso.m_district m where m.district_id = c.district_id) as district_name ";
			       sql +="\n ,(select name from pensso.m_province m where m.province_id = c.province_id) as province_name ";
			       sql +="\n ,(select count(*)  from pensso.t_order_nis n"
			       		+ "    where n.order_id = ("
			       		+ "     select max(order_id) from pensso.t_order_nis ns where "
			       		+ "     ns.customer_id = c.customer_id and ns.salesrep_code is not null)"
			       		+ "   ) as order_c ";
			       sql +="\n from pensso.m_customer_nis c where 1=1" ;
			if(customerId != 0){
			   sql +="\n and c.customer_id ="+customerId;
			}
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				p = new CustomerNissin();
				p.setId(rst.getLong("CUSTOMER_ID"));
				p.setName(Utils.isNull(rst.getString("name")));
				p.setCustomerType(Utils.isNull(rst.getString("customer_type")));
				p.setInterfaces(rst.getString("INTERFACES").trim());
				p.setAddressLine1(Utils.isNull(rst.getString("address_line1")));
				p.setAddressLine2(Utils.isNull(rst.getString("address_line2")));
				p.setAddressLine3(Utils.isNull(rst.getString("address_line3")));
				p.setDistrictId(Utils.isNull(rst.getString("district_id")));
				p.setProvinceId(Utils.isNull(rst.getString("province_id")));
				p.setMobile(Utils.isNull(rst.getString("mobile")));
				p.setExported(rst.getString("EXPORTED"));
				p.setDistrictName(Utils.isNull(rst.getString("district_name")));
				p.setProvinceName(Utils.isNull(rst.getString("province_name")));
				p.setAddressSummary(p.getAddressLine1()+" "+p.getAddressLine2()+" "+p.getAddressLine3());
				p.setAddressSummary(p.getAddressSummary()+" "+p.getDistrictName()+ " "+p.getProvinceName());
				
				//set can edit customer or new order
                if(rst.getInt("order_c") >0){
                	p.setCanEdit(false);
                }else{
                	p.setCanEdit(true);
                }
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
	
	public String getProvinceGroupModel(Connection conn,long customerId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String provinceGroup = "";
		try{
			String sql ="\n select (select group_name from pensso.m_province_group g where g.group_id=p.group_id) as province_group"
					+"\n from pensso.m_customer_nis m,pensso.m_address a ,pensso.m_province p"
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
	public CustomerNissin[] search(String whereCause) throws Exception {
		List<CustomerNissin> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, CustomerNissin.class);
		if (pos.size() == 0) return null;
		CustomerNissin[] array = new CustomerNissin[pos.size()];
		array = pos.toArray(array);
		return array;
	}
	
	public int getTotalRowCustomer(Connection conn,String whereCause,User user) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		int totalRow = 0;
		try{
			String sql ="\n select count(*) total_row  from("+
			            "\n   select distinct c.* "+
			            "\n   from pensso.m_customer_nis c "+
			            "\n   where 1=1   " +
					    "\n   "+whereCause+
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
	
	public CustomerNissin[] searchOpt(Connection conn,String whereCause,User user,int start) throws Exception {
		return searchOptModel(conn,whereCause,user,start);
	}
	
    public CustomerNissin[] searchOpt(String whereCause,User user,int start) throws Exception {
	   Connection conn = null;
	   try{
		   conn = new DBCPConnectionProvider().getConnection(conn);
		   return searchOptModel(conn,whereCause,user,start);
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
	private CustomerNissin[] searchOptModel(Connection conn,String whereCause,User user,int currPage) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<CustomerNissin> custList = new ArrayList<CustomerNissin>();
		CustomerNissin[] array = null;
		String sql = "";
		CustomerNissin m = null;
		try {
			   sql +=" select M.* from ( \n";
			   sql +=" select A.* ,rownum as r__ from (\n";
		       sql += "  select   \n";
		       sql+="     c.customer_id,c.customer_type,c.name \n";
		       sql+="    ,c.address_line1,c.address_line2,c.address_line3 \n";
			   sql+="    ,c.district_id ,c.province_id,c.mobile \n";
			   sql+="    ,(select name from pensso.m_district m where m.district_id = c.district_id) district_name \n";
			   sql+="    ,(select name from pensso.m_province m where m.province_id = c.province_id) province_name \n";
			   sql+="    ,c.INTERFACES ,c.EXPORTED \n";
			   
			   sql +="\n ,(select count(*)  from pensso.t_order_nis n"
			       		+ "    where n.order_id = ("
			       		+ "     select max(order_id) from pensso.t_order_nis ns where "
			       		+ "     ns.customer_id = c.customer_id and ns.salesrep_code is not null)"
			       		+ ") as order_c ";
			   
               sql+="    from pensso.m_customer_nis c \n" ;
               sql+="    where 1=1 \n";
		       sql+=     whereCause;
		       sql+="    order by c.customer_id desc \n";
		       sql+="   )A \n";
		       sql+="    WHERE rownum < (("+currPage+" * "+50+") + 1 )  \n";
		       sql+=" )M  \n";
		       sql+="  WHERE r__ >= ((("+currPage+"-1) * "+50+") + 1)  \n";
		    
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			int start = Utils.calcStartNoInPage(currPage, 50);
			while(rst.next()){
				m = new CustomerNissin();
				m.setNo(start);
				// Mandatory
				m.setId(rst.getLong("CUSTOMER_ID"));
				m.setName(Utils.isNull(rst.getString("name")));
				m.setCustomerType(Utils.isNull(rst.getString("customer_type")));
				m.setInterfaces(rst.getString("INTERFACES").trim());
				m.setAddressLine1(Utils.isNull(rst.getString("address_line1")));
				m.setAddressLine2(Utils.isNull(rst.getString("address_line2")));
				m.setAddressLine3(Utils.isNull(rst.getString("address_line3")));
				m.setDistrictId(Utils.isNull(rst.getString("district_id")));
				m.setDistrictName(Utils.isNull(rst.getString("district_name")));
				m.setProvinceId(Utils.isNull(rst.getString("province_id")));
				m.setProvinceName(Utils.isNull(rst.getString("province_name")));
				m.setMobile(Utils.isNull(rst.getString("mobile")));
				m.setExported(rst.getString("EXPORTED"));
                m.setAddressSummary(m.getAddressLine1()+" "+m.getAddressLine2()+" "+m.getAddressLine3());
                m.setAddressSummary(m.getAddressSummary()+" "+m.getDistrictName()+ " "+m.getProvinceName());
                
                //set can edit customer
                if(rst.getInt("order_c") >0){
                	m.setCanEdit(false);
                }else{
                	m.setCanEdit(true);
                }
				custList.add(m);
				
				start++;
			}
			
			//convert to Obj
			if(custList != null && custList.size() >0){
				array = new CustomerNissin[custList.size()];
				array = custList.toArray(array);
			}else{
				array = null;
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if(rst != null)rst.close();
			if(stmt != null)stmt.close();
		}
		
		return array;
	}
	

	/**
	 * Save
	 * 
	 * @param CustomerNissin
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private long id = 0;

	public boolean save(CustomerNissin customer, int activeUserID,String salesCode, Connection conn) throws Exception {

		if (customer.getId() ==0) {
			id = SequenceProcessAll.getIns().getNextValueBySeqDBApp("PENSSO.M_CUSTOMER_NIS_S.nextval").longValue();
		} else {
			id = customer.getId();
		}

		Object[] values = { id, 
				Utils.isNull(customer.getName()),
				Utils.isNull(customer.getCustomerType()),
				Utils.isNull(customer.getMobile()),
				Utils.isNull(customer.getAddressLine1()),
				Utils.isNull(customer.getAddressLine2()),
				Utils.isNull(customer.getAddressLine3()),
				Utils.isNull(customer.getDistrictId()),
				Utils.isNull(customer.getProvinceId()),
				salesCode
		      };
		if (super.save(TABLE_NAME, columns, values, customer.getId(), conn)) {
			customer.setId(id);
		}
		return true;
	}
	
	public boolean update(CustomerNissin customer, int activeUserID,String salesCode, Connection conn) throws Exception {
		PreparedStatement ps = null;
		try{
			String sql = "update pensso.m_customer_nis set name ='"+Utils.isNull(customer.getName())+"' " +
			        "\n ,ADDRES_LINE1='"+Utils.isNull(customer.getAddressLine1())+"'"+
			        "\n ,ADDRES_LINE2='"+Utils.isNull(customer.getAddressLine2())+"'"+
			        "\n ,ADDRES_LINE3='"+Utils.isNull(customer.getAddressLine3())+"'"+
			        "\n ,DISTRICT_ID='"+Utils.isNull(customer.getDistrictId())+"'"+
			        "\n ,PROVINCE_ID='"+Utils.isNull(customer.getProvinceId())+"'"+
			        "\n ,MOBILE='"+Utils.isNull(customer.getMobile())+"'"+
			        "\n ,CUSTOMER_TYPE='"+Utils.isNull(customer.getCustomerType())+"'"+
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
	
}
