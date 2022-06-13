<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="titleName" value="${message(code: 'import.label', default: 'Import')}"/>
  <title>${titleName}</title>
  <style>
    .form-import a{
      float: left;
      margin-right: 15px;
    }
  </style>
</head>

<body>

<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">${titleName}</a>
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
  <g:if test="${flash.error}">
    <div class="alert alert-danger" role="alert">
      <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
      <span class="sr-only">Error:</span>
      ${flash.error}
    </div>
  </g:if>
</div>

<div class="panel panel-success">
  <div class="panel-heading">
    <h2 class="panel-title"><g:message code="product.label"/></h2>
  </div>

  <div class="panel-body">
    <g:form action="importProduct" enctype="multipart/form-data" class="form-horizontal form-import" style="margin:0!important;" role="form">

      <g:link class="edit btn btn-warning" action="exportProductTemplate">
        <span class="glyphicon glyphicon-file"></span> เทมเพลตไฟล์นำเข้า
      </g:link>

      <div class="input-group col-sm-4">
        <g:field type="file" name="saleOrderFile" class="form-control" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" required=""/>
        <span class="input-group-btn">
          <button type="submit" class="btn btn-success" name="save">
            <span class="glyphicon glyphicon-floppy-disk"></span>
            <g:message code="default.button.update.label" default="Update"/>
          </button>
        </span>
      </div>
    </g:form>

  </div>
</div>
