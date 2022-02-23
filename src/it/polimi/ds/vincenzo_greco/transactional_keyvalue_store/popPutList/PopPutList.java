package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.popPutList;

public class PopPutList<T> {
    private PopPutNode<T> head;
    private PopPutNode<T> tail;

    public synchronized T pop() {
        T res = head.keyValue;

        head = head.previousNode;
        head.nextNode = null;

        return res;
    }

    public synchronized void put(T value) {
        PopPutNode<T> popPutNode = new PopPutNode<T>(value);

        popPutNode.nextNode = tail;
        tail.previousNode = popPutNode;

        tail = popPutNode;
    }
}
