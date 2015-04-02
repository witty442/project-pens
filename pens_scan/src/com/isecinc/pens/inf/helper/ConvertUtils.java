package com.isecinc.pens.inf.helper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;

import org.hibernate.cfg.Configuration;

/**
 * @author WITTY
 *
 */
public class ConvertUtils {
 private static String DECIMAL_FORMAT ="#.00000000000000000000";
 private static String CURRENCY_FORMAT ="#,###.00";
                                       // 0.00000000000000001000
 // private static String DECIMAL_FORMAT ="#.####################";
  
  public static void main(String[] s){
	  try{
		String ss =" xx,1042,CT,0040426,ร้านเฮียฮก,,,,,0,,,,N,,7% OUTPUT VAT GOODS,,,,,200000,,,,,,,,,,,,,,,,,,100000050,Y,O,[LINE[0]->ERROR:Data truncation: Data too long for column 'VAT_CODE' at row 1]";
	    System.out.println("Length:"+ss.split(",").length);
	  }catch(Exception e){
		  e.printStackTrace();
	  }
  }
  
  
  public static StringBuffer genHTMLCode(String dataStr) {
	  StringBuffer str = new StringBuffer("");
	  try{
		  if(dataStr != null){
			  String[] dataArray = dataStr.split("\n");
			  str.append("<table align='center' border='0' cellpadding='3' cellspacing='1' class='result'>");
			  /***  gen Header ***************/
	          str.append("<tr>"); 
              String[] columnHeader = dataArray[0].split(",");
              for(int c=0;c<columnHeader.length;c++){
            	str.append("<th>");
            	str.append(columnHeader[c]);
            	str.append("</th>");
              }
	          str.append("</tr>");
	          /***  gen Header ***************/
	          if(dataArray != null && dataArray.length > 0){
		          /***  gen Detail ***************/
		          for(int line =1;line<dataArray.length;line++){
		        	  String[] dataLineArray = dataArray[line].split(",");
		        	  String styleClass = (line%2==0)?"lineE":"lineO";
		        	  str.append("<tr class='"+styleClass+"'>");  
		        	  System.out.println("H1:"+dataLineArray.length+":"+columnHeader.length);
		        	 
			          for(int c=0;c<columnHeader.length;c++){
			              str.append("<td>");
			              str.append(c>dataLineArray.length-1?"":dataLineArray[c]);
			              str.append("</td>");
			          }
		        	  
			          str.append("</tr>");
		          }
	          }
	          /***  gen Detail ***************/
			  str.append("</table>");
		  }
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return str;
  }
  
  public static StringBuffer genHTMLCodeTransaction(String dataStr) {
	  StringBuffer str = new StringBuffer("");
	  try{
		  if(dataStr != null){
			  String[] dataArray = dataStr.split("\n");
			  str.append("<table align='center' border='0' cellpadding='3' cellspacing='1' class='result'>");
			  /***  gen Header ***************/
	          str.append("<tr>"); 
              String[] columnHeaderH = dataArray[0].split(",");
              for(int c=0;c<columnHeaderH.length;c++){
            	str.append("<th>");
            	str.append(columnHeaderH[c]);
            	str.append("</th>");
              }
	          str.append("</tr>");
	          
	          /***  gen Header Detail***************/
	          if(dataArray != null && dataArray.length > 0){
		          str.append("<tr>"); 
	              String[] columnHeaderD = dataArray[1].split(",");
	              for(int c=0;c<columnHeaderD.length;c++){
	            	str.append("<th>");
	            	str.append(columnHeaderD[c]);
	            	str.append("</th>");
	              }
		          str.append("</tr>");
		          /***  gen Header ***************/
		          
		          /***  gen Detail ***************/
		          for(int line =2;line<dataArray.length;line++){
		        	  //System.out.println("dataLine:"+dataArray[line]);
		        	  String[] dataLineArray = dataArray[line].split(",");
		        	  //System.out.println("dataLineArray length:"+dataLineArray.length);
		        	  String styleClass = (line%2==0)?"lineE":"lineO";
		        	  str.append("<tr class='"+styleClass+"'>"); 
		        	  if(Utils.isNull(dataLineArray[0]).equals("H")){
				          for(int c=0;c<columnHeaderH.length;c++){
				              str.append("<td>");
				              str.append(c>dataLineArray.length-1?"":dataLineArray[c]);
				              str.append("</td>");
				          }
			          }else{

			        	  for(int c=0;c<columnHeaderD.length;c++){
				              str.append("<td>");
				              str.append(c>dataLineArray.length-1?"":dataLineArray[c]);
				              str.append("</td>");
				          }
			        	  
			          }
			          str.append("</tr>");
		          }
		          /***  gen Detail ***************/
	          }
			  str.append("</table>");
		  }
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return str;
  }
  
	public static int convertToInt(String str) throws Exception{
		if(str == null){
			return 0;
		}
		return Integer.parseInt(str);
	}
	
	public static double convertToDoubleBK(String str) throws Exception{
		if(str == null){
			return 0.00000000000000000000;
		}
		DecimalFormat dc=new DecimalFormat();
		dc.applyPattern(DECIMAL_FORMAT);
		String ff =dc.format(Double.parseDouble(str));
		System.out.println("FF:"+ff);
		return new Double(ff);
	}
	
	public static double convertToDouble(String str) throws Exception{
		if(str == null){
			return 0;
		}
		return Double.parseDouble(str);
	}
	
	public static long convertToLong(String str) throws Exception{
		if(str == null){
			return 0;
		}
		return Long.parseLong(str);
	}
	
	public static BigDecimal convertToBigDecimal(String str) throws Exception{
		if(str == null){
			return new BigDecimal(0.00000000000000000000);
		}
		DecimalFormat dc=new DecimalFormat();
		dc.applyPattern(DECIMAL_FORMAT);
		String ff =dc.format(Double.parseDouble(str));
		System.out.println("FF:"+ff);
		System.out.println("Big:"+new BigDecimal(ff));
		return new BigDecimal(ff);
	}
	
	public static String convertToDecimalStr(String str) throws Exception{
		if(str == null){
			return "0.00000000000000000000";
		}
		DecimalFormat dc=new DecimalFormat();
		dc.applyPattern(DECIMAL_FORMAT);
		String ff =dc.format(Double.parseDouble(str));
	
		return ff;
	}
	
	public static String convertToCurrencyStr(String s) {
		String currencyStr = "0.00";
		try{
			if( !Utils.isNull(s).equals("")){
				 Double d = new Double(Utils.isNull(s));
				 DecimalFormat dc=new DecimalFormat();
			     dc.applyPattern(CURRENCY_FORMAT);
			     currencyStr =dc.format(d);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	
	public static String convertToCurrencyStr(double s) {
		String currencyStr = "0.00";
		try{
			 Double d = new Double(s);
			 DecimalFormat dc=new DecimalFormat();
		     dc.applyPattern(CURRENCY_FORMAT);
		     currencyStr =dc.format(d);
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	
	public static String convertToString(String str) throws Exception{
		if(str == null){
			return "";
		}
		return str.trim();
	}
	
	public static String genEnvStr(){
		String str = "";
		EnvProperties env= EnvProperties.getInstance();
		try{
			Configuration hibernateConfig = new Configuration();
			hibernateConfig.configure();
			String url = hibernateConfig.getProperty("connection.url");
			
			str += "DataBase:"+url +" \n";
			str += " ,FTP Server:"+env.getProperty("ftp.ip.server")+" ,UserFTP:"+env.getProperty("ftp.username")+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
}
