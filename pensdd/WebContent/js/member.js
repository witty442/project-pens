function cancel_member(path, id){
	window.open(path + "/jsp/pop/cancelMemberPopup.jsp?id="+id, "Cancel Member", "width=939,height=250,location=No,resizable=No");
}

function recommented_member(path, id){
	window.open(path + "/jsp/pop/view/recommentedViewPopoup.jsp?id="+id, "Recommented Member", "width=939,height=430,location=No,resizable=No");
}

function prepare(path,type,id){
	if(id!=null){
		document.memberForm.action = path + "/jsp/memberAction.do?do=prepare&id="+id+"&action="+type;
	}else{
		document.memberForm.action = path + "/jsp/memberAction.do?do=prepare"+"&action="+type;
	}
	document.memberForm.submit();
	return true;
}

function calculateAge(objText, objText2){
	var d = new Date();
	var curYear = d.getFullYear() + 543;
	var birthYear = objText.value.split('/')[2];
	var diffYear = eval(curYear) - eval(birthYear);
	objText2.value = diffYear;
}
function search(path){
	document.memberForm.action = path + "/jsp/memberAction.do?do=search&rf=Y";
	document.memberForm.submit();
	return true;
}

function searchKeypress(e,path){
	if(e != null && e.keyCode == 13){
		search(path);
	}
}

function backsearch(path) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.memberForm.action = path + "/jsp/memberAction.do?do=search";
	} else {
		document.memberForm.action = path + "/jsp/member.do";
	}
	document.memberForm.submit();
	return true;
}

function renewsearch(path){
	var type = document.getElementsByName('type')[0].value;
	document.memberForm.action = path + "/jsp/memberRenewAction.do?do=search&type="+type;
	document.memberForm.submit();
	return true;
}

function open_renew(path, id, type){
	window.open(path + "/jsp/pop/memberRenewPopup.jsp?id="+id+"&type="+type, "Member Renew", "width=939,height=300,location=No,resizable=No");
}

function save(path) {
	if(Trim(document.getElementsByName('member.name')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.name')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.territory')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.territory')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.memberType')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.memberType')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.registerDate')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.registerDate')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.paymentTerm')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.paymentTerm')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.vatCode')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.vatCode')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.paymentMethod')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.paymentMethod')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.orderAmountPeriod')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.orderAmountPeriod')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.shippingDate')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.shippingDate')[0].focus();
		return false;
	}
	if(Trim(document.getElementsByName('member.shippingTime')[0].value)==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		document.getElementsByName('member.shippingTime')[0].focus();
		return false;
	}
	
	if($("#personIDNo").val()!='' && !checkNum($("#personIDNo"))){return false;}
	if($("#chrolesterol").val()!='' && !checkNum($("#chrolesterol"))){return false;}
	if($("#monthlyIncome").val()!='' && !checkNum($("#monthlyIncome"))){return false;}
	
	// Check sum member product
	var orderAmt = document.getElementsByName('member.orderAmountPeriod')[0].value;
	var pds = document.getElementsByName("prod.orderQty");
	var sumOrderAmt = 0;
	for(var i=0; i<pds.length; i++){
		sumOrderAmt = sumOrderAmt + parseInt(pds[i].value);
	}
	
	
	//not VIP
	//if(!document.getElementById('isvip').checked)
		//if(sumOrderAmt != orderAmt){
			//alert('จำนวนสินค้าไม่ถูกต้อง');
			//return;
		//}
	
	if(document.getElementsByName('member.totalOrderQty')[0].value <= 0 ){
		alert('กรุณาระบุจำนวนขวดรวม');
		document.getElementsByName('member.totalOrderQty')[0].focus();
		return false;
	} 
	
	if(!createAddressList()){return false;}
	if(!createContactList()){return false;}
	
	//not VIP
	if(!document.getElementById('isvip').checked)
		if(!createProductList()){return false;}
	
	document.memberForm.action = path + "/jsp/memberAction.do?do=save";
	document.memberForm.submit();
	return true;
}

