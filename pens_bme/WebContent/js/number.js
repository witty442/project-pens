
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

function isNum(obj){
  if(obj.value != ""){
	if(isNaN(obj.value)){
		alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
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
		alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
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

function currenyToNum(object){
	var temp =  object.value.replace(/\,/g,''); //alert(r);
	return temp;
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