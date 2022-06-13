
<%@ page import="nida.production.SubDistrict" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'subDistrict.label', default: 'SubDistrict')}"/>
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
    
    <g:if test="${subDistrictInstance?.code}">
      <dt><g:message code="subDistrict.code.label" default="Code"/></dt>
      
      <dd><g:fieldValue bean="${subDistrictInstance}" field="code"/></dd>
      
    </g:if>
    
    <g:if test="${subDistrictInstance?.name}">
      <dt><g:message code="subDistrict.name.label" default="Name"/></dt>
      
      <dd><g:fieldValue bean="${subDistrictInstance}" field="name"/></dd>
      
    </g:if>
    
    <g:if test="${subDistrictInstance?.creator}">
      <dt><g:message code="default.creator.label" default="Creator"/></dt>
      <dd>${subDistrictInstance?.creator?.encodeAsHTML()}</dd>
    </g:if>

    <g:if test="${subDistrictInstance?.updater}">
      <dt><g:message code="default.updater.label" default="Updater"/></dt>
      <dd>${subDistrictInstance?.updater?.encodeAsHTML()}</dd>
    </g:if>

    <g:if test="${subDistrictInstance?.dateCreated}">
      <dt><g:message code="default.dateCreated.label" default="Date Created"/></dt>
      <dd><g:formatDate date="${subDistrictInstance?.dateCreated}" formatName="default.datetime.format"/></dd>
    </g:if>

    <g:if test="${subDistrictInstance?.lastUpdated}">
      <dt><g:message code="default.lastUpdated.label" default="Last Updated"/></dt>
      <dd><g:formatDate date="${subDistrictInstance?.lastUpdated}" formatName="default.datetime.format"/></dd>
    </g:if>
    

    <g:form url="[resource: subDistrictInstance, action: 'delete']" method="DELETE">
      <g:link class="btn btn-primary" action="edit" resource="${subDistrictInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:form>

    <g:render template="/shared/updaterDetail" model="[instance: subDistrictInstance]"/>
  </dl>

</div>
</body>
</html>
