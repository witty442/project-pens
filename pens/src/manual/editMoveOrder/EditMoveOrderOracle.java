package manual.editMoveOrder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.NumberToolsUtil;

import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.model.MUOMConversion;

public class EditMoveOrderOracle {

	private static Logger logger = Logger.getLogger("PENS");
	private static int count = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//getConnectionMysql();
		//getConnectionOracle();
		try{
			 logger.debug("--- Start process ------");
		     process();
		     logger.debug("--- End process Record["+count+"]------");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void process() throws Exception{
		Connection connMysql = getConnectionMysql();
		Connection connOracle = getConnectionOracle();
		try{
			//List MoveOrder Line int_flag is null
			List<MoveOrderLine> lineList = getMoveOrderLineList(connOracle);
			
			//Loop cacl new QTY 
			if(lineList != null && lineList.size() > 0){
				for(int i=0;i<lineList.size();i++){
					MoveOrderLine line = (MoveOrderLine)lineList.get(i);
					/** Calc Promary Qty **/
					line.setPrimary_quantity(calcPrimaryQty(connMysql, line));
					
					/** Update Oracle Temp Line **/
					updatePriQtyInLineTemp(connOracle, line);
					count++;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(connMysql != null){
				connMysql.close();connMysql=null;
			}
			if(connOracle != null){
				connOracle.close();connOracle=null;
			}
		}
	}
	
	public  static Connection getConnectionMysql(){		
		Connection connMysql = null;
		try {	
			
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/pens?useUnicode=true&amp;characterEncoding=UTF-8";
			String username = "pens";
			String password = "pens";
			
			logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			connMysql = DriverManager.getConnection(url,username,password);	
			
			logger.debug("Connection:"+connMysql);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return connMysql;	
	}
	
	public  static Connection getConnectionOracle(){	
		Connection connOracle = null;
		try {	
			
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@//192.168.37.185:1521/PENS";
			String username = "apps";
			String password = "apps";
			
			logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			Class.forName(driver);
			connOracle = DriverManager.getConnection(url,username,password);	
			logger.debug("Connection:"+connOracle);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return connOracle;	
	}
	
	public static List<MoveOrderLine> getMoveOrderLineList(Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<MoveOrderLine> list = new ArrayList<MoveOrderLine>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from xxpens_inv_morder_lines_temp where request_number in ( \n");
			sql.append("    select request_number from xxpens_inv_morder_headers_temp where (int_flag is null or int_flag ='E') \n");
			sql.append(" ) \n");
			
			//for Dev
			//sql.append(" and request_number ='V203-P002-55090011' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				MoveOrderLine line = new MoveOrderLine();
				line.setRequestNumber(rs.getString("request_number"));
				line.setLineNumber(rs.getInt("line_number"));
				line.setInventoryItemId(rs.getString("inventory_item_id"));
				
				line.setPrimary_quantity(rs.getDouble("primary_quantity"));
				line.setCtn_qty(rs.getDouble("ctn_qty"));
				line.setPcs_qty(rs.getDouble("pcs_qty"));
				
				list.add(line);
			}
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
		return list;
	}
	
	public static double calcPrimaryQty(Connection conn,MoveOrderLine line) throws Exception{
	    double priQty = 0;
		try{
			UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(Integer.parseInt(line.getInventoryItemId()), "CTN");//UOM1 default CTN
			UOMConversion  uc2 = new MUOMConversion().getCurrentConversionNotIn(Integer.parseInt(line.getInventoryItemId()), "CTN");//UOM1  <> CTN
			
			//No round  5 digit
			double pcsQtyTemp = 0;
			if(uc2.getConversionRate() > 0){
				pcsQtyTemp = line.getPcs_qty()/ (uc1.getConversionRate()/uc2.getConversionRate())  ;
			}
		
			String pcsQtyStr5Digit = NumberToolsUtil.decimalFormat(pcsQtyTemp, NumberToolsUtil.format_current_6_digit);
			pcsQtyStr5Digit = pcsQtyStr5Digit.substring(0,pcsQtyStr5Digit.length()-1);
			
			//logger.debug("pcsQtyStr5Digit:"+pcsQtyStr5Digit);
			//logger.debug("pcsQtyStr5Digit2:"+Double.parseDouble((pcsQtyStr5Digit)));
			
			double pcsQty = Double.parseDouble((pcsQtyStr5Digit));
			
		    priQty = line.getCtn_qty()  + pcsQty;
		      
		}catch(Exception e){
	      throw e;
		}finally{
			
		}
		return priQty;
	}
	
	public static void updatePriQtyInLineTemp(Connection conn ,MoveOrderLine line) throws Exception {
		PreparedStatement ps = null;
		try {
			logger.debug("updatePriQtyInLineTemp");
			
			StringBuffer sql = new StringBuffer("");
			sql.append("\n UPDATE xxpens_inv_morder_lines_temp ");
			sql.append("\n SET primary_quantity = ?  ");
			sql.append("\n , last_update_login = ?  ");
			sql.append("\n WHERE request_number = ? ");
			sql.append("\n and line_number = ? ");
			sql.append("\n and inventory_item_id = ? ");
			
			ps = conn.prepareStatement(sql.toString());
		    ps.setDouble(1, line.getPrimary_quantity());
		    ps.setDouble(2, 777);
		    ps.setString(3, line.getRequestNumber());
		    ps.setInt(4, line.getLineNumber());
		    ps.setString(5, line.getInventoryItemId());
		    
		    logger.debug("update result:"+ps.executeUpdate());
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
}
