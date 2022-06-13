package nida.production

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import groovy.sql.Sql
import org.hibernate.jdbc.Work

import java.sql.Connection
import java.text.SimpleDateFormat

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_PO'])
@Transactional(readOnly = true)
class PoController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def sequenceService
  def reportService
  def poiService

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    if (params.excel) params.max = null
    def df = new SimpleDateFormat(message(code: 'default.date.format'))

    def authPoTypes = SecUserPoType.findAllBySecUser(currentUser)?.poType?.flatten()

    List<Po> list = []
    if (params.poItemName) {
      List<Po> listByItem = Po.createCriteria().listDistinct {
        if (authPoTypes) inList('poType', authPoTypes)
        if (params.dateFrom) ge('date', df.parse(params.dateFrom))
        if (params.dateTo) le('date', df.parse(params.dateTo))
        if (params.code) ilike('code', "%${params.code}%")
        if (params.getList('poStatus')) inList('poStatus', params.getList('poStatus').collect { it as PoStatus })
        if (params.getLong('poType.id')) eq('poType', PoType.get(params.getLong('poType.id')))
        if (params.getLong('vendor.id')) eq('vendor', Vendor.get(params.getLong('vendor.id')))
        poDetails {
          sqlRestriction("upper(name) like '%${params.poItemName.toUpperCase()}%'")
        }
      }
      if (listByItem) {
        list = Po.createCriteria().list(params) {
          inList('id', listByItem.id)
          order('date', 'desc')
          order('code', 'desc')
        }
      }
    } else {
      list = Po.createCriteria().list(params) {
        if (authPoTypes) inList('poType', authPoTypes)
        if (params.dateFrom) ge('date', df.parse(params.dateFrom))
        if (params.dateTo) le('date', df.parse(params.dateTo))
        if (params.code) ilike('code', "%${params.code}%")
        if (params.getList('poStatus')) inList('poStatus', params.getList('poStatus').collect { it as PoStatus })
        if (params.getLong('poType.id')) eq('poType', PoType.get(params.getLong('poType.id')))
        if (params.getLong('vendor.id')) eq('vendor', Vendor.get(params.getLong('vendor.id')))
        if (params.poItemName) {
          poDetails {
            like("name", "%${params.poItemName}%")
          }
        }
        order('date', 'desc')
        order('code', 'desc')
      }
    }

    if (params.excel) {
      if (!list) {
        flash.message = "ไม่พบข้อมูล"
        return [poInstanceList: list, poInstanceCount: list.totalCount]
      }
      return exportExcel(list.id)
    }
    [poInstanceList: list, poInstanceCount: list.totalCount]
  }

  def show(Po poInstance) {
    [poInstance: poInstance]
  }

  def create() {
    def poInstance = new Po(params)
    def company = Company.first()
    poInstance.vatRate = company.vatRate
    [poInstance: poInstance, cmd: getCmd(poInstance)]
  }

  def copy(Po oldPo) {
    def company = Company.first()

    Po poInstance = new Po()
    poInstance.date = new Date()
    poInstance.vendor = oldPo.vendor
    poInstance.poType = oldPo.poType
    poInstance.enableScale = oldPo.enableScale
    poInstance.deliveryDate = oldPo.deliveryDate
    poInstance.paymentTerm = oldPo.paymentTerm
    poInstance.shipLocation = oldPo.shipLocation
    poInstance.vatRate = oldPo.vatRate
    poInstance.discountAmount = oldPo.discountAmount
    poInstance.remark = oldPo.remark

    oldPo.poDetails.each { oldPoDetail ->
      PoDetail poDetail = new PoDetail()
      poDetail.name = oldPoDetail.name
      poDetail.pricePerUnit = oldPoDetail.pricePerUnit
      poDetail.qty = oldPoDetail.qty
      poDetail.uom = oldPoDetail.uom
      poDetail.packSize = oldPoDetail.packSize
      poInstance.addToPoDetails(poDetail)
    }

    render(view:"create", model: [poInstance: poInstance, cmd: getCmd(poInstance)])
  }

  @Transactional
  def save(PoCmd cmd) {
    if (cmd == null) {
      notFound()
      return
    }
    def poInstance = new Po()
    mapProperties(poInstance, cmd)

    poInstance.code = "XXXX"
    poInstance.validate()
    validate(poInstance)
    if (poInstance.hasErrors()) {
      respond poInstance.errors, view: 'create', model: [cmd: cmd]
      return
    }

    sequenceService.lock.lock()
    try {
      poInstance.code = sequenceService.getNextCode(SequenceType.PO, "${poInstance.date.format('yyyyMM')}", 3)
      poInstance.save flush: true
    } finally {
      sequenceService.lock.unlock()
    }

    flash.message = message(code: 'default.created.message', args: [message(code: 'po.label'), poInstance])
    redirect action: 'show', id: poInstance.id
  }

  def edit(Po poInstance) {
    [poInstance: poInstance, cmd: getCmd(poInstance)]
  }

  @Transactional
  def newRev(Po oldPo) {
    if (oldPo.poStatus != PoStatus.NEW) {
      flash.message = "PO นี้ไม่สามารถสร้าง Rev. ใหม่ได้ เพราะสถานะไม่ใช่สร้างใหม่"
      redirect(action: 'show', id: oldPo.id)
      return
    }

    Po poInstance = new Po()
    poInstance.date = new Date()
    poInstance.rev = (oldPo.rev ?: 0) + 1
    def revStr = poInstance.rev > 9 ? "${poInstance.rev}" : "0${poInstance.rev}"
    poInstance.code = "${oldPo.code.split("_")[0]}_REV${revStr}"
    poInstance.vendor = oldPo.vendor
    poInstance.poType = oldPo.poType
    poInstance.enableScale = oldPo.enableScale
    poInstance.deliveryDate = oldPo.deliveryDate
    poInstance.paymentTerm = oldPo.paymentTerm
    poInstance.shipLocation = oldPo.shipLocation
    poInstance.vatRate = oldPo.vatRate
    poInstance.discountAmount = oldPo.discountAmount
    poInstance.remark = oldPo.remark
    poInstance.creator = currentUser
    poInstance.updater = currentUser

    oldPo.poDetails.each { oldPoDetail ->
      PoDetail poDetail = new PoDetail()
      poDetail.name = oldPoDetail.name
      poDetail.pricePerUnit = oldPoDetail.pricePerUnit
      poDetail.qty = oldPoDetail.qty
      poDetail.uom = oldPoDetail.uom
      poDetail.packSize = oldPoDetail.packSize
      poInstance.addToPoDetails(poDetail)
    }

    poInstance.save()
    oldPo.poStatus = PoStatus.CANCELED
    oldPo.save()

    redirect(action: 'edit', id: poInstance.id)
  }

  @Transactional
  def close(Po poInstance) {
    poInstance.poStatus = PoStatus.CLOSED;
    poInstance.save flush: true

    flash.message = message(code: 'default.updated.message', args: [message(code: 'po.label'), poInstance])
    redirect poInstance
  }

  @Transactional
  def update() {//def update(PoCmd cmd) <- bug cmd == null
    PoCmd cmd = new PoCmd()
    cmd.properties = params

    def poInstance = Po.get(cmd.id)
    cmd.code = poInstance.code
    mapProperties(poInstance, cmd)

    poInstance.validate()
    validate(poInstance)
    if (poInstance.hasErrors()) {
      respond poInstance.errors, view: 'edit', model: [cmd: cmd]
      return
    }

    poInstance.save flush: true

    flash.message = message(code: 'default.updated.message', args: [message(code: 'po.label'), poInstance])
    redirect poInstance
  }


  @Transactional
  def delete(Po poInstance) {

    if (poInstance == null) {
      notFound()
      return
    }

    poInstance.delete flush: true

    flash.message = message(code: 'default.deleted.message', args: [message(code: 'po.label'), poInstance])
    redirect action: "index", method: "GET"
  }

  def exportPdf(Po po) {
    if (po == null) {
      notFound()
      return
    }

    def reportName = ConvertFileNameUtils.toThai("${message(code: 'po.label')}_${po.code}_${new Date().format('yyyyMMddHHmm')}")

    try {
      def details = []
      po.poDetails.sort { it.id }.each { poDetail ->
        def detail = [:]
        detail.name = "${poDetail.name}"
        detail.pricePerUnit = poDetail.pricePerUnit
        detail.qty = poDetail.qty
        detail.uom = poDetail.uom
        detail.amount = poDetail.qty * poDetail.pricePerUnit
        details << detail
      }

//      if (po.discountAmount) {
//        def detail = [:]
//        detail.productName = "ส่วนลด"
//        detail.qty = null
//        detail.uom = null
//        detail.amount = -po.discountAmount
//        details << detail
//      }

      def company = currentCompany
      HashMap parameters = new HashMap()
      parameters.put("logoPath", servletContext.getRealPath("/images/logo_po.png").replace("\\", "/"))
      parameters.put("printDate", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date()))
      parameters.put("companyName", company.name)
      parameters.put("taxNo", company.taxNo)

      parameters.put("poId", po.id)
      parameters.put("date", new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(po.date))
      parameters.put("vendor", po.vendor.name)
      parameters.put("vendorAddress", po.vendor.address)
      parameters.put("isHeadOffice", po.shipLocation.equals(ShipLocation.HEAD_OFFICE))
      parameters.put("vatRate", "${formatNumber(number: po.vatRate, format: "0")}".toString())
      parameters.put("deliveryDate", po.deliveryDate)
      parameters.put("paymentTerm", po.paymentTerm)
      parameters.put("code", po.code)
      parameters.put("enableScale", po.enableScale)

      parameters.put("details", details)
      parameters.put("remark", po.remark ?: "-")
      parameters.put("amountDetail", po.amountDetail)
      parameters.put("discountAmount", po.discountAmount)
      parameters.put("vat", po.vat)
      parameters.put("amountWithVat", po.amountWithVat)

      reportService.exportJasperDesignAsPdf("/WEB-INF/reports/po.jrxml", parameters, reportName, response)
    } catch (Exception e) {
      log.error(e.message, e)
      flash.message = "Error: ${e.message}"
      redirect action: 'show', id: po.id
    }

  }

  private def exportExcel(List<Long> ids) {
    def list = []
    Po.withSession { s ->
      s.doWork({ Connection connection ->
        Sql sql = new Sql(connection)
        list = sql.rows("""
        select po.code as เลขที่ใบสั่งซื้อ
          , to_char(po.date, 'DD-MM-YYYY') as วันที่
          , case po.po_status
            when 'NEW' then 'สร้างใหม่'
            when 'CLOSED' then 'ปิด'
            when 'CANCELED' then 'ยกเลิก'
            else po.po_status end as "สถานะ PO"
          , t.code type_code, t.name type_name
          , v.code vendor_code, v.name vendor_name
          , case po.ship_location 
            when 'HEAD_OFFICE' then 'ออฟฟิศ'
            when 'PLANT_1' then 'โรงงาน'
            else po.ship_location end as สาขาที่ออกใบกำกับ
          , po.delivery_date as กำหนดส่งสินค้า
          , po.payment_term as เครดิต
          , po.vat_rate as "VAT %"
          , po.discount_amount as ส่วนลด
          , po.remark as หมายเหตุ
          , d.po_details_idx+1 as ลำดับ
          , d.name as รายการ
          , d.pack_size as "pack size"
          , case when d.pack_size is not null then d.qty / d.pack_size else null end as "จำนวน pack size"
          , d.qty as จำนวน
          , d.price_per_unit as ราคาต่อหน่วย
          , d.uom as หน่วย
          , d.qty * d.price_per_unit  as จำนวนเงิน
        from po 
          join po_type t on t.id = po.po_type_id
          join vendor v on v.id = po.vendor_id
          join po_detail d on d.po_id = po.id
        where po.id in (${ids.join(',')})
        order by po.date desc, po.code desc
        """.toString())
      } as Work)
    }
    poiService.writeTableWithDataFieldHeader(0, 0, list)
    poiService.export(response, "PO_${new Date().format('yyyyMMdd')}")
  }

  protected void notFound() {
    flash.message = message(code: 'default.not.found.message', args: [message(code: 'po.label'), params.id])
    redirect action: "index", method: "GET"
  }

  def ajaxAddDetail() {
    render(template: 'poDetail', model: [i: params.i, poDetail: new PoDetailCmd()])
  }

  private getCmd(Po po) {
    def cmd = new PoCmd()
    cmd.id = po.id
    cmd.version = po.version
    cmd.date = po.date
    cmd.code = po.code
    cmd.vendor = po.vendor
    cmd.poType = po.poType
    cmd.enableScale = po.enableScale
    cmd.deliveryDate = po.deliveryDate
    cmd.paymentTerm = po.paymentTerm
    cmd.poStatus = po.poStatus
    cmd.shipLocation = po.shipLocation
    cmd.vatRate = po.vatRate
    cmd.discountAmount = po.discountAmount
    cmd.remark = po.remark
    cmd.poDetails = []
    po.poDetails?.each { poDetail ->
      def poDetailCmd = new PoDetailCmd()
      poDetailCmd.id = poDetail.id
      poDetailCmd.name = poDetail.name
      poDetailCmd.pricePerUnit = poDetail.pricePerUnit
      poDetailCmd.qty = poDetail.qty
      poDetailCmd.uom = poDetail.uom
      poDetailCmd.packSize = poDetail.packSize
      cmd.poDetails << poDetailCmd
    }

    cmd
  }

  private mapProperties(Po poInstance, PoCmd cmd) {
    poInstance.id = cmd.id
    poInstance.version = cmd.version
    poInstance.vendor = cmd.vendor
    poInstance.poType = cmd.poType
    poInstance.enableScale = cmd.enableScale
    poInstance.deliveryDate = cmd.deliveryDate
    poInstance.paymentTerm = cmd.paymentTerm
    poInstance.poStatus = cmd.poStatus ?: PoStatus.NEW
    poInstance.shipLocation = cmd.shipLocation
    poInstance.vatRate = cmd.vatRate
    poInstance.discountAmount = cmd.discountAmount
    poInstance.remark = cmd.remark
    if (!poInstance.id) poInstance.creator = currentUser //create
    poInstance.updater = currentUser

    def deletedDetails = poInstance.poDetails?.findAll { !(it.id in cmd.poDetails?.id) }

    if (deletedDetails) {
      deletedDetails?.each { deletedDetail ->
        poInstance.removeFromPoDetails(deletedDetail)
        deletedDetail.delete()
      }
    }

    if (deletedDetails) poInstance.save flush: true
    cmd.poDetails.each { poDetailCmd ->
      PoDetail poDetail
      if (poDetailCmd.id) poDetail = PoDetail.get(poDetailCmd.id)
      if (!poDetail) {
        poDetail = new PoDetail()
        poInstance.addToPoDetails(poDetail)
      }
      poDetail.name = poDetailCmd.name
      poDetail.pricePerUnit = poDetailCmd.pricePerUnit
      poDetail.qty = poDetailCmd.qty
      poDetail.uom = poDetailCmd.uom
      poDetail.packSize = poDetailCmd.packSize
    }

  }

  private validate(Po poInstance) {
    if (!poInstance.poDetails) {
      poInstance.errors.rejectValue('poDetails'
          , 'default.invalid.min.size.message'
          , [message(code: 'po.poDetails.label'), message(code: 'po.label'), 0, 1] as Object[]
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

class PoCmd {
  Long id
  Long version
  Date date
  String code
  Vendor vendor
  PoType poType
  Boolean enableScale
  String deliveryDate
  String paymentTerm
  ShipLocation shipLocation = ShipLocation.PLANT_1
  PoStatus poStatus = PoStatus.NEW
  BigDecimal vatRate
  BigDecimal discountAmount = BigDecimal.ZERO
  String remark
  List<PoDetailCmd> poDetails
}

class PoDetailCmd {
  Long id
  String name
  BigDecimal pricePerUnit
  BigDecimal qty
  String uom
  BigDecimal packSize
}
