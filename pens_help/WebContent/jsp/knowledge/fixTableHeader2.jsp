<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620">
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script> 
<title>Insert title here</title>
<style>
</style>

<script type="text/javascript">
$(function(){
 
    var objTR = $("#place_data").find("tr"); // อ้างอิง tr ใน tbody
     
    // เก็บตัวแปรค่าของข้อมูลของแถวแรก ในตารางส่วนขงอ tbody คอลัมน์ที่ 2 (ค่าในโปรแกรมเป็น 1)
    var dataTopic = objTR.eq(0).find("td:eq(1)").text();
    $("#place_show").html("Name: "+dataTopic); // แสดงค่าเริ่มต้น   
     
    // เมื่อ tbody มีการเลื่อน
    $("#place_data").scroll(function () {
        var pos_one=null; // ไว้เก็บตัวแปรตำแหน่ง tr ที่จะใช้งาน
        // วน tr ใน tbody
        objTR.each(function(i,v){
            var pos_val = objTR.eq(i).offset(); // เก็บค่าตำแหน่ง tr
            if(pos_val.top>=$("#place_data").offset().top){
                pos_one=i; // เก็บค่า index ของ tr
                return false; // ยกเลิกการวนลูป
            }
        });
        // เก็บค่าข้อมูลใน tr จากตำแหน่งที่ได้จากค่า pos_one โดยใช้ค่าในคอลัมน์ 2 (ในโค้ด 1)
        var dataTopic = objTR.eq(pos_one).find("td:eq(1)").text();
        $("#place_show").html("Name: "+dataTopic); // แสดงค่าข้อมูล
 
    });
     
});
</script>    

</head>
<body>

 <div class="" style="margin:auto;width:65%;position:relative;height:300px;">      
<table class="" style="width:800px;position:relative;" border="1">
      <thead>
       <tr class="bg-warning">
           <td colspan="6" class="text-center" id="place_show">
           </td>
       </tr>
        <tr class="bg-warning" style="display: table;width:100%;">
          <td style="width:50px;">#</td>
          <td style="width:150px;">First Name</td>
          <td style="width:150px;">Last Name</td>
          <td style="width:150px;">Username</td>
          <td style="width:150px;">Username2</td>
          <td style="width:150px;">Username3</td>
        </tr>
      </thead>
<!--      ค่า top 114px คือค่าที่ต้องการยับจากด้านบนออกจากส่วนของ thead-->
      <tbody id="place_data" style="
        position: absolute;
        width: 800px;
        display: table-column-group;
        height: 200px;
        overflow: auto;
         ">
         <%for(int i=0;i<=20;i++){ %>
	          <tr style="display: table;width:100%;">
	          <td style="width:50px;">1</td>
	          <td style="width:150px;">Mark </td>
	          <td style="width:150px;">Otto</td>
	          <td style="width:150px;">@mdo</td>
	          <td style="width:150px;">@mdox</td>
	          <td style="width:150px;">@mdox</td>
	        </tr>
        <%} %>
      </tbody>
    </table>        
    </div>   
     
    <br style="clear:both;">    
    <br style="clear:both;">
	
</body>
</html>