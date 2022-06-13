package nida.production

class Sequencer {
  SequenceType sequenceType
  String series
  Long lastSeqNo = 0

  static mapping = {
    version false
  }

  static constraints = {
    series nullable: false, blank: true, unique: ['sequenceType']
  }

}
