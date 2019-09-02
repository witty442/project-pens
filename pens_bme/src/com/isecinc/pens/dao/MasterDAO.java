package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;

public class MasterDAO {
	public static Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public String save(Connection conn,String action,String referenceCode,String interfaceValue,String interfaceDesc,String pensValue
			,String pensDesc,String pensDesc2,String createUser,String pensDesc3
			,String sequence ,String status){
		return saveModel(conn, action, referenceCode, interfaceValue, interfaceDesc, pensValue, pensDesc, pensDesc2, createUser, pensDesc3, sequence, status);
	}
	public String save(String action,String referenceCode,String interfaceValue,String interfaceDesc,String pensValue
			,String pensDesc,String pensDesc2,String createUser,String pensDesc3
			,String sequence ,String status){
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return saveModel(conn, action, referenceCode, interfaceValue, interfaceDesc, pensValue, pensDesc, pensDesc2, createUser, pensDesc3, sequence, status);
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception ee){}
		}
		return "";
	}
	//(action,referenceCode, interfaceValue, interfaceDesc, pensValue, pensDesc,pensDesc2, createUser,pensDesc3,sequence,status);
	public String saveModel(Connection conn,String action,String referenceCode,String interfaceValue,String interfaceDesc,String pensValue
			,String pensDesc,String pensDesc2,String createUser,String pensDesc3
			,String sequence ,String status){
		String msg ="";
		try{
			boolean dup = isDuplicate(conn, referenceCode, interfaceValue, interfaceDesc, pensValue, pensDesc);
			if("add".equalsIgnoreCase(action)){
				msg = saveMaster(conn, referenceCode, interfaceValue, interfaceDesc, pensValue, pensDesc,pensDesc2, createUser,pensDesc3,sequence,status);
			}
			
			else if("edit".equalsIgnoreCase(action)){
			    msg = updateMaster(conn, referenceCode, interfaceValue, interfaceDesc, pensValue, pensDesc,pensDesc2, createUser,pensDesc3,sequence,status);
			}
			
			else if("delete".equalsIgnoreCase(action)){
			   msg = deleteMaster(conn, referenceCode, interfaceValue);
			}
		}catch(Exception e){
			logger.error(e);
		}finally{
		}
		return msg;
	}
	public String saveMaster(Connection conn,String referenceCode,String interfaceValue,String interfaceDesc,String pensValue,
			String pensDesc,String pensDesc2,String createUser,String pensDesc3 ,String sequence,String status){
		
		PreparedStatement ps = null;
		String msg = "";
		int id = 0;
		try{
		    id = SequenceProcess.getNextValue("PENSBME_MST_REFERENCE");
		    
			StringBuffer sql = new StringBuffer(" INSERT INTO PENSBME_MST_REFERENCE\n"); 
			sql.append("(REFERENCE_CODE, INTERFACE_VALUE, INTERFACE_DESC, "
					+ "PENS_VALUE, PENS_DESC,PENS_DESC2, "
					+ "CREATE_DATE, CREATE_USER,PENS_DESC3,"
					+ "SEQUENCE,STATUS,ID) \n");
			sql.append("VALUES (?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,?,?) \n");
			
		    ps = conn.prepareStatement(sql.toString());
		    
		    ps.setString(1, Utils.isNull(referenceCode));
		    ps.setString(2, Utils.isNull(interfaceValue));
		    ps.setString(3, Utils.isNull(interfaceDesc));
		    ps.setString(4, Utils.isNull(pensValue));
		    ps.setString(5, Utils.isNull(pensDesc));
		    ps.setString(6, Utils.isNull(pensDesc2));
		    ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
		    ps.setString(8, Utils.isNull(createUser));
		    ps.setString(9, Utils.isNull(pensDesc3));
		    ps.setString(10, Utils.isNull(sequence));
		    ps.setString(11, Utils.isNull(status));
		    ps.setInt(12, id);
		    ps.execute();
	    
	        msg = "บันทึกข้อมูลเรียบร้อยแล้ว";
			
		}catch(Exception e){
			e.printStackTrace();
			msg = e.getMessage();
		}finally{
			try{
				if(ps != null){
				   ps.close();ps=null;
				}
				
			}catch(Exception e){}
		}
		return msg;
	}
	
