<%@ page import="nida.production.SecUser" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'secUser.label', default: 'SecUser')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="default.show.label" args="[entityName]"/></a>
    </div>

    <div class="navbar-default">
      <ul class="nav navbar-nav">
        <li>
          <g:link action="index">
            <i class="glyphicon glyphicon-th-list"></i>
            <g:message code="default.list.label" args="[entityName]"/>
          </g:link>
        </li>
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
  <dl class="dl-horizontal dl-show">

    <g:if test="${secUserInstance?.username}">
      <dt><g:message code="secUser.username.label" default="Username"/></dt>
      <dd><g:fieldValue bean="${secUserInstance}" field="username"/></dd>
    </g:if>

    <g:if test="${secUserInstance?.code}">
      <dt><g:message code="secUser.code.label" default="Code"/></dt>
      <dd><g:fieldValue bean="${secUserInstance}" field="code"/></dd>
    </g:if>

    <g:if test="${secUserInstance?.firstName}">
      <dt><g:message code="secUser.firstName.label" default="First Name"/></dt>
      <dd><g:fieldValue bean="${secUserInstance}" field="firstName"/></dd>
    </g:if>

    <g:if test="${secUserInstance?.lastName}">
      <dt><g:message code="secUser.lastName.label" default="Last Name"/></dt>
      <dd><g:fieldValue bean="${secUserInstance}" field="lastName"/></dd>
    </g:if>

    <dt><g:message code="secUser.enabled.label" default="Enabled"/></dt>
    <dd><g:formatBoolean boolean="${secUserInstance?.enabled}"/></dd>

    <g:if test="${roles}">
      <dt><g:message code="secUser.role.label" default="Role"/></dt>
      <dd>
        <ul>
          <g:each in="${roles}" var="r">
            <li>${r?.encodeAsHTML()}</li>
          </g:each>
        </ul>
      </dd>
    </g:if>

    <g:if test="${poTypes}">
      <dt><g:message code="secUser.poType.label" default="PoType"/></dt>
      <dd>
        <ul>
          <g:each in="${poTypes}" var="r">
            <li>${r?.encodeAsHTML()}</li>
          </g:each>
        </ul>
      </dd>
    </g:if>

      <g:form url="[resource: secUserInstance, action: 'delete']" method="DELETE">
      <g:link class="btn btn-primary" action="edit" resource="${secUserInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
      <g:link class="btn btn-warning" action="changePassword" id="${secUserInstance.id}">Change Password</g:link>
    </g:form>

    <g:render template="/shared/updaterDetail" model="[instance: secUserInstance]"/>
  </dl>
</div>
</body>
</html>
