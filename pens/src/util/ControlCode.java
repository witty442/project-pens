package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class ControlCode {
	public static Logger logger = Logger.getLogger("PENS");
	
	public static boolean canExecuteMethod(String className,String methodName){
		boolean canExecute = true;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "select isactive from c_control_code where class_name='"+className+"' and method_name='"+methodName+"'";
		try{
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps =conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(Utils.isNull(rs.getString("isactive")).equalsIgnoreCase("Y")){
					canExecute = true;
				}else if(Utils.isNull(rs.getString("isactive")).equalsIgnoreCase("N")){
					canExecute = false;
				}
			}
			logger.info("class_name["+className+"]method_name["+methodName+"]canExecute["+canExecute+"]");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				conn.close();
				rs.close();
				ps.close();
			}catch(Exception ee){}
		}
		return canExecute;
	}
}
