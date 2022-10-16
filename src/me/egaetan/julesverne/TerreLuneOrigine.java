package me.egaetan.julesverne;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class TerreLuneOrigine {
	
	public static void main(String[] args) throws IOException {
		int[][] grid = loadImage("carte_asteroides.png");
		Node compute = new TerreLuneOrigine().computeTable(new Grid(grid), new Position(0,0));
		System.out.println(compute.value);
		drawPath(compute.path(), "path.png");		
	}

	static void drawPath(List<Position> path, String output) throws IOException, FileNotFoundException {
		BufferedImage resultat = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		for (Position p : path) {
			resultat.setRGB(p.x, p.y, new Color(52, 180, 235).getRGB());
		}
		ImageIO.write(resultat, "PNG", new FileOutputStream(output));
	}

	static int[][] loadImage(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] res = new int[height][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = new Color(image.getRGB(i, j)).getRed();
			}
		}
		return res;
	}
	
	public record Position (int x, int y) {}

	public static class Grid {
		final int limit;
		final private int[][] grid;
		Grid(int[][] grid) {
			this.limit = grid.length - 1;
			this.grid = grid;
		}
		public int at(Position p) {
			return grid[p.x][p.y];
		}
	}

	static class Node {
		Position p;
		Node previous;
		int value;
		
		public Node(Position p, Node previous, int val) {
			super();
			this.p = p;
			this.previous = previous;
			this.value = val;
		}

		public List<Position> path() {
			List<Position> path = new ArrayList<>();
			Node current = this;
			while (current != null) {
				path.add(current.p);
				current = current.previous;
			}
			Collections.reverse(path);
			return path;
		}

		public Node derive(Position position, int val) {
			return new Node(position, this, this.value+val);
		}
	}
	
	Node computeTable(Grid grid, Position p) {
		Node[] ligne = new Node[grid.limit + 1];
		for (int l = 0; l <= grid.limit; l++) {
			for (int c = 0; c <= grid.limit; c++) {
				Position position = new Position(l, c);
				if (l == 0) {
					if (c == 0) {
						ligne[c] = new Node(position, null, grid.at(position)); 
					}
					else {
						ligne[c] = ligne[c-1].derive(position, grid.at(position)); 
					}
				}
				else if (c == 0) {
					ligne[c] = ligne[0].derive(position, grid.at(position)); 
				}
				else {
					if (ligne[c].value <= ligne[c-1].value) {
						ligne[c] = ligne[c].derive(position, grid.at(position)); 
					}
					else {
						ligne[c] = ligne[c-1].derive(position, grid.at(position)); 
					}
				}
			}
		}
		return ligne[grid.limit];
	}
}
