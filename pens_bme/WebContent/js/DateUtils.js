/** 10/10/2555 to 10/10/2012**/
function thaiDateToChristDate(thaiDate){
	if(thaiDate != ''){
		var dd = thaiDate.substring(0,2);
	    var mm =  thaiDate.substring(3,5);
	    var yyyy =  parseFloat(thaiDate.substring(6,10))-543;
		//alert(dd+"/"+mm+"/"+yyyy);
	    return new Date(mm+"/"+dd+"/"+yyyy);
	}
}

/** 10/10/2561 to 10/10/2018**/
function thaiDateToEngDate(thaiDate){
	if(thaiDate != ''){
		var dd = thaiDate.substring(0,2);
	    var mm =  thaiDate.substring(3,5);
	    var yyyy =  parseFloat(thaiDate.substring(6,10))-543;
		//alert(dd+"/"+mm+"/"+yyyy);
	    return new Date(mm+"/"+dd+"/"+yyyy);
	}
}

/**
 * 
 * @param date1(thaiDate) 10/10/2561
 * @param date2(thaiDate) 10/11/2561
 * Return : 0(equals) , 1(date2 >= date1), 2(date2 > date1) , -1(date2 < date1)
 */
function compareDate(date1,date2){
	 date1 = thaiDateToEngDate(date1);
	 date2 = thaiDateToEngDate(date2);
	 
	 if(date2.getTime() == date1.getTime()){
		 return 0;
	 }
	 if(date2.getTime() >= date1.getTime()){
		 return 1;
	 }
	 if(date2.getTime() > date1.getTime()){
		 return 2;
	 }
	 if(date2.getTime() < date1.getTime()){
		 return -1;
	 }
}
