
package com.isecinc.pens.web.report;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;


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
		List<StringBuffer> resultList = new ArrayList<StringBuffer>();
		try{
			StringBuffer sql = genMainSql(conn,user,salesBean);
			if( !Utils.isNull(sql.toString()).equals("")){
			    ps = conn.prepareStatement(sql.toString());
			    rs = ps.executeQuery();
			    
			    /** Gen Html Code **/
			    resultList.add(genReportHtml(rs,salesBean));
			    resultList.add(sql);
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
				
				if(ReportProcess.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
					htmlStr.append("<tr> \n");
					if(!Utils.isNull(salesBean.getDay()).equals("") && !Utils.isNull(salesBean.getDayTo()).equals(""))
						htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : วัน  : "+salesBean.getDay()+" - "+salesBean.getDayTo()+"</td>  \n");
					else
						htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : วัน  : "+(!Utils.isNull(salesBean.getDay()).equals("")?salesBean.getDay():salesBean.getDayTo())+"</td>  \n");
					
					htmlStr.append("</tr> \n");
					
					
				}else if(ReportProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
					htmlStr.append("<tr> \n");
					htmlStr.append(" <td colspan='"+columnCount+"'>รอบเวลา : เดือน  :  \n");
						for(int i=0;i<salesBean.getChkMonth().length;i++){
							if(i==salesBean.getChkMonth().length-1){
						       htmlStr.append( Utils.isNull(ReportProcess.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i])));
							}else{
							   htmlStr.append( Utils.isNull(ReportProcess.getInstance().MONTH_MAP.get(salesBean.getChkMonth()[i])) +", ");
							}
						}
					htmlStr.append("   พ.ศ. :"+Utils.convertToYearBushdish(Integer.parseInt(salesBean.getYear()))+"\n");
					
					htmlStr.append("</td> \n");
					htmlStr.append("</tr> \n");
					
					
				}else if(ReportProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
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
	   
	private static StringBuffer genReportHtml(ResultSet rs,SABean salesBean) throws Exception{
		StringBuffer htmlStr = new StringBuffer("");
		int no=0;
		boolean found = false;
		BigDecimal summary1 = new BigDecimal("0");
		BigDecimal summary2 = new BigDecimal("0");
		BigDecimal summary3 = new BigDecimal("0");
		BigDecimal summary4 = new BigDecimal("0");
		ReportProcess report = ReportProcess.getInstance();
		
		logger.debug("DispBy:"+salesBean.getDispBy());
		
		logger.debug("colDisp1:"+Utils.isNull(salesBean.getColNameDisp1()));
		logger.debug("colDisp2:"+Utils.isNull(salesBean.getColNameDisp2()));
		logger.debug("colDisp3:"+Utils.isNull(salesBean.getColNameDisp3()));
		logger.debug("colDisp4:"+Utils.isNull(salesBean.getColNameDisp4()));
		
		/** Head table **/
		htmlStr.append("<table border='1' width='100%' class='result2' id='sort-table'> <thead> \n");
		htmlStr.append("<tr>  \n");
		htmlStr.append("<th>No</th>  \n");
	    if("disp_1".equalsIgnoreCase(salesBean.getDispBy())){
		    htmlStr.append("<th>Style</th>  \n");
	    }else if("disp_2".equalsIgnoreCase(salesBean.getDispBy())){
			htmlStr.append("<th>Store No</th>  \n");
			htmlStr.append("<th>Store Name</th>  \n");
		}else if("disp_3".equalsIgnoreCase(salesBean.getDispBy())){
			htmlStr.append("<th>Store No</th>  \n");
			htmlStr.append("<th>Store Name</th>  \n");
			htmlStr.append("<th>Style</th>  \n");
		}else if("disp_4".equalsIgnoreCase(salesBean.getDispBy())){
			htmlStr.append("<th>Sales Date</th>  \n");
		    htmlStr.append("<th>Store No</th>  \n");
			htmlStr.append("<th>Store Name</th>  \n");	
		}else if("disp_5".equalsIgnoreCase(salesBean.getDispBy())){
			htmlStr.append("<th>Salse Date</th>  \n");
			htmlStr.append("<th>Store No</th>  \n");
			htmlStr.append("<th>Store Name</th>  \n");
			htmlStr.append("<th>Style</th>  \n");
		}else if("disp_6".equalsIgnoreCase(salesBean.getDispBy())){
			htmlStr.append("<th>Salse Date</th>  \n");
			htmlStr.append("<th>Store No</th>  \n");
			htmlStr.append("<th>Store Name</th>  \n");
			htmlStr.append("<th>Style</th>  \n");
			htmlStr.append("<th>Item</th>  \n");
		}
	    
		//Disp Column
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp1())) ){
		   htmlStr.append(" <th>"+report.getDispColumnTH(salesBean.getColNameDisp1())+"</th>  \n");
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp2())) ){
			htmlStr.append(" <th>"+report.getDispColumnTH(salesBean.getColNameDisp2())+"</th>  \n");
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp3())) ){
			htmlStr.append(" <th>"+report.getDispColumnTH(salesBean.getColNameDisp3())+"</th>  \n");
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp4())) ){
			htmlStr.append(" <th>"+report.getDispColumnTH(salesBean.getColNameDisp4())+"</th>  \n");
		}
		htmlStr.append("</tr>  \n");
		
		/** Row **/
		while(rs.next()){
			no++;
			found = true;
			htmlStr.append("<tr> \n");
			htmlStr.append(" <td>"+no+"</td>  \n");
			if("disp_1".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("style"))+"</td>  \n");
			}else if("disp_2".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_no"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_name"))+"</td>  \n");
			}else if("disp_3".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_no"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_name"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("style"))+"</td>  \n");
			}else if("disp_4".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='left'>"+DateUtil.stringValue(rs.getDate("sales_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_no"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_name"))+"</td>  \n");
			}else if("disp_5".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='left'>"+DateUtil.stringValue(rs.getDate("sales_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_no"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_name"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("style"))+"</td>  \n");
			}else if("disp_6".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='left'>"+DateUtil.stringValue(rs.getDate("sales_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_no"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("store_name"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("style"))+"</td>  \n");
				htmlStr.append(" <td align='left'>"+Utils.isNull(rs.getString("item"))+"</td>  \n");
			}
			
			//Disp Column
			if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp1())) ){
			   summary1 = summary1.add(rs.getBigDecimal(Utils.isNull(salesBean.getColNameDisp1())));
			   htmlStr.append(" <td align='right'>"+Utils.convertDigitToDisplay(Utils.isNull(salesBean.getColNameDisp1()),rs.getBigDecimal(Utils.isNull(salesBean.getColNameDisp1())))+"</td>  \n");
			}
			if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp2())) ){
				summary2 = summary2.add(rs.getBigDecimal(Utils.isNull(salesBean.getColNameDisp2())));
				htmlStr.append(" <td align='right'>"+Utils.convertDigitToDisplay(Utils.isNull(salesBean.getColNameDisp2()),rs.getBigDecimal(Utils.isNull(salesBean.getColNameDisp2())))+"</td>  \n");
			}
			if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp3())) ){
				summary3 = summary3.add(rs.getBigDecimal(Utils.isNull(salesBean.getColNameDisp3())));
				htmlStr.append(" <td align='right'>"+Utils.convertDigitToDisplay(Utils.isNull(salesBean.getColNameDisp3()),rs.getBigDecimal(Utils.isNull(salesBean.getColNameDisp3())))+"</td>  \n");
			}
			if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp4())) ){
				summary4 = summary4.add(rs.getBigDecimal(Utils.isNull(salesBean.getColNameDisp4())));
				htmlStr.append(" <td align='right'>"+Utils.convertDigitToDisplay(Utils.isNull(salesBean.getColNameDisp4()),rs.getBigDecimal(Utils.isNull(salesBean.getColNameDisp4())))+"</td>  \n");
			}
			htmlStr.append("</tr> \n");
		}
		
		/** Summary **/
		if(found){
			htmlStr.append("<tr> \n");
			if("disp_1".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='right' colspan='2'>Total</td>  \n");
			}else if("disp_2".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='right' colspan='3'>Total</td>  \n");
			}else if("disp_3".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='right' colspan='4'>Total</td>  \n");
			}else if("disp_4".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='right' colspan='4'>Total</td>  \n");
			}else if("disp_5".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='right' colspan='5'>Total</td>  \n");
			}else if("disp_6".equalsIgnoreCase(salesBean.getDispBy())){
				htmlStr.append(" <td align='right' colspan='6'>Total</td>  \n");
			}
			
			//Disp Column
			if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp1())) ){
			   htmlStr.append(" <td align='right'>"+Utils.convertDigitToDisplay(salesBean.getColNameDisp1(),summary1)+"</td>  \n");
			}
			if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp2())) ){
				htmlStr.append(" <td align='right'>"+Utils.convertDigitToDisplay(salesBean.getColNameDisp2(),summary2)+"</td>  \n");
			}
			if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp3())) ){
				htmlStr.append(" <td align='right'>"+Utils.convertDigitToDisplay(salesBean.getColNameDisp3(),summary3)+"</td>  \n");
			}
			if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp4())) ){
				htmlStr.append(" <td align='right'>"+Utils.convertDigitToDisplay(salesBean.getColNameDisp4(),summary4)+"</td>  \n");
			}
			htmlStr.append("</tr> \n");
		}
		
		htmlStr.append("</table> \n");
		return htmlStr;
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
		try{
			conn = DBConnection.getInstance().getConnection();
		    StringBuffer sql = genMainSql(conn,user,salesBean);
		    htmlCode = sql.toString();
		    
		  return htmlCode;
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public static StringBuffer genMainSql(Connection conn,User user,SABean salesBean) throws Exception{
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT ");
		sql.append(genTopSelect("A.",salesBean.getDispBy()));
		sql.append(genTopDispColumn("A.",salesBean));
		sql.append(" 'a' as a \n");
		sql.append(" FROM ( \n");
		sql.append("   "+genMainSqlSub(conn, user, salesBean, "PENSBME_SALES_FROM_LOTUS"));
		sql.append("    UNION ALL \n");
		sql.append("   "+genMainSqlSub(conn, user, salesBean, "PENSBME_SALES_FROM_BIGC"));
		sql.append("  )A  ");
		
		/** Group by **/
		sql.append(genTopGroupBy("A.",salesBean.getDispBy()));
		
		/** Order BY **/
		sql.append(genTopOrderBy("A.",salesBean.getDispBy()));
		
		logger.debug("*************** Generate SQL ******************** \n"+sql.toString()+"\n*****************************************************\n");
		return sql;
	}
	
	public static StringBuffer genMainSqlSub(Connection conn,User user,SABean salesBean,String tableName) throws Exception{
		StringBuffer sql = new StringBuffer("");
		String allCond = "";
		sql.append("SELECT \n");
		
		/** Gen Disp Type **/
		sql.append(genSelect(salesBean.getDispBy()));
		/** Gen Display Column **/
		sql.append(genDispColumn("",salesBean));

		sql.append(" 'a' as a \n");
		sql.append("FROM "+tableName+" \n");
		sql.append("WHERE 1=1 \n");
		
		//Date
		if(ReportProcess.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch())){
		   Date dateFrom = DateUtil.parse(salesBean.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
		   Date dateTo = DateUtil.parse(salesBean.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
		
		   String dateFromStr = DateUtil.stringValue(dateFrom, DateUtil.DD_MM_YYYY_WITH_SLASH);
		   String dateToStr = DateUtil.stringValue(dateTo, DateUtil.DD_MM_YYYY_WITH_SLASH);
		   
		   sql.append("AND SALES_DATE >= TO_DATE('"+dateFromStr+"','dd/mm/yyyy') \n");
		   sql.append("AND SALES_DATE <= TO_DATE('"+dateToStr+"','dd/mm/yyyy') \n");
		}
		//Month ,Year
		if(ReportProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
			
			for(int i=0;i<salesBean.getChkMonth().length;i++){
				logger.debug("name:["+i+"]value:["+salesBean.getChkMonth()[i]+"]");
				allCond +="'"+salesBean.getChkMonth()[i]+"',";
			}
			if( !Utils.isNull(allCond).equals("")){
				allCond = allCond.substring(0,allCond.length()-1);
			}
			sql.append("AND SALES_MONTH IN("+allCond+") \n");
			sql.append("AND SALES_YEAR = '"+salesBean.getYear()+"' \n");
		}
		
		//Year
		if(ReportProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
			for(int i=0;i<salesBean.getChkYear().length;i++){
				logger.debug("name:["+i+"]value:["+salesBean.getChkYear()[i]+"]");
				allCond +="'"+salesBean.getChkYear()[i]+"',";
			}
			if( !Utils.isNull(allCond).equals("")){
				allCond = allCond.substring(0,allCond.length()-1);
			}
			sql.append("AND SALES_YEAR IN("+allCond+") \n");
		}
		/** Gen Where Condition **/
		sql.append(genWhereCondition(salesBean));
	
		/** Gen Group by **/
		sql.append(genGroupBy(salesBean.getDispBy()));
		return sql;
	}
	
	public static String genTopDispColumn(String a,SABean salesBean) throws Exception{
		String sql = "";
		
		//  change  "0" to "-1" for fix condition data
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp1())) ){
			if("QTY".equals(Utils.isNull(salesBean.getColNameDisp1()))){
				sql += "NVL(SUM("+a+"QTY),0) as QTY ,\n";
			}
			if("WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp1()))){
				sql += "NVL(SUM("+a+"WHOLE_PRICE_BF),0) as WHOLE_PRICE_BF , \n";
			}
			if("RETAIL_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp1()))){
				sql += "NVL(SUM("+a+"RETAIL_PRICE_BF),0) as RETAIL_PRICE_BF ,\n";
			}
			
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp2())) ){
			if("QTY".equals(Utils.isNull(salesBean.getColNameDisp2()))){
				sql += "NVL(SUM("+a+"QTY),0) as QTY , \n";
			}
			if("WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp2()))){
				sql += "NVL(SUM("+a+"WHOLE_PRICE_BF),0) as WHOLE_PRICE_BF ,\n";
			}
			if("RETAIL_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp2()))){
				sql += "NVL(SUM("+a+"RETAIL_PRICE_BF),0) as RETAIL_PRICE_BF ,\n";
			}
			
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp3())) ){
			if("QTY".equals(Utils.isNull(salesBean.getColNameDisp3()))){
				sql += "NVL(SUM("+a+"QTY),0) as QTY , \n";
			}
			if("WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp3()))){
				sql += "NVL(SUM("+a+"WHOLE_PRICE_BF),0) as WHOLE_PRICE_BF , \n";
			}
			if("RETAIL_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp3()))){
				sql += "NVL(SUM("+a+"RETAIL_PRICE_BF),0) as RETAIL_PRICE_BF ,\n";
			}
			
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp4())) ){
			if("QTY".equals(Utils.isNull(salesBean.getColNameDisp4()))){
				sql += "NVL(SUM("+a+"QTY),0) as QTY , \n";
			}
			if("WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp4()))){
				sql += "NVL(SUM("+a+"WHOLE_PRICE_BF),0) as WHOLE_PRICE_BF , \n";
			}
			if("RETAIL_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp4()))){
				sql += "NVL(SUM("+a+"RETAIL_PRICE_BF),0) as RETAIL_PRICE_BF ,\n ";
			}
			
		}
		return sql;
	}
	
	public static String genDispColumn(String a,SABean salesBean) throws Exception{
		String sql = "";
		
		//  change  "0" to "-1" for fix condition data
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp1())) ){
			if("QTY".equals(Utils.isNull(salesBean.getColNameDisp1()))){
				sql += "NVL(SUM("+a+"QTY),0) as QTY ,\n";
			}
			if("WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp1()))){
				sql += "NVL(SUM("+a+"TOTAL_WHOLE_PRICE_BF),0) as WHOLE_PRICE_BF , \n";
			}
			if("RETAIL_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp1()))){
				sql += "NVL(SUM("+a+"RETAIL_PRICE_BF),0) as RETAIL_PRICE_BF ,\n";
			}
			if("TOTAL_WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp1()))){
				sql += "NVL(SUM("+a+"TOTAL_WHOLE_PRICE_BF),0) as TOTAL_WHOLE_PRICE_BF ,\n";
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp2())) ){
			if("QTY".equals(Utils.isNull(salesBean.getColNameDisp2()))){
				sql += "NVL(SUM("+a+"QTY),0) as QTY , \n";
			}
			if("WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp2()))){
				sql += "NVL(SUM("+a+"TOTAL_WHOLE_PRICE_BF),0) as WHOLE_PRICE_BF ,\n";
			}
			if("RETAIL_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp2()))){
				sql += "NVL(SUM("+a+"RETAIL_PRICE_BF),0) as RETAIL_PRICE_BF ,\n";
			}
			if("TOTAL_WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp2()))){
				sql += "NVL(SUM("+a+"TOTAL_WHOLE_PRICE_BF),0) as TOTAL_WHOLE_PRICE_BF ,\n";
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp3())) ){
			if("QTY".equals(Utils.isNull(salesBean.getColNameDisp3()))){
				sql += "NVL(SUM("+a+"QTY),0) as QTY , \n";
			}
			if("WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp3()))){
				sql += "NVL(SUM("+a+"TOTAL_WHOLE_PRICE_BF),0) as WHOLE_PRICE_BF , \n";
			}
			if("RETAIL_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp3()))){
				sql += "NVL(SUM("+a+"RETAIL_PRICE_BF),0) as RETAIL_PRICE_BF ,\n";
			}
			if("TOTAL_WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp3()))){
				sql += "NVL(SUM("+a+"TOTAL_WHOLE_PRICE_BF),0) as TOTAL_WHOLE_PRICE_BF ,\n";
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getColNameDisp4())) ){
			if("QTY".equals(Utils.isNull(salesBean.getColNameDisp4()))){
				sql += "NVL(SUM("+a+"QTY),0) as QTY , \n";
			}
			if("WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp4()))){
				sql += "NVL(SUM("+a+"TOTAL_WHOLE_PRICE_BF),0) as WHOLE_PRICE_BF , \n";
			}
			if("RETAIL_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp4()))){
				sql += "NVL(SUM("+a+"RETAIL_PRICE_BF),0) as RETAIL_PRICE_BF ,\n ";
			}
			if("TOTAL_WHOLE_PRICE_BF".equals(Utils.isNull(salesBean.getColNameDisp4()))){
				sql += "NVL(SUM("+a+"TOTAL_WHOLE_PRICE_BF),0) as TOTAL_WHOLE_PRICE_BF ,\n";
			}
		}
		return sql;
	}
	
	public static String genWhereCondition(SABean salesBean) throws Exception{
		String sql = "";
		
		//sql.append("AND PENS_CUST_CODE LIKE '020047%'  /** Lotus or BigC **/ \n");
		//sql.append("AND PENS_CUST_CODE ='020047-5' /** Store **/ \n");
		//sql.append("AND PENS_GROUP_TYPE = 'ME1103'/** STYLE **/ \n");
		//sql.append("AND PENS_GROUP = 'BRA'  /** Group **/ \n");
		//sql.append("AND PENS_ITEM ='835055' /** ITEM **/ \n");
		
		
		//  change  "0" to "-1" for fix condition data
		if( !"-1".equals(Utils.isNull(salesBean.getCondName1())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue1()))){
			if("Customer".equals(Utils.isNull(salesBean.getCondName1()))){
				sql += " AND PENS_CUST_CODE LIKE '"+Utils.isNull(salesBean.getCondValue1())+"%' \n";
			}
			if("Store".equals(Utils.isNull(salesBean.getCondName1()))){
				sql +=" AND PENS_CUST_CODE IN("+SAUtils.converToText("PENS_CUST_CODE",salesBean.getCondValue1())+") /** Store **/ \n";
			}
			if("Style".equals(Utils.isNull(salesBean.getCondName1()))){
				sql +=" AND PENS_GROUP_TYPE IN("+SAUtils.converToText("PENS_GROUP_TYPE",salesBean.getCondValue1())+") /** Style **/ \n";
			}
			if("Group".equals(Utils.isNull(salesBean.getCondName1()))){
				sql +=" AND PENS_GROUP IN("+SAUtils.converToText("PENS_GROUP",salesBean.getCondValue1())+") /** Group **/ \n";
			}
			if("Item".equals(Utils.isNull(salesBean.getCondName1()))){
				sql +=" AND PENS_ITEM IN("+SAUtils.converToText("PENS_ITEM",salesBean.getCondValue1())+") /** Item **/ \n";
			}
			
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName2())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue2()))){
			if("Customer".equals(Utils.isNull(salesBean.getCondName2()))){
				sql += " AND PENS_CUST_CODE LIKE '"+Utils.isNull(salesBean.getCondValue2())+"%' \n";
			}
			if("Store".equals(Utils.isNull(salesBean.getCondName2()))){
				sql +=" AND PENS_CUST_CODE IN("+SAUtils.converToText("PENS_CUST_CODE",salesBean.getCondValue2())+") /** Store **/ \n";
			}
			if("Style".equals(Utils.isNull(salesBean.getCondName2()))){
				sql +=" AND PENS_GROUP_TYPE IN("+SAUtils.converToText("PENS_GROUP_TYPE",salesBean.getCondValue2())+") /** Style **/ \n";
			}
			if("Group".equals(Utils.isNull(salesBean.getCondName2()))){
				sql +=" AND PENS_GROUP IN("+SAUtils.converToText("PENS_GROUP",salesBean.getCondValue2())+") /** Group **/ \n";
			}
			if("Item".equals(Utils.isNull(salesBean.getCondName2()))){
				sql +=" AND PENS_ITEM IN("+SAUtils.converToText("PENS_ITEM",salesBean.getCondValue2())+") /** Item **/ \n";
			}
			
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName3())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue3()))){
			if("Customer".equals(Utils.isNull(salesBean.getCondName3()))){
				sql += " AND PENS_CUST_CODE LIKE '"+Utils.isNull(salesBean.getCondValue3())+"%' \n";
			}
			if("Store".equals(Utils.isNull(salesBean.getCondName3()))){
				sql +=" AND PENS_CUST_CODE IN("+SAUtils.converToText("PENS_CUST_CODE",salesBean.getCondValue3())+") /** Store **/ \n";
			}
			if("Style".equals(Utils.isNull(salesBean.getCondName3()))){
				sql +=" AND PENS_GROUP_TYPE IN("+SAUtils.converToText("PENS_GROUP_TYPE",salesBean.getCondValue3())+") /** Style **/ \n";
			}
			if("Group".equals(Utils.isNull(salesBean.getCondName3()))){
				sql +=" AND PENS_GROUP IN("+SAUtils.converToText("PENS_GROUP",salesBean.getCondValue3())+") /** Group **/ \n";
			}
			if("Item".equals(Utils.isNull(salesBean.getCondName3()))){
				sql +=" AND PENS_ITEM IN("+SAUtils.converToText("PENS_ITEM",salesBean.getCondValue3())+") /** Item **/ \n";
			}
			
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName4())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue4()))){
			if("Customer".equals(Utils.isNull(salesBean.getCondName4()))){
				sql += " AND PENS_CUST_CODE LIKE '"+Utils.isNull(salesBean.getCondValue4())+"%' \n";
			}
			if("Store".equals(Utils.isNull(salesBean.getCondName4()))){
				sql +=" AND PENS_CUST_CODE IN("+SAUtils.converToText("PENS_CUST_CODE",salesBean.getCondValue4())+") /** Store **/ \n";
			}
			if("Style".equals(Utils.isNull(salesBean.getCondName4()))){
				sql +=" AND PENS_GROUP_TYPE IN("+SAUtils.converToText("PENS_GROUP_TYPE",salesBean.getCondValue4())+") /** Style **/ \n";
			}
			if("Group".equals(Utils.isNull(salesBean.getCondName4()))){
				sql +=" AND PENS_GROUP IN("+SAUtils.converToText("PENS_GROUP",salesBean.getCondValue4())+") /** Group **/ \n";
			}
			if("Item".equals(Utils.isNull(salesBean.getCondName4()))){
				sql +=" AND PENS_ITEM IN("+SAUtils.converToText("PENS_ITEM",salesBean.getCondValue4())+") /** Item **/ \n";
			}
		}
		return sql;
	}
	
	private static StringBuffer genTopSelect(String a,String dispType){
		StringBuffer sql = new StringBuffer("");
		if("disp_1".equalsIgnoreCase(dispType)){
			sql.append(a+"style, \n");
		}else if("disp_2".equalsIgnoreCase(dispType)){
			sql.append(a+"store_no, \n");
			sql.append(a+"store_name, \n");
		}else if("disp_3".equalsIgnoreCase(dispType)){
			sql.append(a+"store_no, \n");
			sql.append(a+"store_name, \n");
			sql.append(a+"style, \n");
		}else if("disp_4".equalsIgnoreCase(dispType)){
			sql.append(a+"sales_date, \n");
			sql.append(a+"store_no, \n");
			sql.append(a+"store_name, \n");
		}else if("disp_5".equalsIgnoreCase(dispType)){
			sql.append(a+"sales_date, \n");
			sql.append(a+"store_no, \n");
			sql.append(a+"store_name, \n");
			sql.append(a+"style, \n");
		}else if("disp_6".equalsIgnoreCase(dispType)){
			sql.append(a+"sales_date, \n");
			sql.append(a+"store_no, \n");
			sql.append(a+"store_name, \n");
			sql.append(a+"style, \n");
			sql.append(a+"item, \n");
		}
		return sql;
	}
	
	private static StringBuffer genSelect(String dispType){
		StringBuffer sql = new StringBuffer("");
		if("disp_1".equalsIgnoreCase(dispType)){
			sql.append("PENS_GROUP_TYPE as style, \n");
		}else if("disp_2".equalsIgnoreCase(dispType)){
			sql.append("pens_cust_code as store_no, \n");
			sql.append("pens_cust_desc as store_name, \n");
		}else if("disp_3".equalsIgnoreCase(dispType)){
			sql.append("pens_cust_code as store_no, \n");
			sql.append("pens_cust_desc as store_name, \n");
			sql.append("PENS_GROUP_TYPE as style, \n");
		}else if("disp_4".equalsIgnoreCase(dispType)){
			sql.append("sales_date, \n");
			sql.append("pens_cust_code as store_no, \n");
			sql.append("pens_cust_desc as store_name, \n");
		}else if("disp_5".equalsIgnoreCase(dispType)){
			sql.append("sales_date, \n");
			sql.append("pens_cust_code as store_no, \n");
			sql.append("pens_cust_desc as store_name, \n");
			sql.append("PENS_GROUP_TYPE as style, \n");
		}else if("disp_6".equalsIgnoreCase(dispType)){
			sql.append("sales_date, \n");
			sql.append("pens_cust_code as store_no, \n");
			sql.append("pens_cust_desc as store_name, \n");
			sql.append("PENS_GROUP_TYPE as style, \n");
			sql.append("PENS_ITEM as item, \n");
		}
		return sql;
	}
	
	private static StringBuffer genTopGroupBy(String a,String dispType){
		StringBuffer sql = new StringBuffer("");
		if("disp_1".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY "+a+"style \n");
		}else if("disp_2".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY "+a+"store_no ,"+a+"store_name \n");
		}else if("disp_3".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY "+a+"store_no ,"+a+"store_name,"+a+"style \n");
		}else if("disp_4".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY "+a+"sales_date,"+a+"store_no ,"+a+"store_name \n");
		}else if("disp_5".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY "+a+"sales_date,"+a+"store_no ,"+a+"store_name,"+a+"style \n");
		}else if("disp_6".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY "+a+"sales_date,"+a+"store_no ,"+a+"store_name,"+a+"style ,"+a+"item \n");
		}
		return sql;
	}
	
	private static StringBuffer genGroupBy(String dispType){
		StringBuffer sql = new StringBuffer("");
		if("disp_1".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY PENS_GROUP_TYPE \n");
		}else if("disp_2".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY pens_cust_code ,pens_cust_desc \n");
		}else if("disp_3".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY pens_cust_code ,pens_cust_desc,PENS_GROUP_TYPE \n");
		}else if("disp_4".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY sales_date,pens_cust_code ,pens_cust_desc \n");
		}else if("disp_5".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY sales_date,pens_cust_code ,pens_cust_desc,PENS_GROUP_TYPE \n");
		}else if("disp_6".equalsIgnoreCase(dispType)){
			sql.append("GROUP BY sales_date,pens_cust_code ,pens_cust_desc,PENS_GROUP_TYPE,PENS_ITEM \n");
		}
		return sql;
	}
	
	private static StringBuffer genTopOrderBy(String alias ,String dispType){
		StringBuffer sql = new StringBuffer("");
		if("disp_1".equalsIgnoreCase(dispType)){
			sql.append("ORDER BY "+alias+"style \n");
		}else if("disp_2".equalsIgnoreCase(dispType)){
			sql.append("ORDER BY "+alias+"store_no ,"+alias+"store_name \n");
		}else if("disp_3".equalsIgnoreCase(dispType)){
			sql.append("ORDER BY "+alias+"store_no ,"+alias+"store_name,"+alias+"style \n");
		}else if("disp_4".equalsIgnoreCase(dispType)){
			sql.append("ORDER BY "+alias+"sales_date,"+alias+"store_no ,"+alias+"store_name \n");
		}else if("disp_5".equalsIgnoreCase(dispType)){
			sql.append("ORDER BY "+alias+"sales_date,"+alias+"store_no ,"+alias+"store_name,"+alias+"style \n");
		}else if("disp_6".equalsIgnoreCase(dispType)){
			sql.append("ORDER BY "+alias+"sales_date,"+alias+"store_no ,"+alias+"store_name,"+alias+"style,"+alias+"item \n");
		}
		return sql;
	}

}
