var userLang,userName,passWord;
fjs.onReady(function(){
userName=fjs.form.textField({renderTo:"name_box",width:180,maxLength:30,value:""});
userNum=fjs.form.textField({renderTo:"num_box",width:180,maxLength:30,value:""});
passWord=fjs.form.textField({renderTo:"pass_box",type:"password",width:180,maxLength:16});userName.focus()});
var my={
reset:function(){userName.setValue("");passWord.setValue("");userNum.setValue("");return false;},
submit:function(){
var a=userName.getValue(),b=passWord.getValue();num=userNum.getValue();
if(!a||a==""){FZM.msg.showError("用户名不能为空请输入！");userName.focus();return false}
if(!b||b.length<3){FZM.msg.showError("密码不能为空，或输入有 误请输入！");passWord.focus();return false}
if(!num || num.length != 6){FZM.msg.showError("工号不能为空，或输入有误请重新输入！");userNum.focus();return false}
new FZM.Ajax({url:"login.jsp",method:"post",param:"name="+a+"&pass="+b + "&num=" + num,oncomplete:this.getLogin});
return false},
getLogin:function(res){
	var a=eval("("+res.responseText+")");
	if(a.success){location.href="index.html";}else{FZM.msg.showError(a.message);return false;}
}}
