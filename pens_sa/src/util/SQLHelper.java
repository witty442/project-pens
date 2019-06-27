package util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SQLHelper {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static String genStrCondChkNull(String value,String schema){
		String sql = "";
		if( !Utils.isNull(value).equals("")){
			sql = "\n "+schema +" '"+Utils.isNull(value)+"'";
		}
		return sql;	
	}
	
	public static String genStrCond(String value,String schema){
		return "\n "+schema +" '"+Utils.isNull(value)+"'";	
	}
	
	public static String genStrArrCondChkNull(String value,String schema){
		String sql = "";
		if( !Utils.isNull(value).equals("") &&  !Utils.isNull(value).equals("ALL")){
			sql = "\n "+schema +" ("+Utils.converToTextSqlIn(value)+")";
		}
		return sql;	
	}
	
	public static String genStrArrCond(String value,String schema){
		return "\n "+schema +" ("+Utils.converToTextSqlIn(value)+")";
	}
	
	public static String genNumCondChkNull(String value,String schema){
		String sql = "";
		if( !Utils.isNull(value).equals("")){
			sql = "\n "+schema +" "+Utils.isNull(value)+"";
		}
		return sql;	
	}
	
	public static String genNumCond(String value,String schema){
		return "\n "+schema +" "+Utils.isNull(value)+"";	
	}
	
	public static String genTHDateCondChkNull(String value,String format,String schema) throws Exception{
		String sql = "";
		if( !Utils.isNull(value).equals("")){
			Date d = Utils.parse(value, format,Utils.local_th);
			String dateStr = Utils.stringValue(d, format);//en format
			sql = "\n and "+schema +" to_date('"+dateStr+"','"+format+"')";
		}
		return sql;	
	}
	
	public static String genTHDateCond(String value,String format,String schema) throws Exception{
		String sql = "";
		Date d = Utils.parse(value, format,Utils.local_th);
		String dateStr = Utils.stringValue(d, format);//en format
		sql = "\n and "+schema +" to_date('"+dateStr+"','"+format+"')";
		return sql;
	}
   public static String converToTextSqlIn(String value){
		List<String> valuesText = new ArrayList<String>() ;
		String[] values = value.split("[,]");
		
		for(String text : values){
			valuesText.add("'"+text+"'");
		}
		
		return StringUtils.join(valuesText, ","); 
	}
}
