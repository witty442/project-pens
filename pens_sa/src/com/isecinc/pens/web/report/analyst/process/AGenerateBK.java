
package com.isecinc.pens.web.report.analyst.process;

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

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.report.analyst.bean.ABean;
import com.isecinc.pens.web.report.analyst.bean.ConfigBean;
import com.isecinc.pens.web.report.analyst.bean.CriteriaBean;
import com.isecinc.pens.web.report.analyst.helper.AUtils;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Debug;
import com.pens.util.Utils;



/**
 * @author WITTY
 *
 */
public class AGenerateBK {
   
	public static  Debug debug = new Debug(true,Debug.level_0);//debug all
	//public static  Debug debug = new Debug(true,Debug.level_1);//debug some by user
    
	public static AUtils reportU = new AUtils();
	public static AInitial aInit = null;
	
	/** Logger */
	protected Logger logger = Logger.getLogger("PENS");
	
	public AGenerateBK(AInitial aaInit){
		aInit = aaInit;
	}
	/**
	 * Gen Report 
	 * @param salesBean
	 * @return
	 * @throws Exception
	 */
	public List<StringBuffer> genReport(Connection conn,String contextPath,User user,ABean salesBean) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ConfigBean> colGroupList = new ArrayList<ConfigBean>();
		ConfigBean groupByBean = null;
		String allCond = "";
		List<StringBuffer> resultList = new ArrayList<StringBuffer>();
		CriteriaBean criBean = new CriteriaBean();
		AGenerateSQL aSQL = new AGenerateSQL(aInit);
		try{
			if(AConstants.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
				/** Set Group Display  **/
				if( !StringUtils.isEmpty(salesBean.getDay()) && !StringUtils.isEmpty(salesBean.getDayTo())){
					Date startDate = DateUtil.convertStringToDate(salesBean.getDay());
					Date endDate = DateUtil.convertStringToDate(salesBean.getDayTo());
					
					if(startDate.compareTo(endDate) != 0){
						while(startDate.compareTo(endDate) <= 0){
							colGroupList.add(new ConfigBean(DateUtil.convertToString(startDate,"yyyyMMdd"),DateUtil.convertToString(startDate),DateUtil.convertToString(startDate)));
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
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(aInit.GROUP_BY_MAP.get(salesBean.getGroupBy())));
			   
				StringBuffer sql = aSQL.genMainSql(conn,user,salesBean,colGroupList,criBean);
				if( !Utils.isNull(sql.toString()).equals("")){
				    ps = conn.prepareStatement(sql.toString());
				    debug.debug("DateStr:"+salesBean.getDay());
				    debug.debug("Date:"+DateUtil.parseToBudishDate(salesBean.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH));
				    rs = ps.executeQuery();
				    
				    /** Generate HTML Code **/
				    resultList.add(genReportHtml(contextPath,groupByBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(AConstants.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkMonth:"+salesBean.getChkMonth().length);
				
				String yearCri = "";
				String monthCri = "";
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkMonth().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkMonth()[i]+"]");
					
					colGroupList.add(new ConfigBean(salesBean.getChkMonth()[i],salesBean.getChkMonth()[i],Utils.isNull(aInit.MONTH_MAP.get(salesBean.getChkMonth()[i]))));
					if(i != salesBean.getChkMonth().length-1){
					   allCond +="'"+salesBean.getChkMonth()[i]+"',";
					}else{
					   allCond +="'"+salesBean.getChkMonth()[i]+"'";
					}
					/*if(i != salesBean.getChkMonth().length-1){
					   yearCri +="'"+(salesBean.getChkMonth()[i]).substring(0,4)+"',"; 
					   monthCri +="'"+(salesBean.getChkMonth()[i]).substring(4,6)+"',"; 
					}else{
					   yearCri +="'"+(salesBean.getChkMonth()[i]).substring(0,4)+"'"; 
					   monthCri +="'"+(salesBean.getChkMonth()[i]).substring(4,6)+"'"; 
					}*/
					
				}
				criBean.setMonth(monthCri);
				criBean.setYear(yearCri);
				criBean.setAllCond(allCond);
				if(colGroupList.size() > 0){
					Collections.sort(colGroupList);
				}
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(aInit.GROUP_BY_MAP.get(salesBean.getGroupBy())));
				
				logger.debug("AGen:tableName:"+aInit.TABLE_VIEW);
				
				StringBuffer sql = aSQL.genMainSql(conn,user,salesBean,colGroupList,criBean);
				debug.debug("sql Month:"+sql.toString());
				if( !Utils.isNull(sql.toString()).equals("")){
				    ps = conn.prepareStatement(sql.toString());
				    rs = ps.executeQuery();
				    debug.debug("execute sql Month:");
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,groupByBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(AConstants.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkQuarter:"+salesBean.getChkQuarter().length);
				String yearCri = "";
				String quarterCri = "";
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkQuarter().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkQuarter()[i]+"]",1);
					colGroupList.add(new ConfigBean(salesBean.getChkQuarter()[i],salesBean.getChkQuarter()[i],Utils.isNull(aInit.QUARTER_MAP.get(salesBean.getChkQuarter()[i]))));
					if(i != salesBean.getChkQuarter().length-1){
						allCond +="'"+salesBean.getChkQuarter()[i]+"',";
					}else{
						allCond +="'"+salesBean.getChkQuarter()[i]+"'";
					}
					
					/*if(i != salesBean.getChkQuarter().length-1){
					   yearCri +="'"+(salesBean.getChkQuarter()[i]).substring(0,4)+"',"; 
					   quarterCri +="'"+(salesBean.getChkQuarter()[i]).substring(4,5)+"',"; 
					}else{
					   yearCri +="'"+(salesBean.getChkQuarter()[i]).substring(0,4)+"'"; 
					   quarterCri +="'"+(salesBean.getChkQuarter()[i]).substring(4,5)+"'"; 
					}*/
				}
				criBean.setQuarter(quarterCri);
				criBean.setYear(yearCri);
				criBean.setAllCond(allCond);
				
				if(colGroupList.size() > 0){
					Collections.sort(colGroupList);
				}
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(aInit.GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = aSQL.genMainSql(conn,user,salesBean,colGroupList,criBean);
				if( !Utils.isNull(sql.toString()).equals("")){
				    ps = conn.prepareStatement(sql.toString());
				    rs = ps.executeQuery();
				    
				    /** Gen Html Code **/
				    resultList.add(genReportHtml(contextPath,groupByBean,colGroupList,setDispCol(salesBean),rs,salesBean.getSummaryType()));
				    resultList.add(sql);
				}
				
			}else if(AConstants.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				debug.debug("chkYear:"+salesBean.getChkYear().length);
				/** Set Group Display  **/
				for(int i=0;i<salesBean.getChkYear().length;i++){
					debug.debug("name:["+i+"]value:["+salesBean.getChkYear()[i]+"]");
					colGroupList.add(new ConfigBean(salesBean.getChkYear()[i],salesBean.getChkYear()[i],DateUtil.convertToYearBushdish(Integer.parseInt(salesBean.getChkYear()[i]))+"" ));
					if(i != salesBean.getChkYear().length-1){
					  allCond +="'"+salesBean.getChkYear()[i]+"',";
					}else{
					  allCond +="'"+salesBean.getChkYear()[i]+"'";
					}
				}
				debug.debug("allCond:"+allCond);
				criBean.setAllCond(allCond);
				
				/** Display group by **/
				groupByBean = new ConfigBean(salesBean.getGroupBy(),salesBean.getGroupBy(),Utils.isNull(aInit.GROUP_BY_MAP.get(salesBean.getGroupBy())));
				StringBuffer sql = aSQL.genMainSql(conn,user,salesBean,colGroupList,criBean);
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
			debug.info(e.getMessage());
			e.printStackTrace();
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
 * Gen Header Excel
 * @param salesBean
 * @return
 * @throws Exception
 */
   public  StringBuffer genHeaderReportExportExcel(User user,ABean salesBean,String columnCount,String condDisp1,String condDisp2,String condDisp3,String condDisp4,String condDisp5) throws Exception{
		
		StringBuffer htmlStr = new StringBuffer("");
		try{
			htmlStr.append("<table border='1' width='100%' cellpadding='3' cellspacing='1' class='result'> \n");
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'><b>Sales Analysis Report</b></td>  \n");
			htmlStr.append("</tr> \n");
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'> "+new String(condDisp5.getBytes("ISO8859_1"), "TIS-620")+"  </td>  \n") ;
			htmlStr.append("</tr> \n");
			
			if(AConstants.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				if(!StringUtils.isEmpty(salesBean.getDay()) && !StringUtils.isEmpty(salesBean.getDayTo()))
					htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : วัน  : "+salesBean.getDay()+" - "+salesBean.getDayTo()+"</td>  \n");
				else
					htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : วัน  : "+(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo())+"</td>  \n");
				
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(aInit.GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(AConstants.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : เดือน  :  \n");
					for(int i=0;i<salesBean.getChkMonth().length;i++){
						if(i==salesBean.getChkMonth().length-1){
					       htmlStr.append( Utils.isNull(aInit.MONTH_MAP.get(salesBean.getChkMonth()[i])));
						}else{
						   htmlStr.append( Utils.isNull(aInit.MONTH_MAP.get(salesBean.getChkMonth()[i])) +", ");
						}
					}
				htmlStr.append("   พ.ศ. :"+DateUtil.convertToYearBushdish(Integer.parseInt(salesBean.getYear()))+"\n");
				
				htmlStr.append("</td> \n");
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(aInit.GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(AConstants.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : ไตรมาส  :  \n");
					for(int i=0;i<salesBean.getChkQuarter().length;i++){
						if(i==salesBean.getChkQuarter().length-1){
					       htmlStr.append( Utils.isNull(aInit.QUARTER_MAP.get(salesBean.getChkQuarter()[i])));
						}else{
						   htmlStr.append( Utils.isNull(aInit.QUARTER_MAP.get(salesBean.getChkQuarter()[i])) +", ");
						}
					}
				htmlStr.append("   พ.ศ. "+DateUtil.convertToYearBushdish(Integer.parseInt(salesBean.getYear()))+"\n");
				
				htmlStr.append("</td> \n");
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(aInit.GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}else if(AConstants.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : ปี  :  \n");
					for(int i=0;i<salesBean.getChkYear().length;i++){
						if(i==salesBean.getChkYear().length-1){
					       htmlStr.append(DateUtil.convertToYearBushdish(Integer.parseInt(salesBean.getChkYear()[i])));
						}else{
						   htmlStr.append(DateUtil.convertToYearBushdish(Integer.parseInt(salesBean.getChkYear()[i])) +", ");
						}
					}
				htmlStr.append("</td> \n");
				htmlStr.append("</tr> \n");
				htmlStr.append("<tr> \n");
				htmlStr.append(" <td colspan='"+columnCount+"'>จัดกลุ่มตาม : "+Utils.isNull(aInit.GROUP_BY_MAP.get(salesBean.getGroupBy()))+"</th>  \n");
				htmlStr.append("</tr> \n");
				
			}
			
			//Gen Condition
			genConditionDisp(htmlStr,salesBean,columnCount,condDisp1,condDisp2,condDisp3,condDisp4);
			
		    /** Gen Timestamp **/
			Calendar currentTime = Calendar.getInstance(new Locale("th","TH"));
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td colspan='"+columnCount+"'>Exported date :"+DateUtil.stringValue(currentTime.getTime(), DateUtil.DD_MM_YYYY_HH_MM_SS_WITH_SLASH,new Locale("th","TH"))+"");
			htmlStr.append(" ,Created by:"+user.getName()+" </td> \n");
			htmlStr.append("</tr> \n");
			htmlStr.append("</table> \n");
			
		  return htmlStr;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
	private static StringBuffer genReportHtml(String contextPath,ConfigBean groupByBean,List colGroupList,List colDispList ,ResultSet rs,String summaryType) throws Exception{
		List<ConfigBean> rowCodeList = new ArrayList<ConfigBean>();
		StringBuffer htmlStr = new StringBuffer("");
		boolean found = false;
		Map<String,BigDecimal> summaryRowMap = new HashMap<String,BigDecimal>();
		Map<String,BigDecimal> summaryAllRowMap = new HashMap<String,BigDecimal>();
		Map<String,BigDecimal> summaryColumnMap = new HashMap<String,BigDecimal>();
		Map<String,BigDecimal> summaryPerMap = new HashMap<String,BigDecimal>();
		Map<String,BigDecimal> summaryPercentMap = new HashMap<String,BigDecimal>();
		BigDecimal bigZero = new BigDecimal("0");
		BigDecimal big100 = new BigDecimal("100");
		BigDecimal conPercent = null;
		String colSumKey = "";
		BigDecimal colSum = null;
		String rowSumKey = "";
		BigDecimal rowSum = null;
		String conIDKey = "";
		int i=0,gc=0,r=0,col=0;
		BigDecimal value = null;
		BigDecimal valueRowSummary = null;
		BigDecimal valueColSummary = null;
		String resultRowSumBean ="";
		BigDecimal valueRowSum =null;
		BigDecimal rowSumAll = null;
		String keySumRowMap = "";
		int totalRecord = 0;
		int no =0;
		int columnCount = 3; //default
		String[] arrayfldNoDisplayCode=new String[]{"อำเภอ","จังหวัด","ตำบล","วันที่(Order)","วันที่ขาย(Invoice)","เลขที่สั่งซื้อ (Order No.)","เลขที่ขาย (Invoice No.)","ร้านค้า-ที่ส่งสินค้า","ร้านค้า-ที่ส่งบิล"};
		
		boolean isSummry = false;
		if(AConstants.SUMMARY_TYPE_SUM.equals(summaryType) 
			|| AConstants.SUMMARY_TYPE_SUM_CONTRIBUTE.equals(summaryType) 
			|| AConstants.SUMMARY_TYPE_AVG.equals(summaryType)
			|| AConstants.SUMMARY_TYPE_PERCENT.equals(summaryType)){
			isSummry = true;
		}
		
		boolean isPercent = false;
		if(AConstants.SUMMARY_TYPE_PERCENT.equals(summaryType)){
			isPercent = true;
		}
		
        //Set Function enable or disable
		boolean isDebug = false;
		boolean hideRowEnable = true;
		boolean isFoundDataInRow = false;
		ConfigBean colDispBean = null;
		ConfigBean configGroupBean = null;
		try{
			debug.debug("colGroupList Size:"+colGroupList.size(),1);
			
			String groupBy = groupByBean.getDispText();
			
			Boolean isNoDisplayed = ArrayUtils.contains(arrayfldNoDisplayCode,groupBy); 
		
			//style='border:1px solid black; '
			htmlStr.append("<table  width='100%' class='result2' id='sort-table' cellpadding='4' cellspacing='2'> <thead> \n");
			
			/***** Header Table ******************************/
			htmlStr.append("<tr> \n");
			/** Rows Display**/
			htmlStr.append(" <th rowspan='2'>ลำดับ</th>  \n");
			
			if(!isNoDisplayed){
				debug.debug("groupBy:"+groupByBean.getValue());
				if(!(groupByBean.getValue().equalsIgnoreCase("SALES_ORDER_DATE") || groupByBean.getValue().equalsIgnoreCase("Invoice_Date"))){
					htmlStr.append(" <th rowspan='2'>รหัส</th> \n");
				}
			}
			
			htmlStr.append(" <th rowspan='2'>"+groupBy+"</th>  \n");
			for(i=0;i<colGroupList.size();i++){
				ConfigBean configBean = (ConfigBean)colGroupList.get(i);
				htmlStr.append("<th colspan='"+colDispList.size()+"'>"+configBean.getDispText()+"</th> \n");
			}
			
			if(isSummry){
				String label = "ผลรวม";
				if(AConstants.SUMMARY_TYPE_AVG.equals(summaryType)){
					label = "ค่าเฉลี่ย";
				}else if(AConstants.SUMMARY_TYPE_PERCENT.equals(summaryType)){
					ConfigBean colGroup1 = (ConfigBean)colGroupList.get(colGroupList.size()-2);
					ConfigBean colGroup2 = (ConfigBean)colGroupList.get(colGroupList.size()-1);
					
					label = "เปอร์เซ็นต์  <br/> ("+colGroup2.getDispText()+" เทียบ "+colGroup1.getDispText()+")";
				}
				if(AConstants.SUMMARY_TYPE_SUM_CONTRIBUTE.equals(summaryType)){
					htmlStr.append("<th colspan='"+colDispList.size()+"'>"+label+"</th> \n");
					htmlStr.append("<th colspan='"+colDispList.size()+"'>% Contribute</th> \n");
				}else{
				    htmlStr.append("<th colspan='"+colDispList.size()+"'>"+label+"</th> \n");
				}
			}
			
			htmlStr.append("</tr>\n");
			
			htmlStr.append("<tr> \n");
			/** Display Column **/
			for(i=0;i<colGroupList.size();i++){
				configGroupBean = (ConfigBean)colGroupList.get(i);
				for( col=0;col<colDispList.size();col++){
					colDispBean = (ConfigBean)colDispList.get(col);
					String columnOrder = colDispBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
					debug.debug("ColumnOrder:"+columnOrder,0);
					String sortIdKey = colDispBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName()); 
					
					if(AConstants.SUMMARY_TYPE_SUM_CONTRIBUTE.equals(summaryType)){
						htmlStr.append("<th nowrap>"+colDispBean.getDispText()+"&nbsp;&nbsp;");
					    htmlStr.append("  <img style=\"cursor:pointer\" src='"+contextPath+"/icons/img_sort-asc.png' width='20px' height='20px' href='#' class='link-sort asc' id='"+sortIdKey+"'/>");
					    htmlStr.append("  &nbsp;&nbsp;");
					    htmlStr.append("  <img style=\"cursor:pointer\" src='"+contextPath+"/icons/img_sort-desc.png' width='20px' height='20px' href='#' class='link-sort desc' id='"+sortIdKey+"'/>");
					    htmlStr.append("</th> \n");
					   
					}else{
						htmlStr.append("<th nowrap>"+colDispBean.getDispText()+"&nbsp;&nbsp;");
					    htmlStr.append("  <img style=\"cursor:pointer\" src='"+contextPath+"/icons/img_sort-asc.png' width='20px' height='20px' href='#' class='link-sort asc' id='"+sortIdKey+"'/>");
					    htmlStr.append("  &nbsp;&nbsp;");
					    htmlStr.append("  <img style=\"cursor:pointer\" src='"+contextPath+"/icons/img_sort-desc.png' width='20px' height='20px' href='#' class='link-sort desc' id='"+sortIdKey+"'/>");
					    htmlStr.append("</th> \n");
					}
				}//for 2
			}//for 1
			
			//new Add Head
			if(isSummry){
					ConfigBean configGroupBean1 = (ConfigBean)colGroupList.get(0);
					for(col=0;col<colDispList.size();col++){
						colDispBean = (ConfigBean)colDispList.get(col);
						String columnOrder = colDispBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName());
						debug.debug("ColumnOrder:"+columnOrder,1);
						
						String sortIdKey = summaryType+"_"+colDispBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName()); 
					
						htmlStr.append("<th>"+colDispBean.getDispText()+"&nbsp;&nbsp;");
					    htmlStr.append(" <img  style=\"cursor:pointer\" src='"+contextPath+"/icons/img_sort-asc.png' width='20px' height='20px' href='#' class='link-sort asc' id='"+sortIdKey+"'/>");
					    htmlStr.append(" &nbsp;&nbsp;");
					    htmlStr.append(" <img  style=\"cursor:pointer\" src='"+contextPath+"/icons/img_sort-desc.png' width='20px' height='20px' href='#' class='link-sort desc' id='"+sortIdKey+"'/>");
					    htmlStr.append("</th> \n");
						
					}//for 
					
					if(AConstants.SUMMARY_TYPE_SUM_CONTRIBUTE.equals(summaryType)){
						for(col=0;col<colDispList.size();col++){
						    colDispBean = (ConfigBean)colDispList.get(col);
							String columnOrder = colDispBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName());
							debug.debug("ColumnOrder:"+columnOrder,1);
							
							String sortIdKey = summaryType+"_"+colDispBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName()); 
							String debug =isDebug?"Con:":"";
							
							//%Contribute
						    htmlStr.append("<th nowrap> "+debug+colDispBean.getDispText()+"&nbsp;&nbsp;");
						    htmlStr.append("  <img style=\"cursor:pointer\" src='"+contextPath+"/icons/img_sort-asc.png' width='20px' height='20px' href='#' class='link-sort asc' id='"+sortIdKey+"'/>");
						    htmlStr.append("  &nbsp;&nbsp;");
						    htmlStr.append("  <img style=\"cursor:pointer\" src='"+contextPath+"/icons/img_sort-desc.png' width='20px' height='20px' href='#' class='link-sort desc' id='"+sortIdKey+"'/>");
						    htmlStr.append("</th> \n");
						}//for
					}//if
			}//if
			
			htmlStr.append("</tr>  </thead> \n");
			/**************** Header Table *********************************/
			
			
			/***************** Rows Data ***********************************/
			htmlStr.append("<tbody> \n");
			
			StringBuffer rowHtml = new StringBuffer("");
			int rowNo=0;
			while(rs.next()){
				rowNo++;
				found = true;
				isFoundDataInRow = false;
				StringBuffer rowNoHtml = new StringBuffer("");
                debug.debug("rs next groupByBean.getName():"+groupByBean.getName());
                
				if("Invoice_Date".equalsIgnoreCase(groupByBean.getName())){
					String dateStr = DateUtil.stringValue(new Date(rs.getDate(groupByBean.getName()+"_DESC",Calendar.getInstance(Locale.US)).getTime()),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					debug.debug("dateStr:"+dateStr);
					if(!isNoDisplayed)
				    rowHtml.append(" <td class='text'>"+dateStr+"</td>  \n");

				}else if("SALES_ORDER_DATE".equalsIgnoreCase(groupByBean.getName())){
					String dateStr = DateUtil.stringValue(new Date(rs.getDate(groupByBean.getName()+"_DESC",Calendar.getInstance(Locale.US)).getTime()),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					debug.debug("dateStr:"+dateStr);
					if(!isNoDisplayed)
				       rowHtml.append(" <td class='text'>"+dateStr+"</td>  \n");
					
				}else if("SALES_ORDER_NO".equalsIgnoreCase(groupByBean.getName())
						|| "INVOICE_NO".equalsIgnoreCase(groupByBean.getName())){
					if(!isNoDisplayed)
						rowHtml.append(" <td align='left' class='text'>"+rs.getString(groupByBean.getName()+"_CODE")+"</td> \n");
					
					//rowHtml.append(" <td align='left' style='mso-number-format:@'>"+rs.getString(groupByBean.getName()+"_DESC")+"</td>  \n");
					rowHtml.append(" <td align='left' class='text'>"+rs.getString(groupByBean.getName()+"_DESC")+"</td>  \n");
				}else{
					if(!isNoDisplayed)
					   rowHtml.append(" <td align='left' class='text'>"+rs.getString(groupByBean.getName()+"_CODE")+"</td> \n");
					
					rowHtml.append(" <td align='left' class='text'>"+rs.getString(groupByBean.getName()+"_DESC")+"</td>  \n");
				}
				
				/** Calculation Summary All Row For Loop  ***/
				summaryRowMap = new HashMap<String,BigDecimal>();
				debug.debug("**** Start SummaryRowMap Row["+rowNo+"] ****");
				for(gc=0;gc<colGroupList.size();gc++){
					ConfigBean rowGroupBean = (ConfigBean)colGroupList.get(gc);
					debug.debug("rowGroupBean.getName():"+rowGroupBean.getName());
					//debug.debug("colDispList:"+colDispList.size());
					
					value = null;
					valueRowSummary = null;
					valueColSummary = null;
				
					for(col=0;col<colDispList.size();col++){
						colDispBean = (ConfigBean)colDispList.get(col);
						String resultKey = colDispBean.getName()+"_"+reportU.getShortColName(rowGroupBean.getName());
						
						debug.debug("resultKey:"+resultKey);
						
						/** Case Column CALL Summary CALL= NP_CALL+key(Get distinct customer) DUP  and Type Summary **/
						if(resultKey.startsWith("CALL") && !resultKey.startsWith("CALL_NEW") && isSummry && AConstants.SUMMARY_TYPE_SUM.equals(summaryType)){
							debug.debug("CALL:"+resultKey);
							/** Sum No Dup **/
							//value = Utils.isNullToZero(rs.getBigDecimal(AConstants.NO_DUP_PREFIX+resultKey));//ND_Normal
							valueRowSummary = Utils.isNullToZero(rs.getBigDecimal(AConstants.NO_DUP_PREFIX+resultKey));//ND_ Value
							
							/** Sum Dup **/
							value = Utils.isNullToZero(rs.getBigDecimal(resultKey));//ND_Normal
							//valueRowSummary = Utils.isNullToZero(rs.getBigDecimal(resultKey));//ND_ Value
							
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
		
						String resultKey1 = colDispBean.getName();
						/** Summary By Row **/
						if(summaryRowMap.get(resultKey1) != null){
							BigDecimal sumRowValueAdd = (BigDecimal)summaryRowMap.get(resultKey1);
							sumRowValueAdd = sumRowValueAdd.add(valueRowSummary);
							summaryRowMap.put(resultKey1, sumRowValueAdd);
						}else{
							summaryRowMap.put(resultKey1, valueRowSummary);
						}
						
						/** Summary 1 For case PER **/
						if(summaryPerMap.get(resultKey1) != null){
							BigDecimal summaryValueAdd = (BigDecimal)summaryPerMap.get(resultKey1);
							summaryValueAdd = summaryValueAdd.add(valueRowSummary);
							summaryPerMap.put(resultKey1, summaryValueAdd);
						}else{
							summaryPerMap.put(resultKey1, valueRowSummary);
						}			
						
						String debug =isDebug?"D:":"";
						
						String sortIdKey = colDispBean.getName()+"_"+reportU.getShortColName(rowGroupBean.getName());
						
					    rowHtml.append("<td align='right' class='sort_"+sortIdKey+"'>"+debug+Utils.convertDigitToDisplay(colDispBean.getDispText(),value)+"</td> \n");
					
					}// For 1
				}//for 2
				
				if(isSummry){
					/** Calculation Summary Case Percent **/
					//new 2
					for(col=0;col<colDispList.size();col++){
					    colDispBean = (ConfigBean)colDispList.get(col);
						
						debug.debug("configBean Name["+col+"]:"+colDispBean.getName());
						
						resultRowSumBean = colDispBean.getName();
						valueRowSum = (BigDecimal)summaryRowMap.get(resultRowSumBean);
						debug.debug("RSumXX["+colDispBean.getName()+"]value["+valueRowSum+"]");
						boolean isPct = false;
						
						//set summary all row By SUM_ROW_CodeRow+colDispName
						keySumRowMap = "SUM_ROW_"+rs.getString(groupByBean.getName()+"_CODE")+"_"+colDispBean.getName();
						summaryAllRowMap.put(keySumRowMap, valueRowSum);
						
						if(resultRowSumBean.startsWith("PER")){
							//Sum1
							ConfigBean configBean1 = (ConfigBean)colDispList.get(col-2);
							String resultKey1 = configBean1.getName();
							BigDecimal summaryValue1 = new BigDecimal("0");
							if(summaryRowMap != null && summaryRowMap.get(resultKey1) != null){
								summaryValue1 = (BigDecimal)summaryRowMap.get(resultKey1);
							}
							
							//Sum2
							ConfigBean configBean2 = (ConfigBean)colDispList.get(col-1);
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
						
						//Calc Percent
						if(isPercent){
							ConfigBean colGroup1 = (ConfigBean)colGroupList.get(colGroupList.size()-2);
							ConfigBean colGroup2 = (ConfigBean)colGroupList.get(colGroupList.size()-1);
							
							ConfigBean colDispBeanP =  (ConfigBean)colDispList.get(col);
							
							//Sum1
							String resultKey1 = colDispBeanP.getName()+"_"+reportU.getShortColName(colGroup1.getName());
							BigDecimal summaryValue1 = Utils.isNullToZero(rs.getBigDecimal(resultKey1));
							
							//Sum2
							String resultKey2 = colDispBeanP.getName()+"_"+reportU.getShortColName(colGroup2.getName());
							BigDecimal summaryValue2 = Utils.isNullToZero(rs.getBigDecimal(resultKey2));
							
							debug.debug("summaryValue1:"+summaryValue1,1);
							debug.debug("summaryValue2:"+summaryValue2,1);
							
							if(summaryValue1.compareTo(bigZero) != 0){
								valueRowSum = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
								isPct = true;
							}
						}
						
						if(AConstants.SUMMARY_TYPE_AVG.equals(summaryType) && !isPct){
							valueRowSum = valueRowSum.divide(BigDecimal.valueOf(colGroupList.size()), 2, BigDecimal.ROUND_HALF_UP);
						}
						//debug
						String debug = isDebug?"RSum:":"";
						
						ConfigBean configGroupBean1 = (ConfigBean)colGroupList.get(0);
						String sortIdKey = summaryType+"_"+colDispBean.getName()+"_"+reportU.getShortColName(configGroupBean1.getName()); 
						
						rowHtml.append("<td align='right' class='sort_"+sortIdKey+"'>"+debug+Utils.convertDigitToDisplay(colDispBean.getDispText(),valueRowSum)+"</td> \n");

					}//for
					
					//add rowCodeLits 
					ConfigBean rowCodeBean = new ConfigBean(rs.getString(groupByBean.getName()+"_CODE"), rs.getString(groupByBean.getName()+"_CODE"), "");
					rowCodeList.add(rowCodeBean);
					
					// add column %contribute summary by Row 
					if(AConstants.SUMMARY_TYPE_SUM_CONTRIBUTE.equals(summaryType)){
						for(int c=0;c<colDispList.size();c++){
							colDispBean = (ConfigBean)colDispList.get(c);
							//debug
							String debug = isDebug?"RSumCon:":"";
							conIDKey = "CON_"+rs.getString(groupByBean.getName()+"_CODE")+"_"+colDispBean.getName(); 
							rowHtml.append("<td align='right' class='sort_'>"+debug+"<span >"+conIDKey+"</span>"+"</td> \n");
						}//for
					}
				}//if

				rowHtml.append("</tr>\n");
				
				/** Summary All Column Hide or Show Row  **/
				/** For Check Row Sum Case ==0 Hide Row  **/
				rowSumAll = bigZero;
				for(col=0;col<colDispList.size();col++){
					colDispBean = (ConfigBean)colDispList.get(col);
					resultRowSumBean = colDispBean.getName();
					 valueRowSum = (BigDecimal)summaryRowMap.get(resultRowSumBean);
					debug.debug("before["+resultRowSumBean+"] valueRowSum["+valueRowSum.doubleValue()+"]");
					
					if(valueRowSum.doubleValue() <0){
						valueRowSum = valueRowSum.multiply(new BigDecimal("-1"));
						debug.debug("valueRowSum<0");
					}
					debug.debug("after["+resultRowSumBean+"] valueRowSum["+valueRowSum.doubleValue()+"]");
					
					rowSumAll = rowSumAll.add(valueRowSum);
				}
				
				//Validate Show All Row Sum > 0 is show
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
			//
			String cSpan = "";
			if(groupByBean.getValue().equalsIgnoreCase("SALES_ORDER_DATE") || groupByBean.getValue().equalsIgnoreCase("Invoice_Date")){
				cSpan = "2"; 
			}else{
				cSpan = ((isNoDisplayed)?"2":"3");
			}
			
			if(groupByBean.getValue().equalsIgnoreCase("SALES_ORDER_DATE") || groupByBean.getValue().equalsIgnoreCase("Invoice_Date")){
				columnCount = 2;
			}else{
				columnCount  = ((isNoDisplayed)?2:3); 
			}
			
			//Summary Column ALl
			StringBuffer summaryRowHtml = new StringBuffer("");
			StringBuffer summaryRowHtml2 = new StringBuffer("");
			BigDecimal summaryAll = new BigDecimal("0");
			if(found){
				for(i=0;i<colGroupList.size();i++){
					configGroupBean = (ConfigBean)colGroupList.get(i);
					for(int d=0;d<colDispList.size();d++){
						ConfigBean configBean = (ConfigBean)colDispList.get(d);
						String resultKey = configBean.getName()+"_"+reportU.getShortColName(configGroupBean.getName());
						
						BigDecimal summaryValue = new BigDecimal("0");
		
						if(summaryColumnMap != null && summaryColumnMap.get(resultKey) != null){
							summaryValue = (BigDecimal)summaryColumnMap.get(resultKey);
						}//if
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
							}//if
						}//if
							
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
						    
						    if(summaryPercentMap.get(colDispKey1) != null){
								BigDecimal colPercentAdd = summaryPercentMap.get(colDispKey1);
								colPercentAdd = colPercentAdd.add(colPercent);
								summaryPercentMap.put(colDispKey1, colPercentAdd);
							}else{
								summaryPercentMap.put(colDispKey1, colPercent);
							}//if		
						}//if
					}//for
				}//ifisPercent 			
			}//if found data
			
			// Summary All case SumRow == Show not Show
			if(hideRowEnable == false ){
				 summaryRowHtml.append(" <td class='summary' colspan='"+cSpan+"' align='right'><b>ยอดรวม  "+totalRecord+" รายการ</b></td>  \n");
				 summaryRowHtml.append(summaryRowHtml2); 
			}else{
				if(summaryAll.compareTo(bigZero)!=0 ){
				    summaryRowHtml.append(" <td class='summary' colspan='"+cSpan+"' align='right'><b>ยอดรวม  "+totalRecord+" รายการ</b></td>  \n");
				    summaryRowHtml.append(summaryRowHtml2);
				}else{
					summaryRowHtml.append(" <td class='summary' colspan='"+cSpan+"' align='right'><b>ยอดรวม  0  รายการ</b></td>  \n");
					summaryRowHtml.append(summaryRowHtml2);
				}
			}
			
			htmlStr.append(summaryRowHtml);
			
			//Add Total Summary
			if(isSummry && found){
				for(col=0;col<colDispList.size();col++){
					colDispBean = (ConfigBean)colDispList.get(col);
					resultRowSumBean = colDispBean.getName();
					 valueRowSum = (BigDecimal)summaryPerMap.get(resultRowSumBean);
					boolean isPct = false;
					
					if(resultRowSumBean.startsWith("PER")){
						//Sum1
						ConfigBean configBean1 = (ConfigBean)colDispList.get(col-2);
						String resultKey1 = configBean1.getName();
						BigDecimal summaryValue1 = new BigDecimal("0");
						if(summaryPerMap != null && summaryPerMap.get(resultKey1) != null){
							summaryValue1 = (BigDecimal)summaryPerMap.get(resultKey1);
						}
						
						//Sum2
						ConfigBean configBean2 = (ConfigBean)colDispList.get(col-1);
						String resultKey2 = configBean2.getName();
						BigDecimal summaryValue2 = new BigDecimal("0");
						if(summaryPerMap != null && summaryPerMap.get(resultKey2) != null){
							summaryValue2 = (BigDecimal)summaryPerMap.get(resultKey2);
						}
						//debug.debug("summaryValue1:"+summaryValue1);
						//debug.debug("summaryValue2:"+summaryValue2);
						
						if(summaryValue1.compareTo(bigZero) != 0){
							valueRowSum = (summaryValue2.divide(summaryValue1,4,BigDecimal.ROUND_FLOOR)).multiply(new BigDecimal("100"));
							isPct = true;
						}
					}//if PER-
					
					if(isPercent){
						ConfigBean colGroup1 = (ConfigBean)colGroupList.get(colGroupList.size()-2);
						ConfigBean configBeanP =  (ConfigBean)colDispList.get(col);
						
						//Sum1
						String resultKey1 = configBeanP.getName()+"_"+reportU.getShortColName(colGroup1.getName());
						BigDecimal summaryValue1 = summaryPercentMap.get(resultKey1) != null?(BigDecimal)summaryPercentMap.get(resultKey1):bigZero;

						debug.debug("summaryPercentValue1:"+summaryValue1,1);
					    valueRowSum = summaryValue1;
					}//if isPercent
					
					if(AConstants.SUMMARY_TYPE_AVG.equals(summaryType) && !isPct){
						valueRowSum = valueRowSum.divide(BigDecimal.valueOf(colGroupList.size()), 2, BigDecimal.ROUND_HALF_UP);
					}
					
					columnCount++;
					String debug = isDebug?"TSum:":"";
					htmlStr.append("<td class='summary' align='right'><b>"+debug+Utils.convertDigitToDisplay(colDispBean.getDispText(),valueRowSum)+"</b></td> \n");
				}//for
				
				//add Total Summary %Contribute
				if(AConstants.SUMMARY_TYPE_SUM_CONTRIBUTE.equals(summaryType)){
					for(col=0;col<colDispList.size();col++){
						columnCount++;
						String debug = isDebug?"TSum:":"";
						htmlStr.append("<td class='summary' align='right'><b>"+debug+"100.00"+"</b></td> \n");
					}//for
				}//if %contribute set
			
				debug.debug("*** Start Cal % Contribute ***");
				String htmlStrTemp  = htmlStr.toString();
				if(rowCodeList != null && rowCodeList.size()>0){
					for(r =0;r<rowCodeList.size();r++){
						ConfigBean rowCodeBean = rowCodeList.get(r);
						for(gc=0;gc<colGroupList.size();gc++){
							configGroupBean = (ConfigBean)colGroupList.get(gc);
								// add column %contribute summary by Row 
								for(col=0;col<colDispList.size();col++){
									colDispBean = (ConfigBean)colDispList.get(col);
									//debug.debug("colDispBean Name["+c+"]:"+colDispBean.getName());
									
									debug.debug("*************************************************",1);
									//summary by Column
									colSumKey = colDispBean.getName();
									colSum = (BigDecimal)summaryPerMap.get(colSumKey);
									debug.debug("row["+r+"]gc["+gc+"]col["+col+"]colSumKey["+colSumKey+"]colSum["+colSum+"]",1);
									
									//summary by Row
									rowSumKey = "SUM_ROW_"+rowCodeBean.getName()+"_"+colDispBean.getName();
									rowSum = (BigDecimal)summaryAllRowMap.get(rowSumKey);
									debug.debug("row["+r+"]gc["+gc+"]col["+col+"]rowSumKey["+rowSumKey+"]rowSum["+rowSum+"]",1);
									
									//calc contribute
									debug.debug("colSum["+colSum.doubleValue()+"]",1);
									if(colSum.doubleValue()!=0){
									   conPercent = new BigDecimal(rowSum.doubleValue()/colSum.doubleValue());
									}else{
									   conPercent =new BigDecimal("0");
									}
									debug.debug("row["+r+"]gc["+gc+"]col["+col+"]BeforeconPercent["+conPercent+"]",1);
									conPercent = conPercent.multiply(big100).setScale(6,BigDecimal.ROUND_HALF_UP);
									debug.debug("row["+r+"]gc["+gc+"]col["+col+"]AfterconPercent["+conPercent+"]",1);
									
									//debug
									//debug = isDebug?"TSumCon:":"";
									conIDKey = "CON_"+rowCodeBean.getName()+"_"+colDispBean.getName(); 
									debug.debug("row["+r+"]gc["+gc+"]col["+col+"]conIDKey["+conIDKey+"] for replace value["+conPercent.doubleValue()+"]",1);
									htmlStrTemp = htmlStrTemp.replace(conIDKey,Utils.decimalFormat(conPercent.doubleValue(),Utils.format_current_2_disgit));
									
									//replace StringBuffer By ConIDKey
									
								}//for 3
						}//for 2
					}//for 1
				}//if rowCodeList
				debug.debug("*** End Cal % Contribute ***");
				
				htmlStr = new StringBuffer(htmlStrTemp);
				
			}//if summary and found data
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
	private static List<ConfigBean> setDispCol(ABean salesBean) throws Exception{
		List<ConfigBean> colDispList = new ArrayList<ConfigBean>();	
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			//Case Column Display =CALL  set unit = "" 
			if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1())) ||
			   "CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1())) ){
				
			   colDispList.add(new ConfigBean(salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1",Utils.isNull(salesBean.getColNameDisp1()),Utils.isNull(aInit.DISP_COL_MAP.get(salesBean.getColNameDisp1()))+""));
			}else{
			   colDispList.add(new ConfigBean(salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1",Utils.isNull(salesBean.getColNameDisp1()),Utils.isNull(aInit.DISP_COL_MAP.get(salesBean.getColNameDisp1()))+"("+Utils.isNull(aInit.UNIT_MAP.get(salesBean.getColNameUnit1()))+")"));
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
			//Case Column Display =CALL  set unit = "" 
			if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2())) ||
			   "CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2()))){
				
				colDispList.add(new ConfigBean(salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2",Utils.isNull(salesBean.getColNameDisp2()),Utils.isNull(aInit.DISP_COL_MAP.get(salesBean.getColNameDisp2()))+""));
			}else{
			    colDispList.add(new ConfigBean(salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2",Utils.isNull(salesBean.getColNameDisp2()),Utils.isNull(aInit.DISP_COL_MAP.get(salesBean.getColNameDisp2()))+"("+Utils.isNull(aInit.UNIT_MAP.get(salesBean.getColNameUnit2()))+")"));
			}
			if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
				colDispList.add(new ConfigBean("PER1","%","%"));
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			//Case Column Display =CALL  set unit = "" 
			if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3())) || 
			   "CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3()))	){
	
			    colDispList.add(new ConfigBean(salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3",Utils.isNull(salesBean.getColNameDisp3()),Utils.isNull(aInit.DISP_COL_MAP.get(salesBean.getColNameDisp3()))+""));
			}else{
				colDispList.add(new ConfigBean(salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3",Utils.isNull(salesBean.getColNameDisp3()),Utils.isNull(aInit.DISP_COL_MAP.get(salesBean.getColNameDisp3()))+"("+Utils.isNull(aInit.UNIT_MAP.get(salesBean.getColNameUnit3()))+")"));
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			//Case Column Display =CALL  set unit = "" 
			if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4())) || 
			   "CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4()))	){
			    colDispList.add(new ConfigBean(salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4",Utils.isNull(salesBean.getColNameDisp4()),Utils.isNull(aInit.DISP_COL_MAP.get(salesBean.getColNameDisp4()))+""));
			}else{
				colDispList.add(new ConfigBean(salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4",Utils.isNull(salesBean.getColNameDisp4()),Utils.isNull(aInit.DISP_COL_MAP.get(salesBean.getColNameDisp4()))+"("+Utils.isNull(aInit.UNIT_MAP.get(salesBean.getColNameUnit4()))+")"));
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
	private  static void genConditionDisp(StringBuffer htmlStr,ABean salesBean,String columnCount ,String condDisp1,String condDisp2,String condDisp3,String condDisp4) throws Exception{
		
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
