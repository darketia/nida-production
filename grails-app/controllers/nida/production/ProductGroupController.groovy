package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_PRODUCT_GROUP'])
@Transactional(readOnly = true)
class ProductGroupController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def list = ProductGroup.createCriteria().list(params) {
      if (params.code) ilike('code', "%${params.code}%")
      if (params.name) ilike('name', "%${params.name}%")
    }
    [productGroupInstanceList: list, productGroupInstanceCount: list.totalCount]
  }

  def show(ProductGroup productGroupInstance) {
    [productGroupInstance: productGroupInstance]
  }

  def create() {
    [productGroupInstance: new ProductGroup(params)]
  }

  @Transactional
  def save(ProductGroup productGroupInstance) {
    if (productGroupInstance == null) {
      notFound()
      return
    }

    productGroupInstance.creator = currentUser
    productGroupInstance.updater = currentUser

    if (!productGroupInstance.validate()) {
      respond productGroupInstance.errors, view: 'create'
      return
    }

    productGroupInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'productGroup.label', default: 'productGroup'), productGroupInstance])
        redirect productGroupInstance
      }
      '*' { respond productGroupInstance, [status: CREATED] }
    }
  }

  def edit(ProductGroup productGroupInstance) {
    [productGroupInstance: productGroupInstance]
  }

  @Transactional
  def update(ProductGroup productGroupInstance) {
    if (productGroupInstance == null) {
      notFound()
      return
    }

    productGroupInstance.updater = currentUser

    if (!productGroupInstance.validate()) {
      respond productGroupInstance.errors, view: 'edit'
      return
    }

    productGroupInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'productGroup.label', default: 'productGroup'), productGroupInstance])
        redirect productGroupInstance
      }
      '*' { respond productGroupInstance, [status: OK] }
    }
  }

  @Transactional
  def delete(ProductGroup productGroupInstance) {

    if (productGroupInstance == null) {
      notFound()
      return
    }

    productGroupInstance.delete flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'productGroup.label', default: 'productGroup'), productGroupInstance])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'productGroup.label', default: 'ProductGroup'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}
