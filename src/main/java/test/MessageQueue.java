package test;

import java.util.LinkedList;

public class MessageQueue<T> {
    private LinkedList<T> storage = new LinkedList<T>();

    public synchronized void push(T e) {//需要加上同步
        storage.add(e);
    }

    public T peek() {
        return storage.getFirst();
    }

    public void pop() {
        storage.removeFirst();
    }

    public boolean empty() {
        return storage.isEmpty();
    }

    @Override
    public String toString() {
        return storage.toString();
    }
}