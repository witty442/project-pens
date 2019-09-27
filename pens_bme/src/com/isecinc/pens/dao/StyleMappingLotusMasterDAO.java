package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.StyleMappingLotusMasterBean;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class StyleMappingLotusMasterDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static List<StyleMappingLotusMasterBean> searchStyleMappingLotusMaster(StyleMappingLotusMasterBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		StyleMappingLotusMasterBean h = null;
		List<StyleMappingLotusMasterBean> items = new ArrayList<StyleMappingLotusMasterBean>();
		try {
			sql.append("\n select J.* from PENSBME_STYLE_MAPPING J   ");
			sql.append("\n where 1=1");
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and MATERIAL_MASTER LIKE '"+Utils.isNull(o.getGroupCode())+"%'");
			}
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and PENS_ITEM LIKE '%"+Utils.isNull(o.getPensItem())+"%'");
			}
			if( !Utils.isNull(o.getStyleNo()).equals("")){
				sql.append("\n and STYLE LIKE '%"+Utils.isNull(o.getStyleNo())+"%'");
			}
			sql.append("\n order by MATERIAL_MASTER,STYLE asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
		       h = new StyleMappingLotusMasterBean();
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setStyleNo(Utils.isNull(rst.getString("style")));
			   h.setGroupCode(Utils.isNull(rst.getString("MATERIAL_MASTER"))); 
			   h.setCanEdit(true);
			   items.add(h);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return items;
	}

	public static StyleMappingLotusMasterBean isExist(Connection conn,StyleMappingLotusMasterBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StyleMappingLotusMasterBean h = null;
		try {
			sql.append("\n select j.* from PENSBME_STYLE_MAPPING J   ");
			sql.append("\n where 1=1   ");
			sql.append("\n and STYLE = '"+Utils.isNull(o.getStyleNo())+"'");
			sql.append("\n and MATERIAL_MASTER = '"+Utils.isNull(o.getGroupCode())+"'");
			sql.append("\n and PENS_ITEM = '"+Utils.isNull(o.getPensItem())+"'");
			
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				h = new StyleMappingLotusMasterBean();
				h.setStyleNo(Utils.isNull(rst.getString("style")));
				h.setGroupCode(Utils.isNull(rst.getString("MATERIAL_MASTER"))); 
				h.setPensItem(Utils.isNull(rst.getString("pens_item"))); 
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return h;
	}
	
	 public static void insertNew(Connection conn,StyleMappingLotusMasterBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_STYLE_MAPPING \n");
				sql.append(" (STYLE,MATERIAL_MASTER, PENS_ITEM) \n");
			    sql.append(" VALUES (?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());

				ps.setString(c++, Utils.isNull(o.getStyleNo()));
				ps.setString(c++, o.getGroupCode());
				ps.setString(c++, o.getPensItem());
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
		public static void update(Connection conn,StyleMappingLotusMasterBean oldBean,StyleMappingLotusMasterBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				logger.debug("***Key to Update:***");
				logger.debug("GROUP_CODE:"+Utils.isNull(oldBean.getGroupCode()));
				logger.debug("STYLE:"+Utils.isNull(oldBean.getStyleNo()));
				logger.debug("PENS_ITEM:"+Utils.isNull(oldBean.getPensItem()));
				
				StringBuffer sql = new StringBuffer("");
				sql.append("  UPDATE PENSBME_STYLE_MAPPING SET  \n");
				sql.append("  MATERIAL_MASTER = '"+Utils.isNull(o.getGroupCode())+"' \n" );
				sql.append(", PENS_ITEM = '"+Utils.isNull(o.getPensItem())+"' \n" );
				sql.append(", STYLE = "+Utils.convertStrToDouble(o.getStyleNo())+" \n" );
				
				sql.append(" WHERE MATERIAL_MASTER ='"+Utils.isNull(oldBean.getGroupCode())+"'\n");
				sql.append(" AND PENS_ITEM = '"+ Utils.isNull(oldBean.getPensItem())+"' \n" );
				sql.append(" AND STYLE = '"+ Utils.isNull(oldBean.getStyleNo())+"' \n" );
                
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				c = ps.executeUpdate();
				logger.debug("Result Update:"+c);
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
}
