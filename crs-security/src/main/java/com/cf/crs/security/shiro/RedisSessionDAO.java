package com.cf.crs.security.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis实现共享session
 */
@Component
@Slf4j
public class RedisSessionDAO extends EnterpriseCacheSessionDAO {
    // session 在redis过期时间是120分钟120*60
    private static int expireTime = 7200;

    private static String prefix = "crs:shiro-session:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static ThreadLocal sessionsInThread = new ThreadLocal();

    private static final long DEFAULT_SESSION_IN_MEMORY_TIMEOUT = 1000L;

    private long sessionInMemoryTimeout = DEFAULT_SESSION_IN_MEMORY_TIMEOUT;

    // 创建session，保存到redis
    @Override
    protected Serializable doCreate(Session session) {

        Serializable sessionId = super.doCreate(session);
        String key = prefix + sessionId.toString();
        redisTemplate.opsForValue().set(key, session);
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        return sessionId;
    }

    // 获取session
    @Override
    protected Session doReadSession(Serializable sessionId) {
        //从threadLocal中读取session
//        System.out.println("读取session start" + sessionId);
//        long startTime = System.currentTimeMillis();
        Session session  = getSessionFromThreadLocal(sessionId);
        if (session == null) {
            // 从缓存中获取session
            session = (Session) redisTemplate.opsForValue().get(prefix + sessionId.toString());
            //将session存入threadLocal
            setSessionToThreadLocal(sessionId, session);
        }
//        long endTime = System.currentTimeMillis();
//        System.out.println("读取session end" + sessionId);
//        System.out.println("读取session:"  + (endTime - startTime));
        return session;
    }

    // 更新session的最后一次访问时间
    @Override
    protected void doUpdate(Session session) {
        if (session == null || session.getId() == null) {
            throw new UnknownSessionException("session or session id is null");
        }
        String key = prefix + session.getId().toString();
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, session);
        }
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        //将session存入ThreadLocal
        this.setSessionToThreadLocal(session.getId(), session);
    }

    // 删除session
    @Override
    protected void doDelete(Session session) {
        super.doDelete(session);
        redisTemplate.delete(prefix + session.getId().toString());
    }

    private void setSessionToThreadLocal(Serializable sessionId, Session s) {
        Map<Serializable, SessionInMemory> sessionMap = (Map<Serializable, SessionInMemory>) sessionsInThread.get();
        if (sessionMap == null) {
            sessionMap = new HashMap<Serializable, SessionInMemory>();
            sessionsInThread.set(sessionMap);
        }

        removeExpiredSessionInMemory(sessionMap);

        SessionInMemory sessionInMemory = new SessionInMemory();
        sessionInMemory.setCreateTime(new Date());
        sessionInMemory.setSession(s);
        sessionMap.put(sessionId, sessionInMemory);
    }

    private void removeExpiredSessionInMemory(Map<Serializable, SessionInMemory> sessionMap) {
        Iterator<Serializable> it = sessionMap.keySet().iterator();
        while (it.hasNext()) {
            Serializable sessionId = it.next();
            SessionInMemory sessionInMemory = sessionMap.get(sessionId);
            if (sessionInMemory == null) {
                it.remove();
                continue;
            }
            long liveTime = getSessionInMemoryLiveTime(sessionInMemory);
            if (liveTime > sessionInMemoryTimeout) {
                it.remove();
            }
        }
    }

    private Session getSessionFromThreadLocal(Serializable sessionId) {

        if (sessionsInThread.get() == null) {
            return null;
        }

        Map<Serializable, SessionInMemory> sessionMap = (Map<Serializable, SessionInMemory>) sessionsInThread.get();
        SessionInMemory sessionInMemory = sessionMap.get(sessionId);
        if (sessionInMemory == null) {
            return null;
        }
        long liveTime = getSessionInMemoryLiveTime(sessionInMemory);
        if (liveTime > sessionInMemoryTimeout) {
            sessionMap.remove(sessionId);
            return null;
        }

        return sessionInMemory.getSession();
    }

    private long getSessionInMemoryLiveTime(SessionInMemory sessionInMemory) {
        Date now = new Date();
        return now.getTime() - sessionInMemory.getCreateTime().getTime();
    }
}
