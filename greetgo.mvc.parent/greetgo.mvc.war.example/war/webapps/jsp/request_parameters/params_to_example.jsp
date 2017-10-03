<%--suppress HtmlFormInputWithoutLabel --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%--suppress CssUnusedSymbol --%>
<style>
  #params-to-example .err {
    border: 1px solid red;
  }
</style>

<div id="params-to-example" class="example-container">
  <h3 class="title">@ParamsTo Example</h3>
  <table>
    <tbody>
    <tr>
      <td>id</td>
      <td>=</td>
      <td><input type="text" class="id" value="5426675"></td>
      <td>use</td>
      <td><input type="checkbox" class="id-use" checked></td>
    </tr>
    <tr>
      <td>name</td>
      <td>=</td>
      <td><input type="text" class="name" value="John"></td>
      <td>use</td>
      <td><input type="checkbox" class="name-use" checked></td>
    </tr>
    <tr>
      <td>amount</td>
      <td>=</td>
      <td><input type="text" class="amount" value="180 000.34">&#8376;</td>
      <td>use</td>
      <td><input type="checkbox" class="amount-use"></td>
    </tr>
    <tr>
      <td>address</td>
      <td>=</td>
      <td><input type="text" class="address1" value="New York, Stone st., 27"></td>
      <td>use</td>
      <td><input type="checkbox" class="address1-use" checked></td>
    </tr>
    <tr>
      <td>address</td>
      <td>=</td>
      <td><input type="text" class="address2" value="London, Baker st., 221"></td>
      <td>use</td>
      <td><input type="checkbox" class="address2-use" checked></td>
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

    var requestUriBase = "${contextPath}/api/request_parameters/params-to-example";

    var self = $("#params-to-example");

    var id = self.find(".id");
    var idUse = self.find(".id-use");

    var name = self.find(".name");
    var nameUse = self.find(".name-use");

    var amount = self.find(".amount");
    var amountUse = self.find(".amount-use");

    var address1 = self.find(".address1");
    var address1Use = self.find(".address1-use");
    var address2 = self.find(".address2");
    var address2Use = self.find(".address2-use");

    var uri = self.find(".uri");

    var callButton = self.find(".call-button");
    var resultContainer = self.find(".resultContainer");
    var resultBody = self.find(".resultBody");

    var resultCode = self.find(".resultCode");


    var requestUri = function (html) {
      var pars = [];

      if (idUse.is(':checked')) {
        pars.push("id=" + encodeURIComponent(id.val()));
      }

      if (nameUse.is(':checked')) {
        pars.push("name=" + encodeURIComponent(name.val()));
      }

      if (amountUse.is(':checked')) {
        pars.push("amount=" + encodeURIComponent(amount.val()));
      }

      if (address1Use.is(':checked')) {
        pars.push("addresses=" + encodeURIComponent(address1.val()));
      }
      if (address2Use.is(':checked')) {
        pars.push("addresses=" + encodeURIComponent(address2.val()));
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
      uri.html(requestUri(true));
    };

    inputChanged();

    id.on('keyup', inputChanged);
    idUse.on('change', inputChanged);

    name.on('keyup', inputChanged);
    nameUse.on('change', inputChanged);

    amount.on('keyup', inputChanged);
    amountUse.on('change', inputChanged);

    address1.on('keyup', inputChanged);
    address2.on('keyup', inputChanged);
    address1Use.on('change', inputChanged);
    address2Use.on('change', inputChanged);

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
