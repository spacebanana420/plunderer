package yakumo.client
import yakumo.browser.*
import yakumo.*
import yakumo.transfer.*

import java.io.InputStream

def receiveServerFileInfo(is: InputStream, howMany: Int = 0, i: Int = -1, filenames: List[String] = List[String]()): List[String] =
  if i == -1 then
    val howMany = readInt(is)
    receiveServerFileInfo(is, howMany, 0, filenames)
  else if i >= howMany then
    filenames
  else
    val len = readInt(is)
    val name = readString(len, is)
    receiveServerFileInfo(is, howMany, i+1, filenames :+ name)

def chooseServerFile(files: List[String], is: InputStream): List[Int] = { //what if you choose each at a time instead
  def separateStringLines(str: String, line: String = "", lines: List[String] = List(), i: Int = 0): List[String] =
    if i == str.length then
      lines :+ line
    else if str(i) == ' ' then
      separateStringLines(str, "", lines :+ line, i+1)
    else
      separateStringLines(str, line + str(i), lines, i+1)

  def linesToNumbers(strlines: List[String], maxval: Int, filenums: List[Int] = List(), i: Int = 0): List[Int] =
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

  def getAllFiles(files: List[String], n: List[Int] = List(), i: Int = 0): List[Int] =
    if i >= files.length then
      n
    else
      getAllFiles(files, n :+ i, i+1)

  val green = foreground("green")
  val default = foreground("default")
  var i = 0
  var screen = "--Server File Storage--\n\n"
  var filesInLine = 0
  for file <- files do
    if filesInLine < 3 then
      screen ++= s"$green$i:$default $file   "
      filesInLine += 1
    else
      screen ++= s"$green$i:$default $file\n"
      filesInLine = 0
    i += 1

  screen ++= "\nChoose the server file(s) to download\nTo download multiple files, type each number and separate then by a space\nTo download the whole directory, press enter with an empty input"
  val choice = readUserInput(screen)
  val filenums =
    if choice != "" then
      linesToNumbers(separateStringLines(choice), files.length-1) //optimize this later maybe
    else
      getAllFiles(files) //test!!!!!!!
  filenums
}
