
package com.isecinc.pens.imports.excel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.pens.util.Utils;

/**
 * @author WITTY
 *
 */
public class ValidateImportHelperExcel {

	protected static  Logger logger = Logger.getLogger("PENS");
	
	/**
	 * validate (column)
	 * @param conn
	 * @param colBean
	 * @param lineArray
	 * @param userBean
	 * @throws Exception
	 */
	public static void validate(Connection conn,ColumnBean colBean,String[] lineArray,User userBean,String tableName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
        String result = "";
        String value = "";
        String msg ="";
        String classException = "";
        String valueDispError = "";
		try{
			if(colBean.getTextPosition() >= lineArray.length ){
				value = "";
			}else{
				value = Utils.isNull(lineArray[colBean.getTextPosition()]);
			}	
			
			msg ="Validate Func"+colBean.getValidateFunc()+" User[]Column["+colBean.getColumnName()+"] value["+value+"]";
			
			if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_PENS_ITEM")){
				
				ImportDAO importDAO = new ImportDAO();
				 String pensItem = importDAO.getItemByInterfaceValueTypeLotusCase1(conn, com.isecinc.pens.dao.constants.Constants.STORE_TYPE_LOTUS_ITEM, value);
					
		         if(Utils.isNull(pensItem).equals("")){
		        	 value = value.substring(0,6);
		        	 pensItem = importDAO.getItemByInterfaceValueTypeLotusCase2(conn, com.isecinc.pens.dao.constants.Constants.STORE_TYPE_LOTUS_ITEM, value);
		         }
		         if(Utils.isNull(pensItem).equals("")){
				
				     classException ="FieldValueNotFoundException";
				     valueDispError = value;
				   
					/** Throws Exception Class **/
					ExceptionHandle.throwExceptionClass(classException,valueDispError,colBean.getColumnName());
		         }
			}
			msg += "resultValue["+result+"]";
			
			//logger.debug(msg);
		}catch(Exception e){
		
			/** Throws Exception Case Exception unknow **/
			ExceptionHandle.throwExceptionClass(classException,valueDispError,colBean.getColumnName());
	   
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
	}

}
