package yakumo

import java.io.File
import java.io.FileOutputStream
import scala.sys.exit

@main def main() = {
    val cyan = foreground("cyan")
    val default = foreground("default")
    if File("config.txt").isFile() == false then
        createConfig()
    val mode = readUserInput(s"$cyan[Yakumo v0.3]\n$default--Choose an option--\n0: Exit   1: Server   2: Client\n")
    while true do {
        mode match
            case "0" => exit()
            case "1" =>
                if isConfigFine() == true then
                    server(getPort())
                else
                    println("You need to have a properly configured config.txt file!\nCancelling server launch")
                    exit()
            case "2" =>
                // val file = getFile()
                val ip = getIP()
                val port = getPort()
                try
                    client(ip, port)
                catch
                    case e: Exception => readUserInput("Connection failed!\nMaybe the server isn't open?\n\nPress enter to continue")
            case _ => exit()
    }
}

def getPort(): Int = {
    val yellow = foreground("yellow")
    val default = foreground("default")
    val portstr = readUserInput(s"Type the port to use\nDefault: ${yellow}42069${default}\n")
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

// def getDir(): String = {
//     val default = File("").getAbsolutePath()
//     val answer = readUserInput(s"Choose the server's storage directory\nDefault: $default")
//     if File(answer).isDirectory() == false then
//         println(s"The directory $answer does not exist! Using default directory")
//         default
//     else
//         answer
// }

def getIP(): String = {
    val yellow = foreground("yellow")
    val default = foreground("default")
    val answer = readUserInput(s"Input the IP to connect to (default: ${yellow}localhost${default})")
    if answer != "" then
        answer
    else
        "localhost"
}
