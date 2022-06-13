package nida.production

class SaleOrderDetail {
  static belongsTo = [saleOrder: SaleOrder]

  Product product //should be unique:'saleOrder'
  BigDecimal pricePerUnit
  BigDecimal qty
  static mapping = {

  }

  static constraints = {
    pricePerUnit nullable: false, scale: 2, min: BigDecimal.ZERO
    qty nullable: false, scale: 2
  }
}
