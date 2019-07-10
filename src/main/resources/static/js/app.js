window.alert = function (msg, callback) {
    if ($('#jsAlertDialog').length <= 0) {
        var dialogHTML = '<div class="js_dialog" id="jsAlertDialog" style="opacity: 1;">';
        dialogHTML += '<div class="weui-mask"></div>';
        dialogHTML += '<div class="weui-dialog">';
        dialogHTML += '<div class="weui-dialog__bd weui-alert-dialog__msg">'+msg+'</div>';
        dialogHTML += '<div class="weui-dialog__ft">';
        dialogHTML += '<a href="javascript:;" class="weui-dialog__btn weui-dialog__btn_primary">知道了</a>';
        dialogHTML += '</div>';
        dialogHTML += ' </div>';
        dialogHTML += '</div>';
        $('body').append(dialogHTML);
    } else {
        $(".weui-alert-dialog__msg").html(msg);
    }
    $(".weui-dialog__btn").off("click");
    $('.weui-dialog__btn').on('click', function () {
        $("#jsAlertDialog").fadeOut(100);
        if (callback=!undefined) {
            callback();
        }
    });
    $("#jsAlertDialog").fadeIn(100);
}