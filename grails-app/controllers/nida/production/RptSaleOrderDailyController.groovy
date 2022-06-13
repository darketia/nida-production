package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import java.text.SimpleDateFormat

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_REPORT'])
@Transactional(readOnly = true)
class RptSaleOrderDailyController extends BaseController{
  def reportService

  def index() {}

  def rpt(){
    println "rpt:${params}"
    def df = new SimpleDateFormat(message(code: 'default.date.format'))
    def date = df.parse(params.date)
    def customer = params.getLong('customer.id') ? Customer.get(params.getLong('customer.id')) : null
    def priceType = params.priceType ? params.priceType as PriceType : null

    List<SaleOrder> saleOrderList = SaleOrder.createCriteria().list(params) {
      if (date) eq('date', date)
      if (customer) eq('customer', customer)
      if (priceType) eq('priceType', priceType)
    }

    if(!saleOrderList){
      flash.message = "ไม่พบข้อมูลจากเงื่อนไขที่ระบุ"
      redirect action: 'index'
      return
    }

    def list = []
    saleOrderList.each{ saleOrder ->
      def record = [:]
      record.code = saleOrder.code
      record.customer = saleOrder.customer.name
      record.priceType = message(code:"enum.PriceType.${saleOrder.priceType}").toString()
      record.amount = saleOrder.amount
      record.remark = saleOrder.remark
      println record.remark
      list << record
    }

    def list2 = []
    list.groupBy {it.priceType}.each{k,v->
      def record = [:]
      record.priceType = k
      record.amount = v.amount.sum()
      list2 << record
    }

    String reportPath = "/WEB-INF/reports/rptSaleOrderDaily.jrxml"
    def reportName = ConvertFileNameUtils.toThai("${message(code:'rptSaleOrderDaily.label')}_${date.format('yyyyMMdd')}")
    HashMap parameters = new HashMap()
    parameters.put("date", ts.formatDate(date:date).toString())
    parameters.put("subTitle", "${customer?"ลูกค้า: ${customer.name}, ":''} ${priceType ? "ประเภทราคาขาย: ${message(code:"enum.PriceType.${priceType}")}":''}".toString())
    parameters.put("list", list)
    parameters.put("list2", list2)
    reportService.exportJasperDesignAsPdf(reportPath, parameters, reportName, response, false)
  }
}
