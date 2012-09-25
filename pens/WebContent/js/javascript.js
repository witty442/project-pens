/**
 * struts-layout core javascript
 *
 * All rights reserved.
 */
 
// Dynamic result update
// for mathDataCollection on CollectionInputTab

function setMenuCookie(name, state) {	
	if (name.indexOf("treeView")!=-1) {
		if (state=="show") {
			var cookie = getMenuCookie("treeView", "");
			if (cookie=="???") cookie = "_";
			cookie = cookie + name + "_";
			document.cookie = "treeView=" + escape(cookie);

		} else {
			var cookie = getMenuCookie("treeView", "");
			var begin = cookie.indexOf("_" + name + "_");
			if (cookie.length > begin + name.length + 2) {
				cookie = cookie.substring(0, begin+1) + cookie.substring(begin + 2 + name.length);
			} else {
				cookie = cookie.substring(0, begin+1);
			}
			document.cookie = "treeView=" + escape(cookie);
		}
		return;
	} 
	if (name.indexOf("selectedTab")!=-1) {
		document.cookie = "selectedTab=" + escape(state) + getCookieContextPath();
	} else {
		var cookie = name + "STRUTSMENU=" + escape(state);
		document.cookie = cookie;	
	}
}

function getCookieContextPath() {
	if (window.contextPath) {
		return "; path=" + window.contextPath;
	} else {
		return "";
	}
}


function setTabCookie(name, value) {
	var cookie = getMenuCookie("selectedTab", "");	
	var start;
	var end;
	if (cookie=="undefined") cookie = "";
	if (cookie==null) cookie = "";
	if (cookie=="???") cookie = "";	
	start = cookie.indexOf(name + "=");
	if (start==-1) {
		cookie = cookie + name + "=" + value + ";"
	} else {
		end = cookie.substring(start).indexOf(";");
		cookie = cookie.substring(0, start) + name + "=" + value + cookie.substring(start+end);
	}
	setMenuCookie("selectedTab", cookie);
}

function getMenuCookie(name, suffix) {
	if (suffix==null) {
		suffix = "STRUTSMENU";
	}
	var prefix = name + suffix + "=";
	var cookieStartIndex = document.cookie.indexOf(prefix);
	if (cookieStartIndex == -1) return "???";
	var cookieEndIndex = document.cookie.indexOf(";", cookieStartIndex + prefix.length);
	if (cookieEndIndex == -1) cookieEndIndex = document.cookie.length;
	return unescape(document.cookie.substring(cookieStartIndex + prefix.length, cookieEndIndex));
}

/**
 * Tabs code.
 *
 * @param tabVarName: name of the form variable that holds the id of the selected tab.
 */
function selectTab(tabGroupId, tabGroupSize, selectedTabId, enabledStyle, disabledStyle, errorStyle, tabKeyName, tabKeyValue) {
	// first unselect all tab in the tag groups.
	for (i=0;i<tabGroupSize;i++) {
		element = document.getElementById("tabs" + tabGroupId + "head" + i);
		if (element.classNameErrorStdLayout) {
			element.className = errorStyle;
			element.style.color = "";			
		} else if (element.className == enabledStyle) {
			element.className = disabledStyle;
			element.style.color = "";
		} else if (element.className == errorStyle) {
			// do nothing more
		}
		
		document.getElementById("tabs" + tabGroupId + "tab" + i).style.display = "none";
	}
	if (document.getElementById("tabs" + tabGroupId + "head" + selectedTabId).className==errorStyle) {
		document.getElementById("tabs" + tabGroupId + "head" + selectedTabId).classNameErrorStdLayout = new Object();
	}
	document.getElementById("tabs" + tabGroupId + "head" + selectedTabId).className = enabledStyle;
	document.getElementById("tabs" + tabGroupId + "head" + selectedTabId).style.cursor = "default";
	document.getElementById("tabs" + tabGroupId + "tab" + selectedTabId).style.display = "";
	
	// update a cookie holding the name of the selected tab.
	if (tabKeyName!=null) {
		setTabCookie(tabKeyName, tabKeyValue);
	}
	
	// Add This Line For Adjust iFrame When Change Tab
	parent.changeHeight(parent.document.getElementById("mainframe"));
}
function onTabHeaderOver(tabGroupId, selectedTabId, enabledStyle) {
	element = document.getElementById("tabs" + tabGroupId + "head" + selectedTabId);
	if (element.className == enabledStyle) {
		element.style.cursor = "default";
	} else {
		element.style.cursor = "hand";
	}
}

