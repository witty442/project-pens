function search(path){
	var form = document.orderNissinForm;
	form.action = path + "/jsp/orderNissinAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function clearSearch(path){
	var form = document.orderNissinForm;
	form.action = path + "/jsp/orderNissinAction.do?do=prepareSearchHead&action=new";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.orderNissinForm;
	form.action = path + "/jsp/orderNissinAction.do?do=exportToExcel";
	form.submit();
	return true;
}
function viewDetail(path,orderId){
	var form = document.orderNissinForm;
	form.action = path + "/jsp/orderNissinAction.do?do=viewDetail&orderId="+orderId;
	form.submit();
	return true;
}
function saveByNissin(path) {
	if(!createProductList()){return false;}

	/**Control Save Lock Screen **/
    startControlSaveLockScreen();
    
	document.orderNissinForm.action = path + "/jsp/orderNissinAction.do?do=save";
	document.orderNissinForm.submit();
	return true;
}

function saveByPens(path) {

	//validate invoiceNo
	var salesrepCode = document.getElementById("salesrepCode");
	var invoiceNo = document.getElementById("invoiceNo");
	if(salesrepCode.value ==""){
		alert("กรุณาระบุข้อมูล Sales Code ");
		salesrepCode.focus();
		return false;
	}
	/*if(invoiceNo.value ==""){
		alert("กรุณาระบุข้อมูล Invoice No ");
		invoiceNo.focus();
		return false;
	}*/
	/**Control Save Lock Screen **/
    startControlSaveLockScreen();
    
	document.orderNissinForm.action = path + "/jsp/orderNissinAction.do?do=saveByPens";
	document.orderNissinForm.submit();
	return true;
}
function saveByPensSales(path) {
	//validate invoiceNo
	var salesrepCode = document.getElementById("salesrepCode");
	var invoiceNo = document.getElementById("invoiceNo");

	/**Control Save Lock Screen **/
    startControlSaveLockScreen();
    
	document.orderNissinForm.action = path + "/jsp/orderNissinAction.do?do=saveByPensSales";
	document.orderNissinForm.submit();
	return true;
}
function backSearch(path) {
	var fromPage = document.getElementById("fromPage").value;
	if("customerNissin"==fromPage){
	   document.orderNissinForm.action = path + "/jsp/customerNissinAction.do?do=search&action=back&rf=Y";
	}else{
	   document.orderNissinForm.action = path + "/jsp/orderNissinAction.do?do=searchHead&action=back";
	}
	document.orderNissinForm.submit();
	return true;
}
function backToCustDetail(path) {
	var shortCustId = document.getElementById("shortCustId").value;
	document.orderNissinForm.action = path + "/jsp/customerNissinAction.do?do=prepare&fromPage=customerNissin&id="+shortCustId;
	document.orderNissinForm.submit();
	return true;
}
function lockScreen() {
	$('#div_body').hide();
	
	 /*  $.blockUI({ 
		overlayCSS: { backgroundColor: '#00f' },
	    message: ''
	});   */
	  
	 // disable_scroll();
}

function unlockScreen() {
	// $.unblockUI({backgroundColor: '#00f' }); 
	 //enable_scroll();
	$('#div_body').show();
}

