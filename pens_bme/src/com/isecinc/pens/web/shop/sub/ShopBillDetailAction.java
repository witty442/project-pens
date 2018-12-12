package com.isecinc.pens.web.shop.sub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.shop.ShopBean;
import com.isecinc.pens.web.shop.ShopForm;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class ShopBillDetailAction {
 private static Logger logger = Logger.getLogger("PENS");
 public static int pageSize = 50;
 
 public static ShopForm search(ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchShopBillDetail");
		ShopForm aForm = (ShopForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		boolean getTotalRec = false;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				getTotalRec = true;
				allRec = true;
				List<ShopBean> itemsTotalRec = searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize,getTotalRec);
				aForm.setTotalRecord(itemsTotalRec!=null?itemsTotalRec.get(0).getTotalRec():0);
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    getTotalRec = false;
			    allRec = false;
				List<ShopBean> items = searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize,getTotalRec);
				aForm.setResults(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				   aForm.setResults(null);
				}
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    getTotalRec = false;
			    allRec = false;
				List<ShopBean> items = searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize,getTotalRec);
				aForm.setResults(items);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return aForm;
	}
 
 public static List<ShopBean> searchHeadList(Connection conn,ShopBean o,boolean allRec ,int currPage,int pageSize,boolean getTotalRec) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ShopBean h = null;
		List<ShopBean> items = new ArrayList<ShopBean>();
		int no=1;
		Date dateTemp = null;
		String dateStr ="";
		try {
			if(getTotalRec){
			  sql.append("\n SELECT count(*) as c FROM(");	
			}
			sql.append("\n SELECT M.* FROM (");
			sql.append("\n SELECT A.* ,rownum as r__ FROM (");
            ////////////////////////////
			sql.append("\n SELECT AA.*");
			sql.append("\n ,(AA.retail_sell_amt-AA.discount_amt) as sell_af_disc");
			sql.append("\n ,(AA.retail_sell_amt-AA.discount_amt-AA.whole_sell_amt) as wacoal_amt");
			sql.append("\n ,((AA.retail_sell_amt-AA.discount_amt) *6/100) as pens_amt");
			sql.append("\n FROM(");
			sql.append("\n   SELECT ");
			sql.append("\n   P.promo_name,P.sub_promo_name,P.start_date,P.end_date");
			sql.append("\n   ,P.start_promo_qty,P.end_promo_qty,P.discount_percent");
			sql.append("\n   ,A.MATERIAL_MASTER,A.pens_item");
			sql.append("\n   ,(A.price*A.qty) as retail_sell_amt");
			sql.append("\n   ,(");
			sql.append("\n     SELECT M.LIST_PRICE_PER_UNIT *A.qty");
			sql.append("\n     FROM APPS.XXPENS_OM_ITEM_MST_V M");
			sql.append("\n     where A.inventory_item_id = M.inventory_item_id");
			sql.append("\n   ) as whole_sell_amt");
			sql.append("\n   ,A.qty");
			sql.append("\n   ,( ((P.discount_percent/100)*a.price) * A.qty) discount_amt");
			sql.append("\n   FROM(");
			sql.append("\n     SELECT ");
			sql.append("\n     H.order_date,MP.MATERIAL_MASTER");
			sql.append("\n     ,MP.pens_item,MP.inventory_item_id");
			sql.append("\n     ,D.price,nvl(D.discount_percent ,0) as discount_percent");
			sql.append("\n     ,sum(ordered_quantity) as qty");
			sql.append("\n     FROM APPS.XXPENS_OM_SHOP_ORDER_MST H");
			sql.append("\n     ,APPS.XXPENS_OM_SHOP_ORDER_DT D");
			sql.append("\n     ,( ");
			sql.append("\n       SELECT I.inventory_item_id ,MP.pens_desc2 as group_code");
			sql.append("\n      ,MP.PENS_VALUE as PENS_ITEM,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
			sql.append("\n      ,MP.INTERFACE_DESC as BARCODE ");
			sql.append("\n      FROM PENSBI.PENSBME_MST_REFERENCE MP ,APPS.XXPENS_OM_ITEM_MST_V I");
			sql.append("\n      where MP.reference_code ='7CItem' ");
			sql.append("\n      AND MP.pens_value =I.segment1");
			sql.append("\n     ) MP");
			sql.append("\n     where H.order_number = D.order_number");
			sql.append("\n     AND D.product_id = MP.inventory_item_id ");
			
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n    AND MP.MATERIAL_MASTER LIKE '"+Utils.isNull(o.getGroupCode())+"%'");
			}
			if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("") ){
				dateTemp = Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateStr = Utils.stringValue(dateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n    AND H.ORDER_DATE >= to_date('"+dateStr+"','dd/mm/yyyy')");
				
				dateTemp = Utils.parse(o.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateStr = Utils.stringValue(dateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n    AND H.ORDER_DATE <= to_date('"+dateStr+"','dd/mm/yyyy')");
				
			}else if( !Utils.isNull(o.getStartDate()).equals("") && Utils.isNull(o.getEndDate()).equals("") ){
				dateTemp = Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateStr = Utils.stringValue(dateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n    AND H.ORDER_DATE = to_date('"+dateStr+"','dd/mm/yyyy')");
			}
			sql.append("\n        group by H.order_date,MP.MATERIAL_MASTER,MP.pens_item,MP.inventory_item_id ,D.price,nvl(D.discount_percent ,0)");
			sql.append("\n  )A");
			sql.append("\n  LEFT OUTER JOIN");
			sql.append("\n  (");
			sql.append("\n     SELECT M.start_date,M.end_date");
			sql.append("\n     ,M.promo_name,D.sub_promo_name");
			sql.append("\n     ,D.start_promo_qty ,D.end_promo_qty,D.DISCOUNT_PERCENT");
			sql.append("\n     from PENSBI.M_C4_MST M,PENSBI.M_C4_DT D ");
			sql.append("\n     where M.promo_id = D.promo_id");
			sql.append("\n  )P");
			sql.append("\n  ON A.order_date between P.start_date and P.end_date");
			sql.append("\n  and A.discount_percent = P.discount_percent ");
			sql.append("\n )AA");
			sql.append("\n WHERE AA.retail_sell_amt > 0 ");
			sql.append("\n order by AA.promo_name,AA.sub_promo_name,AA.MATERIAL_MASTER,AA.pens_item");
			////////////////////////////
            sql.append("\n   )A ");
         
     	    // get record start to end 
            if( !allRec){
     	      sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
     	    sql.append("\n )M  ");
		    if( !allRec){
			  sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
		    }
		    if(getTotalRec){
			   sql.append("\n ) ");	
			}
		    
		    if( getTotalRec){
				logger.debug("sql:"+sql);
		    }
		    if( !getTotalRec){
			 // logger.debug("sql:"+sql);
		      if(logger.isDebugEnabled()){
			      FileUtil.writeFile("D://dev_temp//temp//sql.sql", sql.toString());
		      }
		    }
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(getTotalRec){
				if(rst.next()){
				  ShopBean s = new ShopBean(); 
				  s.setTotalRec(rst.getInt("c"));
				  items.add(s);
				}
			}else{
				while(rst.next()) {
				   h = new ShopBean();
				   h.setPromoName(Utils.isNull(rst.getString("PROMO_NAME")));
				   h.setSubPromoName(Utils.isNull(rst.getString("SUB_PROMO_NAME")));
				   h.setStartDate(Utils.stringValueNull(rst.getDate("start_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setEndDate(Utils.stringValueNull(rst.getDate("end_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setStyle(Utils.isNull(rst.getString("MATERIAL_MASTER")));
				   h.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
				   h.setQty(Utils.decimalFormat(rst.getInt("QTY"),Utils.format_current_no_disgit));
				   h.setWholeSellAmt(Utils.decimalFormat(rst.getDouble("whole_sell_amt"),Utils.format_current_2_disgit));
				   h.setRetailSellAmt(Utils.decimalFormat(rst.getDouble("retail_sell_amt"),Utils.format_current_2_disgit));
				   
				   h.setDiscountPercent(Utils.decimalFormat(rst.getDouble("discount_percent"),Utils.format_current_2_disgit));
				   h.setDiscountAmt(Utils.decimalFormat(rst.getDouble("discount_amt"),Utils.format_current_2_disgit));
				   
				   h.setSellAfDisc(Utils.decimalFormat(rst.getDouble("sell_af_disc"),Utils.format_current_2_disgit));
				   h.setWacoalAmt(Utils.decimalFormat(rst.getDouble("wacoal_amt"),Utils.format_current_2_disgit));
				   h.setPensAmt(Utils.decimalFormat(rst.getDouble("pens_amt"),Utils.format_current_2_disgit));
				   items.add(h);
				   no++;
				}//while
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
 
 public static StringBuffer genWheresearchList(ShopBean o){
	 StringBuffer sql = new StringBuffer();
	 if( !Utils.isNull(o.getPromoName()).equals("")){
		 sql.append("\n and j.promo_name ='"+o.getPromoName()+"'");
	 }
	 if(o.getPromoId() != 0){
		 sql.append("\n and j.promo_id ="+o.getPromoId()+"");
	 }
	 return sql;
 }
 public static StringBuffer exportToExcel(HttpServletRequest request, ShopForm form,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String colspan ="14";
		ShopBean bean = form.getBean();
		Connection conn =null;
		try{
			//search all
			conn = DBConnection.getInstance().getConnectionApps();
			List<ShopBean> itemsList = searchHeadList(conn,form.getBean(),true,1,pageSize,false);
			
			h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			h.append("<table border='1'> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"'><b>สรุปรายละเอียด การวางบิล เก็บเงินระหว่าง Wacoal กับ PENS</b></td> \n");
			h.append(" </tr> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"' ><b>วันที่ขาย :"+bean.getStartDate()+" ถึงวันที่ขาย "+bean.getEndDate()+"</b></td> \n");
			h.append(" </tr> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"' ><b>Group Code:"+bean.getGroupCode()+"</b></td> \n");
			h.append(" </tr> \n");
			h.append("</table> \n");

			if(itemsList != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th rowspan='2'>Promotion หลัก</th> \n");
				  h.append("<th rowspan='2'>Promotion</th> \n");
				  h.append("<th rowspan='2'>จากวันที่</th> \n");
				  h.append("<th rowspan='2'>ถึงวันที่</th> \n");
				  h.append("<th rowspan='2'>สีไซร์</th> \n");
				  h.append("<th rowspan='2'>Pens Item</th> \n");
				  h.append("<th rowspan='2'>ยอดขายชิ้น</th> \n");
				  h.append("<th rowspan='2'>ขายปลีก</th> \n");
				  h.append("<th rowspan='2'>ขายส่ง</th> \n");
				  h.append("<th colspan='2'>ส่วนลดลูกค้า</th> \n");
				  h.append("<th rowspan='2'>ยอดขาย หักส่วนลดลูกค้า</th> \n");
				  h.append("<th rowspan='2'>ไทยวาโก้ เก็บชดเชย</th> \n");
				  h.append("<th rowspan='2'>PENS เก็บค่าบริการขาย 6%</th> \n");
				h.append("</tr> \n");
				h.append("<tr> \n");
				  h.append("<th>%</th> \n");
				  h.append("<th>AMT</th> \n");
				h.append("</tr> \n");
				for(int i=0;i<itemsList.size();i++){
					ShopBean s = (ShopBean)itemsList.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getPromoName())+"</td> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getSubPromoName())+"</td> \n");
					  h.append("<td class='text'>"+s.getStartDate()+"</td> \n");
					  h.append("<td class='text'>"+s.getEndDate()+"</td> \n");
					  h.append("<td class='text'>"+s.getStyle()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getQty()+"</td> \n");
					  h.append("<td class='currency'>"+s.getRetailSellAmt()+"</td> \n");
					  h.append("<td class='currency'>"+s.getWholeSellAmt()+"</td> \n");
					  h.append("<td class='currency'>"+s.getDiscountPercent()+"</td> \n");
					  h.append("<td class='currency'>"+s.getDiscountAmt()+"</td> \n");
					  h.append("<td class='currency'>"+s.getSellAfDisc()+"</td> \n");
					  h.append("<td class='currency'>"+s.getWacoalAmt()+"</td> \n");
					  h.append("<td class='currency'>"+s.getPensAmt()+"</td> \n");
					h.append("</tr>");
				}
				/** Summary **/
				//ShopBean s = (ShopBean)request.getSession().getAttribute("summary");
				//h.append("<tr> \n");
				 
				 /* if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }else{
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
				  h.append("<td></td> \n");
				  h.append("<td class='currency_bold'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
				h.append("</tr>");*/
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return h;
	}

}
