package yakumo

import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream

// def createLog() = {
//
// }

def writeLog(msg: String) =
    FileOutputStream("log.txt", true).write(stringToBytes(s"${msg}\n"))

def showLog() = {
  if File("log.txt").isFile() == true then
    val file = new FileInputStream("log.txt")
    val bytes = new Array[Byte](file.available())
    file.read(bytes)
    file.close()
    val log = fileToString(bytes)
    val green = foreground("green")
    val default = foreground("default")
    readUserInput(s"${green}////LOG START////${default}\n\n$log\n\n${green}////LOG END////${default}\n\nPress enter to continue")
  else
    readUserInput("There's no log file in the server directory!\nThe server automatically creates one and writes messages to it once it's up and running")
}
//this function is also in config.scala, merge them later
// private def fileToStringList(cfgBytes: Array[Byte], line: String = "", cfgstr: List[String] = List[String](), i: Int = 0): List[String] = {
//   val chr = cfgBytes(i).toChar
//   if i == cfgBytes.length-1 then
//     cfgstr :+ line
//   else if chr == '\n' then
//     configToStringList(cfgBytes, "", cfgstr :+ line, i+1)
//   else
//     configToStringList(cfgBytes, line + chr, cfgstr, i+1)
// }

private def fileToString(bytes: Array[Byte], line: String = "", i: Int = 0): String = {
  if i == bytes.length-1 then
    line
  else
    fileToString(bytes, line + bytes(i).toChar, i+1)
}
