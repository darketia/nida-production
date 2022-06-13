<%@ page import="nida.production.SaleOrderCmd; nida.production.PriceType; nida.production.SaleOrderCmd; nida.production.SaleOrder" %>
<g:set var="saleOrderInstance" value="${(SaleOrder) saleOrderInstance}"/>
<g:set var="cmd" value="${(nida.production.SaleOrderCmd) cmd}"/>
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

    detailIndex = parseInt('${cmd.saleOrderDetails?.size()?:0}')
    returnDetailIndex = parseInt('${cmd.saleOrderReturnDetails?.size()?:0}')
    sumQty()

    $("#barcode").keydown(function (event) {
      if (event.keyCode == 13) {
        event.preventDefault();
        addDetailByBarcode();
        return false;
      }
    });

    $("#returnBarcode").keydown(function (event) {
      if (event.keyCode == 13) {
        event.preventDefault();
        addReturnDetailByBarcode();
        return false;
      }
    });
  })

  function resetDetail() {
    $("#detailTbody").html('')
    detailIndex = 0
    sumQty()
  }

  function changeCustomer() {
    $.ajax({
      url: "${createLink(action: 'ajaxCustomerInfo')}",
      type: "POST", dataType: "json",
      data: {customerId: $("input[name='customer.id']").val()},
      async: true,
      success: function (data) {
        $("#name").val(data.name)
        $("#address").val(data.address)
        $("#telNo").val(data.telNo)
        $("#priceType").val(data.priceType)
        changePriceType()
      },
      error: function (data) {
        if (data.status == 400) alert(data.responseText)
        else alert("HTTP Status " + data.status + " - " + data.statusText)
      }
    })
  }

  function changePriceType() {
    if (detailIndex > 0 && $("#priceTypeOld").val() != $("#priceType").val()) {
      resetDetail()
    }
    $("#priceTypeOld").val($("#priceType").val())
  }

  function addDetailById() {
    if (!isNaN($("input[name='product.id']").val())) {
      addDetail({i: detailIndex, priceType: $('#priceType').val(), productId: $("input[name='product.id']").val()})
    }
  }

  function addDetailByBarcode() {
    if ($("#barcode").val().length > 0) {
      addDetail({i: detailIndex, priceType: $('#priceType').val(), barcode: $('#barcode').val()})
    }
  }

  function addDetail(data) {
    var productIndex = -1
    setTimeout(function () {$("#addDetailTbody input").val('')}, 200)

    if (data.productId) {
      $("#detailTbody input[id^=detail-product-id-]").each(function () {
        if ($(this).val() == data.productId) {
          var temp = $(this).attr('id').split('-')
          productIndex = temp[temp.length - 1]
        }
      })
    }
    if (data.barcode) {
      $("#detailTbody td[id^=detail-product-code-]").each(function () {
        if ($(this).text().trim() == data.barcode) {
          var temp = $(this).attr('id').split('-')
          productIndex = temp[temp.length - 1]
        }
      })
    }
    if (productIndex >= 0) {
      $("#detail-qty-" + productIndex).val(parseFloat($("#detail-qty-" + productIndex).val()) + 1)
      calPrice('detail', productIndex)
      return
    }

    $.ajax({
      url: "${createLink(action: 'ajaxAddDetail')}",
      type: "POST", dataType: "text",
      data: data,
      async: true,
      success: function (data) {
        $("#detailTbody").append(data);
        detailIndex = $("#detailTbody tr").size()
        sumQty()
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
          $(this).attr('name', 'saleOrderDetails[' + count + '].id')
        })

        $(this).find('input[id^=detail-product-id-]').each(function () {
          $(this).attr('id', 'detail-product-id-' + count)
          $(this).attr('name', 'saleOrderDetails[' + count + '].product.id')
        })

        $(this).find('input[id^=detail-pricePerUnit-]').each(function () {
          $(this).attr('id', 'detail-pricePerUnit-' + count)
          $(this).attr('name', 'saleOrderDetails[' + count + '].pricePerUnit')
        })

        $(this).find('td[id^=detail-ord-]').each(function () {
          $(this).attr('id', 'detail-ord-' + count)
          $(this).text((
                  count + 1
              ) + ".")
        })

        $(this).find('td[id^=detail-product-code-]').each(function () {
          $(this).attr('id', 'detail-product-code-' + count)
        })

        $(this).find('input[id^=detail-qty-]').each(function () {
          $(this).attr('id', 'detail-qty-' + count)
          $(this).attr('name', 'saleOrderDetails[' + count + '].qty')
        })

        $(this).find('span[id^=detail-price-]').each(function () {
          $(this).attr('id', 'detail-price-' + count)
        })

        count++
      })
    }
  }

  function calPrice(prefix, i) {
    var pricePerUnit = $("#" + prefix + "-pricePerUnit-" + i).val()
    var qty = $("#" + prefix + "-qty-" + i).val()
    var price = 0
    if (!isNaN(pricePerUnit) && !isNaN(qty)) {
      price = pricePerUnit * qty
    }
    $("#" + prefix + "-price-" + i).text(price);
    $("#" + prefix + "-price-" + i).number(true, 2)
    sumQty()
  }

  function sumQty() {
    var sum = 0
    $("#detailTbody span[id^=detail-price-]").each(function () {
      var temp = parseFloat($(this).text().replace(',', ''))
      if (!isNaN(temp)) sum += temp
    })
    if (isNaN(sum)) sum = 0
    $("#detail-price-sum").text(sum);
    $("#detail-price-sum").number(true, 2)

    var returnSum = 0
    $("#returnDetailTbody span[id^=returnDetail-price-]").each(function () {
      var temp = parseFloat($(this).text().replace(',', ''))
      if (!isNaN(temp)) returnSum += temp
    })
    if (isNaN(returnSum)) returnSum = 0
    $("#returnDetail-price-sum").text(returnSum);
    $("#returnDetail-price-sum").number(true, 2)


    sumQty2()
  }

  function sumQty2() {
    var sum = parseFloat($("#detail-price-sum").text().replace(',', ''))
    if (isNaN(sum)) sum = 0
    var returnSum = parseFloat($("#returnDetail-price-sum").text().replace(',', ''))
    if (isNaN(returnSum)) returnSum = 0
    var deliveryPrice = parseFloat($("#deliveryPrice").val())
    if (isNaN(deliveryPrice)) deliveryPrice = 0
    var discountAmount = parseFloat($("#discountAmount").val())
    if (isNaN(discountAmount)) discountAmount = 0
    var sum2 = sum - returnSum + deliveryPrice - discountAmount
    $("#detail-price-sum2").text(sum2)
    $("#detail-price-sum2").number(true, 2)
    if (sum2 < 0) $("#detail-price-sum2").text("- " + $("#detail-price-sum2").text())

    calcChangeAmount()
  }

  function calcChangeAmount(){
    var sum2 = parseFloat($("#detail-price-sum2").text().replace(',', ''))
    if (isNaN(sum2)) sum2 = 0
    var receiveAmount = parseFloat($("#receiveAmount").val())
    if (isNaN(receiveAmount)) receiveAmount = 0
    var changeAmount = receiveAmount - sum2
    $("#changeAmount").text(changeAmount)
    $("#changeAmount").number(true, 2)
    if (changeAmount < 0) $("#changeAmount").text("- " + $("#changeAmount").text())
    console.log(receiveAmount+" - "+sum2+" = "+sum2)
    return changeAmount
  }

  function changeSubDistrict() {
    ajaxDDL("${createLink(action: 'ajaxHousingEstates')}"
        , {subDistrictId: $("#subDistrict").val()}
        , $("#deliveryHousingEstate")
        , noSelection, changeDeliveryHousingEstate)
  }
  function changeDeliveryHousingEstate() {
    $.ajax({
      url: "${createLink(action: 'ajaxDeliveryPrice')}",
      type: "POST", dataType: "text",
      data: {housingEstateId: $("#deliveryHousingEstate").val()},
      async: true,
      success: function (data) {
        $("#deliveryPricePerTrip").val(data)
        calcDeliveryPrice()
      },
      error: function (data) {
        if (data.status == 400) alert(data.responseText)
        else alert("HTTP Status " + data.status + " - " + data.statusText)
      }
    })
  }

  function changeDeliveryTrip() {
    calcDeliveryPrice()
  }

  function calcDeliveryPrice() {
    var deliveryPricePerTrip = $("#deliveryPricePerTrip").val()
    var deliveryTrip = $("#deliveryTrip").val()
    var deliveryPrice = deliveryPricePerTrip * deliveryTrip
    $("#deliveryPrice").val(deliveryPrice)
    $("#deliveryPriceSpan").text(deliveryPrice)
    $("#deliveryPriceSpan").number(true, 2)

    sumQty2()
  }


  function changeDiscountAmount() {
    sumQty2()
  }

  function changeReceiveAmount(){
    calcChangeAmount()
  }

  function validate() {
    if (detailIndex < 1) {
      alert('กรุณาระบุสินค้า')
      return false
    }

    if ($("#receiveAmount").val().length==0) {
      alert('กรุณาระบุรับเงิน')
      return false
    }

    /*var changeAmount = calcChangeAmount()
     if (changeAmount < 0) {
     alert('เงินทอน ติดลบ')
     return false
     }*/

    return confirm('ยืนยัน')
  }

  //returnDetail
  function addReturnDetailById() {
    if (!isNaN($("input[name='returnProduct.id']").val())) {
      addReturnDetail({
        i: returnDetailIndex,
        refReturnCode: $("#refReturnCode").val(),
        productId: $("input[name='returnProduct.id']").val()
      })
    }
  }

  function addReturnDetailByBarcode() {
    if ($("#returnBarcode").val().length > 0) {
      addReturnDetail({
        i: returnDetailIndex,
        refReturnCode: $("#refReturnCode").val(),
        barcode: $('#returnBarcode').val()
      })
    }
  }

  function addReturnDetail(data) {
    var productIndex = -1
    setTimeout(function () {$("#addReturnDetailTbody .otp input").val('')}, 200)

    if (data.refReturnCode.length == 0) {
      alert('กรุณระบุ รหัสเอกสาร')
      return
    }
    if (data.refReturnCode.length > 0 && data.refReturnCode == '${cmd?.code}') {
      alert('ไม่สามารถคืนสินค้า ในประวัติการขายเดียวกันได้')
      return
    }

    if (data.productId) {
      $("#returnDetailTbody input[id^=returnDetail-product-id-]").each(function () {
        if ($(this).val() == data.productId && $(this).parent().find("input[id^=returnDetail-refReturnCode-]").val() == data.refReturnCode) {
          var temp = $(this).attr('id').split('-')
          productIndex = temp[temp.length - 1]
        }
      })
    }
    if (data.barcode) {
      $("#returnDetailTbody td[id^=returnDetail-product-code-]").each(function () {
        if ($(this).text().trim() == data.barcode && $(this).parent().find("input[id^=returnDetail-refReturnCode-]").val() == data.refReturnCode) {
          var temp = $(this).attr('id').split('-')
          productIndex = temp[temp.length - 1]
        }
      })
    }
    if (productIndex >= 0) {
      $("#returnDetail-qty-" + productIndex).val(parseFloat($("#returnDetail-qty-" + productIndex).val()) + 1)
      calPrice('returnDetail', productIndex)
      return
    }

    $.ajax({
      url: "${createLink(action: 'ajaxAddReturnDetail')}",
      type: "POST", dataType: "text",
      data: data,
      async: true,
      success: function (data) {
        $("#returnDetailTbody").append(data);
        returnDetailIndex = $("#returnDetailTbody tr").size()
        sumQty()
      },
      error: function (data) {
        if (data.status == 400) alert(data.responseText)
        else alert("HTTP Status " + data.status + " - " + data.statusText)
      }
    })
  }
  function deleteReturnDetail(obj) {
    if (confirm("${message(code:'default.button.delete.confirm.message')}")) {
      var row = $(obj).closest('tr');
      row.remove();

      sumQty()

      returnDetailIndex--
      //reIndex!
      var count = 0
      $("#returnDetailTbody tr").each(function () {
        $(this).find('input[id^=returnDetail-id-]').each(function () {
          $(this).attr('id', 'returnDetail-id-' + count)
          $(this).attr('name', 'saleOrderReturnDetails[' + count + '].id')
        })

        $(this).find('input[id^=returnDetail-refReturnCode-]').each(function () {
          $(this).attr('id', 'returnDetail-refReturnCode-' + count)
          $(this).attr('name', 'saleOrderReturnDetails[' + count + '].refReturnCode')
        })

        $(this).find('input[id^=returnDetail-product-id-]').each(function () {
          $(this).attr('id', 'returnDetail-product-id-' + count)
          $(this).attr('name', 'saleOrderReturnDetails[' + count + '].product.id')
        })

        $(this).find('input[id^=returnDetail-pricePerUnit-]').each(function () {
          $(this).attr('id', 'returnDetail-pricePerUnit-' + count)
          $(this).attr('name', 'saleOrderReturnDetails[' + count + '].pricePerUnit')
        })

        $(this).find('td[id^=returnDetail-ord-]').each(function () {
          $(this).attr('id', 'returnDetail-ord-' + count)
          $(this).text((
                  count + 1
              ) + ".")
        })

        $(this).find('td[id^=returnDetail-product-code-]').each(function () {
          $(this).attr('id', 'returnDetail-product-code-' + count)
        })

        $(this).find('input[id^=returnDetail-qty-]').each(function () {
          $(this).attr('id', 'returnDetail-qty-' + count)
          $(this).attr('name', 'saleOrderReturnDetails[' + count + '].qty')
        })

        $(this).find('span[id^=returnDetail-price-]').each(function () {
          $(this).attr('id', 'returnDetail-price-' + count)
        })

        count++
      })
    }
  }

