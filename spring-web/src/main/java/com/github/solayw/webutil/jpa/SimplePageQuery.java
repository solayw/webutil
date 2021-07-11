package com.github.solayw.webutil.jpa;

import com.github.solayw.webutil.Page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import lombok.Getter;
import lombok.SneakyThrows;

public abstract class SimplePageQuery<T> implements PageQueryObj<T>
{

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Equal {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Like {
        String value();
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Range {
        String value();
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Raw {}
    abstract String select();
    abstract String selectCount();

    abstract String from();

    abstract String orderBy();

    final void addArgument(String key, Object arg) {
        args.put(key, arg);
    }
    HashMap<String, Object> args = new HashMap<>();
    private static final ConcurrentHashMap<Class, List<Field>> fields = new ConcurrentHashMap<>();

    @Getter
    private Page page = new Page();

    private List<Field> _fields;



    @SneakyThrows
    String where() {
        ArrayList<String> sb = new ArrayList<String>();
        for (Field field : _fields) {
            final Object obj = field.get(this);
            String argName = field.getName();
            if (obj == null || (obj instanceof String && (((String) obj).isEmpty()))) {
                continue;
            }
            final Equal eq = field.getAnnotation(Equal.class);
            if (eq != null) {
                sb.add(eq.value() + " = :" + argName);
                args.put(argName, obj);
                continue;
            }
            final Like like = field.getAnnotation(Like.class);
            if (like != null) {
                sb.add(like.value() + " like :" + argName);
                args.put(argName, "%" + (String) obj+ "%");
                continue;
            }
            final Range range = field.getAnnotation(Range.class);
            if(range != null) {
                final com.github.solayw.webutil.Range _range = (com.github.solayw.webutil.Range) obj;
                if(_range.start() != null) {
                    String key = argName + "Start";
                    sb.add(range.value() + " >= :" + key);
                    args.put(key, _range.start());
                }
                if(_range.end() != null) {
                    String key = argName + "End";
                    sb.add(range.value() + " < :" + key);
                    args.put(key, _range.end());
                }
                continue;
            }
            Raw raw = field.getAnnotation(Raw.class);
            if(raw != null) {
                String s;
                if(obj instanceof Supplier) {
                    s = ((String) ((Supplier<?>) obj).get());
                } else {
                    s = (String) obj;
                }
                sb.add("(" + s + ")");
            }
        }
        StringBuilder _sb = new StringBuilder();
        for (int i = 0; i < sb.size(); i++) {
            _sb.append(
                    i == 0 ? "where": " and"
            ).append(" ").append(sb.get(i));
        }
        return _sb.toString();
    }



    public SimplePageQuery() {
        _fields = SimplePageQuery.fields.get(getClass());
        if(_fields == null) {
            _fields = new ArrayList<>();
            Class _clazz = this.getClass();
            while (_clazz != SimplePageQuery.class) {
                for(Field _field : _clazz.getDeclaredFields()) {
                    if (Modifier.isStatic(_field.getModifiers())) {
                        continue;
                    }
                    _field.setAccessible(true);
                    _fields.add(_field);
                }
                _clazz = _clazz.getSuperclass();
            }
            fields.put(getClass(), _fields);
        }
    }


    private String _where = null;
    @Override
    public List<T> queryForList(EntityManager em) {
        if(_where == null) {
            _where = where();
        }
        String hql = select() + " " + from() + " " + _where;
        if(orderBy() != null) {
            hql += " " + orderBy();
        }
        final Query q = em.createQuery(hql);
        if(page != null) {
            q.setMaxResults(page.getPageSize());
            q.setFirstResult(page.offset());
        }
        args.forEach(q::setParameter);
        List<T> res = new ArrayList<>();
        for (Object o : q.getResultList()) {
            res.add(convert(o));
        }
        return res;
    }

    @Override
    public int queryForCount(EntityManager em) {
        if(_where == null) {
            _where = where();
        }
        String hql = selectCount() + " " + from() + " " + _where;
        final Query q = em.createQuery(hql);
        args.forEach(q::setParameter);
        final Object o = q.getSingleResult();
        return ((Number)(o)).intValue();
    }

    public void setCurrentPage(int currentPage) {
        page.setCurrentPage(currentPage);
    }
    public void setPageSize(int pageSize) {
        page.setPageSize(pageSize);
    }

}
