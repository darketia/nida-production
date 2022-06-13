package nida.production

class SecUser extends UserMaintainedDomain {
  transient springSecurityService

  String code
  String username
  String password
  String firstName
  String lastName

  boolean enabled = true
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired

  static transients = ['springSecurityService']

  static constraints = {
    username blank: false, unique: true
    password blank: false
    firstName blank: true, nullable: true
    lastName blank: true, nullable: true
  }

  static mapping = {

  }

  Set<Role> getAuthorities() {
    SecUserRole.findAllBySecUser(this).collect { it.role }
  }

  def beforeInsert() {
    encodePassword()
  }

  def beforeUpdate() {
    if (isDirty('password')) {
      encodePassword()
    }
  }

  /**
   * @return ( firstName ? : ' ' ) + ' ' + (lastName?:'')
   */
  String toString() { (firstName ?: '') + ' ' + (lastName ?: '') }

  String getFullString() { (firstName ?: '') + ' ' + (lastName ?: '') + '(' + username + ')' }

  protected void encodePassword() {
    password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
  }
}
