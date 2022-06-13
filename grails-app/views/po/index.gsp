<g:set var="authPoTypes" value="${nida.production.SecUserPoType.findAllBySecUser(currentUser)?.poType?.flatten()}"/>
<%@ page import="nida.production.PriceType; nida.production.SaleOrder" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'po.label', default: 'SaleOrder')}"/>
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
      <g:form action="index" class="form-horizontal skipWaiting">
        <g:set var="queryParams" value="${[
            'dateFrom'   : params.dateFrom
            , 'dateTo'   : params.dateTo
            , 'code'     : params.code
            , 'vendor.id': params.vendor?.id
            , 'poType.id': params.poType?.id
            , 'poItemName' : params.poItemName
            , 'poStatus' : params.list('poStatus') ?: []
        ]}"/>

        <div class="form-group">
          <label for="code" class="col-sm-2 control-label">
            <g:message code="po.code.label"/>
          </label>

          <div class="col-sm-3">
            <g:textField name="code" value="${params.code}" class="form-control"/>
          </div>

          <label for="dateFrom" class="col-sm-2 control-label">
            <g:message code="po.date.label"/>
          </label>

          <div class="col-sm-3">
            <g:render template="/shared/dateRange" name="dateFrom"
                      model="[dateFromName: 'dateFrom', dateFromValueString: params.dateFrom
                              , dateToName: 'dateTo', dateToValueString: params.dateTo
                              , isRequired: false]"/>
          </div>
        </div>

        <div class="form-group">
          <label for="poType.id" class="col-sm-2 control-label">
            <g:message code="po.poType.label"/>
          </label>

          <div class="col-sm-3">
            <g:select name="poType.id" from="${authPoTypes ? authPoTypes.sort{it.code}: nida.production.PoType.list().sort{it.code}}"
                      noSelection="['': message(code: 'default.select.label')]"
                      optionKey="id" value="${params.poType?.id}" class="form-control"/>
          </div>

          <label class="col-sm-2 control-label">
            <g:message code="po.vendor.label"/>
          </label>

          <div class="col-sm-3">
            <ts:autoComplete name="vendor" action="searchVendor" value="${nida.production.Vendor.get(params.getLong('vendor.id'))}"/>
          </div>
        </div>

        <div class="form-group">
          <label for="poStatus" class="col-sm-2 control-label">
            <g:message code="po.poStatus.label"/>
          </label>

          <div class="col-sm-3" style="margin-top: 5px">
            <g:each in="${nida.production.PoStatus.values()}" var="poStatus">
              <label style="margin-right: 10px">
                <g:checkBox name="poStatus" value="${poStatus}" checked="${(params.list('poStatus') ?: []).contains(poStatus.toString())}"/>
                <g:message code="enum.poStatus.${poStatus}"/>
              </label>
            </g:each>
          </div>
        </div>

        <div class="form-group">
          <label for="code" class="col-sm-2 control-label">
            รายละเอียดในรายการ
          </label>

          <div class="col-sm-3">
            <g:textField name="poItemName" value="${params.poItemName}" class="form-control"/>
          </div>

        </div>

        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-6">
            <g:submitButton name="search" class="btn btn-primary" value="${message(code: 'default.button.search.label')}"/>
            <g:link class="btn btn-warning" action="index"><g:message code="default.button.reset.label"/></g:link>

            <sec:ifAnyGranted roles="ROLE_ADMIN">
              <g:submitButton name="excel" class="btn btn-info" value="${message(code: 'default.button.excel.label')}"/>
            </sec:ifAnyGranted>
          </div>
        </div>
      </g:form>
    </div>

    <div class="row">
      <div class="table-responsive">
        <table class="table">
          <thead>
          <tr>
            <th class="text-center"><g:message code="po.date.label"/></th>
            <th class="text-center"><g:message code="po.code.label"/></th>
            <th class="text-center"><g:message code="po.poType.label"/></th>
            <th class="text-center"><g:message code="po.vendor.label"/></th>
            <th class="text-center"><g:message code="po.poStatus.label"/></th>
            <th class="text-center"><g:message code="poDetail.price.label"/></th>
            <th class="text-center"><g:message code="po.remark.label"/></th>
            <th class="text-center"><g:message code="default.manage.label"/></th>
          </tr>
          </thead>
          <tbody>
          <g:each in="${poInstanceList}" status="i" var="poInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
              <td class="center"><g:formatDate date="${poInstance.date}"/></td>

              <td>
                <g:link action="show" id="${poInstance.id}">
                  ${fieldValue(bean: poInstance, field: "code")}
                </g:link>
              </td>
              <td>${poInstance?.poType}</td>
              <td>${fieldValue(bean: poInstance, field: "vendor")}</td>
              <td class="center"><g:message code="enum.poStatus.${poInstance?.poStatus}"/></td>
              <td class="right"><g:formatNumber number="${poInstance.amountWithVat}" formatName="default.currency.format"/></td>
              <td><g:render template="/shared/showTextArea" model="[remark: poInstance.remark]"/></td>

              <td >
                <g:link class="btn btn-info" action="show" id="${poInstance.id}"><g:message code="default.button.show.label" default="Show"/></g:link>
                <g:link class="btn btn-primary" action="edit" id="${poInstance.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
                <g:if test="${poInstance.poStatus == nida.production.PoStatus.NEW}">
                  <g:link class="btn btn-warning" action="close" id="${poInstance.id}">ปิด</g:link>
                </g:if>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>

        <div class="text-center">
          <g:paginate total="${poInstanceCount ?: 0}" params="${queryParams}"/>
        </div>
      </div>
    </div>
</body>
</html>
