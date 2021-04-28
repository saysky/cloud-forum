$(function () {


    // 获取文章数据
    function getDataList(pageIndex) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/manage/answers",
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