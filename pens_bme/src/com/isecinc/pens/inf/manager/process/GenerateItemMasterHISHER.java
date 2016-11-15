package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

public class GenerateItemMasterHISHER extends InterfaceUtils{
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
			//Prepare FTP Manager **/
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.icc.ip.server"), env.getProperty("ftp.icc.username"), env.getProperty("ftp.icc.password"));
			ftpManager.canConnectFTPServer();
			
			//Prepare parameter
			Date date = Utils.parse(batchParamMap.get(PARAM_TRANS_DATE), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String productType = Utils.isNull(batchParamMap.get(PARAM_PRODUCT_TYPE));
			
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//Get Config Map ALL
			configMap = InterfaceUtils.getConfigInterfaceAllBySubject(conn, "PENSTOCK",productType);
			
			sql.append("\n SELECT A.barcode, A.group_code, A.product_code ");
			sql.append("\n ,(select max(pens_value) from PENSBME_MST_REFERENCE  M ");
			sql.append("\n  WHERE M.reference_code = 'LotusItem' ");
			sql.append("\n  AND M.interface_desc = A.barcode ) as pens_item ");
			sql.append("\n ,NVL(sum(A.qty),0) as qty ,A.WHOLE_PRICE_BF, A.RETAIL_PRICE_BF ");
			sql.append("\n FROM( ");
			sql.append("\n 		SELECT m.interface_desc as barcode, O.group_code, M.interface_value as product_code, 0 as qty ");
			sql.append("\n 		,NVL(O.WHOLE_PRICE_BF,0) as WHOLE_PRICE_BF, NVL(O.RETAIL_PRICE_BF,0) as RETAIL_PRICE_BF ");
			sql.append("\n 		FROM PENSBME_PRICELIST O  ");
			sql.append("\n 		LEFT OUTER JOIN PENSBME_MST_REFERENCE M	 ");
			sql.append("\n   		ON  O.group_code = M.pens_desc2     ");
			sql.append("\n   		AND M.reference_code = 'LotusItem'    ");
			sql.append("\n 		where O.store_type = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		and   O.product = '"+productType+"'");
			sql.append("\n 		and  ( O.interface_icc = 'N' or O.interface_icc is null)	 ");
			sql.append("\n 		group by M.interface_desc, O.group_code,M.interface_value,O.WHOLE_PRICE_BF,O.RETAIL_PRICE_BF ");
			sql.append("\n  )A ");
			sql.append("\n  WHERE A.barcode not in(select barcode from PENSBME_BARCODE_IN_ICC) ");
			sql.append("\n group by A.barcode, A.group_code, A.product_code,A.WHOLE_PRICE_BF,A.RETAIL_PRICE_BF ");
			sql.append("\n order by A.group_code ,A.product_code ,A.barcode ");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			
			rs = ps.executeQuery();
			while(rs.next()){
				foundData = true;
				
				barcode = Utils.isNull(rs.getString("barcode"));
				groupCode = Utils.isNull(rs.getString("group_code"));
				pensItem = Utils.isNull(rs.getString("pens_item"));
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
					insertMonitorItemResult(conn,monitorItemBean.getId(),"FAIL","PENS_ITEM["+pensItem+"] ,GROUP CODE["+groupCode+"] ,BARCODE["+barcode+"] ,InterfaceValue["+productCode+"] ,QTY["+qty+"] ��辺������� PENSBME_MST_REFERENCE");
				}else{
					//Validate Price List
					if(wholePriceBF==0 || retailPriceBF ==0){
						failCount++;
						nextStep = false;
					   //insert result msg
					   insertMonitorItemResult(conn,monitorItemBean.getId(),"FAIL","PENS_ITEM["+pensItem+"] ,GROUP CODE["+groupCode+"] ,BARCODE["+barcode+"] ,InterfaceValue["+productCode+"] ,QTY["+qty+"] ��辺������� PENSBME_PRICELIST");
					   
					}else{
						 //Gen Line
						outputLine.append(genLine(configMap,date,groupCode,barcode,pensItem,productCode,qty,wholePriceBF,retailPriceBF));
					   
						successCount++;
						//insert result msg
						insertMonitorItemResult(conn,monitorItemBean.getId(),"SUCCESS","PENS_ITEM["+pensItem+"] ,GROUP CODE["+groupCode+"] ,BARCODE["+barcode+"] ,InterfaceValue["+productCode+"] ,QTY["+qty+"] ");
						 
						// insert barcode
						insertBARCODE_IN_ICC(conn,barcode,productCode,user.getUserName());
					}
				}
	
			}//while
			
