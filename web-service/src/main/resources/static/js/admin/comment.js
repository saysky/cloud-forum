"use strict";
//# sourceURL=main.js

// DOM 加载完再执行
$(function () {

// 获取用户列表
    function getDataList(pageIndex) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: url,
            type: 'GET',
            data: {
                "async": true,
                "pageIndex": pageIndex,
                "keywords": $("#user-keywords").val(),
                "status": $("#status").val(),
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $("#mainContainer").html(data);
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


    //点击状态筛选
    $(document).on('click', '.search-status', function () {
        var current = $(this);
        var order = $(this).attr('data-search-status');
        $("#status").val(order);
        getDataList(1);
    });


    // 删除评论
    $(document).on("click", ".delete-comment", function () {
        var url = "/admin/comment/" + $(this).attr("data-id");
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        layer.confirm('你确认要删除该评论吗', {
            btn: ['确认', '取消'], //按钮
            icon: 8
        }, function () {
            $.ajax({
                url: url,
                type: 'DELETE',
                beforeSend: function (request) {
                    request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
                },
                success: function (data) {
                    if (data.success) {
                        layer.alert("删除成功", {icon: 1});
                        getDataList(pageIndex);
                    } else {
                        layer.alert(data.message, {icon: 2});
                    }
                },
                error: function () {
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});                }
            });
        })
    });

    // 恢复评论
    $(document).on("click", ".restore-comment", function () {

        var url = "/admin/comment/restore/" + $(this).attr("data-id");
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: url,
            type: 'PUT',
            beforeSend: function (request) {
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    // 重新刷新主界面
                    layer.alert("还原成功", {icon: 1});
                    getDataList(pageIndex);
                } else {
                    alert(data.message);
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});            }
        });

    })
});