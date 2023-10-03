package yakumo

import java.net.ServerSocket
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File


def client(host: String = "localhost", port: Int = 42069) = {
    println(s"Connecting to host \"$host\" at port $port")
    val sock = Socket(host, port)
    val os = sock.getOutputStream()
    val is = sock.getInputStream()
    println("Connection established to server")

    val password = stringToBytes(readUserInput("Input connection password:"))
    os.write(password)
    val status = new Array[Byte](1)
    is.read(status)

    if status(0) == 1 then
        val mode = readUserInput("Connection accepted\n\n0: Download     1: Upload\nChoose a mode")
        if mode == "0" then
            os.write(Array[Byte](0))
            clientDownload(is, os)
        else
            os.write(Array[Byte](1))
            clientUpload(is, os)
    else
        println("Incorrect password! Connection was refused")
    //sock.close()
}

def clientDownload(is: InputStream, os: OutputStream) = {
    //while is.available == 0 do {Thread.sleep(350)}
    val howMany_byte = new Array[Byte](4)
    is.read(howMany_byte)
    val howMany = bytesToInt(howMany_byte)
    if howMany != 0 then
        val filenames = receiveServerFileInfo(is, howMany)
        val choice = chooseServerFile(filenames)
        val choicebytes = intToBytes(choice)
        os.write(choicebytes)
        val lenbytes = new Array[Byte](8)
        is.read(lenbytes)
        val len = bytesToLong(lenbytes)
        println(s"--Downloading File--\nName: ${filenames(choice)}\nLength: $len bytes")
        download(is, filenames(choice), len)
    else
        println("The server's storage is empty!\nClosing connection")
}

def receiveServerFileInfo(is: InputStream, howMany: Int, i: Int = 1, filenames: List[String] = List[String]()): List[String] = {
    if i <= howMany then
        val lenbytes = new Array[Byte](4)
        is.read(lenbytes)
        val len = bytesToInt(lenbytes)
        val namebytes = new Array[Byte](len)
        is.read(namebytes)
        val name = bytesToString(namebytes)
        receiveServerFileInfo(is, howMany, i+1, filenames :+ name)
    else
        filenames
}

def chooseServerFile(files: List[String]): Int = {
    var i = 0
    var screen = "--Server File Storage--\n\n"
    var filesInLine = 0
    for file <- files do {
        if filesInLine < 3 then
            screen ++= s"$i: $file     "
            filesInLine += 1
        else  
            screen ++= s"$i: $file\n"
            filesInLine = 0
        i += 1
    }
    val choice = readUserInput(screen)
    choice.toInt
}

def clientUpload(is: InputStream, os: OutputStream) = {
    val filepath = browse()
    val filename = getRelativePath(filepath)

    val len = File(filepath).length()
    val lenbytes = longToBytes(len)
    val namelen = filename.length
    val namelen_bytes = intToBytes(namelen)
    val name_bytes = stringToBytes(filename)
    os.write(lenbytes)
    os.write(namelen_bytes)
    os.write(name_bytes)
    val status = new Array[Byte](1)
    is.read(status)
    if status(0) == 1 then
        println(s"--Uploading File--\nName: $filename\nLength: $len bytes")
        upload(os, filepath, len)
    else
        println("Connection refused\nFile exceeds the server's configured limit or filename is empty")
}
