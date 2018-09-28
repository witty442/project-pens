/**
 * @summary     FixedColumns
 * @description Freeze columns in place on a scrolling DataTable
 * @file        FixedColumns.js
 * @version     2.0.3
 * @author      Allan Jardine (www.sprymedia.co.uk)
 * @license     GPL v2 or BSD 3 point style
 * @contact     www.sprymedia.co.uk/contact
 *
 * @copyright Copyright 2010-2011 Allan Jardine, all rights reserved.
 *
 * This source file is free software, under either the GPL v2 license or a
 * BSD style license, available at:
 *   http://datatables.net/license_gpl2
 *   http://datatables.net/license_bsd
 */


/* Global scope for FixedColumns */
var FixedColumns;

(function($, window, document) {


/** 
 * When making use of DataTables' x-axis scrolling feature, you may wish to 
 * fix the left most column in place. This plug-in for DataTables provides 
 * exactly this option (note for non-scrolling tables, please use the  
 * FixedHeader plug-in, which can fix headers, footers and columns). Key 
 * features include:
 *   <ul class="limit_length">
 *     <li>Freezes the left or right most columns to the side of the table</li>
 *     <li>Option to freeze two or more columns</li>
 *     <li>Full integration with DataTables' scrolling options</li>
 *     <li>Speed - FixedColumns is fast in its operation</li>
 *   </ul>
 *
 *  @class
 *  @constructor
 *  @param {object} oDT DataTables instance
 *  @param {object} [oInit={}] Configuration object for FixedColumns. Options are defined by {@link FixedColumns.defaults}
 * 
 *  @requires jQuery 1.3+
 *  @requires DataTables 1.8.0+
 * 
 *  @example
 *  	var oTable = $('#example').dataTable( {
 *  		"sScrollX": "100%"
 *  	} );
 *  	new FixedColumns( oTable );
 */
FixedColumns = function ( oDT, oInit ) {
	/* Sanity check - you just know it will happen */
	if ( ! this instanceof FixedColumns )
	{
		alert( "FixedColumns warning: FixedColumns must be initialised with the 'new' keyword." );
		return;
	}
	
	if ( typeof oInit == 'undefined' )
	{
		oInit = {};
	}
	
	/**
	 * Settings object which contains customisable information for FixedColumns instance
	 * @namespace
	 * @extends FixedColumns.defaults
	 */
	this.s = {
		/** 
		 * DataTables settings objects
		 *  @type     object
		 *  @default  Obtained from DataTables instance
		 */
		"dt": oDT.fnSettings(),
		
		/** 
		 * Number of columns in the DataTable - stored for quick access
		 *  @type     int
		 *  @default  Obtained from DataTables instance
		 */
		"iTableColumns": oDT.fnSettings().aoColumns.length,
		
		/** 
		 * Original widths of the columns as rendered by DataTables
		 *  @type     array.<int>
		 *  @default  []
		 */
		"aiWidths": [],
		
		/** 
		 * Flag to indicate if we are dealing with IE6/7 as these browsers need a little hack
		 * in the odd place
		 *  @type     boolean
		 *  @default  Automatically calculated
		 *  @readonly
		 */
		"bOldIE": ($.browser.msie && ($.browser.version == "6.0" || $.browser.version == "7.0"))
	};
	
	
	/**
	 * DOM elements used by the class instance
	 * @namespace
	 * 
	 */
	this.dom = {
		/**
		 * DataTables scrolling element
		 *  @type     node
		 *  @default  null
		 */
		"scroller": null,
		
		/**
		 * DataTables header table
		 *  @type     node
		 *  @default  null
		 */
		"header": null,
		
		/**
		 * DataTables body table
		 *  @type     node
		 *  @default  null
		 */
		"body": null,
		
		/**
		 * DataTables footer table
		 *  @type     node
		 *  @default  null
		 */
		"footer": null,

		/**
		 * Display grid elements
		 * @namespace
		 */
		"grid": {
			/**
			 * Grid wrapper. This is the container element for the 3x3 grid
			 *  @type     node
			 *  @default  null
			 */
			"wrapper": null,

			/**
			 * DataTables scrolling element. This element is the DataTables
			 * component in the display grid (making up the main table - i.e.
			 * not the fixed columns).
			 *  @type     node
			 *  @default  null
			 */
			"dt": null,

			/**
			 * Left fixed column grid components
			 * @namespace
			 */
			"left": {
				"wrapper": null,
				"head": null,
				"body": null,
				"foot": null
			},

			/**
			 * Right fixed column grid components
			 * @namespace
			 */
			"right": {
				"wrapper": null,
				"head": null,
				"body": null,
				"foot": null
			}
		},
		
		/**
		 * Cloned table nodes
		 * @namespace
		 */
		"clone": {
			/**
			 * Left column cloned table nodes
			 * @namespace
			 */
			"left": {
				/**
				 * Cloned header table
				 *  @type     node
				 *  @default  null
				 */
				"header": null,
		  	
				/**
				 * Cloned body table
				 *  @type     node
				 *  @default  null
				 */
				"body": null,
		  	
				/**
				 * Cloned footer table
				 *  @type     node
				 *  @default  null
				 */
				"footer": null
			},
			
			/**
			 * Right column cloned table nodes
			 * @namespace
			 */
			"right": {
				/**
				 * Cloned header table
				 *  @type     node
				 *  @default  null
				 */
				"header": null,
		  	
				/**
				 * Cloned body table
				 *  @type     node
				 *  @default  null
				 */
				"body": null,
		  	
				/**
				 * Cloned footer table
				 *  @type     node
				 *  @default  null
				 */
				"footer": null
			}
		}
	};

	/* Attach the instance to the DataTables instance so it can be accessed easily */
	this.s.dt.oFixedColumns = this;
	
	/* Let's do it */
	this._fnConstruct( oInit );
};



FixedColumns.prototype = {
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Update the fixed columns - including headers and footers. Note that FixedColumns will
	 * automatically update the display whenever the host DataTable redraws.
	 *  @returns {void}
	 *  @example
	 *  	var oTable = $('#example').dataTable( {
	 *  		"sScrollX": "100%"
	 *  	} );
	 *  	var oFC = new FixedColumns( oTable );
	 *  	
	 *  	// at some later point when the table has been manipulated....
	 *  	oFC.fnUpdate();
	 */
	"fnUpdate": function ()
	{
		this._fnDraw( true );
	},
	
	
	/**
	 * Recalculate the resizes of the 3x3 grid that FixedColumns uses for display of the table.
	 * This is useful if you update the width of the table container. Note that FixedColumns will
	 * perform this function automatically when the window.resize event is fired.
	 *  @returns {void}
	 *  @example
	 *  	var oTable = $('#example').dataTable( {
	 *  		"sScrollX": "100%"
	 *  	} );
	 *  	var oFC = new FixedColumns( oTable );
	 *  	
	 *  	// Resize the table container and then have FixedColumns adjust its layout....
	 *      $('#content').width( 1200 );
	 *  	oFC.fnRedrawLayout();
	 */
	"fnRedrawLayout": function ()
	{
		this._fnGridLayout();
	},
	
	
	/**
	 * Mark a row such that it's height should be recalculated when using 'semiauto' row
	 * height matching. This function will have no effect when 'none' or 'auto' row height
	 * matching is used.
	 *  @param   {Node} nTr TR element that should have it's height recalculated
	 *  @returns {void}
	 *  @example
	 *  	var oTable = $('#example').dataTable( {
	 *  		"sScrollX": "100%"
	 *  	} );
	 *  	var oFC = new FixedColumns( oTable );
	 *  	
	 *  	// manipulate the table - mark the row as needing an update then update the table
	 *  	// this allows the redraw performed by DataTables fnUpdate to recalculate the row
	 *  	// height
	 *  	oFC.fnRecalculateHeight();
	 *  	oTable.fnUpdate( $('#example tbody tr:eq(0)')[0], ["insert date", 1, 2, 3 ... ]);
	 */
	"fnRecalculateHeight": function ( nTr )
	{
		nTr._DTTC_iHeight = null;
		nTr.style.height = 'auto';
	},
	
	
	/**
	 * Set the height of a given row - provides cross browser compatibility
	 *  @param   {Node} nTarget TR element that should have it's height recalculated
	 *  @param   {int} iHeight Height in pixels to set
	 *  @returns {void}
	 *  @example
	 *  	var oTable = $('#example').dataTable( {
	 *  		"sScrollX": "100%"
	 *  	} );
	 *  	var oFC = new FixedColumns( oTable );
	 *  	
	 *  	// You may want to do this after manipulating a row in the fixed column
	 *  	oFC.fnSetRowHeight( $('#example tbody tr:eq(0)')[0], 50 );
	 */
	"fnSetRowHeight": function ( nTarget, iHeight )
	{
		var jqBoxHack = $(nTarget).children(':first');
		var iBoxHack = jqBoxHack.outerHeight() - jqBoxHack.height();

		/* Can we use some kind of object detection here?! This is very nasty - damn browsers */
		if ( $.browser.mozilla || $.browser.opera )
		{
			nTarget.style.height = iHeight+"px";
		}
		else
		{
			$(nTarget).children().height( iHeight-iBoxHack );
		}
	},
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Private methods (they are of course public in JS, but recommended as private)
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * Initialisation for FixedColumns
	 *  @param   {Object} oInit User settings for initialisation
	 *  @returns {void}
	 *  @private
	 */
	"_fnConstruct": function ( oInit )
	{
		var i, iLen, iWidth,
			that = this;
		
		/* Sanity checking */
		if ( typeof this.s.dt.oInstance.fnVersionCheck != 'function' ||
		     this.s.dt.oInstance.fnVersionCheck( '1.8.0' ) !== true )
		{
			alert( "FixedColumns "+FixedColumns.VERSION+" required DataTables 1.8.0 or later. "+
				"Please upgrade your DataTables installation" );
			return;
		}
		
		if ( this.s.dt.oScroll.sX === "" )
		{
			this.s.dt.oInstance.oApi._fnLog( this.s.dt, 1, "FixedColumns is not needed (no "+
				"x-scrolling in DataTables enabled), so no action will be taken. Use 'FixedHeader' for "+
				"column fixing when scrolling is not enabled" );
			return;
		}
		
		/* Apply the settings from the user / defaults */
		this.s = $.extend( true, this.s, FixedColumns.defaults, oInit );

		/* Set up the DOM as we need it and cache nodes */
		this.dom.grid.dt = $(this.s.dt.nTable).parents('div.dataTables_scroll')[0];
		this.dom.scroller = $('div.dataTables_scrollBody', this.dom.grid.dt )[0];

		var iScrollWidth = $(this.dom.grid.dt).width();
		var iLeftWidth = 0;
		var iRightWidth = 0;

		$('tbody>tr:eq(0)>td', this.s.dt.nTable).each( function (i) {
			iWidth = $(this).outerWidth();
			that.s.aiWidths.push( iWidth );
			if ( i < that.s.iLeftColumns )
			{
				iLeftWidth += iWidth;
			}
			if ( that.s.iTableColumns-that.s.iRightColumns <= i )
			{
				iRightWidth += iWidth;
			}
		} );

		if ( this.s.iLeftWidth === null )
		{
			this.s.iLeftWidth = this.s.sLeftWidth == 'fixed' ?
				iLeftWidth : (iLeftWidth/iScrollWidth) * 100; 
		}
		
		if ( this.s.iRightWidth === null )
		{
			this.s.iRightWidth = this.s.sRightWidth == 'fixed' ?
				iRightWidth : (iRightWidth/iScrollWidth) * 100;
		}
		
		/* Set up the DOM that we want for the fixed column layout grid */
		this._fnGridSetup();

		/* Use the DataTables API method fnSetColumnVis to hide the columns we are going to fix */
		for ( i=0 ; i<this.s.iLeftColumns ; i++ )
		{
			this.s.dt.oInstance.fnSetColumnVis( i, false );
		}
		for ( i=this.s.iTableColumns - this.s.iRightColumns ; i<this.s.iTableColumns ; i++ )
		{
			this.s.dt.oInstance.fnSetColumnVis( i, false );
		}

		/* Event handlers */
		$(this.dom.scroller).scroll( function () {
			that.dom.grid.left.body.scrollTop = that.dom.scroller.scrollTop;
			if ( that.s.iRightColumns > 0 )
			{
				that.dom.grid.right.body.scrollTop = that.dom.scroller.scrollTop;
			}
		} );

		$(window).resize( function () {
			that._fnGridLayout.call( that );
		} );
		
		var bFirstDraw = true;
		this.s.dt.aoDrawCallback = [ {
			"fn": function () {
				that._fnDraw.call( that, bFirstDraw );
				that._fnGridHeight( that );
				bFirstDraw = false;
			},
			"sName": "FixedColumns"
		} ].concat( this.s.dt.aoDrawCallback );
		
		/* Get things right to start with - note that due to adjusting the columns, there must be
		 * another redraw of the main table. It doesn't need to be a full redraw however.
		 */
		this._fnGridLayout();
		this._fnGridHeight();
		this.s.dt.oInstance.fnDraw(false);
	},
	
	
	/**
	 * Set up the DOM for the fixed column. The way the layout works is to create a 1x3 grid
	 * for the left column, the DataTable (for which we just reuse the scrolling element DataTable
	 * puts into the DOM) and the right column. In each of he two fixed column elements there is a
	 * grouping wrapper element and then a head, body and footer wrapper. In each of these we then
	 * place the cloned header, body or footer tables. This effectively gives as 3x3 grid structure.
	 *  @returns {void}
	 *  @private
	 */
	"_fnGridSetup": function ()
	{
		var that = this;

		this.dom.body = this.s.dt.nTable;
		this.dom.header = this.s.dt.nTHead.parentNode;
		this.dom.header.parentNode.parentNode.style.position = "relative";
		
		var nSWrapper = 
			$('<div class="DTFC_ScrollWrapper" style="position:relative; clear:both;">'+
				'<div class="DTFC_LeftWrapper" style="position:absolute; top:0; left:0;">'+
					'<div class="DTFC_LeftHeadWrapper" style="position:relative; top:0; left:0; overflow:hidden;"></div>'+
					'<div class="DTFC_LeftBodyWrapper" style="position:relative; top:0; left:0; overflow:hidden;"></div>'+
					'<div class="DTFC_LeftFootWrapper" style="position:relative; top:0; left:0; overflow:hidden;"></div>'+
			  	'</div>'+
				'<div class="DTFC_RightWrapper" style="position:absolute; top:0; left:0;">'+
					'<div class="DTFC_RightHeadWrapper" style="position:relative; top:0; left:0; overflow:hidden;"></div>'+
					'<div class="DTFC_RightBodyWrapper" style="position:relative; top:0; left:0; overflow:hidden;"></div>'+
					'<div class="DTFC_RightFootWrapper" style="position:relative; top:0; left:0; overflow:hidden;"></div>'+
			  	'</div>'+
			  '</div>')[0];
		nLeft = nSWrapper.childNodes[0];
		nRight = nSWrapper.childNodes[1];

		this.dom.grid.wrapper = nSWrapper;
		this.dom.grid.left.wrapper = nLeft;
		this.dom.grid.left.head = nLeft.childNodes[0];
		this.dom.grid.left.body = nLeft.childNodes[1];

		if ( this.s.iRightColumns > 0 )
		{
			this.dom.grid.right.wrapper = nRight;
			this.dom.grid.right.head = nRight.childNodes[0];
			this.dom.grid.right.body = nRight.childNodes[1];
		}
		
		if ( this.s.dt.nTFoot )
		{
			this.dom.footer = this.s.dt.nTFoot.parentNode;
			this.dom.grid.left.foot = nLeft.childNodes[2];
			if ( this.s.iRightColumns > 0 )
			{
				this.dom.grid.right.foot = nRight.childNodes[2];
			}
		}

		nSWrapper.appendChild( nLeft );
		this.dom.grid.dt.parentNode.insertBefore( nSWrapper, this.dom.grid.dt );
		nSWrapper.appendChild( this.dom.grid.dt );

		this.dom.grid.dt.style.position = "absolute";
		this.dom.grid.dt.style.top = "0px";
		this.dom.grid.dt.style.left = this.s.iLeftWidth+"px";
		this.dom.grid.dt.style.width = ($(this.dom.grid.dt).width()-this.s.iLeftWidth-this.s.iRightWidth)+"px";
	},
	
	
	/**
	 * Style and position the grid used for the FixedColumns layout based on the instance settings.
	 * Specifically sLeftWidth ('fixed' or 'absolute'), iLeftWidth (px if fixed, % if absolute) and
	 * there 'right' counterparts.
	 *  @returns {void}
	 *  @private
	 */
	"_fnGridLayout": function ()
	{
		var oGrid = this.dom.grid;
		var iTotal = $(oGrid.wrapper).width();
		var iLeft = 0, iRight = 0, iRemainder = 0;

		if ( this.s.sLeftWidth == 'fixed' )
		{
			iLeft = this.s.iLeftWidth;
		}
		else
		{
			iLeft = ( this.s.iLeftWidth / 100 ) * iTotal;
		}

		if ( this.s.sRightWidth == 'fixed' )
		{
			iRight = this.s.iRightWidth;
		}
		else
		{
			iRight = ( this.s.iRightWidth / 100 ) * iTotal;
		}

		iRemainder = iTotal - iLeft - iRight;

		oGrid.left.wrapper.style.width = iLeft+"px";
		oGrid.dt.style.width = iRemainder+"px";
		oGrid.dt.style.left = iLeft+"px";

		if ( this.s.iRightColumns > 0 )
		{
			oGrid.right.wrapper.style.width = iRight+"px";
			oGrid.right.wrapper.style.left = (iTotal-iRight)+"px";
		}
	},
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       