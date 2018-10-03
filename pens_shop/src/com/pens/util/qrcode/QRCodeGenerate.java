package com.pens.util.qrcode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class QRCodeGenerate {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	public static String generate(Map<String, Object> inputMap) throws Exception{
		String qrcode = "";
		QRCodeBean bean = null;
		List<QRCodeBean> columnQRCodeList = new ArrayList<QRCodeBean>();
		Map<String, String> DATA_INFO_MAP = null;
		Connection conn = null;
		int i=0;
		try{
			//init connection
			conn = DBConnection.getInstance().getConnection();
			
			//init fix data map
			DATA_INFO_MAP = initDataMap(conn);
			
			//init List Column Qrcode
			columnQRCodeList = getListQRCodColumn(conn);
			
			//Gen
			if(columnQRCodeList!= null){
				for(i=0;i<columnQRCodeList.size();i++){
					bean = columnQRCodeList.get(i);
					if(bean.getSubItemList()!= null && bean.getSubItemList().size() >0){
					    //Loop
					}else{
						//qrcode += mapQrcode(DATA_INFO_MAP,bean);
						
					}
				}//for
			}//if
			return qrcode;
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	public static String mapQrcode(Map<String, Object> DATA_INFO_MAP ,QRCodeBean bean) throws Exception{
		String qrcode = "";
		try{
			if("FIXED".equalsIgnoreCase(bean.getType())){
			    qrcode += bean.getValue();
			}else if("GET_INFO".equalsIgnoreCase(bean.getType())){
				//qrcode += bean.getValue()+getLen(DATA_INFO_MAP.get(bean.getValue()))+DATA_INFO_MAP.get(bean.getValue());
			}else if("GET_CHECKSUM".equalsIgnoreCase(bean.getType())){
				qrcode += bean.getValue()+"0145";//Fortest
			}else{
				// Get Data From INPUT MAP +length +input data
				qrcode += bean.getValue()+getLen("");
			}
			return qrcode;
		}catch(Exception e){
			throw e;
		}
	}
	public static String getLen(String s){
		String len = "";
		if(s.length()<10){
			len ="0"+s.length();
		}else{
			len = String.valueOf(s.length());
		}
		return len;
	}
	public static Map<String, String> initDataMap(Connection conn) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		Map<String, String> DATA_FIX_MAP = new HashMap<String, String>();
		try{
			sql.append(" select * from c_qrcode where isactive ='Y' and seq = 0");
			ps = conn.prepareStatement(sql.toString());
			rs =ps.executeQuery();
			while(rs.next()){
				DATA_FIX_MAP.put(Utils.isNull(rs.getString("name")), Utils.isNull(rs.getString("value")));
			}
			return DATA_FIX_MAP;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps !=null){
				ps.close();ps=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}
	}
	
	public static List<QRCodeBean> getListQRCodColumn(Connection conn) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		List<QRCodeBean> columnQRCodeList = new ArrayList<QRCodeBean>();
		QRCodeBean bean = null;
		try{
			sql.append(" select * from c_qrcode where isactive ='Y' and seq <> 0 order by seq");
			ps = conn.prepareStatement(sql.toString());
			rs =ps.executeQuery();
			while(rs.next()){
				bean = new QRCodeBean();
				bean.setSeq(rs.getInt("seq"));
				bean.setName(Utils.isNull(rs.getString("name")));
				bean.setValue(Utils.isNull(rs.getString("value")));
				bean.setType(Utils.isNull(rs.getString("type")));
				bean.setDesc(Utils.isNull(rs.getString("description")));
				
				//Get Sub Qrcode
				bean.setSubItemList(getSubListQRCodColumn(conn, bean.getName()));
				
				columnQRCodeList.add(bean);
			}
			return columnQRCodeList;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps !=null){
				ps.close();ps=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}
	}
	
	public static List<QRCodeBean> getSubListQRCodColumn(Connection conn,String mainName) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		List<QRCodeBean> columnQRCodeList = new ArrayList<QRCodeBean>();
		QRCodeBean bean = null;
		try{
			sql.append(" select * from c_qrcode_sub where main_name='"+mainName+"' isactive ='Y' and seq <> 0 order by seq");
			ps = conn.prepareStatement(sql.toString());
			rs =ps.executeQuery();
			while(rs.next()){
				bean = new QRCodeBean();
				bean.setSeq(rs.getInt("seq"));
				bean.setName(Utils.isNull(rs.getString("name")));
				bean.setValue(Utils.isNull(rs.getString("value")));
				bean.setType(Utils.isNull(rs.getString("type")));
				bean.setDesc(Utils.isNull(rs.getString("description")));
				
				columnQRCodeList.add(bean);
			}
			return columnQRCodeList;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps !=null){
				ps.close();ps=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}
		
	}
}
