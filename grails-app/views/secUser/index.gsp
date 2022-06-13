<%@ page import="nida.production.SecUser" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'secUser.label', default: 'SecUser')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
  <script>
    $(document).ready(function () {

      $('#role').select2({
        placeholder: '${message(code:'default.select.label')}',
        allowClear: true,
        width: '400px'
      }).select2('val', '${params?.role?.id}');

      $('#dcGroup').select2({
        placeholder: '${message(code:'default.select.label')}',
        allowClear: true,
        width: '400px'
      }).select2('val', '${params?.dcGroup?.id}');


    });
  </script>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="default.list.label" args="[entityName]"/></a>
    </div>
    <div class="navbar-default">
      <ul class="nav navbar-nav">
        <li>
          <g:link action="create">
            <i class="glyphicon glyphicon-plus"></i>
            <g:message code="default.create.label" args="[entityName]"/>
          </g:link>
        </li>
      </ul>
    </div>
  </div>
</nav>

<div class="row">
  <g:if test="${flash.message}">
    <div class="alert alert-info" role="alert">
      <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
      ${flash.message}
    </div>
  </g:if>
  <g:if test="${flash.errors}">
    <div class="alert alert-danger" role="alert">
      <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
      <span class="sr-only">Error:</span>
      ${flash.errors}
    </div>
  </g:if>

  <g:form name="secUserList" action="index" class="form-horizontal">
    <g:set var="queryParams" value="${[
        'username': params.username,
        'firstName': params.firstName,
        'lastName': params.lastName
    ]}"/>

    <div class="form-group">
      <label for="username" class="col-sm-2 control-label">
        <g:message code="secUser.username.label" default="Username"/>
      </label>

      <div class="col-sm-4">
        <g:textField class="form-control" id="username" name="username" value="${params.username}"/>
      </div>
    </div>

    <div class="form-group">
      <label for="firstName" class="col-sm-2 control-label">
        <g:message code="secUser.firstName.label" default="First Name"/>
      </label>

      <div class="col-sm-4">
        <g:textField class="form-control" id="firstName" name="firstName" value="${params.firstName}"/>
      </div>
    </div>

    <div class="form-group">
      <label for="firstName" class="col-sm-2 control-label">
        <g:message code="secUser.lastName.label" default="Last Name"/>
      </label>

      <div class="col-sm-4">
        <g:textField class="form-control" id="lastName" name="lastName" value="${params.lastName}"/>
      </div>
    </div>
    
    <div class="form-group">
      <label for="role" class="col-sm-2 control-label"><g:message code="secUser.role.label" default="DC Group"/></label>
      <div class="col-sm-4">
        <ts:selectWithOptGroup id="role" name="role.id" from="${nida.production.Role.list()}" optionKey="id" groupBy="group" class="select2"
                               value="${params?.role?.id}" noSelection="['': message(code: 'default.select.all.label')]"/>
      </div>
    </div>


    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-8">
        <g:submitButton name="search" class="btn btn-primary" value="Search"/>
        <g:link action="index" name="reset" value="Reset" class="btn btn-warning">Reset</g:link>
      </div>
    </div>
  </g:form>
</div>
<div class="row">
  <div class="table-responsive">
    <table class="table">
      <thead>
      <tr>
        <g:sortableColumn class="text-center" property="username" title="${message(code: 'secUser.username.label', default: 'Username')}"/>
        <g:sortableColumn class="text-center" property="code" title="${message(code: 'secUser.code.label', default: 'code')}"/>
        <g:sortableColumn class="text-center" property="firstName" title="${message(code: 'secUser.firstName.label', default: 'First Name')}"/>
        <g:sortableColumn class="text-center" property="lastName" title="${message(code: 'secUser.lastName.label', default: 'Lasr Name')}"/>
        <th class="center"><g:message code="secUser.enabled.label"/></th>
        <th class="text-center"><g:message code="default.manage.label" default="Manage"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${secUserInstanceList}" status="i" var="secUserInstance">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
          <td><g:link action="show" id="${secUserInstance.id}">${fieldValue(bean: secUserInstance, field: "username")}</g:link></td>
          <td>${fieldValue(bean: secUserInstance, field: "code")}</td>
          <td>${fieldValue(bean: secUserInstance, field: "firstName")}</td>
          <td>${fieldValue(bean: secUserInstance, field: "lastName")}</td>
          <td class="text-center"><g:formatBoolean boolean="${secUserInstance.enabled}"/></td>
          <td class="center">
            <g:link class="btn btn-info" action="show" id="${secUserInstance.id}"><g:message code="default.button.show.label" /></g:link>
            <g:link class="btn btn-primary" action="edit" id="${secUserInstance.id}"><g:message code="default.button.edit.label" /></g:link>
            <g:link class="btn btn-warning" action="changePassword" id="${secUserInstance.id}"><g:message code="secUser.changePassword.label" /></g:link>
            <g:link class="btn btn-danger" action="resetPassword" id="${secUserInstance.id}"><g:message code="secUser.resetPassword.label" /></g:link>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
    <div class="text-center">
      <g:paginate total="${secUserInstanceCount ?: 0}" params="${queryParams}"/>
    </div>
  </div>
</div>
</body>
</html>
