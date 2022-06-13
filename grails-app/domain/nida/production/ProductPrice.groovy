package nida.production

class ProductPrice {
  static belongsTo = [product: Product]
  PriceType priceType
  BigDecimal price

  static constraints = {
    priceType nullable: false, unique: 'product'
    price nullable: false, scale: 2, min:BigDecimal.ZERO
  }
}
