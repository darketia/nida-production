<%@ page import="nida.production.Uom" %>



<div class="form-group ${hasErrors(bean: uomInstance, field: 'code', 'error')} required">
	<label for="code" class="control-label col-sm-4">
		<g:message code="uom.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="code" required="" value="${uomInstance?.code}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: uomInstance, field: 'name', 'error')} required">
	<label for="name" class="control-label col-sm-4">
		<g:message code="uom.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="name" required="" value="${uomInstance?.name}" class="form-control"/>

	</div>
	
</div>


