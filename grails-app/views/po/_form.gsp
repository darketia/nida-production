<g:set var="poInstance" value="${(nida.production.Po) poInstance}"/>
<g:set var="authPoTypes" value="${nida.production.SecUserPoType.findAllBySecUser(currentUser)?.poType?.flatten()}"/>
<g:set var="cmd" value="${(nida.production.PoCmd) cmd}"/>
<style>
#poDetailTable td {
  vertical-align: top !important;
}

#poDetailTable .labelTd {
  padding-top: 15px;
}
</style>
<script>
  function confirmLink() {
    $('a:not([href$="#"])').click(function () {
      return confirm('ยืนยันการเปลี่ยนไปหน้าอื่น ข้อมูลที่กรอกจะไม่ได้รับการบันทึก!')
    })
  }

  var noSelection = {value: '', text: '${message(code:'default.select.label')}'}
  var detailIndex = 0
  var returnDetailIndex = 0
  $(document).ready(function () {
    confirmLink()

    detailIndex = parseInt('${cmd.poDetails?.size()?:0}')
    sumQty()

    if (detailIndex == 0) addDetail();
    changeEnableScale();
  })

  function resetDetail() {
    $("#detailTbody").html('')
    detailIndex = 0
    sumQty()
  }

  function addDetail() {
    var productIndex = -1
    setTimeout(function () {$("#addDetailTbody input").val('')}, 200)

    $("#detailTbody input[id^=detail-product-id-]").each(function () {
      if ($(this).val() == data.productId) {
        var temp = $(this).attr('id').split('-')
        productIndex = temp[temp.length - 1]
      }
    })

    if (productIndex >= 0) {
      $("#detail-qty-" + productIndex).val(parseFloat($("#detail-qty-" + productIndex).val()) + 1)
      calPrice($("#detail-qty-" + productIndex))
      return
    }

    $.ajax({
      url: "${createLink(action: 'ajaxAddDetail')}",
      type: "POST", dataType: "text",
      data: {i: detailIndex},
      async: true,
      success: function (data) {
        $("#detailTbody").append(data);
        detailIndex = $("#detailTbody tr").size()
        sumQty()
        changeEnableScale()
      },
      error: function (data) {
        if (data.status == 400) alert(data.responseText)
        else alert("HTTP Status " + data.status + " - " + data.statusText)
      }
    })
  }

  function deleteDetail(obj) {
    if (confirm("${message(code:'default.button.delete.confirm.message')}")) {
      var row = $(obj).closest('tr');
      row.remove();

      sumQty()

      detailIndex--
      //reIndex!
      var count = 0
      $("#detailTbody tr").each(function () {
        $(this).find('input[id^=detail-id-]').each(function () {
          $(this).attr('id', 'detail-id-' + count)
          $(this).attr('name', 'poDetails[' + count + '].id')
        })

        $(this).find('textarea[id^=detail-name-]').each(function () {
          $(this).attr('id', 'detail-name-' + count)
          $(this).attr('name', 'poDetails[' + count + '].name')
        })

        $(this).find('input[id^=detail-pricePerUnit-]').each(function () {
          $(this).attr('id', 'detail-pricePerUnit-' + count)
          $(this).attr('name', 'poDetails[' + count + '].pricePerUnit')
        })

        $(this).find('td[id^=detail-ord-]').each(function () {
          $(this).attr('id', 'detail-ord-' + count)
          $(this).text((
              count + 1
          ) + ".")
        })

        $(this).find('input[id^=detail-packSize-]').each(function () {
          $(this).attr('id', 'detail-packSize-' + count)
          $(this).attr('name', 'poDetails[' + count + '].packSize')
        })
        $(this).find('input[id^=detail-packSizeQty-]').each(function () {
          $(this).attr('id', 'detail-packSizeQty-' + count)
          $(this).attr('name', 'poDetails[' + count + '].packSizeQty')
        })

        $(this).find('input[id^=detail-qty-]').each(function () {
          $(this).attr('id', 'detail-qty-' + count)
          $(this).attr('name', 'poDetails[' + count + '].qty')
        })

        $(this).find('input[id^=detail-uom-]').each(function () {
          $(this).attr('id', 'detail-uom-' + count)
          $(this).attr('name', 'poDetails[' + count + '].uom')
        })

        $(this).find('span[id^=detail-price-]').each(function () {
          $(this).attr('id', 'detail-price-' + count)
        })

        count++
      })
    }
  }

  function calQty(obj) {
    var row = $(obj).closest('tr');
    var packSize = $(row).find("input[id^=detail-packSize-]").val()
    var packSizeQty = $(row).find("input[id^=detail-packSizeQty-]").val()
    var qty = 0
    if (!isNaN(packSize) && packSize != '' && !isNaN(packSizeQty) && packSizeQty != '') {
      qty = packSize * packSizeQty
      $(row).find("input[id^=detail-qty-]").val(qty);
      calPrice(obj)
    }
  }

  function changeEnableScale(){
    var isEnableScale = $("#enableScale").prop("checked");
    if (isEnableScale){
      $("input[id^=detail-packSize-]").prop("step","0.01")
      $("input[id^=detail-qty-]").prop("step","0.01")
    }else{
      $("input[id^=detail-packSize-]").prop("step","1")
      $("input[id^=detail-qty-]").prop("step","1")
    }
  }

  function changeQty(obj) {
    var row = $(obj).closest('tr');
    $(row).find("input[id^=detail-packSize-]").val("")
    $(row).find("input[id^=detail-packSizeQty-]").val("")
    calPrice(obj)
  }

  function calPrice(obj) {
    var row = $(obj).closest('tr');
    console.log("row", row)
    var pricePerUnit = $(row).find("input[id^=detail-pricePerUnit-]").val()
    var qty = $(row).find("input[id^=detail-qty-]").val()
    console.log("pricePerUnit-qty", pricePerUnit, qty)
    var price = 0
    if (!isNaN(pricePerUnit) && !isNaN(qty)) {
      price = pricePerUnit * qty
    }
    $(row).find("span[id^=detail-price-]").text(price);
    $(row).find("span[id^=detail-price-]").number(true, 2)
    sumQty()
  }

  function sumQty() {
    var sum = 0
    $("#detailTbody span[id^=detail-price-]").each(function () {
      var temp = parseFloat($(this).text().replace(/,/g, ''))
      if (!isNaN(temp)) sum += temp
    })
    if (isNaN(sum)) sum = 0
    $("#detail-price-sum").text(sum);
    $("#detail-price-sum").number(true, 2)

    sumQty2()
  }

  function sumQty2() {
    var sum = parseFloat($("#detail-price-sum").text().replace(/,/g, ''))
    if (isNaN(sum)) sum = 0
    console.log("sumQty2 sum", sum)
    var discountAmount = parseFloat($("#discountAmount").val())
    if (isNaN(discountAmount)) discountAmount = 0
    console.log("sumQty2 discountAmount", discountAmount)
    var vatRate = parseFloat($("#vatRate").val())
    if (isNaN(vatRate)) vatRate = 0
    console.log("sumQty2 vatRate", vatRate)
    sum -= discountAmount
    var vat = sum * vatRate / 100
    var sum2 = sum + vat
    $("#detail-vat").text(vat)
    $("#detail-vat").number(true, 2)
    $("#detail-price-sum2").text(sum2)
    $("#detail-price-sum2").number(true, 2)
    console.log("sumQty2", vat, sum2)
    if (vat < 0) $("#detail-vat").text("- " + $("#detail-vat").text())
    if (sum2 < 0) $("#detail-price-sum2").text("- " + $("#detail-price-sum2").text())
  }

  function validate() {
    if (detailIndex < 1) {
      alert('กรุณาระบุสินค้า')
      return false
    }

    if ($("#receiveAmount").val().length == 0) {
      alert('กรุณาระบุรับเงิน')
      return false
    }

    return confirm('ยืนยัน')
  }

