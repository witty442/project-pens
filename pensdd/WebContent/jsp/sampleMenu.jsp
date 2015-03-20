<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.bean.User"%>
<style type="text/css">
html{
	height:100%;
}
body{
	font-family: Trebuchet MS, Lucida Sans Unicode, Arial, sans-serif;
	font-size:0.8em;
	margin:0px;
	padding:0px;

	background-color:#E2EBED;
	height:100%;
	text-align:center;
}
.clear{
	clear:both;
}

#mainContainer{
	text-align:left;
	margin:0 auto;
	background-color: #FFF;
	border-left:1px solid #000;
	border-right:1px solid #000;
	height:100%;
}

#topBar{
}
#leftMenu{
	width:200px;
	padding-left:10px;
	padding-right:10px;
	float:left;
}
#mainContent{
	padding-right:10px;
	float:left;
}
/*
General rules
*/

/* Active menu item */
#dhtmlgoodies_slidedown_menu .dhtmlgoodies_activeItem {
	font-weight:bold;
	color: green;
}

#dhtmlgoodies_slidedown_menu li{
	list-style-type:none;
	position:relative;
}
#dhtmlgoodies_slidedown_menu ul{
	margin:0px;
	padding:0px;
	position:relative;

}

#dhtmlgoodies_slidedown_menu div{
	margin:0px;
	padding:0px;
}
/* 	Layout CSS */
#dhtmlgoodies_slidedown_menu{
	width:205px;
}

/* All A tags - i.e menu items. */
#dhtmlgoodies_slidedown_menu a{
	color: #000;
	text-decoration:none;
	display:block;
	clear:both;
	width:170px;
	padding-left:2px;
}

/* Active menu item */
#dhtmlgoodies_slidedown_menu .dhtmlgoodies_activeItem {
	font-weight:bold;
	color: green;
}

/*
A tags
*/
#dhtmlgoodies_slidedown_menu .slMenuItem_depth1{	/* Main menu items */
	margin-top:1px;
	border-bottom:1px solid #000;
	font-weight:bold;
}
#dhtmlgoodies_slidedown_menu .slMenuItem_depth2{	/* Sub menu items */
	margin-top:1px;
}
#dhtmlgoodies_slidedown_menu .slMenuItem_depth3{	/* Sub menu items */
	margin-top:1px;
	font-style:italic;
	color:blue;
}
#dhtmlgoodies_slidedown_menu .slMenuItem_depth4{	/* Sub menu items */
	margin-top:1px;
	color:red;
}
#dhtmlgoodies_slidedown_menu .slMenuItem_depth5{	/* Sub menu items */
	margin-top:1px;
}

/* UL tags, i.e group of menu utems.
It's important to add style to the UL if you're specifying margins. If not, assign the style directly
to the parent DIV, i.e.

#dhtmlgoodies_slidedown_menu .slideMenuDiv1

instead of

#dhtmlgoodies_slidedown_menu .slideMenuDiv1 ul
*/

#dhtmlgoodies_slidedown_menu .slideMenuDiv1 ul{
	padding:1px;
}
#dhtmlgoodies_slidedown_menu .slideMenuDiv2 ul{
	margin-left:5px;
	padding:1px;
}
#dhtmlgoodies_slidedown_menu .slideMenuDiv3 ul{
	margin-left:10px;
	padding:1px;
}
#dhtmlgoodies_slidedown_menu .slMenuItem_depth4 ul{
	margin-left:15px;
	padding:1px;
}

</style>
<script language="javascript">
var expandFirstItemAutomatically = false;	// Expand first menu item automatically ?
var initMenuIdToExpand = false;	// Id of menu item that should be initially expanded. the id is defined in the <li> tag.
var expandMenuItemByUrl = true;	// Menu will automatically expand by url - i.e. if the href of the menu item is in the current location, it will expand


var initialMenuItemAlwaysExpanded = true;	// NOT IMPLEMENTED YET

