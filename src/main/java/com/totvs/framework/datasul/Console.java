package com.totvs.framework.datasul;

import java.util.Scanner;

public class Console implements AutoCloseable {

	private Scanner scanner;
	private java.io.Console console;

	public Console() {
		this.scanner = new Scanner(System.in);
		this.console = System.console();
	}

	public String readLine() {
		if (console == null)
			return scanner.nextLine();
		else
			return console.readLine();
	}

	public String readPassword() {
		if (console == null)
			return scanner.nextLine();
		else
			return new String(console.readPassword());
	}

	@Override
	public void close() throws Exception {
		scanner.close();
	}
}