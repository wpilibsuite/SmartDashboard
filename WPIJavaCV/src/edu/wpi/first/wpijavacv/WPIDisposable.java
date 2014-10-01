package edu.wpi.first.wpijavacv;

import com.googlecode.javacpp.Pointer;

/**
 * This class is an abstract class defining disposable elements.
 * It is the superclass of most things which links directly to the javacv core.
 * 
 * @author Joe Grinstead
 */
public abstract class WPIDisposable {

    /** The memory pool to report to when disposed, or null if there is none */
    private WPIMemoryPool pool = null;
    
    /** Whether or not this is disposed */
    private boolean disposed = false;

    /**
     * Sets the {@link WPIMemoryPool} that this disposable is linked to.
     * @param pool the pool to link to (null is allowed)
     */
    protected void setPool(WPIMemoryPool pool) {
        this.pool = pool;
    }

    protected WPIMemoryPool getPool() {
        return pool;
    }

    /**
     * Disposes this object.  This may be called multiple times.
     *
     * Programmers should call this when they no longer need an object.  However, even if they don't, this will be called
     * when this object is collected by the garbage collector.
     */
    public void dispose() {
        if (!disposed) {
            disposed = true;
            disposed();
            if (pool != null) {
                pool.removeFromPool(this);
            }
        }
    }

    /**
     * This is called when {@link WPIDisposable#dispose() dispose()} is called for the first time.
     * Subclasses should clear out whatever internal resources they are using.
     */
    protected abstract void disposed();

    /**
     * Returns whether or not this object is disposed.
     * @return whether or not this object is disposed
     *
     * @see WPIDisposable#dispose() dispose()
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Checks if this {@link WPIDisposable} has already been disposed.  If it has,
     * then it will throw a {@link DisposedException} with a default message.
     */
    protected void validateDisposed() {
        if (disposed) {
            throw new DisposedException(this + " has been disposed");
        }
    }

    /**
     * Checks if this {@link WPIDisposable} has already been disposed.  If it has,
     * then it will throw a {@link DisposedException} with the given message.
     * @param message the message to give the exception
     */
    protected void validateDisposed(String message) {
        if (disposed) {
            throw new DisposedException(message);
        }
    }

    /**
     * Returns whether or not the given pointer is null in either the java sense or the javacv sense.
     * @param pointer the pointer
     * @return whether it is null
     */
    protected static boolean isNull(Pointer pointer) {
        return pointer == null || pointer.isNull();
    }

    /**
     * Attempts to free (in the c sense) the given pointer.  Does nothing if given null.
     * @param pointer the pointer to free
     */
    protected static void free(Pointer pointer) {
        if (pointer != null && !pointer.isNull()) {
            pointer.deallocate();
        }
    }

    /**
     * An exception to be thrown if an element has already been disposed and the user attempts to
     * perform an operation on it.
     */
    public static class DisposedException extends RuntimeException {

        public DisposedException(String message) {
            super(message);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
}
