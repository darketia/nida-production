<%@ page import="nida.production.SaleOrder" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'saleOrder.label', default: 'SaleOrder')}"/>
  <title><g:message code="saleOrder.show.label"/></title>
  <script>
    $(document).ready(function(){
      if(${params.print == 'shortInvoice'}){
        window.open("${createLink(action: 'shortInvoice', params: ['id': saleOrderInstance.id])}",'_blank');
      }else if(${params.print == 'a4Invoice'}){
        window.open("${createLink(action: 'a4Invoice', params: ['id': saleOrderInstance.id, 'custOnly': params.custOnly])}",'_blank');
      }
    })
  </script>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="saleOrder.show.label"/></a>
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

  <g:form url="[resource: saleOrderInstance, action: 'delete']" class="form-horizontal" method="DELETE">
    <fieldset class="form">

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="saleOrder.code.label"/>
        </label>

        <div class="col-sm-4">
          ${saleOrderInstance?.code}
        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="saleOrder.date.label" default="Date"/>
        </label>

        <div class="col-sm-4">
          <g:formatDate date="${saleOrderInstance?.date}"/>
        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="saleOrder.customer.label" default="Customer"/>
        </label>

        <div class="col-sm-4">
          ${saleOrderInstance?.customer}
        </div>
      </div>

      <div class="panel panel-success" style="padding: 0" id="customerPanel">
        <div class="panel-heading"><g:message code="customer.label" default="customer"/></div>

        <div class="panel-body">
          <div class="form-group">
            <label class="show-label col-sm-3">
              <g:message code="saleOrder.name.label" default="name"/>
            </label>

            <div class="col-sm-4">
              ${saleOrderInstance?.name}
            </div>
          </div>

          <div class="form-group">
            <label class="show-label col-sm-3">
              <g:message code="saleOrder.address.label" default="address"/>
            </label>

            <div class="col-sm-4">
              ${saleOrderInstance?.address}
            </div>
          </div>

          <div class="form-group">
            <label class="show-label col-sm-3">
              <g:message code="saleOrder.telNo.label" default="telNo"/>
            </label>

            <div class="col-sm-4">
              ${saleOrderInstance?.telNo}
            </div>
          </div>

        </div>
      </div>






      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="saleOrder.priceType.label" default="Price Type"/>
        </label>

        <div class="col-sm-4">
          <g:message code="enum.PriceType.${saleOrderInstance?.priceType}"/>
        </div>
      </div>

      <div class="panel panel-success" style="padding: 0" id="productPanel">
        <div class="panel-heading"><g:message code="saleOrder.saleOrderDetails.label" default="saleOrderDetails"/></div>

        <div class="panel-body">
          <table class="table">
            <tr>
              <th class="center"><g:message code="default.ord.label"/></th>
              <th class="center" colspan="2"><g:message code="saleOrderDetail.product.label"/></th>
              <th class="center"><g:message code="saleOrderDetail.pricePerUnit.label"/></th>
              <th class="center"><g:message code="saleOrderDetail.qty.label"/></th>
              <th class="center" style="width: 250px"><g:message code="saleOrderDetail.price.label"/></th>
              <th class="center"></th>
            </tr>
            <tbody id="detailTbody">
            <g:each in="${saleOrderInstance?.saleOrderDetails}" var="saleOrderDetail" status="i">
              <tr>
                <td class="right">${(i as Integer) + 1}.</td>
                <td class="left">
                  ${saleOrderDetail.product.code}
                </td>
                <td class="left" >
                  ${saleOrderDetail.product.name}
                </td>
                <td class="right" >
                  <g:formatNumber number="${saleOrderDetail.pricePerUnit}" formatName="default.currency.format"/> บาท
                </td>
                <td class="right" >
                  <g:formatNumber number="${saleOrderDetail.qty}" formatName="default.qty.format"/> ${saleOrderDetail.product.uom.name}
                </td>
                <td class="right" >
                  <g:formatNumber number="${saleOrderDetail.pricePerUnit * saleOrderDetail.qty}" formatName="default.currency.format"/> บาท
                </td>
              </tr>
            </g:each>
            <tr style="font-weight: bold">
              <td colspan="5" class="right">รวมราคาสินค้า</td>
              <td class="right">
                <g:formatNumber number="${saleOrderInstance?.saleOrderDetails?.sum{it.pricePerUnit * it.qty}}" formatName="default.currency.format"/> บาท
              </td>
              <td></td>
            </tr>
            <g:each in="${saleOrderInstance?.saleOrderReturnDetails}" var="saleOrderReturnDetail" status="i">
              <tr>
                <td class="right">คืน ${(i as Integer) + 1}.</td>
                <td class="left">
                  ${saleOrderReturnDetail.product.code}
                </td>
                <td class="left" >
                  ${saleOrderReturnDetail.product.name} (${saleOrderReturnDetail.refReturnCode})
                </td>
                <td class="right" >
                  <g:formatNumber number="${saleOrderReturnDetail.pricePerUnit}" formatName="default.currency.format"/> บาท
                </td>
                <td class="right" >
                  <g:formatNumber number="${saleOrderReturnDetail.qty}" formatName="default.qty.format"/> ${saleOrderReturnDetail.product.uom.name}
                </td>
                <td class="right" >
                  <g:formatNumber number="${saleOrderReturnDetail.pricePerUnit * saleOrderReturnDetail.qty}" formatName="default.currency.format"/> บาท
                </td>
              </tr>
            </g:each>
            <tr style="font-weight: bold">
              <td colspan="5" class="right">รวมราคาคืน</td>
              <td class="right">
                <g:formatNumber number="${saleOrderInstance?.saleOrderReturnDetails?.sum{it.pricePerUnit * it.qty}}" formatName="default.currency.format"/> บาท
              </td>
              <td></td>
            </tr>
            </tbody>

            <tfoot>
            <tr>
              <td colspan="5" class="right">
                <g:message code="saleOrder.deliveryPrice.label" default="Delivery Price"/>
                ${saleOrderInstance.deliveryHousingEstate?.fullName}
                ${saleOrderInstance.deliveryTrip ? "${saleOrderInstance.deliveryTrip} เที่ยว":''}
              </td>
              <td class="right">
                <g:formatNumber number="${saleOrderInstance?.deliveryPrice}" formatName="default.currency.format"/> บาท
              </td>
              <td></td>
            </tr>
            <tr>
              <td colspan="5" class="right">
                <g:message code="saleOrder.discountAmount.label" default="Discount Amount"/>
              </td>
              <td class="right">
                <g:formatNumber number="${saleOrderInstance?.discountAmount}" formatName="default.currency.format"/> บาท
              </td>
              <td></td>
            </tr>
            <tr style="font-weight: bold">
              <td colspan="5" class="right">ราคารวมสุทธิ</td>
              <td class="right">
                <g:formatNumber number="${saleOrderInstance.amount}" formatName="default.currency.format"/> บาท
              </td>
              <td></td>
            </tr>
            <tr>
              <td colspan="5" class="right">
                <g:message code="saleOrder.receiveAmount.label" default="Receive Amount"/>
              </td>
              <td class="right">
                <g:formatNumber number="${saleOrderInstance.receiveAmount}" formatName="default.currency.format"/> บาท
              </td>
              <td></td>
            </tr>
            <tr style="font-weight: bold">
              <td colspan="5" class="right">
                <g:message code="saleOrder.changeAmount.label" default="Change Amount"/>
              </td>
              <td class="right">
                <g:formatNumber number="${(saleOrderInstance.receiveAmount?:0) - (saleOrderInstance.amount?:0)}" formatName="default.currency.format"/> บาท
              </td>
              <td></td>
            </tr>
            </tfoot>
          </table>

        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="saleOrder.remark.label" default="Remark"/>
        </label>

        <div class="col-sm-8">
          <g:render template="/shared/showTextArea" model="[remark:saleOrderInstance?.remark]"/>
        </div>
      </div>

      <g:link class="btn btn-info" action="a4Invoice" target="_blank" resource="${saleOrderInstance}"><g:message code="saleOrder.a4Invoice.label"/></g:link>
      <g:link class="btn btn-warning" action="a4Invoice" target="_blank" resource="${saleOrderInstance}" params="[custOnly:true]"><g:message code="saleOrder.a4Invoice.custOnly.label"/></g:link>
      <g:link class="btn btn-primary" action="edit" resource="${saleOrderInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>

      <g:render template="/shared/updaterDetail" model="[instance: saleOrderInstance]"/>

    </fieldset>

  </g:form>
</body>
</html>
