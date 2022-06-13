<%@ page import="nida.production.Company" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'company.label', default: 'Company')}"/>
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

    <g:if test="${companyInstance?.code}">
      <dt><g:message code="company.code.label" default="Code"/></dt>

      <dd><g:fieldValue bean="${companyInstance}" field="code"/></dd>

    </g:if>

    <g:if test="${companyInstance?.name}">
      <dt><g:message code="company.name.label" default="Name"/></dt>

      <dd><g:fieldValue bean="${companyInstance}" field="name"/></dd>

    </g:if>

    <g:if test="${companyInstance?.address}">
      <dt><g:message code="company.address.label" default="Address"/></dt>

      <dd><g:fieldValue bean="${companyInstance}" field="address"/></dd>

    </g:if>

    <g:if test="${companyInstance?.taxNo}">
      <dt><g:message code="company.taxNo.label" default="Tax No"/></dt>

      <dd><g:fieldValue bean="${companyInstance}" field="taxNo"/></dd>

    </g:if>

    <dt><g:message code="company.vatRate.label" /></dt>

    <dd><g:fieldValue bean="${companyInstance}" field="vatRate"/></dd>


    <g:link class="btn btn-primary" action="edit" resource="${companyInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>

    <g:render template="/shared/updaterDetail" model="[instance: companyInstance]"/>
  </dl>
</div>
</body>
</html>
