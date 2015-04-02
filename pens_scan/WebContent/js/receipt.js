function cancelReceipt(path){
	var remark = document.getElementsByName('receipt.reason');
	
	if(remark[0].value == ''){
		alert("กรุณากรอกเหตุผลในการยกเลิก");
		remark[0].focus();
		return false;
	}
	if(remark[0].value.length > 300){
		alert("ข้อความยาวเกินกว่าที่ระบบตั้งไว้ กรุณากรอกเหตุผลในการยกเลิกใหม่");
		remark[0].focus();
		return false;
	}
	document.receiptForm.action = path + "/jsp/receiptAction.do?do=cancelReceipt";
    document.receiptForm.submit();
    return true;
}

function openCancelReceiptPopup(path,receiptNo,id){
	window.open(path + "/jsp/pop/cancelReceiptPopup.jsp?id="+id+"&receiptNo="+receiptNo, "Cancel Receipt.", "width=400,height=260,location=No,resizable=No");
}

function prepare(path, type, id) {
	if (id != null) {
		document.receiptForm.action = path
				+ "/jsp/receiptAction.do?do=prepare&id=" + id + "&action="
				+ type;
	} else {
		document.receiptForm.action = path + "/jsp/receiptAction.do?do=prepare"
				+ "&action=" + type;
	}
	document.receiptForm.submit();
	return true;
}

function prepareMR(path, type, id) {
	if (id != null) {
		document.receiptForm.action = path
				+ "/jsp/memberReceiptAction.do?do=prepareMR&id=" + id + "&action="
				+ type;
	} else {
		document.receiptForm.action = path + "/jsp/memberReceiptAction.do?do=prepareMR"
				+ "&action=" + type;
	}
	document.receiptForm.submit();
	return true;
}

function search(path){
	if(!datedifference(document.getElementById('receiptDateFrom'),document.getElementById('receiptDateTo'))){return false;}
	document.receiptForm.action = path + "/jsp/receiptAction.do?do=search&rf=Y";
	document.receiptForm.submit();
	return true;
}

function backsearch(path,customerId) {
	if (document.getElementsByName("criteria.searchKey")[0].value != '') {
		document.receiptForm.action = path + "/jsp/receiptAction.do?do=search";
	} else {
		document.receiptForm.action = path + "/jsp/receiptAction.do?do=prepare&customerId="+customerId;
	}
	document.receiptForm.submit();
	return true;
}

function clearForm(path){
	document.receiptForm.action = path + "/jsp/receiptAction.do?do=clearForm";
	document.receiptForm.submit();
	return true;
}

function backToCusotmer(path,customerId) {
	var orderType = document.getElementsByName("receipt.orderType")[0].value;
	if(orderType=='DD'){
		window.location = path+'/jsp/memberAction.do?do=prepare&id='+customerId+'&action=process';
	}else{
		window.location = path+'/jsp/customerAction.do?do=prepare&id='+customerId+'&action=process';
	}
}

function add_bill(path, id) {
	// window.open(path + "/jsp/pop/billPopup.jsp?id=" + id, "Bill",
	// "width=650,height=400,location=No,resizable=No");
	alert('addBill');
	
	window.open(path + "/jsp/pop/billPopup2.jsp?id=" + id, "Bill",
	"width=939,height=400,location=No,resizable=No");
	return;
}

function open_bill(path, rowNo) {
	
	//alert('openBill');
	
	//var screen_height= $(window).height();
	var screen_width = $(window).width()-10;
	
	// get selected id
	var selected="";
	var objtxt = document.getElementsByName('bill.orderId');
	for(var i=0;i<objtxt.length;i++)
	{
		selected+=','+objtxt[i].value;
	}
	if(selected.length>0)
		selected = selected.substring(1, selected.length);
	
	var customerId = document.getElementsByName('receipt.customerId')[0].value;
	
	if (rowNo == null)
// window.open(path + "/jsp/pop/billPopup.jsp?selected="+selected, "Bill",
// "width=650,height=400,location=No,resizable=No");
		window.open(path + "/jsp/pop/billPopup2.jsp?selected="+selected+"&cust="+customerId, "Bill",
			"width="+screen_width+",height=400,location=0");
	else
// window.open(path + "/jsp/pop/billPopup.jsp?row=" +
// rowNo+"&selected="+selected, "Bill",
// "width=650,height=400,location=No,resizable=No");
		window.open(path + "/jsp/pop/billPopup2.jsp?row=" + rowNo+"&selected="+selected+"&cust="+customerId, "Bill",
			"width="+screen_width+",height=400,location=0");
	return;
}

function editBill(path, rowId){
	var tbl = document.getElementById('tblBill');
	tbl.rows[rowId].cells[6].innerHTML = '';
	tbl.rows[rowId].cells[6].innerHTML += "<input type='text' id='editPaid' name='editPaid' size=10 style='text-align:right;' value='"+ document.getElementsByName('bill.paidAmt')[rowId-1].value +"' onkeydown='return isNum0to9andpoint(this, event);' onblur='calculateRemain(this.value,"+rowId+");'>";
	
	$('#editPaid').focus();
}

function calculateRemain(paid,row){
	var tbl = document.getElementById('tblBill');
	row--;
	var crd = document.getElementsByName('bill.creditAmt')[row].value;
	
	if(crd-paid<0 || paid==0){
		alert('ใส่จำนวนเงินไม่ถูกต้อง');
		$('#editPaid').focus();
		$('#editPaid').select();
		return false;
	}
	
	document.getElementsByName('bill.paidAmt')[row].value = paid;
	document.getElementsByName('bill.remainAmt')[row].value = crd-paid;
	
	tbl.rows[row+1].cells[6].innerHTML = addCommas(Number(paid).toFixed(2));
	tbl.rows[row+1].cells[7].innerHTML = addCommas(Number(crd-paid).toFixed(2));
	
	return true;
}

