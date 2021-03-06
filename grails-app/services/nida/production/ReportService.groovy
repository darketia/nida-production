package nida.production

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.hibernate.Session
import org.hibernate.jdbc.Work
import org.springframework.context.i18n.LocaleContextHolder as LCH

import ar.com.fdvs.dj.core.DynamicJasperHelper
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.AutoText
import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.builders.StyleBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.Page
import ar.com.fdvs.dj.domain.constants.Transparency
import ar.com.fdvs.dj.domain.constants.VerticalAlign
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import com.lowagie.text.Document
import com.lowagie.text.pdf.PdfImportedPage
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.PdfSmartCopy
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JRExporterParameter
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.design.JasperDesign
import net.sf.jasperreports.engine.export.JRGraphics2DExporter
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter
import net.sf.jasperreports.engine.export.JRHtmlExporter
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.export.JRPdfExporterParameter
import net.sf.jasperreports.engine.export.JRXlsExporter
import net.sf.jasperreports.engine.export.JRXlsExporterParameter
import net.sf.jasperreports.engine.xml.JRXmlLoader
import org.apache.tools.zip.ZipEntry
import org.springframework.jdbc.core.ConnectionCallback
import org.springframework.jdbc.core.JdbcTemplate

import java.awt.Color
import java.awt.image.BufferedImage
import java.sql.Connection
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter

/**
 * ???????????? Service ????????????????????????????????? Report ??????????????????????????????????????????
 */
class ReportService extends AbstractEnvAwareService {

  boolean transactional = false
  def dataSource
  ServletContext servletContext

  /**
   * Style ??????????????????????????? Dynamic jasper
   * TITLE_STYLE ??????????????????????????????
   * SUB_TITLE_STYLE ???????????????????????????
   * HEADER_STYLE
   * LEFT = ?????????????????????
   * CENTER = ?????????????????????????????????
   * JUSTIFIED = ??????????????????????????????????????????
   * RIGHT = ??????????????????
   * GROUP_HEADER_STYLE = ?????????????????????????????????
   * SUB_TOTAL_STYLE = ?????????????????????????????????????????????
   * SUB_TOTAL_STYLE_COUNT = ??????????????????????????????????????????
   * GRAND_TOTAL_STYLE = ?????????????????????????????????????????????
   * GRAND_TOTAL_STYLE_COUNT = ??????????????????????????????????????????
   */
  final Style TITLE_STYLE = setTahomaBoldFont(new StyleBuilder(false), 14).setHorizontalAlign(HorizontalAlign.CENTER).build()
  final Style SUB_TITLE_STYLE = setTahomaFont(new StyleBuilder(false), 11).setHorizontalAlign(HorizontalAlign.CENTER).build()
  final Style HEADER_STYLE = setTahomaBoldFont(new StyleBuilder(false), 8).setHorizontalAlign(HorizontalAlign.CENTER).setVerticalAlign(VerticalAlign.MIDDLE).setBackgroundColor(Color.LIGHT_GRAY).setBorder(Border.PEN_1_POINT).setTransparency(Transparency.OPAQUE).build()
  final Style LEFT = setTahomaFont(new StyleBuilder(false), 8).setHorizontalAlign(HorizontalAlign.LEFT).build()
  final Style CENTER = setTahomaFont(new StyleBuilder(false), 8).setHorizontalAlign(HorizontalAlign.CENTER).build()
  final Style JUSTIFIED = setTahomaFont(new StyleBuilder(false), 8).setHorizontalAlign(HorizontalAlign.JUSTIFY).build()
  final Style RIGHT = setTahomaFont(new StyleBuilder(false), 8).setHorizontalAlign(HorizontalAlign.RIGHT).build()
  final Style GROUP_HEADER_STYLE = setTahomaBoldFont(new StyleBuilder(false), 11).setHorizontalAlign(HorizontalAlign.LEFT).setBorderTop(Border.PEN_1_POINT).build()
  final Style SUB_TOTAL_STYLE = setTahomaBoldFont(new StyleBuilder(false), 11).setHorizontalAlign(HorizontalAlign.RIGHT).setBorderTop(Border.PEN_1_POINT).build()
  final Style SUB_TOTAL_STYLE_COUNT = setTahomaBoldFont(new StyleBuilder(false), 11).setHorizontalAlign(HorizontalAlign.CENTER).setBorderTop(Border.PEN_1_POINT).setPattern("#,##0 ??????????????????").build()
  final Style GRAND_TOTAL_STYLE = setTahomaBoldFont(new StyleBuilder(false), 11).setHorizontalAlign(HorizontalAlign.RIGHT).setBorderTop(Border.PEN_1_POINT).setBorderBottom(Border.PEN_2_POINT).setBackgroundColor(Color.LIGHT_GRAY).setTransparency(Transparency.OPAQUE).build()
  final Style GRAND_TOTAL_STYLE_COUNT = setTahomaBoldFont(new StyleBuilder(false), 11).setHorizontalAlign(HorizontalAlign.CENTER).setBorderTop(Border.PEN_1_POINT).setBorderBottom(Border.PEN_2_POINT).setBackgroundColor(Color.LIGHT_GRAY).setTransparency(Transparency.OPAQUE).setPattern("#,##0 ??????????????????").build()

