package com.github.solayw.webutil.jpa;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;

public class PageList<T> extends ArrayList<T>
{
    public PageList(Collection<? extends T> c) {
        super(c);
    }

    @Getter
    int total;
    @Getter
    int totalPage;

}
