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
        <script src="jquery-1.10.1.min.js"></script>
    </head>
    
    <body>
    
    <table width="700" border="1" align="center" style="border-collapse:collapse;border-spacing:0;border-left:1px solid #888;border-top:1px solid #888;background:#d4ff7e;">
      <tbody align="center">
        
        <tr>
          <td  colspan="3">
          <label for="a1" title="PassiveApparatusFetch" id="b1">PassiveApparatusFetch:</label>
          <input type="checkbox" id="a1" <%=formaldebugstatus.get("PassiveApparatusFetch") %>  onClick="on2click('b1',this)"> 
          
          </td>
          <td colspan="3">
          <label for="a2" title="PassiveDataFetch" id="b2">PassiveDataFetch:</label>
          <input type="checkbox" id="a2"  <%=formaldebugstatus.get("PassiveDataFetch") %>  onClick="on2click('b2',this)"> 
          </td>
          
        </tr>
        <tr>
          <td  colspan="3">
          <label for="a3" title="DataInquire" id="b3">DataInquire:</label>
          <input type="checkbox" id="a3" <%=formaldebugstatus.get("DataInquire") %>  onClick="on2click('b3',this)"> 
          </td>
          <td  colspan="3">
          <label for="a4" title="DataUpdate" id="b4">DataUpdate:</label>
          <input type="checkbox" id="a4" <%=formaldebugstatus.get("DataUpdate") %>  onClick="on2click('b4',this)"> 
          </td>
        </tr>
        <tr>
          <td  colspan="3">
          <label for="a5" title="ApparatusInquire" id="b5">ApparatusInquire:</label>
          <input type="checkbox" id="a5" <%=formaldebugstatus.get("ApparatusInquire") %>  onClick="on2click('b5',this)"> 
          </td>
          <td  colspan="3">
          <label for="a6" title="ApparatusUpdate" id="b6">ApparatusUpdate:</label>
          <input type="checkbox" id="a6" <%=formaldebugstatus.get("ApparatusUpdate") %>  onClick="on2click('b6',this)"> 
          </td>
        </tr>
        <tr>
          <td  colspan="3">
          <label for="a7" title="Control" id="b7">Control:</label>
          <input type="checkbox" id="a7" <%=formaldebugstatus.get("Control") %>  onClick="on2click('b7',this)"> 
          </td>
          <td  colspan="3">
          
          </td>
        </tr>
        <tr >
          <td  colspan="2">
            <label for="pass">PASSWORD:</label>
    		<input type="password" name="password" id="pass" value="${pass}" > </td>
          <td  colspan="2">
            <label for="user">USER:</label>
            <input type="text" name="user" id="user" value="${user}" ></td>
          <td height="50" colspan="2" align="right">
          	<iframe id="id_frame" name="hidden_frame" style="display:block;width:233px;height:inherit" ></iframe></td>
        </tr>
      </tbody>
      </table>
    	<form id="subform" target="hidden_frame"></form>
    	<form id="subform2" target="_self"></form>
    	<form id="subform3" target="_self" style="display:none" method="post">
        	<input type="password" name="pass" id="temppass">
        	<input type="text" name="user" id="tempuser">
        </form>
    	<br>
        <div align="center">
        <input type="button" name="Refresh" id="Refresh" title="Refresh"  value="Refresh" onClick="refresh3 ()">
        </div>
</body>
 
<script>
            
            function sub(dff){
                var content = document.getElementById("user");
                //alert(content.value);
				var temp= dff.value;
				alert(temp);
				
            }
			
            function refresh3(){
				$('#temppass').val($('#pass').val());
				$('#tempuser').val($('#user').val());
				$('#subform3').attr('action',"./debugconfig?Refresh=true");
				$('#subform3').submit();
					
			}
            
			function refresh2(){
				$('#subform2').attr("method","post");
				$('#subform2').attr('action',"./debugconfig?user="+$("#user").val()+"&pass="+$("#pass").val()+"&Refresh=true");
				$('#subform2').submit();
					
			}
			
			function on2click (id,obj){
				//alert (id);
				dom=document.getElementById(id);
				submitform (dom,obj);
			}
			
			function submitform (lable,checkbox){
				
				var temp= lable.title;
				var temp2=checkbox.checked;
				var user=$("#user").val();
				
				//var pass;
				//alert($("#user").val());
				//user = document.getElementById("user");
				//alert (user);
				//alert(temp+temp2);
				$('#subform').attr("method","post");
				$('#subform').attr('action',"./debugconfig?user="+$("#user").val()+"&pass="+$("#pass").val()+"&Type="+temp+"&activated="+temp2);
				$('#subform').submit();
			}
            
			setTimeout('refresh3 ()',5000);
			
    </script>
</html>

