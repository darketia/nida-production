<%@ page import="nida.production.HousingEstate" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'housingEstate.label', default: 'HousingEstate')}"/>
  <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="default.edit.label" args="[entityName]"/></a>
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

<div id="edit-housingEstate" class="content scaffold-edit" role="main">
  <g:if test="${flash.message}">
    <div class="alert alert-info" role="alert">
      <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
      ${flash.message}
    </div>
  </g:if>
  <g:hasErrors bean="${housingEstateInstance}">
    <div id="form-error" class="alert alert-danger" role="alert">
      <g:eachError bean="${housingEstateInstance}" var="error">
        <div class="control-label">
          <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span><g:message error="${error}"/>
        </div>
      </g:eachError>
    </div>
  </g:hasErrors>
  <g:form url="[resource: housingEstateInstance, action: 'update']" method="PUT"  class="form-horizontal">
  <g:hiddenField name="version" value="${housingEstateInstance?.version}"/>
  <fieldset class="form">
    <g:render template="form"/>
  </fieldset>
  <g:actionSubmit class="btn btn-primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
  </g:form>
</div>
</body>
</html>
