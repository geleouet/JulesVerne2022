package me.egaetan.julesverne;

import java.math.BigInteger;

public class Nautilus {

	public static void main(String[] args) {
		System.out.println(new Nautilus().compute(10));
		System.out.println(new Nautilus().computeMemo(10));
		System.out.println(new Nautilus().computeMatrix(8));

		System.out.println(new Nautilus().computeBig(20_000));
		System.out.println(new Nautilus().computeMatrix(20_000));
	}

	Cache<Integer> cache = new Cache<>();

	public int computeMemo(int n) {
		if (cache.doesntContains(n)) {
			if (n < 0) {
				cache.memo(n, 0);
			} else if (n == 0) {
				cache.memo(n, 1);
			} else {
				cache.memo(n, computeMemo(n - 1) + computeMemo(n - 2) + computeMemo(n - 3));
			}
		}
		return cache.get(n);
	}

	public int compute(int n) {
		int n_1 = 1; int n_2 = 0; int n_3 = 0;
		for (int i = 0; i < n; i++) {
			int t = n_1 + n_2 + n_3;
			n_3 = n_2;
			n_2 = n_1;
			n_1 = t;
		}
		return n_1;
	}

	public BigInteger computeBig(int n) {
		var n_1 = BigInteger.ONE;
		var n_2 = BigInteger.ZERO;
		var n_3 = BigInteger.ZERO;
		for (int i = 0; i < n; i++) {
			var t = n_1.add(n_2).add(n_3);
			n_3 = n_2;
			n_2 = n_1;
			n_1 = t;
		}
		return n_1;
	}

	public BigInteger computeMatrix(int n) {
		return computeMatrix(BigInteger.valueOf(n));
	}

	public BigInteger computeMatrix(BigInteger n) {
		Matrice reference = new Matrice(new int[][] 
				{ { 0, 0, 1 },
			      { 1, 0, 1 }, 
			      { 0, 1, 1 } });
		Matrice power = reference.pow(n);
		return power.m[2][2];
	}

	static class Matrice {
		BigInteger[][] m;

		private Matrice(int n) {
			this.m = new BigInteger[n][n];
		}

		public Matrice pow(BigInteger n) {
			if (n.subtract(BigInteger.ONE).signum() == 0) {
				return this;
			} else if (n.mod(BigInteger.valueOf(2)).signum() == 0) {
				return (this.mul(this)).pow(n.divide(BigInteger.valueOf(2)));
			} else {
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
					if (j > 0)
						sb.append(", ");
					sb.append(m[i][j].toString());
				}
			}
			return sb.toString();
		}
	}

}
