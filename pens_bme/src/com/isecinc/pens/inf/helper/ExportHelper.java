package com.isecinc.pens.inf.helper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.pens.util.Utils;

public class ExportHelper extends InterfaceUtils{

	protected static Logger logger = Logger.getLogger("PENS");
	
	
	public static void main(String[] s){
		try{
			//DECIMAL4
			 BigDecimal valueBig = new BigDecimal("3245.36878");
			 valueBig = valueBig.setScale(4, BigDecimal.ROUND_HALF_UP);
			 String valueAll = String.valueOf(valueBig);
			 String BF1 ="";String BF2 ="";
			 logger.debug("valueAll["+valueAll+"]valueAllLength["+valueAll.length()+"]");
			 
			 if(String.valueOf(valueAll).indexOf(".") != -1){
			    BF1 = String.valueOf(valueAll).substring(0,String.valueOf(valueAll).indexOf("."));
			    
			    logger.debug(".LEN["+String.valueOf(valueAll).indexOf(".")+"]");
			    
			    if(valueAll.length() >= String.valueOf(valueAll).indexOf(".")+5){
		           BF2 = String.valueOf(valueAll).substring(String.valueOf(valueAll).indexOf(".")+1,String.valueOf(valueAll).indexOf(".")+5);
			    }else   if(valueAll.length() < String.valueOf(valueAll).indexOf(".")+5){
			       BF2 = String.valueOf(valueAll).substring(String.valueOf(valueAll).indexOf(".")+1,String.valueOf(valueAll).indexOf(".")+4);
			    }
			 }else{
				BF1 = String.valueOf(valueAll);
				BF2 = "0000";
			 }
			 
		     int pos1 = 11-4;
		     int pos2 = 4;
		     logger.debug("BF1["+BF1+"],BF2["+BF2+"]");
			 String dataConvertStr= appendNumLeft(BF1,"0",pos1)+appendDecRightByLength(BF2,"0",pos2);//0002275
			 logger.debug("result["+dataConvertStr+"]");
			 
		}catch(Exception e){
			
		}
	}
	public static void main1(String[] s){
		try{
			//DECIMAl
			 BigDecimal valueBig = new BigDecimal("3245.368");
			 valueBig = valueBig.setScale(2, BigDecimal.ROUND_HALF_UP);
			 String valueAll = String.valueOf(valueBig);
			 String BF1 ="";String BF2 ="";
			 logger.debug("valueAll["+valueAll+"]length["+valueAll.length()+"]");
			 
			 if(String.valueOf(valueAll).indexOf(".") != -1){
				logger.debug("case1");
			    BF1 = String.valueOf(valueAll).substring(0,String.valueOf(valueAll).indexOf("."));
			    
			    if(valueAll.length() >= String.valueOf(valueAll).indexOf(".")+3){
		           BF2 = String.valueOf(valueAll).substring(String.valueOf(valueAll).indexOf(".")+1,String.valueOf(valueAll).indexOf(".")+3);
			    }else   if(valueAll.length() < String.valueOf(valueAll).indexOf(".")+3){
			       BF2 = String.valueOf(valueAll).substring(String.valueOf(valueAll).indexOf(".")+1,String.valueOf(valueAll).indexOf(".")+2);
			    }
			 }else{
				logger.debug("case2");
				BF1 = String.valueOf(valueAll);
				BF2 = "00";
			 }
		     int pos1 = 13 -2;
		     int pos2 = 2;
		     logger.debug("BF1["+BF1+"],BF2["+BF2+"]");
			 String dataConvertStr= appendNumLeft(BF1,"0",pos1)+appendDecRightByLength(BF2,"0",pos2);//0002275
			 logger.debug("result["+dataConvertStr+"]");
			 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * genPrepareSQL
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public static TableBean  genPrepareSQL(TableBean tableBean,User userBean ,Map<String, String> batchParamMap) throws Exception{
		Statement st = null;
		ResultSet rs = null;
	    String selectSql = "";
		try{	
			selectSql = ExportSQL.genSpecialSQL(tableBean,userBean,batchParamMap);
			//set select SQL
			tableBean.setPrepareSqlSelect(selectSql);

		    return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(st != null){
				st.close();st =null;
			}
			if(rs != null){
				rs.close();rs =null;
			}
		}
	}
	
	/**
	 * covertDataToExport
	 * @param colBean
	 * @param rs
	 * @return
	 * @throws Exception
	 * DESC : Convert To String to Export
	 */
	public static String covertToFormatExport(ColumnBean colBean,ResultSet rs) throws Exception{
		String dataConvertStr = "";
		
		if(Constants.EXPORT_FILL_SYMBOL_ZERO.equalsIgnoreCase(colBean.getFillSymbol())){
			dataConvertStr = Constants.EXPORT_STRING_SYMBOL_ZERO_DEFALUE;
			return dataConvertStr;
		}else{
			
			/** Case Normal  **/
			if(colBean.getColumnType().equalsIgnoreCase("DATE")){
				logger.debug("colBean.getColumnName():"+colBean.getColumnName());
				
				if(!"".equalsIgnoreCase(colBean.getDefaultValue())){
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
					  // dataConvertStr = Utils.format(rs.getDate(colBean.getColumnName()), colBean.getDefaultValue());
						dataConvertStr = appendRightByLength(Utils.format(rs.getDate(colBean.getColumnName()), colBean.getDefaultValue())," ",colBean.getTextLength());
					}
				}else{
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
				      // dataConvertStr = Utils.format(rs.getDate(colBean.getColumnName()), "ddMMyyyy");
				       dataConvertStr = appendRightByLength(Utils.format(rs.getDate(colBean.getColumnName()), "ddMMyyyy")," ",colBean.getTextLength());
					}
				}
			}else if(colBean.getColumnType().equalsIgnoreCase("TIMESTAMP")){
				if(!"".equalsIgnoreCase(colBean.getDefaultValue())){
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
					  // dataConvertStr = Utils.format(rs.getTimestamp(colBean.getColumnName()), "ddMMyyyyHHmmss");
					   dataConvertStr = appendRightByLength(Utils.format(rs.getTimestamp(colBean.getColumnName()), "ddMMyyyyHHmmss")," ",colBean.getTextLength());
					}
				}else{
					if( !Utils.isNull(rs.getString(colBean.getColumnName())).equals("")){
				     // dataConvertStr = Utils.format(rs.getTimestamp(colBean.getColumnName()), "ddMMyyyyHHmmss");
					 dataConvertStr = appendRightByLength(Utils.format(rs.getTimestamp(colBean.getColumnName()), "ddMMyyyyHHmmss")," ",colBean.getTextLength());
					}
				}
	
			}else if(colBean.getColumnType().equalsIgnoreCase("CHAR")){
				 //dataConvertStr = Utils.isNull(rs.getString(Utils.removeStringEnter(colBean.getColumnName())));
				dataConvertStr = appendRightByLength(Utils.isNull(rs.getString(colBean.getColumnName()))," ",colBean.getTextLength());
				
			}else if(colBean.getColumnType().equalsIgnoreCase("DECIMAL")){
				
				 BigDecimal valueBig = new BigDecimal(rs.getString(colBean.getColumnName()));
				 valueBig = valueBig.setScale(2, BigDecimal.ROUND_HALF_UP);
				 String valueAll = String.valueOf(valueBig);
				 String BF1 ="";String BF2 ="";
				 logger.debug("valueAll["+valueAll+"]");
				 
				 if(String.valueOf(valueAll).indexOf(".") != -1){
				    BF1 = String.valueOf(valueAll).substring(0,String.valueOf(valueAll).indexOf("."));
				    if(valueAll.length() >= String.valueOf(valueAll).indexOf(".")+3){
			           BF2 = String.valueOf(valueAll).substring(String.valueOf(valueAll).indexOf(".")+1,String.valueOf(valueAll).indexOf(".")+3);
				    }else   if(valueAll.length() < String.valueOf(valueAll).indexOf(".")+3){
				       BF2 = String.valueOf(valueAll).substring(String.valueOf(valueAll).indexOf(".")+1,String.valueOf(valueAll).indexOf(".")+2);
				    }
				 }else{
					BF1 = String.valueOf(valueAll);
					BF2 = "00";
				 }
			     int pos1 = colBean.getTextLength() -2;
			     int pos2 = 2;
			     logger.debug("BF1["+BF1+"],BF2["+BF2+"]");
				 dataConvertStr= appendNumLeft(BF1,"0",pos1)+appendDecRightByLength(BF2,"0",pos2);//0002275
				 logger.debug("result["+dataConvertStr+"]");
				 
				 //247.25
			}else if(colBean.getColumnType().equalsIgnoreCase("DECIMAL4")){
				 BigDecimal valueBig = new BigDecimal(rs.getString(colBean.getColumnName()));
				 valueBig = valueBig.setScale(4, BigDecimal.ROUND_HALF_UP);
				 String valueAll = String.valueOf(valueBig);
				 
				 String BF1 ="";String BF2 ="";
				 logger.debug("valueAll["+valueAll+"]");
				 
				 if(String.valueOf(valueAll).indexOf(".") != -1){
				    BF1 = String.valueOf(valueAll).substring(0,String.valueOf(valueAll).indexOf("."));
				    
				    if(valueAll.length() >= String.valueOf(valueAll).indexOf(".")+5){
			           BF2 = String.valueOf(valueAll).substring(String.valueOf(valueAll).indexOf(".")+1,String.valueOf(valueAll).indexOf(".")+5);
				    }else   if(valueAll.length() < String.valueOf(valueAll).indexOf(".")+5){
				       BF2 = String.valueOf(valueAll).substring(String.valueOf(valueAll).indexOf(".")+1,String.valueOf(valueAll).indexOf(".")+4);
				    }
				 }else{
					BF1 = String.valueOf(valueAll);
					BF2 = "0000";
				 }
				 
			     int pos1 = colBean.getTextLength() -4;
			     int pos2 = 4;
			     logger.debug("BF1["+BF1+"],BF2["+BF2+"]");
				 dataConvertStr= appendNumLeft(BF1,"0",pos1)+appendDecRightByLength(BF2,"0",pos2);//0002275
				 logger.debug("result["+dataConvertStr+"]");
			}
			
		}
	   return dataConvertStr;
	}
	
	public static String covertToFormatExportByFunction(Connection conn,ColumnBean colBean ,String value) throws Exception{
		String dataConvertStr = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
		   if("GET_REF_ADDRESS_ID".equalsIgnoreCase(colBean.getExternalFunction())){
			   ps = conn.prepareStatement("select address_id,reference_id from m_address where address_id = "+value);
			   rs= ps.executeQuery();
			   if(rs.next()){
				   
			   }
		   }
       }catch(Exception e){
    	   logger.error(e.getMessage(),e);
       }finally{
    	   if(ps != null){
    		   ps.close();ps=null;
    	   }
    	   if(rs != null){
    		   rs.close();rs=null;
    	   }
       }
	   return dataConvertStr;
	}
	
	
	
	/**
	 * 
	 * @param value
	 * @param fixLength
	 * @return
	 * @throws Exception
	 * Ex: value:100 ,FixLength :10
	 * Result:[100XXXXXXX]
	 */
	public static String appendLeft(String value,String cAppend ,int start,int end) throws Exception{
		int i = 0;
		int fixLength = (end -start)+1;
		int loopAppend = 0;
		if(value.length() < fixLength){
			loopAppend = fixLength - value.length();
		}
		for(i=0;i<loopAppend;i++){
			value +=cAppend;
		}
		return value;
	}
	
	public static String appendRight(String value,String cAppend ,int start,int end) throws Exception{
		int i = 0;
		int fixLength = (end -start)+1;
		int loopAppend = 0;
		if(value.length() < fixLength){
			loopAppend = fixLength - value.length();
		}
		for(i=0;i<loopAppend;i++){
			value +=cAppend;
		}
		return value;
	}
	
	public static String appendRightBK(String value,String cAppend ,int start,int end) throws Exception{
		int i = 0;
		int fixLength = (end -start)+1;
		int loopAppend = 0;
		String blankTemp ="";
		if(value.length() < fixLength){
			loopAppend = fixLength - value.length();
		}
		for(i=0;i<loopAppend;i++){
			blankTemp +=cAppend;
		}
		value = blankTemp+value;
		
		return value;
	}
	
	
	public static String appendByLength(String value,String cAppend ,int fixLength) throws Exception{
		int i = 0;
		for(i=0;i<fixLength;i++){
			value +=cAppend;
		}
		//logger.debug("Append:length:"+fixLength+",:value["+value+"]");
		return value;
	}

	
}
