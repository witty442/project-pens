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
import com.isecinc.pens.web.autocn.AutoCNBean;
import com.isecinc.pens.web.shop.ShopBean;
import com.isecinc.pens.web.shop.ShopForm;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;

public class ShopPromotionAction {
 private static Logger logger = Logger.getLogger("PENS");
 public static int pageSize = 25;
 
 /**
	 * Save
	 */
	public static ShopForm searchPromotionDetail(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ShopForm aForm = (ShopForm) form;
		ShopBean bean = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			String mode =Utils.isNull(request.getParameter("mode"));
			String promoId =Utils.isNull(request.getParameter("promoId"));
			logger.debug("mode:"+mode+",promoId:"+promoId);
			
			if("add".equalsIgnoreCase(mode)){
				bean = new ShopBean();
				aForm.setBean(bean);
				aForm.setResults(new ArrayList<ShopBean>());
			}else if("edit".equalsIgnoreCase(mode)){
				 ShopBean criBean = new ShopBean();
				 criBean.setPromoId(Long.parseLong(promoId));
				 aForm.setBean(searchPromotionHeadList(conn, criBean, true, 1, pageSize).get(0));
				 aForm.setResults(searchPromotionItemList(conn,aForm.getBean()));
				 
			}else if("view".equalsIgnoreCase(mode)){
				 ShopBean criBean = new ShopBean();
				 criBean.setPromoId(Long.parseLong(promoId));
				 aForm.setBean(searchPromotionHeadList(conn, criBean, true, 1, pageSize).get(0));
				 aForm.setResults(searchPromotionItemList(conn,aForm.getBean()));
			}

		} catch (Exception e) {
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return aForm;
	}
 /**
	 * Save
	 */
	public static ShopForm save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ShopForm aForm = (ShopForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int i= 0;
		ShopBean item = null;
		List<ShopBean> items = new ArrayList<ShopBean>();
		List<ShopBean> itemsDelete = new ArrayList<ShopBean>();
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			ShopBean head = aForm.getBean();
			head.setUserName(user.getUserName());
			
			//get Parametervalues
			String[] keyDatas = request.getParameterValues("keyData");
			String[] subPromoId = request.getParameterValues("subPromoId");
			String[] modifierLineId = request.getParameterValues("modifierLineId");
			String[] subPromoName = request.getParameterValues("subPromoName");
			String[] startPromtionQty = request.getParameterValues("startPromtionQty");
			String[] endPromtionQty = request.getParameterValues("endPromtionQty");
			String[] discountPercent = request.getParameterValues("discountPercent");
			
			logger.debug("keyDatas length:"+keyDatas.length);
			
			for(i=0;i<keyDatas.length;i++){
				logger.debug("keyData:"+keyDatas[i]+",subPromoId:"+subPromoId[i]+","+Utils.convertToLong(subPromoId[i]));
				if( !Utils.isNull(subPromoName[i]).equals("") 
					&& !Utils.isNull(keyDatas[i]).equals("CANCEL") ){
					
				   item = new ShopBean();
				   item.setSubPromoId(Utils.convertToLong(subPromoId[i]));
				   item.setModifierLineId(modifierLineId[i]);
				   item.setSubPromoName(subPromoName[i]);
				   item.setStartPromtionQty(startPromtionQty[i]);
				   item.setEndPromtionQty(endPromtionQty[i]);
				   item.setDiscountPercent(discountPercent[i]);
				   item.setUserName(head.getUserName());
				   items.add(item);
				}else if( !Utils.isNull(subPromoName[i]).equals("") 
					&& Utils.isNull(keyDatas[i]).equals("CANCEL") ){
					
				   item = new ShopBean();
				   item.setSubPromoId(Utils.convertToLong(subPromoId[i]));
				   item.setModifierLineId(modifierLineId[i]);
				   item.setSubPromoName(subPromoName[i]);
				   item.setStartPromtionQty(startPromtionQty[i]);
				   item.setEndPromtionQty(endPromtionQty[i]);
				   item.setDiscountPercent(discountPercent[i]);
				   item.setUserName(head.getUserName());
				   itemsDelete.add(item);
				}
			}
			head.setItemsList(items);
			head.setItemsDeleteList(itemsDelete);
			
			//save db
			saveShopPromotionDB(conn, head);
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อย");
			
		    conn.commit();
		    
		    aForm.setResults(searchPromotionItemList(conn,aForm.getBean()));
		} catch (Exception e) {
			logger.debug("Conn Rollback");
			conn.rollback();
         logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			return aForm;
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return aForm;
	}
	
