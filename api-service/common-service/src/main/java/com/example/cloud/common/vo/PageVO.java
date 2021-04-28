package com.example.cloud.common.vo;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 分页工具类
 *
 * @author 言曌
 * @date 2021/3/30 6:31 下午
 */
public class PageVO<T> {

    private long number;

    private long totalPages;

    private boolean first;

    private boolean last;

    private long totalElements;

    private List<T> content;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public static PageVO build(PageInfo pageInfo) {
        PageVO pageVO = new PageVO();
        pageVO.setTotalElements(pageInfo.getTotal());
        pageVO.setNumber(pageInfo.getPageNum() - 1);
        pageVO.setTotalPages(pageInfo.getPages());
        pageVO.setFirst(pageInfo.isIsFirstPage());
        pageVO.setLast(pageInfo.isIsLastPage());
        pageVO.setContent(pageInfo.getList());
        return pageVO;
    }


}
