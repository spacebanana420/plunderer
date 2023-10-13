package yakumo

import java.net.ServerSocket
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream

import java.io.FileInputStream
import java.io.FileOutputStream
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
        val mode = readUserInput(s"Connection accepted\n\n${green}0:${default} Download     ${green}1:${default} Upload\nChoose a mode")
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
    val howMany = bytesToInt(readBytes(4, is))
    if howMany != 0 then
        val filenames = receiveServerFileInfo(is, howMany)
        val choice = chooseServerFile(filenames)
        os.write(intToBytes(choice))
        val len = bytesToLong(readBytes(8, is))

        println(s"--Downloading File--\nName: ${filenames(choice)}\nLength: $len bytes")
        download(is, filenames(choice), len)
        readUserInput(s"Finished downloading ${filenames(choice)}!\nPress enter to continue")
    else
        println("The server's storage is empty!\nClosing connection")
}

def receiveServerFileInfo(is: InputStream, howMany: Int, i: Int = 1, filenames: List[String] = List[String]()): List[String] = {
    if i <= howMany then
        val len = bytesToInt(readBytes(4, is))
        val name = bytesToString(readBytes(len, is))
        receiveServerFileInfo(is, howMany, i+1, filenames :+ name)
    else
        filenames
}

def chooseServerFile(files: List[String]): Int = {
    val green = foreground("green")
    val default = foreground("default")
    var i = 0
    var screen = "--Server File Storage--\n\n"
    var filesInLine = 0
    for file <- files do {
        if filesInLine < 3 then
            screen ++= s"$green$i:$default $file     "
            filesInLine += 1
        else  
            screen ++= s"$green$i:$default $file\n"
            filesInLine = 0
        i += 1
    }
    val choice = readUserInput(screen)
    choice.toInt
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
        println("Connection refused\nFile exceeds the server's configured limit or filename is empty")
}
