$(document).ready(function () {
    $('pre code').each(function (i, block) {
        hljs.highlightBlock(block);
    });
});


var editor = new Simditor({
    textarea: $('#editor'),
    placeholder: '写下你的答案...',
    pasteImage: true,
    toolbarFloat: true,
    defaultImage: '/components/simditor/images/image.png', //编辑器插入图片时使用的默认图片
    upload: {
        url: "/upload", //文件上传的接口地址
        fileKey: 'upload_file', //服务器端获取文件数据的参数名
        connectionCount: 3,
        leaveConfirm: '正在上传文件'
    },
});

$(function () {

    //对回答进行回答
    $(document).on('click', '.comment-answer', function () {
        $(this).parents('.post-offset').find('.widget-comments').toggle();
    })

    // 异步获取回答列表
    function getAnswerList(questionId, pageIndex) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var orderby = $("#orderby").val();
        $.ajax({
            url: '/questions/' + questionId,
            type: 'GET',
            data: {
                "async": true,
                "questionId": questionId,
                "pageIndex": pageIndex,
                "orderby": orderby
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $("#answer-list").html(data);

            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
            }
        });
    };

    //分页获取回答列表
    $(document).on('click', '.page-link', function () {
        if ($(this).hasClass('current')) {
            return false;
        }
        var pageIndex = $(this).attr("pageIndex");
        getAnswerList(questionId, pageIndex);
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
            getAnswerList(questionId, pageIndex);
        }
    });

    //提交回答
    $(document).on("click", "#answer-submit", function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var answerContent = $("#editor").val();
        if (answerContent.length < 1) {
            layer.msg("回答不可为空!", {icon: 2});
            return false;
        }

        $.ajax({
            url: "/answers",
            type: 'POST',
            data: {
                questionId: questionId,
                content: answerContent
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                console.log(data);
                if (data.success) {
                    layer.msg("回答成功！", {icon: 1});
                    getAnswerList(questionId);
                    $(".simditor-body").html("");
                    $("#editor").val("");
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
    });

    //对回答进行评论
    $(document).on('click', '.postComment', function () {
        let t = $(this);
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var answerId = t.attr("data-answer-id");
        var commentId = t.attr("data-comment-id");
        var content = t.parents('form').find('textarea').val();
        if (content.length < 3) {
            layer.msg("评论字数太少啦!", {icon: 2});
        }
        $.ajax({
            url: "/answers/comment",
            type: 'POST',
            data: {
                answerId: answerId,
                commentId: commentId,
                content: content
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    layer.msg("评论成功！", {icon: 1});
                    getAnswerList(questionId);
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
    });

    $(document).on('click', '.comment-answer', function () {
        var id = $(this).attr('data-comment-id');
        var textarea = $(this).parents('.post-offset').find('textarea');
        var nickname = $(this).attr('data-user-nickname');
        textarea.attr('data-comment-id', id);
        textarea.attr('placeholder', '@' + nickname + ' ');
        textarea.focus();

    })

    //对评论进行回复
    $(document).on('click', '.reply-answer', function () {
        var id = $(this).attr('data-comment-id');
        var nickname = $(this).attr('data-user-nickname');
        var textarea = $(this).parents('.post-offset').find('textarea');
        textarea.attr('data-comment-id', id);
        textarea.val('@' + nickname + ' ');
        textarea.focus();

    })

    //    问题访问量+1
    function increaseViewCount() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        //如果没有当前页面的cookie，则添加访问量
        if ($.cookie("questionId") != questionId) {
            $.ajax({
                async: false,
                type: "POST",
                url: "/questions/increaseView",
                data: {questionId: questionId},
                dataType: "text",
                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (data) {
                    //添加cookie
                    $.cookie(
                        "questionId", questionId, {"path": "/"}
                    );
                },
            });
        }
    }

    increaseViewCount();

    /**
     * 评论显示排序
     */
    $(document).on('click', '.answer-orderby', function () {
        var order = $(this).attr('data-orderby');
        $("#orderby").val(order);
        getAnswerList(questionId, pageIndex);
    });


//对回复进行点赞
    $(document).on('click', '.like', function () {
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $("#loginModalBtn").click();
            return false;
        }

        var current = $(this);
        var answerId = current.attr("data-id");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/zan/answer",
            type: 'POST',
            data: {
                "answerId": answerId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                console.log(data);
                if (data.success) {
                    window.location.reload();
                } else if (data.success == false) {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function (data) {
                //未登录
                if ("不允许访问" == data.responseJSON.message) {
                    window.location.href = "/login";
                }
            }
        });
    });


//对回复进行点踩
    $(document).on('click', '.hate', function () {
        var current = $(this);
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $("#loginModalBtn").click();
            return false;
        }
        var answerId = current.attr("data-id");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/cai/answer",
            type: 'POST',
            data: {
                "answerId": answerId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                console.log(data);
                if (data.success) {
                   window.location.reload();
                } else if (data.success == false) {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function (data) {
                //未登录
                if ("不允许访问" == data.responseJSON.message) {
                    window.location.href = "/login";
                }
            }
        });
    });


    //采纳答案
    $(document).on('click', '.accept-answer', function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var answerId = $(this).attr('data-answer-id');
        $.ajax({
            url: "/answers/accept",
            type: 'POST',
            data: {
                "answerId": answerId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function () {
                getAnswerList(questionId, pageIndex);
            },
            error: function (data) {

            }
        });
    })



})