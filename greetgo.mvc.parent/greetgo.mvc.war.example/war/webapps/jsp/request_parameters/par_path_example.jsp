<%--suppress HtmlFormInputWithoutLabel --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%--suppress CssUnusedSymbol --%>
<style>
  #par-path-example .err {
    border: 1px solid red;
  }
</style>

<div id="par-path-example" class="example-container">
  <h3 class="title">@ParPath Example</h3>
  <table>
    <tbody>
    <tr>
      <td>id</td>
      <td>=</td>
      <td><input type="text" class="id" value="5426675"></td>
    </tr>
    <tr>
      <td>name</td>
      <td>=</td>
      <td><input type="text" class="name" value="John Smith"></td>
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

  <%--suppress JSUnresolvedFunction --%>
  <script>(function () {

    var requestUriBase = "${contextPath}/api/request_parameters/par-path-example";

    var self = $("#par-path-example");

    var id = self.find(".id");

    var name = self.find(".name");

    var uri = self.find(".uri");

    var callButton = self.find(".call-button");
    var resultContainer = self.find(".resultContainer");
    var resultBody = self.find(".resultBody");

    var resultCode = self.find(".resultCode");


    var requestUri = function (html) {

      var ret = requestUriBase + "/id:" + id.val() + '/' + name.val();

      return html ? '<b>' + ret + '</b>' : ret;
    };

    var inputChanged = function () {
      uri.html(requestUri(true));
    };

    inputChanged();

    id.on('keyup', inputChanged);

    name.on('keyup', inputChanged);

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
