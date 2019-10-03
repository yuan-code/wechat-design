<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>个人中心</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <style>
        .page-top{
            width:100%;
            text-align:center;
            line-height:40px;
            background:#F6F6FE;
            color:#ABBAEB;
            font-size:13px;
            margin-bottom:10px;
        }
        .page-content{
            padding:10px 10px;
        }
        .my-card{
            position: relative;
        }
        .my-card__ct{
            position: relative;
            overflow: hidden;
            font-size: 14px;
            background-color: #FF7700;
            background-clip: padding-box;
            padding: 10px;
            padding-top:40px;
            display: -webkit-box;
            display: -webkit-flex;
            display: flex;
            color:#FFF;
            border-radius: 5px 5px 0px 0px;
        }
        .my-card .my-card__hd {
            padding-top: 10px;
        }
        .my-card .my-card__hd img{
            width: 60px;
            height: 60px;
            font-size: 0;
            line-height: 0;
            border-radius: 50px;
        }
        .my-card  .my-card_body {
            margin-bottom: 0;
            padding-left:10px;
            -webkit-box-flex: 1;
            -webkit-flex: 1;
            flex: 1;
        }
        .my-card  .my-card_body h2{
            font-size: 18px;
            font-weight: 500;
            line-height: 80px;
            color:#FFF;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
        .my-card  .my-card_body p{
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
        .my-card .my-card__ft{
        }
        .my-card .my-card__ft img{
            width: 80px;
            height: 80px;
            font-size: 0;
            line-height: 0;
        }
        .my-card .my-card__bt{
            background-color:#FFF;
            box-shadow: 0 1px 2px rgba(0,0,0,.3);
            padding:20px 10px;
            font-size:14px;
            border-radius: 0px 0px 5px 5px;
            color:#777;
        }
        .my-card a{
            position: absolute;
            right: 0px;
            z-index: 10;
            top: 10px;
            background: #F1F1F1;
            line-height: 20px;
            font-size: 14px;
            padding-left: 10px;
            border-top-left-radius: 20px;
            border-bottom-left-radius: 20px;
            color: #777;
        }
        .weui-cells {
            border-radius: 5px;
        }
    </style>
    <script src="/js/jquery-3.0.0.min.js" type="text/javascript" charset="utf-8"></script>
    <script>
        $(function(){
            $.post('/pay/vipEndTime', {}, function (response) {
                var endTime = response.data
                $("#endTime").html(endTime);
            })
        })
    </script>
</head>
<body ontouchstart>

<div class="page-top">
    个人名片自动添加至您的文章顶部，完善信息宣传自己
</div>

<div class="page-content">
    <div class="my-card">
        <a href="/user/userInfo">修改名片</a>
        <div class="my-card__ct">
            <div  class="my-card__hd">
                <img src="${user.headimgurl}">
            </div>
            <div class="my-card_body">
                <h2>${user.nickname}</h2>
            </div>
            <div class="my-card__ft">
                <img src="${user.qrcode}">
            </div>
        </div>
        <div class="my-card__bt">
            ${user.slogan}
        </div>
    </div>
    <div class="weui-cells">
        <a class="weui-cell weui-cell_access" href="/account/coin">
            <div class="weui-cell__bd">
                我的金币
            </div>
            <div class="weui-cell__ft">
                14,451个
            </div>
        </a>
        <a class="weui-cell weui-cell_access" href="/pay/vip">
            <div class="weui-cell__bd">
                ${user.available?string('续费','开通会员')}
            </div>
            <div class="weui-cell__ft">
            </div>
        </a>
        <#if user.available>
            <a class="weui-cell weui-cell_access" href="javascript:;">
                <div class="weui-cell__bd">
                    会员到期日期
                </div>
                <div class="weui-cell__ft" id="endTime">

                </div>
            </a>
        </#if>
        <#if user.agent>
            <div class="weui-cell">
                <div class="weui-cell__bd">
                    账户余额
                </div>
                <div class="weui-cell__ft">
                    14.44元
                </div>
            </div>
            <!-- 展示代理推广信息-->
            <a class="weui-cell weui-cell_access" href="/account/members">
                <div class="weui-cell__bd">
                    推广明细
                </div>
                <div class="weui-cell__ft">
                    &nbsp;
                </div>
            </a>
            <a class="weui-cell weui-cell_access" href="/account/draw">
                <div class="weui-cell__bd">
                    提款
                </div>
                <div class="weui-cell__ft">
                    &nbsp;
                </div>
            </a>
        <#else>
            <!-- 如果未开通代理会员展示以下a 标签的内容-->
            <a class="weui-cell weui-cell_access" href="/account/agent">
                <div class="weui-cell__bd">
                    开通代理
                </div>
                <div class="weui-cell__ft">
                    &nbsp;
                </div>
            </a>
        </#if>

    </div>
</div>


<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
</body>
</html>