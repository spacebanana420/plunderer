package yakumo.config
import yakumo.*

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


def createConfig() = {
  val defaultConfig = stringToBytes("//Server settings//\nusepassword=yes\npassword=test123\ndirectory=.\nmaxperfile=20\nmaxtotal=30\n\n//Client Settings//\npathsperline=2")
  val file = new FileOutputStream("config.txt")
  file.write(defaultConfig)
  file.close()
}

def isConfigFine(): Boolean = {
  val config = getConfigFile()
  val password = getPassword(config)
  val usepass = passwordEnabled(config)
  val dir = getStorageDirectory(config)
  val direxists = File(dir).isDirectory()
  val perfile = getFileLimit(config, "perfile")
  val total = getFileLimit(config, "total")

  if ((password != "" && usepass == true) || usepass == false) && direxists == true && perfile != -1 && total != -1 then
    true
  else
    false
}


def getConfigFile(): List[String] = {
  val file = new FileInputStream("config.txt")
  val bytes = new Array[Byte](file.available())
  file.read(bytes)
  file.close()

  val config = configToStringList(bytes)
  config
}

private def configToStringList(cfgBytes: Array[Byte], line: String = "", cfgstr: List[String] = List[String](), i: Int = 0): List[String] = {
  val chr = cfgBytes(i).toChar
  if i == cfgBytes.length-1 then
    cfgstr :+ line
  else if chr == '\n' then
    if line(0) != '/' then
      configToStringList(cfgBytes, "", cfgstr :+ line, i+1)
    else
      configToStringList(cfgBytes, "", cfgstr, i+1)
  else
    configToStringList(cfgBytes, line + chr, cfgstr, i+1)
}
