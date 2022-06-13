package nida.production

import org.apache.commons.lang.builder.CompareToBuilder

abstract class StdMasterDomain extends UserMaintainedDomain implements Comparable<StdMasterDomain> {

  String code
  String name

  static constraints = {
    code nullable: false, unique: true
    name nullable: false
  }

  String toString() { "${code} : ${name}"}

  @Override
  int compareTo(StdMasterDomain that) {
    new CompareToBuilder().
        append(this.code.padLeft(18, "0"), that.code.padLeft(18, "0")).
        toComparison()
  }

  public boolean equals(Object obj) {
    this.class == obj.class && this.code == ((StdMasterDomain)obj).code
  }

}
