<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--设计分配角色的页面--%>
<html>
    <head>
        
    </head>
    <body>
        <div class="layui-card">
            <div class="layui-card-header" style="padding: 10px">
                用户编号：<input class="layui-input layui-input-inline" value="${param.uid}" id="uid" name="uid" style="width:150px;" />
                用户名称：<input class="layui-input layui-input-inline" value="${param.uname}" id="uname" name="uname" style="width:150px;" />
            </div>
            <div class="layui-card-body">
                <table class="layui-table">
                    <thead>
                        <tr>
                            <th><input id="allCheck" type="checkbox" onclick="toCheckAll()" /></th>
                            <th>角色编号</th>
                            <th>角色名称</th>
                            <th>角色描述</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="role" items="${setRoleInfoList}">
                            <tr>
                                <th><input type="checkbox" name="rid" value="${role.rid}" <c:if test="${role.flag==1}" >checked</c:if> /></th>
                                <th>${role.rid}</th>
                                <th>${role.rname}</th>
                                <th>${role.rdescription}</th>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    
        <script>
            function toCheckAll() {
                //将表格行中的复选框状态与当前触发事件的（所有）复选框设置相同
                //根据标签name属性获得一组同名标签，返回值是数组
                var checks=document.getElementsByName('rid');
                var allCheck=document.getElementById('allCheck');
                for(var i=0;i<checks.length;i++){
                    var checkbox=checks[i];
                    checkbox.checked=allCheck.checked;
                }

            }
        </script>

    </body>
</html>
