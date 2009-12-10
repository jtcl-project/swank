canvas .c -width 400 -height 600 -bg red
pack .c
.c create rectangle 50 50 100 100 -fill green
set text {<html>
<h1>HTML Text</h1>
This is html text<br> with <b>bold</b> and <i>italics</i><br>
And here is a <sup>15</sup>N superscript
<br>
This is a table
<table>
<tr>
<td>1.34</td>
<td>2.45</td>
</tr>
<tr>
<td>5.01</td>
<td>20.97</td>
</tr>
</table>
</html>
}
.c create htext 20 100 200 500  -text $text  -font "Helvetic 36" -fill yellow  -outline green
.c bind 1 <1> "puts howdy"
.c hselect 1 1

vwait done
