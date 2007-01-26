

set files [glob Swk*]
foreach file $files {
    puts $file
	set newname Swk[string range $file 3 end]
	puts $newname
#	if {[file exists $newname]} {exec del $newname}
	file copy $file $newname
}
