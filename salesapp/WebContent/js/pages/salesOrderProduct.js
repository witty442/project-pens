

function addProductToSalesOrder(){
	//alert("addProductToSalesOrder!");
	var path = document.getElementById("path").value;
	var data = '';
	var custId = document.getElementById("order.customerId").value;

	var getData = $.ajax({
		url: path+"/jsp/ajax/addProductToSO.jsp",
		contentType: "application/json; charset=utf-8",
		data : "custId=" +custId,
		async: false,
		success: function(getData){
			data = jQuery.trim(getData);
			//alert(data);
		}
	}).responseText;
	

	var products = [];
	products = eval(data);
	//alert(products);
	//alert(products.length);
	//alert(products[0].taxable);
	
	//Load data from screen to product screen
	if(products != null){
		for(var i=0; i < products.length ; i++){
			//var p = new Object();
			var product = new Object();
			product.productId = products[i].productId;
			product.product = products[i].productCode;
			//alert(products[i].productName);
			product.productLabel = decodeURIComponent(escapeParameter(products[i].productName));
	       //alert(products[i].productName);
			
			product.uom1 = products[i].uom1;
			product.uom2 = products[i].uom2;
			product.uomLabel1 = products[i].uom1;
			product.uomLabel2 = products[i].uom2;
			product.price1 = products[i].price1;
			product.price2 = products[i].price2;
			
			//alert(products[i].qty1);
			
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
			
			product.taxable = products[i].taxable;
			
			addProduct(path, product);
		}
	}
	
	//close brand modal
	//$('#brand-dialog').modal('hide');
		
}

function addProductToBasket(){
	//alert("addProductToBasket");
	var path = document.getElementById("path").value;
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
	var custId = document.getElementById("order.customerId").value;
	var taxables = document.getElementsByName("taxable");
	
	var categoryCode = document.getElementById("categoryCode").value;
	var selected = false;
    var param ="";
    
   //debug
  // alert("productIds:"+productIds.length+","+productIds.value);
   
	for(var i =0;i < productIds.length; i++){
		if(!selected && Number(lineAmts[i].value) > 0 ){
			selected = true;
			//alert("product["+i+"]:"+productIds[i].value);
		}
		
		param  ="custId=" +custId;
		param +="&pId="+productIds[i].value;
		param +="&pCode="+productCodes[i].value ;
		param +="&pName="+encodeURIComponent(escapeParameter(productNames[i].value));
		param +="&uom1="+uom1s[i].value;
		param +="&uom2="+uom2s[i].value;
		param +="&price1="+price1s[i].value;
		param +="&price2="+price2s[i].value;
		param +="&qty1="+qty1s[i].value;
		param +="&qty2="+qty2s[i].value;
		param +="&lineAmt="+lineAmts[i].value;
		param +="&taxable="+taxables[i].value;
		
		$.ajax({
			url: path+"/jsp/ajax/addProductToBasket.jsp",
			data : param,
			success: function(getData){
				var status = jQuery.trim(getData);
				if(status != "")
					alert(status);
			}
		}).responseText;
		
	}//for

	// Set Background 
	if(selected){
		$("#"+categoryCode).css('background-color', '#FFFF99');
	}else{
		$("#"+categoryCode).css('background-color', '');
	}

	//close brand modal
	//$('#product-dialog').modal('hide');
	
	//open brand dialog
	$("#brand-dialog").modal({backdrop: 'static', keyboard: true,show:true}) ; 
}

function escapeParameter(param){
	return param.replace("%","%25");
}

function addProduct(path,objValue){
	var tbl = document.getElementById('tblProduct');
	var index = findDupIndex(tbl,objValue);
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
		var qty2Lable = addCommas(qty2);
		var qtyFullLabel = qty1Label + '/' + qty2Lable;
		
		tbl.rows[index+1].cells[4].innerHTML = qtyFullLabel; //qty
		//tbl.rows[index+1].cells[5].innerHTML = addCommas(qty1) + '/' + addCommas(qty2);//price
		tbl.rows[index+1].cells[6].innerHTML = addCommas((amt1 + amt2).toFixed(2)); //total amount
		tbl.rows[index+1].cells[7].innerHTML = addCommas((disc1 + disc2).toFixed(2));//total discount
		
		//taxable
		//alert(objValue.taxable);
		if(objValue.taxable =='Y'){
		   tbl.rows[index+1].cells[8].innerHTML ="<img border=0 src='"+path+"/icons/check.gif'/>";
		}else{
		   tbl.rows[index+1].cells[8].innerHTML = "";	
		}
		//tbl.rows[index+1].cells[9] //promotion
		
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
		
		//WIT Edit :09/06/2011 :add method calculatePrice()
		calculatePrice();
	}
}

