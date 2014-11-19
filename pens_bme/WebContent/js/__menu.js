var admin_click = false;
var master_click = false;
var transaction_click = false;
var interface_click = false;
var report_click = false;
var interval = 350;

function show_sub_menu(menu){
	if(menu=='admin'){
		if(admin_click==false){
			$('#s_m_' + menu).slideDown(interval);
			hide_sub_menu(menu);
			admin_click = true;	
		}else{
			hide_sub_menu('all');
		}
	} 
	if(menu=='master'){
		if(master_click==false){
			$('#s_m_' + menu).slideDown(interval);
			hide_sub_menu(menu);
			master_click = true;	
		}else{
			hide_sub_menu('all');
		}
	} 
	if(menu=='transaction'){
		if(transaction_click==false){
			$('#s_m_' + menu).slideDown(interval);
			hide_sub_menu(menu);
			transaction_click = true;	
		}else{
			hide_sub_menu('all');
		}
	}
	if(menu=='interface'){
		if(interface_click==false){
			$('#s_m_' + menu).slideDown(interval);
			hide_sub_menu(menu);
			interface_click = true;	
		}else{
			hide_sub_menu('all');
		}
	}
	if(menu=='report'){
		if(report_click==false){
			$('#s_m_' + menu).slideDown(interval);
			hide_sub_menu(menu);
			report_click = true;	
		}else{
			hide_sub_menu('all');
		}
	}
}

function hide_sub_menu(menu){
	if(menu!='admin'){
		$('#s_m_admin').slideUp(interval);
		admin_click = false;
	}
	if(menu!='master'){
		$('#s_m_master').slideUp(interval);
		master_click = false;
	}
	if(menu!='transaction'){
		$('#s_m_transaction').slideUp(interval);
		transaction_click = false;
	}
	if(menu!='interface'){
		$('#s_m_interface').slideUp(interval);
		interface_click = false;
	}
	if(menu!='report'){
		$('#s_m_report').slideUp(interval);
		report_click = false;
	}
}