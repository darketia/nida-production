package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_CUSTOMER'])
@Transactional(readOnly = true)
class CustomerController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def list = Customer.createCriteria().list(params) {
      if (params.code) ilike('code', "%${params.code}%")
      if (params.name) ilike('name', "%${params.name}%")
    }
    [customerInstanceList: list, customerInstanceCount: list.totalCount]
  }

  def show(Customer customerInstance) {
    [customerInstance: customerInstance]
  }

  def create() {
    [customerInstance: new Customer(params)]
  }

  @Transactional
  def save(Customer customerInstance) {
    if (customerInstance == null) {
      notFound()
      return
    }

    customerInstance.creator = currentUser
    customerInstance.updater = currentUser

    if (!customerInstance.validate()) {
      respond customerInstance.errors, view: 'create'
      return
    }

    customerInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'customer.label', default: 'customer'), customerInstance])
        redirect customerInstance
      }
      '*' { respond customerInstance, [status: CREATED] }
    }
  }

  def edit(Customer customerInstance) {
    [customerInstance: customerInstance]
  }

  @Transactional
  def update(Customer customerInstance) {
    if (customerInstance == null) {
      notFound()
      return
    }

    customerInstance.updater = currentUser

    if (!customerInstance.validate()) {
      respond customerInstance.errors, view: 'edit'
      return
    }

    customerInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'customer.label', default: 'customer'), customerInstance])
        redirect customerInstance
      }
      '*' { respond customerInstance, [status: OK] }
    }
  }

  @Transactional
  def delete(Customer customerInstance) {

    if (customerInstance == null) {
      notFound()
      return
    }

    customerInstance.delete flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'customer.label', default: 'customer'), customerInstance])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}
