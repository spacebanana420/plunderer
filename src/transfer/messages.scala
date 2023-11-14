package yakumo.transfer
import yakumo.*

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

//test these muthafuckas
def readShort(is: InputStream): Short = bytesToShort(readBytes(2, is))

def readInt(is: InputStream): Int = bytesToInt(readBytes(4, is))

def readLong(is: InputStream): Long = bytesToLong(readBytes(8, is))

def readString(len: Int, is: InputStream): String = bytesToString(readBytes(len, is))

def sendShort(msg: Short, os: OutputStream) = os.write(shortToBytes(msg))

def sendInt(msg: Int, os: OutputStream) = os.write(intToBytes(msg))

def sendLong(msg: Long, os: OutputStream) = os.write(longToBytes(msg))

def sendString(msg: String, os: OutputStream) = os.write(stringToBytes(msg))

//extend with download, upload, etc
def sendMessage(message: String, os: OutputStream) = {
  val bytes =
    message match
      case "go" => Array[Byte](1)
      case "stop" => Array[Byte](0)
      case "close" => Array[Byte](2)
  os.write(bytes)
}



// def sendBytes(bytes: Array[Byte], os: OutputStream) = {
//   os.write(bytes)
// }
