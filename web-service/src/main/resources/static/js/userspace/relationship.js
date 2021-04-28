$(function () {
// 获取关系列表
    function getRelationshipList(pageIndex, dataType) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: '/manage/relationships/' + dataType,
            type: 'GET',
            data: {
                "async": true,
                "pageIndex": pageIndex,
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $("#right-box-body").html(data);
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
        var dataType = $("#relationship-nav-tabs").attr('data-type');
        getRelationshipList(pageIndex, dataType);
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
            var dataType = $("#relationship-nav-tabs").attr('data-type');
            getRelationshipList(pageIndex, dataType);
        }
    });


//重新获取数据
    $(document).on('click', '.relationship-tab-btn', function () {
        var dataType = $(this).attr("data-type");
        getRelationshipList(1, dataType);
    });

//取消关注
    $(document).on('click', '.js-concern-already,.js-concern-mutual', function () {
        var current = $(this);

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var url = "/manage/relationships/";

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                optType: current.attr('opt-type'),
                userId: current.attr('data-uid'),
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                current.hide();
                current.prev("a").show();
                current.parents(".box").find(".fan-size").html(data.body);
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});            }
        });
    });

//关注
    $(document).on('click', '.js-concern-follow', function () {
        var current = $(this);

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var url = "/manage/relationships";

        $.ajax({
            url: url,
            type: 'POST',
            data: {
                optType: current.attr('opt-type'),
                userId: current.attr('data-uid'),
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                current.hide();
                current.next("a").show();
                current.parents(".box").find(".fan-size").html(data.body);
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});            }
        });
    });
})