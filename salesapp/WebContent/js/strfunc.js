 //<script language="javascript">
function LTrim(str)
/*
   PURPOSE: Remove leading blanks from our string.
   IN: str - the string we want to LTrim
*/
{
   var whitespace = new String(" \t\n\r");
   var s = new String(str);
   if (whitespace.indexOf(s.charAt(0)) != -1) {
      // We have a string with leading blank(s)...

      var j=0, i = s.length;

      // Iterate from the far left of string until we
      // don't have any more whitespace...
      while (j < i && whitespace.indexOf(s.charAt(j)) != -1)
         j++;

      // Get the substring from the first non-whitespace
      // character to the end of the string...
      s = s.substring(j, i);
   }
   return s;
}
 function RTrim(str)
    /*
       PURPOSE: Remove trailing blanks from our string.
       IN: str - the string we want to RTrim

    */
    {
       // We don't want to trip JUST spaces, but also tabs,
       // line feeds, etc.  Add anything else you want to
       // "trim" here in Whitespace
       var whitespace = new String(" \t\n\r");

       var s = new String(str);

       if (whitespace.indexOf(s.charAt(s.length-1)) != -1) {
          // We have a string with trailing blank(s)...

          var i = s.length - 1;       // Get length of string

          // Iterate from the far right of string until we
          // don't have any more whitespace...
          while (i >= 0 && whitespace.indexOf(s.charAt(i)) != -1)
             i--;


          // Get the substring from the front of the string to
          // where the last non-whitespace character is...
          s = s.substring(0, i+1);
       }

       return s;
    }

    function Trim(str)
    /*
       PURPOSE: Remove trailing and leading blanks from our string.
       IN: str - the string we want to Trim

       RETVAL: A Trimmed string!
    */
    {
       return RTrim(LTrim(str));
    }
    function numbersonly(){
     if (event.keyCode<48||event.keyCode>57)
        return false
  }
   function numbersonly1(){
           if (event.keyCode==46)
             return true
           else
           if (event.keyCode<48||event.keyCode>57)
              return false
       }


             function ReplaceAll( inText, inFindStr, inReplStr, inCaseSensitive ) {
                var searchFrom = 0;
                var offset = 0;
                var outText = "";
                var searchText = "";
                if ( inCaseSensitive == null ) {
                   inCaseSensitive = false;
                }
                if ( inCaseSensitive ) {
                   searchText = inText.toLowerCase();
                   inFindStr = inFindStr.toLowerCase();
                } else {
                   searchText = inText;
                }
                offset = searchText.indexOf( inFindStr, searchFrom );
                while ( offset != -1 ) {
                   outText += inText.substring( searchFrom, offset );
                   outText += inReplStr;
                   searchFrom = offset + inFindStr.length;
                   offset = searchText.indexOf( inFindStr, searchFrom );
                }
                outText += inText.substring( searchFrom, inText.length );

                return ( outText );
             }
             function replaceurl( inText ) {
                inText = ReplaceAll (inText , '%', '%25');
                inText = ReplaceAll (inText , '&', '%26');
                inText = ReplaceAll (inText , '<', '%3C');
                inText = ReplaceAll (inText , '>', '%3E');
                inText = ReplaceAll (inText , '=', '%3D');
                inText = ReplaceAll (inText , '+', '%2B');
                inText = ReplaceAll (inText , '#', '%23');
                inText = ReplaceAll (inText , '?', '%40');
                outText= inText
                return ( outText );
             }