			/** Case Found Error No Gen File **/
			if(nextStep && foundData){
				 result = Constants.STATUS_SUCCESS;
				 /** Add number of line **/
				 outputFile.append(appendNumLeft(String.valueOf(successCount),"0",10)+"\n");
				 outputFile.append(outputLine);
				 
			    //write file To Ftp
			    String fileName = env.getProperty("path.icc.hisher.export.master.txt")+"/"+batchParamMap.get(PARAM_FILE_NAME);
			 	logger.debug("Step Upload ALL File To FTP Server");
				ftpManager.uploadAllFileToFTP_OPT2_BY_FILE(env.getProperty("path.icc.hisher.export.master.txt"),fileName, outputFile);
			     
				//Backup file to DD Server
				String ddServerPath =env.getProperty("path.backup.icc.hisher.export.master.txt")+"/"+batchParamMap.get(PARAM_FILE_NAME);
			    logger.debug("Backup Text File:"+ddServerPath);
			    FileUtil.writeFile(ddServerPath, outputFile, "TIS-620");
			    
			     //Update Exported ='Y' in PRICELIST
			     logger.debug("Update Exported ='Y' in PRICELIST");
			     int updateCount = updatePriceListInterfaceFlag(conn, user.getUserName(), batchParamMap.get(PARAM_CUST_GROUP));
			     logger.debug("updateCount:"+updateCount);

			     
			}else if(!foundData){
				
				result = Constants.STATUS_SUCCESS;
			}
			
			logger.debug("Con Commit");
			conn.commit();
			
