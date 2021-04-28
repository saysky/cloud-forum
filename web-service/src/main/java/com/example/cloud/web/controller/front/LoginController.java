package com.example.cloud.web.controller.front;

import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.common.enums.UserStatusEnum;
import com.example.cloud.common.vo.Response;
import com.example.cloud.common.vo.ResultVO;
import com.example.cloud.user.api.entity.MailRetrieve;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.JobService;
import com.example.cloud.user.api.feign.MailRetrieveService;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.web.controller.common.BaseController;
import com.example.cloud.web.util.AvatarUtil;
import com.example.cloud.web.util.MailUtil;
import com.google.code.kaptcha.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/2/23 下午5:31
 */

@Controller
@Slf4j
public class LoginController extends BaseController {


    //角色(用户)的值
    private static final Integer ROLE_USER_AUTHORITY_ID = 2;

    //未设置职业的值
    private static final Long JOB_NOTSET_ID = 1L;


    //保留字符
    private static final String[] DISABLED_USERNAME = {"admin", "admins", "user", "users", "category", "categorys", "search", "bulletin", "notice", "forum", "question", "questions", "answer", "answers", "manage", "manages", "post", "posts", "article", "articles", "blog", "blogs", "ajax", "css", "js", "forum", "static", "public", "resource", "login", "logout", "forget", "resetpass", "reset", "template", "components", "css", "img", "images", "js", "getKaptchaImage", "匿名", "管理员", "站长", "版主"};

    @Autowired
    private UserService userService;

    @Autowired
    private MailRetrieveService mailRetrieveService;

    @Autowired
    private MailUtil mailService;

    @Autowired
    private JobService jobService;


    /**
     * 登录页面显示
     *
     * @return
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("site_title", SiteTitleEnum.LOGIN_PAGE.getTitle());
        log.info("登录页面，{}", 1);
        System.out.println("sys:::::::");
        return "home/login/login";
    }

    @GetMapping("/loginModal")
    public ModelAndView getLoginModal(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async) {
        return new ModelAndView(async == true ? "home/common/login_modal :: #box-login" : "home/login/login");
    }


    /**
     * 注册页面显示
     *
     * @return
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("site_title", SiteTitleEnum.REGISTER_PAGE.getTitle());
        return "home/login/register";
    }


    /**
     * 注册成功跳转页面
     *
     * @return
     */
    @GetMapping("/register-success")
    public String registerSuccess(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("site_title", SiteTitleEnum.REGISTER_SUCCESS_PAGE.getTitle());
        model.addAttribute("user", user);
        return "home/login/register_success";
    }


    /**
     * 登录提交
     *
     * @return
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public ResponseEntity<Response> loginSubmit(@RequestParam("username") String username,
                                                @RequestParam("password") String password,
                                                @RequestParam("kaptcha") String kaptcha,
                                                HttpSession session) {
        String expect = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (expect != null && !expect.equalsIgnoreCase(kaptcha)) {
            return ResponseEntity.ok().body(new Response(false, "验证码错误"));
        }
        // 查询用户名是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在"));
        }

        // 判断密码是否正确
        if (!Objects.equals(user.getPassword(), password)) {
            return ResponseEntity.ok().body(new Response(false, "密码错误"));
        }

        // 判断用户是否禁用
        if (Objects.equals(user.getStatus(), UserStatusEnum.BANNED.getCode())) {
            return ResponseEntity.ok().body(new Response(false, "账号已被冻结，请联系管理员"));
        }

        // 登录成功
        session.setAttribute("user", userService.queryById(user.getId()));
        return ResponseEntity.ok().body(new Response(true, "登录成功"));

    }

    /**
     * 注册提交
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Response> registerUser(User user) {
        String expect = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (expect != null && !expect.equalsIgnoreCase(request.getParameter("kaptcha"))) {
            return ResponseEntity.ok().body(new Response(false, "验证码错误"));
        }

        //1、验证用户名长度
        if (user.getUsername() != null) {
            if (user.getUsername().trim().length() < 4 || user.getUsername().trim().length() > 20) {
                return ResponseEntity.ok().body(new Response(false, "用户名长度不合法"));
            }
        } else {
            return ResponseEntity.ok().body(new Response(false, "用户名不可为空"));
        }
        //2、验证密码长度
        if (user.getPassword() != null) {
            if (user.getPassword().trim().length() < 6 || user.getPassword().trim().length() > 20) {
                return ResponseEntity.ok().body(new Response(false, "密码长度不合法"));
            }
        } else {
            return ResponseEntity.ok().body(new Response(false, "密码不可为空"));
        }

        //3、验证用户名可用性
        for (String str : DISABLED_USERNAME) {
            if (str.equals(user.getUsername())) {
                return ResponseEntity.ok().body(new Response(false, "用户名不合法，包含保留字符！"));
            }
        }

        //4、验证用户名是否存在
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.ok().body(new Response(false, "用户名已被注册！"));
        }

        //5、验证邮箱是否存在
        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.ok().body(new Response(false, "电子邮箱已被注册！"));
        }

        //6、设置用户信息
        user.setRole("user");
        user.setNickname(user.getUsername());
        user.setAvatar("/img/default-avatar.jpg");
        user.setCreateTime(new Date());
        user.setFanSize(0L);
        user.setFollowSize(0L);
        user.setViewSize(0L);
        user.setQuestionSize(0L);
        user.setArticleSize(0L);
        user.setAnswerSize(0L);
        user.setStatus(UserStatusEnum.NORMAL.getCode());
        user.setJobId(JOB_NOTSET_ID);
        try {
            //6、添加用户
            userService.insert(user);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        // 自动登录成功
        session.setAttribute("user", userService.findByUsername(user.getUsername()));
        return ResponseEntity.ok().body(new Response(true, "注册成功", 1));

    }


    /**
     * 找回密码页面显示
     *
     * @return
     */
    @GetMapping("/forget")
    public String forget(Model model) {
        model.addAttribute("site_title", SiteTitleEnum.FORGET_PAGE.getTitle());
        return "home/login/forget";
    }


