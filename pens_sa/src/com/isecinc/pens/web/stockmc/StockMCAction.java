package com.isecinc.pens.web.stockmc;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBConnection;
import util.ExcelHeader;
import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockMCAction extends I_Action {

	public static int pageSize = 60;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			
			if("new".equals(action)){
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResults(null);
				//prepare bean
				StockMCBean bean = new StockMCBean();
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
				
			}else if("back".equals(action)){
				//clear session 
				aForm.setResults(null);
				//prepare bean
				StockMCBean bean = new StockMCBean();
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		StockMCForm aForm = (StockMCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(StockMCDAO.searchStockMCListTotalRec(conn,aForm.getBean()));
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
			   
				List<StockMCBean> items = StockMCDAO.searchStockMCList(conn,aForm.getBean(),allRec,currPage,pageSize);
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
			    List<StockMCBean> items = StockMCDAO.searchStockMCList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResults(items);
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
	}
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("viewDetail : ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = aForm.getPageName();
		Connection conn = null;
		StockMCDAO dao = new StockMCDAO();
		try {
			//save criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			String id = Utils.isNull(request.getParameter("id"));
			String action = Utils.isNull(request.getParameter("action"));
			
			logger.debug("action:"+action);
			logger.debug("id:"+id);
			
			if("add".equalsIgnoreCase(action)){
				StockMCBean bean = new StockMCBean();
				// for test
			/*	bean.setCustomerCode("020011");
				bean.setCustomerName("Lotus");
				bean.setMcName("MC NameNaja");
				bean.setStoreCode("020047-1");*/
				
				bean.setStockDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				bean.setCanEdit(true);
				aForm.setBean(bean);
				aForm.setResults(null);
				aForm.setMode("add");
			}else if("edit".equalsIgnoreCase(action)){
				StockMCBean bean = new StockMCBean();
			
				bean.setId(Utils.convertStrToInt(id));
				bean = dao.searchStockMC(conn, bean, true);
				if(bean != null){
					bean.setCanEdit(true);
					aForm.setBean(bean);
					aForm.setResults(bean.getItems());
				}
				aForm.setMode("edit");
			}else if("view".equalsIgnoreCase(action)){
				StockMCBean bean = new StockMCBean();
				bean.setId(Utils.convertStrToInt(id));
				bean = dao.searchStockMC(conn, bean, true);
				if(bean != null){
					aForm.setBean(bean);
					aForm.setResults(bean.getItems());
				}
				aForm.setMode("view");
			}
			// save token
			saveToken(request);			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	public ActionForward loadItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("loadItem : ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = aForm.getPageName();
		Connection conn = null;
		try {
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			 aForm.setMode("edit");
			 aForm.setResults(StockMCDAO.getProductMCItemList(conn,aForm.getBean().getCustomerCode()));
			
			// save token
			saveToken(request);			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward clearForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("clearForm  ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = aForm.getPageName();
		Connection conn = null;
		try {
			StockMCBean bean = new StockMCBean();
			bean.setStockDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			bean.setCanEdit(true);
			aForm.setBean(bean);
			aForm.setResults(null);
			aForm.setMode("add");
			
			// save token
			saveToken(request);			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockMCForm stockForm = (StockMCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		StockMCDAO dao = new StockMCDAO();
		try {
			logger.debug("save-->");

			// check Token
			if (!isTokenValid(request)) {
				logger.debug("Token invalid");
				//stockForm.getResults().clear();
				return "new";
			}
			
			StockMCBean m = stockForm.getBean();
			m.setCreateUser(user.getUserName());
			m.setUpdateUser(user.getUserName());
			 
			List<StockMCBean> itemList = new ArrayList<StockMCBean>();
			String[] lineId =request.getParameterValues("lineId");
			//String[] status =request.getParameterValues("status");
			String[] productCode =request.getParameterValues("productCode");
			String[] barcode =request.getParameterValues("barcode");
			String[] productPackSize =request.getParameterValues("productPackSize");
			String[] productAge =request.getParameterValues("productAge");
			String[] retailPriceBF =request.getParameterValues("retailPriceBF");
			
			String[] promotionPrice =request.getParameterValues("promotionPrice");
			String[] legQty =request.getParameterValues("legQty");
			String[] inStoreQty =request.getParameterValues("inStoreQty");
			String[] backendQty =request.getParameterValues("backendQty");
			String[] uom =request.getParameterValues("uom");
			
			String[] frontendQty1 =request.getParameterValues("frontendQty1");
			String[] uom1 =request.getParameterValues("uom1");
			String[] expireDate1 =request.getParameterValues("expireDate1");
			
			String[] frontendQty2 =request.getParameterValues("frontendQty2");
			String[] uom2 =request.getParameterValues("uom2");
			String[] expireDate2 =request.getParameterValues("expireDate2");
			
			String[] frontendQty3 =request.getParameterValues("frontendQty3");
			String[] uom3 =request.getParameterValues("uom3");
			String[] expireDate3 =request.getParameterValues("expireDate3");
			
			logger.debug("productCode:"+productCode.length);
			logger.debug("backendQty:"+backendQty+":"+backendQty.length);
			
			//add value to Results
			if(productCode != null && productCode.length > 0){
				for(int i=0;i<productCode.length;i++){
					if( !Utils.isNull(productCode[i]).equals("")){// && !Utils.isNull(status[i]).equals("DELETE")){
						StockMCBean l = new StockMCBean();
						 logger.debug("lineId:"+lineId[i]);
						// logger.debug("promotionPrice["+i+"]["+promotionPrice[i]+"]");
						 
						 l.setLineId(Utils.convertStrToInt(lineId[i]));
						 l.setProductCode(Utils.isNull(productCode[i]));
						 l.setBarcode(Utils.isNull(barcode[i]));
						 l.setProductPackSize(Utils.isNull(productPackSize[i]));
						 l.setProductAge(Utils.isNull(productAge[i]));
						 l.setRetailPriceBF(retailPriceBF[i]);
						 
						 l.setPromotionPrice(promotionPrice[i]);
						 l.setLegQty(Utils.isNull(legQty[i]));
						 
						 l.setInStoreQty(inStoreQty[i]);
						 l.setBackendQty(backendQty[i]);
						 l.setUom(Utils.isNull(uom[i]));
						 
						 l.setFrontendQty1(frontendQty1[i]);
						 l.setUom1(uom1[i]);
						 l.setExpireDate1(Utils.isNull(expireDate1[i]));
					      
						 l.setFrontendQty2(frontendQty2[i]);
						 l.setUom2(uom2[i]);
						 l.setExpireDate2(Utils.isNull(expireDate2[i]));
						 
						 l.setFrontendQty3(frontendQty3[i]);
						 l.setUom3(uom3[i]);
						 l.setExpireDate3(Utils.isNull(expireDate3[i]));
						 
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 
						 itemList.add(l);
					}//if
				}//for
			}//if
			
			//set items list
			m.setItems(itemList);
			
			//Save to DB
			m = dao.save(m);
			
			//search 
			m = dao.searchStockMC(m,true);
			m.setCanEdit(true);
			m.setLineIdDeletes("");//clear lineId delete 
			stockForm.setBean(m);
			stockForm.setResults(m.getItems());
			
			request.setAttribute("Message","บันทึกข้อมูลเรียบร้อย");
		
			// save token
			saveToken(request);
		} catch (Exception e) {
			e.printStackTrace();
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		StockMCForm aForm = (StockMCForm) form;
		StringBuffer resultTable = null;
		String pageName = aForm.getPageName();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			List<StockMCBean> items = StockMCDAO.searchStockMCList(conn,aForm.getBean(),true,0,pageSize);
		    if(items!= null && items.size() >0){
		    	resultTable = genExcelHTMLTable(items);
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
		   }else{
			  request.setAttribute("Message","ไม่พบข้อมูล");
		   }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	private StringBuffer genExcelHTMLTable(List<StockMCBean> items) throws Exception{
	 StringBuffer h = new StringBuffer("");
	 int no = 0;
	 h.append(ExcelHeader.EXCEL_HEADER);
	 
	 h.append("<table border='1'> \n");
	 h.append("<tr><td colspan='10'><b>รายงาน  ข้อมูลตั้งกองโชว์ </b></td></tr> \n");
	 h.append("</table> \n");
	 h.append("<table border='1'> \n");
	 h.append("<tr> \n");
	 h.append("<th>No.</th>\n");
	 h.append("<th>ภาคการขาย</th>\n");
	 h.append("<th>พนักงานขาย</th>\n");
	 h.append("<th>รหัสร้านค้า</th>\n");
	 h.append("<th>ชื่อร้านค้า</th>\n");
	 h.append("<th>เลขที่ ตั้งกอง</th>\n");
	 h.append("<th>วันที่ทำรายการ</th>\n");
	 h.append("<th>แบรนด์</th>\n");
	 h.append("<th>รูปที่ 1</th>\n");
	 h.append("<th>รูปที่ 2</th>\n");
	 h.append("<th>รูปที่ 3</th>\n");
	 h.append("</tr> \n");
	 for(int i=0;i<items.size();i++){
		 StockMCBean p = items.get(i);
		 no++;
		/* h.append("<tr> \n");
		 h.append("<td class='text'> "+no+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesChannelName()+"</td>\n");
		 h.append("<td class='text'> "+p.getSalesrepCode()+"-"+p.getSalesrepName()+"</td>\n");
		 h.append("<td class='text'> "+p.getCustomerCode()+"</td>\n");
		 h.append("<td class='text'> "+p.getCustomerName()+"</td>\n");
		 h.append("<td class='text'> "+p.getOrderNo()+"</td>\n");
		 h.append("<td class='text'> "+p.getShowDate()+"</td>\n");
		 h.append("<td class='text'> "+p.getBrand()+"-"+p.getBrandName()+"</td>\n");
		 h.append("<td class='text'> "+p.getPic1()+"</td>\n");
		 h.append("<td class='text'> "+p.getPic2()+"</td>\n");
		 h.append("<td class='text'> "+p.getPic3()+"</td>\n");*/
		 h.append("</tr> \n");
	 }
	 h.append("</table> \n");
	 return h;
	}
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockMCForm orderForm = (StockMCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
	}
	
	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {

	}
	
	
}
