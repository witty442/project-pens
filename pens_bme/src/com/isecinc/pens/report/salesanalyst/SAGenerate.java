
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

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.salesanalyst.SAGenCondition;
import com.isecinc.pens.web.salesanalyst.SAUtils;
import com.pens.util.DateToolsUtil;
import com.pens.util.Debug;
import com.pens.util.FileUtil;
import com.pens.util.Utils;



/**
 * @author WITTY
 *
 */
public class SAGenerate {
   
	protected static  Debug debug = new Debug(true,0);
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
		ConfigBean configBean = null;
		String allCond = "";
		List<StringBuffer> resultList = new ArrayList<StringBuffer>();
		try{
			if(SAInitial.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
				/** Set Group Display  **/
				if( !StringUtils.isEmpty(salesBean.getDay()) && !StringUtils.isEmpty(salesBean.getDayTo())){
					Date startDate = DateToolsUtil.convertStringToDate(salesBean.getDay());
					Date endDate = DateToolsUtil.convertStringToDate(salesBean.getDayTo());
					
					if(startDate.compareTo(endDate) != 0){
						while(startDate.compareTo(endDate) <= 0){
							colGroupList.add(new ConfigBean(DateToolsUtil.convertToString(startDate,"yyyyMMdd"),DateToolsUtil.convertToString(startDate),DateToolsUtil.convertToString(startDate)));
							startDate = DateUtils.addDays(startDate, 1); 
						}
					}else{
						//DateFrom == DateTo
						colGroupList.add(new ConfigBean(salesBean.getGroupBy(),StringUtils.isEmpty(salesBean.getDay())?salesBean.getDayTo():salesBean.getDay(),StringUtils.isEmpty(salesBean.getDay())?salesBean.getDayTo():salesBean.getDay()));
					}
				}else{
					colGroupList.add(new ConfigBean(salesBean.getGroupBy(),StringUtils.isEmpty(salesBean.getDay())?salesBean.getDayTo():salesBean.getDay(),StringUtils.isEmpty(salesBean.getDay())?salesBean.getDayTo():salesBean.getDay()));
				}
				/** Display group by **/
				configBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
			   
				
				StringBuffer sql = SAInitial.getInstance().genMainSql(conn,user,salesBean,colGroupList,"");
				if( !Utils.isNull(sql.toString()).equals("")){
				    ps = conn.prepareStatement(sql.toString());
				    debug.debug("DateStr:"+salesBean.getDay());
				    debug.debug("Date:"+Utils.parseToBudishDate(salesBean.getDay(), Utils.DD_MM_YYYY_WITH_SLASH));
				    //debug.debug("Sql:"+sql.toString());
				    FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
				    
				   rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,salesBean.getGroupBy(),configBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(SAInitial.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkMonth:"+salesBean.getChkMonth().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkMonth().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkMonth()[i]+"]");
					
					colGroupList.add(new ConfigBean(salesBean.getChkMonth()[i],salesBean.getChkMonth()[i],Utils.isNull(SAInitial.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i]))));
					allCond +="'"+salesBean.getChkMonth()[i]+"',";
				}
				
				if(colGroupList.size() > 0){
					Collections.sort(colGroupList);
				}
				
