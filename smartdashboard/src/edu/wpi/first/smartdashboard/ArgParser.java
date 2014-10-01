package edu.wpi.first.smartdashboard;

import java.util.*;

public class ArgParser {
	private Map<String, String> argValues = new HashMap<String, String>();
	private Set<String> flags = new HashSet<String>();
	private final boolean ignoreLeadingDash;
	private final boolean ignoreCase;
	private String getProcessedName(String name){
		if(ignoreLeadingDash && name.startsWith("-"))
			name = name.substring(1);
		if(ignoreCase)
			name = name.toLowerCase();
		return name;
	}
	public ArgParser(String[] args, boolean ignoreLeadingDash, boolean ignoreCase, String[] valueArgs) {
		this.ignoreLeadingDash = ignoreLeadingDash;
		this.ignoreCase = ignoreCase;
		
		if(ignoreLeadingDash){
			for(int i = 0; i<valueArgs.length; ++i)
				valueArgs[i] = getProcessedName(valueArgs[i]);
		}

		argLoop: for (int i = 0; i < args.length; i++) {
			String arg = getProcessedName(args[i]);
			
			for (String possibleValueArg : valueArgs) {
				if (possibleValueArg.equals(arg)) {
					if (i < args.length - 1) {
						argValues.put(arg, args[i + 1]);
						++i;
					} else
						argValues.put(arg, "");
					continue argLoop;
				}
			}
			flags.add(arg);
		}
	}
	
	public boolean hasFlag(String name){
		return flags.contains(getProcessedName(name));
	}
	
	public boolean hasValue(String name){
		return argValues.get(getProcessedName(name))!=null;
	}
	public String getValue(String name){
		return argValues.get(getProcessedName(name));
	}
}