</script>

<div class="form-group">
  <label class="show-label col-sm-3">
    <g:message code="po.code.label"/>
  </label>

  <div class="col-sm-4">
    ${cmd?.code ?: "ระบบสร้างให้"}
  </div>
</div>

<div class="form-group">
  <label class="show-label col-sm-3">
    <g:message code="po.date.label" default="Date"/>
  </label>

  <div class="col-sm-4">
    <g:formatDate date="${cmd?.date}"/>
  </div>
</div>

<div class="form-group ${hasErrors(bean: poInstance, field: 'priceType', 'error')} required">
  <label for="poType.id" class="control-label col-sm-3">
    <g:message code="po.poType.label" default="Price Type"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:select name="poType.id" from="${authPoTypes ? authPoTypes.sort { it.code } : nida.production.PoType.list().sort { it.code }}"
              noSelection="['': message(code: 'default.select.label')]" required=""
              optionKey="id" value="${cmd?.poType?.id}" class="form-control"/>
  </div>
</div>

<div class="form-group ${hasErrors(bean: poInstance, field: 'enableScale', 'error')}">
  <label for="enableScale" class="control-label col-sm-3">
    <g:message code="po.enableScale.label" default="enableScale"/>
  </label>

  <div class="col-sm-4" style="margin-top: 5px">
    <g:checkBox name="enableScale" value="${cmd?.enableScale}" onchange="changeEnableScale()"/>
  </div>
