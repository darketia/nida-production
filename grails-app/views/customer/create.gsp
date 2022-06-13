<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}"/>
  <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="default.create.label" args="[entityName]"/></a>
    </div>
    <div class="navbar-default">
      <ul class="nav navbar-nav">
        <li>
          <g:link action="index">
            <i class="glyphicon glyphicon-th-list"></i>
            <g:message code="default.list.label" args="[entityName]"/>
          </g:link>
        </li>
      </ul>
    </div>
  </div>
</nav>

<div id="create-customer" class="content scaffold-create" role="main">
  <g:if test="${flash.message}">
    <div class="alert alert-info" role="alert">
      <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
      ${flash.message}
    </div>
  </g:if>
  <g:hasErrors bean="${customerInstance}">
    <div id="form-error" class="alert alert-danger" role="alert">
      <g:eachError bean="${customerInstance}" var="error">
        <div class="control-label">
          <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span><g:message error="${error}"/>
        </div>
      </g:eachError>
    </div>
  </g:hasErrors>
  <g:form url="[resource: customerInstance, action: 'save']"  class="form-horizontal">
    <fieldset class="form">
      <g:render template="form"/>
    </fieldset>
    <g:submitButton name="create" class="btn btn-primary" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
  </g:form>
</div>
</body>
</html>
