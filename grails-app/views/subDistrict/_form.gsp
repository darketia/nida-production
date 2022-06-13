<%@ page import="nida.production.SubDistrict" %>



<div class="form-group ${hasErrors(bean: subDistrictInstance, field: 'code', 'error')} required">
	<label for="code" class="control-label col-sm-4">
		<g:message code="subDistrict.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="code" required="" value="${subDistrictInstance?.code}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: subDistrictInstance, field: 'name', 'error')} required">
	<label for="name" class="control-label col-sm-4">
		<g:message code="subDistrict.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="name" required="" value="${subDistrictInstance?.name}" class="form-control"/>

	</div>
	
</div>