 public static ShopForm search(ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchPromotion");
		ShopForm aForm = (ShopForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(searchPromotionTotalRecList(conn,aForm.getBean()));
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
				List<ShopBean> items = searchPromotionHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
				List<ShopBean> items = searchPromotionHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
 
 public static int searchPromotionTotalRecList(Connection conn,ShopBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ShopBean h = null;
	    int totalRec = 0;
		try {
            sql.append("\n select count(*) as c FROM PENSBI.M_C4_MST j ");
		    sql.append("\n where 1=1 ");
		    //Where Condition
		      sql.append(genWheresearchPromotionList(o).toString());
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRec = rst.getInt("c");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRec;
	}
 
 public static List<ShopBean> searchPromotionHeadList(Connection conn,ShopBean o,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ShopBean h = null;
		List<ShopBean> items = new ArrayList<ShopBean>();
		int no=1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
            sql.append("\n   select j.* ");
		    sql.append("\n   from PENSBI.M_C4_MST j  ");
		    sql.append("\n   where 1=1 ");
		    //Where Condition
		    sql.append(genWheresearchPromotionList(o).toString());
            sql.append("\n     order by j.promo_id desc");
            sql.append("\n   )A ");
         
     	    // get record start to end 
            if( !allRec){
     	     sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
     	    sql.append("\n )M  ");
		    if( !allRec){
			  sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
		    }
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new ShopBean();
			   h.setPromoId(rst.getLong("promo_id"));
			   h.setPromoName(Utils.isNull(rst.getString("PROMO_NAME")));
			   h.setStartDate(Utils.stringValueNull(rst.getDate("start_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setEndDate(Utils.stringValueNull(rst.getDate("end_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  
			   h.setCanEdit(true);
			   
			   items.add(h);
			   no++;
			}//while
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
 
 public static StringBuffer genWheresearchPromotionList(ShopBean o){
	 StringBuffer sql = new StringBuffer();
	 if( !Utils.isNull(o.getPromoName()).equals("")){
		 sql.append("\n and j.promo_name ='"+o.getPromoName()+"'");
	 }
	 if(o.getPromoId() != 0){
		 sql.append("\n and j.promo_id ="+o.getPromoId()+"");
	 }
	 return sql;
 }
 public static List<ShopBean> searchPromotionItemList(Connection conn,ShopBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ShopBean h = null;
		List<ShopBean> items = new ArrayList<ShopBean>();
		int no=1;
		try {
            sql.append("\n   select j.* ");
		    sql.append("\n   from PENSBI.M_C4_DT j ");
		    sql.append("\n   where j.promo_id="+o.getPromoId());
		    sql.append("\n   order by j.sub_promo_id asc ");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new ShopBean();
			   h.setModifierLineId(Utils.isNull(rst.getString("modifier_line_id")));
			   h.setSubPromoId(rst.getLong("sub_promo_id"));
			   h.setSubPromoName(Utils.isNull(rst.getString("SUB_PROMO_NAME")));
			   h.setStartPromtionQty(rst.getInt("START_PROMO_QTY")+"");
			   h.setEndPromtionQty(rst.getInt("END_PROMO_QTY")+"");
			   h.setDiscountPercent(rst.getInt("DISCOUNT_PERCENT")+"");
			   items.add(h);
			   no++;
			}//while
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
 public static void saveShopPromotionDB(Connection conn,ShopBean head) throws Exception{
		int i=0;
		ShopBean item = null;
		try{
			//insert head case edit
			if( !isPromotionHeadExist(conn,head) ){
			   insertPromotionHeadModel(conn, head);
			}else{
			   updatePromotionHeadModel(conn, head);
			}
			
			//delete item cancel by promo_id,sub_promo_id
		    if(head.getItemsDeleteList() != null && head.getItemsDeleteList().size() >0){
		    	for(i=0;i<head.getItemsDeleteList().size();i++){
		    		item = head.getItemsDeleteList().get(i);
					deletePromotionItemByPK(conn, head, item);
		    	}
		    }
			 
			//insert item
			long maxSubPromoID =getMaxSubPromoID(conn, head)+1;
		    if(head.getItemsList() != null && head.getItemsList().size() >0){
		    	for(i=0;i<head.getItemsList().size();i++){
		    		item = head.getItemsList().get(i);
		    		if(item.getSubPromoId() != 0){
		    			updatePromotionItemModel(conn, head, item);
		    		}else{
			    		item.setSubPromoId(maxSubPromoID);
			    		//insert item
			    		insertPromotionItemModel(conn, head, item);
			    		maxSubPromoID++;
		    		}
		    	}
		    }
		}catch(Exception e){
			throw e;
		}
	}
	
	public static void cancelPromotion(Connection conn,AutoCNBean head) throws Exception{
		try{
			//update status head and item
			//updateStatusHeadModel(conn, head);
			//updateStatusItemModel(conn, head);
			
		}catch(Exception e){
			throw e;
		}
	}
	
	 public static int insertPromotionHeadModel(Connection conn,ShopBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				//Get Next Id promoID
				head.setPromoId(SequenceProcess.getNextValue("M_C4_MST"));
				
				sql.append("INSERT INTO PENSBI.M_C4_MST \n");
				sql.append("(PROMO_ID, PROMO_NAME, START_DATE, END_DATE,CREATE_DATE,CREATE_USER) \n");
				sql.append("VALUES(?,?,?,?,?,?) \n");
				
				//logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
	
				
				ps.setLong(c++, head.getPromoId());
				ps.setString(c++, Utils.isNull(head.getPromoName()));  
				ps.setDate(c++, new java.sql.Date(Utils.parse(head.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th).getTime())); 
				ps.setDate(c++, new java.sql.Date(Utils.parse(head.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th).getTime()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				
				r =ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return r;
		}
	 
	 public static int updatePromotionHeadModel(Connection conn,ShopBean head) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.M_C4_MST \n");
				sql.append("SET PROMO_NAME =? ,START_DATE =?, END_DATE =?  ,UPDATE_DATE = ?, UPDATE_USER = ? \n");
				sql.append("WHERE PROMO_ID =? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, Utils.isNull(head.getPromoName()));
				ps.setDate(c++, new java.sql.Date(Utils.parse(head.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th).getTime())); 
				ps.setDate(c++, new java.sql.Date(Utils.parse(head.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th).getTime()));
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setLong(c++, head.getPromoId()); 
				
				r =ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return r;
		}
	 
	 public static long getMaxSubPromoID(Connection conn,ShopBean head) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			logger.debug("isHeadExist");
			long r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("select MAX(SUB_PROMO_ID) as c from PENSBI.M_C4_DT \n");
				sql.append("WHERE promo_id = "+head.getPromoId()+"  \n");
				logger.debug("sql:"+sql.toString());
				
		        ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					if(rs.getInt("c") >0){
						r = rs.getInt("c");
					}
				}
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs !=null){
					rs.close();rs =null;
				}
			}
			return r;
		}
	 
	 public static boolean isPromotionHeadExist(Connection conn,ShopBean head) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			logger.debug("isHeadExist");
			boolean r =false;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("select count(*) as c from PENSBI.M_C4_MST \n");
				sql.append("WHERE promo_id = "+head.getPromoId()+"  \n");
				logger.debug("sql:"+sql.toString());
				
		        ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					if(rs.getInt("c") >0){
						r = true;
					}
				}
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs !=null){
					rs.close();rs =null;
				}
			}
			return r;
		}
	 public static boolean isPromotionItemExist_BK(Connection conn,ShopBean head) throws Exception{
			PreparedStatement ps = null;
			ResultSet rs = null;
			logger.debug("isHeadExist");
			boolean r =false;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("select count(*) as c from PENSBI.M_C4_MST \n");
				sql.append("WHERE promo_id = "+head.getPromoId()+" and sub_promo_id="+head.getSubPromoId()+" \n");
				logger.debug("sql:"+sql.toString());
				
		        ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					if(rs.getInt("c") >0){
						r = true;
					}
				}
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs !=null){
					rs.close();rs =null;
				}
			}
			return r;
		}
	 
	 public static int insertPromotionItemModel(Connection conn,ShopBean head,ShopBean item) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertItemModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("INSERT INTO PENSBI.M_C4_DT \n");
				sql.append("(PROMO_ID, SUB_PROMO_ID, SUB_PROMO_NAME \n");
				sql.append(",START_PROMO_QTY , END_PROMO_QTY,DISCOUNT_PERCENT, CREATE_DATE, CREATE_USER,MODIFIER_LINE_ID) \n");
				sql.append("VALUES(?,?,?,?,?,?,?,?,?) \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
	
				ps.setLong(c++, head.getPromoId());
				ps.setLong(c++, SequenceProcess.getNextValue("M_C4_DT"));
				ps.setString(c++, Utils.isNull(item.getSubPromoName())); 
				ps.setDouble(c++, Utils.convertStrToDouble(item.getStartPromtionQty())); 
				ps.setDouble(c++, Utils.convertStrToDouble(item.getEndPromtionQty())); 
				ps.setDouble(c++, Utils.convertStrToDouble(item.getDiscountPercent())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setInt(c++, Utils.convertStrToInt(item.getModifierLineId()));
				
				r =ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return r;
		}
	 
	 public static int updatePromotionItemModel(Connection conn,ShopBean head,ShopBean item) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertItemModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("UPDATE PENSBI.M_C4_DT \n");
				sql.append("SET SUB_PROMO_NAME = ?,START_PROMO_QTY =? , END_PROMO_QTY =?");
				sql.append(",DISCOUNT_PERCENT =? , UPDATE_DATE=?, UPDATE_USER= ? \n");
				sql.append(",MODIFIER_LINE_ID =? \n");
				sql.append("WHERE PROMO_ID =? AND SUB_PROMO_ID = ? \n");
				
				//logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, Utils.isNull(item.getSubPromoName())); 
				ps.setDouble(c++, Utils.convertStrToDouble(item.getStartPromtionQty())); 
				ps.setDouble(c++, Utils.convertStrToDouble(item.getEndPromtionQty())); 
				ps.setDouble(c++, Utils.convertStrToDouble(item.getDiscountPercent())); 
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, head.getUserName());
				ps.setInt(c++, Utils.convertStrToInt(item.getModifierLineId()));
				ps.setLong(c++, head.getPromoId());
				ps.setLong(c++, item.getSubPromoId());
				
				r =ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return r;
		}
	 
	 public static int deletePromotionItemByPK(Connection conn,ShopBean head,ShopBean item) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertHeadModel");
			int c =1;int r = 0;
			StringBuffer sql = new StringBuffer("");
			try{
				sql.append("DELETE FROM PENSBI.M_C4_DT \n");
				sql.append("WHERE PROMO_ID = "+head.getPromoId()+" AND SUB_PROMO_ID = "+item.getSubPromoId()+"  \n");
			
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				r =ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return r;
		}
}
