
function convetTxtObjToFloat(obj){
	// alert(obj.value);
	 try{
	  if(obj.value==''){
		  return 0;
	  }
	  return parseFloat(obj.value.replace(/\,/g,''));
	 }catch(err) {
		 alert(err.message);
	 }
  }

function convetTxtValueToFloat(value){
  // alert(obj.value);
  try{
	 if(value==''){
		  return 0;
	 }
	 return parseFloat(value.replace(/\,/g,''));
  }catch(err) {
	 alert(err.message);
  }
}

function isNum(obj){
  if(obj.value != ""){
	  //remove comma
	obj.value = obj.value.replace(/\,/g,'');
	if(isNaN(obj.value)){
		alert('����͡��੾�е���Ţ��ҹ��');
		obj.value = "";
		obj.focus();
		return false;
	}else{
		//if found comma
		return true;
	}
   }
  return true;
}

function isNumPositive(obj){
  if(obj.value != ""){
	 var v = obj.value;
	if(isNaN(v)){
		alert('����͡��੾�е���Ţ��ҹ��');
		obj.value = "";
		obj.focus();
		return false;
	}else{
		if(v < 0){
			alert('�������ö��͡ ����Ţ�Դź��');
			obj.value = "";
			obj.focus();
			return false;
		}
		return true;
	}
   }
  return true;
}

function isNum2Digit(obj){
  try{
	  if(obj.value != ""){
		var newNum = convetTxtValueToFloat(obj.value);
		if( !isNotNumeric(obj.value)){
			alert('����͡��੾�е���Ţ��ҹ��');
			obj.value = "";
			obj.focus();
			return false;
		}else{
			toCurreny(obj);
			return true;
		}
	   }
	  return true;
  }catch(Exception ){
	  alert('����͡��੾�е���Ţ��ҹ��');
	  obj.value = "";
	  obj.focus();
	  return false;
  }
 }

function isNum2DigitValue(value){
  try{
	  if(value != ""){
		var newNum = convetTxtValueToFloat(value);
		if( !isNotNumeric(value)){
			return false;
		}else{
			return true;
		}
	   }
	  return true;
  }catch(Exception ){
	  return false;
  }
 }

function isNotNumeric(n) {
	n = n.replace(/\,/g,'');
	//alert(n);
	return !isNaN(parseFloat(n)) && isFinite(n);
}

function currenyToNum(object){
	var temp =  object.value.replace(/\,/g,''); //alert(r);
	return temp;
}
	
function toCurreny(object){
	var temp =  object.value.replace(/\,/g,''); //alert(r);
	object.value = addCommas(Number(toFixed(temp,2)).toFixed(2));
}

function toCurrenyNoDigit(object){
	var temp =  object.value.replace(/\,/g,''); //alert(r);
	object.value = addCommas(Number(temp));
}

function toFixed(num, pre){
	num = Number(num);
	num *= Math.pow(10, pre);
	num = (Math.round(num, pre) + (((num - Math.round(num, pre))>=0.5)?1:0)) / Math.pow(10, pre);
	return num.toFixed(pre);
}