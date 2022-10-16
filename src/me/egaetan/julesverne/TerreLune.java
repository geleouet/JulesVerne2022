package me.egaetan.julesverne;

import static java.lang.Math.min;

public class TerreLune {
	
	public static void main(String[] args) {
		int[][] grid = new int[][] {
			{100, 80, 60, 80},
			{25, 10, 20, 95},
			{90, 20, 30, 40},
			{80, 15, 80, 95}};
		System.out.println(new TerreLune().compute(new Grid(grid), new Position(0,0)));
		System.out.println(new TerreLune().computeMemo(new Grid(grid), new Position(0,0)));
		System.out.println(new TerreLune().computeTable(new Grid(grid), new Position(0,0)));
		System.out.println(new TerreLune().computeLigne(new Grid(grid), new Position(0,0)));
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
	
	int compute(Grid grid, Position p) {
		if (p.x < grid.limit) {
			if (p.y < grid.limit) {
				return grid.at(p) 
						+ min(compute(grid, new Position(p.x + 1, p.y)), 
								compute(grid, new Position(p.x, p.y + 1)));
			}
			else {
				return grid.at(p) + compute(grid, new Position(p.x + 1, p.y));
			}
		}
		else if (p.y < grid.limit) {
			return grid.at(p) + compute(grid, new Position(p.x, p.y + 1));
		}
		else {
			return grid.at(p);
		}
	}
	
	Cache<Position> cache = new Cache<>();
	int computeMemo(Grid grid, Position p) {
		if (cache.doesntContains(p)) { 
			if (p.x < grid.limit) {
				if (p.y < grid.limit) {
					cache.memo(p, grid.at(p) 
							+ min(computeMemo(grid, new Position(p.x + 1, p.y)), 
									computeMemo(grid, new Position(p.x, p.y + 1))));
				}
				else {
					cache.memo(p, grid.at(p) + computeMemo(grid, new Position(p.x + 1, p.y)));
				}
			}
			else if (p.y < grid.limit) {
				cache.memo(p, grid.at(p) + computeMemo(grid, new Position(p.x, p.y + 1)));
			}
			else {
				return grid.at(p);
			}
		}
		return cache.get(p);
	}
	
	int computeTable(Grid grid, Position p) {
		int[][] ligne = new int[grid.limit + 1][grid.limit + 1];
		for (int l = grid.limit; l >= 0; l--) {
			for (int c = grid.limit; c >= 0; c--) {
				if (l == grid.limit && c == grid.limit) {
					ligne[c][l] = grid.at(new Position(l, c));
				} else if (l == grid.limit) {
					ligne[c][l] = grid.at(new Position(l, c)) + ligne[c + 1][l];
				} else if (c == grid.limit) {
					ligne[c][l] = grid.at(new Position(l, c)) + ligne[c][l + 1];
				} else {
					ligne[c][l] = grid.at(new Position(l, c)) + min(ligne[c][l + 1], ligne[c + 1][l]);
				}
			}
		}
		return ligne[0][0];
	}

	
	int computeLigne(Grid grid, Position p) {
		int[] ligne = new int[grid.limit + 1];
		for (int l = grid.limit; l >= 0; l--) {
			for (int c = grid.limit; c >= 0; c--) {
				if (l == grid.limit && c == grid.limit) {
					ligne[c] = grid.at(new Position(l, c));
				} else if (l == grid.limit) {
					ligne[c] = grid.at(new Position(l, c)) + ligne[c + 1];
				} else if (c == grid.limit) {
					ligne[c] = grid.at(new Position(l, c)) + ligne[c];
				} else {
					ligne[c] = grid.at(new Position(l, c)) + min(ligne[c], ligne[c + 1]);
				}
			}
		}
		return ligne[0];
	}
	
}
