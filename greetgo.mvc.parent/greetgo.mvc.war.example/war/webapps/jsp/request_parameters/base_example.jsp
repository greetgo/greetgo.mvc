<%--suppress HtmlFormInputWithoutLabel --%>

<style>
  #base-example {
    border: 3px solid #5c56ff;
    padding: 1rem;
  }

  #base-example .resultBody {
    border: 1px dashed #18d52d;
  }

  #base-example .err {
    border: 1px solid red;
  }

  #base-example .call-button {
    padding: 0.1rem;
    margin-top: 1rem;
    margin-bottom: 1rem;
  }
</style>

<div id="base-example">
  <h3 class="title">Base Example</h3>
  <table>
    <tbody>
    <tr>
      <td>helloMessage</td>
      <td>=</td>
      <td><input type="text" class="helloMessage" value="Hello World!!!"></td>
      <td>use</td>
      <td><input type="checkbox" class="helloMessage-use" checked></td>
    </tr>
    <tr>
      <td>age</td>
      <td>=</td>
      <td><input type="text" class="age" value="18"></td>
      <td>use</td>
      <td><input type="checkbox" class="age-use"></td>
    </tr>
    <tr>
      <td>amount</td>
      <td>=</td>
      <td><input type="text" class="amount" value="180 000.34">&#8376;</td>
      <td>use</td>
      <td><input type="checkbox" class="amount-use"></td>
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

  <script>(function () {

    var requestUriBase = "${contextPath}/api/request_parameters/base-example";

    var self = $("#base-example");

    var helloMessage = self.find(".helloMessage");
    var helloMessageUse = self.find(".helloMessage-use");

    var age = self.find(".age");
    var ageUse = self.find(".age-use");

    var amount = self.find(".amount");
    var amountUse = self.find(".amount-use");

    var uri = self.find(".uri");

    var callButton = self.find(".call-button");
    var resultContainer = self.find(".resultContainer");
    var resultBody = self.find(".resultBody");
    var resultCode = self.find(".resultCode");

    var requestUri = function (html) {
      var pars = [];
      if (helloMessageUse.is(':checked')) {
        pars.push("helloMessage=" + encodeURIComponent(helloMessage.val()));
      }
      if (ageUse.is(':checked')) {
        pars.push("age=" + encodeURIComponent(age.val()));
      }
      if (amountUse.is(':checked')) {
        pars.push("amount=" + encodeURIComponent(amount.val()));
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

    helloMessage.on('keyup', inputChanged);
    helloMessageUse.on('change', inputChanged);
    age.on('keyup', inputChanged);
    ageUse.on('change', inputChanged);
    amount.on('keyup', inputChanged);
    amountUse.on('change', inputChanged);

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
