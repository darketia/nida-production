<%@ page import="nida.production.HousingEstate" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'housingEstate.label', default: 'HousingEstate')}"/>
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

    <g:if test="${housingEstateInstance?.code}">
      <dt><g:message code="housingEstate.code.label" default="Code"/></dt>

      <dd><g:fieldValue bean="${housingEstateInstance}" field="code"/></dd>

    </g:if>

    <g:if test="${housingEstateInstance?.name}">
      <dt><g:message code="housingEstate.name.label" default="Name"/></dt>

      <dd><g:fieldValue bean="${housingEstateInstance}" field="name"/></dd>

    </g:if>

    <g:if test="${housingEstateInstance?.subDistrict}">
      <dt><g:message code="housingEstate.subDistrict.label" default="Sub District"/></dt>

      <dd>${housingEstateInstance?.subDistrict?.encodeAsHTML()}</dd>

    </g:if>

    <g:if test="${housingEstateInstance?.deliveryPrice}">
      <dt><g:message code="housingEstate.deliveryPrice.label" default="Delivery Price"/></dt>

      <dd><g:formatNumber number="${housingEstateInstance?.deliveryPrice}" formatName="default.currency.format"/> บาท</dd>
    </g:if>


    <g:if test="${housingEstateInstance?.creator}">
      <dt><g:message code="default.creator.label" default="Creator"/></dt>
      <dd>${housingEstateInstance?.creator?.encodeAsHTML()}</dd>
    </g:if>

    <g:if test="${housingEstateInstance?.updater}">
      <dt><g:message code="default.updater.label" default="Updater"/></dt>
      <dd>${housingEstateInstance?.updater?.encodeAsHTML()}</dd>
    </g:if>

    <g:if test="${housingEstateInstance?.dateCreated}">
      <dt><g:message code="default.dateCreated.label" default="Date Created"/></dt>
      <dd><g:formatDate date="${housingEstateInstance?.dateCreated}" formatName="default.datetime.format"/></dd>
    </g:if>

    <g:if test="${housingEstateInstance?.lastUpdated}">
      <dt><g:message code="default.lastUpdated.label" default="Last Updated"/></dt>
      <dd><g:formatDate date="${housingEstateInstance?.lastUpdated}" formatName="default.datetime.format"/></dd>
    </g:if>


    <g:form url="[resource: housingEstateInstance, action: 'delete']" method="DELETE">
      <g:link class="btn btn-primary" action="edit" resource="${housingEstateInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:form>

    <g:render template="/shared/updaterDetail" model="[instance: housingEstateInstance]"/>
  </dl>

</div>
</body>
</html>
