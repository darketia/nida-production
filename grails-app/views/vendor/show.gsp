<%@ page import="nida.production.Vendor" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'vendor.label', default: 'vendor')}"/>
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

    <g:if test="${vendorInstance?.code}">
      <dt><g:message code="vendor.code.label" default="Code"/></dt>

      <dd><g:fieldValue bean="${vendorInstance}" field="code"/></dd>

    </g:if>

    <g:if test="${vendorInstance?.name}">
      <dt><g:message code="vendor.name.label" default="Name"/></dt>
      <dd><g:fieldValue bean="${vendorInstance}" field="name"/></dd>
    </g:if>

    <g:if test="${vendorInstance?.address}">
      <dt><g:message code="vendor.address.label" default="address"/></dt>
      <dd>
        <g:render template="/shared/showTextArea" model="[remark: vendorInstance?.address]"/>
      </dd>
    </g:if>

    <g:if test="${vendorInstance?.taxNo}">
      <dt><g:message code="vendor.taxNo.label" default="taxNo"/></dt>
      <dd><g:fieldValue bean="${vendorInstance}" field="taxNo"/></dd>
    </g:if>
    <g:if test="${vendorInstance?.telNo}">
      <dt><g:message code="vendor.telNo.label" default="telNo"/></dt>
      <dd><g:fieldValue bean="${vendorInstance}" field="telNo"/></dd>
    </g:if>
    <g:if test="${vendorInstance?.email}">
      <dt><g:message code="vendor.email.label" default="email"/></dt>
      <dd><g:fieldValue bean="${vendorInstance}" field="email"/></dd>
    </g:if>
    <g:if test="${vendorInstance?.contactPerson}">
      <dt><g:message code="vendor.contactPerson.label" default="contactPerson"/></dt>
      <dd><g:fieldValue bean="${vendorInstance}" field="contactPerson"/></dd>
    </g:if>


    <g:form url="[resource: vendorInstance, action: 'delete']" method="DELETE">
      <g:link class="btn btn-primary" action="edit" resource="${vendorInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:form>

    <g:render template="/shared/updaterDetail" model="[instance: vendorInstance]"/>
  </dl>

</div>
</body>
</html>
