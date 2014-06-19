package org.sudos.wiki.engine;

public class ApplicationRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SearchEngine engine = new WikiSearchEngine();
		if (args.length < 2) {
			System.out.println("Tip: enter index <filename> or search <query>");
			return;
		}

		if (args[0].equals("index")) {
			engine.index(args[1]);
		} else if (args[0].equals("search")) {
			System.out.println(engine.search(args[1]));
		} else {
			System.out.println("Tip: enter index <filename> or search <query>");
			return;
		}
	}

}
