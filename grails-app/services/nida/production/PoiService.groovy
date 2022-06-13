package nida.production

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.web.util.WebUtils
import org.hibernate.Session
import org.hibernate.jdbc.Work

import javax.servlet.http.HttpServletRequest
import java.sql.Connection
import java.text.SimpleDateFormat

class PoiService extends AbstractEnvAwareService {

  boolean transactional = false

  def sessionFactory

  private Map<HttpServletRequest, XSSFWorkbook> workingWbs = [:]
  private Map<HttpServletRequest, XSSFSheet> workingSheets = [:]
  private Map<HttpServletRequest, Map<String, XSSFCellStyle>> workingCellStyles = [:]

  def final fullDateFormat = new SimpleDateFormat('dd/MM/yyyy HH:mm:ss', new Locale('th', 'TH'))
  def final dateFormat = new SimpleDateFormat('dd/MM/yyyy', new Locale('th', 'TH'))

  private getRequest() { return WebUtils.retrieveGrailsWebRequest().getCurrentRequest() }

  XSSFWorkbook getNewWorkbook() {
    workingWbs[request] = new XSSFWorkbook()
    workingSheets[request] = null
    workingCellStyles[request] = [:]
    return workingWbs[request]
  }

  XSSFSheet getNewSheet(String sheetName = 'Sheet1') {
    workingSheets[request] = getWorkbook().createSheet(sheetName)
    return workingSheets[request]
  }

  XSSFWorkbook getWorkbook() { workingWbs[request] ?: getNewWorkbook() }

  private clearRequestData() {
    workingWbs.remove(request)
    workingSheets.remove(request)
    workingCellStyles.remove(request)
  }

  XSSFSheet getSheet() { workingSheets[request] ?: getNewSheet() }

  Row getRow(int rowIdx) {
    getSheet().getRow(rowIdx) ?: getSheet().createRow(rowIdx)
  }

  Cell getCell(int rowIdx, int colIdx) {
    getRow(rowIdx).getCell(colIdx) ?: getRow(rowIdx).createCell(colIdx)
  }

  int mergeCell(int top, int left, int bottom, int right) {
    getSheet().addMergedRegion(new CellRangeAddress(top, bottom, left, right))
  }

  Cell setCell(int rowIdx, int colIdx, value, PoiCellStyle style = null) {
    Cell cell = getCell(rowIdx, colIdx)
    cell.setCellValue(value)
    if (style) cell.setCellStyle(getCellStyle(style))
    return cell
  }

  String setCellWithRefText(int rowIdx, int colIdx, value, PoiCellStyle style = null) {
    this.getCellRefText(this.setCell(rowIdx, colIdx, value, style))
  }

  Cell setCellFormula(int rowIdx, int colIdx, formula, PoiCellStyle style = null) {
    Cell cell = getCell(rowIdx, colIdx)
    cell.setCellFormula(formula)
    if (style) cell.setCellStyle(getCellStyle(style))
    return cell
  }

  String setCellFormulaWithRefText(int rowIdx, int colIdx, String formula, PoiCellStyle style = null) {
    this.getCellRefText(this.setCellFormula(rowIdx, colIdx, formula, style))
  }

  String getCellRefText(Cell cell) {
    new CellReference(cell).formatAsString()
  }

  Cell setMergedCell(int top, int left, int bottom, int right, value, PoiCellStyle style = null) {
    Cell cell = getCell(top, left)
    cell.setCellValue(value)
    mergeCell(top, left, bottom, right)
    if (style) {
      (top..bottom).each { r ->
        (left..right).each { c ->
          getCell(r, c).setCellStyle(getCellStyle(style))
        }
      }
    }
    return cell
  }

  XSSFCellStyle getCellStyle(PoiCellStyle style) {
    def workbook = getWorkbook()
    if (!workingCellStyles[request]) {
      workingCellStyles[request] = [:]
    }
    if (!workingCellStyles[request][style.config]) {
      workingCellStyles[request][style.config] = style.createCellStyle(workbook)
    }
    return workingCellStyles[request][style.config]
  }

  def writeTableWithDataFieldHeader(int rowIdx, int colIdx, List data) {
    if (!data) return rowIdx
    def fields = data.first().collect { key, value -> key }
    return writeTable(rowIdx, colIdx, data, fields, fields, [])
  }