    /**
     * 退出登录
     *
     * @return
     */
    @GetMapping(value = "/logout")
    public String logoutPage(HttpSession session, Model model) {
        session.removeAttribute("user");
        session.invalidate();
        model.addAttribute("site_title", SiteTitleEnum.LOGIN_PAGE.getTitle());
        return "redirect:/login?logout";
    }

    /**
     * 忘记密码提交,然后跳转登录页面
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/forget")
    @ResponseBody
    public ResponseEntity<Response> forgetUser(HttpServletRequest request, String username, String email) {
        String expect = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (expect != null && !expect.equalsIgnoreCase(request.getParameter("kaptcha"))) {
            return ResponseEntity.ok().body(new Response(false, "验证码错误"));
        }
        User user = userService.findByUsername(username);
        //1、判断用户是否存在
        if (user == null || !Objects.equals(user.getEmail(), email)) {
            return ResponseEntity.ok().body(new Response(false, "用户名或邮箱不正确!"));
        }
        //2、判断是否验证了邮箱
        if (!Objects.equals(user.getIsVerifyEmail(), "Y")) {
            return ResponseEntity.ok().body(new Response(false, "该账号邮箱没有验证，无法找回，请联系管理员!"));
        }

        //2、如果已发送，且时间在60s内，则提示
        MailRetrieve mailRetrieve = mailRetrieveService.findByAccount(username);
        if (mailRetrieve != null && (System.currentTimeMillis() - mailRetrieve.getCreateTime()) < 60 * 1000) {
            return ResponseEntity.ok().body(new Response(false, "邮件已发送，请稍后再试!"));
        }
        //3、生成链接
        String basePath = "http://localhost:8080/resetpass";
        String mailUrl = mailRetrieveService.getEmailUrl(basePath, username);
        //4、发送邮件
        String receiver = user.getEmail();//接收者
        String subject = "【" + SITE_NAME + "】重置密码";//邮件主题(标题)
        StringBuilder content = new StringBuilder();
        content.append("尊敬的用户" + user.getNickname() + "，您好：<br/>");
        content.append("您正在" + SITE_NAME + "进行重置密码操作，请您点击下面的链接完成重置<br/>");
        content.append("<a href=" + mailUrl + " target=\"_blank\">" + mailUrl + "</a><br/><br/>");
        content.append("若这不是您本人要求的，请忽略本邮件，一切如常。<br/><br/>");
        mailService.sendHtmlMail(receiver, subject, content.toString());

        return ResponseEntity.ok().body(new Response(true, "发送成功"));
    }


    @GetMapping("/verify-resetpass-error")
    public String verifyResetpassError(String username, String errorMsg, Model model) {
        model.addAttribute("username", username);
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("site_title", SiteTitleEnum.RESET_PASSWORD_VERIFY_FAIL.getTitle());
        return "home/login/verify_resetpass_error";
    }

    /**
     * 重置密码验证
     * 验证通过，显示修改密码页面
     *
     * @param sid
     * @param username
     * @return
     */
    @GetMapping(value = "/resetpass")
    public String verifyMail(String sid, String username, HttpServletRequest request, Model model) {

        ResultVO resultVO = mailRetrieveService.verifyMailUrl(sid, username);
        //验证通过,显示设置密码页面
        if (resultVO.getCode() == 0) {
            //验证成功
            model.addAttribute("username", username);
            model.addAttribute("sid", sid);
            model.addAttribute("site_title", SiteTitleEnum.RESET_PASSWORD_PAGE.getTitle());
            return "home/login/resetpass";
        } else {
            //验证失败
            String errorMsg = resultVO.getMsg();
            return "forward:/verify-resetpass-error?username=" + username + "&errorMsg=" + errorMsg;
        }
    }


    /**
     * 重置密码提交
     *
     * @param sid
     * @param username
     * @param password
     * @return
     */
    @PostMapping(value = "/resetpass")
    @ResponseBody
    public ResponseEntity<Response> resetPass(String sid, String username, String password) {
        ResultVO resultVO = mailRetrieveService.verifyMailUrl(sid, username);
        //如果验证通过(防止用户自定义表单，再次验证)
        if (resultVO.getCode() == 0) {
            //修改密码
            User user = userService.findByUsername(username);
            user.setPassword(password);
            userService.update(user);
            return ResponseEntity.ok().body(new Response(true, "修改成功"));
        }
        //验证失败
        else {
            return ResponseEntity.ok().body(new Response(false, resultVO.getMsg()));
        }
    }


}