function clearForm(path){
	document.memberForm.action = path + "/jsp/memberAction.do?do=clearForm";
	document.memberForm.submit();
	return true;
}
function copyMember(path){
	if(confirm("ยืนยัน Copy ข้อมูลสมาชิก")){
		if(document.getElementById('memberCodeCopy').value ==""){
			alert("กรุณาระบุ รหัสลูกค้าที่ต้องการ Copy");
			document.getElementById('memberCodeCopy').focus();
			return false;
	    }
		document.memberForm.action = path + "/jsp/memberAction.do?do=copyMember&memberCodeCopy="+document.getElementById('memberCodeCopy').value;
		document.memberForm.submit();
		return true;
	}
	return false;
}

function viewMember(path){
	if(document.getElementById('memberCodeCopy').value ==""){
		alert("กรุณาระบุ รหัสลูกค้าที่ต้องการ ดูข้อมูล");
		document.getElementById('memberCodeCopy').focus();
		return false;
    }
	document.memberForm.action = path + "/jsp/memberAction.do?do=viewMember&memberCodeCopy="+document.getElementById('memberCodeCopy').value;
	document.memberForm.submit();
	return true;
}

/** ADDRESS **/
function open_address(path, rowNo, type){
	if(rowNo==null)
		window.open(path + "/jsp/pop/addressPopup.jsp", "Address", "width=939,height=400,location=No,resizable=No");
	else
		window.open(path + "/jsp/pop/addressPopup.jsp?row="+rowNo+"&type="+type, "Address", "width=939,height=400,location=No,resizable=No");
	return;
}


/**Address Table*/
function addAddress(path,objValue){
	var jQtable = $('#tblAddress');
    jQtable.each(function(){
        var $table = $(this);
        // Number of td's in the last table row
        var n = $('tr', this).length;
        //n++;
        var className="lineO";
        if(n%2==0)
        	className="lineE";
        objValue.row=n;
       
        var tds = '<tr style="cursor: pointer; cursor: hand;" class='+className+'>';
        tds += '<td align="left"></td>';
        tds += '<td align="center"></td>';
        tds += '<td align="center" width="80px;"></td>';
        tds += '<td align="center" width="20px;"></td>';
        //aneak.t
        tds += '<td align="center" width="20px;"></td>';
        
        tds += '</tr>';
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    setValueToAddress(path,objValue);
}

/**Set Value to Address*/
function setValueToAddress(path, objValue){
	//alert(objValue.row);
	
	var tbl = document.getElementById('tblAddress');
	
	//address object split
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
	proposeLabel+=(objValue.purposeLabel);

	var statusLabel="";
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
	var copyLabel="";
	copyLabel+='<a href="#" onclick="open_address(\''+path+'\','+(eval(objValue.row)+1)+',\'copy\');">';
	copyLabel+="Copy</a>";
	
	tbl.rows[objValue.row].cells[0].innerHTML=addressLabel+inputLabel;
	tbl.rows[objValue.row].cells[1].innerHTML=proposeLabel;
	tbl.rows[objValue.row].cells[2].innerHTML=statusLabel;
	tbl.rows[objValue.row].cells[3].innerHTML=iconLabel;
	//aneak.t
	tbl.rows[objValue.row].cells[4].innerHTML=copyLabel;

	if(objValue.purpose=='S'){
		checkDDRoute(path,objValue.district, document.getElementById('registerDate').value);
		checkDDGroup(path,objValue.district);
	}
	
	return true;
}

/**Create Lazy List*/
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
	
	divAddr.innerHTML="";
	for(i=0;i<ids.length;i++){
		innerHTML="";
		inputLabel+="<input type='hidden' name='addresses["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].line1' value='"+lines1[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].line2' value='"+lines2[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].line3' value='"+lines3[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].line4' value='"+districtsLabel[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].district.id' value='"+districts[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].district.name' value='"+districtsLabel[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].province.id' value='"+provinces[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].province.name' value='"+provincesLabel[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].postalCode' value='"+postcodes[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].purpose' value='"+purposes[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].purposeLabel' value='"+purposesLabel[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].isActive' value='"+statuses[i].value+"'>";
		inputLabel+="<input type='hidden' name='addresses["+i+"].activeLabel' value='"+statusesLabel[i].value+"'>";
		
		inputLabel+="<br>";
		divAddr.innerHTML += inputLabel;
		
		if(purposes[i].value=='S'){
			bshipto = true;
		}
	}
	if(!bshipto){
		alert('ใส่ข้อมูลที่อยู่ แบบ Ship To');
		return false;
	}
	return true;
}

