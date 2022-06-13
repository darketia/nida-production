package nida.production

class Role {

  String authority
  String description
  RoleGroup group

  static mapping = {
    cache true
  }

  static constraints = {
    authority blank: false, unique: true
    group nullable: true
  }

  String toString(){ description }
}
