<%@ page import="nida.production.Company" %>



<div class="form-group ${hasErrors(bean: companyInstance, field: 'code', 'error')} required">
	<label for="code" class="control-label col-sm-4">
		<g:message code="company.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="code" required="" value="${companyInstance?.code}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: companyInstance, field: 'name', 'error')} required">
	<label for="name" class="control-label col-sm-4">
		<g:message code="company.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="name" required="" value="${companyInstance?.name}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: companyInstance, field: 'address', 'error')} ">
	<label for="address" class="control-label col-sm-4">
		<g:message code="company.address.label" default="Address" />
		
	</label>
	
	<div class="col-sm-4">
		<g:textField name="address" value="${companyInstance?.address}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: companyInstance, field: 'taxNo', 'error')} ">
	<label for="taxNo" class="control-label col-sm-4">
		<g:message code="company.taxNo.label" default="Tax No" />
		
	</label>
	
	<div class="col-sm-4">
		<g:textField name="taxNo" value="${companyInstance?.taxNo}" class="form-control"/>

	</div>
	
</div>


<div class="form-group ${hasErrors(bean: companyInstance, field: 'vatRate', 'error')} ">
	<label for="vatRate" class="control-label col-sm-4">
		<g:message code="company.vatRate.label" />

	</label>

	<div class="col-sm-4">
		<div class="input-group" style="width: 150px;">
			<g:field type="number" name="vatRate" value="${companyInstance.vatRate}"
							 class="form-control" style="text-align: right;" step="1" min="0" max="100" onchange="sumQty2()"/>
			<span class="input-group-addon">%</span>
		</div>
	</div>


</div>