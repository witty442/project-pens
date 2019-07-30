package com.isecinc.pens.web.stockonhand;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockBean.Comparators;

public class StockOnhandBean implements Comparable<StockOnhandBean> ,Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;

	private String pdType;
	private String pdCode;
	private String pdCodeIntransit;
	private String pdDesc;
	private String salesChannelNo;
	private String salesChannelName;

	private String brand;
	private String brandName;
	private String productCode;
	private String productName;
	private String dispType;
	private String dispPlan;
	private String dispPrice;
	private String dispHaveQty;
	private String pdQty;
	private String pdPrice;
	private String pdIntQty;
	private String pdIntPrice;
	private String createUser;
	private String updateUser;
	private String salesZone;
	private String subInv;
	private String subInvDesc;
	private String reportType;
	
	//option for display
	private List<StockOnhandBean> rowColumnDataList;
	private List<StockOnhandBean> itemsList;
	private StringBuffer dataStrBuffer;
	private String columnNameSort;
	private String orderSortType;
	private String onhandQty;
	private String priUomCode;
	private String orgCode;
	
	
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getOnhandQty() {
		return onhandQty;
	}
	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
	}
	public String getPriUomCode() {
		return priUomCode;
	}
	public void setPriUomCode(String priUomCode) {
		this.priUomCode = priUomCode;
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
	public List<StockOnhandBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<StockOnhandBean> itemsList) {
		this.itemsList = itemsList;
	}
    
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getSubInvDesc() {
		return subInvDesc;
	}
	public void setSubInvDesc(String subInvDesc) {
		this.subInvDesc = subInvDesc;
	}
	public String getSalesZone() {
		return salesZone;
	}
	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}
	public String getPdPrice() {
		return pdPrice;
	}
	public void setPdPrice(String pdPrice) {
		this.pdPrice = pdPrice;
	}
	public String getPdIntPrice() {
		return pdIntPrice;
	}
	public void setPdIntPrice(String pdIntPrice) {
		this.pdIntPrice = pdIntPrice;
	}
	public String getDispPrice() {
		return dispPrice;
	}
	public void setDispPrice(String dispPrice) {
		this.dispPrice = dispPrice;
	}
	public String getDispHaveQty() {
		return dispHaveQty;
	}
	public void setDispHaveQty(String dispHaveQty) {
		this.dispHaveQty = dispHaveQty;
	}
	public String getPdCodeIntransit() {
		return pdCodeIntransit;
	}
	public void setPdCodeIntransit(String pdCodeIntransit) {
		this.pdCodeIntransit = pdCodeIntransit;
	}
	public String getPdQty() {
		return pdQty;
	}
	public void setPdQty(String pdQty) {
		this.pdQty = pdQty;
	}
	
	public String getPdIntQty() {
		return pdIntQty;
	}
	public void setPdIntQty(String pdIntQty) {
		this.pdIntQty = pdIntQty;
	}
	public String getDispType() {
		return dispType;
	}
	public void setDispType(String dispType) {
		this.dispType = dispType;
	}
	public String getDispPlan() {
		return dispPlan;
	}
	public void setDispPlan(String dispPlan) {
		this.dispPlan = dispPlan;
	}
	public String getPdType() {
		return pdType;
	}
	public void setPdType(String pdType) {
		this.pdType = pdType;
	}
	public String getPdCode() {
		return pdCode;
	}
	public void setPdCode(String pdCode) {
		this.pdCode = pdCode;
	}
	public String getPdDesc() {
		return pdDesc;
	}
	public void setPdDesc(String pdDesc) {
		this.pdDesc = pdDesc;
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
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
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
	public List<StockOnhandBean> getRowColumnDataList() {
		return rowColumnDataList;
	}
	public void setRowColumnDataList(List<StockOnhandBean> rowColumnDataList) {
		this.rowColumnDataList = rowColumnDataList;
	}
	
	
	 @Override
	 public int compareTo(StockOnhandBean o) {
	     return Comparators.BRAND_ASC.compare(this, o);
	  }
	 
	  public static class Comparators {
		
	        public static Comparator<StockOnhandBean> BRAND_ASC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o1.brand.compareTo(o2.brand);
	            }
	        };
	        public static Comparator<StockOnhandBean> BRAND_DESC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o2.brand.compareTo(o1.brand);
	            }
	        };
	        
	        public static Comparator<StockOnhandBean> SUBINVENTORY_CODE_ASC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o1.subInv.compareTo(o2.subInv);
	            }
	        };
	        public static Comparator<StockOnhandBean> SUBINVENTORY_CODE_DESC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o2.subInv.compareTo(o1.subInv);
	            }
	        };
	        
	        public static Comparator<StockOnhandBean> SEGMENT1_ASC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o1.productCode.compareTo(o2.productCode);
	            }
	        };
	        public static Comparator<StockOnhandBean> SEGMENT1_DESC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o2.productCode.compareTo(o1.productCode);
	            }
	        };
	        
	        public static Comparator<StockOnhandBean> DESCRIPTION_ASC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o1.productName.compareTo(o2.productName);
	            }
	        };
	        public static Comparator<StockOnhandBean> DESCRIPTION_DESC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o2.productName.compareTo(o1.productName);
	            }
	        };
	        
	        public static Comparator<StockOnhandBean> PRIMARY_QUANTITY_ASC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o1.onhandQty.compareTo(o2.onhandQty);
	            }
	        };
	        public static Comparator<StockOnhandBean> PRIMARY_QUANTITY_DESC = new Comparator<StockOnhandBean>() {
	            @Override
	            public int compare(StockOnhandBean o1, StockOnhandBean o2) {
	                return o2.onhandQty.compareTo(o1.onhandQty);
	            }
	        };
	  }
	
}
