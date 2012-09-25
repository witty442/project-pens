package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.DeliveryGroup;

/**
 * MProvince Class
 * 
 * @author atiz.b
 * @version $Id: MProvince.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MDeliveryGroup extends I_Model<DeliveryGroup> {

	private static final long serialVersionUID = -6254839726381212634L;

	public static String TABLE_NAME = "m_delivery_group";
	public static String COLUMN_ID = "DISTRICT_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public DeliveryGroup find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, DeliveryGroup.class);
	}

	/**
	 * Delivery Group
	 * 
	 * @param conn
	 * @param referenceId
	 * @return
	 * @throws Exception
	 */
	 public String getDeliveryGroupId(Connection conn, int referenceId) throws Exception{
			PreparedStatement ps =null;
			ResultSet rs = null;
			StringBuffer sql = new StringBuffer("");
			String deliveryGroupId = "";
			try{
				sql.append(" select value from c_reference where code ='DeliveryGroup' \n");
				sql.append(" and reference_id = " + referenceId);
			    logger.debug(sql.toString());
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					deliveryGroupId = rs.getString("value");
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

			return deliveryGroupId;
		} 
}
