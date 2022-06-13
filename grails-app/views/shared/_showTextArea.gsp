<g:if test="${remark}">
  <g:each in="${remark.split("\n")}" var="remarkRow">${remarkRow}<br></g:each>
</g:if>