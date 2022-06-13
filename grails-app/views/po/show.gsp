<%@ page import="nida.production.Po; nida.production.PoStatus" %>
<g:set var="isEnableScale" value="${poInstance?.enableScale ?: false}"/>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'po.label', default: 'Po')}"/>
  <title><g:message code="po.show.label"/></title>
  <style>
  #poDetailTable td {
    vertical-align: top !important;
  }
  </style>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="po.show.label"/></a>
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

  <g:form url="[resource: poInstance, action: 'delete']" class="form-horizontal" method="DELETE">
    <fieldset class="form">

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.code.label"/>
        </label>

        <div class="col-sm-4">
          ${poInstance?.code}
        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.date.label" default="Date"/>
        </label>

        <div class="col-sm-4">
          <g:formatDate date="${poInstance?.date}"/>
        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.poType.label"/>
        </label>

        <div class="col-sm-4">
          ${poInstance?.poType}
        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.enableScale.label"/>
        </label>

        <div class="col-sm-4">
          <g:formatBoolean boolean="${poInstance?.enableScale}"/>
        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.vendor.label"/>
        </label>

        <div class="col-sm-4">
          ${poInstance?.vendor}
        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.shipLocation.label"/>
        </label>

        <div class="col-sm-4">
          <g:message code="enum.shipLocation.${poInstance?.shipLocation}"/>
        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.deliveryDate.label"/>
        </label>

        <div class="col-sm-4">
          ${poInstance?.deliveryDate}
        </div>
      </div>


      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.paymentTerm.label"/>
        </label>

        <div class="col-sm-4">
          ${poInstance?.paymentTerm}
        </div>
      </div>


      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.poStatus.label"/>
        </label>

        <div class="col-sm-4">
          <g:message code="enum.poStatus.${poInstance?.poStatus}"/>
        </div>
      </div>

      <div class="panel panel-success" style="padding: 0" id="productPanel">
        <div class="panel-heading"><g:message code="po.poDetails.label" default="poDetails"/></div>

        <div class="panel-body">
          <table class="table" id="poDetailTable">
            <tr>
              <th class="center" style="width:50px"><g:message code="default.ord.label"/></th>
              <th class="center" style=""><g:message code="poDetail.name.label"/></th>
              <th class="center" style="width:120px"><g:message code="poDetail.packSize.label"/></th>
              <th class="center" style="width:120px"><g:message code="poDetail.packSizeQty.label"/></th>
              <th class="center" style="width:120px"><g:message code="poDetail.qty.label"/></th>
              <th class="center" style="width:120px"><g:message code="poDetail.uom.label"/></th>
              <th class="center" style="width:250px"><g:message code="poDetail.pricePerUnit.label"/></th>
              <th class="center" style="width: 250px"><g:message code="poDetail.price.label"/></th>
            </tr>
            <tbody id="detailTbody">
            <g:each in="${poInstance?.poDetails?.sort { it.id }}" var="poDetail" status="i">
              <tr>
                <td class="right">${(i as Integer) + 1}.</td>
                <td class="left">
                  <g:render template="/shared/showTextArea" model="[remark: poDetail.name]"/>
                </td>
                <td class="right">
                  <g:formatNumber number="${poDetail.packSize}" formatName="${isEnableScale ? "default.qty.format" : "default.int.format"}"/>
                </td>
                <td class="right">
                  <g:if test="${poDetail.packSize}">
                    <g:formatNumber number="${poDetail.qty / poDetail.packSize}" formatName="default.int.format"/>
                  </g:if>
                </td>
                <td class="right">
                  <g:formatNumber number="${poDetail.qty}" formatName="${isEnableScale ? "default.qty.format" : "default.int.format"}"/>
                </td>
                <td class="left">
                  ${poDetail.uom}
                </td>
                <td class="right">
                  <g:formatNumber number="${poDetail.pricePerUnit}" format="#,##0.00#"/> บาท
                </td>
                <td class="right">
                  <g:formatNumber number="${poDetail.pricePerUnit * poDetail.qty}" formatName="default.currency.format"/> บาท
                </td>
              </tr>
            </g:each>

            <tfoot>
            <tr style="font-weight: bold">
              <td colspan="7" class="right">ราคารวมก่อนส่วนลด</td>
              <td class="right">
                <g:formatNumber number="${poInstance?.amountDetail}" formatName="default.currency.format"/> บาท
              </td>
            </tr>
            <tr>
              <td colspan="7" class="right">ส่วนลด</td>
              <td class="right">
                <g:formatNumber number="${poInstance?.discountAmount}" formatName="default.currency.format"/> บาท
              </td>
            </tr>
            <tr>
              <td colspan="7" class="right">Vat ${poInstance?.vatRate}%</td>
              <td class="right">
                <g:formatNumber number="${poInstance?.vat}" formatName="default.currency.format"/> บาท
              </td>
            </tr>

            <tr style="font-weight: bold">
              <td colspan="7" class="right">ราคารวมสุทธิ</td>
              <td class="right">
                <g:formatNumber number="${poInstance.amountWithVat}" formatName="default.currency.format"/> บาท
              </td>
            </tr>
            </tfoot>
          </table>

        </div>
      </div>

      <div class="form-group">
        <label class="show-label col-sm-3">
          <g:message code="po.remark.label" default="Remark"/>
        </label>

        <div class="col-sm-8">
          <g:render template="/shared/showTextArea" model="[remark: poInstance?.remark]"/>
        </div>
      </div>

      <g:link class="btn btn-info" action="exportPdf" resource="${poInstance}">PDF</g:link>
      <g:if test="${poInstance.poStatus == PoStatus.NEW}">
        <g:link class="btn btn-primary" action="edit" resource="${poInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      </g:if>
      <g:if test="${poInstance.poStatus == PoStatus.NEW}">
        <g:link class="btn btn-warning" action="newRev" resource="${poInstance}" onclick="return confirm('ยืนยันสร้าง Rev. ใหม่');">สร้าง Rev. ใหม่</g:link>
      </g:if>
      <g:link class="btn btn-success" action="copy" resource="${poInstance}">สั่งซ้ำ</g:link>
      <g:if test="${poInstance.poStatus == PoStatus.NEW}">
        <g:actionSubmit class="btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
      </g:if>

      <g:render template="/shared/updaterDetail" model="[instance: poInstance]"/>

    </fieldset>

  </g:form>
</body>
</html>
