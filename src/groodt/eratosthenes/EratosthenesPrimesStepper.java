package groodt.eratosthenes;

public class EratosthenesPrimesStepper {

	private final int upperBound;
	private int upperBoundSquareRoot;
	private boolean[] isComposite;
	private int index = 2;
	private int multiple = 0;
	private boolean removingMultiples;
	private boolean inProgress;
	private int op;

	// This is some hideous stuff. It was surprisingly hard to unwind the
	// algorithm. There must be a nicer state machine type way to do it.
	public EratosthenesPrimesStepper(final int n) {
		this.upperBound = n;
		this.upperBoundSquareRoot = (int) Math.sqrt(n);
		this.isComposite = new boolean[n + 1];
		// horrible. fix this. op: 0 is removing multiples. op: 1 is found
		// prime. op: -1 is stopped.
		this.op = -1;
	}

	public int getIndex() {
		return index;
	}

	public int getMultiple() {
		return multiple;
	}

	public int getOp() {
		return op;
	}

	public void step() {
		if (!inProgress) {
			// choose first prime
			op = 1;
			index = 2;
			inProgress = true;
			// System.out.println("first prime: " + index);
			// System.out.println("upperBoundSquareRoot: " +
			// upperBoundSquareRoot);
		} else if (inProgress && canRemoveMultiples() && removingMultiples) {
			multiple += index;
			// System.out.println("next multiple: " + multiple);
			op = 0;
			isComposite[multiple] = true;
		} else if (inProgress && canRemoveMultiples() && !removingMultiples) {
			removingMultiples = true;
			multiple = index * index;
			// System.out.println("first multiple: " + multiple);
			op = 0;
			isComposite[multiple] = true;
		} else if (inProgress && primesLeft()) {
			index = nextCandidatePrime();
			removingMultiples = false;
			op = 1;
			// System.out.println("next prime: " + index);
		} else if (inProgress && !primesLeft() && (leftOverPrime() > -1)) {
			index = leftOverPrime();
			op = 1;
			// System.out.println("leftover prime: " + index);
		} else {
			// System.out.println("restart");
			inProgress = false;
			op = -1;
		}
	}

	private int leftOverPrime() {
		int nextPrime = -1;
		for (int m = index + 1; m <= upperBound; m++) {
			if (!isComposite[m]) {
				return m;
			}
		}
		return nextPrime;
	}

	private boolean primesLeft() {
		return index + 1 <= upperBoundSquareRoot;
	}

	private int nextCandidatePrime() {
		int nextPrime = -1;
		for (int m = index + 1; m <= upperBound; m++) {
			if (!isComposite[m]) {
				return m;
			}
		}
		return nextPrime;
	}

	private boolean canRemoveMultiples() {
		int nextMultiple = removingMultiples ? multiple + index : index * index;
		return nextMultiple <= upperBound;
	}

//	public static void main(String[] args) {
//		EratosthenesPrimesStepper p = new EratosthenesPrimesStepper(120);
//		for (int i = 1; i < 1000; i++) {
//			p.step();
//		}
//	}
}
