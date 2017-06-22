
function open_product(path, rowNo){
	if(rowNo==null)
		window.open(path + "/jsp/stock/stockProductPopup.jsp", "Product", "width=700,height=400,location=No,resizable=No");
	else
		window.open(path + "/jsp/stock/stockProductPopup.jsp?row="+rowNo, "Product", "width=700,height=400,location=No,resizable=No");
	return;
}

function open_product_premium(path, rowNo){
	if(rowNo==null)
		window.open(path + "/jsp/stock/stockProductPremiumPopup.jsp", "Product", "width=700,height=400,location=No,resizable=No");
	else
		window.open(path + "/jsp/stock/stockProductPremiumPopup.jsp?row="+rowNo, "Product", "width=700,height=400,location=No,resizable=No");
	return;
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
        //No,checkBox,Product,uom ,qty,createDate,expireDate 
        tds += '<td align="center"></td>';//0 No
        tds += '<td align="center"></td>';//1 checkBox
        tds += '<td align="left"></td>';//2 product
        tds += '<td align="center"></td>';//3 uom
        tds += '<td align="center"></td>';//4 qty 
        tds += '<td align="center"><input type="text" name="lined.createDate" id="createDate" onmouseover="popCalendar(this,this)" readonly/></td>';//5
        tds += '<td align="center"><input type="text" name="lines.expireDate" id="expireDate" onmouseover="popCalendar(this,this)" readonly/><font color="red">*</font></td>';//6
        
        tds += '</tr>';
        if($('tbody', this).length > 0){
            $('tbody', this).append(tds);
        }else {
            $(this).append(tds);
        }
    });
    //alert(objValue);
    setValueToProduct(path,objValue);
    
}

function addProduct(path,objValue){
	
	var tbl = document.getElementById('tblProduct');
	var index = -1;//findDupIndex(tbl,objValue);
	//alert("addproduct:index["+index+"]");
	
	if(index<0){
		addProduct2(path,objValue); //row blank
		return;
	}else{
		//alert(1);
		var lineNo = eval(document.getElementsByName('lines.lineNo')[index].value) + eval(objValue.lineNo);
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
		
		tbl.rows[index+1].cells[4].innerHTML = addCommas(qty1) //QTY
		
		//hidden field
		//alert("lineNo:"+lineNo);
		//document.getElementsByName('lines.lineNo')[index].value = lineNo;
		document.getElementsByName('lines.qty1')[index].value = qty1;
		document.getElementsByName('lines.qty2')[index].value = qty2;
		document.getElementsByName('lines.amount1')[index].value = amt1;
		document.getElementsByName('lines.amount2')[index].value = amt2;
		document.getElementsByName('lines.disc1')[index].value = disc1;
		document.getElementsByName('lines.disc2')[index].value = disc2;
		document.getElementsByName('lines.total1')[index].value = total1;
		document.getElementsByName('lines.total2')[index].value = total2;
		
		//WIT Edit :09/06/2011 :add method calculatePrice()
		calculatePrice();
	}
}

