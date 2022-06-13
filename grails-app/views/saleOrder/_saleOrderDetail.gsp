<%@ page import="nida.production.SaleOrderDetailCmd" %>
<g:set var="saleOrderDetail" value="${(SaleOrderDetailCmd) saleOrderDetail}"/>

<tr>
  <g:hiddenField id="detail-id-${i}" name="saleOrderDetails[${i}].id" value="${saleOrderDetail.id}"/>
  <g:hiddenField id="detail-product-id-${i}" name="saleOrderDetails[${i}].product.id" value="${saleOrderDetail.product.id}"/>
  <g:hiddenField id="detail-pricePerUnit-${i}" name="saleOrderDetails[${i}].pricePerUnit" value="${saleOrderDetail.pricePerUnit}"/>
  <td class="right"  id="detail-ord-${i}">${(i as Integer) + 1}.</td>
  <td class="left"  id="detail-product-code-${i}">
    ${saleOrderDetail.product.code}
  </td>
  <td class="left" >
    ${saleOrderDetail.product.name}
  </td>
  <td class="right" >
    <span><g:formatNumber number="${saleOrderDetail.pricePerUnit}" formatName="default.currency.format"/></span>
    <span>บาท</span>
  </td>
  <td class="right" >
    <div class="input-group" style="width: 200px;float: right">
      <g:field type="number" id="detail-qty-${i}" name="saleOrderDetails[${i}].qty" value="${saleOrderDetail.qty}"
               class="form-control" style="text-align: right;" step="0.01" min="0.01" required="" onchange="calPrice('detail', ${i})"/>
      <span class="input-group-addon">${saleOrderDetail.product.uom.name}</span>
    </div>
  </td>
  <td class="right" >
    <span id="detail-price-${i}"><g:formatNumber number="${saleOrderDetail.pricePerUnit * saleOrderDetail.qty}" formatName="default.currency.format"/></span>
    <span>บาท</span>
  </td>
  <td class="text-center" >
    <div class="glyphicon glyphicon-trash btn btn-danger" onclick="deleteDetail(this)"></div>
  </td>
</tr>