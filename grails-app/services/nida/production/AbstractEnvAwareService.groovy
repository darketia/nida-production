package nida.production

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.i18n.LocaleContextHolder

class AbstractEnvAwareService implements InitializingBean {
  GrailsApplication grailsApplication

  /**
   * ใช้ดึงค่า message จาก message.properties ตาม code
   * @param code
   * @return
   */
  String getMessage(String code) {
    grailsApplication.mainContext.getMessage(code, new Object[0], LocaleContextHolder.locale)
  }

  /**
   * ใช้ดึงค่า message จาก message.properties ตาม code และ args
   * @param code
   * @param args
   * @return
   */
  String getMessage(String code, List args) {
    grailsApplication.mainContext.getMessage(code, args.toArray(), LocaleContextHolder.locale)
  }

  /**
   *  ใช้ตั้งค่าการใช้งานปัจจุบันของผู้ใช้งาน ได้แก่ โรงงานที่ถูกประมวลผล โรงงานของUser UserName ปีการผลิต ที่อยู่ของUser
   */
  @Override
  void afterPropertiesSet() {

  }
}
