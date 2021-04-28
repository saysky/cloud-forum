$(function () {


    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    //注册表单验证
    $(".userForm").bootstrapValidator({
        message: '输入值不合法',
        fields: {
            username: {
                message: '用户名不合法',
                validators: {
                    notEmpty: {
                        message: '用户名不能为空'
                    },
                    stringLength: {
                        min: 4,
                        max: 20,
                        message: '用户名为4到20个字符'
                    },
                    regexp: {
                        regexp: /^[a-zA-Z0-9_@]+$/,
                        message: '用户名只能由字母、数字和下划线组成'
                    },
                }

            }
            ,
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
                        max: 30,
                        message: '密码为6到30个字符'
                    },
                    regexp: {
                        regexp: /^[a-zA-Z0-9_$@.]+$/,
                        message: '密码字符不合法'
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
                        max: 30,
                        message: '请输入6到30个字符'
                    },
                    regexp: {
                        regexp: /^[a-zA-Z0-9_$@.]+$/,
                        message: '密码字符不合法'
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
                        message: '请输入正确的邮箱地址',
                    }
                }
            },
        }
    });

    //切换验证码
    $(document).on('click', '.changeCaptcha', function () {
        var num = Math.random() * (1000 - 1) + 1;
        var url = "/getKaptchaImage?random=" + num;
        $(".captcha").attr("src", url);
    });

    //登录
    $(document).on("click", "#login-btn", function () {
        var randomAnim = Math.floor(Math.random() * 7);
        if ($("#kaptcha").val().length < 1) {
            layer.alert("验证码不可为空!", {icon: 2, anim: randomAnim});
            return false;
        }
        $.ajax({
            url: "/login",
            type: 'POST',
            data: $("#loginForm").serialize(),
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    window.location.href = "/";
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2, anim: randomAnim});
                    $(".changeCaptcha").click();
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


    //注册
    $(document).on('click', '#register-btn', function () {
        var randomAnim = Math.floor(Math.random() * 7);
        if ($("#register-kaptcha").val().length < 1) {
            layer.alert("验证码不可为空!", {icon: 2, anim: randomAnim});
            return false;
        }
        if (!$("#agree").is(':checked')) {
            layer.alert("请同意服务条款!", {icon: 2, anim: randomAnim});
            return false;
        }
        $.ajax({
            url: "/register",
            type: 'POST',
            data: $("#registerForm").serialize(),
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    window.location.href = '/register-success';
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2, anim: randomAnim});
                }
            },
            error: function () {
                layer.msg("后台好像偷了点小懒哦，重新刷新一下试试！", {icon: 2, anim: randomAnim});
            }
        });
        return false;
    });


    $(document).on('click', '#forget-btn', function () {
        var randomAnim = Math.floor(Math.random() * 7);
        if ($("#forget-username").val().length < 1) {
            layer.alert("用户名不可为空!", {icon: 2, anim: randomAnim});
            return false;
        }
        if ($("#forget-email").val().length < 1) {
            layer.alert("电子邮箱不可为空!", {icon: 2, anim: randomAnim});
            return false;
        }
        if ($("#forget-kaptcha").val().length < 1) {
            layer.alert("验证码不可为空!", {icon: 2, anim: randomAnim});
            return false;
        }
        $.ajax({
            url: "/forget",
            type: 'POST',
            data: $("#forgetForm").serialize(),
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    layer.alert('我们已经向您的邮箱发送了一封邮件！', {icon: 1}, function () {
                        window.location.href = "/login";
                    });
                } else if (response.success == false) {
                    layer.alert(response.message, {icon: 2, anim: randomAnim});
                    $(".changeCaptcha").click();
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


    $(document).on('click', '#resetpass-btn', function () {
        $.ajax({
            url: "/resetpass",
            type: 'POST',
            data: $("#resetpassForm").serialize(),
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            },
            success: function (response) {
                if (response.success) {
                    layer.alert('密码修改成功！', {icon: 1}, function () {
                        window.location.href = "/login";
                    });

                } else {
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


});



