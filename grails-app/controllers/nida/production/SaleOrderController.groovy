package nida.production

import grails.converters.JSON

import java.text.SimpleDateFormat

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_SALE_ORDER'])
@Transactional(readOnly = true)
class SaleOrderController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def sequenceService
  def reportService
  def stockCardService

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def df = new SimpleDateFormat(message(code: 'default.date.format'))

    def list = SaleOrder.createCriteria().list(params) {
      if (params.dateFrom) ge('date', df.parse(params.dateFrom))
      if (params.dateTo) le('date', df.parse(params.dateTo))
      if (params.code) ilike('code', "%${params.code}%")
      if (params.getLong('customer.id')) eq('customer', Customer.get(params.getLong('customer.id')))
      if (params.priceType) eq('priceType', params.priceType as PriceType)
      order('date', 'desc')
      order('code', 'desc')
    }
    [saleOrderInstanceList: list, saleOrderInstanceCount: list.totalCount]
  }

  def show(SaleOrder saleOrderInstance) {
    [saleOrderInstance: saleOrderInstance]
  }

  def create() {
    def saleOrderInstance = new SaleOrder(params)
    [saleOrderInstance: saleOrderInstance, cmd: getCmd(saleOrderInstance)]
  }

  def save(SaleOrderCmd cmd) {
    if (cmd == null) {
      notFound()
      return
    }
    def saleOrderInstance = new SaleOrder()
    mapProperties(saleOrderInstance, cmd)

    saleOrderInstance.code = "XXXX"
    saleOrderInstance.validate()
    validate(saleOrderInstance)
    if (saleOrderInstance.hasErrors()) {
      respond saleOrderInstance.errors, view: 'create', model: [cmd: cmd]
      return
    }

    sequenceService.lock.lock()
    try {
      saleOrderInstance.code = sequenceService.getNextCode(SequenceType.SALE_ORDER, "${saleOrderInstance.date.format('yyyyMM')}", 5)
      saleOrderInstance.save flush: true
    } finally {
      sequenceService.lock.unlock()
    }

    saleOrderInstance.saleOrderDetails?.each {
      stockCardService.saveOrUpdateBySaleOrderDetail(it)
    }
    saleOrderInstance.saleOrderReturnDetails?.each {
      stockCardService.saveOrUpdateBySaleOrderReturnDetail(it)
    }

    flash.message = message(code: 'default.created.message', args: [message(code: 'saleOrder.label', default: 'saleOrder'), saleOrderInstance])
    redirect action: 'show', id: saleOrderInstance.id, params: [print: (params.create1 ? 'shortInvoice' : 'a4Invoice'), custOnly: params.create3?true:false]
  }

  def edit(SaleOrder saleOrderInstance) {
    [saleOrderInstance: saleOrderInstance, cmd: getCmd(saleOrderInstance)]
  }

  @Transactional
  def update() {//def update(SaleOrderCmd cmd) <- bug cmd == null
    SaleOrderCmd cmd = new SaleOrderCmd()
    cmd.properties = params

    def saleOrderInstance = SaleOrder.get(cmd.id)
    cmd.code = saleOrderInstance.code
    println "saleOrderInstance.date=${saleOrderInstance.date}"
    mapProperties(saleOrderInstance, cmd)
    println "saleOrderInstance.date=${saleOrderInstance.date}"

    saleOrderInstance.validate()
    validate(saleOrderInstance)
    if (saleOrderInstance.hasErrors()) {
      respond saleOrderInstance.errors, view: 'edit', model: [cmd: cmd]
      return
    }

    saleOrderInstance.save flush: true

    saleOrderInstance.saleOrderDetails?.each {
      stockCardService.saveOrUpdateBySaleOrderDetail(it)
    }
    saleOrderInstance.saleOrderReturnDetails?.each {
      stockCardService.saveOrUpdateBySaleOrderReturnDetail(it)
    }

    flash.message = message(code: 'default.updated.message', args: [message(code: 'saleOrder.label', default: 'product'), saleOrderInstance])
    redirect saleOrderInstance
  }

  @Transactional
  def delete(SaleOrder saleOrderInstance) {

    if (saleOrderInstance == null) {
      notFound()
      return
    }

    saleOrderInstance.saleOrderDetails?.each { deletedDetail ->
      stockCardService.deleteBySaleOrderDetails(deletedDetail)
    }

    saleOrderInstance.saleOrderReturnDetails?.each { deletedReturnDetail ->
      stockCardService.deleteBySaleOrderReturnDetails(deletedReturnDetail)
    }

    saleOrderInstance.delete flush: true

    flash.message = message(code: 'default.deleted.message', args: [message(code: 'saleOrder.label', default: 'saleOrder'), saleOrderInstance])
    redirect action: "index", method: "GET"
  }

  protected void notFound() {
    flash.message = message(code: 'default.not.found.message', args: [message(code: 'saleOrder.label', default: 'SaleOrder'), params.id])
    redirect action: "index", method: "GET"
  }

  def ajaxCustomerInfo() {
    def customer = params.getLong('customerId') ? Customer.get(params.getLong('customerId')) : null
    render([name       : customer?.name ?: ''
            , address  : customer?.address ?: ''
            , telNo    : customer?.telNo ?: ''
            , priceType: customer?.priceType?.name() ?: ''
    ] as JSON)
  }

  def ajaxAddDetail() {
    def saleOrderDetail = new SaleOrderDetailCmd()
    if (!params.priceType) {
      render status: BAD_REQUEST, text: "กรุณาระบุ ${message(code: 'saleOrder.priceType.label')}"
      return
    }
    if (!params.productId && !params.barcode) {
      render status: BAD_REQUEST, text: "ไม่พบ สินค้า"
      return
    }
    def product = params.productId ? Product.get(params.productId) : Product.findByCode(params.barcode)
    if (!product) {
      render status: BAD_REQUEST, text: "ไม่พบ สินค้า[${params.productId ?: params.barcode}]"
      return
    }
    if (product.cancel) {
      render status: BAD_REQUEST, text: "สินค้า[${product}] เลิกขายแล้ว"
      return
    }
    def productPrice = ProductPrice.findByProductAndPriceType(product, params.priceType as PriceType)
    if (!productPrice) {
      render status: BAD_REQUEST, text: "ไม่พบ ราคาสินค้า[${product}]"
      return
    }

    saleOrderDetail.product = product
    saleOrderDetail.pricePerUnit = productPrice.price
    saleOrderDetail.qty = 1
    render(template: 'saleOrderDetail', model: [i: params.i, saleOrderDetail: saleOrderDetail])
  }

  def ajaxAddReturnDetail() {
    def saleOrderReturnDetail = new SaleOrderDetailCmd()
    def refSaleOrder = SaleOrder.findByCode(params.refReturnCode)
    if (!refSaleOrder) {
      render status: BAD_REQUEST, text: "ไม่พบ ประวัติการขาย[${params.refReturnCode}]"
      return
    }
    if (!params.productId && !params.barcode) {
      render status: BAD_REQUEST, text: "ไม่พบ สินค้า"
      return
    }
    def product = params.productId ? Product.get(params.productId) : Product.findByCode(params.barcode)
    if (!product) {
      render status: BAD_REQUEST, text: "ไม่พบ สินค้า[${params.productId ?: params.barcode}]"
      return
    }

    def saleOrderDetail = refSaleOrder.saleOrderDetails.find { it.product == product }

    if (!saleOrderDetail) {
      render status: BAD_REQUEST, text: "ประวัติการขาย[${refSaleOrder.code}] ไม่พบการซื้อสินค้า[${product}]"
      return
    }

    saleOrderReturnDetail.refReturnCode = refSaleOrder.code
    saleOrderReturnDetail.product = product
    saleOrderReturnDetail.pricePerUnit = saleOrderDetail.pricePerUnit
    saleOrderReturnDetail.qty = 1
    render(template: 'saleOrderReturnDetail', model: [i: params.i, saleOrderReturnDetail: saleOrderReturnDetail])
  }

  def shortInvoice(SaleOrder saleOrderInstance) {
    if (saleOrderInstance == null) {
      notFound()
      return
    }

    def reportName = ConvertFileNameUtils.toThai("${message(code: 'saleOrder.shortInvoice.label')}_${saleOrderInstance.code}_${new Date().format('yyyyMMddHHmm')}")

    try {
      def details = []
      saleOrderInstance.saleOrderDetails?.each { saleOrderDetail ->
        def detail = [:]
        detail.productName = "${saleOrderDetail.product.name}"
        detail.pricePerUnit = saleOrderDetail.pricePerUnit
        detail.qty = saleOrderDetail.qty
        detail.uom = saleOrderDetail.product.uom.name
        detail.amount = saleOrderDetail.qty * saleOrderDetail.pricePerUnit
        details << detail
      }
      saleOrderInstance.saleOrderReturnDetails?.each { saleOrderReturnDetail ->
        def detail = [:]
        detail.productName = "${saleOrderReturnDetail.product.name}(${saleOrderReturnDetail.refReturnCode})"
        detail.pricePerUnit = saleOrderReturnDetail.pricePerUnit
        detail.qty = -saleOrderReturnDetail.qty
        detail.uom = saleOrderReturnDetail.product.uom.name
        detail.amount = -saleOrderReturnDetail.qty * saleOrderReturnDetail.pricePerUnit
        details << detail
      }
      if (saleOrderInstance.deliveryPrice) {
        def detail = [:]
        detail.productName = "ค่าขนส่ง"
        detail.pricePerUnit = (saleOrderInstance.deliveryPrice != null && saleOrderInstance.deliveryTrip != null) ? saleOrderInstance.deliveryPrice / saleOrderInstance.deliveryTrip : null
        detail.qty = saleOrderInstance.deliveryTrip
        detail.uom = 'เที่ยว'
        detail.amount = saleOrderInstance.deliveryPrice
        details << detail
      }
      if (saleOrderInstance.discountAmount) {
        def detail = [:]
        detail.productName = "ส่วนลด"
        detail.qty = null
        detail.uom = null
        detail.amount = -saleOrderInstance.discountAmount
        details << detail
      }
      def company = currentCompany
      String reportPath = "/WEB-INF/reports/shortInvoice.jrxml"
      HashMap parameters = new HashMap()
      parameters.put("saleOrderId", saleOrderInstance.id)
      parameters.put("taxNo", company.taxNo)
      parameters.put("details", details)
      parameters.put("amount", saleOrderInstance.amount)
      parameters.put("logoPath", servletContext.getRealPath("/images/logo.png").replace("\\", "/"))
      reportService.exportJasperDesignAsPdf(reportPath, parameters, reportName, response, true)
    } catch (Exception e) {
      log.error(e.message, e)
      flash.message = "Error: ${e.message}"
      redirect action: 'show', id: saleOrderInstance.id
    }

  }

  def a4Invoice(SaleOrder saleOrderInstance) {
    if (saleOrderInstance == null) {
      notFound()
      return
    }

    def reportName = ConvertFileNameUtils.toThai("${message(code: 'saleOrder.a4Invoice.label')}_${saleOrderInstance.code}_${new Date().format('yyyyMMddHHmm')}")

    try {
      def details = []
      saleOrderInstance.saleOrderDetails?.each { saleOrderDetail ->
        def detail = [:]
        detail.productName = "${saleOrderDetail.product.name}"
        detail.pricePerUnit = saleOrderDetail.pricePerUnit
        detail.qty = saleOrderDetail.qty
        detail.uom = saleOrderDetail.product.uom.name
        detail.amount = saleOrderDetail.qty * saleOrderDetail.pricePerUnit
        details << detail
      }
      saleOrderInstance.saleOrderReturnDetails?.each { saleOrderReturnDetail ->
        def detail = [:]
        detail.productName = "${saleOrderReturnDetail.product.name}(${saleOrderReturnDetail.refReturnCode})"
        detail.pricePerUnit = saleOrderReturnDetail.pricePerUnit
        detail.qty = -saleOrderReturnDetail.qty
        detail.uom = saleOrderReturnDetail.product.uom.name
        detail.amount = -saleOrderReturnDetail.qty * saleOrderReturnDetail.pricePerUnit
        details << detail
      }
      if (saleOrderInstance.deliveryPrice) {
        def detail = [:]
        detail.productName = "ค่าขนส่ง"
        detail.pricePerUnit = (saleOrderInstance.deliveryPrice != null && saleOrderInstance.deliveryTrip != null) ? saleOrderInstance.deliveryPrice / saleOrderInstance.deliveryTrip : null
        detail.qty = saleOrderInstance.deliveryTrip
        detail.uom = 'เที่ยว'
        detail.amount = saleOrderInstance.deliveryPrice
        details << detail
      }
      if (saleOrderInstance.discountAmount) {
        def detail = [:]
        detail.productName = "ส่วนลด"
        detail.qty = null
        detail.uom = null
        detail.amount = -saleOrderInstance.discountAmount
        details << detail
      }
      def company = currentCompany
      HashMap parameters = new HashMap()
      parameters.put("saleOrderId", saleOrderInstance.id)
      parameters.put("taxNo", company.taxNo)
      parameters.put("details", details)
      parameters.put("amount", saleOrderInstance.amount)
      parameters.put("logoPath", servletContext.getRealPath("/images/logo.png").replace("\\", "/"))
      parameters.put("printDate", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date()))
      parameters.put("priceType", message(code: "enum.PriceType.${saleOrderInstance.priceType}.rpt"))
      if(params.custOnly){
        parameters.put("docType", "ลูกค้า")
        def copy = reportService.exportJasperDesignAsPdf("/WEB-INF/reports/a4Invoice_cust.jrxml", parameters, reportName, null)
        reportService.mergePdfs([copy], reportName, response, true)
      } else {
        parameters.put("docType", "ร้านค้า")
        def orig = reportService.exportJasperDesignAsPdf("/WEB-INF/reports/a4Invoice_shop.jrxml", parameters, reportName, null)
        parameters.put("docType", "ลูกค้า")
        def copy = reportService.exportJasperDesignAsPdf("/WEB-INF/reports/a4Invoice_cust.jrxml", parameters, reportName, null)
        reportService.mergePdfs([orig, copy], reportName, response, true)
      }
    } catch (Exception e) {
      log.error(e.message, e)
      flash.message = "Error: ${e.message}"
      redirect action: 'show', id: saleOrderInstance.id
    }

  }

  private getCmd(SaleOrder saleOrder) {
    def cmd = new SaleOrderCmd()
    cmd.id = saleOrder.id
    cmd.version = saleOrder.version
    cmd.date = saleOrder.date
    cmd.code = saleOrder.code
    cmd.customer = saleOrder.customer
    cmd.priceType = saleOrder.priceType
    cmd.deliveryHousingEstate = saleOrder.deliveryHousingEstate
    cmd.deliveryTrip = saleOrder.deliveryTrip
    cmd.deliveryPrice = saleOrder.deliveryPrice
    cmd.discountAmount = saleOrder.discountAmount
    cmd.receiveAmount = saleOrder.receiveAmount
    cmd.remark = saleOrder.remark
    cmd.saleOrderDetails = []
    saleOrder.saleOrderDetails?.each { saleOrderDetail ->
      def saleOrderDetailCmd = new SaleOrderDetailCmd()
      saleOrderDetailCmd.id = saleOrderDetail.id
      saleOrderDetailCmd.product = saleOrderDetail.product
      saleOrderDetailCmd.pricePerUnit = saleOrderDetail.pricePerUnit
      saleOrderDetailCmd.qty = saleOrderDetail.qty
      cmd.saleOrderDetails << saleOrderDetailCmd
    }
    cmd.saleOrderReturnDetails = []
    saleOrder.saleOrderReturnDetails?.each { saleOrderReturnDetail ->
      def saleOrderReturnDetailCmd = new SaleOrderDetailCmd()
      saleOrderReturnDetailCmd.id = saleOrderReturnDetail.id
      saleOrderReturnDetailCmd.product = saleOrderReturnDetail.product
      saleOrderReturnDetailCmd.pricePerUnit = saleOrderReturnDetail.pricePerUnit
      saleOrderReturnDetailCmd.qty = saleOrderReturnDetail.qty
      saleOrderReturnDetailCmd.refReturnCode = saleOrderReturnDetail.refReturnCode
      cmd.saleOrderReturnDetails << saleOrderReturnDetailCmd
    }
    cmd
  }

  private mapProperties(SaleOrder saleOrderInstance, SaleOrderCmd cmd) {
    saleOrderInstance.id = cmd.id
    saleOrderInstance.version = cmd.version
    saleOrderInstance.customer = cmd.customer
    saleOrderInstance.name = cmd.name
    saleOrderInstance.address = cmd.address
    saleOrderInstance.telNo = cmd.telNo
    if (cmd.updateCustomerInfo) {
      saleOrderInstance.customer.name = saleOrderInstance.name
      saleOrderInstance.customer.address = saleOrderInstance.address
      saleOrderInstance.customer.telNo = saleOrderInstance.telNo
      saleOrderInstance.customer.updater = currentUser
    }
    saleOrderInstance.priceType = cmd.priceType
    saleOrderInstance.deliveryHousingEstate = cmd.deliveryHousingEstate
    saleOrderInstance.deliveryTrip = cmd.deliveryHousingEstate ? cmd.deliveryTrip : null
    saleOrderInstance.deliveryPrice = cmd.deliveryHousingEstate ? cmd.deliveryPrice : null
    saleOrderInstance.discountAmount = cmd.discountAmount
    saleOrderInstance.receiveAmount = cmd.receiveAmount
    saleOrderInstance.remark = cmd.remark
    if (!saleOrderInstance.id) saleOrderInstance.creator = currentUser //create
    saleOrderInstance.updater = currentUser

    def deletedDetails = saleOrderInstance.saleOrderDetails?.findAll { !(it.id in cmd.saleOrderDetails?.id) }
    if (deletedDetails) {
      deletedDetails?.each { deletedDetail ->
        saleOrderInstance.removeFromSaleOrderDetails(deletedDetail)
        stockCardService.deleteBySaleOrderDetails(deletedDetail)
        deletedDetail.delete()
      }
    }

    def deletedReturnDetails = saleOrderInstance.saleOrderReturnDetails?.findAll {
      !(it.id in cmd.saleOrderReturnDetails?.id)
    }
    if (deletedReturnDetails) {
      deletedReturnDetails?.each { deletedReturnDetail ->
        saleOrderInstance.removeFromSaleOrderReturnDetails(deletedReturnDetail)
        stockCardService.deleteBySaleOrderReturnDetails(deletedReturnDetail)
        deletedReturnDetail.delete()
      }
    }

    if (deletedDetails || deletedReturnDetails) saleOrderInstance.save flush: true

    cmd.saleOrderDetails.each { saleOrderDetailCmd ->
      SaleOrderDetail saleOrderDetail
      if (saleOrderDetailCmd.id) saleOrderDetail = SaleOrderDetail.get(saleOrderDetailCmd.id)
      if (!saleOrderDetail) {
        saleOrderDetail = new SaleOrderDetail()
        saleOrderInstance.addToSaleOrderDetails(saleOrderDetail)
      }
      saleOrderDetail.product = saleOrderDetailCmd.product
      saleOrderDetail.pricePerUnit = saleOrderDetailCmd.pricePerUnit
      saleOrderDetail.qty = saleOrderDetailCmd.qty
    }

    cmd.saleOrderReturnDetails.each { saleOrderReturnDetailCmd ->
      SaleOrderReturnDetail saleOrderReturnDetail
      if (saleOrderReturnDetailCmd.id) saleOrderReturnDetail = SaleOrderReturnDetail.get(saleOrderReturnDetailCmd.id)
      if (!saleOrderReturnDetail) {
        saleOrderReturnDetail = new SaleOrderReturnDetail()
        saleOrderInstance.addToSaleOrderReturnDetails(saleOrderReturnDetail)
      }
      saleOrderReturnDetail.product = saleOrderReturnDetailCmd.product
      saleOrderReturnDetail.pricePerUnit = saleOrderReturnDetailCmd.pricePerUnit
      saleOrderReturnDetail.qty = saleOrderReturnDetailCmd.qty
      saleOrderReturnDetail.refReturnCode = saleOrderReturnDetailCmd.refReturnCode
    }
  }

  private validate(SaleOrder saleOrderInstance) {
    if (!saleOrderInstance.saleOrderDetails) {
      saleOrderInstance.errors.rejectValue('saleOrderDetails'
          , 'default.invalid.min.size.message'
          , [message(code: 'saleOrder.saleOrderDetails.label'), message(code: 'saleOrder.label'), 0, 1] as Object[]
          , "Property [{0}] of class [{1}] with value [{2}] is less than the minimum size of [{3}]")
    }

  }

  def ajaxHousingEstates() {
    def subDistrict = params.getLong('subDistrictId') ? SubDistrict.get(params.getLong('subDistrictId')) : null

    def ddl = []
    def temp = subDistrict ? HousingEstate.findAllBySubDistrict(subDistrict) : []
    temp.each { ddl << [value: it.id, text: "${it}"] }

    render([ddl: ddl] as JSON)
  }

  def ajaxDeliveryPrice() {
    def housingEstate = params.getLong('housingEstateId') ? HousingEstate.get(params.getLong('housingEstateId')) : null
    render text: housingEstate?.deliveryPrice ?: ''
  }
}

class SaleOrderCmd {
  Long id
  Long version
  Date date = new Date()
  String code
  Customer customer
  String name
  String address
  String telNo
  PriceType priceType
  HousingEstate deliveryHousingEstate
  Integer deliveryTrip
  BigDecimal deliveryPrice
  BigDecimal discountAmount
  BigDecimal receiveAmount
  String remark
  boolean updateCustomerInfo
  List<SaleOrderDetailCmd> saleOrderDetails
  List<SaleOrderDetailCmd> saleOrderReturnDetails
}

class SaleOrderDetailCmd {
  Long id
  Product product
  BigDecimal pricePerUnit
  BigDecimal qty
  String refReturnCode
}
