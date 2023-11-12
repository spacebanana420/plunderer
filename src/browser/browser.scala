package yakumo

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
        else if answernum - 2 - paths(0).length <= paths(1).length-1 then
          s"${basedir}/${paths(1)(answernum - 2 - paths(0).length)}"
        else
          basedir
      chosenpath
    catch
      case e: Exception => basedir
  else
    println("Closing client...") //maybe add message for server to shut down??
    exit()
}

def fileBrowser(basedir: String): Array[Array[String]] = {
  val green = foreground("green")
  val red = foreground("red")
  val default = foreground("default")
  val linewidth = 2

  var browserScreen = s"$basedir\n\n${red}0:$default Exit   ${green}1:$default Go back\n\n---Directories---\n"
  val paths = getPaths(basedir)

  val dirs = paths.filter(x => File(s"${basedir}/${x}").isFile() == false)
  val files = paths.filter(x => File(s"${basedir}/${x}").isFile() == true)
  var pathsAdded = 0
  var pathNum = 2
  for dir <- dirs do {
    if pathsAdded < linewidth then
      browserScreen ++= s"$green${pathNum}:$default ${dir}     "
      pathsAdded += 1
    else
      browserScreen ++= s"$green${pathNum}:$default ${dir}\n"
      pathsAdded = 0
    pathNum += 1
  }
  pathsAdded = 0
  browserScreen ++= "\n---Files---\n"
  for file <- files do {
    if pathsAdded < linewidth then
      browserScreen ++= s"$green${pathNum}:$default ${file}     "
      pathsAdded += 1
    else
      browserScreen ++= s"$green${pathNum}:$default ${file}\n"
      pathsAdded = 0
    pathNum += 1
  }
  browserScreen ++= "\nPick a file to send or navigate through the filesystem"
  println(browserScreen)
  Array(dirs, files)
}
