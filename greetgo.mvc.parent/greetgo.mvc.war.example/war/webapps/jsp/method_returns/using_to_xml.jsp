<%--suppress HtmlFormInputWithoutLabel --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%--suppress CssUnusedSymbol --%>
<style>

  #using-to-xml .err {
    border: 1px solid red;
  }
</style>

<div id="using-to-xml" class="example-container">
  <h3 class="title">Using @ToXml</h3>
  <button class="call-button">
    Request <b>GET </b><span class="uri"></span>
  </button>
  <div class="resultContainer" style="display: none">
    <p>Result code: <span class="resultCode"></span></p>
    <pre class="resultBody"></pre>
  </div>

  <%--suppress JSUnresolvedFunction, JSUnresolvedVariable --%>
  <script>(function () {

    var requestUriBase = "${contextPath}/api/method_returns/using-to-xml";

    var self = $("#using-to-xml");

    var uri = self.find(".uri");

    var callButton = self.find(".call-button");
    var resultContainer = self.find(".resultContainer");
    var resultBody = self.find(".resultBody");
    var resultCode = self.find(".resultCode");

    var requestUri = function (html) {
      return html ? '<b>' + requestUriBase + '</b>' : requestUriBase;
    };

    var inputChanged = function () {
      uri.html(requestUri(true));
    };

    inputChanged();

    callButton.on('click', function () {
      callButton.prop('disabled', true);
      resultCode.text('');
      resultBody.text('');
      resultBody.removeClass('err');
      resultContainer.hide();

      $.ajax({
        url     : requestUri(false),
        complete: function (xhr) {
          resultContainer.show();
          if (200 <= xhr.status && xhr.status < 300) {
            resultBody.removeClass('err');
          } else {
            resultBody.addClass('err');
          }
          callButton.prop('disabled', false);
          resultCode.text(xhr.status);
          resultBody.text(xhr.responseText);
        }
      });

    });
  })();</script>
</div>
