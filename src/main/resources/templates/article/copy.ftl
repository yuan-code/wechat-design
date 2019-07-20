
<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>发布文章</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <style>
        .weui-cells{
            margin:5px;
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
    <div class="page-content">
        <div class="weui-cells__title">目前只支持复制微信公众号文章</div>
        <div class="weui-cells weui-cells_form">
            <div class="weui-cell">
                <div class="weui-cell__bd">
                    <textarea class="weui-textarea" id="artUrl" placeholder="请复制公众号文章地址" rows="8"></textarea>
                </div>
            </div>
        </div>

        <a class="weui-btn weui-btn_primary" href="javascript:" id="submitBtn" style="margin:20px 10px;">确定</a>
       <#-- <div class="weui-footer">
            <p class="weui-footer__links">
                <a href="https://mp.weixin.qq.com/s/OCkRMjb_K_oBAEC_fRf1HA" class="weui-footer__link">如何复制公众号文章地址?</a>
            </p>
        </div>-->
    </div>
</div>
<div id="loadingToast" style="opacity: 0; display: none;">
    <div class="weui-mask_transparent"></div>
    <div class="weui-toast">
        <i class="weui-loading weui-icon_toast"></i>
        <p class="weui-toast__content">正在复制文章……</p>
    </div>
</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script src="/js/app.js"></script>
<script>
    var $loadingToast = $('#loadingToast');
    String.prototype.startWith=function(s){
        if(s==null||s==""||this.length==0||s.length>this.length)
            return false;
        if(this.substr(0,s.length)==s)
            return true;
        else
            return false;
        return true;
    }
    $("#submitBtn").on('click', function(e){
        var url = $.trim($("#artUrl").val());
        if(url==null||url==""){
            alert("请输入连接地址");
            return;
        }
        if(!url.startWith('https://mp.weixin.qq.com/')){
            alert("目前只支持已https://mp.weixin.qq.com/开头的链接地址");
            return;
        }
        $loadingToast.fadeIn(100);
        $.post('/article/passport/copy', {source: url}, function (result) {
            if (result.success) {
                window.location.href = "/article/auth/detail/"+result.data.articleid;
            } else {
                alert("复制文章失败");
            }
        })
    });
</script>
</body>
</html>