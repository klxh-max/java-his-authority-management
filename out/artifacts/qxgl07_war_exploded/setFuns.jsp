<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%--设计分配功能的页面  除了标记，其余都是语雀5.1--%>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="layui/css/layui.css"/>
    <script src="./layui/layui.all.js"></script>
</head>
<body>
<div class="layui-card">
    <div class="layui-card-header" style="padding: 10px;text-align: center">
        角色编号：<input class="layui-input layui-input-inline" value="${param.rid}" id="rid2" name="rid2" style="width:150px;" />
        角色名称：<input class="layui-input layui-input-inline" value="${param.rname}" id="rname2" name="rname2" style="width:150px;" />
    </div>
    <div class="layui-card-body">
        <%--数据展示的位置--%>
        <div id="funGrid"></div>
    </div>
</div>

<!-- layui-js代码实现 -->
<script>
    var insTb ;
    layui.config({
        base: './treetable-lay/' //指定treetable组件所在的文件夹目录
    }).use(['treeTable'], function () { //use() 使用指定的layui组件
        var treeTable = layui.treeTable;
        var $ = layui.jquery ;

        insTb = treeTable.render({
            elem: '#funGrid',//指定渲染的位置
            url: 'funAll',//使用ajax技术展示请求表格中要展示的数据
            cols: [[//告诉组件按照什么样的表格形式去展示数据
                {type:'numbers',width:70},     // <td>
                {type:'checkbox',width:70},     // <td>
                {title: '功能名称', field: 'fname'}//<td>
            ]],
            tree:{//设置查询数据之间的子父关系
                iconIndex: 2,//指定展开合并图标出现在哪一列
                idName:'fid',//按照id和pid的关系实现子父级别
                pidName:'pid',
                isPidData:true,//表示按照id和pid的关系实现子父级别
            },
            done:function () {
                //当treetable表格数据加载完毕时调用的函数
                //数据库查询当前角色上一次分配的功能编号
                //需要js代码发请求 location.href , window.open() , ajax
                $.ajax({
                    type:'get',
                    url:'findFidsByRole',
                    data:{rid:$("#rid2").val()},
                    synch:true,
                    success:function (responseText){
                        insTb.setChecked(responseText);
                        //JSON.parse(responseText);
                    },
                    dataType:'json'//告诉jQuery，将响应回来的字符串json反序列化
                });
            }
        });

    });

</script>

</body>
</html>