function deleteBill(path){
	var tbl = document.getElementById('tblBill');
	var chk = document.getElementsByName("billids");
	var delId = document.getElementsByName('deletedId')[0];
	var billIds = document.getElementsByName('bill.orderId');
	var drow;
	var bcheck=false;
	var applyId = document.getElementsByName('pb.allBillId');
	var allId;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			
			// check with apply
			for(var j=0;j<applyId.length;j++){
				allId = ','+applyId[j].value+',';
				// alert(allId);
				if(allId.indexOf(','+billIds[i].value+',')!=-1){
					alert('ไม่สามารถลบรายการใบแจ้งหนี้ได้ เนื่องจากมีการตัดชำระอยู่');
					return false;
				}
			}
			
			if(chk[i].value!=0)
				delId.value+=","+chk[i].value;
			// alert(i);
			drow = tbl.rows[i+1];
			$(drow).remove();
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	chk = document.getElementsByName("billids");
	var iconLabel="";
	for(var i=0;i<chk.length;i++){
		tbl.rows[i+1].cells[0].innerHTML=(i+1);
		iconLabel = "";
		iconLabel += '<a href="#" onclick="editBill(\'' + path + '\','	+ (i+1) + ');">';
		iconLabel += "<img border=0 src='" + path + "/icons/doc_edit.gif'></a>";
		tbl.rows[i+1].cells[8].innerHTML = iconLabel;
	}
}

function save(path,type) {

	if(document.getElementsByName('receipt.internalBank')[0].value==''){
		alert('ใส่ข้อมูลการฝากเงินเข้าบัญชี');
		document.getElementsByName('receipt.internalBank')[0].focus();
		return false;
	}
	if(!createBillList()){return false;}
	if(!createBysList()){return false;}
	createCNSList();
	
	//if($("#applyAmount").val()=='' || Number($("#applyAmount").val())==0){
	
	var totalBillAmt = eval($("#totalBillAmount").val());
	var totalCNAmt = eval($("#totalCNAmount").val());
	
	var totalReceiptAmt = totalBillAmt+totalCNAmt;
	
	/** wit edit : 21/11/2012  not check remainAmount**/
	/*if($("#applyAmount").val()=='' || (Number($("#applyAmount").val())==0 && totalReceiptAmt != 0 ))
	{
		alert('ไม่มียอดการตัดชำระ กรุณาตัดชำระใบแจ้งหนี้');return false;
	}*/
	
	
	if(type=='VAN'){
		if(Number($("#remainAmount").val())!=0){ 
			alert('ยอดตัดชำระไม่ครบถ้วน   คงเหลือ '+ addCommas(Number($("#remainAmount").val()).toFixed(2)));return false;
		}
	}
	/** validate case TT **/
	if(validateReceiptByAmtCash_USER_TT(type) == false){
		return false;
	}
	
	document.receiptForm.action = path + "/jsp/receiptAction.do?do=save";
	document.receiptForm.submit();
	return true;
}

/**WIT : Validate Receipt BY Case Pay by cash and Amount < 1 Must check to Sell pay **/
function validateReceiptByAmtCash_USER_TT(type){
	var r = true;
	if(type =='TT'){
		var recAmts = document.getElementsByName('pb.recAmount');
		var writeOff = document.getElementsByName('pb.writeOff');
		var payMethod = document.getElementsByName('pb.method');
		//alert(recAmts.length +":"+recAmts[0].value+":"+recAmts[1].value);
		
		var totalBillAmount = eval($("#totalBillAmount").val());
		var totalCNAmount = eval($("#totalCNAmount").val());
		
		var isZero = ((totalBillAmount+totalCNAmount)==0);
		
		if(recAmts != null){
			for(var i=0;i<recAmts.length;i++){
				//alert(recAmts[i].value+":"+Number(recAmts[i].value).toFixed(2)+":"+checkAmtLessthan1(Number(recAmts[i]).toFixed(2)) +":WriteOff:"+writeOff[i].checked+",isZero["+isZero+"]");
				//alert(payMethod[i].value);
				if(payMethod[i].value == 'CS'){
					if(checkAmtLessthan1(Number(recAmts[i].value).toFixed(2)) && writeOff[i].checked == false ){
						alert("กรุณาระบุข้อมูลให้ถูกต้อง ในกรณีที่จำนวนเงินเป็นเศษ(บันทึกเซลล์จ่าย)");
						r = false;
						break;
					}//if 
					else if(checkAmtmorethan1(Number(recAmts[i].value).toFixed(2)) && writeOff[i].checked == true ){
						alert("กรุณาระบุข้อมูลให้ถูกต้อง ในกรณีที่จำนวนเงินเป็นเศษ(บันทึกเซลล์จ่าย) ยอดต้องน้อยกว่า 0");
						r = false;
						break;
					}
					
				}//if
			}//for
		}//if
	}//if
	return r;
}

