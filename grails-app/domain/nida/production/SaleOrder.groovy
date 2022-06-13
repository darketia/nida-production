package nida.production

class SaleOrder extends UserMaintainedDomain {

  Date date = new Date()
  String code

  Customer customer

  String name
  String address
  String telNo

  PriceType priceType //วิธีคิดเงิน

  HousingEstate deliveryHousingEstate //หมู่บ้านที่ให้ไปส่ง
  Integer deliveryTrip //จน.เที่ยว
  BigDecimal deliveryPrice //ค่าน้ำมันในการขนส่งสินค้า

  BigDecimal discountAmount //ส่วนลด
  BigDecimal receiveAmount //รับเงิน

  String remark

  List<SaleOrderDetail> saleOrderDetails
  List<SaleOrderReturnDetail> saleOrderReturnDetails

  static hasMany = [saleOrderDetails: SaleOrderDetail, saleOrderReturnDetails: SaleOrderReturnDetail]

  static mapping = {
    date type: 'date'
    remark type: 'text'
  }
  static constraints = {
    code nullable: false, unique: true
    remark nullable: true, blank: false
    deliveryHousingEstate nullable: true
    deliveryTrip nullable: true, min:1
    deliveryPrice nullable: true, min:BigDecimal.ZERO, scale: 2
    discountAmount nullable: true, min:BigDecimal.ZERO, scale: 2
    receiveAmount nullable: true, min:BigDecimal.ZERO, scale: 2//nullable: false
    saleOrderDetails minSize: 1
    name nullable: true, blank: false
    address nullable: true, blank: false
    telNo nullable: true, blank: false
  }

  String toString(){
    "${code} : ${name} - ${date?.format('dd/MM/yyyy')}"
  }

  BigDecimal getAmount(){
    def amount = 0
    saleOrderDetails.each { saleOrderDetail->
      amount += saleOrderDetail.qty * saleOrderDetail.pricePerUnit
    }
    saleOrderReturnDetails?.each { saleOrderReturnDetail->
      amount -= saleOrderReturnDetail.qty * saleOrderReturnDetail.pricePerUnit
    }

    amount + (deliveryPrice?:0) - (discountAmount?:0)
  }
}
