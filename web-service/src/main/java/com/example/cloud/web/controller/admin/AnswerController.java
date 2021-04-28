package com.example.cloud.web.controller.admin;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.qa.api.entity.Answer;
import com.example.cloud.qa.api.entity.Question;
import com.example.cloud.qa.api.feign.AnswerService;
import com.example.cloud.qa.api.feign.QuestionService;
import com.example.cloud.user.api.entity.User;
import com.example.cloud.user.api.feign.UserService;
import com.example.cloud.common.enums.IsAcceptedEnum;
import com.example.cloud.common.enums.PostStatusEnum;
import com.example.cloud.common.enums.SiteTitleEnum;
import com.example.cloud.common.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/6/4 下午2:31
 */
@Controller
public class AnswerController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 发表回答
     *
     * @param questionId
     * @param content
     * @return
     */
    @PostMapping("/answers")
    public ResponseEntity<Response> createAnswer(@RequestParam(value = "questionId") Long questionId,
                                                 HttpSession session,
                                                 @RequestParam(value = "content") String content) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        Question question = questionService.queryById(questionId);
        if (question == null) {
            return ResponseEntity.ok().body(new Response(false, "问题不存在!"));
        }
        try {
            //1、回答问题
            Answer answer = new Answer();
            answer.setQuestionId(question.getId());
            answer.setContent(content);
            answer.setUserId(user.getId());
            answer.setReplyUserId(question.getUserId());
            answer.setCommentSize(0);
            answer.setIsAccepted(IsAcceptedEnum.NOT_ACCEPT_STATUS.getCode());
            answer.setPid(0L);
            answer.setZanSize(0);
            answer.setCaiSize(0);
            answer.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            answer.setCreateTime(new Date());
            answerService.insert(answer);

            //2、修改问题的回答数目
            question.setAnswerSize(question.getAnswerSize() + 1);
            questionService.update(question);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功!", null));
    }


    /**
     * 对回答进行评论
     *
     * @param answerId
     * @param content
     * @return
     */
    @PostMapping("/answers/comment")
    public ResponseEntity<Response> createCommentForAnswer(@RequestParam(value = "answerId") Long answerId,
                                                           @RequestParam(value = "commentId") Long commentId,
                                                           @RequestParam(value = "content") String content,
                                                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }

        Answer answer = answerService.queryById(answerId);
        Answer comment = answerService.queryById(commentId);
        if (answer == null) {
            return ResponseEntity.ok().body(new Response(false, "回答不存在!"));
        }
        if (comment == null) {
            return ResponseEntity.ok().body(new Response(false, "评论不存在!"));
        }
        try {
            //1、评论回答问题
            Answer commentAnswer = new Answer();
            commentAnswer.setQuestionId(answer.getQuestionId());
            commentAnswer.setContent(content);
            commentAnswer.setUserId(user.getId());
            commentAnswer.setReplyUserId(comment.getUserId());
            commentAnswer.setPid(answerId);
            commentAnswer.setCommentSize(0);
            commentAnswer.setIsAccepted(IsAcceptedEnum.NOT_ACCEPT_STATUS.getCode());
            commentAnswer.setCommentSize(0);
            commentAnswer.setUserId(user.getId());
            commentAnswer.setCaiSize(0);
            commentAnswer.setZanSize(0);
            commentAnswer.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            answerService.insert(commentAnswer);

            //2、修改回答的评论数目
            answer.setCommentSize(answer.getCommentSize() + 1);
            answerService.update(answer);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功!", null));
    }

    //    用户中心
    @GetMapping("/manage/answers")
    public ModelAndView answerManage(Model model,
                                     @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                     @RequestParam(value = "status", required = false, defaultValue = "") String status,
                                     @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                     HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("home/login/login");
        }
        Answer condition = new Answer();
        condition.setStatus(status);
        condition.setUserId(user.getId());
        PageVO<Answer> page = answerService.findAll(condition, pageIndex, pageSize);
        for (Answer temp : page.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
            temp.setQuestion(questionService.queryById(temp.getQuestionId()));

        }
        model.addAttribute("page", page);
        model.addAttribute("site_title", SiteTitleEnum.MY_ANSWER_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/question/answer-list :: #tab-pane" : "home/userspace/question/answer-list");
    }

    @PostMapping("/answers/accept")
    public ResponseEntity<Response> acceptAnswer(Long answerId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户未登录!"));
        }
        Answer answer = answerService.queryById(answerId);
        if (answer == null) {
            return ResponseEntity.ok().body(new Response(false, "该回答不存在"));
        }
        Question question = questionService.queryById(answer.getQuestionId());
        if (question != null && Objects.equals(question.getUserId(), user.getId())) {
            //采纳答案
            if (PostStatusEnum.PUBLISH_POST.getCode().equals(question.getStatus())) {
                //保存问题
                question.setStatus(PostStatusEnum.RESOLVED_POST.getCode());
                questionService.update(question);
                //保存回答
                answer.setIsAccepted(IsAcceptedEnum.ACCEPT_STATUS.getCode());
                answerService.update(answer);
            }
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限！"));

        }
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }

    //后台
    @GetMapping("/admin/answer")
    public ModelAndView adminArticleList(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        Answer condition = new Answer();
        condition.setStatus(status);
        PageVO<Answer> answerPage = answerService.findAll(condition, pageIndex, pageSize);
        for (Answer temp : answerPage.getContent()) {
            temp.setUser(userService.queryById(temp.getUserId()));
            temp.setQuestion(questionService.queryById(temp.getQuestionId()));
        }
        model.addAttribute("page", answerPage);
        return new ModelAndView(async == true ? "admin/answer/list :: #mainContainerReplace" : "admin/answer/list");
    }

    /**
     * 删除回答
     *
     * @return
     */
    @DeleteMapping("/admin/answer/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
        try {
            answerService.deleteById(id);
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
    @PutMapping("/admin/answer/restore/{id}")
    public ResponseEntity<Response> restore(@PathVariable("id") Long id) {
        try {
            Answer answer = answerService.queryById(id);
            answer.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            answerService.update(answer);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


}
