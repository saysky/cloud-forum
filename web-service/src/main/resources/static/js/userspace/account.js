

/**
 * 上传头像
 * @param file
 */
function uploadAvatar(file) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    if (!file) {
        return;
    }
    if (file.size > (1024 * 1024) * 1) {
        layer.alert('图片大小不能超过1MB！', {icon: 2});
        return;
    }
    var filename = file.name;
    var suffix = filename.substr(filename.lastIndexOf("."));
    if (!".jpg" == suffix && !".png" == suffix && !".jpeg" == suffix && !".gif" == suffix && !".bmp" == suffix) {
        layer.alert("文件格式不允许!", {icon: 2});
        return;
    }
    layer.load(2);
    var formData = new FormData();
    formData.append("avatarFile", file);
    $.ajax({
        url: "/manage/account/avatar",
        type: 'POST',
        data: formData,
        cache: false,                      // 不缓存
        processData: false,                // jQuery不要去处理发送的数据
        contentType: false,                // jQuery不要去设置Content-Type请求头
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        success: function (response) {
            layer.closeAll();
            if (response.success) {
                layer.msg('修改成功!', {icon: 1});
                $('.user-image').attr({'src': response.body})
            } else {
                layer.alert(response.message, {icon: 2});
            }
        },
        error: function (data) {
            layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2});
            console.log(data);
        }

    });

}

$(function () {

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $(document).on('click', ".nav-tabs>li>a", function () {
        var url = "/manage/account/" + $(this).attr("id");
        $.ajax({
            url: url,
            data: {async: true},
            success: function (data) {
                $("#right-box-body").html(data)
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
            }
        });
    });

    $(document).on('blur', 'input', function () {
        $(".userForm").bootstrapValidator({
            message: '输入值不合法',
            feedbackIcons: {
//                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                nickname: {
                    message: '昵称不合法',
                    validators: {
                        notEmpty: {
                            message: '昵称不能为空'
                        },
                        stringLength: {
                            min: 2,
                            max: 12,
                            message: '昵称为2到12个字符'
                        }
                    }
                },
                password: {
                    message: '密码不合法',
                    validators: {
                        notEmpty: {
                            message: '密码不能为空'
                        },
                        stringLength: {
                            min: 6,
                            max: 100,
                            message: '密码为6到100个字符'
                        },

                    }
                },
                confirm_password: {
                    message: '确认密码不合法',
                    validators: {
                        notEmpty: {
                            message: '确认密码不能为空'
                        },
                        stringLength: {
                            min: 6,
                            max: 100,
                            message: '请输入6到100个字符'
                        },
                        regexp: {
                            regexp: /^[a-zA-Z0-9_$@./]+$/,
                            message: '确认只能由字母、数字和下划线组成'
                        },
                        identical: {
//需要验证的field
                            field: 'password',
                            message: '两次密码输入不一致'
                        }
                    }
                },
                email: {
                    validators: {
                        notEmpty: {
                            message: '邮箱地址不能为空'
                        },
                        emailAddress: {
                            message: '请输入正确的邮箱地址'
                        },
                    }
                },
                job: {
                    message: '职业不合法',
                    validators: {
                        notEmpty: {
                            message: '请选择职业'
                        },
                    }
                },
                github: {
                    message: 'Github不合法',
                    validators: {
                        uri: {
                            message: 'Github不合法，别忘了加http(s)://'
                        },
                        stringLength: {
                            max: 50,
                            message: 'URL不要超过50个字符'
                        },
                    }
                },
                homepage: {
                    message: '个人主页不合法',
                    validators: {
                        uri: {
                            message: '个人主页不合法，别忘了加 http(s)://'
                        },
                        stringLength: {
                            max: 50,
                            message: 'URL不要超过50个字符'
                        },
                    }
                },
                profile: {
                    message: '简介不合法',
                    validators: {
                        stringLength: {
                            max: 1000,
                            message: '简介不要超过1000字符'
                        },
                    }
                },
            }
        });
    });


    $(document).on('click', '#profile-form-submit', function () {
        $.ajax({
            url: "/manage/account/profile",
            type: 'POST',
            data: $("#profileForm").serialize(),
            async: false,
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    layer.msg("修改成功!", {icon: 1});
                    $(".tab-content").load("/manage/account/profile .tab-pane");
                    $("#user-nav-wrapper").load("/manage/account/profile #user-nav");
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
        return false;
    });


    /**
     * 修改密码
     * @returns {boolean}
     */

    $(document).on("click", "#password-form-submit", function () {
        $.ajax({
            url: "/manage/account/password",
            type: 'POST',
            data: {password: $("#password").val()},
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    layer.alert('修改成功', {icon: 1});
                    $("#passwordModal").modal('hide');
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
        return false;
    })

    /**
     * 修改Email
     * @returns {boolean}
     */
    $(document).on('click', '#email-form-submit', function () {
        $.ajax({
            url: "/manage/account/email",
            type: 'POST',
            data: {email: $("#email").val()},
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    $("#emailModal").modal('hide');
                    layer.alert('邮箱修改成功，需要重新验证！', {icon: 1});
                    $(".tab-content").load("/manage/account/security .tab-pane");
                    $("#user-nav-wrapper").load("/manage/account/profile #user-nav");
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
        return false;
    })


    /**
     * 验证Email
     */
    $(document).on('click', "#verify-email-a", function () {
        $.ajax({
            url: "/manage/account/verifyEmail",
            type: 'POST',
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    layer.alert('我们已经向您的邮箱发送了一封邮件', {icon: 1});
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2}, function () {
                        layer.alert('如果没有收到邮件，请查看垃圾箱', {icon: 5});
                    });
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2}, function () {
                    window.location.reload();
                });
            }
        });
        $(this).html("已发送，待验证");
    });

    //解除第三方绑定
    $(document).on('click', ".cancel-bind", function () {

        // 获取 CSRF Token
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var bindId = $(this).attr('data-bind-id');
        var tr = $(this).parents("tr");
        layer.confirm('你确认要解除绑定吗？', {
            btn: ['确认', '取消'], //按钮
            icon: 8
        }, function () {
            $.ajax({
                url: '/manage/account/bind/' + bindId,
                type: 'DELETE',
                beforeSend: function (request) {
                    request.setRequestHeader(header, token); // 添加  CSRF Token
                },
                success: function (data) {
                    if (data.success) {
                        layer.msg("解除成功!", {icon: 1});
                        tr.remove();
                    } else if (data.success == false) {
                        layer.alert(data.message, {icon: 2});
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

})