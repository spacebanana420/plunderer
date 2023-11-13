package yakumo.client
import yakumo.*

import java.io.InputStream

def receiveServerFileInfo(is: InputStream, howMany: Int, i: Int = 1, filenames: List[String] = List[String]()): List[String] = {
  if i <= howMany then
    val len = bytesToInt(readBytes(4, is)) //seems to be unnecessary
    val name = bytesToString(readBytes(len, is))
    receiveServerFileInfo(is, howMany, i+1, filenames :+ name)
  else
    filenames
}

def chooseServerFile(files: List[String], howMany: Int, is: InputStream): List[Int] = { //what if you choose each at a time instead
  def separateStringLines(str: String, line: String = "", lines: List[String] = List(), i: Int = 0): List[String] = {
    if i == str.length then
      lines :+ line
    else if str(i) == ' ' then
      separateStringLines(str, "", lines :+ line, i+1)
    else
      separateStringLines(str, line + str(i), lines, i+1)
  }

  def linesToNumbers(strlines: List[String], maxval: Int, filenums: List[Int] = List(), i: Int = 0): List[Int] = {
    if i == strlines.length then
      filenums
    else
      try
        val linenum = strlines(i).toInt
        if linenum <= maxval then
          linesToNumbers(strlines, maxval, filenums :+ linenum, i+1)
        else
          linesToNumbers(strlines, maxval, filenums, i+1)
      catch
        case e: Exception => linesToNumbers(strlines, maxval, filenums, i+1)
  }

  val green = foreground("green")
  val default = foreground("default")
  var i = 0
  var screen = "--Server File Storage--\n\n"
  var filesInLine = 0
  for file <- files do {
    if filesInLine < 3 then
      screen ++= s"$green$i:$default $file   "
      filesInLine += 1
    else
      screen ++= s"$green$i:$default $file\n"
      filesInLine = 0
    i += 1
  }
  screen ++= "Choose the server file(s) to download\nTo download multiple files, type each number and separate then by a space"
  val choice = readUserInput(screen)
  val filenums = linesToNumbers(separateStringLines(choice), files.length-1)
  filenums
}