</script>

<div class="form-group">
  <label class="show-label col-sm-3">
    <g:message code="saleOrder.code.label"/>
  </label>

  <div class="col-sm-4">
    ${cmd?.code ?: "ระบบสร้างให้"}
  </div>
</div>

<div class="form-group">
  <label class="show-label col-sm-3">
    <g:message code="saleOrder.date.label" default="Date"/>
  </label>

  <div class="col-sm-4">
    <g:formatDate date="${cmd?.date}"/>
  </div>
</div>

<div class="form-group ${hasErrors(bean: saleOrderInstance, field: 'customer', 'error')} required">
  <label for="customer" class="control-label col-sm-3">
    <g:message code="saleOrder.customer.label" default="Customer"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <ts:autoComplete name="customer" action="searchCustomer" value="${cmd?.customer}" required="" callback="changeCustomer"/>
  </div>
</div>

<div class="panel panel-success" style="padding: 0" id="customerPanel">
  <div class="panel-heading"><g:message code="customer.label" default="customer"/></div>

  <div class="panel-body">
    <div class="form-group ${hasErrors(bean: saleOrderInstance, field: 'name', 'error')} required">
      <label for="name" class="control-label col-sm-3">
        <g:message code="saleOrder.name.label" default="name"/>
        <span class="required-indicator">*</span>
      </label>

      <div class="col-sm-4">
        <g:textField name="name" required="" value="${saleOrderInstance?.name}" class="form-control"/>
      </div>
    </div>

    <div class="form-group ${hasErrors(bean: saleOrderInstance, field: 'address', 'error')} ">
      <label for="address" class="control-label col-sm-3">
        <g:message code="saleOrder.address.label" default="Address"/>
      </label>

      <div class="col-sm-4">
        <g:textField name="address" value="${saleOrderInstance?.address}" class="form-control"/>
      </div>
    </div>

    <div class="form-group ${hasErrors(bean: saleOrderInstance, field: 'telNo', 'error')} ">
      <label for="telNo" class="control-label col-sm-3">
        <g:message code="saleOrder.telNo.label" default="telNo"/>
      </label>

      <div class="col-sm-4">
        <g:textField name="telNo" value="${saleOrderInstance?.telNo}" class="form-control"/>
      </div>
    </div>

    <div class="form-group">
      <label for="updateCustomerInfo" class="control-label col-sm-4 col-sm-offset-3" style="text-align: left">
        <g:checkBox name="updateCustomerInfo"/>
        บันทึกรายละเอียดนี้ ลงข้อมูลลูกค้าด้วย
      </label>
    </div>


  </div>
