/*
 *
 *
 * Copyright (c) 2000-2004 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * See the file \"LICENSE\" for information on usage and redistribution
 * of this file.
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO
 * ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
 * SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE
 * IS PROVIDED ON AN "AS IS" BASIS, AND THE AUTHORS AND
 * DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *
 */
package com.onemoonscientific.swank;

/**
 *
 * @author brucejohnson
 */
public class ResourceObject {

    /**
     *
     */
    public String resource = null;
    /**
     *
     */
    public String className = null;
    /**
     *
     */
    public String defaultVal = null;
    /**
     *
     */
    public int optNum = 0;

    /**
     *
     * @param resource
     * @param className
     */
    public ResourceObject(String resource, String className) {
        this.resource = resource;
        this.className = className;
    }

    /**
     *
     * @param resource
     * @param className
     * @param optNum
     */
    public ResourceObject(String resource, String className, int optNum) {
        this.resource = resource;
        this.className = className;
        this.optNum = optNum;
    }

    /**
     *
     * @param resource
     * @param className
     * @param optNum
     * @param defaultVal
     */
    public ResourceObject(String resource, String className, int optNum,
            String defaultVal) {
        this.resource = resource;
        this.className = className;
        this.optNum = optNum;
        this.defaultVal = defaultVal;
    }
}
