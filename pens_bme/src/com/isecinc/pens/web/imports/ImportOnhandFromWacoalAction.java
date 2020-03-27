package com.isecinc.pens.web.imports;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.MasterBean;
import com.isecinc.pens.bean.Message;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class ImportOnhandFromWacoalAction {
	
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	public ActionForward importOhhandLotusFromWacoal(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportForm importForm = (ImportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try{
			//clear results
			importForm.setSummaryErrorList(null);
			importForm.setSummarySuccessList(null);
				  
			importForm.setTotalSize(0);
			importForm.setSummaryWacoalListErrorSize(0);
		    importForm.setSummaryWacoalListSuccessSize(0);
		    
		    importForm.setSummaryBoxErrorList(null);
			importForm.setSummaryBoxSuccessList(null);
				  
			importForm.setTotalBoxSize(0);
			importForm.setSummaryWacoalBoxListErrorSize(0);
		    importForm.setSummaryWacoalBoxListSuccessSize(0);
			
			//1 import txt onhand from wacoal
			importForm = importOhhandLotusFromWacoalModel(importForm, user, request);
			
			//2 import txt total box from wacoal
			importForm = importTotalBoxFromWacoalModel(importForm, user, request);
	   }catch(Exception e){
		   request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		   logger.error(e.getMessage(),e);
	   }
	   return mapping.findForward("success");
	}
	
	public ImportForm importOhhandLotusFromWacoalModel(ImportForm importForm,User user,HttpServletRequest request)  throws Exception {
		logger.debug("Import txt onhand from wacoal:Excel");
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    PreparedStatement psBmeLocked = null;
	    PreparedStatement psDelete = null;
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
		StringBuffer sql = new StringBuffer("");
		 BigDecimal onhandQtyValid =bigZero;
		try {
			request.setAttribute("Message","");//clear message disp
			
			String noCheckError = Utils.isNull(request.getParameter("NO_CHECK_ERROR"));
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            
			FormFile dataFile = importForm.getDataFile();
			//logger.debug("dataFile:"+dataFile+",fileName:"+Utils.isNull(dataFile.getFileName()));
			String fileName = "";
			java.util.Date fileNameAsOfDate = null;
			if( !Utils.isNull(dataFile.getFileName()).equals("")){
				/** Step 1 Validate Name Date of File ,Must more than old import  **/
				fileName = dataFile.getFileName();
				fileNameAsOfDate = DateUtil.parse(fileName.substring(9,17),DateUtil.YYYY_MM_DD_WITHOUT_SLASH);
				// Get LastFileNameImport 
				String lastFileNameImport = importDAO.getLastFileNameImport(conn);
				if( !Utils.isNull(lastFileNameImport).equals("")){
					java.util.Date lastFileNameAsOfDate = DateUtil.parse(lastFileNameImport.substring(9,17),DateUtil.YYYY_MM_DD_WITHOUT_SLASH);
					if(fileNameAsOfDate.before(lastFileNameAsOfDate)){ //dateImport < lastDateImport
						request.setAttribute("Message","ชื่อไฟล์ที่  Upload ["+fileName+"] วันที่น้อยกว่า  ชื่อไฟล์วันที่ล่าสุดที่  Upload ["+lastFileNameImport+"] ");
						//return mapping.findForward("success");
						importError = true;
					}
				}
			}
			
			if (importError == false && !Utils.isNull(dataFile.getFileName()).equals("")) {
			  logger.debug("contentType: " + dataFile.getContentType());
			  logger.debug("fileName: " + dataFile.getFileName());

			  /** Delete All before Import **/
			  psDelete = conn.prepareStatement("delete from PENSBME_ONHAND_BME");
			  psDelete.executeUpdate();
	
			  sql.append("INSERT INTO PENSBME_ONHAND_BME( \n");
			  sql.append(" AS_OF_DATE, MATERIAL_MASTER, BARCODE, \n");
			  sql.append(" ONHAND_QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,  \n");
			  sql.append(" ITEM, ITEM_DESC, CREATE_DATE, CREATE_USER,FILE_NAME,GROUP_ITEM,STATUS,MESSAGE,PENS_ITEM) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?,?,?,?)");
			  
			  ps = conn.prepareStatement(sql.toString());
			  
			  sql = new StringBuffer("");
			  sql.append("INSERT INTO PENSBME_ONHAND_BME_LOCKED( \n");
			  sql.append(" AS_OF_DATE, MATERIAL_MASTER, BARCODE, \n");
			  sql.append(" ONHAND_QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,  \n");
			  sql.append(" ITEM, ITEM_DESC, CREATE_DATE, CREATE_USER,FILE_NAME,GROUP_ITEM,STATUS,MESSAGE,PENS_ITEM) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?,?,?,?)");
			  
			  psBmeLocked = conn.prepareStatement(sql.toString());
			  
			  String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "tis-620");
			  //String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "UTF-8");
			  
			  //replace '?' in index =0
			  //dataFileStr = dataFileStr.substring(1,dataFileStr.length());
			  logger.debug("dataFileStr:"+dataFileStr);
			  
	    	  String[] dataStrArray = dataFileStr.split("\n");

		      int start = 0;
		      int end = 0;
		      String lines = "";
		      String materialMaster = "";
		      String barcode = "";
		      String onhandQty = "";
		      String onhandQty2Digit = "";
		      String wholePriceBF = "";
		      String wholePriceBF2Digit = "";
		      String retailPriceBF = "";
		      String retailPriceBF2Digit = "";
		      String item  ="";
		      String itemDesc= "";
		      String groupItem = "";
		      String status = "";
		      String message ="";
		      String pensItem = "";
		      
		     for(int i=0;i<dataStrArray.length;i++){
		    	 lineError = false;
		    	 errorMsgList = new ArrayList<Message>();
		    	 lines = dataStrArray[i];
		    	 //logger.debug("lines:"+lines);
		         if(!Utils.isNull(lines).equals("")){
		        	try{
				         // 1
			        	 start = 0;
				         end = 18;
				         materialMaster = lines.substring(start,end);
				         //logger.debug("materialMaster["+materialMaster+"]length["+materialMaster.length()+"]");
				         
				         //2
				         start = end;
				         end = start+13;
				         barcode = lines.substring(start,end);
				        // logger.debug("barcode["+barcode+"]length["+barcode.length()+"]");

				         //3
				         start = end;
				         end = start+11;
				         onhandQty = lines.substring(start,end-2);
				         onhandQty2Digit = lines.substring(end-2,end);
				         //logger.debug("onhandQty["+onhandQty+"]onhandQty2Digit["+onhandQty2Digit+"]length["+onhandQty.length()+"]");
				         
				         //4
				         start = end;
				         end = start+11;
				         wholePriceBF = lines.substring(start,end-2);
				         wholePriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("wholePriceBF["+wholePriceBF+"]wholePriceBF2Digit["+wholePriceBF2Digit+"]length["+wholePriceBF.length()+"]");
				         
				         //5
				         start = end;
				         end = start+11;
				         retailPriceBF = lines.substring(start,end-2);
				         retailPriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("retailPriceBF["+retailPriceBF+"]retailPriceBF2Digit["+retailPriceBF2Digit+"]length["+retailPriceBF.length()+"]");
				         
				         //6
				         start = end;
				         end = start+35;
				         item = lines.substring(start,end);
				        // logger.debug("item["+item+"]length["+item.length()+"]");
				         
				         //7
				         start = end;
				         end = start+40;
				         itemDesc = lines.substring(start,end);
				         //logger.debug("itemDesc["+itemDesc+"]length["+itemDesc.length()+"]");
				         
				         /** Prepare Message To Display **/
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         OnhandSummary oh = new OnhandSummary();
				         oh.setAsOfDate(DateUtil.stringValue(fileNameAsOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				         oh.setItem(Utils.isNull(item));
				         oh.setItemDesc(Utils.isNull(itemDesc));
				         oh.setOnhandQty(Utils.isNull(onhandQty+onhandQty2Digit));
				         oh.setWholePriceBF(Utils.isNull(wholePriceBF+wholePriceBF2Digit));
				         oh.setRetailPriceBF(Utils.isNull(retailPriceBF+retailPriceBF2Digit));
				         oh.setBarcode(Utils.isNull(barcode));
				         oh.setMaterialMaster(Utils.isNull(materialMaster));
				         //Find pens_item **/
				         pensItem = importDAO.getItemByBarcode(conn,Constants.STORE_TYPE_LOTUS_ITEM, oh.getBarcode());
				         oh.setPensItem(Utils.isNull(pensItem));
				         
				         s.setOnhandSummary(oh);
				         
				         /** Case Onhand Qty == 0 No validate **/
				         onhandQtyValid = new BigDecimal(onhandQty+"."+onhandQty2Digit);
				         if(onhandQtyValid.compareTo(bigZero) != 0 ){
				         
					         /** Validate Barcode **/
					         MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_LOTUS_ITEM, barcode);
					         String itemCodeValid = mb!=null?mb.getItem():"";
					         groupItem = mb!=null?mb.getGroup():"";
					         
					         logger.debug("itemCodeValid["+itemCodeValid+"]");
					         if(Utils.isNull(itemCodeValid).equals("")){
					        	 Message m = new Message();
					        	 m.setMessage("ไม่พบข้อมูล Barcode");
					        	 errorMsgList.add(m);
					        	 lineError = true;
					         }
					         
					         /** Validate ItemCodeOracle VS ItemCodeWacoal IS Equals**/
					         /*if( !Utils.isNull(itemCodeValid).equals("")){
						         if( !Utils.isNull(itemCodeValid).equals(Utils.isNull(item))){
						        	 Message m = new Message();
						        	 m.setMessage("ข้อมูล Item Wacoal["+Utils.isNull(item)+"] กับ Item Oracle["+Utils.isNull(itemCodeValid)+"] ไม่ตรงกัน");
						        	 errorMsgList.add(m);
						        	 lineError = true;
						         }
					         }*/
					         
					         /** Validate WholePriceBF **/
					         BigDecimal wholePriceBFOracle = importDAO.getWholePriceBFFromOracle(conn, itemCodeValid);
					    
					         if(wholePriceBFOracle.compareTo(bigZero) ==0){//== 0
					        	 //Not found Add Fail Msg
					        	 Message m = new Message();
						         m.setMessage("ไม่พบข้อมูลราคาขายส่งนี้ในระบบ Oracle");
						         errorMsgList.add(m);
						         lineError = true;
					         }else if( wholePriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 BigDecimal wholePriceBFWacoal =  new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit);
					        	
					        	 if( wholePriceBFWacoal.compareTo(bigZero) != 0 &&  wholePriceBFOracle.compareTo(wholePriceBFWacoal) != 0){
					        		 Message m = new Message();
							         m.setMessage("ราคาขายส่ง  Wacoal["+wholePriceBFWacoal+"] กับ  Oracle["+wholePriceBFOracle+"] ไม่ตรงกัน");
							         errorMsgList.add(m);
							         lineError = true;
					        	 }
					         }
					         
					         /** Validate RetailPriceBF **/
					         //BigDecimal retailPriceBFOracle = importDAO.getRetailPriceBFFromOracle(conn, itemCodeValid);
					    
					         //if(retailPriceBFOracle.compareTo(bigZero) ==0){ // =0
					        	 //Not found Add Fail Msg
					        	 //Message m = new Message();
						         //m.setMessage("ไม่พบข้อมูลราคาขายปลีกนี้ในระบบ Oracle");
						         //errorMsgList.add(m);
						         //lineError = true;
					        // }else if( retailPriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 //BigDecimal reatilPriceBFWacoal =  new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit);
					        	 //if(reatilPriceBFWacoal.compareTo(bigZero) != 0 && retailPriceBFOracle.compareTo(reatilPriceBFWacoal) != 0){
					        		 //Message m = new Message();
							         //m.setMessage("ราคาขายปลีก  Wacoal["+reatilPriceBFWacoal+"] กับ Oracle["+retailPriceBFOracle+"] ไม่ตรงกัน");
							         //errorMsgList.add(m);
							         //lineError = true;
					        	 //}
					         //}
					         
				         }else{//if onhand qty != 0
				        	 //Case QTY =0  no validate item
				        	 MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_LOTUS_ITEM, barcode);
					         groupItem = mb!=null?mb.getGroup():"";
				         }
				         
				         message = "";
				         
				         if(lineError){
				        	 importError = true;
				        	 s.setErrorMsgList(errorMsgList);
				        	 errorList.add(s);

				        	 /** set Message TO Save **/
				        	 for(int e=0;e<errorMsgList.size();e++){
				        		 Message me = (Message)errorMsgList.get(e);
				        		 message += me.getMessage()+",";
				        	 }
				         }else{
				        	 Message m = new Message();
				        	 m.setMessage("Success");
				        	 errorMsgList.add(m);
				        	 successList.add(s);
				         }
				         
				         //Check Status and Message
				         status = lineError?"ERROR":"SUCCESS";
				         
				         //Line No Error
				         if(lineError==false || "NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
					         ps.setDate(1, new java.sql.Date(fileNameAsOfDate.getTime()));
					         ps.setString(2, Utils.isNull(materialMaster));
					         ps.setString(3, Utils.isNull(barcode));
					         ps.setBigDecimal(4, new BigDecimal(onhandQty+"."+onhandQty2Digit));
					         ps.setBigDecimal(5, new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit));
					         ps.setBigDecimal(6, new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit));
					         ps.setString(7, Utils.isNull(item));
					         ps.setString(8, Utils.isNull(itemDesc));
					         ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
					         ps.setString(10, user.getUserName());
					         ps.setString(11, fileName);
					         ps.setString(12, groupItem);
					         ps.setString(13, status);
					         ps.setString(14, message);
					         ps.setString(15, pensItem);
					         
					         allCount++;
					         
					         ps.executeUpdate();
					         
					        // logger.debug("mat["+materialMaster+"]onhandQty["+onhandQtyValid+"]");
					         
					         //Case onhandQty ==0 no insert bme_locked
					         if(onhandQtyValid.compareTo(bigZero) != 0 ){
						         //Check barcodeExist BMELocked
						         if( !GeneralDAO.isExistBarcodeInBMELocked(conn, barcode,materialMaster,groupItem,pensItem)){
						        	 //insert New Barcode BMELOCKED
						        	 psBmeLocked.setDate(1, new java.sql.Date(fileNameAsOfDate.getTime()));
						        	 psBmeLocked.setString(2, Utils.isNull(materialMaster));
						        	 psBmeLocked.setString(3, Utils.isNull(barcode));
						        	 psBmeLocked.setBigDecimal(4, new BigDecimal("0"));
						        	 psBmeLocked.setBigDecimal(5, new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit));
						        	 psBmeLocked.setBigDecimal(6, new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit));
						        	 psBmeLocked.setString(7, Utils.isNull(item));
						        	 psBmeLocked.setString(8, Utils.isNull(itemDesc));
						        	 psBmeLocked.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
						        	 psBmeLocked.setString(10, user.getUserName());
						        	 psBmeLocked.setString(11, fileName);
						        	 psBmeLocked.setString(12, groupItem);
						        	 psBmeLocked.setString(13, status);
						        	 psBmeLocked.setString(14, message);
						        	 psBmeLocked.setString(15, pensItem);
						        	 
						        	 psBmeLocked.executeUpdate();
						         }//if
					         }//if
				         }
					  }catch(Exception e){
					     failCount++;
					     logger.error(e.getMessage(),e);
					     importError=true;
					  }
		         }//if
		      }//while

		     importForm.setSummaryErrorList(errorList);
			 importForm.setSummarySuccessList(successList);
			  
			 importForm.setTotalSize(errorList.size()+successList.size());
			 importForm.setSummaryWacoalListErrorSize(errorList!=null?errorList.size():0);
			 importForm.setSummaryWacoalListSuccessSize(successList!=null?successList.size():0);
		     
			 if("NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
				 request.setAttribute("Message","Upload ไฟล์ (Onhand Wacoal)"+fileName+"สำเร็จ โดย นำข้อมูลที่ Error เข้าทั้งหมด"); 
				 conn.commit();
			 }else{
				 if(importError){
					request.setAttribute("Message","Upload ไฟล์(Onhand Wacoal) "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
					logger.debug("Transaction Rollback");
					  
					   if( "".equalsIgnoreCase(noCheckError)){
				         conn.rollback();
					   }
					}else{
					   request.setAttribute("Message","Upload ไฟล์(Onhand Wacoal) "+fileName+" สำเร็จ");
					   logger.debug("Transaction Commit");
					   conn.commit();
					}
				 }
			}//if
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์(Onhand Wacoal)ไม่ถูกต้อง:"+e.toString());
		}finally{
		    	// dispose all the resources after using them.
			      if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
		    }
		return importForm;
	}
	
	public ImportForm importTotalBoxFromWacoalModel(ImportForm importForm,User user,HttpServletRequest request)  throws Exception {
		logger.debug("Import txt total box from wacoal:Excel");
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
		StringBuffer sql = new StringBuffer("");
		try {
			request.setAttribute("Message2","");//clear message disp
			
			String noCheckError = Utils.isNull(request.getParameter("NO_CHECK_ERROR"));
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            
			FormFile dataFile = importForm.getDataFile2();
			if (importError == false && !Utils.isNull(dataFile.getFileName()).equals("")) {
			  
			  logger.debug("contentType: " + dataFile.getContentType());
			  logger.debug("fileName: " + dataFile.getFileName());

			  /** Delete All before Import **/
			  //psDelete = conn.prepareStatement("delete from PENSBI.PENSBME_BOX_BYWACOAL");
			  //psDelete.executeUpdate();
	
			  sql.append("INSERT INTO PENSBI.PENSBME_BOX_BYWACOAL( \n");
			  sql.append(" STORE_CODE, STORE_NAME, LOT_NO, \n");
			  sql.append(" LOT_DATE, TOTAL_BOX, FORWARDER,  \n");
			  sql.append(" PROVINCE, REF_ID,SEND_DATE,FILE_NAME, CREATE_DATE, CREATE_USER) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?)");
			  
			  ps = conn.prepareStatement(sql.toString());
			  
			  String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "UTF-8");
			  //replace '?' in index =0
			  //dataFileStr = dataFileStr.substring(1,dataFileStr.length());
			  logger.debug("dataFileStr:"+dataFileStr);
			  
	    	  String[] dataStrArray = dataFileStr.split("\n");

		      String[] lines = null;
		      String storeCode = "";
		      String storeName = "";
		      String lotNo = "";
		      String lotDate = "";
		      String totalBox = "";
		      String forwarder = "";
		      String province = "";
		      String refId = "";
		      String sendDate = "";
		      String message = "";
		      String status = "";
		      int index = 0;
		      boolean isLotNoExist = false;
		     for(int i=0;i<dataStrArray.length;i++){
		    	 lines = null;
		    	 lineError = false;
		    	 index = 0;
		    	 errorMsgList = new ArrayList<Message>();
		    	 if(i >1){
		    	   lines = dataStrArray[i].split("\\|");
		    	   logger.debug("lines:"+lines.toString());
		    	 }
		         if( !Utils.isNull(dataStrArray[i]).equals("") && lines != null){
		        	try{
				         storeCode = Utils.isNull(lines[0]);
				         storeName = Utils.isNull(lines[1]);
				         lotNo = Utils.isNull(lines[2]);
				         lotDate = Utils.isNull(lines[3]);
				         totalBox = Utils.isNull(lines[4]);
				         forwarder = Utils.isNull(lines[5]);
				         province = Utils.isNull(lines[6]);
				         refId = Utils.isNull(lines[7]);
				         sendDate = Utils.isNull(lines[8]);
				         
				         logger.debug("storeCode:"+storeCode);
				         logger.debug("storeName:"+storeName);
				         logger.debug("lotNo:"+lotNo);
				         logger.debug("lotDate:"+lotDate);
				         logger.debug("totalBox:"+totalBox);
				         logger.debug("forwarder:"+forwarder);
				         logger.debug("province:"+province);
				         logger.debug("refId:"+refId);
				         logger.debug("sendDate:"+sendDate);
				         
				         /** Prepare Message To Display **/
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         OnhandSummary oh = new OnhandSummary();
				         oh.setStoreCode(storeCode);
				         oh.setStoreName(storeName);
				         oh.setLotNo(lotNo);
				         oh.setLotDate(lotDate);
				         oh.setTotalBox(totalBox);
				         oh.setForwarder(forwarder);
				         oh.setProvince(province);
				         oh.setRefId(refId);
				         oh.setSendDate(sendDate);
				         s.setOnhandSummary(oh);
				        
				         message = "";
				         lineError = false;
				         /** Validate LotNo is Exist **/
				         isLotNoExist= isLotNoExist(conn, oh.getLotNo());
				    
				         if(isLotNoExist==true){//== 0
				        	 //Not found Add Fail Msg
				        	 Message m = new Message();
					         m.setMessage("LotNo นี้ มีในระบบแล้ว");
					         errorMsgList.add(m);
					         lineError = true;
				         }
				         
				         if(lineError){
				        	 importError = true;
				        	 s.setErrorMsgList(errorMsgList);
				        	 errorList.add(s);

				        	 /** set Message TO Save **/
				        	 for(int e=0;e<errorMsgList.size();e++){
				        		 Message me = (Message)errorMsgList.get(e);
				        		 message += me.getMessage()+",";
				        	 }
				         }else{
				        	 Message m = new Message();
				        	 m.setMessage("Success");
				        	 errorMsgList.add(m);
				        	 successList.add(s);
				         }
				         
				         //Check Status and Message
				         status = lineError?"ERROR":"SUCCESS";
				         
				         //Line No Error
				         if(lineError==false || "NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
				        	 ps.setString(++index,storeCode);
				        	 ps.setString(++index,storeName);
				        	 ps.setString(++index,lotNo);
					         ps.setDate(++index, new java.sql.Date(DateUtil.parse(lotDate, "yyyy-MM-dd").getTime()));
					         ps.setBigDecimal(++index, new BigDecimal(totalBox));
					         ps.setString(++index,forwarder);
					         ps.setString(++index,province);
					         ps.setString(++index,refId);
					         ps.setDate(++index, new java.sql.Date(DateUtil.parse(sendDate, "yyyy-MM-dd").getTime()));
					         ps.setString(++index, dataFile.getFileName());
					         ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
					         ps.setString(++index ,user.getUserName());
					         
					         allCount++;
					         ps.executeUpdate();
				         }//if check error
					  }catch(Exception e){
					     failCount++;
					     logger.error(e.getMessage(),e);
					     importError=true;
					  }
		         }//if
		      }//for

		     importForm.setSummaryBoxErrorList(errorList);
			 importForm.setSummaryBoxSuccessList(successList);
			  
			 importForm.setTotalBoxSize(errorList.size()+successList.size());
			 importForm.setSummaryWacoalBoxListErrorSize(errorList!=null?errorList.size():0);
			 importForm.setSummaryWacoalBoxListSuccessSize(successList!=null?successList.size():0);
		     
			 if("NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
				 request.setAttribute("Message2","Upload ไฟล์ (สรุปจำนวนกล่อง)"+dataFile.getFileName()+"สำเร็จ โดย นำข้อมูลที่ Error เข้าทั้งหมด"); 
				 conn.commit();
			 }else{
				 if(importError){
					request.setAttribute("Message2","Upload ไฟล์ (สรุปจำนวนกล่อง)"+dataFile.getFileName()+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
					logger.debug("Transaction Rollback");
					  
					   if( "".equalsIgnoreCase(noCheckError)){
				         conn.rollback();
					   }
					}else{
					   request.setAttribute("Message2","Upload ไฟล์ (สรุปจำนวนกล่อง)"+dataFile.getFileName()+" สำเร็จ");
					   logger.debug("Transaction Commit");
					   conn.commit();
					}
				 }
			}//if
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message2","ข้อมูลไฟล์(สรุปจำนวนกล่อง)ไม่ถูกต้อง:"+e.toString());
		}finally{
		    // dispose all the resources after using them.
		      if(conn != null){
		    	 conn.close();conn=null;
		      }
		      if(ps != null){
		    	 ps.close();ps=null;
		      }
		    }
		return importForm;
	}
	
	public static boolean isLotNoExist(Connection conn,String lotNo) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        boolean r = false;
		try {
			sql.append("\n select count(*) as c FROM PENSBME_BOX_BYWACOAL WHERE LOT_NO ='"+Utils.isNull(lotNo)+"' ");
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				if(rst.getInt("c")>0) r=true;
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return r;
	}
}
