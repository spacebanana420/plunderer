package plunderer

import java.io.File
import java.io.FileOutputStream
import scala.sys.exit
import scala.sys.process.*

@main def main() = {
    if File("config.txt").isFile() == false then
        createConfig()
    val mode = readUserInput("--Choose an option--\n0: Exit   1: Server   2: Client\n")
    while true do {
        mode match
            case "0" => exit()
            case "1" =>
                if isConfigFine() == true then
                    server(getPort())
                else
                    println("You need to have a properly configured config.txt file!\nCancelling server launch")
            case "2" =>
                val file = getFile()
                val ip = getIP()
                val port = getPort()
                try
                    client(ip, port, file(0), file(1))
                catch
                    case e: Exception => readUserInput("Connection failed!\nMaybe the server isn't open?\n\nPress any key")
            case _ => exit()
    }
}

def getPort(): Int = {
    val portstr = readUserInput("Type the port to use\ndefault: 42069\n")
    try
        portstr.toInt
    catch
        case e: Exception => 42069
}

def getFile(): Array[String] = {
    val filepath = browse()
    val relative = getRelativePath(filepath)
    Array[String](relative, filepath)
}

def getIP(): String = {
    val answer = readUserInput("Input the IP to connect to (default: localhost)")
    if answer != "" then
        answer
    else
        "localhost"
}

def readUserInput(message: String = ""): String = {
    if message != "" then
        println(message)
    scala.io.StdIn.readLine()
}

def clear() = { //add windows support
    List[String]("clear").!
    //List[String]("cmd", "/c", "cls").!
}

def createConfig() = {
    val defaultConfig = stringToBytes("password=test123\nmaxperfile=20\nmaxtotal=30")
    val file = new FileOutputStream("config.txt")
    file.write(defaultConfig)
    file.close()
}

def isConfigFine(): Boolean = {
    val config = getConfigFile()
    val password = getPassword(config)
    val perfile = getFileLimit(config, "perfile")
    val total = getFileLimit(config, "total")

    val isConfigOk =
        if password != "" && perfile != -1 && total != -1 then
            true
        else
            false
    isConfigOk
}