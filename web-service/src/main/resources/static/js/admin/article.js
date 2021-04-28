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
                "orderby": $("#orderby").val()
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $("#mainContainer").html(data);
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});
            }
        });
    };


    //分页获取文章列表
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

    //点击排序筛选
    $(document).on('click', '.search-order', function () {
        var order = $(this).attr('data-search-order');
        $("#orderby").val(order);
        getDataList(1);
    });

    //点击状态筛选
    $(document).on('click', '.search-status', function () {
        var order = $(this).attr('data-search-status');
        $("#status").val(order);
        getDataList(1);
    });


    // 删除文章
    $(document).on("click", ".delete-article", function () {
        var current = $(this);
        var url = "/admin/article/" + $(this).attr("id");
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        layer.confirm('你确认要删除该用户吗', {
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
                        // 重新刷新主界面
                        layer.alert("删除成功", {icon: 1});
                        getDataList(pageIndex);
                    } else {
                        alert(data.message);
                    }
                },
                error: function () {
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});
                }
            });
        })
    });

    // 删除文章
    $(document).on("click", ".delete-article", function () {
        var url = "/admin/article/" + $(this).attr("data-id");
        // 获取 CSRF Token
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        layer.confirm('你确认要删除该文章吗', {
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
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});
                }
            });
        })
    });

    // 恢复文章
    $(document).on("click", ".restore-article", function () {

        var url = "/admin/article/restore/" + $(this).attr("data-id");
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
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});
            }
        });

    })


    $(document).on('click', '#selectAll', function () {
        var ch = document.getElementsByName("checkbox");
        if (document.getElementById("selectAll").checked == true) {
            for (var i = 0; i < ch.length; i++) {
                ch[i].checked = true;
            }
        } else {
            for (var i = 0; i < ch.length; i++) {
                ch[i].checked = false;
            }
        }
    });


    $(document).on('click', '#clearup', function () {
        layer.confirm('你确定是否清空？', function (index) {

            // 获取 CSRF Token
            var csrfToken = $("meta[name='_csrf']").attr("content");
            var csrfHeader = $("meta[name='_csrf_header']").attr("content");

            $.ajax({
                type: 'POST',
                url: '/admin/article/clear',
                async: false,
                beforeSend: function (request) {
                    request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
                },
                success: function (data) {
                    if (data.code == 0) {
                        layer.alert("清空失败！", {icon: 2});
                    } else {
                        layer.alert("清空成功！", {icon: 1});
                        setTimeout(function(){ window.location.reload();; }, 1000);
                    }

                }
            });
            layer.close(index);
        });
    });

    $(document).on('click', '#batchRemove', function () {
        var checkedNum = $("input[name='checkbox']:checked").length;
        if (checkedNum == 0) {
            alert("请至少选择一项!");
            return false;
        }
        layer.confirm('你确定是否批量删除？', function (index) {

            // 获取 CSRF Token
            var csrfToken = $("meta[name='_csrf']").attr("content");
            var csrfHeader = $("meta[name='_csrf_header']").attr("content");

            var checkedList = new Array();
            $("input[name='checkbox']:checked").each(function () {
                checkedList.push($(this).val());
            });
            $.ajax({
                type: 'POST',
                url: '/admin/article/batchRemove',
                async: false,
                beforeSend: function (request) {
                    request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
                },
                data: {
                    'ids': checkedList.toString()
                },
                success: function (data) {
                    if (data.code == 0) {
                        layer.alert("删除失败！", {icon: 2});
                    } else {
                        layer.alert("删除成功！", {icon: 1});
                        setTimeout(function(){ window.location.reload();; }, 1000);
                    }

                }
            });
            layer.close(index);
        });
    })


});