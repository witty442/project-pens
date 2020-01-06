package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.external.process.ControlInterfaceICC;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class GenerateHISHER extends InterfaceUtils{
	private static Logger logger = Logger.getLogger("PENS");
	public final static String PARAM_FILE_NAME = "FILE_NAME";
	public final static String PARAM_OUTPUT_PATH = "OUTPUT_PATH";
	public final static String PARAM_CUST_GROUP = "CUST_GROUP";
	public final static String PARAM_TRANS_DATE = "TRANS_DATE";//Budish Date
	public final static String PARAM_PRODUCT_TYPE = "PRODUCT_TYPE";
	
	public static MonitorItemBean runProcess(User user,MonitorItemBean monitorItemBean,Map<String, String> batchParamMap) throws Exception{
		EnvProperties env = EnvProperties.getInstance();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		int result = Constants.STATUS_FAIL;
		boolean nextStep = true;
		boolean foundData = false;
		int successCount = 0;
		int failCount = 0;
	    Map<String, String> configMap = new HashMap<String, String>();
		//Field
		String barcode ="";
		String groupCode = "";
		String pensItem = "";
		String productCode = "";
		int qty = 0;
		double wholePriceBF = 0;
		double retailPriceBF = 0;
		StringBuffer outputLine = new StringBuffer("");
		StringBuffer outputFile = new StringBuffer("");
		try{
			//Prepare parameter
			Date date = DateUtil.parse(batchParamMap.get(PARAM_TRANS_DATE), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String productType = Utils.isNull(batchParamMap.get(PARAM_PRODUCT_TYPE));
			
			//Update Transaction Type by product;
			ControlInterfaceICC.updateTransInterfaceICC(productType);
			
			//Prepare FTP Manager **/
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.icc.ip.server"), env.getProperty("ftp.icc.username"), env.getProperty("ftp.icc.password"));
			ftpManager.canConnectFTPServer();
			
		
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//Get Config Map ALL
			configMap = InterfaceUtils.getConfigInterfaceAllBySubject(conn, "PENSTOCK",productType);
			sql.append("\n SELECT A.barcode, A.group_code ,A.product_code ");
			sql.append("\n , NVL(sum(A.qty),0) as qty ,A.WHOLE_PRICE_BF, A.RETAIL_PRICE_BF ");
			sql.append("\n FROM( ");
			sql.append("\n 		SELECT O.barcode, O.group_code " );
			sql.append("\n      ,( select max(M.interface_value) from PENSBME_MST_REFERENCE  M");
			sql.append("\n        WHERE  O.barcode = M.interface_desc     ");
			sql.append("\n        AND M.reference_code = 'LotusItem' )  as product_code ");
			sql.append("\n      , NVL(sum(O.qty),0) as qty ");
			sql.append("\n 		, NVL(P.WHOLE_PRICE_BF,0) as WHOLE_PRICE_BF, NVL(P.RETAIL_PRICE_BF,0) as RETAIL_PRICE_BF ");
			sql.append("\n 		FROM PENSBME_ORDER O  ");
			sql.append("\n 		LEFT OUTER JOIN PENSBME_PRICELIST P   ");
			sql.append("\n   		ON  P.group_code = O.group_code ");
			sql.append("\n   		AND P.STORE_TYPE ='"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n   		AND P.product ='"+productType+"'");
			sql.append("\n 		where O.store_type = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		and O.order_date = ?  ");
			sql.append("\n 		and  ( (O.exported <> 'Y' and O.exported <> 'G') or O.exported is null)	 ");
			sql.append("\n 		group by O.barcode, O.group_code,P.WHOLE_PRICE_BF,P.RETAIL_PRICE_BF ");
			
			sql.append("\n 		UNION ALL ");
			
			/** Stock_ISSUE **/
			sql.append("\n 		SELECT I.barcode, I.group_code" );
			sql.append("\n      ,( select max(M.interface_value) from PENSBME_MST_REFERENCE  M");
			sql.append("\n        WHERE  I.barcode = M.interface_desc     ");
			sql.append("\n        AND M.reference_code = 'LotusItem')  as product_code ");
			sql.append("\n      , NVL(sum(I.issue_qty),0) as qty ");
			sql.append("\n 		, NVL(P.WHOLE_PRICE_BF,0) as WHOLE_PRICE_BF, NVL(P.RETAIL_PRICE_BF,0) as RETAIL_PRICE_BF ");
			sql.append("\n 		from PENSBME_STOCK_ISSUE H ,PENSBME_STOCK_ISSUE_ITEM I  ");
			sql.append("\n 		LEFT OUTER JOIN PENSBME_PRICELIST P   ");
			sql.append("\n 		ON P.group_code = I.group_code ");
			sql.append("\n   		AND P.STORE_TYPE ='"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n   		AND P.product ='"+productType+"'");
			sql.append("\n 		WHERE 1=1 ");
			sql.append("\n 		and H.ISSUE_REQ_NO = I.ISSUE_REQ_NO ");
			sql.append("\n 		and H.STATUS = '"+PickConstants.STATUS_ISSUED+"'");
			sql.append("\n 		and H.cust_group = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		and H.delivery_date = ?  ");
			sql.append("\n 		and  ( (H.exported <> 'Y' and H.exported <> 'G') or H.exported is null)	 ");
			sql.append("\n 		group by I.barcode, I.group_code,P.WHOLE_PRICE_BF,P.RETAIL_PRICE_BF ");
			
			/** Pick Stock edit :14/11/2019 **/
			sql.append("\n 		UNION ALL ");
			
			sql.append("\n 		SELECT " );
			sql.append("\n      ( select M.interface_desc from PENSBME_MST_REFERENCE M");
			sql.append("\n        WHERE  I.pens_item = M.pens_value ");
			sql.append("\n        AND  I.material_master = M.interface_value ");
			sql.append("\n        AND M.reference_code = 'LotusItem')  as barcode ");
			sql.append("\n 		, I.group_code,I.material_master as product_code" );
			sql.append("\n      , count(*) as qty ");
			sql.append("\n 		, NVL(P.WHOLE_PRICE_BF,0) as WHOLE_PRICE_BF, NVL(P.RETAIL_PRICE_BF,0) as RETAIL_PRICE_BF ");
			sql.append("\n 		from PENSBME_PICK_STOCK H ,PENSBME_PICK_STOCK_I I  ");
			sql.append("\n 		LEFT OUTER JOIN PENSBME_PRICELIST P   ");
			sql.append("\n 		ON P.group_code = I.group_code ");
			sql.append("\n   		AND P.STORE_TYPE ='"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n   		AND P.product ='"+productType+"'");
			sql.append("\n 		WHERE 1=1 ");
			sql.append("\n 		and H.ISSUE_REQ_NO = I.ISSUE_REQ_NO ");
			sql.append("\n 		and H.ISSUE_REQ_STATUS = '"+PickConstants.STATUS_ISSUED+"'");
			sql.append("\n 		and H.cust_group = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		and H.delivery_date = ?  ");
			sql.append("\n 		and  ( (H.exported <> 'Y' and H.exported <> 'G') or H.exported is null)	 ");
			sql.append("\n 		group by I.pens_item,I.material_master, I.group_code,P.WHOLE_PRICE_BF,P.RETAIL_PRICE_BF ");
			
			sql.append("\n )A ");
			sql.append("\n group by A.barcode, A.group_code, A.product_code,A.WHOLE_PRICE_BF,A.RETAIL_PRICE_BF ");
			sql.append("\n order by A.group_code ,A.product_code ");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setDate(1, new java.sql.Date(date.getTime()));
			ps.setDate(2, new java.sql.Date(date.getTime()));
			ps.setDate(3, new java.sql.Date(date.getTime()));
			rs = ps.executeQuery();
			while(rs.next()){
				foundData = true;
				
				barcode = Utils.isNull(rs.getString("barcode"));
				groupCode = Utils.isNull(rs.getString("group_code"));
				//pensItem = Utils.isNull(rs.getString("pens_item"));
				productCode = Utils.isNull(rs.getString("product_code"));//Get From MST
				qty = rs.getInt("qty");
				wholePriceBF  = rs.getDouble("WHOLE_PRICE_BF");
				retailPriceBF =  rs.getDouble("RETAIL_PRICE_BF");
				
				//validate MST 
				if("".equals(productCode)){
					nextStep = false;
					result = Constants.STATUS_FAIL;
					failCount++;
					
					//insert result msg
					insertMonitorItemResult(conn,monitorItemBean.getId(),"FAIL","GROUP CODE["+groupCode+"] ,BARCODE["+barcode+"] ,InterfaceValue["+productCode+"] ,QTY["+qty+"] ไม่พบข้อมูลใน PENSBME_MST_REFERENCE");
				}else{
					//Validate Price List
					if(wholePriceBF==0 || retailPriceBF ==0){
						failCount++;
						nextStep = false;
					   //insert result msg
					   insertMonitorItemResult(conn,monitorItemBean.getId(),"FAIL","GROUP CODE["+groupCode+"] ,BARCODE["+barcode+"] ,InterfaceValue["+productCode+"] ,QTY["+qty+"] ไม่พบข้อมูลใน PENSBME_PRICELIST");
					   
					}else{
						 //Gen Line
						outputLine.append(genLine(configMap,groupCode,barcode,pensItem,productCode,qty,wholePriceBF,retailPriceBF));
					     
						successCount++;
						//insert result msg
						insertMonitorItemResult(conn,monitorItemBean.getId(),"SUCCESS","GROUP CODE["+groupCode+"] ,BARCODE["+barcode+"] ,InterfaceValue["+productCode+"] ,QTY["+qty+"] ");
					}
				}
	
			}//while
			
			/** Case Found Error No Gen File **/
			if(nextStep && foundData){
				 result = Constants.STATUS_SUCCESS;
				 /** Add number of line **/
				 outputFile.append(appendNumLeft(String.valueOf(successCount),"0",10)+"\n");
				 outputFile.append(outputLine);
				 
			     //write file
			     String fileName = env.getProperty("path.icc.hisher.export.txt")+"/"+batchParamMap.get(PARAM_FILE_NAME);
			    
			     
			 	logger.debug("Step Upload ALL File To FTP Server");
				ftpManager.uploadAllFileToFTP_OPT2_BY_FILE(env.getProperty("path.icc.hisher.export.txt"),fileName, outputFile);
			     
				//Backup file to DD Server
				String ddServerPath =env.getProperty("path.backup.icc.hisher.export.txt")+"/"+batchParamMap.get(PARAM_FILE_NAME);
			    logger.debug("Backup Text File:"+ddServerPath);
			    FileUtil.writeFile(ddServerPath, outputFile, "TIS-620");
				
			     //Update Exported ='Y' in BME_ORDER
			     logger.debug("**Update Exported ='Y' in BME_ORDER**");
			     int updateCount = updateOrderExportFlag(conn, user.getUserName(), date, batchParamMap.get(PARAM_CUST_GROUP));
			     logger.debug("-updateCount:"+updateCount);
			     
			     logger.debug("**Update Exported ='Y' in STOCK_ISSUE**");
			     updateCount = updateStockIssueExportFlag(conn, user.getUserName(), date, batchParamMap.get(PARAM_CUST_GROUP));
			     logger.debug("-updateCount:"+updateCount);
			     
			     logger.debug("**Update Exported ='Y' in PICK_STOCK**");
			     updateCount = updatePickStockExportFlag(conn, user.getUserName(), date, batchParamMap.get(PARAM_CUST_GROUP));
			     logger.debug("-updateCount:"+updateCount);
			     
			}else if(!foundData){
				
				result = Constants.STATUS_SUCCESS;
			}
			
			logger.debug("Con Commit");
			conn.commit();
			
			//monitorItemBean.setOutputFile(outputFile);
			monitorItemBean.setStatus(result);
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			
			
		}catch(Exception e){
			conn.rollback();
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return monitorItemBean;	
	}
	
	private static int  updateOrderExportFlag(Connection conn,String userName,Date orderDate,String storeType) throws Exception {
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int updateCount = 0;
		try{
			sql.append("\n UPDATE PENSBME_ORDER O  ");
			sql.append("\n SET EXPORTED ='Y' ,update_user ='"+userName+"' ,update_date = sysdate");
			sql.append("\n where O.store_type = '"+storeType+"'");
			sql.append("\n and O.order_date = ?  ");
			sql.append("\n and (O.exported = 'N' or O.exported is null)	 ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			
			updateCount = ps.executeUpdate();
			
			return updateCount;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	private static int  updateStockIssueExportFlag(Connection conn,String userName,Date orderDate,String storeType) throws Exception {
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int updateCount = 0;
		try{
			sql.append("\n UPDATE PENSBME_STOCK_ISSUE O  ");
			sql.append("\n SET EXPORTED ='Y' ,update_user ='"+userName+"' ,update_date = sysdate");
			sql.append("\n where O.cust_group = '"+storeType+"'");
			sql.append("\n and O.delivery_date = ?  ");
			sql.append("\n and (O.exported = 'N' or O.exported is null)	 ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			
			updateCount = ps.executeUpdate();
			
			return updateCount;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	//W3
	private static int  updatePickStockExportFlag(Connection conn,String userName,Date orderDate,String storeType) throws Exception {
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int updateCount = 0;
		try{
			sql.append("\n UPDATE PENSBME_PICK_STOCK O  ");
			sql.append("\n SET EXPORTED ='Y' ,update_user ='"+userName+"' ,update_date = sysdate");
			sql.append("\n where O.cust_group = '"+storeType+"'");
			sql.append("\n and O.delivery_date = ?  ");
			sql.append("\n and (O.exported = 'N' or O.exported is null)	 ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			
			updateCount = ps.executeUpdate();
			
			return updateCount;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	private static void insertMonitorItemResult(Connection conn,BigDecimal monitorItemId,String status,String msg) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO MONITOR_ITEM_RESULT(MONITOR_ITEM_ID, STATUS, MESSAGE)VALUES(?,?,?) ";
			//logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, monitorItemId);
			ps.setString(++index,status);
			ps.setString(++index,msg);
			
			int r = ps.executeUpdate();
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	private static String debug(int no,String s){
		return "";//"[No["+no+"]["+s.length()+"] \n";
	}
	
	private static String genLine(Map<String, String> configMap,String groupCode
			,String barcode,String pensItem,String productCode,
			int qty,double wholePriceBF,double retailPriceBF) throws Exception{
		
		String line="";
		String sizeCode ="";
		String colorCode ="";
		String baseUomCode = "";
		String wholePriceBF1 = "";
		String wholePriceBF2 = "";
		String retailPriceBF1 ="";
		String retailPriceBF2 ="";
		String newPrice1  = "";
		String newPrice2 ="";
		int no =1;
		Date date = new Date();
		try{
			
			/**1 */line += appendRightByLength(Utils.isNull(configMap.get("FACT_ID"))," ",5);//FACT_ID	รหัสโรงงาน	CHAR(5)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("FACT_ID"))," ",5));no++;
			       
			/**2 */line += appendRightByLength(Utils.isNull(configMap.get("SITE_ID"))," ",3);//SITE_ID	รหัส Site	CHAR(3)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("SITE_ID"))," ",3));no++;
			       
			/**3 */line += appendRightByLength(Utils.isNull(configMap.get("BUS_CODE"))," ",1);//BUS_CODE	รหัสธุรกิจ	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("BUS_CODE"))," ",1));no++;
			       
			/**4 */line += appendRightByLength(Utils.isNull(configMap.get("DEPT_CODE"))," ",1);//DEPT_CODE	รหัสฝ่าย	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("DEPT_CODE"))," ",1));no++;
			       
			/**5 */line += appendRightByLength(Utils.isNull(configMap.get("PRODUCT_CODE"))," ",1);//PRODUCT_CODE	รหัสผลิตภัณฑ์	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("PRODUCT_CODE"))," ",1));no++;
			       
			/**6 */line += appendRightByLength(Utils.isNull(configMap.get("SPD_CODE"))," ",1);//SPD_CODE	รหัสผลิตภัณฑ์ย่อย	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("SPD_CODE"))," ",1));no++;
			       
			/**7 */line += appendRightByLength(Utils.isNull(configMap.get("NEW_CORRECT"))," ",1);//NEW_CORRECT	สถานะของสินค้าที่ Online มา Blank=สินค้า  ใหม่หรือ สินค้าเก่าที่มีการเปลี่ยนแปลงราคาขายหรือต้นทุนหรือ สินค้าที่ไม่เปลี่ยนแปลง	CHAR(1)'D' = รายการสินค้าส่งมาลบ 	
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("NEW_CORRECT"))," ",1));no++;
			       
			/**8 */line += appendRightByLength(productCode," ",15);//ITEM_ID	รหัสสินค้า	CHAR(15)
			       line += debug(no,appendRightByLength(productCode," ",15));no++;
			       
		    	/** new Case Product (AM1001LGY[9], edit:25102019**/
			    //productCode ME1M03A3BL
			     if(productCode.length()==10){
			    	//AM1001XLGY[10]
			        sizeCode =productCode.substring(6,8);
			        colorCode =productCode.substring(8,10);
			     }else{
			    	//AM1001LGY[9]
			        sizeCode =productCode.substring(6,7)+" ";
			        colorCode =productCode.substring(7,9);  
			     }
				//logger.debug("productCode["+productCode+"]sizeCode["+sizeCode+"]colorCode["+colorCode+"]");
			/**9 */line += appendRightByLength(sizeCode," ",4);//SIZE_CODE	ขนาดของสินค้า	CHAR(4)
			       line += debug(no,appendRightByLength(sizeCode," ",4));no++;
			       
			/**10 */line += appendRightByLength(colorCode," ",3);//COLOR_CODE	สีของสินค้า	CHAR(3)
			       line += debug(no,appendRightByLength(colorCode," ",3));no++;
			       
			        baseUomCode = Utils.isNull(configMap.get("BUS_CODE"))+Utils.isNull(configMap.get("DEPT_CODE"))+Utils.isNull(configMap.get("PRODUCT_CODE"))+Utils.isNull(configMap.get("SPD_CODE"))+"EACH";
			/**11 */line += appendRightByLength(baseUomCode," ",10);//BASE_UOM_CODE	หน่วยนับสินค้า	CHAR(10)
			       line += debug(no,appendRightByLength(baseUomCode," ",10));no++;
			       
			/**12 */line += appendRightByLength(baseUomCode," ",10);//DFB_UOM_CODE	หน่วยเล็กสุดที่ใช้ตัด Stock สินค้า	CHAR(10)
			       line += debug(no,appendRightByLength(baseUomCode," ",10));no++;
			       
			
			/**13 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_TDESC"))," ",40);//ITEM_TDESC	คำอธิบายหรือชื่อสินค้าเป็นภาษาไทย	CHAR(40)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_TDESC"))," ",40));no++;
			       
			/**14 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_EDESC"))," ",40);//ITEM_EDESC	คำอธิบายหรือชื่อสินค้าเป็นภาษาอังกฤษ	CHAR(40)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_EDESC"))," ",40));no++;
			       
			/**15 */line += appendRightByLength(""," ",10);//LOT_NO	หมายเลข Lot ของสินค้า	CHAR(10)
			       line += debug(no,appendRightByLength(""," ",10));no++;
			       
			/**16 */line += appendRightByLength(Utils.isNull(configMap.get("STOCK_METHOD"))," ",1);//STOCK_METHOD	วิธีการเก็บ Stock ของสินค้า	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("STOCK_METHOD"))," ",1));no++;
			       
			       //debug
			       //logger.debug("qty:"+qty);
			/**17 */line += appendNumLeft(String.valueOf(qty),"0",12)+appendDecRightByLength("0","0",4);//TOTAL_QTY	จำนวนสินค้าที่ Online มา ต่อหน่วย ข้อ 12.	CHAR(16)
			        line += debug(no,appendNumLeft(String.valueOf(qty),"0",12)+appendDecRightByLength("0","0",4));no++;
			       
			        wholePriceBF1 = String.valueOf(wholePriceBF).substring(0,String.valueOf(wholePriceBF).indexOf("."));
			        wholePriceBF2 = String.valueOf(wholePriceBF).substring(String.valueOf(wholePriceBF).indexOf(".")+1,String.valueOf(wholePriceBF).length());
			       
			        //debug
			        //logger.debug("wholePriceBF["+wholePriceBF+"]wholePriceBF1["+wholePriceBF1+"]wholePriceBF2["+wholePriceBF2+"]");
			       // logger.debug("appendDecRightByLength(wholePriceBF2,0,2):"+appendDecRightByLength(wholePriceBF2,"0",2));
			        
			/**18 */line += appendNumLeft(wholePriceBF1,"0",9)+appendDecRightByLength(wholePriceBF2,"0",2);//TW_PRICE	ราคาที่โรงงานขายให้ Icc.  ต่อหน่วย ข้อ 11.	CHAR(11)
			        line += debug(no,appendNumLeft(wholePriceBF1,"0",9)+appendDecRightByLength(wholePriceBF2,"0",2));no++;
			        
			        retailPriceBF1 = String.valueOf(retailPriceBF).substring(0,String.valueOf(retailPriceBF).indexOf("."));
			        retailPriceBF2 = String.valueOf(retailPriceBF).substring(String.valueOf(retailPriceBF).indexOf(".")+1,String.valueOf(retailPriceBF).length());
			
		    /**19 */line += appendNumLeft(retailPriceBF1,"0",9)+appendDecRightByLength(retailPriceBF2,"0",2);//ICC_PRICE	ราคาที่ Icc. ขายให้ลูกค้า  ต่อหน่วย ข้อ 11.	CHAR(11)เป็นราคาที่ยังไม่รวม  VAT	
			        line += debug(no,appendNumLeft(retailPriceBF1,"0",9)+appendDecRightByLength(retailPriceBF2,"0",2));no++;
			        
			/**20 */line += appendRightByLength(Utils.isNull(configMap.get("VAT_TYPE"))," ",2);//VAT_TYPE	รหัสประเภทของ Vat	CHAR(2)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("VAT_TYPE"))," ",2));no++;
			        
			/**21 */line += appendRightByLength(DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITHOUT_SLASH,Utils.local_th)," ",8);//DATE	วันที่ Online ข้อมูล	CHAR(8)
			        line += debug(no,appendRightByLength(DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITHOUT_SLASH,Utils.local_th)," ",8));no++;
			        
			/**22 */line += appendRightByLength(barcode," ",13);//ITEM_BARCODE	 รหัส Barcode ของสินค้า ที่มี ขนาด , สี และหน่วย Item_Size_Color_Base_UOM_Barcode	CHAR(13)
			        line += debug(no,appendRightByLength(barcode," ",13));no++;
			        
			/**23 */line += appendRightByLength(barcode," ",13);//STANDARD_BARCODE	 รหัส Barcodeของสินค้าตาม มาตรฐาน 885   เก็บเฉพาะ ITEM_BARCODE ไม่รวม ขนาด , สี และหน่วย	CHAR(13)
			        line += debug(no,appendRightByLength(barcode," ",13));no++;
			        
			/**24 */line += appendRightByLength(groupCode," ",15);//ITEM_PBILL	รหัสสินค้าที่ใช้พิมพ์ในบิล	CHAR(15)
			        line += debug(no,appendRightByLength(groupCode," ",15));no++;
			        
			/**25 */line += appendRightByLength(Utils.isNull(configMap.get("GROUP_SC_CODE"))," ",1);//GROUP_SC_CODE	รหัสกลุ่ม ขนาดหรือสี ที่จะใช้พิมพ์ลงหน้าบิลเพื่อพิมพ์สินค้าสีเดียวกันคนละขนาด หรือขนาดเดียวกันคนละสี  เพื่อจัดกลุ่มพิมพ์ในบรรทัดเดียวกัน	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("GROUP_SC_CODE"))," ",1));no++;
			        
			/**26 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_METHOD"))," ",1);//ITEM_METHOD	รหัสวิธีการดูแลสินค้า	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_METHOD"))," ",1));no++;
			        
			/**27 */line += appendRightByLength(Utils.isNull(configMap.get("COST_METHOD"))," ",1);//COST_METHOD	รหัสวิธีการคิดต้นทุนสินค้า	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("COST_METHOD"))," ",1));no++;
			        
			/**28 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_TRAN_CODE"))," ",1);//ITEM_TRAN_CODE	รหัสวิธีการคิดค่าขนส่งสินค้า	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_TRAN_CODE"))," ",1));no++;
			        
			/**29 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_SUBTRAN_CODE"))," ",1);//ITEM_SUBTRAN_CODE	รหัสวิธีการคิดค่าขนส่งย่อย	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_SUBTRAN_CODE"))," ",1));no++;
			        
			/**30 */line += appendRightByLength(""," ",3);//BRAND_CODE	รหัสยี่ห้อ	CHAR(3)
			        line += debug(no,appendRightByLength(""," ",3));no++;
			        
			/**31 */line += appendRightByLength(""," ",3);//SBD_CODE	รหัสกลุ่มย่อยในแต่ละยีห้อ	CHAR(3)
			        line += debug(no,appendRightByLength(""," ",3));no++;
			        
			/**32 */line += appendRightByLength(""," ",2);//ITEM_SUPER_TYPE	รหัสการจัดกลุ่มของสินค้าเช่น กลุ่มเครื่องหนัง ,กลุ่มเครื่องประดับ ฯ	CHAR(2)	เสื้อผ้า, กระเป๋า,เข็มขัด ฯ	
			        line += debug(no,appendRightByLength(""," ",2));no++;
			        
			/**33 */line += appendRightByLength(""," ",2);//ITEM_TYPE CHAR(2)
			        line += debug(no,appendRightByLength(""," ",2));no++;
			        
			/**34 */line += appendRightByLength(""," ",2);//ITEM_SUB_TYPE	รหัสย่อยประเภทการจัดสินค้า	CHAR(2)
			        line += debug(no,appendRightByLength(""," ",2));no++;
			        
			/**35 */line += appendRightByLength(Utils.isNull(configMap.get("GRADE_ITEM"))," ",1);//GRADE_ITEM	เกรดสินค้า	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("GRADE_ITEM"))," ",1));no++;
			        
			/**36 */line += appendRightByLength(""," ",8);//DATE_NEW_PRICE	วันที่จะเปลี่ยนแปลงราคาขายสินค้า	CHAR(8)
			        line += debug(no,appendRightByLength(""," ",8));no++;
			        
			/**37 */line += appendRightByLength(""," ",11);//NEW_PRICE	ราคาขายสินค้าที่จะทำการเปลี่ยนแปลงต่อหน่วยข้อ 11.	CHAR(11)
			        line += debug(no,appendRightByLength(""," ",11));no++;
			        
			/**38 */line += appendRightByLength(""," ",8);//last_can_return_date	กำหนดวันที่อนุญาตให้ทำรับคืนสุดท้าย	CHAR(8)
			        line += debug(no,appendRightByLength(""," ",8));
			        
			        logger.debug("lineLength:"+line.length());
			        
			        /** append new line **/
			        line +="\n";
			       
			        
			return line;
		}catch(Exception e){
			throw e;
		}
	}
	
	
}
