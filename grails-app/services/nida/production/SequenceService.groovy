package nida.production

import org.springframework.beans.factory.InitializingBean
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import java.text.SimpleDateFormat
import java.util.concurrent.locks.ReentrantLock

/**
 *  Service สำหรับสร้าง Sequence ให้กับ process
 */
class SequenceService implements InitializingBean {
  static scope = "singleton"

  private ReentrantLock lock
  def yearMonthFormat = new SimpleDateFormat('yyyyMM')

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = [Exception])
  String getNextCode(SequenceType sequenceType, series, padSize){
    def seq = getNextSequenceStrings(1, sequenceType, series)[0]
    "${sequenceType.shortName}${series}${seq.padLeft(padSize,'0')}"
  }

  @Transactional(propagation = Propagation.MANDATORY, rollbackFor = [Exception])
  List<String> getNextSequenceStrings(Integer count, SequenceType sequenceType, String series) {
    if (!lock.heldByCurrentThread) {
      throw new IllegalStateException("You must first acquire the lock OUTSIDE OF a transactional code block and BEFORE calling this method.")
    }

    def seq = Sequencer.findWhere(sequenceType: sequenceType, series: series)
    if (!seq) seq = new Sequencer(sequenceType: sequenceType, series: series, lastSeqNo: 0)
    def sequenceStrings = []
    count.times {
      seq.lastSeqNo += 1
      sequenceStrings << (seq.lastSeqNo).toString()
    }

    seq.save(failOnError: true, flush: true)

    return sequenceStrings
  }

  ReentrantLock getLock() {
    return lock
  }

  @Override
  void afterPropertiesSet() {
    lock = new ReentrantLock(false)
  }
}
