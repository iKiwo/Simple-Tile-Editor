package com.editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.editor.input.Mouse;

public class MapEditor extends JFrame {
	
	private Mouse mouse;
	private JFileChooser fileChooser;
	public JPanel drawingPanel;
	private JPanel palettePanel;
	private HashMap<Integer, ImageIcon> imageMap = new HashMap<>();
	private int[][] map = new int[10][10];
	private int selectedValue;
	public Point cameraOffset = new Point(0, 0);
	private boolean gridVisible = true;

	public boolean isDrawing = false;
	public Point lastPoint;
	public Point lastMousePosition;

	public MapEditor() {
		super("Simple Tile Editor");
		setSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		mouse = new Mouse(this);
		fileChooser = new JFileChooser();
		addToolbar();
		render();
		
		drawingPanel.addMouseListener(mouse);
		drawingPanel.addMouseMotionListener(mouse);
		add(drawingPanel);
		addPalettePanel();
	}
	
	private void addPalettePanel() {
		palettePanel = new JPanel();
		add(new JScrollPane(palettePanel), BorderLayout.CENTER);
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Tiles", palettePanel);

		JSplitPane split = new JSplitPane();
		split.setDividerLocation(250);
		split.setLeftComponent(tabPane);
		split.setRightComponent(drawingPanel);
		add(split, BorderLayout.CENTER);
	}
	
	private void addToolbar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		// Menu bar
		JMenuBar menuBar = new JMenuBar();
		toolBar.add(menuBar);
		
		menuBar.add(addFileMenu());
		menuBar.add(addViewMenu());
		menuBar.add(addMapSizeMenu());

