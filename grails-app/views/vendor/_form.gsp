<%@ page import="nida.production.Vendor" %>



<div class="form-group ${hasErrors(bean: vendorInstance, field: 'code', 'error')} required">
  <label for="code" class="control-label col-sm-4">
    <g:message code="vendor.code.label" default="Code"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:textField name="code" required="" value="${vendorInstance?.code}" class="form-control"/>
  </div>
</div>


<div class="form-group ${hasErrors(bean: vendorInstance, field: 'name', 'error')} required">
  <label for="name" class="control-label col-sm-4">
    <g:message code="vendor.name.label" default="Name"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:textField name="name" required="" value="${vendorInstance?.name}" class="form-control"/>

  </div>

</div>

<div class="form-group ${hasErrors(bean: vendorInstance, field: 'address', 'error')} required">
  <label for="address" class="control-label col-sm-4">
    <g:message code="vendor.address.label" default="address"/>
  </label>

  <div class="col-sm-4">
    <g:textArea name="address" value="${vendorInstance?.address}" class="form-control" rows="5" ></g:textArea>
  </div>
</div>

<div class="form-group ${hasErrors(bean: vendorInstance, field: 'taxNo', 'error')} required">
  <label for="taxNo" class="control-label col-sm-4">
    <g:message code="vendor.taxNo.label" default="taxNo"/>
  </label>

  <div class="col-sm-4">
    <g:textField name="taxNo" value="${vendorInstance?.taxNo}" class="form-control" />
  </div>
</div>

<div class="form-group ${hasErrors(bean: vendorInstance, field: 'telNo', 'error')} required">
  <label for="telNo" class="control-label col-sm-4">
    <g:message code="vendor.telNo.label" default="telNo"/>
  </label>

  <div class="col-sm-4">
    <g:textField name="telNo" value="${vendorInstance?.telNo}" class="form-control" />
  </div>
</div>

<div class="form-group ${hasErrors(bean: vendorInstance, field: 'email', 'error')} required">
  <label for="email" class="control-label col-sm-4">
    <g:message code="vendor.email.label" default="email"/>
  </label>

  <div class="col-sm-4">
    <g:textField name="email" value="${vendorInstance?.email}" class="form-control" />
  </div>
</div>

<div class="form-group ${hasErrors(bean: vendorInstance, field: 'contactPerson', 'error')} required">
  <label for="contactPerson" class="control-label col-sm-4">
    <g:message code="vendor.contactPerson.label" default="contactPerson"/>
  </label>

  <div class="col-sm-4">
    <g:textField name="contactPerson" value="${vendorInstance?.contactPerson}" class="form-control" />
  </div>
</div>


