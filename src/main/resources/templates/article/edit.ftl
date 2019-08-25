<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>${article.title}</title>
    <link rel="shortcut icon" type="image/x-icon" href="/image/icon.png">
    <link rel="stylesheet" href="/css/weui.min.css"/>
    <link rel="stylesheet" href="/css/app.css"/>
    <link rel="stylesheet" href="/css/iconfont.css"/>
    <script src="http://res.wx.qq.com/open/js/jweixin-1.4.0.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript">
        wx.config({
            debug: false,
            appId: '${appID}',
            timestamp:${timestamp},
            nonceStr: '${noncestr}',
            signature: '${signature}',
            jsApiList: ['checkJsApi', 'hideAllNonBaseMenuItem', 'chooseImage', 'uploadImage']
        });
    </script>
    <style>
        body {
            background: #D8D8D8;
        }

        .page-content {
            padding: 5px 20px;
        }

        .mask {
            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;
            right: 0;
            z-index: 2002;
            background: rgba(0, 0, 0, 0.7);
        }

        .mask-dialog {
            position: absolute;
            z-index: 5000;
            width: 60%;
            max-width: 300px;
            top: 40%;
            left: 50%;
            -webkit-transform: translate(-50%, -50%);
            transform: translate(-50%, -50%);
            background: transparent;
            text-align: center;
        }

        .insert-box {
            background-color: #FFF;
            color: #FFF;
            display: -webkit-box;
            display: -webkit-flex;
            display: flex;
            border-radius: 5px;
            line-height: 2.5;
            text-align: center;
            margin: 10px auto;
            margin-bottom: 20px;
        }

        .insert-box .weui-flex__item {
            margin: 10px auto;
            display: flex;
            justify-content: center;
            align-items: Center;
        }

        .footer {
            position: fixed;
            bottom: 0;
            left: 0;
            right: 0;
            height: 2.5;
            line-height: 2.5;
            text-align: center;
            z-index: 1000;
            background-color: #FFF;
            color: #FFF;
            display: -webkit-box;
            display: -webkit-flex;
            display: flex;
            border-top: 2px solid #F1F1F1;
        }

        .footer .weui-flex__item {
        }

        .footer .weui-btn {
            margin: 5px 20px;
            line-height: 2;
        }

        .art-title {
            text-align: center;
            margin: 10px 0;
        }

        .art-content {
            overflow: hidden;
            margin-bottom: 60px;
        }

        .select-box {
            margin: 10px auto;
            position: relative;
            overflow: hidden;
        }

        .select-box:before {
            content: " ";
            position: absolute;
            left: 0;
            top: 0;
            right: 0;
            height: 1px;
            border-top: 1px solid #E5E5E5;
            color: #E5E5E5;
            -webkit-transform-origin: 0 0;
            transform-origin: 0 0;
            -webkit-transform: scaleY(0.5);
            transform: scaleY(0.5);
        }

        .select-box__bd {
            background: #F6D3CA;
            border-top-left-radius: 5px;
            border-top-right-radius: 5px;
            border: 2px dotted #FF4E19;
            padding: 5px;
        }

        .select-box__ft {
            background: #FFF;
            border-bottom-left-radius: 5px;
            border-bottom-right-radius: 5px;
            text-align: center;
        }

        .container .weui-btn {
            padding: 0.2em 1em;
            border-radius: 1.5em;
        }

        .select-box__ft .weui-btn {
            margin-bottom: 10px;
            margin-left: 5px;
        }

        .weui-btn .iconfont {
            font-size: 13px;
            margin-right: 3px;
        }

        .weui-textarea__bd {
            border: 1px solid #F1F1F1;
            width: 100%;
            height: 100%;
        }

        .weui-dialog__bd:first-child {
            padding: 15px;
            color: #353535;
        }
    </style>
</head>
<body ontouchstart>
<div class="container" id="container">
    <div class="page-content">

        <h2 class="art-title">${article.title}</h2>

        <div class="insert-box">
            <div class="weui-flex__item">
                <a href="${user.available?string('javascript:;','/vip/vip')}" class="weui-btn weui-btn_mini weui-btn_warn"><span
                        class="iconfont icon-edit"></span>插入文本</a>
            </div>
            <div class="weui-flex__item">
                <a href="${user.available?string('javascript:;','/vip/vip')}" class="weui-btn weui-btn_mini weui-btn_warn"><span
                        class="iconfont icon-tupian"></span>插入图片</a>
            </div>
        </div>

        <div id="selectBox" style="display:none">
            <div class="select-box">
                <div class="select-box__bd">
                </div>
                <div class="select-box__ft">
                    <a href="${user.available?string('javascript:;','/vip/vip')}" class="${user.available?string('delete-btn','')} weui-btn weui-btn_mini weui-btn_warn"><span
                            class="iconfont icon-chuyidong1-copy"></span>删除</a>
                    <a href="${user.available?string('javascript:;','/vip/vip')}" class="${user.available?string('edit-btn','')} weui-btn weui-btn_mini weui-btn_warn"><span
                            class="iconfont icon-edit"></span>插入文字</a>
                    <a href="${user.available?string('javascript:;','/vip/vip')}" class="${user.available?string('img-btn','')} weui-btn weui-btn_mini weui-btn_warn"><span
                            class="iconfont icon-tupian"></span>插入图片</a>
                </div>
            </div>
        </div>


        <div class="art-content">
            ${article.content}
        </div>


    </div>
