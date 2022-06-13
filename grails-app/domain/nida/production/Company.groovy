package nida.production

class Company extends StdMasterDomain{

  String address
  String taxNo
  BigDecimal vatRate = 7

  static constraints = {
    address nullable: true, blank: false
    taxNo nullable: true, blank: false
    vatRate nullable: false, scale:2, min:BigDecimal.ZERO, max:BigDecimal.valueOf(100.0)
  }
}