function addProductToBasket(){
	
	var productIds = document.getElementsByName("productId");
	var productNames = document.getElementsByName("productName");
	var productCodes = document.getElementsByName("productCode");
	var uom1s = document.getElementsByName("uom1");
	var uom2s = document.getElementsByName("uom2");
	var qty1s = document.getElementsByName("qty1");
	var qty2s = document.getElementsByName("qty2");
	var price1s = document.getElementsByName("price1");
	var price2s = document.getElementsByName("price2");
	var lineAmts = document.getElementsByName("totalLineAmt");
	var custId = 9;// document.getElementById("order.customerId").value;
	var taxables = document.getElementsByName("taxable");
	
	var subBrandCode = document.getElementById("subBrandCode").value;
	var selected = false;
	var param = "";
	var path = document.getElementById("path").value;
	//alert("addProductToBasket");
	
	for(var i =0;i < lineAmts.length; i++){
		if(!selected && (Number(qty1s[i].value) > 0 || Number(qty2s[i].value) > 0)){
			selected = true;
		}
		
		param = "custId=" +custId
		  +"&pId="+productIds[i].value
		  +"&pCode="+productCodes[i].value 
		  +"&pName="+escapeParameter(productNames[i].value)
		  +"&uom1="+escapeParameter(uom1s[i].value)
		  +"&uom2="+escapeParameter(uom2s[i].value)
		  +"&price1="+price1s[i].value
		  +"&price2="+price2s[i].value
		  +"&qty1="+qty1s[i].value
		  +"&qty2="+qty2s[i].value
		  +"&lineAmt="+lineAmts[i].value
		  +"&taxable="+taxables[i].value;
		
		$(function(){
			var getData = $.ajax({
				url: path+"/jsp/orderNissin/nissinAddProductToBasket.jsp",
				data : param,
				async: false,
				success: function(getData){
					var status = jQuery.trim(getData);
					if(status != "")
						alert(status);
				}
			}).responseText;
		});
	}

	// Set Background 
	if(selected){
		$("#"+subBrandCode).css('background-color', '#FFFF99');
	}
	else{
		$("#"+subBrandCode).css('background-color', '');
	}

	$(document).ready(function() {
	    $("#selectProduct").dialog("close");
	}); 	
}

function addProductToSalesOrder(){
	//alert("addProductToSalesOrder!");
	var path = document.getElementById("path").value;
	
	unlockScreen();
	 
	var data = '';
	var custId = 9;//document.getElementById("order.customerId").value;
	$(function(){
		var getData = $.ajax({
			url: path+"/jsp/orderNissin/nissinAddProductToSO.jsp",
			contentType: "application/json; charset=utf-8",
			data : "custId=" +custId,
			async: false,
			success: function(getData){
				data = jQuery.trim(getData);
			}
		}).responseText;
	});

	var products = [];
	products = eval(data);
	if(products != null){
		for(var i=0; i < products.length ; i++){
			//var p = new Object();
			var product = new Object();
			//product.lineNo = products[i].lineNo;
			
			product.productId = products[i].productId;
			product.product = products[i].productCode;
	//		alert(products[i].productName);
			product.productLabel = decodeURIComponent(escapeParameter(products[i].productName));
			//alert(products[i].productName);
			
			product.uom1 = decodeURIComponent(escapeParameter(products[i].uom1));
			product.uom2 = decodeURIComponent(escapeParameter(products[i].uom2));
			product.uomLabel1 = decodeURIComponent(escapeParameter(products[i].uom1));
			product.uomLabel2 = decodeURIComponent(escapeParameter(products[i].uom2));
			product.price1 = products[i].price1;
			product.price2 = products[i].price2;
			product.qty1 = products[i].qty1;
			product.qty2 = products[i].qty2;
			
			product.amount1 = products[i].amount1;
			product.amount2 = products[i].amount2;
			product.disc1 = 0;
			product.disc2 = 0;
			product.total1 = products[i].amount1;
			product.total2 = products[i].amount2;
	
			product.vat1 = "";
			product.vat2 = "";
			
			product.ship = "";
			product.req = "";
			product.id = "";
			product.row = "";
			
			addProduct(path, product);
		}
	}
	
	$(document).ready(function() {
	    $("#brand-dialog").dialog("close");
	});
}

/** escape some charecter cannot display ajax % ,# **/
function escapeParameter(param){
	param = param.replace("%","%25");
	param = param.replace("#","%23");
	return param;
}
function findDupIndex(tbl,objValue){
	var i;
	var index = -1;
	//alert("table len:"+tbl.rows.length);
	for(i=0;i<tbl.rows.length;i++){
		if(i >1){
			//alert(objValue.productId);
			//alert("i["+(i-2)+"]productId len:"+document.getElementsByName('lines.productId').length);
			//alert("i["+(i-2)+"]"+document.getElementsByName('lines.productId')[i-2].value);
			if(document.getElementsByName('lines.productId')[i-2].value==objValue.productId){
				index = (i-2);	
			}
		}
	}
	return index;
}