</div>
<div class="mask">
    <div class="mask-dialog">
        <img src="/image/hand_click.gif" width="60%">
        <img src="/image/hand_click_tip.png" width="100%">
    </div>
</div>
<div class="js_dialog" id="titleDialog" style="opacity: 0; display: none;">
    <div class="weui-mask"></div>
    <div class="weui-dialog">
        <div class="weui-dialog__hd"><strong class="weui-dialog__title">修改标题</strong></div>
        <div class="weui-dialog__bd">
            <div class="weui-textarea__bd">
                <textarea id="artTitleArea" class="weui-textarea" placeholder="输入标题" rows="5"></textarea>
                <div class="weui-textarea-counter"><span id="inputLength">0</span>/100</div>
            </div>
        </div>
        <div class="weui-dialog__ft">
            <a href="javascript:;" id="dialogCancelBtn" class="weui-dialog__btn weui-dialog__btn_default">取消</a>
            <a href="javascript:;" id="dialogConfirmBtn" class="weui-dialog__btn weui-dialog__btn_primary">确定</a>
        </div>
    </div>
</div>
<div class="js_dialog" id="contentDialog" style="opacity: 0; display: none;">
    <div class="weui-mask"></div>
    <div class="weui-dialog">
        <div class="weui-dialog__hd"><strong class="weui-dialog__title">插入内容</strong></div>
        <div class="weui-dialog__bd">
            <div class="weui-textarea__bd">
                <textarea id="artContentArea" class="weui-textarea" placeholder="输入内容" rows="5"></textarea>
            </div>
        </div>
        <div class="weui-dialog__ft">
            <a href="javascript:;" id="contentCancelBtn" class="weui-dialog__btn weui-dialog__btn_default">取消</a>
            <a href="javascript:;" id="contentConfirmBtn" class="weui-dialog__btn weui-dialog__btn_primary">确定</a>
        </div>
    </div>
</div>
<div class="footer">
    <div class="weui-flex__item">
        <a href="/article/detail/${article.articleid}" class="weui-btn weui-btn_default">返回</a>
    </div>
    <div class="weui-flex__item">
        <a href="${user.available?string('javascript:;','/vip/vip')}" class="${user.available?string('save-btn','')} weui-btn weui-btn_primary">保存</a>
    </div>
</div>
<div id="loadingToast" style="opacity: 0; display: none;">
    <div class="weui-mask_transparent"></div>
    <div class="weui-toast">
        <i class="weui-loading weui-icon_toast"></i>
        <p class="weui-toast__content">正在上传……</p>
    </div>
