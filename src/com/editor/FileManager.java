package com.editor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class FileManager {
	public static void savePalette(HashMap<Integer, ImageIcon> imageMap, String filePath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			for (Integer key : imageMap.keySet()) {
				ImageIcon icon = imageMap.get(key);
				String path = icon.getDescription();
				writer.write(key + ", " + path);
				writer.newLine();
			}
		}
	}

	public static HashMap<Integer, ImageIcon> loadPalette(String filePath) throws IOException {
		HashMap<Integer, ImageIcon> imageMap = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(", ");
				int key = Integer.parseInt(parts[0]);
				String path = parts[1];
				ImageIcon icon = new ImageIcon(path);
				icon.setDescription(path);
				imageMap.put(key, icon);
			}
		}
		return imageMap;
	}

	public static void saveMap(int[][] map, String filePath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			for (int j = 0; j < map[0].length; j++) {
				for (int i = 0; i < map.length; i++) {
					writer.write(map[i][j] + " ");
				}
				writer.newLine();
			}
		}
	}

	public static int[][] loadMap(String filePath) throws IOException {
		int rows = 0;
		int cols = 0;

		// First pass to determine the size of the map
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				rows++;
				String[] values = line.split(" ");
				cols = Math.max(cols, values.length);
			}
		}

		int[][] map = new int[rows][cols];

		// Second pass to populate the map
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			int row = 0;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(" ");
				for (int col = 0; col < values.length; col++) {
					map[row][col] = Integer.parseInt(values[col]);
				}
				row++;
			}
		}
		return map;
	}
}