function deleteProduct(path,type){
	//todo play with type
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("lineids");
	var delId = document.getElementsByName('deletedId')[0];
	var drow;
	var bcheck=false;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			delId.value+=","+chk[i].value;
			// alert(i);
			drow = tbl.rows[i+2];
			$(drow).remove();
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	chk = document.getElementsByName("lineids");
	//alert(chk.length);
	
	var iconLabel="";
	for(var i=0;i<chk.length;i++){
		//alert("row["+(i+1)+"]:"+tbl.rows[i+1].cells[2].innerHTML);
		tbl.rows[i+2].cells[0].innerHTML=(i+1);
	}
	//calculatePrice();
}

function addProduct(path,objValue){
	var tbl = document.getElementById('tblProduct');
	var index = findDupIndex(tbl,objValue);
	//alert("index:"+index);
	if(index < 0){
		//Add Column Head
		addProduct2(path,objValue);
		return;
	}else{
		//alert(1);
		
		var qty1 = eval(document.getElementsByName('lines.qty1')[index].value) + eval(objValue.qty1);
		var qty2 = eval(document.getElementsByName('lines.qty2')[index].value) + eval(objValue.qty2);
		var amt1 = eval(document.getElementsByName('lines.amount1')[index].value) + eval(objValue.amount1);
		var amt2 = eval(document.getElementsByName('lines.amount2')[index].value) + eval(objValue.amount2);
		var disc1 = eval(document.getElementsByName('lines.disc1')[index].value) + eval(objValue.disc1);
		var disc2 = eval(document.getElementsByName('lines.disc2')[index].value) + eval(objValue.disc2);
		var total1 = eval(document.getElementsByName('lines.total1')[index].value) + eval(objValue.total1);
		var total2 = eval(document.getElementsByName('lines.total2')[index].value) + eval(objValue.total2);
		var vat1 = eval(document.getElementsByName('lines.vat1')[index].value) + eval(objValue.vat1);
		var vat2 = eval(document.getElementsByName('lines.vat2')[index].value) + eval(objValue.vat2);
		var taxable = objValue.taxable;
		//alert("uom1:"+objValue.uom1+",uom2:"+objValue.uom2);
		
		var qty1Label = addCommas(qty1);
		var qty2Label = addCommas(qty2);
		var qtyFullLabel = qty1Label + '/' + qty2Label;
		
		tbl.rows[index+2].cells[4].innerHTML = qtyFullLabel; //qty1
		
		document.getElementsByName('lines.qty1')[index].value = qty1;
		document.getElementsByName('lines.qty2')[index].value = qty2;
		document.getElementsByName('lines.amount1')[index].value = amt1;
		document.getElementsByName('lines.amount2')[index].value = amt2;
		document.getElementsByName('lines.disc1')[index].value = disc1;
		document.getElementsByName('lines.disc2')[index].value = disc2;
		document.getElementsByName('lines.total1')[index].value = total1;
		document.getElementsByName('lines.total2')[index].value = total2;
	
		document.getElementsByName('lines.price1')[index].value = objValue.price1;
		document.getElementsByName('lines.price2')[index].value = objValue.price2;
		
		document.getElementsByName('lines.uom1')[index].value = objValue.uom1;
		document.getElementsByName('lines.uom2')[index].value = objValue.uom2;
		document.getElementsByName('lines.uomLabel1')[index].value = objValue.uom1;
		document.getElementsByName('lines.uomLabel2')[index].value = objValue.uom2;
		document.getElementsByName('lines.taxable')[index].value = objValue.taxable;
		
	}
}

