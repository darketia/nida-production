package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_UOM'])
@Transactional(readOnly = true)
class UomController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def list = Uom.createCriteria().list(params) {
      if (params.code) ilike('code', "%${params.code}%")
      if (params.name) ilike('name', "%${params.name}%")
    }
    [uomInstanceList: list, uomInstanceCount: list.totalCount]
  }

  def show(Uom uomInstance) {
    [uomInstance: uomInstance]
  }

  def create() {
    [uomInstance: new Uom(params)]
  }

  @Transactional
  def save(Uom uomInstance) {
    if (uomInstance == null) {
      notFound()
      return
    }

    uomInstance.creator = currentUser
    uomInstance.updater = currentUser

    if (!uomInstance.validate()) {
      respond uomInstance.errors, view: 'create'
      return
    }

    uomInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'uom.label', default: 'uom'), uomInstance])
        redirect uomInstance
      }
      '*' { respond uomInstance, [status: CREATED] }
    }
  }

  def edit(Uom uomInstance) {
    [uomInstance: uomInstance]
  }

  @Transactional
  def update(Uom uomInstance) {
    if (uomInstance == null) {
      notFound()
      return
    }

    uomInstance.updater = currentUser

    if (!uomInstance.validate()) {
      respond uomInstance.errors, view: 'edit'
      return
    }

    uomInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'uom.label', default: 'uom'), uomInstance])
        redirect uomInstance
      }
      '*' { respond uomInstance, [status: OK] }
    }
  }

  @Transactional
  def delete(Uom uomInstance) {

    if (uomInstance == null) {
      notFound()
      return
    }

    uomInstance.delete flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'uom.label', default: 'uom'), uomInstance])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'uom.label', default: 'Uom'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}
