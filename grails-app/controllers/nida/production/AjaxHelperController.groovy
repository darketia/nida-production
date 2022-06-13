package nida.production

import grails.plugin.springsecurity.annotation.Secured

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class AjaxHelperController extends BaseController {

  def searchCustomer(String term) {
    def searchTerms = term.split().collect { "%${it}%" }
    def results = Customer.createCriteria().list {
      searchTerms.each { st ->
        or {
          ilike 'code', "%${st}%"
          ilike 'name', "%${st}%"
        }
      }
    }

    render(contentType: 'text/json') {
      array {
        results?.each { r ->
          def label = r.toString()
          jsonCustomer id: r.id, label: label, code: r.code, value: r.code
        }
      }
    }
  }

  def searchVendor(String term) {
    def searchTerms = term.split().collect { "%${it}%" }
    def results = Vendor.createCriteria().list {
      searchTerms.each { st ->
        or {
          ilike 'code', "%${st}%"
          ilike 'name', "%${st}%"
        }
      }
    }

    render(contentType: 'text/json') {
      array {
        results?.each { r ->
          def label = r.toString()
          jsonVendor id: r.id, label: label, code: r.code, value: r.code
        }
      }
    }
  }

  def searchProduct(String term) {
    def searchTerms = term.split().collect { "%${it}%" }
    def results = Product.createCriteria().list {
      searchTerms.each { st ->
        or {
          ilike 'code', "%${st}%"
          ilike 'name', "%${st}%"
        }
      }
    }

    render(contentType: 'text/json') {
      array {
        results?.each { r ->
          def label = r.toString()
          jsonObj id: r.id, label: label, code: r.code, value: r.code
        }
      }
    }
  }

  def searchActiveProduct(String term) {
    def searchTerms = term.split().collect { "%${it}%" }
    def results = Product.createCriteria().list {
      or{
        isNull('cancel')
        eq('cancel', false)
      }
      searchTerms.each { st ->
        or {
          ilike 'code', "%${st}%"
          ilike 'name', "%${st}%"
        }
      }
    }

    render(contentType: 'text/json') {
      array {
        results?.each { r ->
          def label = r.toString()
          jsonObj id: r.id, label: label, code: r.code, value: r.code
        }
      }
    }
  }
}
