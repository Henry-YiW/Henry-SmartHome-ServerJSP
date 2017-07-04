<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
    <head>
        <meta http-equiv=content-type content="text/html;charset=utf-8">
        <title>DebugConfig<%=(request.getAttribute("ContextName")!=null)? request.getAttribute("ContextName"):""%></title>
        <!--<script src="jquery-1.10.1.min.js"></script>-->
    </head>
    
    <body>
    <div align="center" id="item_div">
    <iframe name="item_frame" id="item_frame" style="border:0px;padding:0px" scrolling="no" width="800"  src="./debugconfig?Refresh=true" onLoad="initiateframe(this,0)">
    
    </iframe>
    </div>
    
    <table width="700" border="1" align="center" style="border-collapse:collapse;border-spacing:0;border-left:1px solid #888;border-top:1px solid #888;background:#d4ff7e;">
      
      <tbody align="center">
      	<tr >
          <td  colspan="2">
            <label for="pass">PASSWORD:</label>
    		<input type="password" name="password" id="pass" value="${pass}"> </td>
          <td  colspan="2">
            <label for="user">USER:</label>
            <input type="text" name="user" id="user" value="${user}"></td>
          <td height="50" colspan="2" align="right">
          	<iframe id="id_frame" name="hidden_frame" style="display:block;width:233px;height:inherit;padding:0px;border:0px;" onLoad="initiateframe2(this)">
          	</iframe>
          	</td>
        </tr>
      </tbody>
      </table>
    	<form id="subform" target="hidden_frame" style="display:none" method="post">
    		<input type="password" name="pass" id="temppass">
        	<input type="text" name="user" id="tempuser"> </form>
    	<form id="subform2" target="_self" style="display:none" method="post"></form>
    	<form id="subform3" target="item_frame" style="display:none" method="post"></form>
        
        
</body>
 
