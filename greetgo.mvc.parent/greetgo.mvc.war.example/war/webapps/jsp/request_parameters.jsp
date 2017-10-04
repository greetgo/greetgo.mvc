<%--suppress HtmlUnknownTarget --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="contextPath" value="${pageContext.request.getContextPath()}"/>

<!DOCTYPE html>
<html lang="en">
<head>
  <%@include file="_head_std.jsp" %>
  <title>Request Parameters: MVC Example - greetgo!</title>
  <%--suppress JSUnresolvedLibraryURL --%>
  <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
</head>
<body>

<%@include file="request_parameters/base_example.jsp" %>
&nbsp;
<%@include file="request_parameters/par_json_example.jsp" %>
&nbsp;
<%@include file="request_parameters/params_to_example.jsp" %>
&nbsp;
<%@include file="request_parameters/par_path_example.jsp" %>
&nbsp;
<%@include file="request_parameters/par_session_example.jsp" %>

<div style="height: 100rem"></div>

</body>
</html>