  def writeTable(int rowIdx, int colIdx, List data, List fields, List headers, Collection summaries) {

    if (!data) return rowIdx

    def cell
    Sheet sheet = getSheet()

    if (fields && headers) {
      headers.eachWithIndex { fieldName, i ->
        setCell(rowIdx, i + colIdx, fieldName, PoiCellStyle.HEADER)
      }
      rowIdx++
    }


    def sum = [:]
    data.eachWithIndex { result, rowNum ->
      if (rowNum % 100 == 0) {
        sessionFactory.currentSession.clear()
      }
      if (rowNum % 1000 == 0) {
        log.info("processing rowIdx number ${rowNum + rowIdx}")
      }

      fields.eachWithIndex { field, fieldIdx ->
        def pcs = new PoiCellStyle(PoiCellStyle.DATA)
        def dataFormat = getDataFormatByCellDataType(result[field])
        if (dataFormat) pcs.addConfig([dataFormat: dataFormat])
        setCell(rowNum + rowIdx, fieldIdx + colIdx, result[field], pcs)
        if (summaries && field in summaries) sum[field] = (sum[field] ?: 0.0) + result[field]
      }
    }
    rowIdx += data.size()

    if (summaries) {
      fields.eachWithIndex { field, fieldIdx ->
        def pcs = new PoiCellStyle(PoiCellStyle.SUMMARY)
        def dataFormat = getDataFormatByCellDataType(sum[field])
        if (dataFormat) pcs.addConfig([dataFormat: dataFormat])
        setCell(rowIdx, fieldIdx + colIdx, (summaries && field in summaries) ? sum[field] : '', pcs)
      }
      rowIdx++
    }
    return rowIdx
  }

  def autoSizeColumns(int noOfCols, Sheet sheet = null) {
    def colIndices = (0..noOfCols - 1)

    if (sheet) {
      colIndices.each { n ->
        sheet.setColumnWidth(n, 3500)
        sheet.autoSizeColumn(n)
      }
    } else {
      def wb = getWorkbook()
      for (int i = 0; i < wb.getNumberOfSheets(); i++) {
        sheet = wb.getSheetAt(i)
        colIndices.each { n ->
          // column width unit is 1/256th of 1 character
          // maximum of width is 255 characters in excel, so the max value is 255 * 256 = 65280
          sheet.setColumnWidth(n, 60000)
          sheet.autoSizeColumn(n)
          sheet.setColumnWidth(n, sheet.getColumnWidth(n) + 600) // plus ~2 characters because some Thai characters are thin or super, sub scripted.
        }
      }
    }
  }

  def export(response, String fileName) {
    def i = fileName.lastIndexOf('.');
    def extension = ''
    fileName = fileName ?: 'Download'
    if (i > 0) {
      extension = fileName.substring(i + 1);
    }
    if (extension.toUpperCase() != 'XLSX') fileName += ".xlsx"

    response.setContentType("application/vnd.ms-excel")
    def dlName = ConvertFileNameUtils.toThai(fileName)
    response.setHeader("Content-Disposition", "attachment; filename=${dlName}")
    OutputStream out = response.getOutputStream()
    getWorkbook().write(out)
    out.close();
    clearRequestData()
  }

  def getDataFormatByCellDataType(data) {
    def dataFormat = ''
    if (data == null) return ''
    switch (data?.class?.name) {
      case 'java.math.BigDecimal':
        if (data?.scale() >= 3) dataFormat = '#,##0.000'
        else if (data != null) dataFormat = '#,##0.00'
        break
      case 'java.math.Integer':
      case 'java.math.Long':
        dataFormat = '#,##0'
        break
    }
    return dataFormat
  }

  def nativeQuery(sqlTxt, parameters) {
    List<GroovyRowResult> dataRows

    log.info 'Native Query started'
    Company.withSession { Session session ->
      session.doWork({ Connection connection ->
        Sql sql = new Sql(connection)
        dataRows = sql.rows(sqlTxt, parameters)
      } as Work)
    }
    log.info 'Native Query finished'

    return dataRows
  }

  private getStaticProperty(String name, Class clazz) {
    def noArgs = [].toArray()
    def methodName = 'get' + name[0].toUpperCase()

    if (name.size() > 1) {
      methodName += name[1..-1]
    }

    clazz.metaClass.getStaticMetaMethod(methodName, noArgs)
  }
}

class PoiCellStyle {
  static final ALL_BORDER = [borderTop: BorderStyle.THIN, borderLeft: BorderStyle.THIN, borderRight: BorderStyle.THIN, borderBottom: BorderStyle.THIN]

  static final NORMAL = new PoiCellStyle([fontSize: 11, fontWeight: Font.BOLDWEIGHT_NORMAL, verticalAlign: CellStyle.VERTICAL_CENTER])

  static final LEFT = new PoiCellStyle(NORMAL, [textAlign: CellStyle.ALIGN_LEFT])
  static final LEFT_UNDERLINE = new PoiCellStyle(LEFT, [borderBottom: BorderStyle.THIN])

  static final CENTER = new PoiCellStyle(NORMAL, [textAlign: CellStyle.ALIGN_CENTER])
  static final CENTER_UNDERLINE = new PoiCellStyle(CENTER, [borderBottom: BorderStyle.THIN])

  static final RIGHT = new PoiCellStyle(NORMAL, [textAlign: CellStyle.ALIGN_RIGHT])
  static final RIGHT_UNDERLINE = new PoiCellStyle(RIGHT, [borderBottom: BorderStyle.THIN])
  static final RIGHT_BIG_DECIMAL2 = new PoiCellStyle(RIGHT, [dataFormat: '#,##0.00'])

  static final SUPER_HEADER = new PoiCellStyle(CENTER, [fontWeight: Font.BOLDWEIGHT_BOLD, bgColor: IndexedColors.PALE_BLUE])
  static final HEADER = new PoiCellStyle(SUPER_HEADER, ALL_BORDER)

