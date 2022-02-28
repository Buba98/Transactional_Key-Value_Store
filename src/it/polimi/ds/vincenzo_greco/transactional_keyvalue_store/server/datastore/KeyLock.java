package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.server.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyLock {

    final Map<String, List<Lock>> keyLock = new HashMap<>();
    private final List<Lock> emptyList = new ArrayList<>();

    private LockType getLockType(String key) {
        List<Lock> locks = keyLock.getOrDefault(key, emptyList);

        if (locks.size() > 1) {
            return LockType.SHARED;
        } else if (locks.size() == 1) {
            return locks.get(0).lockType;
        } else {
            return LockType.FREE;
        }
    }

    private void addLock(Lock lock) {
        List<Lock> locks = keyLock.computeIfAbsent(lock.key, k -> new ArrayList<>());

        locks.add(lock);
    }

    private List<Lock> removeLock(Lock lock) {
        List<Lock> locks = keyLock.get(lock.key);
        Lock l;

        l = locks.remove(0);

        assert l.lockType == lock.lockType;

        return locks;
    }

    public synchronized void lock(Lock lock) throws InterruptedException {

        assert lock.lockType != LockType.FREE;

        if (lock.lockType == LockType.SHARED) {
            while (getLockType(lock.key) == LockType.EXCLUSIVE) {
                wait();
            }
            addLock(lock);
        } else {
            while (getLockType(lock.key) != LockType.FREE) {
                wait();
            }
            addLock(lock);
        }
    }

    public synchronized void free(Lock lock) {
        if (removeLock(lock).size() == 0) {
            notifyAll();
        }
    }
}