public String updateMaster(Connection conn,String referenceCode,String interfaceValue,String interfaceDesc,String pensValue,
		String pensDesc,String pensDesc2,String createUser,String pensDesc3 ,String sequence,String status){
		
		PreparedStatement ps = null;
		String msg = "";
		try{
			
			StringBuffer sql = new StringBuffer(" UPDATE PENSBME_MST_REFERENCE \n"); 
			
			if( !Utils.isNull(referenceCode).equals("Store")){
			   sql.append("SET INTERFACE_DESC=?, PENS_VALUE=?, PENS_DESC=?, CREATE_DATE=?, CREATE_USER=? ,PENS_DESC2 =? ,PENS_DESC3 =? ,SEQUENCE=? ,STATUS = ? \n");
			   sql.append("WHERE REFERENCE_CODE = ? AND INTERFACE_VALUE =?\n");
			}else{
			   sql.append("SET INTERFACE_DESC=?, PENS_VALUE=?, PENS_DESC=?, CREATE_DATE=?, CREATE_USER=? ,PENS_DESC2 =? ,PENS_DESC3 =? ,SEQUENCE=? ,STATUS = ?,INTERFACE_VALUE =? \n");
			   sql.append("WHERE REFERENCE_CODE = ? AND PENS_VALUE =?\n");
			}
			//System.out.println("sql :"+sql.toString());
			//System.out.println("REFERENCE_CODE:"+referenceCode);
			//System.out.println("INTERFACE_VALUE:"+Utils.isNull(interfaceValue));
			//System.out.println("PENS_VALUE:"+pensValue);
			//System.out.println("pensDesc3:"+pensDesc3);
			
		    ps = conn.prepareStatement(sql.toString());
		    
		    ps.setString(1, Utils.isNull(interfaceDesc));
		    ps.setString(2, Utils.isNull(pensValue));
		    ps.setString(3, Utils.isNull(pensDesc));
		    ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
		    ps.setString(5, Utils.isNull(createUser));
		    ps.setString(6, Utils.isNull(pensDesc2));
		    ps.setString(7, Utils.isNull(pensDesc3));
		    ps.setString(8, Utils.isNull(sequence));
		    ps.setString(9, Utils.isNull(status));
		    
		    if( !Utils.isNull(referenceCode).equals("Store")){
		       ps.setString(10, Utils.isNull(referenceCode));
		       ps.setString(11, Utils.isNull(interfaceValue));
		    }else{
		       ps.setString(10, Utils.isNull(interfaceValue));
		       ps.setString(11, Utils.isNull(referenceCode));
			   ps.setString(12, Utils.isNull(pensValue));	
		    }
		    ps.execute();
	    
	        msg = "บันทึกแก้ไขข้อมูลเรียบร้อยแล้ว";
			
		}catch(Exception e){
			e.printStackTrace();
			msg = e.getMessage();
		}finally{
			try{
				if(ps != null){
				   ps.close();ps=null;
				}
				
			}catch(Exception e){}
		}
		return msg;
	}

	public String deleteMaster(Connection conn,String referenceCode,String interfaceValue){
		
		PreparedStatement ps = null;
		String msg = "";
		try{
			
			StringBuffer sql = new StringBuffer(" DELETE PENSBME_MST_REFERENCE\n"); 
			sql.append("WHERE REFERENCE_CODE = ? AND INTERFACE_VALUE =? \n");
			
		    ps = conn.prepareStatement(sql.toString());

		    ps.setString(1, Utils.isNull(referenceCode));
		    ps.setString(2, Utils.isNull(interfaceValue));
		    ps.execute();
	    
	        msg = "ลบข้อมูลเรียบร้อยแล้ว";
			
		}catch(Exception e){
			e.printStackTrace();
			msg = e.getMessage();
		}finally{
			try{
				if(ps != null){
				   ps.close();ps=null;
				}
				
			}catch(Exception e){}
		}
		return msg;
	}
	
	public boolean isDuplicate(Connection conn,String referenceCode,String interfaceValue,String interfaceDesc,String pensValue,String pensDesc) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		boolean dup = false;
		StringBuilder sql = new StringBuilder();;
		try {
			sql.append("\n  SELECT * from PENSBME_MST_REFERENCE \n");
			sql.append("\n  where 1=1  \n");
			sql.append(" and Reference_code ='"+referenceCode+"' \n");
			sql.append(" and Interface_value ='"+interfaceValue+"' \n");
			sql.append(" and pens_value = '"+pensValue+"' \n");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if(rst.next()) {
				dup = true;
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return dup;
	}

}
