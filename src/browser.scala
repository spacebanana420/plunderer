package plunderer

import java.io.File
import scala.sys.process.*
import scala.io.StdIn.readLine
import scala.sys.exit

def browse(): String = {
    var chosenpath = File("").getAbsolutePath()
    var chosenfile = ""
    while chosenfile == "" do {
        if File(chosenpath).isFile == false then
            chosenpath = browseLoop(chosenpath)
        else
            chosenfile = chosenpath
    }
    println(s"Selected ${chosenfile}")
    chosenfile
}

def browseLoop(basedir: String): String = {
    clear()
    val paths = fileBrowser(basedir)
    val answer = readLine()
    if answer != "0" then
        try
            val answernum = answer.toInt
            val chosenpath = //needs fix
                if answernum == 1 then
                    getParentPath(basedir)
                else if answernum - 2 <= paths(0).length-1 then
                    s"${basedir}/${paths(0)(answernum - 2)}"
                else if answernum - 2 - paths(0).length -1 <= paths(1).length-1 then
                    s"${basedir}/${paths(1)(answernum - 2 - paths(0).length-1)}"
                else
                    basedir
            chosenpath
        catch
            case e: Exception => basedir
    else
        println("Closing client...")
        exit()
}

def fileBrowser(basedir: String): Array[Array[String]] = {
    var browserScreen = s"$basedir\n\n0: Exit     1: Go back\n\n---Directories---\n"
    val paths = File(basedir).list()

    val dirs = paths.filter(x => File(s"${basedir}/${x}").isFile() == false)
    val files = paths.filter(x => File(s"${basedir}/${x}").isFile() == true)
    var pathsAdded = 0
    var pathNum = 2
    for dir <- dirs do {
        if pathsAdded < 3 then
            browserScreen ++= s"${pathNum}: ${dir}     "
            pathsAdded += 1
        else
            browserScreen ++= s"${pathNum}: ${dir}\n"
            pathsAdded = 0
        pathNum += 1
    }
    pathsAdded = 0
    browserScreen ++= "\n---Files---\n"
    for file <- files do {
        if pathsAdded < 3 then
            browserScreen ++= s"${pathNum}: ${file}     "
            pathsAdded += 1
        else
            browserScreen ++= s"${pathNum}: ${file}\n"
            pathsAdded = 0
        pathNum += 1
    }
    browserScreen ++= "\nPick a file to send or navigate through the filesystem"
    println(browserScreen)
    Array(dirs, files)
}

// def separatePaths(paths: Array[String], mode: String, i: Int = 0, finalpaths: List[String] = List[String]()): List[String] = {
//     if i < paths.length-1 then
//         if (File(paths(i)).isFile == true && mode == "file") || (File(paths(i)).isFile == false && mode == "dir") then
//             separatePaths(paths, mode, i+1, finalpaths :+ paths(i))
//         else
//             separatePaths(paths, mode, i+1, finalpaths)
//     else
//         finalpaths
// }

def getParentPath(path: String): String = {
    try
        File(path).getParent()
    catch
        case e: Exception => path
}

def getRelativePath(fullpath: String): String = {
    var relativepath = ""
    fullpath.foreach(i => if i == '/' || i == '\\' then relativepath = "" else relativepath += i)
    relativepath
}

// def indexFromList(paths: List[String], answer: Int, i: Int = 0): Int = {
//     if answer != i + 2 then
//         indexFromList(paths, answer, i+1)
// }
