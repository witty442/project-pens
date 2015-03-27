function isNum(obj){
  if(obj.value != ""){
	var newNum = parseFloat(obj.value);
	if(isNaN(newNum)){
		alert('����͡��੾�е���Ţ��ҹ��');
		obj.value = "";
		obj.focus();
		return false;
	}else{return true;}
   }
  return true;
}

function isNum2Digit(obj){
  if(obj.value != ""){
	var newNum = parseFloat(obj.value);
	if(isNaN(newNum)){
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
 }

function toCurreny(object){
	var temp =  object.value.replace(/\,/g,''); //alert(r);
	object.value = addCommas(Number(toFixed(temp,2)).toFixed(2));
}

function toFixed(num, pre){
	num = Number(num);
	num *= Math.pow(10, pre);
	num = (Math.round(num, pre) + (((num - Math.round(num, pre))>=0.5)?1:0)) / Math.pow(10, pre);
	return num.toFixed(pre);
}