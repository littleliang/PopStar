package popstar.solve;

public class Node implements Comparable<Node>{
	private int x;
	private int y;
	public Node(int x, int y){
		this.x = x;
		this.y = y;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	@Override
	public String toString() {
		return "[x=" + x + "-y=" + y + "]";
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	public int compareTo(Node o) {
		if (this.x < o.getX()) {
			return -1;
		} else if (this.x > o.getX()) {
			return 1;
		} else {
			return 0;
		}
	}
}
