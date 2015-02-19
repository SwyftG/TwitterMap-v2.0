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
		<%
		DBManager dBManager = new DBManager();
		dBManager.getDirver();
		dBManager.connectAWS();
		String num = dBManager.queryNum();
		%>
		<br>
		<%=num%>
    </div>
    
    <br> sentiment analysis
    <div>
    <form method="post" id="sentimentServ">
    	<input type="hidden" name="sentimentFlag" id="sentimentFlag"/>
    </form>
    <input type = "button" value = "start" onclick="JavaScript:sentimentManager('1');">
    <input type = "button" value = "stop" onclick="JavaScript:sentimentManager('0');">
    </div>
  <script> 
	function sentimentManager(sentimentFlag)
	{    
		var sTitle;
		if (sentimentFlag==1) {
			sTitle = "start";
		}
		else
			sTitle = "stop";
		
		if (confirm(sTitle + " ？") != 1) return;	
		document.getElementById("sentimentFlag").value=sentimentFlag;
		var oForm = document.getElementById("sentimentServ");	
		oForm.action="sentimentAction.jsp";
		oForm.submit();
	}
	
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