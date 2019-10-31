
package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ColumnBean;
import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.pens.util.Constants;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

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
	public static String findExternalFunc(Connection conn,TableBean tableBean,ColumnBean colBean,String[] lineArray,User userBean) throws Exception{
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
			
			msg ="Column["+colBean.getColumnName()+"]findExtFunc["+colBean.getExternalFunction()+"] value["+value+"]";
			
			
			 if(Utils.isNull(colBean.getExternalFunction()).equals("GET_USER_ID")){	
				findColumn = "USER_ID";
				sql.append(" select "+findColumn+" FROM ad_user WHERE CODE = '"+value+"'" );
				if(Utils.isNull(value).equals("")){
					exe = false;
				}
		   
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("GET_SEQ")){
				//VALUE :GET-SEQ|TABLE_NAME
				 String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				 id = SequenceProcess.getNextValue(values[1]).toString();
				 exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_INTERFACES_FLAG")){	
				id = "Y";
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CURRENT_DATE")){	
				id = DateUtil.format(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CURRENT_USER")){	
				id = String.valueOf(userBean.getUserName());
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CANCEL_FLAG")){	
				if(Utils.isNull(value).equals(Constants.INTERFACES_DOC_STATUS_VOID)){
					id = "Y";
				}else{
					id = "N";
				}
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_CURRENT_TIMESTAMP")){	
				id = DateUtil.format(new Date(), DateUtil.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH);
				//logger.debug("GET_CURRENT_TIMESTAMP:"+id);
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_FILE_NAME_IMPORT")){	
				
				id = Utils.isNull(tableBean.getFileFtpNameFull());
				exe = false;
			}else if(Utils.isNull(colBean.getExternalFunction()).startsWith("CONCAT")){
				/** CONCAT STR  ->CONCAT|1|2   ->1,2 position of Text Array**/
				findColumn = "";
				String[] values = Utils.isNull(colBean.getExternalFunction()).split(Constants.delimeterPipe);
				id = Utils.isNull(lineArray[Integer.parseInt(values[1])]) + " "+Utils.isNull(lineArray[Integer.parseInt(values[2])]);
				exe = false;
		
			/** Get Role By UserName :V001 ->VAN  ,S101 -> TT  **/
			}else if(Utils.isNull(colBean.getExternalFunction()).equals("GET_ROLE_BY_USER")){	
				id = "";
				if(userBean.getUserName().startsWith("V")){
					id = "VAN";
				}else if(userBean.getUserName().startsWith("S")){
					id ="TT";
				}
				
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
				}
			}
			
			msg += "resultValue["+id+"]";
			//logger.debug(msg);
			
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
	
	/** Case Order Manaul (from Oracle )
	 * order_no length == 12 only
	 * order ที่ขึ้นต้นด้วย 2  ให้ replace เป็น S
       order ที่ขึ้นต้นด้วย 3 ให้ replace เป็น C
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	public static String getReplcaeNewOrderNo(String orderNo) throws Exception{
		if(Utils.isNull(orderNo).length() ==12){
			String firstPos = orderNo.substring(0,1);
			if(firstPos.equals("2")){
				orderNo = "S"+orderNo.substring(1,orderNo.length());
			}else if(firstPos.equals("3")){
				orderNo = "V"+orderNo.substring(1,orderNo.length());
			}
		}
		return orderNo;
	}
	
	
}
