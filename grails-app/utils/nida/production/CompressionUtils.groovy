package nida.production

import lzma.streams.LzmaOutputStream
import org.apache.tools.tar.TarEntry
import org.apache.tools.tar.TarOutputStream
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

class CompressionUtils {
  static void zipFromFiles(String zipName, Map<String, File> fileMap, HttpServletResponseWrapper response, Boolean autoFileDeletion = false) {
    Map<String, InputStream> isMap = (Map<String, InputStream>) fileMap.collectEntries { String entryName, File file -> [entryName, new FileInputStream(file)] }
    zipFromInputStreams(zipName, isMap, response)
    if (autoFileDeletion) {
      fileMap.values().each { f ->
        FileUtils.deleteWhenPossible(f)
      }
    }
  }

  static void zipFromInputStreams(String zipName, Map<String, InputStream> inputStreamMap, HttpServletResponse response) {
    response.characterEncoding = 'tis-620'
    response.contentType = "application/zip"
    response.setHeader("Content-disposition", "attachment; filename=${ConvertFileNameUtils.toThai(zipName)}.zip");
    def zos = new ZipOutputStream(response.outputStream)
    inputStreamMap.eachWithIndex {String fileName, InputStream is, index ->
      ZipEntry ze = new ZipEntry(fileName)
      zos.putNextEntry(ze)
      zos << is
    }
    zos.flush()
    zos.close()
  }

  static void writeLzmaResponseFromInputStream(String archiveName, InputStream contentInputStream, HttpServletResponse response) {
    response.characterEncoding = 'tis-620'
    response.contentType = "application/x-lzma"
    response.setHeader("Content-disposition", "attachment; filename=${ConvertFileNameUtils.toThai(archiveName)}.lzma");

    def lzmaOs = new LzmaOutputStream.Builder(new BufferedOutputStream(response.outputStream)).
        useMaximalDictionarySize().
        useEndMarkerMode(true).
        useBT4MatchFinder().
        build()

    lzmaOs << contentInputStream
    lzmaOs.flush()
    lzmaOs.close()
  }

  static void writeLzmaResponseFromInputStreams(String archiveName, List<TlzEntry> entries, HttpServletResponse response) {
    response.characterEncoding = 'tis-620'
    response.contentType = "application/x-lzma"
    response.setHeader("Content-disposition", "attachment; filename=${ConvertFileNameUtils.toThai(archiveName)}.tar.lzma");

    TarOutputStream tarFilterOs = new TarOutputStream(
        new LzmaOutputStream.Builder(new BufferedOutputStream(response.outputStream)).
            useMaximalDictionarySize().
            useEndMarkerMode(true).
            useBT4MatchFinder().
            build()
    )

    entries.each { entry ->
      TarEntry ze = new TarEntry(entry.entryName)
      ze.size = entry.entrySize
      tarFilterOs.putNextEntry(ze)
      tarFilterOs << entry.contentInputStream
      tarFilterOs.closeEntry()
    }

    tarFilterOs.flush()
    tarFilterOs.close()
  }

  public static class TlzEntry {
    String entryName
    InputStream contentInputStream
    Long entrySize

    public TlzEntry(String entryName, InputStream contentInputStream, Long entrySize) {
      this.entryName = entryName
      this.contentInputStream = contentInputStream
      this.entrySize = entrySize
    }
  }
}
