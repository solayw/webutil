package com.github.solayw.webutil

import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

class Mapper<E, V>( entityType: Class<E>, viewType: Class<V>,) {
    private val fields = ArrayList<Pair<Field, Field>>()
    private val methods = ArrayList<Pair< Method, Field>>()
    init {
        val sourceFields = ReflectionUtil.fields(entityType, false, true, true, true, false, null)
        val sourceMethods = ReflectionUtil.methods(entityType, false, true, true, null)
            .filter { it.parameterCount == 0 }
        val targetFields = ReflectionUtil.fields(viewType, false, true, false, true, false, null)
        for (tf in targetFields) {
            val sf = sourceFields.find { sf -> sf.name == tf.name && tf.type.isAssignableFrom(sf.type) }
            if(sf != null) {
                sf.isAccessible = true
                tf.isAccessible = true
                fields.add(Pair(sf, tf))
                continue
            }
            val sm = sourceMethods.find { sm -> sm.name == tf.name && tf.type.isAssignableFrom(sm.returnType)}
            if(sm != null) {
                sm.isAccessible = true
                tf.isAccessible = true
                methods.add(Pair(sm, tf))
            }
        }
    }
    fun apply(entity: E, view: V) {
        val e = if(entity is HibernateProxy) {
            entity.hibernateLazyInitializer.initialize()
            Hibernate.unproxy(this)
        }  else {
            this
        }
        fields.forEach { it.second.set(view, it.first.get(e)) }
        methods.forEach { it.second.set(view, it.first.invoke(e)) }
    }
}
