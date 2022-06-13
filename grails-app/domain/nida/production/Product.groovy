package nida.production

class Product extends StdMasterDomain{

  ProductGroup productGroup
  Uom uom
  List<ProductPrice> productPrices

  BigDecimal minimumStock

  Boolean cancel = Boolean.FALSE

  static hasMany = [productPrices: ProductPrice]

  static constraints = {
    minimumStock nullable: true, scale: 2
    cancel nullable: true
  }

  StockCard getStockCard(){
    StockCard.findByProduct(this, [sort:'date', order:'desc'])
  }
}
