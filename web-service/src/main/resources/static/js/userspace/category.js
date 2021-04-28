"use strict";

// DOM 加载完再执行
$(function () {



    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");


    function trim(str) { //删除左右两端的空格
        return str.replace(/(^\s*)|(\s*$)/g, "");
    }


    function getCategoryList(pageIndex) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/manage/categorys',
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
        getCategoryList(pageIndex);
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
            getCategoryList(pageIndex);
        }
    });


    /**
     * 添加分类
     */
    $(document).on('click', '#category-form-submit', function () {

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
                    layer.msg('添加成功', {icon: 1});
                    getCategoryList();
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2})
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});            }
        });
        return false;
    });

    //点击分类名称，触发编辑
    $(document).on('click', '.category-input-disabled', function () {
        //先移除其它的点击样式
        $(".category-input").addClass("category-input-disabled");
        $(".category-input").nextAll("a").remove();
        var input = $(this);
        input.removeClass("category-input-disabled");
        input.after(' <a href="javascipt:void(0)" class="btn-save">保存</a>\n' +
            '<a href="javascipt:void(0)" class="btn-cancel-edit">取消</a>');
    });

    //点击编辑按钮
    $(document).on('click', '.edit-category', function () {
        //先移除其它的点击样式
        $(".category-input").addClass("category-input-disabled");
        $(".category-input").nextAll("a").remove();
        var input = $(this).parents("tr").find(".category-input");
        input.removeClass("category-input-disabled");
        input.after(' <a href="javascipt:void(0)" class="btn-save">保存</a>\n' +
            '<a href="javascipt:void(0)" class="btn-cancel-edit">取消</a>');
    });


    //修改分类名称提交
    $(document).on('click', '.btn-save', function () {
        var input = $(this).prevAll(".category-input");
        var categoryId = $(this).parents("tr").attr("data-id");
        if (trim(input.val()).length < 1) {
            layer.alert('分类名称不可为空', {icon: 2});
            return false;
        }
        if (trim(input.val()).length > 20) {
            layer.alert('分类名称不可超过20个字符', {icon: 2});
            return false;
        }
        $.ajax({
            url: "/category/" + categoryId + "/name",
            type: 'PUT',
            data: {
                id: categoryId,
                categoryName: input.val(),
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    layer.msg('修改成功', {icon: 1});
                    getCategoryList();
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2})
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});            }
        });

    })

    //取消保存
    $(document).on('click', '.btn-cancel-edit', function () {
        var input = $(this).prevAll(".category-input");
        input.addClass("category-input-disabled");
        input.nextAll("a").remove();
    })


    // 删除分类
    $(document).on("click", ".delete-category", function () {
        var categoryId = $(this).parents("tr").attr("data-id");
        layer.confirm('你确认要删除吗？', {
            btn: ['确认', '取消'], //按钮
            icon:8
        }, function () {
            $.ajax({
                url: "/category/" + categoryId,
                type: 'DELETE',

                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (response) {
                    if (response.success) {
                        layer.msg("删除成功", {icon: 1});
                        getCategoryList();
                    } else if (response.success == false) {
                        layer.alert(response.message, {icon: 2});
                    }
                },
                error: function () {
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});                }
            });
        });
    });

    // 显示分类
    $(document).on("click", ".show-category", function () {
        var categoryId = $(this).parents("tr").attr("data-id");
        $.ajax({
            url: "/category/" + categoryId + "/isHidden",
            type: 'PUT',
            data: {
                id: categoryId,
                isHidden: "N",
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (response) {
                if (response.success) {
                    layer.msg("已将该分类和对应的文章公开", {icon: 1});
                    getCategoryList();
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2});
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});            }
        });
    });

    // 隐藏分类
    $(document).on("click", ".hidden-category", function () {
        var categoryId = $(this).parents("tr").attr("data-id");
        $.ajax({
            url: "/category/" + categoryId + "/isHidden",
            type: 'PUT',
            data: {
                id: categoryId,
                isHidden: "Y",
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (response) {
                if (response.success) {
                    layer.msg("已将该分类和对应的文章隐藏", {icon: 1});
                    getCategoryList();
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2});
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});            }
        });
    });

    // 移动位置，向上移动
    $(document).on("click", ".btn-position-up", function () {
        var currentId = $(this).parents("tr").attr("data-id");
        var upId = $(this).parents("tr").prev("tr").attr("data-id");
        if (upId != null) {
            $.ajax({
                url: "/category/position",
                type: 'PUT',
                data: {
                    currentId: currentId,
                    upId: upId,
                },
                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (response) {
                    if (response.success) {
                        layer.msg("操作成功", {icon: 1});
                        getCategoryList();
                    } else if (response.success == false) {
                        layer.alert(response.message, {icon: 2});
                    }
                },
                error: function () {
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});                }
            });
        }
    });

    // 移动位置，向x下移动
    $(document).on("click", ".btn-position-down", function () {
        var currentId = $(this).parents("tr").attr("data-id");
        var downId = $(this).parents("tr").next("tr").attr("data-id");
        if (downId != null) {
            $.ajax({
                url: "/category/position",
                type: 'PUT',
                data: {
                    currentId: currentId,
                    downId: downId,
                },
                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (response) {
                    if (response.success) {
                        layer.msg("操作成功", {icon: 1})
                        getCategoryList();
                    } else if (response.success == false) {
                        layer.alert(response.message, {icon: 2});
                    }
                },
                error: function () {
                    layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});                }
            });
        }
    });

})

