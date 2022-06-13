package nida.production

class PoDetail {
  static belongsTo = [po: Po]

  String name
  BigDecimal pricePerUnit
  BigDecimal qty
  String uom
  BigDecimal packSize//todo migration Integer->BigDecimal

  static mapping = {
    name type: 'text'
  }

  static constraints = {
    name nullable: false, blank: false
    pricePerUnit nullable: false, scale: 3, min: BigDecimal.ZERO
    qty nullable: false, scale: 3, min: BigDecimal.ZERO
    uom nullable: false, blank: false
    packSize nullable: true, scale: 3, min: BigDecimal.ZERO
  }
}
