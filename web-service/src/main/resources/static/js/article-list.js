$(function () {

// 获取文章列表
    function getArticleList(pageIndex) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: '/articles',
            type: 'GET',
            data: {
                "async": true,
                "pageIndex": pageIndex,
                "orderby": $("#search-order").val(),
                "keywords": $("#search-keyword").val()
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
        getArticleList(pageIndex);
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
            getArticleList(pageIndex);
        }
    });
    $(document).on('click', '#search-btn', function () {
        var input = $("#search-keyword").val();
        if (input == null || input == "") {
            layer.msg("请输入关键字", {icon: 2});
        } else {
            $("#article-search-form").submit();
        }
    });
    $(document).on('click', '.search-order', function () {
        var current = $(this);
        var order = $(this).attr('data-search-order');
        $("#search-order").val(order);
        getArticleList(1);
        $('.fa-check').attr('style','visibility:hidden');
        current.find('.fa-check').attr('style','visibility:visible');
    });

    $(document).on('submit','#article-search-form',function () {
        getArticleList(1);
        return false;
    })

    $(document).on("click",".ranking-list-item-more",function () {
        $(".ranking-list-item").removeClass("hide");
        $(".ranking-list-item-more").hide();
    });



})