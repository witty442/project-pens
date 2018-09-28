<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.inf.helper.Constants"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>


<%

//String typeSearch = com.isecinc.pens.inf.helper.Utils.isNull(request.getAttribute("DATA"));
java.util.List yearList = null;
if(request.getAttribute("yearList") != null){ 
	yearList = (java.util.List)request.getAttribute("yearList");
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesAnalyst.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablednd_0_5.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript" language="javascript"><!--
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('day'));

	chkSearch();
}

function chkSearch(){
   var typeSearch = document.getElementsByName("salesBean.typeSearch")[0];
   //alert(typeSearch.value);

    disabledObj(document.getElementsByName("salesBean.quarter")[0] ,false);
	disabledObj(document.getElementsByName("salesBean.day")[0] ,false);
	for(i=0;i<12;i++){
     disabledObj(document.getElementsByName("salesBean.chkMonth")[i],false);
    }
	for(i=0;i<<%=yearList!=null?yearList.size():0%>;i++){
	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],false);
	}
	   
   if(typeSearch.value == 'DAY'){
	   disabledObj(document.getElementsByName("salesBean.quarter")[0] ,true);
	   for(i=0;i<12;i++){
	      disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
	   }
	   for(i=0;i<<%=yearList!=null?yearList.size():0%>;i++){
		   disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
		}
    }else  if(typeSearch.value == 'MONTH'){
       disabledObj(document.getElementsByName("salesBean.quarter")[0] ,true);
       disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
       for(i=0;i<<%=yearList!=null?yearList.size():0%>;i++){
    	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
    	}
    }else  if(typeSearch.value == 'QUARTER'){
   	   disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
   	   for(i=0;i<12;i++){
	      disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
	   }
	   	for(i=0;i<<%=yearList!=null?yearList.size():0%>;i++){
	 	   disabledObj(document.getElementsByName("salesBean.chkYear")[i],true);
	 	}
    }else  if(typeSearch.value == 'YEAR'){
    	disabledObj(document.getElementsByName("salesBean.quarter")[0] ,true);
    	disabledObj(document.getElementsByName("salesBean.day")[0] ,true);
    	for(i=0;i<12;i++){
 	     disabledObj(document.getElementsByName("salesBean.chkMonth")[i],true);
 	    }
    }
}

function disabledObj(obj,flag){
   obj.disabled = flag;
}

</script>

