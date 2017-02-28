package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.PriceListMasterBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class PriceListMasterDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static List<PriceListMasterBean> searchPriceListMaster(PriceListMasterBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		PriceListMasterBean h = null;
		List<PriceListMasterBean> items = new ArrayList<PriceListMasterBean>();

		try {
			sql.append("\n select J.* " +
					  " \n ,(select M.Interface_desc " +
				      " \n   from PENSBME_MST_REFERENCE M WHERE 1=1 " +
				      " \n  and M.pens_value =J.store_type  and M.reference_code = 'Idwacoal') as cust_group_desc "+
					  " \n from PENSBME_PRICELIST J   ");
			sql.append("\n where 1=1   ");
			
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and STORE_TYPE = '"+Utils.isNull(o.getCustGroup())+"'");
			}
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and GROUP_CODE = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and PENS_ITEM = '"+Utils.isNull(o.getPensItem())+"'");
			}
			if( !Utils.isNull(o.getProductType()).equals("")){
				sql.append("\n and PRODUCT = '"+Utils.isNull(o.getProductType())+"'");
			}
			
			sql.append("\n order by STORE_TYPE,GROUP_CODE asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();
			while(rst.next()) {
				   h = new PriceListMasterBean();
				   h.setProductType(Utils.isNull(rst.getString("product")));
				   h.setCustGroup(Utils.isNull(rst.getString("store_type")));
				   h.setCustGroupDesc(Utils.isNull(rst.getString("cust_group_desc")));
				   h.setGroupCode(Utils.isNull(rst.getString("group_code"))); 
				   h.setPensItem(Utils.isNull(rst.getString("pens_item"))); 
				
				   h.setWholePriceBF(Utils.isNull(rst.getString("whole_price_bf"))); 
				   h.setRetailPriceBF(Utils.isNull(rst.getString("retail_price_bf"))); 
				   
				   if(Utils.isNull(rst.getString("interface_icc")).equalsIgnoreCase("Y") ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
 
			   items.add(h);

			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return items;
	}

	public static PriceListMasterBean isExist(Connection conn,PriceListMasterBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PriceListMasterBean h = null;
		try {
			sql.append("\n select j.* " +
					  " \n ,(select M.Interface_desc " +
				      " \n  from PENSBME_MST_REFERENCE M WHERE 1=1 " +
				      " \n  and M.pens_value =J.store_type  and M.reference_code = 'Idwacoal') as cust_group_desc "+
					  " \n  from PENSBME_PRICELIST J   ");
			sql.append("\n where 1=1   ");
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and STORE_TYPE = '"+Utils.isNull(o.getCustGroup())+"'");
			}
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and GROUP_CODE = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and PENS_ITEM = '"+Utils.isNull(o.getPensItem())+"'");
			}
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				h = new PriceListMasterBean();
				h.setProductType(Utils.isNull(rst.getString("product")));
				h.setCustGroup(Utils.isNull(rst.getString("store_type")));
				h.setCustGroupDesc(Utils.isNull(rst.getString("cust_group_desc")));
				h.setGroupCode(Utils.isNull(rst.getString("group_code"))); 
				h.setPensItem(Utils.isNull(rst.getString("pens_item"))); 
				
				h.setWholePriceBF(Utils.isNull(rst.getString("whole_price_bf"))); 
				h.setRetailPriceBF(Utils.isNull(rst.getString("retail_price_bf"))); 
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return h;
	}
	
	public static boolean isInterfaceICC(Connection conn,PriceListMasterBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean r = false;
		try {
			sql.append("\n select j.interface_icc from PENSBME_PRICELIST J   ");
			sql.append("\n where 1=1   ");
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and STORE_TYPE = '"+Utils.isNull(o.getCustGroup())+"'");
			}
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and GROUP_CODE = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and PENS_ITEM = '"+Utils.isNull(o.getPensItem())+"'");
			}
			if( !Utils.isNull(o.getProductType()).equals("")){
				sql.append("\n and PRODUCT = '"+Utils.isNull(o.getProductType())+"'");
			}
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				 if(Utils.isNull(rst.getString("interface_icc")).equalsIgnoreCase("Y")){
					 r = true;
				 }

			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return r;
	}
	
	 public static boolean isFoundPensItem(Connection conn,PriceListMasterBean o ) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean f = false;
			try {
				sql.append("\n select pens_value FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and reference_code ='LotusItem' ");
				sql.append("\n and pens_desc2 = '"+o.getGroupCode()+"' \n");
				sql.append("\n and pens_value = '"+o.getPensItem()+"' \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					if( !Utils.isNull(rst.getString("pens_value")).equals("")){
						f = true;
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
			return f;
		}
	
	
	 
	 public static void insertNew(Connection conn,PriceListMasterBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_PRICELIST \n");
				sql.append(" (PRODUCT,STORE_TYPE, GROUP_CODE, PENS_ITEM, WHOLE_PRICE_BF, RETAIL_PRICE_BF, CREATE_DATE, CREATE_USER) \n");
			    sql.append(" VALUES (?,?, ?, ?, ?, ?, ?, ? ) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setString(c++, Utils.isNull(o.getProductType()));
				ps.setString(c++, o.getCustGroup());
				ps.setString(c++, o.getGroupCode());
				ps.setString(c++, o.getPensItem());
				ps.setDouble(c++, Utils.convertStrToDouble(o.getWholePriceBF()));
				ps.setDouble(c++, Utils.convertStrToDouble(o.getRetailPriceBF()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 

		public static void update(Connection conn,PriceListMasterBean key,PriceListMasterBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				logger.debug("STORE_TYPE:"+Utils.isNull(key.getCustGroup()));
				logger.debug("GROUP_CODE:"+Utils.isNull(key.getGroupCode()));
				logger.debug("PENS_ITEM:"+Utils.isNull(key.getPensItem()));
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_PRICELIST SET  \n");
				sql.append("  GROUP_CODE = '"+Utils.isNull(o.getGroupCode())+"' \n" );
				sql.append(", PENS_ITEM = '"+Utils.isNull(o.getPensItem())+"' \n" );
				sql.append(", WHOLE_PRICE_BF = "+Utils.convertStrToDouble(o.getWholePriceBF())+" \n" );
				sql.append(", RETAIL_PRICE_BF = "+Utils.convertStrToDouble(o.getRetailPriceBF())+" \n" );
				sql.append(", UPDATE_DATE = sysdate \n" );
				sql.append(", UPDATE_USER = '"+Utils.isNull(o.getUpdateUser())+"' \n" );

				sql.append(" WHERE STORE_TYPE= '"+Utils.isNull(key.getCustGroup())+"'" +
						" AND GROUP_CODE ='"+Utils.isNull(key.getGroupCode())+"'" +
						" AND PENS_ITEM = '"+ Utils.isNull(key.getPensItem())+"' \n" +
				        " AND PRODUCT = '"+ Utils.isNull(key.getProductType())+"' \n" );
                
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
			
				c = ps.executeUpdate();
				logger.debug("Result Update:"+c);
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
}
