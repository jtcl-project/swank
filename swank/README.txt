Contents of directory
  README.txt: this file
  LICENSE: License for Swank and Jacl : these are essentially the BSD license.
  swank.jar: A Java archive file containing the Swank class files, scripts 
     and demo scripts
  jacl.jar and tcljava.jar: Java archive files containing the Jacl class files.
	Note: the jacl.jar and tcljava.jar files contained here are basically the
        same as the 1.4.0 versions, with a fix to elminate a bug in log10.
  tjc.jar and  janino.jar:  Java archive files containing code necessary for the 
        Tcl to Java compiler.  See the normal Jacl 1.4 distribution for details.
  itcl.jar:  Java archive files for ITcl an object oriented library for Tcl.
        See the normal Jacl 1.4 distribution for details.
  wisk: A shell script that will start up the Jacl interpreter with one Swank
        toplevel window.  This is analagous to the "wish" application that 
        comes with Tcl/Tk.  After startup you can type in Jacl/Swank commands
        or source in script files.
	This script assumes that an appropriate version of Java (Swank was
        compiled with Java 1.5) is in your path and you are running the 
        script from the directory containing the swank.jar, jacl.jar and
        tcljava.jar files.
	Alternatively, you can change the following variables in the script to
         contain the appropriate values for your installations.
        The directory containing jacl.jar and tcljava.jar
          JACL_DIR=.

        The directory containing swank.jar
          SWANK_DIR=.

        Fully qualified path name of JVM executable
           JAVA=java

  wisk.bat: A Windows batch file that does the same thing as the above file.
  wiskcon: A shell script that starts up the Jacl interpreter and loads a
           console in which one can execute commands or run demos.
  wiskcon.bat: A Windows batch file that does the same thing as the above file.

The minimal command to start Swank without using the above scripts is:

java -classpath ./swank.jar:./tcljava.jar:./jacl.jar tcl.lang.SwkShell

The above  assumes you are in the directory containing the three jar files
and java is in your path.

If you want to use TJC or ITcl, you must also include the appropriate jar files
   in your classpath.

Jacl supports most Tcl commands.  Documentation for Tcl commands can be found at:
http://www.tcl.tk/man/tcl8.4/TclCmd/contents.htm

Besides the standard Tcl commands Jacl has a variety of unique commands.  
In particular, these commands can allow one to access fields and methods of Java
classes that are not explicitly part of Jacl.  Any public field or method in a
class accessible via the classpath can be used.

Documentation on these Jacl specific commands can be found at:
http://tcljava.sourceforge.net/docs/TclJava/contents.html


Swank supports most Tk commands.  Documentation for Tk commands can be found at:
http://www.tcl.tk/man/tcl8.4/TkCmd/contents.htm

At present not all features of Tk commands are implemented in Swank.  On the other
hand, their are some commands unique to Swank, and most commands that are
common to Tk and Swank have additional options in Swank.  Development of 
documentation describing the similarities and differences of Tk and Swank is
in progress.

To build swank from src you need ant and you need jacl.jar and tcljava.jar installed in directory libs
in the swank distribution (or change the dir.libs parameter in build.xml).

Then type:

ant gensrc
ant



