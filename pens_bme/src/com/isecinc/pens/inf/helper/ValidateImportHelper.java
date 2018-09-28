
package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.pens.util.Utils;

/**
 * @author WITTY
 *
 */
public class ValidateImportHelper {

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
        String findColumn = "";
        String sql = "";
        String value = "";
        boolean exe = true;
        String msg ="";
        String classException = "";
        boolean isRequireField = false;
        String valueDispError = "";
		try{
			if(colBean.getTextPosition() >= lineArray.length ){
				value = "";
			}else{
				value = Utils.isNull(lineArray[colBean.getTextPosition()]);
			}	
			
			msg ="Validate Func"+colBean.getValidateFunc()+" User[]Column["+colBean.getColumnName()+"] value["+value+"]";
			
			if(Utils.isNull(colBean.getColumnName()).equals("PRODUCT_ID")){
				findColumn = "PRODUCT_ID";
				sql = " select "+findColumn+" FROM m_product WHERE product_id ='"+value+"'" ;
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
				classException ="FindProductException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_USER_ID")){
				findColumn = "USER_ID";
				sql = " select "+findColumn+" FROM ad_user WHERE USER_ID ="+value+"" ;
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
				classException ="FindUserException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_ORDER_ID")){
				/** GET_ORDER_ID **/
				findColumn = "ORDER_ID";
				sql = " select "+findColumn+" FROM t_order WHERE order_no = '"+value+"'";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindOrderIDException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_ORDER_ID_MANUAL")){
				/** GET_ORDER_ID CASE Manual Form Oracle and Regen OrderNo  **/
				/** Regen OrderNo New Beacuse Oracle set OrderNo difference SaleApp **/ 
				value = ExternalFunctionHelper.getReplcaeNewOrderNo(value);
				findColumn = "ORDER_ID";
				sql = " select "+findColumn+" FROM t_order WHERE order_no = '"+value+"'";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindOrderIDException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_CUSTOMER_ID")){
				findColumn = "CUSTOMER_ID";
				sql = " select "+findColumn+" FROM m_customer WHERE REFERENCE_ID = "+value+"" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
				classException ="FindCustomerException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_CUSTOMER_ID_BY_CODE")){
				
				findColumn = "CUSTOMER_ID";
				sql = " select "+findColumn+" FROM m_customer WHERE CODE = '"+value+"'" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
				classException ="FindCustomerException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).startsWith("VALIDATE_BILL_ADDRESS_ID_BY_CUSTOCODE")){
				
				/** FIND_BILL_ADDRESS_ID_BY_CUSTOCODE|6 (By position of value) **/
				findColumn = "ADDRESS_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql = " select "+findColumn+" FROM m_address WHERE customer_id  = (select customer_id from m_customer where code ='"+lineArray[Integer.parseInt(values[1])]+"')" ;		
				sql += " and purpose ='B' ";
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("")){
					exe = false;
				}
				classException ="FindAddressBillToException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).startsWith("VALIDATE_SHIP_ADDRESS_ID_BY_CUSTOCODE")){
				/** FIND_BILL_ADDRESS_ID_BY_CUSTOCODE|6 (By position of value) **/
				findColumn = "ADDRESS_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql = " select "+findColumn+" FROM m_address WHERE customer_id  = (select customer_id from m_customer where code ='"+lineArray[Integer.parseInt(values[1])]+"')" ;		
				sql += " and purpose ='S' ";
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("")){
					exe = false;
				}
				classException ="FindAddressShipToException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_ORDER_NO")){
				
				/** VALIDATE ORDER_NO **/
				findColumn = "ORDER_NO";
				sql = " select "+findColumn+" FROM t_order WHERE order_no = '"+value+"'";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindOrderIDException";
				valueDispError = value;
				
			/** RECEIPT_NO have 2 case  **/
			/** Case 1 Pay by Cash Receipt_no = SaleApp.receipt_no(ORCL) **/
			/** Case 2 Pay by Cheque receipt_no = cheque_no(ORCL)  **/
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_RECEIPT_NO")){
				/** VALIDATE ORDER_NO **/
				findColumn = "RECEIPT_ID";
				sql =  "  select "+findColumn+" FROM t_receipt WHERE receipt_no = '"+value+"' \n";	
				sql += "  union all \n";
				sql += "  select distinct "+findColumn+" from t_receipt_match where receipt_by_id in( \n";
				sql += " 	select receipt_by_id from t_receipt_by  where cheque_no ='"+value+"' \n";
				sql += "  ) \n";
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindReceiptIdException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_VISIT_ID")){
				/** VALIDATE CODE **/
				findColumn = "VISIT_ID";
				sql = " select "+findColumn+" FROM t_visit WHERE code = '"+value+"'";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindVisitIdException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_MODIFIER_ID")){
				
				findColumn = "MODIFIER_ID";
				sql = " select "+findColumn+" FROM m_modifier WHERE modifier_id = "+value+"";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindModifierIdException";
				valueDispError = value;
	      }else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_MODIFIER_LINE_ID")){
				
				findColumn = "MODIFIER_LINE_ID";
				sql = " select "+findColumn+" FROM m_modifier_line WHERE modifier_line_id = "+value+"";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindModifierLineIdException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_PRICELIST_ID")){
				
				findColumn = "pricelist_id";
				sql = " select "+findColumn+" FROM m_pricelist WHERE pricelist_id = "+value+"";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindPriceListIdException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_UOM_ID")){
				
				findColumn = "UOM_ID";
				sql = " select "+findColumn+" FROM m_uom WHERE uom_id = '"+value+"'";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindUOMException";
				valueDispError = value;
			}else if(Utils.isNull(colBean.getValidateFunc()).equalsIgnoreCase("VALIDATE_PRODUCT_ID")){
				findColumn = "PRODUCT_ID";
				sql = " select "+findColumn+" FROM m_product WHERE product_id ='"+value+"'" ;
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				classException ="FindProductException";
				valueDispError = value;
			}else{
				/** not found function validate **/
				exe = false;
			}
			
			
			/** Check Validate Require Filed **/
			if(colBean.getRequireField().equalsIgnoreCase("Y")){
				isRequireField = true;
			}
			
			logger.debug("Exc:"+exe);
			/** Case Value Validate NOT NULL **/
			if(exe){
				logger.debug("SQL:"+sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()){
					//logger.info("rs.getString(findColumn):"+rs.getString(findColumn));
					result = Utils.isNull(rs.getString(findColumn));
				}
				
				logger.debug("result["+result+"]");
				
				/** Validate Exception  */
				if(result.equals("")){
					/** Throws Exception Class **/
					ExceptionHandle.throwExceptionClass(classException,valueDispError,colBean.getColumnName());
				}
			}else{
				/** Case Value Validate IS NULL*/
				if(isRequireField){
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