function findDupIndex(tbl,objValue){
	var i;
	var index = -1;
	
	for(i=0;i<tbl.rows.length;i++){
		if(i!=0){
			//alert(document.getElementsByName('lines.productId')[i-1].value+":"+objValue.productId+"<>"+document.getElementsByName('lines.uom1')[i-1].value+":"+objValue.uom1);
			if(document.getElementsByName('lines.productId')[i-1].value==objValue.productId){
				if(document.getElementsByName('lines.uom1')[i-1].value==objValue.uom1){
					index = (i-1);
				}
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
	//alert(objValue.lineNo);
			
	inputLabel+="<input type='hidden' name='lines.id' value='"+objValue.id+"'>";//1
	inputLabel+="<input type='hidden' name='lines.row' value='"+objValue.row+"'>";//2
	inputLabel+="<input type='hidden' name='lines.lineNo' value='"+objValue.lineNo+"'>";//2.1 
	inputLabel+="<input type='hidden' name='lines.productId' value='"+objValue.productId+"'>";//3
	inputLabel+="<input type='hidden' name='lines.product' value='"+objValue.product+"'>";//4
	inputLabel+="<input type='hidden' name='lines.productLabel' value='"+objValue.productLabel+"'>";//5
	
	inputLabel+="<input type='hidden' name='lines.uom1' value='"+objValue.uom1+"'>";//6
	inputLabel+="<input type='hidden' name='lines.uom2' value='"+objValue.uom2+"'>";//7
	inputLabel+="<input type='hidden' name='lines.uomLabel1' value='"+objValue.uomLabel1+"'>";//8
	inputLabel+="<input type='hidden' name='lines.uomLabel2' value='"+objValue.uomLabel2+"'>";//9
	inputLabel+="<input type='hidden' name='lines.fullUom' value='"+objValue.uom1+'/'+objValue.uom2+"'>";//10
	inputLabel+="<input type='hidden' name='lines.price1' value='"+objValue.price1+"'>";//11
	inputLabel+="<input type='hidden' name='lines.price2' value='"+objValue.price2+"'>";//12
	inputLabel+="<input type='hidden' name='lines.qty1' value='"+objValue.qty1+"'>";//13
	inputLabel+="<input type='hidden' name='lines.qty2' value='"+objValue.qty2+"'>";//14
	inputLabel+="<input type='hidden' name='lines.amount1' value='"+objValue.amount1+"'>";//15
	inputLabel+="<input type='hidden' name='lines.amount2' value='"+objValue.amount2+"'>";//16
	inputLabel+="<input type='hidden' name='lines.disc1' value='"+objValue.disc1+"'>";//17
	inputLabel+="<input type='hidden' name='lines.disc2' value='"+objValue.disc2+"'>";//18
	inputLabel+="<input type='hidden' name='lines.afdisc1'>";//19
	inputLabel+="<input type='hidden' name='lines.afdisc2'>";//20
	inputLabel+="<input type='hidden' name='lines.vat' value='0'>";//21
	inputLabel+="<input type='hidden' name='lines.vat1' value='0'>";//22
	inputLabel+="<input type='hidden' name='lines.vat2' value='0'>";//23
	inputLabel+="<input type='hidden' name='lines.total1' value='"+objValue.total1+"'>";//24
	inputLabel+="<input type='hidden' name='lines.total2' value='"+objValue.total2+"'>";//25
	
	var checkBoxLabel='<input type="checkbox" name="lineids" value="0"/>';//30
	
	var iconLabel="";
	iconLabel+='<a href="#" onclick="open_product(\''+path+'\','+objValue.row+');">';
	iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
	
	var createDateLabel='<input type="text" name="lines.createDate" id="createDate" onmouseover="popCalendar(this,this)"  readonly/>';
	var expireDateLabel='<input type="text" name="lines.expireDate" id="expireDate" onmouseover="popCalendar(this,this)"  readonly/><font color="red">*</font>';
	
	//Column
	//No,Product,uom ,qty,lineAmount,edit
	var c=0;
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.row;
	tbl.rows[objValue.row].cells[c++].innerHTML=checkBoxLabel;
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.product+' '+objValue.productLabel+inputLabel;
	tbl.rows[objValue.row].cells[c++].innerHTML=objValue.uomLabel1;// + '/' + objValue.uomLabel2;
	tbl.rows[objValue.row].cells[c++].innerHTML= addCommas(objValue.qty1) ;//+ '/' + addCommas(objValue.qty2);
	tbl.rows[objValue.row].cells[c++].innerHTML= createDateLabel;
	tbl.rows[objValue.row].cells[c++].innerHTML= expireDateLabel;
	
	calculatePrice();
	return true;
}

/** Create Product Lazy List */
function createProductList(){

	var divlines = document.getElementById('productList');
	
	var ids=document.getElementsByName('lines.id');
	var lineNo=document.getElementsByName('lines.lineNo');
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
	
	var createDate= document.getElementsByName('lines.createDate');
	var expireDate= document.getElementsByName('lines.expireDate');
	
	var inputLabel="";
	
	if(ids.length==0)
	{
		alert('ใส่ข้อมูลสินค้า');
		return false;
	}
	
	//Validate creatDate
	var error = false;
	var errorDate = false;
	for(var i=0;i<ids.length;i++){
		/*if(createDate[i].value==""){
			createDate[i].className ="lineError";
			error = true;
		}else{
			createDate[i].className ="";
		}*/
		
		if(expireDate[i].value==""){
			expireDate[i].className ="lineError";
			error = true;
		}else{
			expireDate[i].className ="";
		}
		//Check case CreateDate not null
		if(createDate[i].value !=""){
			if(validateDate(createDate[i].value,expireDate[i].value)==false ){
				errorDate = true;
				createDate[i].className ="lineError";
				expireDate[i].className ="lineError";
				//alert("false");
			}else{
				//alert("true");
				createDate[i].className ="";
				expireDate[i].className ="";
			}
		}
	}
	
	if(error==true){
		alert('ใส่ข้อมูลให้ครบถ้วน');
		return false;
	}
	if(errorDate==true){
		alert('ขอบเขตวันที่ไม่ถูกต้อง   วันที่หมดอายุ ต้องมากกว่าวันที่ผลิต');
		return false;
	}
	
	
	divlines.innerHTML="";
	
	for(i=0;i<ids.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='lines["+i+"].id' value='"+ids[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].lineNo' value='"+lineNo[i].value+"'>";
		
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
		
		inputLabel+="<input type='text' name='lines["+i+"].amount1' value='"+amounts1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].amount2' value='"+amounts2[i].value+"'>";
	
		inputLabel+="<input type='text' name='lines["+i+"].discount1' value='"+discs1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].discount2' value='"+discs2[i].value+"'>";
		
		inputLabel+="<input type='text' name='lines["+i+"].vatAmount1' value='"+vats1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].vatAmount2' value='"+vats2[i].value+"'>";
		
		inputLabel+="<input type='text' name='lines["+i+"].totalAmount1' value='"+totals1[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].totalAmount2' value='"+totals2[i].value+"'>";
		
		inputLabel+="<input type='text' name='lines["+i+"].createDate' value='"+createDate[i].value+"'>";
		inputLabel+="<input type='text' name='lines["+i+"].expireDate' value='"+expireDate[i].value+"'>";
		inputLabel+="<hr/>";
		divlines.innerHTML += inputLabel;
		//alert(inputLabel);
	}
	
	//alert(divlines.innerHTML);
	
	return true;
}

