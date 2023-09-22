package plunderer

import java.io.File
import scala.sys.exit
import scala.sys.process.*

@main def main() = {
    val mode = readUserInput("--Choose an option--\nexit\nserver\nclient\n")
    val passExists = File("password.txt").isFile()
    while true do {
        mode match
            case "server" =>
                if passExists == true then
                    server(getPort())
                else
                    println("You need to have a password.txt file in the root of the server\nCancelling server launch")
            case "client" =>
                val file = getFile()
                client(port = getPort(), file(0), file(1))
            case "exit" => exit()
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

def readUserInput(message: String = ""): String = {
    if message != "" then
        println(message)
    scala.io.StdIn.readLine()
}

def clear() = { //add windows support
    List[String]("clear").!
}
