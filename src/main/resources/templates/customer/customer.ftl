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
            <h2>#{todayCount}</h2>
            <p>今天看了</p>
        </div>
        <div class="weui-flex__item">
            <h2>#{allCount}</h2>
            <p>看我总数</p>
        </div>
        <#if user.available>
            <div class="weui-flex__item" style="line-height: 77px;padding:0;font-size: 1.2em;">
                    <a href="/article/custom/-1">文章统计</a>
            </div>
        </#if>
    </div>

      <#if user.available>
        <div class="weui-cells" id="content">

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
      <#else>
       <div class="mask">
           <div class="mask-dialog">
               <p>共有#{allCount}人关注了我的文章</p>
               <a href="${user.available?string('javascript:;','/vip/vip')}" class="weui-btn weui-btn_primary">立即查看</a>
               <p>或者</p>
               <a href="/article/copy" class="weui-btn weui-btn_primary">发篇文章试试看</a>
           </div>
       </div>
      </#if>
</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<#if user.available>
<script type='text/javascript' src="/js/clipboard.min.js"></script>
<script src="/js/app.js"></script>
</#if>

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
<#if user.available>
<script>
    var sessionUserId = "u22a27fcf4b8e09b";
    function copy(){
        var clipboard = new Clipboard('.weui-btn').on('success', function(e) {
            alert("复制成功");
        }).on('error', function(e) {
            alert("复制失败");
        });
    }
    var page = 0;
    var hasMore = true;
    var loading = false;
    var pageSize = 10;
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
        $.post('/customer/list', {pageNo:page,pageSize:pageSize}, function (response) {
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
                        html += '<div class="weui-cell">';
                        html += '<div class="weui-cell__hd"><img src="'+o.customerUser.headimgurl+'"></div>';
                        html += '<div class="weui-cell__bd">';
                        html += '    <p>'+o.customerUser.nickname+'</p>';
                        html += '</div>';
                        html += '<div class="weui-cell__ft">';
                        if(o.subscribe==1){
                            html += '	<a href="/chat/'+sessionUserId+'/'+o.customerUser.userid+'" class="weui-btn weui-btn_mini weui-btn_warn">撩TA</a>';
                        }else{
                            html += '	<a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_primary" data-clipboard-text="'+o.nickName+'">复制TA</a>';
                        }
                        html += '</div>';
                        html += '</div>';
                    }
                    $("#content").append(html);
                    copy();
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

</#if>
</body>
</html>