</div>
<script src="/js/zepto.min.js"></script>
<script src="/js/weui.min.js"></script>
<script>
    var $loadingToast = $('#loadingToast');
    var targetPanel = null;
    $(function () {
        bindAddContent();
        $(".save-btn").on("click", function () {
            $(".art-content").find(".select-box").each(function () {
                $this = $(this);
                $this.replaceWith($this.find(".select-box__bd").html());
            });
            var html = $(".art-content").html();
            var title = $(".art-title").html();
            var artId = "${article.articleid}";
            $loadingToast.fadeIn(100);
            $.post('/article/save', {title: title, pid:artId,content: html,openid:'${article.openid}'}, function (result) {
                $loadingToast.fadeOut(100);
                window.location.href = "/article/detail/" + result.data.articleid;
            });
        });
        $("#contentCancelBtn").on("click", function () {
            $("#contentDialog").fadeOut(200);
        });
        $("#contentConfirmBtn").on("click", function () {
            var html = $.trim($("#artContentArea").val());
            if (html == "") {
                return;
            } else {
                if (targetPanel == null) {
                    $(".art-content").prepend('<p style="margin: 0em 0.5em;max-width: 100%;color: rgb(0, 0, 0);white-space: normal;line-height: 1.75em;box-sizing: border-box !important;word-wrap: break-word !important;">' + html + '</p>')
                } else {
                    targetPanel.after('<p style="margin: 0em 0.5em;max-width: 100%;color: rgb(0, 0, 0);white-space: normal;line-height: 1.75em;box-sizing: border-box !important;word-wrap: break-word !important;">' + html + '</p>');
                }
                bindContentClick();
            }
            $("#contentDialog").fadeOut(200);
            targetPanel = null;
        });
        $(".art-title").on("click", function () {
            var title = $(this).html();
            $("#inputLength").html(title.length);
            $("#artTitleArea").val(title);
            $("#titleDialog").fadeIn(200);
        });
        $("#dialogCancelBtn").on("click", function () {
            $("#titleDialog").fadeOut(200);
        });
        $("#dialogConfirmBtn").on("click", function () {
            var html = $.trim($("#artTitleArea").val());
            if (html == "") {
                alert("请输入文章标题");
                return;
            }
            if (html.length > 100) {
                alert("文章标题不能大于100个字符");
                return;
            }
            $(".art-title").html(html)
            $("#titleDialog").fadeOut(200);
        });
        $("#artTitleArea").on("input", function () {
            var title = $("#artTitleArea").val();
            $("#inputLength").html(title.length);
        });
        $(".mask").on("click", function () {
            $(this).fadeOut(100);
        });

        wx.hideAllNonBaseMenuItem();
        var contentWidth = $(".art-content").offset().width;
        var windowWidth = $(window).width();
        $(".art-content").find("img").each(function (index) {
            var originWidth = $(this).attr('data-w');
            if (originWidth == undefined) {
                if ($(this).width() > windowWidth) ;
                $(this).css("width", "100%");
            }
            var ratio_ = 1 * $(this).attr('data-ratio');
            if (originWidth > windowWidth) {
                $(this).css("width", "100%");
            } else {
                $(this).css("width", originWidth + "px");
            }
        })

        //wx.hideMenuItems({
        //	menuList: ["menuItem:share:appMessage","menuItem:share:timeline","menuItem:share:qq","menuItem:share:weiboApp","menuItem:favorite"]
        //});

        bindContentClick();

        $(".insert-image-btn").on('click', function (e) {
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
                            $loadingToast.fadeIn(100);
                            var serverId = res.serverId;
                            $.post('/cos/upload', {mediaID: serverId}, function (response) {
                                $loadingToast.fadeOut(100);
                                //var path = "/res/i/30e22ad1-6a9c-4ed3-b4ad-7082fa57bbbd-jpeg";
                                var path = response.data;
                                $(".art-content").prepend('<p style="margin: 0em 0.5em;max-width: 100%;color: rgb(0, 0, 0);white-space: normal;line-height: 1.75em;box-sizing: border-box !important;word-wrap: break-word !important;"><img src="' + path + '" style="max-width:100%;height:auto;"></p>')
                                bindContentClick();
                            })
                        }
                    });
                }
            });
        })
    });

    function bindAddContent() {
        $(".edit-btn").off("click");
        $(".edit-btn").on("click", function () {
            $("#artContentArea").val("");
            $("#contentDialog").fadeIn(200);
        });
    }

    function bindImageUpload($panel) {
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
                        $loadingToast.fadeIn(100);
                        $.post('/cos/upload', {mediaID: serverId}, function (response) {
                            $loadingToast.fadeOut(100);
                            var path = json.data;
                            $panel.after('<p style="margin: 0em 0.5em;max-width: 100%;color: rgb(0, 0, 0);white-space: normal;line-height: 1.75em;box-sizing: border-box !important;word-wrap: break-word !important;"><img src="' + path + '" style="max-width:100%;height:auto;"></p>')
                            bindContentClick();
                        })
                    }
                });
            }
        });
    }

    function bindContentClick() {
        $(".art-content p").off("click");
        $(".art-content p").on("click", function () {
            var $this = $(this);
            bindPClick($this);
        })
    }

    function bindPClick($this) {
        recoveryContent();
        console.log($this.children("img")[0]);
        var $html = $this.clone();
        var panel = $($("#selectBox").html());
        panel.find(".select-box__bd").append($html);
        panel.find(".delete-btn").on("click", deleteEdit)
        if ($this.children("img").length > 0) {
            panel.find(".edit-btn").css("display", "none");
        }
        panel.find(".img-btn").on("click", function () {
            bindImageUpload(panel);
        });
        $this.replaceWith(panel);
        targetPanel = panel;
        bindAddContent();
    }

    function recoveryContent() {
        $(".art-content .select-box").each(function () {
            var $this = $(this);
            var html = $this.find(".select-box__bd").html()
            $this.replaceWith(html);
        });
        bindContentClick();
    }

    function deleteEdit() {
        var panel = $(this).parent().parent();
        var $panel = $(panel);
        $panel.remove();
    }
</script>
</body>
</html>