  /**
   * ??????????????????????????????????????? stype ?????????????????? Tahoma font ??????????????????
   * @param styleBuilder
   * @param fontSize
   * @return
   */
  StyleBuilder setTahomaFont(StyleBuilder styleBuilder, int fontSize) {
    styleBuilder.setFont(new Font(fontSize, "Tahoma", "tahoma.ttf", Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing, true))
    styleBuilder.setTextColor(Color.BLACK)
    styleBuilder.setVerticalAlign(VerticalAlign.MIDDLE)
  }

  /**
   * ??????????????????????????????????????? stype ?????????????????? Tahoma font ??????????????????
   * @param styleBuilder
   * @param fontSize
   * @return
   */
  StyleBuilder setTahomaBoldFont(StyleBuilder styleBuilder, int fontSize) {
    styleBuilder.setFont(new Font(fontSize, "Tahoma", "tahomabd.ttf", Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing, true))
    styleBuilder.setTextColor(Color.BLACK)
    styleBuilder.setVerticalAlign(VerticalAlign.MIDDLE)
  }

  /**
   * ???????????????????????????????????? excel file ????????? jasper file ??????????????? exportJasperAsXls
   */
  def exportJasperPrintAsXls = { JasperPrint jp, fileName, response ->
    JRXlsExporter exporterXLS = new JRXlsExporter();
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, byteArray);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.TRUE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
    exporterXLS.exportReport();
    if (!response) {
      return byteArray.toByteArray()
    }
    exportXlsToResponse(byteArray.toByteArray(), response, fileName)
  }

  def exportJasperDesignAsXls(String designPath, Map parameters, String fileName, HttpServletResponse response) {
    InputStream resourceInputStream = servletContext.getResourceAsStream(designPath)
    JasperDesign design = JRXmlLoader.load(resourceInputStream)
    JasperReport report = JasperCompileManager.compileReport(design);

    new JdbcTemplate(dataSource).execute({ Connection conn ->
      JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, conn)
      if (response == null && jasperPrint.pages.size() == 0) {
        return null
      } else {
        exportJasperPrintAsXls(jasperPrint, fileName, response)
      }
    } as ConnectionCallback)
  }

  def exportJasperPrintAsXlsx = {JasperPrint jp, fileName, response ->
    JRXlsxExporter exporterXLSX = new JRXlsxExporter();
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporterXLSX.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
    exporterXLSX.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, byteArray);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.TRUE);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
    exporterXLSX.exportReport();
    if (!response) {
      return byteArray.toByteArray()
    }
    exportXlsxToResponse(byteArray.toByteArray(), response, fileName)
  }

  def exportJasperDesignAsXlsx(String designPath, Map parameters, String fileName, HttpServletResponse response) {
    InputStream resourceInputStream = servletContext.getResourceAsStream(designPath)
    JasperDesign design = JRXmlLoader.load(resourceInputStream)
    JasperReport report = JasperCompileManager.compileReport(design);

    new JdbcTemplate(dataSource).execute({ Connection conn ->
      JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, conn)
      if (response == null && jasperPrint.pages.size() == 0) {
        return null
      } else {
        exportJasperPrintAsXlsx(jasperPrint, fileName, response)
      }
    } as ConnectionCallback)
  }

  /**
   * ????????????????????? export report ???????????? XLS
   *
   * @deprecated Do not refer to *.jasper file directly; Always compile at runtime. Use <code>exportJasperDesignAsXls</code>
   * @see #exportJasperDesignAsPdf
   */
  @Deprecated
  def exportJasperAsXls = {jasperFile, parameters, fileName, response ->
    new JdbcTemplate(dataSource).execute({ conn ->
      def input
      if (jasperFile instanceof JasperReport) input = jasperFile
      else input = jasperFile?.openStream()
      JasperPrint jasperPrint = JasperFillManager.fillReport(input, parameters, conn)
      if (response == null && jasperPrint.pages.size() == 0) {
        return null
      } else {
        exportJasperPrintAsXls(jasperPrint, fileName, response)
      }
    } as ConnectionCallback)
  }

  /**
   * ????????????????????? export report ???????????? XLSX
   *
   * @deprecated Do not refer to *.jasper file directly; Always compile at runtime. Use <code>exportJasperDesignAsXlsx</code>
   * @see #exportJasperDesignAsPdf
   */
  @Deprecated
  def exportJasperAsXlsx = {jasperFile, parameters, fileName, response ->
    new JdbcTemplate(dataSource).execute({ conn ->
      def input
      if (jasperFile instanceof JasperReport) input = jasperFile
      else input = jasperFile?.openStream()
      JasperPrint jasperPrint = JasperFillManager.fillReport(input, parameters, conn)
      if (response == null && jasperPrint.pages.size() == 0) {
        return null
      } else {
        exportJasperPrintAsXlsx(jasperPrint, fileName, response)
      }
    } as ConnectionCallback)
  }

  /**
   * ???????????????????????????????????? excel file ????????? jasper file ?????????????????????????????????????????? cell type ?????????????????????????????????????????? excel ????????????????????????????????????
   */
  def exportJasperPrintAsXlsNoCellDetected = {JasperPrint jp, fileName, response ->
    JRXlsExporter exporterXLS = new JRXlsExporter();
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, byteArray);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
    exporterXLS.exportReport();
    exportXlsToResponse(byteArray.toByteArray(), response, fileName)
  }

  /**
   * ???????????????????????????????????? excel file ???????????????????????????????????? 1 sheets ?????? file ???????????????
   */
  def exportJasperPrintAsXls2MoreSheets = {JasperPrint jp, fileName, response, String[] sheetNames = [] ->
    JRXlsExporter exporterXLS = new JRXlsExporter();
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
    exporterXLS.setParameter(JRXlsExporterParameter.SHEET_NAMES, sheetNames);
    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, byteArray);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.TRUE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
    exporterXLS.exportReport();
    exportXlsToResponse(byteArray.toByteArray(), response, fileName)
  }

  /**
   * ???????????????????????????????????? excel file ???????????????????????????????????? 1 sheets ?????? file ???????????????
   */
  def exportJasperPrintAsXlsx2MoreSheets = {JasperPrint jp, fileName, response, String[] sheetNames = [] ->
    JRXlsxExporter exporterXLSX = new JRXlsxExporter();
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporterXLSX.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
    exporterXLSX.setParameter(JRXlsExporterParameter.SHEET_NAMES, sheetNames);
    exporterXLSX.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, byteArray);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.TRUE);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
    exporterXLSX.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
    exporterXLSX.exportReport();
    exportXlsxToResponse(byteArray.toByteArray(), response, fileName)
  }

  /** ???????????????????????????????????? excel file ???????????????????????????????????? 1 sheets ?????? file ??????????????? ?????????????????? DetectCellType*/
  def exportJasperPrintAsXls2MoreSheetsNoDetectCell = {JasperPrint jp, fileName, response, String[] sheetNames = [] ->
    JRXlsExporter exporterXLS = new JRXlsExporter();
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
    exporterXLS.setParameter(JRXlsExporterParameter.SHEET_NAMES, sheetNames);
    exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, byteArray);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
    exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
    exporterXLS.exportReport();
    exportXlsToResponse(byteArray.toByteArray(), response, fileName)
  }

  /**
   * ????????????????????? export report ???????????? PDF
   *
   * @deprecated Do not refer to *.jasper file directly; Always compile at runtime. Use <code>exportJasperDesignAsPdf</code>
   * @see #exportJasperDesignAsPdf
   */
  @Deprecated
  def exportJasperAsPdf = {jasperFile, parameters, fileName, response ->
    new JdbcTemplate(dataSource).execute({ conn ->
      def input
      if (jasperFile instanceof JasperReport) input = jasperFile
      else input = jasperFile?.openStream()
      JasperPrint jasperPrint = JasperFillManager.fillReport(input, parameters, conn)
      if (response == null && jasperPrint.pages.size() == 0) {
        return null
      } else {
        exportJasperPrintAsPdf(jasperPrint, fileName, response)
      }
    } as ConnectionCallback)
  }

  def exportJasperDesignAsPdf(String designPath, Map parameters, String pdfFileName, HttpServletResponse response, boolean inline=false) {
    InputStream resourceInputStream = servletContext.getResourceAsStream(designPath)
    JasperDesign design = JRXmlLoader.load(resourceInputStream)
    JasperReport report = JasperCompileManager.compileReport(design);

    new JdbcTemplate(dataSource).execute({ Connection conn ->
      JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, conn)
      if (response == null && jasperPrint.pages.size() == 0) {
        return null
      } else {
        exportJasperPrintAsPdf(jasperPrint, pdfFileName, response,inline)
      }
    } as ConnectionCallback)
  }

  def exportJasperDesignAsPdfByInputStream(InputStream ips, Map parameters, String pdfFileName, HttpServletResponse response) {
    JasperDesign design = JRXmlLoader.load(ips)
    JasperReport report = JasperCompileManager.compileReport(design);

    new JdbcTemplate(dataSource).execute({ Connection conn ->
      JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, conn)
      if (response == null && jasperPrint.pages.size() == 0) {
        return null
      } else {
        exportJasperPrintAsPdf(jasperPrint, pdfFileName, response)
      }
    } as ConnectionCallback)
  }

  /**
   * ???????????????????????????????????? pdf file ????????? jasper file ??????????????? exportJasperAsPdf
   */
  def exportJasperPrintAsPdf(JasperPrint jp, String pdfFileName, HttpServletResponse response, boolean inline=false) {
    JRPdfExporter exporter = new JRPdfExporter()
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp)
    exporter.setParameter(JRPdfExporterParameter.FORCE_LINEBREAK_POLICY, true)
    exporter.exportReport()
    if (!response) {
      return byteArray.toByteArray()
    }
    exportPdfToResponse(byteArray.toByteArray(), response, pdfFileName,inline)
  }

  def exportPdfToResponse(byte[] byteArray, HttpServletResponse response, String fileName, boolean inline=false) {
    def i = fileName.lastIndexOf('.');
    def extension = ''
    fileName = fileName ?: 'Download'
    if (i > 0) {
      extension = fileName.substring(i+1);
    }
    if (extension.toUpperCase() != 'PDF')  fileName += ".pdf"
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-disposition", "${inline?"inline":"attachment"}; filename*=UTF-8''" + fileName);
    response.contentType = "application/pdf; charset=UTF-8"
    response.outputStream << byteArray
  }

  def exportXlsToResponse(byte[] byteArray, HttpServletResponse response, String fileName) {
    def i = fileName.lastIndexOf('.');
    def extension = ''
    fileName = fileName ?: 'Download'
    if (i > 0) {
      extension = fileName.substring(i+1);
    }
    if (extension.toUpperCase() != 'XLS')  fileName += ".xls"
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-disposition", "attachment; filename*=UTF-8''" + fileName);
    response.contentType = "application/vnd.ms-excel; charset=UTF-8"
    response.outputStream << byteArray
  }

  def exportXlsxToResponse(byte[] byteArray, HttpServletResponse response, String fileName) {
    def i = fileName.lastIndexOf('.');
    def extension = ''
    fileName = fileName ?: 'Download'
    if (i > 0) {
      extension = fileName.substring(i+1);
    }
    if (extension.toUpperCase() != 'XLSX')  fileName += ".xlsx"
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-disposition", "attachment; filename*=UTF-8''" + fileName);
    response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8"
    response.outputStream << byteArray
  }

  /**
   * ????????????????????? export report ???????????? HTML
   *
   * @deprecated Do not refer to *.jasper file directly; Always compile at runtime.
   * @see #exportJasperDesignAsPdf
   */
  @Deprecated
  def exportJasperAsHTML = {jasperFile, parameters, fileName, response ->
    new JdbcTemplate(dataSource).execute({ conn ->
      InputStream input = jasperFile.openStream()
      JasperPrint jasperPrint = JasperFillManager.fillReport(input, parameters, conn)
      exportJasperPrintAsHTML(jasperPrint, fileName, response)
    } as ConnectionCallback)
  }

  /**
   * ???????????????????????????????????? HTML file ????????? jasper file ??????????????? exportJasperAsHTML
   */
  def exportJasperPrintAsHTML = {JasperPrint jp, fileName, response ->
    JRHtmlExporter exporter = new JRHtmlExporter()
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp)
    exporter.setParameter(JRHtmlExporterParameter.ZOOM_RATIO, (1.75).asType(Float));
    exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
    exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "test");
    exporter.exportReport()
    if (!response) {
      return byteArray.toByteArray()
    }
    response.outputStream << byteArray.toByteArray()
  }

  /** ????????????????????? export report ???????????? Image */
  def exportJasperAsImage = {jasperFile, parameters, fileName, response ->
    new JdbcTemplate(dataSource).execute({ conn ->
      InputStream input = jasperFile.openStream()
      JasperPrint jasperPrint = JasperFillManager.fillReport(input, parameters, conn)
      exportJasperPrintAsImage(jasperPrint, fileName, response)
    } as ConnectionCallback)
  }

  /** ???????????????????????????????????? Image file ????????? jasper file ??????????????? exportJasperAsImage */
  def exportJasperPrintAsImage = {JasperPrint jp, fileName, response ->
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()
    BufferedImage pageImage = new BufferedImage(jp.getPageWidth() + 1, jp.getPageHeight() + 1, BufferedImage.TYPE_INT_RGB);
    JRGraphics2DExporter exporter = new JRGraphics2DExporter();

//    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp)
    exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D, pageImage.getGraphics());
    exporter.setParameter(JRExporterParameter.PAGE_INDEX, new Integer(pageIndex));
