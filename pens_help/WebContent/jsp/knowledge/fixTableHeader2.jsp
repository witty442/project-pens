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
 
    var objTR = $("#place_data").find("tr"); // ��ҧ�ԧ tr � tbody
     
    // �纵���ä�Ңͧ�����Ţͧ���á 㹵��ҧ��ǹ��� tbody ��������� 2 (����������� 1)
    var dataTopic = objTR.eq(0).find("td:eq(1)").text();
    $("#place_show").html("Name: "+dataTopic); // �ʴ�����������   
     
    // ����� tbody �ա������͹
    $("#place_data").scroll(function () {
        var pos_one=null; // ����纵���õ��˹� tr ������ҹ
        // ǹ tr � tbody
        objTR.each(function(i,v){
            var pos_val = objTR.eq(i).offset(); // �纤�ҵ��˹� tr
            if(pos_val.top>=$("#place_data").offset().top){
                pos_one=i; // �纤�� index �ͧ tr
                return false; // ¡��ԡ���ǹ�ٻ
            }
        });
        // �纤�Ң������ tr �ҡ���˹觷����ҡ��� pos_one ������㹤������ 2 (��� 1)
        var dataTopic = objTR.eq(pos_one).find("td:eq(1)").text();
        $("#place_show").html("Name: "+dataTopic); // �ʴ���Ң�����
 
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
<!--      ��� top 114px ��ͤ�ҷ���ͧ����Ѻ�ҡ��ҹ���͡�ҡ��ǹ�ͧ thead-->
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