/** Product Table */
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
       
        var tds = '<tr class='+className+'>';
        tds += '<td align="center"></td>';//no 0
        tds += '<td align="center"></td>';//chk 1
        tds += '<td align="left"></td>';//productCode 2
        tds += '<td align="left"></td>';//productName 3
        tds += '<td align="center"></td>';// qty1/qty2 4
        tds += '<td align="center"></td>';//uom1/uom2 5
        
        tds += '</tr>';
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    
  //  alert(objValue);
    
    setValueToProduct(path,objValue);
}
/** Set Value to Product */
function setValueToProduct(path, objValue){
	// alert(objValue.row);
	var tbl = document.getElementById('tblProduct');
	
	var inputLabel="";
	inputLabel+="<input type='hidden' name='lines.id' value='"+objValue.id+"'>";
	inputLabel+="<input type='hidden' name='lines.row' value='"+objValue.row+"'>";
	inputLabel+="<input type='hidden' name='lines.productId' value='"+objValue.productId+"'>";
	inputLabel+="<input type='hidden' name='lines.product' value='"+objValue.product+"'>";
	inputLabel+="<input type='hidden' name='lines.productLabel' value='"+objValue.productLabel+"'>";
	
	inputLabel+="<input type='hidden' name='lines.uom1' value='"+objValue.uom1+"'>";
	inputLabel+="<input type='hidden' name='lines.uom2' value='"+objValue.uom2+"'>";
	inputLabel+="<input type='hidden' name='lines.uomLabel1' value='"+objValue.uomLabel1+"'>";
	inputLabel+="<input type='hidden' name='lines.uomLabel2' value='"+objValue.uomLabel2+"'>";
	inputLabel+="<input type='hidden' name='lines.fullUom' value='"+objValue.uom1+'/'+objValue.uom2+"'>";
	inputLabel+="<input type='hidden' name='lines.price1' value='"+objValue.price1+"'>";
	inputLabel+="<input type='hidden' name='lines.price2' value='"+objValue.price2+"'>";
	inputLabel+="<input type='hidden' name='lines.qty1' value='"+objValue.qty1+"'>";
	inputLabel+="<input type='hidden' name='lines.qty2' value='"+objValue.qty2+"'>";
	inputLabel+="<input type='hidden' name='lines.amount1' value='"+objValue.amount1+"'>";
	inputLabel+="<input type='hidden' name='lines.amount2' value='"+objValue.amount2+"'>";
	inputLabel+="<input type='hidden' name='lines.disc1' value='"+objValue.disc1+"'>";
	inputLabel+="<input type='hidden' name='lines.disc2' value='"+objValue.disc2+"'>";
	inputLabel+="<input type='hidden' name='lines.afdisc1'>";
	inputLabel+="<input type='hidden' name='lines.afdisc2'>";
	inputLabel+="<input type='hidden' name='lines.vat' value='0'>";
	inputLabel+="<input type='hidden' name='lines.vat1' value='0'>";
	inputLabel+="<input type='hidden' name='lines.vat2' value='0'>";
	inputLabel+="<input type='hidden' name='lines.total1' value='"+objValue.total1+"'>";
	inputLabel+="<input type='hidden' name='lines.total2' value='"+objValue.total2+"'>";
	
	
	inputLabel+="<input type='hidden' name='lines.ship' value='"+objValue.ship+"'>";
	inputLabel+="<input type='hidden' name='lines.req' value='"+objValue.req+"'>";
	inputLabel+="<input type='hidden' name='lines.promo' value='N'>";
	inputLabel+="<input type='hidden' name='lines.lineno' value='0'>";
	inputLabel+="<input type='hidden' name='lines.tripno' value='1'>";
	inputLabel+="<input type='hidden' name='lines.taxable' value='"+objValue.taxable+"'>";
	var checkBoxLabel='<input type="checkbox" name="lineids" value="0"/>';
	
	var iconLabel="";
	iconLabel+='<a href="#" onclick="open_product(\''+path+'\','+objValue.row+');">';
	iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	
	
	var c=0;
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.row-1;
	tbl.rows[objValue.row].cells[c++].innerHTML=checkBoxLabel;
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.product;//รหัสสินค้า
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.productLabel+inputLabel;//ชื่อสินค้า

	
	var qty1Label = addCommas(objValue.qty1);
	var qty2Label = addCommas(objValue.qty2);
	var qtyFullLabel = qty1Label + '/' + qty2Label;
	
	var priceLabel1 = addCommas5format(objValue.price1);
	var priceLabel2 = addCommas5format(objValue.price2);
	var priceFullLabel = priceLabel1 + '/' + priceLabel2;
	
	tbl.rows[objValue.row].cells[c++].innerHTML= qtyFullLabel;// qty1/qty2
	tbl.rows[objValue.row].cells[c++].innerHTML= objValue.uomLabel1+"/"+objValue.uomLabel2 ; // uom1/uom2
	
	//caculate price
	//calculatePrice();
	
	return true;
}
/** Create Product Lazy List */
function createProductList(){

	var divlines = document.getElementById('productList');
	var ids=document.getElementsByName('lines.id');
	var productIds=document.getElementsByName('lines.productId');
	var products=document.getElementsByName('lines.product');
	var productLabels=document.getElementsByName('lines.productLabel');
	var uomIds=document.getElementsByName('lines.uom');
	var uomIds1=document.getElementsByName('lines.uom1');
	var uomIds2=document.getElementsByName('lines.uom2');
	var uomLabels=document.getElementsByName('lines.uomLabel');
	var uomLabels1=document.getElementsByName('lines.uomLabel1');
	var uomLabels2=document.getElementsByName('lines.uomLabel2');
	var fullUoms=document.getElementsByName('lines.fullUom');
	var prices=document.getElementsByName('lines.price');
	var prices1=document.getElementsByName('lines.price1');
	var prices2=document.getElementsByName('lines.price2');
	var qtys=document.getElementsByName('lines.qty');
	var qtys1=document.getElementsByName('lines.qty1');
	var qtys2=document.getElementsByName('lines.qty2');
	
	var amounts=document.getElementsByName('lines.amount');
	var amounts1=document.getElementsByName('lines.amount1');
	var amounts2=document.getElementsByName('lines.amount2');
	var discs=document.getElementsByName('lines.disc');
	var discs1=document.getElementsByName('lines.disc1');
	var discs2=document.getElementsByName('lines.disc2');
	var totals=document.getElementsByName('lines.total');
	var totals1=document.getElementsByName('lines.total1');
	var totals2=document.getElementsByName('lines.total2');
	var vats=document.getElementsByName('lines.vat');
	var vats1=document.getElementsByName('lines.vat1');
	var vats2=document.getElementsByName('lines.vat2');
	
	var ships=document.getElementsByName('lines.ship');
	var reqs=document.getElementsByName('lines.req');
	var promos=document.getElementsByName('lines.promo');
	var linenos=document.getElementsByName('lines.lineno');
	
	var tripnos=document.getElementsByName('lines.tripno');
	var taxables=document.getElementsByName('lines.taxable');
	
	var inputLabel="";
	
	if(ids.length==0)
	{
		alert('ใส่ข้อมูลสินค้า');
		return false;
	}
	
	divlines.innerHTML="";
	for(var i=0;i<ids.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='lines["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].product.id' value='"+productIds[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].product.code' value='"+products[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].product.name' value='"+productLabels[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].uom1.id' value='"+uomIds1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].uom2.id' value='"+uomIds2[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].fullUom' value='"+fullUoms[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].uom1.code' value='"+uomLabels1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].uom2.code' value='"+uomLabels2[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].price1' value='"+prices1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].price2' value='"+prices2[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].qty1' value='"+qtys1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].qty2' value='"+qtys2[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].lineAmount1' value='"+amounts1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].lineAmount2' value='"+amounts2[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].discount1' value='"+discs1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].discount2' value='"+discs2[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].vatAmount1' value='"+vats1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].vatAmount2' value='"+vats2[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].totalAmount1' value='"+totals1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].totalAmount2' value='"+totals2[i].value+"'>";
		
		inputLabel+="<input type='text' name='lines["+i+"].shippingDate' value='"+ships[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].requestDate' value='"+reqs[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].promotion' value='"+promos[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].lineNo' value='"+linenos[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].tripNo' value='"+tripnos[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].taxable' value='"+taxables[i].value+"'>";
		inputLabel+="<hr/>";
		divlines.innerHTML += inputLabel;
		
		//alert(divlines.innerHTML);
	}
	return true;
}

