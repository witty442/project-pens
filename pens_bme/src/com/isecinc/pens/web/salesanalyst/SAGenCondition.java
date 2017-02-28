package com.isecinc.pens.web.salesanalyst;

import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.report.salesanalyst.SAInitial;


public class SAGenCondition {
	
	protected static  Logger logger = Logger.getLogger("PENS");
	SAUtils reportU = new SAUtils();
	
	public static String genColumnAll(String groupBy){
		logger.debug("GroupBy:"+groupBy);
		String groupBySQl = "";
		
		if("disp_1".equalsIgnoreCase(groupBy)){
			groupBySQl ="style";
		}else if("disp_2".equalsIgnoreCase(groupBy)){
			groupBySQl ="store_no,store_name,style";
		}else if("disp_3".equalsIgnoreCase(groupBy)){
			groupBySQl ="store_no,store_name,style";
		}else if("disp_4".equalsIgnoreCase(groupBy)){
			groupBySQl ="sales_date,store_no,store_name";
		}else if("disp_5".equalsIgnoreCase(groupBy)){
			groupBySQl ="sales_date,store_no,store_name,style";
		}else if("disp_6".equalsIgnoreCase(groupBy)){
			groupBySQl ="sales_date,store_no,store_name,style,item";
		}else if("disp_7".equalsIgnoreCase(groupBy)){
			
		}
    	return groupBySQl;
	}
	public static String genColumnCodeAll(String groupBy){
		logger.debug("GroupBy:"+groupBy);
		String groupBySQl = "";
		
		if("disp_1".equalsIgnoreCase(groupBy)){
			groupBySQl ="style as style_code";
		}else if("disp_2".equalsIgnoreCase(groupBy)){
			groupBySQl ="store_no as store_no_code,store_name as store_name_code,style as style_code";
		}else if("disp_3".equalsIgnoreCase(groupBy)){
			groupBySQl ="store_no as store_no_code,store_name as store_name_code,style as style_code";
		}else if("disp_4".equalsIgnoreCase(groupBy)){
			groupBySQl ="sales_date as sales_date_code,store_no as store_no_code,store_name as store_name_code";
		}else if("disp_5".equalsIgnoreCase(groupBy)){
			groupBySQl ="sales_date as sales_date_code,store_no as store_no_code,store_name as store_name_code,style as style_code";
		}else if("disp_6".equalsIgnoreCase(groupBy)){
			groupBySQl ="sales_date as sales_date_code,store_no as store_no_code,store_name as store_name_code,style as style_code,item as item_code";
		}else if("disp_7".equalsIgnoreCase(groupBy)){
			
		}
    	return groupBySQl;
	}
	public static String genColumnDispAll(String groupBy){
		logger.debug("GroupBy:"+groupBy);
		String groupBySQl = "";
		
		if("disp_1".equalsIgnoreCase(groupBy)){
			groupBySQl ="Style";
		}else if("disp_2".equalsIgnoreCase(groupBy)){
			groupBySQl ="Store No,Store Name,Style";
		}else if("disp_3".equalsIgnoreCase(groupBy)){
			groupBySQl ="Store No,Store Name,Style";
		}else if("disp_4".equalsIgnoreCase(groupBy)){
			groupBySQl ="Sales Date,Store No,Store Name";
		}else if("disp_5".equalsIgnoreCase(groupBy)){
			groupBySQl ="Sales Sate,Store No,Store Name,Style";
		}else if("disp_6".equalsIgnoreCase(groupBy)){
			groupBySQl ="Sales Date,Store No,Store Name,Style,Item";
		}else if("disp_7".equalsIgnoreCase(groupBy)){
			
		}
    	return groupBySQl;
	}
	
    public static String genColumnSQL(String groupBy){
      return genColumnAll(groupBy);
    }
    public static String genColumnCodeSQL(String groupBy){
        return genColumnCodeAll(groupBy);
      }
    public static String genGroupBySQL(String groupBy){
    	  return genColumnAll(groupBy);
    }
    
