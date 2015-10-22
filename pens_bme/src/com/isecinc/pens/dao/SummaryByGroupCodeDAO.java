package com.isecinc.pens.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.Constants;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.exception.LogisticException;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.web.order.OrderAction;

public class SummaryByGroupCodeDAO {
	
	protected static Logger logger = Logger.getLogger("PENS");
	
	public int getTotalRowItem(Connection conn,Order o) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
        int totalRow = 0;
		StringBuilder sql = new StringBuilder();
		try {
			
			sql.append("\n SELECT count(*) as total_row FROM( ");
			sql.append("\n SELECT distinct ");
			sql.append("\n   m.pens_desc2 as group_code ");
			sql.append("\n  ,substr(m.interface_value,7,2) as sizes ,  substr(m.interface_value,9,2) as color ");
			sql.append("\n from PENSBME_MST_REFERENCE m  WHERE 1=1 \n");
			sql.append("\n AND m.reference_code ='LotusItem' ");
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and m.pens_desc2 = '"+Utils.isNull(o.getGroupCode())+"'  ");
			}
			sql.append("\n ) ");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
		
			if (rst.next()) {
				totalRow = rst.getInt("total_row");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				
			} catch (Exception e) {}
		}
		return totalRow;
	}
	
	
	public List<Order> prepareNewOrder(Connection conn,Order o,List<StoreBean> storeList,User user,int pageNumber,int pageSize) throws Exception {
		logger.debug("prepareNewOrder");
		PreparedStatement ps = null;
		ResultSet rst = null;
		List<Order> pos = new ArrayList<Order>();
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n SELECT * FROM( \n");
			sql.append("\n SELECT a.*, rownum r__ \n");
			sql.append("\n FROM ( \n");
			sql.append("\n  SELECT s.* FROM(  \n");
				sql.append("\n SELECT distinct ");
				sql.append("\n m.pens_desc2 as group_code ");
				sql.append("\n ,substr(m.interface_value,7,2) as sizes ,  substr(m.interface_value,9,2) as color ");
				sql.append("\n from PENSBME_MST_REFERENCE m   \n");
				sql.append("\n where 1=1  ");
				sql.append("\n AND m.reference_code ='LotusItem' ");
				if( !Utils.isNull(o.getGroupCode()).equals("")){
					sql.append("\n and m.pens_desc2 = '"+Utils.isNull(o.getGroupCode())+"'  ");
				}
				sql.append("\n  ) s  ");
				sql.append("\n order by s.group_code,s.sizes,s.color asc  ");
				sql.append("\n ) a  ");
			sql.append("\n  WHERE rownum < (("+pageNumber+" * "+pageSize+") + 1 )  ");
			sql.append("\n )  ");
			sql.append("\n WHERE r__ >= ((("+pageNumber+"-1) * "+pageSize+") + 1)  ");
					
			logger.debug("sql:"+sql);
			
			//Get StoreBeanMap 
			Map<String, StoreBean> storeBeanOrderMap = getStoreBeanSalesOutMap(conn,o);
			
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();
			int n=0;
			List<StoreBean> storeItemList = null;
			while (rst.next()) {
				Order item = new Order();
				
				item.setGroupCode(rst.getString("group_code"));
				item.setSize(rst.getString("sizes"));
				item.setColor(rst.getString("color"));
				
				//** add Store 
				//** OrderNo ,qty by StoreCode
				if(storeList != null && storeList.size() >0){
					storeItemList = new ArrayList<StoreBean>();
					for(int c =0;c<storeList.size();c++){
						StoreBean s = (StoreBean)storeList.get(c);
						String keyMap = s.getStoreCode()+"_"+item.getGroupCode()+"_"+item.getSize()+"_"+item.getColor();
						//logger.debug("KeyMap["+keyMap+"]");
						
						StoreBean order = storeBeanOrderMap.get(keyMap)!=null?(StoreBean)storeBeanOrderMap.get(keyMap):null;
						if(order!= null){
							order.setStoreDisp(s.getStoreDisp());
							order.setStoreName(s.getStoreName());
							//System.out.println("StoreCode["+order.getStoreCode()+"]qty["+order.getQty()+"]");
                           
						}else{
							order = s;
							order.setQty("");
						}
						storeItemList.add(order);

					}//for
				}//if
				
				//logger.debug("storeItemList Size:"+storeItemList.size());
				item.setStoreItemList(storeItemList);
				pos.add(item);
				n++;
				//if(n==3){break;}
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(rst !=null){
				  rst.close();rst=null;
				}
				if(ps != null){
				  ps.close();ps=null;
				}
			} catch (Exception e) {}
		}
		return pos;
	}
	
	
	private Map<String,StoreBean> getStoreBeanSalesOutMap(Connection conn,Order o) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String,StoreBean> map = new HashMap<String,StoreBean>();
		
		try{
			StringBuffer sql = new StringBuffer("");
			
			sql.append("\n SELECT h.cust_no,h.group_code");
			sql.append("\n ,substr(h.material_master,7,2) as sizes ,  substr(h.material_master,9,2) as color ");
			sql.append("\n ,(SELECT a.pens_desc FROM PENSBME_MST_REFERENCE a WHERE a.pens_value= h.cust_no and  a.reference_code ='Store') as cust_desc ");
			sql.append("\n ,count(*) as QTY ");
			sql.append("\n from PENSBME_SALES_OUT h ");
			sql.append("\n where 1=1  ");

			if( !Utils.isNull(o.getSalesDateFrom()).equals("")){
				 Date dateFrom = Utils.parse(o.getSalesDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				 String dateFromStrCH = Utils.stringValue(dateFrom, Utils.DD_MM_YYYY_WITH_SLASH);
				 sql.append("\n AND h.sale_date >= to_date('"+dateFromStrCH+"','dd/MM/yyyy')");
			}
				
			if( !Utils.isNull(o.getSalesDateTo()).equals("")){
			    Date dateTo = Utils.parse(o.getSalesDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			    String dateFromStrCH = Utils.stringValue(dateTo, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n AND h.sale_date <= to_date('"+dateFromStrCH+"','dd/MM/yyyy')");
			}
			
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and h.group_code = '"+Utils.isNull(o.getGroupCode())+"'  ");
			}
			
			if(Constants.STORE_TYPE_BIGC_CODE.equals(o.getStoreType())){
				sql.append("\n and h.cust_no LIKE '"+Constants.STORE_TYPE_BIGC_CODE+"%'  ");
			}else if(Constants.STORE_TYPE_LOTUS_CODE.equals(o.getStoreType())){
				sql.append("\n and h.cust_no LIKE '"+Constants.STORE_TYPE_LOTUS_CODE+"%'  ");
			}else if(Constants.STORE_TYPE_MTT_CODE_1.equals(o.getStoreType()) 
				|| Constants.STORE_TYPE_KING_POWER.equals(o.getStoreType())){
				sql.append("\n and ( h.cust_no LIKE '"+Constants.STORE_TYPE_MTT_CODE_1+"%' OR  h.cust_no LIKE '"+Constants.STORE_TYPE_KING_POWER+"%')");
			}
			
			sql.append("\n  GROUP BY h.cust_no,h.group_code,h.material_master ");
		    
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			
		
			rs = ps.executeQuery();
			String keyMap = "";
			while(rs.next()){
				StoreBean m = new StoreBean();
				m.setStoreCode(Utils.isNull(rs.getString("cust_no")));
				m.setStoreName(Utils.isNull(rs.getString("cust_desc")));
				m.setGroupCode(Utils.isNull(rs.getString("group_code")));
				m.setSize(Utils.isNull(rs.getString("sizes")));
				m.setColor(Utils.isNull(rs.getString("color")));
				m.setQty(Utils.isNull(rs.getString("qty")));
				
				keyMap = m.getStoreCode()+"_"+m.getGroupCode()+"_"+m.getSize()+"_"+m.getColor();
				map.put(keyMap, m);
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
		return map;
	} 
	
	
	
	public List<StoreBean> getStoreList(Connection conn,String storeType) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<StoreBean> storeList = new ArrayList<StoreBean>();
		ImportDAO importDAO = new ImportDAO();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("select a.* from( \n");
			sql.append(" select m.* \n");
			sql.append(",(case when (m.sequence is null or m.sequence ='') then 0 else to_number(m.sequence) end ) as seq \n");
		    sql.append("  from PENSBME_MST_REFERENCE m WHERE 1=1 and m.reference_code ='Store' \n");
			sql.append("  and m.status ='Active'  \n");
			sql.append("  and substr(m.pens_value, 0, 6) ='"+storeType+"' \n");
			sql.append(" )a \n ");;
			sql.append("order by m.pens_value \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				StoreBean m = new StoreBean();
				m.setStoreCode(Utils.isNull(rs.getString("pens_value")));
				m.setStoreName(Utils.isNull(rs.getString("pens_desc")));
				m.setBillType(Utils.isNull(rs.getString("pens_desc4")));
				m.setValidFrom(Utils.isNull(rs.getString("pens_desc5")));
				m.setValidTo(Utils.isNull(rs.getString("pens_desc6")));
				
				if( !Utils.isNull(m.getStoreName()).equals("")){
					//m.setStoreDisp(m.getStoreName().substring(0,8)+" "+m.getStoreName().substring(9,m.getStoreName().length()));
					//String disp = Utils.isNull(m.getStoreName().substring(m.getStoreName().indexOf("-")+1,m.getStoreName().length()));

					m.setStoreDisp(m.getStoreCode()+" "+m.getStoreName());
					//m.setStoreDispShort(disp);
					
				}
				m.setQty("");
				storeList.add(m);
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
		return storeList;
	} 
	
	public List<StoreBean> getStoreListSaleOut(Connection conn,Order o) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<StoreBean> storeList = new ArrayList<StoreBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select distinct m.cust_no  \n");
			sql.append(" ,(SELECT a.pens_desc FROM PENSBME_MST_REFERENCE a WHERE a.pens_value=m.cust_no and  a.reference_code ='Store') as cust_desc  \n");
		    sql.append(" from PENSBME_SALES_OUT m WHERE 1=1  \n");
		    
		    if( !Utils.isNull(o.getSalesDateFrom()).equals("")){
				 Date dateFrom = Utils.parse(o.getSalesDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				 String dateFromStrCH = Utils.stringValue(dateFrom, Utils.DD_MM_YYYY_WITH_SLASH);
				 sql.append("\n AND m.sale_date >= to_date('"+dateFromStrCH+"','dd/MM/yyyy')");
			}
				
			if( !Utils.isNull(o.getSalesDateTo()).equals("")){
			    Date dateTo = Utils.parse(o.getSalesDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			    String dateFromStrCH = Utils.stringValue(dateTo, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n AND m.sale_date <= to_date('"+dateFromStrCH+"','dd/MM/yyyy')");
			}
			
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and m.group_code = '"+Utils.isNull(o.getGroupCode())+"'  ");
			}
			
			if(Constants.STORE_TYPE_BIGC_CODE.equals(o.getStoreType())){
				sql.append("\n and m.cust_no LIKE '"+Constants.STORE_TYPE_BIGC_CODE+"%'  ");
			}else if(Constants.STORE_TYPE_LOTUS_CODE.equals(o.getStoreType())){
				sql.append("\n and m.cust_no LIKE '"+Constants.STORE_TYPE_LOTUS_CODE+"%'  ");
			}else if(Constants.STORE_TYPE_MTT_CODE_1.equals(o.getStoreType()) 
				|| Constants.STORE_TYPE_KING_POWER.equals(o.getStoreType())){
				sql.append("\n and ( m.cust_no LIKE '"+Constants.STORE_TYPE_MTT_CODE_1+"%' OR  m.cust_no LIKE '"+Constants.STORE_TYPE_KING_POWER+"%')");
			}
			sql.append("order by m.cust_no \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				StoreBean m = new StoreBean();
				m.setStoreCode(Utils.isNull(rs.getString("cust_no")));
				m.setStoreName(Utils.isNull(rs.getString("cust_desc")));
				
				if( !Utils.isNull(m.getStoreName()).equals("")){
					//m.setStoreDisp(m.getStoreName().substring(0,8)+" "+m.getStoreName().substring(9,m.getStoreName().length()));
					//String disp = Utils.isNull(m.getStoreName().substring(m.getStoreName().indexOf("-")+1,m.getStoreName().length()));

					m.setStoreDisp(m.getStoreCode()+" "+m.getStoreName());
					//m.setStoreDispShort(disp);
					
				}
				m.setQty("");
				storeList.add(m);
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
		return storeList;
	} 
	
	public static void initStoreTypeMap() throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			StringBuffer sql = new StringBuffer("");
			conn = DBConnection.getInstance().getConnection();
			sql.append(" SELECT * FROM PENSBI.PENSBME_MST_REFERENCE WHERE reference_code ='Customer'  \n");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				OrderAction.STORE_TYPE_MAP.put(rs.getString("pens_value"), rs.getString("pens_desc"));
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
	
	} 
	
	private static String appendCustType(String custType){
		if("G".equals(Utils.isNull(custType))){
			return " (G)";
		}
		return "";
	}

}
