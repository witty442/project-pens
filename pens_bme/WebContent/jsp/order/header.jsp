<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!-- HEAD -->
<% if(session.getAttribute("storeList") != null){ %>
<table align="center" border="1" cellpadding="3" cellspacing="0" class="body">
                <tr>
                  <td width="5%">Group</td>
                  <td width="5%">Item</td>
                  <td width="10%">Description</td>
                  <td width="5%">Onhand Qty</td>
                  <td width="5%">ราคาขายส่งก่อนVAT</td>
                  <td width="5%">ราคาขายปลีกก่อนVAT</td>
                  <td width="5%">Barcode</td>
                   <%
                
               	List<StoreBean> storeList =(List<StoreBean>)session.getAttribute("storeList");
                   if(storeList != null && storeList.size()>0){ 
               	  for(int k=0;k<storeList.size();k++){
                        StoreBean s = (StoreBean)storeList.get(k);
                %>
                         <td width="5%"><%=s.getStoreName() %></td>
                <%
                       }//for 2
                   }//if 
               
                %>
            </tr>    
        <% } %>   
        </table>
 <!-- HEAD -->
