<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'saleOrder.label', default: 'SaleOrder')}"/>
  <title><g:message code="saleOrder.create.label"/></title>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="saleOrder.create.label"/></a>
    </div>
    <div class="navbar-default">
      <ul class="nav navbar-nav">

      </ul>
    </div>
  </div>
</nav>

<div id="create-saleOrder" class="content scaffold-create" role="main">
  <g:if test="${flash.message}">
    <div class="alert alert-info" role="alert">
      <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
      ${flash.message}
    </div>
  </g:if>
  <g:hasErrors bean="${saleOrderInstance}">
    <div id="form-error" class="alert alert-danger" role="alert">
      <g:eachError bean="${saleOrderInstance}" var="error">
        <div class="control-label">
          <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span><g:message error="${error}"/>
        </div>
      </g:eachError>
    </div>
  </g:hasErrors>
  <g:form url="[resource: saleOrderInstance, action: 'save']"  class="form-horizontal">
    <fieldset class="form">
      <g:render template="form"/>
    </fieldset>
    %{--<g:submitButton name="create1" class="btn btn-primary" value="สร้าง และพิมพ์ใบเสร็จอย่างย่อ" onclick="return validate()"/>--}%
    <g:submitButton name="create4" class="btn btn-primary" value="บันทึก" onclick="return validate()"/>
    <g:submitButton name="create2" class="btn btn-primary" value="สร้าง และพิมพ์ใบเสนอราคา" onclick="return validate()"/>
    <g:submitButton name="create3" class="btn btn-primary" value="สร้าง และพิมพ์เฉพาะลูกค้า" onclick="return validate()"/>
    <br/>
    <br/>
  </g:form>
</div>
</body>
</html>
