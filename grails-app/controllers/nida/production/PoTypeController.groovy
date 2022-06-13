package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_UOM'])
@Transactional(readOnly = true)
class PoTypeController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def list = PoType.createCriteria().list(params) {
      if (params.code) ilike('code', "%${params.code}%")
      if (params.name) ilike('name', "%${params.name}%")
    }
    [poTypeInstanceList: list, poTypeInstanceCount: list.totalCount]
  }

  def show(PoType poTypeInstance) {
    [poTypeInstance: poTypeInstance]
  }

  def create() {
    [poTypeInstance: new PoType(params)]
  }

  @Transactional
  def save(PoType poTypeInstance) {
    if (poTypeInstance == null) {
      notFound()
      return
    }

    poTypeInstance.creator = currentUser
    poTypeInstance.updater = currentUser

    if (!poTypeInstance.validate()) {
      respond poTypeInstance.errors, view: 'create'
      return
    }

    poTypeInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'poType.label', default: 'poType'), poTypeInstance])
        redirect poTypeInstance
      }
      '*' { respond poTypeInstance, [status: CREATED] }
    }
  }

  def edit(PoType poTypeInstance) {
    [poTypeInstance: poTypeInstance]
  }

  @Transactional
  def update(PoType poTypeInstance) {
    if (poTypeInstance == null) {
      notFound()
      return
    }

    poTypeInstance.updater = currentUser

    if (!poTypeInstance.validate()) {
      respond poTypeInstance.errors, view: 'edit'
      return
    }

    poTypeInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'poType.label', default: 'poType'), poTypeInstance])
        redirect poTypeInstance
      }
      '*' { respond poTypeInstance, [status: OK] }
    }
  }

  @Transactional
  def delete(PoType poTypeInstance) {

    if (poTypeInstance == null) {
      notFound()
      return
    }

    poTypeInstance.delete flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'poType.label', default: 'poType'), poTypeInstance])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'poType.label', default: 'PoType'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}
