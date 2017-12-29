<jsp:useBean id="SOME_ARGUMENT" scope="request" type="java.lang.String"/>
<jsp:useBean id="NAME_FROM_COOKIE" scope="request" type="java.lang.String"/>
<jsp:useBean id="ACT" scope="request" type="java.lang.String"/>
<%--suppress HtmlFormInputWithoutLabel --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div id="redirect_param1">
  <h3 class="title">${ACT} Redirect Param = 1</h3>
  <table>
    <tbody>
    <tr>
      <th colspan="2">
        Dollar parameter name
      </th>
      <th>
        Dollar parameter value
      </th>
    </tr>

    <tr>
      <td>
        \${SOME_ARGUMENT}
      </td>
      <td>=</td>
      <td>
        ${SOME_ARGUMENT}
      </td>
    </tr>

    <tr>
      <td>
        \${NAME_FROM_COOKIE}
      </td>
      <td>=</td>
      <td>
        ${NAME_FROM_COOKIE}
      </td>
    </tr>
    </tbody>
  </table>
</div>
