$(function () {


    function getDataList(pageIndex) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/admin/question",
            type: 'GET',
            data: {
                "async": true,
                "status": $("#status").val(),
                "pageIndex": pageIndex,

            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $("#mainContainer").html(data);
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

    // 删除问题
    $(document).on("click", ".delete-question", function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var id = $(this).attr("data-id");
        layer.confirm('你确认要删除吗？', {
            btn: ['确认', '取消'], //按钮
            icon: 8
        }, function () {
            $.ajax({
                url: "/admin/question/" + id,
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
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                        window.location.reload();
                    });
                }
            });
        });
    });

    // 还原问题
    $(document).on("click", ".restore-question", function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var id = $(this).attr("data-id");
        $.ajax({
            url: "/admin/question/restore/" + id,
            type: 'PUT',
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (response) {
                if (response.success) {
                    layer.msg("还原成功", {icon: 1});
                    getDataList(1);
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


})