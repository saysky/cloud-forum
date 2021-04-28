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
            url: '/admin/tag',
            type: 'GET',
            data: {
                "async": true,
                "pageIndex": pageIndex,
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


    /**
     * 添加标签
     */
    $(document).on('click', '#tag-submit', function () {
        if ($("#name").val() == null || $("#name").val() == "") {
            layer.msg('标签名不可为空', {icon: 2});
            return false;
        }
        $.ajax({
            url: "/admin/tag",
            type: 'POST',
            data: $("#tagForm").serialize(),
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    layer.msg('保存成功', {icon: 1});
                    getDataList(1);
                    $("#tag-reset").click();
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2})
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});            }
        });
        return false;
    });

    //编辑标签
    $(document).on('click', '.edit-tag', function () {
        var id = $(this).parents("tr").attr("data-id");
        $.ajax({
            url: '/admin/tag/edit/' + id,
            type: 'GET',
            data: {
                "async": true,
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $("#left-box-body").html(data);
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});                window.location.reload();
            }
        });

    })

    // 删除标签
    $(document).on("click", ".delete-tag", function () {
        var tagId = $(this).parents("tr").attr("data-id");
        layer.confirm('你确认要删除吗？', {
            btn: ['确认', '取消'], //按钮
            icon: 8
        }, function () {
            $.ajax({
                url: "/admin/tag/" + tagId,
                type: 'DELETE',

                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (response) {
                    if (response.success) {
                        layer.msg("删除成功", {icon: 1});
                        getDataList(1);
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

