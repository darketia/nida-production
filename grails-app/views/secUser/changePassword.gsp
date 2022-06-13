<%@ page import="nida.production.SecUser" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'secUser.changePassword.label')}"/>
  <title>${entityName}</title>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">${entityName}</a>
    </div>
  </div>
</nav>

<div id="edit-secUser" class="content scaffold-edit" role="main">
  <g:if test="${flash.message}">
    <div class="alert alert-info" role="alert">
      <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
      ${flash.message}
    </div>
  </g:if>
  <g:if test="${flash.errors}">
    <div class="alert alert-danger" role="alert">
      <span class="glyphicon glyphicon-danger-sign" aria-hidden="true"></span>
      ${flash.errors}
    </div>
  </g:if>
  <g:form url="[resource: secUserInstance, action: 'updatePassword']" method="PUT"  class="form-horizontal">
    <fieldset class="form">
      <div class="form-group required">
        <label for="oldPassword" class="control-label col-sm-4">
          <g:message code="secUser.oldPassword.label" default="Old Password" />
          <span class="required-indicator">*</span>
        </label>

        <div class="col-sm-4">
          <g:passwordField name="oldPassword" required="" class="form-control"/>
        </div>
      </div>

      <div class="form-group required">
        <label for="newPassword" class="control-label col-sm-4">
          <g:message code="secUser.newPassword.label" default="New Password" />
          <span class="required-indicator">*</span>
        </label>

        <div class="col-sm-4">
          <g:passwordField name="newPassword" required="" class="form-control"/>
        </div>
      </div>

      <div class="form-group  required">
        <label for="confirmPassword" class="control-label col-sm-4">
          <g:message code="secUser.confirmPassword.label" default="Confirm Password" />
          <span class="required-indicator">*</span>
        </label>

        <div class="col-sm-4">
          <g:passwordField name="confirmPassword" required="" class="form-control"/>
        </div>
      </div>
    </fieldset>
    <g:actionSubmit class="btn btn-primary" action="updatePassword" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
  </g:form>
</div>
</body>
</html>
