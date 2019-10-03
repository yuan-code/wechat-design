<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>我的推广明细</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <style>
        .page, body {
            background-color: #FFF;
        }
        .weui-flex{
            background-color:#FF7700;
            height:77px;
        }
        .weui-flex__item{
            text-align:center;
            padding:10px 5px;
            color:#FFF;
        }
        .weui-flex__item h2{
            color:#FFF;
        }
        .weui-msg {
            padding-top: 5px;
            text-align: center;
        }
        .weui-msg__icon-area {
            margin-bottom: 5px;
        }
        .weui-msg__desc {
            font-size: 13px;
            color: #000;
            padding-bottom: 10px;
            text-align:left;
        }
    </style>
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
</head>
<body ontouchstart>
<div class="container" id="container">

    <div class="weui-flex">
        <a href="/account/draw" class="weui-flex__item">
            <p>待提</p>
            <h2>￥#{sumAccount}</h2>
        </a>
        <a href="/account/members" class="weui-flex__item">
            <p>累计</p>
            <h2>￥#{totalAccount}</h2>
        </a>
        <a href="/account/members" class="weui-flex__item">
            <p>成交人数</p>
            <h2>#{agentCount}</h2>
        </a>
    </div>

    <div class="weui-msg">
        <div class="weui-msg__icon-area">
            <img src="/image/rate_img.jpeg" style="width:50%;">
        </div>
        <div class="weui-msg__text-area">
            <p class="weui-msg__desc">
                1、每推荐成功一个新会员注册并付费，你将获得50%会员费作为奖励；
            </p>
            <p class="weui-msg__desc">
                2、推荐方式：
            <p class="weui-msg__desc" style="padding-left:15px;">
                1、分享文章，朋友点击「免费转换成我的名片文章」；
            </p>
            <p class="weui-msg__desc" style="padding-left:15px;">
                2、推荐专属推广二维码图片；
            </p>
            </p>
            <p class="weui-msg__desc">
                3、你必须是高级会员才能提现；
            </p>
            <p class="weui-msg__desc">
                4、点击上方待提金额可提现。
            </p>
        </div>
    </div>

</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script src="/js/app.js"></script>
<script>
</script>
</body>
</html>