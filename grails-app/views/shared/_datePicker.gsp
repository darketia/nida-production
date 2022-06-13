<div class="input-group date datePicker" style="width: 140px">
  <g:if test="${isRequired}">
    <g:textField class="form-control" name="${dateName}" value="${dateValueString ?: formatDate(date:dateValue)}" required=""/>
  </g:if>
  <g:else>
    <g:textField class="form-control" name="${dateName}" value="${dateValueString ?: formatDate(date:dateValue)}"/>
  </g:else>
  <span class="input-group-addon cursor"><span class="glyphicon glyphicon-calendar"></span></span>
</div>