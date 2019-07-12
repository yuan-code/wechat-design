<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>开通会员</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <style>
        .weui-cell__bd{
            color:#343537;
            line-height:30px;
        }
        .weui-cell__ft{
            line-height:30px;
        }
        .weui-img .weui-cell__ft{
            height: 40px;
        }
        .weui-cell__ft img{
            width: 40px;
            height: 40px;
            font-size: 0;
            line-height: 0;
            border-radius: 40px;
            margin-bottom:0;
        }
        .weui-cells__bottom {
            margin-top: .3em;
            padding-left: 10px;
            padding-right: 10px;
            color: #999;
            font-size: 14px;
            text-align:right;
        }
        .text-imp{
            color:#FF7700;
        }
        .text-imp .weui-icon-success {
            color: #FF7700;
        }
        .weui-check__label p{
            color:#333;
        }
        .weui-check__label p span{
            color:#999;
            font-size:14px;
        }
    </style>
    <script src="http://res.wx.qq.com/open/js/jweixin-1.4.0.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript">
        wx.config({
            debug: false,
            appId: 'wx2aa93c5623a0285f',
            timestamp:'1562509603',
            nonceStr: '0553919efa1145c0',
            signature: 'e5cfc6d14503a2e4918ab056dc2b891d6899045b',
            jsApiList: ['checkJsApi','onMenuShareTimeline','onMenuShareAppMessage','updateAppMessageShareData','updateTimelineShareData']
        });
    </script>
</head>
<body ontouchstart>
<div class="container" id="container">
    <div class="weui-cells__title">会员权益</div>
    <div class="weui-cells">
        <div class="weui-cell">
            <div class="weui-cell__hd text-imp">
                <i class="weui-icon-success"></i>
            </div>
            <div class="weui-cell__bd text-imp">
                编辑我的文章
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd text-imp">
                <i class="weui-icon-success"></i>
            </div>
            <div class="weui-cell__bd text-imp">
                创建我的文章
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd text-imp">
                <i class="weui-icon-success"></i>
            </div>
            <div class="weui-cell__bd text-imp">
                谁查看了我的文章
            </div>
        </div>
    </div>
    <div class="weui-cells  weui-cells_checkbox">
        <label class="weui-cell weui-check__label" for="level1">
            <div class="weui-cell__hd">
                <input type="checkbox" class="weui-check vip-level" name="level1" id="level1" value="28.9" vipType="1">
                <i class="weui-icon-checked"></i>
            </div>
            <div class="weui-cell__ft">
                <p>1月28.9元
                    <span>(原价78.9 约0.9元/天)</span>
                </p>
            </div>
        </label>
        <label class="weui-cell weui-check__label" for="level2">
            <div class="weui-cell__hd">
                <input type="checkbox" class="weui-check vip-level" value="98.9" name="level2" checked="checked" id="level2" vipType="2">
                <i class="weui-icon-checked"></i>
            </div>
            <div class="weui-cell__ft">
                <p>6月98.9元
                    <span>(原价288.9 约0.5元/天)</span>
                </p>
            </div>
        </label>
        <label class="weui-cell weui-check__label" for="level3">
            <div class="weui-cell__hd">
                <input type="checkbox" class="weui-check vip-level" value="148.9" name="level3" id="level3" vipType="3">
                <i class="weui-icon-checked"></i>
            </div>
            <div class="weui-cell__ft">
                <p>12月148.9元
                    <span>(原价588.9 约0.4元/天)</span>
                </p>
            </div>
        </label>
    </div>
    <a href="javascript:;" id="payBtn" class="weui-btn weui-btn_primary" style="margin:20px 10px;">微信支付
        <span id="agentAmount">98.9</span>元
    </a>
</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script>
    var vipType = 2;
    $(".vip-level").on('change', function(e){
        $(".vip-level").prop("checked", false);
        $(this).prop("checked", true);
        $("#agentAmount").html($(this).val())
        vipType = $(this).attr("vipType");
    });
    $("#payBtn").on('click', function(e){
        $.post('/my/vip/pay', {vipType:vipType}, function (response) {
            var json = eval("(" + response + ")")
            if (json["code"] == 1) {
                wx.chooseWXPay({
                    timestamp: json.timestamp,
                    nonceStr: json.nonceStr,
                    package: json.package,
                    signType: json.signType,
                    paySign: json.paySign,
                    success: function (res) {
                        alert("支付成功",true);
                        window.location.href = "/art/a22b1d3a4265f526/edit"+"?id="+10000*Math.random();
                    }
                });
            } else if(json["code"] == 2) {
                alert(json.errorMsg,true);
                window.location.href = "/art/a22b1d3a4265f526/edit"+"?id="+10000*Math.random();
            }else {
                alert("操作失败",false);
            }
        })
    });

    window.alert = function (msg, back) {
        if ($('#jsAlertDialog').length <= 0) {
            var dialogHTML = '<div class="js_dialog" id="jsAlertDialog" style="opacity: 1;">';
            dialogHTML += '<div class="weui-mask"></div>';
            dialogHTML += '<div class="weui-dialog">';
            dialogHTML += '<div class="weui-dialog__bd weui-alert-dialog__msg">'+msg+'</div>';
            dialogHTML += '<div class="weui-dialog__ft">';
            dialogHTML += '<a href="javascript:;" class="weui-dialog__btn weui-dialog__btn_primary">知道了</a>';
            dialogHTML += '</div>';
            dialogHTML += '</div>';
            dialogHTML += '</div>';
            $('body').append(dialogHTML);
        } else {
            $(".weui-alert-dialog__msg").html(msg);
        }
        $(".weui-dialog__btn").off("click");
        $('.weui-dialog__btn').on('click', function () {
            $("#jsAlertDialog").fadeOut(100);
        });
        $("#jsAlertDialog").fadeIn(100);
    }
</script>
<script type="text/javascript">
    (function(){
        wx.ready(function () {
            if(wx.onMenuShareAppMessage){
                wx.onMenuShareTimeline({
                    title: '推客行',
                    link: 'http://yx.herbedu.com/vip?p=u22a642b644f6f69',
                    imgUrl: 'http://yx.herbedu.com/image/icon.png',
                    success: function () {
                    },
                    fail:function(e){
                    }
                });
                wx.onMenuShareAppMessage({
                    title: '推客行',
                    desc: '超过20万人正在用的销售线索追踪神器',
                    link: 'http://yx.herbedu.com/vip?p=u22a642b644f6f69',
                    imgUrl: 'http://yx.herbedu.com/image/icon.png',
                    success: function () {
                    },
                    fail:function(e){
                    }

                });
            } else {
                wx.updateTimelineShareData({
                    title: '推客行',
                    desc: '超过20万人正在用的销售线索追踪神器',
                    link: 'http://yx.herbedu.com/vip?p=u22a642b644f6f69',
                    imgUrl: 'http://yx.herbedu.com/image/icon.png',
                    success: function () {
                    },
                    fail:function(e){
                    }
                });
                wx.updateAppMessageShareData({
                    title: '推客行',
                    desc: '超过20万人正在用的销售线索追踪神器',
                    link: 'http://yx.herbedu.com/vip?p=u22a642b644f6f69',
                    imgUrl: 'http://yx.herbedu.com/image/icon.png',
                    success: function () {
                    },
                    fail:function(e){
                    }

                });
            }
        });
    })();
</script>
</body>
</html>