<%@ page import="nida.production.PoDetailCmd" %>
<g:set var="poDetail" value="${(PoDetailCmd) poDetail}"/>

<tr>
  <td class="right labelTd" id="detail-ord-${i}">${(i as Integer) + 1}.</td>
  <td class="left">
    <g:textArea rows="2" id="detail-name-${i}"  name="poDetails[${i}].name" value="${poDetail.name}" required="" style="width:100%" class="form-control"></g:textArea>
  </td>
  <td class="right">
    <g:field type="number" id="detail-packSize-${i}" name="poDetails[${i}].packSize" value="${poDetail.packSize}"
             class="form-control" style="text-align: right;" step="1" min="1" onchange="calQty(this)"/>
  </td>
  <td class="right">
    <g:field type="number" id="detail-packSizeQty-${i}" name="poDetails[${i}].packSizeQty" value="${poDetail.packSize ? poDetail.qty / poDetail.packSize : ""}"
             class="form-control" style="text-align: right;" step="1" min="1" onchange="calQty(this)"/>
  </td>
  <td class="right">
    <g:field type="number" id="detail-qty-${i}" name="poDetails[${i}].qty" value="${poDetail.qty}"
             class="form-control" style="text-align: right;" step="1" min="1" required="" onchange="changeQty(this)"/>
  </td>
  <td class="left">
    <g:textField id="detail-uom-${i}" name="poDetails[${i}].uom" value="${poDetail.uom}" required="" style="width:100%" class="form-control"/>
  </td>
  <td class="right">
    <div class="input-group" style="float: right">
      <g:field type="number" id="detail-pricePerUnit-${i}" name="poDetails[${i}].pricePerUnit" value="${poDetail.pricePerUnit}"
               class="form-control" style="text-align: right;" step="0.001" min="0" required="" onchange="calPrice(this)"/>
      <span class="input-group-addon">บาท</span>
    </div>
  </td>

  <td class="right labelTd">
    <span id="detail-price-${i}"><g:formatNumber number="${(poDetail.pricePerUnit!=null || poDetail.qty!=null) ? (poDetail.pricePerUnit * poDetail.qty) : java.math.BigDecimal.ZERO}" formatName="default.currency.format"/></span>
    <span>บาท</span>
  </td>

  <td class="text-center">
    <g:hiddenField id="detail-id-${i}" name="poDetails[${i}].id" value="${poDetail.id}"/>
    <div class="glyphicon glyphicon-trash btn btn-danger" onclick="deleteDetail(this)"></div>
  </td>
</tr>