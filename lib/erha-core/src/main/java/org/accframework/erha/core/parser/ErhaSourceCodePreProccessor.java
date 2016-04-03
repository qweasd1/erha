package org.accframework.erha.core.parser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ErhaSourceCodePreProccessor{
	
	private String headAppendText;
	
	public ErhaSourceCodePreProccessor(){
		
	}
	
	public String preProcess(String origin) {
		String temp = origin;
		temp = appendHead(temp);
		temp = removeEmptyLines(temp);
		temp = appendTailNewLine(temp);
		return temp;
	}

	public String getHeadAppendText() {
		return headAppendText;
	}

	public void setHeadAppendText(String headAppendText) {
		this.headAppendText = headAppendText;
	}

	
	
	
	private String appendHead(String origin) {
		if (headAppendText != null) {
			return headAppendText + origin;
		}
		else {
			return origin;
		}
	}
	
	
	private String removeEmptyLines(String origin) {
		return Pattern.compile("\\n([ \\t]*(\\r)?\n)+|\\n[ \\t]+$").matcher(origin).replaceAll("\n");
	}
	
	private String appendTailNewLine(String origin) {
		if (origin.endsWith("\n")) {
			return origin;
		}
		else {
			return origin + "\n";
		}
	}






	
}
