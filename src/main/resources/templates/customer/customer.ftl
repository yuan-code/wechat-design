<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>谁看了我</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <script src="/js/jquery-3.0.0.min.js" type="text/javascript" charset="utf-8"></script>
    <script src="http://res.wx.qq.com/open/js/jweixin-1.4.0.js" type="text/javascript" charset="utf-8"></script>
    <style>
        .weui-flex{
            background-color:#FFF;
            height:77px;
        }
        .weui-flex__item{
            text-align:center;
            padding:10px 5px;
        }
        .weui-flex__item h2{
            color:#FF7700;
        }
        .weui-flex__item p{
            font-size:0.7em;
        }
        .weui-cell__bd{
            color:#343537;
            line-height:30px;
        }
        .weui-cells{
            margin-top:0px;
        }
        .weui-cells,.weui-cell,.weui-cell__bd,.weui-cell__ft{
            background:transparent;
        }
        .weui-cell__hd img{
            width: 40px;
            height: 40px;
            font-size: 0;
            line-height: 0;
            border-radius: 40px;
            margin-bottom:0;
            display:block;
        }
        .weui-cell__bd{
            padding:5px 10px;
        }

        .mask{
            position: fixed;
            top: 77px;
            bottom: 0;
            left: 0;
            right: 0;
            z-index: 2002;
            background: rgba(234, 234, 234, 0.95);
        }
        .mask-dialog {
            position: absolute;
            z-index: 5000;
            width: 70%;
            max-width: 300px;
            top: 50%;
            left: 50%;
            -webkit-transform: translate(-50%, -50%);
            transform: translate(-50%, -50%);
            background:transparent;
            text-align: center;
            border-radius: 3px;
            overflow: hidden;
        }
        .weui-btn {
            margin-top:10px;
        }
        .container{
            overflow:hidden;
        }
        .weui-cells{
            overflow:hidden;
        }
    </style>
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
        <div class="weui-flex__item">
            <h2>0</h2>
            <p>今天看了</p>
        </div>
        <div class="weui-flex__item">
            <h2>0</h2>
            <p>看我总数</p>
        </div>
    </div>

    <div class="mask">
        <div class="mask-dialog">
            <p>共有0人关注了我的文章</p>
            <a href="<#--/vip/vip?path=/customer-->" class="weui-btn weui-btn_primary">立即查看</a>
            <p>或者</p>
            <a href="/article/copy" class="weui-btn weui-btn_primary">发篇文章试试看</a>
        </div>
    </div>

</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script type="text/javascript">
    (function(){
        wx.ready(function () {
            if(wx.onMenuShareAppMessage){
                wx.onMenuShareTimeline({
                    title: '青山高创',
                    link: '',
                    imgUrl: '',
                    success: function () {
                    },
                    fail:function(e){
                    }
                });
                wx.onMenuShareAppMessage({
                    title: '青山高创',
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
                    title: '青山高创',
                    desc: '',
                    link: '',
                    imgUrl: '',
                    success: function () {
                    },
                    fail:function(e){
                    }
                });
                wx.updateAppMessageShareData({
                    title: '青山高创',
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