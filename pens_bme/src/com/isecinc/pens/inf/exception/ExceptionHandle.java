package com.isecinc.pens.inf.exception;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;


public class ExceptionHandle {
    public static Map<String,String> ERROR_MAPPING = new HashMap<String,String>();
    static{
    	ERROR_MAPPING.put("UNKNOW","UNKNOW");
    	ERROR_MAPPING.put("NOUPDATE","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16 Update \u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E44\u0E14\u0E49\u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01\u0E2B\u0E32 Key \u0E44\u0E21\u0E48\u0E1E\u0E1A ");
    	ERROR_MAPPING.put("MySQLIntegrityConstraintViolationException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E25\u0E07\u0E10\u0E32\u0E19\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E19\u0E35\u0E49 \u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01 Key \u0E17\u0E35\u0E48\u0E40\u0E01\u0E35\u0E48\u0E22\u0E27\u0E02\u0E49\u0E2D\u0E07\u0E44\u0E21\u0E48\u0E21\u0E35");
    	ERROR_MAPPING.put("FTPException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16\u0E15\u0E34\u0E14\u0E15\u0E48\u0E2D FTP Server \u0E44\u0E14\u0E49");
    	ERROR_MAPPING.put("NullPointerException","\u0E21\u0E35\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E40\u0E1B\u0E47\u0E19 NULL ");
    	ERROR_MAPPING.put("DataTruncationException","\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E17\u0E35\u0E48\u0E19\u0E33\u0E40\u0E02\u0E49\u0E32\u0E21\u0E32\u0E21\u0E35\u0E02\u0E19\u0E32\u0E14\u0E21\u0E32\u0E01\u0E01\u0E27\u0E48\u0E32 \u0E02\u0E19\u0E32\u0E14\u0E02\u0E2D\u0E07 Field \u0E17\u0E35\u0E48\u0E01\u0E33\u0E2B\u0E19\u0E14\u0E44\u0E27\u0E49");
    	
    	ERROR_MAPPING.put("UniqueConstraintException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E44\u0E14\u0E49 Key Duplicate");
    
    }
	public static void main (String[] f){
//		System.out.println(Utils.toUnicodeChar("UNKNOW"));
//		System.out.println(Utils.toUnicodeChar("ไม่สามารถ Update ข้อมูลได้เนื่องจากหา Key ไม่พบ "));
//		System.out.println(Utils.toUnicodeChar("ไม่สามารถบันทึกลงฐานข้อมูลนี้ เนื่องจาก Key ที่เกี่ยวข้องไม่มี"));
//		System.out.println(Utils.toUnicodeChar("ไม่สามารถติดต่อ FTP Server ได้"));
//		System.out.println(Utils.toUnicodeChar("มีข้อมูลเป็น NULL "));
//		System.out.println(Utils.toUnicodeChar("ข้อมูลที่นำเข้ามามีขนาดมากกว่า ขนาดของ Field ที่กำหนดไว้"));
//		System.out.println(Utils.toUnicodeChar("ข้อมูลพื้นฐานบางส่วนมีปัญหา กรุณาตรวจสอบและ Import  ใหม่อีกครั้ง"));
//		System.out.println(Utils.toUnicodeChar("ไม่พบข้อมูล Customer ID "));
//		System.out.println(Utils.toUnicodeChar("ไม่พบข้อมูล Product ID "));
//		System.out.println(Utils.toUnicodeChar("ไม่พบข้อมูล UOM ID "));
//		System.out.println(Utils.toUnicodeChar("ไม่พบข้อมูล User ID "));
//		System.out.println(Utils.toUnicodeChar("ไม่พบข้อมูล Order ID "));
//		System.out.println(Utils.toUnicodeChar("ไม่พบข้อมูล Address ID "));
//		System.out.println(Utils.toUnicodeChar("ไม่พบข้อมูล Ship To Address ID "));
//		System.out.println(Utils.toUnicodeChar("ไม่พบข้อมูล Bill To Address ID "));
	}
	public static String getExceptionCode(Exception e){
		String exceptionCode = "";
		try{
			// e.printStackTrace();
			 String errMsg = e.getMessage();
			 System.out.println("errMsg: \n"+errMsg);
			 
			 if( e instanceof FTPException){
				exceptionCode = "FTPException";
			}else if( e instanceof NullPointerException){
				exceptionCode = "NullPointerException";
			}else if( e instanceof FindUserException){
				exceptionCode = "FindUserException";         
			}else if( e instanceof java.sql.SQLIntegrityConstraintViolationException
					|| errMsg.indexOf("unique constraint") != -1 
					){
				exceptionCode = "UniqueConstraintException";
			}else if( e instanceof SQLException){
				
				if(errMsg.indexOf("Data truncation") != -1){
					exceptionCode ="DataTruncationException";
				}else{
					exceptionCode ="UNKNOW";
				}
  		    }else{
  		    	exceptionCode ="UNKNOW";
  		    }

			 System.out.println("exceptionCode:"+exceptionCode);
		}catch(Exception ee){
			ee.printStackTrace();
		}
		return exceptionCode;
	}
	/**
	 * com.mysql.jdbc.MysqlDataTruncation: Data truncation: Data too long for column 'SALES_ORDER_NO' at row 1
	 * @param conn
	 * @param errorCode
	 * @param errorMsg
	 */
	
	public static void insertErrorCode(Connection conn,String errorCode,String errorMsg){
		try{
			insertErrorCodeModel(conn,errorCode,errorMsg);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
		}
	}
	
	public static void insertErrorCode(String errorCode,String errorMsg){
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			insertErrorCodeModel(conn,errorCode,errorMsg);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn != null){
				   conn.close();conn=null;
				}
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
	}
	
	public static void insertErrorCodeModel(Connection conn,String errorCode,String errorMsg){
		Statement st = null;
		ResultSet rs = null;
		Statement stInsert= null;
		try{
			st = conn.createStatement();
			rs = st.executeQuery("select ERROR_CODE FROM monitor_error_mapping where error_code ='"+errorCode+"'");
			if( !rs.next()){
				//System.out.println("Insert Error Msg");
				if( !Utils.isNull(errorMsg).equals("")){
				   stInsert = conn.createStatement();
				   stInsert.execute(" INSERT INTO monitor_error_mapping(error_code,error_msg)values('"+errorCode+"','"+errorMsg+"')");
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(st != null){
					st.close();st=null;
				}
				if(stInsert != null){
					stInsert.close();stInsert=null;
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void throwExceptionClass(String classException ,String value,String colName) throws Exception{
		if(classException.equals("UniqueConstraintException")){
			 throw new java.sql.SQLIntegrityConstraintViolationException("ColName["+colName+"] Value["+value+"]: Duplicate Primary Key ");
		}else if(classException.equals("FindUserException")){
			 throw new FindUserException("ColName["+colName+"] Value["+value+"]: Cannot find User ID ");
		}
		
	}
}
