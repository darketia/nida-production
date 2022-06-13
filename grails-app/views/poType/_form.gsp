<%@ page import="nida.production.PoType" %>



<div class="form-group ${hasErrors(bean: poTypeInstance, field: 'code', 'error')} required">
	<label for="code" class="control-label col-sm-4">
		<g:message code="poType.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="code" required="" value="${poTypeInstance?.code}" class="form-control"/>

	</div>

</div>


<div class="form-group ${hasErrors(bean: poTypeInstance, field: 'name', 'error')} required">
	<label for="name" class="control-label col-sm-4">
		<g:message code="poType.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="name" required="" value="${poTypeInstance?.name}" class="form-control"/>

	</div>

</div>


