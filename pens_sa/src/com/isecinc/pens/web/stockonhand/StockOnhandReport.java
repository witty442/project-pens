package com.isecinc.pens.web.stockonhand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import util.DBConnection;
import util.DateToolsUtil;
import util.ExcelHeader;
import util.SQLHelper;
import util.Utils;

public class StockOnhandReport {
	protected static Logger logger = Logger.getLogger("PENS");
	private static Map<String, String> COLUMNNAME_MAP = new HashMap<String, String>();
	static{
		COLUMNNAME_MAP.put("BRAND", "Brand");
		COLUMNNAME_MAP.put("SUBINVENTORY_CODE", "SubInv");
		COLUMNNAME_MAP.put("SEGMENT1", "SKU");
		COLUMNNAME_MAP.put("DESCRIPTION", "DESCRIPTION");
		COLUMNNAME_MAP.put("PRIMARY_QUANTITY", "ONHAND QTY");
		COLUMNNAME_MAP.put("PRIMARY_UOM_CODE", "Primary UOM");
	}
	public static StockOnhandBean searchOnhandReport(String contextPath ,StockOnhandBean o,boolean excel){
	    logger.debug("excel:"+excel);
		if(o.getItemsList() !=null && o.getItemsList().size()>0){
			logger.debug("itemList:"+o.getItemsList().size());
			return searchOnhandReportModelCaseSort(contextPath,o,excel);
		}
		
		return searchOnhandReportModel(contextPath,o,excel);
	}
	public static StockOnhandBean searchOnhandReportModel(String contextPath,StockOnhandBean o,boolean excel){
		StockOnhandBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String[] columnNameArr = null;
		String columnSql = "";
		String columnGroupBy = "";
		String columnOrderBy = "";
		StringBuffer html = null;
		int r = 0;
		List<StockOnhandBean> itemList = new ArrayList<StockOnhandBean>();
		double totalOnhandQty = 0;
		try{
			//create connection
			conn = DBConnection.getInstance().getConnection();
			
			//split column array
			columnNameArr = o.getReportType().split("\\,");
			String[] columnAllArr = genSelectColumnName(columnNameArr);
			columnSql = columnAllArr[0];
			columnGroupBy = columnAllArr[1];
			
			//add onhand_qty ,uom_code 
			columnSql +=",PRIMARY_UOM_CODE ";
			columnSql +=",NVL(SUM(PRIMARY_QUANTITY),0) as PRIMARY_QUANTITY";
	
			//add uom_code 
			columnGroupBy +=",PRIMARY_UOM_CODE ";
			
			//orderby
			columnOrderBy += o.getReportType();
			
			sql.append("\n  SELECT "+columnSql);
			sql.append("\n  FROM (");
			sql.append("\n    SELECT ");
			sql.append("\n    SUBINVENTORY_CODE,NAME,SUBSTR(SEGMENT1,1,3) as brand ,SEGMENT1 ");
			sql.append("\n    ,DESCRIPTION,PRIMARY_QUANTITY,PRIMARY_UOM_CODE ");
			sql.append("\n    FROM apps.xxpens_inv_onhand_b00_v M ");
			sql.append("\n    where organization_code = '"+o.getOrgCode()+"'");
			
			//for test
			//sql.append("\n    and rownum =1");
			
			sql.append("\n  )M ");
			sql.append("\n WHERE 1=1 ");
			
			//only have qty
			if(!Utils.isNull(o.getDispHaveQty()).equals("")){
				sql.append("\n and M.PRIMARY_QUANTITY <> 0 ");
			}
		     //subInv
			if( !Utils.isNull(o.getSubInv()).equals("")){
				sql.append("\n and M.SUBINVENTORY_CODE in("+SQLHelper.converToTextSqlIn(o.getSubInv())+")");
			}
			 //Brand
			if( !Utils.isNull(o.getBrand()).equals("")){
		    	sql.append("\n  AND M.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
			}
			//Sku
			if( !Utils.isNull(o.getProductCode()).equals("")){
				sql.append("\n and M.segment1 in("+SQLHelper.converToTextSqlIn(o.getProductCode())+")");
			}
			
			sql.append("\n GROUP BY "+columnGroupBy );
			sql.append("\n ORDER BY "+columnOrderBy);
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new StockOnhandBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(contextPath,o,excel,columnNameArr));
			  }
			  
			  for(int i=0;i<columnNameArr.length;i++){
				   if("BRAND".equalsIgnoreCase(columnNameArr[i])){
						item.setBrand(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setBrandName(Utils.isNull(rst.getString("BRAND_NAME")));
					}else if("SUBINVENTORY_CODE".equalsIgnoreCase(columnNameArr[i])){
						item.setSubInv(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setSubInvDesc(Utils.isNull(rst.getString("NAME")));
					}else if("SEGMENT1".equalsIgnoreCase(columnNameArr[i])){
						item.setProductCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setProductName(Utils.isNull(rst.getString("DESCRIPTION")));
					}
			  }//for
			 
			  item.setOnhandQty(Utils.decimalFormat(rst.getDouble("PRIMARY_QUANTITY"), Utils.format_current_no_disgit)); 
			  item.setPriUomCode(Utils.isNull(rst.getString("PRIMARY_UOM_CODE")));
			 
			  //add to List
			  itemList.add(item);
			  
			  //Gen Row Table
			  html.append(genRowTable(o,excel,columnNameArr,item));
			  //Summary
			  totalOnhandQty +=rst.getDouble("PRIMARY_QUANTITY");
			  
			}//while
			
			//Check Execute Found data
			if(r>0){
			  // Get Total
			   html.append(genTotalTable(o,excel,columnNameArr, totalOnhandQty));
			  // gen end Table
			  html.append("</table>");
			}
			
			o.setItemsList(itemList);
			o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	
	private static String[] genSelectColumnName(String[] columnNameArr) throws Exception{
		String[] sqlAll = new String[2];
		String sql = "";
		String columnGroupBy ="";
		for(int i=0;i<columnNameArr.length;i++){
		  if("Brand".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.BRAND,";
			  sql +="\n (select x.brand_desc from PENSBI.XXPENS_BI_MST_BRAND X WHERE X.brand_no = M.brand) as BRAND_NAME,";
			  columnGroupBy +="\n M.brand,";
		  }else if("SUBINVENTORY_CODE".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.SUBINVENTORY_CODE,";
			  sql +="\n M.NAME,";
			  columnGroupBy +="\n M.SUBINVENTORY_CODE ,";
			  columnGroupBy +="\n M.NAME,";
		  }else if("SEGMENT1".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.SEGMENT1,";
			  sql +="\n M.DESCRIPTION,";
			  columnGroupBy +="\n M.SEGMENT1 ,M.DESCRIPTION,";
		  }
		}//for
		
		//remove "," in last position
		 sql = sql.substring(0,sql.length()-1);
	     columnGroupBy = columnGroupBy.substring(0,columnGroupBy.length()-1);
		
		sqlAll[0] = sql;
		sqlAll[1] = columnGroupBy;
	  return sqlAll;
	}
	
	public static StockOnhandBean searchOnhandReportModelCaseSort(String contextPath,StockOnhandBean o,boolean excel){
		StockOnhandBean item = null;
		String[] columnNameArr = null;
		StringBuffer html = null;
		int r = 0;
		List<StockOnhandBean> itemList = o.getItemsList();
		double totalOnhandQty = 0;
		try{
			logger.debug("searchReportModelCaseSort");
			logger.debug("columnNameSort:"+o.getColumnNameSort());
			logger.debug("orderSortType:"+o.getOrderSortType());
			
			//sort by Column
			if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("BRAND")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockOnhandBean.Comparators.BRAND_DESC);
				}else{
				   Collections.sort(itemList, StockOnhandBean.Comparators.BRAND_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("SUBINVENTORY_CODE")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockOnhandBean.Comparators.SUBINVENTORY_CODE_DESC);
				}else{
				   Collections.sort(itemList, StockOnhandBean.Comparators.SUBINVENTORY_CODE_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("SEGMENT1")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockOnhandBean.Comparators.SEGMENT1_DESC);
				}else{
				   Collections.sort(itemList, StockOnhandBean.Comparators.SEGMENT1_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("DESCRIPTION")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockOnhandBean.Comparators.DESCRIPTION_DESC);
				}else{
				   Collections.sort(itemList, StockOnhandBean.Comparators.DESCRIPTION_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("PRIMARY_QUANTITY")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockOnhandBean.Comparators.PRIMARY_QUANTITY_DESC);
				}else{
				   Collections.sort(itemList, StockOnhandBean.Comparators.PRIMARY_QUANTITY_ASC);
				}
			}
			
			//split column array
			columnNameArr = o.getReportType().split("\\,");
			
			for (int row=0;row<itemList.size();row++) {
			  item = itemList.get(row);
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(contextPath,o,excel,columnNameArr));
			  }

