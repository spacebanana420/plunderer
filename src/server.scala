package plunderer

import java.net.ServerSocket
import java.net.Socket
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File


def server(port: Int = 42069) = {
    println("Opened server with port " + port)
    val ss = new ServerSocket(port)
    val sock = ss.accept()
    val is = sock.getInputStream()
    val os = sock.getOutputStream()
    val password = readPassFile("password.txt")

    println("Waiting for connection requests")
    while is.available() == 0 do {
        Thread.sleep(400)
    }

    println("New connection\nRequesting password")
    val inpass = new Array[Byte](is.available())
    is.read(inpass)

    if inpass.sameElements(password) == true then
        println("Correct password input, proceeding")
        os.write(Array[Byte](1))

        val lengbytes = new Array[Byte](8)
        is.read(lengbytes)
        val len = bytesToLong(lengbytes)
        val maxlen: Long = 20000000000

        val namelen_bytes = new Array[Byte](4)
        is.read(namelen_bytes)
        val namelen = bytesToInt(namelen_bytes)
        val name_bytes = new Array[Byte](namelen)
        is.read(name_bytes)
        val name = bytesToString(name_bytes)
        println(s"--Downloading File--\nName: $name\nLength: $len bytes")

        if len <= maxlen && namelen > 0 then
            os.write(Array[Byte](1))
            serverWrite(sock, name, len)
        else
            println("Requested file transfer exceeds 20GB or name length is 0\nClosing connection")
            os.write(Array[Byte](0))
    else
        println("Incorrect password sent, closing server")
        os.write(Array[Byte](0))
    ss.close()
}

def serverWrite(sock: Socket, name: String, len: Long) = {
    val is = sock.getInputStream()
    val os = sock.getOutputStream()

    val fileout = new FileOutputStream(getDownloadName(name))
    var buf = 0
    val data = new Array[Byte](256)
    while buf <= len-256 do {
        is.read(data)
        fileout.write(data)
        buf += 256
    }
    if buf < len then
        val finalbyte = new Array[Byte]((len-buf).toInt)
        is.read(finalbyte)
        fileout.write(finalbyte)
    fileout.close()
    println("File successfully written!\nClosing server...")
}

def getDownloadName(name: String, i: Int = 1): String = {
    val finalname =
        if File(s"NEW_$name").exists() == false then
            s"NEW_$name"
        else if File(s"NEW${i}_$name").exists() == false then
            s"NEW${i}_$name"
        else
            getDownloadName(name, i+1)
    finalname
}
