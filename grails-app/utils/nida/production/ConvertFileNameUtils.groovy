package nida.production

class ConvertFileNameUtils {
  static toThai(String fileName) {
    //['%2F','/'] --> ['%2F','_']
    //['%5C','\'] --> ['%5C','_']
    //['%2C',','] --> ['%2C','_']
    def decodeList = [
        ['+','%20'], ['%21','!'], ['%22','"'], ['%23','#'], ['%24','$'], ['%25','%'], ['%26','&'], ['%27','\''], ['%28','('], ['%29',')'], ['%2B','+'], ['%2C','_'], ['%2F','_'],
        ['%3A',':'], ['%3B',';'], ['%3C','<'], ['%3D','='], ['%3E','>'], ['%3F','?'], ['%40','@'], ['%5B','['], ['%5C','_'], ['%5D',']'],['%5E','^'], ['%60','`'],
        ['%7B','{'], ['%7C','|'], ['%7D','}'], ['%7E','~']
    ]
    def encodeFileName = fileName.encodeAsURL()
    def decodeSpecialChars = encodeFileName
    decodeList.each{l->
      decodeSpecialChars = decodeSpecialChars.replace(l[0], l[1])
    }
    return decodeSpecialChars
  }

  static def safeFileName(String s) {
    def xs = ['\\', '/', ':', '*', '?', '"', '>', '<', '|']
    xs.each { x ->
      s = s.replace(x, '_')
    }
    return s
  }

}
