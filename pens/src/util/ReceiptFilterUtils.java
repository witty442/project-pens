package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;


public class ReceiptFilterUtils {

	public static Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Filter Can Receipt Cheque
	///	String canReceiptCheque = ReceiptFilterUtils.canReceiptCheque(customerCode);

	}
	
	public  static String canReceiptCheque(int customerId){
		String canFlag = "N";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			String sql = "select count(*) as x from m_customer c, c_customer_receipt_cheque r \n" +
					" where c.customer_id ="+customerId+" and r.isactive ='Y' \n"+
					" and r.customer_code = c.code \n";
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("x") > 0){
					canFlag = "Y";
				}
			}
			
			logger.debug("canFlag:"+canFlag);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn != null){
				   conn.close();conn=null;
				}
				if(rs !=null){
				   rs.close();rs=null;
				}
				if(ps !=null ){
				  ps.close();ps=null;
				}
			}catch(Exception e){
				
			}
		}
		return canFlag;
	}

}