function getAutoOnblur(e,obj,pageName){
	var form = document.orderNissinForm;
	if(obj.value ==''){
		if("INVOICE_SA" == pageName){
		   form.invoiceNo.value = '';
		   form.invoiceDate.value = '';
		   form.oraCustomerId.value ="";
		   form.oraCustomerCode.value = '';
		   form.oraCustomerName.value = "";
		   form.orderNo.value = "";
		}else if("SALESREP_SA" == pageName){
		  form.salesrepId.value = '';
		  form.salesrepCode.value = '';
		}
	}else{
		getAutoDetail(obj,pageName);
	}
}
function getAutoKeypress(e,obj,pageName){
	var form = document.orderNissinForm;
	if(e != null && e.keyCode == 13){
		if(obj.value ==''){
			if("INVOICE_SA" == pageName){
				 form.invoiceNo.value = '';
				 form.invoiceDate.value = '';
				 form.oraCustomerCode.value = '';
				 form.oraCustomerName.value = "";
				 form.oraCustomerId.value ="";
				 form.orderNo.value = "";
			}else if("SALESREP_SA" == pageName){
				form.salesrepId.value = '';
				form.salesrepCode.value = '';
			}
		}else{
			getAutoDetail(obj,pageName);
		}
	}
}
function getAutoOnclick(pageName){
	var form = document.orderNissinForm;
    var obj = document.getElementById("invoiceNo");
	if(obj.value ==''){
		if("INVOICE_SA" == pageName){
			 form.invoiceNo.value = '';
			 form.invoiceDate.value = '';
			 form.oraCustomerId.value ="";
			 form.oraCustomerCode.value = '';
			 form.oraCustomerName.value = "";
			 form.orderNo.value = "";
		}
	}else{
		getAutoDetail(obj,pageName);
	}
	
}
function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.orderNissinForm;
	var path = document.getElementById("path").value;
	//prepare parameter
	var param = "";
	if("INVOICE_SA"==pageName){
		param  ="pageName="+pageName;
		param +="&invoiceNo="+obj.value;
	}else if("SALESREP_SA"==pageName){
		param  ="pageName="+pageName;
		param +="&salesrepCode="+obj.value;
	}
	var getData = $.ajax({
			url: path+"/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	 
	if("INVOICE_SA" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.invoiceNo.value = retArr[1];
			form.invoiceDate.value = retArr[2];
			form.oraCustomerId.value = retArr[3];
			form.oraCustomerCode.value = retArr[4];
			form.oraCustomerName.value = retArr[5];
			
			//Case Sales key 4 Order Digit check vs Get from SA
			if(form.orderNo.value ==""){
			  form.orderNo.value = retArr[6];
			}else{
				if(form.orderNo.value != retArr[5]){
					if(confirm("Order No (4 ตัว) ข้อมูลไม่ตรงกับข้อมูลส่วนกลาง Oracle ต้องการ Overwrite หรือไม่")){
						form.orderNo.value = retArr[6];
					}
				}
			}
		}else{
			alert("ไม่พบข้อมูล");
			form.invoiceNo.focus();
			form.invoiceNo.value = '';
			form.invoiceDate.value = '';
			form.oraCustomerId.value ="";
			form.oraCustomerCode.value = '';
			form.oraCustomerName.value = "";
			form.orderNo.value = "";
		}
	}else if("SALESREP_SA" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.salesrepCode.value = retArr[1];
			form.oraCustomerId.value =retArr[2];
		}else{
			alert("ไม่พบข้อมูล");
			form.salesrepCode.focus();
			form.salesrepId.value = '';
			form.salesrepCode.value = '';
		}
	}
}
