package nida.production

enum SequenceType {
  SALE_ORDER("SO"),
  //CREDIT_NOTE("CN"),
  //DEBIT_NOTE("DN"),
  PO("PO")


  private String shortName

  private SequenceType(String shortName) { this.shortName = shortName }

  public String getShortName() { shortName }
}
