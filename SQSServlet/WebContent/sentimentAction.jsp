<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="analysis.*" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8">
    <title>sentiment analysis!</title>
    <link rel="stylesheet" href="styles/styles.css" type="text/css" media="screen">
</head>
<body>       
    <div>
		<%
		SentimentAnalyse sentimentAnalyse = SentimentAnalyse.getInstance();
		System.out.println(sentimentAnalyse);
			int flag = Integer.parseInt(request.getParameter("sentimentFlag"));
			System.out.println("flag: " + flag);
			if(flag == 1) {
				sentimentAnalyse.startThreadPool();
			}
			else {
				sentimentAnalyse.closeThreadPool();
			}
		%>
    </div>
</body>
</html>