</div>




<div class="form-group ${hasErrors(bean: saleOrderInstance, field: 'priceType', 'error')} required">
  <label for="priceType" class="control-label col-sm-3">
    <g:message code="saleOrder.priceType.label" default="Price Type"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:hiddenField name="priceTypeOld" value="${cmd?.priceType?.name()}"/>
    <g:select name="priceType" from="${nida.production.PriceType?.values()}"
              keys="${nida.production.PriceType.values()*.name()}" valueMessagePrefix="enum.PriceType"
              noSelection="['': message(code: 'default.select.label')]" required=""
              value="${cmd?.priceType?.name()}" class="form-control"
              onblur="changePriceType()"/>
  </div>
</div>

<div class="panel panel-success" style="padding: 0" id="productPanel">
  <div class="panel-heading"><g:message code="saleOrder.saleOrderDetails.label" default="saleOrderDetails"/></div>

  <div class="panel-body">
    <table class="table">
      <tr>
        <th class="center"><g:message code="default.ord.label"/></th>
        <th class="center" colspan="2"><g:message code="saleOrderDetail.product.label"/></th>
        <th class="center"><g:message code="saleOrderDetail.pricePerUnit.label"/></th>
        <th class="center"><g:message code="saleOrderDetail.qty.label"/></th>
        <th class="center" style="width: 250px"><g:message code="saleOrderDetail.price.label"/></th>
        <th class="center"></th>
      </tr>
      <tbody id="detailTbody">
      <g:each in="${cmd?.saleOrderDetails}" var="saleOrderDetail" status="i">
        <g:render template="saleOrderDetail" model="[i: i, saleOrderDetail: saleOrderDetail]"/>
      </g:each>
      </tbody>
      <tbody id="addDetailTbody">
      <tr class="info">
        <td class="right" style="font-weight: bold">เพิ่มสินค้า</td>
        <td colspan="6">
          <div class="col-sm-2"></div>

          <div class="col-sm-4">
            <ts:autoComplete name="product" action="searchActiveProduct" value="${null}" callback="addDetailById"/>
          </div>

          <div class="col-sm-1" style="width: auto;margin-top: 8px;">หรือ</div>

          <div class="col-sm-2">
            <g:textField name="barcode" value="" class="form-control" placeholder="${message(code: "product.code.label")}"/>
          </div>
        </td>
      </tr>
      </tbody>
      <tbody id="detailTbodySum">
      <tr style="font-weight: bold">
        <td colspan="5" class="right">รวมราคาสินค้า</td>
        <td class="right">
          <span id="detail-price-sum">0.00</span>
          <span>บาท</span>
        </td>
        <td></td>
      </tr>
      </tbody>
      <tbody id="returnDetailTbody">
      <g:each in="${cmd?.saleOrderReturnDetails}" var="saleOrderReturnDetail" status="i">
        <g:render template="saleOrderReturnDetail" model="[i: i, saleOrderReturnDetail: saleOrderReturnDetail]"/>
      </g:each>
      </tbody>
      <tbody id="addReturnDetailTbody">
      <tr class="danger">
        <td class="right" style="font-weight: bold">คืนสินค้า</td>
        <td colspan="6">
          <div class="col-sm-2">
            <g:textField name="refReturnCode" value="" class="form-control" placeholder="${message(code: 'saleOrder.code.label')}"/>
          </div>

          <div class="col-sm-4 otp">
            <ts:autoComplete name="returnProduct" action="searchActiveProduct" value="${null}" callback="addReturnDetailById"/>
          </div>

          <div class="col-sm-1" style="width: auto;margin-top: 8px;">หรือ</div>

          <div class="col-sm-2 otp">
            <g:textField name="returnBarcode" value="" class="form-control" placeholder="${message(code: "product.code.label")}"/>
          </div>
        </td>
      </tr>
      </tbody>
      <tbody id="returnDetailTbodySum">
      <tr style="font-weight: bold">
        <td colspan="5" class="right">รวมราคาคืน</td>
        <td class="right">
          <span id="returnDetail-price-sum">0.00</span>
          <span>บาท</span>
        </td>
        <td></td>
      </tr>
      </tbody>
      <tfoot>

      <tr>
        <td colspan="5" class="right">
          <div style="float: right">
            <div class="col-sm-1"></div>

            <div class="col-sm-3" style="margin-top: 8px;">
              <g:message code="saleOrder.deliveryPrice.label" default="Delivery Price"/>
            </div>

            <div class="col-sm-3">
              <div class="input-group">
                <span class="input-group-addon">
                  <g:message code="subDistrict.label" default="subDistrict"/>
                </span>
                <g:select id="subDistrict" name="subDistrict.id"
                          from="${nida.production.SubDistrict.list()}"
                          optionKey="id" value="${saleOrderInstance?.deliveryHousingEstate?.subDistrict?.id}"
                          class="many-to-one form-control"
                          noSelection="['': message(code: 'default.select.label')]"
                          onchange="changeSubDistrict()"/>
              </div>
            </div>

            <div class="col-sm-3">
              <div class="input-group">
                <span class="input-group-addon">
                  <g:message code="housingEstate.label" default="housingEstate"/>
                </span>
                <g:select id="deliveryHousingEstate" name="deliveryHousingEstate.id"
                          from="${nida.production.HousingEstate.findAllBySubDistrict(saleOrderInstance?.deliveryHousingEstate?.subDistrict ?: null)}"
                          optionKey="id" value="${saleOrderInstance?.deliveryHousingEstate?.id}"
                          class="many-to-one form-control"
                          noSelection="['': message(code: 'default.select.label')]"
                          onchange="changeDeliveryHousingEstate()"/>
                <g:hiddenField name="deliveryPricePerTrip" value="${(saleOrderInstance?.deliveryPrice!=null && saleOrderInstance?.deliveryTrip!=null) ? (saleOrderInstance.deliveryPrice / saleOrderInstance.deliveryTrip) : ''}"/>
              </div>
            </div>

            <div class="col-sm-2">
              <div class="input-group">
                <g:field type="number" name="deliveryTrip" value="${saleOrderInstance.deliveryTrip}"
                         class="form-control" style="text-align: right;" step="1" min="1" onchange="changeDeliveryTrip()"/>
                <span class="input-group-addon">เที่ยว</span>
              </div>
            </div>
          </div>

        </td>
        <td class="right">
          <span id="deliveryPriceSpan">
            <g:formatNumber number="${saleOrderInstance?.deliveryPrice}" formatName="default.currency.format"/>
          </span>
          <span>บาท</span>
          <g:hiddenField name="deliveryPrice" value="${saleOrderInstance?.deliveryPrice}"/>
        </td>
        <td></td>
      </tr>
      <tr>
        <td colspan="5" class="right">
          <g:message code="saleOrder.discountAmount.label" default="Discount Amount"/>
        </td>
        <td class="right">
          <div class="input-group" style="width: 250px;float: right">
            <g:field type="number" name="discountAmount" value="${saleOrderInstance.discountAmount}"
                     class="form-control" style="text-align: right;" step="0.01" min="0.00" onchange="changeDiscountAmount()"/>
            <span class="input-group-addon">บาท</span>
          </div>
        </td>
        <td></td>
      </tr>
      <tr style="font-weight: bold">
        <td colspan="5" class="right">ราคารวมสุทธิ</td>
        <td class="right">
          <span id="detail-price-sum2"></span>
          <span>บาท</span>
        </td>
        <td></td>
      </tr>
      <tr>
        <td colspan="5" class="right">
          <g:message code="saleOrder.receiveAmount.label" default="Receive Amount"/>
        </td>
        <td class="right">
          <div class="input-group" style="width: 250px;float: right">
            <g:field type="number" name="receiveAmount" value="${saleOrderInstance.receiveAmount}"
                     class="form-control" style="text-align: right;" required=""
                     step="0.01" min="0.00" onchange="changeReceiveAmount()"/>
            <span class="input-group-addon">บาท</span>
          </div>
        </td>
        <td></td>
      </tr>
      <tr style="font-weight: bold">
        <td colspan="5" class="right">ทอนเงิน</td>
        <td class="right">
          <span id="changeAmount"></span>
          <span>บาท</span>
        </td>
        <td></td>
      </tr>

      </tfoot>

    </table>
  </div>
</div>

<div class="form-group ${hasErrors(bean: saleOrderInstance, field: 'remark', 'error')} ">
  <label for="remark" class="control-label col-sm-3">
    <g:message code="saleOrder.remark.label" default="Remark"/>
  </label>

  <div class="col-sm-8">
    <g:textArea class="form-control" rows="5" name="remark" value="${cmd?.remark}"></g:textArea>
  </div>
</div>
</div>