var dhtmlgoodies_slmenuObj;
var divToScroll = false;
var ulToScroll = false;
var divCounter = 1;
var otherDivsToScroll = new Array();
var divToHide = false;
var parentDivToHide = new Array();
var ulToHide = false;
var offsetOpera = 0;
if(navigator.userAgent.indexOf('Opera')>=0)offsetOpera=1;
var slideMenuHeightOfCurrentBox = 0;
var objectsToExpand = new Array();
var initExpandIndex = 0;
var alwaysExpanedItems = new Array();

var dg_activeItem = null;

function popMenusToShow()
{
	var obj = divToScroll;
	var endArray = new Array();
	while(obj && obj.tagName!='BODY'){
		if(obj.tagName=='DIV' && obj.id.indexOf('slideDiv')>=0){
			var objFound = -1;
			for(var no=0;no<otherDivsToScroll.length;no++){
				if(otherDivsToScroll[no]==obj){
					objFound = no;
				}
			}
			if(objFound>=0){
				otherDivsToScroll.splice(objFound,1);
			}
		}
		obj = obj.parentNode;
	}
}

function showSubMenu(e,inputObj)
{

	if(this && this.tagName)inputObj = this.parentNode;
	if(inputObj && inputObj.tagName=='LI'){
		divToScroll = inputObj.getElementsByTagName('DIV')[0];
		for(var no=0;no<otherDivsToScroll.length;no++){
			if(otherDivsToScroll[no]==divToScroll)return;
		}
	}
	hidingInProcess = false;
	if(otherDivsToScroll.length>0){
		if(divToScroll){
			if(otherDivsToScroll.length>0){
				popMenusToShow();
			}
			if(otherDivsToScroll.length>0){
				autoHideMenus();
				hidingInProcess = true;
			}
		}
	}
	if(divToScroll && !hidingInProcess){
		divToScroll.style.display='';
		otherDivsToScroll.length = 0;
		otherDivToScroll = divToScroll.parentNode;
		otherDivsToScroll.push(divToScroll);
		while(otherDivToScroll && otherDivToScroll.tagName!='BODY'){
			if(otherDivToScroll.tagName=='DIV' && otherDivToScroll.id.indexOf('slideDiv')>=0){
				otherDivsToScroll.push(otherDivToScroll);

			}
			otherDivToScroll = otherDivToScroll.parentNode;
		}
		ulToScroll = divToScroll.getElementsByTagName('UL')[0];
		if(divToScroll.style.height.replace('px','')/1<=1)scrollDownSub();
	}

	if(e || inputObj) {

		if(dg_activeItem) {
			dg_activeItem.className = dg_activeItem.className.replace('dhtmlgoodies_activeItem','');
		}
		var aTags = inputObj.getElementsByTagName('A');
		if(aTags.length>0) {
			aTags[0].className = aTags[0].className + ' dhtmlgoodies_activeItem';
			dg_activeItem = aTags[0];

		}

	}

	return false;

}



function autoHideMenus()
{
	if(otherDivsToScroll.length>0){
		divToHide = otherDivsToScroll[otherDivsToScroll.length-1];
		parentDivToHide.length=0;
		var obj = divToHide.parentNode.parentNode.parentNode;
		while(obj && obj.tagName=='DIV'){
			if(obj.id.indexOf('slideDiv')>=0)parentDivToHide.push(obj);
			obj = obj.parentNode.parentNode.parentNode;
		}
		var tmpHeight = (divToHide.style.height.replace('px','')/1 - slideMenuHeightOfCurrentBox);
		if(tmpHeight<0)tmpHeight=0;
		if(slideMenuHeightOfCurrentBox)divToHide.style.height = tmpHeight  + 'px';
		ulToHide = divToHide.getElementsByTagName('UL')[0];
		slideMenuHeightOfCurrentBox = ulToHide.offsetHeight;
		scrollUpMenu();
	}else{
		slideMenuHeightOfCurrentBox = 0;
		showSubMenu();
	}
}


