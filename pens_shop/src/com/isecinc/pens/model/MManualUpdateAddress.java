package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.manual.ManualUpdateAddressBean;

public class MManualUpdateAddress {
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args){
		try{
			String s = "เขตจ.อ.เมืองพิจิตร";
			String r = s.replace("\\อ.", "");
			System.out.println("r["+r+"]");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
     
	public ManualUpdateAddressBean[] findUpdateAddress(String textRemove,String ids) throws Exception{
		Connection conn = null;
		PreparedStatement ps =null;
		ResultSet rs = null;
		int provinceId = 0;
		String masterProvinceName = "";
		int districtId = 0;
		String masterDistrictName = "";
		List<ManualUpdateAddressBean> r = new ArrayList<ManualUpdateAddressBean>();
		try{
			//Replace textRemove []
			textRemove = textRemove.replaceAll("\\[", "");
			textRemove = textRemove.replaceAll("\\]", "");
			logger.debug("textRemoveXX:"+textRemove);
			
			conn = DBConnection.getInstance().getConnection();
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" select address_id,province_id,province_name,district_id,line4 from m_address  \n");
			sql.append(" where (province_id is null or province_id =0 or district_id is null or district_id =0 ) \n");
			if( !Utils.isNull(ids).equals("")){
				sql.append(" or address_id in("+ids+") \n");       
			}
		   
			logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
	
			rs = ps.executeQuery();
			
			while(rs.next()){
				ManualUpdateAddressBean m = new ManualUpdateAddressBean();
				m.setAddressId(rs.getInt("address_id"));
				m.setProvinceId(rs.getInt("province_id"));
				m.setProvinceName(rs.getString("province_name"));
				m.setDistrictId(rs.getInt("district_id"));
				m.setDistrictName(rs.getString("line4"));
				
				if(m.getProvinceId()==0){
					m.setProvinceStyle("lineError");
				}
				if(m.getDistrictId()==0){
					m.setDistrictStyle("lineError");
				}
				provinceId = rs.getInt("province_id");
				if(provinceId==0){
				   String[] provinceArray = findProvinceId(conn, rs.getString("province_name"),textRemove);
				   provinceId = Utils.convertStrToInt(provinceArray[0]);
				   masterProvinceName = provinceArray[1];
				}else{
					masterProvinceName = findProvinceNameDB(conn, provinceId);
				}
				
				districtId = rs.getInt("district_id");
				if(provinceId !=0 && districtId == 0){
					String[] districtArray = findDistrictId(conn, provinceId, rs.getString("line4"),textRemove);
					districtId = Utils.convertStrToInt(districtArray[0]);
					masterDistrictName = districtArray[1];
				}else{
					masterDistrictName = findDistrictNameDB(conn, provinceId, districtId);
				}
				logger.debug("province_id["+provinceId+"]district_id["+districtId+"]addressId["+rs.getInt("address_id")+"]");
			    
				m.setMasterProvinceId(provinceId);
				m.setMasterProvinceName(masterProvinceName);
				m.setMasterDistrictId(districtId);
				m.setMasterDistrictName(masterDistrictName);

				r.add(m);
				
				masterDistrictName = "";
				masterProvinceName = "";
			}

			if (r.size() == 0) return null;
			ManualUpdateAddressBean[] array = new ManualUpdateAddressBean[r.size()];
			array = r.toArray(array);
			
			return array;
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
				   conn.close();conn=null;
				}
		}
	} 
	
	public int updateAddress(String textRemove) throws Exception{
		Connection conn = null;
		PreparedStatement ps =null;
		PreparedStatement psUpdate =null;
		ResultSet rs = null;
		int provinceId = 0;
		int districtId = 0;
		int countUpd = 0;
		try{
			textRemove = textRemove.replaceAll("\\[", "");
			textRemove = textRemove.replaceAll("\\]", "");
			logger.debug("textRemoveXX:"+textRemove);
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" select address_id,province_id,province_name,district_id,line4 from m_address  \n");
			sql.append("where (province_id is null or province_id =0 or district_id is null or district_id =0) \n");
		   
			logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			psUpdate = conn.prepareStatement("update m_address set province_id =? ,district_id = ? where address_id =? ");
			rs = ps.executeQuery();
			
			while(rs.next()){
				provinceId = rs.getInt("province_id");
				if(provinceId==0){
				   String[] provinceArray = findProvinceId(conn, rs.getString("province_name"),textRemove);
				   provinceId = Utils.convertStrToInt(provinceArray[0]);
				}
				
				districtId = rs.getInt("district_id");
				if(provinceId !=0 && districtId == 0){
					String[] districtArray = findDistrictId(conn, provinceId, rs.getString("line4"),textRemove);
					districtId = Utils.convertStrToInt(districtArray[0]);
				}
				
				logger.debug("province_id["+provinceId+"]district_id["+districtId+"]addressId["+rs.getInt("address_id")+"]");
			    // update province_id,district_id
				if(provinceId ==0){
				    psUpdate.setNull(1,java.sql.Types.INTEGER);	
				}else{
					psUpdate.setInt(1, provinceId);
				}
				
				if(districtId ==0){
					psUpdate.setNull(2,java.sql.Types.INTEGER);	
				}else{
					psUpdate.setInt(2, districtId);
				}
				psUpdate.setInt(3, rs.getInt("address_id"));
				psUpdate.addBatch();
			}
			countUpd = psUpdate.executeBatch().length;
			conn.commit();
			logger.debug("updateAddress commit");
			
			return countUpd;
		}catch(Exception e){
		  conn.rollback();
		  logger.debug("updateAddress Rollback");
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(psUpdate != null){
				psUpdate.close();psUpdate = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
			   conn.close();conn=null;
			}
		}
	} 
	
	public String[] findProvinceId(Connection conn,String provinceName,String textRemove) throws Exception{
		int provinceId =0;
		String[] provinceArray = null;
		textRemove = textRemove.replaceAll("\\n", "");
		String[] textRemoveArray = textRemove.split("\\,");
		if(textRemoveArray != null && textRemoveArray.length >0){
			for(int i=0;i<textRemoveArray.length;i++){
				String strRemove = Utils.removeStringEnter((textRemoveArray[i]));
				String provinceNameKey = provinceName.replace(""+strRemove, "");
				provinceArray = findProvinceIdDB(conn, provinceNameKey);
				provinceId = Utils.convertStrToInt(provinceArray[0]);
				
				logger.debug("strRemove["+strRemove +"]provinceName["+provinceName +"]provinceNameKey["+provinceNameKey+"]");
				
				if(provinceId != 0){
					break;
				}
				
				provinceName =provinceNameKey;
			}
		}
		return provinceArray;
	}
	
	public String[] findProvinceIdDB(Connection conn,String provinceName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String[] re = new String[2];
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select province_id , name from m_province  \n");
			sql.append(" where name like '%"+provinceName+"%' \n");
		   
			logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				re[0] = rs.getString("province_id");
				re[1] = rs.getString("name");
			}
		   return re;
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
	} 
	
	public String findProvinceNameDB(Connection conn,int provinceId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String re = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select name , name from m_province  \n");
			sql.append(" where province_id ="+provinceId+" \n");
		   
			logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				re = rs.getString("name");
			}
		   return re;
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
	} 
	
	
	
	public String[] findDistrictId(Connection conn,int provinceId,String districtName,String textRemove) throws Exception{
		int districtId =0;
		String[] districtArray = null;
		textRemove = textRemove.replaceAll("\\n", "");
		String[] textRemoveArray = textRemove.split("\\,");
		if(textRemoveArray != null && textRemoveArray.length > 0){
			for(int i=0;i<textRemoveArray.length;i++){
				String strRemove = Utils.removeStringEnter((textRemoveArray[i]));
				
				String districtNameKey = districtName.replace(""+strRemove, "");
				logger.debug("strRemove["+strRemove +"]districtName["+districtName +"]districtNameKey["+districtNameKey+"]");
				
				districtArray = findDistrictIdDB(conn, provinceId,districtNameKey);
				districtId = Utils.convertStrToInt(districtArray[0]);
				if(districtId != 0){
					break;
				}
				districtName = districtNameKey;
			}
		}
		return districtArray;
	}
	
	public String[] findDistrictIdDB(Connection conn,int provinceId,String districtName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String[] re = new String[2];
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select district_id ,name from m_district  \n");
			sql.append(" where name like '%"+districtName+"%' \n");
			sql.append(" and province_id = "+provinceId);
		   
			logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				re[0] = rs.getString("district_id");
				re[1] = rs.getString("name");
			}
		   return re;
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
	} 
	
	public String findDistrictNameDB(Connection conn,int provinceId,int districtId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String re = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select district_id ,name from m_district  \n");
			sql.append(" where district_id ="+districtId+"\n");
			sql.append(" and province_id = "+provinceId);
		   
			logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				re = rs.getString("name");
			}
		   return re;
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
	} 
}