</div>


<div class="form-group ${hasErrors(bean: poInstance, field: 'customer', 'error')} required">
  <label for="vendor" class="control-label col-sm-3">
    <g:message code="po.vendor.label"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <ts:autoComplete name="vendor" action="searchVendor" value="${cmd?.vendor}" required=""/>
  </div>
</div>

<div class="form-group ${hasErrors(bean: poInstance, field: 'shipLocation', 'error')} required">
  <label for="shipLocation" class="control-label col-sm-3">
    <g:message code="po.shipLocation.label"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4" style="margin-top: 5px">
    <g:radioGroup name="shipLocation" value="${cmd?.shipLocation}"
                  values="${nida.production.ShipLocation.values()}"
                  labels="${nida.production.ShipLocation.values()}">
      <label style="font-weight: initial;text-align: initial;width: auto;margin-right: 10px">${it.radio} <g:message code="enum.shipLocation.${it.label}"/></label>
    </g:radioGroup>
  </div>
</div>

<div class="form-group ${hasErrors(bean: poInstance, field: 'deliveryDate', 'error')}">
  <label for="deliveryDate" class="control-label col-sm-3">
    <g:message code="po.deliveryDate.label"/>
  </label>

  <div class="col-sm-4">
    <g:textField name="deliveryDate" value="${poInstance.deliveryDate}" class="form-control"/>
  </div>
</div>

<div class="form-group ${hasErrors(bean: poInstance, field: 'paymentTerm', 'error')}">
  <label for="paymentTerm" class="control-label col-sm-3">
    <g:message code="po.paymentTerm.label"/>
  </label>

  <div class="col-sm-4">
    <g:textField name="paymentTerm" value="${poInstance.paymentTerm}" class="form-control"/>
  </div>
</div>


<g:if test="${poInstance.id}">
  <div class="form-group ${hasErrors(bean: poInstance, field: 'poStatus', 'error')} required">
    <label for="shipLocation" class="control-label col-sm-3">
      <g:message code="po.poStatus.label"/>
      <span class="required-indicator">*</span>
    </label>

    <div class="col-sm-4" style="margin-top: 5px">
      <g:radioGroup name="poStatus" value="${cmd?.poStatus}"
                    values="${nida.production.PoStatus.values()}"
                    labels="${nida.production.PoStatus.values()}">
        <label style="font-weight: initial;text-align: initial;width: auto;margin-right: 10px">${it.radio} <g:message code="enum.poStatus.${it.label}"/></label>
      </g:radioGroup>
    </div>
  </div>
