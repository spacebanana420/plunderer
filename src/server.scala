package yakumo

import java.net.ServerSocket
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File

//This file includes the code exclusive to the server

def server(port: Int = 42069) = {
    println(s"Opened server with port $port\nWaiting for incoming requests...")
    val ss = ServerSocket(port)
    //var closeServer = false
    while true do {
        try
            println("---Opening new connection---\n")
            serverSession(ss)
            println("---Closing connection---")
        catch
            case e: Exception => printStatus("The server has crashed or the client disconnected unexpectedly!", true)
    }
    ss.close()
}

def serverSession(ss: ServerSocket) = {
    val sock = ss.accept()
    val is = sock.getInputStream()
    val os = sock.getOutputStream()
    val config = getConfigFile()
    val password = getPassword(config)
    val dir = getStorageDirectory(config)
    println("Connection established with client")

    while is.available() == 0 do {
        Thread.sleep(250)
    }

    val inpass = bytesToString(readBytes(is.available(), is))
    if inpass == password then
        println("Password is correct, proceeding")
        os.write(Array[Byte](1))

        var closeServer = false
        while closeServer == false do {
            val clientRequest = readStatusByte(is)
            if clientRequest == 1 then
                println("Client requested file download")
                serverUpload(is, os, dir)
            else if clientRequest == 2 then
                println("Client requested file upload")
                serverDownload(is, os, dir)
            else
                closeServer = true
                println("Client ended connection")
        }
    else
        printStatus("Incorrect password received", false)
        os.write(Array[Byte](0))
    sock.close()
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

def serverDownload(is: InputStream, os: OutputStream, dir: String) = {
    val nameLen = bytesToInt(readBytes(4, is))
    val name = bytesToString(readBytes(nameLen, is))
    val fileLen = bytesToLong(readBytes(8, is))
    //println(s"Name: $name\nnamelen: $nameLen\nFile length: $fileLen")

    if isFileValid(fileLen, nameLen) == true then
        println(s"\n--Downloading File--\nName: $name\nLength: $fileLen bytes\n")
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
        printStatus("The server storage is empty, there is nothing to send to the client", true)
    else
        while readStatusByte(is) != 0 do {
            val chosen = bytesToInt(readBytes(4, is))
            val len = File(files(chosen)).length()
            os.write(longToBytes(len))

            println(s"\n--Uploading File--\nName: ${files(chosen)}\nLength: $len bytes")
            upload(os, s"$dir${files(chosen)}")
            println(s"Finished uploading ${files(chosen)}!")
        }
}

def sendServerFileInfo(os: OutputStream, dir: String): Array[String] = {
    val files = File(dir).list().filter(x => File(x).isFile == true)
    val howMany = intToBytes(files.length)
    os.write(howMany)
    for file <- files do {
        os.write(intToBytes(file.length))
        os.write(stringToBytes(file))
    }
    files
}
