$(function () {

//关注
    $(document).on('click', '.follow', function () {
        var current = $(this);
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $(".user-card").hide();
            $("#loginModalBtn").click();
            return false;
        }
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var uid = $(this).attr('data-uid');
        var default_text = $(this).find('.default-text');
        var hover_text = $(this).find('.hover-text');
        $.ajax({
            url: "/relationships/follow",
            type: 'POST',
            data: {
                uid: uid,
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    default_text.html("<b>已关注</b>");
                    hover_text.html("<b>取消关注</b>");
                    current.removeClass("follow").addClass("notfollow");

                } else {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                // layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2},function () {                    window.location.reload();                });
            }
        });
    });

//取消关注
    $(document).on('click', '.notfollow', function () {
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $(".user-card").hide();
            $("#loginModalBtn").click();
            return false;
        }
        var current = $(this);

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var uname = $("meta[name='_username']").attr("content");
        var uid = $(this).attr('data-uid');
        var default_text = $(this).find('.default-text');
        var hover_text = $(this).find('.hover-text');
        $.ajax({
            url: "/" + uname + "/relationships/notfollow",
            type: 'POST',
            data: {
                uid: uid,
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    default_text.html("<b>关注</b>");
                    hover_text.html("<b>关注</b>");
                    current.removeClass("notfollow").addClass("follow");
                } else {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                // layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2},function () {                    window.location.reload();                });
            }
        });
    });
})