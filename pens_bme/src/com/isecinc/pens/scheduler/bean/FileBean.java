package com.isecinc.pens.scheduler.bean;

import java.io.Serializable;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class FileBean implements Serializable{
 private String noOfRecord;
 private String sizeOfFile;
 private StringBuffer data;
 private String folderName1;
 private String folderName2;
 private String folderName3;
 private String fileName;
 private String path ;
 private String pathFull;
 HSSFWorkbook bookExcel;

 

public HSSFWorkbook getBookExcel() {
	return bookExcel;
}
public void setBookExcel(HSSFWorkbook bookExcel) {
	this.bookExcel = bookExcel;
}
public String getFolderName1() {
	return folderName1;
}
public void setFolderName1(String folderName1) {
	this.folderName1 = folderName1;
}
public String getFolderName2() {
	return folderName2;
}
public void setFolderName2(String folderName2) {
	this.folderName2 = folderName2;
}
public String getFolderName3() {
	return folderName3;
}
public void setFolderName3(String folderName3) {
	this.folderName3 = folderName3;
}
public String getPathFull() {
	return pathFull;
}
public void setPathFull(String pathFull) {
	this.pathFull = pathFull;
}
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
}
public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}
public String getNoOfRecord() {
	return noOfRecord;
}
public void setNoOfRecord(String noOfRecord) {
	this.noOfRecord = noOfRecord;
}
public String getSizeOfFile() {
	return sizeOfFile;
}
public void setSizeOfFile(String sizeOfFile) {
	this.sizeOfFile = sizeOfFile;
}
public StringBuffer getData() {
	return data;
}
public void setData(StringBuffer data) {
	this.data = data;
}
 
 
}
