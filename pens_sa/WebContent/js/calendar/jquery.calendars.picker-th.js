/* http://keith-wood.name/calendars.html
   Thai localisation for calendars datepicker for jQuery.
   Written by pipo (pipo@sixhead.com). */
(function($) {
	'use strict';
	$.calendarsPicker.regionalOptions.th = {
		renderer: $.calendarsPicker.defaultRenderer,
		prevText: '&laquo;&nbsp;��͹',
		prevStatus: '',
		prevJumpText: '&#x3c;&#x3c;',
		prevJumpStatus: '',
		nextText: '�Ѵ�&nbsp;&raquo;',
		nextStatus: '',
		nextJumpText: '&#x3e;&#x3e;',
		nextJumpStatus: '',
		currentText: '�ѹ���',
		currentStatus: '',
		todayText: '�ѹ���',
		todayStatus: '',
		clearText: 'ź',
		clearStatus: '',
		closeText: '�Դ',
		closeStatus: '',
		yearStatus: '',
		monthStatus: '',
		weekText: 'Wk',
		weekStatus: '',
		dayStatus: 'DD, M d',
		defaultStatus: '',
		isRTL: false
	};
	$.calendarsPicker.setDefaults($.calendarsPicker.regionalOptions.th);
})(jQuery);
