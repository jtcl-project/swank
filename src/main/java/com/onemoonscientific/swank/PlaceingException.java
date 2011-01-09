package com.onemoonscientific.swank;

/**
 *
 * @author brucejohnson
 */
//this exception is thrown if invalid arguments are passed
//to the placer layout
class PlaceingException extends RuntimeException {

    public PlaceingException(String desc) {
        super(desc);
    }
}
