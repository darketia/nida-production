<html>
<head>
  <meta name='layout' content='main'/>
  <title><g:message code="springSecurity.login.title"/></title>
  <asset:stylesheet src="signin.css"/>
</head>

<body>
<form class="form-signin skipWaiting" action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>
  <h2 class="form-signin-heading">Please Login</h2>
  <label for="username" class="sr-only"><g:message code="springSecurity.login.username.label"/></label>
  <input type="text" id="username" class="form-control" name="j_username" placeholder="${message(code: 'springSecurity.login.username.label')}" required="" autofocus="">
  <label for="password" class="sr-only"><g:message code="springSecurity.login.password.label"/></label>
  <input type="password" id="password" class="form-control" name="j_password" placeholder="${message(code: 'springSecurity.login.password.label')}" required="">
  <button class="btn btn-lg btn-primary btn-block" type="submit">${message(code: "springSecurity.login.button")}</button>
  <g:if test='${flash.message}'>
    <div class='alert alert-danger' role="alert">
      <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
      <span class="sr-only">Error:</span>
      ${flash.message}
    </div>
  </g:if>
</form>
</body>
</html>
