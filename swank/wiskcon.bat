set SWANK_HOME=.
set JACL_HOME=.
set CLASSPATH=%SWANK_HOME%\swank.jar;%JACL_HOME%\tcljava.jar;%JACL_HOME%\jacl.jar
java -ms32m -mx64m tcl.lang.SwkShell resource:/com/onemoonscientific/swank/library/startconsole.tcl
