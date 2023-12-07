package yakumo.client
import yakumo.browser.*
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
  val green = foreground("green")
  val default = foreground("default")

  def session(): Unit =
    clear()
    val mode = readUserInput(s"${green}IP:${default} $host   ${green}Port:${default} $port\n\n${green}0:${default} Exit   ${green}1:${default} Download   ${green}2:${default} Upload\nChoose a mode")
    if mode == "1" then
      clientDownload(is, os)
      session()
    else if mode == "2" then
      clientUpload(is, os)
      session()
    else
      sendMessage("close", os)
      println("Closing connection with server")

  if readStatusByte(is) == 1 then
    session()
  else
    sendString(readUserInput("Connection established to server\nServer has password security, input connection password:"), os)
    if readStatusByte(is) == 1 then
      println("Password is correct, connection accepted\n")
      session()
    else
      printStatus("Incorrect password! Connection was refused\nPress enter to continue", true)
      readUserInput()
}

def clientDownload(is: InputStream, os: OutputStream) = { //add multi file support for upload too
  sendMessage("getfiles", os)
  val filenames = receiveServerFileInfo(is)
  if filenames.length != 0 then
    val filenums = chooseServerFile(filenames, is)
    var willdownload = ""
    for file <- filenums do
      willdownload += s"  * ${filenames(file)}\n"
    println(s"The following files will be downloaded:\n$willdownload")
    for filenum <- filenums do {
      sendMessage("download", os)
      sendInt(filenum, os)
      val len = readLong(is)

      println(s"--Downloading File--\n  * Name: ${filenames(filenum)}\n  * Length: $len bytes")
      download(is, filenames(filenum), len, "./")
      println(s"Finished downloading ${filenames(filenum)}\n")
    }
    readUserInput("Press enter to continue")
  else
    printStatus("The server's storage is empty!\nThere's nothing to download", true)
}

def clientUpload(is: InputStream, os: OutputStream) = {
  def sendfile(path: String) =
    val name = getRelativePath(path)
    val nameLen = name.length
    val fileLen = File(path).length()

    sendMessage("upload", os)
    sendInt(nameLen, os)
    sendString(name, os)
    sendLong(fileLen, os)
    if readStatusByte(is) == 1 then
      println(s"--Uploading File--\n  * Name: $name\n  * Length: $fileLen bytes")
      upload(os, path)
      readUserInput(s"Finished uploading $name!\nPress enter to continue")
    else
      printStatus(s"Connection refused\nFile $name exceeds the server's configured limit or filename is empty", true)
  val filepath = browse()
  if filepath != "!cancelled!" then //implement something better maybe
    if File(filepath).isFile() then
      sendfile(filepath)
    else
      for i <- File(filepath).list() do
        val fp = s"$filepath/$i"
        if File(fp).isFile() && File(fp).isHidden() == false then
          sendfile(s"$filepath/$i")

}

