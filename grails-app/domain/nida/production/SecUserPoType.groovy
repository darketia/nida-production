package nida.production

import org.apache.commons.lang.builder.HashCodeBuilder

class SecUserPoType implements Serializable {

  private static final long serialVersionUID = 1

  SecUser secUser
  PoType poType

  boolean equals(other) {
    if (!(other instanceof SecUserPoType)) {
      return false
    }

    other.secUser?.id == secUser?.id &&
        other.poType?.id == poType?.id
  }

  int hashCode() {
    def builder = new HashCodeBuilder()
    if (secUser) builder.append(secUser.id)
    if (poType) builder.append(poType.id)
    builder.toHashCode()
  }

  static mapping = {
    id composite: ['poType', 'secUser']
    version false
  }
}
