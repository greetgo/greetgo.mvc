<%--suppress HtmlFormInputWithoutLabel --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%--suppress CssUnusedSymbol --%>
<style>

  #throw-redirect .err {
    border: 1px solid red;
  }
</style>

<div id="throw-redirect" class="example-container">
  <h3 class="title">Throw Redirect</h3>
  <table>
    <tbody>
    <tr>
      <td>param</td>
      <td>=</td>
      <td>
        <select class="param">
          <option value="param1">param1</option>
          <option value="param2" selected>param2</option>
          <option value="RuntimeException">RuntimeException</option>
        </select>
      </td>
    </tr>
    </tbody>
  </table>
  <button class="call-button">
    Request <b>GET </b><span class="uri"></span>
  </button>
  <div class="resultContainer" style="display: none">
    <p>Result code: <span class="resultCode"></span></p>
    <pre class="resultBody"></pre>
  </div>

  <%--suppress JSUnresolvedFunction, JSUnresolvedVariable --%>
  <script>(function () {

    var requestUriBase = "${contextPath}/api/method_returns/throw-redirect";

    var self = $("#throw-redirect");

    var param = self.find(".param");

    var uri = self.find(".uri");

    var callButton = self.find(".call-button");
    var resultContainer = self.find(".resultContainer");
    var resultBody = self.find(".resultBody");
    var resultCode = self.find(".resultCode");

    var requestUri = function (html) {
      var pars = [];
      pars.push("param=" + encodeURIComponent(param.val()));

      {
        var params = pars.length === 0 ? '' : (html ? "?" + pars.join("&amp;") : "?" + pars.join("&"));
        return html ? '<b>' + requestUriBase + '</b>' + params : requestUriBase + params;
      }
    };

    var inputChanged = function () {
      uri.html(requestUri(true));
    };

    inputChanged();

    param.on('change', inputChanged);

    callButton.on('click', function () {
      callButton.prop('disabled', true);
      resultCode.text('');
      resultBody.text('');
      resultBody.removeClass('err');
      resultContainer.hide();

      location.href = requestUri(false);
    });
  })();</script>
</div>
