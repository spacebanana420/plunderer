package yakumo

import java.net.ServerSocket
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File


def server(port: Int = 42069) = {
    println(s"Opened server with port $port\nWaiting for incoming requests...")
    val ss = new ServerSocket(port)
    //var closeServer = false
    while true do {
        serverSession(ss)
    }
    ss.close()
}

def serverSession(ss: ServerSocket) = {
    val sock = ss.accept()
    val is = sock.getInputStream()
    val os = sock.getOutputStream()
    val config = getConfigFile()
    val password = getPassword(config)

    println("New connection\nRequesting password")
    while is.available() == 0 do {
        Thread.sleep(350) 
    }
    val inpass_bytes = new Array[Byte](is.available())
    is.read(inpass_bytes)
    val inpass = bytesToString(inpass_bytes)

    if inpass == password then
        os.write(Array[Byte](1))
        val mode = new Array[Byte](1)
        is.read(mode)
        if mode(0) == 0 then
            serverUpload(is, os)
        else
            serverDownload(is, os)
    else
        println("Incorrect password received, closing connection")
        os.write(Array[Byte](0))
    sock.close()
}

def serverDownload(is: InputStream, os: OutputStream) = {
    val config = getConfigFile()
    val maxperfile = getFileLimit(config, "perfile")
    val maxtotal = getFileLimit(config, "total")

    val lenbytes = new Array[Byte](8)
    is.read(lenbytes)
    val len = bytesToLong(lenbytes)

    val namelen_bytes = new Array[Byte](4)
    is.read(namelen_bytes)
    val namelen = bytesToInt(namelen_bytes)
    val name_bytes = new Array[Byte](namelen)
    is.read(name_bytes)
    val name = bytesToString(name_bytes)

    if len / 1000000000 <= maxperfile && namelen > 0 then
        println(s"--Downloading File--\nName: $name\nLength: $len bytes\n")
        os.write(Array[Byte](1))
        download(is, name, len)
        println(s"Finished downloading $name!\nClosing connection")
    else
        println(s"Requested file transfer exceeds ${maxperfile}GB or file name length is 0\nClosing connection")
        os.write(Array[Byte](0))
}

def serverUpload(is: InputStream, os: OutputStream) = {
    val files = sendServerFileInfo(os)
    val chosen_byte = new Array[Byte](4)
    is.read(chosen_byte)
    val chosen = bytesToInt(chosen_byte)
    val len = File(files(chosen)).length()
    // val lenbytes = longToBytes(len)
    // val namelen_bytes = intToBytes(files(chosen).length)
    println(s"--Uploading File--\nName: ${files(chosen)}\nLength: $len bytes")
    // os.write(lenbytes)
    // os.write(namelen_bytes)
    // os.write(stringToBytes(files(chosen)))
    upload(os, files(chosen), len)
    println(s"Finished uploading ${files(chosen)}!\nClosing connection")
}

def sendServerFileInfo(os: OutputStream): Array[String] = {
    //val os = sock.getOutputStream()
    val files = File(".").list().filter(x => File(x).isFile == true)
    val howMany = intToBytes(files.length)
    os.write(howMany)
    for file <- files do {
        val namelen_bytes = intToBytes(file.length)
        val namebytes = stringToBytes(file)
        os.write(namelen_bytes)
        os.write(namebytes)
    }
    files
}

// def serverWrite(sock: Socket, name: String, len: Long) = {
//     val is = sock.getInputStream()
//     val os = sock.getOutputStream()

//     val fileout = new FileOutputStream(getDownloadName(name))
//     var buf = 0
//     val data = new Array[Byte](256)
//     while buf <= len-256 do {
//         is.read(data)
//         fileout.write(data)
//         buf += 256
//     }
//     if buf < len then
//         val finalbyte = new Array[Byte]((len-buf).toInt)
//         is.read(finalbyte)
//         fileout.write(finalbyte)
//     fileout.close()
// }
