@echo off

rem swank [ script  [ arg ... ] ]
rem 
rem optional environment variables:
rem
rem JAVA_HOME  - directory of JDK/JRE, if not set then 'java' must be found on PATH
rem CLASSPATH  - colon separated list of additional jar files & class directories
rem JAVA_OPTS  - list of JVM options, e.g. "-Xmx256m -Dfoo=bar"
rem TCLLIBPATH - space separated list of Tcl library directories
rem


if "%OS%" == "Windows_NT" setlocal

set swankver=${project.version}
set swankmain=tcl.lang.SwkShell

set dir=%~dp0

set cp="%dir%\swank-%swankver%.jar;%dir%\lib\jcommon-1.0.16.jar;%dir%\lib\jfreechart-1.0.13.jar;%dir%\lib\javahelp-2.0.05.jar;%CLASSPATH%"

if "%TCLLIBPATH%" == "" goto nullTcllib
set tcllibpath=-DTCLLIBPATH="%TCLLIBPATH%"
:nullTcllib

java %tcllibpath% -cp %cp% %JAVA_OPTS% %swankmain% %*

