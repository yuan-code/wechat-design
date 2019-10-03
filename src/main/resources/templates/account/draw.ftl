<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>金币提现</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <style>
        .weui-btn-area {
            margin: 1.17647059em 15px 0.3em;
        }
        .page-top{
            text-align:left;
            line-height:30px;
            background:#FF7700;
            padding-left:10px;
            color:#FFF;
            font-size:13px;
        }
    </style>
</head>
<body ontouchstart>
<div class="container" id="container">
    <input type="hidden" id="coinAmount" value="29092">
    <div class="page-top">
        您当前有<span id="sumCoin"></span>金币，还差<font style="font-size:15px;font-weight:600;color:black;padding:0 10px;"><span id="subCount"></span> </font>金币即可提现
    </div>
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
                <label class="weui-label">姓名</label>
            </div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="text" id="userName" placeholder="真实姓名" value="">
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd">
                <label class="weui-label">身份证号</label>
            </div>
            <div class="weui-cell__bd">
                <input class="weui-input" type="text" id="cardNo" placeholder="身份证号">
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd"><label class="weui-label">提款金额</label></div>
            <div class="weui-cell__bd">
                <input class="weui-input" readonly type="number" id="drawAmount" maxValue="" value="100">
            </div>
            <div class="weui-cell__ft" style="font-size: 15px;">
                元
            </div>
        </div>
    </div>
    <div class="weui-cells__tips" style="color:red">请正确填写当前微信号绑定的手机号、姓名、身份证号信息，否则微信提现会审核拒绝</div>
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
    $.post('/account/coinCount',{},function (response) {
        if (!response.success) {
            $("#nonLoadingMoreTips").html("加载失败");
        } else {
            $("#sumCoin").html(response.data.sumCoin)
            var subCount = 10000 - response.data.sumCoin
            subCount = subCount < 0 ? 0 : subCount
            $("#subCount").html(subCount)
        }
    })
    var $loadingToast = $('#loadingToast');
    var mobileReg = /(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/;
    //手机号验证
    function validMobile(value){
        return mobileReg.test(value);
    }
    $("#submitBtn").on('click', function(e){
        if($("#coinAmount").val()<100000){
            alert("不满足提现条件,提现金额必须大于100");
            return;
        }
        var userMobile = $.trim($("#userMobile").val());
        var cardNo = $.trim($("#cardNo").val());
        var userName = $.trim($("#userName").val());
        var drawAmount = $.trim($("#drawAmount").val());
        if(!validMobile(userMobile)){
            alert("请正确填写手机号码");
            return;
        }
        if(cardNo==""){
            alert("请填写身份证号");
            return;
        }
        if(userName==""){
            alert("请填写真实姓名");
            return;
        }
        if(drawAmount==""){
            alert("请填写提款金额");
            return;
        }
        drawAmount = parseFloat(drawAmount);
        if(drawAmount<=0){
            alert("请正确填写提款金额");
            return;
        }
        $loadingToast.fadeIn(100);
        $.post('/my/coin/draw', {userMobile: userMobile,cardNo:cardNo,userName:userName,drawAmount:drawAmount}, function (response) {
            $loadingToast.fadeOut(100);
            var json = eval("(" + response + ")")
            if (json["code"] != 0) {
                alert(json["errorMsg"]);
                return;
            }
            alert("提款申请已提交，我们将在24小时内处理您的提款订单",function(){
                window.location.href = "/my/coin";
            });
        })
    });
</script>
</body>
</html>