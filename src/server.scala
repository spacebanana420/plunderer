package yakumo

import java.net.ServerSocket
import java.net.Socket
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
    val maxperfile = getFileLimit(config, "perfile")
    val maxtotal = getFileLimit(config, "total")
    //val password = readPassFile("password.txt")

    println("New connection\nRequesting password")
        while is.available() == 0 do {
            Thread.sleep(350)
        }
    val inpass = new Array[Byte](is.available())
    is.read(inpass)

    if inpass.sameElements(password) == true then
        println("Correct password input, proceeding\n\n")
        os.write(Array[Byte](1))

        val lenbytes = new Array[Byte](8)
        is.read(lenbytes)
        val len = bytesToLong(lenbytes)

        val namelen_bytes = new Array[Byte](4)
        is.read(namelen_bytes)
        val namelen = bytesToInt(namelen_bytes)
        val name_bytes = new Array[Byte](namelen)
        is.read(name_bytes)
        val name = bytesToString(name_bytes)
        println(s"--Downloading File--\nName: $name\nLength: $len bytes\n")

        if len / 1000000000 <= maxperfile && namelen > 0 then
            os.write(Array[Byte](1))
            serverWrite(sock, name, len)
            println("File successfully written!\nClosing connection")
        else
            println(s"Requested file transfer exceeds ${maxperfile}GB or file name length is 0\nClosing connection")
            os.write(Array[Byte](0))
    else
        println("Incorrect password received, closing connection")
        os.write(Array[Byte](0))
    sock.close()
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
