<%@ page import="nida.production.PriceType; nida.production.SaleOrder" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'saleOrder.label', default: 'SaleOrder')}"/>
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
            'dateFrom': params.dateFrom
            , 'dateTo': params.dateTo
            , 'code': params.code
            , 'customer.id': params.customer?.id
            , 'priceType': params.priceType
        ]}"/>

        <div class="form-group">
          <label for="code" class="col-sm-2 control-label">
            <g:message code="saleOrder.code.label"/>
          </label>
          <div class="col-sm-3">
            <g:textField name="code" value="${params.code}" class="form-control"/>
          </div>

          <label for="dateFrom" class="col-sm-2 control-label">
            <g:message code="saleOrder.date.label"/>
          </label>
          <div class="col-sm-3">
            <g:render template="/shared/dateRange" name="dateFrom"
                      model="[dateFromName: 'dateFrom', dateFromValueString: params?.dateFrom
                          , dateToName: 'dateTo', dateToValueString: params?.dateTo
                          , isRequired: false]"/>
          </div>
        </div>

        <div class="form-group">
          <label for="customer" class="col-sm-2 control-label">
            <g:message code="saleOrder.customer.label"/>
          </label>
          <div class="col-sm-3">
            <ts:autoComplete name="customer" action="searchCustomer" value="${nida.production.Customer.get(params?.getLong('customer.id'))}" />
          </div>

          <label for="priceType" class="col-sm-2 control-label">
            <g:message code="saleOrder.priceType.label"/>
          </label>
          <div class="col-sm-3">
            <g:select name="priceType" from="${nida.production.PriceType?.values()}"
                      keys="${nida.production.PriceType.values()*.name()}" valueMessagePrefix="enum.PriceType"
                      noSelection="['': message(code: 'default.select.all.label')]"
                      value="${params?.priceType}" class="form-control"/>
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
        <th class="text-center"><g:message code="saleOrder.code.label"/></th>
        <th class="text-center"><g:message code="saleOrder.date.label"/></th>
        %{--<th class="text-center"><g:message code="saleOrder.docBkBl.label"/></th>--}%
        <th class="text-center"><g:message code="saleOrder.customer.label"/></th>
        <th class="text-center"><g:message code="saleOrder.priceType.label"/></th>
        <th class="text-center"><g:message code="saleOrderDetail.price.label"/></th>
        <th class="text-center"><g:message code="saleOrder.remark.label"/></th>
        <th class="text-center"><g:message code="default.manage.label"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${saleOrderInstanceList}" status="i" var="saleOrderInstance">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

          <td class="center">
            <g:link action="show" id="${saleOrderInstance.id}">
              ${fieldValue(bean: saleOrderInstance, field: "code")}
            </g:link>
          </td>
          <td class="center"><g:formatDate date="${saleOrderInstance.date}"/></td>
          %{--<td class="center">${fieldValue(bean: saleOrderInstance, field: "docBk")}/${fieldValue(bean: saleOrderInstance, field: "docBl")}</td>--}%
          <td>${fieldValue(bean: saleOrderInstance, field: "customer")}</td>
          <td class="center"><g:message code="enum.PriceType.${saleOrderInstance?.priceType}"/></td>
          <td class="right"><g:formatNumber number="${saleOrderInstance?.saleOrderDetails?.sum { it.pricePerUnit * it.qty } + (saleOrderInstance?.deliveryPrice ?: 0)}" formatName="default.currency.format"/> บาท</td>
          <td><g:render template="/shared/showTextArea" model="[remark:saleOrderInstance.remark]"/></td>

          <td class="center">
            <g:link class="btn btn-info" action="show" id="${saleOrderInstance.id}"><g:message code="default.button.show.label" default="Show"/></g:link>
            <g:link class="btn btn-primary" action="edit" id="${saleOrderInstance.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>

    <div class="text-center">
      <g:paginate total="${saleOrderInstanceCount ?: 0}" params="${queryParams}"/>
    </div>
  </div>
</div>
</body>
</html>
