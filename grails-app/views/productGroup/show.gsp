
<%@ page import="nida.production.ProductGroup" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'productGroup.label', default: 'ProductGroup')}"/>
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
  <dl class="dl-horizontal dl-show">
    
    <g:if test="${productGroupInstance?.code}">
      <dt><g:message code="productGroup.code.label" default="Code"/></dt>
      
      <dd><g:fieldValue bean="${productGroupInstance}" field="code"/></dd>
      
    </g:if>
    
    <g:if test="${productGroupInstance?.name}">
      <dt><g:message code="productGroup.name.label" default="Name"/></dt>
      
      <dd><g:fieldValue bean="${productGroupInstance}" field="name"/></dd>
      
    </g:if>
    
    <g:form url="[resource: productGroupInstance, action: 'delete']" method="DELETE">
      <g:link class="btn btn-primary" action="edit" resource="${productGroupInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:form>

    <g:render template="/shared/updaterDetail" model="[instance: productGroupInstance]"/>
  </dl>

</div>
</body>
</html>
