package plunderer

import java.io.File
import scala.sys.exit

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
                client(port = getPort())
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


def readUserInput(message: String = ""): String = {
    if message != "" then
        println(message)
    scala.io.StdIn.readLine()
}
