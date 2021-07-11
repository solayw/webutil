package com.github.solayw.webutil.jpa;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SimplePageQueryTest
{
    public static class QueryObj extends SimplePageQuery<Object> {
        @Override
        protected String select() {
            return "select a";
        }

        @Override
        protected String from() {
            return "from Person a";
        }

        @Override
        protected String orderBy() {
            return "a.createTime desc";
        }



        @Override
        protected String selectCount() {
            return "select count(a)";
        }
        @Equal("a.age")
        Integer age;

        @Like("a.name")
        String name;


        @Raw
        Supplier<String> rate = () -> {
            addArgument("rate", Arrays.asList(1,2,3));
            return "a.rate in :rate";
        };

        @Range("a.range")
        com.github.solayw.webutil.Range range = new com.github.solayw.webutil.Range() {
            @Override
            public Object start() {
                return 1;
            }

            @Override
            public Object end() {
                return 2;
            }
        };

    }
    @Test
    public void doTest() {
        QueryObj obj = new QueryObj();
        obj.age = 1;
        obj.name = "sds";
        EntityManager em = mock(EntityManager.class);
        Query query = mock(Query.class);
        when(query.getSingleResult()).thenReturn(100);
        when(em.createQuery(anyString())).thenReturn(query);
        obj.queryForList(em);
        verify(em).createQuery(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(Object _argument) {
                String s = (String) _argument;
                return s.startsWith("select a from Person a where")
                        && s.contains(" a.name like :name")
                        && s.contains(" a.age = :age ")
                        && s.contains(" (a.rate in :rate) ")
                        && s.contains(" a.range >= :rangeStart ")
                        && s.contains(" a.range < :rangeEnd ")
                        && s.endsWith(" a.createTime desc");
            }
        }));
        boolean pass = obj.args.get("name").equals("%" + obj.name + "%")
                && obj.args.get("age").equals(obj.age)
                && obj.args.get("rate").equals(Arrays.asList(1,2,3))
                && obj.args.get("rangeStart").equals(obj.range.start())
                && obj.args.get("rangeEnd").equals(obj.range.end());
        if(!pass) {
            fail();
        }

    }


}
