package nida.production

import grails.converters.JSON
import groovy.sql.Sql
import org.hibernate.Session
import org.hibernate.jdbc.Work

import java.sql.Connection

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_STOCK_CARD'])
@Transactional(readOnly = true)
class StockCardController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def stockCardService

  /*def testInit() {
    println "testInit"
    stockCardService.initialStockCard()
    flash.message = 'stockCardService.initialStockCard() done'
    redirect action: 'index'
  }*/

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)

    def list = []
    def totalCount

    def startRowNo = (params.getInt('offset') ?: 0) + 1
    def endRowNo = startRowNo + params.getInt('max') - 1

    StockCard.withSession { Session session ->
      session.doWork({ Connection connection ->
        Sql sql = new Sql(connection)
        def sqlStatement = """
            from product p
              left join (
                select *, rank() over ( partition by product_id order by date desc) rank 
                from stock_card
              ) sc on sc.product_id = p.id
            where (sc.rank is null or sc.rank=1)
              ${params.code ? "and p.code like '%${params.code}%'" : ''}
              ${params.name ? "and p.name like '%${params.name}%'" : ''}
              ${params.getLong('productGroup.id') ? "and p.product_group_id = ${params.getLong('productGroup.id')}" : ''}
              ${params.underMinimumStock ? "and p.minimum_stock is not null and isnull(sc.balance,0.00) < p.minimum_stock" : ''}
              ${params.soonMinimumStock ? "and p.minimum_stock is not null and (isnull(sc.balance,0.00) - p.minimum_stock )/ p.minimum_stock < 0.1" : ''}
           """
        def idList = sql.rows("""
            WITH query AS (
                select p.id, row_number() OVER (ORDER BY p.code) rowNo
              ${sqlStatement}
            ) select * from query where rowNo between ${startRowNo} and ${endRowNo}""".toString())?.id

        if (idList) list = Product.findAllByIdInList(idList)?.sort { it.code }

        totalCount = sql.firstRow("""select isnull(count(distinct p.id), 0) c ${sqlStatement}""".toString())?.c


      } as Work)
    }

    [productInstanceList: list, productInstanceCount: totalCount]
  }

  def show(Product product) {
    List<StockCard> list = StockCard.createCriteria().list([max: 500]) {
      eq('product', product)
      order('date', 'desc')
      order('id', 'desc')
    }
    /*if (!list) {
      flash.message = message(code: 'stockCard.not.found.message', args: [message(code: 'stockCard.label', default: 'StockCard'), product])
      redirect action: "index"
      return
    }*/
    if (list) {
      list = list?.sort { a, b -> a.date <=> b.date ?: a.id <=> b.id }
      [stockCardInstance: list[-1], stockCardInstanceList: list]
    } else {
      [stockCardInstance: [product: product, balance: BigDecimal.ZERO], stockCardInstanceList: []]
    }

  }

  def create() {
    [stockCardInstance: new StockCard(params)]
  }

  @Transactional
  def save(StockCard stockCardInstance) {
    if (stockCardInstance == null) {
      notFound()
      return
    }

    StockCard.withTransaction { tx ->
      try {
        def cmd = new StockCardCmd(stockCardInstance)
        def now = new Date()
        stockCardInstance.creator = currentUser
        stockCardInstance.dateCreated = now
        stockCardInstance.updater = currentUser
        stockCardInstance.lastUpdated = now

        def latestBalance = stockCardService.getBalance(cmd)
        stockCardInstance.balance = latestBalance + stockCardInstance.qty
        cmd.balance = stockCardInstance.balance

        if (!stockCardInstance.validate()) {
          respond stockCardInstance.errors, view: 'create'
          return
        }

        stockCardInstance.save flush: true
        cmd.id = stockCardInstance.id
        stockCardService.updateNextBalance(cmd)

        flash.message = message(code: 'default.created.message', args: [message(code: 'stockCard.label', default: 'stockCard'), stockCardInstance])
        redirect action: 'show', params: ['product.id': stockCardInstance.product.id]

      } catch (Exception e) {
        log.error(e.message, e)
        tx.setRollbackOnly()
        stockCardInstance.discard()
        respond stockCardInstance.errors, view: 'create'
      }
    }
  }

  def edit(StockCard stockCardInstance) {
    [stockCardInstance: stockCardInstance]
  }

  @Transactional
  def update(StockCard stockCardInstance) {
    if (stockCardInstance == null) {
      notFound()
      return
    }

    StockCard.withTransaction { tx ->
      try {
        def oldCmd = new StockCardCmd(stockCardInstance, true)
        def newCmd = new StockCardCmd(stockCardInstance)
        println "date: ${oldCmd.date} -> ${newCmd.date}"
        println "product: ${oldCmd.product} -> ${newCmd.product}"
        println "qty: ${oldCmd.qty} -> ${newCmd.qty}"


        def now = new Date()
        stockCardInstance.updater = currentUser
        stockCardInstance.lastUpdated = now

        def latestBalance = stockCardService.getBalance(newCmd)
        println "latestBalance:${latestBalance}"
        stockCardInstance.balance = latestBalance + stockCardInstance.qty //valid if not only changeDateToFuture
        newCmd.balance = stockCardInstance.balance
        println "stockCardInstance.balance:${stockCardInstance.balance}"

        if (!stockCardInstance.validate()) {
          respond stockCardInstance.errors, view: 'edit'
          return
        }

        stockCardInstance.save flush: true
        stockCardService.updateNextBalances(oldCmd, newCmd)

        flash.message = message(code: 'default.updated.message', args: [message(code: 'stockCard.label', default: 'stockCard'), stockCardInstance])
        redirect action: 'show', params: ['product.id': stockCardInstance.product.id]

      } catch (Exception e) {
        log.error(e.message, e)
        tx.setRollbackOnly()
        stockCardInstance.discard()
        respond stockCardInstance.errors, view: 'edit'
      }
    }
  }

  @Transactional
  def delete(StockCard stockCardInstance) {
    if (stockCardInstance == null) {
      notFound()
      return
    }
    def cmd = new StockCardCmd(stockCardInstance)

    stockCardInstance.delete flush: true

    stockCardService.updateNextBalances(cmd, null)

    flash.message = message(code: 'default.deleted.message', args: [message(code: 'stockCard.label', default: 'stockCard'), stockCardInstance])
    redirect action: "show", params: ['product.id': cmd.product.id]
  }

  protected void notFound() {
    flash.message = message(code: 'default.not.found.message', args: [message(code: 'stockCard.label', default: 'StockCard'), params.id])
    redirect action: "index"
  }

  def ajaxProductInfo() {
    Product product
    if (params.getLong('productId')) {
      product = Product.get(params.getLong('productId'))
    }
    render([uom: product?.uom?.name ?: ''] as JSON)
  }

}

class StockCardCmd {
  Long id
  Date date
  Product product
  BigDecimal qty
  BigDecimal balance

  StockCardCmd(StockCard stockCard) {
    id = stockCard.id
    date = stockCard.date
    product = stockCard.product
    qty = stockCard.qty
    balance = stockCard.balance
  }

  StockCardCmd(StockCard stockCard, boolean persistentValue) {
    if (persistentValue) {
      id = stockCard.id
      date = stockCard.getPersistentValue('date')
      product = stockCard.getPersistentValue('product')
      qty = stockCard.getPersistentValue('qty')
      balance = stockCard.getPersistentValue('balance')
    } else {
      StockCardCmd(stockCard)
    }
  }
}
