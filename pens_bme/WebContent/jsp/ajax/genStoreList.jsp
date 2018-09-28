<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.bean.Master"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String groupStore = (String)request.getParameter("groupStore");
System.out.println("groupStore="+groupStore);
List<Master> items = new ArrayList<Master>();
try{
	if( !Utils.isNull(groupStore).equals("")){
		items = GeneralDAO.getStoreList(groupStore);
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>

<table  border="1" style="width:40%;" id="tableStoreList">
 <tr style="background-color: #03A4B6;">
   <th>รหัสร้านค้า</th>
   <th>ชื่อร้านค้า</th>
   <th>เลือก</th>
  </tr>
<%for(Master u : items){ %>
  <tr>
    <td width="10%"><%=u.getPensValue() %></td>
    <td width="20%"><%=u.getPensDesc() %></td>
    <td width="10%" align="center"><input type="checkbox" name="storeCode" value="<%=u.getPensValue()  %>"> </td>
  </tr>
<%}%>
</table>