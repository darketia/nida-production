<%@ page import="nida.production.StockCard" %>
<script>
  $(document).ready(function () {

  })

  function changeProduct(){
    $.ajax({
      url: "${createLink(action: 'ajaxProductInfo')}",
      type: "POST", dataType: "json",
      data: {productId: $("input[name='product.id']").val()},
      async: true,
      success: function (data) {
        $("#uomSpan").text(data.uom)
      },
      error: function (data) {
        if (data.status == 400) alert(data.responseText)
        else alert("HTTP Status " + data.status + " - " + data.statusText)
      }
    })
  }

</script>

<g:set var="stockCardInstance" value="${(StockCard)stockCardInstance}"/>

<div class="form-group ${hasErrors(bean: stockCardInstance, field: 'date', 'error')} required">
  <label for="date" class="control-label col-sm-4">
    <g:message code="stockCard.date.label" default="Date"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <div class="input-group date datePicker">
      <g:textField class="form-control " name="date" value="${ts.formatDate(date: stockCardInstance?.date ?: new Date())}" required=""/>
      <span class="input-group-addon cursor"><span class="glyphicon glyphicon-calendar"></span>
      </span>
    </div>

  </div>

</div>

<div class="form-group ${hasErrors(bean: stockCardInstance, field: 'product', 'error')} required">
  <label for="product" class="control-label col-sm-4">
    <g:message code="stockCard.product.label" default="Product"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <ts:autoComplete name="product" action="searchProduct" value="${stockCardInstance?.product}" callback="changeProduct" required=""/>
  </div>

</div>

<div class="form-group ${hasErrors(bean: stockCardInstance, field: 'qty', 'error')} required">
  <label for="qty" class="control-label col-sm-4">
    <g:message code="stockCard.qty.label" default="Qty"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <div class="input-group">
      <g:field type="number" name="qty" value="${stockCardInstance.qty}"
               class="form-control" style="text-align: right;" step="0.01" required=""/>
      <span id="uomSpan" class="input-group-addon">${stockCardInstance?.product?.uom?.name}</span>
    </div>
  </div>

</div>