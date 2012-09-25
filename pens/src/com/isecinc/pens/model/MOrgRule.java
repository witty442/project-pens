package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.OrgRuleBean;
import com.isecinc.pens.web.sales.bean.ProductCatalog;

/**
 * MProduct Class
 * 
 * @author Aneak.t
 * @version $Id: MProduct.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MOrgRule {

	private static final long serialVersionUID = 3881159581550423821L;
	
	public static String TABLE_NAME_W1 = "m_org_rule";
	public static String TABLE_NAME_W2 = "m_org_rule_item";
	
	public OrgRuleBean getOrgRule(String org) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		String sql = "select * from "+TABLE_NAME_W1+" where org = '"+org+"'" ;		 
		conn = new DBCPConnectionProvider().getConnection(conn);
		OrgRuleBean w1Bean = new OrgRuleBean();
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				w1Bean.setOrg(rst.getString("org"));
				w1Bean.setSubInv(rst.getString("sub_inv"));
				w1Bean.setName(rst.getString("name"));
				w1Bean.setDefaultValue(rst.getString("default_value")); //Y or N
				w1Bean.setCheckItem(rst.getString("check_item"));//Y or N    
		   }
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return w1Bean;
	}
	
	public List<References> getW1RefList(String org,String subInv) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		List<References> w1List = new ArrayList<References>();
		StringBuffer sql = new StringBuffer("select * from "+TABLE_NAME_W1+" where 1=1 ");
		if( !"".equals(ConvertNullUtil.convertToString(org))){
		    sql.append(" and org = '"+org+"'") ;	
		}
		if( !"".equals(ConvertNullUtil.convertToString(subInv))){
		    sql.append(" and sub_inv = '"+subInv+"'") ;	
		}
		sql.append(" order by default_value desc");
		
		System.out.println("sql:"+sql);
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				References ref = new References(rst.getString("org")+"|"+rst.getString("sub_inv"), rst.getString("name"));
				w1List.add(ref);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return w1List;
	}
	
	public Map<String,String> getOrgRuleItemMap(String org,String subInv) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		Map<String, String> w2Map = new HashMap<String, String>();
		
		StringBuffer sql = new StringBuffer("select item from "+TABLE_NAME_W2+" where 1=1 ");
		if( !"".equals(ConvertNullUtil.convertToString(org))){
		    sql.append(" and org = '"+org+"'") ;	
		}
		if( !"".equals(ConvertNullUtil.convertToString(subInv))){
		    sql.append(" and sub_inv = '"+subInv+"'") ;	
		}
		System.out.println("sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				w2Map.put(rst.getString("item"), rst.getString("item"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return w2Map;
	}
	
	public List<OrgRuleBean> getW1List(String org,String subInv) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		List<OrgRuleBean> w1List = new ArrayList<OrgRuleBean>();
		
		StringBuffer sql = new StringBuffer("select * from "+TABLE_NAME_W1+" where 1=1 ");
		if( !"".equals(ConvertNullUtil.convertToString(org))){
		    sql.append(" and org = '"+org+"'") ;	
		}
		if( !"".equals(ConvertNullUtil.convertToString(subInv))){
		    sql.append(" and sub_inv = '"+subInv+"'") ;	
		}
		sql.append(" order by default_value desc");
		
		System.out.println("sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				OrgRuleBean w1Bean = new OrgRuleBean();
				w1Bean.setOrg(rst.getString("org"));
				w1Bean.setSubInv(rst.getString("sub_inv"));
				w1Bean.setName(rst.getString("name"));
				w1Bean.setDefaultValue(rst.getString("default_value")); //Y or N
				w1Bean.setCheckItem(rst.getString("check_item"));//Y or N
				
				w1List.add(w1Bean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return w1List;
	}
}
