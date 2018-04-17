/*
Program #3
Jeremy Krick
cssc0915
*/

package data_structures;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BinaryHeapPriorityQueue<E extends Comparable<E>>
        implements PriorityQueue<E> {
    private int maxSize, currentSize;
    private long modCounter, entryNumber;
    private Wrapper<E> [] storage;

    private class Wrapper<E> implements Comparable<Wrapper<E>> {
        E data;
        long sequenceNumber;

        public Wrapper(E obj) {
            data = obj;
            sequenceNumber = entryNumber++;
        }

        public int compareTo(Wrapper<E> obj) {
            int tmp = ((Comparable<E>)data).compareTo(obj.data);
            if (tmp == 0)
                return (int)(sequenceNumber - obj.sequenceNumber);
            return tmp;
        }

        public String toString() {return ""+data;}
    }

    public BinaryHeapPriorityQueue() {this(DEFAULT_MAX_CAPACITY);}

    public BinaryHeapPriorityQueue(int size) {
        currentSize = 0;
        modCounter = 0;
        maxSize = size;
        entryNumber = 0;
        storage = new Wrapper[maxSize];
    }

    private void trickleUp() {
        int newIndex = currentSize-1;
        int parentIndex = (newIndex-1) >> 1;
        Wrapper<E> newValue = storage[newIndex];
        while(parentIndex >= 0 && newValue.compareTo(storage[parentIndex]) < 0) {
            storage[newIndex] = storage[parentIndex];
            newIndex = parentIndex;
            parentIndex = (parentIndex-1) >> 1;
        }
        storage[newIndex] = newValue;
        modCounter++;
    }

    public boolean insert(E object) {
        if (isFull()) return false;
        Wrapper<E> obj = new Wrapper<E>(object);
        storage[currentSize++] = obj;
        trickleUp();
        return true;
    }

    private void trickleDown() {
        int current = 0;
        int child = getNextChild(current);      // gets smallest child
        while (child != -1 &&
                storage[current].compareTo(storage[child]) < 0 &&
                storage[child].compareTo(storage[currentSize-1]) < 0) {
            storage[current] = storage[child];
            current = child;
            child = getNextChild(current);
        }
        storage[current] = storage[currentSize-1];
        currentSize--;
        modCounter++;
    }

    private int getNextChild(int current) {
        int left = (current << 1) + 1;
        int right = left+1;
        if (right < currentSize) {  // there are two children
            if (storage[left].compareTo(storage[right]) < 0)
                return left;        // the left child is smaller
            return right;           // the right child is smaller
        }
        if (left < currentSize)     // there is only one child
            return left;
        return -1;                  // no children
    }

    public E remove() {
        if (isEmpty()) return null;
        Wrapper<E> tmp = storage[0];
        trickleDown();
        return tmp.data;
    }

    public boolean delete(E obj) {
        BinaryHeapPriorityQueue<E> tmp = new BinaryHeapPriorityQueue(currentSize);
        boolean didDelete = false;
        for (int i=0; i < currentSize; i++) {
            if (storage[i].data.compareTo(obj) != 0) {
                tmp.insert(storage[i].data);
            } else {
                didDelete = true;
                modCounter++;
            }
        }
        storage = tmp.storage;
        currentSize = tmp.currentSize;
        return didDelete;
    }

    public E peek() {
        if (isEmpty()) return null;
        return storage[0].data;
    }

    public boolean contains(E obj) {
        for (int i=0; i < currentSize; i++)
            if (storage[i].data.compareTo(obj) == 0)
                return true;
        return false;
    }

    public int size() {return currentSize;}

    public void clear() {
        currentSize = 0;
        modCounter = 0;
    }

    public boolean isEmpty() {return currentSize == 0;}

    public boolean isFull() {return currentSize == maxSize;}

    public Iterator<E> iterator() {return new IteratorHelper();}

    class IteratorHelper implements Iterator<E> {
        int iterIndex;
        long modCheck;

        public IteratorHelper() {
            iterIndex = 0;
            modCheck = modCounter;
        }

        public boolean hasNext() {
            if (modCheck != modCounter)
                throw new ConcurrentModificationException();
            return iterIndex < currentSize;
        }

        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            return storage[iterIndex++].data;
        }

        public void remove() {throw new UnsupportedOperationException();}
    }
}