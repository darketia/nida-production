package nida.production

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import groovy.sql.Sql
import org.hibernate.jdbc.Work

import java.sql.Connection

@Transactional(readOnly = true)
class SecUserController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def springSecurityService
  def passwordEncoder

  @Secured(['ROLE_ADMIN'])
  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def startRowNo = (params.getInt('offset') ?: 0) + 1
    def endRowNo = startRowNo + params.getInt('max') - 1

    if (params.msg) flash.message = params.msg
    def list = []
    def totalCount

    SecUser.withTransaction {
      SecUser.withSession { s ->
        s.doWork({ Connection connection ->
          Sql sql = new Sql(connection)
          def sqlStatement = """

            from sec_user su
            left join SEC_USER_ROLE sur on sur.SEC_USER_ID = su.ID
            left join ROLE r on r.ID = sur.ROLE_ID
            where su.username like '%'
            ${params.username ? " and su.username like '%${params.username}%'" : ""}
            ${params.firstName ? " and su.first_name like '%${params.firstName}%'" : ""}
            ${params.lastName ? " and su.last_name like '%${params.lastName}%'" : ""}
            ${params.role?.id ? " and r.id = ${params.role.id}" : ""}
           """
          def idList = sql.rows("""
            WITH query AS (
                select su.id, row_number() OVER (ORDER BY su.username desc) rowNo
              ${sqlStatement}
              group by su.id,su.username
            ) select * from query where rowNo between ${startRowNo} and ${endRowNo}""".toString())?.id

          if (idList) list = SecUser.findAllByIdInList(idList)?.sort { it.username }

          totalCount = sql.firstRow("""select count(distinct su.id) c ${sqlStatement}""".toString())?.c ?: 0
        } as Work)
      }
    }

    respond list, model: [secUserInstanceCount: totalCount]
  }

  @Secured(['ROLE_ADMIN'])
  def show(SecUser secUserInstance) {
    def roles = SecUserRole.findAllBySecUser(secUserInstance)?.role?.flatten()
    def poTypes = SecUserPoType.findAllBySecUser(secUserInstance)?.poType?.flatten()
    respond secUserInstance, model: [roles: roles, poTypes: poTypes]
  }

  @Secured(['ROLE_ADMIN'])
  def create() {
    respond new SecUser(params)
  }

  @Secured(['ROLE_ADMIN'])
  @Transactional
  def save(SecUser secUserInstance) {
    if (secUserInstance == null) {
      notFound()
      return
    }

    if (secUserInstance.hasErrors()) {
      respond secUserInstance.errors, view: 'create'
      return
    }

    secUserInstance.creator = currentUser
    secUserInstance.save flush: true

    /**
     * Update userRoles
     */
    def roles
    if (params.roles) {
      roles = [params.roles].flatten()?.collect { Role.get(it) }
      roles.each { role ->
        SecUserRole.create(secUserInstance, role, true)
      }
    }

    def poTypes
    if (params.poTypes) {
      poTypes = [params.poTypes].flatten()?.collect { PoType.get(it) }
      poTypes.each { poType ->
        SecUserPoType.create(secUserInstance, poType, true)
      }
    }

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'secUser.label', default: 'SecUser'), secUserInstance])
        redirect secUserInstance
      }
      '*' { respond secUserInstance, model: [roles: roles], [status: CREATED] }
    }
  }

  @Secured(['ROLE_ADMIN'])
  def edit(SecUser secUserInstance) {
    def roles = SecUserRole.findAllBySecUser(secUserInstance)?.role?.flatten()
    def poTypes = SecUserPoType.findAllBySecUser(secUserInstance)?.poType?.flatten()
    respond secUserInstance, model: [roles: roles, poTypes: poTypes]
  }

  @Secured(['ROLE_ADMIN'])
  @Transactional
  def update(SecUser secUserInstance) {
    if (secUserInstance == null) {
      notFound()
      return
    }

    def roles
    if (params.roles) {
      roles = [params.roles].flatten()?.collect { Role.get(it) }

      def existRoles = SecUserRole.findAllBySecUser(secUserInstance)?.role.flatten()
      existRoles.each { eRole ->
        if (!(eRole.id in roles?.id.flatten())) {
          SecUserRole.remove(secUserInstance, eRole, true)
        }
      }

      roles.each { role ->
        if (!SecUserRole.exists(secUserInstance.id, role.id)) {
          SecUserRole.create(secUserInstance, role, true)?.role
        }
      }
    }

    /*def poTypes
    if (params.poTypes) {
      poTypes = [params.poTypes].flatten()?.collect { PoType.get(it) }

      def existPoTypes = SecUserPoType.findAllBySecUser(secUserInstance)?.poType.flatten()
      existPoTypes.each { ePoType ->
        if (!(ePoType.id in poTypes?.id.flatten())) {
          SecUserPoType.findBySecUserAndPoType(secUserInstance, ePoType).delete()
        }
      }

      poTypes.each { poType ->
        if (!SecUserPoType.exists(secUserInstance.id, poType.id)) {
          SecUserPoType.create(secUserInstance, poType, true)?.poType
        }
      }
    }*/

    SecUserPoType.findBySecUser(secUserInstance).delete()
    def poTypes
    if (params.poTypes) {
      poTypes = [params.poTypes].flatten()?.collect { PoType.get(it) }
      poTypes.each { poType ->
        def sp = new SecUserPoType()
        sp.secUser = secUserInstance
        sp.poType = poType
        sp.save()
      }
    }

    if (secUserInstance.hasErrors()) {
      respond secUserInstance.errors, view: 'edit', model: [roles: roles, poTypes: poTypes]
      return
    }

    secUserInstance.updater = currentUser
    secUserInstance.save(flush: true)

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'secUser.label', default: 'SecUser'), secUserInstance])
        redirect secUserInstance
      }
      '*' { respond secUserInstance, model: [roles: roles], [status: OK] }
    }
  }

  @Secured(['ROLE_ADMIN'])
  @Transactional
  def delete(SecUser secUserInstance) {

    if (secUserInstance == null) {
      notFound()
      return
    }

    def hasError = false
    SecUser.withTransaction { tx ->
      try {
        SecUserRole.findAllBySecUser(secUserInstance).each { ur ->
          ur.delete()
        }
        SecUserPoType.findAllBySecUser(secUserInstance).each { up ->
          up.delete()
        }
        secUserInstance.delete flush: true
      } catch (Exception e) {
        tx.setRollbackOnly()
        if (e.message.contains("integrity constraint")) flash.errors = message(code: 'default.integrity.constraint.message', args: [secUserInstance.toString()])
        else flash.errors = e.message
        hasError = true
      }
    }

    if (hasError) {
      redirect action: 'show', params: [id: secUserInstance.id]
      return
    } else {
      request.withFormat {
        form multipartForm {
          flash.message = message(code: 'default.deleted.message', args: [message(code: 'secUser.label', default: 'SecUser'), secUserInstance])
          redirect action: "index", method: "GET"
        }
        '*' { render status: NO_CONTENT }
      }
    }
  }

  @Secured(['IS_AUTHENTICATED_REMEMBERED'])
  def changePassword(SecUser secUserInstance) {
    if (!SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN") && secUserInstance?.id != currentUser?.id) {
      flash.message = 'User not found!'
      redirect controller: 'index'
      return
    }
    respond secUserInstance
  }

  @Secured(['IS_AUTHENTICATED_REMEMBERED'])
  @Transactional
  def updatePassword(SecUser secUserInstance) {
    if (!SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN") && secUserInstance?.id != currentUser?.id) {
      flash.message = 'User not found!'
      redirect controller: 'index'
      return
    }
    if (secUserInstance == null) {
      notFound()
      return
    }

    /**
     * Validate old password
     */
    def oldPassword = params.oldPassword
    def newPassword = params.newPassword
    def confirmPassword = params.confirmPassword

    if (!passwordEncoder.isPasswordValid(secUserInstance.password, oldPassword, null)) {
      flash.errors = "Wrong password!"
      respond secUserInstance, view: 'changePassword'
      return
    } else if (oldPassword == newPassword) {
      flash.errors = "New Password must not equal current password!"
      respond secUserInstance, view: 'changePassword'
      return
    } else if (newPassword != confirmPassword) {
      flash.errors = "Confirm password not matched!"
      respond secUserInstance, view: 'changePassword'
      return
    }

    secUserInstance.password = newPassword
    secUserInstance.updater = currentUser
    secUserInstance.save flush: true

    flash.message = 'Change password success.'
    redirect controller: 'index'
  }

  @Secured(['ROLE_ADMIN'])
  @Transactional
  def resetPassword(SecUser secUserInstance) {
    if (secUserInstance == null) {
      notFound()
      return
    }


    secUserInstance.password = secUserInstance.username
    secUserInstance.updater = currentUser
    secUserInstance.save flush: true

    flash.message = "Reset password completed. New password is '${secUserInstance.username}'"
    redirect(action: 'index')
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}

class SecUserCmd {
  Long id
  String username
  String password
  String firstName
  String lastName
  boolean enabled = true
  List<Role> roles
}