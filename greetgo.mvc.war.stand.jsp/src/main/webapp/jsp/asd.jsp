<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<html>
<body>
Hello from asd.jsp asdName = ${attrObject.hi}

<c:forEach var="hiElement" items="${hiList}">
    <div>
        sending ${hiElement.hi}
    </div>
</c:forEach>

</body>
</html>
