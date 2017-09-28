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
    <tr>
      <td>weather</td>
      <td>=</td>
      <td>
        <select class="weather">
          <option value="0">SUNNY</option>
          <option value="1" selected>CLOUDY</option>
          <option value="2">RAINY</option>
          <option value="3">HOT</option>
        </select>
      </td>
      <td colspan="2" class="radio_group">
        <input type="radio" name="use" class="weather-use-ordinal" id="n543b654n7b43v5" checked>
        <label for="n543b654n7b43v5">Use Ordinal</label>
        <input type="radio" name="use" class="weather-use-name" id="a234nj4n56j7">
        <label for="a234nj4n56j7">Use Name</label>
        <input type="radio" name="use" class="weather-not-use" id="a5bh4j35bh5bj25h">
        <label for="a5bh4j35bh5bj25h">Not Use</label>
      </td>
    </tr>
    <tr>
      <td>happenedAt</td>
      <td>=</td>
      <td><input type="text" class="happenedAt" value="1991-01-23 23:45:11"></td>
      <td>use</td>
      <td><input type="checkbox" class="happenedAt-use"></td>
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

    var weather = self.find(".weather");
    var weatherUseName = self.find(".weather-use-name");
    var weatherUseOrdinal = self.find(".weather-use-ordinal");
    var weatherNotUse = self.find(".weather-not-use");

    var happenedAt = self.find(".happenedAt");
    var happenedAtUse = self.find(".happenedAt-use");

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

      if (weatherUseOrdinal.is(':checked')) {
        pars.push("weather=" + encodeURIComponent(weather.val()));
      } else if (weatherUseName.is(':checked')) {
        pars.push("weather=" + encodeURIComponent(weather.find("option:selected").text()));
      }

      if (happenedAtUse.is(':checked')) {
        pars.push("happenedAt=" + encodeURIComponent(happenedAt.val()));
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

    weather.on('change', inputChanged);
    weatherUseName.on('change', inputChanged);
    weatherUseOrdinal.on('change', inputChanged);
    weatherNotUse.on('change', inputChanged);

    happenedAt.on('keyup', inputChanged);
    happenedAtUse.on('change', inputChanged);

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
