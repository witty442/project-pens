
package com.isecinc.pens.report.salesanalyst;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import util.DateToolsUtil;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.SAUtils;
import com.isecinc.pens.report.salesanalyst.helper.Utils;



/**
 * @author WITTY
 *
 */
public class SAGenerate {
   
	protected static  Logger logger = Logger.getLogger("PENS");
    public static SAUtils reportU = new SAUtils();
	
	
	/**
	 * Gen Report 
	 * @param salesBean
	 * @return
	 * @throws Exception
	 */
	public static List<StringBuffer> genReport(Connection conn,String contextPath,User user,SABean salesBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String htmlCode = "";
		List<ConfigBean> colGroupList = new ArrayList<ConfigBean>();
		ConfigBean groupByBean = null;
		String allCond = "";
		List<StringBuffer> resultList = new ArrayList<StringBuffer>();
		try{
			if(SAProcess.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
				/** Set Group Display  **/
				if( !StringUtils.isEmpty(salesBean.getDay()) && !StringUtils.isEmpty(salesBean.getDayTo())){
					Date startDate = DateToolsUtil.convertStringToDate(salesBean.getDay());
					Date endDate = DateToolsUtil.convertStringToDate(salesBean.getDayTo());
					
					while(startDate.compareTo(endDate) <= 0){
						colGroupList.add(new ConfigBean(DateToolsUtil.convertToString(startDate,"yyyyMMdd"),DateToolsUtil.convertToString(startDate),DateToolsUtil.convertToString(startDate)));
						startDate = DateUtils.addDays(startDate, 1); 
					}
				}
				else{
					colGroupList.add(new ConfigBean(salesBean.getGroupBy(),StringUtils.isEmpty(salesBean.getDay())?salesBean.getDayTo():salesBean.getDay(),StringUtils.isEmpty(salesBean.getDay())?salesBean.getDayTo():salesBean.getDay()));
				}
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
			   
				
				StringBuffer sql = SAProcess.getInstance().genMainSql(conn,user,salesBean,colGroupList,"");
				if( !Utils.isNull(sql.toString()).equals("")){
				    ps = conn.prepareStatement(sql.toString());
				    logger.debug("DateStr:"+salesBean.getDay());
				    logger.debug("Date:"+Utils.parseToBudishDate(salesBean.getDay(), Utils.DD_MM_YYYY_WITH_SLASH));
				    rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,groupByBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(SAProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				logger.debug("chkMonth:"+salesBean.getChkMonth().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkMonth().length;i++){
					logger.debug("name:["+i+"]value:["+salesBean.getChkMonth()[i]+"]");
					
					colGroupList.add(new ConfigBean(salesBean.getChkMonth()[i],salesBean.getChkMonth()[i],Utils.isNull(SAProcess.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i]))));
					allCond +="'"+salesBean.getChkMonth()[i]+"',";
				}
				
				if(colGroupList.size() > 0){
					Collections.sort(colGroupList);
				}
				
				allCond +="'0'";
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				
				StringBuffer sql = SAProcess.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				if( !Utils.isNull(sql.toString()).equals("")){
				    ps = conn.prepareStatement(sql.toString());
				    rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,groupByBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(SAProcess.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				logger.debug("chkQuarter:"+salesBean.getChkQuarter().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkQuarter().length;i++){
					logger.debug("name:["+i+"]value:["+salesBean.getChkQuarter()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkQuarter()[i],salesBean.getChkQuarter()[i],Utils.isNull(SAProcess.getInstance().QUARTER_MAP.get(salesBean.getChkQuarter()[i]))));
					allCond +="'"+salesBean.getChkQuarter()[i]+"',";
				}
				allCond +="'0'";
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = SAProcess.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				if( !Utils.isNull(sql.toString()).equals("")){
				    ps = conn.prepareStatement(sql.toString());
				    rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,groupByBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(SAProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				logger.debug("chkYear:"+salesBean.getChkYear().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkYear().length;i++){
					logger.debug("name:["+i+"]value:["+salesBean.getChkYear()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkYear()[i],salesBean.getChkYear()[i],Utils.convertToYearBushdish(Integer.parseInt(salesBean.getChkYear()[i]))+"" ));
					allCond +="'"+salesBean.getChkYear()[i]+"',";
				
				}
				allCond +="'0'";
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = SAProcess.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				if( !Utils.isNull(sql.toString()).equals("")){
				    ps = conn.prepareStatement(sql.toString());
				    rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,groupByBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
			}

		  return resultList;
		}catch(Exception e){
			logger.info(e.getMessage());
			throw e;
		}finally{
			
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
	}
	
	/**
	 * Gen SQL 
	 * @param salesBean
	 * @return
	 * @throws Exception
	 */
	public static String genSQL(User user,SABean salesBean) throws Exception{
		Connection conn = null;
		String htmlCode = "";
		List<ConfigBean> colGroupList = new ArrayList<ConfigBean>();
		ConfigBean groupByBean = null;
		String allCond = "";
		try{
			conn = DBConnection.getInstance().getConnection();
			
			if(SAProcess.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
				/** Set Group Display  **/
				
				colGroupList.add(new ConfigBean(salesBean.getGroupBy(),salesBean.getDay(),salesBean.getDay()));
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
			    
				StringBuffer sql = SAProcess.getInstance().genMainSql(conn,user,salesBean,colGroupList,"");
				htmlCode = sql.toString();
			}else if(SAProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				logger.debug("chkMonth:"+salesBean.getChkMonth().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkMonth().length;i++){
					logger.debug("name:["+i+"]value:["+salesBean.getChkMonth()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkMonth()[i],salesBean.getChkMonth()[i],Utils.isNull(SAProcess.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i]))));
					allCond +="'"+salesBean.getChkMonth()[i]+"',";
				}
				allCond +="'0'";
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				
				StringBuffer sql = SAProcess.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				htmlCode = sql.toString();
			}else if(SAProcess.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				logger.debug("chkQuarter:"+salesBean.getChkQuarter().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkQuarter().length;i++){
					logger.debug("name:["+i+"]value:["+salesBean.getChkQuarter()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkQuarter()[i],salesBean.getChkQuarter()[i],Utils.isNull(SAProcess.getInstance().QUARTER_MAP.get(salesBean.getChkQuarter()[i]))));
					allCond +="'"+salesBean.getChkQuarter()[i]+"',";
					
				}
				allCond +="'0'";
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = SAProcess.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				htmlCode = sql.toString();
			}else if(SAProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				logger.debug("chkYear:"+salesBean.getChkYear().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkYear().length;i++){
					logger.debug("name:["+i+"]value:["+salesBean.getChkYear()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkYear()[i],salesBean.getChkYear()[i],salesBean.getChkYear()[i]));
					allCond +="'"+salesBean.getChkYear()[i]+"',";
					
				}
				allCond +="'0'";
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = SAProcess.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				htmlCode = sql.toString();
			}
		  return htmlCode;
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
/**
 * Gen Header Excel
 * @param salesBean
 * @return
 * @throws Exception
 */
   public static StringBuffer genHeaderReportExportExcel(User user,SABean salesBean,String columnCount,String condDisp1,String condDisp2,String condDisp3,String condDisp4,String condDisp5) throws Exception{
		
		StringBuffer htmlStr = new StringBuffer("");
		try{
			htmlStr.append("<table border='1' width='100%' class='result' id='result'> \n");
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'><b>Sales Analysis Report</b></td>  \n");
			htmlStr.append("</tr> \n");
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'> "+new String(condDisp5.getBytes("ISO8859_1"), "TIS-620")+"  </td>  \n") ;
			htmlStr.append("</tr> \n");
			
			if(SAProcess.getInstance().TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				if(!StringUtils.isEmpty(salesBean.getDay()) && !StringUtils.isEmpty(salesBean.getDayTo()))
					htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : วัน  : "+salesBean.getDay()+" - "+salesBean.getDayTo()+"</td>  \n");
				else
					htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : วัน  : "+(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo())+"</td>  \n");
				
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(SAProcess.getInstance().TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : เดือน  :  \n");
					for(int i=0;i<salesBean.getChkMonth().length;i++){
						if(i==salesBean.getChkMonth().length-1){
					       htmlStr.append( Utils.isNull(SAProcess.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i])));
						}else{
						   htmlStr.append( Utils.isNull(SAProcess.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i])) +", ");
						}
					}
				htmlStr.append("   พ.ศ. :"+Utils.convertToYearBushdish(Integer.parseInt(salesBean.getYear()))+"\n");
				
				htmlStr.append("</td> \n");
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(SAProcess.getInstance().TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : ไตรมาส  :  \n");
					for(int i=0;i<salesBean.getChkQuarter().length;i++){
						if(i==salesBean.getChkQuarter().length-1){
					       htmlStr.append( Utils.isNull(SAProcess.getInstance().QUARTER_MAP.get(salesBean.getChkQuarter()[i])));
						}else{
						   htmlStr.append( Utils.isNull(SAProcess.getInstance().QUARTER_MAP.get(salesBean.getChkQuarter()[i])) +", ");
						}
					}
				htmlStr.append("   พ.ศ. "+Utils.convertToYearBushdish(Integer.parseInt(salesBean.getYear()))+"\n");
				
				htmlStr.append("</td> \n");
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(SAProcess.getInstance().TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : ปี  :  \n");
					for(int i=0;i<salesBean.getChkYear().length;i++){
						if(i==salesBean.getChkYear().length-1){
					       htmlStr.append(Utils.convertToYearBushdish(Integer.parseInt(salesBean.getChkYear()[i])));
						}else{
						   htmlStr.append(Utils.convertToYearBushdish(Integer.parseInt(salesBean.getChkYear()[i])) +", ");
						}
					}
				htmlStr.append("</td> \n");
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(SAProcess.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}
			
			//Gen Condition
			genConditionDisp(htmlStr,salesBean,columnCount,condDisp1,condDisp2,condDisp3,condDisp4);
			
		    /** Gen Timestamp **/
			Calendar currentTime = Calendar.getInstance(new Locale("th","TH"));
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'>Exported date :"+Utils.stringValue(currentTime.getTime(), Utils.DD_MM_YYYY_HH_MM_SS_WITH_SLASH,new Locale("th","TH"))+"");
			htmlStr.append(" ,Created by:"+user.getName()+" </td> \n");
			htmlStr.append("</tr> \n");
			htmlStr.append("</table> \n");
			
		  return htmlStr;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
   /**
    * 
    * @param contextPath
    * @param groupByBean
    * @param colGroupList
    * @param colDispList
    * @param rs
    * @param summaryType
    * @return
    * @throws Exception
    */
	private static void genResultSetToJsonData(String contextPath,ConfigBean groupByBean,List colGroupList,List colDispList ,ResultSet rs,String summaryType) throws Exception{
	}
	
	private static StringBuffer genReportHtml(String contextPath,ConfigBean groupByBean,List colGroupList,List colDispList ,ResultSet rs,String summaryType) throws Exception{
		StringBuffer htmlStr = new StringBuffer("");
		boolean found = false;
		Map<String,BigDecimal> summaryColumnMap = new HashMap<String,BigDecimal>();
		Map<String,BigDecimal> summaryMap1 = new HashMap<String,BigDecimal>();
		BigDecimal bigZero = new BigDecimal("0");
		int totalRecord = 0;
		int no =0;
		int columnCount = 3; //default
		String[] arrayfldNoDisplayCode=new String[]{"อำเภอ","จังหวัด","ตำบล","วันที่(Order)","วันที่ขาย(Invoice)","เลขที่สั่งซื้อ (Order No.)","เลขที่ขาย (Invoice No.)","ร้านค้า-ที่ส่งสินค้า","ร้านค้า-ที่ส่งบิล"};
		
		boolean isSummry = true;
		if(SAProcess.SUMMARY_TYPE_NONE.equals(summaryType))
			isSummry = false;
		
		//boolean isDebug = false;
		boolean isDebug = true;
		
		try{
			String groupBy = groupByBean.getDispText();
			
			Boolean isNoDisplayed = ArrayUtils.contains(arrayfldNoDisplayCode,groupBy); 
			
			htmlStr.append("<table border='1' width='100%' class='result2' id='sort-table'> <thead> \n");
			
			/***** Header Table ******************************/
			htmlStr.append("<tr> \n");
			/** Rows Display**/
			htmlStr.append(" <th rowspan='2'>ลำดับ</th>  \n");
			
			if(!isNoDisplayed){
				logger.debug("groupBy:"+groupByBean.value);
				if(!(groupByBean.value.equalsIgnoreCase("SALES_ORDER_DATE") || groupByBean.value.equalsIgnoreCase("Invoice_Date"))){
					htmlStr.append(" <th rowspan='2'>รหัส</th> \n");
				}
			}
			htmlStr.append(" <th rowspan='2'>"+groupBy+"</th>  \n");
			for(int i=0;i<colGroupList.size();i++){
				ConfigBean configBean = (ConfigBean)colGroupList.get(i);
				htmlStr.append("<th colspan='"+colDispList.size()+"'>"+configBean.getDispText()+"</th> \n");
			}
			
			if(isSummry){
				String label = "ผลรวม";
				if(SAProcess.SUMMARY_TYPE_AVG.equals(summaryType))
					label = "ค่าเฉลี่ย";
				
				htmlStr.append("<th colspan='"+colDispList.size()+"'>"+label+"</th> \n");
			}
			
			htmlStr.append("</tr>\n");
			
			htmlStr.append("<tr> \n");
			/** Display Column **/
			for(int i=0;i<colGroupList.size();i++){
				ConfigBean configGroupBean = (ConfigBean)colGroupList.get(i);
				for(int d=0;d<colDispList.size();d++){
					ConfigBean configBean = (ConfigBean)colDispList.get(d);
					String columnOrder = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
					logger.debug("ColumnOrder:"+columnOrder);
					
					String sortIdKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName()); 
					
				    htmlStr.append("<th>"+configBean.getDispText()+"&nbsp;&nbsp;");
				    htmlStr.append(" <img style=\"cursor:pointer\" src='"+contextPath+"/icons/arrow_down.gif' href='#' class='link-sort asc' id='"+sortIdKey+"'/>");
				    htmlStr.append(" &nbsp;&nbsp;");
				    htmlStr.append(" <img style=\"cursor:pointer\" src='"+contextPath+"/icons/arrow_up.gif' href='#' class='link-sort desc' id='"+sortIdKey+"'/>");
				    htmlStr.append("</th> \n");
   				    
				}
			}
			
			//new Add Head
			if(isSummry){
				ConfigBean configGroupBean1 = (ConfigBean)colGroupList.get(0);
				for(int d=0;d<colDispList.size();d++){
					ConfigBean configBean = (ConfigBean)colDispList.get(d);
					String columnOrder = configBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName());
					logger.debug("ColumnOrder:"+columnOrder);
					
					String sortIdKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName()); 
							
				    htmlStr.append("<th>"+configBean.getDispText()+"&nbsp;&nbsp;");
				    htmlStr.append(" <img  style=\"cursor:pointer\" src='"+contextPath+"/icons/arrow_down.gif' href='#' class='link-sort asc' id='"+sortIdKey+"'/>");
				    htmlStr.append(" &nbsp;&nbsp;");
				    htmlStr.append(" <img  style=\"cursor:pointer\" src='"+contextPath+"/icons/arrow_up.gif' href='#' class='link-sort desc' id='"+sortIdKey+"'/>");
				    htmlStr.append("</th> \n");
   				    
				}
			}
			
			htmlStr.append("</tr>  </thead> \n");
			/**************** Header Table *********************************/
			
			
			/***************** Rows Data ***********************************/
			htmlStr.append("<tbody> \n");
			while(rs.next()){
				found = true;
				no++;
				htmlStr.append("  <tr> \n");
				htmlStr.append(" <td>"+no+"</td>  \n");
				
				if("Invoice_Date".equalsIgnoreCase(groupByBean.getName())){
					String dateStr = Utils.stringValue(new Date(rs.getDate(groupByBean.getName()+"_DESC",Calendar.getInstance(Locale.US)).getTime()),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					logger.debug("dateStr:"+dateStr);
					if(!isNoDisplayed)
				    htmlStr.append(" <td>"+dateStr+"</td>  \n");

				}else if("SALES_ORDER_DATE".equalsIgnoreCase(groupByBean.getName())){
					String dateStr = Utils.stringValue(new Date(rs.getDate(groupByBean.getName()+"_DESC",Calendar.getInstance(Locale.US)).getTime()),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					logger.debug("dateStr:"+dateStr);
					if(!isNoDisplayed)
				    htmlStr.append(" <td>"+dateStr+"</td>  \n");

				}else{
					if(!isNoDisplayed)
					htmlStr.append(" <td>"+rs.getString(groupByBean.getName()+"_CODE")+"</td> \n");
					htmlStr.append(" <td align='left'>"+rs.getString(groupByBean.getName()+"_DESC")+"</td>  \n");
				}
				
				
				/** Calculation Summary By For Loop  ***/
				Map<String,BigDecimal> summaryRowMap = new HashMap<String,BigDecimal>();
				
				for(int i=0;i<colGroupList.size();i++){
					ConfigBean configGroupBean = (ConfigBean)colGroupList.get(i);
					
					logger.debug("colDispList:"+colDispList.size());
					BigDecimal value = null;
					BigDecimal valueSummary = null;
					for(int d=0;d<colDispList.size();d++){
						ConfigBean configBean = (ConfigBean)colDispList.get(d);
						String resultKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
						
						/** Case Column CALL Summary CALL_NO DUP  and Type Summary **/
						if(resultKey.startsWith("CALL") && isSummry && SAProcess.SUMMARY_TYPE_SUM.equals(summaryType)){
							value = Utils.isNullToZero(rs.getBigDecimal(resultKey));//Normal Value
							valueSummary = Utils.isNullToZero(rs.getBigDecimal(SAProcess.NO_DUP_PREFIX+resultKey));
						}else{
							value = Utils.isNullToZero(rs.getBigDecimal(resultKey));//Normal Value
							valueSummary = Utils.isNullToZero(rs.getBigDecimal(resultKey));//for summary
						}
						
						//logger.debug("Summary **>>ColumnName:"+resultKey);
					
						String resultKey1 = configBean.getName();
						
						/** Summary By Row **/
						if(summaryRowMap.get(resultKey1) != null){
							BigDecimal sumRowValueAdd = (BigDecimal)summaryRowMap.get(resultKey1);
							sumRowValueAdd = sumRowValueAdd.add(valueSummary);
							summaryRowMap.put(resultKey1, sumRowValueAdd);
						}else{
							summaryRowMap.put(resultKey1, valueSummary);
						}
						
						/** Summary 1 For case PER **/
						if(summaryMap1.get(resultKey1) != null){
							BigDecimal summaryValueAdd = (BigDecimal)summaryMap1.get(resultKey1);
							summaryValueAdd = summaryValueAdd.add(valueSummary);
							summaryMap1.put(resultKey1, summaryValueAdd);

						}else{
							summaryMap1.put(resultKey1, valueSummary);
						}
						
						/** Summary By Column **/						
						if(summaryColumnMap.get(resultKey) != null){
							BigDecimal summaryValueAdd = (BigDecimal)summaryColumnMap.get(resultKey);
							if(resultKey.startsWith("CALL") && isSummry && SAProcess.SUMMARY_TYPE_SUM.equals(summaryType)){
							    summaryValueAdd = summaryValueAdd.add(value);
							}else{
							    summaryValueAdd = summaryValueAdd.add(valueSummary);
							}
							summaryColumnMap.put(resultKey, summaryValueAdd);
						}else{
							if(resultKey.startsWith("CALL") && isSummry && SAProcess.SUMMARY_TYPE_SUM.equals(summaryType)){
							    summaryColumnMap.put(resultKey, value);	
							}else{
							    summaryColumnMap.put(resultKey, valueSummary);	
							}
						}
						
						String sortIdKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
						
					    htmlStr.append("<td align='right' class='sort_"+sortIdKey+"'>"+Utils.convertDigitToDisplay(configBean.getDispText(),value)+"</td> \n");
					}
				}//for 2
				
				
				if(isSummry){
					/** Calculation Summary Case Percent **/
					//new 2
					for(int d=0;d<colDispList.size();d++){
						ConfigBean configBean = (ConfigBean)colDispList.get(d);
						String resultRowSumBean = configBean.getName();
						BigDecimal valueRowSum = (BigDecimal)summaryRowMap.get(resultRowSumBean);
						boolean isPct = false;
						
						if(resultRowSumBean.startsWith("PER")){
							//Sum1
							ConfigBean configBean1 = (ConfigBean)colDispList.get(d-2);
							String resultKey1 = configBean1.getName();
							BigDecimal summaryValue1 = new BigDecimal("0");
							if(summaryRowMap != null && summaryRowMap.get(resultKey1) != null){
								summaryValue1 = (BigDecimal)summaryRowMap.get(resultKey1);
							}
							
							//Sum2
							ConfigBean configBean2 = (ConfigBean)colDispList.get(d-1);
							String resultKey2 = configBean2.getName();
							BigDecimal summaryValue2 = new BigDecimal("0");
							if(summaryRowMap != null && summaryRowMap.get(resultKey2) != null){
								summaryValue2 = (BigDecimal)summaryRowMap.get(resultKey2);
							}
							//logger.debug("summaryValue1:"+summaryValue1);
							//logger.debug("summaryValue2:"+summaryValue2);
							
							if(summaryValue1.compareTo(bigZero) != 0){
								valueRowSum = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
								isPct = true;
							}
						}
						
						if(SAProcess.SUMMARY_TYPE_AVG.equals(summaryType) && !isPct){
							valueRowSum = valueRowSum.divide(BigDecimal.valueOf(colGroupList.size()), 2, BigDecimal.ROUND_HALF_UP);
						}
						//debug
						String debug = isDebug?"RSum:":"";
						
						htmlStr.append("<td align='right'>"+debug+Utils.convertDigitToDisplay(configBean.getDispText(),valueRowSum)+"</td> \n");
					}
				}

				htmlStr.append("</tr>\n");
				totalRecord++;
			}//while 
			
			htmlStr.append("</tbody> \n");
			/***************************************************/
			
			/****  Summary  ***********************************/
			htmlStr.append("<tfoot> <tr> \n");
			//
			String cSpan = "";
			if(groupByBean.value.equalsIgnoreCase("SALES_ORDER_DATE") || groupByBean.value.equalsIgnoreCase("Invoice_Date")){
				cSpan = "2"; 
			}else{
				cSpan = ((isNoDisplayed)?"2":"3");
			}
			
			if(groupByBean.value.equalsIgnoreCase("SALES_ORDER_DATE") || groupByBean.value.equalsIgnoreCase("Invoice_Date")){
				columnCount = 2;
			}else{
				columnCount  = ((isNoDisplayed)?2:3); 
			}
			
			htmlStr.append(" <td colspan='"+cSpan+"' align='right'>ยอดรวม  "+totalRecord+" รายการ</td>  \n");
			
			if(found){
				for(int i=0;i<colGroupList.size();i++){
					ConfigBean configGroupBean = (ConfigBean)colGroupList.get(i);
					for(int d=0;d<colDispList.size();d++){
						ConfigBean configBean = (ConfigBean)colDispList.get(d);
						String resultKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
						
						BigDecimal summaryValue = new BigDecimal("0");
		
						if(summaryColumnMap != null && summaryColumnMap.get(resultKey) != null){
							summaryValue = (BigDecimal)summaryColumnMap.get(resultKey);
						}
						
						if(resultKey.startsWith("PER")){
								//Sum1
								ConfigBean configBean1 = (ConfigBean)colDispList.get(d-2);
								String resultKey1 = configBean1.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
								BigDecimal summaryValue1 = new BigDecimal("0");
								if(summaryColumnMap != null && summaryColumnMap.get(resultKey1) != null){
									summaryValue1 = (BigDecimal)summaryColumnMap.get(resultKey1);	
								}
								//Sum2
								ConfigBean configBean2 = (ConfigBean)colDispList.get(d-1);
								String resultKey2 = configBean2.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
								BigDecimal summaryValue2 = new BigDecimal("0");
								if(summaryColumnMap != null && summaryColumnMap.get(resultKey2) != null){
									summaryValue2 = (BigDecimal)summaryColumnMap.get(resultKey2);
								
								}
								logger.debug("summaryValue1:"+summaryValue1);
								logger.debug("summaryValue2:"+summaryValue2);
								
								if(summaryValue1.compareTo(bigZero) != 0){
									summaryValue = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
								}
							}
							
						 columnCount++;
						 String debug = isDebug?"CSum:":"";
					     htmlStr.append("<td align='right'>"+debug+Utils.convertDigitToDisplay(configBean.getDispText(),summaryValue)+"</td> \n");    
						   
					}//for2
				}//for1
			}
			
			if(isSummry && found){
				for(int d=0;d<colDispList.size();d++){
					ConfigBean configBean = (ConfigBean)colDispList.get(d);
					String resultRowSumBean = configBean.getName();
					BigDecimal valueRowSum = (BigDecimal)summaryMap1.get(resultRowSumBean);
					boolean isPct = false;
					
					if(resultRowSumBean.startsWith("PER")){
						//Sum1
						ConfigBean configBean1 = (ConfigBean)colDispList.get(d-2);
						String resultKey1 = configBean1.getName();
						BigDecimal summaryValue1 = new BigDecimal("0");
						if(summaryMap1 != null && summaryMap1.get(resultKey1) != null){
							summaryValue1 = (BigDecimal)summaryMap1.get(resultKey1);
						}
						
						//Sum2
						ConfigBean configBean2 = (ConfigBean)colDispList.get(d-1);
						String resultKey2 = configBean2.getName();
						BigDecimal summaryValue2 = new BigDecimal("0");
						if(summaryMap1 != null && summaryMap1.get(resultKey2) != null){
							summaryValue2 = (BigDecimal)summaryMap1.get(resultKey2);
						}
						//logger.debug("summaryValue1:"+summaryValue1);
						//logger.debug("summaryValue2:"+summaryValue2);
						
						if(summaryValue1.compareTo(bigZero) != 0){
							valueRowSum = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
							isPct = true;
						}
					}
					if(SAProcess.SUMMARY_TYPE_AVG.equals(summaryType) && !isPct){
						valueRowSum = valueRowSum.divide(BigDecimal.valueOf(colGroupList.size()), 2, BigDecimal.ROUND_HALF_UP);
					}
					columnCount++;
					String debug = isDebug?"TSum:":"";
					htmlStr.append("<td align='right'>"+debug+Utils.convertDigitToDisplay(configBean.getDispText(),valueRowSum)+"</td> \n");
				}
			}
			htmlStr.append("</tr> </tfoot> \n");
			/*************************************************/
			
			htmlStr.append("</table> \n");
			
			/** Write For Colspan Gen Header Excel **/
			htmlStr.append("<input type='hidden' name='columnCount' value='"+columnCount+"'/> \n");
			
			int sumColumn = 0;
			if(isSummry){
				sumColumn = colDispList.size();
			}
			
			if(!found){
			    return new StringBuffer("");
			}else{
				return htmlStr;
			}
		}catch(Exception e){
		  throw e;
		}
	}
	
	
	
	/** Set Column Display **/
	private static List<ConfigBean> setDispCol(SABean salesBean) throws Exception{
		List<ConfigBean> colDispList = new ArrayList<ConfigBean>();	
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			//Case Column Display =CALL  set unit = "" 
			if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1()))){
				 colDispList.add(new ConfigBean(salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1",Utils.isNull(salesBean.getColNameDisp1()),Utils.isNull(SAProcess.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp1()))+""));
			}else{
			   colDispList.add(new ConfigBean(salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1",Utils.isNull(salesBean.getColNameDisp1()),Utils.isNull(SAProcess.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp1()))+"("+Utils.isNull(SAProcess.getInstance().UNIT_MAP.get(salesBean.getColNameUnit1()))+")"));
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
			//Case Column Display =CALL  set unit = "" 
			if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2()))){
				colDispList.add(new ConfigBean(salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2",Utils.isNull(salesBean.getColNameDisp2()),Utils.isNull(SAProcess.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp2()))+""));
			}else{
			    colDispList.add(new ConfigBean(salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2",Utils.isNull(salesBean.getColNameDisp2()),Utils.isNull(SAProcess.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp2()))+"("+Utils.isNull(SAProcess.getInstance().UNIT_MAP.get(salesBean.getColNameUnit2()))+")"));
			}
			if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
				colDispList.add(new ConfigBean("PER1","%","%"));
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			//Case Column Display =CALL  set unit = "" 
			if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3()))){
			    colDispList.add(new ConfigBean(salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3",Utils.isNull(salesBean.getColNameDisp3()),Utils.isNull(SAProcess.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp3()))+""));
			}else{
				colDispList.add(new ConfigBean(salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3",Utils.isNull(salesBean.getColNameDisp3()),Utils.isNull(SAProcess.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp3()))+"("+Utils.isNull(SAProcess.getInstance().UNIT_MAP.get(salesBean.getColNameUnit3()))+")"));
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			//Case Column Display =CALL  set unit = "" 
			if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4()))){
			    colDispList.add(new ConfigBean(salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4",Utils.isNull(salesBean.getColNameDisp4()),Utils.isNull(SAProcess.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp4()))+""));
			}else{
				colDispList.add(new ConfigBean(salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4",Utils.isNull(salesBean.getColNameDisp4()),Utils.isNull(SAProcess.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp4()))+"("+Utils.isNull(SAProcess.getInstance().UNIT_MAP.get(salesBean.getColNameUnit4()))+")"));
			}
			if( !"0".equals(Utils.isNull(salesBean.getCompareDisp2()))){
				colDispList.add(new ConfigBean("PER2","%","%"));
			}
		}
		return colDispList;
	}
	
	
	/**
	 * gen Header Display
	 * @param salesBean
	 * @return
	 * @throws Exception
	 */
	private  static void genConditionDisp(StringBuffer htmlStr,SABean salesBean,String columnCount ,String condDisp1,String condDisp2,String condDisp3,String condDisp4) throws Exception{
		
		if(    !"-1".equals(Utils.isNull(salesBean.getCondName1())) 
			|| !"-1".equals(Utils.isNull(salesBean.getCondName2())) 
			|| !"-1".equals(Utils.isNull(salesBean.getCondName3())) 
			|| !"-1".equals(Utils.isNull(salesBean.getCondName4())) ){
			
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'>ขอบเขตข้อมูล</th>  \n");
			htmlStr.append("</tr> \n");
		}
		
		if( !"-1".equals(Utils.isNull(salesBean.getCondName1())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue1()))){
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'> 1:"+new String(condDisp1.getBytes("ISO8859_1"), "TIS-620")+" = "+Utils.isNull(salesBean.getCondValueDisp1()) +"</td>  \n") ;
			htmlStr.append("</tr> \n");
		}
		
		if( !"-1".equals(Utils.isNull(salesBean.getCondName2())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue2()))){
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'> 2:"+new String(condDisp2.getBytes("ISO8859_1"), "TIS-620")+" = "+Utils.isNull(salesBean.getCondValueDisp2()) +"</td>  \n") ;
			htmlStr.append("</tr> \n");
		}
		
		if( !"-1".equals(Utils.isNull(salesBean.getCondName3())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue3()))){
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'> 3:"+new String(condDisp3.getBytes("ISO8859_1"), "TIS-620")+" = "+Utils.isNull(salesBean.getCondValueDisp3()) +"</td>  \n") ;
			htmlStr.append("</tr> \n");
		}
		
		if( !"-1".equals(Utils.isNull(salesBean.getCondName4())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue4()))){
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'> 4:"+new String(condDisp4.getBytes("ISO8859_1"), "TIS-620")+" = "+Utils.isNull(salesBean.getCondValueDisp4()) +"</td>  \n") ;
			htmlStr.append("</tr> \n");
		}

	}
	
	
}
