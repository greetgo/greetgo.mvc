<%--suppress HtmlUnknownTarget --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <%@include file="_head_std.jsp" %>
  <title>MVC Example - greetgo!</title>
</head>
<body>
<p>
  <a href="request_parameters/form">Request Parameters Example</a>
</p>
<ul>
  <li><a href="request_parameters/form#base-example">Base Example</a></li>
  <li><a href="request_parameters/form#par-json-example">@Par @Json Example</a></li>
  <li><a href="request_parameters/form#params-to-example">@ParamsTo Example</a></li>
  <li><a href="request_parameters/form#par-path-example">@ParPath Example</a></li>
  <li><a href="request_parameters/form#par-session-example">@ParSession Example</a></li>
</ul>
</body>
</html>
