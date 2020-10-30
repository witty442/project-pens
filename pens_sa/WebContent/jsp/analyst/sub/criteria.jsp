<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<% 
String reportName = Utils.isNull(request.getParameter("reportName"));
java.util.List yearList = null;
if(session.getAttribute("yearList") != null) 
	yearList = (java.util.List)session.getAttribute("yearList");

java.util.List yearListASC = null;
if(session.getAttribute("yearListASC") != null) 
	yearListASC = (java.util.List)session.getAttribute("yearListASC");

%>
         <%if( !"ProjectCAnalyst".equalsIgnoreCase(reportName)) {%>
           <!-- Role Access Column By User Display -->
           <table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
                <tr><td>
                       <%out.println(Utils.isNull(session.getAttribute("USER_ROLE_INFO"))); %>
                  </td></tr>
           </table>
          <%} %>
          
               <table width="80%" border="0" align="center" cellpadding="1" cellspacing="1">
                    <tr><td>
                        <fieldset>
                          <legend><b>รูปแบบการค้นหาที่ใช้ประจำ</b></legend>
		                    <table width="80%" border="0" align="left" cellpadding="1" cellspacing="1">
		                       <tr>
		                       <td width="15%" align="left" nowrap>
		                            <html:select property="salesBean.profileId" styleId="profileId" onchange="changeProfile('${pageContext.request.contextPath}','')" styleClass="txt_style" >
								        <html:options collection="profileList" property="key" labelProperty="name"/>
						            </html:select>
		                         </td>
		                        <td width="65%" align="left" nowrap>     
		                            <input type="button" value="บันทึกรูปแบบการค้นหา" class="btnSmallLong" style="width: 185px;" 
								    onClick="javascript:saveProfile('${pageContext.request.contextPath}','admin')" /> 
							 	    <%//if(user.getUserName().equalsIgnoreCase("pornsawas") {%>
								 	  &nbsp;&nbsp;
									  <input type="button" value="เพิ่ม/แก้ไข" class="btnSmallLong" style="width: 150px;" 
									  onClick="javascript:editProfile('${pageContext.request.contextPath}','admin')" /> 
							        <%//} %>
							      </td>
		                         </tr>
		                      </table>
                        </fieldset>
                   </td> </tr>
               </table>
                        
            <table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
		      <tr><td align="left">
		      <fieldset>
                      <table width="80%" border="0" align="left" cellpadding="3" cellspacing="1">
						<tr><td align="left">
							<font size="2"><b><u>ข้อมูล ณ วันที่ &nbsp;:&nbsp;<%=(String)session.getAttribute("maxOrderedDate")%>
							&nbsp;&nbsp;เวลา&nbsp;:<%=(String)session.getAttribute("maxOrderedTime")%>
							</u></b></font>
						</td>
						</tr>
						<tr>
						  <td align="right">
						    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
							<tr class="txt_style" >
						  	<td width="13%" align="left" nowrap><b>รอบเวลา </b>
						  	      &nbsp;&nbsp;
						  	    <html:select property="salesBean.typeSearch" onchange="chkSearch()" styleClass="txt_style">
						         <html:options collection="typeSearchList" property="key" labelProperty="name"/>
					           </html:select>
					           </td>
						     <td width="6%" align="right"><b>วันที่</b></td>
						     <td width="27%" align="left" nowrap>
						      <html:text property="salesBean.day" readonly="true" styleId="day" size="12"> </html:text>
						      &nbsp;&nbsp;-&nbsp;&nbsp;
						      <html:text property="salesBean.dayTo" readonly="true" styleId="dayTo" size="12"></html:text>
                                     </td> 
							<td width="2%" align="right"><b>ปี</b></td>
							<td width="6%" align="left">
							  <html:select property="salesBean.year" styleId="yearList" onchange="chkYear()" styleClass="txt_style">
						         <html:options collection="yearList" property="key" labelProperty="name"/>
				              </html:select>
							</td>
						    <td width="8%" align="right" nowrap><b>จัดกลุ่มตาม</b></td>
						    <td width="20%" align="left">
						        <html:select property="salesBean.groupBy" styleClass="txt_style">
							         <html:options collection="groupByList" property="key" labelProperty="name"/>
					            </html:select>
					            
					            <input type="hidden" name="maxOrderedDate" value=<%=(String)session.getAttribute("maxOrderedDate")%> id="maxOrderedDate"/>
                                         <input type="hidden" name="maxOrderedTime" value=<%=(String)session.getAttribute("maxOrderedTime")%> id="maxOrderedTime"/>
                                </td>
						    </tr>
						  </table>
						</td></tr>
					  </table>
					 </fieldset>
				   </td></tr>
					
					<tr><td colspan="8" align="left">
					     <fieldset>
					       <legend>เดือน</legend>
						    <table width="100%" border="0">
						       <c:forEach var="item" items="${yearList}" >
                                   <tr id="${item.key}" class="txt_style" >
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}01</html:multibox>&nbsp;ม.ค.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}02</html:multibox>&nbsp;ก.พ.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}03</html:multibox>&nbsp;มี.ค.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}04</html:multibox>&nbsp;เม.ย.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}05</html:multibox>&nbsp;พ.ค.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}06</html:multibox>&nbsp;มิ.ย.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}07</html:multibox>&nbsp;ก.ค.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}08</html:multibox>&nbsp;ส.ค.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}09</html:multibox>&nbsp;ก.ย.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}10</html:multibox>&nbsp;ต.ค.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}11</html:multibox>&nbsp;พ.ย.</td>
                                    <td width="5%"><html:multibox  property="salesBean.chkMonth">${item.key}12</html:multibox>&nbsp;ธ.ค.</td>
                                   </tr>
                                 </c:forEach>
                               </table>
                            </fieldset>
                    </td>
			       </tr>
			       <tr>
					  <td colspan="8" align="left">
					       <fieldset>
					       <legend>ไตรมาส</legend>
						    <table width="100%" border="0">
						     <c:forEach var="item" items="${yearList}" >
                                    <tr id="${item.key}_Q" class="txt_style" >
                                     <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}1</html:multibox>&nbsp;ไตรมาส 1</td>
                                     <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}2</html:multibox>&nbsp;ไตรมาส 2</td>
                                     <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}3</html:multibox>&nbsp;ไตรมาส 3</td>
                                     <td width="5%"><html:multibox  property="salesBean.chkQuarter">${item.key}4</html:multibox>&nbsp;ไตรมาส 4</td>
                                    </tr>
                               </c:forEach>
                              </table>
                             </fieldset>
                       </td>
			        </tr>
			       <tr>
					  <td colspan="8" align="left">
					    <fieldset>
					       <legend>ปี</legend>
						    <table width="80%" border="0">
                                  <tr class="txt_style" >
                                      <%if(yearListASC != null){ 
                                          for(int i=0;i<yearListASC.size();i++){
                                         	 com.isecinc.core.bean.References ref=(com.isecinc.core.bean.References)yearListASC.get(i);
                                           
                                       %>
                                        <td width="5%"><html:multibox  property="salesBean.chkYear"><%=ref.getKey()%></html:multibox>&nbsp;<%=ref.getName() %></td>
                                      <%}} %>
                                  </tr>
                               </table>
                            </fieldset>
                        </td>
			     </tr>
          </table>

		<table width="80%" align="center" border="0" cellpadding="1" cellspacing="1" >
                      <tr>
                        <td>
                         <fieldset id="condition-frame">
                         <legend>เงื่อนไขในการเลือกข้อมูล</legend>
                          <table width="100%"  border="0" cellpadding="1" cellspacing="1" >
                            <tr  class="txt_style" >
                              <td align="left" width="20%"><b>ขอบเขตข้อมูล</b> </td>
                              <td align="center" width="5%">=</td>
                              <td align="left" width="75%"><b>ข้อมูลเงื่อนไข</b></td>
                            </tr>
                            <tr  class="txt_style" >
                              <td align="left">
                                  <html:select property="salesBean.condName1" onchange="clearText(1);" styleId="condName1">
						        <html:options collection="conditionList" property="key" labelProperty="name"/>
					        </html:select>
				        </td>
                              <td align="center" >
                                 =
                              </td>
                              <td align="left">
							 <html:text property="salesBean.condCode1" styleId="condCode1" style="width:100px;" onkeyup="loadValue(event, 1,true);"
							  onchange="set_display_value1(event, 1);" styleClass="\" autoComplete=\"off"/>&nbsp;   
							 <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','1');">
					           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" width="20px" height="20px"/>
					        </a>
					        &nbsp;
					         <a href="javascript:clearText('1');">
					          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png" width="20px" height="20px"/>
					        </a>                                        
					         <html:text property="salesBean.condValueDisp1" readonly ="true" styleId="condValueDisp1" style="width:200px"></html:text>
					         <html:hidden property="salesBean.condValue1" styleId="condValue1"></html:hidden>
				        </td>
                            </tr>
                           <tr  class="txt_style" >
                              <td align="left">
                                  <html:select property="salesBean.condName2" onchange="clearText(2);" styleId="condName2" >
						        <html:options collection="conditionList" property="key" labelProperty="name"/>
					        </html:select>
				       </td>
                              <td align="center">
                                 =
                              </td>
                              <td align="left">
							 <html:text property="salesBean.condCode2" styleId="condCode2" style="width:100px;" onkeyup="loadValue(event, 2,true);" 
							 onchange="set_display_value2(event, 2);" styleClass="\" autoComplete=\"off"/>&nbsp;
							 <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','2');">
					           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
					        </a>
					        &nbsp;
					         <a href="javascript:clearText('2');">
					          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"  width="20px" height="20px"/>
					        </a>
                             <html:text property="salesBean.condValueDisp2" readonly="true" styleId="condValueDisp2" style="width:200px"></html:text>
					         <html:hidden property="salesBean.condValue2"  styleId="condValue2"></html:hidden>
					        
				       </td>
                            </tr>
                           <tr class="txt_style" >
                              <td align="left">
                                  <html:select property="salesBean.condName3" onchange="clearText(3);" styleId="condName3">
						        <html:options collection="conditionList" property="key" labelProperty="name"/>
					        </html:select>
				       </td>
                              <td align="center">
                                 =
                              </td>
                              <td align="left">
							 <html:text property="salesBean.condCode3" styleId="condCode3" style="width:100px;" onkeyup="loadValue(event, 3,true);" 
							 onchange="set_display_value3(event, 3);" styleClass="\" autoComplete=\"off"/>&nbsp;
							  <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','3');">
					           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
					        </a>
					        &nbsp;
					         <a href="javascript:clearText('3');">
					          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png"  width="20px" height="20px"/>
					        </a>
              				 <html:text property="salesBean.condValueDisp3" styleId="condValueDisp3" readonly="true" style="width:200px"></html:text>
					         <html:hidden property="salesBean.condValue3"  styleId="condValue3"></html:hidden>
					       
				       </td>
                            </tr>
                            <tr  class="txt_style" >
                              <td align="left">
                                <html:select property="salesBean.condName4" onchange="clearText(4);" styleId="condName4">
						        <html:options collection="conditionList" property="key" labelProperty="name"/>
					        </html:select>
				        </td>
                              <td align="center">
                                 =
                              </td>
                              <td align="left" >
                                 <html:text property="salesBean.condCode4" styleId="condCode4" style="width:100px;" onkeyup="loadValue(event, 4,true);"
                                          onchange="set_display_value4(event, 4);" styleClass="\" autoComplete=\"off"/>&nbsp;
							    <a href="javascript:showSearchValuePopup('${pageContext.request.contextPath}','4');">
					             <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"  width="20px" height="20px"/>
					            </a>
					            &nbsp;
					         <a href="javascript:clearText('4');">
					          <img border=0 src="${pageContext.request.contextPath}/icons/clean.png" width="20px" height="20px"/>
					        </a>
              				<html:text property="salesBean.condValueDisp4" styleId="condValueDisp4" readonly="true" style="width:200px"></html:text>
					        <html:hidden property="salesBean.condValue4"  styleId="condValue4"></html:hidden>							 
				        </td>
                            </tr>
                            
                            <tr>
                              <%-- <td align="left" width="20%"></td>
                              <td align="center" width="5%"></td>
                              <td align="left" width="75%"><html:radio property="salesBean.includePos" value="Y" />รวม Pos
                              &nbsp;<html:radio property="salesBean.includePos" value="N"/>Offtake</td> --%>
                            </tr>
                            
                           </table>
                        </fieldset>
                        </td>
                        <td>
                         <fieldset id="display-frame">
                         <legend>การเลือกแสดงข้อมูล</legend>
                            <table width="100%"  border="0" cellpadding="1" cellspacing="1">
                             <tr>
                             	<td colspan="3">
                             		<html:select property="salesBean.summaryType" styleId="summaryType">
							    	<html:options collection="summaryTypeList" property="key" labelProperty="name"/>
					         	</html:select>
                             	</td>
                             </tr>
                             <tr class="txt_style" >
                               <td align="left"><b>ประเภทข้อมูล</b></td>
                               <td align="left"><b>หน่วย</b></td>
                               <td align="left"><b>เปรียบเทียบ</b></td>
                             </tr>
                             <tr class="txt_style" >
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
                             <tr class="txt_style" >
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
                           <tr class="txt_style" >
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
                            <tr class="txt_style" >
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
     </fieldset>