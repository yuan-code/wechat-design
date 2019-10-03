<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>成为高级合伙人</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <script src="http://res.wx.qq.com/open/js/jweixin-1.4.0.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript">
        wx.config({
            debug: false,
            appId: '${appID}',
            timestamp:${timestamp},
            nonceStr: '${noncestr}',
            signature: '${signature}',
            jsApiList: ['checkJsApi','onMenuShareTimeline','onMenuShareAppMessage','updateAppMessageShareData','updateTimelineShareData']
        });
    </script>
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
    </style>
</head>
<body ontouchstart>

<div class="container" id="container">
    <div class="weui-cells__title">合伙人权益</div>
    <div class="weui-cells">
        <div class="weui-cell">
            <div class="weui-cell__hd text-imp">
                <i class="weui-icon-success"></i>
            </div>
            <div class="weui-cell__bd text-imp">
                可获得所有下线会员费50%奖励
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd text-imp">
                <i class="weui-icon-success"></i>
            </div>
            <div class="weui-cell__bd text-imp">
                会员每次续费均可得会员费50%奖励
            </div>
        </div>
        <div class="weui-cell">
            <div class="weui-cell__hd text-imp">
                <i class="weui-icon-success"></i>
            </div>
            <div class="weui-cell__bd text-imp">
                成功推荐10个会员退还合伙费用
            </div>
        </div>
    </div>
    <a href="javascript:;" class="weui-btn weui-btn_primary" style="margin:20px 10px;" id="payBtn">微信支付<span>99元</span></a>
</div>

<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script src="/js/app.js"></script>
<script>
    $("#payBtn").on('click', function(e){
        $.post('/pay/create', {vipType: 4}, function (response) {
            if (response.success) {
                    wx.chooseWXPay({
                        appId: response.data.appId,
                        nonceStr: response.data.nonceStr,
                        timestamp: response.data.timeStamp,
                        package: response.data.package,
                        signType: response.data.signType,
                        paySign: response.data.paySign,
                        success: function (res) {
                            alert("支付成功", true);
                            setTimeout(function() {
                                window.location.href=document.referrer;
                            }, 1000)
                        }
                    });
            } else {
                alert("操作失败", false);
            }
        })
    });
</script>
<script type="text/javascript">
    (function(){
        wx.ready(function () {
            if(wx.onMenuShareAppMessage){
                wx.onMenuShareTimeline({
                    title: '',
                    link: '',
                    imgUrl: '',
                    success: function () {
                    },
                    fail:function(e){
                    }
                });
                wx.onMenuShareAppMessage({
                    title: '',
                    desc: '',
                    link: '',
                    imgUrl: '',
                    success: function () {
                    },
                    fail:function(e){
                    }

                });
            } else {
                wx.updateTimelineShareData({
                    title: '',
                    desc: '',
                    link: '',
                    imgUrl: '',
                    success: function () {
                    },
                    fail:function(e){
                    }
                });
                wx.updateAppMessageShareData({
                    title: '',
                    desc: '',
                    link: '',
                    imgUrl: '',
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