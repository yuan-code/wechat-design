<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>我的文章</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <style>
        .weui-loadmore_line .weui-loadmore__tips {
            background:transparent;
        }
        .weui-cells,.weui-cell,.weui-cell__bd,.weui-cell__ft{
            background:transparent;
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
    <div class="weui-panel weui-panel_access">
        <div class="weui-panel__hd">我的文章</div>
        <div class="weui-panel__bd" id="content">

        </div>
    </div>

    <div class="weui-loadmore" id="loadMore" style="display:none;">
        <a href="javascript:void(0)" id="loadMoreBtn" style="">加载更多</a>
    </div>
    <div class="weui-loadmore" id="loadingMore" style="display:none;">
        <i class="weui-loading"></i>
        <span class="weui-loadmore__tips">正在加载</span>
    </div>
    <div class="weui-loadmore weui-loadmore_line" id="nonLoadingMore" style="display:none;">
        <span class="weui-loadmore__tips" id="nonLoadingMoreTips">暂无数据</span>
    </div>
</div>

<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script type="text/javascript">
    (function(){
        wx.ready(function () {
            if(wx.onMenuShareAppMessage){
                wx.onMenuShareTimeline({
                    title: '${author.nickname}的文章列表',
                    link: 'http://wechat.ictry.com/article/custom/${author.openid}',
                    imgUrl: 'http://wechat.ictry.com/image/icon.png',
                    success: function () {
                    },
                    fail:function(e){
                    }
                });
                wx.onMenuShareAppMessage({
                    title: '${author.nickname}的文章列表',
                    desc: '超过20万人正在用的销售线索追踪神器',
                    link: 'http://wechat.ictry.com/article/custom/${author.openid}',
                    imgUrl: 'http://wechat.ictry.com/image/icon.png',
                    success: function () {
                    },
                    fail:function(e){
                    }

                });
            } else {
                wx.updateTimelineShareData({
                    title: '${author.nickname}的文章列表',
                    desc: '超过20万人正在用的销售线索追踪神器',
                    link: 'http://wechat.ictry.com/article/custom/${author.openid}',
                    imgUrl: 'http://wechat.ictry.com/image/icon.png',
                    success: function () {
                    },
                    fail:function(e){
                    }
                });
                wx.updateAppMessageShareData({
                    title: '${author.nickname}的文章列表',
                    desc: '超过20万人正在用的销售线索追踪神器',
                    link: 'http://wechat.ictry.com/article/custom/${author.openid}',
                    imgUrl: 'http://wechat.ictry.com/image/icon.png',
                    success: function () {
                    },
                    fail:function(e){
                    }

                });
            }
        });
    })();
</script>
<script>
    var page = 0;
    var hasMore = true;
    var loading = false;
    var pageSize = 20;
    function loadMore(){
        if(loading){
            return;
        }
        loading = true;
        if(!hasMore){
            $("#loadMore").css("display","none");
            $("#loadingMore").css("display","none");
            $("#nonLoadingMore").css("display","");
            loading = false;
            return;
        }
        $("#loadMore").css("display","none");
        $("#loadingMore").css("display","");
        $("#nonLoadingMore").css("display","none");
        page = page+1;
        $.post('/article/list/${author.openid}', {pageNo:page,pageSize:pageSize}, function (response) {
            loading = false;
            if (!response.success) {
                $("#nonLoadingMoreTips").html("加载失败");
            } else {
                hasMore = response.data.pages == page;
                var data = response.data.records;
                if(page==1&&data.length==0){
                    $("#loadMoreBtn").css("display","none");
                    $("#nonLoadingMore").css("display","");
                    $("#loadingMore").css("display","none");
                    $("#nonLoadingMoreTips").html("暂无数据");
                }else{
                    var html = "";
                    for(var i=0;i<data.length;i++){
                        var o = data[i];
                        html += '<a href="/article/detail/'+o.articleid+'" class="weui-media-box weui-media-box_appmsg">';
                        html += '<div class="weui-media-box__hd">';
                        html += '<img class="weui-media-box__thumb" src="'+o.thumbnail+'">';
                        html += '</div>';
                        html += '<div class="weui-media-box__bd">';
                        html += '<h4 class="weui-media-box__title">'+o.title+'</h4>';
                        html += '<p class="weui-media-box__desc">'+o.summary+'</p>';
                        html += '</div>';
                        html += '</a>';
                    }
                    $("#content").append(html);
                    if(hasMore){
                        console.log(hasMore)
                        $("#loadMore").css("display","");
                        console.log($("#loadMoreBtn").css("display"))
                        $("#nonLoadingMore").css("display","none");
                        $("#loadingMore").css("display","none");
                    }else{
                        $("#loadMore").css("display","none");
                        $("#nonLoadingMore").css("display","");
                        $("#loadingMore").css("display","none");
                        $("#nonLoadingMoreTips").html("已全部加载完成");
                    }
                }
            }
        })
    }
    loadMore();
    $("#loadMore").on("click",function(){
        loadMore();
    });
</script>
</body>
</html>