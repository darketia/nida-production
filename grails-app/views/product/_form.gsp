<%@ page import="nida.production.ProductCmd; nida.production.ProductCmd; nida.production.Product" %>
<g:set var="productInstance" value="${(Product) productInstance}"/>
<g:set var="cmd" value="${(nida.production.ProductCmd) cmd}"/>

<div class="form-group ${hasErrors(bean: productInstance, field: 'code', 'error')} required">
	<label for="code" class="control-label col-sm-4">
		<g:message code="product.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="code" required="" value="${cmd?.code}" class="form-control"/>
	</div>
</div>


<div class="form-group ${hasErrors(bean: productInstance, field: 'name', 'error')} required">
	<label for="name" class="control-label col-sm-4">
		<g:message code="product.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="name" required="" value="${cmd?.name}" class="form-control"/>
	</div>
</div>


<div class="form-group ${hasErrors(bean: productInstance, field: 'productGroup', 'error')} required">
	<label for="productGroup" class="control-label col-sm-4">
		<g:message code="product.productGroup.label" default="Product Group" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:select id="productGroup" name="productGroup.id" from="${nida.production.ProductGroup.list()}" optionKey="id" required="" value="${cmd?.productGroup?.id}" class="many-to-one form-control" noSelection="['': message(code:'default.select.label')]"/>
	</div>
</div>

<div class="form-group ${hasErrors(bean: productInstance, field: 'uom', 'error')} required">
	<label for="uom" class="control-label col-sm-4">
		<g:message code="product.uom.label" default="Uom" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:select id="uom" name="uom.id" from="${nida.production.Uom.list()}" optionKey="id" required="" value="${cmd?.uom?.id}" class="many-to-one form-control" noSelection="['': message(code:'default.select.label')]"/>
	</div>
</div>

<div class="form-group ${hasErrors(bean: productInstance, field: 'minimumStock', 'error')} ">
	<label for="minimumStock" class="control-label col-sm-4">
		<g:message code="product.minimumStock.label" default="minimumStock" />
	</label>

	<div class="col-sm-4">
		<g:field type="number" name="minimumStock" value="${productInstance.minimumStock}"
						 class="form-control" style="text-align: right;" step="0.01" min="0.00"/>
	</div>
</div>

<div class="form-group ${hasErrors(bean: productInstance, field: 'cancel', 'error')} ">
	<label for="cancel" class="control-label col-sm-4">
		<g:message code="product.cancel.label" default="cancel" />
	</label>

	<div class="col-sm-4">
		<g:checkBox name="cancel" value="${productInstance.cancel}"/>
	</div>
</div>

<div class="form-group required">
  <label class="control-label col-sm-4">
    <g:message code="product.productPrices.label" default="productPrices"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <table class="table">
      <tr>
        <th class="center"><g:message code="productPrice.priceType.label"/></th>
        <th class="center"><g:message code="productPrice.price.label"/></th>
      </tr>
      <g:each in="${cmd?.productPrices}" var="productPrice" status="i">
        <tr>
          <g:hiddenField name="productPrices[${i}].priceType" value="${productPrice.priceType}"/>
          <td class="right">${message(code: "enum.PriceType.${productPrice.priceType}")}</td>
          <td class="right">
            <div class="input-group" style="width: 220px; float: right;">
              <g:field type="number" name="productPrices[${i}].price" value="${productPrice.price}"
                       class="form-control" style="text-align: right;" step="0.01" min="0.00" required=""/>
              <span class="input-group-addon">บาท</span>
            </div>
          </td>
        </tr>
      </g:each>
    </table>
  </div>
</div>