</g:if>


<div class="panel panel-success" style="padding: 0" id="productPanel">
  <div class="panel-heading"><g:message code="po.poDetails.label" default="poDetails"/></div>

  <div class="panel-body">
    <table class="table" id="poDetailTable">
      <thead>
      <tr>
        <th class="center" style="width:50px"><g:message code="default.ord.label"/></th>
        <th class="center" style=""><g:message code="poDetail.name.label"/></th>
        <th class="center" style="width:120px"><g:message code="poDetail.packSize.label"/></th>
        <th class="center" style="width:120px"><g:message code="poDetail.packSizeQty.label"/></th>
        <th class="center" style="width:120px"><g:message code="poDetail.qty.label"/></th>
        <th class="center" style="width:120px"><g:message code="poDetail.uom.label"/></th>
        <th class="center" style="width:250px"><g:message code="poDetail.pricePerUnit.label"/></th>
        <th class="center" style="width: 250px"><g:message code="poDetail.price.label"/></th>
        <th class="center"></th>
      </tr>
      </thead>
      <tbody id="detailTbody">
      <g:each in="${cmd?.poDetails.sort { it.id }}" var="poDetail" status="i">
        <g:render template="poDetail" model="[i: i, poDetail: poDetail]"/>
      </g:each>
      </tbody>
      <tbody id="addDetailTbody">
      <tr class="info">
        <td colspan="9" class="text-center">
          <div class="glyphicon glyphicon-plus btn btn-primary" style="width: 180px" onclick="addDetail()">เพิ่มสินค้า</div>
        </td>
      </tr>
      </tbody>
      <tfoot>
      <tr style="font-weight: bold">
        <td colspan="7" class="right">ราคารวมก่อนส่วนลด</td>
        <td class="right">
          <span id="detail-price-sum">0.00</span>
          <span>บาท</span>
        </td>
        <td></td>
      </tr>
      <tr>
        <td colspan="7" class="right">
          <g:message code="po.discountAmount.label" default="Discount Amount"/>
        </td>
        <td class="right">
          <div class="input-group" style="width: 250px;float: right">
            <g:field type="number" name="discountAmount" value="${poInstance.discountAmount}" required=""
                     class="form-control" style="text-align: right;" step="0.01" min="0.00" onchange="sumQty2()"/>
            <span class="input-group-addon">บาท</span>
          </div>
        </td>
        <td></td>
      </tr>
      <tr>
        <td colspan="7" class="right">
          <div class="input-group" style="width: 250px;float: right">
            <span class="input-group-addon">Vat</span>
            <g:field type="number" name="vatRate" value="${poInstance.vatRate}"
                     class="form-control" style="text-align: right;" step="1" min="0" max="100" onchange="sumQty2()"/>
            <span class="input-group-addon">%</span>
          </div>
        </td>
        <td class="right">
          <span id="detail-vat"></span>
          <span>บาท</span>
        </td>
        <td></td>
      </tr>

      <tr style="font-weight: bold">
        <td colspan="7" class="right">ราคารวมสุทธิ</td>
        <td class="right">
          <span id="detail-price-sum2"></span>
          <span>บาท</span>
        </td>
        <td></td>
      </tr>
      </tfoot>

    </table>
  </div>
</div>

<div class="form-group ${hasErrors(bean: poInstance, field: 'remark', 'error')} ">
  <label for="remark" class="control-label col-sm-3">
    <g:message code="po.remark.label" default="Remark"/>
  </label>

  <div class="col-sm-5">
    <g:textArea class="form-control" rows="5" name="remark" value="${cmd?.remark}"></g:textArea>
  </div>
</div>
