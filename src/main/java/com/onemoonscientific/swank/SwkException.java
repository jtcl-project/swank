/*
 * SwkException.java
 *
 * Created on November 9, 2005, 2:43 PM
 */
package com.onemoonscientific.swank;

/**
 *
 * @author brucejohnson
 */
public class SwkException extends Exception {

    /** Creates a new instance of SwkException
     * @param message
     */
    public SwkException(String message) {
        super(message);
        System.out.println("new swkexcept " + message);
    }
}
