<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="layui/css/layui.css"/>
        <script src="./layui/layui.all.js"></script>
    </head>
    <body>
        <%--新建根菜单按钮--%>
        <button type="button" class="layui-btn layui-btn-normal" onclick="toAdd(-1,'根菜单')">
            <i class="layui-icon">&#xe61f;</i>新建</button>

        <%--数据展示的位置--%>
        <div id="funGrid"></div>

        <%--功能类型处理后显示的文本--%>
        <script type="text/html" id="typeText">
            {{#  if(d.ftype == 1){      }}
            <span class="layui-badge layui-bg-green">菜单</span>
            {{#  }else{                 }}
            <span class="layui-badge">按钮</span>
            {{#  }                      }}
        </script>

        <%--操作处理后显示的按钮（新建、删除、修改--%>
        <script type="text/html" id="rowBtns">
            {{#  if(d.ftype == 1){      }}
            <button type="button" class="layui-btn layui-btn-normal layui-btn-sm" onclick="toAdd('{{d.fid}}','{{d.fname}}')">
                <i class="layui-icon">&#xe61f;</i>新建</button>
            {{#  }else{                 }}
            <button type="button" class="layui-btn layui-btn-normal layui-btn-sm layui-btn-disabled">
                <i class="layui-icon">&#xe61f;</i>新建</button>
            {{#  }                      }}
            <button type="button" class="layui-btn layui-btn-danger layui-btn-sm">
                <i class="layui-icon">&#xe640;</i>删除</button>
            <button type="button" class="layui-btn layui-btn-warm layui-btn-sm">
                <i class="layui-icon">&#xe642;</i>修改</button>
        </script>

        <!-- 弹出层中的表单模板 -->
        <form id="funAddForm" class="layui-form" action="funAdd" style="display:none;padding-top:10px;">
            <div class="layui-form-item">
                <label class="layui-form-label">功能名称</label>
                <div class="layui-input-inline">
                    <input type="text" name="fname" required  lay-verify="required" placeholder="请输入名称" autocomplete="off" class="layui-input layui-input-inline">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">功能类别</label>
                <div  class="layui-input-inline">
                    <input type="radio" name="ftype" value="1" title="菜单" checked>
                    <input type="radio" name="ftype" value="2" title="按钮">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">功能链接(URL)</label>
                <div  class="layui-input-inline">
                    <input type="text" name="fhref" placeholder="请输入链接" autocomplete="off" class="layui-input">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">功能范围</label>
                <div  class="layui-input-inline">
                    <input type="text" name="auth" required  lay-verify="required" placeholder="请输入功能范围" autocomplete="off" class="layui-input">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">所属父级</label>
                <div  class="layui-input-inline">
                    <input type="hidden" name="pid" id="pid" value="-1" />
                    <input type="text" name="pname" id="pname" readonly  autocomplete="off" class="layui-input">
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
                    <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                </div>
            </div>

        </form>

        <%--用于解决radio不显示的问题--%>
        <script>
            layui.use('form', function(){
                var form = layui.form;
                form.render('radio');
            });
        </script>

        <!-- layui-js代码实现 -->
        <script>
            layui.config({
                base: './treetable-lay/' //指定treetable组件所在的文件夹目录
            }).use(['treeTable'], function () { //use() 使用指定的layui组件
                var treeTable = layui.treeTable;
                treeTable.render({
                    elem: '#funGrid',//指定渲染的位置
                    url: 'funAll',//使用ajax技术展示请求表格中要展示的数据
                    cols: [[//告诉组件按照什么样的表格形式去展示数据
                        {type:'numbers',width:70},     // <td>
                        {type:'checkbox',width:70},     // <td>
                        {title: '功能名称', field: 'fname',width:200},//<td>
                        {title: '功能类别', field: 'ftype',width:90, templet:'#typeText'},
                        {title: '请求url', field: 'fhref',width:300},
                        {title: '权限范围', field: 'auth',width:200},
                        {title: '操作',templet: '#rowBtns'}
                    ]],
                    tree:{//设置查询数据之间的子父关系
                        iconIndex:2,
                        idName:'fid',//按照id和pid的关系实现子父级别
                        pidName:'pid',
                        isPidData:true,//表示按照id和pid的关系实现子父级别
                    }
                });

                    });

            //弹出一个新建功能的窗口
            function toAdd(pid,pname) {
                layui.use(['layer'],function () {
                    var layer=layui.layer;
                    var $=layui.jquery;

                    //将pid，pname更新到输入框中，建议使用id寻找
                    var input=document.getElementById('pid');
                    input.value=pid;//设置input标签的value属性
                    $('#pname').val(pname);//jQuery写法设置标签value属性

                    layer.open({
                        type:1,//打开一个页面层，可以展示一些html效果
                        area:[400,400],
                        title:'新建功能',
                        content:$('#funAddForm')//必须使用jQuery,jQuery根据id找到form模板，将其引入到弹出层
                    })
                })
            }
        </script>

    </body>
</html>
