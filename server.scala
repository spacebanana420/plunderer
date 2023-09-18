package network

import java.net.ServerSocket
import java.net.Socket
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File
import java.nio.ByteBuffer

@main def main(arg: String) = {
    if arg == "server" then server()
    else client()
}

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
        println("File length: " + len + " bytes")
        if len <= 20000000000 then
            os.write(Array[Byte](1))
            serverWrite(sock, len)
        else
            println("Requested file transfer exceeds 20GB\nClosing connection")
            os.write(Array[Byte](1))
            ss.close()
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

def client(host: String = "localhost", port: Int = 42069) = {
    val sock = new Socket(host, port)
    val os = sock.getOutputStream()
    val is = sock.getInputStream()
    println(s"Connecting to host \"$host\" at port $port")

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
        is.read(status)
        if status(0) == 1 then
            println("Uploading file")
            clientWrite(sock, len)
        else
            println("Connection refused\nFile exceeds 20GB")
    else
        println("Refused connection!")
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

// def printfuckingarrays(a1: Array[Byte], a2: Array[Byte]) = {
//     println("Array 1")
//     for i <- a1 do {
//         print(i + " ")
//     }
//     println("Array2")
//     for i <- a2 do {
//         print(i + " ")
//     }
// }
def readUserInput(message: String = ""): String = {
    if message != "" then
        println(message)
    scala.io.StdIn.readLine()
}

// def readTextFile(pathstr: String): List[String] = {
//     if File(pathstr).isFile == true then
//         val path = Paths.get(pathstr)
//         Files.readAllLines(path)
//     else
//         List[String]()
// }

def readTextFile(path: String): Array[Byte] = {
    val file = new FileInputStream(path)
    val bytes = new Array[Byte](file.available())
    var bytes_filtered = Array[Byte]()
    file.read(bytes)
    file.close()
    for i <- bytes do {
        if i != 10 then
            bytes_filtered = bytes_filtered :+ i
    }
    bytes_filtered
}

def stringToBytes(txt: String): Array[Byte] = {
    var charBytes = new Array[Byte](txt.length)
    for i <- 0 to txt.length-1 do {
        charBytes(i) = txt(i).toByte
    }
    charBytes
}

def bytesToString(txt: Array[Byte]): String = {
    var str = ""
    for i <- txt do {
        str += i.toChar
    }
    str
}

def longToBytes(num: Long): Array[Byte] = {
    Array[Byte](
    (num>>56).toByte,
    (num>>48).toByte,
    (num>>40).toByte,
    (num>>32).toByte,
    (num>>24).toByte,
    (num>>16).toByte,
    (num>>8).toByte,
    num.toByte
    )
}

def bytesToLong(bytes: Array[Byte]): Long = ByteBuffer.wrap(bytes).getLong()

// def getHeader(instruction: String): Array[Byte] = {
//     instruction match
//         case "go" => Array[Byte](1)
//         case "lenght" => Array[Byte](2)
//         case "final" => Array[Byte](3)
//         case "stop" => Array[Byte](0)
// }
