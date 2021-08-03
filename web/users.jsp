<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="layui/css/layui.css"/>
    <script src="./layui/layui.all.js"></script>
</head>
<body>
<div>
    <c:if test="${sessionScope.loginAuth.auths.contains('com.qxgl.auth.user.add')}">
        <a href="userImport.jsp" class="layui-btn layui-btn-normal layui-btn-sm">
            <i class="layui-icon">&#xe770;</i>数据导入
        </a>
    </c:if>

</div>
<table class="layui-table">
    <thead>
    <tr>
        <th>编号</th>
        <th>用户名</th>
        <th>密码</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.pageInfo.data}" var="user">
        <tr>
            <td>${user.uid}</td>
            <td>${user.uname}</td>
            <td>${user.upass}</td>
            <td>
                <a href="#" class="layui-btn layui-btn-xs">
                    <i class="layui-icon">&#xe642;</i>编辑
                </a>

                <c:if test="${sessionScope.loginAuth.auths.contains('com.qxgl.auth.user.delete')}">
                    <a href="#" class="layui-btn layui-btn-danger layui-btn-xs">
                        <i class="layui-icon">&#xe640;</i>删除
                    </a>
                </c:if>

                <a href="#" class="layui-btn layui-btn layui-btn-normal layui-btn-xs" onclick="toSetRole('${user.uid}','${user.uname}')">
                    <i class="layui-icon">&#xe716;</i>分配角色
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div id="page-tool"></div>

<script>
    <%--    使用layui-laypage分页组件--%>
    layui.use(['laypage'], function(){
        var laypage = layui.laypage
            ,layer = layui.layer;
        <%--    渲染组件--%>
        //总页数低于页码总数
        laypage.render({
            elem: 'page-tool'
            ,count: ${requestScope.pageInfo.total} //数据总数
            ,limit:${requestScope.pageInfo.rows}
            ,limits:[2,5,8,10]
            ,curr:${requestScope.pageInfo.page}
            ,layout:['prev','page','next','limit']
            ,jump: function(obj, first) {
                //obj包含了当前分页的所有参数，比如：
                console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                console.log(obj.limit); //得到每页显示的条数

                //首次不执行
                if (!first) {
                    location.href = 'findUser?page=' + obj.curr + '&rows=' + obj.limit;
                }
            }
        });
    });

    function toSetRole(uid , uname) {
        layui.use(['layer'],function() {
            var layer = layui.layer;
            var $=layui.$;

            //使用AJAX方式去请求“分配角色”网页模板
            //js中有3中常用的请求方式，location.href  window.open()  ajax
            //1.创建xml对象
            var xml=new XMLHttpRequest();
            //2.设置请求信息
            xml.open('get','toSetRole?uid='+uid+'&uname='+uname,true);
            //3.提供回调函数，告诉AJAX接收响应后，就调用该函数，该函数处理响应
            function doBack(responseText) {
                //具体的处理逻辑，和AJAX无关
                layer.open({
                    type:1,
                    area:[600,500],
                    title:'分配角色',
                    content:responseText,  //需要弹出层展示的网页模板内容，请求
                    btn:['保存','取消'],
                    yes:function () {
                        //点击保存按钮时触发的事件函数
                        var uid=document.getElementById('uid');
                        var rids=document.getElementsByName('rid');
                        var uid_value=uid.value;
                        var rid_values='';//存储所有被选中的角色id
                        for(var i=0;i<rids.length;i++){
                            if(rids[i].checked==true){
                                rid_values+=rids[i].value+',';
                            }
                        }
                        rid_values=rid_values.substring(0,rid_values.length-1);
                        //发送请求，传递uid，rids
                        $.ajax({
                            type:'post',
                            url:'setRolesForUser',
                            data:{uid:uid_value,rids:rid_values},//自动拼装
                            synch:true,//设置异步的ajax请求还是同步的
                            success:function (responseText) {
                                //响应成功后调用的回调函数
                                layer.alert('设置成功',function () {
                                    layer.closeAll();
                                });
                            }
                        });

                    }
                })
            }
            xml.onreadystatechange=function () {
                if(xml.readyState==4){
                    if(xml.status==200) {
                        //接受了完整的正确的响应，可以将响应交给指定的回调函数处理
                        doBack(xml.responseText);
                    }else {
                        alert('出现未知错误，请重试！');
                    }
                }
            }
            //发送请求
            xml.send();
        });
    }

</script>

</body>
</html>