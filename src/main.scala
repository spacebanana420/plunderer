package yakumo

import java.io.File
import java.io.FileOutputStream
import scala.sys.exit

@main def main() = { //add user authentication and unique folder per user
    val cyan = foreground("cyan")
    val default = foreground("default")
    if File("config.txt").isFile() == false then
        createConfig()
    val mode = readUserInput(s"$cyan[Yakumo v0.2]\n$default--Choose an option--\n0: Exit   1: Server   2: Client\n")
    while true do {
        mode match
            case "0" => exit()
            case "1" =>
                if isConfigFine() == true then
                    server(getPort())
                else
                    println("You need to have a properly configured config.txt file!\nCancelling server launch")
            case "2" =>
                // val file = getFile()
                val ip = getIP()
                val port = getPort()
                try
                    client(ip, port)
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

// def getFile(): Array[String] = {
//     val filepath = browse()
//     val relative = getRelativePath(filepath)
//     Array[String](relative, filepath)
// }

def getIP(): String = {
    val answer = readUserInput("Input the IP to connect to (default: localhost)")
    if answer != "" then
        answer
    else
        "localhost"
}