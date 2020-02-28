
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
	
	if(!createAddressList()){return false;}
	if(!createContactList()){return false;}
	document.customerForm.action = path + "/jsp/customerAction.do?do=save";
	document.customerForm.submit();
	return true;
}

function saveEdit(path) {
	if(!createAddressList()){return false;}
	if(!createContactList()){return false;}
	document.customerForm.action = path + "/jsp/customerAction.do?do=saveEdit";
	document.customerForm.submit();
	return true;
}
function saveEditCredit(path) {
	if(!createAddressList()){return false;}
	if(!createContactList()){return false;}
	document.customerForm.action = path + "/jsp/customerAction.do?do=saveEditCredit";
	document.customerForm.submit();
	return true;
}

function clearForm(path){
	document.customerForm.action = path + "/jsp/customerAction.do?do=clearForm";
	document.customerForm.submit();
	return true;
}


/** ADDRESS */
function open_address(path, rowNo, type){
	if(rowNo==null){
		var address2 = document.getElementsByName('addr.purpose').length;
		if(address2 < 2){
		   window.open(path + "/jsp/pop/addressPopup.jsp", "Address", "width=939,height=400,location=No,resizable=No");
     	}
	}else{
		window.open(path + "/jsp/pop/addressPopup.jsp?row="+rowNo+"&type="+type, "Address", "width=939,height=400,location=No,resizable=No");
	}
	return;
}

