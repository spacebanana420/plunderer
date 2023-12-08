package yakumo.browser
import yakumo.*

import java.io.File
import scala.sys.process.*
import scala.io.StdIn.readLine
import scala.sys.exit

def browse(): String = {
  def checkAnswer(n: Int, basedir: String, paths: Array[String]): Byte =
    if n == 0 then
      0
    else if n == 1 then
      1
    else if File(s"${basedir}/${paths(n-2)}").isFile() then
      2
    else if File(s"${basedir}/${paths(n-2)}").isDirectory() then
      3
    else
      -1

  def browseLoop(basedir: String): String =
    val paths = browsedir(basedir)
    val answer = readLine()
    if browserCommand(answer, basedir) == true then
      browseLoop(basedir)
    else if answer == "" then
      basedir
    else
      try
        val n = answer.toInt
        checkAnswer(n, basedir, paths) match
          case 0 => "!cancelled!"
          case 1 => browseLoop(getParentPath(basedir))
          case 2 => s"${basedir}/${paths(n-2)}"
          case 3 => browseLoop(s"${basedir}/${paths(n-2)}")
          case _ => browseLoop(basedir)
      catch
        case e: Exception => browseLoop(basedir)

  val chosenfile = browseLoop(File("").getAbsolutePath())
  if chosenfile != "!cancelled!" then
    println(s"Selected ${chosenfile}")
  chosenfile
}

def getspace(len: Int, limit: Int, space: String = "     ", i: Int = 0): String =
  if i+len >= limit then
    space
  else
    getspace(len, limit, space + " ", i+1)

def shortenName(n: String, limit: Int, n2: String = "", i: Int = 0): String =
  if i >= n.length || i >= limit-1 then
    n2 + "[...]"
  else
    shortenName(n, limit, n2 + n(i), i+1)

def browsedir(basedir: String): Array[String] = { //test
  val green = foreground("green")
  val red = foreground("red")
  val default = foreground("default")

  def addtoscreen(elements: Array[String], pathNum: Int, screen: String = "", i: Int = 0, added: Int = 0): String =
    if i >= elements.length then
      screen
    else
      val name =
        if elements(i).length >= 25 then
          shortenName(elements(i), 25)
        else
          elements(i)
      if added < 2 then
        val newstr = s"$green${pathNum}:$default $name${getspace(name.length, 24)}"
        addtoscreen(elements, pathNum + 1, screen + newstr, i+1, added+1)
      else
        val newstr = s"$green${pathNum}:$default $name\n"
        addtoscreen(elements, pathNum + 1, screen + newstr, i+1, 0)

  val paths = getPaths(basedir)
  val dirs = paths.filter(x => File(s"${basedir}/${x}").isFile() == false)
  val files = paths.filter(x => File(s"${basedir}/${x}").isFile() == true)

  val browserScreen =
    s"$basedir\n\n${red}0:$default Exit   ${green}1:$default Go back\n\n---Directories---\n"
    + addtoscreen(dirs, 2)
    + "\n---Files---\n"
    + addtoscreen(files, 2 + dirs.length)
    + "\n\n* Pick a file to send or navigate through the filesystem\n* Press enter without prompting anything to upload the whole current directory's files\n* Type \"help\" to see the list of commands\n"

  clear()
  println(browserScreen)
  dirs ++ files //this shit makes sure all dirs come before the files to fix a bug
}

def browser_seek(basedir: String, seek: String) = { //test
  val green = foreground("green")
  val red = foreground("red")
  val default = foreground("default")

  def addtoscreen(elements: Array[String], pathNum: Int, screen: String = "", i: Int = 0, added: Int = 0): String = {
    if i >= elements.length then
      screen
    else if elements(i).contains(seek) == true then
      val name =
        if elements(i).length >= 40 then
          shortenName(elements(i), 40)
        else
          elements(i)
      if added < 1 then
        val newstr = s"$green${pathNum}:$default $name${getspace(name.length, 45, "        ")}"
        addtoscreen(elements, pathNum + 1, screen + newstr, i+1, added+1)
      else
        val newstr = s"$green${pathNum}:$default $name\n"
        addtoscreen(elements, pathNum + 1, screen + newstr, i+1, 0)
    else
        addtoscreen(elements, pathNum + 1, screen, i+1, added)
  }

  val paths = getPaths(basedir)
  val dirs = paths.filter(x => File(s"${basedir}/${x}").isFile() == false)
  val files = paths.filter(x => File(s"${basedir}/${x}").isFile() == true)

  val browserScreen =
    s"$basedir\n\n---Directories---\n"
    + addtoscreen(dirs, 2)
    + "\n---Files---\n"
    + addtoscreen(files, 2 + dirs.length)
    + s"\n\nThe following entries were found\n${green}Press enter to continue${default}"

  clear()
  readUserInput(browserScreen)
}
