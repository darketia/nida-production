<div class="input-daterange input-group" style="width: 250px">
  <g:if test="${isRequired}">
    <g:textField name="${dateFromName}" class="form-control datePicker" value="${dateFromValueString ?: ts.formatDate(date:dateFromValue)}" required=""/>
    <span class="input-group-addon">ถึง</span>
    <g:textField name="${dateToName}" class="form-control datePicker" value="${dateToValueString ?: ts.formatDate(date:dateToValue)}" required=""/>
  </g:if>
  <g:else>
    <g:textField name="${dateFromName}" class="form-control datePicker" value="${dateFromValueString ?: ts.formatDate(date:dateFromValue)}"/>
    <span class="input-group-addon">ถึง</span>
    <g:textField name="${dateToName}" class="form-control datePicker" value="${dateToValueString ?: ts.formatDate(date:dateToValue)}"/>
  </g:else>
</div>