/**CONTACT*/
function open_contact(path, rowNo){
	if(rowNo==null)
		window.open(path + "/jsp/pop/contactPopup.jsp", "Address", "width=939,height=400,location=No,resizable=No");
	else
		window.open(path + "/jsp/pop/contactPopup.jsp?row="+rowNo, "Address", "width=939,height=400,location=No,resizable=No");
	return;
}

/**Contact Table*/
function addContact(path,objValue){
	var jQtable = $('#tblContact');
    jQtable.each(function(){
        var $table = $(this);
        // Number of td's in the last table row
        var n = $('tr', this).length;
        //n++;
        var className="lineO";
        if(n%2==0)
        	className="lineE";
        objValue.row=n;
       
        var tds = '<tr style="cursor: pointer; cursor: hand;" class='+className+'>';
        tds += '<td align="left" valign="top"></td>';
        tds += '<td align="left" valign="top"></td>';
        tds += '<td align="center" valign="middle" width="80px;"></td>';
        tds += '<td align="center" valign="middle" width="20px;"></td>';
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

/**Set Value to Address*/
function setValueToContact(path, objValue){
	var tbl = document.getElementById('tblContact');
	var index = objValue.row;
	//address object split
    var contactLabel="";
    contactLabel+=(objValue.contactTo)+'<br/>';
    if(objValue.relation != ''){
    	contactLabel+= '(' + (objValue.relation) + ')';
    }
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
	inputLabel+="<input type='hidden' name='cont.row' value='"+(eval(index)+1)+"'>";
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
	iconLabel+='<a href="#" onclick="open_contact(\''+path+'\','+(eval(index)+1)+');">';
	iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	var delIconLabel="";
	delIconLabel+='<a href="#" onclick="deleteContact(\''+path+'\','+(eval(index))+', ' + objValue.id + ');">';
	delIconLabel+="<img border=0 src='"+path+"/icons/doc_inactive.gif'></a>";
	tbl.rows[objValue.row].cells[0].innerHTML=contactLabel+inputLabel;
	tbl.rows[objValue.row].cells[1].innerHTML=phoneFaxLabel;
	tbl.rows[objValue.row].cells[2].innerHTML=statusLabel;
	tbl.rows[objValue.row].cells[3].innerHTML=iconLabel;
	tbl.rows[objValue.row].cells[4].innerHTML=delIconLabel;
	
	return true;
}

function deleteContact(path, index, Id){
	var ids = document.getElementsByName('ids_contact');
	if(Id!=0){
		ids[0].value = ids[0].value + "," + Id;
	}
	var tbl = document.getElementById('tblContact');
	var drow;
	drow = tbl.rows[index];
	$(drow).remove();
	
	var rows = tbl.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
	
	for(i=0;i<tbl.rows.length;i++){
		var editIconLabel="";
		editIconLabel+='<a href="#" onclick="open_contact(\''+path+'\','+(i + 1)+');">';
		editIconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
		tbl.rows[i].cells[3].innerHTML=editIconLabel;
		var delIconLabel="";
		delIconLabel+='<a href="#" onclick="deleteContact(\''+path+'\','+(i)+', ' + document.getElementsByName('cont.id')[i].value + ');">';
		delIconLabel+="<img border=0 src='"+path+"/icons/doc_inactive.gif'></a>";
		tbl.rows[i].cells[4].innerHTML=delIconLabel;
	}
}

/**Create Contact Lazy List*/
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
		alert('ใส่ข้อมูลผู้ติดต่อ');
		return false;
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

/** PRODUCT */
function open_product(path, rowNo){
	
	if(rowNo == null){
		window.open(path + "/jsp/pop/memberProductPopup.jsp", "Product", "width=939,height=350,location=No,resizable=No");
	}else{
		window.open(path + "/jsp/pop/memberProductPopup.jsp?row="+rowNo, "Product", "width=939,height=350,location=No,resizable=No");
	}
	return;
}

/**Product Table*/
function addProduct2(path,objValue){
	var jQtable = $('#tblProduct');
    jQtable.each(function(){
        var $table = $(this);
        // Number of td's in the last table row
        var n = $('tr', this).length;
        var className="lineO";
        if(n%2==0)
        	className="lineE";
        objValue.row=n;
       
        var tds = '<tr style="cursor: pointer; cursor: hand;" class='+className+'>';
        tds += '<td align="left"></td>';
        tds += '<td align="center" valign="middle" width="80px;"></td>';
        tds += '<td align="center" valign="middle" width="20px;"></td>';
        tds += '<td align="center" valign="middle" width="20px;"></td>';
        tds += '</tr>';
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    setValueToProduct(path,objValue);
}

function addProduct(path,objValue){
	var tbl = document.getElementById('tblProduct');
	var index = findDupIndex(tbl,objValue);
	if(index<0){
		addProduct2(path,objValue);
		return;
	}else{
		var amount = jQuery.trim(tbl.rows[index].cells[1].innerHTML).split('&nbsp;&nbsp;')[0];
		var qty = eval(amount) + eval(objValue.amount);
		tbl.rows[index].cells[1].innerHTML = qty + '&nbsp;&nbsp;' + objValue.uomLabel;
		document.getElementsByName('prod.orderQty')[index].value = qty;
	}
}

function findDupIndex(tbl,objValue){
	var i;
	var index = -1;
	for(i=0;i<tbl.rows.length;i++){
		if(i!=0){
			if(document.getElementsByName('prod.product.code')[i-1].value==objValue.pCode){
				if(document.getElementsByName('prod.uomId')[i-1].value==objValue.uomId){
					index = (i-1);
				}
			}
		}
	}
	return index;
}

/**Set Value to Product*/
function setValueToProduct(path, objValue){
	var tbl = document.getElementById('tblProduct');
    var productLabel = "";
    var amountLabel = "";
    var index = objValue.row;
    
    productLabel += objValue.pCode + ' '+ objValue.pName + ' ';
    amountLabel += (objValue.amount) + ' ' + (objValue.uomLabel);
	
	var inputLabel="";
	inputLabel+="<input type='hidden' name='prod.id' value='" + objValue.id + "'/>";
	inputLabel+="<input type='hidden' name='prod.product.id' value='" + objValue.pId + "'>";
	inputLabel+="<input type='hidden' name='prod.row' value='" + (eval(index) + 1) + "'>";
	inputLabel+="<input type='hidden' name='prod.product.code' value='" + objValue.pCode + "'>";
	inputLabel+="<input type='hidden' name='prod.product.name' value='" + objValue.pName + "'>";
	inputLabel+="<input type='hidden' name='prod.uomId' value='" + objValue.uomId + "'>";
	inputLabel+="<input type='hidden' name='prod.uomLabel' value='" + objValue.uomLabel + "'>";
	inputLabel+="<input type='hidden' name='prod.orderQty' value='" + objValue.amount + "'>";
	
	var editIconLabel="";
	editIconLabel+='<a href="#" onclick="open_product(\''+path+'\','+(eval(index)+1)+');">';
	editIconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	var delIconLabel="";
	delIconLabel+='<a href="#" onclick="deleteProduct(\''+path+'\','+(eval(index))+', ' + objValue.id + ');">';
	delIconLabel+="<img border=0 src='"+path+"/icons/doc_inactive.gif'></a>";
	
	tbl.rows[index].cells[0].innerHTML = productLabel + inputLabel; 
	tbl.rows[index].cells[1].innerHTML = amountLabel;
	tbl.rows[index].cells[2].innerHTML = editIconLabel;
	tbl.rows[index].cells[3].innerHTML = delIconLabel;
	
	return true;
}

function deleteProduct(path, index, Id){
	var ids = document.getElementsByName('ids');
	if(Id!=0){
		ids[0].value = ids[0].value + "," + Id;
	}
	var tbl = document.getElementById('tblProduct');
	var drow;
	drow = tbl.rows[index];
	$(drow).remove();
	
	var rows = tbl.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
	
	for(i=0;i<tbl.rows.length;i++){
		var editIconLabel="";
		editIconLabel+='<a href="#" onclick="open_product(\''+path+'\','+(i + 1)+');">';
		editIconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
		tbl.rows[i].cells[2].innerHTML=editIconLabel;
		var delIconLabel="";
		delIconLabel+='<a href="#" onclick="deleteProduct(\''+path+'\','+(i)+', ' + document.getElementsByName('prod.id')[i].value + ');">';
		delIconLabel+="<img border=0 src='"+path+"/icons/doc_inactive.gif'></a>";
		tbl.rows[i].cells[3].innerHTML=delIconLabel;
	}
}

/**Create Product Lazy List*/
function createProductList(){
	var divProd = document.getElementById('productList');
	var ids = document.getElementsByName('prod.id');
	var pId = document.getElementsByName('prod.product.id');
	var pCode = document.getElementsByName('prod.product.code');
	var pName = document.getElementsByName('prod.product.name');
	var uomId = document.getElementsByName('prod.uomId');
	var uomLabel = document.getElementsByName('prod.uomLabel');
	var orderQty = document.getElementsByName('prod.orderQty');
	
	
	var inputLabel="";
	if(ids.length==0)
	{
		alert('ใส่ข้อมูลสินค้า');
		return false;
	}
	
	divProd.innerHTML = "";
	for(i=0;i<ids.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='memberProducts["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='memberProducts["+i+"].product.id' value='"+pId[i].value+"'>";
		inputLabel+="<input type='text' name='memberProducts["+i+"].product.code' value='"+pCode[i].value+"'>";
		inputLabel+="<input type='text' name='memberProducts["+i+"].product.name' value='"+pName[i].value+"'>";
		inputLabel+="<input type='text' name='memberProducts["+i+"].uomId' value='"+uomId[i].value+"'>";
		inputLabel+="<input type='text' name='memberProducts["+i+"].uomLabel' value='"+uomLabel[i].value+"'>";
		inputLabel+="<input type='text' name='memberProducts["+i+"].orderQty' value='"+orderQty[i].value+"'>";
		
		inputLabel+="<hr/>";
		divProd.innerHTML += inputLabel;
	}
	return true;
}




function open_renewHist(path, id){
	window.open(path + "/jsp/pop/memberRenewHistoryPopup.jsp?id="+id, "Member Renew History", "width=939,height=300,location=No,resizable=No");
}

function checkDDRoute(path,districtId,registerdate){
	$(function(){
		var getData = $.ajax({
			url: path+"/jsp/ajax/DDRouteQuery.jsp",
			data : "districtId=" + districtId
			+ "&registerDate=" + registerdate,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				$('#shippingDate').val(returnString);
			}
		}).responseText;
	});
}

function checkDDGroup(path,districtId){
	$(function(){
		var getData = $.ajax({
			url: path+"/jsp/ajax/DDGroupQuery.jsp",
			data : "districtId=" + districtId,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				//alert(returnString);
				$('#deliveryGroup').val(returnString);
			}
		}).responseText;
	});
}

function editAddressRow(rowNo){
	editAddress('${pageContext.request.contextPath}', rowNo);
}

function changeRecommend(val){
	var type = jQuery.trim(val);
	$('#mCode').val('');
	$('#mName').val('');
	if(type == 'M'){
		$('#lookMember').show();
		$('#mCode').attr("disabled", false);
		$('#mName').attr("readOnly", true);
		$('#mName').attr("class", 'disableText');
	}else if(type == 'O'){
		$('#lookMember').hide();
		$('#mCode').attr("disabled", true);
		$('#mName').attr("readOnly", false);
		$('#mName').attr("class", '');
	}	
}

function showMember(path, id){
	window.open(path + "/jsp/pop/view/memberViewPopup.jsp?id="+id, "Member List", "width=500,height=350,location=No,resizable=No");
}

function setMember(code, name){
	$('#mCode').val(code);
	$('#mName').val(name);
	loadMember(null);
}

function changeVIP(vipchecked){
	if(vipchecked){
		$('#trproduct').hide();
		$('#trLineProduct').hide();
	}
	else{
		$('#trproduct').show();
		$('#trLineProduct').show();
	}
}