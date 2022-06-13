package nida.production

//หมู่บ้าน
class HousingEstate extends StdMasterDomain{
  static belongsTo = [subDistrict: SubDistrict]

  BigDecimal deliveryPrice //ค่าน้ำมันในการขนส่งสินค้า
  String getFullName(){
    "หมู่บ้าน ${name} (อ. ${subDistrict.name})"
  }
  static constraints = {
    deliveryPrice nullable: false, min:BigDecimal.ZERO, scale: 2
  }
}
