package plunderer

import java.io.File
import scala.sys.exit

@main def main() = {
    val mode = readUserInput("--Choose an option--\nexit\nserver\nclient")
    val passExists = File("password.txt").isFile()
    while true do {
        mode match
            case "server" =>
                if passExists == true then
                    server()
                else
                    println("You need to have a password.txt file in the root of the server\nCancelling server launch")
            case "client" =>
                client()
            case "exit" => exit()
            case _ => exit()
    }
}


def readUserInput(message: String = ""): String = {
    if message != "" then
        println(message)
    scala.io.StdIn.readLine()
}
