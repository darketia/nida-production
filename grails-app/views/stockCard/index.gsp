<%@ page import="nida.production.StockCard" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'stockCard.label', default: 'StockCard')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
  <style>
  .underMinimumStock {
    color: red
  }
  .soonMinimumStock {
    color: #a6a600
  }
  </style>
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
            'code'             : params.code,
            'name'             : params.name,
            'productGroup.id'  : params.productGroup?.id,
            'underMinimumStock': params.underMinimumStock,
            'soonMinimumStock' : params.soonMinimumStock,
        ]}"/>

        <div class="form-group">
          <label for="code" class="col-sm-2 control-label"><g:message code="product.code.label"/></label>

          <div class="col-sm-4"><g:textField class="form-control" id="code" name="code" value="${params.code}"/></div>
        </div>

        <div class="form-group">
          <label for="name" class="col-sm-2 control-label"><g:message code="product.name.label"/></label>

          <div class="col-sm-4"><g:textField class="form-control" id="name" name="name" value="${params.name}"/></div>
        </div>

        <div class="form-group">
          <label for="productGroup" class="col-sm-2 control-label"><g:message code="product.productGroup.label"/></label>

          <div class="col-sm-4">
            <g:select id="productGroup" name="productGroup.id" from="${nida.production.ProductGroup.list()}" optionKey="id" value="${params?.productGroup?.id}" class="many-to-one form-control" noSelection="['': message(code: 'default.select.all.label')]"/>
          </div>
        </div>

        <div class="form-group">
          <label class="col-sm-2 control-label"><g:message code="stockCard.condition.label"/></label>

          <div class="col-sm-4 checkbox">
            <label>
              <g:checkBox name="underMinimumStock" value="${params.underMinimumStock}"/>
              <g:message code="stockCard.underMinimumStock.label"/>
            </label>
            <label>
              <g:checkBox name="soonMinimumStock" value="${params.soonMinimumStock}"/>
              <g:message code="stockCard.soonMinimumStock.label"/>
            </label>
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
        <th class="text-center"><g:message code="product.label" default="product"/></th>

        <th class="text-center"><g:message code="productGroup.label" default="Product Group"/></th>

        <th class="text-center"><g:message code="stockCard.balance.label" default="Balance"/></th>

        <th class="text-center"><g:message code="product.minimumStock.label" default="minimumStock"/></th>

        <th class="text-center"><g:message code="default.manage.label" default="Manage"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${productInstanceList}" status="i" var="productInstance">
        <g:set var="stockCard" value="${productInstance.stockCard}"/>
        <g:set var="balance" value="${stockCard?.balance ?: BigDecimal.ZERO}"/>
        <g:set var="classBalance" value="${productInstance.minimumStock != null ? (productInstance.minimumStock > balance ? 'underMinimumStock': (balance - productInstance.minimumStock )/ productInstance.minimumStock < 0.1 ? "soonMinimumStock": '') : ''}"/>
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

          <td><g:link action="show" id="${productInstance.id}">${productInstance}</g:link></td>

          <td>${fieldValue(bean: productInstance, field: "productGroup")}</td>

          <td class="right ${classBalance}"><g:formatNumber number="${balance}" formatName="default.qty.format"/> ${productInstance.uom.name}</td>
          <td class="right">
            <g:if test="${productInstance.minimumStock}">
              <g:formatNumber number="${productInstance.minimumStock}" formatName="default.qty.format"/> ${productInstance.uom.name}
            </g:if>
          </td>

          <td class="center">
            <g:link class="btn btn-info" action="show" id="${productInstance.id}"><g:message code="default.button.show.label" default="Show"/></g:link>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>

    <div class="text-center">
      <g:paginate total="${productInstanceCount ?: 0}"/>
    </div>
  </div>
</div>
</body>
</html>
