<%@page import="twittMap.DBManager"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="twittMap.*" %>


<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8">
    <title>Hello AWS Web World!</title>
    <link rel="stylesheet" href="styles/styles.css" type="text/css" media="screen">
</head>
<body>
    <div>
    <form method="post" id="gatherServ">
    	<input type="hidden" name="gatherFlag" id="gatherFlag"/>
    	<br>number
    	<input name="gatherNumber" id="gatherNumber"/>
    	<br>sleepTime
    	<input name="sleepTime" id="sleepTime"/>
    </form>
    <input type = "button" value = "start" onclick="JavaScript:gatherManager('1');">
    <input type = "button" value = "stop" onclick="JavaScript:gatherManager('0');">
    <input type = "button" value = "quick " onclick="JavaScript:gatherManager('2');">
    </div>
  <script> 
  
    function gatherManager(gatherFlag)
			{    
				var sTitle;
				if (gatherFlag==1) {
					sTitle = "start";
				}
				else if (gatherFlag==2) {
					sTitle = "quick";
				}
				else
					sTitle = "stop";
				
				if (confirm(sTitle + " ？") != 1) return;	
				document.getElementById("gatherFlag").value=gatherFlag;
				var oForm = document.getElementById("gatherServ");	
				oForm.action="gatherAction.jsp";
				oForm.submit();
			}
    </script>
</body>
</html>