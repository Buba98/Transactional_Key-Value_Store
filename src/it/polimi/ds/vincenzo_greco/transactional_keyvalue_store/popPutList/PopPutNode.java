package it.polimi.ds.vincenzo_greco.transactional_keyvalue_store.popPutList;

class PopPutNode<T> {
    protected final T keyValue;
    protected PopPutNode<T> nextNode;
    protected PopPutNode<T> previousNode;

    public PopPutNode(T keyValue) {
        this.keyValue = keyValue;
    }
}
