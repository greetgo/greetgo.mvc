<jsp:useBean id="param1" scope="request" type="java.lang.String"/>
<jsp:useBean id="param2" scope="request" type="java.lang.Long"/>
<%--suppress HtmlUnknownTarget --%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Asd PARAMS</title>
</head>
<body>
<h1>Params</h1>
<p>param1 = ${param1}</p>
<p>param2 = ${param2}</p>
</body>
</html>