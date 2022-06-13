<% import grails.persistence.Event %>
<%=packageName%>
<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><g:message code="default.show.label" args="[entityName]"/></a>
    </div>
    <div class="navbar-default">
      <ul class="nav navbar-nav">
        <li>
          <g:link action="index">
            <i class="glyphicon glyphicon-th-list"></i>
            <g:message code="default.list.label" args="[entityName]"/>
          </g:link>
        </li>
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
  <dl class="dl-horizontal dl-show">
    <% excludedProps = Event.allEvents.toList() << 'id' << 'version' << 'dateCreated' << 'lastUpdated' << 'creator' << 'updater'
    allowedNames = domainClass.persistentProperties*.name
    props = domainClass.properties.findAll {
      allowedNames.contains(it.name) && !excludedProps.contains(it.name) && (domainClass.constrainedProperties[it.name] ? domainClass.constrainedProperties[it.name].display : true)
    }
    Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
    props.each { p -> %>
    <g:if test="\${${propertyName}?.${p.name}}">
      <dt><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}"/></dt>
      <% if (p.isEnum()) { %>
      <dd><g:message code="enum.${p.type.simpleName}.\${${propertyName}.${p.name}}"/></dd>
      <% } else if (p.oneToMany || p.manyToMany) { %>
      <dd>
        <ul>
          <g:each in="\${${propertyName}.${p.name}}" var="${p.name[0]}">
            \${${p.name[0]}?.encodeAsHTML()}
          </g:each>
        </ul>
      </dd>
      <% } else if (p.manyToOne || p.oneToOne) { %>
      <dd>\${${propertyName}?.${p.name}?.encodeAsHTML()}</dd>
      <% } else if (p.type == Boolean || p.type == boolean) { %>
      <dd><g:formatBoolean boolean="\${${propertyName}?.${p.name}}"/></dd>
      <% } else if (p.type == Date || p.type == java.sql.Date || p.type == java.sql.Time || p.type == Calendar) { %>
      <dd><g:formatDate date="\${${propertyName}?.${p.name}}"/></dd>
      <% } else if (!p.type.isArray()) { %>
      <dd><g:fieldValue bean="\${${propertyName}}" field="${p.name}"/></dd>
      <% } %>
    </g:if>
    <% }
    if (domainClass.properties.name.containsAll(['creator', 'updater', 'dateCreated', 'lastUpdated'])) { %>
    <g:if test="\${${propertyName}?.creator}">
      <dt><g:message code="default.creator.label" default="Creator"/></dt>
      <dd>\${${propertyName}?.creator?.encodeAsHTML()}</dd>
    </g:if>

    <g:if test="\${${propertyName}?.updater}">
      <dt><g:message code="default.updater.label" default="Updater"/></dt>
      <dd>\${${propertyName}?.updater?.encodeAsHTML()}</dd>
    </g:if>

    <g:if test="\${${propertyName}?.dateCreated}">
      <dt><g:message code="default.dateCreated.label" default="Date Created"/></dt>
      <dd><g:formatDate date="\${${propertyName}?.dateCreated}" formatName="default.datetime.format"/></dd>
    </g:if>

    <g:if test="\${${propertyName}?.lastUpdated}">
      <dt><g:message code="default.lastUpdated.label" default="Last Updated"/></dt>
      <dd><g:formatDate date="\${${propertyName}?.lastUpdated}" formatName="default.datetime.format"/></dd>
    </g:if>
    <% } %>

    <g:form url="[resource: ${ propertyName }, action: 'delete']" method="DELETE">
      <g:link class="btn btn-primary" action="edit" resource="\${${propertyName}}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
      <g:actionSubmit class="btn btn-danger" action="delete" value="\${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:form>

    <g:render template="/shared/updaterDetail" model="[instance: ${propertyName}]"/>
  </dl>

</div>
</body>
</html>
