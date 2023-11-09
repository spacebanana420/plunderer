package yakumo.client
import yakumo.*

import java.net.ServerSocket
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream
import java.io.File

//This file includes the code exclusive to the client


def client(host: String = "localhost", port: Int = 42069) = {
  println(s"Connecting to host \"$host\" at port $port")
  val sock = Socket(host, port)
  val os = sock.getOutputStream()
  val is = sock.getInputStream()
  println("Connection established to server")

  val password = stringToBytes(readUserInput("Input connection password:"))
  os.write(password)
  //val status = new Array[Byte](1)
  //is.read(status)

  if readStatusByte(is) == 1 then
    val green = foreground("green")
    val default = foreground("default")
    println("Password is correct, connection accepted\n")
    var closeClient = false
    while closeClient == false do {
      val mode = readUserInput(s"${green}0:${default} Exit   ${green}1:${default} Download   ${green}2:${default} Upload\nChoose a mode")
      if mode == "1" then
        os.write(Array[Byte](1))
        clientDownload(is, os)
      else if mode == "2" then
        os.write(Array[Byte](2))
        clientUpload(is, os)
      else
        closeClient = true
        os.write(Array[Byte](0))
        println("Closing connection with server")
    }
  else
    printStatus("Incorrect password! Connection was refused", true)
  //sock.close()
}

def clientDownload(is: InputStream, os: OutputStream) = {
  val howMany = bytesToInt(readBytes(4, is))
  if howMany != 0 then
    val filenames = receiveServerFileInfo(is, howMany)
    val choice = chooseServerFile(filenames)
    val filenums = linesToNumbers(separateStringLines(choice), filenames.length-1) //needs testing

    if filenums.length == 0 then
      printStatus("You did not choose any file!", false)
    else
      var willdownload = ""
      for file <- filenames do {
        willdownload += s"$file\n"
      }
      println(s"The following files will be downloaded:\n$willdownload")
    for filenum <- filenums do {
      os.write(Array[Byte](1))
      os.write(intToBytes(filenum))
      val len = bytesToLong(readBytes(8, is))

      println(s"--Downloading File--\nName: ${filenames(filenum)}\nLength: $len bytes")
      download(is, filenames(filenum), len, "./")
      println(s"Finished downloading ${filenames(filenum)}\n")
    }
    os.write(Array[Byte](0))
    readUserInput("Press enter to continue")
  else
    printStatus("The server's storage is empty!\nClosing connection", true)
}

def clientUpload(is: InputStream, os: OutputStream) = {
  val filepath = browse()
  val name = getRelativePath(filepath)
  val nameLen = name.length
  val fileLen = File(filepath).length()

  os.write(intToBytes(nameLen))
  os.write(stringToBytes(name))
  os.write(longToBytes(fileLen))

  if readStatusByte(is) == 1 then
    println(s"--Uploading File--\nName: $name\nLength: $fileLen bytes")
    upload(os, filepath)
    readUserInput(s"Finished uploading $name!\nPress enter to continue")
  else
    printStatus("Connection refused\nFile exceeds the server's configured limit or filename is empty", true)
}

def receiveServerFileInfo(is: InputStream, howMany: Int, i: Int = 1, filenames: List[String] = List[String]()): List[String] = {
  if i <= howMany then
    val len = bytesToInt(readBytes(4, is))
    val name = bytesToString(readBytes(len, is))
    receiveServerFileInfo(is, howMany, i+1, filenames :+ name)
  else
    filenames
}

def chooseServerFile(files: List[String]): String = { //what if you choose each at a time instead
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
  readUserInput(screen)
}
// needs testing
private def linesToNumbers(strlines: List[String], maxval: Int, filenums: List[Int] = List(), i: Int = 0): List[Int] = {
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

private def separateStringLines(str: String, line: String = "", lines: List[String] = List(), i: Int = 0): List[String] = {
  if i == str.length then
    lines :+ line
  else if str(i) == ' ' then
    separateStringLines(str, "", lines :+ line, i+1)
  else
    separateStringLines(str, line + str(i), lines, i+1)
}
