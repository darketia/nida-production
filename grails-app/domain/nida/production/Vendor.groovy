package nida.production

class Vendor extends StdMasterDomain{
  String address
  String telNo
  String email
  String taxNo
  String contactPerson

  static mapping = {
    sort "code"
    address type: 'text'
  }

  static constraints = {
    address nullable: true, blank: false
    telNo nullable: true, blank: false
    email nullable: true, blank: false
    taxNo nullable: true, blank: false
    contactPerson nullable: true, blank: false
  }
}
