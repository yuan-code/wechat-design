<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>我</title>
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
            color:#E48072;
        }
        .weui-cell__bd{
            min-width:80px;
        }
        .weui-cell__ft{
            text-overflow:ellipsis;
            white-space:nowrap;
            overflow:hidden;
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
            jsApiList: ['checkJsApi','chooseImage','uploadImage','onMenuShareTimeline','onMenuShareAppMessage','updateAppMessageShareData','updateTimelineShareData']
        });
    </script>
</head>
<body ontouchstart>

<div class="container" id="container">
    <div class="weui-cells">
        <div class="weui-cell weui-img">
            <div class="weui-cell__bd">
                头像
            </div>
            <div class="weui-cell__ft">
                <img src="${user.headimgurl}">
            </div>
        </div>
        <a href="/passport/user/nickname" class="weui-cell weui-cell_access">
            <div class="weui-cell__bd">
                姓名
            </div>
            <div class="weui-cell__ft">
                ${user.nickname}
            </div>
        </a>
    </div>

    <div class="weui-cells">
        <a class="weui-cell weui-cell_access" href="/passport/user/slogan?path=/passport/user/userInfo">
            <div class="weui-cell__bd">
                签名
            </div>
            <div class="weui-cell__ft" style="">
                ${user.slogan}
            </div>
        </a>
    </div>

    <div class="weui-cells">
        <a class="weui-cell weui-cell_access" href="/passport/user/phone">
            <div class="weui-cell__bd">
                手机
            </div>
            <div class="weui-cell__ft">
                ${user.phone}
            </div>
        </a>
        <div class="weui-cell weui-img" id="qrcodeCell">
            <div class="weui-cell__bd">
                微信二维码
            </div>
            <div class="weui-cell__ft">
                <img src="${user.qrcode}" id="wxQrcode">
            </div>
        </div>
    </div>
    <div class="weui-cells__bottom">上传微信二维码快速获取销售线索</div>

</div>

<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script>
    $("#qrcodeCell").on('click', function(e){
        wx.chooseImage({
            count: 1, // 默认9
            sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
            sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
            success: function (res) {
                var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                console.log(localIds);
                wx.uploadImage({
                    localId: localIds[0], // 需要上传的图片的本地ID，由chooseImage接口获得
                    isShowProgressTips: 1, // 默认为1，显示进度提示
                    success: function (res) {
                        var serverId = res.serverId; // 返回图片的服务器端ID
                        $.post('/user/passport/updateByID', {"qrcode":serverId}, function (response) {
                            var imageUrl = "https://wechat-design-1257895402.cos.ap-beijing.myqcloud.com/" + response.data.qrcode
                            $("#wxQrcode").attr("src",imageUrl)
                        })
                    }
                });
            }
        });
    })
</script>
</body>
</html>