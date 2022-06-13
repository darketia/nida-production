package nida.production


class StockLockUtils {
  static Map<String, Object> locks = [:]

  static def getLock(what) {
    def lockName = what.class.getName() + what.id
    println "getLock : ${what} - ${lockName}"
    def obj = locks[lockName]
    if (!obj) {
      obj = new Object()
      locks[lockName] = obj
    }
    obj
  }

  static lock(Collection xs, Closure work) {
    // clean xs : unique remove null
    // sort xs : dcGroup, customer, oth.
    xs = xs.unique()
    xs.remove(null)

//    def dcGroupList = xs.findAll{it instanceof DcGroup}?.sort{it.id}
//    def customerList = xs.findAll{it instanceof Customer}?.sort{it.id}
//    xs.removeAll(dcGroupList)
//    xs.removeAll(customerList)
    def othList = xs.sort{[it.class.getName(), it.id]}

    def xs2 = []
//    xs2.addAll(dcGroupList)
//    xs2.addAll(customerList)
    xs2.addAll(othList)
    println "lockList : ${xs2}"

    _lockAll(xs2, work)
  }

  private static _lockAll(List xs, Closure work) {
    if (xs.size() == 0)
      work()
    else {
      def min = xs.remove(0)
      synchronized (getLock(min)) {
        _lockAll(xs, work)
      }
    }
  }

}
