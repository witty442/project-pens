package com.isecinc.pens.web.popupsearch;

public class PopupSearchHelper {
	public String titleSearch = "";
	public String codeSearchTxtName = "";
    public String descSearchTxtName = "";
	
	public static PopupSearchHelper setDescription(String pageName){
		PopupSearchHelper b = new PopupSearchHelper();
		 /** Criteria Name **/
	    if("StoreCodeBME".equalsIgnoreCase(pageName)){
	    	b.setTitleSearch("ร้านค้า");
	    	b.setCodeSearchTxtName("รหัสร้านค้า");
	    	b.setDescSearchTxtName("ชื่อร้านค้า");
	    }else if("FIND_GroupCode_IN_StyleMappingLotus".equalsIgnoreCase(pageName)){
	    	b.setTitleSearch("Group In Style Mapping");
	    	b.setCodeSearchTxtName("Group Code");
	    	b.setDescSearchTxtName("");
	    }else if("FIND_StyleNo_IN_StyleMappingLotus".equalsIgnoreCase(pageName)){
	    	b.setTitleSearch("Article In Style Mapping");
	    	b.setCodeSearchTxtName("Article");
	    	b.setDescSearchTxtName("");
	    }else if("FIND_PensItem_IN_StyleMappingLotus".equalsIgnoreCase(pageName)){
	    	b.setTitleSearch("PensItem In Style Mapping");
	    	b.setCodeSearchTxtName("Pens Item");
	    	b.setDescSearchTxtName("");
	    }else if("FIND_GroupCode".equalsIgnoreCase(pageName)){
	    	b.setTitleSearch("Group Code(MST)");
	    	b.setCodeSearchTxtName("Group Code");;
	    	b.setDescSearchTxtName("");
	    }else if("FIND_PensItem".equalsIgnoreCase(pageName)){
	    	b.setTitleSearch("Pens Item(MST)");;
	    	b.setCodeSearchTxtName("Pens Item");
	    	b.setDescSearchTxtName("");
	    }else if("FIND_Style".equalsIgnoreCase(pageName)){
	    	b.setTitleSearch("Style(MST)");
	    	b.setCodeSearchTxtName("Style");
	    	b.setDescSearchTxtName("");
	    }
	    return b;
	}

	public String getTitleSearch() {
		return titleSearch;
	}

	public void setTitleSearch(String titleSearch) {
		this.titleSearch = titleSearch;
	}

	public String getCodeSearchTxtName() {
		return codeSearchTxtName;
	}

	public void setCodeSearchTxtName(String codeSearchTxtName) {
		this.codeSearchTxtName = codeSearchTxtName;
	}

	public String getDescSearchTxtName() {
		return descSearchTxtName;
	}

	public void setDescSearchTxtName(String descSearchTxtName) {
		this.descSearchTxtName = descSearchTxtName;
	}
	
	
	
}
