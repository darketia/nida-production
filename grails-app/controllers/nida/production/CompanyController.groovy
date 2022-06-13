package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_COMPANY'])
@Transactional(readOnly = true)
class CompanyController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index() {

  }

  def show(Company companyInstance) {
    [companyInstance: companyInstance ?: Company.first()]
  }

  def edit(Company companyInstance) {
    [companyInstance: companyInstance]
  }

  @Transactional
  def update(Company companyInstance) {
    if (companyInstance == null) {
      notFound()
      return
    }

    companyInstance.updater = currentUser

    if (!companyInstance.validate()) {
      respond companyInstance.errors, view: 'edit'
      return
    }

    companyInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'company.label', default: 'company'), companyInstance])
        redirect companyInstance
      }
      '*' { respond companyInstance, [status: OK] }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'company.label', default: 'Company'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}
