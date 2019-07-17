<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>修改手机号</title>
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
                    <input class="weui-input" type="number" id="mobile" pattern="[0-9]*" placeholder="请输入手机号">
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
    var mobileReg = /(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/;
    //手机号验证
    function validMobile(value){
        return mobileReg.test(value);
    }
    $("#submitBtn").on('click', function(e){
        var mobile = $.trim($("#mobile").val());
        if(!validMobile(mobile)){
            alert("请正确填写手机号码");
            return;
        }
        $.post('/user/passport/updateByID', {phone: mobile}, function (response) {
            alert("修改成功");
            window.location.href = "/passport/user/userInfo";
        })
    });
</script>
</body>
</html>