/** Product Table */
function addProduct2(path,objValue){
	var orderType = document.getElementsByName('order.orderType')[0].value;
	
	var jQtable = $('#tblProduct');
    jQtable.each(function(){
        var $table = $(this);
        // Number of td's in the last table row
        var n = $('tr', this).length;
        var className="lineO";
        if(n%2==0)
        	className="lineE";
        objValue.row=n;
       
        var tds = " <thead class='thead-dark'>";
        tds += '<tr class='+className+'>';
        tds += '<td align="center"></td>';
        tds += '<td align="center"></td>';
        tds += '<td align="left"></td>';
        tds += '<td align="center"></td>';
        tds += '<td align="right"></td>';
        tds += '<td align="right"></td>';
        tds += '<td align="right"></td>';
        tds += '<td align="right"></td>';
        tds += '<td align="right"></td>';
        tds += '<td align="center"></td>';
        tds += '<td align="center"></td>';
        tds += '</tr></thead>';
        
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    
  //  alert(objValue);
    
    setValueToProduct(path,objValue);
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
			drow = tbl.rows[i+1];
			$(drow).remove();
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	var orderType = document.getElementsByName('order.orderType')[0].value;
	
	chk = document.getElementsByName("lineids");
	//alert(chk.length);
	
	var iconLabel="";
	for(var i=0;i<chk.length;i++){
		//alert("row["+(i+1)+"]:"+tbl.rows[i+1].cells[2].innerHTML);
		tbl.rows[i+1].cells[0].innerHTML=(i+1);
		iconLabel="";
		iconLabel+='<a href="#" onclick="open_product(\''+path+'\','+(i+1)+');">';
		iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
		
		tbl.rows[i+1].cells[11].innerHTML= "";//iconLabel;
	}
	calculatePrice();
}

function findDupIndex(tbl,objValue){
	var i;
	var index = -1;
	for(i=0;i<tbl.rows.length;i++){
		if(i!=0){
			if(document.getElementsByName('lines.productId')[i-1].value==objValue.productId){
				//if(document.getElementsByName('lines.uom1')[i-1].value==objValue.uom1){
					if(document.getElementsByName('lines.ship')[i-1].value==objValue.ship){
						if(document.getElementsByName('lines.req')[i-1].value==objValue.req){
							index = (i-1);
						}
					}
				//}
			}
		}
	}
	return index;
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
	
	//alert(iconLabel);
	var orderType = document.getElementsByName('order.orderType')[0].value;
	
	var c=0;
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.row;
	tbl.rows[objValue.row].cells[c++].innerHTML=checkBoxLabel;
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.product+' '+objValue.productLabel+inputLabel;//ชื่อรหีสสินค้า
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.uomLabel1 + '/' + objValue.uomLabel2; //uom
	
	var qty1Label = addCommas(objValue.qty1);
	var qty2Lable = addCommas(objValue.qty2);
	var qtyFullLabel = qty1Label + '/' + qty2Lable;
	
	var priceLabel1 = addCommas5format(objValue.price1);
	var priceLabel2 = addCommas5format(objValue.price2);
	var priceFullLabel = priceLabel1 + '/' + priceLabel2;
	
	tbl.rows[objValue.row].cells[c++].innerHTML= qtyFullLabel;//qty 3
	tbl.rows[objValue.row].cells[c++].innerHTML= priceFullLabel;//price per unit 10 
	
	tbl.rows[objValue.row].cells[c++].innerHTML=addCommas((eval(objValue.amount1) + eval(objValue.amount2)).toFixed(5));//amount 
	tbl.rows[objValue.row].cells[c++].innerHTML=addCommas((eval(objValue.disc1) + eval(objValue.disc2)).toFixed(5));//discount
	tbl.rows[objValue.row].cells[c++].innerHTML='0';//netAmount
	
	//taxable
	if(objValue.taxable =='Y'){
	  tbl.rows[objValue.row].cells[c++].innerHTML ="<img border=0 src='"+path+"/icons/check.gif'/>";
	}else{
	  tbl.rows[objValue.row].cells[c++].innerHTML = "";	
	}
	tbl.rows[objValue.row].cells[c++].innerHTML= "";//iconLabel; //icon edit
	
	//caculate price
	calculatePrice();
	
	return true;
}

/** Create Product Lazy List */
function createProductList(){
	var orderType = document.getElementsByName('order.orderType')[0].value;

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
		
	}
	return true;
}

function validateItemVatOrNoNotInSameBill(){
	var ids=document.getElementsByName('lines.id');
	var productIds=document.getElementsByName('lines.productId');
	var products=document.getElementsByName('lines.product');
	var productLabels=document.getElementsByName('lines.productLabel');
	var taxables=document.getElementsByName('lines.taxable');
	
	var countItemHavevat = 0;
	var countItemNoVat = 0;
	for(var i=0;i<ids.length;i++){
		if(taxables[i].value =='Y'){
			countItemHavevat++;
		}else{
			countItemNoVat++;
		}
	}
	
	if(countItemHavevat > 0 && countItemNoVat >0 ){
		alert("สินค้ามี VAT และ ไม่มี VAT ห้ามเปิดร่วมกันให้แยกบิลกัน");
		return false;
	}
	return true;
}

function calculatePrice(){
	var tbl = document.getElementById('tblProduct');
	// todo check vat
	var v = document.getElementsByName("order.vatCode")[0].value;
	var vl=0;
	
	var amounts = document.getElementsByName("lines.amount");
	var amounts1 = document.getElementsByName("lines.amount1");
	var amounts2 = document.getElementsByName("lines.amount2");
	
	var discs = document.getElementsByName("lines.disc");
	var discs1 = document.getElementsByName("lines.disc1");
	var discs2 = document.getElementsByName("lines.disc2");
	
	var afdiscs = document.getElementsByName("lines.afdisc");
	var afdiscs1 = document.getElementsByName("lines.afdisc1");
	var afdiscs2 = document.getElementsByName("lines.afdisc2");
	
	var totals = document.getElementsByName("lines.total");
	var totals1 = document.getElementsByName("lines.total1");
	var totals2 = document.getElementsByName("lines.total2");
	
	var vats = document.getElementsByName("lines.vat");
	var vats1 = document.getElementsByName("lines.vat1");
	var vats2 = document.getElementsByName("lines.vat2");
	
	var taxables = document.getElementsByName("lines.taxable");
	
	var total_ex_disc=0;
	var totalAmtSubDis=0,totalAmtSubDis1=0,totalAmtSubDis2=0 ;
	var vatLine=0,vatLine1=0,vatLine2=0;
	var totalAmtInVat=0,stotalAmtInVat1=0,totalAmtInVat2=0;
	
	var sumT=0;
	var sumTNonVat=0;
	var sumVat=0;
	var sumTot = 0;
	
	var orderType = document.getElementsByName('order.orderType')[0].value;
	var memberVIP = document.getElementsByName('memberVIP')[0].value;
	//alert(orderType);
	
	for(var i=0;i<totals1.length;i++){
		totalAmtSubDis=0;
		vatLine=0;
		totalAmtInVat=0;
		
		//Case product have Tax
		if(taxables[i].value=='Y'){
			if(amounts[i]){
				discs[i].value = Number(toFixed(discs[i].value,5)).toFixed(5);
				amounts[i].value = Number(toFixed(amounts[i].value,5)).toFixed(5);
				totalAmtSubDis = Number(toFixed(amounts[i].value,5)) - Number(toFixed(discs[i].value,5));
			}
			
			vatLine = (Number(v)*Number(totalAmtSubDis))/100;
			totalAmtInVat = Number(totalAmtSubDis)+Number(vatLine);
			
			if(amounts[i]){
				afdiscs[i].value = Number(toFixed(totalAmtSubDis,5)).toFixed(2);
				vats[i].value=Number(toFixed(vatLine,2)).toFixed(2);
				totals[i].value = Number(toFixed(totalAmtInVat,5)).toFixed(2);
			}
			
			//alert("bf amount1["+amounts1[i].value+"]af amounts1["+Number(toFixed(amounts1[i].value,5)).toFixed(5)+"] ::: bf amount2["+amounts2[i].value+"]af amounts2["+Number(toFixed(amounts2[i].value,5)).toFixed(5)+"]");
			
			amounts1[i].value = Number(toFixed(amounts1[i].value,5)).toFixed(5);
			amounts2[i].value = Number(toFixed(amounts2[i].value,5)).toFixed(5);
			
			discs1[i].value = Number(toFixed(discs1[i].value,5)).toFixed(5);
			discs2[i].value = Number(toFixed(discs2[i].value,5)).toFixed(5);
			
			totalAmtSubDis1 = Number(toFixed(amounts1[i].value,5)) - Number(toFixed(discs1[i].value,5));
			totalAmtSubDis2 = Number(toFixed(amounts2[i].value,5)) - Number(toFixed(discs2[i].value,5));
			
			vatLine1 = (Number(v)*Number(totalAmtSubDis1))/100;
			vatLine2 = (Number(v)*Number(totalAmtSubDis2))/100;
			
			totalAmtInVat1 = Number(totalAmtSubDis1)+ Number(vatLine1);
			totalAmtInVat2 = Number(totalAmtSubDis2)+ Number(vatLine2);
			
			afdiscs1[i].value = Number(toFixed(totalAmtSubDis1,5)).toFixed(2);
			afdiscs2[i].value = Number(toFixed(totalAmtSubDis2,5)).toFixed(2);
			
			vats1[i].value = Number(toFixed(vatLine1,2)).toFixed(2);
			vats2[i].value = Number(toFixed(vatLine2,2)).toFixed(2);
			
			totals1[i].value = Number(toFixed(totalAmtInVat1,5)).toFixed(2);
			totals2[i].value = Number(toFixed(totalAmtInVat2,5)).toFixed(2);
			
			sumT+= Number(afdiscs1[i].value) + Number(afdiscs2[i].value);
			//alert("afdiscs1["+afdiscs1[i].value+"],afdiscs2["+afdiscs2[i].value+"] -->sumT["+sumT+"]");
			
			sumVat+=Number(vats1[i].value) +  Number(vats2[i].value);
			sumTot+=Number(totals1[i].value) + Number(totals2[i].value);
			
			//display in table
			total_ex_disc = totalAmtSubDis1 + totalAmtSubDis2;
			//after discount >> netAmount
			tbl.rows[i+1].cells[8].innerHTML=addCommas(Number(Number(afdiscs1[i].value) + Number(afdiscs2[i].value)).toFixed(2));
		}else{
			
			//Case Product is no tax
			if(amounts[i]){
				discs[i].value = Number(toFixed(discs[i].value,5)).toFixed(5);
				amounts[i].value = Number(toFixed(amounts[i].value,5)).toFixed(5);
				totalAmtSubDis = Number(toFixed(amounts[i].value,5)) - Number(toFixed(discs[i].value,5));
			}
			
			vatLine = 0;
			totalAmtInVat = Number(totalAmtSubDis)+Number(vatLine);
			
			if(amounts[i]){
				afdiscs[i].value = Number(toFixed(totalAmtSubDis,5)).toFixed(2);
				vats[i].value=Number(toFixed(vatLine,2)).toFixed(2);
				totals[i].value = Number(toFixed(totalAmtInVat,5)).toFixed(2);
			}
			
			//alert("bf amount1["+amounts1[i].value+"]af amounts1["+Number(toFixed(amounts1[i].value,5)).toFixed(5)+"] ::: bf amount2["+amounts2[i].value+"]af amounts2["+Number(toFixed(amounts2[i].value,5)).toFixed(5)+"]");
			
			amounts1[i].value = Number(toFixed(amounts1[i].value,5)).toFixed(5);
			amounts2[i].value = Number(toFixed(amounts2[i].value,5)).toFixed(5);
			
			discs1[i].value = Number(toFixed(discs1[i].value,5)).toFixed(5);
			discs2[i].value = Number(toFixed(discs2[i].value,5)).toFixed(5);
			
			totalAmtSubDis1 = Number(toFixed(amounts1[i].value,5)) - Number(toFixed(discs1[i].value,5));
			totalAmtSubDis2 = Number(toFixed(amounts2[i].value,5)) - Number(toFixed(discs2[i].value,5));
			
			vatLine1 = 0;
			vatLine2 = 0;
			
			totalAmtInVat1 = Number(totalAmtSubDis1)+ Number(vatLine1);
			totalAmtInVat2 = Number(totalAmtSubDis2)+ Number(vatLine2);
			
			afdiscs1[i].value = Number(toFixed(totalAmtSubDis1,5)).toFixed(2);
			afdiscs2[i].value = Number(toFixed(totalAmtSubDis2,5)).toFixed(2);
			
			vats1[i].value = Number(toFixed(vatLine1,2)).toFixed(2);
			vats2[i].value = Number(toFixed(vatLine2,2)).toFixed(2);
			
			totals1[i].value = Number(toFixed(totalAmtInVat1,5)).toFixed(2);
			totals2[i].value = Number(toFixed(totalAmtInVat2,5)).toFixed(2);
			
			sumTNonVat+= Number(afdiscs1[i].value) + Number(afdiscs2[i].value);
			//alert("afdiscs1["+afdiscs1[i].value+"],afdiscs2["+afdiscs2[i].value+"] -->sumT["+sumT+"]");
			
			sumVat+=Number(vats1[i].value) +  Number(vats2[i].value);
			sumTot+=Number(totals1[i].value) + Number(totals2[i].value);
			
			//display in table
			total_ex_disc = totalAmtSubDis1 + totalAmtSubDis2;
			//after discount >> netAmount
			tbl.rows[i+1].cells[8].innerHTML=addCommas(Number(Number(afdiscs1[i].value) + Number(afdiscs2[i].value)).toFixed(2));
			
		}//if chekc taxable
	}
	
	//alert(sumT);
	//WIT Edit :08/06/2011  Cale Vat by total_amount of Header 
	
	//total_amount- total_discount  excluded vat
	//alert("sumT["+sumT+"]");
	
	var totalAmount = toFixed(Number(sumT)+Number(sumTNonVat),2);
	document.getElementsByName("order.totalAmount")[0].value = totalAmount;
	
	var totalAmountHaveVat = toFixed(Number(sumT),2);
	document.getElementById("tempTotalAmount").value = addCommas(Number(totalAmountHaveVat).toFixed(2));
	
	//Total Amount Non Vat
	var totalAmountNonVat = toFixed(Number(sumTNonVat),2);
	document.getElementsByName("order.totalAmountNonVat")[0].value = totalAmountNonVat;
	document.getElementById("tempTotalAmountNonVat").value = addCommas(Number(totalAmountNonVat).toFixed(2));
	
	//total_vat_amount
	var sumVatAmount = toFixed((Number(v)*Number(sumT))/100,2); 
	
	//alert("sumT["+sumT+"]sumTNonVat["+sumTNonVat+"]sumVatAmount["+sumVatAmount+"]");
	
	//sumTotalAmount+sumTotalAmountNonvat+totalVatAmount
	var totalNetAmount = toFixed(Number(sumT)+Number(sumVatAmount)+Number(sumTNonVat),2) ;
	document.getElementsByName("order.netAmount")[0].value = totalNetAmount;
	document.getElementById("tempNetAmount").value = addCommas(Number(totalNetAmount).toFixed(2));
	
	//recalc TotalVatAmount
	var sumVatAmount = Number(totalNetAmount).toFixed(2) - Number(totalAmount).toFixed(2);
	document.getElementsByName("order.vatAmount")[0].value = sumVatAmount;
	//Case sumVat =0 display "-"
	if(sumVatAmount ==0){
	   document.getElementById("tempVatAmount").value = "-";
	}else{
	   document.getElementById("tempVatAmount").value = addCommas(Number(sumVatAmount).toFixed(2));
	}
}
