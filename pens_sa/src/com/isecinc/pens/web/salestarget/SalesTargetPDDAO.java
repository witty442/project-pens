package com.isecinc.pens.web.salestarget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class SalesTargetPDDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static SalesTargetBean searchTargetPD(SalesTargetBean o,User user,String pageName ,boolean excel) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		SalesTargetBean h = null;
		Map<String, String> columnPDMap = new HashMap<String, String>();
		Map<String, String> rowProductMap = new HashMap<String, String>();
		Map<String, SalesTargetBean> dataMap = new HashMap<String, SalesTargetBean>();
		String keyDataMap ="";//itemCode+pdCode
		int rowId=0;
		try {
			//convert Criteria
			o = convertCriteria(o);
			if( !Utils.isNull(o.getDispCurrentStockPD()).equals("")){
				sql.append("\n select M.* ,S.PD_QTY ,PD.PD_STOCK_QTY ");
				sql.append("\n from (");
				sql.append("\n  select distinct M.inventory_item_code,M.inventory_item_desc ");
				sql.append("\n  ,M.subinventory,M.description ");
				sql.append("\n  from apps.xxpens_inv_pd_target_sales_v M");
				sql.append("\n  ,apps.xxpens_om_item_mst_v P ");
				sql.append("\n  where M.inventory_item_id =P.inventory_item_id");
				//genWhereSQL
				sql.append(genWhereSQL(o, user));
				
				sql.append("\n  union ");
				sql.append("\n  select distinct M.segment1 as inventory_item_code,M.description as inventory_item_desc ");
				sql.append("\n  ,M.subinventory_code as subinventory,M.name as description ");
				sql.append("\n  from apps.xxpens_inv_onhand_r00_v M");
				sql.append("\n  ,apps.xxpens_om_item_mst_v P ");
				sql.append("\n  WHERE M.inventory_item_id =P.inventory_item_id");
				sql.append("\n  and M.subinventory_code like 'P%'");//PD ONLY
				sql.append("\n  AND ROUND(M.primary_quantity,2) <> 0");
				if( !Utils.isNull(o.getSalesZone()).equals("")){
					 sql.append("\n  AND M.subinventory_code in(");
					 sql.append("\n    select subinv.subinventory from ");
					 sql.append("\n    apps.xxpens_inv_subinv_access subinv ");
					 sql.append("\n   ,apps.xxpens_salesreps_v s");
					 sql.append("\n   ,pensbi.XXPENS_BI_MST_SALES_ZONE  z");
					 sql.append("\n    where subinv.salesrep_id = s.salesrep_id");
					 sql.append("\n    and s.code = z.salesrep_code");
					 sql.append("\n    and z.zone = '"+Utils.isNull(o.getSalesZone())+"'");
					 sql.append("\n  )");
				}
				if( !Utils.isNull(o.getPdCode()).equals("")){
					sql.append("\n and M.subinventory_code in("+SQLHelper.converToTextSqlIn(o.getPdCode())+")");
				}
				if( !Utils.isNull(o.getBrand()).equals("")){
					sql.append("\n and P.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
				}
				if( !Utils.isNull(o.getItemCode()).equals("")){
					sql.append("\n and M.segment1 in("+SQLHelper.converToTextSqlIn(o.getItemCode())+")");
				}
				sql.append("\n ) M LEFT OUTER JOIN ");
				sql.append("\n ( select M.inventory_item_code,M.subinventory ");
				sql.append("\n  ,NVL(SUM(M.PD_QTY),0) as PD_QTY ");
				sql.append("\n   from apps.xxpens_inv_pd_target_sales_v M");
				sql.append("\n   ,apps.xxpens_om_item_mst_v P ");
				sql.append("\n   where M.inventory_item_id =P.inventory_item_id");
				//genWhereSQL
				sql.append(genWhereSQL(o, user));
				sql.append("\n   GROUP BY M.inventory_item_code,M.subinventory");
				sql.append("\n )S ON ");
				sql.append("\n M.inventory_item_code = S.inventory_item_code ");
				sql.append("\n and M.subinventory = S.subinventory ");
				
				sql.append("\n  LEFT OUTER JOIN ");
				sql.append("\n ( select M.segment1 as inventory_item_code ,M.subinventory_code as subinventory");
				sql.append("\n ,NVL(SUM(M.PRIMARY_QUANTITY),0) as PD_STOCK_QTY ");
				sql.append("\n   from apps.xxpens_inv_onhand_r00_v M");
				sql.append("\n  ,apps.xxpens_om_item_mst_v P ");
				sql.append("\n  WHERE M.inventory_item_id =P.inventory_item_id");
				sql.append("\n  and M.subinventory_code like 'P%'");//PD ONLY
				sql.append("\n  AND ROUND(M.primary_quantity,2) <> 0");
				//genWhereSQL
				if( !Utils.isNull(o.getSalesZone()).equals("")){
					 sql.append("\n  AND M.subinventory_code in(");
					 sql.append("\n    select subinv.subinventory from ");
					 sql.append("\n    apps.xxpens_inv_subinv_access subinv ");
					 sql.append("\n   ,apps.xxpens_salesreps_v s");
					 sql.append("\n   ,pensbi.XXPENS_BI_MST_SALES_ZONE  z");
					 sql.append("\n    where subinv.salesrep_id = s.salesrep_id");
					 sql.append("\n    and s.code = z.salesrep_code");
					 sql.append("\n    and z.zone = '"+Utils.isNull(o.getSalesZone())+"'");
					 sql.append("\n  )");
				}
				if( !Utils.isNull(o.getPdCode()).equals("")){
					sql.append("\n and M.subinventory_code in("+SQLHelper.converToTextSqlIn(o.getPdCode())+")");
				}
				if( !Utils.isNull(o.getBrand()).equals("")){
					sql.append("\n and P.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
				}
				if( !Utils.isNull(o.getItemCode()).equals("")){
					sql.append("\n and M.segment1 in("+SQLHelper.converToTextSqlIn(o.getItemCode())+")");
				}
				sql.append("\n   GROUP BY M.segment1,M.subinventory_code");
				sql.append("\n )PD ON ");
				sql.append("\n M.inventory_item_code = PD.inventory_item_code ");
				sql.append("\n and M.subinventory = PD.subinventory ");
				
			}else{
				sql.append("\n select M.inventory_item_code,M.inventory_item_desc ");
				sql.append("\n ,M.subinventory,M.description , NVL(SUM(M.PD_QTY),0) as PD_QTY");
				sql.append("\n from apps.xxpens_inv_pd_target_sales_v M");
				sql.append("\n ,apps.xxpens_om_item_mst_v P");
				sql.append("\n where M.inventory_item_id =P.inventory_item_id");
				//genWhereSQL
				sql.append(genWhereSQL(o, user));
				
				sql.append("\n GROUP BY M.inventory_item_id,M.inventory_item_code ");
				sql.append("\n ,M.inventory_item_desc,M.subinventory,M.description ");
				
			}
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   rowId++;
			   h = new SalesTargetBean();
			   h.setRowId(rowId);
			
			   h.setPdCode(rst.getString("subinventory"));  
			   h.setPdDesc(rst.getString("description"));  
			   h.setItemCode(rst.getString("inventory_item_code"));  
			   h.setItemName(rst.getString("inventory_item_desc"));  
			   if( !Utils.isNull(o.getDispRoundUp()).equalsIgnoreCase("")){
			       h.setPdQty(Utils.decimalFormatRoundUp(rst.getDouble("PD_QTY"), Utils.format_current_no_disgit));
			   }else{
				   h.setPdQty(Utils.decimalFormat(rst.getDouble("PD_QTY"), Utils.format_current_2_disgit,""));
			   }
			   
			   if( !Utils.isNull(o.getDispCurrentStockPD()).equals("")){
				   if( !Utils.isNull(o.getDispRoundUp()).equalsIgnoreCase("")){
					  h.setPdStockQty(Utils.decimalFormatRoundUp(rst.getDouble("PD_STOCK_QTY"), Utils.format_current_no_disgit));  
				   }else{
				      h.setPdStockQty(Utils.decimalFormat(rst.getDouble("PD_STOCK_QTY"), Utils.format_current_2_disgit,""));
				   }
	               //Map Column
				   if(  ( !h.getPdQty().equals("") && !h.getPdQty().equals("0.00") && !h.getPdQty().equals("-0.00")
						   && !h.getPdQty().equals("0") && !h.getPdQty().equals("-0") ) 
					 || ( !h.getPdStockQty().equals("") && !h.getPdStockQty().equals("0.00") && !h.getPdStockQty().equals("-0.00")
						   && !h.getPdStockQty().equals("0") && !h.getPdStockQty().equals("-0")) 
					 ){
				      columnPDMap.put(h.getPdCode(), h.getPdDesc());
				      rowProductMap.put(h.getItemCode(), h.getItemName());
				   }
			   }else{
	               //Map Column
				   if(!h.getPdQty().equals("") && !h.getPdQty().equals("0.00") && !h.getPdQty().equals("-0.00")
					  && !h.getPdQty().equals("0") && !h.getPdQty().equals("-0")	   ){
				      columnPDMap.put(h.getPdCode(), h.getPdDesc());
				      rowProductMap.put(h.getItemCode(), h.getItemName());
				   }
			   }//if
			   
			   keyDataMap = h.getItemCode()+"-"+h.getPdCode();
			   dataMap.put(keyDataMap, h);
			}//while
			
			/*** Sort column and Row Map  ***/
			//Sort pdCode asc to List
			List<SalesTargetBean> columnPDList = new ArrayList<SalesTargetBean>();
			Map<String, String> columnPDMapSortMap = new TreeMap<String, String>(columnPDMap);
			Iterator<String> its = columnPDMapSortMap.keySet().iterator();
			SalesTargetBean b =null;
			while(its.hasNext()){
				String key = (String)its.next();
				String value = columnPDMapSortMap.get(key);
				//logger.debug("no["+r+"]key["+key+"]value["+value+"]");
				b = new SalesTargetBean();
				b.setPdCode(key);
				b.setPdDesc(value);
				//logger.debug("debug P1245 :pdCode:"+b.getPdCode());
				columnPDList.add(b);
			}
			
			//Sort Row Product asc To List
			List<SalesTargetBean> rowProductList = new ArrayList<SalesTargetBean>();
			Map<String, String> rowProductSortMap = new TreeMap<String, String>(rowProductMap);
			its = rowProductSortMap.keySet().iterator();
			while(its.hasNext()){
				String key = (String)its.next();
				String value = rowProductSortMap.get(key);
				//logger.debug("no["+r+"]key["+key+"]value["+value+"]");
				b = new SalesTargetBean();
				b.setItemCode(key);
				b.setItemName(value);
				rowProductList.add(b);
			}
			
			//Get To HTML to Display
			if(rowId >0){
			   o.setDataStrBuffer(SalesTargetPDExport.genResultSearchTargetPD(o,columnPDList,rowProductList,dataMap,excel));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	public static StringBuffer genWhereSQL(SalesTargetBean o,User user){
		StringBuffer sql = new StringBuffer();
		if( !Utils.isNull(o.getTargetMonth()).equals("")){
			sql.append("\n and M.invoice_month = '"+Utils.isNull(o.getTargetMonth())+"'");
		}
		if( !Utils.isNull(o.getTargetYear()).equals("")){
			sql.append("\n and M.invoice_year = '"+Utils.isNull(o.getTargetYear())+"'");
		}
		if( !Utils.isNull(o.getPdCode()).equals("")){
			sql.append("\n and M.subinventory in("+SQLHelper.converToTextSqlIn(o.getPdCode())+")");
		}
		if( !Utils.isNull(o.getBrand()).equals("")){
			sql.append("\n and P.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
		}
		if( !Utils.isNull(o.getItemCode()).equals("")){
			sql.append("\n and M.inventory_item_code in("+SQLHelper.converToTextSqlIn(o.getItemCode())+")");
		}
		if( !Utils.isNull(o.getSalesZone()).equals("")){
			 sql.append("\n  and M.salesrep_id in( ");
			 sql.append("\n  select salesrep_id from PENSBI.XXPENS_BI_MST_SALES_ZONE ");
			 sql.append("\n  where  ZONE='"+o.getSalesZone()+"'");
			 sql.append("\n  )");
		}
		//filter salesrep by user login
		if (SalesTargetPDControlPage.isUserMapCustSalesTT(user)){
			 sql.append("\n  and M.salesrep_id in( ");
			 sql.append("\n  select salesrep_id from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT ");
			 sql.append("\n  where  user_name='"+user.getUserName()+"'");
			 sql.append("\n  )");
		}
		return sql;
	}
	public static SalesTargetBean convertCriteria(SalesTargetBean o) throws Exception{
		//logger.debug("startDate:"+o.getStartDate());
		Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MMM_YYYY);
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		//set Month
		String month =String.valueOf(c.get(Calendar.MONTH)+1);
		o.setTargetMonth(month.length()==1?"0"+month:month);
        //Set Year
		o.setTargetYear(c.get(Calendar.YEAR)+"");
		//set quarter
		if(Integer.parseInt(month) >= 1&& Integer.parseInt(month) <=3){
			o.setTargetQuarter("1");
		}else if(Integer.parseInt(month) >= 4&& Integer.parseInt(month) <=6){
			o.setTargetQuarter("2");
		}else if(Integer.parseInt(month) >= 7&& Integer.parseInt(month) <=9){
			o.setTargetQuarter("3");
		}else{
			o.setTargetQuarter("4");
		}
		
		return o;
	}
}
