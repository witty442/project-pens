package com.isecinc.pens.web.externalprocess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.log4j.Logger;

import util.ExcelHeader;
import util.SQLHelper;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class ProcessAfterAction {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	public final static String SAVE_ORDER = "SaveOrder";
	public final static String SAVE_RECEIPT = "SaveReceipt";
	public final static String SAVE_STOCK_RETURN = "SaveStockReturn";
	public final static String SAVE_REQ_PROMOTION = "SaveReqPromotion";
	public final static String SAVE_STOCK_CUSTOMER = "SaveStockCustomer";
	public final static String SAVE_STOCK_DISCOUNT = "SaveStockDiscount";
	public final static String SAVE_MOVE_ORDER = "SaveMoveOrder";
	public final static String SAVE_CUSTOMER = "SaveCustomer";
	public final static String SAVE_TRANS_MONEY = "SaveTransMoney";
	
	/** Process run after Action ,ex SaveOrder ,SaveStockReturn */
	//sql :update t_stock_return set total_amount =0 where request_number=$request_number;
	public static void processAfterAction(String actionName,String keyValueDB){
		Connection conn = null;
		logger.info("processAfterAction Name["+actionName+"]keyValueDB["+keyValueDB+"]");
		try{
			conn = DBConnection.getInstance().getConnection();
			//Get sqlMethod 
			ProcessAfterBean pBean = getSql(conn, actionName);
			if(pBean != null){
				logger.info("found Process After Save["+actionName+"] sql:"+pBean.getSql());
				
				//replace key_db $request_number to keyValueDB
				String sql = pBean.getSql();
				logger.debug("before sql:"+sql);
				//replace key_db $request_number to keyValueDB (S0016305001)
				
				logger.debug("index of $"+pBean.getKeyDB()+":"+sql.indexOf("$"+pBean.getKeyDB()));
				if(sql.indexOf("$"+pBean.getKeyDB()) != -1 && !Utils.isNull(keyValueDB).equals("")){
					sql = sql.replaceAll("\\$"+pBean.getKeyDB(), "'"+keyValueDB+"'");
				}
				logger.debug("after sql:"+sql);
				if( !Utils.isNull(sql).equals("")){
					SQLHelper.excUpdate(conn, sql);
				}
			}else{
				logger.info("No Process After["+actionName+"] Save");
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();conn = null;
				}
			}catch(Exception ee){}
		}
	}
	
	public static ProcessAfterBean getSql(Connection conn,String actionName) {
	    PreparedStatement ps =null;
		ResultSet rs = null;
        String sql = "";
        ProcessAfterBean keyBean = null;
		try{
			sql = "select * from c_after_action_sql where action_name='"+actionName+"'";
			//System.out.println("sql:"+sql);   
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
		    if(rs.next()){
		    	keyBean = new ProcessAfterBean();
		    	keyBean.setKeyDB(Utils.isNull(rs.getString("key_db")));
		    	keyBean.setSql(Utils.isNull(rs.getString("sql_action")));
		    }
		}catch(Exception e){
	      e.printStackTrace();
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
				if(rs != null){
				   rs.close();rs = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return keyBean;
  }
	
}
