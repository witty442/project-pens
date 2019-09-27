package com.isecinc.pens.web.mtt;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.MTTBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.MTTBeanDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.shop.ShopBean;
import com.isecinc.pens.web.shop.ShopForm;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MTTAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		MTTForm aForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResults(null);
				aForm.setSummary(null);
				
				MTTBean ad = new MTTBean();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				
				aForm.setBean(ad);
				
				//init session CustGroupList
				List<PopupForm> billTypeList = new ArrayList();
				PopupForm ref = new PopupForm("",""); 
				billTypeList.add(ref);
				billTypeList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
				request.getSession().setAttribute("custGroupList",billTypeList);
				
			}else if("back".equals(action)){
				aForm.setBean(aForm.getBeanCriteria());//prev criteria
				search2(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("prepare2");
	}
	
	 public static ActionForward search2(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
			logger.debug("search2");
			MTTForm aForm = (MTTForm) form;
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
					aForm.setTotalRecord(MTTBeanDAO.searchHeadTotalRecList(conn,aForm.getBean()));
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
					List<MTTBean> items = MTTBeanDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
					aForm.setResults(items);
					
					if(items.size() <=0){
					   request.setAttribute("Message", "ไม่พบข้อมูล");
					   aForm.setResults(null);
					}else{
						//currPage ==totalPage ->get Summary
						if(currPage==aForm.getTotalPage()){
						   aForm.setSummary(MTTBeanDAO.searchHeadTotalSummary(conn, aForm.getBean()));
						}
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
					List<MTTBean> items = MTTBeanDAO.searchHeadList(conn,aForm.getBean(),allRec,currPage,pageSize);
					aForm.setResults(items);
					
					//currPage ==totalPage ->get Summary
					if(currPage==aForm.getTotalPage()){
					   aForm.setSummary(MTTBeanDAO.searchHeadTotalSummary(conn, aForm.getBean()));
					}
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
			return mapping.findForward("search2");
	}
	
	 /**
		 * Prepare without ID
		 */
		protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			String forward = "prepare";
			MTTForm aForm = (MTTForm) form;
			User user = (User) request.getSession().getAttribute("user");
			try {
				//save old criteria
				aForm.setBeanCriteria(aForm.getBean());
				
	            String saleDate = Utils.isNull(request.getParameter("sale_date"));
	            String docNo = Utils.isNull(request.getParameter("docNo"));
	            String mode = Utils.isNull(request.getParameter("mode"));
	            
				if( !"".equals(docNo)){
					logger.debug("prepare edit docNo:"+docNo);
					MTTBean c = new MTTBean();
					c.setDocNo(docNo);
					
					MTTBean bean = MTTBeanDAO.searchHead(c).getItems().get(0);
					
					//search item
					MTTBean items = MTTBeanDAO.searchItem( c);
					aForm.setResults(items.getItems());
					
					aForm.setBean(bean);
					aForm.setMode(mode);//Mode Edit
				}else{
					
					logger.debug("prepare new docNo");
					aForm.setResults(new ArrayList<MTTBean>());
					MTTBean ad = new MTTBean();
					ad.setCanEdit(true);
					ad.setSaleDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					aForm.setBean(ad);
					
					aForm.setMode(mode);//Mode Add new
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("Message", "err:"+ e.getMessage());
				throw e;
			}finally{
				
			}
			return forward;
		}

		
	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareReport");
		MTTForm aForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResults(null);
				MTTBean ad = new MTTBean();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));//default Current date
				request.getSession().setAttribute("summary",null);
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("report");
	}
	
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchReport");
		MTTForm aForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setBean(MTTBeanDAO.searchHead(aForm.getBean()));
			aForm.setResults(aForm.getBean().getItems());
			
			//set summary
			MTTBean summary = new MTTBean();
			summary.setTotalQty(aForm.getBean().getTotalQty());
			request.getSession().setAttribute("summary", summary);
			
			if(aForm.getResults().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResults(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("report");
	}
	
	public ActionForward exportReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		MTTForm aForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			htmlTable = genExportReport(request,aForm,user);
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close(); 
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("report");
	}
	
	private StringBuffer genExportReport(HttpServletRequest request,MTTForm form,User user) throws Exception{
		StringBuffer h = new StringBuffer("");
		String a= "@";
		String colSpan = "15";
		Connection conn = null;
		int totalQty = 0;
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr class='colum_head'> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>รายงานข้อมูลขาย Sale-Out</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr class='colum_head'> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >จากวันที่ขาย:"+form.getBean().getSaleDateFrom()+"  ถึงวันที่ขาย:"+form.getBean().getSaleDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr class='colum_head'> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >กลุ่มร้านค้า:"+form.getBean().getCustGroup()+"&nbsp;"+Utils.isNull(form.getBean().getCustGroupName())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr class='colum_head'> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รหัสร้านค้า:"+form.getBean().getStoreCode()+"&nbsp;"+Utils.isNull(form.getBean().getStoreName())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr class='colum_head'> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Group Code:"+form.getBean().getGroupCode()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr class='colum_head'> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >จากวันที่ Scan:"+form.getBean().getCreateDateFrom()+"  ถึงวันที่ Scan:"+form.getBean().getCreateDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
			
			
			//search new
			conn = DBConnection.getInstance().getConnection();
			List<MTTBean> items = MTTBeanDAO.searchHeadList(conn,form.getBean(),true,0,pageSize);

			if(items != null){
				h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				  h.append("<td>No.</td> \n");
				  h.append("<td>SaleDate</td> \n");
				  h.append("<td>วันที่ Scan</td> \n");
				  h.append("<td>DocNo</td> \n");
				  h.append("<td>กลุ่ม</td> \n");
				  h.append("<td>รหัสกลุ่ม</td> \n");
				  h.append("<td>รหัสร้านค้า</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>GroupCode</td> \n");
				  h.append("<td>Material Master </td> \n");
				  h.append("<td>Pens Item </td> \n");
				  h.append("<td>จำนวนชิ้นที่ขาย</td> \n");
				  h.append("<td>ราคาขายปลีกก่อน VAT</td> \n");
				  h.append("<td>Remark</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<items.size();i++){
					MTTBean s = (MTTBean)items.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+(i+1)+"</td> \n");
					  h.append("<td>"+s.getSaleDate()+"</td> \n");
					  h.append("<td>"+s.getCreateDate()+"</td> \n");
					  h.append("<td>"+s.getDocNo()+"</td> \n");
					  h.append("<td>"+s.getCustGroup()+"</td> \n");
					  h.append("<td>"+s.getCustGroupName()+"</td> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td>"+s.getGroupCode()+"</td> \n");
					  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='num'>"+s.getQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getRetailPriceBF()+"</td> \n");
					  h.append("<td>"+s.getRemark()+"</td> \n");
					h.append("</tr>");
					
					//add summary
					totalQty += s.getQty();
				}
				//Add Summary
				h.append("<tr> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td class='colum_head'>Total</td> \n");
				  h.append("<td class='num_currency_bold'>"+totalQty+"</td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
		return h;
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		MTTForm aForm = (MTTForm) form;
		try {
			aForm.setResults(null);
			aForm.setSummary(null);
			aForm.setBean(new MTTBean());
			
			MTTBean ad = new MTTBean();
			ad.setSaleDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MTTForm summaryForm = (MTTForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MTTForm orderForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			MTTBean aS = null;//AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());

			orderForm.setBean(aS);
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		MTTForm aForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MTTBean h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			List<MTTBean> itemList = new ArrayList<MTTBean>();
			//Set Item
			String[] lineId = request.getParameterValues("lineId");
			String[] barcode = request.getParameterValues("barcode");
			String[] materialMaster = request.getParameterValues("materialMaster");
			String[] groupCode = request.getParameterValues("groupCode");
			String[] pensItem = request.getParameterValues("pensItem");
			//String[] wholePriceBF = request.getParameterValues("wholePriceBF");
			String[] retailPriceBF = request.getParameterValues("retailPriceBF");
			String[] status = request.getParameterValues("status");
			
			logger.debug("barcode:"+barcode.length);
			int maxLineId = 0;
			
			if( !"".equals(h.getDocNo())){
				maxLineId = MTTBeanDAO.getMaxLineIdFromMTTByDocNo(h);
			}
			
			//add value to Results
			if(barcode != null && barcode.length > 0){
				for(int i=0;i<barcode.length;i++){
					if( !Utils.isNull(barcode[i]).equals("") && !Utils.isNull(materialMaster[i]).equals("")){
						 MTTBean l = new MTTBean();
						 
						 if( Utils.isNull(lineId[i]).equals("") ||  Utils.isNull(lineId[i]).equals("0")){
						     maxLineId++;
						 }else{
							 maxLineId =  Integer.parseInt(Utils.isNull(lineId[i]));
						 }
						 
						 l.setLineId(maxLineId);
						 
						 l.setBarcode(Utils.isNull(barcode[i]));
						 l.setMaterialMaster(Utils.isNull(materialMaster[i]));
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setPensItem(Utils.isNull(pensItem[i]));
						// l.setWholePriceBF(Utils.isNull(wholePriceBF[i]));
						 l.setRetailPriceBF(Utils.isNull(retailPriceBF[i]));
						 l.setStatus(Utils.isNull(status[i]));
						 
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 itemList.add(l);
					}
				}
			}
			
			h.setItems(itemList);
			
			//Store in Session
			aForm.setResults(itemList);
			
			//save
			MTTBeanDAO.save(h);
			
			//commit
			conn.commit();

			//search
			h = MTTBeanDAO.searchHead(h);
			aForm.setBean(h);
			aForm.setResults(MTTBeanDAO.searchItem(h).getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			
			//save old criteria
			MTTBean cri = new MTTBean();
			cri.setSaleDateFrom(h.getSaleDate());
			cri.setSaleDateTo(h.getSaleDate());
			cri.setCustGroup(h.getCustGroup());
			cri.setStoreCode(h.getStoreCode());
		
			aForm.setBeanCriteria(cri);
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "search";
	}
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		MTTForm aForm = (MTTForm) form;
		try {
			aForm.setResults(new ArrayList<MTTBean>());
			
			MTTBean ad = new MTTBean();
			ad.setCustGroup(aForm.getBean().getCustGroup());
			ad.setStoreCode(aForm.getBean().getStoreCode());
			ad.setStoreName(aForm.getBean().getStoreName());
			ad.setSaleDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}

	public ActionForward newBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("newBox");
		MTTForm aForm = (MTTForm) form;
		try {
			aForm.setResults(new ArrayList<MTTBean>());
			MTTBean ad = new MTTBean();
			//ad.setJobId(aForm.getBean().getJobId());
			//ad.setName(aForm.getBean().getName());
			ad.setSaleDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
			/*ad.setStoreCode(aForm.getBean().getStoreCode());
			ad.setStoreName(aForm.getBean().getStoreName());
			ad.setStoreNo(aForm.getBean().getStoreNo());
			ad.setSubInv(aForm.getBean().getSubInv());
			ad.setRemark(aForm.getBean().getRemark());
			*/
			ad.setCanEdit(true);
			
			aForm.setBean(ad);
			aForm.setMode("add");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		MTTForm aForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			MTTBean h = aForm.getBean();
			h.setUpdateUser(user.getUserName());
			h.setStatus(MTTBeanDAO.STATUS_CANCEL);
			
			MTTBeanDAO.updateMTTStatusByDocNo(conn, h);
			
			conn.commit();
			
			MTTBean items = MTTBeanDAO.searchItem(h);
			aForm.setResults(items.getItems());
			aForm.setBean(h);
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward prepareScanReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareScanReport");
		MTTForm aForm = (MTTForm) form;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResults(null);
				MTTBean ad = new MTTBean();	
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("scanReport");
	}
	
	public ActionForward searchScanReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchScanReport");
		MTTForm aForm = (MTTForm) form;
		try {
			aForm.setBean(MTTBeanDAO.searchScanReport(aForm.getBean()));
			aForm.setResults(aForm.getBean().getItems());
			
			if(aForm.getResults().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResults(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("scanReport");
	}
	
	public ActionForward exportScanReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportScanReport");
		MTTForm aForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			htmlTable = genExportScanReport(request,aForm,user);
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close(); 
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("scanReport");
	}
	
	
	private StringBuffer genExportScanReport(HttpServletRequest request,MTTForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		int colSpan =7;
		try{
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'"+a+"'; \n");
			h.append(" } \n");
			h.append("</style> \n");
			
			 if("barcode".equalsIgnoreCase(form.getBean().getDispType())){
				 colSpan = 9;
			 }
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"'>รายงาน ผล Scan ยอดสต๊อกคงเหลือ</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >กลุ่มร้านค้า:"+form.getBean().getCustGroup()+"&nbsp;"+Utils.isNull(form.getBean().getCustGroupName())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >รหัสร้านค้า:"+form.getBean().getStoreCode()+"&nbsp;"+Utils.isNull(form.getBean().getStoreName())+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >จากวันที่ทำรายการ:"+form.getBean().getDocDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Group Code:"+form.getBean().getGroupCode()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >แสดงผลตาม:"+form.getBean().getDispType()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResults() != null){
			    List<MTTBean> list = (List<MTTBean>)form.getResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				 h.append("<td>เลขที่เอกสาร</td> \n");
				 h.append("<td>วันที่ทำรายการ</td> \n");
				 h.append("<td>Cust Group</td> \n");
				 h.append("<td>Cust No</td> \n");
				 h.append("<td>GroupCode</td> \n");
				 h.append("<td>Pens Item </td> \n");
				 if("barcode".equalsIgnoreCase(form.getBean().getDispType())){
				   h.append("<td>Material Master </td> \n");
				   h.append("<td>Barcode </td> \n");
				 }
				 h.append("<td>QTY</td> \n");
				
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					MTTBean s = (MTTBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getDocNo()+"</td> \n");
					  h.append("<td>"+s.getDocDate()+"</td> \n");
					  h.append("<td>"+s.getCustGroup()+"&nbsp;"+s.getCustGroupName()+"</td> \n");
					  h.append("<td>"+s.getStoreCode()+"&nbsp;"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroupCode()+"</td> \n");
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  if("barcode".equalsIgnoreCase(form.getBean().getDispType())){
						  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
						  h.append("<td class='num'>"+s.getBarcode()+"</td> \n");
					  }
					  h.append("<td class='num'>"+s.getQty()+"</td> \n");
					
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public ActionForward clearScanReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearScanReport");
		MTTForm aForm = (MTTForm) form;
		try {
			aForm.setResults(new ArrayList<MTTBean>());
			
			MTTBean ad = new MTTBean();
			ad.setDocDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("scanReport");
	}
	
	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report : " + this.getClass());
		MTTForm reportForm = (MTTForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
	
		} catch (Exception e) {
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		// return null;
		return null;
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
