Swank - Tcl Language Interpreter in Java

http://jtcl.kenai.com

Swank is an implementation of the Tk Graphical User Interface
Toolkit written in Java.
Swank implements a large extent of Tk 8.4 syntax and commands,
but is not an exact replacement for Tk.  Some commands and
configuration options are different, and there are some
additional widgets provided.


INSTALLING SWANK

Swank requires a Java JVM 1.5 or higher (Java 1.6 is recommended).  You may 
have to install Java if you don't already have it installed.  

Swank is distributed as a ZIP archive with a jar file
containing Swank, JTcl (the Tcl interpreter written in Java),
and much of the tcllib library of utilities written in Tcl.
In addition the zip file contains the binary jar files for the
JFreeChart charting library and JavaHelp.  Both of these
are accessible through builtin commands in Swank.

Download the latest swank-{version}.zip file from the Swank website, 
and unzip into a directory of your choice.  
Unix/Linux/Mac OSX shell script (`wisk') and Windows batch file (`wisk.bat') 
are included.   

Start the wisk script with the "-demo" argument to see a demo of
some of the Swank widgets, or with the "-swkcon" argument to start up
with Swank's version of the TkCon console.

SOURCE CODE

Swank uses the Mercurial distributed source code control system.  Install
Mercurial using your OS distribution package manager or get executables 
from the Mercurial site:  http://mercurial.selenic.com/

Browse Swank source code:   http://kenai.com/projects/swank/sources
Clone the Swank repository: hg clone https://hg.kenai.com/hg/swank~swank-main 


DOCUMENTATION

Please refer to the Swank web site.


MAILING LIST and BUG REPORTING

Please subscribe and use the Swank mailing list for questions and to
share information.  The Swank Bugzilla bug tracker should be used to
report bugs.

Mailing lists: http://kenai.com/projects/swank/lists
Bug reporting: http://kenai.com/jira/browse/SWANK

LICENSES

This Swank distribution includes JTcl and tcllib code bundled into the swank jar file.  The
Swank code and bundled code is subject to the licenses described in the license files
in this directory.

The lib subdirectory includes code from the JFreeChart project which is subject to the
LPGL license, a copy of which is placed together with the jar files in the lib directory.

The lib subdirectory includes JavaHelp code available from Sun (now Oracle).  This is
subject to a Sun License (copy in the lib directory).  Take special note of the 
JavaHelp Supplemental License.

**The Swank Team**
