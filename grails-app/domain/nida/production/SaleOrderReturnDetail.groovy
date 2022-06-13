package nida.production

class SaleOrderReturnDetail {
  static belongsTo = [saleOrder: SaleOrder]

  Product product //should be unique:'saleOrder'
  BigDecimal pricePerUnit
  BigDecimal qty
  String refReturnCode //รหัาการขาย ไม่เก็บ fk เผื่อแก้ไขต้นทาง
  static mapping = {

  }

  static constraints = {
    pricePerUnit nullable: false, scale: 2, min: BigDecimal.ZERO
    qty nullable: false, scale: 2
    refReturnCode nullable: false, blank: false
  }
}