			monitorItemBean.setStatus(result);
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			
		}catch(Exception e){
			e.printStackTrace();
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
	
	private static int  updatePriceListInterfaceFlag(Connection conn,String userName,String storeType) throws Exception {
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int updateCount = 0;
		try{
			sql.append("\n UPDATE PENSBME_PRICELIST O  ");
			sql.append("\n SET INTERFACE_ICC ='Y' ,update_user ='"+userName+"' ,update_date = sysdate");
			sql.append("\n where O.store_type = '"+storeType+"'");
			sql.append("\n and (O.INTERFACE_ICC = 'N' or O.INTERFACE_ICC is null)	 ");
			
			ps = conn.prepareStatement(sql.toString());
			
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
	
	private static void insertBARCODE_IN_ICC(Connection conn,String barcode,String productCode,String userName) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO PENSBME_BARCODE_IN_ICC(BARCODE,MATERIAL_MASTER,CREATE_USER, CREATE_DATE)VALUES(?,?,?,?) ";
			//logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index, barcode);
			ps.setString(++index, productCode);
			ps.setString(++index,userName);
			ps.setDate(++index,Utils.getSqlCurrentDate());
			
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
		return""; //"[No["+no+"]["+s.length()+"] \n";
	}
	private static String debug(int no,String s,String prefix){
		return""; //"[No["+no+"]["+prefix+"]["+s+"] \n";
	}
	
	private static String genLine(Map<String, String> configMap,Date date,String groupCode
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
		try{
			
			/**1 */line += appendRightByLength(Utils.isNull(configMap.get("FACT_ID"))," ",5);//FACT_ID	�����ç�ҹ	CHAR(5)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("FACT_ID"))," ",5));no++;
			       
			/**2 */line += appendRightByLength(Utils.isNull(configMap.get("SITE_ID"))," ",3);//SITE_ID	���� Site	CHAR(3)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("SITE_ID"))," ",3));no++;
			       
			/**3 */line += appendRightByLength(Utils.isNull(configMap.get("BUS_CODE"))," ",1);//BUS_CODE	���ʸ�áԨ	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("BUS_CODE"))," ",1));no++;
			       
			/**4 */line += appendRightByLength(Utils.isNull(configMap.get("DEPT_CODE"))," ",1);//DEPT_CODE	���ʽ���	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("DEPT_CODE"))," ",1),"DEPT_CODE");no++;
			       
			/**5 */line += appendRightByLength(Utils.isNull(configMap.get("PRODUCT_CODE"))," ",1);//PRODUCT_CODE	���ʼ�Ե�ѳ��	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("PRODUCT_CODE"))," ",1),"PRODUCT_CODE");no++;
			       
			/**6 */line += appendRightByLength(Utils.isNull(configMap.get("SPD_CODE"))," ",1);//SPD_CODE	���ʼ�Ե�ѳ������	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("SPD_CODE"))," ",1));no++;
			       
			/**7 */line += appendRightByLength(Utils.isNull(configMap.get("NEW_CORRECT"))," ",1);//NEW_CORRECT	ʶҹТͧ�Թ��ҷ�� Online �� Blank=�Թ���  �������� �Թ�����ҷ���ա������¹�ŧ�ҤҢ�����͵鹷ع���� �Թ��ҷ���������¹�ŧ	CHAR(1)'D' = ��¡���Թ�������ź 	
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("NEW_CORRECT"))," ",1));no++;
			       
			/**8 */line += appendRightByLength(productCode," ",15);//ITEM_ID	�����Թ���	CHAR(15)
			       line += debug(no,appendRightByLength(productCode," ",15));no++;
			       
			       //productCode ME1M03A3BL
			       sizeCode =productCode.substring(6,8);
			/**9 */line += appendRightByLength(sizeCode," ",4);//SIZE_CODE	��Ҵ�ͧ�Թ���	CHAR(4)
			       line += debug(no,appendRightByLength(sizeCode," ",4));no++;
			       
			       colorCode =productCode.substring(8,10);
			/**10 */line += appendRightByLength(colorCode," ",3);//COLOR_CODE	�բͧ�Թ���	CHAR(3)
			       line += debug(no,appendRightByLength(colorCode," ",3));no++;
			       
			        baseUomCode = Utils.isNull(configMap.get("BUS_CODE"))+Utils.isNull(configMap.get("DEPT_CODE"))+Utils.isNull(configMap.get("PRODUCT_CODE"))+Utils.isNull(configMap.get("SPD_CODE"))+"EACH";
			/**11 */line += appendRightByLength(baseUomCode," ",10);//BASE_UOM_CODE	˹��¹Ѻ�Թ���	CHAR(10)
			       line += debug(no,appendRightByLength(baseUomCode," ",10));no++;
			       
			/**12 */line += appendRightByLength(baseUomCode," ",10);//DFB_UOM_CODE	˹�������ش�����Ѵ Stock �Թ���	CHAR(10)
			       line += debug(no,appendRightByLength(baseUomCode," ",10));no++;
			       
			
			/**13 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_TDESC"))," ",40);//ITEM_TDESC	��͸Ժ�����ͪ����Թ�����������	CHAR(40)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_TDESC"))," ",40));no++;
			       
			/**14 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_EDESC"))," ",40);//ITEM_EDESC	��͸Ժ�����ͪ����Թ����������ѧ���	CHAR(40)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_EDESC"))," ",40));no++;
			       
			/**15 */line += appendRightByLength(""," ",10);//LOT_NO	�����Ţ Lot �ͧ�Թ���	CHAR(10)
			       line += debug(no,appendRightByLength(""," ",10));no++;
			       
			/**16 */line += appendRightByLength(Utils.isNull(configMap.get("STOCK_METHOD"))," ",1);//STOCK_METHOD	�Ըա���� Stock �ͧ�Թ���	CHAR(1)
			       line += debug(no,appendRightByLength(Utils.isNull(configMap.get("STOCK_METHOD"))," ",1));no++;
			       
			/**17 */line += appendNumLeft(String.valueOf(qty),"0",12)+appendDecRightByLength("0","0",4);//TOTAL_QTY	�ӹǹ�Թ��ҷ�� Online �� ���˹��� ��� 12.	CHAR(16)
			        line += debug(no,appendNumLeft(String.valueOf(qty),"0",12)+appendDecRightByLength("0","0",4));no++;
			        
			        wholePriceBF1 = String.valueOf(wholePriceBF).substring(0,String.valueOf(wholePriceBF).indexOf("."));
			        wholePriceBF2 = String.valueOf(wholePriceBF).substring(String.valueOf(wholePriceBF).indexOf(".")+1,String.valueOf(wholePriceBF).length());
			       
			/**18 */line += appendNumLeft(wholePriceBF1,"0",9)+appendDecRightByLength(wholePriceBF2,"0",2);//TW_PRICE	�Ҥҷ���ç�ҹ������ Icc.  ���˹��� ��� 11.	CHAR(11)
			        line += debug(no,appendNumLeft(wholePriceBF1,"0",9)+appendDecRightByLength(wholePriceBF2,"0",2));no++;
			        
			        retailPriceBF1 = String.valueOf(retailPriceBF).substring(0,String.valueOf(retailPriceBF).indexOf("."));
			        retailPriceBF2 = String.valueOf(retailPriceBF).substring(String.valueOf(retailPriceBF).indexOf(".")+1,String.valueOf(retailPriceBF).length());
			
		    /**19 */line += appendNumLeft(retailPriceBF1,"0",9)+appendDecRightByLength(retailPriceBF2,"0",2);//ICC_PRICE	�Ҥҷ�� Icc. �������١���  ���˹��� ��� 11.	CHAR(11)���Ҥҷ���ѧ������  VAT	
			        line += debug(no,appendNumLeft(retailPriceBF1,"0",9)+appendDecRightByLength(retailPriceBF2,"0",2));no++;
			        
			/**20 */line += appendRightByLength(Utils.isNull(configMap.get("VAT_TYPE"))," ",2);//VAT_TYPE	���ʻ������ͧ Vat	CHAR(2)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("VAT_TYPE"))," ",2));no++;
			        
			/**21 */line += appendRightByLength(Utils.stringValue(date, Utils.DD_MM_YYYY_WITHOUT_SLASH,Utils.local_th)," ",8);//DATE	�ѹ��� Online ������	CHAR(8)
			        line += debug(no,appendRightByLength(Utils.stringValue(date, Utils.DD_MM_YYYY_WITHOUT_SLASH,Utils.local_th)," ",8));no++;
			        
			/**22 */line += appendRightByLength(barcode," ",13);//ITEM_BARCODE	 ���� Barcode �ͧ�Թ��� ����� ��Ҵ , �� ���˹��� Item_Size_Color_Base_UOM_Barcode	CHAR(13)
			        line += debug(no,appendRightByLength(barcode," ",13));no++;
			        
			/**23 */line += appendRightByLength(barcode," ",13);//STANDARD_BARCODE	 ���� Barcode�ͧ�Թ��ҵ�� �ҵðҹ 885   ��੾�� ITEM_BARCODE ������ ��Ҵ , �� ���˹���	CHAR(13)
			        line += debug(no,appendRightByLength(barcode," ",13));no++;
			        
			/**24 */line += appendRightByLength(groupCode," ",15);//ITEM_PBILL	�����Թ��ҷ��������㹺��	CHAR(15)
			        line += debug(no,appendRightByLength(groupCode," ",15));no++;
			        
			/**25 */line += appendRightByLength(Utils.isNull(configMap.get("GROUP_SC_CODE"))," ",1);//GROUP_SC_CODE	���ʡ���� ��Ҵ������ ����������ŧ˹�Һ�����;�����Թ��������ǡѹ���Т�Ҵ ���͢�Ҵ���ǡѹ������  ���ͨѴ����������㹺�÷Ѵ���ǡѹ	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("GROUP_SC_CODE"))," ",1));no++;
			        
			/**26 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_METHOD"))," ",1);//ITEM_METHOD	�����Ըա�ô����Թ���	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_METHOD"))," ",1));no++;
			        
			/**27 */line += appendRightByLength(Utils.isNull(configMap.get("COST_METHOD"))," ",1);//COST_METHOD	�����Ըա�äԴ�鹷ع�Թ���	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("COST_METHOD"))," ",1));no++;
			        
			/**28 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_TRAN_CODE"))," ",1);//ITEM_TRAN_CODE	�����Ըա�äԴ��Ң����Թ���	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_TRAN_CODE"))," ",1));no++;
			        
			/**29 */line += appendRightByLength(Utils.isNull(configMap.get("ITEM_SUBTRAN_CODE"))," ",1);//ITEM_SUBTRAN_CODE	�����Ըա�äԴ��Ң�������	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("ITEM_SUBTRAN_CODE"))," ",1));no++;
			        
			/**30 */line += appendRightByLength(""," ",3);//BRAND_CODE	����������	CHAR(3)
			        line += debug(no,appendRightByLength(""," ",3));no++;
			        
			/**31 */line += appendRightByLength(""," ",3);//SBD_CODE	���ʡ������������������	CHAR(3)
			        line += debug(no,appendRightByLength(""," ",3));no++;
			        
			/**32 */line += appendRightByLength(""," ",2);//ITEM_SUPER_TYPE	���ʡ�èѴ������ͧ�Թ����� ���������ͧ˹ѧ ,���������ͧ��дѺ �	CHAR(2)	����ͼ��, ������,����Ѵ �	
			        line += debug(no,appendRightByLength(""," ",2));no++;
			        
			/**33 */line += appendRightByLength(""," ",2);//ITEM_TYPE CHAR(2)
			        line += debug(no,appendRightByLength(""," ",2));no++;
			        
			/**34 */line += appendRightByLength(""," ",2);//ITEM_SUB_TYPE	�������»�������èѴ�Թ���	CHAR(2)
			        line += debug(no,appendRightByLength(""," ",2));no++;
			        
			/**35 */line += appendRightByLength(Utils.isNull(configMap.get("GRADE_ITEM"))," ",1);//GRADE_ITEM	�ô�Թ���	CHAR(1)
			        line += debug(no,appendRightByLength(Utils.isNull(configMap.get("GRADE_ITEM"))," ",1));no++;
			        
			/**36 */line += appendRightByLength(""," ",8);//DATE_NEW_PRICE	�ѹ��������¹�ŧ�ҤҢ���Թ���	CHAR(8)
			        line += debug(no,appendRightByLength(""," ",8));no++;
			        
			/**37 */line += appendRightByLength(""," ",11);//NEW_PRICE	�ҤҢ���Թ��ҷ��зӡ������¹�ŧ���˹��¢�� 11.	CHAR(11)
			        line += debug(no,appendRightByLength(""," ",11));no++;
			        
			/**38 */line += appendRightByLength(""," ",8);//last_can_return_date	��˹��ѹ���͹حҵ�����Ѻ�׹�ش����	CHAR(8)
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
