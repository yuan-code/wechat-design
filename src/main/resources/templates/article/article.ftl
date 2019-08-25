<!DOCTYPE html>
<html style="-webkit-text-size-adjust: 100%;">
${article.head}
<style>
    .rich_media_author{
        position: relative;
        overflow: hidden;
        font-size: 14px;
        background-color: #fff;
        background-clip: padding-box;
        border-radius: 2px;
        box-shadow: 0 1px 2px rgba(0,0,0,.3);
        padding: 10px;
        background:#F1F1F1;
        margin-bottom:10px;
        display: -webkit-box;
        display: -webkit-flex;
        display: flex;
    }
    .rich_media_author img{
        width: 70px;
        height: 70px;
        font-size: 0;
        line-height: 0;
        border-radius: 70px;
    }
    .rich_media_author  .rich_media_author_body {
        margin-bottom: 0;
        padding-left:10px;
        -webkit-box-flex: 1;
        -webkit-flex: 1;
        flex: 1;
    }
    .rich_media_author  .rich_media_author_body h2{
        font-size: 18px;
        font-weight: 500;
        line-height: 40px;
        color: #333;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
    .rich_media_author  .rich_media_author_body p{
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
    .rich_media_author .rich_media_author__ft{
        line-height:70px;
    }
    .footer-btn{
        position: fixed;
        bottom: 0;
        left: 0;
        right: 0;
        height: 45px;
        line-height: 45px;
        text-align: center;
        z-index: 2000;
        background-color: #d81e06;
        color: #FFF;
    }
    .footer-btn a{
        height: 45px;
        line-height: 45px;
        color: #FFF;
        text-align: center;
        width:100%;
    }
    .rich_media{
        margin-bottom:10px;
    }
    .weui-footer{
        margin-top:20px;
        margin-bottom:20px;
    }
    .mask{
        position: fixed;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        z-index: 2002;
        background: rgba(17, 17, 17, 0.7);
    }
    .mask-dialog {
        position: fixed;
        z-index: 5000;
        width: 80%;
        max-width: 300px;
        top: 40%;
        left: 50%;
        -webkit-transform: translate(-50%, -50%);
        transform: translate(-50%, -50%);
        background-color: #FFFFFF;
        text-align: center;
        border-radius: 3px;
        overflow: hidden;
    }
    .right-edit-btn{
        position: fixed;
        right: 0;
        bottom: 70px;
        padding: 3px 10px;
        background: red;
        color: #FFF;
        border-bottom-left-radius: 20px;
        border-top-left-radius: 20px;
        font-size:14px;
        text-align:center;
        min-width: 65px;
    }
    .right-customer-btn{
        background: #1296db;
        position: fixed;
        bottom: 110px;
        right: 0;
        padding: 3px 10px;
        color: #FFF;
        border-bottom-left-radius: 20px;
        border-top-left-radius: 20px;
        font-size:14px;
        text-align:center;
        min-width: 65px;
    }
    .toptip{
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 2002;
        background: rgba(0, 0, 0, 0.6);
        height:40px;
        line-height:40px;
        text-align:center;
        color:#FFF;
        font-size:14px;
    }
    .toptip:after {
        content: " ";
        display: inline-block;
        height: 6px;
        width: 6px;
        border-width: 2px 2px 0 0;
        border-color: #E64340;
        border-style: solid;
        -webkit-transform: matrix(0.71, 0.71, -0.71, 0.71, 0, 0);
        transform: matrix(0.71, 0.71, -0.71, 0.71, 0, 0);
        position: relative;
        top: -2px;
        position: absolute;
        top: 50%;
        margin-top: -4px;
        right: 10px;
    }
    .toptip span{
        font-size:15px;
        color:#E64340;
        padding-left:5px;
        padding-right:5px;
    }
</style>
<script src="/js/jquery-3.0.0.min.js" type="text/javascript" charset="utf-8"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.4.0.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    wx.config({
        debug: false,
        appId: '${appID}',
        timestamp:${timestamp},
        nonceStr: '${noncestr}',
        signature: '${signature}',
        jsApiList: ['checkJsApi', 'onMenuShareTimeline', 'onMenuShareAppMessage', 'updateAppMessageShareData', 'updateTimelineShareData']
    });
</script>

<body id="activity-detail" class="zh_CN mm_appmsg  appmsg_skin_default appmsg_style_default">
    <#--只有二次编辑的文章 并且是作者 或者原创文章 才显示这个-->
    <#--<#if !user?? || (user?? && openID?? && author.openid = openid)>-->
        <a href="/customer/customer?source=top" class="toptip">
            共有<span>0人</span>阅读了该文章，点击查看
        </a>
    <#--</#if>-->
    <div id="js_article" class="rich_media">
        <div id="js_top_ad_area" class="top_banner"></div>
        <div class="rich_media_inner">
            <div id="page-content" class="rich_media_area_primary">
                <div class="rich_media_area_primary_inner">

                    <!-- img-content -->
                    <div id="img-content">
                        <h2 class="rich_media_title" id="activity-name">
                            ${article.title}
                        </h2>
                        <div class="rich_media_author">
                            <div class="rich_media_author__hd">
                                <img src="${user.headimgurl}">
                            </div>
                            <div class="rich_media_author_body">
                                <h2>${user.nickname}</h2>
                                <p>
                                    <#--如果是原创文章 显示设置 如果是二次编辑 设置了显示内容 没设置显示编辑 如果是分享 显示内容-->
                                    <#--<#if user?? && (author.slogan?? || author.openID != openID)>-->
                                        ${user.slogan}
                                    <#--<#else>-->
                                        <a href="/user/slogan?path=/article/detail/${article.articleid}" style="color:#d6613f">设置签名</a>
                                    <#--</#if>-->
                                </p>
                            </div>
                            <div class="rich_media_author__ft">
                                <a href="javascript:void(0);" style="color:#d6613f;font-size: 16px;" class="add-myweixin">加我微信</a>
                            </div>
                        </div>
                        ${article.content}
                        <div id="meta_content" class="rich_media_meta_list">
                            <span class="rich_media_meta rich_media_meta_nickname" id="profileBt">
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <p style="padding-left:10px;text-align:left;font-size:14px;">
        阅读量：
        <span style="color:#1296db">0
            <#--如果是原创文章 显示设置 如果是二次编辑 显示设置 如果是分享 什么都不展示-->
            <#--<#if !user?? || (user?? && openID?? && author.openid = openid)>-->
                <a href="/customer/customer?source=bottom" class="right-customer-btn">0人关注</a>
            <#--</#if>-->
        </span>
    </p>
    <div class="weui-footer">
        <p class="weui-footer__text">Copyright © 2019 青山高创</p>
    </div>
    <#--如果是原创文章 显示设置 如果是二次编辑 显示设置 如果是分享 什么都不展示-->
    <#--<#if !user?? || (user?? && openID?? && author.openid = openid)>-->
        <a href="/article/edit/${article.articleid}" class="right-edit-btn">修改文章</a>
    <#--</#if>-->

    <div class="mask mask_gzh" style="display:none">
        <div class="mask-dialog" style="padding-top:20px;">
            <img src="" style="width:80%;height:80%">
            <p style="color:red;margin-bottom:20px;">长按二维码关注公众号</p>
        </div>
    </div>
    <div class="mask mask_gr" style="display:none">
        <div class="mask-dialog" style="padding-top:20px;">
            <img src="${user.qrcode}" style="width:80%;height:80%">
            <p style="color:red;margin-bottom:20px;">长按二维码加我微信</p>
        </div>
    </div>

    <script src="/js/zepto.min.js"></script>
    <script type="text/javascript">

        if (!window.console) window.console = { log: function() {} };

        if (typeof getComputedStyle == 'undefined') {
            if (document.body.currentStyle) {
                window.getComputedStyle = function(el) {
                    return el.currentStyle;
                }
            } else {
                window.getComputedStyle = {};
            }
        }
        (function(){
            window.__zoom = 1;

            (function(){
                var validArr = ","+([0.875, 1, 1.125, 1.25, 1.375]).join(",")+",";
                var match = window.location.href.match(/winzoom=(\d+(?:\.\d+)?)/);
                if (match && match[1]) {
                    var winzoom = parseFloat(match[1]);
                    if (validArr.indexOf(","+winzoom+",")>=0) {
                        window.__zoom = winzoom;
                    }
                }
            })();

            var ua = navigator.userAgent.toLowerCase();
            var re = new RegExp("msie ([0-9]+[\.0-9]*)");
            var version;
            if (re.exec(ua) != null) {
                version = parseInt(RegExp.$1);
            }
            var isIE = false;
            if (typeof version != 'undefined' && version >= 6 && version <= 9) {
                isIE = true;
            }
            var getMaxWith=function(){
                var container = document.getElementById('img-content');
                var max_width = container.offsetWidth;
                var container_padding = 0;
                var container_style = getComputedStyle(container);
                container_padding = parseFloat(container_style.paddingLeft) + parseFloat(container_style.paddingRight);
                max_width -= container_padding;
                if (!max_width) {
                    max_width = window.innerWidth - 30;
                }
                return max_width;
            };
            var getParentWidth = function(dom){
                var parent_width = 0;
                var parent = dom.parentNode;
                var outerWidth = 0;
                while (true) {
                    if(!parent||parent.nodeType!=1) break;
                    var parent_style = getComputedStyle(parent);
                    if (!parent_style) break;
                    parent_width = parent.clientWidth - parseFloat(parent_style.paddingLeft) - parseFloat(parent_style.paddingRight) - outerWidth;
                    if (parent_width > 0) break;
                    outerWidth += parseFloat(parent_style.paddingLeft) + parseFloat(parent_style.paddingRight) + parseFloat(parent_style.marginLeft) + parseFloat(parent_style.marginRight) + parseFloat(parent_style.borderLeftWidth) + parseFloat(parent_style.borderRightWidth);
                    parent = parent.parentNode;
                }
                return parent_width;
            }
            var getOuterW=function(dom){
                var style=getComputedStyle(dom),
                    w=0;
                if(!!style){
                    w = parseFloat(style.paddingLeft) + parseFloat(style.paddingRight) + parseFloat(style.borderLeftWidth) + parseFloat(style.borderRightWidth);
                }
                return w;
            };
            var getOuterH =function(dom){
                var style=getComputedStyle(dom),
                    h=0;
                if(!!style){
                    h = parseFloat(style.paddingTop) + parseFloat(style.paddingBottom) + parseFloat(style.borderTopWidth) + parseFloat(style.borderBottomWidth);
                }
                return h;
            };
            var insertAfter = function(dom,afterDom){
                var _p = afterDom.parentNode;
                if(!_p){
                    return;
                }
                if(_p.lastChild === afterDom){
                    _p.appendChild(dom);
                }else{
                    _p.insertBefore(dom,afterDom.nextSibling);
                }
            };
            var getQuery = function(name,url){

                var u  = arguments[1] || window.location.search,
                    reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"),
                    r = u.substr(u.indexOf("\?")+1).match(reg);
                return r!=null?r[2]:"";
            };


            function setImgSize(item, widthNum, widthUnit, ratio, breakParentWidth) {
                setTimeout(function () {
                    var img_padding_border = getOuterW(item) || 0;
                    var img_padding_border_top_bottom = getOuterH(item) || 0;

                    if (widthNum > getParentWidth(item) && !breakParentWidth) {
                        widthNum = getParentWidth(item);
                    }
                    height = (widthNum - img_padding_border) * ratio + img_padding_border_top_bottom;
                    item.style.cssText += ";width: " + widthNum + widthUnit + " !important;";
                    item.style.cssText += ";height: " + height + widthUnit + " !important;";
                }, 10);
            }

            (function(){
                var images = document.getElementsByTagName('img');
                var length = images.length;
                var max_width = getMaxWith();
                for (var i = 0; i < length; ++i) {
                    if (window.__second_open__ && images[i].getAttribute('__sec_open_place_holder__')) {
                        continue;
                    }
                    var imageItem = images[i];
                    var src_ = imageItem.getAttribute('data-src');
                    var realSrc = imageItem.getAttribute('src');
                    if (!src_ || realSrc) continue;

                    var originWidth = imageItem.getAttribute('data-w');
                    var ratio_ = 1 * imageItem.getAttribute('data-ratio');

                    var height = 100;
                    if (ratio_ && ratio_ > 0) {
                        var parent_width = getParentWidth(imageItem) || max_width;
                        var initWidth = imageItem.style.width || imageItem.getAttribute('width') || originWidth || parent_width;
                        initWidth = parseFloat(initWidth, 10) > max_width ? max_width : initWidth;

                        if (initWidth) {
                            imageItem.setAttribute('_width', !isNaN(initWidth * 1) ? initWidth + 'px' : initWidth);
                        }

                        if (typeof initWidth === 'string' && initWidth.indexOf('%') !== -1) {
                            initWidth = parseFloat(initWidth.replace('%', ''), 10) / 100 * parent_width;
                        }

                        if (initWidth === 'auto') {
                            initWidth = originWidth;
                        }

                        var res = /^(\d+(?:\.\d+)?)([a-zA-Z%]+)?$/.exec(initWidth);
                        var widthNum = res && res.length >= 2 ? res[1] : 0;
                        var widthUnit = res && res.length >= 3 && res[2] ? res[2] : 'px';


                        setImgSize(imageItem, widthNum, widthUnit, ratio_, true);

                        (function (item, widthNumber, unit, ratio) {
                            setTimeout(function () {
                                setImgSize(item, widthNumber, unit, ratio, false);
                            });
                        })(imageItem, widthNum, widthUnit, ratio_);
                    } else {
                        imageItem.style.cssText += ";visibility: hidden !important;";
                    }

                }
            })();
            window.__videoDefaultRatio=16/9;
            window.__getVideoWh = function(dom){
                var max_width = getMaxWith(),
                    width = max_width,
                    ratio_ = dom.getAttribute('data-ratio')*1,
                    arr = [4/3, 16/9],
                    ret = arr[0],
                    abs = Math.abs(ret - ratio_);
                if (!ratio_) {
                    if (dom.getAttribute("data-mpvid")) {
                        ratio_ = 16/9;
                    } else {
                        ratio_ = 4/3;
                    }
                } else {
                    for (var j = 1, jl = arr.length; j < jl; j++) {
                        var _abs = Math.abs(arr[j] - ratio_);
                        if (_abs < abs) {
                            abs = _abs;
                            ret = arr[j];
                        }
                    }
                    ratio_ = ret;
                }

                var parent_width = getParentWidth(dom)||max_width,
                    width = width > parent_width ? parent_width : width,
                    outerW = getOuterW(dom)||0,
                    outerH = getOuterH(dom)||0,
                    videoW = width - outerW,
                    videoH = videoW/ratio_,
                    height = videoH + outerH;
                return {w:Math.ceil(width),h:Math.ceil(height),vh:videoH,vw:videoW,ratio:ratio_};
            };


            (function(){
                var iframe = document.getElementsByTagName('iframe');
                for (var i=0,il=iframe.length;i<il;i++) {
                    if (window.__second_open__ && iframe[i].getAttribute('__sec_open_place_holder__')) {
                        continue;
                    }
                    var a = iframe[i];
                    var src_ = a.getAttribute('src')||a.getAttribute('data-src')||"";
                    if(!/^http(s)*\:\/\/v\.qq\.com\/iframe\/(preview|player)\.html\?/.test(src_)
                        && !/^http(s)*\:\/\/mp\.weixin\.qq\.com\/mp\/readtemplate\?t=pages\/video_player_tmpl/.test(src_)
                    ){
                        continue;
                    }
                    var vid = getQuery("vid",src_);
                    if(!vid){
                        continue;
                    }
                    vid=vid.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,"");
                    a.removeAttribute('src');
                    a.style.display = "none";
                    var obj = window.__getVideoWh(a),
                        videoPlaceHolderSpan = document.createElement('span'),
                        videoPlayerIconSpan = document.createElement('span'),
                        mydiv = document.createElement('img');

                    videoPlaceHolderSpan.className = "js_img_loading db";
                    videoPlaceHolderSpan.setAttribute("data-vid", vid);


                    videoPlayerIconSpan.className = 'wx_video_context db';
                    videoPlayerIconSpan.style.display = 'none';
                    videoPlayerIconSpan.innerHTML = '<span class="wx_video_thumb_primary"></span><button class="wx_video_play_btn">播放</button><span class="wx_video_mask"></span>';

                    mydiv.className = "img_loading";

                    mydiv.src="data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQImWNgYGBgAAAABQABh6FO1AAAAABJRU5ErkJggg==";


                    videoPlaceHolderSpan.style.cssText = "width: " + obj.w + "px !important;";
                    mydiv.style.cssText += ";width: " + obj.w + "px";
                    videoPlaceHolderSpan.appendChild(videoPlayerIconSpan);
                    videoPlaceHolderSpan.appendChild(mydiv);
                    insertAfter(videoPlaceHolderSpan, a);

                    a.style.cssText += ";width: " + obj.w + "px !important;";
                    a.setAttribute("width",obj.w);
                    if(window.__zoom!=1){
                        a.style.display = "block";
                        videoPlaceHolderSpan.style.display = "none";
                        a.setAttribute("_ratio",obj.ratio);
                        a.setAttribute("_vid",vid);
                    }else{
                        videoPlaceHolderSpan.style.cssText += "height: " + obj.h + "px !important;";
                        mydiv.style.cssText += "height: " + obj.h + "px !important;";
                        a.style.cssText += "height: " + obj.h + "px !important;";
                        a.setAttribute("height",obj.h);
                    }
                    a.setAttribute("data-vh",obj.vh);
                    a.setAttribute("data-vw",obj.vw);
                    if(a.getAttribute("data-mpvid")){
                        a.setAttribute("data-src",location.protocol+"//mp.weixin.qq.com/mp/readtemplate?t=pages/video_player_tmpl&auto=0&vid="+vid);
                    }else{
                        a.setAttribute("data-src",location.protocol+"//v.qq.com/iframe/player.html?vid="+ vid + "&width="+obj.vw+"&height="+obj.vh+"&auto=0");
                    }
                    if(a.getAttribute("data-mpvid")){
                        a.setAttribute("src",location.protocol+"//mp.weixin.qq.com/mp/readtemplate?t=pages/video_player_tmpl&auto=0&vid="+vid);
                    }else{
                        a.setAttribute("src",location.protocol+"//v.qq.com/iframe/player.html?vid="+ vid + "&width="+obj.vw+"&height="+obj.vh+"&auto=0");
                    }
                }
            })();

            (function(){
                if(window.__zoom!=1){
                    if (!window.__second_open__) {
                        document.getElementById('page-content').style.zoom = window.__zoom;
                        var a = document.getElementById('activity-name');
                        var b = document.getElementById('meta_content');
                        if(!!a){
                            a.style.zoom = 1/window.__zoom;
                        }
                        if(!!b){
                            b.style.zoom = 1/window.__zoom;
                        }
                    }
                    var images = document.getElementsByTagName('img');
                    for (var i = 0,il=images.length;i<il;i++) {
                        if (window.__second_open__ && images[i].getAttribute('__sec_open_place_holder__')) {
                            continue;
                        }
                        images[i].style.zoom = 1/window.__zoom;
                    }
                    var iframe = document.getElementsByTagName('iframe');
                    for (var i = 0,il=iframe.length;i<il;i++) {
                        if (window.__second_open__ && iframe[i].getAttribute('__sec_open_place_holder__')) {
                            continue;
                        }
                        var a = iframe[i];
                        a.style.zoom = 1/window.__zoom;
                        var src_ = a.getAttribute('data-src')||"";
                        if(!/http(s)*\:\/\/v\.qq\.com\/iframe\/(preview|player)\.html\?/.test(src_)){
                            continue;
                        }
                        var ratio = a.getAttribute("_ratio");
                        var vid = a.getAttribute("_vid");
                        a.removeAttribute("_ratio");
                        a.removeAttribute("_vid");
                        var vw = a.offsetWidth - (getOuterW(a)||0);
                        var vh = vw/ratio;
                        var h = vh + (getOuterH(a)||0)
                        a.style.cssText += "height: " + h + "px !important;"
                        a.setAttribute("height",h);
                        a.setAttribute("data-src",location.protocol+"//v.qq.com/iframe/player.html?vid="+ vid + "&width="+vw+"&height="+vh+"&auto=0");
                        a.style.display = "none";
                        var parent = a.parentNode;
                        if(!parent){
                            continue;
                        }
                        for(var j=0,jl=parent.children.length;j<jl;j++){
                            var child = parent.children[j];
                            if(child.className.indexOf("img_loading")>=0 && child.getAttribute("data-vid")==vid){
                                child.style.cssText += "height: " + h + "px !important;";
                                child.style.display = "";
                            }
                        }
                    }
                }
            })();
        })();
    </script>
    <script type="text/javascript">
        (function(){
            wx.ready(function () {
                if(wx.onMenuShareAppMessage){
                    wx.onMenuShareTimeline({
                        title: '${article.title}',
                        link: window.location.href,
                        imgUrl: '${article.thumbnail}',
                        success: function () {
                        },
                        fail:function(e){
                        }
                    });
                    wx.onMenuShareAppMessage({
                        title: '${article.title}',
                        desc: '${article.summary}',
                        link: window.location.href,
                        imgUrl: '${article.thumbnail}',
                        success: function () {
                        },
                        fail:function(e){
                        }

                    });
                } else {
                    wx.updateTimelineShareData({
                        title: '${article.title}',
                        link: window.location.href,
                        imgUrl: '${article.thumbnail}',
                        success: function () {
                        },
                        fail:function(e){
                        }
                    });
                    wx.updateAppMessageShareData({
                        title: '${article.title}',
                        desc: '${article.summary}',
                        link: window.location.href,
                        imgUrl: '${article.thumbnail}',
                        success: function () {
                        },
                        fail:function(e){
                        }

                    });
                }
            });
            $(".mask").on("click",function(){
                $(".mask").css("display","none")
            });
            $(".mask-dialog").on("click",function(){
                return;
            });
            $(".footer-btn").on("click",function(){
                $(".mask_gzh").css("display","")
            });
            $(".follow-weixin").on("click",function(){
                $(".mask_gzh").css("display","")
            });
            $(".add-myweixin").on("click",function(){
                $(".mask_gr").css("display","")
            });
        })();
    </script>
    <script>
        $(".add-qrcode").on('click', function(e){
            wx.chooseImage({
                count: 1,
                sizeType: ['original', 'compressed'],
                sourceType: ['album', 'camera'],
                success: function (res) {
                    var localIds = res.localIds;
                    console.log(localIds);
                    wx.uploadImage({
                        localId: localIds[0],
                        isShowProgressTips: 1,
                        success: function (res) {
                            var serverId = res.serverId;
                            $.post('/user/updateByID', {qrcode: serverId}, function (response) {
                                location.reload();
                            })
                        }
                    });
                }
            });
        })
    </script>
    <script type="text/javascript">document.addEventListener("touchstart", function() {},false);</script>

</body>
</html>
