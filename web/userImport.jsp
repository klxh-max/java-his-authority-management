<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <link rel="stylesheet" href="./layui/css/layui.css">
        <script src="./layui/layui.js"></script>

        <style>
            .layui-card{
                width: 300px;
                background: #eee;
            }
            li{
                /*margin 设置两个区域之间的上下左右的间距*/
                margin-top: 10px;
            }
        </style>
    </head>
    <body>
    <div class="layui-card">
        <div class="layui-card-header">用户信息导入</div>
        <div class="layui-card-body">
            <form action="userImport" method="post" enctype="multipart/form-data">
                <ul>
                    <li><input type="file" name="excel"></li>
                    <li>
                        <button type="submit" class="layui-btn layui-btn-sm">
                            <i class="layui-icon">&#x1005</i>保存
                        </button>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    </body>
</html>
