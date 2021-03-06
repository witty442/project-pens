package com.isecinc.pens.interfaces.exports;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.FileImportTransBean;
import com.isecinc.pens.bean.KeyNoImportTransBean;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.MonitorItemDetailBean;
import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.ExportSQL;
import com.isecinc.pens.inf.helper.ExternalFunctionHelper;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.TransactionHelper;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.process.ExportProcess;
import com.isecinc.pens.inf.manager.process.ResultImportBean;
import com.isecinc.pens.inf.manager.process.export.ExportOrder;
import com.isecinc.pens.inf.manager.process.export.ExportReceipt;
import com.isecinc.pens.inf.manager.process.export.ExportReqPromotion;
import com.isecinc.pens.inf.manager.process.export.ExportStockDiscount;
import com.isecinc.pens.inf.manager.process.export.ExportStockReturn;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptHelper;
import com.isecinc.pens.interfaces.exports.process.LockboxProcess;
import com.isecinc.pens.interfaces.imports.ImportOrderProcess;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

/**
 * @author WITTY
 *
 */
public class ExportAllManager {
   
	public static Logger logger = Logger.getLogger("PENS");
	public static String PATH_CONTROL ="inf-config/config-all-export/";
	public static String FILE_CONTROL_NAME ="control_all_export.csv";
	
    public static boolean debug = true;
    
    //Run By Scheduler
    public MonitorBean process(User userLogin,String salesCode) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("Export All");
			monitorModel.setType(BatchTaskConstants.TYPE_EXPORT);
			monitorModel.setStatus(BatchTaskConstants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType("TRANSACTION");
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
	
			logger.debug(" **********Start Export All Sales :"+monitorModel.getStatus()+" ******************");
			
			//List all sales 
			List<User> salesrepList = new MUser().getSalesList(salesCode);
			if(salesrepList != null && salesrepList.size()>0) {
				for(User salesUser:salesrepList) {
			        exportToTxt(monitorModel.getTransactionId(), monitorModel.getMonitorId(), FILE_CONTROL_NAME, salesUser);
				}
			}
			logger.debug(" **********Result Export All Sales:"+monitorModel.getStatus()+" ******************");
	
			//Stamp Task to Success
			logger.info("startTaskStatus transactionId["+monitorModel.getTransactionId()+"]monitorId["+monitorModel.getMonitorId()+"]");
			dao.updateTaskStatus(monitorModel.getTransactionId(),monitorModel.getMonitorId(),"0"); //Start BatchTask = 0;
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
	    return monitorModel;
	}
    
	/**
	 * 
	 * @param transactionId
	 * @param monitorId
	 * @param transactionType
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean exportToTxt(BigDecimal transactionId,BigDecimal monitorId, String transactionType,User user) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = null;
		MonitorBean monitorModel = new MonitorBean();
		InterfaceDAO dao = new InterfaceDAO();
		EnvProperties env = EnvProperties.getInstance();
		boolean isExc = false;
		ExportProcess exProcess = new ExportProcess();
		int fileCount = 0;
		InterfaceDAO infDAO = new InterfaceDAO();
		String fileSize = "0 bytes";
		int statusTaskAll = Constants.STATUS_SUCCESS;
		FTPManager ftpManager = null;
		List<String> sqlUpdateExportFlagList = new ArrayList<String>();
		try{
			//initial FTP Manager
			ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			monitorModel.setTransactionId(transactionId);
			monitorModel.setMonitorId(monitorId);
			monitorModel.setTransactionType(transactionType);
			
			/** Check Status FTP Server Alive  if Can't Connection Throw Exception*/
			ftpManager.canConnectFTPServer();
			
			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

			/** init table config to import **/
			initConfigMap = ExportHelper.initExportConfig(PATH_CONTROL,FILE_CONTROL_NAME,conn,user);

