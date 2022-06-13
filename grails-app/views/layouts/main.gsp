<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<html lang="en" class="no-js" xmlns="http://www.w3.org/1999/html"><!--<![endif]-->
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title><g:layoutTitle default="Grails"/></title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
  <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
  <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
  <asset:stylesheet src="application.css"/>
  <asset:stylesheet src="customization.css"/>
  <asset:javascript src="application.js"/>
  <g:layoutHead/>
  <style>
  .select2-selection__rendered {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  </style>
</head>

<body>
<header class="navbar navbar-inverse navbar-static-top bs-docs-nav navbar-fixed-top" id="top" role="banner">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#example-navbar-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>

      <div id="logo" role="banner">
        <g:link controller="index" class="navbar-logo"><asset:image src="logo.png" alt="UST" class="img-rounded" style="height: 36px"/></g:link>
        <g:link controller="index" class="navbar-brand">Nida Pharma Incorporation Co.Ltd</g:link>
      </div>
    </div>
    <sec:ifLoggedIn>
      <div class="collapse navbar-collapse" id="example-navbar-collapse">
      <nav class="nav-menu">
        <ul class="nav navbar-nav" style="padding: 0;">

          <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_PO">
            <li><g:link controller="po" action="create"><g:message code="po.create.label"/></g:link></li>
          </sec:ifAnyGranted>
          <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_PO">
            <li><g:link controller="po" action="index"><g:message code="po.index.label"/></g:link></li>
          </sec:ifAnyGranted>


          %{--<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_SALE_ORDER">
            <li><g:link controller="saleOrder" action="create"><g:message code="saleOrder.create.label"/></g:link></li>
          </sec:ifAnyGranted>

          <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_SALE_ORDER">
            <li><g:link controller="saleOrder" action="index"><g:message code="saleOrder.index.label"/></g:link></li>
          </sec:ifAnyGranted>

          <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_CUSTOMER">
            <li><g:link controller="customer" action="index"><g:message code="customer.label"/></g:link></li>
          </sec:ifAnyGranted>--}%

          %{--<li class="dropdown">--}%
            %{--<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">--}%
              %{--สินค้า<span class="caret"></span>--}%
            %{--</a>--}%
            %{--<ul class="dropdown-menu" role="menu">--}%
              %{--<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_PRODUCT_GROUP">--}%
                %{--<li><g:link controller="productGroup" action="index"><g:message code="productGroup.label"/></g:link></li>--}%
              %{--</sec:ifAnyGranted>--}%
              %{--<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_PRODUCT">--}%
                %{--<li><g:link controller="product" action="index"><g:message code="product.label"/></g:link></li>--}%
              %{--</sec:ifAnyGranted>--}%
              %{--<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_UOM">--}%
                %{--<li><g:link controller="uom" action="index"><g:message code="uom.label"/></g:link></li>--}%
              %{--</sec:ifAnyGranted>--}%
              %{--<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_STOCK_CARD">--}%
                %{--<li><g:link controller="stockCard" action="index"><g:message code="stockCard.label"/></g:link></li>--}%
              %{--</sec:ifAnyGranted>--}%
            %{--</ul>--}%
          %{--</li>--}%

          %{--<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_PAGE_REPORT">--}%
            %{--<li class="dropdown">--}%
              %{--<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">--}%
                %{--รายงาน<span class="caret"></span>--}%
              %{--</a>--}%
              %{--<ul class="dropdown-menu" role="menu">--}%
                %{--<li><g:link controller="rptSaleOrderDaily" action="index"><g:message code="rptSaleOrderDaily.label"/></g:link></li>--}%
              %{--</ul>--}%
            %{--</li>--}%
          %{--</sec:ifAnyGranted>--}%

          <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
              ตั้งค่า<span class="caret"></span>
            </a>
            <ul class="dropdown-menu" role="menu">
              <sec:ifAnyGranted roles="ROLE_ADMIN">
                <li><g:link controller="company" action="show"><g:message code="company.label"/></g:link></li>
                <li><g:link controller="secUser" action="index"><g:message code="secUser.label"/></g:link></li>
                <li><g:link controller="poType" action="index"><g:message code="poType.label"/></g:link></li>
                <li><g:link controller="vendor" action="index"><g:message code="vendor.label"/></g:link></li>
                %{--<li><g:link controller="subDistrict" action="index"><g:message code="subDistrict.label"/></g:link></li>--}%
                %{--<li><g:link controller="housingEstate" action="index"><g:message code="housingEstate.label"/></g:link></li>--}%
                %{--<li><g:link controller="Import" action="index"><g:message code="import.label"/></g:link></li>--}%
              </sec:ifAnyGranted>
            </ul>
          </li>

        </ul>

        <ul class="nav navbar-nav navbar-right" style="padding: 0;">
          <li class="dropdown">
            <a href="#" class="dropdown-toggle user-settings" data-toggle="dropdown" role="button" aria-expanded="false">
              %{--<sec:loggedInUserInfo field="fullName"/>(<sec:username/>)<span class="caret"></span>--}%
              ${currentUser} <span class="caret"></span>
            </a>
            <ul class="dropdown-menu" role="menu">
              <li>
                <g:link controller="secUser" action="changePassword" id="${sec.loggedInUserInfo(field: 'id')}">
                  <span class="glyphicon glyphicon-edit" aria-hidden="true"></span> <g:message code="secUser.changePassword.label"/>
                </g:link>
              </li>
              <li class="divider"></li>
              <li>
                <g:link controller="logout">
                  <span class="glyphicon glyphicon-log-out" aria-hidden="true"></span> <g:message code="secUser.logout.label"/>
                </g:link>
              </li>

            </ul>
          </li>
        </ul>
      </nav>
      </div>
    </sec:ifLoggedIn>
  </div>
</header>

<div class="container">
  <g:layoutBody/>
</div>

<asset:script>
  var myApp;
  var maxShownMenus = 15;



  $(function () {
    $("input[type=number]").bind("mousewheel", function(){
        $(this).blur();
      });

    $('.doConfirm').click(function(){
      return confirm("${message(code: 'default.button.confirm.message', default: 'Are you sure?')}")
    })

    $('.datePicker').datetimepicker({
      format: 'dd/mm/yyyy', todayBtn: 1, autoclose: 1, todayHighlight: 1,   minView: 2, language: 'th'
    });

    $('.datetimePicker').datetimepicker({
      format: 'dd/mm/yyyy hh:ii', todayBtn: 1, autoclose: 1, todayHighlight: 1, language: 'th'
    });

    $('.input-daterange .datePicker').datetimepicker().on('changeDate', function (e) {
      var targetObj = e.target
      var dateFromObj = e.target.parentNode.firstElementChild
      var dateFromVal = getDate(dateFromObj.value)
      var dateToObj = e.target.parentNode.lastElementChild
      var dateToVal = getDate(dateToObj.value)
      if (dateFromVal > dateToVal) {//if exist invalid date, the condition will be false
        if (targetObj.id == dateFromObj.id) dateToObj.value = dateFromObj.value
        else if (targetObj.id == dateToObj.id) dateFromObj.value = dateToObj.value
      }
    });

%{--var is_chrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1 || (navigator.userAgent.toLowerCase().indexOf('applewebkit') > -1);
if (!is_chrome) {
window.location.replace('${createLink(controller: 'browserNotSupport')}');
  return;
}--}%

  $("form").not('.skipWaiting').submit(function(){
   waitingDialog.show("${message(code: 'default.loading.message.label')}");
    })
  });

  function sliceMenu(){
    var dd = $(this)
    var childs = $(dd).children()
    if(childs.length > maxShownMenus){
      var ddParent = $(dd).parent()
      var filterChild = $(childs).filter('li:gt('+maxShownMenus+')')
      var newdd = $(dd).clone()
      newdd.children().remove()
      newdd.append(filterChild)

      var sumWidth=0;
      $(ddParent).find('.dropdown-menu').each( function(){ sumWidth += $(this).width(); });
      ddParent.append(newdd)
      newdd.css('left', sumWidth-2)
      $(dd).children().filter('li:gt(maxShownMenus)').remove()

      newdd.each(sliceMenu)
    }
  }

  function initDatePicker(datePickerObj){
    datePickerObj.datetimepicker({
      format: 'dd/mm/yyyy', todayBtn: 1, autoclose: 1, todayHighlight: 1, minView: 2
    });
  }

  function getDate(text) {//dd/MM/yyyy
    var dd =  parseInt(text.split("/")[2], 10)
    var MM =  parseInt((text.split("/")[1]), 10) - 1
    var yyyy =  parseInt(text.split("/")[0], 10)
    return new Date(dd, MM, yyyy, 0, 0, 0, 0)
  }

  function ajaxDDL(url, data, targetDDL, noSelection, callbackFn){
    $.ajax({
      url:url,
      type:"POST", dataType:"json",
      data: data,
      async: true,
      success: function (data) {
        setOptions(targetDDL, data.ddl, noSelection)
        if(callbackFn) callbackFn()
      }
    })
  }

  function setOptions(obj, ddl, noSelection){
    var optionTemplate = '<option value="{value}">{text}</option>'
    var options = ''
    if(noSelection){
      options += optionTemplate.replace("{value}", noSelection.value).replace("{text}", noSelection.text)
    }
    for(var i=0; i < ddl.length; i++){
      options += optionTemplate.replace("{value}", ddl[i].value).replace("{text}", ddl[i].text)
    }
    obj.html($(options))
  }

  function formToJson(jqObj){
    var form = {}
    jqObj.each(function() {
      var self = $(this);
      var name = self.attr('name');
      form[name] = form[name] ? (form[name] + ',' + self.val()) : self.val();
    })
    return form
  }

</asset:script>
<asset:deferredScripts/>
</body>
</html>