function addBill(path, arrayVal) {

	var jQtable = $('#tblBill');

	for (var i = 0; i < arrayVal.length; i++) {
		jQtable.each(function() {
			var $table = $(this);
			// Number of td's in the last table row
				var n = $('tr', this).length;
				// n++;
				// alert(n);
				var className = "lineO";
				if (n % 2 == 0)
					className = "lineE";
				arrayVal[i].rows = n;

				var tds = '<tr class=' + className + '>';
				tds += '<td align="center"></td>';
				tds += '<td align="center"></td>';
				tds += '<td align="center"></td>';
				tds += '<td align="center"></td>';
				tds += '<td align="right"></td>';
				tds += '<td align="right"></td>';
				tds += '<td align="right"></td>';
				tds += '<td align="right"></td>';
				// tds += '<td align="center"></td>';
				tds += '</tr>';
				if ($('tbody', this).length > 0) {
					$('tbody', this).append(tds);
				} else {
					$(this).append(tds);
				}
			});
	}
	setValueToBill(path, arrayVal);
}

/** Set Value to Bill */
function setValueToBill(path, arrayVal) {

	var tbl = document.getElementById('tblBill');
	var checkBoxLabel;
	var iconLabel;
	var inputLabel;
	for (var i = 0; i < arrayVal.length; i++) {

		checkBoxLabel = '<input type="checkbox" name="billids" value="0"/>';

		iconLabel = "";
		iconLabel += '<a href="#" onclick="editBill(\'' + path + '\','	+ arrayVal[i].rows + ');">';
		iconLabel += "<img border=0 src='" + path + "/icons/doc_edit.gif'  align='absmiddle'></a>";

		inputLabel="";
		inputLabel+="<input type='hidden' name='bill.id' value='0'>";
		inputLabel+="<input type='hidden' name='bill.orderId' value='"+arrayVal[i].orderId+"'>";
		inputLabel+="<input type='hidden' name='bill.invoiceNo' value='"+arrayVal[i].invoiceNo+"'>";
		inputLabel+="<input type='hidden' name='bill.salesOrderNo' value='"+arrayVal[i].salesOrderNo+"'>";
		inputLabel+="<input type='hidden' name='bill.netAmt' value='"+arrayVal[i].netAmount+"'>";
		inputLabel+="<input type='hidden' name='bill.creditAmt' value='"+arrayVal[i].creditAmount+"'>";
		inputLabel+="<input type='hidden' name='bill.paidAmt' value='"+arrayVal[i].paidAmount+"'>";
		inputLabel+="<input type='hidden' name='bill.remainAmt' value='"+arrayVal[i].remainAmount+"'>";
		
		tbl.rows[arrayVal[i].rows].cells[0].innerHTML = arrayVal[i].rows;
		tbl.rows[arrayVal[i].rows].cells[1].innerHTML = checkBoxLabel;
		tbl.rows[arrayVal[i].rows].cells[2].innerHTML = arrayVal[i].invoiceNo+inputLabel;
		tbl.rows[arrayVal[i].rows].cells[3].innerHTML = arrayVal[i].salesOrderNo;
		tbl.rows[arrayVal[i].rows].cells[4].innerHTML = addCommas(Number(arrayVal[i].netAmount).toFixed(2));
		tbl.rows[arrayVal[i].rows].cells[5].innerHTML = addCommas(Number(arrayVal[i].creditAmount).toFixed(2));
		tbl.rows[arrayVal[i].rows].cells[6].innerHTML = addCommas(Number(arrayVal[i].paidAmount).toFixed(2));
		tbl.rows[arrayVal[i].rows].cells[7].innerHTML = addCommas(Number(arrayVal[i].remainAmount).toFixed(2));
		// tbl.rows[arrayVal[i].rows].cells[8].innerHTML = iconLabel;
	
	}

	calculateAll();
	
	return true;
}

/** Create Bill Lazy List */
function createBillList(){
	var divlines = document.getElementById('BillList');
	var ids=document.getElementsByName('bill.id');
	var orderIds=document.getElementsByName('bill.orderId');
	var invoices=document.getElementsByName('bill.invoiceNo');
	var orders=document.getElementsByName('bill.salesOrderNo');
	var netamts=document.getElementsByName('bill.netAmt');
	var creditamts=document.getElementsByName('bill.creditAmt');
	var paidamts=document.getElementsByName('bill.paidAmt');
	var remainamts=document.getElementsByName('bill.remainAmt');
	
	// var totalPaid=0;
	
	var inputLabel="";
	
	if(ids.length==0)
	{
		alert('ใส่ข้อมูลใบแจ้งหนี้');
		return false;
	}
	
	divlines.innerHTML="";
	for(var i=0;i<ids.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='lines["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].order.id' value='"+orderIds[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].arInvoiceNo' value='"+invoices[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].salesOrderNo' value='"+orders[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].invoiceAmount' value='"+netamts[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].creditAmount' value='"+creditamts[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].paidAmount' value='"+paidamts[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].remainAmount' value='"+remainamts[i].value+"'>";
		
		inputLabel+="<hr/>";
		divlines.innerHTML += inputLabel;
		
		// totalPaid+=Number(paidamts[i].value);
	}
// if(totalPaid!=Number(document.getElementsByName('receipt.receiptAmount')[0].value)){
// alert('จำนวนเงินรับชำระไม่เท่ากับจำนวนเงินรวมในรายการ');
// document.getElementsByName('receipt.receiptAmount')[0].focus();
// return false;
// }
	return true;
}


function open_recpBy(path, rowNo) {
	// get selected id
	var selected="";
	var screen_width = $(window).width()-10;
	
	window.open(path + "/jsp/pop/payByPopup.jsp?selected="+selected, "Bill",
		"width="+screen_width+",height=400,location=No,resizable=No");
	return;
}