			Set s = initConfigMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) {
				String fileFtpName = (String) it.next();
				TableBean tableBean = (TableBean) initConfigMap.get(fileFtpName);
				logger.debug("-Export TableName:"+tableBean.getTableName());
				MonitorItemBean modelItem = new MonitorItemBean();
				MonitorItemDetailBean[] modelDetailItem = null;
                try{
					if(tableBean.getTableName().equalsIgnoreCase("m_customer")){
						logger.info("--Start Export m_customer --");
					   /** Count Record and Prepare Monitor_item_detail(Data Export) */
					   modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					   
					   /** Check Data Found Before Export **/
					   if(modelDetailItem != null && modelDetailItem.length > 0){
					      tableBean = exProcess.exportCustomer(conn,tableBean,user);
					   }
					   logger.info("--End Export m_customer --");
					   
					   /** Export Customer Location and image only ischange ='Y' **/
					}else if(tableBean.getTableName().equalsIgnoreCase("m_customer_location")){
						logger.info("--Start Export m_customer_location --");
					    /** Count Record and Prepare Monitor_item_detail(Data Export) */
					    modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					   
					    /** Check Data Found Before Export **/
					    if(modelDetailItem != null && modelDetailItem.length > 0){
					      tableBean = exProcess.exportCustomerLocation(conn,tableBean,user);
					    }
					    
						logger.info("--End Export m_customer_location --");
						
					}else if(tableBean.getTableName().equalsIgnoreCase("t_order")){
						
						//For separate Task 
						
						//logger.info("--Start Export t_order --");
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						//modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						//if(modelDetailItem != null && modelDetailItem.length > 0){
						   //tableBean = new ExportOrder().exportSaleOrder(conn,tableBean,user);	
						//}
						
						//logger.info("--End Export t_order --");
					
					/** Export t_req_promotion **/
					}else if(tableBean.getTableName().equalsIgnoreCase("t_req_promotion")){
						logger.info("--Start Export t_req_promotion --");
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						    tableBean = new ExportReqPromotion().exportReqPromotion(conn,tableBean,user);	
						}
						
						logger.info("--End Export t_req_promotion --");
					/** T_MOVE_ORDER **/	
					}else if(tableBean.getTableName().equalsIgnoreCase("t_move_order")){
						logger.info("--Start Export t_move_order --");
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						   tableBean = exProcess.exportMoveOrder(conn,tableBean,user);	
						}
						
						logger.info("--Start Export t_move_order --");
						
						/** T_PROD_SHOW **/	
					}else if(tableBean.getTableName().equalsIgnoreCase("t_prod_show")){
						logger.info("--Start Export t_prod_show --");
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						   tableBean = exProcess.exportProdShow(conn,tableBean,user);	
						}
						
						logger.info("--Start Export t_prod_show --");
					/** T_STOCK **/	
					}else if(tableBean.getTableName().equalsIgnoreCase("t_stock")){
						logger.info("--Start Export t_stock --");
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						   tableBean = exProcess.exportStock(conn,tableBean,user);	
						}
						
						logger.info("--Start Export t_stock --");
						
					/** T_REQUISITION_PRODUCT **/	
					}else if(tableBean.getTableName().equalsIgnoreCase("t_requisition_product")){
						logger.info("--Start Export t_requisition_product --");
						
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						   tableBean = exProcess.exportRequisitionProduct(conn,tableBean,user);	
						}
						
						logger.info("--End Export t_requisition_product --");
						
						/** T_BILL_PLAN(BILL T) **/	
					}else if(tableBean.getTableName().equalsIgnoreCase("t_bill_plan")){
						logger.info("--Start Export t_bill_plan --");
						
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						   tableBean = exProcess.exportBillPlan(conn,tableBean,user);	
						}
						
						logger.info("--End Export t_bill_plan --");
						
					}else if(tableBean.getTableName().equalsIgnoreCase("t_visit")){
						logger.info("--Start Export t_visit --");
						
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());
						
						/** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						  tableBean = exProcess.exportSalesVisit(conn,tableBean,user);
						}
						
						logger.info("--End Export t_visit --");
					}else if(tableBean.getTableName().equalsIgnoreCase("m_trip")){
						 logger.info("--Start Export m_trip --");
						 tableBean = exProcess.exportMTrip(conn,tableBean,user); 
						 logger.info("--End Export m_trip --");
						 
				    /*** TT (LOCKBOX)  ,VAN (RECEIPT)   ***/
					}else if(tableBean.getTableName().equalsIgnoreCase("t_receipt")){
						
						/** Export Lockbox no scheduler task  ,export by Sales **/
						if(User.TT.equalsIgnoreCase(user.getType())){
							//logger.info("--Start Export t_receipt(lockbox) --");
							/** Count Payment LockBox  and Prepare Monitor_item_detail(Data Export)  **/
							//tableBean.setPrepareSqlSelect(ExportSQL.genSqlCountCaseReceiptLockBoxPayment(tableBean, user));
							
							//modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());
							
							/** Check Data Found Before Export **/
							//if( modelDetailItem != null && modelDetailItem.length > 0){
							    //tableBean = new LockboxProcess().exportSalesReceiptLockBoxProcess(conn, tableBean,user);	
							//}
							
							logger.info("--End Export t_receipt(lockbox) --");
						}else if(User.VAN.equalsIgnoreCase(user.getType())){
							logger.info("--Start Export t_receipt--");
							/** Gen Select SQL  **/
							tableBean.setPrepareSqlSelect(ExportSQL.genSqlSalesReceiptVan(tableBean, user));
							/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						    modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());
						   
						    /** Check Data Found Before Export **/
						    if( modelDetailItem != null && modelDetailItem.length > 0){
							    tableBean = new ExportReceipt().exportSalesReceiptHeaderVan(conn, tableBean,user);	
						    } 
						    logger.info("--End Export t_receipt--");
						}
						
					/*** User Role :DD (RECEIPT_LINE)   ***/
					}else if(tableBean.getTableName().equalsIgnoreCase("t_receipt_rec")){
						logger.info("--Start Export t_receipt_rec--");
                		// Only Credit Sales 
                		if(User.TT.equalsIgnoreCase(user.getType())){
                			tableBean.setPrepareSqlSelect(ExportSQL.genSqlSalesReceiptCredit(tableBean, user));
							/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						    modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());
						   
						    /** Check Data Found Before Export **/
						    if( modelDetailItem != null && modelDetailItem.length > 0){
							   tableBean = exProcess.exportSalesReceiptHeaderCredit(conn, tableBean,user);	
						    }
                		}
                		logger.info("--End Export t_receipt_rec--");
                	}else if(tableBean.getTableName().equalsIgnoreCase("t_pd_receipt_his")){
                		if(User.VAN.equalsIgnoreCase(user.getType())){
	                		logger.info("--Start Export t_pd_receipt_his--");
	                		
	                		/** Count Record and Prepare Monitor_item_detail(Data Export)  */
							modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());
							 /** Check Data Found Before Export **/
						    if( modelDetailItem != null && modelDetailItem.length > 0){
							   tableBean = exProcess.exportPDReceiptHis(connMonitor, tableBean, user);
						    }
						    logger.info("--End Export t_pd_receipt_his--");
                		}
	            	}else if(tableBean.getTableName().equalsIgnoreCase("t_bank_transfer")){
	            		if(User.VAN.equalsIgnoreCase(user.getType())){
	                		logger.info("--Start Export t_bank_transfer--");
	                		
	                		/** Count Record and Prepare Monitor_item_detail(Data Export)  */
							modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());
							 /** Check Data Found Before Export **/
						    if( modelDetailItem != null && modelDetailItem.length > 0){
							   tableBean = exProcess.exportBankTransfer(connMonitor, tableBean, user);
						    }
						    logger.info("--End Export t_bank_transfer--");
	            		}
					// 2011/11/30 Create New Export File
					// New Export File To Temp2
	               }else if(tableBean.getTableName().equalsIgnoreCase("t_order_rec")){
						logger.info("--Start Export t_order_rec--");
						
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						   tableBean = exProcess.exportSaleOrderTemp2(conn,tableBean,user);	
						}
						
						logger.info("--End Export t_order_rec--");
				  
					//Export to db temp oracle 
	               }else if(tableBean.getTableName().equalsIgnoreCase("t_stock_return")){
						logger.info("--Start Export t_stock_return--");
						
						//get sql prepare select 
						tableBean.setPrepareSqlSelect(ExportStockReturn.getSqlPrepareSelect(tableBean,user));
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
							//Export to db temp oracle 
						   tableBean = new ExportStockReturn().exportStockReturn(conn,tableBean,user);	
						}
						
						logger.info("--End Export t_stock_return--");	
						
					//Export to db temp oracle (no txt file)
	               }else if(tableBean.getTableName().equalsIgnoreCase("t_stock_discount")){
						logger.info("--Start Export t_stock_discount--");
						
						//get sql prepare select 
						tableBean.setPrepareSqlSelect(ExportStockDiscount.getSqlPrepareSelect(tableBean,user));
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
							//Export to db temp oracle 
						   tableBean = new ExportStockDiscount().exportStockDiscount(conn,tableBean,user);	
						}
						
						logger.info("--End Export t_stock_discount--");	
				   /** Case Export Order Line Only User Role DD **/
				   }else{
						logger.info("--Start Export "+tableBean.getTableName()+"--");
						// other case no insert monitor_item_detail ->Authen
					    tableBean = exProcess.exportDataDB(conn,tableBean,user); 
					    logger.info("--End Export "+tableBean.getTableName()+"--");
				   }
					
					modelItem.setStatus(Constants.STATUS_SUCCESS);
                }catch(Exception e){
                	logger.error(e.getMessage(),e);
                	modelItem.setStatus(Constants.STATUS_FAIL);
                	modelItem.setErrorMsg(e.getMessage());
                	modelItem.setErrorCode(ExceptionHandle.getExceptionCode(e));
                	
                	/** SET Task Fail In Header **/
                	statusTaskAll = Constants.STATUS_FAIL;
                }
			
                initConfigMap.put(fileFtpName, tableBean);
                
                /** Case More File Per Table **/
                if(tableBean.getFileExportList() != null && tableBean.getFileExportList().size() > 0){
                	fileCount = 0;
                	for(int f=0;f<tableBean.getFileExportList().size();f++){
                		TableBean fileExportBean =(TableBean)tableBean.getFileExportList().get(f);
                		/** DataString is null no insert monitor_item **/
                		if( fileExportBean.getDataStrExport()!= null && !Utils.isNull(fileExportBean.getDataStrExport().toString()).equals("")){ 
	                		fileCount++;
	                		 /** get Size of File  **/
	    	                fileSize = "0 bytes";
	    	                if(fileExportBean.getDataStrExport() != null){
	    	                   fileSize = FileUtil.getFileSize(fileExportBean.getDataStrExport().toString());
	    	                }
	    					/** insert to monitor_item **/
	    					logger.debug("-Start Insert Monitor Item  TableName:"+fileExportBean.getTableName());
	    					modelItem.setMonitorId(monitorModel.getMonitorId());
	    					modelItem.setId(null);//running new
	    					modelItem.setSource(fileExportBean.getSource());
	    					modelItem.setDestination(fileExportBean.getDestination());
	    					modelItem.setTableName(fileExportBean.getTableName());
	    					modelItem.setFileName(fileExportBean.getFileFtpNameFull());
	    					modelItem.setSubmitDate(new Date());
	    					modelItem.setFileSize(fileSize);
	    					/** Set Rows Count Of Text **/
	    					if(modelDetailItem != null && modelDetailItem.length >0){
	    					    modelItem.setDataCount(modelDetailItem.length);
	    					}else{
	    						modelItem.setDataCount(fileExportBean.getExportCount());
	    					}
	    					/** Insert Monitor_item  ********/
	    					modelItem = dao.insertMonitorItem(connMonitor,modelItem);
                		}
                	}//for
                	
                /** Case one file Export per Table   
                 *  DataString is null no insert monitor_item 
                 * **/
                }else if( tableBean.getDataStrExport() != null && !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){ 
	                /** get Size of File  **/
                	fileCount++;
	                fileSize = "0 bytes";
	                if(tableBean.getDataStrExport() != null){
	                   fileSize = FileUtil.getFileSize(tableBean.getDataStrExport().toString());
	                }
					/** insert to monitor_item **/
					logger.debug("-Start Insert Monitor Item  TableName:"+tableBean.getTableName());
					modelItem.setMonitorId(monitorModel.getMonitorId());
					modelItem.setSource(tableBean.getSource());
					modelItem.setDestination(tableBean.getDestination());
					modelItem.setTableName(tableBean.getTableName());
					modelItem.setFileName(tableBean.getFileFtpNameFull());
					modelItem.setSubmitDate(new Date());
					modelItem.setFileSize(fileSize);
					/** Set Rows Count Of Text **/
					if(modelDetailItem != null && modelDetailItem.length >0){
					    modelItem.setDataCount(modelDetailItem.length);
					}else{
						modelItem.setDataCount(tableBean.getExportCount());
					}
					/** Insert Monitor_item  ********/
					modelItem = dao.insertMonitorItem(connMonitor,modelItem);
                }
                
				/** Insert To Monitor_Item_Detail ***/
				if(modelDetailItem != null && modelDetailItem.length > 0){
					logger.debug("-modelDetailItem Size:"+modelDetailItem.length);
					dao.insertMonitorItemDetail(connMonitor, modelItem,modelDetailItem);
				}
				
				/** ADD Update Exported  Flag  ********/
				if(tableBean.getSqlUpdateExportFlagList() != null &&  tableBean.getSqlUpdateExportFlagList().size() > 0){
					sqlUpdateExportFlagList.addAll(tableBean.getSqlUpdateExportFlagList());
				}
				
				isExc = true;
			}//for
			
			logger.info("Step Upload ALL File To FTP Server");
			ftpManager.uploadAllFileToFTP(user,initConfigMap, "");
			
			/** Update Exported  Flag  ********/
			if(sqlUpdateExportFlagList != null &&  sqlUpdateExportFlagList.size() > 0){
				logger.debug("***** -Start  Update Exported=Y  Flag ALL TABLE*************");
			    int updateRecord = exProcess.updateExportFlag(conn, user,sqlUpdateExportFlagList);
			    logger.debug("***** -Result Update Exported=Y  Flag ALL TABLE TotalRec:"+updateRecord+" *************");
			}
			
			logger.info("Step Success Transaction Commit");
			conn.commit();
			
			/** End process ***/
			logger.debug("-Update Monitor to Success");
			monitorModel.setStatus(statusTaskAll);
			monitorModel.setSubmitDate(new Date());
			monitorModel.setFileCount(fileCount);
			dao.updateMonitor(connMonitor,monitorModel);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			if(conn != null){
			   logger.debug("Error:Step Transaction Rollback");
			   conn.rollback();
			   
			   logger.debug("Error:Step delete file in FTP Case Rollback ");
			   ftpManager.deleteAllFileInFTPCaseRollback(initConfigMap, "");
			   
			}
			
			/** End process ***/
			logger.debug("-Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
			monitorModel.setSubmitDate(new Date());
			monitorModel.setFileCount(fileCount);
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			monitorModel.setErrorMsg(e.getMessage());
			
			dao.updateMonitorCaseError(connMonitor,monitorModel);
			
			//clear Task running for next run
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT);
            
			logger.error(e.getMessage(),e);
		}finally{
			if(initConfigMap != null){
				initConfigMap.clear();
				initConfigMap = null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor =null;
			}
			if(conn != null){
				conn.close();
				conn =null;
			}
		}
		return monitorModel;
	}

   
    
}
