package com.isecinc.pens.web.imports.sub;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.MasterBean;
import com.isecinc.pens.bean.Message;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.imports.ImportForm;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class ImportOnhandPensShop {
	
	protected static Logger logger = Logger.getLogger("PENS");
	
	public ActionForward importData(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("import7CatalogFromWacoal :Text");
		ImportForm importForm = (ImportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
		try {
			String noCheckError = Utils.isNull(request.getParameter("NO_CHECK_ERROR"));
			 
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
			FormFile dataFile = importForm.getDataFile();
			
			/** Step 1 Validate Name Date of File ,Must more than old import  **/
			//PENSHSOP_OH_20130923.txt
			//SEVENCAT
			//LOTUS
			String fileName = dataFile.getFileName();
			logger.debug("dateSubStr:"+fileName.substring(12,20));
			
			java.util.Date fileNameAsOfDate = Utils.parse(fileName.substring(12,20),Utils.YYYY_MM_DD_WITHOUT_SLASH);
			// Get LastFileNameImport 
			String lastFileNameImport = importDAO.getLastFileNameImport7Catalog(conn);
			if( !Utils.isNull(lastFileNameImport).equals("")){
				java.util.Date lastFileNameAsOfDate = Utils.parse(lastFileNameImport.substring(12,20),Utils.YYYY_MM_DD_WITHOUT_SLASH);
				
				if(fileNameAsOfDate.before(lastFileNameAsOfDate)){ //dateImport < lastDateImport
					request.setAttribute("Message","ชื่อไฟล์ที่  Upload ["+fileName+"] วันที่น้อยกว่า  ชื่อไฟล์วันที่ล่าสุดที่  Upload ["+lastFileNameImport+"] ");
					return mapping.findForward("success");
				}
			}
			
			if (dataFile != null) {
			  logger.debug("contentType: " + dataFile.getContentType());
			  logger.debug("fileName: " + dataFile.getFileName());

			  /** Delete All before Import **/
			  psDelete = conn.prepareStatement("delete from PENSBME_ONHAND_BME_7CATALOG");
			  psDelete.executeUpdate();
	
			  StringBuffer sql = new StringBuffer("");
			  sql.append("INSERT INTO PENSBME_ONHAND_BME_7CATALOG( \n");
			  sql.append(" AS_OF_DATE, MATERIAL_MASTER, BARCODE, \n");
			  sql.append(" ONHAND_QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,  \n");
			  sql.append(" ITEM, ITEM_DESC, CREATE_DATE, CREATE_USER,FILE_NAME,GROUP_ITEM,STATUS,MESSAGE,PENS_ITEM) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?,?,?,?)");
			  
			  ps = conn.prepareStatement(sql.toString());
			  
			  String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "tis-620");
			  //replace '?' in index =0
			 // dataFileStr = dataFileStr.substring(1,dataFileStr.length());
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
		    	 logger.debug("lines:"+lines);
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
				         oh.setAsOfDate(Utils.stringValue(fileNameAsOfDate, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				         oh.setItem(Utils.isNull(item));
				         oh.setItemDesc(Utils.isNull(itemDesc));
				         oh.setOnhandQty(Utils.isNull(onhandQty+onhandQty2Digit));
				         oh.setWholePriceBF(Utils.isNull(wholePriceBF+wholePriceBF2Digit));
				         oh.setRetailPriceBF(Utils.isNull(retailPriceBF+retailPriceBF2Digit));
				         oh.setBarcode(Utils.isNull(barcode));
				         oh.setMaterialMaster(Utils.isNull(materialMaster));
				         //Find pens_item **/
				         pensItem = importDAO.getItemByBarcode(conn,Constants.STORE_TYPE_7CATALOG_ITEM, oh.getBarcode());//
				         oh.setPensItem(Utils.isNull(pensItem));
				         
				         s.setOnhandSummary(oh);
				         
				         /** Case Onhand Qty == 0 No validate **/
				         BigDecimal onhandQtyValid = new BigDecimal(onhandQty+"."+onhandQty2Digit);
				         if(onhandQtyValid.compareTo(bigZero) != 0 ){
				         
					         /** Validate Barcode **/
					         MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_7CATALOG_ITEM, barcode);
					         String itemCodeValid = mb!=null?mb.getItem():"";
					         groupItem = mb!=null?mb.getGroup():"";
					         
					        // logger.debug("itemCodeValid["+itemCodeValid+"]");
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
					        /* BigDecimal wholePriceBFOracle = importDAO.getWholePriceBFFromOracle(conn, itemCodeValid);
					    
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
					         }*/
					         
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
				        	 MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_7CATALOG_ITEM, barcode);
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
				         }
				         
					  }catch(Exception e){
					     failCount++;
					     e.printStackTrace();
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
				 request.setAttribute("Message","Upload ไฟล์ "+fileName+"สำเร็จ โดย นำข้อมูลที่ Error เข้าทั้งหมด"); 
				 conn.commit();
			 }else{
				 if(importError){
					  request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
					  logger.debug("Transaction Rollback");
					  
					  if( "".equalsIgnoreCase(noCheckError))
				         conn.rollback();
					}else{
					   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
					   logger.debug("Transaction Commit");
					   conn.commit();
					}
				 }
			}//if
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
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
		return mapping.findForward("success");
	}

}
