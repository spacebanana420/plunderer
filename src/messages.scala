package yakumo

import java.io.InputStream
import java.io.OutputStream

def readStatusByte(is: InputStream): Byte = {
    val status = new Array[Byte](1)
    is.read(status)
    status(0)
}

def readBytes(len: Int, is: InputStream): Array[Byte] = {
    val bytes = new Array[Byte](len)
    is.read(bytes)
    bytes
}

// def sendBytes(bytes: Array[Byte], os: OutputStream) = {
//     os.write(bytes)
// }
