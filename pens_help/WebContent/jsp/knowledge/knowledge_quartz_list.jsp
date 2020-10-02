<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<title>Knowledge Quartz Dev Tips</title>

</head>
<body>
<table align="center" border="1" cellspacing="5" width="100%">
   <tr><td>
   <font size="3"> <b>Knowledge Quartz List </b></font> 
   <a href="knowledge_list.jsp">Back to MainPage</a>
   </td></tr>
    <tr>
      <td colspan="2">
        ** หาก job ไม่ run crontrigger ต่อ check trigger_state <br/>
       select trigger_state from PENSSO.Q_SO_TRIGGERS  <br/>
                 หากเป็น ERROR ให้ reSubmitBatch <br/>
                 สาเหตุ มีการใช้ quartz instance name เดียวกันแต่คนละ Server ทำให้เกิด ERROR STATE <br/>
      
      </td>
   </tr>
    <tr>
	    <td>Tip Code</td>
	   <td>Solved or Example </td>
   </tr>
     <tr>
      <td>
      Web generate Cron Maker
     </td>
     <td>
      <a href="http://www.cronmaker.com/;jsessionid=node01ikt40rt1hp23y1fqr94um84m905.node0?0">
        Link cronmaker
      </a>
     </td>
     </tr>
  </table>
</body>
</html>