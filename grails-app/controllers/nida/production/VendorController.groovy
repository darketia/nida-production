package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import groovy.sql.Sql
import org.hibernate.jdbc.Work

import java.sql.Connection

@Secured(['ROLE_ADMIN', 'ROLE_PAGE_VENDOR'])
@Transactional(readOnly = true)
class VendorController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def poiService

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    if (params.excel) params.max = null
    def list = Vendor.createCriteria().list(params) {
      if (params.code) ilike('code', "%${params.code}%")
      if (params.name) ilike('name', "%${params.name}%")
    }

    if (params.excel) {
      if (!list) {
        flash.message = "ไม่พบข้อมูล"
        return [vendorInstanceList: list, vendorInstanceCount: list.totalCount]
      }
      return exportExcel(list.id)
    }
    [vendorInstanceList: list, vendorInstanceCount: list.totalCount]
  }

  def show(Vendor vendorInstance) {
    [vendorInstance: vendorInstance]
  }

  def create() {
    [vendorInstance: new Vendor(params)]
  }

  @Transactional
  def save(Vendor vendorInstance) {
    if (vendorInstance == null) {
      notFound()
      return
    }

    vendorInstance.creator = currentUser
    vendorInstance.updater = currentUser

    if (!vendorInstance.validate()) {
      respond vendorInstance.errors, view: 'create'
      return
    }

    vendorInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'vendor.label', default: 'vendor'), vendorInstance])
        redirect vendorInstance
      }
      '*' { respond vendorInstance, [status: CREATED] }
    }
  }

  def edit(Vendor vendorInstance) {
    [vendorInstance: vendorInstance]
  }

  @Transactional
  def update(Vendor vendorInstance) {
    if (vendorInstance == null) {
      notFound()
      return
    }

    vendorInstance.updater = currentUser

    if (!vendorInstance.validate()) {
      respond vendorInstance.errors, view: 'edit'
      return
    }

    vendorInstance.save flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'vendor.label', default: 'vendor'), vendorInstance])
        redirect vendorInstance
      }
      '*' { respond vendorInstance, [status: OK] }
    }
  }

  @Transactional
  def delete(Vendor vendorInstance) {

    if (vendorInstance == null) {
      notFound()
      return
    }

    vendorInstance.delete flush: true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'vendor.label', default: 'vendor'), vendorInstance])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NO_CONTENT }
    }
  }

  private def exportExcel(List<Long> ids) {
    def list = []
    Vendor.withSession { s ->
      s.doWork({ Connection connection ->
        Sql sql = new Sql(connection)
        list = sql.rows("""
        select v.code as รหัส
          , v.name as ชื่อ
          , v.address as ที่อยู่
          , v.tax_no as เลขประจำตัวผู้เสียภาษี
          , v.tel_no as เบอร์โทรศัพท์
          , v.email as Email
          , v.contact_person as ชื่อผู้ติดต่อ
        from vendor v 
        where v.id in (${ids.join(',')})
        order by v.code
        """.toString())
      } as Work)
    }
    poiService.writeTableWithDataFieldHeader(0, 0, list)
    poiService.export(response, "VENDOR_${new Date().format('yyyyMMdd')}")

  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'vendor.label', default: 'Vendor'), params.id])
        redirect action: "index", method: "GET"
      }
      '*' { render status: NOT_FOUND }
    }
  }
}
