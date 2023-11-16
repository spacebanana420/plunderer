package yakumo.client
import yakumo.*
import yakumo.transfer.*

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

  sendString(readUserInput("Input connection password:"), os)

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
  val howMany = readInt(is)
  if howMany != 0 then
    val filenames = receiveServerFileInfo(is, howMany)
    val filenums = chooseServerFile(filenames, howMany, is)

    if filenums.length == 0 then
      printStatus("You did not choose any file!", false)
    else
      var willdownload = ""
      for file <- filenums do {
        willdownload += s"  * ${filenames(file)}\n"
      }
      println(s"The following files will be downloaded:\n$willdownload")
    for filenum <- filenums do {
      os.write(Array[Byte](1))
      sendInt(filenum, os)
      val len = readLong(is)

      println(s"--Downloading File--\n  * Name: ${filenames(filenum)}\n  * Length: $len bytes")
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

  sendInt(nameLen, os)
  sendString(name, os)
  sendLong(fileLen, os)

  if readStatusByte(is) == 1 then
    println(s"--Uploading File--\n  * Name: $name\n  * Length: $fileLen bytes")
    upload(os, filepath)
    readUserInput(s"Finished uploading $name!\nPress enter to continue")
  else
    printStatus("Connection refused\nFile exceeds the server's configured limit or filename is empty", true)
}