//    exporter.setParameter(JRHtmlExporterParameter.ZOOM_RATIO, (1.75).asType(Float));
//    exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
//    exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "test");
    exporter.exportReport();
    ImageIO.write(pageImage, "jpeg", byteArray);
    if (!response) {
      return byteArray.toByteArray()
    }
    response.outputStream << byteArray.toByteArray()
  }



  /**
   * ????????????????????? export report ???????????? PDF ????????? Zip ????????????
   *
   * @deprecated Do not refer to *.jasper file directly; Always compile at runtime.
   */
  @Deprecated
  def exportJasperAsPdfZip = {jasperFile, parameterList, fileNameList, zipName, response ->
    Map<String, InputStream> inputStreamMap = [:]
    new JdbcTemplate(dataSource).execute({ conn ->
      parameterList.eachWithIndex {parameters, index ->
        InputStream input = jasperFile.openStream()
        JasperPrint jasperPrint = JasperFillManager.fillReport(input, parameters, conn)
        JRPdfExporter exporter = new JRPdfExporter()
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream()
        def jp = jasperPrint

        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp)
        exporter.setParameter(JRPdfExporterParameter.FORCE_LINEBREAK_POLICY, true)
        exporter.exportReport()

        inputStreamMap["${fileNameList[index]}.pdf"] = new ByteArrayInputStream(byteArray.toByteArray())
      }
    } as ConnectionCallback)
    CompressionUtils.zipFromInputStreams(zipName, inputStreamMap, response)
    /*response.contentType = "application/zip"
    response.setHeader("Content-disposition", "attachment; filename=${zipName}.zip");
    ZipOutputStream zos = new ZipOutputStream(response.outputStream)
    new JdbcTemplate(dataSource).execute({ conn ->
      parameterList.eachWithIndex{parameters,index ->
        InputStream input = jasperFile.openStream()
        JasperPrint jasperPrint = JasperFillManager.fillReport(input, parameters, conn)
        JRPdfExporter exporter = new JRPdfExporter()
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream()
        def jp = jasperPrint

        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp)
        exporter.setParameter(JRPdfExporterParameter.FORCE_LINEBREAK_POLICY, true)
        exporter.exportReport()

        ZipEntry ze = new ZipEntry("${fileNameList[index]}.pdf")
        zos.putNextEntry(ze)
        zos.write(byteArray.toByteArray())
      }
    } as ConnectionCallback)
    zos.close()
    zos.flush()*/
  }

  /**
   * ????????????????????? export report ???????????? PDF ????????? Zip ???????????? JasperPrint
   */
  def exportJasperPrintAsPdfZip = {JasperPrint jp, zipName, zos ->
    JRPdfExporter exporter = new JRPdfExporter()
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp)
    exporter.setParameter(JRPdfExporterParameter.FORCE_LINEBREAK_POLICY, true)
    exporter.exportReport()

    ZipEntry ze = new ZipEntry("${zipName}.pdf")
    zos.putNextEntry(ze)
    zos.write(byteArray)
    zos.flush()
  }

  /**
   * ?????????????????????????????? file PDF ??????????????? file PDF ???????????? file
   */
  def mergePdfs = { pdfs, fileName, response, inline=false ->
    log.debug("Starting To Merge Files...");
    log.debug("Total Number Of Files To Be Merged..." + pdfs.size() + "\n");

    OutputStream os
    if (response) {
      def i = fileName.lastIndexOf('.');
      def extension = ''
      fileName = fileName ?: 'Download'
      if (i > 0) {
        extension = fileName.substring(i+1);
      }
      if (extension.toUpperCase() != 'PDF')  fileName += ".pdf"

      response.setCharacterEncoding("UTF-8");
      response.setHeader("Content-disposition", "${inline?"inline":"attachment"}; filename*=UTF-8''" + fileName);
      response.contentType = "application/pdf; charset=UTF-8"
      os = response.outputStream
    } else {
      os = new ByteArrayOutputStream()
    }

    Document document = null;
    PdfSmartCopy writer = null;
    PdfReader reader = null;

    pdfs.eachWithIndex { pdf, fileIndex ->

      if (pdf) {
        /**
         * Create a reader for the file that we are reading
         */
        reader = new PdfReader((byte[]) pdf);
        log.debug("Reading File");

        /**
         * Replace all the local named links with the actual destinations.
         */
        reader.consolidateNamedDestinations();

        /**
         * Retrieve the total number of pages for this document
         */
        int totalPages = reader.getNumberOfPages();

        /**
         * Merging the files to the first file.
         * If we are passing file1, file2 and file3,
         * we will merge file2 and file3 to file1.
         */
        if (fileIndex == 0) {
          /**
           * Create the document object from the reader
           */
          document = new Document(reader.getPageSizeWithRotation(1));

          /**
           * Create a pdf write that listens to this document.
           * Any changes to this document will be written the file
           *
           * outFile is a location where the final merged document
           * will be written to.
           */

          log.debug("Creating an empty PDF...");
          writer = new PdfSmartCopy(document, os);
          writer.setFullCompression()
          /**
           * Open this document
           */
          document.open();
        }
        /**
         * Add the conent of the file into this document (writer).
         * Loop through multiple Pages
         */
        log.debug("Merging File");
        PdfImportedPage page;
        for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
          page = writer.getImportedPage(reader, currentPage);
          writer.addPage(page);
        }
      }
    }

    /**
     * Finally Close the main document, which will trigger the pdfcopy
     * to write back to the filesystem.
     */
    if (document) document.close();

    log.debug("File has been merged");

    if (!response) {
      return os.toByteArray()
    }
  }

  /**
   * ??????????????????????????????????????? builder ????????????????????????????????? report ????????? excel
   * @return
   */
  FastReportBuilder getDynamicJasperBuilderForExcel() {
    getDynamicJasperBuilder(true)
  }

  /**
   * ?????????????????????????????????????????? builder ????????????????????????????????? report
   * @param excel true = ????????????????????? excel
   * @return
   */
  FastReportBuilder getDynamicJasperBuilder(excel = false) {
    def builder = new FastReportBuilder()
    builder.setUseFullPageWidth(true)

    if (!excel) {
      builder.setPrintBackgroundOnOddRows(true)
      builder.setDefaultStyles(TITLE_STYLE, SUB_TITLE_STYLE, HEADER_STYLE, null)
      builder.setPageSizeAndOrientation(Page.Page_A4_Portrait())
      builder.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT)
      builder.addAutoText(currentCompany.companyName, AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 200, LEFT)
      builder.addAutoText('?????????????????????????????? ' + new SimpleDateFormat('d MMM yyyy HH:mm ???.', LCH.locale).format(new Date()), AutoText.POSITION_FOOTER, AutoText.ALIGMENT_LEFT, 200, LEFT)
      builder.setDetailHeight(20)
      builder.setMargins(15, 15, 15, 15)
    } else {
      builder.setUseFullPageWidth(false)
    }
    return builder
  }

  /**
   * ??????????????? column ????????? report ????????? dynamic jasper
   * @param builder
   * @param title
   * @param property
   * @param className
   * @param width
   * @param pattern
   * @param style
   * @return
   */
  AbstractColumn addColumn(FastReportBuilder builder, String title, String property, String className, int width, String pattern, Style style) {
    def colBuilder = ColumnBuilder.getNew()
    colBuilder.setTitle(title)
    colBuilder.setColumnProperty(property, className)
    colBuilder.setWidth(width)
    if (pattern) colBuilder.setPattern(pattern)
    if (style) colBuilder.setStyle(style)
    def col = colBuilder.build()
    builder.addColumn(col)
    return col
  }

  /**
   * ??????????????? column ?????????????????? String ????????? report ????????? dynamic jasper
   * @param builder
   * @param title
   * @param property
   * @param width
   * @param pattern
   * @param style
   * @return
   */
  AbstractColumn addStringColumn(FastReportBuilder builder, String title, String property, int width = 40, String pattern = null, Style style = CENTER) {
    addColumn(builder, title, property, String.class.getName(), width, pattern, style)
  }

  /**
   * ??????????????? column ?????????????????? Integer ????????? report ????????? dynamic jasper
   * @param builder
   * @param title
   * @param property
   * @param width
   * @param pattern
   * @param style
   * @return
   */
  AbstractColumn addIntegerColumn(FastReportBuilder builder, String title, String property, int width = 20, String pattern = "#,##0", Style style = CENTER) {
    addColumn(builder, title, property, Integer.class.getName(), width, pattern, style)
  }

  /**
   * ??????????????? column ?????????????????? BigDecimal ????????? report ????????? dynamic jasper
   * @param builder
   * @param title
   * @param property
   * @param width
   * @param pattern
   * @param style
   * @return
   */
  AbstractColumn addBigDecimalColumn(FastReportBuilder builder, String title, String property, int width = 30, String pattern = "#,##0.00", Style style = RIGHT) {
    addColumn(builder, title, property, BigDecimal.class.getName(), width, pattern, style)
  }

  /**
   *  ???????????????????????????????????? dynamic jasper ???????????? PDF
   */
  def exportDynamicJasperAsPdf = { FastReportBuilder builder, Collection data, fileName, response ->
    JRDataSource ds = new JRBeanCollectionDataSource(data)
    JasperPrint jp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds);
    exportJasperPrintAsPdf(jp, fileName, response)
  }

  /**
   *  ???????????????????????????????????? dynamic jasper ???????????? XSL
   */
  def exportDynamicJasperAsXls = { FastReportBuilder builder, Collection data, fileName, response ->
    JRDataSource ds = new JRBeanCollectionDataSource(data)
    builder.setPrintColumnNames(true)
    builder.setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
    builder.setMargins(0, 0, 0, 0)
    JasperPrint jp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds);
    exportJasperPrintAsXls(jp, fileName, response)
  }

  /**
   * ????????????????????? export excel ????????? dynamic jasper
   * builder.setIgnorePagination(true) >> for Excel, we may dont want pagination, just a plain list
   */
  def exportDynamicJasperAsXlsNoCellDetected = { FastReportBuilder builder, Collection data, fileName, response ->
    JRDataSource ds = new JRBeanCollectionDataSource(data)
    //    builder.setPrintColumnNames(true)
    builder.setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
    builder.setMargins(0, 0, 0, 0)
    JasperPrint jp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds);
    exportJasperPrintAsXlsNoCellDetected(jp, fileName, response)
  }

  /**
   * ???????????????????????????????????? sheet ????????? excel ????????? dynamic jasper
   */
  def exportDynamicJasperAsXls2MoreSheets = { List<FastReportBuilder> builderList, String[] reportNames, Collection data, fileName, response ->
    JasperPrint jp
    builderList.eachWithIndex {builder, index ->
      def pageData = data.take(65532)
      data = data.drop(65532)
      JRDataSource ds = new JRBeanCollectionDataSource(pageData)
      builder.setPrintColumnNames(true)
      builder.setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
      builder.setMargins(0, 0, 0, 0)
      if (index == 0) jp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds);
      else {
        def tempJp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds).getPages()
        jp.addPage(tempJp)
      }
    }
    exportJasperPrintAsXls2MoreSheets(jp, fileName, response, reportNames)
  }

  /**
   * ???????????????????????????????????? sheet ????????? excel ????????? dynamic jasper
   */
  def exportDynamicJasperAsXlsx2MoreSheets = { List<FastReportBuilder> builderList, String[] reportNames, Collection data, fileName, response ->
    JasperPrint jp
    builderList.eachWithIndex {builder, index ->
      def pageData = data.take(1048572)
      data = data.drop(1048572)
      JRDataSource ds = new JRBeanCollectionDataSource(pageData)
      builder.setPrintColumnNames(true)
      builder.setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
      builder.setMargins(0, 0, 0, 0)
      if (index == 0) jp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds);
      else {
        def tempJp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds).getPages()
        jp.addPage(tempJp)
      }
    }
    exportJasperPrintAsXlsx2MoreSheets(jp, fileName, response, reportNames)
  }


  /** ???????????????????????????????????? sheet ????????? excel ????????? dynamic jasper ?????????????????? Detect Cell Type */
  def exportDynamicJasperAsXls2MoreSheetsNoDetectCell = { List<FastReportBuilder> builderList, String[] reportNames, Collection data, fileName, response ->
    JasperPrint jp
    builderList.eachWithIndex {builder, index ->
      def pageData = data.take(65532)
      data = data.drop(65532)
      JRDataSource ds = new JRBeanCollectionDataSource(pageData)
      builder.setPrintColumnNames(true)
      builder.setIgnorePagination(true) //for Excel, we may dont want pagination, just a plain list
      builder.setMargins(0, 0, 0, 0)
      if (index == 0) jp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds);
      else {
        def tempJp = DynamicJasperHelper.generateJasperPrint(builder.build(), new ClassicLayoutManager(), ds).getPages()
        jp.addPage(tempJp)
      }
    }
    exportJasperPrintAsXls2MoreSheetsNoDetectCell(jp, fileName, response, reportNames)
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
}
