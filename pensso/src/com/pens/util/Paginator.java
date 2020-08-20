/**
 * Project Name  : PMR
 * 
 * Package Name  : util
 * Environment   : Java 5.0
 * Version       : 1.0
 * Creation Date : 23/11/2553
 * Implement By  : Mr.Danai Khankham (noomcomputer@hotmail.com)
 */

package com.pens.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;


public class Paginator implements Serializable {
	
	private static final long serialVersionUID = -2374976114577456201L;
	private static Logger logger = Logger.getLogger(Paginator.class);
	
	private final Class clz;
	private final String pageMethod;
	private final String pageRowCountMethod;
	private I_PO critiriaDto;
	private final int pageSize;

//	private int windowSize = SystemConfigService.getSystemConfig("CACHE_PAGE_SIZE").trim().equals("") ? 
//								Constants.CACHE_PAGE_SIZE : Integer.parseInt(SystemConfigService.getSystemConfig("CACHE_PAGE_SIZE"));
	private int windowSize = Constants.CACHE_PAGE_SIZE;

	public Paginator(Class clz, String pageMethod, String pageRowCountMethod, I_PO critiriaDto) {
		super();
		this.clz = clz;
		this.pageMethod = pageMethod;
		this.pageRowCountMethod = pageRowCountMethod;
		this.critiriaDto = critiriaDto;
//		this.pageSize = SystemConfigService.getSystemConfig("LINE_PER_PAGE").trim().equals("") ? 
//							Constants.LINE_PER_PAGE : Integer.parseInt(SystemConfigService.getSystemConfig("LINE_PER_PAGE"));
		this.pageSize = Constants.LINE_PER_PAGE;
		init();
	}

	public Paginator(Class clz, String pageMethod, String pageRowCountMethod, I_PO critiriaDto, int pageSize) {
		super();
		this.clz = clz;
		this.pageMethod = pageMethod;
		this.pageRowCountMethod = pageRowCountMethod;
		this.critiriaDto = critiriaDto;
		this.pageSize = pageSize;
		init();
	}

	private int currentPage;
	private int lastPage;
	private int totalRows;
	private int totalPage;
	private int actualCurrentPage;
	
	private int pageIndicator;

	private boolean hasNext;
	private boolean hasPrevious;
	private boolean hasFirst;
	private boolean hasLast;
	private boolean disableBox;
	
	private List<Object> page;
	private List<Object> allPage;
	

	private void init(){
		fetchDataOfWindow(1,true);
		first();
	}
	public void gotoPage(int pageNumber) {
		page = null;
		if(pageNumber>totalPage){
			pageNumber = totalPage;
		}
		this.currentPage = pageNumber;
		actualCurrentPage = ((pageNumber-1)%windowSize);
		
		/*
		logger.debug(" pageIndicator : "+ pageIndicator);
		logger.debug(" Current Page : "+ this.currentPage);
		logger.debug(" ActualCurrentPage Page : "+ this.actualCurrentPage);
		*/
		
		
		if(currentPage< ((pageIndicator*windowSize)-windowSize+1) || currentPage>(pageIndicator*windowSize)){
			int skipPage = (int) Math.ceil((double)currentPage/windowSize);
			logger.info("currentPage/windowSize :"+((double)currentPage/windowSize));
			fetchDataOfWindow(skipPage, false);
			logger.info("OUT OF WINDOW");
		}
		
		if (currentPage < 0) {
			currentPage = 0;
		}
		try {
			repaginate();
		} catch (ArrayIndexOutOfBoundsException e) {
			hasNext = false;
			hasLast = false;
		}
		if (totalPage == 0) {
			currentPage = 0;
			disableBox = true;
			hasPrevious = false;
			hasFirst = false;
			hasNext = false;
			hasLast = false;
		} else if (currentPage == 1 && totalPage == 1) {
			disableBox = true;
			hasPrevious = false;
			hasFirst = false;
			hasNext = false;
			hasLast = false;
		} else if (currentPage == 1 && totalPage > 1) {
			disableBox = false;
			hasPrevious = false;
			hasFirst = false;
			hasNext = true;
			hasLast = true;
		} else if (currentPage > 1 && totalPage > 1 && currentPage != totalPage) {
			disableBox = false;
			hasPrevious = true;
			hasFirst = true;
			hasNext = true;
			hasLast = true;
		} else {
			disableBox = false;
			hasPrevious = true;
			hasFirst = true;
			hasNext = false;
			hasLast = false;
		}
	}
	
