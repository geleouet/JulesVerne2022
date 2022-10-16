package me.egaetan.julesverne;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BooksOrigine {

	public static void main(String[] args) {
		System.out.println(new BooksOrigine().computeMemo(10, 200));
		System.out.println(new BooksOrigine().computeIteratif(10, 200));
		System.out.println(new BooksOrigine().computeReduceSpaceIteratif(10, 200));
		System.out.println(new BooksOrigine().computeMatrix(10, 200));
	}

	public BigInteger computeMatrix(int n, int m) {
		return compteMatrix(n, m, successeurs(n));
	}
	public BigInteger computeMemo(int n, int m) {
		return compteMemo(0, m, successeurs(n));
	}
	public BigInteger computeIteratif(int n, int m) {
		return compteIteratif(n, m, successeurs(n));
	}
	public BigInteger computeReduceSpaceIteratif(int n, int m) {
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

	public BigInteger compteMatrix(int n, int m, List<List<Integer>> successeurs) {
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

	public BigInteger compteIteratif(int n, int m, List<List<Integer>> successeurs) {
		BigInteger[][] data = new BigInteger[m+1][pow(2,n)];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j < pow(2,n); j++) {
				data[i][j] = BigInteger.ZERO; 
			}
		}
		data[0][0]=BigInteger.ONE;
		for (int i = 1; i <= m; i++) {
			for (int j = 0; j < pow(2,n); j++) {
				for (int successeur : successeurs.get(j)) {
					data[i][successeur] = data[i][successeur].add(data[i-1][j]); 
				}
			}
		}
		return data[m][0];
	}
	public BigInteger compteReduceSpace(int n, int m, List<List<Integer>> successeurs) {
		BigInteger[] data = new BigInteger[pow(2,n)];
		for (int i = 0; i < pow(2, n); i++) {
			data[i]=BigInteger.ZERO;
		}
		data[0]=BigInteger.ONE;
		for (int i = 1; i <= m; i++) {
			BigInteger[] next = new BigInteger[pow(2,n)];
			Arrays.fill(next, BigInteger.ZERO);
			for (int j = 0; j < pow(2,n); j++) {
				for (int successeur : successeurs.get(j)) {
					next[successeur] = next[successeur].add(data[j]); 
				}
			}
			data = next;
		}
		return data[0];
	}
	
	static record Key(int i, int n) {}
	
	CacheBigInteger<Key> cache = new CacheBigInteger<>();
	public BigInteger compteMemo(int i, int n, List<List<Integer>> successeurs) {
		if (i==0 && n == 0) {
			return BigInteger.ONE;
		}
		else if (n == 0) {
			return BigInteger.ZERO;
		}
		else {
			if (cache.contains(new Key(i, n))) { return cache.get(new Key(i, n)); }
			BigInteger res = BigInteger.ZERO;
			for (int successeur : successeurs.get(i)) {
				res = res.add(compteMemo(successeur, n-1, successeurs));
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
		BigInteger[][] m;
		
		private Matrice(int n) {
			this.m = new BigInteger[n][n];
		}
		
		public BigInteger val(int i, int j) {
			return m[0][0];
		}

		public long at(int i, int j) {
			return m[i][j].longValue();
		}

		public Matrice pow(int m) {
			return pow(BigInteger.valueOf(m));
		}

		public Matrice pow(BigInteger n) {
			if (n.subtract(BigInteger.ONE).signum() == 0) {
				return this;
			}
			else if (n.mod(BigInteger.valueOf(2l)).signum() == 0) {
				return (this.mul(this)).pow(n.divide(BigInteger.valueOf(2)));
			}
			else {
				return this.mul((this.mul(this)).pow(n.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2))));
			}
		}
		public Matrice(int[][] m) {
			this.m = new BigInteger[m.length][m.length];
			for (int i = 0; i < m.length; i++) {
				for (int j = 0; j < m.length; j++) {
					this.m[i][j] = BigInteger.valueOf(m[i][j]);
				}
			}
		}
		
		public Matrice mul(Matrice other) {
			BigInteger[][] o = other.m;
			Matrice res = new Matrice(m.length);
			for (int i = 0; i < m.length; i++) {
				for (int j = 0; j < m.length; j++) {
					BigInteger r = BigInteger.ZERO;
					for (int x = 0; x < m.length; x++) {
						r = r.add(m[i][x].multiply(o[x][j]));
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
					sb.append(m[i][j].toString());
				}
				sb.append("\n");
			}
			return sb.toString();
		}
	}
}
