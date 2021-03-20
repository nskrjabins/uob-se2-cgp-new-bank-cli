package newbank.server;

public class Help{

	String printHelp =
			"\nHere are your available options:\n"+
			"\n"+
			"SHOWMYACCOUNTS\n"+
			"Returns a list of all the customers accounts along with their current balance\n"+
			"e.g. Main: 1000.0\n"+
			"\n"+
			"NEWACCOUNT <Name>\n"+
			"e.g. NEWACCOUNT Savings\n"+
			"Returns SUCCESS or FAIL\n"+
			"\n"+
			"MOVE <Amount> <From> <To>\n"+
			"e.g. MOVE 100 Main Savings\n"+
			"Returns SUCCESS or FAIL\n"+
			"\n"+
			"PAY <Person/Company> <Ammount>\n"+
			"e.g. PAY John 100\n"+
			"Returns SUCCESS or FAIL\n"+
			"\n"+
			"\n"+
			"To view these options again, please type 'HELP' anytime.\n";
}