  static final SUMMARY = new PoiCellStyle(HEADER, [textAlign: CellStyle.ALIGN_RIGHT, borderBottom: BorderStyle.DOUBLE])
  static final SUMMARY_BIG_DECIMAL2 = new PoiCellStyle(SUMMARY, [dataFormat: '#,##0.00'])
  static final SUMMARY_BIG_DECIMAL3 = new PoiCellStyle(SUMMARY, [dataFormat: '#,##0.000'])
  static final SUMMARY_INTEGER = new PoiCellStyle(SUMMARY, [dataFormat: '#,##0'])
  static final SUMMARY_LEFT = new PoiCellStyle(SUMMARY, [textAlign: CellStyle.ALIGN_LEFT])

  static final DATA = new PoiCellStyle(NORMAL, ALL_BORDER)
  static final DATA_CENTER = new PoiCellStyle(DATA, [textAlign: CellStyle.ALIGN_CENTER])
  static final DATA_LEFT = new PoiCellStyle(DATA, [textAlign: CellStyle.ALIGN_LEFT])
  static final DATA_RIGHT = new PoiCellStyle(DATA, [textAlign: CellStyle.ALIGN_RIGHT])
  static final DATA_BIG_DECIMAL2 = new PoiCellStyle(DATA, [dataFormat: '#,##0.00'])
  static final DATA_BIG_DECIMAL2_UNDERLINE = new PoiCellStyle(DATA_BIG_DECIMAL2, [borderBottom: BorderStyle.THIN])
  static final DATA_BIG_DECIMAL2_PERCENT = new PoiCellStyle(DATA, [dataFormat: '#,##0.00%'])
  static final DATA_BIG_DECIMAL3 = new PoiCellStyle(DATA, [dataFormat: '#,##0.000'])
  static final DATA_INTEGER = new PoiCellStyle(DATA, [dataFormat: '#,##0'])

  static final TITLE = new PoiCellStyle(NORMAL, [fontSize: 12, fontWeight: Font.BOLDWEIGHT_BOLD, textAlign: CellStyle.ALIGN_CENTER])
  static final TITLE_UNDERLINE = new PoiCellStyle(TITLE, [borderBottom: BorderStyle.THIN])
  static final TITLE_LEFT = new PoiCellStyle(TITLE, [textAlign: CellStyle.ALIGN_LEFT])
  static final TITLE_LEFT_UNDERLINE = new PoiCellStyle(TITLE_LEFT, [borderBottom: BorderStyle.THIN])
  static final TITLE_RIGHT = new PoiCellStyle(TITLE, [textAlign: CellStyle.ALIGN_RIGHT])
  static final TITLE_RIGHT_UNDERLINE = new PoiCellStyle(TITLE_RIGHT, [borderBottom: BorderStyle.THIN])

  private config = [:]

  def PoiCellStyle(Map newConfig) {
    this.config = (newConfig ?: [:]) + [:]
  }

  def PoiCellStyle(PoiCellStyle daddy) {
    this.config = daddy.config + [:]
  }

  def PoiCellStyle(PoiCellStyle daddy, Map additionalConfig) {
    this.config = (daddy.config ?: [:]) + (additionalConfig ?: [:])
  }

  def addConfig(Map additionalConfig) {
    this.config = (this.config ?: [:]) + (additionalConfig ?: [:])
  }

  def removeConfig(Map toBeRemovedConfig) {
    this.config = (this.config ?: [:]) - (toBeRemovedConfig ?: [:])
  }

  def getConfig() {
    (config ?: [:]) + [:]
  }

  XSSFCellStyle createCellStyle(XSSFWorkbook workbook) {
    CellStyle cs = workbook.createCellStyle()
    if (config.containsKey('fontSize') || config.containsKey('fontWeight')) {
      Font font = workbook.createFont()
      if (config.containsKey('fontSize')) font.setFontHeightInPoints(config.fontSize as short)
      if (config.containsKey('fontWeight')) font.setBoldweight(config.fontWeight)
      cs.setFont(font)
    }
    if (config.containsKey('wrapText')) cs.setWrapText(config.wrapText)
    if (config.containsKey('textAlign')) cs.setAlignment(config.textAlign)
    if (config.containsKey('verticalAlign')) cs.setVerticalAlignment(config.verticalAlign)
    if (config.containsKey('bgColor')) {
      cs.setFillForegroundColor(config.bgColor.getIndex())
      cs.setFillPattern(CellStyle.SOLID_FOREGROUND)
    }
    if (config.containsKey('borderTop')) cs.setBorderTop(config.borderTop)
    if (config.containsKey('borderLeft')) cs.setBorderLeft(config.borderLeft)
    if (config.containsKey('borderRight')) cs.setBorderRight(config.borderRight)
    if (config.containsKey('borderBottom')) cs.setBorderBottom(config.borderBottom)
    if (config.containsKey('dataFormat')) cs.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(config.dataFormat))

    return cs
  }


}
