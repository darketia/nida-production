package nida.production

class Customer extends StdMasterDomain{

  String address
  String telNo

  PriceType priceType //for default

  static constraints = {
    address nullable: true
    telNo nullable: true
  }
}
