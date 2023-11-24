package yakumo.server
import yakumo.*
import yakumo.transfer.*

import java.io.File
import java.io.OutputStream


def sendServerFileInfo(os: OutputStream, dir: String) = {
  val files = File(dir).list().filter(x => File(x).isFile == true)
  sendInt(files.length, os)
  for file <- files do
    sendInt(file.length, os)
    sendString(file, os)
}

def isFileValid(len: Long, namelen: Int): Boolean = {
  val config = getConfigFile()
  val maxperfile = getFileLimit(config, "perfile")
  val maxtotal = getFileLimit(config, "total")
  val dir = getStorageDirectory(config)
  val fileSizes = File(dir).list().map(x => File(s"$dir$x").length())
  var total: Long = 0
  for size <- fileSizes do {
    total += size
  }
  if len / 1000000000 <= maxperfile && namelen > 0 && total / 1000000000 < maxtotal then
    true
  else
    false
}
