package yakumo

import java.io.InputStream

def readStatusByte(is: InputStream): Byte = {
    val status = new Array[Byte](1)
    is.read(status)
    status(0)
}
