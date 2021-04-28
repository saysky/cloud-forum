"use strict";

// DOM 加载完再执行
$(function () {

    /**
     * 添加幻灯片
     */
    $(document).on('click', '#slide-submit', function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/admin/settings/slide",
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "id": $('#slide-id').val(),
                "title": $('#title').val(),
                "picture": $('#picture').val(),
                "guid": $('#guid').val(),
                "status": $("#status").val(),
                "position": $("#position").val()
            }),
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    window.location.href = "/admin/settings/slide";
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2})
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


    // 删除标签
    $(document).on("click", ".delete-slide", function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var tr = $(this).parents("tr");
        var id = tr.attr("data-id");
        layer.confirm('你确认要删除吗？', {
            btn: ['确认', '取消'], //按钮
            icon: 8
        }, function () {
            $.ajax({
                url: "/admin/settings/slide/" + id,
                type: 'DELETE',

                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (response) {
                    if (response.success) {
                        tr.remove();
                        layer.msg("删除成功", {icon: 1});
                    } else if (response.success == false) {
                        layer.alert(response.message, {icon: 2});
                    }
                },
                error: function () {
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                        window.location.reload();
                    });
                }
            });
        });
    });


})

