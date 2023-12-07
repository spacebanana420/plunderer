package yakumo.browser
import yakumo.*

import java.io.File
import scala.sys.process.*
import scala.io.StdIn.readLine
import scala.sys.exit

def getParentPath(path: String): String = {
  val parent = File(path).getParent()
  if parent != null then
    parent
  else
    path
}

def getRelativePath(fullpath: String): String = {
  var relativepath = ""
  fullpath.foreach(i => if i == '/' || i == '\\' then relativepath = "" else relativepath += i)
  relativepath
}

def getPaths(path: String): Array[String] = {
  val files = File(path)
    .list()
    .filter(x => File(s"$path/$x").isHidden() == false)
  if files.length > 15000 then
    files
  else
    sortPaths(files)
    files
}

private def sortPaths(paths: Array[String], i: Int = 0): Unit = { //seems to be sped up significantly now
  if paths.length != 0 && i < paths.length then
    val lowest = findLowest(paths, i, i)
    if i != lowest then
      val temp = paths(i)
      paths(i) = paths(lowest)
      paths(lowest) = temp
    sortPaths(paths, i+1)
}

private def findLowest(paths: Array[String], i: Int, lowest: Int): Int = {
  if i >= paths.length then
    lowest
  else if paths(i) < paths(lowest) then
    findLowest(paths, i+1, i)
  else
    findLowest(paths, i+1, lowest)
}
