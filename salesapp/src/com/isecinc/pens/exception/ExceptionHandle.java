package com.isecinc.pens.exception;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.isecinc.pens.inf.exception.CnNoNotFoundException;
import com.isecinc.pens.inf.exception.DataNotFoundException;
import com.isecinc.pens.inf.exception.FTPException;
import com.isecinc.pens.inf.exception.FindAddressBillToException;
import com.isecinc.pens.inf.exception.FindAddressException;
import com.isecinc.pens.inf.exception.FindAddressShipToException;
import com.isecinc.pens.inf.exception.FindCustomerException;
import com.isecinc.pens.inf.exception.FindModifierIdException;
import com.isecinc.pens.inf.exception.FindModifierLineIdException;
import com.isecinc.pens.inf.exception.FindOrderIDException;
import com.isecinc.pens.inf.exception.FindPriceListIdException;
import com.isecinc.pens.inf.exception.FindProductException;
import com.isecinc.pens.inf.exception.FindReceiptIdException;
import com.isecinc.pens.inf.exception.FindUOMException;
import com.isecinc.pens.inf.exception.FindUserException;
import com.isecinc.pens.inf.exception.FindVisitIdException;
//import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class ExceptionHandle {
    public static Map<String,String> ERROR_MAPPING = new HashMap<String,String>();
    static{
    	ERROR_MAPPING.put("UNKNOW","UNKNOW");
    	ERROR_MAPPING.put("NOUPDATE","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16 Update \u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E44\u0E14\u0E49\u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01\u0E2B\u0E32 Key \u0E44\u0E21\u0E48\u0E1E\u0E1A ");
    	ERROR_MAPPING.put("MySQLIntegrityConstraintViolationException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E25\u0E07\u0E10\u0E32\u0E19\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E19\u0E35\u0E49 \u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01 Key \u0E17\u0E35\u0E48\u0E40\u0E01\u0E35\u0E48\u0E22\u0E27\u0E02\u0E49\u0E2D\u0E07\u0E44\u0E21\u0E48\u0E21\u0E35");
    	ERROR_MAPPING.put("FTPException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16\u0E15\u0E34\u0E14\u0E15\u0E48\u0E2D FTP Server \u0E44\u0E14\u0E49");
    	ERROR_MAPPING.put("NullPointerException","\u0E21\u0E35\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E40\u0E1B\u0E47\u0E19 NULL ");
    	ERROR_MAPPING.put("DataTruncationException","\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E17\u0E35\u0E48\u0E19\u0E33\u0E40\u0E02\u0E49\u0E32\u0E21\u0E32\u0E21\u0E35\u0E02\u0E19\u0E32\u0E14\u0E21\u0E32\u0E01\u0E01\u0E27\u0E48\u0E32 \u0E02\u0E19\u0E32\u0E14\u0E02\u0E2D\u0E07 Field \u0E17\u0E35\u0E48\u0E01\u0E33\u0E2B\u0E19\u0E14\u0E44\u0E27\u0E49");
    	ERROR_MAPPING.put("MasterException","\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E1E\u0E37\u0E49\u0E19\u0E10\u0E32\u0E19\u0E1A\u0E32\u0E07\u0E2A\u0E48\u0E27\u0E19\u0E21\u0E35\u0E1B\u0E31\u0E0D\u0E2B\u0E32 \u0E01\u0E23\u0E38\u0E13\u0E32\u0E15\u0E23\u0E27\u0E08\u0E2A\u0E2D\u0E1A\u0E41\u0E25\u0E30 Import  \u0E43\u0E2B\u0E21\u0E48\u0E2D\u0E35\u0E01\u0E04\u0E23\u0E31\u0E49\u0E07");
    	ERROR_MAPPING.put("FindCustomerException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Customer ID ");
    	ERROR_MAPPING.put("FindProductException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Product ID ");
    	ERROR_MAPPING.put("FindUOMException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 UOM ID ");
    	ERROR_MAPPING.put("FindUserException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 User ID");
    	ERROR_MAPPING.put("FindOrderIDException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Order ID ");
    	ERROR_MAPPING.put("FindAddressException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Address ID ");
    	ERROR_MAPPING.put("FindAddressShipToException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Ship To Address ID");
    	ERROR_MAPPING.put("FindAddressBillToException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Bill To Address ID");
    	ERROR_MAPPING.put("FindReceiptIdException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Receipt ID");
    	ERROR_MAPPING.put("FindVisitIdException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Visit ID");
    	ERROR_MAPPING.put("FindModifierIdException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Modifier ID");
    	ERROR_MAPPING.put("FindPriceListIdException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 PriceList ID");
    	ERROR_MAPPING.put("FindModifierLineIdException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25 Modifier Line ID");
    	ERROR_MAPPING.put("CnNoNotFoundException","CN NO Not Found in Master");
    	ERROR_MAPPING.put("MySQLIntegrityConstraintViolationException","Duplicate PK");
    	ERROR_MAPPING.put("DataNotFoundException","Data Not Found");
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
			if( e instanceof java.sql.SQLIntegrityConstraintViolationException){
				exceptionCode = "MySQLIntegrityConstraintViolationException";
			}else if( e instanceof FTPException){
				exceptionCode = "FTPException";
			}else if( e instanceof NullPointerException){
				exceptionCode = "NullPointerException";
			}else if( e instanceof FindCustomerException){
				exceptionCode = "FindCustomerException";
			}else if( e instanceof FindProductException){
				exceptionCode = "FindProductException";
			}else if( e instanceof FindUOMException){
				exceptionCode = "FindUOMException";
			}else if( e instanceof FindUserException){
				exceptionCode = "FindUserException";
			}else if( e instanceof FindOrderIDException){
				exceptionCode = "FindOrderIDException";
			}else if( e instanceof FindAddressException){
				exceptionCode = "FindAddressException";
			}else if( e instanceof FindAddressShipToException){
				exceptionCode = "FindAddressShipToException";
			}else if( e instanceof FindAddressBillToException){
				exceptionCode = "FindAddressBillToException";
			}else if( e instanceof FindReceiptIdException){
				exceptionCode = "FindReceiptIdException";
			}else if( e instanceof FindVisitIdException){
				exceptionCode = "FindVisitIdException";
			}else if( e instanceof FindModifierIdException){
				exceptionCode = "FindModifierIdException";	
			}else if( e instanceof FindPriceListIdException){
				exceptionCode = "FindPriceListIdException";		
			}else if( e instanceof FindModifierLineIdException){
				exceptionCode = "FindModifierLineIdException";	
			}else if( e instanceof CnNoNotFoundException){
				exceptionCode = "CnNoNotFoundException";	
			}else if( e instanceof DataNotFoundException){
				exceptionCode = "DataNotFoundException";	
			}else if( e instanceof SQLException){
				String errMsg = e.getMessage();
				if(errMsg.indexOf("Data truncation") != -1){
					exceptionCode ="DataTruncationException";
				}else{
					exceptionCode ="UNKNOW";
				}
  		    }else{
  		    	exceptionCode ="UNKNOW";
  		    }

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
		if(classException.equals("FindCustomerException")){
			 throw new FindCustomerException("ColName["+colName+"] Value["+value+"]: Cannot find Customer_id ");
		}else if(classException.equals("FindProductException")){
			 throw new FindProductException("ColName["+colName+"] Value["+value+"]: Cannot find Product ID ");
		}else if(classException.equals("FindUOMException")){
			 throw new FindUOMException("ColName["+colName+"] Value["+value+"]: Cannot find UOM ID ");
		}else if(classException.equals("FindUserException")){
			 throw new FindUserException("ColName["+colName+"] Value["+value+"]: Cannot find User ID ");
		}else if(classException.equals("FindOrderIDException")){
			 throw new FindOrderIDException("ColName["+colName+"] Value["+value+"]: Cannot find Order ID ");
		}else if(classException.equals("FindAddressException")){
			 throw new FindAddressException("ColName["+colName+"] Value["+value+"]: Cannot find Address ID ");
		}else if(classException.equals("FindAddressShipToException")){
			 throw new FindAddressShipToException("ColName["+colName+"] Value["+value+"]: Cannot find Ship To Address ID ");
		}else if(classException.equals("FindAddressBillToException")){
			 throw new FindAddressBillToException("ColName["+colName+"] Value["+value+"]: Cannot find Bill To Address ID ");
		}else if(classException.equals("FindReceiptIdException")){
			 throw new FindReceiptIdException("ColName["+colName+"] Value["+value+"]: Cannot find Receipt ID ");
		}else if(classException.equals("FindVisitIdException")){
			 throw new FindVisitIdException("ColName["+colName+"] Value["+value+"]: Cannot find Visit ID ");
		}else if(classException.equals("FindModifierIdException")){
			 throw new FindModifierIdException("ColName["+colName+"] Value["+value+"]: Cannot find Modifier ID ");
		}else if(classException.equals("FindPriceListIdException")){
			 throw new FindPriceListIdException("ColName["+colName+"] Value["+value+"]: Cannot find PriceList ID ");
		}else if(classException.equals("FindModifierLineIdException")){
			 throw new FindModifierLineIdException("ColName["+colName+"] Value["+value+"]: Cannot find Modifier Line ID ");
		}
		
	}
}
