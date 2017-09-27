<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="contextPath" value="${pageContext.request.getContextPath()}"/>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Request Parameters: MVC Example - greetgo!</title>
  <style>
    h3 {
      margin-top: 0;
      margin-bottom: 0.8rem;
    }
  </style>
  <%--suppress JSUnresolvedLibraryURL --%>
  <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
</head>
<body>

<%@include file="request_parameters/base_example.jsp" %>

</body>
</html>
