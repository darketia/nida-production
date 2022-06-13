<%@ page import="nida.production.SaleOrderDetailCmd" %>
<g:set var="saleOrderReturnDetail" value="${(SaleOrderDetailCmd) saleOrderReturnDetail}"/>

<tr>
  <g:hiddenField id="returnDetail-id-${i}" name="saleOrderReturnDetails[${i}].id" value="${saleOrderReturnDetail.id}"/>
  <g:hiddenField id="returnDetail-refReturnCode-${i}" name="saleOrderReturnDetails[${i}].refReturnCode" value="${saleOrderReturnDetail.refReturnCode}"/>
  <g:hiddenField id="returnDetail-product-id-${i}" name="saleOrderReturnDetails[${i}].product.id" value="${saleOrderReturnDetail.product.id}"/>
  <g:hiddenField id="returnDetail-pricePerUnit-${i}" name="saleOrderReturnDetails[${i}].pricePerUnit" value="${saleOrderReturnDetail.pricePerUnit}"/>
  <td class="right" id="returnDetail-ord-${i}">คืน ${(i as Integer) + 1}.</td>
  <td class="left"  id="returnDetail-product-code-${i}">
    ${saleOrderReturnDetail.product.code}
  </td>
  <td class="left" >
    ${saleOrderReturnDetail.product.name}(${saleOrderReturnDetail.refReturnCode})
  </td>
  <td class="right" >
    <span><g:formatNumber number="${saleOrderReturnDetail.pricePerUnit}" formatName="default.currency.format"/></span>
    <span>บาท</span>
  </td>
  <td class="right" >
    <div class="input-group" style="width: 200px;float: right">
      <g:field type="number" id="returnDetail-qty-${i}" name="saleOrderReturnDetails[${i}].qty" value="${saleOrderReturnDetail.qty}"
               class="form-control" style="text-align: right;" step="0.01" min="0.01" required="" onchange="calPrice('returnDetail', ${i})"/>
      <span class="input-group-addon">${saleOrderReturnDetail.product.uom.name}</span>
    </div>
  </td>
  <td class="right" >
    <span id="returnDetail-price-${i}"><g:formatNumber number="${saleOrderReturnDetail.pricePerUnit * saleOrderReturnDetail.qty}" formatName="default.currency.format"/></span>
    <span>บาท</span>
  </td>
  <td class="text-center" >
    <div class="glyphicon glyphicon-trash btn btn-danger" onclick="deleteReturnDetail(this)"></div>
  </td>
</tr>