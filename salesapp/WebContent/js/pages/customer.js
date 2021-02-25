
function manageProdShowTT(path,customerCode){
	var param  = "&customerCode="+customerCode;
	    param += "&fromPage=customerSearch";
	document.customerForm.action = path + "/jsp/prodshow/prodShowSearch.jsp?action=new"+param;
	document.customerForm.submit();
	return true;
}

function showImage(path,customerId){
	 var location= $("#imageFileName").val();
		//alert(lat+","+lng);
	 if(location != "" ){
		var width= window.innerWidth-100;
		var height= window.innerHeight-100;
	
		PopupCenter(path+"/jsp/customer/dispImageLocal.jsp?customerId="+customerId, "แสดงรูปภาพ",width,height);
			
	}else{
		alert("ยังไม่ได้บันทึกข้อมูลรูปภาพ'");
	}
}
  
 /** Display image after upload **/
	function readURL(input) {
      if (input.files && input.files[0]) {
          var reader = new FileReader();

          reader.onload = function (e) {
              $('#blah')
                  .attr('src', e.target.result)
                  .width(150)
                  .height(200);
          };
          reader.readAsDataURL(input.files[0]);
          
          //Hide old images
           //$("imageDBDiv").hide();
          setImageVisible("imageDB",false);
      }
  }
	
function setImageVisible(id, visible) {
	var img = document.getElementById(id);
	img.style.visibility = (visible ? 'visible' : 'hidden');
}
/********************************************/

function prepare(path,type,id){
	if(id!=null){
		document.customerForm.action = path + "/jsp/customerAction.do?do=prepare&id=" + id+"&action="+type;
	}else{
		document.customerForm.action = path + "/jsp/customerAction.do?do=prepare"+"&action="+type;
	}
	document.customerForm.submit();
	return true;
}


function search(path){
	document.customerForm.action = path + "/jsp/customerAction.do?do=search&rf=Y";
	document.customerForm.submit();
	return true;
}

function backsearch(path) {
	var tf = document.getElementsByName('tf')[0].value;
	
	if(tf=='Y'){
		if (document.getElementsByName("criteria.searchKey")[0].value != '') {
			document.customerForm.action = path + "/jsp/tripAction.do?do=search&type=user";
		} else {
			document.customerForm.action = path + "/jsp/trip.do";
		}
	}else{
		if (document.getElementsByName("criteria.searchKey")[0].value != '') {
			document.customerForm.action = path + "/jsp/customerAction.do?do=search";
		} else {
			document.customerForm.action = path + "/jsp/customer.do";
		}
	}
	
	document.customerForm.submit();
	return true;
}

function save(path) {
    alert('save');
	if(Trim(document.getElementsByName('customer.name')[0].value)=='')
	{
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('customer.name')[0].focus();
		return false;
	}
	
	//alert(Trim(document.getElementsByName('customer.name')[0].value).length);
	
	if(Trim(document.getElementsByName('customer.name')[0].value).length > 100)
	{
		alert('กรุณาระบุชื่อใหม่ เนื่องจากมีความยาวเกินกว่าที่ระบบตั้งค่าไว้');
		document.getElementsByName('customer.name')[0].focus();
		return false;
	}
	
	if(document.getElementsByName('customer.tripDay')[0].value == '')
	{
		alert('กรุณาระบุ กำหนดจุด #1 ');
		document.getElementsByName('customer.tripDay')[0].focus();
		return false;
	}
	if(document.getElementsByName('customer.businessType')[0].value == '')
	{
		alert('กรุณาระบุ ประเภท');
		document.getElementsByName('customer.businessType')[0].focus();
		return false;
	}
	
	//Print Branch check store no 
	if(document.getElementsByName('customer.printType')[1].checked 
			&& document.getElementsByName('customer.printBranchDesc')[0].value ==""){
		alert("กรุณาระบุสาขาที่ ");
		document.getElementsByName('customer.printBranchDesc')[0].focus();
		return false;
	}

	document.customerForm.action = path + "/jsp/customerAction.do?do=save";
	document.customerForm.submit();
	return true;
}

function saveEdit(path) {

	//Print Branch check store no 
	if(document.getElementsByName('customer.printType')[1].checked 
			&& document.getElementsByName('customer.printBranchDesc')[0].value ==""){
		alert("กรุณาระบุสาขาที่ ");
		document.getElementsByName('customer.printBranchDesc')[0].focus();
		return false;
	}
	
	document.customerForm.action = path + "/jsp/customerAction.do?do=saveEdit";
	document.customerForm.submit();
	return true;
}
function saveEditCredit(path) {
	document.customerForm.action = path + "/jsp/customerAction.do?do=saveEditCredit";
	document.customerForm.submit();
	return true;
}

function clearForm(path){
	document.customerForm.action = path + "/jsp/customerAction.do?do=clearForm";
	document.customerForm.submit();
	return true;
}

function open_invoiceOst(path, id){
	window.open(path + "/jsp/pop/invoiceOutstandingPopup.jsp?id="+id, "Invoice Outstanding", "width=939,height=300,location=No,resizable=No");
}