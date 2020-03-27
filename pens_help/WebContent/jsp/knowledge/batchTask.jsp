<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>Batch Task Sample</title>
<style>
</style>
</head>
<body>
<b>Batch Task Sample</b> <br/>
 <b><a href="http://localhost:8080/pens_help/jsp/knowledge/knowledge_list.jsp">Back to Knowledge</a></b>
<textarea rows="45" cols="200">
 /** 1.linkto BatchTask**/
<a href="#" onclick="javascript:link(true,'${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=TaskName');">

/** 2.Popup BatchTask from Page  **/	
      //init
      request.getSession().setAttribute("BATCH_TASK_RESULT"
    2.1 submit to FormAction
        2.1.1)form.action = path + "/jsp/b2bAction.do?do=importExcel";
		  form.submit();
        2.1.2)- set parameter to session
         //Prepare Parameter to BatchTask
		 Map<String, String> paraMap = new HashMap<String, String>();
		 paraMap.put(ImportB2BMakroFromExcelTask.PARAM_DATA_TYPE, aForm.getBean().getDataType());
			
		 request.getSession().setAttribute("BATCH_PARAM_MAP",paraMap);
		 request.getSession().setAttribute("DATA_FILE", aForm.getDataFormFile());
		 request.setAttribute("BATCH_TASK_NAME",BatchTaskConstants.EXPORT_REPORT_ONHAND_ROBINSON);//set to popup page to BatchTask
	  
	   2.1.3 onload() popup to BatchTask
		  /** for popup BatchTask in page **/
		  //initBatchFromPageByPopup
		  //initBatchFromPageByPopupNoWait (run and close popup)
			 <%-- <%if(!"".equals(Utils.isNull(request.getAttribute("BATCH_TASK_NAME")))){%> --%>
			    //lockscreen
			    var path = document.getElementById("path").value;
			    /** Init progressbar **/
				$(function() {
					// update the block message 
			        $.blockUI({ message: "<h2>กำลังทำรายการ     กรุณารอสักครู่......</h2>" }); 
				}); 
				    
				//submitedGenStockOnhandTemp
				var url  = path+'/jsp/batchTaskAction.do?do=prepare&pageAction=new';
					<%-- url +='&initBatchAction=initBatchFromPageByPopupNoWait&pageName=<%=Utils.isNull(request.getAttribute("BATCH_TASK_NAME"))%>'; --%>
			<%-- 	popupFull(url,'<%=Utils.isNull(request.getAttribute("BATCH_TASK_NAME"))%>'); --%>
		  <%--  <%}%> --%>
		   
	   2.1.4 After success process  set search in mainpage for display result batch
	    function searchBatch(path){
			var form = document.b2bForm;
			form.action = path + "/jsp/b2bAction.do?do=searchBatch";
			form.submit();
			return true;
		}
	   public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("searchBatch :pageName["+pageName+"]");
	
			 //searchBatch
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
				 
				 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
				 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
				 
				 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
				 logger.debug("fileName:"+batchTaskForm.getMonitorItem().getFileName());
				 
				 //Case file name is not null Is Case Export set fileName to User download
				 if(batchTaskForm.getResults()[0].getName().equalsIgnoreCase(BatchTaskConstants.EXPORT_B2B_MAKRO_TO_EXCEL)){
				    request.setAttribute("LOAD_EXPORT_FILE_NAME", batchTaskForm.getMonitorItem().getFileName());
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	/** clearBatchForm **/
	public ActionForward clearBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearBatchForm");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		try {
			 request.getSession().removeAttribute("BATCH_TASK_RESULT");
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
	}
	public ActionForward loadExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("loadExcel");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		String fileName = ""; 
		String pathFile = "";
		EnvProperties env = EnvProperties.getInstance();
		try {
			logger.debug("loadExcel :pageName["+pageName+"]");
	
			//Load Excel
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				 pathFile = env.getProperty("path.temp");
		    	 fileName = Utils.isNull(request.getParameter("fileName"));
		    	 if( !Utils.isNull(fileName).equals("")){
		    		 pathFile +=fileName; 
			    	 logger.debug("pathFile:"+pathFile);
			    	  
		    		//read file from temp file
					 byte[] bytes = FileUtil.readFileToByte(new FileInputStream(pathFile));
					 
					response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
					response.setContentType("application/excel");
					
					ServletOutputStream servletOutputStream = response.getOutputStream();
					servletOutputStream.write(bytes, 0, bytes.length);
					servletOutputStream.flush();
					servletOutputStream.close();
		    	 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	//onload after searchBatch
	//if(request.getAttribute("LOAD_EXPORT_FILE_NAME") != null){
	  // var pageName = document.getElementsByName("pageName")[0].value;
	  // document.b2bForm.action = path + "/jsp/b2bAction.do?do=loadExcel&pageName="+pageName+"&fileName==//Utils.isNull(request.getAttribute("LOAD_EXPORT_FILE_NAME"))";
	   //document.b2bForm.submit();
	//}
	
	 Sample Code Page:pens_sa/b2bAction
</textarea>     
</body>
</html>