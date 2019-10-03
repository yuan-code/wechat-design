<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>提款</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <style>
        .weui-btn-area {
            margin: 1.17647059em 15px 0.3em;
        }
    </style>
</head>
<body ontouchstart>
<div class="container" id="container">

    <div class="weui-cells weui-cells_form">
        <div class="weui-cell">
            <div class="weui-cell__hd">
                <label class="weui-label">手机号</label>
            </div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="tel" id="userMobile" placeholder="请输入手机号" value="">
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd">
                <label class="weui-label">微信号</label>
            </div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="text" id="userWx" placeholder="微信号">
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd">
                <label class="weui-label">姓名</label>
            </div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="text" id="userName" placeholder="真实姓名" value="${user.nickname}">
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">提款金额</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="number" id="drawAmount" maxValue="" value="">
            </div>
        </div>
    </div>
    <div class="weui-cells__tips" style="color:red">满100元可提现，目前提款只支持以微信转账的方式提取到您的微信账户，请正确填写手机号及微信号</div>
    <div class="weui-btn-area">
        <a class="weui-btn weui-btn_primary" href="javascript:" id="submitBtn">确定</a>
    </div>
</div>
<div id="loadingToast" style="opacity: 0; display: none;">
    <div class="weui-mask_transparent"></div>
    <div class="weui-toast">
        <i class="weui-loading weui-icon_toast"></i>
    </div>
</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script src="/js/app.js"></script>
<script>
    var sumAccount = 0;
    $.post('/account/accountInfo', {}, function (response) {
        if (!response.success) {
            $("#nonLoadingMoreTips").html("加载失败");
        } else {
            sumAccount = response.data.sumAccount
            $("#drawAmount").maxValue = sumAccount
            $("#drawAmount").value = sumAccount
        }
    })

    var $loadingToast = $('#loadingToast');
    var mobileReg = /(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/;

    //手机号验证
    function validMobile(value) {
        return mobileReg.test(value);
    }

    $("#submitBtn").on('click', function (e) {
        var userMobile = $.trim($("#userMobile").val());
        var userWx = $.trim($("#userWx").val());
        var userName = $.trim($("#userName").val());
        var drawAmount = $.trim($("#drawAmount").val());
        if (!validMobile(userMobile)) {
            alert("请正确填写手机号码");
            return;
        }
        if (userWx == "") {
            alert("请填写微信号");
            return;
        }
        if (userName == "") {
            alert("请填写真实姓名");
            return;
        }
        if (drawAmount == "") {
            alert("请填写提款金额");
            return;
        }
        drawAmount = parseFloat(drawAmount);
        if (drawAmount <= 0) {
            alert("请正确填写提款金额");
            return;
        }
        if (drawAmount < 100) {
            alert("满100元才可提款");
            return;
        }
        if (drawAmount > sumAccount) {
            alert("提款金额不能大于余额" + sumAccount + "元");
            return;
        }
        $loadingToast.fadeIn(100);
        $.post('/agent/draw', {
            userMobile: userMobile,
            userWx: userWx,
            userName: userName,
            drawAmount: drawAmount
        }, function (response) {
            $loadingToast.fadeOut(100);
            var json = eval("(" + response + ")")
            if (json["code"] != 0) {
                alert(json["errorMsg"]);
                return;
            }
            alert("提款申请已提交", function () {
                window.location.href = "/my";
            });
        })
    });
</script>
</body>
</html>