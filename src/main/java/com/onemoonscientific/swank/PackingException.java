package com.onemoonscientific.swank;

/**
 *
 * @author brucejohnson
 */
//this exception is thrown if invalid arguments are passed
//to the packer layout
class PackingException extends RuntimeException {

    public PackingException(String desc) {
        super(desc);
    }
}

