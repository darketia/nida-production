<%@ page import="nida.production.Product" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}"/>
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
        <g:set var="queryParams" value="${[
            'code': params.code,
            'name': params.name,
            'productGroup.id': params.productGroup?.id,
            'uom.id': params.uom?.id,
            'status': params.status,
        ]}"/>

        <div class="form-group">
          <label for="code" class="col-sm-2 control-label"><g:message code="product.code.label" /></label>
          <div class="col-sm-4"><g:textField class="form-control" id="code" name="code" value="${params.code}"/></div>
        </div>

        <div class="form-group">
          <label for="name" class="col-sm-2 control-label"><g:message code="product.name.label" /></label>
          <div class="col-sm-4"><g:textField class="form-control" id="name" name="name" value="${params.name}"/></div>
        </div>

        <div class="form-group">
          <label for="productGroup" class="col-sm-2 control-label"><g:message code="product.productGroup.label" /></label>
          <div class="col-sm-4">
            <g:select id="productGroup" name="productGroup.id" from="${nida.production.ProductGroup.list()}" optionKey="id" value="${params?.productGroup?.id}" class="many-to-one form-control" noSelection="['': message(code:'default.select.all.label')]"/>
          </div>
        </div>

        <div class="form-group">
          <label for="uom" class="col-sm-2 control-label"><g:message code="product.uom.label" /></label>
          <div class="col-sm-4">
            <g:select id="uom" name="uom.id" from="${nida.production.Uom.list()}" optionKey="id" value="${params?.uom?.id}" class="many-to-one form-control" noSelection="['': message(code:'default.select.all.label')]"/>
          </div>
        </div>

        <div class="form-group">
          <label for="status" class="col-sm-2 control-label"><g:message code="product.status.label" /></label>
          <div class="col-sm-4">
            <g:select id="status" name="status" from="${[[id:1, label:'ขายปกติ'],[id:2, label:'เลิกขาย']]}"
                      optionKey="id" optionValue="label"
                      value="${params?.status}" class="many-to-one form-control" noSelection="['': message(code:'default.select.all.label')]"/>
          </div>
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

        <g:sortableColumn class="text-center" property="code" title="${message(code: 'product.code.label', default: 'Code')}" params="${queryParams}"/>

        <g:sortableColumn class="text-center" property="name" title="${message(code: 'product.name.label', default: 'Name')}" params="${queryParams}"/>

        <th class="text-center"><g:message code="product.productGroup.label" default="Product Group"/></th>

        <th class="text-center"><g:message code="product.uom.label" default="Uom"/></th>

        <th class="text-center"><g:message code="product.status.label" default="status"/></th>

        <th class="text-center"><g:message code="default.manage.label" default="Manage"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${productInstanceList}" status="i" var="productInstance">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

          <td><g:link action="show" id="${productInstance.id}">${fieldValue(bean: productInstance, field: "code")}</g:link></td>

          <td>${fieldValue(bean: productInstance, field: "name")}</td>

          <td>${fieldValue(bean: productInstance, field: "productGroup")}</td>

          <td>${fieldValue(bean: productInstance, field: "uom")}</td>

          <td class="center">
            ${productInstance.cancel ? 'เลิกขาย': 'ขายปกติ' }
          </td>

          <td class="center">
            <g:link class="btn btn-info" action="show" id="${productInstance.id}"><g:message code="default.button.show.label" default="Show"/></g:link>
            <g:link class="btn btn-primary" action="edit" id="${productInstance.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>

    <div class="text-center">
      <g:paginate total="${productInstanceCount ?: 0}" params="${queryParams}"/>
    </div>
  </div>
</div>
</body>
</html>
