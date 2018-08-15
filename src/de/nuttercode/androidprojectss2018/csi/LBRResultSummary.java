package de.nuttercode.androidprojectss2018.csi;

import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * summarizes a {@link LBRResult} when obtained by a {@link LBRServer} - the
 * {@link #lbrResultState} is {@link LBRResultState#OK} if and only if no
 * exception was thrown and the {@link LBRResult} is not null
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRResultSummary {

	private final LBRResult lbrResult;
	private final LBRResultState lbrResultState;

	public LBRResultSummary(LBRResult lbrResult, LBRResultState lbrResultState) {
		this.lbrResult = lbrResult;
		this.lbrResultState = lbrResultState;
		if (lbrResultState == LBRResultState.OK && lbrResult == null)
			lbrResultState = LBRResultState.Null;
	}

	public LBRResult getLBRResult() {
		if (lbrResult == null)
			throw new NullPointerException();
		return lbrResult;
	}

	public LBRResultState getLbrResultState() {
		return lbrResultState;
	}

	@Override
	public String toString() {
		return "LBRResultSummary [lbrResult=" + lbrResult + ", lbrResultState=" + lbrResultState + "]";
	}

}
