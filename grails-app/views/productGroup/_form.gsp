<%@ page import="nida.production.ProductGroup" %>



<div class="form-group ${hasErrors(bean: productGroupInstance, field: 'code', 'error')} required">
	<label for="code" class="control-label col-sm-4">
		<g:message code="productGroup.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="code" required="" value="${productGroupInstance?.code}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: productGroupInstance, field: 'name', 'error')} required">
	<label for="name" class="control-label col-sm-4">
		<g:message code="productGroup.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="name" required="" value="${productGroupInstance?.name}" class="form-control"/>

	</div>
	
</div>


