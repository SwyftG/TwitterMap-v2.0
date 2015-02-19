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
		<%
			StreamService ser = StreamService.getInstance();
		//System.out.println(ser.toString());
			int flag = Integer.parseInt(request.getParameter("gatherFlag"));
			System.out.println("flag: " + flag);
			//default number = 100, time = 10*1000
			int gatherNumber = request.getParameter("gatherNumber").isEmpty() ? 100 : Integer.parseInt(request.getParameter("gatherNumber"));
			int sleepTime = request.getParameter("sleepTime").isEmpty() ? 10000 : Integer.parseInt(request.getParameter("sleepTime")) * 1000;
			System.out.println("gatherNumber: "+ gatherNumber +" sleepTime: " + sleepTime);
			if(flag == 1) {
				ser.gatherNum(gatherNumber, sleepTime);
			}
			else if(flag == 2) {
				ser.gather();
			} else {
				ser.stopGather();
			}
		%>
    </div>
</body>
</html>