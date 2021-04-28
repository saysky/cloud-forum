package com.example.cloud.web.controller.admin;

import com.example.cloud.common.enums.PostStatusEnum;
import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.common.vo.Response;
import com.example.cloud.qa.api.entity.Answer;
import com.example.cloud.qa.api.entity.Question;
import com.example.cloud.qa.api.feign.AnswerService;
import com.example.cloud.qa.api.feign.QuestionService;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.web.controller.common.BaseController;
import com.example.cloud.web.util.HTMLUtil;
import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/6/3 下午3:21
 */
@Controller
public class QuestionController extends BaseController {


    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String hostName = "http://localhost:8080";


    /**
     * 问题列表显示
     *
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/questions")
    public ModelAndView index(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                              @RequestParam(value = "type", required = false, defaultValue = "") String type,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        Question condition = new Question();
        if (PostStatusEnum.UNANSWERED_POST.getCode().equals(type)) {
            //零回答的问题
            condition.setAnswerSize(0);
            model.addAttribute("url", "/questions/unanswered");
            model.addAttribute("type", "unanswered");
        } else if (PostStatusEnum.RESOLVED_POST.getCode().equals(type)) {
            //已解决的问题
            condition.setStatus(PostStatusEnum.RESOLVED_POST.getCode());
            model.addAttribute("url", "/questions/resolved");
            model.addAttribute("type", "resolved");
        } else {
            //所有的问题
            model.addAttribute("url", "/questions");
            model.addAttribute("type", "all");

        }
        PageVO<Question> questionPage = questionService.findAll(condition, pageIndex, pageSize);
        for (Question temp : questionPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        model.addAttribute("page", questionPage);
        model.addAttribute("site_title", SiteTitleEnum.QUESTION_HOME.getTitle());
        return new ModelAndView(async == true ? "home/question_list :: #tab-pane" : "home/question_list");
    }


    /**
     * 获取新增博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/ask")
    public String createQuestion(Model model) {
        model.addAttribute("site_title", SiteTitleEnum.QUESTION_CREATE.getTitle());
        return "home/userspace/question/add";
    }


    /**
     * 获得问题详情页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/questions/edit/{id}")
    public String questionEdit(@PathVariable(value = "id") Long id, Model model) throws Exception {
        Question question = questionService.queryById(id);
        //1、判断问题是否存在
        if (question == null) {
            throw new NotFoundException("问题不存在!");
        }

        //2、判断是否可以访问
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), question.getStatus())) {
            throw new NotFoundException("该问题已被管理员删除!");
        }

        // 3、判断操作用户是否是的问题所有者
        User loginUser = (User) session.getAttribute("user");
        if (!Objects.equals(loginUser.getId(), question.getUserId())) {
            throw new Exception("没有权限!");
        }
        model.addAttribute("question", question);
        model.addAttribute("site_title", SiteTitleEnum.QUESTION_EDIT.getTitle());
        return "home/userspace/question/edit";
    }

    /**
     * 获得问题详情页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/questions/{id}")
    public ModelAndView questionDetail(@PathVariable(value = "id") Long id,
                                       @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                       @RequestParam(value = "orderby", required = false, defaultValue = "zanSize") String orderby,
                                       @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                       Model model) throws NotFoundException {
        Question question = questionService.queryById(id);
        //1、判断问题是否存在
        if (question == null) {
            throw new NotFoundException("问题不存在!");
        }

        //2、判断是否可以访问
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), question.getStatus())) {
            throw new NotFoundException("该问题已被管理员删除!");
        }

        // 3、判断操作用户是否是的问题所有者
        User loginUser = (User) session.getAttribute("user");
        boolean isOwner = false;
        if (loginUser != null && Objects.equals(loginUser.getId(), question.getUserId())) {
            isOwner = true;
        }

        //4、回答列表
        String answerOrder = "";
        Answer condition = new Answer();
        String orderBy = "";
        if (orderby.equals("zanSize")) {
            orderBy = "is_accepted DESC, zan_size DESC";
            answerOrder = "zanSize";
        } else if (orderby.equals("commentSize")) {
            orderBy = "is_accepted DESC, comment_size DESC";
            answerOrder = "commentSize";
        } else {
            orderBy = "is_accepted DESC, create_time DESC";
            answerOrder = "createTime";
        }
        condition.setPid(0L);
        condition.setOrderBy(orderBy);
        condition.setOrderBy(orderBy);
        PageVO<Answer> answerPage = answerService.findAll(condition, pageIndex, pageSize);
        for (Answer temp : answerPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
            temp.setAnswerList(answerService.findByPid(temp.getId()));
        }

        question.setUser(userService.queryById(question.getUserId()));
        model.addAttribute("question", question);
        model.addAttribute("orderby", answerOrder);
        model.addAttribute("page", answerPage);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("site_title", question.getTitle() + SiteTitleEnum.QUESTION_DETAIL_PAGE.getTitle());

        if (!async) {
            //站点关键词和描述
            model.addAttribute("site_keywords", question.getTags());
            model.addAttribute("site_content", question.getSummary());
        }
        return new ModelAndView(async == true ? "home/question_detail :: #answer-list-replace" : "home/question_detail");
    }


    /**
     * 发布问题提交
     *
     * @param question
     * @return
     */
    @PostMapping("/question")
    public ResponseEntity<Response> saveQuestion(@RequestBody Question question) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        user = userService.findByUsername(user.getUsername());
        String guid = null;

