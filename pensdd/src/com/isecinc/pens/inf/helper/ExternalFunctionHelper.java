
package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.process.document.MemberDocumentProcess;

/**
 * @author WITTY
 *
 */
public class ExternalFunctionHelper {

	protected static  Logger logger = Logger.getLogger("PENS");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * findExternalFunc
	 * @param conn
	 * @param colBean
	 * @param value
	 * @param lineArray
	 * @param userBean
	 * @return
	 * @throws Exception
	 * Desc: Case Import column Special value from Other text file
	 */
	public static String findExternalFunc(Connection conn,ColumnBean colBean,String[] lineArray,User userBean) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
        String id = "";
        String findColumn = "";
        String sql = "";
        String value = "";
        boolean exe = true;
        String msg ="";
		try{
			if(colBean.getTextPosition() >= lineArray.length ){
				value = "";
			}else{
				value = Utils.isNull(lineArray[colBean.getTextPosition()]);
			}	
			
			msg ="User["+userBean.getType()+"]Column["+colBean.getColumnName()+"]findExtFunc["+colBean.getExternalFunction()+"] value["+value+"]";
			
			if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_UOM_CLASS_ID")){
				findColumn = "UOM_CLASS_ID";
				sql = " select "+findColumn+" FROM m_uom_class WHERE NAME ='"+value+"'" ;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_UOM_ID")){
				findColumn = "UOM_ID";
				sql = " select "+findColumn+" FROM m_uom WHERE CODE ='"+value+"'" ;	
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_PROVINCE_ID")){
				findColumn = "PROVINCE_ID";
				sql = " select "+findColumn+" FROM m_province WHERE NAME LIKE '%"+value+"%'" ;
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_DISTRICT_ID")){
				findColumn = "DISTRICT_ID";
				sql = " select "+findColumn+" FROM m_district WHERE NAME LIKE '%"+value+"%'" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			/** FIND_DISTRICT_ID_BY_PROVINCE_ID|position(district)|position(province)  */
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_DISTRICT_ID_BY_PROVINCE_ID")){
				findColumn = "DISTRICT_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				
				sql  = " select "+findColumn+" FROM m_district WHERE NAME LIKE '%"+lineArray[Integer.parseInt(values[1])]+"%' " ;
				sql += " and province_id in( select province_id from m_province where NAME LIKE '%"+lineArray[Integer.parseInt(values[2])]+"%' ) ";

				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("") || Utils.isNull(lineArray[Integer.parseInt(values[2])]).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_CUSTOMER_ID")){
				findColumn = "CUSTOMER_ID";
				sql = " select "+findColumn+" FROM m_customer WHERE REFERENCE_ID = "+value+"" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_CUSTOMER_ID_BY_CODE")){
				findColumn = "CUSTOMER_ID";
				sql = " select "+findColumn+" FROM m_customer WHERE CODE = '"+value+"'" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_MODIFIER_ID")){
				findColumn = "MODIFIER_ID";
				sql = " select "+findColumn+" FROM m_modifier WHERE MODIFIER_ID = "+value+"" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_SALES_TARGET_ID")){
				/** FIND_SALES_TARGET_ID|2 (By position of value) **/
				findColumn = "SALES_TARGET_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql = " select "+findColumn+" FROM m_sales_target WHERE USER_ID = "+lineArray[Integer.parseInt(values[1])]+"" ;		
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_SUB_INVENTORY_ID")){
				/** FIND_SUB_INVENTORY_ID **/
				findColumn = "SUB_INVENTORY_ID";
				sql = " select "+findColumn+" FROM m_sub_inventory WHERE name = '"+value+"'";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_USER_ID")){	
				findColumn = "USER_ID";
				sql = " select "+findColumn+" FROM ad_user WHERE CODE = '"+value+"'";
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("GET_SEQ")){
				//VALUE :GET-SEQ|TABLE_NAME
				 String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				 id = SequenceHelper.getNextValue(values[1]).toString();
				 exe = false;
				 
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_PURPOSE")){	
				if("BILL_TO".equalsIgnoreCase(value)){
					id = "B";
				}else if("SHIP_TO".equalsIgnoreCase(value)){
					id = "S";
				}
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_INTERFACES_FLAG")){	
				id = "Y";
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CURRENT_DATE")){	
				id = Utils.format(new Date(), Utils.DD_MM_YYYY_WITHOUT_SLASH);
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CURRENT_USER")){	
				id = String.valueOf(userBean.getId());
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_ORDER_ID")){
				/** GET_ORDER_ID **/
				findColumn = "ORDER_ID";
				sql = " select "+findColumn+" FROM t_order WHERE order_no = '"+value+"'";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_ORDER_ID_MANUAL")){
				/** GET_ORDER_ID CASE Manual Form Oracl and Regen OrderNo  **/
				findColumn = "ORDER_ID";
				value = ImportHelper.getReplcaeNewOrderNo(value);
				sql = " select "+findColumn+" FROM t_order WHERE order_no = '"+value+"'";		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				
			/** RECEIPT_NO have 2 case  **/
			/** Case 1 Pay by Cash Receipt_no = SaleApp.receipt_no(ORCL) **/
			/** Case 2 Pay by Cheque receipt_no = cheque_no(ORCL)  **/
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_RECEIPT_ID")){
				/** GET_RECEIPT_ID **/
				findColumn = "RECEIPT_ID";
				sql =  "  select "+findColumn+" FROM t_receipt WHERE receipt_no = '"+value+"' \n";		
				sql += "  union all \n";
				sql += "  select distinct "+findColumn+" from t_receipt_match where receipt_by_id in( \n";
				sql += " 	select receipt_by_id from t_receipt_by  where cheque_no ='"+value+"' \n";
				sql += "  ) \n";
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CANCEL_FLAG")){	
				if(Utils.isNull(value).equals(Constants.INTERFACES_DOC_STATUS_VOID)){
					id = "Y";
				}else{
					id = "N";
				}
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_ADDRESS_ID")){
				findColumn = "ADDRESS_ID";
				sql = " select "+findColumn+" FROM m_address WHERE REFERENCE_ID = "+value+"" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_BILL_ADDRESS_ID_BY_CUSTOCODE")){
				/** FIND_BILL_ADDRESS_ID_BY_CUSTOCODE|6 (By position of value) **/
				findColumn = "ADDRESS_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql = " select "+findColumn+" FROM m_address WHERE customer_id  = (select customer_id from m_customer where code ='"+lineArray[Integer.parseInt(values[1])]+"')" ;		
				sql += " and purpose ='B' ";
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_SHIP_ADDRESS_ID_BY_CUSTOCODE")){
				/** FIND_BILL_ADDRESS_ID_BY_CUSTOCODE|6 (By position of value) **/
				findColumn = "ADDRESS_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql = " select "+findColumn+" FROM m_address WHERE customer_id  = (select customer_id from m_customer where code ='"+lineArray[Integer.parseInt(values[1])]+"')" ;		
				sql += " and purpose ='S' ";
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_REPLACE_ORDER_NO")){
				findColumn = "";
				if( !value.equals("")){
					id = ImportHelper.getReplcaeNewOrderNo(value);
				}
				exe = false;
				
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CURRENT_TIMESTAMP")){	
				id = Utils.format(new Date(), Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH);
				exe = false;
				
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GEN_CUST_CODE")){
				// String prefix = new DecimalFormat("00").format(Integer.parseInt(member.getTerritory()));
				String prefix = "";
				id = new MemberDocumentProcess().getNextDocumentNo(userBean.getCode(), prefix, userBean.getId(), conn);
				exe = false;
				
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_CUSTOMER_ID_BY_REFERENCE_ID")){
				findColumn = "CUSTOMER_ID";
				sql = " select "+findColumn+" FROM m_customer WHERE REFERENCE_ID = "+value+"" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_MEMBER_PRODUCT_ID_BY_CODE")){
				findColumn = "PRODUCT_ID";
				sql = " select "+findColumn+" FROM m_product WHERE code = '"+value+"'" ;		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("CONCAT")){
				/** CONCAT STR  ->CONCAT|1|2   ->1,2 position of Text Array**/
				findColumn = "";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				id = Utils.isNull(lineArray[Integer.parseInt(values[1])]) + " "+Utils.isNull(lineArray[Integer.parseInt(values[2])]);
				exe = false;
			
			/** FIND_DELIVERY_GROUP|14|15    **/
			/** ->14 Position of DistrictName **/
			/** ->15 Position of ProvinceName **/
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_DELIVERY_GROUP")){
				findColumn = "CODE";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql  = " select "+findColumn+" from c_reference where reference_id in( \n";
				sql += "   select reference_id from m_delivery_group where district_id in( \n";
				sql += "     select district_id FROM m_district WHERE NAME LIKE '%"+Utils.isNull(lineArray[Integer.parseInt(values[1])])+"%' \n" ;
				sql += "     and province_id in( select province_id from m_province where NAME LIKE '%"+Utils.isNull(lineArray[Integer.parseInt(values[2])])+"%' ) \n";
                sql += "  ) \n";
                sql +="  ) \n";
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("") || Utils.isNull(lineArray[Integer.parseInt(values[2])]).equals("")){
					exe = false;
				}
			}else{
				exe = false;
			}
			
			//logger.debug("Exc:"+exe);
			if(exe){
				logger.debug("FIND SQL:"+sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()){
					id = Utils.isNull(rs.getString(findColumn));
				}
			}
			
			msg += "resultValue["+id+"]";
			logger.debug(msg);
			
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return id;
	}
	
	
}
