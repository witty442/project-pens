package com.manual;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class ReUpdateLotus {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
		   //reUpdateLotus();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void moveTable(){
		Connection sourceConn = null;
		Connection destConn = null;
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void moveTable(Connection sourceConn,Connection destConn){
		PreparedStatement psSource = null;
		ResultSet rsSource = null;
		PreparedStatement psDest = null;
		
		try{
			psSource = sourceConn.prepareStatement("select * from PENSBME_MST_REFERENCE");
			rsSource = psSource.executeQuery();
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBME_MST_REFERENCE( \n");
			sql.append(" REFERENCE_CODE,INTERFACE_VALUE,INTERFACE_DESC ,PENS_VALUE,PENS_DESC,PENS_DESC2," +
					    "PENS_DESC3,sequence,status,PENS_DESC4,PENS_DESC5,PENS_DESC6)\n");
			sql.append(" VALUES( ?,?,?,?,?,?,?,?,?,?,?,?)");
			psDest = destConn.prepareStatement(sql.toString());
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	 public static void reUpdateLotus() throws Exception{
	    Statement stmt = null;
		ResultSet rst = null;
		PreparedStatement psUpdate = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ImportDAO importDAO = new ImportDAO();
		try {
			conn = DBConnection.getInstance().getConnection();
			sql.append("\n  select SALES_DATE,STYLE_NO,description,PENS_CUST_CODE from PENSBME_SALES_FROM_LOTUS  WHERE PENS_ITEM IS NULL ");
			System.out.println("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			//update 
			sql = new StringBuilder("");
			sql.append("UPDATE PENSBME_SALES_FROM_LOTUS SET PENS_ITEM = ? WHERE SALES_DATE = ? AND STYLE_NO = ? AND PENS_CUST_CODE = ? \n ");
			psUpdate = conn.prepareStatement(sql.toString());
			
			while (rst.next()) {
				Date   salesDate = rst.getDate("SALES_DATE");
				String styleNo = rst.getString("STYLE_NO");
				String pensCustCode = rst.getString("PENS_CUST_CODE"); //StoreNo
				String description = rst.getString("description");
				
				String lotusItem = description.substring(0,10);
				String pensItem = "";//importDAO.getItemByInterfaceValueTypeLotus(conn, Constants.STORE_TYPE_LOTUS_STRING, lotusItem);
				System.out.println("Check desc["+description+"]lotusItem["+lotusItem+"] pesnItem["+pensItem+"]");
				
				if( !Utils.isNull(pensItem).equals("")){
					psUpdate.setString(1, pensItem);
					psUpdate.setDate(2, new java.sql.Date(salesDate.getTime()));
					psUpdate.setString(3, styleNo);
					psUpdate.setString(4, pensCustCode);
					
					int r = psUpdate.executeUpdate();
					System.out.println("Update PENSBME_SALES_FROM_LOTUS SALES_DATE["+salesDate+"]styleNo["+styleNo+"]pensCustCode["+pensCustCode+"]->pensItem["+pensItem+"] result["+r+"]");
				}
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				psUpdate.close();
				conn.close();
			} catch (Exception e) {}
		}
	}

}
