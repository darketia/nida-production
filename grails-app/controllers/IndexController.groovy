import grails.plugin.springsecurity.annotation.Secured

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class IndexController {
  /**
   *
   */
  def index = {
    render(view: '/index')
  }
}
