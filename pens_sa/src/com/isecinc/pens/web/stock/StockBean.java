package com.isecinc.pens.web.stock;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import util.Utils;

public class StockBean implements Comparable<StockBean>,Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;
	private long id;
	private long rowId;
	private String typeSearch;
	private String startDate;
	private String endDate;
	private String brand;
	private String brandGroup;
	private String brandName;
	private String salesChannelNo;
	private String salesChannelName;
	private String division;
	private String custCatNo;
	private String customerId;
	private String customerGroup;
	private String customerCode;
	private String customerName;
	private String createUser;
	private String updateUser;
	
	private long lineId;
	private String salesrepId;
	private String salesrepCode;
	private String salesrepName;
	private String itemCode;
	private String itemName;
	private String itemId;
	private String price;
	private String priQty;
	private String secQty;
	private String orderQty;
	private String avgQty;
	private String expireDate;
	private String requestDate;
	//total
	
	//Control Access Button

	//optional
	private String period;
	private String periodDesc;
	private String reportType;
	private String dispType;
	private String dispRequestDate;
	private String dispLastUpdate;
	
	private List<StockBean> itemsList;
    private StringBuffer dataStrBuffer;
    private String columnNameSort;
    private String orderSortType;
    
    
    
	public String getTypeSearch() {
		return typeSearch;
	}

	public void setTypeSearch(String typeSearch) {
		this.typeSearch = typeSearch;
	}

	public String getAvgQty() {
		return avgQty;
	}

	public void setAvgQty(String avgQty) {
		this.avgQty = avgQty;
	}

	public String getDispLastUpdate() {
		return dispLastUpdate;
	}

	public void setDispLastUpdate(String dispLastUpdate) {
		this.dispLastUpdate = dispLastUpdate;
	}

	public String getDispRequestDate() {
		return dispRequestDate;
	}

	public void setDispRequestDate(String dispRequestDate) {
		this.dispRequestDate = dispRequestDate;
	}

	public String getColumnNameSort() {
		return columnNameSort;
	}

	public void setColumnNameSort(String columnNameSort) {
		this.columnNameSort = columnNameSort;
	}

	public String getOrderSortType() {
		return orderSortType;
	}

	public void setOrderSortType(String orderSortType) {
		this.orderSortType = orderSortType;
	}

	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}

	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
	}

	public String getSalesrepName() {
		return salesrepName;
	}

	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getBrandGroup() {
		return brandGroup;
	}

	public void setBrandGroup(String brandGroup) {
		this.brandGroup = brandGroup;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getSalesChannelNo() {
		return salesChannelNo;
	}

	public void setSalesChannelNo(String salesChannelNo) {
		this.salesChannelNo = salesChannelNo;
	}

	public String getSalesChannelName() {
		return salesChannelName;
	}

	public void setSalesChannelName(String salesChannelName) {
		this.salesChannelName = salesChannelName;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getCustCatNo() {
		return custCatNo;
	}

	public void setCustCatNo(String custCatNo) {
		this.custCatNo = custCatNo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerGroup() {
		return customerGroup;
	}

	public void setCustomerGroup(String customerGroup) {
		this.customerGroup = customerGroup;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}


	public long getLineId() {
		return lineId;
	}

	public void setLineId(long lineId) {
		this.lineId = lineId;
	}

	public String getSalesrepId() {
		return salesrepId;
	}

	public void setSalesrepId(String salesrepId) {
		this.salesrepId = salesrepId;
	}

	public String getSalesrepCode() {
		return salesrepCode;
	}

	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPriQty() {
		return priQty;
	}

	public void setPriQty(String priQty) {
		this.priQty = priQty;
	}

	public String getSecQty() {
		return secQty;
	}

	public void setSecQty(String secQty) {
		this.secQty = secQty;
	}

	public String getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(String orderQty) {
		this.orderQty = orderQty;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPeriodDesc() {
		return periodDesc;
	}

	public void setPeriodDesc(String periodDesc) {
		this.periodDesc = periodDesc;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getDispType() {
		return dispType;
	}

	public void setDispType(String dispType) {
		this.dispType = dispType;
	}

	public List<StockBean> getItemsList() {
		return itemsList;
	}

	public void setItemsList(List<StockBean> itemsList) {
		this.itemsList = itemsList;
	}
	/*	public int compareTo(StockBean compareStockBean) {

	String compareBrand = ((StockBean) compareStockBean).getBrand();

	//ascending order
	return this.brand - compareBrand;

	//descending order
	//return compareQuantity - this.quantity;

}*/

	 @Override
	 public int compareTo(StockBean o) {
	     return Comparators.BRAND_ASC.compare(this, o);
	  }
	 
	  public static class Comparators {
		  public static Comparator<StockBean> AVG_QTY_ASC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	            	 return Integer.parseInt(o1.avgQty) - Integer.parseInt(o2.avgQty);
	            }
	        };
	        public static Comparator<StockBean> AVG_QTY_DESC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	            	 return Integer.parseInt(o2.avgQty) - Integer.parseInt(o1.avgQty);
	            }
	        };
	        
	        public static Comparator<StockBean> BRAND_ASC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o1.brand.compareTo(o2.brand);
	            }
	        };
	        public static Comparator<StockBean> BRAND_DESC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o2.brand.compareTo(o1.brand);
	            }
	        };
	        
	        public static Comparator<StockBean> SALES_CHANNEL_ASC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o1.getSalesChannelName().compareTo(o2.getSalesChannelName());
	            }
	        };
	        public static Comparator<StockBean> SALES_CHANNEL_DESC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o2.getSalesChannelName().compareTo(o1.getSalesChannelName());
	            }
	        };
	        
	        public static Comparator<StockBean> CUSTOMER_ASC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o1.getCustomerCode().compareTo(o2.getCustomerCode());
	            }
	        };
	        public static Comparator<StockBean> CUSTOMER_DESC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o2.getCustomerCode().compareTo(o1.getCustomerCode());
	            }
	        };
	        
	        public static Comparator<StockBean> SKU_ASC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o1.getItemCode().compareTo(o2.getItemCode());
	            }
	        };
	        public static Comparator<StockBean> SKU_DESC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o2.getItemCode().compareTo(o1.getItemCode());
	            }
	        };
	        
	        public static Comparator<StockBean> SALES_ASC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o1.getSalesrepCode().compareTo(o2.getSalesrepCode());
	            }
	        };
	        public static Comparator<StockBean> SALES_DESC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return o2.getSalesrepCode().compareTo(o1.getSalesrepCode());
	            }
	        };
	        
	        public static Comparator<StockBean> REQUEST_DATE_ASC = new Comparator<StockBean>(){
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	            	 Date o1Date = null;
	            	 Date o2Date = null;
	            	try{
	            	  o1Date = Utils.parse(o1.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	            	  o2Date = Utils.parse(o2.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

	            	}catch(Exception e){}
	            	return o1Date.compareTo(o2Date);
	            }
	        };
	        public static Comparator<StockBean> REQUEST_DATE_DESC = new Comparator<StockBean>(){
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	            	 Date o1Date = null;
	            	 Date o2Date = null;
	            	try{
	            	  o1Date = Utils.parse(o1.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	            	  o2Date = Utils.parse(o2.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

	            	}catch(Exception e){}
	            	return o2Date.compareTo(o1Date);
	            }
	        };
	        
	        public static Comparator<StockBean> EXPIRE_DATE_ASC = new Comparator<StockBean>(){
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	            	 Date o1Date = null;
	            	 Date o2Date = null;
	            	try{
	            	  o1Date = Utils.parse(o1.getExpireDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	            	  o2Date = Utils.parse(o2.getExpireDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

	            	}catch(Exception e){}
	            	return o1Date.compareTo(o2Date);
	            }
	        };
	        public static Comparator<StockBean> EXPIRE_DATE_DESC = new Comparator<StockBean>(){
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	            	 Date o1Date = null;
	            	 Date o2Date = null;
	            	try{
	            	  o1Date = Utils.parse(o1.getExpireDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	            	  o2Date = Utils.parse(o2.getExpireDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

	            	}catch(Exception e){}
	            	return o2Date.compareTo(o1Date);
	            }
	        };
	        
	        public static Comparator<StockBean> PRI_QTY_ASC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return Integer.parseInt(o1.priQty) - Integer.parseInt(o2.priQty);
	            }
	        };
	        public static Comparator<StockBean> PRI_QTY_DESC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return Integer.parseInt(o2.priQty) - Integer.parseInt(o1.priQty);
	            }
	        };
	        
	        public static Comparator<StockBean> SEC_QTY_ASC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return Integer.parseInt(o1.secQty) - Integer.parseInt(o2.secQty);
	            }
	        };
	        public static Comparator<StockBean> SEC_QTY_DESC = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                return Integer.parseInt(o2.secQty) - Integer.parseInt(o1.secQty);
	            }
	        };
	        
	       /* public static Comparator<StockBean> SKU = new Comparator<StockBean>() {
	            @Override
	            public int compare(StockBean o1, StockBean o2) {
	                int i = o1.name.compareTo(o2.name);
	                if (i == 0) {
	                    i = o1.age - o2.age;
	                }
	                return i;
	            }
	        };*/
	    }

}