        try {
            //1、 判断是修改还是新增
            if (question.getId() != null) {
                //修改
                Question originalQuestion = questionService.queryById(question.getId());
                originalQuestion.setTitle(question.getTitle());
                originalQuestion.setContent(question.getContent());
                originalQuestion.setTags(question.getTags());
                originalQuestion.setStatus(question.getStatus());
                Timestamp nowTime = new Timestamp(System.currentTimeMillis());
                originalQuestion.setUpdateTime(nowTime);
                //summary
                int contentLength = question.getContent().length();
                String summary = null;
                if (contentLength > 150) {
                    summary = HTMLUtil.html2text(question.getContent().substring(0, 150));
                } else {
                    summary = HTMLUtil.html2text(question.getContent().substring(0, contentLength));
                }
                originalQuestion.setSummary(summary);
                questionService.update(originalQuestion);
                guid = originalQuestion.getGuid();
            } else {
                //新增
                question.setUserId(user.getId());
                //summary
                int contentLength = question.getContent().length();
                String summary = null;
                if (contentLength > 150) {
                    summary = HTMLUtil.html2text(question.getContent().substring(0, 150));
                } else {
                    summary = HTMLUtil.html2text(question.getContent().substring(0, contentLength));
                }
                question.setSummary(summary);
                question.setAnswerSize(0);
                question.setCreateTime(new Date());
                question.setUpdateTime(new Date());
                question.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
                question.setViewSize(0);
                question = questionService.insert(question);

                guid = "/questions/" + question.getId();
                question.setGuid(guid);
                questionService.update(question);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", guid));
    }

    /**
     * 浏览量+1
     *
     * @param questionId
     * @return
     */
    @PostMapping("/questions/increaseView")
    public ResponseEntity<Response> increaseView(Long questionId) {
        try {
            questionService.viewIncrease(questionId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(true, "问题不存在！"));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    //    用户中心
    @GetMapping("/manage/questions")
    public ModelAndView questionManage(Model model,
                                       @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                       @RequestParam(value = "status", required = false, defaultValue = "false") String status,
                                       @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        Question condition = new Question();

        //未解决
        if (PostStatusEnum.PUBLISH_POST.getCode().equals(status)) {
            condition.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            model.addAttribute("status", "publish");
        }
        //已解决
        else if (PostStatusEnum.RESOLVED_POST.getCode().equals(status)) {
            condition.setStatus(PostStatusEnum.RESOLVED_POST.getCode());
            model.addAttribute("status", "resolved");
        }
        condition.setUserId(user.getId());
        PageVO<Question> page = questionService.findAll(condition, pageIndex, pageSize);
        for (Question temp : page.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        model.addAttribute("page", page);
        model.addAttribute("site_title", SiteTitleEnum.MY_QUESTION_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/question/question-list :: #tab-pane" : "home/userspace/question/question-list");
    }

    //后台
    @GetMapping("/admin/question")
    public ModelAndView adminArticleList(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        Question condition = new Question();
        condition.setStatus(status);
        PageVO<Question> questionPage = questionService.findAll(condition, pageIndex, pageSize);
        for (Question temp : questionPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
        }
        model.addAttribute("status", status);
        model.addAttribute("page", questionPage);
        return new ModelAndView(async == true ? "admin/question/list :: #mainContainerReplace" : "admin/question/list");
    }

    /**
     * 删除回答
     *
     * @return
     */
    @DeleteMapping("/admin/question/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
        try {
            questionService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 恢复回答
     *
     * @param id
     * @return
     */
    @PutMapping("/admin/question/restore/{id}")
    public ResponseEntity<Response> restore(@PathVariable("id") Long id) {
        try {
            Question question = questionService.queryById(id);
            question.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            questionService.insert(question);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


}
