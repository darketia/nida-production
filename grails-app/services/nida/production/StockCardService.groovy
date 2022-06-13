package nida.production

import grails.transaction.Transactional
import groovy.sql.Sql
import org.hibernate.Session
import org.hibernate.jdbc.Work

import java.sql.Connection

@Transactional
class StockCardService {
  def initialStockCard() {
    StockCard.withTransaction { tx ->
      try {
        StockCard.withSession { Session session ->
          session.doWork({ Connection connection ->
            Sql sql = new Sql(connection)
            sql.execute("delete from stock_card where sale_order_detail_id is not null")

            def ls = SaleOrder.list([sort:'date'])
            ls?.eachWithIndex { saleOrder, idx ->
              println "[${idx+1}/${ls.size()}]saleOrder:${saleOrder}"
              saleOrder.saleOrderDetails?.each { detail -> saveOrUpdateBySaleOrderDetail(detail)}
              saleOrder.saleOrderReturnDetails?.each { detail -> saveOrUpdateBySaleOrderReturnDetail(detail)}
            }

          } as Work)
        }
      } catch (Exception e) {
        log.error(e.message, e)
        tx.setRollbackOnly()
      }

    }
  }

  def saveOrUpdateBySaleOrderDetail(SaleOrderDetail saleOrderDetail) {
    def stockCardInstance = StockCard.findBySaleOrderDetail(saleOrderDetail) ?: new StockCard()
    def oldCmd = stockCardInstance.id ? new StockCardCmd(stockCardInstance, true) : null
    stockCardInstance.saleOrderDetail = saleOrderDetail
    stockCardInstance.product = saleOrderDetail.product
    stockCardInstance.date = saleOrderDetail.saleOrder.dateCreated
    stockCardInstance.qty = saleOrderDetail.qty.negate()
    stockCardInstance.creator = saleOrderDetail.saleOrder.creator
    stockCardInstance.dateCreated = saleOrderDetail.saleOrder.dateCreated
    stockCardInstance.updater = saleOrderDetail.saleOrder.updater
    stockCardInstance.lastUpdated = saleOrderDetail.saleOrder.lastUpdated

    def cmd = new StockCardCmd(stockCardInstance)
    def latestBalance = getBalance(cmd)
    stockCardInstance.balance = latestBalance + stockCardInstance.qty
    cmd.balance = stockCardInstance.balance

    stockCardInstance.save flush: true
    cmd.id = stockCardInstance.id

    if (oldCmd) updateNextBalances(oldCmd, cmd)
    else updateNextBalance(cmd)
  }

  def saveOrUpdateBySaleOrderReturnDetail(SaleOrderReturnDetail saleOrderReturnDetail) {
    def stockCardInstance = StockCard.findBySaleOrderReturnDetail(saleOrderReturnDetail) ?: new StockCard()
    def oldCmd = stockCardInstance.id ? new StockCardCmd(stockCardInstance, true) : null
    stockCardInstance.saleOrderReturnDetail = saleOrderReturnDetail
    stockCardInstance.product = saleOrderReturnDetail.product
    stockCardInstance.date = saleOrderReturnDetail.saleOrder.dateCreated
    stockCardInstance.qty = saleOrderReturnDetail.qty
    stockCardInstance.creator = saleOrderReturnDetail.saleOrder.creator
    stockCardInstance.dateCreated = saleOrderReturnDetail.saleOrder.dateCreated
    stockCardInstance.updater = saleOrderReturnDetail.saleOrder.updater
    stockCardInstance.lastUpdated = saleOrderReturnDetail.saleOrder.lastUpdated
    def cmd = new StockCardCmd(stockCardInstance)
    def latestBalance = getBalance(cmd)
    stockCardInstance.balance = latestBalance + stockCardInstance.qty
    cmd.balance = stockCardInstance.balance

    stockCardInstance.save flush: true
    cmd.id = stockCardInstance.id
    if (oldCmd) updateNextBalances(oldCmd, cmd)
    else updateNextBalance(cmd)
  }

  def deleteBySaleOrderDetails(SaleOrderDetail saleOrderDetail) {
    def stockCardInstance = StockCard.findBySaleOrderDetail(saleOrderDetail)
    def cmd = new StockCardCmd(stockCardInstance)

    stockCardInstance.delete flush: true

    updateNextBalances(cmd, null)
  }

  def deleteBySaleOrderReturnDetails(SaleOrderDetail saleOrderReturnDetail) {
    def stockCardInstance = StockCard.findBySaleOrderReturnDetail(saleOrderReturnDetail)
    def cmd = new StockCardCmd(stockCardInstance)

    stockCardInstance.delete flush: true

    updateNextBalances(cmd, null)
  }

  //ดึง balance ล่าสุดก่อนเกิด tx นี้
  def getBalance(StockCardCmd cmd) {
    StockCard stockCard = StockCard.createCriteria().get {
      if (cmd.id) ne('id', cmd.id)
      or {
        lt('date', cmd.date)
        and {
          eq('date', cmd.date)
          if (cmd.id) lt('id', cmd.id)
        }
      }
      eq('product', cmd.product)

      and {
        order('date', 'desc')
        order('id', 'desc')
      }
      maxResults(1)
    }
    stockCard?.balance ?: BigDecimal.ZERO
  }

  def updateNextBalance(StockCardCmd cmd) {
    List<StockCard> stockCards = StockCard.createCriteria().list {
      if (cmd.id) ne('id', cmd.id)
      ge('date', cmd.date)
      eq('product', cmd.product)

      and {
        order('date')
        order('id')
      }
    }
    def balance = cmd.balance
    stockCards?.each {
      if (it.date > cmd.date || (it.date == cmd.date && it.id > cmd.id)) {
        it.balance = balance + it.qty
        balance = it.balance
        it.save flush: true
      }
    }

  }

  def updateNextBalances(StockCardCmd oldCmd, StockCardCmd newCmd) {
    if (!newCmd || oldCmd.product != newCmd.product) {
      oldCmd.balance = getBalance(oldCmd)
      updateNextBalance(oldCmd)
      if (newCmd) updateNextBalance(newCmd)
    } else if (oldCmd.date != newCmd.date) {
      //changeDate, sameProduct
      if (oldCmd.date < newCmd.date) {
        oldCmd.id = null
        oldCmd.balance = getBalance(oldCmd)
        updateNextBalance(oldCmd)
      } else if (oldCmd.date > newCmd.date) {
        updateNextBalance(newCmd)
      }
    } else if (oldCmd.qty != newCmd.qty) {
      updateNextBalance(newCmd)
    }
  }
}