<script>
			var mousedown=false;
			var updating = true;
			function mouseD(){
				mousedown=true;
			}
			
			function mouseUO(){
				if (mousedown){
					mousedown=false;
					if(!updating){
						
						updating=true;
						//refresh3();
						setTimeout("refresh3()", 1000);
					}
				}
			}
			
			var XMLHttpReq; 
			//创建XMLHttpRequest对象 
			function createXMLHttpRequest() { 
				if(window.XMLHttpRequest) { //Mozilla 浏览器 
					XMLHttpReq = new XMLHttpRequest(); 
				} 
				else if (window.ActiveXObject) { // IE浏览器 
					try { 
						XMLHttpReq = new ActiveXObject("Msxml2.XMLHTTP"); 
					} catch (e) { 
						try { 
							XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP"); 
						} catch (e) {} 
					}
				} 
			}

			function initiateframe2 (iframe){
				//alert("hello");
				var body = iframe.contentWindow.document.body;
				//body.style.width="100%";
				body.style.margin="5px";
				body.style.marginTop="2px";
				body.style.padding="0px";
				body.style.overflowX="hidden";
				body.style.wordBreak = "break-all";
				//body.style.word-wrap="break-word";
				//var html = iframe.contentWindow.document.html;
				//html.width="80";
				//alert(iframe.height);
	  
			}

			function initiateframe (iframe,margin){
				//var iframe = document.getElementById("item_frame");
				//alert("hello");
	    			var bHeight = iframe.contentWindow.document.body.scrollHeight;
	    			var dHeight = iframe.contentWindow.document.documentElement.scrollHeight;
	    			var height = Math.min(bHeight, dHeight);
	    			iframe.height = height-9+margin;
	    			
	    			
	    			//alert(iframe.height);
				  
			}
			
			function iFrameHeight() {
		        var ifm= document.getElementById("item_frame");
		        var subWeb = document.frames ? document.frames["item_frame"].document:ifm.contentDocument;
		            if(ifm != null && subWeb != null) {
		            ifm.height = subWeb.body.scrollHeight;
		            alert(ifm.height);
		            }
		    }
            
			
            function refresh3(){
            	
				//$('#temppass').val($('#pass').val());
				//$('#tempuser').val($('#user').val());
				//$('#subform3').attr('action',"./debugconfig?Refresh=true");
				//$('#subform3').submit();
				//var url = "./debugconfig?Refresh="+new Date().getTime();
				//createXMLHttpRequest();
				var url="./debugconfig?Refresh=true";
				//var arg="user="+user.value+"&pass="+pass.value;
				var arg="";
				XMLHttpReq.open("Post", url, true); 
				XMLHttpReq.onreadystatechange = processResponse;//指定响应函数 
				//////XMLHttpReq.setRequestHeader("Content-Length",arg.length);  
				XMLHttpReq.setRequestHeader("Content-Type","application/x-www-form-urlencoded;"); 
				XMLHttpReq.send(arg); // 发送请求 
				
            	////$.ajax({
            	////       type: "post",
            	////       dataType: "html",
            	////       url: './debugconfig?Refresh=true',
            	////       data: arg,
            	////       success: function (data) {
            	////           if (data != "") {
            	////              //$("#item_div").html(unescape(data));
            	////              document.getElementById("item_frame").contentWindow.document.body.innerHTML=unescape(data);
            	////           }
            	////       },
            	////       //success: function(data, textStatus){
            	////			
            	////		//},
            	////		complete: function(XMLHttpRequest, textStatus){
            	////			setTimeout("refresh3()", 1500); 
            	////		},
            	////		error: function(){
            	////			//请求出错处理
            	////		}
            	////        
            	////    });
			}
            
            function processResponse() { 
            	//if (!mousedown){ //因为随着readyState的变化，这个方法会在一个请求过程中被调用4此（也就是readyState从1-4），
            		//从而导致在一个请求中可能多次让updating为false从而导致在鼠标out或up的时候，生成新的Timeout来调用新的refresh3();导致之后会有多个循环请求的发生
            		if (XMLHttpReq.readyState == 4) { // 判断请求对象是否已经读取服务器响应结束。
            			 if (XMLHttpReq.status == 200){//response状态码为200， 表示一切正常成功
            				//document.getElementById("item_frame").contentWindow.document.body.innerHTML=unescape(XMLHttpReq.responseText);
            				//看起来 item_frame. 是 document.getElementById("item_frame").contentWindow.
            				//而且似乎 直接用id名称来引用不同的对象代表着不同的东西。比如iframe的id名称直接引用就是等于document.getElementById("id").contentWindow
            				//而一般的比如checkbox或label的id名称直接引用就是等于document.getElementById("id")
            				if (!mousedown){
            					item_frame.document.head.innerHTML="<meta http-equiv=content-type content=\"text/html;charset=utf-8\">";
            					item_frame.document.body.innerHTML=unescape(XMLHttpReq.responseText);
            					//item_frame.document.write("");//框架的document.write只是写body的内容。
            					//item_frame.document.write(unescape(XMLHttpReq.responseText));
            					initiateframe(document.getElementById("item_frame"),0);
            				}else{
            					updating=false;
            					return;
            				}
            				//alert(unescape(XMLHttpReq.responseText));
            			}else{//response处于其他状态。
            				//item_frame.document.getElementsByTagName('head')[0].innerHTML="";
            				item_frame.document.head.innerHTML="<meta http-equiv=content-type content=\"text/html;charset=utf-8\">";
            				item_frame.document.body.innerHTML=unescape(" <style>p{color:#FF7500;}k{color:#348DEB}</style><div align=\"center\" style=\"font-family:verdana;font-weight:bold;\"> <p>Response Status is <span style=\"color:#FF0000\">Erroneous.</span> The Status is  <span style=\"color:red\">["+XMLHttpReq.status+"]</span></p> <k>Maybe It was Caused by <span style=\"color:#111111\">Disconnection with the Server,</span> Or <span style=\"color:#dc28bb\">Misconfigurations in the Servlets.</span></k> </div>");
            				initiateframe(document.getElementById("item_frame"),20);
            			}
            			if (!mousedown){
            			 	setTimeout("refresh3()", 1500);
            			}else{
            				updating=false;
            				return;
            			}
            		} else { //请求对象还没有读取完服务器的响应。
            		//alert("Anomaly"); 
            		} 
            	//}else {
            	//	updating=false;
            	//	return;
            	//}
            } 
            
			function refresh2(){
				
				$('#subform2').attr("method","post");
				$('#subform2').attr('action',"./debugconfig?user="+$("#user").val()+"&pass="+$("#pass").val()+"&Refresh=true");
				$('#subform2').submit();
					
			}
			
			//window.onload=createXMLHttpRequest;
			
			//var id=setInterval('refresh3 ()',8000);
			setTimeout("createXMLHttpRequest()", 1500); 
			setTimeout("refresh3()", 1500); 
			
			function on2click (id,obj){
				//alert (id);
				//dom=document.getElementById(id);
				//var lable = $("#"+id,item_frame.document);
				//var lable = $(id);
				//var checkbox=$(obj);
				var lable = id;
				var checkbox=obj;
				//submitform (dom,obj);
				submitform (lable,checkbox);
			}
			
			function submitform (lable,checkbox){
				var temp= lable.title;
				var temp2=checkbox.checked;
				//var temp= lable.attr("title");
				//var temp2= checkbox.is(":checked");
				//alert (temp2);
				//var user2=$('#user',window.parent.document).val();
				//user=window.parent.document.getElementById('user');
				//pass=window.parent.document.getElementById('pass');
				
				//$('#temppass').val(pass.value);
				//$('#temppass').val($('#pass',window.parent.document).val());
				//$('#tempuser').val($('#user',window.parent.document).val());
				//$('#subform').attr("method","post");
				//$('#subform').attr('action',"./debugconfig?Type="+temp+"&activated="+temp2);
				//$('#subform').submit();
				
				temppass.value=pass.value;
				tempuser.value=user.value;
				subform.method="post";
				subform.action="./debugconfig?Type="+temp+"&activated="+temp2;
				subform.submit();
			}
            
    </script>
</html>
