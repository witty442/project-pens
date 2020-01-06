package com.isecinc.pens.web.projectc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.salesanalyst.helper.EnvProperties;
import com.isecinc.pens.web.popup.PopupDAO;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.prodshow.ProdShowBean;
import com.isecinc.pens.web.prodshow.ProdShowDAO;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;



public class ProjectCServlet  extends HttpServlet{

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
		logger.debug("doGet ProjectCServlet...");
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("doPost ProjectCServlet...");
		
		//request.setCharacterEncoding("TIS-620");
		//response.setCharacterEncoding("TIS-620");

		int itemCount = 1;
		int i=0;
		ProjectCBean headBean = new ProjectCBean();
		ProjectCBean productItem = new ProjectCBean();
		ProjectCImageBean imageItem = new ProjectCImageBean();
		List<ProjectCBean> productItemList = new ArrayList<ProjectCBean>();
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		EnvProperties env = EnvProperties.getInstance();
		try {
			//clear Message all
			request.getSession().removeAttribute("Message");
			request.getSession().removeAttribute("ERROR_Message");
	    	request.getSession().removeAttribute("oracleCustNo");
			
	        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
	        logger.debug("items size:"+items.size());
	        for (FileItem item : items) {
	        	/** Input Field txtbox ,checkbox **/
	            if ( item.isFormField()) {
	            	 String fieldName = item.getFieldName();
	            	 String fieldValue = item.getString();
	            	 logger.debug("fieldName:"+fieldName+",fieldValue:"+fieldValue);
	            	
	            	 //Prepare Head Bean
	            	 if("idHead".equalsIgnoreCase(fieldName)){
	            		 headBean.setId(Utils.isNull(fieldValue));
	            	 }else if("checkDate".equalsIgnoreCase(fieldName)){
	            		 headBean.setCheckDate(Utils.isNull(fieldValue));
	            	 }else if("checkUser".equalsIgnoreCase(fieldName)){
	            		 headBean.setCheckUser(new String(Utils.isNull(fieldValue).getBytes("ISO-8859-1"), "UTF-8"));
	            	 }else  if("storeCode".equalsIgnoreCase(fieldName)){
	            		 headBean.setStoreCode(Utils.isNull(fieldValue));
	            	 }else  if("storeName".equalsIgnoreCase(fieldName)){
	            		 headBean.setStoreName(new String(Utils.isNull(fieldValue).getBytes("ISO-8859-1"), "UTF-8"));
	            	 }else  if("branchId".equalsIgnoreCase(fieldName)){
	            		 headBean.setBranchId(fieldValue);
	            	 }else  if("branchName".equalsIgnoreCase(fieldName)){
	            		 headBean.setBranchName(new String(Utils.isNull(fieldValue).getBytes("ISO-8859-1"), "UTF-8"));
	            	 }else  if("remark".equalsIgnoreCase(fieldName)){ 
	            		 headBean.setRemark(new String(Utils.isNull(fieldValue).getBytes("ISO-8859-1"), "UTF-8"));
	            	 }else  if("mode".equalsIgnoreCase(fieldName)){ 
	            		 headBean.setMode(Utils.isNull(fieldValue));
	            	 }else  if("chkLatitude".equalsIgnoreCase(fieldName)){ 
	            		 headBean.setChkLatitude(Utils.isNull(fieldValue));
	            	 }else  if("chkLongitude".equalsIgnoreCase(fieldName)){ 
	            		 headBean.setChkLongitude(Utils.isNull(fieldValue));
		           	 }
	            	 
	            	 //image List
	            	 //case db exist image
	            	 if(fieldName.equalsIgnoreCase("db_pic_1")){
		                imageItem.setImageNameDB1(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_2")){
		                imageItem.setImageNameDB2(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_3")){
			             imageItem.setImageNameDB3(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_4")){
			             imageItem.setImageNameDB4(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_5")){
			             imageItem.setImageNameDB5(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_6")){
			             imageItem.setImageNameDB6(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_7")){
			              imageItem.setImageNameDB7(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_8")){
			              imageItem.setImageNameDB8(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_9")){
			              imageItem.setImageNameDB9(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("db_pic_10")){
			              imageItem.setImageNameDB10(Utils.isNull(fieldValue));	
	            	}
		              
		           //status image by row
            	    if(fieldName.equalsIgnoreCase("status_pic_1")){
		                imageItem.setImageStatus1(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_2")){
		                imageItem.setImageStatus2(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_3")){
			             imageItem.setImageStatus3(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_4")){
			             imageItem.setImageStatus4(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_5")){
			             imageItem.setImageStatus5(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_6")){
			             imageItem.setImageStatus6(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_7")){
			              imageItem.setImageStatus7(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_8")){
			              imageItem.setImageStatus8(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_9")){
			              imageItem.setImageStatus9(Utils.isNull(fieldValue));	
            		 }else if(fieldName.equalsIgnoreCase("status_pic_10")){
			              imageItem.setImageStatus10(Utils.isNull(fieldValue));	
	            	}
		             /*******************/
	                 //item List
	            	 if("productCode".equalsIgnoreCase(fieldName)){
	            		 itemCount++;
	            		 productItem = new ProjectCBean();
	            		 productItem.setProductCode(Utils.isNull(fieldValue));
	            	 }else if("productName".equalsIgnoreCase(fieldName)){
	            		 productItem.setProductName(new String(Utils.isNull(fieldValue).getBytes("ISO-8859-1"), "UTF-8"));
		             }else if("found".equalsIgnoreCase(fieldName)){
	            		 productItem.setFound(Utils.isNull(fieldValue));
		             }else if("leg".equalsIgnoreCase(fieldName)){
	            		 productItem.setLeg(Utils.isNull(fieldValue));
		             }else if("lineRemark".equalsIgnoreCase(fieldName)){
	            		 productItem.setLineRemark(new String(Utils.isNull(fieldValue).getBytes("ISO-8859-1"), "UTF-8"));
	            		 
	            		 //debug
	            		 logger.debug("no["+itemCount+"]productCode["+productItem.getProductCode()+"]:found["+productItem.getFound()+"]leg["+productItem.getLeg()+"]");
	            		  
	            		 //last column new and add to ItemList
	            		 productItemList.add(productItem);
	            		 logger.debug("************************");
		             }
	            }else{
	                // Process form file field (input type="file").
	                String fieldName = item.getFieldName();
	                String fileName = FilenameUtils.getName(item.getName());
	                logger.debug("fieldName:"+fieldName+",fileName:"+fileName);
	                InputStream fileContent = item.getInputStream();
	               
	                if(fieldName.equalsIgnoreCase("pic_1")){
	                	imageItem.setImageId("1");
	                	imageItem.setImageName1(fileName);
	                	imageItem.setImageStream1(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_2")){
	                	imageItem.setImageId("2");
	                	imageItem.setImageName2(fileName);
	                	imageItem.setImageStream2(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_3")){
	                	imageItem.setImageId("3");
	                	imageItem.setImageName3(fileName);
	                	imageItem.setImageStream3(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_4")){
	                	imageItem.setImageId("4");
	                	imageItem.setImageName4(fileName);
	                	imageItem.setImageStream4(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_5")){
	                	imageItem.setImageId("5");
	                	imageItem.setImageName5(fileName);
	                	imageItem.setImageStream5(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_6")){
	                	imageItem.setImageId("6");
	                	imageItem.setImageName6(fileName);
	                	imageItem.setImageStream6(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_7")){
	                	imageItem.setImageId("7");
	                	imageItem.setImageName7(fileName);
	                	imageItem.setImageStream7(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_8")){
	                	imageItem.setImageId("8");
	                	imageItem.setImageName8(fileName);
	                	imageItem.setImageStream8(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_9")){
	                	imageItem.setImageId("9");
	                	imageItem.setImageName9(fileName);
	                	imageItem.setImageStream9(fileContent);
	                }else if(fieldName.equalsIgnoreCase("pic_10")){
	                	imageItem.setImageId("10");
	                	imageItem.setImageName10(fileName);
	                	imageItem.setImageStream10(fileContent);
	                }
	            }//if
	        }//for
	        
	        
	        //Debug Product Item List
	        for(int r=0;r<productItemList.size();r++){
	           ProjectCBean item = productItemList.get(r);
	           logger.debug("debug item["+item.getProductCode()+"]:found["+item.getFound()+"] leg["+item.getLeg()+"]");
	        }
	        
	        String imageLocalPath = env.getProperty("path.projectc.photo");
			logger.debug("imageLocalPath:"+imageLocalPath);
			
	        conn = DBConnection.getInstance().getConnectionApps();
	        conn.setAutoCommit(false);
	        
	        //save cehckStock head
	        headBean.setUserName(user.getUserName());
	        if( !Utils.isNull(headBean.getId()).equals("") && !Utils.isNull(headBean.getId()).equals("0")){
	            ProjectCDAO.updateCheckStockHead(conn, headBean);
	        }else{
	        	headBean = ProjectCDAO.insertCheckStockHead(conn, headBean);
	        }
	        
	        //Save Image Item 10 Record
	        int update = 0;
	        int imageId=0;
			String imageName="";
			String imageNameDB = "";
			InputStream imageStream=null;
			String imageStatus = "";
			ProjectCImageBean imgBeanSave = new ProjectCImageBean();
	        for(i=1;i<=10;i++){
	        	imageId = i;
	        	 if(imageId==1){
					   imageName = Utils.isNull(imageItem.getImageName1());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB1());
					   imageStatus = Utils.isNull(imageItem.getImageStatus1());
					   imageStream = imageItem.getImageStream1();
	        	  }else if(imageId==2){
					   imageName = Utils.isNull(imageItem.getImageName2());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB2());
					   imageStatus = Utils.isNull(imageItem.getImageStatus2());
					   imageStream = imageItem.getImageStream2();
				   }else if(imageId==3){
					   imageName = Utils.isNull(imageItem.getImageName3());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB3());
					   imageStatus = Utils.isNull(imageItem.getImageStatus3());
					   imageStream = imageItem.getImageStream3();
				   }else if(imageId==4){
					   imageName = Utils.isNull(imageItem.getImageName4());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB4());
					   imageStatus = Utils.isNull(imageItem.getImageStatus4());
					   imageStream = imageItem.getImageStream4();
				   }else if(imageId==5){
					   imageName = Utils.isNull(imageItem.getImageName5());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB5());
					   imageStatus = Utils.isNull(imageItem.getImageStatus5());
					   imageStream = imageItem.getImageStream5();
				   }else if(imageId==6){
					   imageName = Utils.isNull(imageItem.getImageName6());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB6());
					   imageStatus = Utils.isNull(imageItem.getImageStatus6());
					   imageStream = imageItem.getImageStream6();
				   }else if(imageId==7){
					   imageName = Utils.isNull(imageItem.getImageName7());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB7());
					   imageStatus = Utils.isNull(imageItem.getImageStatus7());
					   imageStream = imageItem.getImageStream7();
				   }else if(imageId==8){
					   imageName = Utils.isNull(imageItem.getImageName8());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB8());
					   imageStatus = Utils.isNull(imageItem.getImageStatus8());
					   imageStream = imageItem.getImageStream8();
				   }else if(imageId==9){
					   imageName = Utils.isNull(imageItem.getImageName9());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB9());
					   imageStatus = Utils.isNull(imageItem.getImageStatus9());
					   imageStream = imageItem.getImageStream9();
				   }else if(imageId==10){
					   imageName = Utils.isNull(imageItem.getImageName10());
					   imageNameDB = Utils.isNull(imageItem.getImageNameDB10());
					   imageStatus = Utils.isNull(imageItem.getImageStatus10());
					   imageStream = imageItem.getImageStream10();
				   }
	        	 
	        	 //set ImageBean for Save or update ,or delete
	        	 imgBeanSave.setImageId(imageId+"");
	        	 imgBeanSave.setImageName(Utils.isNull(imageName));
	        	 imgBeanSave.setImageNameDB(Utils.isNull(imageNameDB));
	        	 imgBeanSave.setImageStream(imageStream);
	        	 imgBeanSave.setImageStatus(imageStatus);
	        	 
        		logger.debug("row["+i+"]headId:"+headBean.getId());
	        	logger.debug("row["+i+"]imageId:"+imgBeanSave.getImageId());
        		logger.debug("row["+i+"]imageName:"+imgBeanSave.getImageName());
	        	logger.debug("row["+i+"]imageNameDB:"+imgBeanSave.getImageNameDB());
	        	logger.debug("row["+i+"]imageStatus:"+imgBeanSave.getImageStatus());
	        	logger.debug("row["+i+"]imageStream:"+imgBeanSave.getImageStream());
	        	logger.debug("*********************************");
		        	
	        	if( !"DEL".equalsIgnoreCase(imgBeanSave.getImageStatus())){
	        		if(!Utils.isNull(imgBeanSave.getImageName()).equals("")){
		        	
			        	if( !"".equals(imgBeanSave.getImageName())){ //found input file
			        	   //Case change image ->delete old file and gen new file
			        	   if(!"".equals(imgBeanSave.getImageNameDB())){ //delete old file case exist
			        		  logger.debug("delete db_pic1:"+imgBeanSave.getImageNameDB());
				        	  FileUtil.deleteFile(imageLocalPath+imgBeanSave.getImageNameDB()); 
			        	   }
			        	   //gen New file Name By Brand
			        	   imgBeanSave.setImageName(genFileName(headBean,imgBeanSave.getImageName(), imageId+""));
			        	   //write file to Local
			        	   logger.debug("WriteFile To:"+imageLocalPath+imgBeanSave.getImageName());
			        	   FileUtil.writeImageFile(imageLocalPath+imgBeanSave.getImageName(), imgBeanSave.getImageStream());
			        	}else{
			        		//Case image exist and no edit for update
			        		imgBeanSave.setImageName(imgBeanSave.getImageNameDB());
			        	}
			        	
			        	 //debug
			        	logger.debug("save row["+i+"]headId:"+headBean.getId());
			        	logger.debug("save row["+i+"]imageId:"+imgBeanSave.getImageId());
		        		logger.debug("save row["+i+"]imageName(gen):"+imgBeanSave.getImageName());
			        	logger.debug("save row["+i+"]imageNameDB:"+imgBeanSave.getImageNameDB());
			        	logger.debug("save row["+i+"]imageStatus:"+imgBeanSave.getImageStatus());
			        	logger.debug("save row["+i+"]imageStream:"+imgBeanSave.getImageStream());
			        	logger.debug("*********************************");
			        	
			        	//check update or insert
			        	if(ProjectCDAO.isIdExistCheckStockImageItem(conn, headBean.getId(), imgBeanSave.getImageId())){
			        	    update = ProjectCDAO.updateCheckStockImageItem(conn, headBean, imgBeanSave);
			        	    logger.debug("updateCheckStockImageItem update:"+update);
			        	}else{
			        	    update = ProjectCDAO.insertCheckStockImageItem(conn, headBean, imgBeanSave);
			        	    logger.debug("insertCheckStockImageItem update:"+update);
			        	}//if
	        		}//if
	        	}else {

		        	 //debug
		        	logger.debug("del row["+i+"]headId:"+headBean.getId());
		        	logger.debug("del row["+i+"]imageId:"+imgBeanSave.getImageId());
	        		logger.debug("del row["+i+"]imageName(gen):"+imgBeanSave.getImageName());
		        	logger.debug("del row["+i+"]imageNameDB:"+imgBeanSave.getImageNameDB());
		        	logger.debug("del row["+i+"]imageStatus:"+imgBeanSave.getImageStatus());
		        	logger.debug("del row["+i+"]imageStream:"+imgBeanSave.getImageStream());
		        	
	        		//Case Delete All Row
	        		//delete old file by Row
		        	 if(!"".equals(imgBeanSave.getImageNameDB())){
		        		 logger.debug("delete db_pic_"+imgBeanSave.getImageId()+":"+imgBeanSave.getImageNameDB());
			        	 FileUtil.deleteFile(imageLocalPath+imgBeanSave.getImageNameDB()); 
		        	 }
		        	
		        	 if(ProjectCDAO.isIdExistCheckStockImageItem(conn, headBean.getId(), imgBeanSave.getImageId()+"")){
		        		 update = ProjectCDAO.deleteCheckStockImageItem(conn, headBean.getId(),imgBeanSave.getImageId()+"");
		        		 logger.debug("deleteCheckStockImageItem update:"+update);
		        	 }
	        	}//if
	        }//for
	
	        //Save Product Item List
	        for(int r=0;r<productItemList.size();r++){
	           ProjectCBean item = productItemList.get(r);
	           logger.debug("save item["+item.getProductCode()+"]:found["+item.getFound()+"]");
	           item.setId(headBean.getId());
	       	   if(ProjectCDAO.isIdExistCheckStockProductItem(conn, headBean.getId(), item.getProductCode()+"")){
	       		   ProjectCDAO.updateCheckStockProductItem(conn, item);
	       	   }else{
	       		   ProjectCDAO.insertCheckStockProductItem(conn, item);
	       	   }
	        }//for
	        
	        //test rollback
	        /*if(true){
	        	throw new Exception("Error make");
	        }*/
	        
	        conn.commit();
	        request.getSession().setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
	        
	        //Search Data To Display
	        boolean allRec  = true,getItems = true;
	        ProjectCBean projectCBean = null;
	        ProjectCBean criBean = new ProjectCBean();
	        criBean.setId(headBean.getId());
			List<ProjectCBean> itemsSearch = ProjectCDAO.searchCheckStockList(conn,headBean,allRec,0,0,getItems);
			if(itemsSearch != null && itemsSearch.size() >0){
				projectCBean = itemsSearch.get(0);
				 
				PopupForm cri = new PopupForm();
				Map<String, String> criMap = new HashMap<String, String>();
				//Get CustomerName
				cri.setCodeSearch(projectCBean.getStoreCode());
				projectCBean.setStoreName(PopupDAO.searchCustomerCreditSalesProjectCList(request, cri).get(0).getDesc());
					
				//Get BranchName
				criMap.put("storeCode", projectCBean.getStoreCode());
				cri.setCodeSearch(projectCBean.getBranchId());
				projectCBean.setBranchName(PopupDAO.searchCustomerBranchCreditSalesProjectCList(request, cri).get(0).getDesc());
			
				//Check CanSave checkDate < sysdate cannot not save
				projectCBean.setCanSave(true);
				 if(DateUtil.compareWithToday(projectCBean.getCheckDate()) ==-1){ //sysdate more than
					 projectCBean.setCanSave(false);
				 }
			} 
	        request.getSession().setAttribute("projectCBeanServlet", projectCBean);
	        
	        response.sendRedirect(request.getContextPath()+"/jsp/projectC/checkStockDetail.jsp");
	    } catch (FileUploadException e) {
	        throw new ServletException("Cannot parse multipart request.", e);
	    }catch(Exception e){
	    	logger.error(e.getMessage(),e);
	    	try{
	    	  conn.rollback();
	    	  deleteFileCaseRollBack(imageItem);
	    	  
	    	  //set for display error
	    	  request.getSession().setAttribute("ERROR_Message", "ไม่สามารถบันทึกข้อมูลได้");
	    	  request.getSession().setAttribute("oracleCustNo", headBean.getStoreCode());
	    	  headBean.setMode("edit");
	    	  request.getSession().setAttribute("projectCBean", headBean);
	    	  response.sendRedirect(request.getContextPath()+"/jsp/projectC/checkStockDetail.jsp");
	    	}catch(Exception ee){}
	    }finally{
	    	try{
		    	if(conn != null){
		    		conn.close();
		    	}
	    	}catch(Exception ee){}
	    }
	}
	
	private void deleteFileCaseRollBack(ProjectCImageBean imageItem){
		 EnvProperties env = EnvProperties.getInstance();
		 String imageLocalPath = env.getProperty("path.projectc.photo");
		 logger.debug("imageLocalPath:"+imageLocalPath);
		 int imageId=0;
		 String imageName="";
		 String imageNameDB = "";
		 InputStream imageStream=null;
		 String imageStatus = "";
		 try{
		  if(imageItem != null){
		        for(int i=1;i<=10;i++){
		        	imageId = i;
		        	 if(imageId==1){
						   imageName = Utils.isNull(imageItem.getImageName1());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB1());
						   imageStatus = Utils.isNull(imageItem.getImageStatus1());
						   imageStream = imageItem.getImageStream1();
		        	  }else if(imageId==2){
						   imageName = Utils.isNull(imageItem.getImageName2());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB2());
						   imageStatus = Utils.isNull(imageItem.getImageStatus2());
						   imageStream = imageItem.getImageStream2();
					   }else if(imageId==3){
						   imageName = Utils.isNull(imageItem.getImageName3());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB3());
						   imageStatus = Utils.isNull(imageItem.getImageStatus3());
						   imageStream = imageItem.getImageStream3();
					   }else if(imageId==4){
						   imageName = Utils.isNull(imageItem.getImageName4());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB4());
						   imageStatus = Utils.isNull(imageItem.getImageStatus4());
						   imageStream = imageItem.getImageStream4();
					   }else if(imageId==5){
						   imageName = Utils.isNull(imageItem.getImageName5());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB5());
						   imageStatus = Utils.isNull(imageItem.getImageStatus5());
						   imageStream = imageItem.getImageStream5();
					   }else if(imageId==6){
						   imageName = Utils.isNull(imageItem.getImageName6());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB6());
						   imageStatus = Utils.isNull(imageItem.getImageStatus6());
						   imageStream = imageItem.getImageStream6();
					   }else if(imageId==7){
						   imageName = Utils.isNull(imageItem.getImageName7());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB7());
						   imageStatus = Utils.isNull(imageItem.getImageStatus7());
						   imageStream = imageItem.getImageStream7();
					   }else if(imageId==8){
						   imageName = Utils.isNull(imageItem.getImageName8());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB8());
						   imageStatus = Utils.isNull(imageItem.getImageStatus8());
						   imageStream = imageItem.getImageStream8();
					   }else if(imageId==9){
						   imageName = Utils.isNull(imageItem.getImageName9());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB9());
						   imageStatus = Utils.isNull(imageItem.getImageStatus9());
						   imageStream = imageItem.getImageStream9();
					   }else if(imageId==10){
						   imageName = Utils.isNull(imageItem.getImageName10());
						   imageNameDB = Utils.isNull(imageItem.getImageNameDB10());
						   imageStatus = Utils.isNull(imageItem.getImageStatus10());
						   imageStream = imageItem.getImageStream10();
					   }
		        }//for
		        
        		logger.debug("imageId["+imageId+"]imageName:"+imageName);
	        	logger.debug("*********************************");
		  
        	 if( !Utils.isNull(imageName).equals("")){
        	    FileUtil.deleteFile(imageLocalPath+imageName);
        	 }
		  }
		 }catch(Exception e){
			 logger.error(e.getMessage(),e);
		 }
	}
	
	//order-brand-no
	private  String genFileName(ProjectCBean bean,String imageName,String imageId){
		String extension = "";
		
		logger.debug("imageName:"+imageName);
	    extension = imageName.substring(imageName.indexOf("."),imageName.length());
		
		return bean.getStoreCode()+"_"+bean.getCheckDate().replaceAll("\\/", "")+"_"+imageId+extension;
	}
	
}
