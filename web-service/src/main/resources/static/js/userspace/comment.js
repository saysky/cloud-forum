"use strict";

// DOM 加载完再执行
$(function () {


    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");


    function getDataList(pageIndex) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/manage/comments',
            type: 'GET',
            data: {
                "async": true,
                "type": $("#tab-pane").attr("data-comment-type"),
                "pageIndex": pageIndex,
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $("#tab-content").html(data);
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});                window.location.reload();
            }
        });
    };

    //分页获取评论列表
    $(document).on('click', '.page-link', function () {
        if ($(this).hasClass('current')) {
            return false;
        }
        var pageIndex = $(this).attr("pageIndex");
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

    $(document).on('click', '.comment-tab', function () {
        var data = $(this).attr("data-comment-type");
        $("#tab-pane").attr("data-comment-type", data);
        getDataList(1);
    })

    // 删除评论
    $(document).on("click", ".delete-comment", function () {
        var commentId = $(this).attr("data-comment-id");
        layer.confirm('你确认要删除吗？', {
            btn: ['确认', '取消'], //按钮
            icon: 8
        }, function () {
            $.ajax({
                url: "/comments/" + commentId,
                type: 'DELETE',

                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (response) {
                    if (response.success) {
                        layer.msg("删除成功", {icon: 1});
                        getDataList(pageIndex);
                    } else if (response.success == false) {
                        layer.alert(response.message, {icon: 2});
                    }
                },
                error: function () {
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});                }
            });
        });
    });


})

