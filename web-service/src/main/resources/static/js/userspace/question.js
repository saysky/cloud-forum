$(function () {

    function trim(str) { //删除左右两端的空格
        return str.replace(/(^\s*)|(\s*$)/g, "");
    };


    //发布问题
    $(document).on("click", "#ask-submit", function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        if (trim($("#title").val()).length < 5) {
            layer.alert("标题似乎有点短哦！", {icon: 5});
            return false;
        } else if (trim($("#title").val()).length > 200) {
            layer.alert("标题未免太长了些吧！", {icon: 5});
            return false;
        } else if ($("#editor").val() == null) {
            layer.alert("写点内容吧！", {icon: 5});
            return false;
        } else if ($("#editor").val().length < 10) {
            layer.alert("再多写一点内容嘛！", {icon: 5});
            return false;
        } else if ($("#editor").val().length > 500000) {
            layer.alert("似乎写的有点多哦！", {icon: 5});
            return false;
        }
        $.ajax({
            url: '/question',
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "id": $('#question-id').val(),
                "title": $('#title').val(),
                "content": $('#editor').val(),
                "tags": $("#tags").val().split(/[,，]/, 5).toString(),
            }),
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                var storage = window.localStorage;
                storage.clear();
                if (data.success) {
                    // 成功后，重定向
                    layer.alert("发布成功！", {icon: 1}, function () {
                        window.location = data.body;
                    })
                } else {
                    layer.alert(data.message, {icon: 2});
                }


            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
            }
        });
        return false;
    });

    // 获取文章数据
    function getDataList(pageIndex) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/manage/questions",
            type: 'GET',
            data: {
                "async": true,
                "status": $("#tab-pane").attr("data-post-status"),
                "pageIndex": pageIndex,

            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $(".tab-content").html(data);
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
            }
        });
    };


    //分页获取问题列表
    $(document).on('click', '.page-link', function () {
        var pageIndex = $(this).attr("pageIndex");
        if ($(this).hasClass('current')) {
            return false;
        }
        getDataList(pageIndex);
    });


//跳转到指定的页号
    $(document).on('keydown', '.jump-page-size', function (event) {
        var max = parseInt($(this).attr("max"));
        var pageIndex = parseInt($(this).val());
        if (event.keyCode == "13") {//keyCode=13是回车键
            if (pageIndex == "" || pageIndex == null) {
                return false;
            }
            if (!/^\d+$/.test(pageIndex)) {
                pageIndex = 1;
            }
            if (pageIndex < 1) {
                pageIndex = 1;
            }
            if (pageIndex > max) {
                pageIndex = max;
            }
            getDataList(pageIndex);
        }
    });

    //文章列表
    $(document).on('click', '.post-status-tab', function () {
        var pageIndex = 1;
        var status = $(this).attr("data-post-status");
        $("#tab-pane").attr("data-post-status", status);
        getDataList(pageIndex);
    });


})