import nida.production.Vendor
import nida.production.Company
import nida.production.PoType
import nida.production.Role
import nida.production.RoleGroup
import nida.production.SecUser
import nida.production.SecUserRole

class BootStrap {

  def init = { servletContext ->
    println 'BootStrap.init'
    initRoleGroup()
    initRole()
    initUserAdmin()
    initCompany()
    initPoType()
    initVendor()
  }
  def destroy = {
  }

  private initRoleGroup() {
    def newRoleGroup = { authority, description ->
      RoleGroup.findByAuthority(authority) ?: new RoleGroup(authority: authority, description: description).save(failOnError: true)
    }

    newRoleGroup('ROLE_GROUP_ADMIN', 'ผู้ดูแลระบบ')
  }

  private initRole() {
    def newRole = { group, authority, description ->
      def roleGroup = RoleGroup.findByAuthority(group)
      Role.findByAuthority(authority) ?: new Role(group: roleGroup, authority: authority, description: description).save(failOnError: true)
    }

    //ADMIN
    newRole('ROLE_GROUP_ADMIN', 'ROLE_ADMIN', 'ผู้ดูแลระบบ')

  }

  private initUserAdmin() {
    def adminRole = Role.findByAuthority('ROLE_ADMIN')
    def adminUser = SecUser.findByUsername('tawan') ?: new SecUser(
        code: 'tawan',
        username: 'tawan',
        password: 'tawan',
        firstName: 'admin',
        lastName: 'admin',
        enabled: true).save(failOnError: true)

    if (!adminUser.authorities.contains(adminRole)) {
      SecUserRole.create adminUser, adminRole
    }
  }

  private initCompany() {
    if (Company.count == 0) {
      def user = SecUser.first()
      new Company(code: 'NIDA', name: "บริษัท นิด้า ฟาร์มา อินคอร์ปอเรชั่น จำกัด", creator: user, updater: user).save(failOnError: true)
    }

  }

  private initPoType() {
    if (PoType.count == 0) {
      def user = SecUser.first()
      new PoType(code: 'A', name: "A", creator: user, updater: user).save(failOnError: true)
      new PoType(code: 'B', name: "B", creator: user, updater: user).save(failOnError: true)
      new PoType(code: 'C', name: "C", creator: user, updater: user).save(failOnError: true)
    }
  }

  private initVendor() {
    if (Vendor.count == 0) {
      def user = SecUser.first()
      new Vendor(code: 'A', name: "A", creator: user, updater: user).save(failOnError: true)
      new Vendor(code: 'B', name: "B", creator: user, updater: user).save(failOnError: true)
      new Vendor(code: 'C', name: "C", creator: user, updater: user).save(failOnError: true)
    }
  }
}
