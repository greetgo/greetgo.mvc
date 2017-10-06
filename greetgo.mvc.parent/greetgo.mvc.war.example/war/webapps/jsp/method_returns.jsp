<%--suppress HtmlUnknownTarget --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="contextPath" value="${pageContext.request.getContextPath()}"/>

<!DOCTYPE html>
<html lang="en">
<head>
  <%@include file="_head_std.jsp" %>
  <title>Method Returns: MVC Example - greetgo!</title>
  <%--suppress JSUnresolvedLibraryURL --%>
  <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
</head>
<body>

<%@include file="method_returns/using_as_is.jsp" %>
&nbsp;
<%@include file="method_returns/using_to_json.jsp" %>
&nbsp;
<%@include file="method_returns/using_to_xml.jsp" %>
&nbsp;
<%@include file="method_returns/return_redirect.jsp" %>
&nbsp;
<%@include file="method_returns/throw_redirect.jsp" %>

<div style="height: 100rem"></div>

</body>
</html>
