<%@ page import="nida.production.StockCard" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'stockCard.label', default: 'StockCard')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
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

    <dt><g:message code="stockCard.product.label" default="Product"/></dt>
    <dd>${stockCardInstance.product.encodeAsHTML()}</dd>

    <dt><g:message code="stockCard.balance.label" default="Balance"/></dt>
    <dd class="${stockCardInstance.product.minimumStock != null ? (stockCardInstance.product.minimumStock > stockCardInstance.balance ? 'underMinimumStock': (stockCardInstance.balance - stockCardInstance.product.minimumStock )/ stockCardInstance.product.minimumStock < 0.1 ? "soonMinimumStock": '') : ''}">
      <g:formatNumber number="${stockCardInstance.balance}" formatName="default.qty.format"/> ${stockCardInstance.product.uom.name}
    </dd>

    <dt><g:message code="product.minimumStock.label" default="minimumStock"/></dt>
    <dd>
        <g:if test="${stockCardInstance.product.minimumStock != null}">
          <g:formatNumber number="${stockCardInstance.product.minimumStock}" formatName="default.qty.format"/> ${stockCardInstance.product.uom.name}
        </g:if>
    </dd>

    <div class="panel panel-success" style="padding: 0">
      <div class="panel-heading"><g:message code="stockCard.details.label" default="details"/></div>

      <div class="panel-body">
        <table class="table">
          <tr>
            <th class="center"><g:message code="default.ord.label"/></th>
            <th class="center"><g:message code="stockCard.date.label"/></th>
            <th class="center"><g:message code="stockCard.qty.in.label"/> (${stockCardInstance.product.uom.name})</th>
            <th class="center"><g:message code="stockCard.qty.out.label"/> (${stockCardInstance.product.uom.name})</th>
            <th class="center"><g:message code="stockCard.balance.label"/> (${stockCardInstance.product.uom.name})</th>
            <th class="center"><g:message code="default.updater.label"/></th>
            <th class="center"></th>
          </tr>
          <tbody id="detailTbody">

          <g:set var="qtyIn" value="${BigDecimal.ZERO}"/>
          <g:set var="qtyOut" value="${BigDecimal.ZERO}"/>
          <g:each in="${stockCardInstanceList}" var="stockCard" status="i">
            <g:set var="prevBalance" value="${stockCard.balance - stockCard.qty}"/>
            <g:if test="${i == 0 && prevBalance != BigDecimal.ZERO}">
              <tr>
                <td class="right"></td>
                <td class="center">
                  ยอดยกมา
                </td>
                <td class="right">
                  <g:if test="${prevBalance >= BigDecimal.ZERO}">
                    <g:set var="qtyIn" value="${prevBalance}"/>
                    <g:formatNumber number="${qtyIn}" formatName="default.qty.format"/>
                  </g:if>
                </td>
                <td class="right">
                  <g:if test="${prevBalance < BigDecimal.ZERO}">
                    <g:set var="qtyOut" value="${prevBalance}"/>
                    <g:formatNumber number="${qtyOut}" formatName="default.qty.absolute.format"/>
                  </g:if>
                </td>
                <td class="right">
                  <g:formatNumber number="${prevBalance}" formatName="default.qty.format"/>
                </td>
                <td class="left"></td>
                <td class="center"></td>
              </tr>

            </g:if>
            <tr>
              <td class="right">${(i as Integer) + 1}.</td>
              <td class="center">
                <g:formatDate date="${stockCard.date}" formatName="default.datetime.format" />
              </td>
              <td class="right">
                <g:if test="${stockCard.qty >= BigDecimal.ZERO}">
                  <g:formatNumber number="${stockCard.qty}" formatName="default.qty.format"/>
                </g:if>
              </td>
              <td class="right">
                <g:if test="${stockCard.qty < BigDecimal.ZERO}">
                  <g:formatNumber number="${stockCard.qty}" formatName="default.qty.absolute.format"/>
                </g:if>
              </td>
              <td class="right">
                <g:formatNumber number="${stockCard.balance}" formatName="default.qty.format"/>
              </td>
              <td class="center">
                ${stockCard.updater} วันที่ <span style="color:blue;"><g:formatDate date="${stockCard.lastUpdated}" formatName="default.datetime.format"/></span>
              </td>
              <td class="center">
                <g:if test="${stockCard.saleOrderDetail || stockCard.saleOrderReturnDetail}">
                  <g:set var="saleOrder" value="${stockCard.saleOrderDetail ? stockCard.saleOrderDetail.saleOrder : stockCard.saleOrderReturnDetail?.saleOrder}"/>
                  มาจาก
                  <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_SALE_ORDER">
                    <g:link controller="saleOrder" action="show" id="${saleOrder.id}">
                      ${saleOrder.code}
                    </g:link>
                  </sec:ifAnyGranted>
                  <sec:ifNotGranted roles="ROLE_ADMIN,ROLE_PAGE_SALE_ORDER">
                    ${saleOrder.code}
                  </sec:ifNotGranted>
                </g:if>
                <g:else>
                  <g:form url="[resource: stockCard, action: 'delete']" method="DELETE">
                    <g:link class="btn btn-primary" action="edit" resource="${stockCard}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
                    <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
                  </g:form>
                </g:else>
              </td>
            </tr>
          </g:each>


          </tbody>
          <tfoot>
          <tr style="font-weight: bold">
            <td colspan="2" class="right">สรุป</td>
            <td class="right">
              <g:formatNumber number="${qtyIn + (stockCardInstanceList.findAll {
                it.qty >= BigDecimal.ZERO
              }?.qty?.sum() ?: BigDecimal.ZERO)}" formatName="default.qty.format"/>
            </td>
            <td class="right">
              <g:formatNumber number="${qtyOut + (stockCardInstanceList.findAll {
                it.qty < BigDecimal.ZERO
              }?.qty?.sum() ?: BigDecimal.ZERO)}" formatName="default.qty.absolute.format"/>
            </td>
            <td class="right">
              <g:formatNumber number="${stockCardInstance.balance}" formatName="default.qty.format"/>
            </td>
            <td></td>
            <td></td>
          </tr>
          </tfoot>
        </table>

      </div>
    </div>

    %{--<g:render template="/shared/updaterDetail" model="[instance: stockCardInstance]"/>--}%
  </dl>

</div>
</body>
</html>
