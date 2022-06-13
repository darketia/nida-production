package nida.production

import org.apache.commons.lang.builder.CompareToBuilder

class RoleGroup implements Comparable<RoleGroup>{
  String authority
  String description

  @Override
  int compareTo(RoleGroup that) {
    new CompareToBuilder().
        append(this.authority, that.authority).
        toComparison()
  }

  String toString(){ description }
}
