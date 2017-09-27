<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="req" value="${pageContext.request}"/>
<c:set var="url">${req.requestURL}</c:set>
<c:set var="uri" value="${req.requestURI}"/>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Request Parameters: MVC Example - greetgo!</title>
  <%--suppress JSUnresolvedLibraryURL --%>
  <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
</head>
<body>

<h1 id="base-example">Base Example</h1>
<div>
  <button>
    Call <b>GET </b>
  </button>

  <br> req = ${req}
  <br> url = ${url}
  <br> uri = ${uri}
</div>

</body>
</html>