    public static String genLeftJoinSQL(String groupBy,String colGroupName){
    	String columns = "";
    	String sql = "";
    	columns = genColumnAll(groupBy);
    	String [] columnsArr = columns.split("\\,");
    	
    	//sql.append("ON S."+Utils.isNull(salesBean.getGroupBy())+"=M_"+colGroupName+"."+Utils.isNull(salesBean.getGroupBy()) +"\n");
    	sql = "ON ";
    	for(int i=0;i<columnsArr.length;i++){
    		if(i==0){
    		   sql +="\t S."+columnsArr[i]+"="+"M_"+colGroupName+"."+columnsArr[i]+" \n";
    		}else{
    		   sql +=" AND S."+columnsArr[i]+"="+"M_"+colGroupName+"."+columnsArr[i]+" \n";
    		}
    	}
    	sql += "\n";
    	return sql;
    }
	
	
	public static String genSqlWhereCondition(SABean salesBean) throws Exception{
		String sql = "";
		
		//sql.append("AND PENS_CUST_CODE LIKE '020047%'  /** Lotus or BigC **/ \n");
		//sql.append("AND PENS_CUST_CODE ='020047-5' /** Store **/ \n");
		//sql.append("AND PENS_GROUP_TYPE = 'ME1103'/** STYLE **/ \n");
		//sql.append("AND PENS_GROUP = 'BRA'  /** Group **/ \n");
		//sql.append("AND PENS_ITEM ='835055' /** ITEM **/ \n");
		
	//	(sales_date,sales_year,sales_month,sales_quarter,store_no,store_name,style,item,qty,WHOLE_PRICE_BF,RETAIL_PRICE_BF)
		
		//  change  "0" to "-1" for fix condition data
		if( !"-1".equals(Utils.isNull(salesBean.getCondName1())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue1()))){
			if("Customer".equals(Utils.isNull(salesBean.getCondName1()))){
				sql += " AND store_no LIKE '"+Utils.isNull(salesBean.getCondValue1())+"%' \n";
			}
			if("Store".equals(Utils.isNull(salesBean.getCondName1()))){
				sql +=" AND store_no IN("+SAUtils.converToText("PENS_CUST_CODE",salesBean.getCondValue1())+") /** Store **/ \n";
			}
			if("Style".equals(Utils.isNull(salesBean.getCondName1()))){
				sql +=" AND style IN("+SAUtils.converToText("PENS_GROUP_TYPE",salesBean.getCondValue1())+") /** Style **/ \n";
			}
			if("Group".equals(Utils.isNull(salesBean.getCondName1()))){
				sql +=" AND PENS_GROUP IN("+SAUtils.converToText("PENS_GROUP",salesBean.getCondValue1())+") /** Group **/ \n";
			}
			if("Item".equals(Utils.isNull(salesBean.getCondName1()))){
				sql +=" AND item IN("+SAUtils.converToText("PENS_ITEM",salesBean.getCondValue1())+") /** Item **/ \n";
			}
			
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName2())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue2()))){
			if("Customer".equals(Utils.isNull(salesBean.getCondName2()))){
				sql += " AND store_no LIKE '"+Utils.isNull(salesBean.getCondValue2())+"%' \n";
			}
			if("Store".equals(Utils.isNull(salesBean.getCondName2()))){
				sql +=" AND store_no IN("+SAUtils.converToText("PENS_CUST_CODE",salesBean.getCondValue2())+") /** Store **/ \n";
			}
			if("Style".equals(Utils.isNull(salesBean.getCondName2()))){
				sql +=" AND style IN("+SAUtils.converToText("PENS_GROUP_TYPE",salesBean.getCondValue2())+") /** Style **/ \n";
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
				sql += " AND store_no LIKE '"+Utils.isNull(salesBean.getCondValue3())+"%' \n";
			}
			if("Store".equals(Utils.isNull(salesBean.getCondName3()))){
				sql +=" AND store_no IN("+SAUtils.converToText("PENS_CUST_CODE",salesBean.getCondValue3())+") /** Store **/ \n";
			}
			if("Style".equals(Utils.isNull(salesBean.getCondName3()))){
				sql +=" AND style IN("+SAUtils.converToText("PENS_GROUP_TYPE",salesBean.getCondValue3())+") /** Style **/ \n";
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
				sql += " AND store_no LIKE '"+Utils.isNull(salesBean.getCondValue4())+"%' \n";
			}
			if("Store".equals(Utils.isNull(salesBean.getCondName4()))){
				sql +=" AND store_no IN("+SAUtils.converToText("PENS_CUST_CODE",salesBean.getCondValue4())+") /** Store **/ \n";
			}
			if("Style".equals(Utils.isNull(salesBean.getCondName4()))){
				sql +=" AND style IN("+SAUtils.converToText("PENS_GROUP_TYPE",salesBean.getCondValue4())+") /** Style **/ \n";
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
	
	
	
	/**
	 * 
	 * @param type  ORDER || INVOICE
	 * @param salesBean
	 * @return 
	 * @throws Exception
	 */
	
	public StringBuffer genSqlWhereCondByGroup(SABean salesBean,String type,String alias,String colGroupName) throws Exception{
		StringBuffer sql = new StringBuffer("");

		/** Where Condition By Group  **/
		if(SAInitial.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
		    sql.append(" AND (1=1 AND "+alias+"invoice_year||"+alias+"invoice_month = '"+colGroupName+"' ) \n");
		    //sql.append(" AND invoice_year IN('"+salesBean.getYear()+"') \n");
		}else if(SAInitial.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
			sql.append(" AND (1=1 AND "+alias+"invoice_quarter = '"+colGroupName+"' \n");
			sql.append(" AND "+alias+"invoice_year IN('"+salesBean.getYear()+"') ) \n");
		}else if(SAInitial.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
			sql.append(" AND (1=1 AND "+alias+"invoice_year = '"+colGroupName+"' ) \n");
		}else{
			 Date date = Utils.parseToBudishDate(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH);
			 sql.append(" AND (1=1 AND  "+alias+"INVOICE_DATE = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy') ) \n");
		}
		return sql;
	}
	
	public  String genSubSQLByType(Connection conn,User user ,SABean salesBean,String groupBy,String colGroupName) throws Exception{
		StringBuffer sql = new StringBuffer("");
		
		//sql.append("/*** genSubSQLByType **/ \n");

		sql.append("\t"+ "SELECT \n");
		sql.append("\t\t\t\t"+ SAGenCondition.genColumnSQL(salesBean.getGroupBy())+"\n \t\t\t\t ,"+genSQLSelectColumn(salesBean,groupBy,colGroupName)+" '1' AS A \n");
		sql.append("\t\t\t\t"+" FROM "+SAInitial.TABLE_VIEW+" V \n");
		sql.append("\t\t\t\t"+" WHERE 1=1 \n ");
		/** Condition Filter **/
		sql.append("\t\t\t\t"+  genSqlWhereCondition(salesBean));
		
		/** Where Condition By Group  **/
		if(SAInitial.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
		    sql.append("\t\t\t\t"+" AND sales_year||sales_month = '"+colGroupName+"' \n");
		}else if(SAInitial.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
			sql.append("\t\t\t\t"+" AND sales_year||sales_quarter = '"+colGroupName+"' \n");
		}else if(SAInitial.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
			sql.append("\t\t\t\t"+" AND sales_year = '"+colGroupName+"' \n");
		}else{
			Date date = Utils.parseToBudishDate(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH);
			sql.append("\t\t\t\t"+" AND  sales_DATE = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
		}
		
		//Include Pos or not
		//sql.append(ExternalCondition.genIncludePos(salesBean));
		
		/** Filter Displsy Data By User **/
		sql.append("\t\t"+SecurityHelper.genWhereSqlFilterByUser(conn,user, null,"V."));
	
		return sql.toString();
	}
	
	/**
	 * genTopSQLSelectColumn
	 * @param salesBean
	 * @param groupBy
	 * @param colGroupName
	 * @return String[2]  0->columnTop ,1->columnALL
	 * @throws Exception
	 * 
	 */
	
	public  String[] genTopSQLSelectColumn(SABean salesBean,String groupBy,String colGroupName) throws Exception{
		return genTopSQLSelectColumn(salesBean, groupBy, colGroupName,"");
	}
	
	/**
	 * genTopSQLSelectColumn
	 * @param salesBean
	 * @param groupBy
	 * @param colGroupName
	 * @return String[2]  0->columnTop ,1->columnALL
	 * @throws Exception
	 * 
	 */
	public  String[] genTopSQLSelectColumn(SABean salesBean,String groupBy,String colGroupName,String sqlNotIncaseCall) throws Exception{
		StringBuffer columnAll = new StringBuffer("");
		StringBuffer columnTop = new StringBuffer("");

		String result[] = new String[2];
		String columnName1="";
		String columnName2="";
		String columnName3="";
		String columnName4="";
		
		String subSelect1 ="";
		String subSelect2 ="";
		String subSelect3 ="";
		String subSelect4 ="";
		
		logger.debug("genTopSQLSelectColumn:groupBy["+groupBy+"]:colGroupName["+colGroupName+"]");
		
		colGroupName = reportU.getShortColName(colGroupName);
			
		columnTop.append(genColumnSQL(groupBy) +", ");			
		columnAll.append(genColumnSQL(groupBy) +", ");;
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			subSelect1 = "NVL(sum("+salesBean.getColNameDisp1()+"_1_"+colGroupName+"),0)" ;
			//columnName1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;
			columnName1 = salesBean.getColNameDisp1()+"_1_"+colGroupName;
			/** Gen Column ALL **/
			columnTop.append("\n\t\t\t "+ subSelect1+" as "+columnName1+",");
			columnAll.append( columnName1+",  ");
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
			
			subSelect2 = "NVL(sum("+salesBean.getColNameDisp2()+"_2_"+colGroupName+"),0)" ;
			//columnName2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;
			columnName2 = salesBean.getColNameDisp2()+"_2_"+colGroupName;
			/** Gen Column ALL **/
			columnTop.append("\n\t\t\t "+ subSelect2+" as "+columnName2+",");
			columnAll.append( columnName2+",  ");
			
			//PER
			if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
				columnTop.append("(CASE WHEN "+subSelect1+" <> 0");
				columnTop.append(" THEN ("+ subSelect2 +"/"+subSelect1 +")*100  ");
				columnTop.append(" ELSE 0 END )  as PER1_"+colGroupName +",   ");
				
				columnAll.append(" PER1_"+colGroupName+", \n");;
			}
	   }
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			
			subSelect3 = "NVL(sum("+salesBean.getColNameDisp3()+"_3_"+colGroupName+"),0)" ;
			//columnName3 = salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;
			columnName3 = salesBean.getColNameDisp3()+"_3_"+colGroupName;
			/** Gen Column ALL **/
			columnTop.append("\n\t\t\t "+ subSelect3+" as "+columnName3+",");
			columnAll.append( columnName3+",  ");
	   }
		
       if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			subSelect4 = "NVL(sum("+salesBean.getColNameDisp4()+"_4_"+colGroupName+"),0)" ;
			//columnName4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
			columnName4 = salesBean.getColNameDisp4()+"_4_"+colGroupName;
			/** Gen Column ALL **/
			columnTop.append("\n\t\t\t "+subSelect4+" as "+columnName4+",");
			columnAll.append( columnName4+",  ");
			
			//PER
			if( !"0".equals(Utils.isNull(salesBean.getCompareDisp2()))){
				columnTop.append("(CASE WHEN "+subSelect3+" <> 0");
				columnTop.append(" THEN ("+ subSelect4 +"/"+subSelect3 +")*100  ");
				columnTop.append(" ELSE 0 END )  as PER2_"+colGroupName +",   ");
			
				columnAll.append(" PER2_"+colGroupName+", ");;
			}
			
	   }
     
		result[0] = columnTop.toString();
		result[1] = columnAll.toString();
		
		return result;
	}

	/**
	 * 
	 * @param salesBean
	 * @param type
	 * @param groupBy
	 * @param colGroupName
	 * @return
	 * @throws Exception
	 */
	public  String genSQLSelectColumn(SABean salesBean,String groupBy,String colGroupName) throws Exception{
		String sql ="";
		String colName1 = "";String colName2 = "";
		String colName3 = "";String colName4 = "";
		String colAlias1 = "";String colAlias2 = "";
		String colAlias3 = "";String colAlias4 = "";
	
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			  //colAlias1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName ;
			  colAlias1 = salesBean.getColNameDisp1()+"_1_"+colGroupName ;
			  colName1 = reportU.genColumnName(salesBean.getColNameDisp1(),salesBean.getColNameUnit1());
			  
			  sql += "NVL(SUM("+colName1 +"),0) as "+colAlias1+",  ";
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
			 //colAlias2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName  ;
			 colAlias2 = salesBean.getColNameDisp2()+"_2_"+colGroupName  ;
			 colName2 = reportU.genColumnName(salesBean.getColNameDisp2(),salesBean.getColNameUnit2());
			 sql +="NVL(SUM("+colName2 +"),0) as "+colAlias2+",  ";
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			 // colAlias3 =  salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName  ;
			  colAlias3 =  salesBean.getColNameDisp3()+"_3_"+colGroupName  ;
		      colName3 = reportU.genColumnName(salesBean.getColNameDisp3(),salesBean.getColNameUnit3());
		      sql +="NVL(SUM("+colName3 +",0) as "+colAlias3+",  ";
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			 //colAlias4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName ;
			 colAlias4 = salesBean.getColNameDisp4()+"_4_"+colGroupName ;
			 colName4 = reportU.genColumnName(salesBean.getColNameDisp4(),salesBean.getColNameUnit4());
			 sql +="NVL(SUM("+colName4 +",0) as "+colAlias4+",  ";
		}
		return sql;
	}

}
