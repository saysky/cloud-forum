package com.example.cloud.article.service;

import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.entity.ArticleZan;
import com.example.cloud.article.api.entity.Article;
import com.example.cloud.article.api.feign.ArticleService;
import com.example.cloud.article.dao.ArticleDao;
import com.example.cloud.article.dao.ArticleZanDao;
import com.example.cloud.article.dao.CategoryDao;
import com.example.cloud.common.vo.PageVO;
import com.example.cloud.user.api.feign.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 文章表(Article)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("articleService")
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleDao articleDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private ArticleZanDao articleZanDao;

    @Resource
    private UserService userService;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Article queryById(Long id) {
        Article article =  this.articleDao.queryById(id);
        article.setUser(userService.queryById(article.getUserId()));
        return article;
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Article> queryAllByLimit(int offset, int limit) {
        return this.articleDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param article 实例对象
     * @return 实例对象
     */
    @Override
    public Article insert(Article article) {
        article.setCreateTime(new Date());
        this.articleDao.insert(article);
        return article;
    }

    /**
     * 修改数据
     *
     * @param article 实例对象
     * @return 实例对象
     */
    @Override
    public Article update(Article article) {
        this.articleDao.update(article);
        return this.queryById(article.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.articleDao.deleteById(id) > 0;
    }


    @Override
    public Article viewIncrease(Long id) {
        //添加当前文章的cookie
        Article article = articleDao.queryById(id);
        if (article == null) {
            return null;
        }
        article.setViewSize(article.getViewSize() + 1);
        this.update(article);
        return article;
    }


    @Override
    public int createZan(Long articleId, Long userId) {
        ArticleZan articleZan = articleZanDao.queryByUserIdAndArticleId(userId, articleId);
        if (articleZan == null) {
            // 点赞
            articleZan = new ArticleZan();
            articleZan.setArticleId(articleId);
            articleZan.setUserId(userId);
            articleZanDao.insert(articleZan);
            Article article = articleDao.queryById(articleId);
            if (article != null) {
                article.setZanSize(article.getZanSize() + 1);
                articleDao.update(article);
            }
            return article.getZanSize();
        } else {
            // 取消点赞
            articleZanDao.deleteById(articleZan.getId());
            Article article = articleDao.queryById(articleId);
            if (article != null) {
                article.setZanSize(article.getZanSize() - 1 > 0 ? article.getZanSize() - 1 : 0);
                articleDao.update(article);
            }
            return article.getZanSize();
        }
    }

    @Override
    public List<Article> listTop10HotArticles() {
        return articleDao.findTop10HotArticle();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemove(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            articleDao.deleteById(ids.get(i));
        }
    }

    @Override
    public PageVO<Article> findAll(Article condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, condition.getOrderBy());
        List<Article> lists = articleDao.queryAll(condition);
        PageInfo<Article> pageInfo = new PageInfo<>(lists);

        for (Article article : pageInfo.getList()) {
            article.setCategory(categoryDao.queryById(article.getCategoryId()));
        }
        return PageVO.build(pageInfo);

    }

    @Override
    public void deleteByStatus(String status) {
        articleDao.deleteByStatus(status);
    }

    @Override
    public Long countArticleByStatus(String status) {
        return articleDao.countArticleByStatus(status);
    }

    @Override
    public Long countArticleByCategoryId(Long categoryId) {
        return articleDao.countArticleByCategoryId(categoryId);
    }
}