				allCond +="'0'";
				/** Display group by **/
				configBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				
				StringBuffer sql = SAInitial.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				if( !Utils.isNull(sql.toString()).equals("")){
					FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
				    ps = conn.prepareStatement(sql.toString());
				    rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,salesBean.getGroupBy(),configBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(SAInitial.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkQuarter:"+salesBean.getChkQuarter().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkQuarter().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkQuarter()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkQuarter()[i],salesBean.getChkQuarter()[i],Utils.isNull(SAInitial.getInstance().QUARTER_MAP.get(salesBean.getChkQuarter()[i]))));
					allCond +="'"+salesBean.getChkQuarter()[i]+"',";
				}
				
				if(colGroupList.size() > 0){
					Collections.sort(colGroupList);
				}
				allCond +="'0'";
				
				/** Display group by **/
				configBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = SAInitial.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				if( !Utils.isNull(sql.toString()).equals("")){
					FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
				    ps = conn.prepareStatement(sql.toString());
				    rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,salesBean.getGroupBy(),configBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(SAInitial.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkYear:"+salesBean.getChkYear().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkYear().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkYear()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkYear()[i],salesBean.getChkYear()[i],Utils.convertToYearBushdish(Integer.parseInt(salesBean.getChkYear()[i]))+"" ));
					allCond +="'"+salesBean.getChkYear()[i]+"',";
				
				}
				allCond +="'0'";
				
				/** Display group by **/
				configBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = SAInitial.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				if( !Utils.isNull(sql.toString()).equals("")){
					FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
				    ps = conn.prepareStatement(sql.toString());
				    rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,salesBean.getGroupBy(),configBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
			}

		  return resultList;
		}catch(Exception e){
			debug.info(e.getMessage());
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
			
			if(SAInitial.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
				/** Set Group Display  **/
				
				colGroupList.add(new ConfigBean(salesBean.getGroupBy(),salesBean.getDay(),salesBean.getDay()));
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
			    
				StringBuffer sql = SAInitial.getInstance().genMainSql(conn,user,salesBean,colGroupList,"");
				htmlCode = sql.toString();
			}else if(SAInitial.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkMonth:"+salesBean.getChkMonth().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkMonth().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkMonth()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkMonth()[i],salesBean.getChkMonth()[i],Utils.isNull(SAInitial.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i]))));
					allCond +="'"+salesBean.getChkMonth()[i]+"',";
				}
				allCond +="'0'";
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				
				StringBuffer sql = SAInitial.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				htmlCode = sql.toString();
			}else if(SAInitial.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkQuarter:"+salesBean.getChkQuarter().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkQuarter().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkQuarter()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkQuarter()[i],salesBean.getChkQuarter()[i],Utils.isNull(SAInitial.getInstance().QUARTER_MAP.get(salesBean.getChkQuarter()[i]))));
					allCond +="'"+salesBean.getChkQuarter()[i]+"',";
					
				}
				allCond +="'0'";
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = SAInitial.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
				htmlCode = sql.toString();
			}else if(SAInitial.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkYear:"+salesBean.getChkYear().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkYear().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkYear()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkYear()[i],salesBean.getChkYear()[i],salesBean.getChkYear()[i]));
					allCond +="'"+salesBean.getChkYear()[i]+"',";
					
				}
				allCond +="'0'";
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = SAInitial.getInstance().genMainSql(conn,user,salesBean,colGroupList,allCond);
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
			htmlStr.append("<table border='1' width='100%' cellpadding='3' cellspacing='1' class='result'> \n");
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'><b>Sales Analysis Report</b></td>  \n");
			htmlStr.append("</tr> \n");
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'> "+new String(condDisp5.getBytes("ISO8859_1"), "TIS-620")+"  </td>  \n") ;
			htmlStr.append("</tr> \n");
			