function calculatePrice(){
	//var tbl = document.getElementById('tblProduct');
	// todo check vat
	
	//var amounts = document.getElementsByName("lines.amount");
	//var amounts1 = document.getElementsByName("lines.amount1");
	//var amounts2 = document.getElementsByName("lines.amount2");
	//var sumAmount = 0;
	
	//var totals = document.getElementsByName("lines.total");
	//var totals1 = document.getElementsByName("lines.total1");
	//var totals2 = document.getElementsByName("lines.total2");
	
	//for(i=0;i<totals1.length;i++){
		//sumAmount += Number(amounts1[i].value) + Number(amounts2[i].value);
	//}
	//document.getElementsByName("bean.totalAmount")[0].value = addCommas(Number(toFixed(sumAmount,5)).toFixed(2));

}

function toFixed(num, pre){
	num = Number(num);
	num *= Math.pow(10, pre);
	num = (Math.round(num, pre) + (((num - Math.round(num, pre))>=0.5)?1:0)) / Math.pow(10, pre);
	return num.toFixed(pre);
}

function deleteProduct(path,type){
	//todo play with type
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("lineids");
	var lineNo = document.getElementsByName("lines.lineNo");
	var lineNoDeleteArray = document.getElementsByName('lineNoDeleteArray')[0];
	var delId = document.getElementsByName('deletedId')[0];
	
	
	var drow;
	var bcheck=false;
	for(i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			//alert(i+":"+lineNo[i].value);
			if(lineNo[i].value != "0" && lineNo[i].value != "" && lineNo[i].value != 'undefined'){
			   lineNoDeleteArray.value += lineNo[i].value+","; 
			}
			
			delId.value+=","+chk[i].value;
			// alert(i);
			drow = tbl.rows[i+1];
			$(drow).remove();
			bcheck=true;
			
			
		}
	}//for
	//alert(lineNoDeleteArray.value);
	
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	chk = document.getElementsByName("lineids");
	var iconLabel="";
	for(i=0;i<chk.length;i++){
		tbl.rows[i+1].cells[0].innerHTML=(i+1);
		iconLabel="";
		iconLabel+='<a href="#" onclick="open_product(\''+path+'\','+(i+1)+');">';
		iconLabel+="<img border=0 src='"+path+"/icons/doc_edit.gif'></a>";
		
		//tbl.rows[i+1].cells[6].innerHTML=iconLabel;
		
	}
	
	calculatePrice();
}

function validateDate(DateFrom, DateTo){
	if(DateFrom=='' || DateTo==''){return true;}
	DateFrom = DateFrom.split("/");
	starttime = new Date(DateFrom[2],DateFrom[1]-1,DateFrom[0]);

	DateTo = DateTo.split("/");
	endtime = new Date(DateTo[2],DateTo[1]-1,DateTo[0]);
	
	if(starttime.getTime() >= endtime.getTime()){
		return false;
	}else{
		return true;
	}
}
