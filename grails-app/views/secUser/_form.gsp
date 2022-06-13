<g:set var="secUserInstance" value="${(SecUser) secUserInstance}"/>
<%@ page import="nida.production.SecUser" %>
<script type="text/javascript">
  $(document).ready(function() {
//    $('.js-example-basic-multiple').select2();
    $('.js-example-basic-multiple').multiSelect();
  });
</script>


<div class="form-group ${hasErrors(bean: secUserInstance, field: 'username', 'error')} required">
	<label for="username" class="control-label col-sm-4">
		<g:message code="secUser.username.label" default="Username" />
		<span class="required-indicator">*</span>
	</label>
	
	<div class="col-sm-4">
		<g:textField name="username" required="" value="${secUserInstance?.username}" class="form-control"/>

	</div>
	
</div>

<g:if test="${!secUserInstance.id}">
  <div class="form-group ${hasErrors(bean: secUserInstance, field: 'password', 'error')} required">
    <label for="password" class="control-label col-sm-4">
      <g:message code="secUser.password.label" default="Password" />
      <span class="required-indicator">*</span>
    </label>

    <div class="col-sm-4">
      <g:passwordField name="password" required="" value="${secUserInstance?.password}" class="form-control"/>

    </div>

  </div>
</g:if>

<div class="form-group ${hasErrors(bean: secUserInstance, field: 'code', 'error')} required">
  <label for="lastName" class="control-label col-sm-4">
    <g:message code="secUser.code.label" default="Code" />
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:field type="code" name="code" required="" value="${secUserInstance?.code}" class="form-control"/>
  </div>
</div>


<div class="form-group ${hasErrors(bean: secUserInstance, field: 'firstName', 'error')} required">
  <label for="firstName" class="control-label col-sm-4">
    <g:message code="secUser.firstName.label" default="First Name" />
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:textField name="firstName" required="" value="${secUserInstance?.firstName}" class="form-control"/>
  </div>

</div>

<div class="form-group ${hasErrors(bean: secUserInstance, field: 'lastName', 'error')} required">
  <label for="lastName" class="control-label col-sm-4">
    <g:message code="secUser.lastName.label" default="Last Name" />
    <span class="required-indicator">*</span>
  </label>

  <div class="col-sm-4">
    <g:textField name="lastName" required="" value="${secUserInstance?.lastName}" class="form-control"/>
  </div>

</div>

<div class="form-group ${hasErrors(bean: secUserInstance, field: 'enabled', 'error')} ">
  <label for="enabled" class="control-label col-sm-4">
    <g:message code="secUser.enabled.label" default="Enabled" />

  </label>

  <div class="col-sm-4 checkbox">
    <label>
      <g:checkBox name="enabled" value="${secUserInstance?.enabled}" />

    </label>
  </div>

</div>

<div class="form-group">
  <label for="roles" class="control-label col-sm-4">
    <g:message code="secUser.role.label" default="Role" />
  </label>

  <div class="col-sm-4">
    <ts:selectWithOptGroup name="roles" from="${nida.production.Role.list()}"
                            multiple="multiple" optionKey="id" groupBy="group"
                            value="${roles*.id}"
                            class="many-to-many form-control js-example-basic-multiple"/>
  </div>
</div>

<div class="form-group">
  <label for="roles" class="control-label col-sm-4">
    <g:message code="secUser.poType.label" default="Role" />
  </label>

  <div class="col-sm-4">
    <g:select name="poTypes" from="${nida.production.PoType.list()}"
                           multiple="multiple" optionKey="id"
                           value="${poTypes*.id}"
                           class="many-to-many form-control js-example-basic-multiple"/>
  </div>
</div>