# installation

https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html
https://www.scala-sbt.org/1.x/docs/Setup.html

Create hello world

# run the command
sbt new sbt/scala-seed.g8

dotnet new console

# First build error
[error] (Compile / compileIncremental) Error compiling the sbt component 'compiler-bridge_2.12'

https://stackoverflow.com/a/20405679

Or for Java 11:

Bash

sdk install java 11.0.20-tem
sdk default java 11.0.20-tem
Or for Java 8:

Bash

sdk install java 8.0.382-tem
sdk default java 8.0.382-tem

# Fix
sdk home java 11.0.20-tem
export JAVA_HOME="~/.sdkman/candidates/java/current"

# Yes!

[info] Non-compiled module 'compiler-bridge_2.12' for Scala 2.12.8. Compiling...
[info]   Compilation completed in 3.892s.
[info] running example.Hello
hello
[success] Total time: 4 s, completed Jan 9, 2025, 10:20:10 PM
sbt:hello>


# .gitignore
https://github.com/scala/scala/blob/2.13.x/.gitignore