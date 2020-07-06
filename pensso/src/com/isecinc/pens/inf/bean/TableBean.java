package com.isecinc.pens.inf.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author WITTY
 *
 */
public class TableBean implements Serializable{

	private static final long serialVersionUID = -8266909406706427030L;
	
	private String tableName;
    private String fileNameMapping;//name Mapping
    private Map subInvMap;//name Mapping Case OnHand more one file By SubInv
    private Map userCodeMap;
    
    private String fileFtpNameFull;//get Name From Ftp Server
    private String transactionType;
    private String authen;
    private String childTable;
    private String actionDB;
    private String preFunction;
    private String postFunction;
    private String checkDupFile;
    
    private String columnTableAll;
    private List<ColumnBean> columnBeanList;
	private List<ColumnBean> columnBeanOrderUpdateList;
	private List<ColumnBean> columnBeanDeleteList;
	private List<ColumnBean> columnBeanOrderUpdateCSList;
	private List<ColumnBean> columnKeyList;
	
	private String whereClause;
	private String prepareSqlIns;
	private String prepareSqlUpd;
	private String prepareSqlSelect;
	private String prepareSqlDelete;
	private String prepareSqlUpdCS;

	
	private String exportPath;
	private String source;
	private String destination;
	private String fileNameLastImport;
	private List<FTPFileBean> dataLineList;
	private String dateLineStr;
	
	private int importSourceCount;
	private int importDestCount;
	private int exportCount;
	private int exportCountTransaction;
	
	private StringBuffer dataStrExport;
	private List<TableBean> fileExportList;
	//optional
	private List<FTPFileBean> dataStrBuffList;
	
	//** For Update Case Export Flag*/
	private List<String> sqlUpdateExportFlagList;
	
	
	/** List File to Upload **/
	private List<ImageFileBean> imageFileList;
    
	public List<ImageFileBean> getImageFileList() {
		return imageFileList;
	}
	public void setImageFileList(List<ImageFileBean> imageFileList) {
		this.imageFileList = imageFileList;
	}
	
	public List<ColumnBean> getColumnKeyList() {
		return columnKeyList;
	}
	public void setColumnKeyList(List<ColumnBean> columnKeyList) {
		this.columnKeyList = columnKeyList;
	}
	public String getCheckDupFile() {
		return checkDupFile;
	}
	public void setCheckDupFile(String checkDupFile) {
		this.checkDupFile = checkDupFile;
	}
	public String getPreFunction() {
		return preFunction;
	}
	public void setPreFunction(String preFunction) {
		this.preFunction = preFunction;
	}
	public String getPostFunction() {
		return postFunction;
	}
	public void setPostFunction(String postFunction) {
		this.postFunction = postFunction;
	}
	public List<ColumnBean> getColumnBeanOrderUpdateCSList() {
		return columnBeanOrderUpdateCSList;
	}
	public void setColumnBeanOrderUpdateCSList(List<ColumnBean> columnBeanOrderUpdateCSList) {
		this.columnBeanOrderUpdateCSList = columnBeanOrderUpdateCSList;
	}
	public String getPrepareSqlUpdCS() {
		return prepareSqlUpdCS;
	}
	public void setPrepareSqlUpdCS(String prepareSqlUpdCS) {
		this.prepareSqlUpdCS = prepareSqlUpdCS;
	}
	public List<String> getSqlUpdateExportFlagList() {
		return sqlUpdateExportFlagList;
	}
	public void setSqlUpdateExportFlagList(List<String> sqlUpdateExportFlagList) {
		this.sqlUpdateExportFlagList = sqlUpdateExportFlagList;
	}
	public List<TableBean> getFileExportList() {
		return fileExportList;
	}
	public void setFileExportList(List<TableBean> fileExportList) {
		this.fileExportList = fileExportList;
	}
	public String getColumnTableAll() {
		return columnTableAll;
	}
	public void setColumnTableAll(String columnTableAll) {
		this.columnTableAll = columnTableAll;
	}
	public String getDateLineStr() {
		return dateLineStr;
	}
	public void setDateLineStr(String dateLineStr) {
		this.dateLineStr = dateLineStr;
	}
	public int getExportCountTransaction() {
		return exportCountTransaction;
	}
	public void setExportCountTransaction(int exportCountTransaction) {
		this.exportCountTransaction = exportCountTransaction;
	}
	
