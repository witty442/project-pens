package com.isecinc.pens.web.stockvan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import util.Utils;

public class StockVanDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static List<StockVanBean> searchColumnList(Connection conn,StockVanBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<StockVanBean> columnList = new ArrayList<StockVanBean>();
		StockVanBean columnBean = null;
		try {
			//Case 1  แนวตั้ง : PD/หน่วยรถ    แนวนอน :  รหัสสินค้า  
			if("1".equals(o.getDispType())){
				sql.append("\n  SELECT distinct p.segment1 as column_code from ");
				sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
				sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
				sql.append("\n ,apps.xxpens_salesreps_v s");
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n  WHERE pd.subinventory_code = subinv.subinventory");
				sql.append("\n  AND subinv.user_id = s.user_id");
				sql.append("\n  AND pd.inventory_item_id = p.inventory_item_id");
				 //GenWhereSQL
				sql.append(" "+genWhereCondSql(conn,o));
				sql.append("\n  ORDER BY p.segment1 ");
				
			//Case 2  แนวตั้ง : รหัสสินค้า      แนวนอน :    PD/หน่วยรถ
			}else if("2".equals(o.getDispType())){
				//no display pd intransit
				if( Utils.isNull(o.getDispPlan()).equals("")){
					sql.append("\n  SELECT distinct pd.subinventory_code as column_code from ");
					sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
					sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
					sql.append("\n ,apps.xxpens_salesreps_v s");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd.subinventory_code = subinv.subinventory");
					sql.append("\n  AND subinv.user_id = s.user_id");
					sql.append("\n  AND pd.inventory_item_id = p.inventory_item_id");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o));
					sql.append("\n  ORDER BY pd.subinventory_code ");
				
				//display pd intransit
				}else{
					sql.append("\n SELECT DISTINCT A.column_code");
					sql.append("\n FROM ( ");
					sql.append("\n  SELECT distinct pd.subinventory_code as column_code from ");
					sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
					sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
					sql.append("\n ,apps.xxpens_salesreps_v s");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd.subinventory_code = subinv.subinventory");
					sql.append("\n  AND subinv.user_id = s.user_id");
					sql.append("\n  AND pd.inventory_item_id = p.inventory_item_id");
					sql.append("\n  AND ROUND(pd.primary_quantity,2) <> 0");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o));
				
					sql.append("\n  UNION  ");
					
					sql.append("\n  SELECT distinct pd_int.to_subinventory as column_code from ");
					sql.append("\n  apps.xxpens_inv_intransit_r00_v pd_int");
					sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
					sql.append("\n ,apps.xxpens_salesreps_v s");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd_int.to_subinventory = subinv.subinventory");
					sql.append("\n  AND subinv.user_id = s.user_id");
					sql.append("\n  AND pd_int.inventory_item_id = p.inventory_item_id");
					sql.append("\n  AND ROUND(pd_int.quantity_intransit,2) <> 0");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o));
					sql.append("\n  )A");
					sql.append("\n  ORDER BY A.column_code ");
					
				}
			}

			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				columnBean = new StockVanBean();
				if("1".equals(o.getDispType())){
					columnBean.setProductCode(Utils.isNull(rst.getString("column_code")));
				}else if("2".equals(o.getDispType())){
					//no display pd intransit
					if( Utils.isNull(o.getDispPlan()).equals("")){
					   columnBean.setPdCode(Utils.isNull(rst.getString("column_code")));
					}else{
					   //display pd intransit
						columnBean.setPdCode(Utils.isNull(rst.getString("column_code")));
						columnBean.setPdCodeIntransit(Utils.isNull(rst.getString("column_code")));
					}
				}
				columnList.add(columnBean);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return columnList;
	}

	public static List<StockVanBean> searchStockVanList(Connection conn,StockVanBean o ,List<StockVanBean> columnList) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockVanBean rowItem = null;
		List<StockVanBean> items = new ArrayList<StockVanBean>();
		List<StockVanBean> rowColumnDataList = new ArrayList<StockVanBean>();
		StockVanBean columnBean = null;
		StockVanBean columnDataBean = null;
		String keyMap = "";
		try {
			sql.append("\n select A.* from (");
			/******************************************************************************/
			sql.append("\n /** MASTER **/ ");
			sql.append("\n  SELECT distinct ");
			//1 row by pd
			if( Utils.isNull(o.getDispType()).equals("1")){
				sql.append("\n  pd.subinventory_code ,pd.name as subinv_name");
			}else{
				//2 row by product
			   sql.append("\n  p.segment1 as product_code ");
			   sql.append("\n  ,p.description as product_name ");
			}
			sql.append("\n  from ");
			sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
			sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
			sql.append("\n ,apps.xxpens_salesreps_v s");
			sql.append("\n ,apps.xxpens_om_item_mst_v p");
			sql.append("\n  WHERE pd.subinventory_code = subinv.subinventory");
			sql.append("\n  AND subinv.user_id = s.user_id");
			sql.append("\n  AND pd.inventory_item_id = p.inventory_item_id");
			sql.append("\n  AND ROUND(pd.primary_quantity,2) <> 0");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			
			sql.append("\n  UNION ");
			
			sql.append("\n  SELECT distinct ");
			//1 row by pd
			if( Utils.isNull(o.getDispType()).equals("1")){
				sql.append("\n  pd_int.to_subinventory as subinventory_code,pd_int.name as subinv_name");
			}else{
				//2 row by product
			   sql.append("\n  p.segment1 as product_code ");
			   sql.append("\n  ,p.description as product_name ");
			}
			sql.append("\n  from ");
			sql.append("\n  apps.xxpens_inv_intransit_r00_v pd_int");
			sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
			sql.append("\n ,apps.xxpens_salesreps_v s");
			sql.append("\n ,apps.xxpens_om_item_mst_v p");
			sql.append("\n  WHERE pd_int.to_subinventory = subinv.subinventory");
			sql.append("\n  AND subinv.user_id = s.user_id");
			sql.append("\n  AND pd_int.inventory_item_id = p.inventory_item_id");
			sql.append("\n  AND ROUND(pd_int.quantity_intransit,2) <> 0");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			
			/*******************************************************************************/
			sql.append("\n   )A ");
        	
			logger.debug("sql:"+sql);
			
			//Get DataAllMap to Map 
			Map<String, StockVanBean> dataAllMap = getDataAllByColumnCodeToMap(conn, o);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   rowItem = new StockVanBean();
			  
			   //1 row by pd ,column product
			   if( Utils.isNull(o.getDispType()).equals("1")){
				   rowItem.setPdCode(Utils.isNull(rst.getString("subinventory_code")));
				   rowItem.setPdDesc(Utils.isNull(rst.getString("subinv_name")));
			   }else{
				  //2 row by product ,column by pdcode
				   rowItem.setProductCode(Utils.isNull(rst.getString("product_code")));
				   rowItem.setProductName(Utils.isNull(rst.getString("product_name")));
			   }
			   
			   if(columnList != null && columnList.size() >0){
				   rowColumnDataList = new ArrayList<StockVanBean>();
				   for(int c =0;c<columnList.size();c++){
					   columnBean = (StockVanBean)columnList.get(c);
					   
					   //1 row by pd ,column product
					   if( Utils.isNull(o.getDispType()).equals("1")){
						   keyMap =  rowItem.getPdCode()+"_"+columnBean.getProductCode();
					   }else{
						 //2 row by product ,column by pdcode
						   keyMap =  rowItem.getProductCode()+"_"+columnBean.getPdCode();
					   }
					   
					    columnDataBean = dataAllMap.get(keyMap)!=null?(StockVanBean)dataAllMap.get(keyMap):null;
						if(columnDataBean!= null){
							//found in map
						}else{
							//set blank data bean
							columnDataBean = columnBean;
							columnDataBean.setPdQty("");
							columnDataBean.setPdIntQty("");
						}//if
						//add all data By columnList
						rowColumnDataList.add(columnDataBean);
				   }//for
			   }//if
			   
			   //set columnDataList to row
			   rowItem.setRowColumnDataList(rowColumnDataList);
			   
			   items.add(rowItem);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static Map<String,StockVanBean> getDataAllByColumnCodeToMap(Connection conn,StockVanBean o) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String,StockVanBean> map = new HashMap<String,StockVanBean>();
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("\n select M.product_code,M.product_name");
			sql.append("\n ,M.subinventory_code");
			sql.append("\n ,PD.pd_qty");
			sql.append("\n ,PD_INT.pd_int_qty");
			sql.append("\n FROM (");
			sql.append("\n /** MASTER **/ ");
			sql.append("\n  SELECT distinct pd.inventory_item_id ");
			sql.append("\n  ,p.segment1 as product_code ");
			sql.append("\n  ,p.description as product_name ");
			sql.append("\n  ,pd.subinventory_code ");
			sql.append("\n  from ");
			sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
			sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
			sql.append("\n ,apps.xxpens_salesreps_v s");
			sql.append("\n ,apps.xxpens_om_item_mst_v p");
			sql.append("\n  WHERE pd.subinventory_code = subinv.subinventory");
			sql.append("\n  AND subinv.user_id = s.user_id");
			sql.append("\n  AND pd.inventory_item_id = p.inventory_item_id");
			sql.append("\n  AND ROUND(pd.primary_quantity,2) <> 0");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			
			sql.append("\n  UNION ");
			
			sql.append("\n  SELECT distinct pd_int.inventory_item_id ");
			sql.append("\n  ,p.segment1 as product_code ");
			sql.append("\n  ,p.description as product_name ");
			sql.append("\n  ,pd_int.to_subinventory as subinventory_code ");
			sql.append("\n  from ");
			sql.append("\n  apps.xxpens_inv_intransit_r00_v pd_int");
			sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
			sql.append("\n ,apps.xxpens_salesreps_v s");
			sql.append("\n ,apps.xxpens_om_item_mst_v p");
			sql.append("\n  WHERE pd_int.to_subinventory = subinv.subinventory");
			sql.append("\n  AND subinv.user_id = s.user_id");
			sql.append("\n  AND pd_int.inventory_item_id = p.inventory_item_id");
			sql.append("\n  AND ROUND(pd_int.quantity_intransit,2) <> 0");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			
			sql.append("\n ) M ");
			sql.append("\n LEFT OUTER JOIN  ");
			sql.append("\n /** PD **/ ");
			sql.append("\n ( ");
			sql.append("\n  SELECT pd.inventory_item_id ");
			sql.append("\n  ,p.segment1 as product_code ");
			sql.append("\n  ,p.description as product_name ");
			sql.append("\n  ,pd.subinventory_code ");
			sql.append("\n  ,pd.primary_quantity as pd_qty ");
			sql.append("\n  from ");
			sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
			sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
			sql.append("\n ,apps.xxpens_salesreps_v s");
			sql.append("\n ,apps.xxpens_om_item_mst_v p");
			sql.append("\n  WHERE pd.subinventory_code = subinv.subinventory");
			sql.append("\n  AND subinv.user_id = s.user_id");
			sql.append("\n  AND pd.inventory_item_id = p.inventory_item_id");
			sql.append("\n  AND ROUND(pd.primary_quantity,2) <> 0");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			sql.append("\n )PD ON M.inventory_item_id = PD.inventory_item_id ");
			sql.append("\n AND M.subinventory_code = PD.subinventory_code");
			sql.append("\n LEFT OUTER JOIN  ");
			
			sql.append("\n /** PD_INT **/ ");
			sql.append("\n ( ");
			sql.append("\n  SELECT pd_int.inventory_item_id ");
			sql.append("\n  ,p.segment1 as product_code ");
			sql.append("\n  ,p.description as product_name ");
			sql.append("\n  ,pd_int.to_subinventory as subinventory_code ");
			sql.append("\n  ,pd_int.quantity_intransit as pd_int_qty ");
			sql.append("\n  from ");
			sql.append("\n  apps.xxpens_inv_intransit_r00_v pd_int");
			sql.append("\n ,apps.xxpens_inv_subinventory_rule subinv");
			sql.append("\n ,apps.xxpens_salesreps_v s");
			sql.append("\n ,apps.xxpens_om_item_mst_v p");
			sql.append("\n  WHERE pd_int.to_subinventory = subinv.subinventory");
			sql.append("\n  AND subinv.user_id = s.user_id");
			sql.append("\n  AND pd_int.inventory_item_id = p.inventory_item_id");
			sql.append("\n  AND ROUND(pd_int.quantity_intransit,2) <> 0");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			sql.append("\n )PD_INT ON M.inventory_item_id = PD_INT.inventory_item_id ");
			sql.append("\n  AND M.subinventory_code = PD_INT.subinventory_code");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			String keyMap = "";
			while(rs.next()){
				StockVanBean m = new StockVanBean();
			
				if("1".equals(o.getDispType())){
					//no display pd intransit
					if( Utils.isNull(o.getDispPlan()).equals("")){
					   m.setPdCode(Utils.isNull(rs.getString("subinventory_code")));
					   m.setPdDesc(Utils.isNull(rs.getString("subinventory_code")));
					   m.setProductCode(Utils.isNull(rs.getString("product_code")));
					   
					   double pd_qty = (double) Math.round(rs.getDouble("pd_qty") * 100) / 100;
					   
					   m.setPdQty(Utils.decimalFormat(pd_qty,Utils.format_current_2_disgit));
					   //key
					   keyMap = m.getPdCode()+"_"+m.getProductCode();
					}else{
						//display  pd intransit
						m.setPdCode(Utils.isNull(rs.getString("subinventory_code")));
						m.setPdDesc(Utils.isNull(rs.getString("subinventory_code")));
						m.setProductCode(Utils.isNull(rs.getString("product_code")));
						
						double pd_qty = (double) Math.round(rs.getDouble("pd_qty") * 100) / 100;
						double pd_int_qty = (double) Math.round(rs.getDouble("pd_int_qty") * 100) / 100;
						
						m.setPdQty(Utils.decimalFormat(pd_qty,Utils.format_current_2_disgit));
						m.setPdIntQty(Utils.decimalFormat(pd_int_qty,Utils.format_current_2_disgit));
						//key 
						keyMap = m.getPdCode()+"_"+m.getProductCode();
					}
				}else{
					//no display pd intransit
					if( Utils.isNull(o.getDispPlan()).equals("")){
						m.setProductCode(Utils.isNull(rs.getString("product_code")));
						m.setProductName(Utils.isNull(rs.getString("product_name")));
						m.setPdCode(Utils.isNull(rs.getString("subinventory_code")));
						
						double pd_qty = (double) Math.round(rs.getDouble("pd_qty") * 100) / 100;
						
						m.setPdQty(Utils.decimalFormat(pd_qty,Utils.format_current_2_disgit));
					    //key 
						keyMap = m.getProductCode()+"_"+m.getPdCode();
					}else{
						//display pd intransit
						m.setProductCode(Utils.isNull(rs.getString("product_code")));
						m.setProductName(Utils.isNull(rs.getString("product_name")));
						m.setPdCode(Utils.isNull(rs.getString("subinventory_code")));
						
						double pd_qty = (double) Math.round(rs.getDouble("pd_qty") * 100) / 100;
						double pd_int_qty = (double) Math.round(rs.getDouble("pd_int_qty") * 100) / 100;
						
						m.setPdQty(Utils.decimalFormat(pd_qty,Utils.format_current_2_disgit));
						m.setPdIntQty(Utils.decimalFormat(pd_int_qty,Utils.format_current_2_disgit));
					    
					    //key
					    keyMap = m.getProductCode()+"_"+m.getPdCode();
					}
				}
			
				map.put(keyMap, m);
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
		return map;
	} 
	
	public static StringBuffer genWhereCondSql(Connection conn,StockVanBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		
		if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
			sql.append("\n and s.region = '"+Utils.isNull(o.getSalesChannelNo())+"'");
		}
		if( !Utils.isNull(o.getPdType()).equals("")){
			sql.append("\n and subinv.subinventory Like '"+Utils.isNull(o.getPdType())+"%'");
		}
		if( !Utils.isNull(o.getPdCode()).equals("")){
			sql.append("\n and subinv.subinventory = '"+Utils.isNull(o.getPdCode())+"'");
		}
		if( !Utils.isNull(o.getBrand()).equals("")){
			sql.append("\n and P.brand = '"+Utils.isNull(o.getBrand())+"'");
		}
		if( !Utils.isNull(o.getProductCode()).equals("")){
			sql.append("\n and P.segment1 = '"+Utils.isNull(o.getProductCode()+"'"));
		}
		return sql;
	}
}
