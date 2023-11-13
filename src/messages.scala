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