	public String getFileNameMapping() {
		return fileNameMapping;
	}
	public void setFileNameMapping(String fileNameMapping) {
		this.fileNameMapping = fileNameMapping;
	}
	public StringBuffer getDataStrExport() {
		return dataStrExport;
	}
	public void setDataStrExport(StringBuffer dataStrExport) {
		this.dataStrExport = dataStrExport;
	}
	public List<FTPFileBean> getDataStrBuffList() {
		return dataStrBuffList;
	}
	public void setDataStrBuffList(List<FTPFileBean> dataStrBuffList) {
		this.dataStrBuffList = dataStrBuffList;
	}
	public String getActionDB() {
		return actionDB;
	}
	public void setActionDB(String actionDB) {
		this.actionDB = actionDB;
	}
	public String getChildTable() {
		return childTable;
	}
	public void setChildTable(String childTable) {
		this.childTable = childTable;
	}
	public String getPrepareSqlDelete() {
		return prepareSqlDelete;
	}
	public void setPrepareSqlDelete(String prepareSqlDelete) {
		this.prepareSqlDelete = prepareSqlDelete;
	}
	public List<ColumnBean> getColumnBeanDeleteList() {
		return columnBeanDeleteList;
	}
	public void setColumnBeanDeleteList(List<ColumnBean> columnBeanDeleteList) {
		this.columnBeanDeleteList = columnBeanDeleteList;
	}
	
	public Map getSubInvMap() {
		return subInvMap;
	}
	public void setSubInvMap(Map subInvMap) {
		this.subInvMap = subInvMap;
	}
	public Map getUserCodeMap() {
		return userCodeMap;
	}
	public void setUserCodeMap(Map userCodeMap) {
		this.userCodeMap = userCodeMap;
	}
	public String getAuthen() {
		return authen;
	}
	public void setAuthen(String authen) {
		this.authen = authen;
	}
	public int getImportSourceCount() {
		return importSourceCount;
	}
	public void setImportSourceCount(int importSourceCount) {
		this.importSourceCount = importSourceCount;
	}
	public int getImportDestCount() {
		return importDestCount;
	}
	public void setImportDestCount(int importDestCount) {
		this.importDestCount = importDestCount;
	}
	public int getExportCount() {
		return exportCount;
	}
	public void setExportCount(int exportCount) {
		this.exportCount = exportCount;
	}
	
	public List<FTPFileBean> getDataLineList() {
		return dataLineList;
	}
	public void setDataLineList(List<FTPFileBean> dataLineList) {
		this.dataLineList = dataLineList;
	}
	public String getFileNameLastImport() {
		return fileNameLastImport;
	}
	public void setFileNameLastImport(String fileNameLastImport) {
		this.fileNameLastImport = fileNameLastImport;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getExportPath() {
		return exportPath;
	}
	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}
	
	public List<ColumnBean> getColumnBeanList() {
		return columnBeanList;
	}
	public void setColumnBeanList(List<ColumnBean> columnBeanList) {
		this.columnBeanList = columnBeanList;
	}

	public String getFileFtpNameFull() {
		return fileFtpNameFull;
	}
	public void setFileFtpNameFull(String fileFtpNameFull) {
		this.fileFtpNameFull = fileFtpNameFull;
	}
	public String getFileFtpName() {
		return fileNameMapping;
	}
	public void setFileFtpName(String fileFtpName) {
		this.fileNameMapping = fileFtpName;
	}
	public String getWhereClause() {
		return whereClause;
	}
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
    
	public String getPrepareSqlSelect() {
		return prepareSqlSelect;
	}
	public void setPrepareSqlSelect(String prepareSqlSelect) {
		this.prepareSqlSelect = prepareSqlSelect;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<ColumnBean> getColumnBeanOrderUpdateList() {
		return columnBeanOrderUpdateList;
	}
	public void setColumnBeanOrderUpdateList(List<ColumnBean> columnBeanOrderUpdateList) {
		this.columnBeanOrderUpdateList = columnBeanOrderUpdateList;
	}
	public String getPrepareSqlIns() {
		return prepareSqlIns;
	}
	public void setPrepareSqlIns(String prepareSqlIns) {
		this.prepareSqlIns = prepareSqlIns;
	}
	public String getPrepareSqlUpd() {
		return prepareSqlUpd;
	}
	public void setPrepareSqlUpd(String prepareSqlUpd) {
		this.prepareSqlUpd = prepareSqlUpd;
	}
	
	

	
}
