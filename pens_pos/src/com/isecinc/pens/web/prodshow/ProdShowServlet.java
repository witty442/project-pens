package com.isecinc.pens.web.prodshow;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ProdShowBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ProdShowDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;


public class ProdShowServlet  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1406874613983725131L;
	private Logger logger = Logger.getLogger("PENS");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void init() throws ServletException {
		logger.debug("init ProdShowServlet...");
		try {
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			
		}
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException  {
		logger.debug("doGet ProdShowServlet...");
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("doPost ProdShowServlet...");
		int i=0;
		ProdShowBean headBean = new ProdShowBean();
		ProdShowBean beanItem = new ProdShowBean();
		List<ProdShowBean> itemList = new ArrayList<ProdShowBean>();
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		EnvProperties env = EnvProperties.getInstance();
		try {
	        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
	        logger.debug("items size:"+items.size());
	        for (FileItem item : items) {
	        	/** Input Field txtbox ,checkbox **/
	            if ( item.isFormField()) {
	            	 String fieldName = item.getFieldName();
	            	 String fieldValue = item.getString();
	            	 logger.debug("fieldName:"+fieldName+",fieldValue:"+fieldValue);
	            	
	            	 //Prepare Head Bean
	            	 if("docDate".equalsIgnoreCase(fieldName)){
	            		 headBean.setDocDate(Utils.isNull(fieldValue));
	            	 }else  if("customerCode".equalsIgnoreCase(fieldName)){
	            		 headBean.setCustomerCode(Utils.isNull(fieldValue));
	            	 }else  if("orderNo".equalsIgnoreCase(fieldName)){
	            		 headBean.setOrderNo(Utils.isNull(fieldValue));
	            	 }else  if("orderId".equalsIgnoreCase(fieldName)){
	            		 headBean.setOrderId(Utils.convertStrToInt(fieldValue));
	            	 }else  if("remark".equalsIgnoreCase(fieldName)){ 
	            		 headBean.setRemark(Utils.isNull(fieldValue));
		           	 }
	                 //item List
	            	 if("brand".equalsIgnoreCase(fieldName)){
	            		 beanItem.setBrand(Utils.isNull(fieldValue));
	            	 }
	            	 if("id".equalsIgnoreCase(fieldName)){
	            		 beanItem.setId(Utils.convertStrToInt(fieldValue));
		             }
	            	 if("statusRow".equalsIgnoreCase(fieldName)){
	            		 logger.debug("***********************************");
	            		 beanItem.setStatusRow(Utils.isNull(fieldValue));
		             }
	            	 //case db exist image
	            	 if(fieldName.startsWith("db_pic1")){
		                beanItem.setInputFileNameDBPic1(Utils.isNull(fieldValue));	
		              }
	            	 if(fieldName.startsWith("db_pic2")){
			             beanItem.setInputFileNameDBPic2(Utils.isNull(fieldValue));	
			          }
	            	 if(fieldName.startsWith("db_pic3")){
			             beanItem.setInputFileNameDBPic3(Utils.isNull(fieldValue));	
			          }
	            	 //status image by row
	            	 if(fieldName.startsWith("status_pic1")){
			             beanItem.setStatusPic1(Utils.isNull(fieldValue));	
			          }
	            	 if(fieldName.startsWith("status_pic2")){
			             beanItem.setStatusPic2(Utils.isNull(fieldValue));	
			          }
	            	 if(fieldName.startsWith("status_pic3")){
			             beanItem.setStatusPic3(Utils.isNull(fieldValue));	
			          }
	            	 
	            }else{
	                // Process form file field (input type="file").
	                String fieldName = item.getFieldName();
	                String fileName = FilenameUtils.getName(item.getName());
	                logger.debug("fieldName:"+fieldName+",fileName:"+fileName);
	                InputStream fileContent = item.getInputStream();
	                
	                if(fieldName.startsWith("pic1")){
	                	beanItem.setInputFileNamePic1(fileName);
	                	beanItem.setInputFileStreamPic1(fileContent);
	                }
					if(fieldName.startsWith("pic2")){
						beanItem.setInputFileNamePic2(fileName);
						beanItem.setInputFileStreamPic2(fileContent);        	
					}         
					if(fieldName.startsWith("pic3")){
						beanItem.setInputFileNamePic3(fileName);
						beanItem.setInputFileStreamPic3(fileContent);
	                	
						logger.debug("*** new Line start***");
	                	//last column new ProdShwoBean and add to ItemList
						if( !Utils.isNull(beanItem.getBrand()).equals("")){
	                	   itemList.add(beanItem);
	                	   //break;
						}
						beanItem = new ProdShowBean();
					}
	                
	            }//if
	        }//for
	     
	        //debug head  
	        /*logger.debug("\n\n***debug data ***");
	        logger.debug("***head bean***");
	        logger.debug("docDate:"+headBean.getDocDate());
	        logger.debug("customerCode:"+headBean.getCustomerCode());
	       
	        logger.debug("***itemList Size:"+itemList.size()+"***");
	        for(i=0;i<itemList.size();i++){
	        	ProdShowBean itemD = itemList.get(i);
	        	logger.debug("row["+i+"]brand:"+itemD.getBrand());
	        	logger.debug("row["+i+"]pic1:"+itemD.getInputFileNamePic1()+"dbPic1:"+itemD.getInputFileNameDBPic1());
	        	logger.debug("row["+i+"]pic2:"+itemD.getInputFileNamePic2()+"dbPic2:"+itemD.getInputFileNameDBPic2());
	        	logger.debug("row["+i+"]pic3:"+itemD.getInputFileNamePic3()+"dbPic3:"+itemD.getInputFileNameDBPic3());
	        }*/
	        
	        String imageLocalProdShowPath = env.getProperty("path.image.prodshow.local");//"D:/SalesApp/Images-prodshow/";
			//create Dir case no exist
			FileUtil.createDir(imageLocalProdShowPath);
			
	        conn = DBConnection.getInstance().getConnection();
	        conn.setAutoCommit(false);
	        //save prodShow
	        headBean.setCreatedBy(user.getId()+"");
	        headBean.setUpdatedBy(user.getId()+"");
	        int update = ProdShowDAO.updateProdShow(conn, headBean);
	        if(update==0){
	        	update = ProdShowDAO.insertProdShow(conn, headBean);
	        }
	        
	        //Get Max Id By OrderNo
	        int maxId = ProdShowDAO.getMaxIdProdShowLine(conn,headBean.getOrderNo());
	        if(itemList != null && itemList.size() >0){
		        for(i=0;i<itemList.size();i++){
		        	ProdShowBean itemD = itemList.get(i);
		        	itemD.setOrderNo(headBean.getOrderNo());
		        	itemD.setCreatedBy(user.getId()+"");
		        	itemD.setUpdatedBy(user.getId()+"");
		        	
		        	if( !"CANCEL".equalsIgnoreCase(itemD.getStatusRow())){
			        	//regen new file name 
			        	
		        		/****** PIC1 **********/
			        	if( !"".equals(itemD.getInputFileNamePic1())){ //found input file
			        	  //Case change image ->delete old file and gen new file
			        	  if(!"".equals(itemD.getInputFileNameDBPic1())){ //delete old file case exist
			        		  logger.debug("delete db_pic1:"+itemD.getInputFileNameDBPic1());
				        	  FileUtil.deleteFile(imageLocalProdShowPath+itemD.getInputFileNameDBPic1()); 
			        	  }
			        	  //gen New file Name By Brand
			        	  itemD.setInputFileNamePic1(genFileName(itemD, "1"));
			        	  //write file to Local
			        	  FileUtil.writeImageFile(imageLocalProdShowPath+itemD.getInputFileNamePic1(), itemD.getInputFileStreamPic1());
			        	}else{
			        		//Case image exist and no edit for update
			        		itemD.setInputFileNamePic1(itemD.getInputFileNameDBPic1());
			        	}
			        	
			        	/****** PIC2 **********/
			        	if( !"".equals(itemD.getInputFileNamePic2())){
			        		logger.debug("1.pic2 found input file");
				        	logger.debug("1.db_pic2:"+itemD.getInputFileNameDBPic2());
			        	   //delete old file
			        	   if(!"".equals(itemD.getInputFileNameDBPic2())){
			        		  logger.debug("delete db_pic2:"+itemD.getInputFileNameDBPic2());
				        	  FileUtil.deleteFile(imageLocalProdShowPath+itemD.getInputFileNameDBPic2()); 
			        	   }
			        	   itemD.setInputFileNamePic2(genFileName(itemD, "2"));
			        	   //write file to Local
				        	FileUtil.writeImageFile(imageLocalProdShowPath+itemD.getInputFileNamePic2(), itemD.getInputFileStreamPic2());
			        	}else{
			        		logger.debug("2.db_pic2:"+itemD.getInputFileNameDBPic2());
			        		//Case image exist and no edit for update
			        		itemD.setInputFileNamePic2(itemD.getInputFileNameDBPic2());
			        		
			        	   //Case DB Exist user clear image but no add new file ->delete old file
			        		if("CLEAR".equals(itemD.getStatusPic2()) 
			        			&& !"".equals(itemD.getInputFileNameDBPic2())){
				        		 logger.debug("delete db_pic1:"+itemD.getInputFileNameDBPic2());
					        	 FileUtil.deleteFile(imageLocalProdShowPath+itemD.getInputFileNameDBPic2()); 
					        	 
					        	 //set for empty for update db
					        	itemD.setInputFileNamePic2("");
				        	}
			        	}
			        	
			        	/****** PIC3 **********/
			        	if( !"".equals(itemD.getInputFileNamePic3())){
			        		logger.debug("db_pic3:"+itemD.getInputFileNameDBPic3());
			        		//delete old file
				        	if(!"".equals(itemD.getInputFileNameDBPic3())){
				        		logger.debug("delete db_pic3:"+itemD.getInputFileNameDBPic3());
					        	FileUtil.deleteFile(imageLocalProdShowPath+itemD.getInputFileNameDBPic3()); 
				        	}
			        	   itemD.setInputFileNamePic3(genFileName(itemD, "3"));
			        	   //write file to Local
			        	   FileUtil.writeImageFile(imageLocalProdShowPath+itemD.getInputFileNamePic3(), itemD.getInputFileStreamPic3());
			        	}else{
			        		logger.debug("db_pic3:"+itemD.getInputFileNameDBPic3());
			        		//Case image exist and no edit for update
			        		itemD.setInputFileNamePic3(itemD.getInputFileNameDBPic3());
			        		
			        	   //Case DB Exist user clear image but no add new file ->delete old file
			        		if("CLEAR".equals(itemD.getStatusPic3()) 
			        			&& !"".equals(itemD.getInputFileNameDBPic3())){
				        		 logger.debug("delete db_pic1:"+itemD.getInputFileNameDBPic3());
					        	 FileUtil.deleteFile(imageLocalProdShowPath+itemD.getInputFileNameDBPic3()); 
					        	 
					        	 //set for empty for update db
						        itemD.setInputFileNamePic3("");
				        	}
			        	}
			        	
			        	logger.debug("row["+i+"]pic1:"+itemD.getInputFileNamePic1());
			        	logger.debug("row["+i+"]pic2:"+itemD.getInputFileNamePic2());
			        	logger.debug("row["+i+"]pic3:"+itemD.getInputFileNamePic3());
			        	
			        	//check update or insert
			        	if(ProdShowDAO.isIdExistProdShowLine(conn, itemD.getOrderNo(), itemD.getId())){
			        		update = ProdShowDAO.updateProdShowLine(conn, itemD);
			        		logger.debug("updateProdShowLine update:"+update);
			        	}else{
			        		maxId++;
			        		itemD.setId(maxId);
			        	    update = ProdShowDAO.insertProdShowLine(conn, itemD);
			        	    logger.debug("insertProdShowLine update:"+update);
			        	}
		        	}else{
		        		//Case Delete All Row
		        		//delete old file by Row
			        	 if(!"".equals(itemD.getInputFileNameDBPic1())){
			        		 logger.debug("delete db_pic1:"+itemD.getInputFileNameDBPic1());
				        	 FileUtil.deleteFile(imageLocalProdShowPath+itemD.getInputFileNameDBPic1()); 
			        	 }
			        	 if(!"".equals(itemD.getInputFileNameDBPic2())){
			        		 logger.debug("delete db_pic2:"+itemD.getInputFileNameDBPic2());
				        	 FileUtil.deleteFile(imageLocalProdShowPath+itemD.getInputFileNameDBPic2()); 
			        	 }
			        	 if(!"".equals(itemD.getInputFileNameDBPic3())){
			        		 logger.debug("delete db_pic1:"+itemD.getInputFileNameDBPic3());
				        	 FileUtil.deleteFile(imageLocalProdShowPath+itemD.getInputFileNameDBPic3()); 
			        	 }
			        	 if(ProdShowDAO.isIdExistProdShowLine(conn, itemD.getOrderNo(), itemD.getId())){
			        		 update = ProdShowDAO.deleteProdShowLine(conn, itemD);
			        		 logger.debug("deleteProdShowLine update:"+update);
			        	 }
		        	}//if
		        }//for
	        }//if
	        conn.commit();
	        request.getSession().setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
	        
	        //Search Data Refresh
	        ProdShowBean prodShowBean = ProdShowDAO.searchProdShow(conn, headBean.getOrderNo(), true);
	        prodShowBean.setOrderId(headBean.getOrderId());
	       
	        request.getSession().setAttribute("prodShowBean", prodShowBean);
	        
	        response.sendRedirect(request.getContextPath()+"/jsp/prodshow/prodShow.jsp");
	    } catch (FileUploadException e) {
	        throw new ServletException("Cannot parse multipart request.", e);
	    }catch(Exception e){
	    	logger.error(e.getMessage(),e);
	    	try{
	    	  conn.rollback();
	    	  deleteFileCaseRollBack(itemList);
	    	}catch(Exception ee){}
	    }finally{
	    	try{
		    	if(conn != null){
		    		conn.close();
		    	}
	    	}catch(Exception ee){}
	    }
	}
	
	private static void deleteFileCaseRollBack(List<ProdShowBean> itemList){
	    String imageLocalProdShowPath = EnvProperties.getInstance().getProperty("path.image.prodshow.local");//"D:/SalesApp/Images-prodshow/";
		  if(itemList != null && itemList.size() >0){
	         for(int i=0;i<itemList.size();i++){
	        	 ProdShowBean bean = itemList.get(i);
	        	 if( !Utils.isNull(bean.getInputFileNamePic1()).equals("")){
	        	    FileUtil.deleteFile(imageLocalProdShowPath+bean.getInputFileNamePic1());
	        	 }
	        	 if( !Utils.isNull(bean.getInputFileNamePic2()).equals("")){
		        	FileUtil.deleteFile(imageLocalProdShowPath+bean.getInputFileNamePic2());
		         }
	        	 if( !Utils.isNull(bean.getInputFileNamePic3()).equals("")){
		        	FileUtil.deleteFile(imageLocalProdShowPath+bean.getInputFileNamePic3());
		         }
	         }
		  }
	}
	
	//order-brand-no
	private  String genFileName(ProdShowBean bean,String no){
		String extension = "";
		if("1".equals(no)){
			logger.debug("fileNamePic1:"+bean.getInputFileNamePic1());
			extension = bean.getInputFileNamePic1().substring(bean.getInputFileNamePic1().indexOf("."),bean.getInputFileNamePic1().length());
		}else if("2".equals(no)){
			logger.debug("fileNamePic2:"+bean.getInputFileNamePic2());
			extension = bean.getInputFileNamePic2().substring(bean.getInputFileNamePic2().indexOf("."),bean.getInputFileNamePic2().length());
		}else if("3".equals(no)){
			logger.debug("fileNamePic3:"+bean.getInputFileNamePic3());
			extension = bean.getInputFileNamePic3().substring(bean.getInputFileNamePic3().indexOf("."),bean.getInputFileNamePic3().length());
		}
		return bean.getOrderNo()+"-"+bean.getBrand()+"-"+no+extension;
	}
	
}