			if(SAInitial.getInstance().TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				if(!StringUtils.isEmpty(salesBean.getDay()) && !StringUtils.isEmpty(salesBean.getDayTo()))
					htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : วัน  : "+salesBean.getDay()+" - "+salesBean.getDayTo()+"</td>  \n");
				else
					htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : วัน  : "+(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo())+"</td>  \n");
				
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(SAInitial.getInstance().TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : เดือน  :  \n");
					for(int i=0;i<salesBean.getChkMonth().length;i++){
						if(i==salesBean.getChkMonth().length-1){
					       htmlStr.append( Utils.isNull(SAInitial.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i])));
						}else{
						   htmlStr.append( Utils.isNull(SAInitial.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i])) +", ");
						}
					}
				htmlStr.append("   พ.ศ. :"+Utils.convertToYearBushdish(Integer.parseInt(salesBean.getYear()))+"\n");
				
				htmlStr.append("</td> \n");
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(SAInitial.getInstance().TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : ไตรมาส  :  \n");
					for(int i=0;i<salesBean.getChkQuarter().length;i++){
						if(i==salesBean.getChkQuarter().length-1){
					       htmlStr.append( Utils.isNull(SAInitial.getInstance().QUARTER_MAP.get(salesBean.getChkQuarter()[i])));
						}else{
						   htmlStr.append( Utils.isNull(SAInitial.getInstance().QUARTER_MAP.get(salesBean.getChkQuarter()[i])) +", ");
						}
					}
				htmlStr.append("   พ.ศ. "+Utils.convertToYearBushdish(Integer.parseInt(salesBean.getYear()))+"\n");
				
				htmlStr.append("</td> \n");
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(SAInitial.getInstance().TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
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
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(SAInitial.getInstance().GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
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
	
	
	private static StringBuffer genReportHtml(String contextPath,String groupBy,ConfigBean configBeanHead,List colGroupList,List colDispList ,ResultSet rs,String summaryType) throws Exception{
		
		StringBuffer htmlStr = new StringBuffer("");
		boolean found = false;
		Map<String,BigDecimal> summaryColumnMap = new HashMap<String,BigDecimal>();
		Map<String,BigDecimal> summaryPerMap1 = new HashMap<String,BigDecimal>();
		Map<String,BigDecimal> summaryPercentMap1 = new HashMap<String,BigDecimal>();
		BigDecimal bigZero = new BigDecimal("0");
		BigDecimal big100 = new BigDecimal("100");
		
		int totalRecord = 0;
		int no =0;
		int columnCount = 3; //default
		String cSpan = "";
		boolean isSummry = false;
		if(SAInitial.SUMMARY_TYPE_SUM.equals(summaryType) 
			|| SAInitial.SUMMARY_TYPE_AVG.equals(summaryType)
			|| SAInitial.SUMMARY_TYPE_PERCENT.equals(summaryType)){
			isSummry = true;
		}
		
		boolean isPercent = false;
		if(SAInitial.SUMMARY_TYPE_PERCENT.equals(summaryType)){
			isPercent = true;
		}
		
        //Set Function enable or disable
		boolean isDebug = false;
		boolean hideRowEnable = true;
		boolean isFoundDataInRow = false;
		try{
			debug.debug("colGroupList Size:"+colGroupList.size(),1);

			
			//style='border:1px solid black; '
			htmlStr.append("<table  width='100%' class='result2' id='sort-table' cellpadding='4' cellspacing='2'> <thead> \n");
			
			/***** Header Table ******************************/
			htmlStr.append("<tr> \n");
			/** Rows Display**/
			htmlStr.append(" <th rowspan='2'>ลำดับ</th>  \n");
			
			/*if(!isNoDisplayed){
				debug.debug("groupBy:"+groupByBean.value);
				if(!(groupByBean.value.equalsIgnoreCase("SALES_ORDER_DATE") || groupByBean.value.equalsIgnoreCase("Invoice_Date"))){
					htmlStr.append(" <th rowspan='2'>รหัส</th> \n");
				}
			}*/
			/** Get All Column Show **/
			int colDispSize = 1;
			String columnsDisp = SAGenCondition.genColumnDispAll(groupBy);
			debug.debug("columnsDisp:"+columnsDisp);
	    	String [] columnsArr = columnsDisp.split("\\,");
	    	if(columnsArr.length==1){
	    		columnsArr = new String[1];
	    		columnsArr[0] = columnsDisp;
	    	}
	    	 debug.debug("columnsArr length:"+columnsArr.length+":columnsArr[i]:"+columnsArr[0]);
	    	 
	    	 for(int i=0;i<columnsArr.length;i++){
			    htmlStr.append(" <th rowspan='2'>"+columnsArr[i]+"</th>  \n");
			    colDispSize++;
	    	}
			
			for(int i=0;i<colGroupList.size();i++){
				ConfigBean configBean = (ConfigBean)colGroupList.get(i);
				debug.debug("colGroupList:"+configBean.getDispText());
				htmlStr.append("<th colspan='"+colDispList.size()+"'>"+configBean.getDispText()+"</th> \n");
			}
			
			if(isSummry){
				String label = "ผลรวม";
				if(SAInitial.SUMMARY_TYPE_AVG.equals(summaryType)){
					label = "ค่าเฉลี่ย";
				}else if(SAInitial.SUMMARY_TYPE_PERCENT.equals(summaryType)){
					ConfigBean colGroup1 = (ConfigBean)colGroupList.get(colGroupList.size()-2);
					ConfigBean colGroup2 = (ConfigBean)colGroupList.get(colGroupList.size()-1);
					
					label = "เปอร์เซ็นต์  <br/> ("+colGroup2.getDispText()+" เทียบ "+colGroup1.getDispText()+")";
				}
				
				htmlStr.append("<th colspan='"+colGroupList.size()+"'>"+label+"</th> \n");
			}
			
			htmlStr.append("</tr>\n");
			htmlStr.append("<tr> \n");
			/** Display Column **/
			for(int i=0;i<colGroupList.size();i++){
				ConfigBean configGroupBean = (ConfigBean)colGroupList.get(i);
				for(int d=0;d<colDispList.size();d++){
					ConfigBean configBean = (ConfigBean)colDispList.get(d);
					String columnOrder = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
					debug.debug("ColumnOrder:"+columnOrder,1);
					
					String sortIdKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName()); 
					
				    htmlStr.append("<th nowrap>"+SAUtils.genColDispName(configBean.getDispText())+"&nbsp;&nbsp;");
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
					debug.debug("ColumnOrder:"+columnOrder,1);
					
					String sortIdKey = summaryType+"_"+configBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName()); 
							
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
			/** Get All Column Show **/

			String columnsDisp2 = SAGenCondition.genColumnAll(groupBy);
			debug.debug("columnsDisp2:"+columnsDisp2);
	    	String [] columnsArr2 = columnsDisp2.split("\\,");
	    	if(columnsArr2.length==1){
	    		columnsArr2 = new String[1];
	    		columnsArr2[0] = columnsDisp2;
	    	}
	    	
			StringBuffer rowHtml = new StringBuffer("");
			while(rs.next()){
				found = true;
				isFoundDataInRow = false;
				StringBuffer rowNoHtml = new StringBuffer("");

		    	for(int i=0;i<columnsArr2.length;i++){
		           debug.debug("columnName2:"+columnsArr2[i]);
		           
					if("SALES_DATE".equalsIgnoreCase(columnsArr2[i])){
						String dateStr = Utils.stringValue(new Date(rs.getDate(columnsArr2[i],Calendar.getInstance(Locale.US)).getTime()),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						debug.debug("dateStr:"+dateStr);
					    rowHtml.append(" <td>"+dateStr+"</td>  \n");
					}else{
						//if(!isNoDisplayed)
						rowHtml.append(" <td align='left'>"+rs.getString(columnsArr2[i])+"</td> \n");
						//rowHtml.append(" <td align='left'>"+rs.getString(columnsArr[i]+"_DESC")+"</td>  \n");
					}
		     	}
				/** Calculation Summary By For Loop  ***/
				Map<String,BigDecimal> summaryRowMap = new HashMap<String,BigDecimal>();
				
				for(int i=0;i<colGroupList.size();i++){
					ConfigBean configGroupBean = (ConfigBean)colGroupList.get(i);
					
					debug.debug("colDispList:"+colDispList.size());
					BigDecimal value = null;
					BigDecimal valueRowSummary = null;
					BigDecimal valueColSummary = null;
				
					for(int d=0;d<colDispList.size();d++){
						ConfigBean configBean = (ConfigBean)colDispList.get(d);
						String resultKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
						
						debug.debug("resultKey:"+resultKey);
						
						/** Case Column CALL Summary CALL_NO DUP  and Type Summary **/
						if(resultKey.startsWith("CALL") && !resultKey.startsWith("CALL_NEW") && isSummry && SAInitial.SUMMARY_TYPE_SUM.equals(summaryType)){
							debug.debug("CALL:"+resultKey);
							value = Utils.isNullToZero(rs.getBigDecimal(resultKey));//Normal
							valueRowSummary = Utils.isNullToZero(rs.getBigDecimal(SAInitial.NO_DUP_PREFIX+resultKey));//ND_ Value
							valueColSummary = Utils.isNullToZero(rs.getBigDecimal(resultKey));//Normal
						}else{
							value = Utils.isNullToZero(rs.getBigDecimal(resultKey));//Normal Value
							valueRowSummary = Utils.isNullToZero(rs.getBigDecimal(resultKey));//for summary
							valueColSummary = Utils.isNullToZero(rs.getBigDecimal(resultKey));//Normal
						}
						/** Validate all row found data to show or not **/
						if(valueRowSummary.compareTo(bigZero) != 0){
							isFoundDataInRow = true;
						}
						
						/** Summary By Column **/						
						if(summaryColumnMap.get(resultKey) != null){
							BigDecimal summaryColValueAdd = (BigDecimal)summaryColumnMap.get(resultKey);
							summaryColValueAdd = summaryColValueAdd.add(valueColSummary);
							summaryColumnMap.put(resultKey, summaryColValueAdd);
						}else{
							summaryColumnMap.put(resultKey, valueColSummary);	
						}
		
						String resultKey1 = configBean.getName();
						/** Summary By Row **/
						if(summaryRowMap.get(resultKey1) != null){
							BigDecimal sumRowValueAdd = (BigDecimal)summaryRowMap.get(resultKey1);
							sumRowValueAdd = sumRowValueAdd.add(valueRowSummary);
							summaryRowMap.put(resultKey1, sumRowValueAdd);
						}else{
							summaryRowMap.put(resultKey1, valueRowSummary);
						}
						
						/** Summary 1 For case PER **/
						if(summaryPerMap1.get(resultKey1) != null){
							BigDecimal summaryValueAdd = (BigDecimal)summaryPerMap1.get(resultKey1);
							summaryValueAdd = summaryValueAdd.add(valueRowSummary);
							summaryPerMap1.put(resultKey1, summaryValueAdd);
						}else{
							summaryPerMap1.put(resultKey1, valueRowSummary);
						}			
						
						String debug =isDebug?"D:":"";
						
						String sortIdKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
						
					    rowHtml.append("<td align='right' class='sort_"+sortIdKey+"'>"+debug+Utils.convertDigitToDisplay(configBean.getDispText(),value)+"</td> \n");
					
					}// For 1
				}//for 2

				if(isSummry){
					/** Calculation Summary Case Percent **/
					//new 2
					for(int c=0;c<colDispList.size();c++){
						ConfigBean configBean = (ConfigBean)colDispList.get(c);
						
						debug.debug("configBean Name["+c+"]:"+configBean.getName());
						
						String resultRowSumBean = configBean.getName();
						BigDecimal valueRowSum = (BigDecimal)summaryRowMap.get(resultRowSumBean);
						boolean isPct = false;
						
						if(resultRowSumBean.startsWith("PER")){
							//Sum1
							ConfigBean configBean1 = (ConfigBean)colDispList.get(c-2);
							String resultKey1 = configBean1.getName();
							BigDecimal summaryValue1 = new BigDecimal("0");
							if(summaryRowMap != null && summaryRowMap.get(resultKey1) != null){
								summaryValue1 = (BigDecimal)summaryRowMap.get(resultKey1);
							}
							
							//Sum2
							ConfigBean configBean2 = (ConfigBean)colDispList.get(c-1);
							String resultKey2 = configBean2.getName();
							BigDecimal summaryValue2 = new BigDecimal("0");
							if(summaryRowMap != null && summaryRowMap.get(resultKey2) != null){
								summaryValue2 = (BigDecimal)summaryRowMap.get(resultKey2);
							}
							//debug.debug("summaryValue1:"+summaryValue1);
							//debug.debug("summaryValue2:"+summaryValue2);
							
							if(summaryValue1.compareTo(bigZero) != 0){
								valueRowSum = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
								isPct = true;
							}
						}
						
						if(isPercent){

							ConfigBean colGroup1 = (ConfigBean)colGroupList.get(colGroupList.size()-2);
							ConfigBean colGroup2 = (ConfigBean)colGroupList.get(colGroupList.size()-1);
							
							ConfigBean configBeanP =  (ConfigBean)colDispList.get(c);
							
							//Sum1
							String resultKey1 = configBeanP.getName()+"_"+reportU.getShortColName(colGroup1.getName());
							BigDecimal summaryValue1 = Utils.isNullToZero(rs.getBigDecimal(resultKey1));
							
							//Sum2
							String resultKey2 = configBeanP.getName()+"_"+reportU.getShortColName(colGroup2.getName());
							BigDecimal summaryValue2 = Utils.isNullToZero(rs.getBigDecimal(resultKey2));
							
							debug.debug("summaryValue1:"+summaryValue1,1);
							debug.debug("summaryValue2:"+summaryValue2,1);
							
							if(summaryValue1.compareTo(bigZero) != 0){
								valueRowSum = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
								isPct = true;
							}
						}
						
						//
						
						if(SAInitial.SUMMARY_TYPE_AVG.equals(summaryType) && !isPct){
							valueRowSum = valueRowSum.divide(BigDecimal.valueOf(colGroupList.size()), 2, BigDecimal.ROUND_HALF_UP);
						}
						//debug
						String debug = isDebug?"RSum:":"";
						
						ConfigBean configGroupBean1 = (ConfigBean)colGroupList.get(0);
						String sortIdKey = summaryType+"_"+configBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName()); 
						
						rowHtml.append("<td align='right' class='sort_"+sortIdKey+"'>"+debug+Utils.convertDigitToDisplay(configBean.getDispText(),valueRowSum)+"</td> \n");

					}//for
				}//if

				rowHtml.append("</tr>\n");
				
				/** For Check Row Sum Case ==0 Hide Row  **/
				BigDecimal rowSumAll = bigZero;
				for(int d=0;d<colDispList.size();d++){
					ConfigBean configBean = (ConfigBean)colDispList.get(d);
					String resultRowSumBean = configBean.getName();
					BigDecimal valueRowSum = (BigDecimal)summaryRowMap.get(resultRowSumBean);
					debug.debug("before["+resultRowSumBean+"] valueRowSum["+valueRowSum.doubleValue()+"]");
					
					if(valueRowSum.doubleValue() <0){
						valueRowSum = valueRowSum.multiply(new BigDecimal("-1"));
						debug.debug("valueRowSum<0");
					}
					debug.debug("after["+resultRowSumBean+"] valueRowSum["+valueRowSum.doubleValue()+"]");
					
					rowSumAll = rowSumAll.add(valueRowSum);
					
				}
				//Show All Row
				if(hideRowEnable==false){
					 no++;
					 rowNoHtml.append("  <tr> \n");
					 rowNoHtml.append(" <td>"+no+"</td>  \n");
						
					 htmlStr.append(rowNoHtml);
					 htmlStr.append(rowHtml.toString());
					 totalRecord++;
				}else{
					if(bigZero.compareTo(rowSumAll) !=0 || isFoundDataInRow ==true){
					   no++;
					   rowNoHtml.append("  <tr> \n");
					   rowNoHtml.append(" <td>"+no+"</td>  \n");
						
					   htmlStr.append(rowNoHtml);
					   htmlStr.append(rowHtml.toString());
					   totalRecord++;
					}
				}
				//reset RowHtml
				rowHtml = new StringBuffer("");
				
				
			}//while row data all
			
			htmlStr.append("</tbody> \n");
			/***************************************************/
			
			/****  Summary  ***********************************/
			htmlStr.append("<tfoot> <tr> \n");
			
			//Summary Column 
			StringBuffer summaryRowHtml = new StringBuffer("");
			StringBuffer summaryRowHtml2 = new StringBuffer("");
			
			BigDecimal summaryAll = new BigDecimal("0");
			
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
						//For Check Hide Summary 
						summaryAll = summaryAll.add(summaryValue);
						
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
							debug.debug("summaryValue1:"+summaryValue1);
							debug.debug("summaryValue2:"+summaryValue2);
							
							if(summaryValue1.compareTo(bigZero) != 0){
								summaryValue = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
							}
						}
							
						columnCount++;
						String debug = isDebug?"CSum:":"";
						summaryRowHtml2.append("<td class='summary' align='right'><b>"+debug+Utils.convertDigitToDisplay(configBean.getDispText(),summaryValue)+"</b></td> \n");    
						   
					}//for2
				}//for1
				
				/** Summary Col Case Percent */
				if(isPercent){
					BigDecimal colPercent = new BigDecimal("0");
					ConfigBean colGroup2 = (ConfigBean)colGroupList.get(colGroupList.size()-1);
					ConfigBean colGroup1 = (ConfigBean)colGroupList.get(colGroupList.size()-2);
					
					debug.debug("colGroup1["+colGroup1.getName()+"]",1);
					debug.debug("colGroup2["+colGroup2.getName()+"]",1);
					
					for(int d=0;d<colDispList.size();d++){
						ConfigBean configBean = (ConfigBean)colDispList.get(d);
						String colDispKey1 = configBean.getName()+"_"+reportU.getShortColName(colGroup1.getName());
						String colDispKey2 = configBean.getName()+"_"+reportU.getShortColName(colGroup2.getName());
						
						BigDecimal colValue1 = summaryColumnMap.get(colDispKey1);
						BigDecimal colValue2 = summaryColumnMap.get(colDispKey2);
						
						debug.debug("colValue1["+colValue1+"]",1);
						debug.debug("colValue2["+colValue2+"]",1);
						
						if(colValue1.compareTo(bigZero) != 0){
							
						    colPercent = (colValue2.divide(colValue1,4,BigDecimal.ROUND_FLOOR)).multiply(big100);
						    
						    debug.debug("Result colPercent["+colPercent+"]",1);
						    
						    if(summaryPercentMap1.get(colDispKey1) != null){
								BigDecimal colPercentAdd = summaryPercentMap1.get(colDispKey1);
								colPercentAdd = colPercentAdd.add(colPercent);
								summaryPercentMap1.put(colDispKey1, colPercentAdd);
							}else{
								summaryPercentMap1.put(colDispKey1, colPercent);
							}		
						}
					}//for
				}//if				
				
			}//if
			
			// Summary All case SumRow == Show not Show
			if(hideRowEnable == false ){
				 summaryRowHtml.append(" <td class='summary' colspan='"+colDispSize+"' align='right'><b>ยอดรวม  "+totalRecord+" รายการ</b></td>  \n");
				 summaryRowHtml.append(summaryRowHtml2); 
			}else{
				if(summaryAll.compareTo(bigZero)!=0 ){
				    summaryRowHtml.append(" <td class='summary' colspan='"+colDispSize+"' align='right'><b>ยอดรวม  "+totalRecord+" รายการ</b></td>  \n");
				    summaryRowHtml.append(summaryRowHtml2);
				}else{
					summaryRowHtml.append(" <td class='summary' colspan='"+colDispSize+"' align='right'><b>ยอดรวม  0  รายการ</b></td>  \n");
					summaryRowHtml.append(summaryRowHtml2);
				}
			}
			
			htmlStr.append(summaryRowHtml);
			
			if(isSummry && found){
				for(int d=0;d<colDispList.size();d++){
					ConfigBean configBean = (ConfigBean)colDispList.get(d);
					String resultRowSumBean = configBean.getName();
					BigDecimal valueRowSum = (BigDecimal)summaryPerMap1.get(resultRowSumBean);
					boolean isPct = false;
					
					if(resultRowSumBean.startsWith("PER")){
						//Sum1
						ConfigBean configBean1 = (ConfigBean)colDispList.get(d-2);
						String resultKey1 = configBean1.getName();
						BigDecimal summaryValue1 = new BigDecimal("0");
						if(summaryPerMap1 != null && summaryPerMap1.get(resultKey1) != null){
							summaryValue1 = (BigDecimal)summaryPerMap1.get(resultKey1);
						}
						
						//Sum2
						ConfigBean configBean2 = (ConfigBean)colDispList.get(d-1);
						String resultKey2 = configBean2.getName();
						BigDecimal summaryValue2 = new BigDecimal("0");
						if(summaryPerMap1 != null && summaryPerMap1.get(resultKey2) != null){
							summaryValue2 = (BigDecimal)summaryPerMap1.get(resultKey2);
						}
						//debug.debug("summaryValue1:"+summaryValue1);
						//debug.debug("summaryValue2:"+summaryValue2);
						
						if(summaryValue1.compareTo(bigZero) != 0){
							valueRowSum = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
							isPct = true;
						}
					}
					
					if(isPercent){
						ConfigBean colGroup1 = (ConfigBean)colGroupList.get(colGroupList.size()-2);
						
						ConfigBean configBeanP =  (ConfigBean)colDispList.get(d);
						
						//Sum1
						String resultKey1 = configBeanP.getName()+"_"+reportU.getShortColName(colGroup1.getName());
						BigDecimal summaryValue1 = summaryPercentMap1.get(resultKey1) != null?(BigDecimal)summaryPercentMap1.get(resultKey1):bigZero;
						
						
						debug.debug("summaryPercentValue1:"+summaryValue1,1);
						
					    valueRowSum = summaryValue1;
						
					}
					
					if(SAInitial.SUMMARY_TYPE_AVG.equals(summaryType) && !isPct){
						valueRowSum = valueRowSum.divide(BigDecimal.valueOf(colGroupList.size()), 2, BigDecimal.ROUND_HALF_UP);
					}
					
					columnCount++;
					String debug = isDebug?"TSum:":"";
					htmlStr.append("<td class='summary' align='right'><b>"+debug+Utils.convertDigitToDisplay(configBean.getDispText(),valueRowSum)+"</b></td> \n");
				}//for
			}
			htmlStr.append("</tr> </tfoot> \n");
			/*************************************************/
			
			htmlStr.append("</table> \n");
			
			/** Write For Colspan Gen Header Excel **/
			htmlStr.append("<input type='hidden' name='columnCount' value='"+columnCount+"'/> \n");
			
			FileUtil.writeFile("d://temp/data.html", htmlStr.toString());
			
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
			 colDispList.add(new ConfigBean(salesBean.getColNameDisp1()+"_1",Utils.isNull(salesBean.getColNameDisp1()),Utils.isNull(SAInitial.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp1()))+"("+Utils.isNull(SAInitial.getInstance().UNIT_MAP.get(salesBean.getColNameUnit1()))+")"));
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
			colDispList.add(new ConfigBean(salesBean.getColNameDisp2()+"_2",Utils.isNull(salesBean.getColNameDisp2()),Utils.isNull(SAInitial.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp2()))+"("+Utils.isNull(SAInitial.getInstance().UNIT_MAP.get(salesBean.getColNameUnit2()))+")"));
			
			if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
				colDispList.add(new ConfigBean("PER1","%","%"));
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			colDispList.add(new ConfigBean(salesBean.getColNameDisp3()+"_3",Utils.isNull(salesBean.getColNameDisp3()),Utils.isNull(SAInitial.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp3()))+"("+Utils.isNull(SAInitial.getInstance().UNIT_MAP.get(salesBean.getColNameUnit3()))+")"));
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			colDispList.add(new ConfigBean(salesBean.getColNameDisp4()+"_4",Utils.isNull(salesBean.getColNameDisp4()),Utils.isNull(SAInitial.getInstance().DISP_COL_MAP.get(salesBean.getColNameDisp4()))+"("+Utils.isNull(SAInitial.getInstance().UNIT_MAP.get(salesBean.getColNameUnit4()))+")"));
			
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
