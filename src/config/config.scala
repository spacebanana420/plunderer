package yakumo.config
import yakumo.*

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


def createConfig() =
  val default = stringToBytes("//Server settings//\nusepass=yes\npassword=test123\ndirectory=.\nmaxperfile=20\nmaxtotal=30\n\n//Client Settings//\npathsperline=2")
  val file = new FileOutputStream("config.txt")
  file.write(default)
  file.close()

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
  def iscomment(line: String): Boolean =
    if line.length > 0 && line(0) != '/' then
      false
    else
      true
  def convert(cfgBytes: Array[Byte], line: String = "", cfgstr: List[String] = List[String](), i: Int = 0): List[String] = {
    if i >= cfgBytes.length then
      cfgstr :+ line
    else
      val chr = cfgBytes(i).toChar
      if chr == '\n' then
        if iscomment(line) == false then
          convert(cfgBytes, "", cfgstr :+ line, i+1)
        else
          convert(cfgBytes, "", cfgstr, i+1)
      else
        convert(cfgBytes, line + chr, cfgstr, i+1)
  }
  val file = new FileInputStream("config.txt")
  val bytes = new Array[Byte](file.available())
  file.read(bytes)
  file.close()

  val config = convert(bytes)
  config
}