<!-- Move for new index. -->
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe(); MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">

			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="MonitorInterfaces"/>
				<jsp:param name="code" value=""/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      			<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/salesAnalystReportAction">
			            <jsp:include page="../error.jsp"/>	
                            
                            <!-- Criteria -->
                            <fieldset>
                           
                            <table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
									<tr>
									  <td colspan="8" align="right">&nbsp;
									    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
										<tr>
									  <td width="8%" align="right">รอบเวลา</td>
										<td width="11%" align="left"> 
									        <html:select property="salesBean.typeSearch" onchange="chkSearch()">
									         <html:options collection="typeSearchList" property="key" labelProperty="name"/>
								           </html:select>
									    </td>
										<td width="4%" align="right">ปี</td>
										<td width="13%" align="left">
										  <html:select property="salesBean.year">
									         <html:options collection="yearList" property="key" labelProperty="name"/>
							              </html:select>
										</td>
									    <td width="10%" align="right">ไตรมาส</td>
									    <td width="13%" align="left">
									       <html:select property="salesBean.quarter">
									         <html:options collection="quarterList" property="key" labelProperty="name"/>
								           </html:select>
									    </td>
									    <td width="7%" align="right">วัน</td>
									    <td width="34%" align="left">
									      <html:text property="salesBean.day" readonly="true" styleId="day" size="15"></html:text>
                                        </td>
									</tr>
										</table>
									  </td>
                                    </tr>
									
									<tr>
									  <td colspan="8" align="left">
										    <table width="100%" border="0">
	                                         <tr>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">1</html:multibox>ม.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">2</html:multibox>ก.พ.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">3</html:multibox>มี.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">4</html:multibox>เม.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">5</html:multibox>พ.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">6</html:multibox>มิ.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">7</html:multibox>ก.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">8</html:multibox>ส.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">9</html:multibox>ก.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">10</html:multibox>ต.ค.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">11</html:multibox>พ.ย.</td>
	                                          <td width="5%"><html:multibox  property="salesBean.chkMonth">12</html:multibox>ธ.ค.</td>
	                                         </tr>
                                        </table>
                                      </td>
							     </tr>
							     <tr>
									  <td colspan="8" align="left">
										    <table width="80%" border="0">
	                                         <tr>
	                                         <%if(yearList != null){ 
	                                              for(int i=0;i<yearList.size();i++){
	                                            	  com.isecinc.core.bean.References ref=(com.isecinc.core.bean.References)yearList.get(i);
	                                              
		                                         %>
		                                          <td width="5%"><html:multibox  property="salesBean.chkYear"><%=ref.getKey()%></html:multibox><%=ref.getName() %></td>
	                                         <%}} %>
	                                         </tr>
                                        </table>
                                      </td>
							     </tr>
									<tr>
									  <td width="2%" align="left">จัดกลุ่มตาม 
										    <html:select property="salesBean.groupBy">
										         <html:options collection="groupByList" property="key" labelProperty="name"/>
								            </html:select>
							          </td>
									  <td width="98%" colspan="7" align="left">
									      
									  </td>
							     </tr>	
				          </table>
                            
				        </fieldset>
	                     <br><br>
						<table width="80%" align="center" border="0" cellpadding="1" cellspacing="1" >
						 
                          <tr>
                            <td>
	                            <fieldset>
	                            <legend>เงื่อนไขในการเลือกข้อมูล</legend>
		                            <table width="100%"  border="0" cellpadding="1" cellspacing="1" >
		                              <tr >
		                                <td align="left">ขอบเขตข้อมูล </td>
		                                <td align="center">=</td>
		                                <td align="left">ข้อมูลเงื่อนไข</td>
		                              </tr>
		                              <tr >
		                                <td align="left">
		                                    <html:select property="salesBean.condName1">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
		                                    <html:select property="salesBean.condValue1">
										        <html:options collection="valuesList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                              </tr>
		                             <tr >
		                                <td align="left">
		                                    <html:select property="salesBean.condName2">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								       </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
		                                    <html:select property="salesBean.condValue2">
										        <html:options collection="valuesList" property="key" labelProperty="name"/>
									        </html:select>
								       </td>
		                              </tr>
		                             <tr>
		                                <td align="left">
		                                    <html:select property="salesBean.condName3">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								       </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
		                                    <html:select property="salesBean.condValue3">
										        <html:options collection="valuesList" property="key" labelProperty="name"/>
									        </html:select>
								       </td>
		                              </tr>
		                              <tr>
		                                <td align="left">
		                                    <html:select property="salesBean.condName4">
										        <html:options collection="conditionList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                                <td align="center">
		                                   =
		                                </td>
		                                <td align="left">
		                                    <html:select property="salesBean.condValue4">
										        <html:options collection="valuesList" property="key" labelProperty="name"/>
									        </html:select>
								        </td>
		                              </tr>
	                              </table>
	                           </fieldset>
                            </td>
                            <td>
	                            <fieldset>
	                            <legend>การเลือกแสดงข้อมูล</legend>
			                             <table width="100%"  border="0" cellpadding="1" cellspacing="1">
			                              <tr>
			                                <td align="left">ประเภทข้อมูล</td>
			                                <td align="left">หน่วย</td>
			                                <td align="left">เปรียบเทียบ</td>
			                              </tr>
			                              <tr>
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp1">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit1">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.compareDisp1">
											        <html:options collection="compareColumnList" property="key" labelProperty="name"/>
									          </html:select>
										    </td>
			                              </tr>
			                              <tr>
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp2">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit2">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                 
										    </td>
			                              </tr>
			                            <tr>
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp3">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit3">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.compareDisp2">
											        <html:options collection="compareColumnList" property="key" labelProperty="name"/>
									          </html:select>
										    </td>
			                              </tr>
			                             <tr>
			                                <td align="left">
			                                  <html:select property="salesBean.colNameDisp4">
											        <html:options collection="dispColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                   <html:select property="salesBean.colNameUnit4">
											        <html:options collection="unitColumnList" property="key" labelProperty="name"/>
									          </html:select>
			                                </td>
			                                <td align="left">
			                                  
										    </td>
			                              </tr>
		                              </table>
			                    </fieldset>        
                            </td>
                          </tr>
                        </table>						
                            <!-- Criteria -->
                            <br/>
                            
							<!-- BUTTON -->
							<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
								<tr>
									<td align="right">
									     <input type="button" value=" Search " class="newPosBtn" style="width: 120px;" 
										     onClick="javascript:search('${pageContext.request.contextPath}','admin')" />
										 <input type="button" value=" Export " class="newPosBtn" style="width: 120px;" 
										     onClick="javascript:export('${pageContext.request.contextPath}','admin')" />
										 <input type="button" value=" Clear " class="newPosBtn" style="width: 120px;" 
										     onClick="javascript:clearForm('${pageContext.request.contextPath}','admin')" />
									</td>
								</tr>
							</table>
							<!-- BUTTON -->
							<br></br>
							
							<!-- RESULT -->
							<%out.print(Utils.isNull(request.getAttribute("RESULT"))); %>
							
						    <!-- RESULT -->
					  </html:form>
						<!-- BODY -->	
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>