<%=packageName ? "package ${packageName}" : ''%>

import static org.springframework.http.HttpStatus.*
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN','ROLE_PAGE<%=className.replaceAll('[A-Z]') { '_' + it }.toUpperCase()%>'])
@Transactional(readOnly = true)
class ${className}Controller extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def list = ${className}.createCriteria().list(params){
      if(params.code) ilike('code', "%\${params.code}%")
      if(params.name) ilike('name', "%\${params.name}%")
    }
    [${propertyName}List: list, ${propertyName}Count: list.totalCount]
  }

  def show(${className} ${propertyName}) {
    [${propertyName}: ${propertyName}]
  }

  def create() {
    [${propertyName}: new ${className}(params)]
  }

  @Transactional
  def save(${className} ${propertyName}) {
    if (${propertyName} == null) {
      notFound()
      return
    }
    <%=(domainClass.properties.name.containsAll(['creator','updater']))?"\n${propertyName}.creator = currentUser\n${propertyName}.updater = currentUser\n":''%>
    if (!${propertyName}.validate()) {
      respond ${propertyName}.errors, view:'create'
      return
    }

    ${propertyName}.save flush:true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.propertyName}'), ${propertyName}])
        redirect ${propertyName}
      }
      '*' { respond ${propertyName}, [status: CREATED] }
    }
  }

  def edit(${className} ${propertyName}) {
    [${propertyName}: ${propertyName}]
  }

  @Transactional
  def update(${className} ${propertyName}) {
    if (${propertyName} == null) {
      notFound()
      return
    }
    <%=(domainClass.properties.name.containsAll(['updater']))?"\n${propertyName}.updater = currentUser\n":''%>
    if (!${propertyName}.validate()) {
      respond ${propertyName}.errors, view:'edit'
      return
    }

    ${propertyName}.save flush:true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.propertyName}'), ${propertyName}])
        redirect ${propertyName}
      }
      '*'{ respond ${propertyName}, [status: OK] }
    }
  }

  @Transactional
  def delete(${className} ${propertyName}) {

    if (${propertyName} == null) {
      notFound()
      return
    }

    ${propertyName}.delete flush:true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.propertyName}'), ${propertyName}])
        redirect action:"index", method:"GET"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])
        redirect action: "index", method: "GET"
      }
      '*'{ render status: NOT_FOUND }
    }
  }
}
