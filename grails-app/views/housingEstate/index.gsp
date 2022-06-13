<%@ page import="nida.production.HousingEstate" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'housingEstate.label', default: 'HousingEstate')}"/>
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
        <g:set var="queryParams" value="${['code': params.code, 'name': params.name, 'subDistrict.id': params.subDistrict?.id,]}"/>

        <div class="form-group">
          <label for="code" class="col-sm-2 control-label"><g:message code="housingEstate.code.label" default="Code"/></label>

          <div class="col-sm-4"><g:textField class="form-control" id="code" name="code" value="${params.code}"/></div>
        </div>

        <div class="form-group">
          <label for="name" class="col-sm-2 control-label"><g:message code="housingEstate.name.label" default="Name"/></label>

          <div class="col-sm-4"><g:textField class="form-control" id="name" name="name" value="${params.name}"/></div>
        </div>

        <div class="form-group">
          <label for="subDistrict" class="col-sm-2 control-label"><g:message code="housingEstate.subDistrict.label" default="SubDistrict"/></label>

          <div class="col-sm-4">
            <g:select id="subDistrict" name="subDistrict.id" from="${nida.production.SubDistrict.list()}" optionKey="id" value="${params?.subDistrict?.id}" class="many-to-one form-control" noSelection="['': message(code: 'default.select.all.label')]"/>
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
        <g:sortableColumn class="text-center" property="code" title="${message(code: 'housingEstate.code.label', default: 'Code')}" params="${queryParams}"/>
        <g:sortableColumn class="text-center" property="name" title="${message(code: 'housingEstate.name.label', default: 'Name')}" params="${queryParams}"/>
        <th class="text-center"><g:message code="housingEstate.subDistrict.label" default="Sub District"/></th>
        <g:sortableColumn class="text-center" property="deliveryPrice" title="${message(code: 'housingEstate.deliveryPrice.label', default: 'Delivery Price')}" params="${queryParams}"/>

        <th class="text-center"><g:message code="default.manage.label" default="Manage"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${housingEstateInstanceList}" status="i" var="housingEstateInstance">
        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

          <td><g:link action="show" id="${housingEstateInstance.id}">${fieldValue(bean: housingEstateInstance, field: "code")}</g:link></td>
          <td>${fieldValue(bean: housingEstateInstance, field: "name")}</td>
          <td>${fieldValue(bean: housingEstateInstance, field: "subDistrict")}</td>
          <td class="right"><g:formatNumber number="${housingEstateInstance?.deliveryPrice}" formatName="default.currency.format"/> บาท</td>

          <td class="center">
            <g:link class="btn btn-info" action="show" id="${housingEstateInstance.id}"><g:message code="default.button.show.label" default="Show"/></g:link>
            <g:link class="btn btn-primary" action="edit" id="${housingEstateInstance.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>

    <div class="text-center">
      <g:paginate total="${housingEstateInstanceCount ?: 0}" params="${queryParams}"/>
    </div>
  </div>
</div>
</body>
</html>
