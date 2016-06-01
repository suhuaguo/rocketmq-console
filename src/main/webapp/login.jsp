<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String path = request.getContextPath();//获取站点的跟路径，这样，可以防止链接失效
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<html>

<head>
    <script type="text/javascript"
            src="<%=basePath%>js/jquery-1.8.2.js"></script>
    <script type="text/javascript"
            src="<%=basePath%>js/jquery-ui.js"></script>
    <link href="<%=basePath%>js/jquery-ui.css" rel="stylesheet"/>

    <script type="text/javascript">
        $(function () {
            $("#btnLogin").button();
            $("#reset").button();
        });

        function clearAll() {
            var name = document.getElementById("name");
            var password = document.getElementById("password");
            name.value = "";
            password.value = "";
        }
    </script>

    <title>消息中间件控制台</title>

</head>
<body onload='document.loginForm.username.focus();'>

<div style="padding:3px 2px;border-bottom:1px solid #ccc;vertical-align:middle;" align="center">
    <h4 style="color: gold">消息中间件组：煎蛋</h4>

    <c:if test="${not empty error}">
        <div class="error"><h3 style="color: green;">${error}</h3></div>
    </c:if>
    <c:if test="${not empty msg}">
        <div class="msg">${msg}</div>
    </c:if>
    <%-- <div class="msg">${name}：用户测试，用户是否真正的登陆啦</div> --%>

    <form name='loginForm' action="j_spring_security_check" method='POST'
          autocomplete="off">

        <table>
            <tr>
                <td colspan="2"
                    style="font-size: 20px; width: 100px; text-align: right;"><label for="username">用户名&nbsp;</label>
                </td>
                <td><input type='text' name='username' value='' id="username"
                           style="font-size: 20px;"></td>
            </tr>
            <tr>
                <td colspan="2"
                    style="font-size: 20px; width: 100px; text-align: right;"><label for="password">&nbsp;&nbsp;密码&nbsp;</label>
                </td>
                <td><input type='password' name='password' value="" id="password"
                           style="font-size: 20px;"/></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td colspan="2"><input id="_spring_security_remember_me" name="_spring_security_remember_me"
                                       type="checkbox" value="true"/> <label
                        for="_spring_security_remember_me">10天内自动登录</label>
                </td>
            </tr>
            <tr>
                <td></td>
                <td colspan="2">
                    <button type="submit"
                            id="btnLogin">登&nbsp;&nbsp;陆
                    </button>
                    &nbsp;&nbsp;&nbsp;&nbsp;<input
                        type="reset" value="重&nbsp;&nbsp;置" id="reset"></td>
            </tr>

        </table>

        <input type="hidden" name="${_csrf.parameterName}"
               value="${_csrf.token}"/>
    </form>
</div>


</body>
</html>