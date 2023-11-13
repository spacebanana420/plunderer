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
    val filenums = chooseServerFile(filenames, howMany, is)
//     val choice = chooseServerFile(filenames) //put these all together
//     val filenums = linesToNumbers(separateStringLines(choice), filenames.length-1) //needs testing

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
      val len = bytesToLong(readBytes(8, is)) //wrap this shit with a function

      println(s"--Downloading File--\nName: ${filenames(filenum)}\nLength: $len bytes")
      download(is, filenames(filenum), len, "./")
      println(s"Finished downloading ${filenames(filenum)}\n")
    }
    os.write(Array[Byte](0)) //wrap these god damn shits
    readUserInput("Press enter to continue")
  else
    printStatus("The server's storage is empty!\nThere's nothing to download", true)
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

