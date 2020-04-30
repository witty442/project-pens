package com.isecinc.pens.web.stockonhand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class StockVanDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static List<StockOnhandBean> searchColumnList(Connection conn,StockOnhandBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<StockOnhandBean> columnList = new ArrayList<StockOnhandBean>();
		StockOnhandBean columnBean = null;
		try {
			//Case 1  แนวตั้ง : PD/หน่วยรถ    แนวนอน :  รหัสสินค้า  
			if("1".equals(o.getDispType())){
				//no display pd intransit
				if( Utils.isNull(o.getDispPlan()).equals("")){
					sql.append("\n  SELECT distinct p.segment1 as column_code from ");
					sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd.inventory_item_id = p.inventory_item_id");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o,"pd"));
					sql.append("\n  ORDER BY p.segment1 ");
				}else{
					sql.append("\n SELECT distinct p.column_code FROM (");
					sql.append("\n  SELECT distinct p.segment1 as column_code from ");
					sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd.inventory_item_id = p.inventory_item_id");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o,"pd"));
					
					sql.append("\n  UNION ALL");
					
					sql.append("\n  SELECT distinct p.segment1 as column_code from ");
					sql.append("\n  apps.xxpens_inv_intransit_r00_v pd_int");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd_int.inventory_item_id = p.inventory_item_id");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o,"pd_int"));
					sql.append("\n ) p ");
					
					sql.append("\n  ORDER BY p.column_code ");
				}
			//Case 2  แนวตั้ง : รหัสสินค้า      แนวนอน :    PD/หน่วยรถ
			}else if("2".equals(o.getDispType())){
				//no display pd intransit
				if( Utils.isNull(o.getDispPlan()).equals("")){
					sql.append("\n  SELECT distinct pd.subinventory_code as column_code,pd.name as subinv_name ");
					sql.append("\n  ,province from ");
					sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd.inventory_item_id = p.inventory_item_id");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o,"pd"));
					sql.append("\n  ORDER BY pd.subinventory_code ");
				
				//display pd intransit
				}else{
					sql.append("\n SELECT DISTINCT A.column_code,A.subinv_name");
					sql.append("\n FROM ( ");
					sql.append("\n  SELECT distinct pd.subinventory_code as column_code,pd.name as subinv_name ");
					sql.append("\n  ,province from ");
					sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd.inventory_item_id = p.inventory_item_id");
					sql.append("\n  AND ROUND(pd.primary_quantity,2) <> 0");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o,"pd"));
				
					sql.append("\n  UNION  ");
					
					sql.append("\n  SELECT distinct pd_int.to_subinventory as column_code,pd_int.name as subinv_name ");
					sql.append("\n  ,province from ");
					sql.append("\n  apps.xxpens_inv_intransit_r00_v pd_int");
					sql.append("\n ,apps.xxpens_om_item_mst_v p");
					sql.append("\n  WHERE pd_int.inventory_item_id = p.inventory_item_id");
					sql.append("\n  AND ROUND(pd_int.quantity_intransit,2) <> 0");
					 //GenWhereSQL
					sql.append(" "+genWhereCondSql(conn,o,"pd_int"));
					sql.append("\n  )A");
					sql.append("\n  ORDER BY A.column_code ");
				}
			}
			logger.debug("sql:"+sql);
			//Display by SalesZone
            if("3".equals(o.getDispType())){
            	for(int i=0;i<=4;i++){
            		columnBean = new StockOnhandBean();
            		columnBean.setSalesZone(i+"");
            		columnBean.setSalesZoneName(i+"");
            		columnList.add(columnBean);
            	}//for
			}else{
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				while(rst.next()) {
					columnBean = new StockOnhandBean();
					if("1".equals(o.getDispType())){
						columnBean.setProductCode(Utils.isNull(rst.getString("column_code")));
					}else if("2".equals(o.getDispType())){
						//no display pd intransit
						if( Utils.isNull(o.getDispPlan()).equals("")){
						   columnBean.setPdCode(Utils.isNull(rst.getString("column_code")));
						   columnBean.setPdDesc(Utils.isNull(rst.getString("subinv_name")));
						}else{
						   //display pd intransit
							columnBean.setPdCode(Utils.isNull(rst.getString("column_code")));
							columnBean.setPdDesc(Utils.isNull(rst.getString("subinv_name")));
							columnBean.setPdCodeIntransit(Utils.isNull(rst.getString("column_code")));
						}
					}
					columnList.add(columnBean);
				}//while
			}
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

	public static List<StockOnhandBean> searchStockVanList(Connection conn,StockOnhandBean o ,List<StockOnhandBean> columnList) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockOnhandBean rowItem = null;
		List<StockOnhandBean> items = new ArrayList<StockOnhandBean>();
		List<StockOnhandBean> rowColumnDataList = new ArrayList<StockOnhandBean>();
		StockOnhandBean columnBean = null;
		StockOnhandBean columnDataBean = null;
		String keyMap = "";
		try {
			if( !Utils.isNull(o.getDispType()).equals("3")){
				sql.append("\n select A.* from (");
				/******************************************************************************/
				sql.append("\n /** MASTER **/ ");
				sql.append("\n  SELECT distinct ");
				//1 row by pd
				if( Utils.isNull(o.getDispType()).equals("1")){
					sql.append("\n  pd.subinventory_code ,pd.name as subinv_name");
					sql.append("\n  ,pd.province ");
				}else{
					//2 row by product
				   sql.append("\n  p.segment1 as product_code ");
				   sql.append("\n  ,p.description as product_name ");
				}
				sql.append("\n  from ");
				sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n  WHERE pd.inventory_item_id = p.inventory_item_id");
				sql.append("\n  AND ROUND(pd.primary_quantity,2) <> 0");
				 //GenWhereSQL
				sql.append(" "+genWhereCondSql(conn,o,"pd"));
				
				sql.append("\n  UNION ");
				
				sql.append("\n  SELECT distinct ");
				//1 row by pd
				if( Utils.isNull(o.getDispType()).equals("1")){
					sql.append("\n  pd_int.to_subinventory as subinventory_code,pd_int.name as subinv_name");
					sql.append("\n  ,pd_int.province ");
				}else{
					//2 row by product
				   sql.append("\n  p.segment1 as product_code ");
				   sql.append("\n  ,p.description as product_name ");
				}
				sql.append("\n  from ");
				sql.append("\n  apps.xxpens_inv_intransit_r00_v pd_int");
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n  WHERE pd_int.inventory_item_id = p.inventory_item_id");
				sql.append("\n  AND ROUND(pd_int.quantity_intransit,2) <> 0");
				 //GenWhereSQL
				sql.append(" "+genWhereCondSql(conn,o,"pd_int"));
				
				/*******************************************************************************/
				sql.append("\n   )A ");
				
			}else if(Utils.isNull(o.getDispType()).equals("3")){
				sql.append("\n select M.segment1 as product_code ,M.description as product_name");
				sql.append("\n from xxpens_inv_onhand_r00_zone_v M");
				sql.append("\n ,apps.xxpens_om_item_mst_v P");
				sql.append("\n WHERE M.inventory_item_id = p.inventory_item_id");
				if( !Utils.isNull(o.getBrand()).equals("")){
					sql.append("\n and P.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
				}
				if( !Utils.isNull(o.getProductCode()).equals("")){
					sql.append("\n and P.segment1 in("+SQLHelper.converToTextSqlIn(o.getProductCode())+")");
				}
			}
			logger.debug("sql:"+sql);
			
			//Get DataAllMap to Map 
			Map<String, StockOnhandBean> dataAllMap = getDataAllByColumnCodeToMap(conn, o);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   rowItem = new StockOnhandBean();
			  
			   //1 row by pd ,column product
			   if( Utils.isNull(o.getDispType()).equals("1")){
				   rowItem.setPdCode(Utils.isNull(rst.getString("subinventory_code")));
				   rowItem.setPdDesc(Utils.isNull(rst.getString("subinv_name")));
				   rowItem.setProvince(Utils.isNull(rst.getString("province")));
			   }else if(Utils.isNull(o.getDispType()).equals("2")){
				  //2 row by product ,column by pdcode
				   rowItem.setProductCode(Utils.isNull(rst.getString("product_code")));
				   rowItem.setProductName(Utils.isNull(rst.getString("product_name")));
			   }else if(Utils.isNull(o.getDispType()).equals("3")){
				   rowItem.setProductCode(Utils.isNull(rst.getString("product_code")));
				   rowItem.setProductName(Utils.isNull(rst.getString("product_name")));
			   }
			   
			   if(columnList != null && columnList.size() >0){
				   rowColumnDataList = new ArrayList<StockOnhandBean>();
				   for(int c =0;c<columnList.size();c++){
					   columnBean = (StockOnhandBean)columnList.get(c);
					   
					   //1 row by pd ,column product
					   if( Utils.isNull(o.getDispType()).equals("1")){
						   keyMap =  rowItem.getPdCode()+"_"+columnBean.getProductCode();
					   }else if( Utils.isNull(o.getDispType()).equals("2")){
						 //2 row by product ,column by pdcode
						   keyMap =  rowItem.getProductCode()+"_"+columnBean.getPdCode();
					   }else if( Utils.isNull(o.getDispType()).equals("3")){
						  //3 row by product ,column by SalesZone
						   keyMap =  rowItem.getProductCode()+"_"+columnBean.getSalesZone();
					   }
					   
					    columnDataBean = dataAllMap.get(keyMap)!=null?(StockOnhandBean)dataAllMap.get(keyMap):null;
					   // logger.debug("keyMap["+keyMap+"]["+columnDataBean.getSalesZoneQty()+"]");
					    
					    if(columnDataBean!= null){
							//found in map
						}else{
							//set blank data bean
							columnDataBean = columnBean;
							columnDataBean.setPdQty("");
							columnDataBean.setPdIntQty("");
							//for Disp 3
							columnDataBean.setSalesZoneQty("");
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
	
	public static Map<String,StockOnhandBean> getDataAllByColumnCodeToMap(Connection conn,StockOnhandBean o) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String,StockOnhandBean> map = new HashMap<String,StockOnhandBean>();
		StringBuffer sql = new StringBuffer("");
		try{
			
			
			if( !"3".equals(o.getDispType())){
				sql.append("\n select M.product_code,M.product_name");
				sql.append("\n ,M.subinventory_code  ,M.province ");
				sql.append("\n ,PD.pd_qty");
				sql.append("\n ,(NVL(PD.pd_qty,0) * NVL(M.unit_price,0)) as pd_price");
				sql.append("\n ,PD_INT.pd_int_qty");
				sql.append("\n ,(NVL(PD_INT.pd_int_qty,0) * NVL(M.unit_price,0)) as pd_int_price");
				sql.append("\n FROM (");
				sql.append("\n /** MASTER **/ ");
				sql.append("\n  SELECT distinct pd.inventory_item_id ");
				sql.append("\n  ,p.segment1 as product_code ");
				sql.append("\n  ,p.description as product_name ");
				sql.append("\n  ,pd.subinventory_code ");
				sql.append("\n  ,(select unit_price from apps.xxpens_om_price_list_cs_v pr ");
				sql.append("\n    where pr.inventory_item_id = p.inventory_item_id) as unit_price ");
				sql.append("\n  ,pd.province  from ");
				sql.append("\n  apps.xxpens_inv_onhand_r00_v pd");
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n  WHERE pd.inventory_item_id = p.inventory_item_id");
				sql.append("\n  AND ROUND(pd.primary_quantity,2) <> 0");
				 //GenWhereSQL
				sql.append(" "+genWhereCondSql(conn,o,"pd"));
				
				sql.append("\n  UNION ");
				
				sql.append("\n  SELECT distinct pd_int.inventory_item_id ");
				sql.append("\n  ,p.segment1 as product_code ");
				sql.append("\n  ,p.description as product_name ");
				sql.append("\n  ,pd_int.to_subinventory as subinventory_code ");
				sql.append("\n  ,(select unit_price from apps.xxpens_om_price_list_cs_v pr ");
				sql.append("\n    where pr.inventory_item_id = p.inventory_item_id) as unit_price ");
				sql.append("\n  ,pd_int.province from ");
				sql.append("\n  apps.xxpens_inv_intransit_r00_v pd_int");
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n  WHERE pd_int.inventory_item_id = p.inventory_item_id");
				sql.append("\n  AND ROUND(pd_int.quantity_intransit,2) <> 0");
				 //GenWhereSQL
				sql.append(" "+genWhereCondSql(conn,o,"pd_int"));
				
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
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n  WHERE pd.inventory_item_id = p.inventory_item_id");
				sql.append("\n  AND ROUND(pd.primary_quantity,2) <> 0");
				 //GenWhereSQL
				sql.append(" "+genWhereCondSql(conn,o,"pd"));
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
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n  WHERE pd_int.inventory_item_id = p.inventory_item_id");
				sql.append("\n  AND ROUND(pd_int.quantity_intransit,2) <> 0");
				 //GenWhereSQL
				sql.append(" "+genWhereCondSql(conn,o,"pd_int"));
				sql.append("\n )PD_INT ON M.inventory_item_id = PD_INT.inventory_item_id ");
				sql.append("\n  AND M.subinventory_code = PD_INT.subinventory_code");
			 //display all by Sales Zone
			}else if("3".equals(o.getDispType())){
				sql.append("\n select M.segment1 as product_code ,M.description as product_name");
				sql.append("\n ,M.bangkok_west");
				sql.append("\n ,M.bangkok_east");
				sql.append("\n ,M.north");
				sql.append("\n ,M.north_east");
				sql.append("\n ,M.south");
				sql.append("\n from xxpens_inv_onhand_r00_zone_v M");
				sql.append("\n ,apps.xxpens_om_item_mst_v P");
				sql.append("\n WHERE M.inventory_item_id = p.inventory_item_id");
				if( !Utils.isNull(o.getBrand()).equals("")){
					sql.append("\n and P.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
				}
				if( !Utils.isNull(o.getProductCode()).equals("")){
					sql.append("\n and P.segment1 in("+SQLHelper.converToTextSqlIn(o.getProductCode())+")");
				}
			}
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			String keyMap = "";
			while(rs.next()){
				StockOnhandBean m = new StockOnhandBean();
			
				if("1".equals(o.getDispType())){
					//no display pd intransit
					if( Utils.isNull(o.getDispPlan()).equals("")){
					   m.setPdCode(Utils.isNull(rs.getString("subinventory_code")));
					   m.setPdDesc(Utils.isNull(rs.getString("subinventory_code")));
					   m.setProvince(Utils.isNull(rs.getString("province")));
					   m.setProductCode(Utils.isNull(rs.getString("product_code")));
					   
					   double pd_qty = (double) Math.round(rs.getDouble("pd_qty") * 100) / 100;
					   m.setPdQty(Utils.decimalFormat(pd_qty,Utils.format_current_2_disgit));
					   
					   double pd_price = (double) Math.round(rs.getDouble("pd_price") * 100) / 100;
					   m.setPdPrice(Utils.decimalFormat(pd_price,Utils.format_current_2_disgit));
					   //key
					   keyMap = m.getPdCode()+"_"+m.getProductCode();
					}else{
						//display  pd intransit
						m.setPdCode(Utils.isNull(rs.getString("subinventory_code")));
						m.setPdDesc(Utils.isNull(rs.getString("subinventory_code")));
						m.setProvince(Utils.isNull(rs.getString("province")));
						m.setProductCode(Utils.isNull(rs.getString("product_code")));
						
						double pd_qty = (double) Math.round(rs.getDouble("pd_qty") * 100) / 100;
						double pd_price = (double) Math.round(rs.getDouble("pd_price") * 100) / 100;
						double pd_int_qty = (double) Math.round(rs.getDouble("pd_int_qty") * 100) / 100;
						double pd_int_price = (double) Math.round(rs.getDouble("pd_int_price") * 100) / 100;
						
						m.setPdQty(Utils.decimalFormat(pd_qty,Utils.format_current_2_disgit));
						m.setPdPrice(Utils.decimalFormat(pd_price,Utils.format_current_2_disgit));
						m.setPdIntQty(Utils.decimalFormat(pd_int_qty,Utils.format_current_2_disgit));
						m.setPdIntPrice(Utils.decimalFormat(pd_int_price,Utils.format_current_2_disgit));
						
						//key 
						keyMap = m.getPdCode()+"_"+m.getProductCode();
					}
				}else if("2".equals(o.getDispType())){
					//no display pd intransit
					if( Utils.isNull(o.getDispPlan()).equals("")){
						m.setProductCode(Utils.isNull(rs.getString("product_code")));
						m.setProductName(Utils.isNull(rs.getString("product_name")));
						m.setPdCode(Utils.isNull(rs.getString("subinventory_code")));
						
						double pd_qty = (double) Math.round(rs.getDouble("pd_qty") * 100) / 100;
						m.setPdQty(Utils.decimalFormat(pd_qty,Utils.format_current_2_disgit));
						
						double pd_price = (double) Math.round(rs.getDouble("pd_price") * 100) / 100;
						m.setPdPrice(Utils.decimalFormat(pd_price,Utils.format_current_2_disgit));
						
					    //key 
						keyMap = m.getProductCode()+"_"+m.getPdCode();
					}else{
						//display pd intransit
						m.setProductCode(Utils.isNull(rs.getString("product_code")));
						m.setProductName(Utils.isNull(rs.getString("product_name")));
						m.setPdCode(Utils.isNull(rs.getString("subinventory_code")));
						
						double pd_qty = (double) Math.round(rs.getDouble("pd_qty") * 100) / 100;
						double pd_price = (double) Math.round(rs.getDouble("pd_price") * 100) / 100;
						double pd_int_qty = (double) Math.round(rs.getDouble("pd_int_qty") * 100) / 100;
						double pd_int_price = (double) Math.round(rs.getDouble("pd_int_price") * 100) / 100;
						
						m.setPdQty(Utils.decimalFormat(pd_qty,Utils.format_current_2_disgit));
						m.setPdPrice(Utils.decimalFormat(pd_price,Utils.format_current_2_disgit));
						m.setPdIntQty(Utils.decimalFormat(pd_int_qty,Utils.format_current_2_disgit));
						m.setPdIntPrice(Utils.decimalFormat(pd_int_price,Utils.format_current_2_disgit));
					    
					    //key
					    keyMap = m.getProductCode()+"_"+m.getPdCode();
					}
				}else if("3".equals(o.getDispType())){
					/***** 0 ******************/
					m = new StockOnhandBean();
					m.setProductCode(Utils.isNull(rs.getString("product_code")));
					m.setProductName(Utils.isNull(rs.getString("product_name")));
					m.setSalesZone("0");
					double salesZoneQty = (double) Math.round(rs.getDouble("bangkok_West") * 100) / 100;
					m.setSalesZoneQty(Utils.decimalFormat(salesZoneQty,Utils.format_current_2_disgit));
					keyMap = m.getProductCode()+"_"+m.getSalesZone();
					logger.debug("set KeyMap["+keyMap+"]["+m.getSalesZoneQty()+"]");
					map.put(keyMap, m);
					
					/***** 1 ******************/
					m = new StockOnhandBean();
					m.setProductCode(Utils.isNull(rs.getString("product_code")));
					m.setProductName(Utils.isNull(rs.getString("product_name")));
					m.setSalesZone("1");
					salesZoneQty = (double) Math.round(rs.getDouble("bangkok_east") * 100) / 100;
					m.setSalesZoneQty(Utils.decimalFormat(salesZoneQty,Utils.format_current_2_disgit));
					keyMap = m.getProductCode()+"_"+m.getSalesZone();
					logger.debug("set KeyMap["+keyMap+"]["+m.getSalesZoneQty()+"]");
					map.put(keyMap, m);
					
					/***** 2 ******************/
					m = new StockOnhandBean();
					m.setProductCode(Utils.isNull(rs.getString("product_code")));
					m.setProductName(Utils.isNull(rs.getString("product_name")));
					m.setSalesZone("2");
					salesZoneQty = (double) Math.round(rs.getDouble("north") * 100) / 100;
					m.setSalesZoneQty(Utils.decimalFormat(salesZoneQty,Utils.format_current_2_disgit));
					keyMap = m.getProductCode()+"_"+m.getSalesZone();
					logger.debug("set KeyMap["+keyMap+"]["+m.getSalesZoneQty()+"]");
					map.put(keyMap, m);
					
					/***** 3 ******************/
					m = new StockOnhandBean();
					m.setProductCode(Utils.isNull(rs.getString("product_code")));
					m.setProductName(Utils.isNull(rs.getString("product_name")));
					m.setSalesZone("3");
					salesZoneQty = (double) Math.round(rs.getDouble("north_east") * 100) / 100;
					m.setSalesZoneQty(Utils.decimalFormat(salesZoneQty,Utils.format_current_2_disgit));
					keyMap = m.getProductCode()+"_"+m.getSalesZone();
					logger.debug("set KeyMap["+keyMap+"]["+m.getSalesZoneQty()+"]");
					map.put(keyMap, m);
					
					/***** 4 ******************/
					m = new StockOnhandBean();
					m.setProductCode(Utils.isNull(rs.getString("product_code")));
					m.setProductName(Utils.isNull(rs.getString("product_name")));
					m.setSalesZone("4");
					salesZoneQty = (double) Math.round(rs.getDouble("south") * 100) / 100;
					m.setSalesZoneQty(Utils.decimalFormat(salesZoneQty,Utils.format_current_2_disgit));
					keyMap = m.getProductCode()+"_"+m.getSalesZone();
					logger.debug("set KeyMap["+keyMap+"]["+m.getSalesZoneQty()+"]");
					map.put(keyMap, m);
				
				}//if
				//Exception Case Disp Type 3
				if( !"3".equals(o.getDispType())){
					map.put(keyMap, m);
				}
			}//for
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
	
	public static StringBuffer genWhereCondSql(Connection conn,StockOnhandBean o,String symnoname) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if( !Utils.isNull(o.getPdType()).equals("")){
			if(symnoname.equalsIgnoreCase("pd_int")){
			   sql.append("\n and "+symnoname+".to_subinventory Like '"+Utils.isNull(o.getPdType())+"%'");
			}else{
			   sql.append("\n and "+symnoname+".subinventory_code Like '"+Utils.isNull(o.getPdType())+"%'");
			}
		}
		if( !Utils.isNull(o.getPdCode()).equals("")){
			if(symnoname.equalsIgnoreCase("pd_int")){
			    sql.append("\n and "+symnoname+".to_subinventory in("+SQLHelper.converToTextSqlIn(o.getPdCode())+")");
			}else{
				sql.append("\n and "+symnoname+".subinventory_code in("+SQLHelper.converToTextSqlIn(o.getPdCode())+")");
			}
		}
		if( !Utils.isNull(o.getBrand()).equals("")){
			sql.append("\n and P.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
		}
		if( !Utils.isNull(o.getProductCode()).equals("")){
			sql.append("\n and P.segment1 in("+SQLHelper.converToTextSqlIn(o.getProductCode())+")");
		}
		if( !Utils.isNull(o.getSalesZone()).equals("") || !Utils.isNull(o.getSalesChannelNo()).equals("")){
			if(symnoname.equalsIgnoreCase("pd_int")){
				sql.append("\n  AND "+symnoname+".to_subinventory in(");
			}else{
			    sql.append("\n  AND "+symnoname+".subinventory_code in(");
			}
			 sql.append("\n    select subinv.subinventory from ");
			 sql.append("\n    apps.xxpens_inv_subinv_access subinv ");
			 sql.append("\n   ,apps.xxpens_salesreps_v s");
			 sql.append("\n   ,pensbi.XXPENS_BI_MST_SALES_ZONE  z");
			 sql.append("\n    where subinv.salesrep_id = s.salesrep_id");
			 sql.append("\n    and s.code = z.salesrep_code");
			 if(!Utils.isNull(o.getSalesChannelNo()).equals("")){
			     sql.append("\n    and s.region = '"+Utils.isNull(o.getSalesChannelNo())+"'");
			 }
			 if( !Utils.isNull(o.getSalesZone()).equals("")){
			     sql.append("\n    and z.zone = '"+Utils.isNull(o.getSalesZone())+"'");
			 }
			 sql.append("\n  )");
		}
		return sql;
	}
}
