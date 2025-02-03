/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author david.morin
 */
public class DraggedImage {
    private Point currPoint;
    private Point lastPoint;
    private int draggedWidth;
    private int draggedHeight;
    private BufferedImage dragged;
    private Component component;
    private boolean isDragged;

    DraggedImage(Component component, Point currPoint, Point cursoroffset, boolean isDragged) {
        this.component = component;
        dragged = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = dragged.getGraphics();
        component.paint(g);
        // The draggedWidth and draggedHeight represent the distance between
        // the components x and the position of the drag cursor.
        draggedWidth = cursoroffset.x;
        draggedHeight = cursoroffset.y;
        this.currPoint = (Point)currPoint.clone();
        this.lastPoint = (Point)currPoint.clone();
        this.isDragged = isDragged;
    }

    public Component getComponent() {
        return component;
    }

    public boolean getIsDragged() {
        return isDragged;
    }

    public Point getCurrPoint() {
        return currPoint;
    }

    public void setCurrPoint(Point currPoint) {
        this.lastPoint = this.currPoint;
        this.currPoint = (Point) currPoint.clone();
    }

    public BufferedImage getDragged() {
        return dragged;
    }

    public Point getLastPoint() {
        return lastPoint;
    }

    public int getDraggedHeight() {
        return draggedHeight;
    }

    public int getDraggedWidth() {
        return draggedWidth;
    }
}
