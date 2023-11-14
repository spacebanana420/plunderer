package yakumo.server
import yakumo.*
import yakumo.transfer.*

import java.net.ServerSocket
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream
import java.io.File

//This file includes the code exclusive to the server

def server(port: Int = 42069) = {
  println(s"Opened server with port $port\nWaiting for incoming requests...")
   writeLog(s"///Opened server with port $port///")
  val ss = ServerSocket(port)
  //var closeServer = false
  while true do {
    try
      println("---Opening new connection---\n")
      writeLog("Server is awating connection")
      serverSession(ss)
      println("---Closing connection---")
      writeLog("Server closed connection")
    catch
      case e: Exception =>
        printStatus("The server has crashed or the client disconnected unexpectedly!", true)
        writeLog("Server or connection crashed!")
  }
  ss.close()
  writeLog(s"Server with port $port closed")
}

def serverSession(ss: ServerSocket) = {
  val sock = ss.accept()
  val is = sock.getInputStream()
  val os = sock.getOutputStream()
  val config = getConfigFile()
  val password = getPassword(config)
  val dir = getStorageDirectory(config)
  println("Connection established with client, waiting for password input")

  while is.available() == 0 do {
    Thread.sleep(250)
  }
  //val inpass = bytesToString(readBytes(is.available(), is))
  val inpass = readString(is.available(), is)
  if inpass == password then
    println("Password is correct, proceeding")
    os.write(Array[Byte](1))

    var closeServer = false
    while closeServer == false do {
      val clientRequest = readStatusByte(is)
      if clientRequest == 1 then
        println("Client requested file download")
        writeLog("Client established connection, password is correct, requested file download")
        serverUpload(is, os, dir)
      else if clientRequest == 2 then
        println("Client requested file upload")
        writeLog("Client established connection, password is correct, requested file upload")
        serverDownload(is, os, dir)
      else
        closeServer = true
        println("Client ended connection")
    }
  else
    printStatus("Incorrect password received", false)
    writeLog("Client established connection, password is incorrect")
    os.write(Array[Byte](0))
  sock.close()
}

def serverDownload(is: InputStream, os: OutputStream, dir: String) = {
  //val nameLen = bytesToInt(readBytes(4, is))
  val nameLen = readInt(is)
  //val name = bytesToString(readBytes(nameLen, is))
  val name = readString(nameLen, is)
  //val fileLen = bytesToLong(readBytes(8, is))
  val fileLen = readLong(is)
  //println(s"Name: $name\nnamelen: $nameLen\nFile length: $fileLen")

  if isFileValid(fileLen, nameLen) == true then
    println(s"\n--Downloading File--\nName: $name\nLength: $fileLen bytes\n")
    writeLog(s"///Downloading $name\nLength: $fileLen bytes///")
    os.write(Array[Byte](1))
    download(is, name, fileLen, dir)
    println(s"Finished downloading $name!")
  else
    printStatus(s"Requested file transfer exceeds configured limit or file name is empty", true)
    os.write(Array[Byte](0))
}

def serverUpload(is: InputStream, os: OutputStream, dir: String) = {
  val files = sendServerFileInfo(os, dir)
  println("Sending storage information")
  if files.length == 0 then
    printStatus("The server storage is empty, there is nothing to send to the client", false)
  else
    while readStatusByte(is) != 0 do {
      //val chosen = bytesToInt(readBytes(4, is))
      val chosen = readInt(is)
      val len = File(files(chosen)).length()
      //os.write(longToBytes(len))
      sendLong(len, os)

      println(s"\n--Uploading File--\nName: ${files(chosen)}\nLength: $len bytes")
      writeLog(s"Uploading ${files(chosen)}\nLength: $len bytes")
      upload(os, s"$dir${files(chosen)}")
      println(s"Finished uploading ${files(chosen)}!")
    }
}
