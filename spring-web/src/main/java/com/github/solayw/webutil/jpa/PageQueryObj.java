package com.github.solayw.webutil.jpa;

import com.github.solayw.webutil.Page;

import java.util.List;

import javax.persistence.EntityManager;

public interface PageQueryObj<T>
{

    Page getPage();



    default T convert(Object rawResult) {
        return (T) rawResult;
    }
    List<T> queryForList(EntityManager em);

    int queryForCount(EntityManager em);
}