function scrollUpMenu()
{

	var height = divToHide.offsetHeight;
	height-=15;
	if(height<0)height=0;
	divToHide.style.height = height + 'px';

	for(var no=0;no<parentDivToHide.length;no++){
		parentDivToHide[no].style.height = parentDivToHide[no].getElementsByTagName('UL')[0].offsetHeight + 'px';
	}
	if(height>0){
		setTimeout('scrollUpMenu()',5);
	}else{
		divToHide.style.display='none';
		otherDivsToScroll.length = otherDivsToScroll.length-1;
		autoHideMenus();
	}
}

function scrollDownSub()
{
	if(divToScroll){
		var height = divToScroll.offsetHeight/1;
		var offsetMove =Math.min(15,(ulToScroll.offsetHeight - height));
		height = height +offsetMove ;
		divToScroll.style.height = height + 'px';

		for(var no=1;no<otherDivsToScroll.length;no++){
			var tmpHeight = otherDivsToScroll[no].offsetHeight/1 + offsetMove;
			otherDivsToScroll[no].style.height = tmpHeight + 'px';
		}
		if(height<ulToScroll.offsetHeight)setTimeout('scrollDownSub()',5); else {
			divToScroll = false;
			ulToScroll = false;
			if(objectsToExpand.length>0 && initExpandIndex<(objectsToExpand.length-1)){
				initExpandIndex++;

				showSubMenu(false,objectsToExpand[initExpandIndex]);
			}
		}
	}
}

function initSubItems(inputObj,currentDepth)
{
	divCounter++;
	var div = document.createElement('DIV');	// Creating new div
	div.style.overflow = 'hidden';
	div.style.position = 'relative';
	div.style.display='none';
	div.style.height = '1px';
	div.id = 'slideDiv' + divCounter;
	div.className = 'slideMenuDiv' + currentDepth;
	inputObj.parentNode.appendChild(div);	// Appending DIV as child element of <LI> that is parent of input <UL>
	div.appendChild(inputObj);	// Appending <UL> to the div
	var menuItem = inputObj.getElementsByTagName('LI')[0];
	while(menuItem){
		if(menuItem.tagName=='LI'){
			var aTag = menuItem.getElementsByTagName('A')[0];
			aTag.className='slMenuItem_depth'+currentDepth;
			var subUl = menuItem.getElementsByTagName('UL');
			if(subUl.length>0){
				initSubItems(subUl[0],currentDepth+1);
			}
			aTag.onclick = showSubMenu;
		}
		menuItem = menuItem.nextSibling;
	}
}

