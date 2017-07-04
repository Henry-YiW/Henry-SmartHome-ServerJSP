



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<!-- ViewVC :: http://www.viewvc.org/ -->
<head>
<title>[Apache-SVN] Log of /tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java</title>
<meta name="generator" content="ViewVC 1.1.22" />
<link rel="shortcut icon" href="/viewvc/*docroot*/images/favicon.ico" />
<link rel="stylesheet" href="/viewvc/*docroot*/styles.css" type="text/css" />

</head>
<body>
<div class="vc_navheader">
<table><tr>
<td><strong><a href="/viewvc?view=roots"><span class="pathdiv">/</span></a><a href="/viewvc/">[Apache-SVN]</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/">tomcat</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/">trunk</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/">modules</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/">jdbc-pool</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/">src</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/">main</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/">java</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/">org</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/">apache</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/">tomcat</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/">jdbc</a><span class="pathdiv">/</span><a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/">pool</a><span class="pathdiv">/</span>TrapException.java</strong></td>
<td style="text-align: right;"></td>
</tr></table>
</div>
<div style="float: right; padding: 5px;"><a href="http://www.viewvc.org/" title="ViewVC Home"><img src="/viewvc/*docroot*/images/viewvc-logo.png" alt="ViewVC logotype" width="240" height="70" /></a></div>
<h1>Log of /tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java</h1>

<p style="margin:0;">

<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/"><img src="/viewvc/*docroot*/images/back_small.png" class="vc_icon" alt="Parent Directory" /> Parent Directory</a>

| <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=log"><img src="/viewvc/*docroot*/images/log.png" class="vc_icon" alt="Revision Log" /> Revision Log</a>




</p>

<hr />
<table class="auto">



