var db_decimal_scale = 5;

// Create ordered Item
function item(productId){
	this.productId = productId;
	this.productName = "";
	this.productCode = "";
	this.uom1 = "";
	this.uom2 = "";
	this.price1 = "";
	this.price2 = "";
	this.qty1 = "";
	this.qty2 = "";
	this.lineNetAmt = calculatePrice(price1,price2,qty1,qty2);
}

function item(productId,productName,productCode,uom1,uom2,price1,price2,qty1,qty2){
	this.productId = productId;
	this.productName = productName;
	this.productCode = productCode;
	this.uom1 = uom1;
	this.uom2 = uom2;
	this.price1 = price1;
	this.price2 = price2;
	this.qty1 = qty1;
	this.qty2 = qty2;
	this.lineNetAmt = calculatePrice(price1,price2,qty1,qty2);
}

function calculatePrice(price1,price2,qty1,qty2){
	if(qty1 == null && qty2==null)
		return 0;
	
	if(qty1==null)
		qty1 = 0;
	
	if(qty2==null)
		qty2 = 0;
	
	var totalAmt = (price1*qty1) + (price2 * qty2);
	totalAmt = totalAmt.toFixed(db_decimal_scale) ;
}

function shoppingcart(){
	shoppingcart.items = new Array();
}

shoppingcart.prototype.addItem = function (item) {
	shoppingcart.items.push(item);
};

shoppingcart.prototype.removeItem = function (productId) {
	var _items = new Array();
	var n = shoppingcart.items.length;
	
	for(var i = 0;i<=n ; i++ ){
		var item = shoppingcart.items.pop();
		if(item.productId != productId)
			_items.push(item);
	}
	
	shoppingcart.items = _items; 
};

shoppingcart.prototype.getItem = function (productId) {
	var _items = new Array();
	var retItem  = null;
	var n = shoppingcart.items.length;
	
	for(var i = 0;i<=n ; i++ ){
		var item = shoppingcart.items.pop();
		if(item.productId != productId)
			_items.push(item);
		else
			retItem = item;
	}
	
	shoppingcart.items = _items;
	
	return retItem;
};

shoppingcart.prototype.getNoOfItemInBasket = function () {
	return shoppingcart.items.length;
};

shoppingcart.prototype.sortItemById = function () {
	shoppingcart.items.sort(sortById);
};

function sortById(item1,item2){
	return item1.productId - item2.productId;
}

var basket1 = new shoppingcart();
basket1.addItem(new item(500));
basket1.addItem(new item(600));
basket1.addItem(new item(400));
basket1.addItem(new item(200));
basket1.addItem(new item(300));
alert(basket1.getNoOfItemInBasket());
basket1.removeItem(500);
alert(basket1.getNoOfItemInBasket());
var item = basket1.getItem(600);
alert(basket1.getNoOfItemInBasket());
item.productName = 'test';
basket1.addItem(item);
alert(basket1.getNoOfItemInBasket());
alert(item.productName);
alert('get item no 1 '+basket1.items[0].productId);
basket1.sortItemById();
alert('get item no 1 after sort '+basket1.items[0].productId);
