package yakumo

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import java.net.Socket
import java.io.InputStream
import java.io.OutputStream

def download(is: InputStream, name: String, len: Long) = {
    val fileout = FileOutputStream(getDownloadName(name))
    var buf = 0
    val data = new Array[Byte](256)
    while buf <= len - 256 do {
        is.read(data)
        fileout.write(data)
        buf += 256
    }
    if buf < len then
        val finalbyte = new Array[Byte]((len - buf).toInt)
        is.read(finalbyte)
        fileout.write(finalbyte)
    fileout.close()
}

def upload(os: OutputStream, filepath: String, len: Long) = {
    val filein = FileInputStream(filepath)
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
    //readUserInput("File upload successful!\n\nPress any key")
}

def getDownloadName(name: String, i: Int = 1): String = {
    val finalname =
        if File(name).exists() == false then
            name
        else if File(s"new_$name").exists() == false then
            s"new_$name"
        else if File(s"new${i}_$name").exists() == false then
            s"new${i}_$name"
        else
            getDownloadName(name, i+1)
    finalname
}