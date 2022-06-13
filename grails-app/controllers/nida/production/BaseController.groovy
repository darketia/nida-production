package nida.production

import org.codehaus.groovy.grails.commons.GrailsApplication

abstract class BaseController {

  TsService tsService

  GrailsApplication grailsApplication


  def index() {}

  protected void printMap(Map map) {
    map.sort { it.key }.each { a, b -> println "$a = $b" }
  }


  /**
   *
   * @return
   */
  protected SecUser getCurrentUser() {
    return tsService.currentUser
  }

  protected Company getCurrentCompany() {
    return tsService.currentCompany
  }

}
