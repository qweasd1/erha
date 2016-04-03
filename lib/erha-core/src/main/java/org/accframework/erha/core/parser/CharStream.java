package org.accframework.erha.core.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharStream {

	private String text;
	private int p;
	private int totalLength;

	private Stack<Integer> txStack;
	private List<String> skipPatterns;

	public CharStream(String text, List<String> skipPatterns) {
		this.text = text;
		this.skipPatterns = skipPatterns;
		this.totalLength = text.length();

		this.p = 0;
		txStack = new Stack<Integer>();
		
		
		
	}
	
	public String nextString(String str) {
		return nextString(str,true);
	}
	
	public String nextString(String str, boolean isSkip) {
		int strLength = str.length();
		if (getMatchValue(strLength).equals(str)) {
			move(strLength, isSkip);
			return str;
		} else {
			return null;
		}
	}

	public String nextRegex(String regexStr) {
		return nextRegex(regexStr, true);
	}
	
	public String nextRegex(String regexStr, boolean isSkip) {
		Pattern regex = Pattern.compile("^" + regexStr);
		return nextRegex(regex, isSkip);
	}

	private String nextRegex(Pattern regex, boolean isSkip) {
		String rest = this.text.substring(this.p);
		Matcher matcher = regex.matcher(rest);
		if (matcher.find()) {
			String matchValue = matcher.group();
			move(matchValue.length(),isSkip);
			return matchValue;
		} else {
			return null;
		}
	}

	public String nextBlock(String start, String end, String escape, boolean isReserveBounder) {
		return nextBlock(start, end, escape, isReserveBounder, true);
	}
	
	public String nextBlock(String start, String end, String escape,
			boolean isReserveBounder, boolean isSkip) {
		
		start = Pattern.quote(start);
		end   = Pattern.quote(end);
		
		Pattern startPattern = Pattern.compile("^" + start);
		String rest = this.text.substring(this.p);
		Matcher matcher = startPattern.matcher(rest);

		if (!matcher.find()) {
			return null;
		}

		int leftBoundLength = matcher.group().length();
		int offset = leftBoundLength;
		if (checkEnd(offset)) {
			return null;
		}

		Pattern endPattern = Pattern.compile("^" + end);
		Pattern escapePattern = null;
		if (escape != null) {
			escapePattern = Pattern.compile("^" + escape);
		}

		while (true) {
			String rest_ = this.text.substring(this.p + offset);
			if (checkEnd(offset)) {
				return null;
			}

			if (escapePattern != null) {
				// match escape, if reach end then return no match, if success
				// then continue
				Matcher escapeMatcher = escapePattern.matcher(rest_);
				if (escapeMatcher.find()) {
					offset += escapeMatcher.group().length();
					if (checkEnd(offset)) {
						return null;
					} else {
						continue;
					}
				}
			}

			// no escape then match the content not the end

			Matcher endMatcher = endPattern.matcher(rest_);
			if (endMatcher.find()) {
				int rightBoundLength = endMatcher.group().length();
				offset += rightBoundLength;
				String matchValueWithBounder = getMatchValue(offset);
				String matchValue = isReserveBounder ? matchValueWithBounder : matchValueWithBounder.substring(leftBoundLength, offset - rightBoundLength);
				move(offset, isSkip);
				return matchValue;
			}

			offset += 1;
		}

		// Matcher matcher = endPattern.matcher(rest);

	}

	//endBound will be a raw string not a regex pattern
	public String nextUntil(String endBound, String excludePattern) {
		int offset = 0;
		int endBoundLength = endBound.length();
		
		while (!checkEnd(offset + endBoundLength)) {
			String matchValue = getMatchValue(offset);
			if (excludePattern != null && containsExclude(matchValue, excludePattern)) {
				return null;
			}
			
			if (getMatchValue(offset, endBoundLength).equals(endBound)) {
				
				move(offset, false);
				return matchValue;
			}
			offset+=1;
		}
		
		return null;
	}
	
	private boolean containsExclude(String text, String excludePattern) {
		return Pattern.compile(excludePattern).matcher(text).find();
	}
	
	
	
	public boolean checkEnd(int length) {
		return this.p + length > totalLength;
	}

	public boolean isEnd() {
		return this.p >= totalLength;
	}

	public void beginTx() {
		this.txStack.push(this.p);
	}

	public void commit() {
		if (txStack.isEmpty()) {
			throw new CharStreamTransactionException(
					"you want to commit but these is nothing in transaction stack");
		}
		txStack.pop();
	}

	public void rollback() {
		if (txStack.isEmpty()) {
			throw new CharStreamTransactionException(
					"you want to rollback but these is nothing in transaction stack");
		}
		this.p = txStack.pop();
	}

	private String getMatchValue(int length) {
		return this.text.substring(this.p, this.p + length);
	}
	
	private String getMatchValue(int startOffset, int length) {
		return this.text.substring(this.p + startOffset, this.p + startOffset + length);
	}

	private void move(int length, boolean isSkip) {
		this.p += length;
		if (isSkip) {
			skip();
		}
	}
	
	private void skip() {
		
		while (true) {
			for (String skipPattern : skipPatterns) {
				Pattern regex = Pattern.compile("^" +skipPattern);
				String rest = this.text.substring(this.p);
				Matcher matcher = regex.matcher(rest);
				if (matcher.find()) {
					String matchValue = matcher.group();
					this.p += matchValue.length();
					continue;
				}
			}
			break;
		}
	}
	
	
	
	public class CharStreamTransactionException extends RuntimeException {
		public CharStreamTransactionException(String message) {
			super(message);
		}
	}
	
	@Override
	public String toString() {
		return String.format("(p:%s, rest:%s, whole=%s)", this.p, this.text.substring(this.p), this.text);
	}
}