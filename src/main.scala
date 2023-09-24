package plunderer

import java.io.File
import scala.sys.exit
import scala.sys.process.*

@main def main() = {
    val mode = readUserInput("--Choose an option--\n1: Exit   2: Server   3: Client\n")
    val passExists = File("password.txt").isFile()
    //val configExists = File("config.txt").isFile()
    while true do {
        mode match
            case "2" =>
                if passExists == true then
                    server(getPort())
                else
                    println("You need to have a password.txt file in the root of the server\nCancelling server launch")
            case "3" =>
                val file = getFile()
                val ip = getIP()
                val port = getPort()
                try
                    client(ip, port, file(0), file(1))
                catch
                    case e: Exception => readUserInput("Connection failed!\nMaybe the server isn't open?\n\nPress any key")
            case "1" => exit()
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

//def verifyIP(ip: String): String = {

def readUserInput(message: String = ""): String = {
    if message != "" then
        println(message)
    scala.io.StdIn.readLine()
}

def clear() = { //add windows support
    List[String]("clear").!
}
