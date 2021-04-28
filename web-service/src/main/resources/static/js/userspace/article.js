$(function () {
    function trim(str) { //删除左右两端的空格
        return str.replace(/(^\s*)|(\s*$)/g, "");
    };


    // 获取文章数据
    function getDataList(pageIndex, status, keywords) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/manage/articles",
            type: 'GET',
            data: {
                "async": true,
                "status": status,
                "keywords": keywords,
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


    //分页获取文章列表
    $(document).on('click', '.page-link', function () {
        var pageIndex = $(this).attr("pageIndex");
        var keywords = $("#keywords").val();
        if ($(this).hasClass('current')) {
            return false;
        }
        var status = $("#tab-pane").attr("data-post-status");
        getDataList(pageIndex, status, keywords);
    });


//跳转到指定的页号
    $(document).on('keydown', '.jump-page-size', function (event) {
        var max = parseInt($(this).attr("max"));
        var pageIndex = parseInt($(this).val());
        var keywords = $("#keywords").val();
        var status = $("#tab-pane").attr("data-post-status");
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
            getDataList(pageIndex, status, keywords);
        }
    });

    //文章列表
    $(document).on('click', '.post-status-tab', function () {
        var pageIndex = 1;
        var status = $(this).attr("data-post-status");
        var keywords = $("#keywords").val();
        getDataList(pageIndex, status, keywords);
    });


    //发布文章
    $(document).on("click", "#article-submit", function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        if (trim($("#title").val()).length < 2) {
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
        } else if ($("#editor").val().length > 50000000) {
            layer.alert("似乎写的有点多哦！", {icon: 5});
            return false;
        } else if ($("#category").val() == null) {
            layer.alert("请选择个人分类！", {icon: 5});
            return false;
        }
        $.ajax({
            url: '/article/edit',
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "id": $('#article-id').val(),
                "title": $('#title').val(),
                "content": $('#editor').val(),
                "tags": $("#tags").val().split(/[,，]/, 5).toString(),
                "categoryId": $('#category').val(),
                "status": $("#status").val(),
                "isAllowComment": $("#isAllowComment").val(),
            }),
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                var storage = window.localStorage;
                storage.clear();
                console.log(data);
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

    //添加分类框显示和隐藏
    $(document).on('click', '#add-category-btn', function () {
        $("#add-category-input").toggle();
    })

    //添加分类
    $(document).on('click', '#category-submit', function () {

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        if (trim($("#category_name").val()).length < 1) {
            layer.alert('分类名称不可为空', {icon: 2});
            return false;
        }
        if (trim($("#category_name").val()).length > 20) {
            layer.alert('分类名称不可超过20个字符', {icon: 2});
            return false;
        }
        $.ajax({
            url: "/category",
            type: 'POST',
            data: {
                categoryName: $("#category_name").val(),
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    $("#category_name").val('');
                    layer.msg('添加成功', {icon: 1});
                    console.log(response.body);
                    $("#category").append('<option value="' + response.body.id + '" selected="selected">' + response.body.name + '</option>')
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

    //删除文章
    $(document).on('click', '.delete-article', function () {
        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var bookmarkId = $(this).attr('data-article-id');
        var tr = $(this).parents("tr");

        layer.confirm('你确认要删除吗？', {
            btn: ['确认', '取消'], //按钮
            icon: 8
        }, function () {
            $.ajax({
                url: '/article/' + bookmarkId,
                type: 'DELETE',
                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (data) {
                    if (data.success) {
                        layer.msg("删除成功!", {icon: 1});
                        tr.remove();
                    } else if (data.success == false) {
                        layer.alert(data.message, {icon: 2});
                        // window.location.reload();
                    }
                },
                error: function () {
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                        window.location.reload();
                    });
                    window.location.reload();
                }
            });
        })
    });

    //置顶文章
    $(document).on('click', '.stick-article', function () {
        var id = $(this).attr('data-id');
        var keywords = $("#keywords").val();
        var status = $("#tab-pane").attr("data-post-status");
        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: '/manage/article/stick',
            type: 'POST',
            data: {
                id: id
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success == true) {
                    layer.alert('置顶成功！', {icon: 1}, function () {
                        window.location.reload();
                    })
                } else if (data.success == false) {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
            }
        });
    })

    //取消置顶文章
    $(document).on('click', '.cancel-stick-article', function () {
        var id = $(this).attr('data-id');
        var keywords = $("#keywords").val();
        var status = $("#tab-pane").attr("data-post-status");
        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: '/manage/article/cancelStick',
            type: 'POST',
            data: {
                id: id
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success == true) {
                    layer.alert('取消置顶成功！', {icon: 1}, function () {
                        window.location.reload();
                    })
                } else if (data.success == false) {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
            }
        });
    })


});