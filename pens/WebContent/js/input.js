function checkSelect(chk1,chk2)
{
	for(var i=0;i<chk2.length;i++){
		chk2[i].checked = chk1.checked;
	}
	return;
}

// time format ==> hh:mm
function inputTime(e, objText){
	var keynum = e.keyCode;
	
 	if(inputNum(e)){
		if((objText.value.length==0 && keynum!=8) || keynum==37 || keynum==39){
			if(keynum > 50){return false;}	
		}
		if((objText.value.length==2 && keynum!=8) || keynum==37 || keynum==39){
			var hour = objText.value.substring(0, 2);
			if(eval(hour) > 23){
				objText.value = objText.value.substring(0, objText.value.length-1);
				return false;
			}
		}
		if(objText.value.length==2){
			objText.value += ':';
		}
		if((objText.value.length==5 && keynum!=8) || keynum==37 || keynum==39){
			var minute = objText.value.substring(3,5);
			if(eval(minute) > 59){
				objText.value = objText.value.substring(0, objText.value.length-1);
				return false;
			}
		}
		
		return true;
	}else{
		return false;
	}
}

function inputNum(e){
	var keynum;
	if(window.event){// IE
		keynum = e.keyCode;
	}else if(e.which){// NC//FF//OP
		keynum = e.which;
	}
	
	//alert(keynum);
	if(!keynum){return true;}
	if(keynum==8){return true;}
	if(keynum==9){return true;}
	
	//  eng top(pc) 48 -57
	//  thai numpad(tablet) 231
	//  del = 46
	//  backspace = 8
	//  left arrow	 37
	//  right arrow	 39
	//  eng numpad(pc) 96-105
	if ((keynum>=48 && keynum<=57) || keynum==231 || keynum==37 || keynum==39 ||(keynum>=96 && keynum<=105)){
		return true;
	}else{
	    return false;
	}
}

function inputNum2(e,valueObj){
	var keynum;
	if(window.event){// IE
		keynum = e.keyCode;
	}else if(e.which){// NC//FF//OP
		keynum = e.which;
	}
	
	//alert(keynum);
	if(!keynum){return true;}
	if(keynum==8){return true;}
	if(keynum==9){return true;}
	
	//  eng top(pc) 48 -57
	//  thai numpad(tablet) 231
	//  del = 46
	//  backspace = 8
	//  left arrow	 37
	//  right arrow	 39
	//  eng numpad(pc) 96-105
	if ((keynum>=48 && keynum<=57) || keynum==231 || keynum==46 || keynum==8 || keynum==37 || keynum==39 ||(keynum>=96 && keynum<=105)){
		return true;
	}else{
	    return false;
	}
}


function isNum0to9andpoint(objText, e){// get number and point
	var keynum;
	if(window.event){// IE
		keynum = e.keyCode;
	}else if(e.which){// NC//FF//OP
		keynum = e.which;
	}
	//alert("x"+keynum);
	if(!keynum){return true;}
	if(keynum==8 || keynum==9 || keynum==189){return true;}
  	if ((keynum>=48 && keynum<=57) || keynum==46 || keynum==190 || keynum==110 || keynum==37 || keynum==39 || (keynum>=96 && keynum<=105)){
  		if(keynum==190 || keynum==110){
  			//check point(.)
  			if(objText.value.indexOf('.')>0){
  				return false;
  			}
  		}
		return true;
	 }else{
	    return false;
	 }
}

function inputReal(objText,point,e){
	var keynum;
	if(window.event){// IE
		keynum = e.keyCode;
	}else if(e.which){// NC//FF//OP
		keynum = e.which;
	}
	var pos = objText.selectionStart;
	var cursorpos;
	if(pos==0){
		cursorpos = pos;
	}else{
		if(!pos){// IE
			cursorpos = getCursorPos(objText);
		}else{// NC,FF,OP
			cursorpos = pos;
		}
	}
	if(keynum){
		if(keynum!=8){
			if(isNum0to9andpoint(e)==false){
				return false;
			}
			var num = objText.value;
			if(num.indexOf('.')>8){
				if(num.length>11){return false};
			}
			if(keynum!=46){
				if((num.indexOf('.')==-1)&&(num.length==9)){return false;}
			}else{
				if(num.length-cursorpos>2){return false;}
			}
			if((num.indexOf('.')!=-1)&&(keynum==46)){
				return false;
			}
			if((num.indexOf('.')!=-1)){
				var x = num.substring(num.indexOf('.')+1,num.length);
				if(x.length>(point-1)){// check size of number of back point
					if(cursorpos>num.indexOf('.')){
						return false;
					}		
				}
			}
		}
	}
	return true;
}

function getCursorPos(textElement) {
// create a range object and save off it's text
	var objRange = document.selection.createRange();
	var sOldRange = objRange.text;
 
// set this string to a small string that will not normally be encountered
	var sWeirdString = '#%~';
 
// insert the weirdstring where the cursor is at
	objRange.text = sOldRange + sWeirdString; 
	objRange.moveStart('character', (0 - sOldRange.length - sWeirdString.length));

// save off the new string with the weirdstring in it
	var sNewText = textElement.value;

// set the actual text value back to how it was
	objRange.text = sOldRange;

// look through the new string we saved off and find the location of
// the weirdstring that was inserted and return that value
	for (i=0; i <= sNewText.length; i++) {
		var sTemp = sNewText.substring(i, i + sWeirdString.length);
		if (sTemp == sWeirdString) {
			var cursorPos = (i - sOldRange.length);
			return cursorPos;
		}
	}
}