<tr>
<td>Links to HEAD:</td>
<td>
(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=markup">view</a>)
(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=co">download</a>)

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=annotate">annotate</a>)
</td>
</tr>



<tr>
<td>Sticky Revision:</td>
<td><form method="get" action="/viewvc" style="display: inline">
<div style="display: inline">
<input type="hidden" name="orig_pathtype" value="FILE"/><input type="hidden" name="orig_view" value="log"/><input type="hidden" name="orig_path" value="tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java"/><input type="hidden" name="view" value="redirect_pathrev"/>

<input type="text" name="pathrev" value="" size="6"/>

<input type="submit" value="Set" />
</div>
</form>

</td>
</tr>
</table>
 







<div>
<hr />

<a name="rev1561084"></a>


Revision <a href="/viewvc?view=revision&amp;revision=1561084"><strong>1561084</strong></a> -


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1561084&amp;view=markup">view</a>)


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1561084&amp;view=co">download</a>)

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?annotate=1561084">annotate</a>)



- <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=log&amp;r1=1561084">[select for diffs]</a>




<br />

Modified

<em>Fri Jan 24 17:39:41 2014 UTC</em>
(2 years, 10 months ago)
by <em>markt</em>









<br />File length: 2922 byte(s)







<br />Diff to <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1201493&amp;r2=1561084">previous 1201493</a>

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1201493&amp;r2=1561084&amp;diff_format=h">colored</a>)






<pre class="vc_log">A few more of Filip's author tags
</pre>
</div>



<div>
<hr />

<a name="rev1201493"></a>


Revision <a href="/viewvc?view=revision&amp;revision=1201493"><strong>1201493</strong></a> -


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1201493&amp;view=markup">view</a>)


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1201493&amp;view=co">download</a>)

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?annotate=1201493">annotate</a>)



- <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=log&amp;r1=1201493">[select for diffs]</a>




<br />

Modified

<em>Sun Nov 13 19:50:48 2011 UTC</em>
(5 years, 1 month ago)
by <em>kkolinko</em>









<br />File length: 2940 byte(s)







<br />Diff to <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1201251&amp;r2=1201493">previous 1201251</a>

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1201251&amp;r2=1201493&amp;diff_format=h">colored</a>)






<pre class="vc_log">Improve processing of errors that are wrapped into InvocationTargetException.
TrapException interceptor: Rethrow them as an Error. Do not wrap them into RuntimeException.
</pre>
</div>



<div>
<hr />

<a name="rev1201251"></a>


Revision <a href="/viewvc?view=revision&amp;revision=1201251"><strong>1201251</strong></a> -


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1201251&amp;view=markup">view</a>)


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1201251&amp;view=co">download</a>)

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?annotate=1201251">annotate</a>)



- <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=log&amp;r1=1201251">[select for diffs]</a>




<br />

Modified

<em>Sat Nov 12 12:01:48 2011 UTC</em>
(5 years, 1 month ago)
by <em>kkolinko</em>









<br />File length: 2838 byte(s)







<br />Diff to <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1187811&amp;r2=1201251">previous 1187811</a>

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1187811&amp;r2=1201251&amp;diff_format=h">colored</a>)






<pre class="vc_log">Simplify code: remove unneeded classcast.
</pre>
</div>



<div>
<hr />

<a name="rev1187811"></a>


Revision <a href="/viewvc?view=revision&amp;revision=1187811"><strong>1187811</strong></a> -


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1187811&amp;view=markup">view</a>)


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1187811&amp;view=co">download</a>)

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?annotate=1187811">annotate</a>)



- <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=log&amp;r1=1187811">[select for diffs]</a>




<br />

Modified

<em>Sat Oct 22 21:29:55 2011 UTC</em>
(5 years, 1 month ago)
by <em>markt</em>









<br />File length: 2832 byte(s)







<br />Diff to <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1146994&amp;r2=1187811">previous 1146994</a>

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1146994&amp;r2=1187811&amp;diff_format=h">colored</a>)






<pre class="vc_log">Trailing whitespace removal from /modules/jdbc-pool
</pre>
</div>



<div>
<hr />

<a name="rev1146994"></a>


Revision <a href="/viewvc?view=revision&amp;revision=1146994"><strong>1146994</strong></a> -


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1146994&amp;view=markup">view</a>)


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1146994&amp;view=co">download</a>)

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?annotate=1146994">annotate</a>)



- <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=log&amp;r1=1146994">[select for diffs]</a>




<br />

Modified

<em>Fri Jul 15 07:48:27 2011 UTC</em>
(5 years, 5 months ago)
by <em>rjung</em>









<br />File length: 2857 byte(s)







<br />Diff to <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1088665&amp;r2=1146994">previous 1088665</a>

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1088665&amp;r2=1146994&amp;diff_format=h">colored</a>)






<pre class="vc_log">Missing svn:eol-style.

Please fix your svn config. Thanks.

</pre>
</div>



<div>
<hr />

<a name="rev1088665"></a>


Revision <a href="/viewvc?view=revision&amp;revision=1088665"><strong>1088665</strong></a> -


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1088665&amp;view=markup">view</a>)


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1088665&amp;view=co">download</a>)

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?annotate=1088665">annotate</a>)



- <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=log&amp;r1=1088665">[select for diffs]</a>




<br />

Modified

<em>Mon Apr  4 15:46:10 2011 UTC</em>
(5 years, 8 months ago)
by <em>fhanik</em>









<br />File length: 2936 byte(s)







<br />Diff to <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1087467&amp;r2=1088665">previous 1087467</a>

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?r1=1087467&amp;r2=1088665&amp;diff_format=h">colored</a>)






<pre class="vc_log">incorporate feedback based on 
<a href="http://markmail.org/message/gz7lm5dpdpdgcdzq">http://markmail.org/message/gz7lm5dpdpdgcdzq</a>


</pre>
</div>



<div>
<hr />

<a name="rev1087467"></a>


Revision <a href="/viewvc?view=revision&amp;revision=1087467"><strong>1087467</strong></a> -


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1087467&amp;view=markup">view</a>)


(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?revision=1087467&amp;view=co">download</a>)

(<a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?annotate=1087467">annotate</a>)



- <a href="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java?view=log&amp;r1=1087467">[select for diffs]</a>




<br />

Added

<em>Thu Mar 31 22:28:54 2011 UTC</em>
(5 years, 8 months ago)
by <em>fhanik</em>







<br />File length: 2987 byte(s)











<pre class="vc_log">Implement exception traps as suggested by Eiji Takahashi
<a href="http://markmail.org/message/c7hrhky4jtgcto76">http://markmail.org/message/c7hrhky4jtgcto76</a>

</pre>
</div>

 


 <hr />
<p><a name="diff"></a>
This form allows you to request diffs between any two revisions of this file.
For each of the two "sides" of the diff,

enter a numeric revision.

</p>
<form method="get" action="/viewvc/tomcat/trunk/modules/jdbc-pool/src/main/java/org/apache/tomcat/jdbc/pool/TrapException.java" id="diff_select">
<table cellpadding="2" cellspacing="0" class="auto">
<tr>
<td>&nbsp;</td>
<td>
<input type="hidden" name="view" value="diff"/>
Diffs between

<input type="text" size="12" name="r1"
value="1561084" />

and

<input type="text" size="12" name="r2" value="1087467" />

</td>
</tr>
<tr>
<td>&nbsp;</td>
<td>
Type of Diff should be a
<select name="diff_format" onchange="submit()">
<option value="h" >Colored Diff</option>
<option value="l" >Long Colored Diff</option>
<option value="f" >Full Colored Diff</option>
<option value="u" selected="selected">Unidiff</option>
<option value="c" >Context Diff</option>
<option value="s" >Side by Side</option>
</select>
<input type="submit" value=" Get Diffs " />
</td>
</tr>
</table>
</form>





<hr />
<table>
<tr>
<td><address><a href="mailto:infrastructure at apache.org">infrastructure at apache.org</a></address></td>
<td style="text-align: right;"><strong><a href="/viewvc/*docroot*/help_log.html">ViewVC Help</a></strong></td>
</tr>
<tr>
<td>Powered by <a href="http://viewvc.tigris.org/">ViewVC 1.1.22</a></td>
<td style="text-align: right;">&nbsp;</td>
</tr>
</table>
</body>
</html>