function addReceiptBy(path, payBy) {
	
	var jQtable = $('#tblRecpBy');

	jQtable.each(function() {
		var $table = $(this);
		// Number of td's in the last table row
			var n = $('tr', this).length;
			// n++;
			// alert(n);
			var className = "lineO";
			if (n % 2 == 0)
				className = "lineE";
			payBy.rows = n;

			var tds = '<tr class=' + className + '>';
			tds += '<td align="center"></td>';
			tds += '<td align="center"></td>';
			tds += '<td align="center"></td>';
			tds += '<td align="right"></td>';
			tds += '<td align="left"></td>';
			tds += '<td align="left"></td>';
			tds += '<td align="center"></td>';
			tds += '<td align="center"></td>';
			tds += '<td align="center"></td>';
			if(document.getElementsByName('receipt.orderType')[0].value!='DD'){
			tds += '<td align="center"></td>';
			}
			tds += '</tr>';
			if ($('tbody', this).length > 0) {
				$('tbody', this).append(tds);
			} else {
				$(this).append(tds);
			}
		});
	setValueToReceitpBy(path, payBy);
}

function setValueToReceitpBy(path, payBy){
	var tbl = document.getElementById('tblRecpBy');
	var checkBoxLabel;
	var iconLabel;
	var inputLabel;

	checkBoxLabel = '<input type="checkbox" name="recpbyids" value="0"/>';

	iconLabel = "";
	iconLabel += '<a href="#" onclick="applyBill(\'' + path + '\','	+ payBy.rows + ',\''+payBy.methodName+'\','+payBy.recAmount+',\''+payBy.seedId+'\');">';
	iconLabel += "<img border=0 src='" + path + "/icons/doc_edit.gif'  align='absmiddle'></a>";

	inputLabel="";
	inputLabel+="<input type='hidden' name='pb.id' value='0'>";
	inputLabel+="<input type='hidden' name='pb.method' value='"+payBy.method+"'>";
	inputLabel+="<input type='hidden' name='pb.methodName' value='"+payBy.methodName+"'>";
	inputLabel+="<input type='hidden' name='pb.recAmount' value='"+payBy.recAmount+"'>";
	inputLabel+="<input type='hidden' name='pb.bank' value='"+payBy.bank+"'>";
	inputLabel+="<input type='hidden' name='pb.bankName' value='"+payBy.bankName+"'>";
	inputLabel+="<input type='hidden' name='pb.chqNo' value='"+payBy.chqNo+"'>";
	inputLabel+="<input type='hidden' name='pb.chqDate' value='"+payBy.chqDate+"'>";
	inputLabel+="<input type='hidden' name='pb.creditType' value='"+payBy.creditType+"'>";
	inputLabel+="<input type='hidden' name='pb.creditTypeName' value='"+payBy.creditTypeName+"'>";
	inputLabel+="<input type='hidden' name='pb.seedId' value='"+payBy.seedId+"'>";
	inputLabel+="<input type='hidden' name='pb.allBillId' >";
	inputLabel+="<input type='hidden' name='pb.allPaid' >";
	inputLabel+="<input type='hidden' name='pb.allCNId' >";
	inputLabel+="<input type='hidden' name='pb.allCNPaid' >";
	
	tbl.rows[payBy.rows].cells[0].innerHTML = payBy.rows;
	tbl.rows[payBy.rows].cells[1].innerHTML = checkBoxLabel;
	tbl.rows[payBy.rows].cells[2].innerHTML = payBy.methodName+inputLabel;
	tbl.rows[payBy.rows].cells[3].innerHTML = addCommas(Number(payBy.recAmount).toFixed(2));
	tbl.rows[payBy.rows].cells[4].innerHTML = payBy.bankName;
	tbl.rows[payBy.rows].cells[5].innerHTML = payBy.chqNo;
	tbl.rows[payBy.rows].cells[6].innerHTML = payBy.chqDate;
	tbl.rows[payBy.rows].cells[7].innerHTML = payBy.creditTypeName;
	if(payBy.method=='CS'){
		//if(checkAmtLessthan1(Number(payBy.recAmount).toFixed(2))){ Comment Out : Write Off When Amount Less Than Zero
		if (checkAmtLessthanZero(Number(payBy.recAmount).toFixed(2))){
		   tbl.rows[payBy.rows].cells[8].innerHTML = '<input type="checkbox" name="pb.writeOff" value="Y" checked onClick="validateCNForWriteOff(\''+payBy.seedId+'\')" >';
		}else{
		   tbl.rows[payBy.rows].cells[8].innerHTML = '<input type="checkbox" name="pb.writeOff" value="Y" onClick="validateCNForWriteOff(\''+payBy.seedId+'\')" >';
		}
	}else{
		tbl.rows[payBy.rows].cells[8].innerHTML = '<input type="checkbox" name="pb.writeOff" value="Y" disabled="disabled" class="disableText">';
	}
	
	if(document.getElementsByName('receipt.orderType')[0].value!='DD'){
	tbl.rows[payBy.rows].cells[9].innerHTML = iconLabel;
	}
	
	calculateAll();
	
	return true;
}

function checkAmtLessthanZero(amt){
	if(amt < 0){
	  return true;
	}
	return false;
}

function checkAmtLessthan1(amt){
	if(amt < 1 && amt != 0){
	  return true;
	}
	return false;
}

function checkAmtmorethan1(amt){
	if(amt > 1){
	  return true;
	}
	return false;
}

