package yakumo.transfer
import yakumo.*

import java.io.InputStream
import java.io.OutputStream

//This file contains functions so the server and client communicate with each other

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

//tested them, everything seems to be fine
def readShort(is: InputStream): Short = bytesToShort(readBytes(2, is))

def readInt(is: InputStream): Int = bytesToInt(readBytes(4, is))

def readLong(is: InputStream): Long = bytesToLong(readBytes(8, is))

def readString(len: Int, is: InputStream): String = bytesToString(readBytes(len, is))

def sendShort(msg: Short, os: OutputStream) = os.write(shortToBytes(msg))

def sendInt(msg: Int, os: OutputStream) = os.write(intToBytes(msg))

def sendLong(msg: Long, os: OutputStream) = os.write(longToBytes(msg))

def sendString(msg: String, os: OutputStream) = os.write(stringToBytes(msg))


def sendMessage(message: String, os: OutputStream) = {
  val bytes =
    message match
      case "stop" => Array[Byte](0)
      case "go" => Array[Byte](1)
      case "close" => Array[Byte](2)
      case "getfiles" => Array[Byte](3)
      case "upload" => Array[Byte](4)
      case "download" => Array[Byte](5)
      case "delete" => Array[Byte](6) //implement!!!
      case _ =>
        printStatus("Incorrect message sent!", true)
        Array[Byte](-1)
  os.write(bytes)
}

def receiveMessage(is: InputStream): String = {
  val bytes = new Array[Byte](1)
  is.read(bytes)
  bytes(0) match
    case 0 => "stop"
    case 1 => "go"
    case 2 => "close"
    case 3 => "getfiles"
    case 4 => "upload"
    case 5 => "download"
    case 6 => "delete" //implement!!!
    case _ =>
      printStatus("Incorrect message received!", true)
      "error"
}
