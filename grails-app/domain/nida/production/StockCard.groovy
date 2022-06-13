package nida.production

class StockCard {
  Product product
  Date date
  SaleOrderDetail saleOrderDetail
  SaleOrderReturnDetail saleOrderReturnDetail

  BigDecimal qty
  BigDecimal balance

  Date dateCreated
  SecUser creator

  Date lastUpdated
  SecUser updater

  static mapping = {
    autoTimestamp false
  }

  static constraints = {
    saleOrderDetail nullable: true
    saleOrderReturnDetail nullable: true
    qty nullable: false, scale: 2
    balance nullable: false, scale: 2
  }
}