function deleteRecpBy(path){
	var tbl = document.getElementById('tblRecpBy');
	var chk = document.getElementsByName("recpbyids");
	var delId = document.getElementsByName('deletedRecpById')[0];
	var drow;
	var bcheck=false;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			if(chk[i].value!=0)
				delId.value+=","+chk[i].value;
			// alert(i);
			drow = tbl.rows[i+1];
			$(drow).remove();
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	chk = document.getElementsByName("recpbyids");
	var iconLabel="";
	var mt;
	var rca;
	var ss;
	for(var i=0;i<chk.length;i++){
		tbl.rows[i+1].cells[0].innerHTML=(i+1);
		mt = document.getElementsByName('pb.methodName')[i];
		rca = document.getElementsByName('pb.recAmount')[i];
		ss = document.getElementsByName('pb.seedId')[i];
		
		iconLabel = "";
		iconLabel += '<a href="#" onclick="applyBill(\'' + path + '\','	+ (i+1) + ',\''+mt.value+'\','+rca.value+',\''+ss.value+'\');">';
		iconLabel += "<img border=0 src='" + path + "/icons/doc_edit.gif'  align='absmiddle'></a>";
		
		tbl.rows[i+1].cells[7].innerHTML = iconLabel;
	}
	
	calculateAll();
}

function calculateAll(){
	
	// Total Bill Amount
	var creditAmt = document.getElementsByName('bill.creditAmt');
	var totalBillAmt=0;
	for(var i=0;i<creditAmt.length;i++){
		totalBillAmt+=Number(creditAmt[i].value);
	}
	if(creditAmt.length==0){
		//worse case
		var netAmt = document.getElementsByName('bill.netAmt');
		for(i=0;i<netAmt.length;i++){
			totalBillAmt+=Number(netAmt[i].value);
		}
	}
	document.getElementById('totalBillAmount').value = Number(totalBillAmt).toFixed(2); 
	document.getElementById('totalBillAmountFMT').value = addCommas(Number(totalBillAmt).toFixed(2));
	
	// Total CN Amount
	var cnCreditAmt = document.getElementsByName('cn.creditAmt');
	var totalCNAmt=0;
	for(var i=0;i<cnCreditAmt.length;i++){
		totalCNAmt+=Number(cnCreditAmt[i].value);
	}
	
	if(document.getElementById('totalCNAmount'))
		document.getElementById('totalCNAmount').value = Number(totalCNAmt).toFixed(2);
	
	if(document.getElementById('totalCNAmountFMT'))
	document.getElementById('totalCNAmountFMT').value = addCommas(Number(totalCNAmt).toFixed(2));
	
	// total Receipt
	var recamt = document.getElementsByName('pb.recAmount');
	var totRec=0;
	for(var i=0;i<recamt.length;i++){
		totRec+=Number(recamt[i].value);
	}
	document.getElementById('receiptAmount').value = Number(totRec).toFixed(2); 
	document.getElementById('txtReceiptAmount').value = addCommas(Number(totRec).toFixed(2));
	
	// BILL
	updatePaidToBill();
	// Credit Note;
	updatePaidToCN();
	
	// Total Apply
	var totalPaid=0;
	var bp = document.getElementsByName('bill.paidAmt');
	for(var i=0;i<bp.length;i++){
		totalPaid += Number(bp[i].value);
	}
	var cbp = document.getElementsByName('cn.paidAmt');
	for(var i=0;i<cbp.length;i++){
		totalPaid += Number(cbp[i].value);
	}
	if(document.getElementById('applyAmount'))
		document.getElementById('applyAmount').value = Number(totalPaid).toFixed(2) ;
	if(document.getElementById('txtApplyAmount'))
		document.getElementById('txtApplyAmount').value = addCommas(Number(totalPaid).toFixed(2));
	
	if(creditAmt.length==0){
		//worse case
		var recAmt = document.getElementsByName('pb.recAmount');
		for(var i=0;i<recAmt.length;i++){
			totalPaid+=Number(recAmt[i].value);
		}
	}
	
	// Remain Amount
	var remainAmt = Number(totalBillAmt)+Number(totalCNAmt)-Number(totalPaid);
	//alert(remainAmt);
	document.getElementById('remainAmount').value = Number(remainAmt).toFixed(2) ;
	//alert("remainAmt:"+remainAmt);
	document.getElementById('remainAmountFMT').value = addCommas(Number(remainAmt).toFixed(2));
}

function updatePaidToBill(){
	// update paid to Bill
	var orders = document.getElementsByName('bill.orderId');
	var allBill = document.getElementsByName('pb.allBillId');
	var allPaid = document.getElementsByName('pb.allPaid');
	var bill;
	var credit=0;
	var paid=0;
	var remain=0;
	
	var tbl = document.getElementById('tblBill');
	
	for(var i=0;i<orders.length;i++){
		paid=0;
		remain=0;
		for(var j=0;j<allBill.length;j++){
			var bill = allBill[j].value.split(','); 
			for(var b=0;b<bill.length;b++){
				if(bill[b]==orders[i].value){
					// alert(bill[b]);
					// alert(allPaid[j].value.split('||')[b]);
					paid+=Number(allPaid[j].value.split('|')[b]);
				}
			}
		}
		
		if(document.getElementsByName('bill.creditAmt')[i])
			credit = document.getElementsByName('bill.creditAmt')[i].value;
		else
			credit = 0;
		
		if(document.getElementsByName('bill.paidAmt')[i])
			document.getElementsByName('bill.paidAmt')[i].value=paid;
		
		remain=credit-paid;
		if(remain<0)remain=0;
		if(paid>credit){
			paid=credit;
			if(document.getElementsByName('bill.paidAmt')[i])
				document.getElementsByName('bill.paidAmt')[i].value=paid;
		}
		
		if(document.getElementsByName('bill.remainAmt')[i])
			document.getElementsByName('bill.remainAmt')[i].value=remain;
		
		if(document.getElementsByName('receipt.orderType')[0].value=='DD')
		{
		//
		}else{
			tbl.rows[i+1].cells[6].innerHTML = addCommas(Number(paid).toFixed(2));
			tbl.rows[i+1].cells[7].innerHTML = addCommas(Number(remain).toFixed(2));	
		}
		
	}
}

