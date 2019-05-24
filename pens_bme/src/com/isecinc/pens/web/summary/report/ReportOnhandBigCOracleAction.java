package com.isecinc.pens.web.summary.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.sql.ReportOnhandAsOfKingSQL;
import com.isecinc.pens.sql.ReportOnhandMTTDetailSQL;
import com.isecinc.pens.sql.ReportOnhandSizeColorKingSQL;
import com.isecinc.pens.sql.ReportSizeColorLotus_SQL;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class ReportOnhandBigCOracleAction {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static SummaryForm process(HttpServletRequest request,User user,SummaryForm summaryForm) throws Exception{
	Connection conn = null;
	OnhandSummary c = summaryForm.getOnhandSummary();
	boolean pass = true;
	Statement stmt = null;
	ResultSet rst = null;
	List<OnhandSummary> rowAllList = new ArrayList<OnhandSummary>();
	OnhandSummary item = null;
	try{
		//set Page Name
		summaryForm.setPage("onhandBigCOracle");
		//Init Connection
		conn = DBConnection.getInstance().getConnection();

		if(pass){
			List<OnhandSummary> results = null;
			OnhandSummary summary = searchOnhandBigCOracle(conn,summaryForm.getOnhandSummary(),user);
			results = summary != null?summary.getItemsList():null;
			
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("summary" ,summary.getSummary());
				summaryForm.setResults(results);
				
				ImportDAO importDAO = new ImportDAO();
				Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
				if(m != null)
				   summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
											
			} else {
				summaryForm.setResults(null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
		}

	}catch(Exception e){
		logger.error(e.getMessage(),e);
	}finally{
		if(stmt != null){
			stmt.close();stmt=null;
		}
		if(rst != null){
			rst.close();rst=null;
		}
		if(conn != null){
			conn.close();conn=null;
		}
	}
	return summaryForm;
 }
	public static StringBuilder genSQL(OnhandSummary c,User user){
		StringBuilder sql = new StringBuilder();
		sql.append("  select MST.store_code \n");
		sql.append(" ,(select M.pens_desc from pensbi.pensbme_mst_reference M \n");
		sql.append("   where M.pens_value =  MST.store_code \n");
		sql.append("   and M.reference_code ='Store' \n");
		sql.append("  )as store_name \n");
		sql.append(" ,V.subinventory_code \n");
	    sql.append(" ,MI.pens_item ,MI.pens_item_desc ,V.primary_quantity \n");
		sql.append(" from apps.xxpens_inv_onhand_g08_v V \n");
		sql.append(" ,( \n");
		sql.append("    SELECT pens_value as store_code \n" );
		sql.append("    ,interface_desc as subinventory_code \n" );
		sql.append("    from PENSBI.PENSBME_MST_REFERENCE ");
		sql.append("    where reference_code = 'SubInv' \n");
		sql.append("    and pens_value in( \n");
		sql.append("      SELECT pens_value from PENSBI.PENSBME_MST_REFERENCE \n");
		sql.append("      where reference_code = 'Store' \n");
		sql.append("      and pens_desc4 ='N' \n");
		sql.append("    ) \n");
		sql.append(" ) MST \n");
		sql.append(" ,( \n");
		sql.append("    SELECT inventory_item_id \n" );
		sql.append("    ,segment1 as pens_item \n" );
		sql.append("    ,description as pens_item_desc \n" );
		sql.append("    ,(select max(pens_desc2) as group_code \n" );
		sql.append("      from PENSBI.PENSBME_MST_REFERENCE ms \n");
		sql.append("      where reference_code = 'LotusItem' \n");
		sql.append("      and ms.pens_value = vs.segment1 \n");
		sql.append("     ) as group_code \n");
		sql.append("    from apps.xxpens_om_item_mst_v vs \n");
		sql.append(" ) MI \n");
		sql.append(" where MST.subinventory_code = V.subinventory_code \n");
		sql.append(" and MI.inventory_item_id = V.inventory_item_id \n");
		//bigc only
		 sql.append(" and MST.store_code like '"+PickConstants.STORE_TYPE_BIGC_CODE+"%' \n");
		 
		if(!Utils.isNull(c.getPensCustCodeFrom()).equals("") ){
		  sql.append(" and MST.store_code ='"+c.getPensCustCodeFrom()+"'\n");
		} 
		if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
			sql.append(" and MI.pens_item >='"+c.getPensItemFrom()+"'\n");
			sql.append(" and MI.pens_item <='"+c.getPensItemTo()+"'\n");
		}else if(!Utils.isNull(c.getPensItemFrom()).equals("") ){
			sql.append(" and MI.pens_item ='"+c.getPensItemFrom()+"'\n");
		}
		if(!Utils.isNull(c.getGroup()).equals("") ){
			sql.append(" and MI.group_code LIKE '"+c.getGroup()+"%'\n");
		}
		sql.append(" order by MST.store_code,V.subinventory_code,MI.pens_item \n");
		return sql;
	}
	
	 public static OnhandSummary searchOnhandBigCOracle(Connection conn,OnhandSummary c,User user) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			int totalOnhandQty = 0;
			try {
				sql = genSQL(c, user);
				logger.debug(sql.toString());
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					item.setStoreCode(Utils.isNull(rst.getString("store_code")));
					item.setStoreName(Utils.isNull(rst.getString("store_name")));
					item.setSubInv(Utils.isNull(rst.getString("subinventory_code")));
					item.setPensItem(rst.getString("pens_item"));
					item.setItemDesc(rst.getString("pens_item_desc"));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("primary_quantity"),Utils.format_current_no_disgit));
					totalOnhandQty += rst.getDouble("primary_quantity");
					pos.add(item);
					
				}//while
				
				//set summary wait
				c.setItemsList(pos);
				OnhandSummary summary = new OnhandSummary();
				summary.setOnhandQty(Utils.decimalFormat(totalOnhandQty,Utils.format_current_no_disgit));
				c.setSummary(summary);
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return c;
	    }
	 
	 public static StringBuffer genExcel(HttpServletRequest request, SummaryForm summaryForm){
		 StringBuffer h = new StringBuffer("");
		 h.append(ExcelHeader.EXCEL_HEADER);//excel style
		 OnhandSummary c = summaryForm.getOnhandSummary();
		    //Gen Header report
		    h.append("<table id='tblProductHead' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			h.append("<tr> \n");
			h.append("  <td colspan='6' align='center'><font size='3'><b>Stock onhand BigC(On ORACLE)</b> </font></td> \n");
			h.append("</tr> \n");
			/*h.append("<tr> \n");
			h.append("  <td colspan='6'><b>รหัสร้านค้า  :  "+c.getPensCustCodeFrom()+"-"+c.getPensCustNameFrom()+"</b></td> \n");
			h.append("</tr> \n");*/
			h.append("</table> \n");
			
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			h.append("<tr> \n");
			h.append("<th>รหัสร้านค้า</th> \n");
			h.append("<th>ชื่อร้านค้า</th> \n");
			h.append("<th>Sub Inv</th> \n");
			h.append("<th>รหัสสินค้า</th> \n");
			h.append("<th>ชื่อสินค้า</th> \n");
			h.append("<th>Onhand Qty</th> \n");
			h.append("</tr> \n");
			for(int i=0;i<summaryForm.getResults().size();i++){
				OnhandSummary o =summaryForm.getResults().get(i);
				h.append("<tr> \n");
				h.append("<td class='text' width='10%'>"+o.getStoreCode()+"</td> \n");
				h.append("<td class='text' width='10%'>"+o.getStoreName()+"</td> \n");
				h.append("<td class='text' width='10%'>"+o.getSubInv()+"</td> \n");
				h.append("<td class='text' width='10%'>"+o.getPensItem()+"</td> \n");
				h.append("<td class='text' width='10%'>"+o.getItemDesc()+"</td> \n");
				h.append("<td class='num' width='10%'>"+o.getOnhandQty()+"</td> \n");
				h.append("</tr> \n");
			}
			OnhandSummary summary = (OnhandSummary) request.getSession().getAttribute("summary");
			
			h.append("<tr> \n");
			h.append("<td class='text' align='right' colspan='5'><b>รวม</b></td> \n");
			h.append("<td class='num_currency_bold' >"+summary.getOnhandQty()+"</td> \n");
			h.append("</tr> \n");
			
		 return h;
	 }
}
