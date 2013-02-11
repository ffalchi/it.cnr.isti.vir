/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.geom;

/**
 *
 * @author Fabrizio
 */
public class Box {

    public static final boolean xyInBox(float[] xy, float[][] box) {
        if (    xy[0] < box[0][0] ||
                xy[1] < box[0][1] ||
                xy[0] > box[1][0] ||
                xy[1] > box[1][1] ) {
            return false;
        } else {
            return true;
        }
    }

}