function updatePaidToCN(){
	// update paid to CN
	var cns = document.getElementsByName('cn.cnId');
	var allBill = document.getElementsByName('pb.allCNId');
	var allPaid = document.getElementsByName('pb.allCNPaid');
	var bill;
	var credit=0;
	var paid=0;
	var remain=0;
	
	var tbl = document.getElementById('tblCN');
	
	for(var i=0;i<cns.length;i++){
		paid=0;
		remain=0;
		for(var j=0;j<allBill.length;j++){
			var bill = allBill[j].value.split(','); 
			for(var b=0;b<bill.length;b++){
				if(bill[b]==cns[i].value){
					// alert(bill[b]);
					// alert(allPaid[j].value.split('||')[b]);
					paid+=Number(allPaid[j].value.split('|')[b]);
				}
			}
		}
		credit = document.getElementsByName('cn.creditAmt')[i].value;
		document.getElementsByName('cn.paidAmt')[i].value=paid;
		remain=credit-paid;
		document.getElementsByName('cn.remainAmt')[i].value=remain;
		
		tbl.rows[i+1].cells[5].innerHTML = addCommas(Number(paid).toFixed(2));
		tbl.rows[i+1].cells[6].innerHTML = addCommas(Number(remain).toFixed(2));
	}
}


function applyBill(path, rowNo, type , recAmount, seedId) {
	// get selected id
	var screen_width = $(window).width()-10;
	var selected="";
	var objtxt = document.getElementsByName('bill.orderId');
	for(var i=0;i<objtxt.length;i++)
	{
		selected+=','+objtxt[i].value;
	}
	if(selected.length>0)
		selected = selected.substring(1, selected.length);
	
	var customerId = document.getElementsByName('receipt.customerId')[0].value;
	
	// Credit Note..
	var cns="";
	var objCNs = document.getElementsByName('cn.cnId');
	for(var i=0;i<objCNs.length;i++)
		cns+=','+objCNs[i].value;
	
	if(cns.length>0)
		cns = cns.substring(1, cns.length);
	
	// find write off value 
	var writeOffs = document.getElementsByName("pb.writeOff");
	var value = false;
	if(writeOffs.length ==1){
		value = writeOffs[0].checked;
		if(rowNo !=null)
			rowNo = 0+1;
	}
	else{
		var seedIds = document.getElementsByName("pb.seedId");
		for(var i=0;i<seedIds.length;i++){
			if(seedIds[i].value==seedId){
				value = writeOffs[i].checked;
				if(rowNo !=null)
					rowNo = i+1;
			}
		}
	}
	
	if (rowNo == null)
		window.open(path + "/jsp/pop/applyBillPopup.jsp?selected="+selected+"&rec="+recAmount+"&type="+type+"&seed="+seedId+"&cust="+customerId+"&cns="+cns+"&writeOff="+value, "ApplyBill",
			"width="+screen_width+",height=400,location=No,resizable=No");
	else
		window.open(path + "/jsp/pop/applyBillPopup.jsp?row=" + rowNo+"&selected="+selected+"&rec="+recAmount+"&type="+type+"&seed="+seedId+"&cust="+customerId+"&cns="+cns+"&writeOff="+value, "ApplyBill",
			"width="+screen_width+",height=400,location=No,resizable=No");
	return;
}

function fillApply(seed,allbill,allpaid){
	var seedObjs = document.getElementsByName('pb.seedId');
	for(var i=0;i<seedObjs.length;i++){
		if(seedObjs[i].value==seed){
			if(allbill.length>0){allbill=allbill.substring(1,allbill.length);}
			if(allpaid.length>0){allpaid=allpaid.substring(1,allpaid.length);}
			document.getElementsByName('pb.allBillId')[i].value = allbill;
			document.getElementsByName('pb.allPaid')[i].value = allpaid;
			break;
		}
	}
	
	calculateAll();
}

