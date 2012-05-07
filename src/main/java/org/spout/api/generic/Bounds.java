package org.spout.api.generic;

public class Bounds<T extends Number> {
	T left, right, top, bottom;
	
	/**
	 * Inits the property with left, right, top and bottom set to 0
	 * Has to be reimplemented by subclasses
	 */
	public Bounds() {
	}

	public Bounds(T left, T right, T top, T bottom) {
		super();
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	public T getLeft() {
		return left;
	}

	public void setLeft(T left) {
		this.left = left;
	}

	public T getRight() {
		return right;
	}

	public void setRight(T right) {
		this.right = right;
	}

	public T getTop() {
		return top;
	}

	public void setTop(T top) {
		this.top = top;
	}

	public T getBottom() {
		return bottom;
	}

	public void setBottom(T bottom) {
		this.bottom = bottom;
	}

	@Override
	public String toString() {
		return "Bounds [left=" + left + ", right=" + right + ", top=" + top
				+ ", bottom=" + bottom + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bottom == null) ? 0 : bottom.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((top == null) ? 0 : top.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bounds other = (Bounds) obj;
		if (bottom == null) {
			if (other.bottom != null)
				return false;
		} else if (!bottom.equals(other.bottom))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (top == null) {
			if (other.top != null)
				return false;
		} else if (!top.equals(other.top))
			return false;
		return true;
	}
	
	
}
