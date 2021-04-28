$(function () {

    // 获取评论列表
    function getCommentList(articleId, pageIndex, order) {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/articles/' + articleId,
            type: 'GET',
            data: {
                "async": true,
                "articleId": articleId,
                "pageIndex": pageIndex,
                "orderby": order
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                $("#comment-wrapper").html(data);

            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
            }
        });
    };

    $(document).on('click', '.tabs-order .new-sort', function () {
        getCommentList(articleId, pageIndex, "new");
    });
    $(document).on('click', '.tabs-order .hot-sort', function () {
        getCommentList(articleId, pageIndex, "hot");
    });


    //分页获取评论列表
    $(document).on('click', '.page-link', function () {
        var order = $(".paging-box").attr("data-order");
        if ($(this).hasClass('current')) {
            return false;
        }
        var pageIndex = $(this).attr("pageIndex");
        getCommentList(articleId, pageIndex, order);
    });


    //跳转到指定的页号
    $(document).on('keydown', '.jump-page-size', function (event) {
        var max = parseInt($(this).attr("max"));
        var pageIndex = parseInt($(this).val());
        var order = $('.paging-box').attr('data-order');
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
            getCommentList(articleId, pageIndex, order);
        }
    });


// 发表评论
    $(document).on("click", ".comment-submit", function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var commentId = $(this).attr("data-comment-id");
        var replyId = $(this).attr("data-replyId");
        var commentContent = $(this).prev(".comment-content").val();
        if (commentContent.length < 1) {
            layer.msg("评论不可为空!", {icon: 2});
            return false;
        }
        if (commentContent.length > 500) {
            layer.msg("单条评论文字不能超过500个字符！", {icon: 2});
            return false;
        }
        $.ajax({
            url: "/comments",
            type: 'POST',
            data: {
                articleId: articleId,
                commentId: commentId,
                replyId: replyId,
                commentContent: commentContent
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    layer.msg("评论成功！", {icon: 1});
                    getCommentList(articleId);
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


//删除评论
    $(document).on('click', '.comment-delete-btn', function () {
        var currentNode = $(this);
        layer.confirm('你确认要删除吗？', {
            btn: ['确认', '取消'], //按钮
            icon: 8
        }, function () {

            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");

            var currentSize = parseInt($("#comment .results").html());
            //你确定要删除吗？
            var commentId = currentNode.parents(".reply-wrap").attr("data-comment-id");
            //删除
            $.ajax({
                url: "/comments/" + commentId + "?articleId=" + articleId,
                type: 'DELETE',
                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (data) {
                    if (data.success) {
                        layer.msg("删除成功！", {icon: 1});
                        currentNode.parents(".reply-wrap:first").remove();
                        $("#comment .results").html(currentSize - data.body);
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

        }, function () {
        });
    });

//点击回复按钮
    $(document).on('click', '.reply', function () {

        var replyId = $(this).attr("data-replyId");
        var current = $(this);
        $(".comment-list").find(".comment-send:not(:first-child)").hide();
        var comment_send = $(this).parents(".con").children(".comment-send");
        if (comment_send.length > 0) {
            comment_send.remove();
        } else {
            var commentId = current.parents(".reply-wrap:last").attr("data-comment-id");
            var img = $(".comment-send:first").find(".user-face").html();
            if (replyId == null) {
                var str = "";
                var placeholder = "说点什么";
            } else {
                var placeholder = "回复 @" + current.parents(".reply-con").find(".name:first").text() + '：';
                var str = " data-replyId=" + replyId;
            }
            var content =
                '<div class="comment-send">' +
                '<div class="user-face">' + img + '</div>' +
                '<div class="textarea-container">' +
                '<textarea cols="80" name="content" rows="5" placeholder="' + placeholder + '" class="comment-content"></textarea>' +
                '<button type="button" class="comment-submit" data-articleid="'
                + articleId + '"' + str +
                ' data-comment-id="' + commentId + '">发表评论</button>' +
                '</div>' +
                '<div class="comment-emoji">' +
                '<i class="face"></i>' +
                '<span class="text">表情</span>' +
                '</div>' +
                '                                                        <div class="emoji-box" style="display: none;">' +
                '                                                            <div class="emoji-title"><span>颜文字</span></div>' +
                '                                                            <div class="emoji-wrap">' +
                '                                                                <a class="emoji-list emoji-text"' +
                '               data-emoji-text="(⌒▽⌒)">(⌒▽⌒)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="（￣▽￣）">（￣▽￣）</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(=・ω・=)">(=・ω・=)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(｀・ω・´)">(｀・ω・´)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(〜￣△￣)〜">(〜￣△￣)〜</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(･∀･)">(･∀･)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(°∀°)ﾉ">(°∀°)ﾉ</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(￣3￣)">(￣3￣)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="╮(￣▽￣)╭">╮(￣▽￣)╭</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="( ´_ゝ｀)">(' +
                '                                                                ´_ゝ｀)</a><a class="emoji-list emoji-text"' +
                '    data-emoji-text="←_←">←_←</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="→_→">→_→</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="(<_<)">(&lt;_&lt;)</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="(>_>)">(&gt;_&gt;)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(;¬_¬)">(;¬_¬)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(&quot;▔□▔)/">("▔□▔)/</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="(ﾟДﾟ≡ﾟдﾟ)!?">(ﾟДﾟ≡ﾟдﾟ)!?</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="Σ(ﾟдﾟ;)">Σ(ﾟдﾟ;)</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="Σ( ￣□￣||)">Σ(' +
                '                                                                ￣□￣||)</a><a class="emoji-list emoji-text"' +
                '     data-emoji-text="(´；ω；`)">(´；ω；`)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="（/TДT)/">（/TДT)/</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="(^・ω・^ )">(^・ω・^' +
                '                                                                )</a><a class="emoji-list emoji-text" data-emoji-text="(｡･ω･｡)">(｡･ω･｡)</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="(●￣(ｴ)￣●)">(●￣(ｴ)￣●)</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="ε=ε=(ノ≧∇≦)ノ">ε=ε=(ノ≧∇≦)ノ</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(´･_･`)">(´･_･`)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(-_-#)">(-_-#)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="（￣へ￣）">（￣へ￣）</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="(￣ε(#￣) Σ">(￣ε(#￣)' +
                '                                                                Σ</a><a class="emoji-list emoji-text" data-emoji-text="ヽ(`Д´)ﾉ">ヽ(`Д´)ﾉ</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="(╯°口°)╯(┴—┴">(╯°口°)╯(┴—┴</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="（#-_-)┯━┯">（#-_-)┯━┯</a><a' +
                '                                                                    class="emoji-list emoji-text" data-emoji-text="_(:3」∠)_">_(:3」∠)_</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(笑)">(笑)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(汗)">(汗)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(泣)">(泣)</a><a' +
                '                                                                    class="emoji-list emoji-text"' +
                '                                                                    data-emoji-text="(苦笑)">(苦笑)</a>' +
                '                                                            </div>' +
                '                                                        </div>' +
                '</div>';
            current.parents(".con").append(content);
        }

    });


//显示隐藏表情框
    $(document).on('click', '.comment-emoji', function () {
        $(this).next('.emoji-box').toggle();
    });

//选择表情
    $(document).on('click', '.emoji-text', function () {
        var emoji_text = $(this).attr("data-emoji-text");
        var emoji_box = $(this).parents('.emoji-box');
        var textarea = emoji_box.parents('.comment-send:first').find('textarea');
        textarea.val(textarea.val() + emoji_text);
        emoji_box.hide();
    });


//点击...，显示删除和举报
    $(document).on('click', '.spot', function () {
        var opera_list = $(this).next(".opera-list");
        $('.opera-list').hide();
        opera_list.toggle();
    });

//隐藏 删除和举报
    $(document).on('mouseleave', '.info', function () {
        $(this).find('.opera-list').hide();
    });


//隐藏用户信息框
    $(document).on("mouseleave", ".user-face", function () {
        $(this).find('.user-card').hide();
    });

//关注
    $(document).on('click', '.follow', function () {
        var current = $(this);
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $(".user-card").hide();
            $("#loginModalBtn").click();
            return false;
        }
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var uid = $(this).attr('data-uid');
        var default_text = $(this).find('.default-text');
        var hover_text = $(this).find('.hover-text');
        $.ajax({
            url: "/relationships/follow",
            type: 'POST',
            data: {
                uid: uid,
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    default_text.html("<b>已关注</b>");
                    hover_text.html("<b>取消关注</b>");
                    current.removeClass("follow").addClass("notfollow");

                } else {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                // layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2},function () {                    window.location.reload();                });
            }
        });
    });

//取消关注
    $(document).on('click', '.notfollow', function () {
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $(".user-card").hide();
            $("#loginModalBtn").click();
            return false;
        }
        var current = $(this);

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var uname = $("meta[name='_username']").attr("content");
        var uid = $(this).attr('data-uid');
        var default_text = $(this).find('.default-text');
        var hover_text = $(this).find('.hover-text');
        $.ajax({
            url: "/" + uname + "/relationships/notfollow",
            type: 'POST',
            data: {
                uid: uid,
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    default_text.html("<b>关注</b>");
                    hover_text.html("<b>关注</b>");
                    current.removeClass("notfollow").addClass("follow");
                } else {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                // layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2},function () {                    window.location.reload();                });
            }
        });
    });


//对回复进行点赞
    $(document).on('click', '.info .like', function () {
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $("#loginModalBtn").click();
            return false;
        }

        var current = $(this);
        var commentId = current.attr("data-comment-id");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/zan/comment",
            type: 'POST',
            data: {
                "commentId": commentId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    current.find('span').html(data.body);
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


//文章点赞
    $(document).on('click', '.single-share .zan', function () {
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $("#loginModalBtn").click();
            return false;
        }
        var current = $(this);

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/zan/article",
            type: 'POST',
            data: {
                "articleId": articleId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                debugger;
                if (data.success) {
                    current.find('span').html(data.body);
                    current.find('i').removeClass("fa-thumbs-o-up").addClass("fa-thumbs-up");
                    current.removeClass("article-zan").addClass("article-zan-cancel");
                } else {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function (data) {
                window.location.reload();
            }
        });
    });


//对回复进行点踩
    $(document).on('click', '.info .hate', function () {
        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $("#loginModalBtn").click();
            return false;
        }

        var current = $(this);
        var commentId = current.attr("data-comment-id");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/cai/comment",
            type: 'POST',
            data: {
                "commentId": commentId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    current.find('span').html(data.body);
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


    //置顶评论
    $(document).on('click', '.comment-stick-btn', function () {
        var commentId = $(this).parents(".reply-wrap").attr("data-comment-id");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var order = $(".paging-box").attr("data-order");

        $.ajax({
            url: "/comments/stick",
            type: 'PUT',
            data: {
                "commentId": commentId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    getCommentList(articleId, 1, order);
                } else if (data.success == false) {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                window.location.reload();
            }
        });
    });

    //取消置顶评论
    $(document).on('click', '.comment-cancel-stick-btn', function () {
        var commentId = $(this).parents(".reply-wrap").attr("data-comment-id");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        var order = $(".paging-box").attr("data-order");

        $.ajax({
            url: "/comments/cancelStick",
            type: 'PUT',
            data: {
                "commentId": commentId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    getCommentList(articleId, 1, order);
                } else if (data.success == false) {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });

                // window.location.reload();
            }
        });
    });

    //收藏文章
    $(document).on('click', '.article-bookmark', function () {

        var isLogin = $("meta[name='_username']").attr("content");
        if (isLogin == null) {
            $("#loginModalBtn").click();
            return false;
        }


        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/bookmarks",
            type: 'POST',
            data: {
                "articleId": articleId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    layer.msg(data.message, {icon: 1});
                    var bookmarkSize = parseInt($(".article-bookmark-num").html());
                    $(".bookmark-tips").html("已收藏");
                    $(".article-bookmark-num").html(bookmarkSize + 1);
                } else if (data.success == false) {
                    layer.alert(data.message, {icon: 2});
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
                // window.location.reload();
            }
        });
    });

    //开启评论
    $(document).on('click', '.btn-open-comment', function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/article/openComment",
            type: 'POST',
            data: {
                "articleId": articleId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    getCommentList(articleId, 1, "new");
                    $(".btn-open-comment").removeClass().addClass("text-danger btn-close-comment").html("关闭评论");
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
    });

    //关闭评论
    $(document).on('click', '.btn-close-comment', function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/article/closeComment",
            type: 'POST',
            data: {
                "articleId": articleId
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    getCommentList(articleId, 1, "new");
                    $(".btn-close-comment").removeClass().addClass("text-success btn-open-comment").html("开启评论");
                    ;
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
    });

//    文章访问量+1
    function increaseViewCount() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        //如果没有当前页面的cookie，则添加访问量
        if ($.cookie("articleId") != articleId) {
            $.ajax({
                async: false,
                type: "POST",
                url: "/article/increaseView",
                data: {articleId: articleId},
                dataType: "text",
                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (data) {
                    $(".viewSize").html(data.body);
                    //添加cookie
                    $.cookie(
                        "articleId", articleId, {"path": "/"}
                    );
                },
            });
        }
    }
    increaseViewCount();


    $(document).on("click",".show-more",function () {
        $(".category-item").removeClass("hide");
        $(".show-more").hide();
    });
});
