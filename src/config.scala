package yakumo

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


def createConfig() = {
    val defaultConfig = stringToBytes("password=test123\ndirectory=\"\"\nmaxperfile=20\nmaxtotal=30")
    val file = new FileOutputStream("config.txt")
    file.write(defaultConfig)
    file.close()
}

def isConfigFine(): Boolean = {
    val config = getConfigFile()
    val password = getPassword(config)
    val dir = getStorageDirectory(config)
    val direxists = File(dir).isDirectory()
    val perfile = getFileLimit(config, "perfile")
    val total = getFileLimit(config, "total")

    val isConfigOk =
        if password != "" && (dir == "" || direxists == true) && perfile != -1 && total != -1 then
            true
        else
            false
    isConfigOk
}

def getConfigFile(): List[String] = {
    val file = new FileInputStream("config.txt")
    val bytes = new Array[Byte](file.available())
    file.read(bytes)
    file.close()

    val config = configToStringList(bytes)
    config
}

def getPassword(config: List[String]): String = {
    val passline = findLine(config, "password=")
    getLineSetting(passline)
}

def getStorageDirectory(config: List[String]): String = {
    val dirline = findLine(config, "directory=")
    val setting = getLineSetting(dirline)
    if setting == "\"\"" then
        ""
    else if setting(setting.length-1) != '/' then
       s"$setting/"
    else
        setting
}

def getFileLimit(config: List[String], mode: String): Int = {
    val settingName =
        mode match
            case "perfile" => "maxperfile="
            case "total" => "maxtotal="
    val settingLine = findLine(config, settingName)
    val strnum = getLineSetting(settingLine)
    try
        val num = strnum.toInt
        num
    catch
        case e: Exception => -1
}

def getLineSetting(line: String): String = {
    var copy = false
    var setting = ""
    for chr <- line do {
        if copy == true then
            setting += chr
        else if chr == '=' then
            copy = true
    } 
    setting
}

def findLine(config: List[String], seekstr: String, i: Int = 0): String = {
    if config(i).contains(seekstr) == true then
        config(i)
    else if i == config.length-1 then
        ""
    else
        findLine(config, seekstr, i+1)
}

def configToStringList(cfgBytes: Array[Byte], line: String = "", cfgstr: List[String] = List[String](), i: Int = 0): List[String] = {
    val chr = cfgBytes(i).toChar
    if i == cfgBytes.length-1 then
        cfgstr :+ line
    else if chr == '\n' then
        configToStringList(cfgBytes, "", cfgstr :+ line, i+1)
    else
        configToStringList(cfgBytes, line + chr, cfgstr, i+1)
}
