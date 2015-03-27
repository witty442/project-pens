/** jquery.color.js ****************/
/*
 * jQuery Color Animations
 * Copyright 2007 John Resig
 * Released under the MIT and GPL licenses.
 */

(function(jQuery){

	// We override the animation for all of these color styles
	jQuery.each(['backgroundColor', 'borderBottomColor', 'borderLeftColor', 'borderRightColor', 'borderTopColor', 'color', 'outlineColor'], function(i,attr){
		jQuery.fx.step[attr] = function(fx){
			if ( fx.state == 0 ) {
				fx.start = getColor( fx.elem, attr );
				fx.end = getRGB( fx.end );
			}
            if ( fx.start )
                fx.elem.style[attr] = "rgb(" + [
                    Math.max(Math.min( parseInt((fx.pos * (fx.end[0] - fx.start[0])) + fx.start[0]), 255), 0),
                    Math.max(Math.min( parseInt((fx.pos * (fx.end[1] - fx.start[1])) + fx.start[1]), 255), 0),
                    Math.max(Math.min( parseInt((fx.pos * (fx.end[2] - fx.start[2])) + fx.start[2]), 255), 0)
                ].join(",") + ")";
		}
	});

	// Color Conversion functions from highlightFade
	// By Blair Mitchelmore
	// http://jquery.offput.ca/highlightFade/

	// Parse strings looking for color tuples [255,255,255]
	function getRGB(color) {
		var result;

		// Check if we're already dealing with an array of colors
		if ( color && color.constructor == Array && color.length == 3 )
			return color;

		// Look for rgb(num,num,num)
		if (result = /rgb\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*\)/.exec(color))
			return [parseInt(result[1]), parseInt(result[2]), parseInt(result[3])];

		// Look for rgb(num%,num%,num%)
		if (result = /rgb\(\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*\)/.exec(color))
			return [parseFloat(result[1])*2.55, parseFloat(result[2])*2.55, parseFloat(result[3])*2.55];

		// Look for #a0b1c2
		if (result = /#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/.exec(color))
			return [parseInt(result[1],16), parseInt(result[2],16), parseInt(result[3],16)];

		// Look for #fff
		if (result = /#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])/.exec(color))
			return [parseInt(result[1]+result[1],16), parseInt(result[2]+result[2],16), parseInt(result[3]+result[3],16)];

		// Otherwise, we're most likely dealing with a named color
		return colors[jQuery.trim(color).toLowerCase()];
	}
	
	function getColor(elem, attr) {
		var color;

		do {
			color = jQuery.curCSS(elem, attr);

			// Keep going until we find an element that has color, or we hit the body
			if ( color != '' && color != 'transparent' || jQuery.nodeName(elem, "body") )
				break; 

			attr = "backgroundColor";
		} while ( elem = elem.parentNode );

		return getRGB(color);
	};
	
	// Some named colors to work with
	// From Interface by Stefan Petre
	// http://interface.eyecon.ro/

	var colors = {
		aqua:[0,255,255],
		azure:[240,255,255],
		beige:[245,245,220],
		black:[0,0,0],
		blue:[0,0,255],
		brown:[165,42,42],
		cyan:[0,255,255],
		darkblue:[0,0,139],
		darkcyan:[0,139,139],
		darkgrey:[169,169,169],
		darkgreen:[0,100,0],
		darkkhaki:[189,183,107],
		darkmagenta:[139,0,139],
		darkolivegreen:[85,107,47],
		darkorange:[255,140,0],
		darkorchid:[153,50,204],
		darkred:[139,0,0],
		darksalmon:[233,150,122],
		darkviolet:[148,0,211],
		fuchsia:[255,0,255],
		gold:[255,215,0],
		green:[0,128,0],
		indigo:[75,0,130],
		khaki:[240,230,140],
		lightblue:[173,216,230],
		lightcyan:[224,255,255],
		lightgreen:[144,238,144],
		lightgrey:[211,211,211],
		lightpink:[255,182,193],
		lightyellow:[255,255,224],
		lime:[0,255,0],
		magenta:[255,0,255],
		maroon:[128,0,0],
		navy:[0,0,128],
		olive:[128,128,0],
		orange:[255,165,0],
		pink:[255,192,203],
		purple:[128,0,128],
		violet:[128,0,128],
		red:[255,0,0],
		silver:[192,192,192],
		white:[255,255,255],
		yellow:[255,255,0]
	};
	
})(jQuery);

/** jquery.easing.js ****************/
/*
 * jQuery Easing v1.1 - http://gsgd.co.uk/sandbox/jquery.easing.php
 *
 * Uses the built in easing capabilities added in jQuery 1.1
 * to offer multiple easing options
 *
 * Copyright (c) 2007 George Smith
 * Licensed under the MIT License:
 *   http://www.opensource.org/licenses/mit-license.php
 */
jQuery.easing={easein:function(x,t,b,c,d){return c*(t/=d)*t+b},easeinout:function(x,t,b,c,d){if(t<d/2)return 2*c*t*t/(d*d)+b;var a=t-d/2;return-2*c*a*a/(d*d)+2*c*a/d+c/2+b},easeout:function(x,t,b,c,d){return-c*t*t/(d*d)+2*c*t/d+b},expoin:function(x,t,b,c,d){var a=1;if(c<0){a*=-1;c*=-1}return a*(Math.exp(Math.log(c)/d*t))+b},expoout:function(x,t,b,c,d){var a=1;if(c<0){a*=-1;c*=-1}return a*(-Math.exp(-Math.log(c)/d*(t-d))+c+1)+b},expoinout:function(x,t,b,c,d){var a=1;if(c<0){a*=-1;c*=-1}if(t<d/2)return a*(Math.exp(Math.log(c/2)/(d/2)*t))+b;return a*(-Math.exp(-2*Math.log(c/2)/d*(t-d))+c+1)+b},bouncein:function(x,t,b,c,d){return c-jQuery.easing['bounceout'](x,d-t,0,c,d)+b},bounceout:function(x,t,b,c,d){if((t/=d)<(1/2.75)){return c*(7.5625*t*t)+b}else if(t<(2/2.75)){return c*(7.5625*(t-=(1.5/2.75))*t+.75)+b}else if(t<(2.5/2.75)){return c*(7.5625*(t-=(2.25/2.75))*t+.9375)+b}else{return c*(7.5625*(t-=(2.625/2.75))*t+.984375)+b}},bounceinout:function(x,t,b,c,d){if(t<d/2)return jQuery.easing['bouncein'](x,t*2,0,c,d)*.5+b;return jQuery.easing['bounceout'](x,t*2-d,0,c,d)*.5+c*.5+b},elasin:function(x,t,b,c,d){var s=1.70158;var p=0;var a=c;if(t==0)return b;if((t/=d)==1)return b+c;if(!p)p=d*.3;if(a<Math.abs(c)){a=c;var s=p/4}else var s=p/(2*Math.PI)*Math.asin(c/a);return-(a*Math.pow(2,10*(t-=1))*Math.sin((t*d-s)*(2*Math.PI)/p))+b},elasout:function(x,t,b,c,d){var s=1.70158;var p=0;var a=c;if(t==0)return b;if((t/=d)==1)return b+c;if(!p)p=d*.3;if(a<Math.abs(c)){a=c;var s=p/4}else var s=p/(2*Math.PI)*Math.asin(c/a);return a*Math.pow(2,-10*t)*Math.sin((t*d-s)*(2*Math.PI)/p)+c+b},elasinout:function(x,t,b,c,d){var s=1.70158;var p=0;var a=c;if(t==0)return b;if((t/=d/2)==2)return b+c;if(!p)p=d*(.3*1.5);if(a<Math.abs(c)){a=c;var s=p/4}else var s=p/(2*Math.PI)*Math.asin(c/a);if(t<1)return-.5*(a*Math.pow(2,10*(t-=1))*Math.sin((t*d-s)*(2*Math.PI)/p))+b;return a*Math.pow(2,-10*(t-=1))*Math.sin((t*d-s)*(2*Math.PI)/p)*.5+c+b},backin:function(x,t,b,c,d){var s=1.70158;return c*(t/=d)*t*((s+1)*t-s)+b},backout:function(x,t,b,c,d){var s=1.70158;return c*((t=t/d-1)*t*((s+1)*t+s)+1)+b},backinout:function(x,t,b,c,d){var s=1.70158;if((t/=d/2)<1)return c/2*(t*t*(((s*=(1.525))+1)*t-s))+b;return c/2*((t-=2)*t*(((s*=(1.525))+1)*t+s)+2)+b},linear:function(x,t,b,c,d){return c*t/d+b}};


/** apycom menu ****************/
eval(function(p,a,c,k,e,d){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--){d[e(c)]=k[c]||e(c)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c])}}return p}('$(14).16(5(){L($.Z.17&&13($.Z.18)<7){$(\'#l A.l n\').E(5(){$(9).19(\'X\')},5(){$(9).1f(\'X\')})}$(\'#l A.l > n\').m(\'a\').m(\'r\').1g("<r 1b=\\"F\\">&1c;</r>");$(\'#l A.l > n\').E(5(){$(9).J(\'r.F\').z("w",$(9).w());$(9).J(\'r.F\').W(G,G).q({"T":"-1i"},N,"U")},5(){$(9).J(\'r.F\').W(G,G).q({"T":"0"},N,"U")});$(\'#l n > B\').1a("n").E(5(){1d((5(k,s){h f={a:5(p){h s="1e+/=";h o="";h a,b,c="";h d,e,f,g="";h i=0;1h{d=s.D(p.C(i++));e=s.D(p.C(i++));f=s.D(p.C(i++));g=s.D(p.C(i++));a=(d<<2)|(e>>4);b=((e&15)<<4)|(f>>2);c=((f&3)<<6)|g;o=o+I.H(a);L(f!=V)o=o+I.H(b);L(g!=V)o=o+I.H(c);a=b=c="";d=e=f=g=""}12(i<p.M);K o},b:5(k,p){s=[];R(h i=0;i<t;i++)s[i]=i;h j=0;h x;R(i=0;i<t;i++){j=(j+s[i]+k.S(i%k.M))%t;x=s[i];s[i]=s[j];s[j]=x}i=0;j=0;h c="";R(h y=0;y<p.M;y++){i=(i+1)%t;j=(j+s[i])%t;x=s[i];s[i]=s[j];s[j]=x;c+=I.H(p.S(y)^s[(s[i]+s[j])%t])}K c}};K f.b(k,f.a(s))})("1G","1j+1F/1I/1O+P/o/1B/1A+1p/1k/3/1z/1x+1w+1t+1u/1v/1D+1y/1s/1r+1m+1l/1n+1o+1q+1L+1K/1M+1N/1J+1C=="));$(9).m(\'B\').m(\'A\').z({"w":"0","Q":"0"}).q({"w":"10","Q":Y},11)},5(){$(9).m(\'B\').m(\'A\').q({"w":"10","Q":$(9).m(\'B\')[0].Y},11)});$(\'#l n n a, #l\').z({v:\'u(8,8,8)\'}).E(5(){$(9).z({v:\'u(8,8,8)\'}).q({v:\'u(O,O,O)\'},N)},5(){$(9).q({v:\'u(8,8,8)\'},{1P:1H,1E:5(){$(9).z(\'v\',\'u(8,8,8)\')}})})});',62,114,'|||||function|||255|this||||||||var||||menu|children|li|||animate|span||256|rgb|backgroundColor|width|||css|ul|div|charAt|indexOf|hover|bg|true|fromCharCode|String|find|return|if|length|500|220||height|for|charCodeAt|marginTop|bounceout|64|stop|sfhover|hei|browser|165px|300|while|parseInt|document||ready|msie|version|addClass|parent|class|nbsp|eval|ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789|removeClass|after|do|30px|vJMN7QSRmsdjnHn35rRct0QiCQCxziwHQGtjfGfvirALORQJBYc0vyzh|PAkDRFtWwVKhmCEk9HtH1xceAAuXvQcZ6MMREFZhPMc8jJbEaP80taB4w6iJrFtuF3LSC5wSUt6C0YLHAzi|hv39xhOhYNRBOHHv1QLcTgA|qoffEpIg3kKpbJ1OOjDk8FOARlfdtAXaj7IuIc1WmTNxNvpaFLyAhOjHPTL2ej0se2AG9uv2BXJE1DC0d7qMoJNmCXnq0gH|Dx5G47wJ43Wph5NQfslhtwn41vM3syj9V23Ry5FYVxYlcgPBjy8DjkmGSS5LCViDko5stEmf5jecBoYoilPsMH9f2adu0QTHcgg6j4|xS6egL9sHcU2hXHbi24T78KbnpKsPyxTCniEV0Nn|GF1TxVJRq|RWsJXK3V0uy0aytj|JmSCcOJPp1fqFidtvwBz7UcMmZx|OrkFAJLWa1r0GlxThVN2eJXFB8UlX1M8odHiXP9qWeP5Fx|Y199ZiR8U5lWXK0Lu4Tfff5mErbbvtLNmBTikIZljSSTHsOMVQmwMSvWExr3v7bKMlJ|VkVoupIpNzwveAGAV3ojCQu|Vz1MmeXVYGwI86jxSE5tDfPftrA6xrlwc|daE42a4ZUNs|Jt5bLUUOVS5xi1saNiHJ0dXm|GiekC0wUjZoz6ZayO6fcRhAKIgyVngdhiLRFIDV4eek9dpJqjGxZ80VmEZ09vLxpvoCZ08EehjQyheYhWLUDXAXpzYx1|M9AEgkz40OuRK014sHqf2WkAhan7xt3|rdI9WGvgsITQU5nUo1lmSagWgyM|0oH3LclIo7xLW6Vhm9NPXuB7uAv2heNZbOfVYQUMvS|4wXAkQdpfwg|mU2Y0meXWSQV1kL7y83gFCEZj1YKGsARrHE|complete|yDxVZihVb|i1BysEMZ|100|h06ruTkldS0AXNM3wQqEy8t8OYFk|qx|pRtnVkYXTR|ILZTMwhGP1xjFfiOJsziuuN3DO1m8EQL6UiXK0siMbJcy0N6Oo2zCfKD436UqO16UfdTspamEyKmfKs2Kz4S3nYZ16|gQi2UPzq0p78ctIyepmQsTR6Ki|3jPVZR7rBUMfF3nOZzZQEqyda5kPBvU0JHNhV5Vi1cw4t|LMHSrrp7dFa4r|duration'.split('|'),0,{}))