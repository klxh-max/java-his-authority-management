<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>主页面</title>
    <link rel="stylesheet" type="text/css" href="layui/css/layui.css" />
    <script src="layui/layui.js"></script>

    <script>
        layui.use(['tree', 'util'], function () {//语雀6.1
            var tree = layui.tree
                , layer = layui.layer
                , util = layui.util
                , $ = layui.$;

                //需要获得session中存储的权限菜单信息(ajax)   语雀6.1
                $.ajax({
                    type:'get',
                    url:'findUserMenus',
                    data:{},
                    synch:true,
                    success:function (menus) {
                        //通过一个前端js的json反序列化处理，menus就变成了js语法的集合/数组

                        //常规用法  语雀6.1
                        tree.render({
                            elem:'#myTree',//使用标签的id，表示将菜单的数据显示在指定的标签中
                            data:menus
                        })
                    },
                    dataType:'json'//告诉jQuery，将响应回来的字符串json反序列化
                });

        });

        function logout() {
            //浏览器显示一个询问框（确定/取消）,根据点击的按钮返回true或false
            var f= confirm('是否确认退出');
            if(f){
                location.href='logout';
            }
        }
    </script>

</head>
<body>
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">快乐小慧医院管理系统</div>
        <!-- 头部区域（可配合layui 已有的水平导航） -->

        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item"><a href="javascript:logout()">退出系统</a></li>
        </ul>
    </div>

    <div class="layui-side layui-bg-gray">
        <div class="layui-side-scroll">
            <div id="myTree" class="demo-tree demo-tree-box"></div>
        </div>
    </div>

    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div style="padding: 15px;">
            <!--            内嵌子窗口-->
            <iframe name="content" width="100%" height="100%" frameborder="0"/>
        </div>
    </div>

    <div class="layui-footer">
        <!-- 底部固定区域 -->
        底部固定区域
    </div>
</div>

</body>
</html>