		add(toolBar, BorderLayout.NORTH);
	}
	
	
	private JMenu addFileMenu() {
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem importItem = new JMenuItem("Import Tile");
		importItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showOpenDialog(MapEditor.this);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					String filePath = file.getPath();
					String valueStr = JOptionPane.showInputDialog("Enter a value for this image:");
					int value = Integer.parseInt(valueStr);
					ImageIcon icon = new ImageIcon(filePath);
					icon.setDescription(filePath);
					imageMap.put(value, icon);

					JButton imageButton = new JButton(icon);
					imageButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							selectedValue = value;
						}
					});
					palettePanel.add(imageButton);
					palettePanel.revalidate();
					palettePanel.repaint();
				}
			}
		});
		fileMenu.add(importItem);
		
		JMenuItem saveMap = new JMenuItem("Save Map");
		saveMap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showSaveDialog(MapEditor.this) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						FileManager.saveMap(map, file.getPath());
					} catch (IOException ee) {
						ee.printStackTrace();
					}
				}
			}
		});
		
		fileMenu.add(saveMap);
		
		JMenuItem loadMap = new JMenuItem("Load Map");
		loadMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(MapEditor.this) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						map = FileManager.loadMap(file.getPath());
						drawingPanel.setPreferredSize(new Dimension(map.length * Constants.TILE_SIZE, map[0].length * Constants.TILE_SIZE));
						drawingPanel.revalidate();
						drawingPanel.repaint();
					} catch (IOException ee) {
						ee.printStackTrace();
					}
				}
			}
		});
		
		fileMenu.add(loadMap);
		
		JMenuItem savePaletteItem = new JMenuItem("Save Palette");
		savePaletteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showSaveDialog(MapEditor.this) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						FileManager.savePalette(imageMap, file.getPath());
					} catch (IOException ee) {
						ee.printStackTrace();
					}
				}
			}
		});
		
		fileMenu.add(savePaletteItem);
		
		JMenuItem loadPaletteItem = new JMenuItem("Load Palette");
		loadPaletteItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(MapEditor.this) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						imageMap = FileManager.loadPalette(file.getPath());
						palettePanel.removeAll();
						for (Integer key : imageMap.keySet()) {
							ImageIcon icon = imageMap.get(key);
							JButton imageButton = new JButton(icon);
							imageButton.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									 selectedValue = key;
								}
							});
							palettePanel.add(imageButton);
						}
						palettePanel.revalidate();
						palettePanel.repaint();
					} catch (IOException ee) {
						ee.printStackTrace();
					}
				}
			}
		});
		
		fileMenu.add(loadPaletteItem);
		
		return fileMenu;
	}
	
	
	private JMenu addViewMenu() {
		JMenu viewMenu = new JMenu("View");
		
		JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Show Grid", gridVisible);
		gridItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				gridVisible = !gridVisible;
				drawingPanel.repaint();
			}
		});
		viewMenu.add(gridItem);
		
		return viewMenu;
	}
	
	
	private JMenu addMapSizeMenu() {
		JMenu mapSizeMenu = new JMenu("Map");

		JMenuItem mapSizeItem = new JMenuItem("Map Size");
		mapSizeItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField widthField = new JTextField(5);
				JTextField heightField = new JTextField(5);

				JPanel myPanel = new JPanel();
				myPanel.setLayout(new GridLayout(2, 2));
				myPanel.add(new JLabel("Width:"));
				myPanel.add(widthField);
				myPanel.add(new JLabel("Height:"));
				myPanel.add(heightField);

				int result = JOptionPane.showConfirmDialog(null, myPanel, "Enter new dimensions", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					try {
						int rows = Integer.parseInt(heightField.getText());
						int cols = Integer.parseInt(widthField.getText());

						int[][] newMap = new int[rows][cols];
						for (int i = 0; i < Math.min(rows, map.length); i++) {
							for (int j = 0; j < Math.min(cols, map[i].length); j++) {
								newMap[i][j] = map[i][j];
							}
						}
						map = newMap;
						drawingPanel.setPreferredSize(new Dimension(cols * Constants.TILE_SIZE, rows * Constants.TILE_SIZE));
						drawingPanel.revalidate();
						drawingPanel.repaint();
					} catch (NumberFormatException ee) {
						JOptionPane.showMessageDialog(null, "Please enter valid numbers for width and height.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		mapSizeMenu.add(mapSizeItem);

		return mapSizeMenu;
	}

	
	public void handleMouseClick(MouseEvent e) {
		lastPoint = e.getPoint();
		drawPoint(e);
	}

	public void handleMouseDrag(MouseEvent e) {
		if (lastPoint != null) {
			drawLine(lastPoint, e.getPoint());
			lastPoint = e.getPoint();
		}
	}
	
	private void drawPoint(MouseEvent e) {
	    int x = ((e.getX() - cameraOffset.x) / Constants.TILE_SIZE);
	    int y = ((e.getY() - cameraOffset.y) / Constants.TILE_SIZE);
	    if (x >= 0 && x < map.length && y >= 0 && y < map[0].length) {
	        map[x][y] = selectedValue;
	        drawingPanel.repaint();
	    }
	}

	private void drawLine(Point start, Point end) {
		Graphics g = drawingPanel.getGraphics();
		g.setColor(getForeground());
		drawPoint(new MouseEvent(drawingPanel, MouseEvent.MOUSE_CLICKED, System.nanoTime(), 0, end.x, end.y, 1, false));
	}

	private void render() {
		drawingPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				drawMap(g);
				
				if(gridVisible) {
					drawGrid(g2);
				}
			}
		};
	}
	
	private void drawMap(Graphics g) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				int value = map[i][j];
				ImageIcon icon = imageMap.get(value);
				if (icon != null) {
					g.drawImage(icon.getImage(), i * Constants.TILE_SIZE + cameraOffset.x, j * Constants.TILE_SIZE + cameraOffset.y, Constants.TILE_SIZE, Constants.TILE_SIZE, this);
				}
			}
		}
	}
	
    private void drawGrid(Graphics2D g2) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i <= map.length; i++) {
            g2.drawLine(i * Constants.TILE_SIZE + cameraOffset.x, cameraOffset.y, i * Constants.TILE_SIZE + cameraOffset.x, map[0].length * Constants.TILE_SIZE + cameraOffset.y);
        }
        for (int i = 0; i <= map[0].length; i++) {
            g2.drawLine(cameraOffset.x, i * Constants.TILE_SIZE + cameraOffset.y, map.length * Constants.TILE_SIZE + cameraOffset.x, i * Constants.TILE_SIZE + cameraOffset.y);
        }
    }
}