	private void repaginate(){
		int fromIndex = actualCurrentPage * pageSize;
		int toIndex   = actualCurrentPage * pageSize + pageSize;
//		logger.info("FromIndex :"+fromIndex);
//		logger.info("ToIndex :"+toIndex);
		if(this.allPage.size()>this.pageSize){
			if(toIndex>= this.allPage.size()){
				this.page = this.allPage.subList(fromIndex, this.allPage.size());
			}else{
				this.page = this.allPage.subList(fromIndex, toIndex);
			}
		}else{
			this.page = this.allPage;
		}
	}
	
	private void fetchDataOfWindow(int goPage, boolean countFlag){
		int skip = (goPage-1)*windowSize*pageSize;
		int max = windowSize*pageSize;
		
//		logger.info("skip["+skip+"], max["+max+"], recountFlag["+countFlag+"]");

		if(countFlag){
			Method pageCountInvokeMethod;
			Class[] paramClasses = {critiriaDto.getClass()};
			Object[] args = {critiriaDto};
			try {
				pageCountInvokeMethod = clz.getMethod(pageRowCountMethod, paramClasses);
				Integer totalRows = (Integer) pageCountInvokeMethod.invoke(clz,args);
				if(totalRows!=null){
					this.totalRows = totalRows.intValue();
					this.totalPage =   (int) Math.ceil((double)this.totalRows/pageSize);
				}else{
					this.totalRows = 0;
					this.totalPage = 0;
				}
				logger.info("totalRows["+this.totalRows+"], totalPage["+this.totalPage+"]");
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		int diff = totalRows-skip;
		if(diff<max){
			max = diff;
			//logger.debug("MAX > DIFF :"+diff);
		}
		Method pageInvokeMethod;
		Class[] paramClasses = {critiriaDto.getClass(), int.class, int.class};
		Object[] args = {critiriaDto, skip, max};
		try {
			pageInvokeMethod = clz.getMethod(pageMethod,paramClasses);
			this.allPage = (List<Object>) pageInvokeMethod.invoke(clz,args);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		pageIndicator = goPage;
//		logger.debug(" pageIndicator : "+ pageIndicator);
//		logger.debug(" Current Page : "+ this.currentPage);
//		logger.debug(" ActualCurrentPage Page : "+ this.actualCurrentPage);
	}
	
	public void first(){
		gotoPage(1);
	}
	
	public void previous(){
		int page = currentPage-1;
		gotoPage(page);
	}
	public void next(){
		int page = currentPage+1;
		gotoPage(page);
	}
	public void last(){
		int page = totalPage;
		gotoPage(page);
	}
	public void refresh(){
		if(totalRows>0){
			int skipPage = (int) Math.ceil((double)currentPage/windowSize);
			fetchDataOfWindow(skipPage,true);
			gotoPage(currentPage);
		}else{
			init();
		}
	}
	public String getTxtDorA() {
		return this.critiriaDto.getTxtDorA();
	}


	public void setTxtDorA(String txtDorA) {
		this.critiriaDto.setTxtDorA(txtDorA);
	}


	public String getOrderBy() {
		return this.critiriaDto.getOrderBy();
	}


	public void setOrderBy(String orderBy) {
		this.critiriaDto.setOrderBy(orderBy);
	}


	public List<Object> getPage() {
		return this.page;
	}

	public void setPage(List<Object> page) {
		this.page = page;
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getLastPage() {
		return this.lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public boolean isHasNext() {
		return this.hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public boolean isHasPrevious() {
		return this.hasPrevious;
	}

	public void setHasPrevious(boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	public boolean isHasFirst() {
		return this.hasFirst;
	}

	public void setHasFirst(boolean hasFirst) {
		this.hasFirst = hasFirst;
	}

	public boolean isHasLast() {
		return this.hasLast;
	}

	public void setHasLast(boolean hasLast) {
		this.hasLast = hasLast;
	}

	public boolean isDisableBox() {
		return this.disableBox;
	}

	public void setDisableBox(boolean disableBox) {
		this.disableBox = disableBox;
	}

	public List<Object> getAllPage() {
		return this.allPage;
	}

	public void setAllPage(List<Object> allPage) {
		this.allPage = allPage;
	}

	public int getTotalRows() {
		return this.totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getTotalPage() {
		return this.totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	/**
		 * Purpose    : Use to search userProfile
		 * @param     : Value argument's userProfile for search condition
		 *            : - userId is userId
		 *            : - userName is userName
		 *            : - dateReg is user's date of register
		 * @return    : void
	     *            : Return list of userProfile
		 * @throws Exception 
		 * @exception : SqlException exception's database statement
		 * @throws    : Throws SqlException
		 */
	public void sort() {
		fetchDataOfWindow(1,false);
		first();
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public I_PO getCritiriaDto() {
		return this.critiriaDto;
	}
	
}
