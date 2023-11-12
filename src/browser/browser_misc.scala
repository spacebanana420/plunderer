package yakumo

import java.io.File
import scala.sys.process.*
import scala.io.StdIn.readLine
import scala.sys.exit

// def separatePaths(paths: Array[String], mode: String, i: Int = 0, finalpaths: List[String] = List[String]()): List[String] = {
//   if i < paths.length-1 then
//     if (File(paths(i)).isFile == true && mode == "file") || (File(paths(i)).isFile == false && mode == "dir") then
//       separatePaths(paths, mode, i+1, finalpaths :+ paths(i))
//     else
//       separatePaths(paths, mode, i+1, finalpaths)
//   else
//     finalpaths
// }

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
  val files = File(path).list().filter(x => File(s"$path/$x").isHidden == false)
  if files.length > 15000 then
    files
  else
    sortPaths(files)
    files
}

private def sortPaths(paths: Array[String], i: Int = 0): Unit = { //seems to be sped up significantly now
  if paths.length != 0 && i < paths.length then
    val lowest = findLowest(paths, i, i)
    //val newpaths = removeLowest(paths, lowest, i)
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

// private def sortPaths(paths: Array[String], sorted: Array[String] = Array(), i: Int = 0): Array[String] = {
//   if paths.length == 0 then
//     sorted
//   else
//     val lowest = findLowest(paths)
//     val newpaths = removeLowest(paths, lowest)
//     sortPaths(newpaths, sorted :+ paths(lowest), i)
// }
//
// private def findLowest(paths: Array[String], i: Int = 0, lowest: Int = 0): Int = {
//   if i >= paths.length then
//     lowest
//   else if paths(i) < paths(lowest) then
//     findLowest(paths, i+1, i)
//   else
//     findLowest(paths, i+1, lowest)
// }
//
// private def removeLowest(paths: Array[String], position: Int, removed: Array[String] = Array(), i: Int = 0): Array[String] = {
//   if i >= paths.length then
//     removed
//   else if i == position then
//     removeLowest(paths, position, removed, i+1)
//   else
//     removeLowest(paths, position, removed :+ paths(i), i+1)
// }


//for reference
private def quickSort(strings: Array[String]): Unit = {
  def swap(i: Int, j: Int): Unit = {
    val temp = strings(i)
    strings(i) = strings(j)
    strings(j) = temp
  }

  def partition(low: Int, high: Int): Int = {
    val pivot = strings(high)
    var i = low - 1

    for (j <- low until high) {
      if (strings(j) < pivot) {
        i += 1
        swap(i, j)
      }
    }

    swap(i + 1, high)
    i + 1
  }

  def sort(low: Int, high: Int): Unit = {
    if (low < high) {
      val pivotIndex = partition(low, high)
      sort(low, pivotIndex - 1)
      sort(pivotIndex + 1, high)
    }
  }

  sort(0, strings.length - 1)
}
