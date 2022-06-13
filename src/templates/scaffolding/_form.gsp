<%=packageName%>
<% import grails.persistence.Event %>

<%  excludedProps = Event.allEvents.toList() << 'version' << 'dateCreated' << 'lastUpdated' << 'creator' << 'updater'
persistentPropNames = domainClass.persistentProperties*.name
boolean hasHibernate = pluginManager?.hasGrailsPlugin('hibernate') || pluginManager?.hasGrailsPlugin('hibernate4')
if (hasHibernate) {
	def GrailsDomainBinder = getClass().classLoader.loadClass('org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder')
	if (GrailsDomainBinder.newInstance().getMapping(domainClass)?.identity?.generator == 'assigned') {
		persistentPropNames << domainClass.identifier.name
	}
}
props = domainClass.properties.findAll { persistentPropNames.contains(it.name) && !excludedProps.contains(it.name) && (domainClass.constrainedProperties[it.name] ? domainClass.constrainedProperties[it.name].display : true) }
Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
for (p in props) {
	if (p.embedded) {
		def embeddedPropNames = p.component.persistentProperties*.name
		def embeddedProps = p.component.properties.findAll { embeddedPropNames.contains(it.name) && !excludedProps.contains(it.name) }
		Collections.sort(embeddedProps, comparator.constructors[0].newInstance([p.component] as Object[]))
%><div class="panel panel-success"><div class="panel-heading"><g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}" /></div><%
		for (ep in p.component.properties) {
			renderFieldForProperty(ep, p.component, "${p.name}.")
		}
%></div><%
		} else {
			renderFieldForProperty(p, domainClass)
		}
	}

	private renderFieldForProperty(p, owningClass, prefix = "") {
		boolean hasHibernate = pluginManager?.hasGrailsPlugin('hibernate') || pluginManager?.hasGrailsPlugin('hibernate4')
		boolean required = false
		if (hasHibernate) {
			cp = owningClass.constrainedProperties[p.name]
			required = (cp ? !(cp.propertyType in [boolean, Boolean]) && !cp.nullable : false)
		}
   if(!p.oneToMany){ %>
<div class="form-group \${hasErrors(bean: ${propertyName}, field: '${prefix}${p.name}', 'error')} ${required ? 'required' : ''}">
	<label for="${prefix}${p.name}" class="control-label col-sm-4">
		<g:message code="${domainClass.propertyName}.${prefix}${p.name}.label" default="${p.naturalName}" />
		<% if (required) { %><span class="required-indicator">*</span><% } %>
	</label>
	<% if(p.type == Boolean || p.type == boolean){ %>
	<div class="col-sm-4 checkbox">
		<label>
			${renderEditor(p)}
		</label>
	</div>
	<% } else{ %>
	<div class="col-sm-4">
		${renderEditor(p)}
	</div>
	<% } %>
</div>
<%  } %>
<%  } %>
