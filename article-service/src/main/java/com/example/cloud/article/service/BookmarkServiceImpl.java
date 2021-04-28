package com.example.cloud.article.service;

import com.example.cloud.article.api.entity.Bookmark;
import com.example.cloud.article.api.feign.BookmarkService;
import com.example.cloud.article.dao.BookmarkDao;
import com.example.cloud.common.vo.PageVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收藏表(Bookmark)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("bookmarkService")
public class BookmarkServiceImpl implements BookmarkService {
    @Resource
    private BookmarkDao bookmarkDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Bookmark queryById(Long id) {
        return this.bookmarkDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Bookmark> queryAllByLimit(int offset, int limit) {
        return this.bookmarkDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param bookmark 实例对象
     * @return 实例对象
     */
    @Override
    public Bookmark insert(Bookmark bookmark) {
        this.bookmarkDao.insert(bookmark);
        return bookmark;
    }

    /**
     * 修改数据
     *
     * @param bookmark 实例对象
     * @return 实例对象
     */
    @Override
    public Bookmark update(Bookmark bookmark) {
        this.bookmarkDao.update(bookmark);
        return this.queryById(bookmark.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.bookmarkDao.deleteById(id) > 0;
    }


    @Override
    public boolean isMarkArticle(Long userId, Long articleId) {
        List<Bookmark> bookmarkList = bookmarkDao.findByUserIdAndArticleId(userId, articleId);
        if (bookmarkList == null || bookmarkList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public PageVO<Bookmark> findAll(Bookmark condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Bookmark> lists = bookmarkDao.queryAll(condition);
        PageInfo<Bookmark> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);

    }
}
