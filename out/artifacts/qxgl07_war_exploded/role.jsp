<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="layui/css/layui.css"/>
        <script src="./layui/layui.all.js"></script>
    </head>
    <body>
        <%--角色列表--%>
        <table id="roleGrid"></table>

        <%--表格单元的按钮模板--%>
        <script type="text/html" id="rowBtns">
            <button type="button" class="layui-btn layui-btn-danger layui-btn-sm">
                <i class="layui-icon">&#xe640;</i>删除</button>
            <button type="button" class="layui-btn layui-btn-warm layui-btn-sm" onclick="toUpdate('{{d.rid}}','{{d.rname}}','{{d.rdescription}}')">
                <i class="layui-icon">&#xe642;</i>修改</button>
            <button type="button" class="layui-btn layui-btn-normal layui-btn-sm" onclick="toSetFun('{{d.rid}}','{{d.rname}}')">
                <i class="layui-icon">&#xe716;</i>分配功能</button>
        </script>

        <!-- 弹出层中的表单模板 -->
        <form id="roleUpdateForm" class="layui-form" action="roleUpdate" style="display:none;padding-top:10px;">
            <div class="layui-form-item">
                <label class="layui-form-label">角色编号</label>
                <div class="layui-input-inline">
                    <input type="text" name="rid" id="rid" readonly  lay-verify="required" autocomplete="off" class="layui-input layui-input-inline">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">角色名称</label>
                <div class="layui-input-inline">
                    <input type="text" name="rname" id="rname" required  lay-verify="required" placeholder="请输入名称" autocomplete="off" class="layui-input layui-input-inline">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">角色描述</label>
                <div  class="layui-input-inline">
                    <input type="text" name="rdescription" id="rdescription" placeholder="请输入描述" autocomplete="off" class="layui-input">
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
                    <button type="button" class="layui-btn layui-btn-primary" onclick="closeEditWin()">取消</button>
                </div>
            </div>

        </form>

        <script>
            //使用layui的table组件加载数据
            layui.use(['table'],function () {
                var table=layui.table;
                table.render(
                    {
                        elem: '#roleGrid',//绑定渲染位置
                        url: 'roleFindAll',//底层使用ajax请求表格数据
                        cols: [[
                            {title: '角色编号', field: 'rid'},
                            {title: '角色名称', field: 'rname'},
                            {title: '角色描述', field: 'rdescription'},
                            {title: '操作',templet:'#rowBtns'}
                        ]], //指定查询结果与表格单元之间的展示关系，看哪个属性展示在哪个位置
                        page:true
                    });
            })

            //编辑角色
            function toUpdate(rid,rname,rdescription) {
                layui.use(['layer'],function () {
                    var layer=layui.layer;
                    var $=layui.jquery;

                    $('#rid').val(rid) ;//jQuery写法设置标签value属性
                    document.getElementById('rname').value = rname ;
                    $('#rdescription').val(rdescription);

                    layer.open({
                        type:1,//打开一个页面层，可以展示一些html效果
                        area:[400,300],
                        title:'修改角色',
                        content:$('#roleUpdateForm')//必须使用jQuery,jQuery根据id找到form模板，将其引入到弹出层
                    })
                })
            }

            //关闭编辑窗口弹出层
            function closeEditWin() {
                layui.use(['layer'],function () {
                    var layer=layui.layer;
                    layer.closeAll();
                })
            }

            //给角色分配功能  语雀5.1
            function toSetFun(rid,rname) {
                //弹出一个窗口（弹出层）
                layui.use(['layer'],function() {
                    var layer = layui.layer;
                    var $=layui.$;

                    $.ajax({
                        type:'get',
                        url:'setFuns.jsp',
                        data:{rid:rid,rname:rname},//自动拼装
                        synch:true,//设置异步的ajax请求还是同步的
                        success:function (responseText) {
                            layer.open({
                                type:1,
                                area:[600,500],
                                title:'分配功能',
                                content:responseText,  //需要弹出层展示的网页模板内容，请求
                                btn:['保存','取消'],
                                yes:function () {
                                    //点击保存触发函数
                                    //发送请求，传递rid，fids
                                    var checkboxs=insTb.checkStatus();//获取所有被选中的复选框
                                    var fids='';
                                    for(var i=0;i<checkboxs.length;i++){
                                        fids+=checkboxs[i].fid+',';
                                    }
                                    fids=fids.substring(0,fids.length-1);
                                    $.ajax({
                                        type:'post',
                                        url:'setF',
                                        data:{rid:rid,fids:fids},
                                        synch:true,
                                        success:function(respText){
                                            layer.alert('分配成功',function(){
                                                layer.closeAll();
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });





                });
            }

        </script>

    </body>
</html>
