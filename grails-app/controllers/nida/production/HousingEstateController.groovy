package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN'])
@Transactional(readOnly = true)
class HousingEstateController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def list = HousingEstate.createCriteria().list(params) {
      if (params.code) ilike('code', "%${params.code}%")
      if (params.name) ilike('name', "%${params.name}%")
      if (params.getLong('subDistrict.id')) eq('subDistrict', SubDistrict.get(params.getLong('subDistrict.id')))
    }
    [housingEstateInstanceList: list, housingEstateInstanceCount: list.totalCount]
  }

  def show(HousingEstate housingEstateInstance) {
    [housingEstateInstance: housingEstateInstance]
  }

  def create() {
    [housingEstateInstance: new HousingEstate(params)]
  }

  @Transactional
  def save(HousingEstate housingEstateInstance) {
    if (housingEstateInstance == null) {
      notFound()
      return
    }
    
    housingEstateInstance.creator = currentUser
    housingEstateInstance.updater = currentUser

    if (!housingEstateInstance.validate()) {
      respond housingEstateInstance.errors, view: 'create'
      return
    }

    housingEstateInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'housingEstate.label', default: 'housingEstate'), housingEstateInstance])
        redirect housingEstateInstance
      }
      '*' { respond housingEstateInstance, [status: CREATED] }
    }
  }

  def edit(HousingEstate housingEstateInstance) {
    [housingEstateInstance: housingEstateInstance]
  }

  @Transactional
  def update(HousingEstate housingEstateInstance) {
    if (housingEstateInstance == null) {
      notFound()
      return
    }
    
    housingEstateInstance.updater = currentUser

    if (!housingEstateInstance.validate()) {
      respond housingEstateInstance.errors, view: 'edit'
      return
    }

    housingEstateInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'housingEstate.label', default: 'housingEstate'), housingEstateInstance])
        redirect housingEstateInstance
      }
      '*' { respond housingEstateInstance, [status: OK] }
    }
  }

  @Transactional
  def delete(HousingEstate housingEstateInstance) {

    if (housingEstateInstance == null) {
      notFound()
      return
    }

    housingEstateInstance.delete flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'housingEstate.label', default: 'housingEstate'), housingEstateInstance])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'housingEstate.label', default: 'HousingEstate'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}
