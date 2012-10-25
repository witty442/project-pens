package manual.editMoveOrder;

public class MoveOrderLine {

private String requestNumber;
private int lineNumber;
private String inventoryItemId;

private double primary_quantity;
private double ctn_qty;
private double pcs_qty;


public double getCtn_qty() {
	return ctn_qty;
}

public void setCtn_qty(double ctn_qty) {
	this.ctn_qty = ctn_qty;
}

public double getPcs_qty() {
	return pcs_qty;
}

public void setPcs_qty(double pcs_qty) {
	this.pcs_qty = pcs_qty;
}

public String getRequestNumber() {
	return requestNumber;
}

public void setRequestNumber(String requestNumber) {
	this.requestNumber = requestNumber;
}

public int getLineNumber() {
	return lineNumber;
}

public void setLineNumber(int lineNumber) {
	this.lineNumber = lineNumber;
}

public String getInventoryItemId() {
	return inventoryItemId;
}

public void setInventoryItemId(String inventoryItemId) {
	this.inventoryItemId = inventoryItemId;
}

public double getPrimary_quantity() {
	return primary_quantity;
}

public void setPrimary_quantity(double primary_quantity) {
	this.primary_quantity = primary_quantity;
}



}
