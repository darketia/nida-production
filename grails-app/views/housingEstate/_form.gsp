<%@ page import="nida.production.HousingEstate" %>

<div class="form-group ${hasErrors(bean: housingEstateInstance, field: 'code', 'error')} required">
  <label for="code" class="control-label col-sm-4">
    <g:message code="housingEstate.code.label" default="Code"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:textField name="code" required="" value="${housingEstateInstance?.code}" class="form-control"/>

  </div>

</div>

<div class="form-group ${hasErrors(bean: housingEstateInstance, field: 'name', 'error')} required">
  <label for="name" class="control-label col-sm-4">
    <g:message code="housingEstate.name.label" default="Name"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:textField name="name" required="" value="${housingEstateInstance?.name}" class="form-control"/>

  </div>

</div>

<div class="form-group ${hasErrors(bean: housingEstateInstance, field: 'subDistrict', 'error')} required">
  <label for="subDistrict" class="control-label col-sm-4">
    <g:message code="housingEstate.subDistrict.label" default="Sub District"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:select id="subDistrict" name="subDistrict.id" from="${nida.production.SubDistrict.list()}" optionKey="id" required="" value="${housingEstateInstance?.subDistrict?.id}" class="many-to-one form-control" noSelection="['': message(code: 'default.select.label')]"/>
  </div>
</div>

<div class="form-group ${hasErrors(bean: housingEstateInstance, field: 'deliveryPrice', 'error')} required">
  <label for="deliveryPrice" class="control-label col-sm-4">
    <g:message code="housingEstate.deliveryPrice.label" default="Delivery Price"/>
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <div class="input-group" style="width: 220px;">
      <g:field type="number" name="deliveryPrice" value="${housingEstateInstance.deliveryPrice}"
               class="form-control" style="text-align: right;" step="0.01" min="0.00"/>
      <span class="input-group-addon">บาท</span>
    </div>
  </div>

</div>