/** Create Bill Lazy List */
function createBysList(){
	var divlines = document.getElementById('ByList');
	
	var ids=document.getElementsByName('pb.id');
	var methods=document.getElementsByName('pb.method');
	var methodNames=document.getElementsByName('pb.methodName');
	var recAmts=document.getElementsByName('pb.recAmount');
	var banks=document.getElementsByName('pb.bank');
	var bankNames=document.getElementsByName('pb.bankName');
	var chqNos=document.getElementsByName('pb.chqNo');
	var chqDates=document.getElementsByName('pb.chqDate');
	var ccts=document.getElementsByName('pb.creditType');
	var cctNames=document.getElementsByName('pb.creditTypeName');
	var ss=document.getElementsByName('pb.seedId');
	var allbills=document.getElementsByName('pb.allBillId');
	var allpaids=document.getElementsByName('pb.allPaid');
	var writeoffs=document.getElementsByName('pb.writeOff');
	var allCNs=document.getElementsByName('pb.allCNId');
	var allCNpaids=document.getElementsByName('pb.allCNPaid');
	
	var inputLabel="";
	if(ids.length==0)
	{
		alert('ใส่ข้อมูลประเภทการชำระ');
		return false;
	}
	
	divlines.innerHTML="";
	
	var isOnlySalesPaid = true;
	var no_of_customer_pay_by_cash = 0;
	
	for(var i=0;i<ids.length;i++){
		if( (allbills[i].value == null || allbills[i].value.length==0) 
			&& (allCNs[i].value ==null || allCNs[i].value.length==0)){
			alert('ไม่มียอดการตัดชำระ กรุณาตัดชำระใบแจ้งหนี้');return false;
		}
		
		inputLabel="";
		inputLabel+="<input type='text' name='bys["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].paymentMethod' value='"+methods[i].value+"'>";
		
		inputLabel+="<input type='text' name='bys["+i+"].creditCardType' value='"+ccts[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].bank' value='"+banks[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].bankName' value='"+bankNames[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].chequeNo' value='"+chqNos[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].chequeDate' value='"+chqDates[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].receiptAmount' value='"+recAmts[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].seedId' value='"+ss[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].allBillId' value='"+allbills[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].allPaid' value='"+allpaids[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].paymentMethodName' value='"+methodNames[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].creditTypeName' value='"+cctNames[i].value+"'>";
		if(writeoffs[i].checked){
			inputLabel+="<input type='text' name='bys["+i+"].writeOff' value='Y'>";
		}else{
			inputLabel+="<input type='text' name='bys["+i+"].writeOff' value='N'>";
			isOnlySalesPaid = false;
			if(methods[i].value=="CS"){
				no_of_customer_pay_by_cash++;
			}
		}
		inputLabel+="<input type='text' name='bys["+i+"].allCNId' value='"+allCNs[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].allCNPaid' value='"+allCNpaids[i].value+"'>";
		
		inputLabel+="<hr/>";
		divlines.innerHTML += inputLabel;
	}
	
	if(isOnlySalesPaid){
		alert('ไม่สามารถบันทึกรายการรับเงินได้ เนื่องจากมีประเภทการชำระเงินที่เซลจ่ายเพียงรายการเดียว');
		return false;
	}
	
	if(no_of_customer_pay_by_cash > 1){
		alert('ไม่สามารถบันทึกรายการรับเงินได้ เนื่องจากมีประเภทการชำระเงินที่เป็นเงินสดมากกว่า 1 รายการ');
		return false;
	}
		
	return true;
}

function applyBillView(path, rowNo, type , recAmount, seedId) {
	// get selected id
	var selected="";
	var objtxt = document.getElementsByName('bill.orderId');
	for(var i=0;i<objtxt.length;i++)
	{
		selected+=','+objtxt[i].value;
	}
	if(selected.length>0)
		selected = selected.substring(1, selected.length);
	
	var customerId = document.getElementsByName('receipt.customerId')[0].value;
	var prepaid = document.getElementsByName('receipt.prepaid')[0].value;
	
	// Credit Note..
	var cns="";
	var objCNs = document.getElementsByName('cn.cnId');
	for(var i=0;i<objCNs.length;i++)
		cns+=','+objCNs[i].value;
	
	if(cns.length>0)
		cns = cns.substring(1, cns.length);
	
	if (rowNo == null)
		window.open(path + "/jsp/pop/applyBillViewPopup.jsp?selected="+selected+"&rec="+recAmount+"&type="+type+"&cust="+customerId+"&prepaid="+prepaid+"&seed="+seedId+"&cns="+cns, "ApplyBill",
			"width=939,height=400,location=No,resizable=No");
	else
		window.open(path + "/jsp/pop/applyBillViewPopup.jsp?row=" + rowNo+"&selected="+selected+"&rec="+recAmount+"&type="+type+"&cust="+customerId+"&prepaid="+prepaid+"&seed="+seedId+"&cns="+cns, "ApplyBill",
			"width=939,height=400,location=No,resizable=No");
	return;
}


function openCn(path,customerId) {
	// get selected id
	var screen_width = $(window).width()-10;
	var selected="";
	var objtxt = document.getElementsByName('cn.cnId');
	for(var i=0;i<objtxt.length;i++)
	{
		selected+=','+objtxt[i].value;
	}
	if(selected.length>0)
		selected = selected.substring(1, selected.length);
	
	window.open(path + "/jsp/pop/CNPopup.jsp?selected="+selected+"&customerId="+customerId, "CN",
			"width="+screen_width+",height=400,location=No,resizable=No");
	return;
}

function addCN(path, arrayVal) {
	var jQtable = $('#tblCN');
	for (var i = 0; i < arrayVal.length; i++) {
		jQtable.each(function() {
			var $table = $(this);
			// Number of td's in the last table row
				var n = $('tr', this).length;
				// n++;
				// alert(n);
				var className = "lineO";
				if (n % 2 == 0)
					className = "lineE";
				arrayVal[i].rows=n;
				
				var tds = '<tr class=' + className + '>';
				tds += '<td align="center">'+n+'</td>';
				tds += '<td align="center"></td>';
				tds += '<td align="center"></td>';
				tds += '<td align="center"></td>';
				tds += '<td align="right"></td>';
				tds += '<td align="right"></td>';
				tds += '<td align="right"></td>';
				tds += '<td align="right"></td>';
				tds += '</tr>';
				if ($('tbody', this).length > 0) {
					$('tbody', this).append(tds);
				} else {
					$(this).append(tds);
				}
			});
	}
	setValueToCN(path, arrayVal);
}

