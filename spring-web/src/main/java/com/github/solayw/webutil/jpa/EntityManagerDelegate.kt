package com.github.solayw.webutil.jpa

import java.util.*
import javax.persistence.EntityManager
import javax.persistence.Query


class EntityManagerDelegate(private val entityManager: EntityManager) : EntityManager by entityManager {
    fun <T> pageQuery(pageQueryObj: PageQueryObj<T>): PageList<T> {
        val list = pageQueryObj.queryForList(this)
        val count = pageQueryObj.queryForCount(this)
        val pageList = PageList(list)
        pageList.total = count
        val page = Objects.requireNonNull(pageQueryObj.page)
        var totalPage = count / page.pageSize
        if (totalPage * page.pageSize != count) {
            totalPage += 1
        }
        pageList.totalPage = totalPage
        return pageList
    }

    companion object {
        fun singleOrNull(query: Query): Any? {
            val list = query.resultList
            return if (list.size > 0) {
                list[0]
            } else null
        }
    }
}
