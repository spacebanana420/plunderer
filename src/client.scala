package plunderer

import java.net.ServerSocket
import java.net.Socket
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File


def client(host: String = "localhost", port: Int = 42069, filename: String, filepath: String) = {
    println(s"Connecting to host \"$host\" at port $port")
    val sock = new Socket(host, port)
    val os = sock.getOutputStream()
    val is = sock.getInputStream()
    println("Connection established to server")

    val password = stringToBytes(readUserInput("Input connection password:"))
    os.write(password)
    val status = new Array[Byte](1)
    is.read(status)

    if status(0) == 1 then
        println("Connection accepted, uploading file")
        val len = File(filepath).length()
        val lenbytes = longToBytes(len)
        os.write(lenbytes)

        val namelen = filename.length
        val namelen_bytes = intToBytes(namelen)
        val name_bytes = stringToBytes(filename)
        os.write(namelen_bytes)
        os.write(name_bytes)
        is.read(status)
        if status(0) == 1 then
            println("Uploading file")
            clientWrite(sock, filepath, len)
        else
            println("Connection refused\nFile exceeds the server's configured limit or filename is empty")
    else
        println("Incorrect password!")
}

def clientWrite(sock: Socket, filepath: String, len: Long) = {
    val os = sock.getOutputStream()
    val filein = new FileInputStream(filepath)
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
    readUserInput("File upload successful!\n\nPress any key")
}
