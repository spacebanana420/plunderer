package plunderer

import java.net.ServerSocket
import java.net.Socket
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File


def client(host: String = "localhost", port: Int = 42069) = {
    println(s"Connecting to host \"$host\" at port $port")
    val sock = new Socket(host, port)
    val os = sock.getOutputStream()
    val is = sock.getInputStream()
    println("Connection established")

    val password = stringToBytes(readUserInput("Input connection password:"))
    os.write(password)
    val status = new Array[Byte](1)
    is.read(status)

    if status(0) == 1 then
        println("Connection accepted, transferring file")
        val len = File("input.png").length()
        println("File length: " + len)
        val lenbytes = longToBytes(len)
        os.write(lenbytes)

        val namelen = "input.png".length
        val namelen_bytes = intToBytes(namelen)
        val name = stringToBytes("input.png")
        os.write(namelen_bytes)
        os.write(name)
        is.read(status)
        if status(0) == 1 then
            println("Uploading file")
            clientWrite(sock, len)
        else
            println("Connection refused\nFile exceeds 20GB or filename's length is 0")
    else
        println("Incorrect password!")
}

def clientWrite(sock: Socket, len: Long) = {
    val os = sock.getOutputStream()
    val filein = new FileInputStream("input.png")
    val data = new Array[Byte](256)
    while filein.available() >= 256 do {
        filein.read(data)
        os.write(data)
    }
    if filein.available() > 0 then
        val finaldata = new Array[Byte](filein.available())
        filein.read(finaldata)
        os.write(finaldata)
    filein.close()
}
