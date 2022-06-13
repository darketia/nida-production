<% import grails.persistence.Event %>
<%=packageName%>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}"/>
  <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="default.list.label" args="[entityName]"/></a>
    </div>
    <div class="navbar-default">
      <ul class="nav navbar-nav">
        <li>
          <g:link action="create">
            <i class="glyphicon glyphicon-plus"></i>
            <g:message code="default.create.label" args="[entityName]"/>
          </g:link>
        </li>
      </ul>
    </div>
  </div>
</nav>

<div class="row">
  <g:if test="\${flash.message}">
    <div class="alert alert-info" role="alert">
      <span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span>
      \${flash.message}
    </div>
  </g:if>
  <g:if test="\${flash.errors}">
    <div class="alert alert-danger" role="alert">
      <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
      <span class="sr-only">Error:</span>
      \${flash.errors}
    </div>
  </g:if>

  <% excludedProps = Event.allEvents.toList() << 'id' << 'version' << 'creator' << 'updater' << 'dateCreated' << 'lastUpdated'
  allowedNames = domainClass.persistentProperties*.name
  props = domainClass.properties.findAll {
    allowedNames.contains(it.name) && !excludedProps.contains(it.name) && it.type != null && !Collection.isAssignableFrom(it.type) && (domainClass.constrainedProperties[it.name] ? domainClass.constrainedProperties[it.name].display : true)
  }
  Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
  codeNameExist = props.name.contains('code') && props.name.contains('name')
  if(codeNameExist){%>
  <div class="col-md-12">
    <div>
      <g:form action="index" class="form-horizontal">
        <g:set var="queryParams" value="\${[
            'code': params.code,
            'name': params.name,
        ]}"/>

        <div class="form-group">
          <label for="code" class="col-sm-2 control-label"><g:message code="${domainClass.propertyName}.code.label" default="Code"/></label>
          <div class="col-sm-4"><g:textField class="form-control" id="code" name="code" value="\${params.code}"/></div>
        </div>

        <div class="form-group">
          <label for="name" class="col-sm-2 control-label"><g:message code="${domainClass.propertyName}.name.label" default="Name"/></label>
          <div class="col-sm-4"><g:textField class="form-control" id="name" name="name" value="\${params.name}"/></div>
        </div>

        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-6">
            <g:submitButton name="search" class="btn btn-primary" value="\${message(code:'default.button.search.label')}"/>
            <g:link class="btn btn-warning" action="index"><g:message code="default.button.reset.label"/></g:link>
          </div>
        </div>
      </g:form>
    </div>
  </div>
  <%}%>
</div>
<div class="row">
  <div class="table-responsive">
    <table class="table">
      <thead>
      <tr>
        <% props.eachWithIndex { p, i ->
          if (i < 6) {
            if (p.isAssociation()) { %>
        <th class="text-center"><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}"/></th>
        <% } else { %>
        <g:sortableColumn class="text-center" property="${p.name}" title="\${message(code: '${domainClass.propertyName}.${p.name}.label', default: '${p.naturalName}')}"${codeNameExist?' params="\${queryParams}"':''}/>
        <% }
        }
        } %>
        <th class="text-center"><g:message code="default.manage.label" default="Manage"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="\${${propertyName}List}" status="i" var="${propertyName}">
        <tr class="\${(i % 2) == 0 ? 'even' : 'odd'}">
          <% props.eachWithIndex { p, i ->
            if (i == 0) { %>
          <td><g:link action="show" id="\${${propertyName}.id}">\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</g:link></td>
          <% } else if (i < 6) {
            if (p.type == Boolean || p.type == boolean) { %>
          <td class="text-center"><g:formatBoolean boolean="\${${propertyName}.${p.name}}"/></td>
          <% } else if (p.type == Date || p.type == java.sql.Date || p.type == java.sql.Time || p.type == Calendar) { %>
          <td><g:formatDate date="\${${propertyName}.${p.name}}"/></td>
          <% } else if (p.isEnum()) { %>
          <td><g:message code="enum.${p.type.simpleName}.\${${propertyName}.${p.name}}"/></td>
          <% } else { %>
          <td>\${fieldValue(bean: ${propertyName}, field: "${p.name}")}</td>
          <% }
          }
          } %>
          <td class="center">
            <g:link class="btn btn-info" action="show" id="\${${propertyName}.id}"><g:message code="default.button.show.label" default="Show"/></g:link>
            <g:link class="btn btn-primary" action="edit" id="\${${propertyName}.id}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
    <div class="text-center">
      <g:paginate total="\${${propertyName}Count ?: 0}"${codeNameExist?' params="\${queryParams}"':''}/>
    </div>
  </div>
</div>
</body>
</html>
