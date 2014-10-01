package edu.wpi.first.wpijavacv;

/**
 * This class allows a bunch of disposable items to be put into a pool which will do something if all of them are disposed.
 * Subclasses should override the dispose method to react to when everything in the pool is disposed.
 * @author Joe Grinstead
 */
public abstract class WPIMemoryPool extends WPIDisposable {

    /** The number of elements remaining in the pool */
    private int remaining;

    /**
     * Adds the given disposable item to the memory pool
     * @param disposable the item
     */
    public synchronized void addToPool(WPIDisposable disposable) {
        validateDisposed();
        disposable.setPool(this);
        remaining++;
    }

    /**
     * Removes the given disposable item from the memory pool
     * @param disposable
     */
    public synchronized void removeFromPool(WPIDisposable disposable) {
        validateDisposed();
        disposable.setPool(null);
        if (--remaining <= 0) {
            dispose();
        }
    }
}
