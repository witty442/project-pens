
package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	
	public static Map<String, String> RECEIPT_MAP = new HashMap<String, String>();
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
		PreparedStatement psIns =null;
		ResultSet rs = null;
        String id = "";
        String findColumn = "";
        StringBuffer sql = new StringBuffer("");
        StringBuffer insertSQl = new StringBuffer("");
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
				sql.append(" select "+findColumn+" FROM m_uom_class WHERE NAME ='"+value+"'") ;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_UOM_ID")){
				findColumn = "UOM_ID";
				sql.append(" select "+findColumn+" FROM m_uom WHERE CODE ='"+value+"'" );	
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_PROVINCE_ID")){
				findColumn = "PROVINCE_ID";
				sql.append(" select "+findColumn+" FROM m_province WHERE NAME LIKE '%"+Utils.replaceProvinceNameNotMatch(value)+"%'" );
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_DISTRICT_ID")){
				findColumn = "DISTRICT_ID";
				sql.append(" select "+findColumn+" FROM m_district WHERE NAME LIKE '%"+Utils.replaceDistrictNameNotMatch(value)+"%'" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			/** FIND_DISTRICT_ID_BY_PROVINCE_ID|position(district)|position(province)  */
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_DISTRICT_ID_BY_PROVINCE_ID")){
				findColumn = "DISTRICT_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				
				sql.append(" select "+findColumn+" FROM m_district WHERE NAME LIKE '%"+Utils.replaceDistrictNameNotMatch((lineArray[Integer.parseInt(values[1])]))+"%' " );
				sql.append("  and province_id in( select province_id from m_province where NAME LIKE '%"+Utils.replaceProvinceNameNotMatch(lineArray[Integer.parseInt(values[2])])+"%' ) " );

				/** Case Find District ID not found insert new m_district */
				insertSQl.append(" insert into m_district(DISTRICT_ID, NAME, PROVINCE_ID, CODE)  \n");
				insertSQl.append(" select \n");
				insertSQl.append(" (select max(district_id)+1 from m_district ) as DISTRICT_ID , \n" );//DISTRICT_ID
				insertSQl.append("'"+ Utils.isNull(lineArray[Integer.parseInt(values[1])]) +"' as NAME,\n");//NAME
				insertSQl.append(" ( select province_id from m_province where NAME LIKE '%"+Utils.replaceProvinceNameNotMatch(lineArray[Integer.parseInt(values[2])])+"%' ) as PROVINCE_ID , \n");//PROVINCE_ID
				insertSQl.append(" ( \n");//CODE
				insertSQl.append("   select ( case when length(max(code)+1) = 1  \n ");
				insertSQl.append("		 then '0'|| max(code)+1 else max(code)+1 end ) \n");
				insertSQl.append("		 from m_district \n");
				insertSQl.append("		 where province_id in(select province_id from m_province where NAME LIKE '%"+Utils.replaceProvinceNameNotMatch(lineArray[Integer.parseInt(values[2])])+"%' ) \n");
				insertSQl.append("  ) as CODE \n");
				
				
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("") || Utils.isNull(lineArray[Integer.parseInt(values[2])]).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_CUSTOMER_ID")){
				findColumn = "CUSTOMER_ID";
				sql.append(" select "+findColumn+" FROM m_customer WHERE REFERENCE_ID = "+value+"" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_CUSTOMER_ID_BY_CODE")){
				findColumn = "CUSTOMER_ID";
				sql.append(" select "+findColumn+" FROM m_customer WHERE CODE = '"+value+"'" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_CUSTOMER_NAME_BY_CODE")){
				findColumn = "NAME";
				sql.append(" select "+findColumn+" FROM m_customer WHERE CODE = '"+value+"'" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_MODIFIER_ID")){
				findColumn = "MODIFIER_ID";
				sql.append(" select "+findColumn+" FROM m_modifier WHERE MODIFIER_ID = "+value+"" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_SALES_TARGET_ID")){
				/** FIND_SALES_TARGET_ID|2 (By position of value) **/
				findColumn = "SALES_TARGET_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql.append(" select "+findColumn+" FROM m_sales_target WHERE USER_ID = "+lineArray[Integer.parseInt(values[1])]+"" );		
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_SUB_INVENTORY_ID")){
				/** FIND_SUB_INVENTORY_ID **/
				findColumn = "SUB_INVENTORY_ID";
				sql.append(" select "+findColumn+" FROM m_sub_inventory WHERE name = '"+value+"'" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_USER_ID")){	
				findColumn = "USER_ID";
				sql.append(" select "+findColumn+" FROM ad_user WHERE CODE = '"+value+"'" );
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
				sql.append(" select "+findColumn+" FROM t_order WHERE order_no = '"+value+"'" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_ORDER_ID_MANUAL")){
				/** GET_ORDER_ID CASE Manual Form Oracl and Regen OrderNo  **/
				findColumn = "ORDER_ID";
				value = ImportHelper.getReplcaeNewOrderNo(value);
				sql.append(" select "+findColumn+" FROM t_order WHERE order_no = '"+value+"'" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				
			/** RECEIPT_NO have 2 case  **/
			/** Case 1 Pay by Cash Receipt_no = SaleApp.receipt_no(ORCL) **/
			/** Case 2 Pay by Cheque receipt_no = cheque_no(ORCL)  **/
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_RECEIPT_ID")){
				/** GET_RECEIPT_ID **/
				findColumn = "RECEIPT_ID";
				sql.append(" select "+findColumn+" FROM t_receipt WHERE receipt_no = '"+value+"' \n" );		
				sql.append(" union all \n" );
				sql.append(" select distinct "+findColumn+" from t_receipt_match where receipt_by_id in( \n" );
				sql.append("  	select receipt_by_id from t_receipt_by  where cheque_no ='"+value+"' \n" );
				sql.append("   ) \n" );
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			/** RECEIPT_NO have 2 case  **/
			/** Case 1 Pay by Cash Receipt_no = SaleApp.receipt_no(ORCL) **/
			/** Case 2 Pay by Cheque receipt_no = cheque_no(ORCL)  **/
			/** After receipt_id not found gen new receipt_id for insert and put receipt_id for line **/
		    }else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_RECEIPT_ID_IN_HEAD")){
					/** GET_RECEIPT_ID **/
					findColumn = "RECEIPT_ID";
					sql.append(" select "+findColumn+" FROM t_receipt WHERE receipt_no = '"+value+"' \n" );		
					sql.append(" union all \n" );
					sql.append(" select distinct "+findColumn+" from t_receipt_match where receipt_by_id in( \n" );
					sql.append("  	select receipt_by_id from t_receipt_by  where cheque_no ='"+value+"' \n" );
					sql.append("   ) \n" );
					
					if( !Utils.isNull(value).equals("")){
						ps = conn.prepareStatement(sql.toString());
						rs = ps.executeQuery();
						if(rs.next()){
							id = rs.getString(findColumn);
						}
						
						//** Case not found Receipt Id and Insert new receipt  Gen new receipt_id **/
						if("".equals(Utils.isNull(id))){
							id = String.valueOf(SequenceHelper.getNextValue("t_receipt"));
							RECEIPT_MAP.put(Utils.isNull(value), id);//Put New ReceiptId for use in receiptId Line
						}
						
					}	
					exe = false;
			
			/** RECEIPT_NO have 2 case  **/
			/** Case 1 Pay by Cash Receipt_no = SaleApp.receipt_no(ORCL) **/
			/** Case 2 Pay by Cheque receipt_no = cheque_no(ORCL)  **/
			/** After receipt_id not found  get receipt_id from RECEIPT_MAP **/
		    }else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_RECEIPT_ID_IN_LINE")){
				/** GET_RECEIPT_ID **/
				findColumn = "RECEIPT_ID";
				sql.append(" select "+findColumn+" FROM t_receipt WHERE receipt_no = '"+value+"' \n" );		
				sql.append(" union all \n" );
				sql.append(" select distinct "+findColumn+" from t_receipt_match where receipt_by_id in( \n" );
				sql.append("  	select receipt_by_id from t_receipt_by  where cheque_no ='"+value+"' \n" );
				sql.append("   ) \n" );
				
				if( !Utils.isNull(value).equals("")){
					ps = conn.prepareStatement(sql.toString());
					rs = ps.executeQuery();
					if(rs.next()){
						id = rs.getString(findColumn);
					}
					
					//** Case not found Receipt Id Get From RECEIPT_MAP **/
					if("".equals(Utils.isNull(id))){
						id = RECEIPT_MAP.get(Utils.isNull(value));
					}
					
				}	
				exe = false;
			
		    /**
		     * GET_LINE_NO By Receipt ID and get Max(line_no) +1
		     */
		    }else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_LINE_NO_BY_RECEIPT_ID")){
		    	findColumn = "next_line_no";
		    	String receiptId = RECEIPT_MAP.get(Utils.isNull(value));
				sql.append(" select (max(line_no)+1) as next_line_no FROM t_receipt_line WHERE receipt_id = "+receiptId+" \n" );		
                logger.info("sql:"+sql.toString());
                
				if( !Utils.isNull(value).equals("")){
					ps = conn.prepareStatement(sql.toString());
					rs = ps.executeQuery();
					if(rs.next()){
						id = rs.getString(findColumn);
						if("".equals(Utils.isNull(id))){
						   id = "1"; 
						}
					}else{
						id = "1";
					}
				}	
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CANCEL_FLAG")){	
				if(Utils.isNull(value).equals(Constants.INTERFACES_DOC_STATUS_VOID)){
					id = "Y";
				}else{
					id = "N";
				}
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_ADDRESS_ID")){
				findColumn = "ADDRESS_ID";
				sql.append("  select "+findColumn+" FROM m_address WHERE REFERENCE_ID = "+value+"" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_BILL_ADDRESS_ID_BY_CUSTOCODE")){
				/** FIND_BILL_ADDRESS_ID_BY_CUSTOCODE|6 (By position of value) **/
				findColumn = "ADDRESS_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql.append("  select "+findColumn+" FROM m_address WHERE customer_id  = (select customer_id from m_customer where code ='"+lineArray[Integer.parseInt(values[1])]+"')" );		
				sql.append("  and purpose ='B' ");
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("FIND_SHIP_ADDRESS_ID_BY_CUSTOCODE")){
				/** FIND_BILL_ADDRESS_ID_BY_CUSTOCODE|6 (By position of value) **/
				findColumn = "ADDRESS_ID";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				sql.append("  select "+findColumn+" FROM m_address WHERE customer_id  = (select customer_id from m_customer where code ='"+lineArray[Integer.parseInt(values[1])]+"')" );		
				sql.append("  and purpose ='S' ");
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
				sql.append("  select "+findColumn+" FROM m_customer WHERE REFERENCE_ID = "+value+"" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_MEMBER_PRODUCT_ID_BY_CODE")){
				findColumn = "PRODUCT_ID";
				sql.append("  select "+findColumn+" FROM m_product WHERE code = '"+value+"'" );		
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
				sql.append("  select "+findColumn+" from c_reference where reference_id in( \n");
				sql.append("    select reference_id from m_delivery_group where district_id in( \n");
				sql.append("      select district_id FROM m_district WHERE NAME LIKE '%"+Utils.isNull(lineArray[Integer.parseInt(values[1])])+"%' \n") ;
				sql.append("      and province_id in( select province_id from m_province where NAME LIKE '%"+Utils.isNull(lineArray[Integer.parseInt(values[2])])+"%' ) \n");
				sql.append("   ) \n");
				sql.append("   ) \n");
				if(Utils.isNull(lineArray[Integer.parseInt(values[1])]).equals("") || Utils.isNull(lineArray[Integer.parseInt(values[2])]).equals("")){
					exe = false;
				}
			/** Get Role By UserName :V001 ->VAN  ,S101 -> TT  **/
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_ROLE_BY_USER")){	
				id = "";
				if(userBean.getUserName().startsWith("V")){
					id = "VAN";
				}else if(userBean.getUserName().startsWith("S")){
					id ="TT";
				}
				
				exe = false;
			/** Get Code of Payment Method **/	
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_PAYMENT_METHOD_CODE")){
				findColumn = "value";
				sql.append("  select max("+findColumn+") as "+findColumn+" FROM c_reference WHERE code = 'PaymentMethod' and name like'%"+Utils.isNull(value)+"%'");		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
				
			/** Find order_id by ar_invoice_no **/
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_ORDER_ID_BY_AR_INVOICE_NO")){
				findColumn = "ORDER_ID";
				sql.append("  select "+findColumn+" FROM t_order WHERE ar_invoice_no = '"+value+"'" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
			
			/** Find order_line_id by ar_invoice_no **/
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("FIND_ORDER_LINE_ID_BY_AR_INVOICE_NO")){
				findColumn = "ORDER_LINE_ID";
				sql.append("  select "+findColumn+" FROM t_order_line WHERE ar_invoice_no = '"+value+"'" );		
				if(Utils.isNull(value).equals("")){
					exe = false;
				}	
				
			/** GET Order Type  **/
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_ORDER_TYPE")){	
				id =  userBean.getOrderType().getKey();
				exe = false;
			}else{
				exe = false;
			}
			
			//logger.debug("Exc:"+exe);
			if(exe){
				logger.debug("FIND SQL:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				
				if(rs.next()){
					id = Utils.isNull(rs.getString(findColumn));
				}else{
					//Case Find district_id not found  Create New 
					if( !Utils.isNull(insertSQl.toString()).equals("")){
						
						ps = conn.prepareStatement("select (max(district_id)+1) as "+findColumn+"  from m_district");
						rs = ps.executeQuery();
						
						if(rs.next()){
							id = Utils.isNull(rs.getString(findColumn));
						}
						
						logger.debug("Insert SQL:"+insertSQl.toString());
						psIns = conn.prepareStatement(insertSQl.toString());
						logger.debug("Result Insert SQL:"+psIns.executeUpdate());
					}
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
			if(psIns != null){
			   psIns.close();psIns = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return id;
	}
	
	
}
