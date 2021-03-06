package fr.liglab.esprit.binarization.neuron;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.liglab.esprit.binarization.TernaryProbDistrib;

public class ConvBinarizationHalfCached extends AConvBinarization {
	final private List<TernaryProbDistrib[][]> originalOutput;
	final TernaryOutputNeuron originalNeuron;
	final List<byte[]> referenceInput;

	// force posneg always active
	public ConvBinarizationHalfCached(final TernaryOutputNeuron originalNeuron, final short convXSize,
			final short convYSize, final int inputXSize, final int inputYSize, final int nbChannels, final int padding,
			final byte inputMaxVal, final List<byte[]> input, List<byte[]> referenceInput) {
		super(originalNeuron, convXSize, convYSize, inputXSize, inputYSize, nbChannels, padding, inputMaxVal, input);
		if (referenceInput == null) {
			referenceInput = input;
		}
		this.referenceInput = referenceInput;
		this.originalNeuron = originalNeuron;
		this.originalOutput = new ArrayList<>(referenceInput.size());
		Iterator<byte[]> refDataIter = referenceInput.iterator();
		while (refDataIter.hasNext()) {
			final byte[] refData = refDataIter.next();
			final TernaryProbDistrib[][] outputMat = new TernaryProbDistrib[(this.inputXSize - this.convXSize + 1
					+ this.padding * 2)][(this.inputYSize - this.convYSize + 1 + this.padding * 2)];
			for (int y = 0; y < outputMat[0].length; y++) {
				for (int x = 0; x < outputMat.length; x++) {
					outputMat[x][y] = originalNeuron.getConvOutputProbs(refData, x - this.padding, y - this.padding,
							this.inputXSize, this.inputYSize, this.convXSize, this.convYSize, this.nbChannels);
				}
			}
			this.originalOutput.add(outputMat);
		}
	}

	@Override
	TernaryProbDistrib getOriginalOutput(int inputId, int x, int y) {
		TernaryProbDistrib originalOut = this.originalOutput.get(inputId)[x + this.padding][y + this.padding];
		return originalOut;
	}

}
