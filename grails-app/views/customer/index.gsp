<%@ page import="nida.production.Customer" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'customer.label', default: 'Customer')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
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


  <div class="col-md-12">
    <div>
      <g:form action="index" class="form-horizontal">
        <g:set var="queryParams" value="${['code': params.code, 'name': params.name,]}"/>

        <div class="form-group">
          <label for="code" class="col-sm-2 control-label"><g:message code="customer.code.label" default="Code"/></label>

          <div class="col-sm-4"><g:textField class="form-control" id="code" name="code" value="${params.code}"/></div>
        </div>

        <div class="form-group">
          <label for="name" class="col-sm-2 control-label"><g:message code="customer.name.label" default="Name"/></label>

          <div class="col-sm-4"><g:textField class="form-control" id="name" name="name" value="${params.name}"/></div>
        </div>

        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-6">
            <g:submitButton name="search" class="btn btn-primary" value="${message(code: 'default.button.search.label')}"/>
            <g:link class="btn btn-warning" action="index"><g:message code="default.button.reset.label"/></g:link>
          </div>
        </div>
      </g:form>
    </div>
  </div>

</div>

<div class="row">
  <div class="table-responsive">
    <table class="table">
      <thead>
      <tr>

        <g:sortableColumn class="text-center" property="code" title="${message(code: 'customer.code.label', default: 'Code')}" params="${queryParams}"/>

        <g:sortableColumn class="text-center" property="name" title="${message(code: 'customer.name.label', default: 'Name')}" params="${queryParams}"/>

        <g:sortableColumn class="text-center" property="address" title="${message(code: 'customer.address.label', default: 'Address')}" params="${queryParams}"/>

        <g:sortableColumn class="text-center" property="telNo" title="${message(code: 'customer.telNo.label', default: 'Tel No')}" params="${queryParams}"/>

        <g:sortableColumn class="text-center" property="priceType" title="${message(code: 'customer.priceType.label', default: 'Price Type')}" params="${queryParams}"/>

        <th class="text-center"><g:message code="default.manage.label" default="Manage"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${customerInstanceList}" status="i" var="customerInstance">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

          <td><g:link action="show" id="${customerInstance.id}">${fieldValue(bean: customerInstance, field: "code")}</g:link></td>

          <td>${fieldValue(bean: customerInstance, field: "name")}</td>

          <td>${fieldValue(bean: customerInstance, field: "address")}</td>

          <td>${fieldValue(bean: customerInstance, field: "telNo")}</td>

          <td><g:message code="enum.PriceType.${customerInstance.priceType}"/></td>

          <td class="center">
            <g:link class="btn btn-info" action="show" id="${customerInstance.id}"><g:message code="default.button.show.label" default="Show"/></g:link>
            <g:link class="btn btn-primary" action="edit" id="${customerInstance.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>

    <div class="text-center">
      <g:paginate total="${customerInstanceCount ?: 0}" params="${queryParams}"/>
    </div>
  </div>
</div>
</body>
</html>
