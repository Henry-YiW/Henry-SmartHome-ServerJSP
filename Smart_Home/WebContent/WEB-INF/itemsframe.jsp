<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.util.Map"
		 import="java.util.HashMap"
		 import="java.util.Map.Entry"%>
    <%
    Map <String,String> formaldebugstatus = new HashMap <>(7);
    Map <String,Boolean> debugstatus = (Map <String,Boolean>)request.getAttribute("debugstatus");
    //String tempuser = (String)(request.getAttribute("user")); String temppass = (String)(request.getAttribute("pass"));
    //out.println(tempuser); out.println(temppass);
    if (debugstatus==null){
    	request.getRequestDispatcher("/debugconfig?Refresh=true").forward(request,response);
    	//response.sendRedirect("/debugconfig?Refresh=true");
    }else {
   		for (Entry <String,Boolean> temp:debugstatus.entrySet()){
   			//out.println(temp.getKey()+": "+temp.getValue());
   			formaldebugstatus.put(temp.getKey(), (temp.getValue()==true)? "checked=\"true\"":"");
   			//out.println(formaldebugstatus.get(temp.getKey()));
   		}
   		
    }
    //checked=${debugstatus.get('PassiveApparatusFetch')}
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv=content-type content="text/html;charset=utf-8">
        <!--<script src="jquery-1.10.1.min.js"></script>-->
    </head>
    
    <body>
    
    <table width="700" border="1" align="center" style="border-collapse:collapse;border-spacing:0;border-left:1px solid #888;border-top:1px solid #888;background:#c6ff55;">
      <tbody align="center">
        
        <tr height="25">
          <td width="342" >
          <label for="a1" title="PassiveApparatusFetch" id="b1" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">PassiveApparatusFetch:</label>
          <input type="checkbox" id="a1" <%=formaldebugstatus.get("PassiveApparatusFetch") %> onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b1,this)"> 
          
          </td>
          <td width="342" >
          <label for="a2" title="PassiveDataFetch" id="b2" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" >PassiveDataFetch:</label>
          <input type="checkbox" id="a2"  <%=formaldebugstatus.get("PassiveDataFetch") %> onmouseout="window.parent.mouseUO()"  onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b2,this)"> 
          </td>
          
        </tr>
        <tr height="25">
          <td >
          <label for="a3" title="DataInquire" id="b3" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">DataInquire:</label>
          <input type="checkbox" id="a3" <%=formaldebugstatus.get("DataInquire") %> onmouseout="window.parent.mouseUO()"  onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b3,this)"> 
          </td>
          <td >
          <label for="a4" title="DataUpdate" id="b4" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">DataUpdate:</label>
          <input type="checkbox" id="a4" <%=formaldebugstatus.get("DataUpdate") %> onmouseout="window.parent.mouseUO()"  onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b4,this)"> 
          </td>
        </tr>
        <tr height="25">
          <td >
          <label for="a5" title="ApparatusInquire" id="b5" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">ApparatusInquire:</label>
          <input type="checkbox" id="a5" <%=formaldebugstatus.get("ApparatusInquire") %>  onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b5,this)"> 
          </td>
          <td  colspan="3">
          <label for="a6" title="ApparatusUpdate" id="b6" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">ApparatusUpdate:</label>
          <input type="checkbox" id="a6" <%=formaldebugstatus.get("ApparatusUpdate") %>  onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b6,this)"> 
          </td>
        </tr>
        <tr height="25">
          <td >
          <label for="a7" title="Control" id="b7" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">Control:</label>
          <input type="checkbox" id="a7" <%=formaldebugstatus.get("Control") %> onmouseout="window.parent.mouseUO()"  onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b7,this)"> 
          </td>
          <td >
          <label for="a8" title="ConfigurationsInquire" id="b8" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">ConfigurationsInquire:</label>
          <input type="checkbox" id="a8" <%=formaldebugstatus.get("ConfigurationsInquire") %> onmouseout="window.parent.mouseUO()"  onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b8,this)"> 
          </td>
        </tr>
        <tr height="25">
          <td >
          <label for="a9" title="RegulationsInquire" id="b9" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">RegulationsInquire:</label>
          <input type="checkbox" id="a9" <%=formaldebugstatus.get("RegulationsInquire") %> onmouseout="window.parent.mouseUO()"  onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b9,this)"> 
          </td>
          <td >
          <label for="a10" title="RegulationsSet" id="b10" onmouseout="window.parent.mouseUO()" onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()">RegulationsSet:</label>
          <input type="checkbox" id="a10" <%=formaldebugstatus.get("RegulationsSet") %> onmouseout="window.parent.mouseUO()"  onmouseup="window.parent.mouseUO()" onmousedown="window.parent.mouseD()" onClick="window.parent.on2click(b10,this)"> 
          </td>
        </tr>

      </tbody>
      </table>
    	<form id="subform" target="hidden_frame" style="display:none" method="post">
    		<input type="password" name="pass" id="temppass">
        	<input type="text" name="user" id="tempuser">
    	</form>
    	<form id="subform2" target="_self" style="display:none" method="post"></form>
    	<form id="subform3" target="_self" style="display:none" method="post">
        	
        </form>
    	
</body>
 

</html>

