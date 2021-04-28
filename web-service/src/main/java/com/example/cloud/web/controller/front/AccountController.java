package com.example.cloud.web.controller.front;

import com.example.cloud.common.vo.ResultVO;
import com.example.cloud.user.api.entity.Job;
import com.example.cloud.user.api.entity.MailRetrieve;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.JobService;
import com.example.cloud.user.api.feign.MailRetrieveService;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.web.util.FileUtil;
import com.example.cloud.web.util.MailUtil;
import com.example.cloud.common.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/3/26 下午6:51
 */


@Controller
@RequestMapping("/manage/account")
public class AccountController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private MailRetrieveService mailRetrieveService;

    @Autowired
    private MailUtil mailUtil;

    public final static String SITE_NAME = "CoderGroup";


    @GetMapping
    public String index() {
        return "forward:/manage/account/profile";
    }

    /**
     * 个人中心
     *
     * @param model
     * @return
     */
    @GetMapping("/profile")
    public ModelAndView profile(@RequestParam(value = "async", required = false) boolean async,
                                HttpSession session,
                                Model model) {
        User originalUser = (User) session.getAttribute("user");

        model.addAttribute("user", originalUser);
        List<Job> jobList = jobService.queryAllByLimit(0, 100);
        model.addAttribute("jobList", jobList);
        model.addAttribute("site_title", SiteTitleEnum.USER_SPACE_PROFILE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/account/profile :: #right-box-body-replace" : "home/userspace/account/profile");
    }

    /**
     * 保存基本资料
     *
     * @param user
     * @return
     */
    @PostMapping("/profile")
    public ResponseEntity<Response> saveBasicProfile(User user, HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        user.setId(loginUser.getId());
        try {
            userService.update(user);
            session.setAttribute("user", userService.queryById(loginUser.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, "修改失败"));
        }
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }


    /**
     * 获取编辑头像的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/avatar")
    public ModelAndView avatar(@RequestParam(value = "async", required = false) boolean async,
                               Model model,
                               HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        User user = userService.queryById(loginUser.getId());
        model.addAttribute("user", user);
        model.addAttribute("site_title", SiteTitleEnum.USER_SPACE_AVATAR.getTitle());
        return new ModelAndView(async == true ? "home/userspace/account/avatar :: #right-box-body-replace" : "home/userspace/account/avatar");
    }


    /**
     * 保存头像
     *
     * @return
     */
    @PostMapping("/avatar")
    public ResponseEntity<Response> saveAvatar(@RequestParam("avatarFile") MultipartFile avatarFile,
                                               HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        //1、后缀过滤，只支持 jpg,jpeg,png,bmp,gif
        String filename = avatarFile.getOriginalFilename();
        String suffix = (filename.substring(filename.lastIndexOf("."))).toLowerCase();
        if (!".jpg".equals(suffix) && !".png".equals(suffix) && !".jpeg".equals(suffix) && !".gif".equals(suffix) && !".bmp".equals(suffix)) {
            return ResponseEntity.ok().body(new Response(false, "文件格式不允许"));
        }

        //2、文件大小过滤
        if (avatarFile.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.ok().body(new Response(false, "文件太大，请选择小于2MB的图"));
        }

        //3、开始上传
        User originalUser = userService.queryById(user.getId());
        String avatar = "";
        try {
            avatar = FileUtil.upload(avatarFile);
            originalUser.setAvatar(avatar);
            userService.update(originalUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, "更新失败"));
        }

        session.setAttribute("user", userService.queryById(user.getId()));
        return ResponseEntity.ok().body(new Response(true, "更新成功", avatar));
    }


    /**
     * 账号安全页面
     *
     * @return
     */
    @GetMapping("/security")
    public ModelAndView security(@RequestParam(value = "async", required = false) boolean async,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        model.addAttribute("user", user);
        model.addAttribute("site_title", SiteTitleEnum.USER_SPACE_SECURITY.getTitle());
        return new ModelAndView(async == true ? "home/userspace/account/security :: #right-box-body-replace" : "home/userspace/account/security");
    }


    /**
     * 修改密码
     *
     * @param password
     * @return
     */
    @PostMapping("/password")
    public ResponseEntity<Response> savePassword(String password,
                                                 HttpSession session) {
        if (password.trim().length() < 6) {
            return ResponseEntity.ok().body(new Response(false, "密码不符合要求"));
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        User originalUser = userService.queryById(user.getId());
        originalUser.setPassword(password);
        userService.update(originalUser);
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }

    /**
     * 修改Email
     *
     * @return
     */
    @PostMapping("/email")
    public ResponseEntity<Response> saveEmail(String email,
                                              HttpSession session) {
        //1、用户资料
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        //判断Email是否存在
        User checkUser = userService.findByEmail(email);
        //如果Email已存在，且不是当前用户
        if (checkUser != null && !Objects.equals(user.getId(), checkUser.getId())) {
            return ResponseEntity.ok().body(new Response(false, "该邮箱已被使用了哦"));
        }
        //邮箱未修改
        if (Objects.equals(user.getEmail(), email)) {
            return ResponseEntity.ok().body(new Response(false, "邮箱好像没有修改呢"));
        }
        user.setEmail(email);
        user.setIsVerifyEmail("N");

        //2、捕获异常
        try {
            userService.update(user);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        //3、修改session
        session.setAttribute("user", userService.queryById(user.getId()));

        //4、删除找回密码的邮件
        MailRetrieve mailRetrieve = mailRetrieveService.findByAccount(user.getUsername());
        if (mailRetrieve != null) {
            mailRetrieveService.deleteById(mailRetrieve.getId());
        }
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }

    /**
     * 发送验证邮件
     *
     * @return
     */
    @PostMapping("/verifyEmail")
    public ResponseEntity<Response> verifyEmail(HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        //1、如果已发送，且时间在60s内，则提示
        MailRetrieve mailRetrieve = mailRetrieveService.findByAccount(user.getUsername());
        if (mailRetrieve != null && (System.currentTimeMillis() - mailRetrieve.getCreateTime()) < 60 * 1000) {
            return ResponseEntity.ok().body(new Response(false, "邮件已发送，请稍后再试"));
        }
        //2、生成链接
        String basePath = "http://localhost:" + request.getServerPort() + "/manage/account/active-email";
        String mailUrl = mailRetrieveService.getEmailUrl(basePath, user.getUsername());
        //3、配置邮件内容
        String receiver = user.getEmail();//接收者
        String subject = "【" + SITE_NAME + "】激活邮箱";//邮件主题(标题)
        StringBuilder content = new StringBuilder();
        content.append("尊敬的用户" + user.getNickname() + "，您好：<br/>");
        content.append("您正在" + SITE_NAME + "进行激活邮箱操作，请您点击下面的链接完成激活<br/>");
        content.append("<a href=" + mailUrl + " target=\"_blank\">" + mailUrl + "</a><br/><br/>");
        content.append("若这不是您本人要求的，请忽略本邮件，一切如常。<br/><br/>");
        try {
            mailUtil.sendHtmlMail(receiver, subject, content.toString());
        } catch (Exception e) {
            logger.error("发送邮件出现错误", e);
        }

        return ResponseEntity.ok().body(new Response(true, "发送成功"));
    }


    @GetMapping("/verify-email-success")
    public String verifyEmailSuccess(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("site_title", SiteTitleEnum.EMAIL_VERIFY_SUCCESS.getTitle());
        return "home/userspace/account/verify_email_success";
    }

    @GetMapping("/verify-email-error")
    public String verifyEmailError(@RequestParam(required = false) String errorMsg, Model model) {
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("site_title", SiteTitleEnum.EMAIL_VERIFY_FAIL.getTitle());
        return "home/userspace/account/verify_email_error";
    }

    @GetMapping(value = "/active-email")
    public String verifyMail(String sid, String username, HttpServletRequest request) {
        ResultVO resultVO = mailRetrieveService.verifyMailUrl(sid, username);
        //验证通过
        if (resultVO.getCode() == 0) {
            User user = userService.findByUsername(username);
            user.setIsVerifyEmail("Y");
            userService.update(user);
            request.setAttribute("email", user.getEmail());
            return "forward:/manage/account/verify-email-success?email=" + user.getEmail();
        } else {
            return "forward:/manage/account/verify-email-error?errorMsg=" + resultVO.getMsg();
        }
    }
}
