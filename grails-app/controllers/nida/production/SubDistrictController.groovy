package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN'])
@Transactional(readOnly = true)
class SubDistrictController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def list = SubDistrict.createCriteria().list(params) {
      if (params.code) ilike('code', "%${params.code}%")
      if (params.name) ilike('name', "%${params.name}%")
    }
    [subDistrictInstanceList: list, subDistrictInstanceCount: list.totalCount]
  }

  def show(SubDistrict subDistrictInstance) {
    [subDistrictInstance: subDistrictInstance]
  }

  def create() {
    [subDistrictInstance: new SubDistrict(params)]
  }

  @Transactional
  def save(SubDistrict subDistrictInstance) {
    if (subDistrictInstance == null) {
      notFound()
      return
    }
    
    subDistrictInstance.creator = currentUser
    subDistrictInstance.updater = currentUser

    if (!subDistrictInstance.validate()) {
      respond subDistrictInstance.errors, view: 'create'
      return
    }

    subDistrictInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'subDistrict.label', default: 'subDistrict'), subDistrictInstance])
        redirect subDistrictInstance
      }
      '*' { respond subDistrictInstance, [status: CREATED] }
    }
  }

  def edit(SubDistrict subDistrictInstance) {
    [subDistrictInstance: subDistrictInstance]
  }

  @Transactional
  def update(SubDistrict subDistrictInstance) {
    if (subDistrictInstance == null) {
      notFound()
      return
    }
    
    subDistrictInstance.updater = currentUser

    if (!subDistrictInstance.validate()) {
      respond subDistrictInstance.errors, view: 'edit'
      return
    }

    subDistrictInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'subDistrict.label', default: 'subDistrict'), subDistrictInstance])
        redirect subDistrictInstance
      }
      '*' { respond subDistrictInstance, [status: OK] }
    }
  }

  @Transactional
  def delete(SubDistrict subDistrictInstance) {

    if (subDistrictInstance == null) {
      notFound()
      return
    }

    subDistrictInstance.delete flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'subDistrict.label', default: 'subDistrict'), subDistrictInstance])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'subDistrict.label', default: 'SubDistrict'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}
