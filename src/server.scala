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
    val password = readTextFile("password.txt")

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
        println("File length: " + len + " bytes")
        if len <= maxlen then
            os.write(Array[Byte](1))
            serverWrite(sock, len)
        else
            println("Requested file transfer exceeds 20GB\nClosing connection")
            os.write(Array[Byte](1))
    else
        println("Incorrect password sent, closing server")
        os.write(Array[Byte](0))
    ss.close()
}

def serverWrite(sock: Socket, len: Long) = {
    val is = sock.getInputStream()
    val os = sock.getOutputStream()

    val fileout = new FileOutputStream("output.png")
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
