package com.github.solayw.webutil;

public class Page
{


    public static boolean startWithZero = false;
    public static int defaultPageSize = 20;

    private int currentPage;
    private int pageSize = defaultPageSize;

    public void setCurrentPage(int currentPage) {
        int min = startWithZero ? 0 : 1;
        if(currentPage < min) {
            throw new IllegalArgumentException("currentPage min value " + min);
        }
        this.currentPage = currentPage;
    }

    public int offset() {
        return (startWithZero ? currentPage: currentPage - 1) * pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
