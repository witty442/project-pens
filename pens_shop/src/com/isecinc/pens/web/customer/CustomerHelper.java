package com.isecinc.pens.web.customer;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.bean.References;

public class CustomerHelper {

	public static List<References> initTripList(){
		List<References> dataList = new ArrayList<References>();
		dataList.add(new References("", ""));
		for(int i=1;i<= 23;i++){
			dataList.add(new References(i+"", i+""));
		}
		dataList.add(new References(98+"", 98+""));
		return dataList;
	}
}
