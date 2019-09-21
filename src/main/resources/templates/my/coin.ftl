<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>我的金币</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <style>
        .weui-flex{
            background:#FFF;
        }
        .weui-flex__item{
            text-align:center;
            padding:10px 5px;
            border-bottom:1px solid #F5F5F5;
            position: relative;
        }
        .weui-flex__item h2{
            color:#FF7700;
            position: relative;
        }
        .weui-flex__item p{
        }
        .weui-msg {
            padding-top: 5px;
            margin-top:10px;
            text-align: center;
            background:#FFF;
            padding-bottom:30px;
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
        .weui-flex__border {
            margin-top: 30px;
            width: 1px;
            margin-bottom: 10px;
            background: #F1F1F1;
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
    <div class="page-top">
        每次用户阅读都会产生金币，金币可提现
    </div>
    <div class="weui-flex">
        <div class="weui-flex__item">
            <p>今日金币</p>
            <h2>15</h2>
        </div>
        <div class="weui-flex__border"></div>
        <div class=" weui-flex_border weui-flex__item">
            <p>总金币</p>
            <h2>29,092
            </h2>
            <p><a href="/my/coin/draw" class="weui-btn weui-btn_mini weui-btn_warn">提现</a></p>
        </div>
    </div>
    <div class="weui-msg">
        <div class="weui-msg__text-area" style="margin:0;text-align:left;padding: 0 10px;font-size:14px;color: #FF7700;">
            金币获取方式：
        </div>
        <div class="weui-msg__text-area" style="padding: 0 10px;">
            <p class="weui-msg__desc" style="font-weight:600;">
                1、普通用户：
            <p class="weui-msg__desc" style="padding-left:15px;">
                1、通过“获取热文”发布的文章,1关注1个金币；
            </p>
            <p class="weui-msg__desc" style="padding-left:15px;">
                2、通过“发布文章,黏贴链接”发布的文章,1关注2金币；
            </p>
            </p>
            <p class="weui-msg__desc" style="font-weight:600;">
                2、会员用户：
            <p class="weui-msg__desc" style="padding-left:15px;">
                1、通过“获取热文”发布的文章,1关注5金币；
            </p>
            <p class="weui-msg__desc" style="padding-left:15px;">
                2、通过“发布文章,黏贴链接”发布的文章,1关注10金币；
            </p>
            <p class="weui-msg__desc" style="padding-left:15px;">
                3、获取直接下级的发文关注数*0.2,按1关注5金币计算；
            </p>
            <p class="weui-msg__desc" style="padding-left:15px;">
                4、首充金额100%返金币,之后每次按续费金额50%金币返还；
            </p>
            </p>
            </p>
        </div>
        <div class="weui-msg__opr-area">
            <p class="weui-btn-area">
                <a href="/vip" class="weui-btn weui-btn_mini weui-btn_plain-primary">开通会员赚取10倍金币</a>
            </p>
        </div>
    </div>

</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script src="/js/app.js"></script>
<script>
    $("#drawBtn").on("click",function(){
        alert("不满足提现条件");
    });
</script>
</body>
</html>