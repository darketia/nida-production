<%@ page import="nida.production.PriceType; nida.production.SaleOrder" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'rptSaleOrderDaily.label', default: 'rptSaleOrderDaily')}"/>
  <title>${entityName}</title>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">${entityName}</a>
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
      <g:form action="rpt" class="form-horizontal skipWaiting">
        <g:set var="queryParams" value="${[
            'date': params.date
            , 'customer.id': params.customer?.id
            , 'priceType': params.priceType
        ]}"/>

        <div class="form-group">
          <label for="date" class="col-sm-2 control-label">
            <g:message code="saleOrder.date.label"/>
            <span class="required-indicator">*</span>
          </label>
          <div class="col-sm-3">
            <div class="input-group date datePicker">
              <g:textField class="form-control" name="date" value="${params?.date ?: ts.formatDate(date: new Date())}" required=""/>
              <span class="input-group-addon cursor"><span class="glyphicon glyphicon-calendar"></span></span>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="customer" class="col-sm-2 control-label">
            <g:message code="saleOrder.customer.label"/>
          </label>
          <div class="col-sm-3">
            <ts:autoComplete name="customer" action="searchCustomer" value="${nida.production.Customer.get(params?.getLong('customer.id'))}" />
          </div>
        </div>
        <div class="form-group">
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
            <g:submitButton name="search" class="btn btn-primary" value="${message(code: 'default.button.report.label')}"/>
          </div>
        </div>
      </g:form>
    </div>
  </div>


</div>
</body>
</html>
