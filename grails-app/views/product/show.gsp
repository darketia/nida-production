
<%@ page import="nida.production.Product" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}"/>
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
    
    <g:if test="${productInstance?.code}">
      <dt><g:message code="product.code.label" default="Code"/></dt>
      
      <dd><g:fieldValue bean="${productInstance}" field="code"/></dd>
      
    </g:if>
    
    <g:if test="${productInstance?.name}">
      <dt><g:message code="product.name.label" default="Name"/></dt>
      
      <dd><g:fieldValue bean="${productInstance}" field="name"/></dd>
      
    </g:if>
    
    <g:if test="${productInstance?.productGroup}">
      <dt><g:message code="product.productGroup.label" default="Product Group"/></dt>
      
      <dd>${productInstance?.productGroup?.encodeAsHTML()}</dd>
      
    </g:if>
    
    <g:if test="${productInstance?.uom}">
      <dt><g:message code="product.uom.label" default="Uom"/></dt>      
      <dd>${productInstance?.uom?.encodeAsHTML()}</dd>      
    </g:if>

    <g:if test="${productInstance.minimumStock != null}">
      <dt><g:message code="product.minimumStock.label" default="minimumStock"/></dt>
      <dd><g:formatNumber number="${productInstance.minimumStock}" formatName="default.qty.format"/></dd>
    </g:if>

    <dt><g:message code="product.cancel.label" default="cancel"/></dt>
    <dd><g:formatBoolean boolean="${productInstance.cancel?:false}"/></dd>

    <dt>
      <g:message code="product.productPrices.label" default="productPrices"/>
    </dt>
    <dd>
      <div class="col-sm-3"  style="padding-left: 0">
        <table class="table">
          <tr>
            <th class="center" style="min-width: 150px;" ><g:message code="productPrice.priceType.label"/></th>
            <th class="center" ><g:message code="productPrice.price.label"/></th>
          </tr>
          <g:each in="${productPrices}" var="productPrice" status="i">
            <tr>
              <td class="right">${message(code: "enum.PriceType.${productPrice.priceType}")}</td>
              <td class="right">
                <div class="input-group" style="width: 220px; float: right;">
                  <g:formatNumber number="${productPrice.price}" formatName="default.currency.format"/> บาท
                </div>
              </td>
            </tr>
          </g:each>
        </table>
      </div>
    </dd>

    <g:form url="[resource: productInstance, action: 'delete']" method="DELETE">
      <g:link class="btn btn-primary" action="edit" resource="${productInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      <g:link class="btn btn-warning" action="toggleCancel" resource="${productInstance}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
        <g:if test="${productInstance.cancel}"><g:message code="product.notCancel.label" default="Cancel"/></g:if>
        <g:else><g:message code="product.cancel.label" default="Cancel"/></g:else>
      </g:link>
      <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:form>

    <g:render template="/shared/updaterDetail" model="[instance: productInstance]"/>

  </dl>

</div>
</body>
</html>
