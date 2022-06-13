package nida.production

abstract class UserMaintainedDomain implements Cloneable{

  SecUser creator
  SecUser updater
  Date dateCreated
  Date lastUpdated

  static constraints = {
    creator nullable: true
    updater nullable: true
    dateCreated nullable: true
    lastUpdated nullable: true
  }



}
