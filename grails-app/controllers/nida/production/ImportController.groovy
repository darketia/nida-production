package nida.production

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory

@Secured(['ROLE_ADMIN'])
@Transactional
class ImportController extends BaseController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def poiService


  def index() {

  }

  def exportProductTemplate() {
    try {
      poiService.getNewWorkbook()
      poiService.getNewSheet("sheet1")
      saleOrderHeaders.eachWithIndex { fieldName, i ->
        poiService.setCell(0, i, fieldName, PoiCellStyle.HEADER)
      }
      poiService.autoSizeColumns(saleOrderHeaders.size())
      poiService.export(response, "เทมเพลตนำเข้าข้อมูล_${message(code: 'product.label')}")
    } catch (Exception e) {
      log.error(e.message, e)
      flash.message = "error : ${e.message} - ${e.stackTrace[0]}"
      render view: 'index'
      return
    }
  }

  def importProduct() {
    //src
    def file = request.getFile("saleOrderFile")
    def fileName = file?.fileItem?.fileName
    if (!fileName) {
      flash.error = "กรุณาเลือกไฟล์ (File) ให้ถูกต้อง"
      render view: "index"
      return
    } else {
      def ext = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase()
      if (!ext.equals('xls') && !ext.equals('xlsx')) {
        flash.error = 'เอกสารที่นำเข้า ต้องเป็นประเภท .xls และ .xlsx เท่านั้น'
        render view: "index"
        return
      }
    }
    def inputStream = file.inputStream
    def wb = WorkbookFactory.create(inputStream)
    def sheet = wb.getSheetAt(0)
    if (!sheet) {
      flash.error = "เอกสารที่นำเข้าระบบ ไม่ตรงกับเทมเพลตของระบบ (ไม่พบ sheet)"
      render view: "index"
      return
    }

    Row row
    Cell cell
    def colIndex = 0
    def indexRowHeader = 0
    def indexRowData = 1
    def indexLastColumnTemplate = saleOrderHeaders.size() - 1
    def indexLastRow = sheet.getPhysicalNumberOfRows()
    def indexLastColumn = sheet.getRow(indexRowHeader)?.getLastCellNum() - 1

    //check template
    if (indexLastRow < indexRowData || indexLastColumn != indexLastColumnTemplate) {
      flash.error = "เอกสารที่นำเข้าระบบ ไม่ตรงกับเทมเพลตของระบบ จำนวนแถวหรือคอลัมน์น้อยกว่าที่กำหนด"
      render view: "index"
      return
    }

    def records = []
    try {
      (indexRowData..indexLastRow).each { rowIndex ->
        row = sheet.getRow(rowIndex)
        if (!row) return

        colIndex = 0
        def record = [:]

        def isAllBlank = true
        (0..indexLastColumnTemplate).each {
          isAllBlank = isAllBlank && (!row.getCell(it) || row.getCell(it).getCellType() == Cell.CELL_TYPE_BLANK)
        }
        if (isAllBlank) return

        record.code = getCellCode(row.getCell(colIndex++))
        record.name = getCellCode(row.getCell(colIndex++))

        def productGroupCode = getCellCode(row.getCell(colIndex++))
        record.productGroup = ProductGroup.findByCode(productGroupCode)
        if(!record.productGroup) throw new Exception("ผิดพลาดแถวที่ ${rowIndex+1} คอลัมน์ที่ ${colIndex} : ไม่พบกลุ่มสินค้า [${productGroupCode}]")

        def uomCode = getCellCode(row.getCell(colIndex++))
        record.uom = Uom.findByCode(uomCode)
        if(!record.uom) throw new Exception("ผิดพลาดแถวที่ ${rowIndex+1} คอลัมน์ที่ ${colIndex} : ไม่พบหน่วยนับ [${uomCode}]")

        priceTypes.each { priceType ->
          record["${priceType}_price"] = getCellPrice(row.getCell(colIndex++))
        }

        records << record
      }
    } catch (Exception e) {
      log.error(e.message, e)
      flash.error = "error : ${e.message}"
      render view: "index"
      return
    }



    Product.withTransaction { tx ->
      try {
        records.eachWithIndex { record, index ->
          def productInstance = Product.findByCode(record.code)
          if(!productInstance) productInstance = new Product(code: record.code, creator: currentUser)

          productInstance.name = record.name
          productInstance.productGroup = record.productGroup
          productInstance.uom = record.uom
          productInstance.updater = currentUser

          priceTypes.each { priceType ->
            ProductPrice productPrice
            if (productInstance.id) {
              productPrice = productInstance.productPrices.find{it.priceType == priceType}
            }
            if (!productPrice) {
              productPrice = new ProductPrice()
              productPrice.priceType = priceType
              productInstance.addToProductPrices(productPrice)
            }
            productPrice.price = record["${priceType}_price"]
          }


          if (!productInstance.validate()) {
            println productInstance.errors.fieldError
            throw new Exception("ข้อมูลแถวที่ ${index + 1} ไม่สามารถนำเข้าได้")
          } else {
            productInstance.save(flush: true)
          }
        }
      } catch (Exception e) {
        log.error(e.message, e)
        tx.setRollbackOnly()
        flash.error = "error : ${e.message}"
        render view: "index"
        return
      }
    }

    flash.message = " นำเข้าข้อมูล สินค้า สำเร็จ(ทั้งหมด  ${records.size()}  รายการ)"
    redirect action: 'index'
  }


  private getSaleOrderHeaders() {
    [message(code: 'product.code.label'),
        message(code: 'product.name.label'),
        "${message(code: 'product.productGroup.label')} (${message(code: 'productGroup.code.label')})",
        "${message(code: 'product.uom.label')} (${message(code: 'uom.code.label')})",
        "${message(code: 'productPrice.price.label')}${message(code: 'enum.PriceType.CASH')}",
        "${message(code: 'productPrice.price.label')}${message(code: 'enum.PriceType.CASH_TAX')}",
        "${message(code: 'productPrice.price.label')}${message(code: 'enum.PriceType.CREDIT')}",
        "${message(code: 'productPrice.price.label')}${message(code: 'enum.PriceType.CREDIT_TAX')}",
        "${message(code: 'productPrice.price.label')}${message(code: 'enum.PriceType.TECHNICAL')}",
    ]
  }

  private getPriceTypes() {
    [PriceType.CASH, PriceType.CASH_TAX, PriceType.CREDIT, PriceType.CREDIT_TAX, PriceType.TECHNICAL]
  }

  private getCellCode(Cell cell) {
    switch (cell?.getCellType()) {
      case Cell.CELL_TYPE_NUMERIC: return formatNumber(number: cell.getNumericCellValue(), format: '0')
      case Cell.CELL_TYPE_STRING: return cell.getStringCellValue().toString()
      case Cell.CELL_TYPE_FORMULA: return cell.getStringCellValue().toString()
      default: throw new Exception("ผิดพลาดแถวที่ ${cell?.getRowIndex() + 1} คอลัมน์ที่ ${cell?.getColumnIndex() + 1} : ประเภทไม่ถูกต้อง")
    }
  }

  private getCellPrice(Cell cell) {
    switch (cell?.getCellType()) {
      case Cell.CELL_TYPE_NUMERIC: return cell.getNumericCellValue()
      case Cell.CELL_TYPE_FORMULA: return cell.getNumericCellValue()
      default: throw new Exception("ผิดพลาดแถวที่ ${cell?.getRowIndex() + 1} คอลัมน์ที่ ${cell?.getColumnIndex() + 1} : ประเภทไม่ถูกต้อง")
    }
  }

}
