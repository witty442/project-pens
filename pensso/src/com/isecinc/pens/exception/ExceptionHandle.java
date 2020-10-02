package com.isecinc.pens.exception;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcessAll;

public class ExceptionHandle {
    public static Map<String,String> ERROR_MAPPING = new HashMap<String,String>();
    static{
    	ERROR_MAPPING.put("UNKNOW","UNKNOW");
    	ERROR_MAPPING.put("NOUPDATE","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16 Update \u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E44\u0E14\u0E49\u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01\u0E2B\u0E32 Key \u0E44\u0E21\u0E48\u0E1E\u0E1A ");
    	ERROR_MAPPING.put("MySQLIntegrityConstraintViolationException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E25\u0E07\u0E10\u0E32\u0E19\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E19\u0E35\u0E49 \u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01 Key \u0E17\u0E35\u0E48\u0E40\u0E01\u0E35\u0E48\u0E22\u0E27\u0E02\u0E49\u0E2D\u0E07\u0E44\u0E21\u0E48\u0E21\u0E35");
    	ERROR_MAPPING.put("FTPException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16\u0E15\u0E34\u0E14\u0E15\u0E48\u0E2D FTP Server \u0E44\u0E14\u0E49");
    	ERROR_MAPPING.put("NullPointerException","\u0E21\u0E35\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E40\u0E1B\u0E47\u0E19 NULL ");
    	ERROR_MAPPING.put("DataTruncationException","\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E17\u0E35\u0E48\u0E19\u0E33\u0E40\u0E02\u0E49\u0E32\u0E21\u0E32\u0E21\u0E35\u0E02\u0E19\u0E32\u0E14\u0E21\u0E32\u0E01\u0E01\u0E27\u0E48\u0E32 \u0E02\u0E19\u0E32\u0E14\u0E02\u0E2D\u0E07 Field \u0E17\u0E35\u0E48\u0E01\u0E33\u0E2B\u0E19\u0E14\u0E44\u0E27\u0E49");
    	ERROR_MAPPING.put("DataNotFoundException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E17\u0E35\u0E48\u0E08\u0E30 Generate");
    	ERROR_MAPPING.put("DataFailException","\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E44\u0E21\u0E48\u0E16\u0E39\u0E01\u0E15\u0E49\u0E2D\u0E07");
    	ERROR_MAPPING.put("UniqueConstraintException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E44\u0E14\u0E49 Key Duplicate");
    	ERROR_MAPPING.put("DuplicateImportFileException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16 import file \u0E19\u0E35\u0E49\u0E44\u0E14\u0E49\u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01 \u0E21\u0E35\u0E01\u0E32\u0E23 import\u0E44\u0E1B\u0E41\u0E25\u0E49\u0E27 ");
    	ERROR_MAPPING.put("FileNotFoundException","\u0E44\u0E21\u0E48\u0E1E\u0E1A\u0E44\u0E1F\u0E25\u0E4C");
    	ERROR_MAPPING.put("DuplicateImportFileException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16 import file \u0E19\u0E35\u0E49\u0E44\u0E14\u0E49\u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01 \u0E21\u0E35\u0E01\u0E32\u0E23 import\u0E44\u0E1B\u0E41\u0E25\u0E49\u0E27");
    	ERROR_MAPPING.put("DuplicateExportFileException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16 Export file \u0E19\u0E35\u0E49\u0E44\u0E14\u0E49\u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01 \u0E21\u0E35\u0E01\u0E32\u0E23 Export\u0E44\u0E1B\u0E41\u0E25\u0E49\u0E27");
    	ERROR_MAPPING.put("OracleInvoiceNoEmtpyException","\u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16 Export file \u0E19\u0E35\u0E49\u0E44\u0E14\u0E49\u0E40\u0E19\u0E37\u0E48\u0E2D\u0E07\u0E08\u0E32\u0E01 \u0E22\u0E31\u0E07\u0E44\u0E21\u0E48\u0E21\u0E35\u0E01\u0E32\u0E23\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01 oracle invoice no");
    	ERROR_MAPPING.put("OrderFailInterfacetwstockException","Order \u0E15\u0E32\u0E21\u0E27\u0E31\u0E19\u0E17\u0E35\u0E48\u0E23\u0E30\u0E1A\u0E38 \u0E22\u0E31\u0E07\u0E44\u0E21\u0E48\u0E44\u0E14\u0E49\u0E17\u0E33\u0E01\u0E32\u0E23 Interface Onhand to ICC -twstock");
    	ERROR_MAPPING.put("OrderDuplicationGenExcelException","\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E15\u0E32\u0E21\u0E27\u0E31\u0E19\u0E17\u0E35\u0E48 Order \u0E17\u0E35\u0E48\u0E23\u0E30\u0E1A\u0E38 \u0E44\u0E14\u0E49\u0E40\u0E04\u0E22 Generate excel file \u0E44\u0E1B\u0E41\u0E25\u0E49\u0E27");
    	ERROR_MAPPING.put("ImportErrorException","Upload \u0E44\u0E1F\u0E25\u0E4C  $ \u0E44\u0E21\u0E48\u0E2A\u0E33\u0E40\u0E23\u0E47\u0E08 \u0E01\u0E23\u0E38\u0E13\u0E32\u0E41\u0E01\u0E49\u0E44\u0E02\u0E02\u0E49\u0E2D\u0E21\u0E39\u0E25\u0E41\u0E25\u0E49\u0E27\u0E17\u0E33\u0E01\u0E32\u0E23 Import \u0E43\u0E2B\u0E21\u0E48");
     	ERROR_MAPPING.put("SuccessException","\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E23\u0E32\u0E22\u0E01\u0E32\u0E23\u0E40\u0E23\u0E35\u0E22\u0E1A\u0E23\u0E49\u0E2D\u0E22\u0E41\u0E25\u0E49\u0E27");
     	ERROR_MAPPING.put("OrderExistException","\u0E21\u0E35\u0E01\u0E32\u0E23 key order \u0E44\u0E1B\u0E41\u0E25\u0E49\u0E27 \u0E44\u0E21\u0E48\u0E2A\u0E32\u0E21\u0E32\u0E23\u0E16 import \u0E44\u0E14\u0E49");
    }
	public static void main (String[] f){

		System.out.println(Utils.toUnicodeChar("บันทึกรายการเรียบร้อยแล้ว"));
		//System.out.println(Utils.toUnicodeChar("ข้อมูลตามวันที่ Order ที่ระบุ ได้เคย Generate excel file ไปแล้ว"));
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
			}else if( e instanceof DataFailException){
				exceptionCode = "DataFailException";
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
				   int errorId = SequenceProcessAll.getIns().getNextValue(conn, "monitor_error_mapping.error_id").intValue();
				   stInsert.execute(" INSERT INTO monitor_error_mapping(error_id,error_code,error_msg)values("+errorId+",'"+errorCode+"','"+errorMsg+"')");
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
		}else if(classException.equals("FieldValueNotFoundException")){
			 throw new FieldValueNotFoundException("ColName["+colName+"] Value["+value+"]: Cannot find "+colName+"  ");
		}
		
	}
}
