package com.isecinc.pens.web.imports;

import java.util.List;

import org.apache.struts.upload.FormFile;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.OnhandSummary;

/**
 * Trip Form
 * 
 * @author Witty.B
 * @version $Id: TripForm.java,v 1.0 19/10/2010 00:00:00 Witty.B Exp $
 * 
 */

public class ImportForm extends I_Form {

	private static final long serialVersionUID = 9066506758859129582L;

	private ImportCriteria criteria = new ImportCriteria();

	private boolean imported;
	
	private List<OnhandSummary> results;
    
	private String[] chkIndex;
   
	private FormFile dataFile;
	
	private OnhandSummary summary;
	
	private String pageTypeImport;
	
	private List<ImportSummary> summarySuccessList;
	private List<ImportSummary> summaryErrorList;
	
	private int totalSize;
	private int summaryLotusSuccessSize;
	private int summaryLotusErrorSize;
	
	private int summaryKingSuccessSize;
	private int summaryKingErrorSize;
	
	private int summaryBigCSuccessSize;
	private int summaryBigCErrorSize;
	
	private int summaryPhyListErrorSize;
	private int summaryPhyListSuccessSize;
	
	private int summaryWacoalListErrorSize;
	private int summaryWacoalListSuccessSize;
	
	private int summaryReturnWacoalListErrorSize;
	private int summaryReturnWacoalListSuccessSize;
	
	private int shoppingListErrorSize;
	private int shoppingListSuccessSize;
	
	private String countDate;
	private String custCode;
	
	private String storeType;
	private String storeCodeTemp;
	private String storeCode;
	private String storeName;
	private String importDate;
	private String boxNo;
	
	
	
	public int getShoppingListErrorSize() {
		return shoppingListErrorSize;
	}

	public void setShoppingListErrorSize(int shoppingListErrorSize) {
		this.shoppingListErrorSize = shoppingListErrorSize;
	}

	public int getShoppingListSuccessSize() {
		return shoppingListSuccessSize;
	}

	public void setShoppingListSuccessSize(int shoppingListSuccessSize) {
		this.shoppingListSuccessSize = shoppingListSuccessSize;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public int getSummaryKingSuccessSize() {
		return summaryKingSuccessSize;
	}

	public void setSummaryKingSuccessSize(int summaryKingSuccessSize) {
		this.summaryKingSuccessSize = summaryKingSuccessSize;
	}

	public int getSummaryKingErrorSize() {
		return summaryKingErrorSize;
	}

	public void setSummaryKingErrorSize(int summaryKingErrorSize) {
		this.summaryKingErrorSize = summaryKingErrorSize;
	}

	public String getStoreCodeTemp() {
		return storeCodeTemp;
	}

	public void setStoreCodeTemp(String storeCodeTemp) {
		this.storeCodeTemp = storeCodeTemp;
	}

	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getImportDate() {
		return importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public int getSummaryWacoalListErrorSize() {
		return summaryWacoalListErrorSize;
	}

	public void setSummaryWacoalListErrorSize(int summaryWacoalListErrorSize) {
		this.summaryWacoalListErrorSize = summaryWacoalListErrorSize;
	}

	public int getSummaryWacoalListSuccessSize() {
		return summaryWacoalListSuccessSize;
	}

	public void setSummaryWacoalListSuccessSize(int summaryWacoalListSuccessSize) {
		this.summaryWacoalListSuccessSize = summaryWacoalListSuccessSize;
	}

	public boolean isImported() {
		return imported;
	}

	public void setImported(boolean imported) {
		this.imported = imported;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getSummaryPhyListErrorSize() {
		return summaryPhyListErrorSize;
	}

	public int getSummaryPhyListSuccessSize() {
		return summaryPhyListSuccessSize;
	}

	public List<ImportSummary> getSummarySuccessList() {
		return summarySuccessList;
	}

	public void setSummarySuccessList(List<ImportSummary> summarySuccessList) {
		this.summarySuccessList = summarySuccessList;
	}

	public List<ImportSummary> getSummaryErrorList() {
		return summaryErrorList;
	}

	public void setSummaryErrorList(List<ImportSummary> summaryErrorList) {
		this.summaryErrorList = summaryErrorList;
	}
    
	
	public int getSummaryLotusSuccessSize() {
		return summaryLotusSuccessSize;
	}

	public void setSummaryLotusSuccessSize(int summaryLotusSuccessSize) {
		this.summaryLotusSuccessSize = summaryLotusSuccessSize;
	}

	public int getSummaryLotusErrorSize() {
		return summaryLotusErrorSize;
	}

	public void setSummaryLotusErrorSize(int summaryLotusErrorSize) {
		this.summaryLotusErrorSize = summaryLotusErrorSize;
	}

	public int getSummaryBigCSuccessSize() {
		return summaryBigCSuccessSize;
	}

	public void setSummaryBigCSuccessSize(int summaryBigCSuccessSize) {
		this.summaryBigCSuccessSize = summaryBigCSuccessSize;
	}

	public int getSummaryBigCErrorSize() {
		return summaryBigCErrorSize;
	}

	public void setSummaryBigCErrorSize(int summaryBigCErrorSize) {
		this.summaryBigCErrorSize = summaryBigCErrorSize;
	}

	public int getSummaryReturnWacoalListErrorSize() {
		return summaryReturnWacoalListErrorSize;
	}

	public void setSummaryReturnWacoalListErrorSize(
			int summaryReturnWacoalListErrorSize) {
		this.summaryReturnWacoalListErrorSize = summaryReturnWacoalListErrorSize;
	}

	public int getSummaryReturnWacoalListSuccessSize() {
		return summaryReturnWacoalListSuccessSize;
	}

	public void setSummaryReturnWacoalListSuccessSize(
			int summaryReturnWacoalListSuccessSize) {
		this.summaryReturnWacoalListSuccessSize = summaryReturnWacoalListSuccessSize;
	}

	public void setSummaryPhyListSuccessSize(int summaryPhyListSuccessSize) {
		this.summaryPhyListSuccessSize = summaryPhyListSuccessSize;
	}

	public void setSummaryPhyListErrorSize(int summaryPhyListErrorSize) {
		this.summaryPhyListErrorSize = summaryPhyListErrorSize;
	}

	public String getCountDate() {
		return countDate;
	}

	public void setCountDate(String countDate) {
		this.countDate = countDate;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}


	public String getPageTypeImport() {
		return pageTypeImport;
	}

	public void setPageTypeImport(String pageTypeImport) {
		this.pageTypeImport = pageTypeImport;
	}

	public List<OnhandSummary> getResults() {
		return results;
	}

	public void setResults(List<OnhandSummary> results) {
		this.results = results;
	}

	public OnhandSummary getSummary() {
		return summary;
	}

	public void setSummary(OnhandSummary summary) {
		this.summary = summary;
	}

	public FormFile getDataFile() {
		return dataFile;
	}

	public void setDataFile(FormFile dataFile) {
		this.dataFile = dataFile;
	}

	public String[] getChkIndex() {
		return chkIndex;
	}

	public void setChkIndex(String[] chkIndex) {
		this.chkIndex = chkIndex;
	}

	public ImportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ImportCriteria criteria) {
		this.criteria = criteria;
	}

	
	

}
