package com.isecinc.pens.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.Constants;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MasterBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

/**
 * @author WITTY
 *
 */
public class ImportDAO {

	protected static  Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public String getLastFileNameImport(Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String lastFileName ="";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select max(file_name)as last_file_name  from PENSBME_ONHAND_BME \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				lastFileName = rs.getString("last_file_name");
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
		return lastFileName;
	} 
	
	public String getLastFileNameImportFriday(Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String lastFileName ="";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select max(file_name)as last_file_name  from PENSBME_ONHAND_BME_FRIDAY \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				lastFileName = rs.getString("last_file_name");
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
		return lastFileName;
	} 
	
	public Boolean importLotusFileNameIsDuplicate(Connection conn ,String fileName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean dup = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_SALES_FROM_LOTUS WHERE  lower(file_name) ='"+Utils.isNull(fileName).toLowerCase()+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				dup = true;
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
		return dup;
	} 
	
	public Boolean importTopsFileNameIsDuplicate(Connection conn ,String fileName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean dup = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_SALES_FROM_TOPS WHERE  lower(file_name) ='"+Utils.isNull(fileName).toLowerCase()+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				dup = true;
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
		return dup;
	} 
	
	public boolean importPhyFileNameIsDuplicate(String fileName) throws Exception{
		Connection conn= null;
		boolean dup = false;
		try{
			conn = DBConnection.getInstance().getConnection();
			//validate filename duplicate
			dup = importPhyFileNameIsDuplicate(conn,fileName);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return dup;
	}
	
	public Boolean importPhyFileNameIsDuplicate(Connection conn ,String fileName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean dup = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_PHYSICAL_COUNT WHERE  lower(file_name) ='"+Utils.isNull(fileName).toLowerCase()+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				dup = true;
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
		return dup;
	} 
	
	public boolean importPhyFileNameIsDuplicate(String countDate,String pensCustCode) throws Exception{
		Connection conn= null;
		boolean dup = false;
		try{
			conn = DBConnection.getInstance().getConnection();
		    dup= importPhyFileNameIsDuplicate(conn,countDate,pensCustCode,null);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return dup;
	}
	public boolean importPhyFileNameIsDuplicate(Connection conn ,String countDate,String pensCustCode,String fileName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean dup = false;
		boolean psCountDate= true;
		int index=0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_PHYSICAL_COUNT WHERE 1=1 \n");
			if( !Utils.isNull(countDate).equals("")){
			  sql.append("and trunc(count_date) =? \n");
			}
			if( !Utils.isNull(pensCustCode).equals("")){
			  sql.append("and cust_code ='"+Utils.isNull(pensCustCode)+"' \n");
			}
			if( !Utils.isNull(fileName).equals("")){
			  sql.append("and lower(file_name) ='"+Utils.isNull(fileName).toLowerCase()+"' \n");
			}
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			if(psCountDate){
			   ps.setTimestamp(++index, new java.sql.Timestamp(Utils.parse(countDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			}
			rs = ps.executeQuery();
			if(rs.next()){
				dup = true;
				logger.debug("dup:"+dup);
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
		return dup;
	} 
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Master getMasterMapping(Connection conn ,String type ,String storeNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Master m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE Reference_code ='"+type+"' and interface_value ='"+storeNo+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new Master();
				m.setReferenceCode(rs.getString("reference_code"));
				m.setPensValue(rs.getString("pens_value"));
				m.setPensDesc(rs.getString("pens_desc"));
				m.setInterfaceDesc(rs.getString("interface_desc"));
				m.setInterfaceValue(rs.getString("interface_value"));
				m.setPensDesc2(rs.getString("pens_desc2"));
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
		return m;
	} 
	
	public Master getStoreTops(Connection conn,String storeName ) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Master m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE Reference_code ='Store' and interface_desc ='"+storeName+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new Master();
				m.setReferenceCode(rs.getString("reference_code"));
				m.setPensValue(rs.getString("pens_value"));
				m.setPensDesc(rs.getString("pens_desc"));
				m.setInterfaceDesc(rs.getString("interface_desc"));
				m.setInterfaceValue(rs.getString("interface_value"));
				m.setPensDesc2(rs.getString("pens_desc2"));
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
		return m;
	} 
	
	public Master getGroupTops(Connection conn,String item ) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Master m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select M.*, \n" +
			        " ( select max(s.pens_value) from pensbme_mst_reference s where s.reference_code ='Group' \n" +
			        "   and s.interface_value = M.pens_desc2 ) group_code \n"+
					" from PENSBME_MST_REFERENCE M WHERE M.Reference_code ='TOPSitem' and M.pens_desc ='"+item+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new Master();
				m.setReferenceCode(rs.getString("reference_code"));
				m.setPensDesc(rs.getString("pens_desc"));
				m.setPensValue(rs.getString("pens_value"));//pens_item
				m.setPensDesc2(rs.getString("pens_desc2"));//group_code_type ME1000
				m.setPensDesc3(rs.getString("group_code"));//group_code ,BRA,or UNDERWARE
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
		return m;
	} 
	
	public Master getMasterByBarcodeTypeBigC(Connection conn ,String value) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Master m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE Reference_code ='BigCitem' and pens_desc ='"+value+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new Master();
				m.setReferenceCode(rs.getString("reference_code"));
				m.setPensValue(rs.getString("pens_value"));
				m.setPensDesc(rs.getString("pens_desc"));
				m.setInterfaceDesc(rs.getString("interface_desc"));
				m.setInterfaceValue(rs.getString("interface_value"));
				m.setPensDesc2(rs.getString("pens_desc2"));
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
		return m;
	} 
	
	public Master getStoreName(String refCode ,String storeNo) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getStoreName(conn, refCode, storeNo);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		
	}
	
	public Master getStoreName(String refCode ,String storeNo,String storeType) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getStoreName(conn, refCode, storeNo,storeType);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		
	}
	
	public Master getStoreName(Connection conn ,String refCode ,String storeNo,String storeType) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Master m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE  pens_value ='"+storeNo+"' and reference_code ='"+refCode+"' \n");
			
			if( !Utils.isNull(storeType).equalsIgnoreCase("")){
				if(storeType.equalsIgnoreCase("lotus")){
					sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_LOTUS_CODE+"%' \n");
				}else if(storeType.equalsIgnoreCase("bigc")){
					sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_BIGC_CODE+"%' \n");
				}else if(storeType.equalsIgnoreCase("tops")){
					sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_TOPS_CODE+"%' \n");
				}else if(storeType.equalsIgnoreCase("MTT")){
					sql.append(" and ( pens_value LIKE '"+Constants.STORE_TYPE_MTT_CODE_1+"%' \n");
					sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_MTT_CODE_2+"%' ) \n");
				}
			}
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new Master();
				m.setPensValue(rs.getString("pens_value"));
				m.setPensDesc(rs.getString("pens_desc"));
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
		return m;
	} 
	
	public Master getStoreName(Connection conn ,String refCode ,String storeNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Master m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE  pens_value ='"+storeNo+"' and reference_code ='"+refCode+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new Master();
				m.setPensValue(rs.getString("pens_value"));
				m.setPensDesc(rs.getString("pens_desc"));
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
		return m;
	} 
	
	public Master getStoreTypeName(Connection conn ,String storeTypeCode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Master m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE  pens_value ='"+storeTypeCode+"' and reference_code ='Customer' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new Master();
				m.setInterfaceValue(rs.getString("interface_value"));//C1
				m.setInterfaceDesc(rs.getString("interface_desc"));//BigC,Lotus
				m.setPensValue(rs.getString("pens_value"));//storeTypeCode
				m.setPensDesc(rs.getString("pens_desc"));//storeTypeName
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
		return m;
	} 
	
	public Master getLogisticsName(Connection conn ,String storeTypeCode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Master m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE  pens_value ='"+storeTypeCode+"' and reference_code ='Logistic' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new Master();
				m.setInterfaceValue(rs.getString("interface_value"));//Logistic ID
				m.setInterfaceDesc(rs.getString("interface_desc"));//Logistic Name
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
		return m;
	} 
	
	public String getItemByBarcode(Connection conn ,String barcode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String item = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_value  from PENSBME_MST_REFERENCE WHERE interface_desc ='"+barcode+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = Utils.isNull(rs.getString("pens_value"));
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
		return item;
	} 
	
	public String getItemByBarcode(Connection conn ,String storeType,String barcode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String item = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_value  from PENSBME_MST_REFERENCE WHERE reference_code ='"+storeType+"' and interface_desc ='"+barcode+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = Utils.isNull(rs.getString("pens_value"));
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
		return item;
	} 
	/** code 10 position ME1204C4BL **/
	public String getItemByInterfaceValueTypeLotusCase1(Connection conn ,String storeType,String code) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String item = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_desc3  from PENSBME_MST_REFERENCE WHERE reference_code ='"+storeType+"' and interface_value ='"+code+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = Utils.isNull(rs.getString("pens_desc3"));
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
		return item;
	} 
	
	public OnhandSummary getItemByMaterialMaster(Connection conn ,String matrialMaster) throws Exception{
		PreparedStatement ps =null;
		ResultSet rst = null;
		OnhandSummary item  = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_ONHAND_BME WHERE material_master ='"+matrialMaster+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()){
				item = new OnhandSummary();
				item.setAsOfDate(Utils.stringValue(rst.getDate("as_of_date"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				item.setMaterialMaster(rst.getString("Material_master"));
				item.setBarcode(rst.getString("BARCODE"));
				item.setOnhandQty(rst.getString("Onhand_QTY"));
				
				item.setWholePriceBF(rst.getString("Whole_Price_BF"));
				item.setRetailPriceBF(rst.getString("Retail_Price_BF"));
				
				item.setItem(rst.getString("item"));
				item.setItemDesc(rst.getString("item_desc"));
				item.setFileName(rst.getString("file_name"));
				item.setGroup(rst.getString("group_item"));
				item.setStatus(rst.getString("STATUS"));
				item.setMessage(rst.getString("MESSAGE"));
				
				item.setPensItem(rst.getString("PENS_ITEM"));
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rst != null){
				rst.close();rst = null;
			}
			
		}
		return item;
	} 
	
	/** code 6 position ME1204 **/
	public String getItemByInterfaceValueTypeLotusCase2(Connection conn ,String storeType,String code) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String item = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_value  from PENSBME_MST_REFERENCE WHERE reference_code ='"+storeType+"' and pens_desc2 ='"+code+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = Utils.isNull(rs.getString("pens_value"));
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
		return item;
	} 
	
	public String getItemByBarcodeTypeBigC(Connection conn ,String storeType,String barcode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String item = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_value  from PENSBME_MST_REFERENCE WHERE reference_code ='"+storeType+"' and pens_desc ='"+barcode+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = Utils.isNull(rs.getString("pens_value"));
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
		return item;
	} 
	
	public MasterBean getMasterBeanByBarcode(Connection conn ,String barcode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		MasterBean item = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select interface_desc,pens_value,pens_desc,pens_desc2  from PENSBME_MST_REFERENCE WHERE interface_desc ='"+barcode+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = new MasterBean();
				item.setItem(Utils.isNull(rs.getString("pens_value")));
				item.setBarcode(Utils.isNull(rs.getString("interface_desc")));
				item.setGroup(Utils.isNull(rs.getString("pens_desc2")));
				
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
		return item;
	} 
	
	public MasterBean getMasterBeanByBarcode(Connection conn ,String storeType,String barcode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		MasterBean item = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select interface_desc,pens_value,pens_desc,pens_desc2 " +
					" from PENSBME_MST_REFERENCE WHERE reference_code ='"+storeType+"' and interface_desc ='"+barcode+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				item = new MasterBean();
				item.setItem(Utils.isNull(rs.getString("pens_value")));
				item.setBarcode(Utils.isNull(rs.getString("interface_desc")));
				item.setGroup(Utils.isNull(rs.getString("pens_desc2")));
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
		return item;
	} 

	public BigDecimal getWholePriceBFFromOracle(Connection conn ,String itemCode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		BigDecimal wholePriceBFOracle = new BigDecimal("0");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select list_price_per_unit from xxpens_bi_mst_price_list where item_code ='"+itemCode+"' and header_name='MT-Price List-Standard' and uom_code ='EA' \n");
			sql.append(" and ( ( line_end_date is null) \n" );
			sql.append("       or \n");
			sql.append("       ( line_end_date is not null and line_end_date <= sysdate)  ) \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				wholePriceBFOracle = rs.getBigDecimal("list_price_per_unit");
				logger.debug("wholePriceBFOracle:"+wholePriceBFOracle);
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
		return wholePriceBFOracle;
	} 
	
	public BigDecimal getRetailPriceBFFromOracle(Connection conn ,String itemCode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		BigDecimal wholePriceBFOracle = new BigDecimal("0");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select price from xxpens_bi_mst_price_list where item_code ='"+itemCode+"' and header_name='MT-Price List-Standard' and uom_code ='EA' \n");
			sql.append(" and ( ( line_end_date is null) \n" );
			sql.append("       or \n");
			sql.append("       ( line_end_date is not null and line_end_date <= sysdate)  ) \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				wholePriceBFOracle = rs.getBigDecimal("price");
				logger.debug("wholePriceBFOracle:"+wholePriceBFOracle);
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
		return wholePriceBFOracle;
	} 
	
	public List<References> getReferenceCodeList() throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<References> storeList = new ArrayList<References>();
		Connection conn = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select distinct reference_code  from PENSBME_MST_REFERENCE \n");
			
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs.getString("reference_code"),rs.getString("reference_code"), rs.getString("reference_code"));
				storeList.add(r);
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
			if(conn != null){
			   conn.close();conn=null;
			}
			
		}
		return storeList;
	} 

	
	public List<References> getStoreList() throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<References> storeList = new ArrayList<References>();
		Connection conn = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_value ,pens_desc  from PENSBME_MST_REFERENCE WHERE Reference_code ='Store' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs.getString("pens_value"), rs.getString("pens_desc"));
				storeList.add(r);
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
			if(conn != null){
			   conn.close();conn=null;
			}
			
		}
		return storeList;
	} 
	public List<References> getStoreListByStoreType(String storeType) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<References> storeList = new ArrayList<References>();
		Connection conn = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_value ,pens_desc  from PENSBME_MST_REFERENCE WHERE Reference_code ='Store' and pens_value like '"+storeType+"%' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs.getString("pens_value"), rs.getString("pens_desc"));
				storeList.add(r);
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
			if(conn != null){
			   conn.close();conn=null;
			}
			
		}
		return storeList;
	} 
	
	public List<References> getStoreTypeList() throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getStoreTypeList(conn);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
	}
	public List<References> getStoreTypeList(Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<References> storeList = new ArrayList<References>();
	
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_value ,pens_desc  from PENSBME_MST_REFERENCE WHERE Reference_code ='Customer' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs.getString("pens_value"), rs.getString("pens_desc"));
				storeList.add(r);
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
	
	public List<References> getBillTypeList() throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getBillTypeList(conn);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
	}
	public List<References> getBillTypeList(Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<References> storeList = new ArrayList<References>();
	
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select interface_value,pens_value  from PENSBME_MST_REFERENCE \n" +
					" WHERE Reference_code ='Cust_type' \n" +
					" and status ='A' \n" +
					" order by interface_value desc \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs.getString("interface_value"), rs.getString("pens_value"));
				storeList.add(r);
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
	
	public List<References> getRegionList() throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getRegionList(conn);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
	}
	
	public List<References> getRegionList(Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<References> storeList = new ArrayList<References>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select pens_value ,pens_desc  from PENSBME_MST_REFERENCE WHERE Reference_code ='Region' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			References r = null;
			
			r = new References("", "ทุกภาค");
			storeList.add(r);
			while(rs.next()){
				r = new References(rs.getString("pens_value"), rs.getString("pens_desc"));
				storeList.add(r);
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

}
