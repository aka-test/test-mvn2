/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echodatacontainer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.border.LineBorder;

/**
 *
 * @author david.morin
 */
public class DashedBorder extends LineBorder {

    public DashedBorder(Color c, boolean solid) {
        this(c, 1, solid);
    }

    public DashedBorder(Color c, int thickness, boolean solid) {
        this(c, thickness, new float[] {5, 5}, solid);
    }

    public DashedBorder(Color c, int thickness, float [] dash, boolean solid) {
        super(c, thickness);
        this.solid = solid;
        this.stroke = new BasicStroke(thickness, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL, 1, dash, 0);
    }

    private boolean solid;
    private BasicStroke stroke;

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int
    width, int height) {
        if(!solid) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(stroke);
        super.paintBorder(c, g2d, x, y, width, height);
        g2d.dispose();
        }
        else {
        super.paintBorder(c, g, x, y, width, height);
        }
    }
}


