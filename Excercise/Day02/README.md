# Scala

https://www.educative.io/blog/scala-101-a-beginners-guide
https://onecompiler.com/scala/435ntrwfh


# example
```
object FizzBuzzBoom {

  def fizzBuzzBoom(n: Int): String = {
    (n % 3, n % 5, n % 15, n % 7) match {
      case (0, 0, _, _) => "FizzBuzz"
      case (0, _, _, _) => "Fizz"
      case (_, 0, _, _) => "Buzz"
      case (_, _, 0, _) => "FizzBuzz" // Handles cases divisible by both 3 and 5, but also 15
      case (_, _, _, 0) => "Boom"
      case _             => n.toString
    }
  }

  def main(args: Array[String]): Unit = {
    for (i <- 1 to 17) {
      println(fizzBuzzBoom(i))
    }
  }
}
```


https://docs.scala-lang.org/cheatsheets/index.html
https://homepage.cs.uiowa.edu/~tinelli/classes/022/Fall13/Notes/scala-quick-reference.pdf

https://landing.skooldio.com/scala-cheat-sheet
https://github.com/Azure-Player/CheatSheets/blob/master/scala-cheat-sheet.pdf


https://www.codewars.com/collections/kata-scala
https://github.com/mwttg-code-katas/code-katas-scala
https://www.codewars.com/kata/search/scala



