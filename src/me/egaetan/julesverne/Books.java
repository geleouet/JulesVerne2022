package me.egaetan.julesverne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Books {

	public static void main(String[] args) {
		System.out.println(new Books().computeMemo(5, 6));
		System.out.println(new Books().computeIteratif(5, 6));
		System.out.println(new Books().computeReduceSpaceIteratif(5, 6));
		System.out.println(new Books().computeMatrix(5, 6));
	}

	public int computeMatrix(int n, int m) {
		return compteMatrix(n, m, successeurs(n));
	}
	public int computeMemo(int n, int m) {
		return compteMemo(0, m, successeurs(n));
	}
	public int computeIteratif(int n, int m) {
		return compteIteratif(n, m, successeurs(n));
	}
	public int computeReduceSpaceIteratif(int n, int m) {
		return compteReduceSpace(n, m, successeurs(n));
	}

	private List<List<Integer>> successeurs(int n) {
		List<List<Integer>> successeurs = new ArrayList<>();
		for (int i = 0; i < pow(2, n); i++) {
			List<Integer> sort = sort(combinaison(i, n));
			successeurs.add(sort);
		}
		return successeurs;
	}

	public int compteMatrix(int n, int m, List<List<Integer>> successeurs) {
		int[][] matrix = new int[pow(2, n)][pow(2, n)];
		for (int i = 0; i < successeurs.size(); i++) {
			var successeur = successeurs.get(i);
			for (int j : successeur) {
				matrix[i][j] = 1;
			}
		}		
		
		Matrice matrice = new Matrice(matrix);
		matrice = matrice.pow(m);
		return matrice.val(0, 0);
	}

	public long compte(int i, int n, List<List<Integer>> successeurs) {
		if (i==0 && n == 0) {
			return 1;
		}
		else if (n == 0) {
			return 0;
		}
		else {
			long res = 0;
			for (int successeur : successeurs.get(i)) {
				res += compte(successeur, n-1, successeurs);
			}
			return res;
		}
	}
	
	public int compteIteratif(int n, int m, List<List<Integer>> successeurs) {
		int[][] data = new int[m+1][pow(2,n)];
		data[0][0]=1;
		for (int i = 1; i <= m; i++) {
			for (int j = 0; j < pow(2,n); j++) {
				for (int successeur : successeurs.get(j)) {
					data[i][successeur] += data[i-1][j]; 
				}
			}
		}
		return data[m][0];
	}
	public int compteReduceSpace(int n, int m, List<List<Integer>> successeurs) {
		int[] data = new int[pow(2,n)];
		data[0]=1;
		for (int i = 1; i <= m; i++) {
			int[] next = new int[pow(2,n)];
			for (int j = 0; j < pow(2,n); j++) {
				for (int successeur : successeurs.get(j)) {
					next[successeur] += data[j]; 
				}
			}
			data = next;
		}
		return data[0];
	}
	
	static record Key(int i, int n) {}
	
	Cache<Key> cache = new Cache<>();
	public int compteMemo(int i, int n, List<List<Integer>> successeurs) {
		if (i==0 && n == 0) {
			return 1;
		}
		else if (n == 0) {
			return 0;
		}
		else {
			if (cache.contains(new Key(i, n))) { return cache.get(new Key(i, n)); }
			int res = 0;
			for (int successeur : successeurs.get(i)) {
				res += compteMemo(successeur, n-1, successeurs);
			}
			return cache.memo(new Key(i, n), res);
		}
	}

	public List<Integer> combinaison(int startedCombination, int size) {
		if (size == 0) {
			return List.of(0);
		} else if (startedCombination % pow(2, 1) == 1) { // case "0" occupée
			return transform(combinaison(startedCombination / 2, size - 1), 0 /* libre */);
		} else if (((startedCombination % pow(2, 2)) == 0) && size > 1) { // case "0" & "1" libre
			return union(transform(combinaison(1 + startedCombination / 2, size - 1), 0/* vertical => libre */),
					transform(combinaison(startedCombination / 2, size - 1), 1/* horizontal => occupé */));
		} else { // case "0" libre
			return transform(combinaison(startedCombination / 2, size - 1), 1/* horizontal => occupé */);
		}
	}

	private List<Integer> transform(List<Integer> nexts, int nextColonne) {
		List<Integer> res = new ArrayList<>();
		for (var j : nexts) {
			res.add(2 * j + nextColonne);
		}
		return res;
	}

	private List<Integer> union(List<Integer> a, List<Integer> b) {
		List<Integer> res = new ArrayList<>();
		res.addAll(a);
		res.addAll(b);
		return res;
	}

	private static int pow(int a, int n) {
		return (int) Math.round(Math.pow(a, n));
	}
	
	public static List<Integer> sort(List<Integer> a) {
		Collections.sort(a);
		return a;
	}
	
	static class Matrice {
		int[][] m;
		
		private Matrice(int n) {
			this.m = new int[n][n];
		}
		
		public int val(int i, int j) {
			return m[0][0];
		}

		public Matrice pow(int n) {
			if (n == 1) {
				return this;
			} else if (n % 2 == 0) {
				return (this.mul(this)).pow(n / 2);
			} else {
				return this.mul((this.mul(this)).pow((n - 1) / 2));
			}
		}
		public Matrice(int[][] m) {
			this.m = m;
		}
		
		public Matrice mul(Matrice other) {
			int[][] o = other.m;
			Matrice res = new Matrice(m.length);
			for (int i = 0; i < m.length; i++) {
				for (int j = 0; j < m.length; j++) {
					int r = 0;
					for (int x = 0; x < m.length; x++) {
						r += m[i][x] * o[x][j];
					}
					res.m[i][j] = r;
				}
			}
			return res;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < m.length; i++) {
				for (int j = 0; j < m.length; j++) {
					if (j > 0) sb.append(", ");
					sb.append(m[i][j]+ "");
				}
				sb.append("\n");
			}
			return sb.toString();
		}
	}
}
