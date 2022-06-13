<%@ page import="nida.production.PriceType; nida.production.Customer" %>



<div class="form-group ${hasErrors(bean: customerInstance, field: 'code', 'error')} required">
	<label for="code" class="control-label col-sm-4">
		<g:message code="customer.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="code" required="" value="${customerInstance?.code}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: customerInstance, field: 'name', 'error')} required">
	<label for="name" class="control-label col-sm-4">
		<g:message code="customer.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="name" required="" value="${customerInstance?.name}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: customerInstance, field: 'address', 'error')} ">
	<label for="address" class="control-label col-sm-4">
		<g:message code="customer.address.label" default="Address" />
		
	</label>
	
	<div class="col-sm-4">
		<g:textField name="address" value="${customerInstance?.address}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: customerInstance, field: 'telNo', 'error')} ">
	<label for="telNo" class="control-label col-sm-4">
		<g:message code="customer.telNo.label" default="Tel No" />
		
	</label>
	
	<div class="col-sm-4">
		<g:textField name="telNo" value="${customerInstance?.telNo}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: customerInstance, field: 'priceType', 'error')} required">
	<label for="priceType" class="control-label col-sm-4">
		<g:message code="customer.priceType.label" default="Price Type" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:select name="priceType" from="${nida.production.PriceType?.values()}" keys="${nida.production.PriceType.values()*.name()}" valueMessagePrefix="enum.PriceType" noSelection="['': message(code:'default.select.label')]" required="" value="${customerInstance?.priceType?.name()}" class="form-control"/>

	</div>
	
</div>