			  //Gen Row Table
			  html.append(genRowTable(o,excel,columnNameArr,item));
			  //Summary
			  totalOnhandQty +=Utils.convertStrToDouble(item.getOnhandQty());
			
			}//while
			
			//Check Execute Found data
			if(r>0){
				// Get Total
				html.append(genTotalTable(o,excel,columnNameArr, totalOnhandQty));
				// gen end Table
				html.append("</table>");
			}
			
			o.setItemsList(itemList);
			o.setDataStrBuffer(html);
		}catch(Exception e){
			//logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			try {
				
			} catch (Exception e) {}
		}
	  return o;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTable(String contextPath,StockOnhandBean head,boolean excel,String[] columnNameArr) throws Exception{
		StringBuffer h = new StringBuffer("");
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='1'> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+((columnNameArr.length*2)+2)+"><b> Stock On-hand</b> </td>\n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append(" <td colspan="+((columnNameArr.length*2)+2)+"> \n");
			h.append(" วันที่พิมพ์ : &nbsp;"+DateToolsUtil.getCurrentDateTime(Utils.DD_MM_YYYY_HH_MM_SS_WITH_SLASH)+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
		}
		String width="100%";
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		for(int i=0;i<columnNameArr.length;i++){
		   h.append(" <th  nowrap>"+COLUMNNAME_MAP.get(columnNameArr[i])+"</th> \n");
		   if( excel ==true){
			   if( "SUBINVENTORY_CODE".equalsIgnoreCase(columnNameArr[i])){
				   h.append(" <th  nowrap>SubInv Name</th> \n");
			   }else  if( "BRAND".equalsIgnoreCase(columnNameArr[i])){
				   h.append(" <th  nowrap>Brand Name</th> \n");
			   }else  if( "SEGMENT1".equalsIgnoreCase(columnNameArr[i])){
				   h.append(" <th  nowrap>ชื่อ SKU </th> \n");
			   }
		   }
		  
		}//for
		
		h.append(" <th  nowrap>ONHAND QTY");
		 if( excel ==false){
		
		 }
		h.append("</th> \n");
		h.append(" <th  nowrap>Primary UOM");
		 if( excel ==false){
			
		 }
		h.append("</th> \n");
		h.append("</tr> \n");
		return h;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @param ROWVALUE_MAP
	 * @param ROWDESC_MAP
	 * @param o
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genRowTable(StockOnhandBean head,boolean excel,String[] columnNameArr,StockOnhandBean item) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className ="";
		String classNameCenter ="";
		String classNameNumber = "td_number";
		if(excel){
			className = "text";
			classNameCenter ="text";
			classNameNumber = "num_currency";
		}
		h.append("<tr> \n");
		
		for(int i=0;i<columnNameArr.length;i++){
			//logger.debug("columnName["+columnNameArr[i]+"]");
			if(excel){
				className = "text";
				classNameCenter ="text";
				classNameNumber = "num_currency";
			}else{
				if("Brand".equalsIgnoreCase(columnNameArr[i])
				 || "SUBINVENTORY_CODE".equalsIgnoreCase(columnNameArr[i])
				 || "SEGMENT1".equalsIgnoreCase(columnNameArr[i])
				 || "DESCRIPTION".equalsIgnoreCase(columnNameArr[i])
				 ){
					className ="td_text";
					classNameCenter="td_text_center";
				}
			}
           if(excel==false){
				if("BRAND".equalsIgnoreCase(columnNameArr[i])){
					 h.append("<td class='"+className+"' width='10%'>"+item.getBrand()+"-"+item.getBrandName()+"</td> \n");
				}else if("SUBINVENTORY_CODE".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='10%'>"+item.getSubInv()+"-"+item.getSubInvDesc()+"</td> \n");
				}else if("SEGMENT1".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='30%'>"+item.getProductCode()+"-"+item.getProductName()+"</td> \n");
				}
           }else{
        	   if("BRAND".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='3%'>"+item.getBrand()+"</td> \n");
					h.append("<td class='"+className+"' width='7%'>"+item.getBrandName()+"</td> \n");
				}else if("SUBINVENTORY_CODE".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='3%'>"+item.getSubInv()+"</td> \n");
					h.append("<td class='"+className+"' width='7%'>"+item.getSubInvDesc()+"</td> \n");
				}else if("SEGMENT1".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='10%'>"+item.getProductCode()+"</td> \n");
					h.append("<td class='"+className+"' width='20%'>"+item.getProductName()+"</td> \n");
				}
		   }
		}
		h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getOnhandQty()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='8%'>"+item.getPriUomCode()+"</td> \n");
		h.append("</tr> \n");
		
		return h;
	}
	
	private static StringBuffer genTotalTable(StockOnhandBean head,boolean excel,String[] columnNameArr,double totalOnhandQty) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className ="hilight_text";
		String classNameNumber = "td_number";
		if(excel){
			className ="colum_head";
			classNameNumber = "num_currency_bold";
		}
		
		h.append("<tr class='"+className+"'> \n");
		int colspan=columnNameArr.length;
		if(excel){
			colspan = 0;
			for(int i=0;i<columnNameArr.length;i++){
			  logger.debug("columnNameArr[i]:"+columnNameArr[i]);
			  colspan++;
			  if("BRAND".equalsIgnoreCase(columnNameArr[i]) 
				|| "SUBINVENTORY_CODE".equalsIgnoreCase(columnNameArr[i])
				|| "SEGMENT1".equalsIgnoreCase(columnNameArr[i])){
				  colspan++;
			  }
			}
		}
		h.append(" <td class='"+className+"' align='right' colspan="+colspan+">Total</td> \n");
		h.append("<td class='"+classNameNumber+"'>"+Utils.decimalFormat(totalOnhandQty, Utils.format_current_no_disgit)+"</td> \n");
		h.append("<td ></td> \n");
		h.append("</tr> \n");
		return h;
	}
}
