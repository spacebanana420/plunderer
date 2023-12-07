package yakumo

import java.nio.ByteBuffer
import java.io.FileInputStream


// def readPassFile(path: String): Array[Byte] = {
//   val file = new FileInputStream(path)
//   val bytes = new Array[Byte](file.available())
//   var bytes_filtered = Array[Byte]()
//   file.read(bytes)
//   file.close()
//   for i <- bytes do {
//     if i != 10 then
//       bytes_filtered = bytes_filtered :+ i
//   }
//   bytes_filtered
// }

def stringToBytes(txt: String): Array[Byte] =
  var charBytes = new Array[Byte](txt.length)
  for i <- 0 to txt.length-1 do
    charBytes(i) = txt(i).toByte
  charBytes

def bytesToString(txt: Array[Byte]): String =
  var str = ""
  for i <- txt do
    str += i.toChar
  str

def utf16ToBytes(txt: String, bytes: Array[Byte] = Array[Byte](), i: Int = 0): Array[Byte] =
  if i < txt.length then
    utf16ToBytes(txt, bytes ++ shortToBytes(txt(i).toShort), i+1)
  else
    bytes

def bytesToUtf16(txt: Array[Byte], buf: Array[Byte] = Array[Byte](), str: String = "", i: Int = 0): String =
  if i == txt.length then
    str + bytesToShort(buf).toChar
  else if buf.length == 2 then
    bytesToUtf16(txt, Array[Byte](txt(i)), str + bytesToShort(buf).toChar, i+1)
  else
    bytesToUtf16(txt, buf :+ txt(i), str, i+1)

def shortToBytes(num: Short): Array[Byte] =
  Array[Byte](
  (num>>8).toByte,
  num.toByte
  )

def intToBytes(num: Int): Array[Byte] =
  Array[Byte](
  (num>>24).toByte,
  (num>>16).toByte,
  (num>>8).toByte,
  num.toByte
  )

def longToBytes(num: Long): Array[Byte] =
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

def bytesToLong(bytes: Array[Byte]): Long = ByteBuffer.wrap(bytes).getLong()

def bytesToInt(bytes: Array[Byte]): Int = ByteBuffer.wrap(bytes).getInt()

def bytesToShort(bytes: Array[Byte]): Short = ByteBuffer.wrap(bytes).getShort()

// def getHeader(instruction: String): Array[Byte] = {
//   instruction match
//     case "accepted" => Array[Byte](1)
//     case "string" => Array[Byte](2)
//     case "download" => Array[Byte](3)
//     case "shutdown" => Array[Byte](0)
// }
