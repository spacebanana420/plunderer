package yakumo

import java.io.File
import scala.sys.process.*

//ANSI escape codes

def readUserInput(message: String = ""): String = {
    if message != "" then
        println(message)
    scala.io.StdIn.readLine()
}

// def clear() = { //test windows support
//     if File("C:").isDirectory == false then
//         List[String]("clear").!
//     else
//         List[String]("cmd", "/c", "cls").!
// }

def spawnScreen(ui: String) = {
    //println(s"\u001B[1J\u001B[H$ui")
    println(s"\u001B[H\u001B[0J$ui")
}

def clear() = { //cross platform solution smiley face? needs to be tested
    print("\u001B[H\u001B[2J")
    
}


def foreground(color: String = "default"): String = {
    //val acceptedValues = List[String]("reset", "black", "")
    color match
        case "black" => "\u001B[30m"
        case "red" => "\u001B[31m"
        case "green" => "\u001B[32m"
        case "yellow" => "\u001B[33m"
        case "blue" => "\u001B[34m"
        case "magenta" => "\u001B[35m"
        case "cyan" => "\u001B[36m"
        case "white" => "\u001B[37m"
        case "default" => "\u001B[39m"
        case "reset" => "\u001B[0m"
        case _ => "\u001B[39m"
}

def background(color: String = "default"): String = {
    //val acceptedValues = List[String]("reset", "black", "")
    color match
        case "black" => "\u001B[40m"
        case "red" => "\u001B[41m"
        case "green" => "\u001B[42m"
        case "yellow" => "\u001B[43m"
        case "blue" => "\u001B[44m"
        case "magenta" => "\u001B[45m"
        case "cyan" => "\u001B[46m"
        case "white" => "\u001B[47m"
        case "default" => "\u001B[49m"
        case "reset" => "\u001B[0m"
        case _ => "\u001B[49m"
}