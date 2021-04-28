if (!/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
    $("#main-search-input").focus(function () {
        $("#main-search-input").animate({width: "350px"}, "fast", "swing");
        $(this).parents("form").next("ul").hide();
    });
    $("#main-search-input").blur(function () {
        $("#main-search-input").animate({width: "250px"}, "fast", function () {
            $(this).parents("form").next("ul").show();
        });
    });
}

$(document).on('click', '.show-login-modal', function () {
    var isLogin = $("meta[name='_username']").attr("content");
    if (isLogin == null) {
        $("#loginModalBtn").click();
        return false;
    }
    ;
})

// DOM 加载完再执行
$(function () {

    $(document).on('click', '#message-size-btn', function () {
        $.ajax({
            url: "/messages/recentNotRead",
            type: "get",
            dataType: 'json',
            success: function (data) {
                console.log(data.body);
                var content = '<ul class="menu">';
                var count = 0;
                $.each(data.body, function (i, item) {
                    content +=
                        '<li>' +
                        '<a href="#" >' +
                        '<div class="pull-left">' +
                        '<img class="img-circle" src="' + item.avatar + '" alt="头像">' +
                        '</div>' +
                        '<h4>' + item.name +
                        '<small><span class="text-maroon">' + item.count + '条未读</span> <i class="fa fa-clock-o"></i> ' + item.createTime + '</small>' +
                        '</h4>' +
                        '<p class="nowrap">' + item.content + '</p>' +
                        '</a>' +
                        '</li>';
                    count += item.count;
                });
                content += '</ul>';
                $("#not-read-message-list").html(content);
            },
            error: function () {
            }
        })
    });

    $(document).on('click', '#notice-size-btn', function () {
        $.ajax({
            url: "/notice/recentNotRead",
            type: "get",
            dataType: 'json',
            success: function (data) {
                var content = '<ul class="menu">';
                var count = 0;
                $.each(data.body, function (i, item) {
                    content +=
                        '<li>' +
                        '<a href="/manage/notices" >' +
                        '<i class="' + item.style + '"></i>' +
                        item.count + " 条" + item.content +
                        '</a>' +
                        '</li>';
                    count += item.count;
                });
                content += '</ul>';
                $("#not-read-notice-list").html(content);
                $(".notice-size").html(count);

            },
            error: function () {
            }
        })
    });


    $(document).on('click', '#loginModalBtn', function () {

        $.ajax({
            url: "/loginModal",
            type: 'GET',
            data: {
                'async': true
            },
            success: function (data) {
                $("#login-modal-body").html(data);
                $("#loginModal").modal('show');
            },
            error: function () {
            },
        });
    });

    //登录提交
    $(document).on("click", "#login_btn", function () {
        var randomAnim = Math.floor(Math.random() * 7);
        if ($("#kaptcha").val().length < 1) {
            layer.alert("验证码不可为空!", {icon: 2, anim: randomAnim});
            return false;
        }

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/login",
            type: 'POST',
            data: $("#login_form").serialize(),
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    window.location.href = "/";
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2, anim: randomAnim});
                    $(".changeCaptcha").click();
                }
            }
        });
        return false;
    });

    //切换验证码
    $(document).on('click', '.changeCaptcha', function () {
        var num = Math.random() * (1000 - 1) + 1;
        var url = "/getKaptchaImage?random=" + num;
        $(".captcha").attr("src", url);
    });


    $(document).on('click', ".post-box > li", function () {
        var href = $(this).find('.title-link').attr('href');
        window.location.href = href;
    })

})
