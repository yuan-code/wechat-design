<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>输入签名</title>
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
                    <textarea class="weui-textarea" id="slogan" placeholder="请输入签名" rows="3" maxlength="20"></textarea>
                    <div class="weui-textarea-counter"><span id="inputLength">0</span>/20</div>
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
$(function(){
     $("#slogan").val(decodeURIComponent(GetQueryString("slogan")));
     var slogan = $("#slogan").val();
    $("#inputLength").html(slogan.length);
});

function GetQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);//search,查询？后面的参数，并匹配正则
     if(r!=null)return (r[2]); return null;
}


    $("#slogan").on("input",function(){
        var slogan = $("#slogan").val();
        $("#inputLength").html(slogan.length);
    });
    $("#submitBtn").on('click', function(e){
        var slogan = $.trim($("#slogan").val());
        if(slogan==""){
            alert("请填写签名");
            return;
        }
        if(slogan.length>20){
            alert("签名不能大于20个字符");
            return;
        }
        $.post('/user/updateByID', {slogan: slogan}, function (response) {
            alert("修改成功");
            window.location.href = "${path[0]}";
        })
    });
</script>
</body>
</html>