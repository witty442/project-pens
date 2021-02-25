
package com.isecinc.pens.scheduler.bean;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Pakonkit Jaikla
 *
 * 
 */
public class TaskConditionDTO {
    private BigDecimal no;
    private String programName;
    private Timestamp createDateFrom;
    private Timestamp createDateTo;
    private String branchCode;
    private Date asOfDateFrom;
    private Date asOfDateTo;
    private String[] Status;
    private String[] type;
    private boolean createDateFromWithTime;
    private boolean createDateToWithTime;
    private String returnCode;
    private int totalRow;
    private int limitRow;
    private String entityId;
    private String product;
    private Date batchDateFrom;
    private Date batchDateTo;
    private String jobName;
    
    public String programId;
    
    
    public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public Date getBatchDateFrom() {
		return batchDateFrom;
	}
	public void setBatchDateFrom(Date batchDateFrom) {
		this.batchDateFrom = batchDateFrom;
	}
	public Date getBatchDateTo() {
		return batchDateTo;
	}
	public void setBatchDateTo(Date batchDateTo) {
		this.batchDateTo = batchDateTo;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String[] getType() {
		return type;
	}
	public void setType(String[] type) {
		this.type = type;
	}
	public int getLimitRow() {
		return limitRow;
	}
	public void setLimitRow(int limitRow) {
		this.limitRow = limitRow;
	}
	public int getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
    public boolean isCreateDateFromWithTime() {
        return createDateFromWithTime;
    }
    public void setCreateDateFromWithTime(boolean createDateFromWithTime) {
        this.createDateFromWithTime = createDateFromWithTime;
    }
    public boolean isCreateDateToWithTime() {
        return createDateToWithTime;
    }
    public void setCreateDateToWithTime(boolean createDateToWithTime) {
        this.createDateToWithTime = createDateToWithTime;
    }
    /**
     * @return Returns the asOfDateFrom.
     */
    public Date getAsOfDateFrom() {
        return asOfDateFrom;
    }
    /**
     * @param asOfDateFrom The asOfDateFrom to set.
     */
    public void setAsOfDateFrom(Date asOfDateFrom) {
        this.asOfDateFrom = asOfDateFrom;
    }
    /**
     * @return Returns the asOfDateTo.
     */
    public Date getAsOfDateTo() {
        return asOfDateTo;
    }
    /**
     * @param asOfDateTo The asOfDateTo to set.
     */
    public void setAsOfDateTo(Date asOfDateTo) {
        this.asOfDateTo = asOfDateTo;
    }
    /**
     * @return Returns the branchCode.
     */
    public String getBranchCode() {
        return branchCode;
    }
    /**
     * @param branchCode The branchCode to set.
     */
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public Timestamp getCreateDateFrom() {
        return createDateFrom;
    }
    public void setCreateDateFrom(Timestamp createDateFrom) {
        this.createDateFrom = createDateFrom;
    }
    public Timestamp getCreateDateTo() {
        return createDateTo;
    }
    public void setCreateDateTo(Timestamp createDateTo) {
        this.createDateTo = createDateTo;
    }
   
    
   
    /**
     * @return Returns the status.
     */
    public String[] getStatus() {
        return Status;
    }
    /**
     * @param status The status to set.
     */
    public void setStatus(String[] status) {
        Status = status;
    }
	public BigDecimal getNo() {
		return no;
	}
	public void setNo(BigDecimal no) {
		this.no = no;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
   
}