/** Set Value to CN */
function setValueToCN(path, arrayVal) {
	var tbl = document.getElementById('tblCN');
	var checkBoxLabel;
	var iconLabel;
	var inputLabel;
	for (var i = 0; i < arrayVal.length; i++) {

		checkBoxLabel = '<input type="checkbox" name="cnids" value="0"/>';

		inputLabel="";
		inputLabel+="<input type='hidden' name='cn.id' value='"+arrayVal[i].id+"'>";
		inputLabel+="<input type='hidden' name='cn.cnId' value='"+arrayVal[i].cnId+"'>";
		inputLabel+="<input type='hidden' name='cn.cnNo' value='"+arrayVal[i].cnNo+"'>";
		inputLabel+="<input type='hidden' name='cn.cnInvoiceNo' value='"+arrayVal[i].cnInvoiceNo+"'>";
		inputLabel+="<input type='hidden' name='cn.totalAmount' value='"+arrayVal[i].totalAmount+"'>";
		inputLabel+="<input type='hidden' name='cn.creditAmt' value='"+arrayVal[i].creditAmount+"'>";
		inputLabel+="<input type='hidden' name='cn.paidAmt' value='"+arrayVal[i].paidAmount+"'>";
		inputLabel+="<input type='hidden' name='cn.remainAmt' value='"+arrayVal[i].remainAmount+"'>";
		
		// tbl.rows[arrayVal[i].rows].cells[0].innerHTML = arrayVal[i].rows;
		tbl.rows[arrayVal[i].rows].cells[1].innerHTML = checkBoxLabel;
		tbl.rows[arrayVal[i].rows].cells[2].innerHTML = arrayVal[i].cnNo+inputLabel;
		tbl.rows[arrayVal[i].rows].cells[3].innerHTML = arrayVal[i].cnInvoiceNo;
		tbl.rows[arrayVal[i].rows].cells[4].innerHTML = addCommas(Number(arrayVal[i].totalAmount).toFixed(2));
		tbl.rows[arrayVal[i].rows].cells[5].innerHTML = addCommas(Number(arrayVal[i].creditAmount).toFixed(2));
		tbl.rows[arrayVal[i].rows].cells[6].innerHTML = addCommas(Number(arrayVal[i].paidAmount).toFixed(2));
		tbl.rows[arrayVal[i].rows].cells[7].innerHTML = addCommas(Number(arrayVal[i].remainAmount).toFixed(2));
		// tbl.rows[arrayVal[i].rows].cells[8].innerHTML = iconLabel;
	}
	calculateAll();
	return true;
}

function deleteCN(path){
	var tbl = document.getElementById('tblCN');
	var chk = document.getElementsByName("cnids");
	var drow;
	var bcheck=false;
	var allId;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			// alert(i);
			drow = tbl.rows[i+1];
			$(drow).remove();
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	chk = document.getElementsByName("cnids");
	var iconLabel="";
	for(var i=0;i<chk.length;i++){
		tbl.rows[i+1].cells[0].innerHTML=(i+1);
	}
}

function fillCN(seed,allbill,allpaid){
	var seedObjs = document.getElementsByName('pb.seedId');
	for(var i=0;i<seedObjs.length;i++){
		if(seedObjs[i].value==seed){
			if(allbill.length>0){allbill=allbill.substring(1,allbill.length);}
			if(allpaid.length>0){allpaid=allpaid.substring(1,allpaid.length);}
			document.getElementsByName('pb.allCNId')[i].value = allbill;
			document.getElementsByName('pb.allCNPaid')[i].value = allpaid;
			break;
		}
	}
	
	calculateAll();
}

/** Create CN Lazy List */
function createCNSList(){
	var divlines = document.getElementById('CNList');
	
	var ids=document.getElementsByName('cn.id');
	var cnIds=document.getElementsByName('cn.cnId');
	var cnNos=document.getElementsByName('cn.cnNo');
	var cnInvNos=document.getElementsByName('cn.cnInvoiceNo');
	var cnAmts=document.getElementsByName('cn.totalAmount');
	var creditamts=document.getElementsByName('cn.creditAmt');
	var paidamts=document.getElementsByName('cn.paidAmt');
	var remainamts=document.getElementsByName('cn.remainAmt');
	
	var inputLabel="";
	
	divlines.innerHTML="";
	for(var i=0;i<ids.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='cns["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='cns["+i+"].creditNote.id' value='"+cnIds[i].value+"'>";
		inputLabel+="<input type='text' name='cns["+i+"].creditNote.creditNoteId' value='"+cnNos[i].value+"'>";
		inputLabel+="<input type='text' name='cns["+i+"].creditNote.arInvoiceId' value='"+cnInvNos[i].value+"'>";
		inputLabel+="<input type='text' name='cns["+i+"].creditNote.totalAmount' value='"+cnAmts[i].value+"'>";
		inputLabel+="<input type='text' name='cns["+i+"].creditAmount' value='"+creditamts[i].value+"'>";
		inputLabel+="<input type='text' name='cns["+i+"].paidAmount' value='"+paidamts[i].value+"'>";
		inputLabel+="<input type='text' name='cns["+i+"].remainAmount' value='"+remainamts[i].value+"'>";
		
		inputLabel+="<hr/>";
		divlines.innerHTML += inputLabel;
		
	}
	return true;
}


function validateCNForWriteOff(seedId){
	var seedIds = document.getElementsByName("pb.seedId");
	var writeOffs = document.getElementsByName("pb.writeOff");
	var cnIds = document.getElementsByName("pb.allCNId");
	for(var i=0;i<seedIds.length;i++){
		if(seedIds[i].value==seedId){
			if(cnIds[i].value !=null && cnIds[i].value != ''){
				if(writeOffs[i].checked){
					writeOffs[i].checked = false;
					alert("ไม่สามารถเลือกเซลล์จ่ายได้เนื่องจากมีรายการตัดชำระใบลดหนี้");
					return false;
				}
			}
		}
	}
	
	return true;
}