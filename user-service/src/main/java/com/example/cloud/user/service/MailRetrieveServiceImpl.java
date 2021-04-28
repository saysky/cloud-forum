package com.example.cloud.user.service;

import com.example.cloud.common.vo.PageVO;
import com.example.cloud.common.vo.ResultVO;
import com.example.cloud.user.api.entity.MailRetrieve;
import com.example.cloud.user.dao.MailRetrieveDao;
import com.example.cloud.user.api.feign.MailRetrieveService;
import com.example.cloud.user.util.MD5Util;
import com.example.cloud.user.util.RandomUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 邮件记录表(MailRetrieve)表服务实现类
 *
 * @author makejava
 * @since 2021-03-28 21:31:53
 */
@Service("mailRetrieveService")
public class MailRetrieveServiceImpl implements MailRetrieveService {
    @Resource
    private MailRetrieveDao mailRetrieveDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public MailRetrieve queryById(Long id) {
        return this.mailRetrieveDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<MailRetrieve> queryAllByLimit(int offset, int limit) {
        return this.mailRetrieveDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param mailRetrieve 实例对象
     * @return 实例对象
     */
    @Override
    public MailRetrieve insert(MailRetrieve mailRetrieve) {
        this.mailRetrieveDao.insert(mailRetrieve);
        return mailRetrieve;
    }

    /**
     * 修改数据
     *
     * @param mailRetrieve 实例对象
     * @return 实例对象
     */
    @Override
    public MailRetrieve update(MailRetrieve mailRetrieve) {
        this.mailRetrieveDao.update(mailRetrieve);
        return this.queryById(mailRetrieve.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.mailRetrieveDao.deleteById(id) > 0;
    }

    @Override
    public MailRetrieve findByAccount(String account) {
        return this.mailRetrieveDao.findByAccount(account);
    }

    @Override
    public String getEmailUrl(String basePath, String account) {
        //生成邮件URL唯一地址
        String key = RandomUtil.getRandom(6) + "";
        long outtimes = System.currentTimeMillis() + 30 * 60 * 1000;
        String sid = account + "&" + key + "&" + outtimes;
        MailRetrieve mailRetrieve = new MailRetrieve(account, MD5Util.encode(sid), outtimes);
        mailRetrieve.setCreateTime(System.currentTimeMillis());
        MailRetrieve findMailRetrieve = mailRetrieveDao.findByAccount(account);
        if (findMailRetrieve != null) {
            mailRetrieveDao.deleteById(findMailRetrieve.getId());
        }
        try {
            mailRetrieveDao.insert(mailRetrieve);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return basePath + "?sid=" + MD5Util.encode(sid) + "&username=" + account;
    }

    @Override
    public ResultVO verifyMailUrl(String sid, String username) {
        ResultVO resultVO = new ResultVO();
        MailRetrieve mailRetrieve = mailRetrieveDao.findByAccount(username);
        if (mailRetrieve != null) {
            long outTime = mailRetrieve.getOutTime();
            long nowTime = System.currentTimeMillis();
            if (outTime <= nowTime) {
                resultVO.setCode(1);
                resultVO.setMsg("邮件已经过期！");
            } else if ("".equals(sid)) {
                resultVO.setCode(1);
                resultVO.setMsg("sid不完整！");
            } else if (!sid.equals(mailRetrieve.getSid())) {
                resultVO.setCode(1);
                resultVO.setMsg("sid错误！");
            } else {
                resultVO.setCode(0);
                resultVO.setMsg("验证成功！");
            }
        } else {
            //account 对应的用户不存在
            resultVO.setCode(1);
            resultVO.setMsg("链接无效！");

        }
        return resultVO;
    }

    @Override
    public PageVO<MailRetrieve> findAll(MailRetrieve condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<MailRetrieve> lists = mailRetrieveDao.queryAll(condition);
        PageInfo<MailRetrieve> pageInfo = new PageInfo<>(lists);
        return PageVO.build(pageInfo);

    }

}