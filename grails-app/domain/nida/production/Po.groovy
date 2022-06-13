package nida.production

class Po extends UserMaintainedDomain {
  Date date = new Date()
  String code
  Integer rev
  Vendor vendor

  PoType poType
  ShipLocation shipLocation = ShipLocation.PLANT_1
  PoStatus poStatus = PoStatus.NEW
  String deliveryDate
  String paymentTerm

  BigDecimal vatRate
  BigDecimal discountAmount = BigDecimal.ZERO

  String remark

  Boolean enableScale = false

  List<PoDetail> poDetails

  static hasMany = [poDetails: PoDetail]

  static mapping = {
    date type: 'date'
    remark type: 'text'
  }

  static constraints = {
    code nullable: false, unique: true
    rev nullable: true
    remark nullable: true, blank: false
    vatRate nullable: false, scale: 2, min: BigDecimal.ZERO
    discountAmount nullable: false, scale: 2, min: BigDecimal.ZERO
    deliveryDate nullable: true, blank: false
    paymentTerm nullable: true, blank: false
    enableScale nullable: false
  }

  BigDecimal getAmountDetail() {
    def amount = 0
    poDetails.each { poDetail ->
      amount += poDetail.qty * poDetail.pricePerUnit
    }

    amount
  }

  BigDecimal getAmountWithDiscount() {
    (getAmountDetail() - (discountAmount?:0) )
  }

  BigDecimal getVat() {
    getAmountWithDiscount() *  vatRate / 100
  }

  BigDecimal getAmountWithVat() {
    getAmountWithDiscount() + getVat()
  }

  String toString(){
    code
  }
}

enum PoStatus {
  NEW, CLOSED, CANCELED
}
enum ShipLocation {
  HEAD_OFFICE, PLANT_1
}