function addCommas(nStr) {
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}

// -------- check telephone number -----------
var digits = "0123456789";
var phoneNumberDelimiters = "()- ";
var validWorldPhoneChars = phoneNumberDelimiters + "+";
var minDigitsInIPhoneNumber = 9;

function isInteger(s){   
	var i;
    for (i = 0; i < s.length; i++){   
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    return true;
}

function trim(s){  
	var i;
    var returnString = "";
    for (i = 0; i < s.length; i++){   
        var c = s.charAt(i);
        if (c != " ") returnString += c;
    }
    return returnString;
}

function stripCharsInBag(s, bag)
{   
	var i;
    var returnString = "";
    for (i = 0; i < s.length; i++){   
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
    return returnString;
}

function checkInternationalPhone(strPhone){
	// allow '-' in phone & mobile field.
	if(strPhone=='-'){
		return true;
	}
	var bracket=3;
	strPhone=trim(strPhone);
	if(strPhone.indexOf("+")>1) return false;
	if(strPhone.indexOf("-")!=-1)bracket=bracket+1;
	if(strPhone.indexOf("(")!=-1 && strPhone.indexOf("(")>bracket)return false;
	var brchr=strPhone.indexOf("(");
	if(strPhone.indexOf("(")!=-1 && strPhone.charAt(brchr+2)!=")")return false;
	if(strPhone.indexOf("(")==-1 && strPhone.indexOf(")")!=-1)return false;
	s=stripCharsInBag(strPhone,validWorldPhoneChars);
	return (isInteger(s) && s.length >= minDigitsInIPhoneNumber);
}

// check number with jQuery.
function checkNum(objText){
	var newNum = parseInt(objText.val());
	if(isNaN(newNum)){
		alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
		objText.focus();
		return false;
	}else{return true;}
}

function compareCurrentDate(objText){
	var d = new Date();
	var startDate = d.getDate() + "/" + (d.getMonth()+1) + "/" + (d.getFullYear()+543);
	var date = objText.value;
	
	if(startDate=='' || date==''){return true;}
	startDate = startDate.split("/");
	starttime = new Date(startDate[2],startDate[1]-1,startDate[0]);

	date = date.split("/");
	endtime = new Date(date[2],date[1]-1,date[0]);
	if((endtime-starttime) < 0){
		alert('วันที่ไม่ถูกต้อง');
		objText.focus();
		return false;
	}else{
		return true;
	}
}

function datedifference(objDateFrom, objDateTo){
	var DateFrom = objDateFrom.value;
	var DateTo = objDateTo.value;
	if(DateFrom=='' || DateTo==''){return true;}
	DateFrom = DateFrom.split("/");
	starttime = new Date(DateFrom[2],DateFrom[1]-1,DateFrom[0]);

	DateTo = DateTo.split("/");
	endtime = new Date(DateTo[2],DateTo[1]-1,DateTo[0]);
	if((endtime-starttime) < 0){
		alert('ขอบเขตวันที่ไม่ถูกต้อง');
		objDateFrom.focus();
		return false;
	}else{
		return true;
	}
}

function checkDateDiff(DateFrom, DateTo){
	if(DateFrom=='' || DateTo==''){return true;}
	DateFrom = DateFrom.split("/");
	starttime = new Date(DateFrom[2],DateFrom[1]-1,DateFrom[0]);

	DateTo = DateTo.split("/");
	endtime = new Date(DateTo[2],DateTo[1]-1,DateTo[0]);
	if((endtime-starttime) < 0){
		alert('ขอบเขตวันที่ไม่ถูกต้อง');
		return false;
	}else{
		return true;
	}
}

function validateCreditExpired(e, objText){
	var keynum = e.keyCode;
	var year = new String(new Date().getFullYear()).substring(2, 4);
	
 	if(inputNum(e)){
		if((objText.value.length==0 && keynum!=8) || keynum==37 || keynum==39){
			if(keynum > 49){return false;}	
		}
		if((objText.value.length==1 && keynum!=8) || keynum==37 || keynum==39){
			if(keynum > 50){return false;}
		}
		if(objText.value.length==2){
			objText.value += '/';
		}
		if((objText.value.length==5 && keynum!=8) || keynum==37 || keynum==39){
			var creditYear = objText.value.substring(3,5); 
			if(eval(creditYear) < eval(year)){
				objText.value = objText.value.substring(0, objText.value.length-1);
				return false;
			}
		}
		
		return true;
	}else{
		return false;
	}
}

function days_between(date1, date2) {
    var one_day = 1000 * 60 * 60 * 24;
    var new_date1 = date1.split('/')[1] +'/'+ date1.split('/')[0]+'/'+ date1.split('/')[2];
    var new_date2 = date2.split('/')[1] +'/'+ date2.split('/')[0]+'/'+ date2.split('/')[2];
    var date1_ms = new Date(new_date1).getTime();
    var date2_ms = new Date(new_date2).getTime();
    var difference_ms = Math.abs(date1_ms - date2_ms);
    
    return Math.round(difference_ms/one_day);
}

function isNumeric(obj){
	if(isNaN(obj.value)){
		obj.value = "";
	}
}