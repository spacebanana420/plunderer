package plunderer

import java.nio.ByteBuffer
import java.io.FileInputStream


def readPassFile(path: String): Array[Byte] = {
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
