<%--suppress HtmlFormInputWithoutLabel --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--suppress CssUnusedSymbol --%>
<style>

  #par-json-example .err {
    border: 1px solid red;
  }

  #par-json-example .errMsg {
    color: red;
  }

  #par-json-example .area {
    width: 20rem;
    height: 6rem;
  }
</style>

<div id="par-json-example" class="example-container">
  <h3 class="title">@Par @Json Example</h3>
  <table>
    <tbody>

    <tr>
      <td>clientToSave</td>
      <td>=</td>
      <td>
<textarea class="clientToSave area">{
  "id"      : "ahj5y4b6",
  "surname" : "Stone",
  "name"    : "John"
}</textarea>
      </td>
      <td>use</td>
      <td><input type="checkbox" class="clientToSave-use" checked></td>
    </tr>

    <tr>
      <td>accountToSave</td>
      <td>=</td>
      <td>
<textarea class="accountToSave area">{
  "number" : "AS-4326786543-1990",
  "amount" : "123000",
  "typeId" : "325445"
}</textarea>
      </td>
      <td>use</td>
      <td><input type="checkbox" class="accountToSave-use"></td>
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

    var requestUriBase = "${contextPath}/api/request_parameters/par-json-example";

    var self = $("#par-json-example");

    var clientToSave = self.find(".clientToSave");
    var clientToSaveUse = self.find(".clientToSave-use");

    var accountToSave = self.find(".accountToSave");
    var accountToSaveUse = self.find(".accountToSave-use");

    var uri = self.find(".uri");

    var callButton = self.find(".call-button");
    var resultContainer = self.find(".resultContainer");
    var resultBody = self.find(".resultBody");
    var resultCode = self.find(".resultCode");

    var requestUri = function (html) {
      var pars = [];

      if (clientToSaveUse.is(':checked')) {

        var clientToSaveObject;

        try {

          clientToSaveObject = JSON.parse(clientToSave.val());

        } catch (e) {
          console.error(e);
          if (html) return "clientToSave : <span class='errMsg'>ERROR " + e + "</span>";
          throw e;
        }

        console.log("clientToSaveObject = ", clientToSaveObject);

        pars.push("clientToSave=" + encodeURIComponent(JSON.stringify(clientToSaveObject)));
      }

      if (accountToSaveUse.is(':checked')) {
        var accountToSaveObject;
        try {
          accountToSaveObject = JSON.parse(accountToSave.val());
        } catch (e) {
          console.error(e);
          if (html) return "accountToSave : <span class='errMsg'>ERROR " + e + "</span>";
          throw e;
        }

        console.log("accountToSaveObject = ", accountToSaveObject);

        pars.push("accountToSave=" + encodeURIComponent(JSON.stringify(accountToSaveObject)));
      }

      if (html) {
        if (pars.length === 0) return '<b>' + requestUriBase + '</b>';
        return '<b>' + requestUriBase + "</b>?" + pars.join("&amp;");
      } else {
        if (pars.length === 0) return requestUriBase;
        return requestUriBase + "?" + pars.join("&");
      }
    };

    var inputChanged = function () {
      callButton.prop('disabled', false);
      uri.html(requestUri(true));
    };

    inputChanged();

    clientToSave.on('keyup', inputChanged);
    clientToSaveUse.on('change', inputChanged);

    accountToSave.on('keyup', inputChanged);
    accountToSaveUse.on('change', inputChanged);

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
