package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.District;
import com.isecinc.pens.bean.Province;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.process.SequenceProcess;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;

/**
 * MAddress Class
 * 
 * @author atiz.b
 * @version $Id: MAddress.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MAddress extends I_Model<Address> {

	private static final long serialVersionUID = -4119649113903274964L;

	public static String TABLE_NAME = "m_address";
	public static String COLUMN_ID = "ADDRESS_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "LINE1", "LINE2", "LINE3", "LINE4", "PROVINCE_ID", "DISTRICT_ID",
			"POSTAL_CODE", "PURPOSE", "CUSTOMER_ID", "ISACTIVE", "CREATED_BY", "UPDATED_BY", "PROVINCE_NAME" , "REFERENCE_ID" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Address find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Address.class);
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
	public Address[] search(String whereCause) throws Exception {
		List<Address> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Address.class);
		if (pos.size() == 0) return null;
		Address[] array = new Address[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param address
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(Address address, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (address.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = address.getId();
		}
		Object[] values = { id, ConvertNullUtil.convertToString(address.getLine1()).trim(),
				ConvertNullUtil.convertToString(address.getLine2()).trim(),
				ConvertNullUtil.convertToString(address.getLine3()).trim(),
				ConvertNullUtil.convertToString(address.getLine4()).trim(), address.getProvince().getId(),
				address.getDistrict().getId(), ConvertNullUtil.convertToString(address.getPostalCode()).trim(),
				ConvertNullUtil.convertToString(address.getPurpose()).trim(), address.getCustomerId(),
				address.getIsActive() != null ? address.getIsActive() : "N", activeUserID, activeUserID,
				ConvertNullUtil.convertToString(address.getProvince().getName()) ,address.getReferenceId()==0?null:address.getReferenceId() };
		if (super.save(TABLE_NAME, columns, values, address.getId(), conn)) {
			address.setId(id);
		}
		return true;
	}

	/**
	 * Look Up
	 */
	public List<Address> lookUp(int customerId) {
		List<Address> pos = new ArrayList<Address>();
		try {
			String whereCause = " AND CUSTOMER_ID = " + customerId + " ORDER BY ADDRESS_ID ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Address.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	public Address findAddressByCustomerId(Connection conn,String customerId) throws Exception {

		Statement stmt = null;
		ResultSet rst = null;
		Address a = null;
		try {
			String sql = "select  a.*,\n";
				 sql +=" (select max(d.name) from m_district d where d.district_id = a.district_id) as district_name \n";
				 sql +=" from m_address a \n";
				 sql +=" where purpose ='S' \n";	
				 sql +=" and customer_id = "+customerId;
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				a = new Address();
				a.setId(rst.getInt("ADDRESS_ID"));
				a.setCustomerId(rst.getInt("CUSTOMER_ID"));
				a.setLine1(rst.getString("Line1").trim());
				a.setLine2(rst.getString("Line2").trim());
				a.setLine3(ConvertNullUtil.convertToString(rst.getString("Line3")).trim());
				a.setLine4(ConvertNullUtil.convertToString(rst.getString("Line4")).trim());
				
				District d = new District();
				d.setName(rst.getString("district_name"));
				a.setDistrict(d);
				Province p = new Province();
				p.setName(rst.getString("province_name"));
				a.setProvince(p);
				
				a.setPostalCode(rst.getString("POSTAL_CODE").trim());
				a.setPurpose(rst.getString("PURPOSE").trim());
				a.setIsActive(rst.getString("ISACTIVE").trim());

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
		
		return a;
	}
	
	public Address findAddressByCustomerId(Connection conn,String customerId,String purpose) throws Exception {

		Statement stmt = null;
		ResultSet rst = null;
		Address a = null;
		try {
			String sql = "select  a.*,\n";
				 sql +=" (select max(d.name) from m_district d where d.district_id = a.district_id) as district_name \n";
				 sql +=" from m_address a \n";
				 sql +=" where purpose ='"+purpose+"' \n";	
				 sql +=" and customer_id = "+customerId;
				 sql +=" ORDER BY ADDRESS_ID DESC";
					
			//logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				a = new Address();
				a.setId(rst.getInt("ADDRESS_ID"));
				a.setCustomerId(rst.getInt("CUSTOMER_ID"));
				a.setLine1(rst.getString("Line1").trim());
				a.setLine2(rst.getString("Line2").trim());
				a.setLine3(ConvertNullUtil.convertToString(rst.getString("Line3")).trim());
				a.setLine4(ConvertNullUtil.convertToString(rst.getString("Line4")).trim());
				
				District d = new District();
				d.setName(rst.getString("district_name"));
				a.setDistrict(d);
				Province p = new Province();
				p.setName(rst.getString("province_name"));
				a.setProvince(p);
				
				a.setPostalCode(rst.getString("POSTAL_CODE").trim());
				a.setPurpose(rst.getString("PURPOSE").trim());
				a.setIsActive(rst.getString("ISACTIVE").trim());

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
		
		return a;
	}
}
