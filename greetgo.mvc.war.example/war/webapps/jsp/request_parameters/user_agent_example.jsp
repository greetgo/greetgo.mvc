<%--suppress HtmlFormInputWithoutLabel --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%--suppress CssUnusedSymbol --%>
<style>
  #user-agent-example .err {
    border: 1px solid red;
  }
</style>

<div id="user-agent-example" class="example-container">
  <h3 class="title">UserAgent Example</h3>
  <button class="call-button">
    Request <b>GET </b><span class="uri"></span>
  </button>
  <div class="resultContainer" style="display: none">
    <p>Result code: <span class="resultCode"></span></p>
    <pre class="resultBody"></pre>
  </div>

  <%--suppress JSUnresolvedFunction --%>
  <script>(function () {

    var requestUriBase = "${contextPath}/api/request_parameters/user-agent-example";

    var self = $("#user-agent-example");

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
