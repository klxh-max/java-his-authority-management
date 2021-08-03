<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
  <head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="layui/css/layui.css" />
    <script src="layui/layui.all.js"></script>
    <style>
      body{
        width:100%;
        height: 100%;
        background-image: url("imgs/login.jpg");
      }

      .layui-card{
        width: 320px;
        /*距上面100px，左右自动居中*/
        margin: 300px auto;
      }

      tr{
        height: 50px;
      }

    </style>
  </head>
  <body>
  <c:if test="${requestScope.result!=null}">
    <script>
      alert("${requestScope.result}");
    </script>
  </c:if>
  <div class="layui-card">
    <div class="layui-card-header">用户登录</div>
    <div class="layui-card-body">
      <form action="login" method="post">
        <table>
          <tr>
            <td>用户名：</td>
            <td>
              <input type="text" name="uname" required lay-verify="required" placeholder="请输入用户名" autocomplete="off" class="layui-input" value="">
            </td>
          </tr>
          <tr>
            <td>密码：</td>
            <td>
              <input type="password" name="upass" required lay-verify="required" placeholder="请输入密码" autocomplete="off" class="layui-input" value="">
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="text" name="code"><img id="codeImg" src="checkcode" onclick="reloadCode()">
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="checkbox" name="autoflag">一周内自动登录
            </td>
          </tr>
          <tr><td><input type="submit" value="登录"></td></tr>
        </table>
      </form>
    </div>
  </div>

  </body>
  <script src="js/reloadcode.js"></script>
</html>