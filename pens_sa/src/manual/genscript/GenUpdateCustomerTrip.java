package manual.genscript;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class GenUpdateCustomerTrip {

	private static Logger logger = Logger.getLogger("PENS");

	static Connection connSource = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//start("V054");
		start();
	}
	
	public  static Connection getConnectionPROD(){		
		try {	
		
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@//192.168.37.185:1521/PENS";
			String username = "apps";
			String password = "apps";
			
			logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			 connSource = DriverManager.getConnection(url,username,password);	
			logger.debug("Connection:"+connSource);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return connSource;	
	}
	public static void start(){
        StringBuffer sql = new StringBuffer("");
        PreparedStatement ps =null;
        ResultSet rs = null;
		try{	
			logger.debug("----Start----");
			
			connSource = getConnectionPROD();
		    sql.append("select code "
		    		+ " from xxpens_salesreps_v  \n"
		    		+ " where isactive ='Y' \n"
		    		+ " and sales_channel ='C' \n"
		    		+ " and code like 'V%' \n");
		   
		    System.out.println("sql: \n"+sql.toString());
			ps = connSource.prepareStatement(sql.toString());
		    rs = ps.executeQuery();
			while(rs.next()){
				genScript(Utils.isNull(rs.getString("code")));
			}
			
			logger.debug("----Success----");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(connSource != null){
					connSource.close();connSource=null;
				}
				
			}catch(Exception e){}
		}
	}
	
	public static void genScript(String salesCode){
        StringBuffer sql = new StringBuffer("");
        PreparedStatement ps =null;
        ResultSet rs = null;
        StringBuffer dataOut = new StringBuffer("");
		try{	
			
			connSource = getConnectionPROD();
		    sql.append("\n select "
		    		+ "\n (select c.customer_code from  xxpens_bi_mst_customer c where customer_id =cs.cust_account_id ) as cust_code"
		    		+" \n ,trip1,trip2,trip3"
		    		+ "\n from xxpens_ar_cust_sales_all cs"
		    		+ "\n where cs.code ='"+salesCode+"' "
		    		+ "\n and (cs.trip1 <> 0 or cs.trip2 <> 0 or cs.trip3 <> 0)");
		   
		    System.out.println("sql: \n"+sql.toString());
			ps = connSource.prepareStatement(sql.toString());
		    rs = ps.executeQuery();
			while(rs.next()){
				if( !Utils.isNull(rs.getString("cust_code")).equals("")){
				  dataOut.append("update m_customer"
						+ " set trip_day="+rs.getInt("trip1")
						+ " , trip_day2="+rs.getInt("trip2")
						+ " , trip_day3="+rs.getInt("trip3")
					    + " where code ='"+Utils.isNull(rs.getString("cust_code"))+"' ; \n");
				}
			}
			FileUtil.writeFile("D:/Work_ISEC/A-TEMP-DB-C4/GenScript/UpdateCustTripVan/script_"+salesCode+".sql", dataOut.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
					
			}catch(Exception e){}
		}
	}
	
	
}
