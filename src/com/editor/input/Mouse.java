package com.editor.input;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import com.editor.MapEditor;

public class Mouse implements MouseMotionListener, MouseListener {
	
	private MapEditor editor;
	
	public Mouse(MapEditor editor) {
		this.editor = editor;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (editor.isDrawing) {
			editor.handleMouseDrag(e);
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			Point currentPoint = e.getPoint();
			int deltaX = currentPoint.x - editor.lastMousePosition.x;
			int deltaY = currentPoint.y - editor.lastMousePosition.y;
			editor.cameraOffset.translate(deltaX, deltaY);
			editor.lastMousePosition = currentPoint;
			editor.drawingPanel.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			editor.lastMousePosition = e.getPoint();
		}
		if (SwingUtilities.isLeftMouseButton(e)) {
			editor.isDrawing = true;
			editor.handleMouseClick(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			editor.isDrawing = false;
			editor.lastPoint = null;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

}