function initSlideDownMenu()
{
	dhtmlgoodies_slmenuObj = document.getElementById('dhtmlgoodies_slidedown_menu');
	dhtmlgoodies_slmenuObj.style.visibility='visible';
	var mainUl = dhtmlgoodies_slmenuObj.getElementsByTagName('UL')[0];
	var mainMenuItem = mainUl.getElementsByTagName('LI')[0];
	mainItemCounter = 1;
	while(mainMenuItem){
		if(mainMenuItem.tagName=='LI'){
			var aTag = mainMenuItem.getElementsByTagName('A')[0];
			aTag.className='slMenuItem_depth1';
			var subUl = mainMenuItem.getElementsByTagName('UL');
			if(subUl.length>0){
				mainMenuItem.id = 'mainMenuItem' + mainItemCounter;
				initSubItems(subUl[0],2);
				aTag.onclick = showSubMenu;
				mainItemCounter++;
			}
		}
		mainMenuItem = mainMenuItem.nextSibling;
	}

	if(location.search.indexOf('mainMenuItemToSlide')>=0){
		var items = location.search.split('&');
		for(var no=0;no<items.length;no++){
			if(items[no].indexOf('mainMenuItemToSlide')>=0){
				values = items[no].split('=');
				showSubMenu(false,document.getElementById('mainMenuItem' + values[1]));
				initMenuIdToExpand = false;
			}
		}
	}else if(expandFirstItemAutomatically>0){
		if(document.getElementById('mainMenuItem' + expandFirstItemAutomatically)){
			showSubMenu(false,document.getElementById('mainMenuItem' + expandFirstItemAutomatically));
			initMenuIdToExpand = false;
		}
	}

	if(expandMenuItemByUrl)
	{
		var aTags = dhtmlgoodies_slmenuObj.getElementsByTagName('A');
		for(var no=0;no<aTags.length;no++){
			var hrefToCheckOn = aTags[no].href;
			if(location.href.indexOf(hrefToCheckOn)>=0 && hrefToCheckOn.indexOf('#')<hrefToCheckOn.length-1){
				initMenuIdToExpand = false;
				var obj = aTags[no].parentNode;
				while(obj && obj.id!='dhtmlgoodies_slidedown_menu'){
					if(obj.tagName=='LI'){
						var subUl = obj.getElementsByTagName('UL');
						if(initialMenuItemAlwaysExpanded)alwaysExpanedItems[obj.parentNode] = true;
						if(subUl.length>0){
							objectsToExpand.unshift(obj);
						}
					}
					obj = obj.parentNode;
				}
				showSubMenu(false,objectsToExpand[0]);
				break;
			}
		}
	}

	if(initMenuIdToExpand)
	{
		objectsToExpand = new Array();
		var obj = document.getElementById(initMenuIdToExpand)
		while(obj && obj.id!='dhtmlgoodies_slidedown_menu'){
			if(obj.tagName=='LI'){
				var subUl = obj.getElementsByTagName('UL');
				if(initialMenuItemAlwaysExpanded)alwaysExpanedItems[obj.parentNode] = true;
				if(subUl.length>0){
					objectsToExpand.unshift(obj);
				}
			}
			obj = obj.parentNode;
		}

		showSubMenu(false,objectsToExpand[0]);

	}

}
	window.onload = initSlideDownMenu;

	function changeURL(url)
	{
		alert(url);
		document.getElementById("mainframe").src=url;
	}
</script>
<div id="mainContainer">
	<div id="leftMenu">
		<!-- START OF MENU -->
		<div id="dhtmlgoodies_slidedown_menu">
			<ul>
				<li><a href="#">Menus</a>
					<ul>
						<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
						<li><a href="#" onclick="changeURL('customer/customerSearch.jsp');">Customer</a></li>
						<%} %>
						<%if(((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
						<li><a href="#" onclick="changeURL('member/memberSearch.jsp');">Member</a></li>
						<%} %>
						<li><a href="#" onclick="changeURL('pricePromotion/pricePromotionSearch.jsp');">Pricelist &amp; Promotion</a></li>
						<li><a href="#" onclick="changeURL('product/productSearch.jsp');">Product</a></li>
						<li><a href="#" onclick="changeURL('salesTarget/salesTargetSearch.jsp');">Sales Target</a></li>
					</ul>
				</li>
				<li><a href="#">Transaction</a>
					<ul>
						<%if(((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.TT)){ %>
						<li><a href="#" onclick="changeURL('customer/customerVisitSearch.jsp');">Customer Visit</a></li>
						<%} %>
						<li><a href="#" onclick="changeURL('sales/salesOrderSearch.jsp');">Sales Order</a></li>
						<%if(((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.VAN)){ %>
						<li><a href="#" onclick="changeURL('receipt/receipt.jsp');">Receipt</a></li>
						<%} %>
					</ul>
				</li>
				<li><a href="#">Report</a>
					<ul>
						<li><a href="#" onclick="changeURL('reports/customerReport.jsp');">Customer</a></li>
					</ul>
				</li>
			</ul>
		</div>
	</div>
</div>
