package nida.production

import grails.plugin.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.transaction.annotation.Propagation

@org.springframework.transaction.annotation.Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
class TsService {


  GrailsApplication grailsApplication

  SpringSecurityService springSecurityService
  SecUser overridingCurrentUser
  Company currentCompany
  /**
   * locale ปัจจุบันของระบบที่ได้ตั้งค่าไว้
   * @return
   */
  public Locale getCurrentLocale() {
    return LocaleContextHolder.locale
  }

  /**
   * ดึงข้อมูลของ SecUser ปัจจุบันที่เข้าใช้ระบบ
   * @return
   */
  public SecUser getCurrentUser() {
    if (overridingCurrentUser)
      return overridingCurrentUser
    else if (springSecurityService.currentUser)
      return (springSecurityService.principal && !(springSecurityService.principal instanceof String)) ?
          SecUser.get(springSecurityService.currentUser.id) :
          null
    else return null
  }

  public Company getCurrentCompany() {
    if (!currentCompany) currentCompany = Company.first()
    return currentCompany
  }
}