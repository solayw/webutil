package com.github.solayw.webutil.jpa;


import com.github.solayw.webutil.Page;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import lombok.experimental.Delegate;


public class EntityManagerDelegate implements EntityManager
{
    @Delegate
    private EntityManager entityManager;

    public EntityManagerDelegate(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public static Object singleOrNull(Query query) {
        final List list = query.getResultList();
        if(list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public <T> PageList<T> pageQuery(PageQueryObj<T> pageQueryObj) {
        final List<T> list = pageQueryObj.queryForList(this);
        final int count = pageQueryObj.queryForCount(this);
        PageList<T> pageList = new PageList<>(list);
        pageList.total = count;
        Page page = Objects.requireNonNull(pageQueryObj.getPage());
        int totalPage = count / page.getPageSize();
        if(totalPage * page.getPageSize() != count) {
            totalPage += 1;
        }
        pageList.totalPage = totalPage;
        return pageList;
    }


}