/** Address Table */
function addAddress(path,objValue){
	var jQtable = $('#tblAddress');
    jQtable.each(function(){
        var $table = $(this);
        // Number of td's in the last table row
        var n = $('tr', this).length;
        // n++;
        var className="lineO";
        if(n%2==0)
        	className="lineE";
        objValue.row=n;
       
        var tds = '<tr style="cursor: pointer; cursor: hand;"  class='+className+'>';
        tds += '<td align="left"></td>';
        tds += '<td align="center"></td>';
        tds += '<td align="center" width="80px;"></td>';
        tds += '<td align="center" width="20px;"></td>';
        //aneak.t
        //tds += '<td align="center" width="20px;"></td>';//Copy Column
        tds += '</tr>';
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    setValueToAddress(path,objValue);
}

/** Set Value to Address */
function setValueToAddress(path, objValue){
	
	var tbl = document.getElementById('tblAddress');
	
	// address object split
    var addressLabel="";
    addressLabel+=(objValue.line1)+' ';
	addressLabel+=(objValue.line2)+' ';
	if(objValue.provinceLabel=='กรุงเทพฯ'||objValue.provinceLabel=='กรุงเทพมหานคร'){
		addressLabel+="แขวง";
		addressLabel+=(objValue.line3)+' ';
		addressLabel+="เขต";
		addressLabel+=(objValue.districtLabel)+' ';
		addressLabel+="";
    }else{
    	addressLabel+="ตำบล";
    	addressLabel+=(objValue.line3)+' ';
    	addressLabel+="อำเภอ";
    	addressLabel+=(objValue.districtLabel)+' ';
    	addressLabel+="จังหวัด";
	}
	addressLabel+=(objValue.provinceLabel)+' ';
	addressLabel+=(objValue.postcode)+' ';
	
	var proposeLabel="";
	// proposeLabel+=(objValue.purpose);
	proposeLabel+=(objValue.purposeLabel);

	var statusLabel="";
	// statusLabel+=(objValue.status);
	statusLabel+=(objValue.statusLabel);
	
	var inputLabel="";
	inputLabel+="<input type='hidden' name='addr.id' value='"+objValue.id+"'>";
	inputLabel+="<input type='hidden' name='addr.row' value='"+(eval(objValue.row)+1)+"'>";
	inputLabel+="<input type='hidden' name='addr.line1' value='"+objValue.line1+"'>";
	inputLabel+="<input type='hidden' name='addr.line2' value='"+objValue.line2+"'>";
	inputLabel+="<input type='hidden' name='addr.line3' value='"+objValue.line3+"'>";
	inputLabel+="<input type='hidden' name='addr.district' value='"+objValue.district+"'>";
	inputLabel+="<input type='hidden' name='addr.districtLabel' value='"+objValue.districtLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.province' value='"+objValue.province+"'>";
	inputLabel+="<input type='hidden' name='addr.provinceLabel' value='"+objValue.provinceLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.postcode' value='"+objValue.postcode+"'>";
	inputLabel+="<input type='hidden' name='addr.purpose' value='"+objValue.purpose+"'>";
	inputLabel+="<input type='hidden' name='addr.purposeLabel' value='"+objValue.purposeLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.status' value='"+objValue.status+"'>";
	inputLabel+="<input type='hidden' name='addr.statusLabel' value='"+objValue.statusLabel+"'>";
	
	var iconLabel="";
	iconLabel+='<a href="#" onclick="open_address(\''+path+'\','+(eval(objValue.row)+1)+',\'edit\');">';
	iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	
	// aneak.t
	//var copyLabel="";
	//copyLabel+='<a href="#" onclick="open_address(\''+path+'\','+(eval(objValue.row)+1)+',\'copy\');">';
	//copyLabel+="Copy</a>";
	
	tbl.rows[objValue.row].cells[0].innerHTML=addressLabel+inputLabel;
	tbl.rows[objValue.row].cells[1].innerHTML=proposeLabel;
	tbl.rows[objValue.row].cells[2].innerHTML=statusLabel;
	tbl.rows[objValue.row].cells[3].innerHTML=iconLabel;
	//aneak.t
	//tbl.rows[objValue.row].cells[4].innerHTML=copyLabel;
	
	// Aneak.t 24/01/2011
	var rowAddr = document.getElementsByName('addr.id').length;
	document.getElementsByName('addr_id')[0].value = rowAddr;
	
	//alert(rowAddr);
	
	if(eval(document.getElementsByName('addr_id')[0].value)==1){
		//alert("copy case add");
	   copyAddress(path, objValue);
	}else{
		//alert("copy case edit");
		var a = objValue;
	   copyAddressCaseEdit(path, a);	
	}
	return true;
}

function copyAddressCaseEdit(path, objValue){
	//Bill to position = 0
	if(document.getElementsByName('addr.purpose')[0].value =="B"){
		if(objValue.purpose=='S'){
			//Current ShipTo
			objValue.row = 0;
			objValue.purpose = 'B';
			objValue.purposeLabel = 'Bill To';
			objValue.id = document.getElementsByName('addr.id')[0].value;
		}else{
			//Current BillTo
			objValue.row = 1;
			objValue.purpose = 'S';
			objValue.purposeLabel = 'Ship To';
			objValue.id = document.getElementsByName('addr.id')[1].value;
		}
	}else{
		//Bill to position = 1
		if(objValue.purpose=='S'){
			//Current ShipTo
			objValue.row = 1;
			objValue.purpose = 'B';
			objValue.purposeLabel = 'Bill To';
			objValue.id = document.getElementsByName('addr.id')[1].value;
		}else{
			//Current BillTo
			objValue.row = 0;
			objValue.purpose = 'S';
			objValue.purposeLabel = 'Ship To';
			objValue.id = document.getElementsByName('addr.id')[0].value;
		}
	}
	var tbl = document.getElementById('tblAddress');
	
	// address object split
    var addressLabel="";
    addressLabel+=(objValue.line1)+' ';
	addressLabel+=(objValue.line2)+' ';
	if(objValue.provinceLabel=='กรุงเทพฯ'||objValue.provinceLabel=='กรุงเทพมหานคร'){
		addressLabel+="แขวง";
		addressLabel+=(objValue.line3)+' ';
		addressLabel+="เขต";
		addressLabel+=(objValue.districtLabel)+' ';
		addressLabel+="";
    }else{
    	addressLabel+="ตำบล";
    	addressLabel+=(objValue.line3)+' ';
    	addressLabel+="อำเภอ";
    	addressLabel+=(objValue.districtLabel)+' ';
    	addressLabel+="จังหวัด";
	}
	addressLabel+=(objValue.provinceLabel)+' ';
	addressLabel+=(objValue.postcode)+' ';
	
	var proposeLabel="";
	// proposeLabel+=(objValue.purpose);
	proposeLabel+=(objValue.purposeLabel);

	var statusLabel="";
	// statusLabel+=(objValue.status);
	statusLabel+=(objValue.statusLabel);
	
	var inputLabel="";
	inputLabel+="<input type='hidden' name='addr.id' value='"+objValue.id+"'>";
	inputLabel+="<input type='hidden' name='addr.row' value='"+(eval(objValue.row)+1)+"'>";
	inputLabel+="<input type='hidden' name='addr.line1' value='"+objValue.line1+"'>";
	inputLabel+="<input type='hidden' name='addr.line2' value='"+objValue.line2+"'>";
	inputLabel+="<input type='hidden' name='addr.line3' value='"+objValue.line3+"'>";
	inputLabel+="<input type='hidden' name='addr.district' value='"+objValue.district+"'>";
	inputLabel+="<input type='hidden' name='addr.districtLabel' value='"+objValue.districtLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.province' value='"+objValue.province+"'>";
	inputLabel+="<input type='hidden' name='addr.provinceLabel' value='"+objValue.provinceLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.postcode' value='"+objValue.postcode+"'>";
	inputLabel+="<input type='hidden' name='addr.purpose' value='"+objValue.purpose+"'>";
	inputLabel+="<input type='hidden' name='addr.purposeLabel' value='"+objValue.purposeLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.status' value='"+objValue.status+"'>";
	inputLabel+="<input type='hidden' name='addr.statusLabel' value='"+objValue.statusLabel+"'>";
	
	var iconLabel="";
	iconLabel+='<a href="#" onclick="open_address(\''+path+'\','+(eval(objValue.row)+1)+',\'edit\');">';
	iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	
	tbl.rows[objValue.row].cells[0].innerHTML= addressLabel+inputLabel;
	tbl.rows[objValue.row].cells[1].innerHTML= proposeLabel;
	tbl.rows[objValue.row].cells[2].innerHTML= statusLabel;
	tbl.rows[objValue.row].cells[3].innerHTML= iconLabel;
}

function copyAddressCaseEdit_BK(path, objValue){
	alert(objValue.purpose);
	if(objValue.purpose=='S'){
		objValue.row = 1;
		objValue.purpose = 'B';
		objValue.purposeLabel = 'Bill To';
		objValue.id = document.getElementsByName('addr.id')[1].value;
	}else{
		objValue.row = 0;
		objValue.purpose = 'S';
		objValue.purposeLabel = 'Ship To';
		objValue.id = document.getElementsByName('addr.id')[0].value;
	}
		
	var tbl = document.getElementById('tblAddress');
	
	// address object split
    var addressLabel="";
    addressLabel+=(objValue.line1)+' ';
	addressLabel+=(objValue.line2)+' ';
	if(objValue.provinceLabel=='กรุงเทพฯ'||objValue.provinceLabel=='กรุงเทพมหานคร'){
		addressLabel+="แขวง";
		addressLabel+=(objValue.line3)+' ';
		addressLabel+="เขต";
		addressLabel+=(objValue.districtLabel)+' ';
		addressLabel+="";
    }else{
    	addressLabel+="ตำบล";
    	addressLabel+=(objValue.line3)+' ';
    	addressLabel+="อำเภอ";
    	addressLabel+=(objValue.districtLabel)+' ';
    	addressLabel+="จังหวัด";
	}
	addressLabel+=(objValue.provinceLabel)+' ';
	addressLabel+=(objValue.postcode)+' ';
	
	var proposeLabel="";
	// proposeLabel+=(objValue.purpose);
	proposeLabel+=(objValue.purposeLabel);

	var statusLabel="";
	// statusLabel+=(objValue.status);
	statusLabel+=(objValue.statusLabel);
	
	var inputLabel="";
	inputLabel+="<input type='hidden' name='addr.id' value='"+objValue.id+"'>";
	inputLabel+="<input type='hidden' name='addr.row' value='"+(eval(objValue.row)+1)+"'>";
	inputLabel+="<input type='hidden' name='addr.line1' value='"+objValue.line1+"'>";
	inputLabel+="<input type='hidden' name='addr.line2' value='"+objValue.line2+"'>";
	inputLabel+="<input type='hidden' name='addr.line3' value='"+objValue.line3+"'>";
	inputLabel+="<input type='hidden' name='addr.district' value='"+objValue.district+"'>";
	inputLabel+="<input type='hidden' name='addr.districtLabel' value='"+objValue.districtLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.province' value='"+objValue.province+"'>";
	inputLabel+="<input type='hidden' name='addr.provinceLabel' value='"+objValue.provinceLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.postcode' value='"+objValue.postcode+"'>";
	inputLabel+="<input type='hidden' name='addr.purpose' value='"+objValue.purpose+"'>";
	inputLabel+="<input type='hidden' name='addr.purposeLabel' value='"+objValue.purposeLabel+"'>";
	inputLabel+="<input type='hidden' name='addr.status' value='"+objValue.status+"'>";
	inputLabel+="<input type='hidden' name='addr.statusLabel' value='"+objValue.statusLabel+"'>";
	
	var iconLabel="";
	iconLabel+='<a href="#" onclick="open_address(\''+path+'\','+(eval(objValue.row)+1)+',\'edit\');">';
	iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	
	tbl.rows[objValue.row].cells[0].innerHTML= addressLabel+inputLabel;
	tbl.rows[objValue.row].cells[1].innerHTML= proposeLabel;
	tbl.rows[objValue.row].cells[2].innerHTML= statusLabel;
	tbl.rows[objValue.row].cells[3].innerHTML= iconLabel;
	
}


function copyAddress(path, objValue){
	if(objValue.purpose=='S'){
		objValue.purpose = 'B';
		objValue.purposeLabel = 'Bill To';
	}else{
		objValue.purpose = 'S';
		objValue.purposeLabel = 'Ship To';
	}
	addAddress(path,objValue);
}

/** Create Address Lazy List */
function createAddressList(){
	var divAddr = document.getElementById('addressList');
	var ids=document.getElementsByName('addr.id');
	var lines1=document.getElementsByName('addr.line1');
	var lines2=document.getElementsByName('addr.line2');
	var lines3=document.getElementsByName('addr.line3');
	var districts=document.getElementsByName('addr.district');
	var districtsLabel=document.getElementsByName('addr.districtLabel');
	var provinces=document.getElementsByName('addr.province');
	var provincesLabel=document.getElementsByName('addr.provinceLabel');
	var postcodes=document.getElementsByName('addr.postcode');
	var purposes=document.getElementsByName('addr.purpose');
	var purposesLabel=document.getElementsByName('addr.purposeLabel');
	var statuses=document.getElementsByName('addr.status');
	var statusesLabel=document.getElementsByName('addr.statusLabel');
	
	var inputLabel="";
	
	if(ids.length==0)
	{
		alert('ใส่ข้อมูลที่อยู่');
		return false;
	}
	
	var bshipto =false;
	var bbillto =false;
	
	divAddr.innerHTML="";
	for(i=0;i<ids.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='addresses["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].line1' value='"+lines1[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].line2' value='"+lines2[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].line3' value='"+lines3[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].line4' value='"+districtsLabel[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].district.id' value='"+districts[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].district.name' value='"+districtsLabel[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].province.id' value='"+provinces[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].province.name' value='"+provincesLabel[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].postalCode' value='"+postcodes[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].purpose' value='"+purposes[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].purposeLabel' value='"+purposesLabel[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].isActive' value='"+statuses[i].value+"'>";
		inputLabel+="<input type='text' name='addresses["+i+"].activeLabel' value='"+statusesLabel[i].value+"'>";
		
		inputLabel+="<hr/>";
		divAddr.innerHTML += inputLabel;
		//alert(inputLabel);
		
		if(purposes[i].value=='S'){
			bshipto = true;
		}
		if(purposes[i].value=='B'){
			bbillto = true;
		}
	}
	if(!bshipto){
		alert('ใส่ข้อมูลที่อยู่ แบบ Ship To');
		return false;
	}
	if(!bbillto){
		alert('ใส่ข้อมูลที่อยู่ แบบ Bill To');
		return false;
	}
	return true;
}

/** CONTACT */
function open_contact(path, rowNo){
	if(rowNo==null)
		window.open(path + "/jsp/pop/contactPopup.jsp", "Address", "width=939,height=400,location=No,resizable=No");
	else
		window.open(path + "/jsp/pop/contactPopup.jsp?row="+rowNo, "Address", "width=939,height=400,location=No,resizable=No");
	return;
}

/** Contact Table */
function addContact(path,objValue){
	var jQtable = $('#tblContact');
    jQtable.each(function(){
        var $table = $(this);
        // Number of td's in the last table row
        var n = $('tr', this).length;
        // n++;
        var className="lineO";
        if(n%2==0)
        	className="lineE";
        objValue.row=n;
       
        var tds = '<tr style="cursor: pointer; cursor: hand;" class='+className+'>';
        tds += '<td align="left" valign="top"></td>';
        tds += '<td align="left" valign="top"></td>';
        tds += '<td align="center" valign="middle" width="80px;"></td>';
        tds += '<td align="center" valign="middle" width="20px;"></td>';
        tds += '</tr>';
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    setValueToContact(path,objValue);
}

/** Set Value to Address */
function setValueToContact(path, objValue){
	var tbl = document.getElementById('tblContact');
	
	// address object split
    var contactLabel="";
    contactLabel+=(objValue.contactTo)+'<br/>';
    contactLabel+=(objValue.relation);
	
	var phoneFaxLabel="";
	phoneFaxLabel+='โทร. '+(objValue.phone);
	if(objValue.phoneSub1.length>0){
		phoneFaxLabel+='ต่อ '+(objValue.phoneSub1);
	}
	if(objValue.phone2.length>0){
		phoneFaxLabel+=', '+(objValue.phone2);
		if(objValue.phoneSub2.length>0){
			phoneFaxLabel+='ต่อ '+(objValue.phoneSub2);
		}
	}
	phoneFaxLabel+='<br>';
	phoneFaxLabel+='มือถือ. '+(objValue.mobile);
	if(objValue.mobile2.length>0){
		phoneFaxLabel+=', '+(objValue.mobile2);
	}
	phoneFaxLabel+='<br>';
	phoneFaxLabel+='แฟกซ์. '+(objValue.fax);
	
	var statusLabel="";
	statusLabel+=(objValue.statusLabel);
	
	var inputLabel="";
	inputLabel+="<input type='hidden' name='cont.id' value='"+objValue.id+"'>";
	inputLabel+="<input type='hidden' name='cont.row' value='"+(eval(objValue.row)+1)+"'>";
	inputLabel+="<input type='hidden' name='cont.contactTo' value='"+objValue.contactTo+"'>";
	inputLabel+="<input type='hidden' name='cont.relation' value='"+objValue.relation+"'>";
	inputLabel+="<input type='hidden' name='cont.phone' value='"+objValue.phone+"'>";
	inputLabel+="<input type='hidden' name='cont.fax' value='"+objValue.fax+"'>";
	inputLabel+="<input type='hidden' name='cont.status' value='"+objValue.status+"'>";
	inputLabel+="<input type='hidden' name='cont.statusLabel' value='"+objValue.statusLabel+"'>";
	inputLabel+="<input type='hidden' name='cont.phone2' value='"+objValue.phone2+"'>";
	inputLabel+="<input type='hidden' name='cont.mobile' value='"+objValue.mobile+"'>";
	inputLabel+="<input type='hidden' name='cont.mobile2' value='"+objValue.mobile2+"'>";
	inputLabel+="<input type='hidden' name='cont.phoneSub1' value='"+objValue.phoneSub1+"'>";
	inputLabel+="<input type='hidden' name='cont.phoneSub2' value='"+objValue.phoneSub2+"'>";
	
	var iconLabel="";
	iconLabel+='<a href="#" onclick="open_contact(\''+path+'\','+(eval(objValue.row)+1)+');">';
	iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	
	tbl.rows[objValue.row].cells[0].innerHTML=contactLabel+inputLabel;
	tbl.rows[objValue.row].cells[1].innerHTML=phoneFaxLabel;
	tbl.rows[objValue.row].cells[2].innerHTML=statusLabel;
	tbl.rows[objValue.row].cells[3].innerHTML=iconLabel;
	
	return true;
}

/** Create Contact Lazy List */
function createContactList(){
	var divAddr = document.getElementById('contactList');
	var ids=document.getElementsByName('cont.id');
	var contacts=document.getElementsByName('cont.contactTo');
	var relations=document.getElementsByName('cont.relation');
	var phones=document.getElementsByName('cont.phone');
	var faxs=document.getElementsByName('cont.fax');
	var statuses=document.getElementsByName('cont.status');
	var statusesLabel=document.getElementsByName('cont.statusLabel');
	
	var phones2=document.getElementsByName('cont.phone2');
	var mobiles=document.getElementsByName('cont.mobile');
	var mobiles2=document.getElementsByName('cont.mobile2');
	
	var sub1=document.getElementsByName('cont.phoneSub1');
	var sub2=document.getElementsByName('cont.phoneSub2');
	
	var inputLabel="";
	
	if(ids.length==0)
	{
		// Aneak.t 24/01/2011
		//alert('ใส่ข้อมูลผู้ติดต่อ');
		//return false;
		return true;
	}
	
	divAddr.innerHTML="";
	for(i=0;i<ids.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='contacts["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].contactTo' value='"+contacts[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].relation' value='"+relations[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].phone' value='"+phones[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].fax' value='"+faxs[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].isActive' value='"+statuses[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].activeLabel' value='"+statusesLabel[i].value+"'>";
		
		inputLabel+="<input type='text' name='contacts["+i+"].phone2' value='"+phones2[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].mobile' value='"+mobiles[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].mobile2' value='"+mobiles2[i].value+"'>";
		
		inputLabel+="<input type='text' name='contacts["+i+"].phoneSub1' value='"+sub1[i].value+"'>";
		inputLabel+="<input type='text' name='contacts["+i+"].phoneSub2' value='"+sub2[i].value+"'>";
		
		inputLabel+="<hr/>";
		divAddr.innerHTML += inputLabel;
	}
	return true;
}


function open_invoiceOst(path, id){
	window.open(path + "/jsp/pop/invoiceOutstandingPopup.jsp?id="+id, "Invoice Outstanding", "width=939,height=300,location=No,resizable=No");
}