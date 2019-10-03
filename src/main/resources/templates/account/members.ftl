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

    <div class="weui-flex">
        <a href="/account/draw" class="weui-flex__item">
            <p>账户余额</p>
            <h2>￥<span id="sumAccount"></span></h2>
        </a>
        <div class="weui-flex__item">
            <p>支付人数</p>
            <h2>#<span id="agentCount"></h2>
        </div>
    </div>

    <div class="weui-cells__title">会员支付明细</div>
    <div class="weui-cells">
    </div>
    <div class="weui-loadmore" id="loadMoreBtn">
        <a href="javascript:void(0)" id="loadMoreBtn" style="display:none;">加载更多</a>
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
<script src="/js/app.js"></script>
<script>
    $.post('/account/accountInfo',{} function (response) {
        if (!response.success) {
            $("#nonLoadingMoreTips").html("加载失败");
        } else {
            $("#sumAccount").html(response.data.sumAccount)
            $("#agentCount").html(response.data.agentCount)
        }
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
            $("#loadMoreBtn").css("display","none");
            $("#loadingMore").css("display","none");
            $("#nonLoadingMore").css("display","");
            loading = false;
            return;
        }
        $("#loadMoreBtn").css("display","none");
        $("#loadingMore").css("display","");
        $("#nonLoadingMore").css("display","none");
        page = page+1;
        // {"code":0,"errorMsg":"success","data":[{"orderId":"156904736503800406","orderStatus":0,"orderType":0,"userName":"漂泊的云","orderAmount":28.9,"payAmount":0.0,"createTime":"2019-09-21 14:29:25"}],"hasMore":false}
        $.post('/pay/agentOrder', {pageNo:page,pageSize:pageSize}, function (response) {
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
                        html += '<div class="weui-cell"><div class="weui-cell__bd">'+o.nickName+'</div><div class="weui-cell__ft">'+o.cashFee+'元</div></div>';
                    }
                    $(".weui-cells").append(html);
                    if(hasMore){
                        $("#loadMoreBtn").css("display","");
                        $("#nonLoadingMore").css("display","none");
                        $("#loadingMore").css("display","none");
                    }else{
                        $("#loadMoreBtn").css("display","none");
                        $("#nonLoadingMore").css("display","");
                        $("#loadingMore").css("display","none");
                        $("#nonLoadingMoreTips").html("已全部加载完成");
                    }
                }
            }
        })
    }
    loadMore();
</script>
</body>
</html>