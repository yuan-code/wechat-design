<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>修改名字</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <link rel="stylesheet" href="/css/art.css"/>
    <style>
        .page-content{
            padding-top:5px;
        }
        .weui-toast {
            position: fixed;
            z-index: 5000;
            width: 100%;
            min-height: 10px;
            top: 0px;
            left:0;
            margin:0;
            padding:10px;
            border-radius: 0px;
            color:red;
        }
        .weui-toast__content {
            margin: 0 0;
        }
    </style>
</head>
<body ontouchstart>
<div class="container" id="container">
    <div class="page-content">

        <div class="weui-cells weui-cells_form">
            <div class="weui-cell">
                <div class="weui-cell__bd">
                    <input class="weui-input" type="text" id="userName" placeholder="请输入您的名字">
                </div>
            </div>
        </div>

        <a class="weui-btn weui-btn_primary" href="javascript:" id="submitBtn" style="margin:20px 10px;">确定</a>

    </div>
</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script src="/js/app.js"></script>
<script>
    $("#submitBtn").on('click', function(e){
        var userName = $.trim($("#userName").val());
        if(userName==""){
            alert("请填写名字");
            return;
        }
        if(userName.length>10){
            alert("名字不能大于10个字符");
            return;
        }
        $.post('/user/passport/updateByID', {"nickname":userName}, function (response) {
            alert("修改成功");
            window.location.href = "/passport/user/userInfo";
        })
    });
</script>
</body>
</html>