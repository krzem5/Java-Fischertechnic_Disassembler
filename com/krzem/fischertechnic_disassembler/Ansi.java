package com.krzem.fischertechnic_disassembler;



import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;



public class Ansi{
	public static final String RESET_ALL="\033[0m";

	public static final String BRIGHT="\033[1m";
	public static final String DIM="\033[2m";
	public static final String UNDERLINE="\033[4m";
	public static final String NORMAL="\033[22m";

	public static final String TITLE="\033]2;%s";

	public static final class CURSOR{
		public static final String UP="\033[%dA";
		public static final String DOWN="\033[%dB";
		public static final String RIGHT="\033[%dC";
		public static final String LEFT="\033[%dD";
		public static final String NEXT_LINE="\033[%dE";
		public static final String PREVIOUS_LINE="\033[%dF";
		public static final String COLUMN="\033[%dG";
		public static final String POSITION="\033[%d;%dH";
		public static final String ERASE_DISPLAY="\033[%dJ";
		public static final String ERASE_LINE="\033[%dK";
		public static final String SCROLL_UP="\033[%dS";
		public static final String SCROLL_DOWN="\033[%dT";
		public static final String HV_POSITION="\033[%d;%df";
		public static final String SGR="\033[%d %d";
		public static final String AUX_PORT_ON="\033[5i";
		public static final String AUX_PORT_OFF="\033[4i";
		public static final String REPORT_POSITION="\033[6n";
	}

	public static final class FOREGROUND{
		public static final String BLACK="\033[30m";
		public static final String RED="\033[31m";
		public static final String GREEN="\033[32m";
		public static final String YELLOW="\033[33m";
		public static final String BLUE="\033[34m";
		public static final String MAGENTA="\033[35m";
		public static final String CYAN="\033[36m";
		public static final String WHITE="\033[37m";
		public static final String RESET="\033[39m";

		public static final String LIGHT_BLACK="\033[90m";
		public static final String LIGHT_RED="\033[91m";
		public static final String LIGHT_GREEN="\033[92m";
		public static final String LIGHT_YELLOW="\033[93m";
		public static final String LIGHT_BLUE="\033[94m";
		public static final String LIGHT_MAGENTA="\033[95m";
		public static final String LIGHT_CYAN="\033[96m";
		public static final String LIGHT_WHITE="\033[97m";

		public static final String CUSTOM="\033[38;2;%d;%d;%dm";
	}

	public static final class BACKGROUND{
		public static final String BLACK="\033[40m";
		public static final String RED="\033[41m";
		public static final String GREEN="\033[42m";
		public static final String YELLOW="\033[43m";
		public static final String BLUE="\033[44m";
		public static final String MAGENTA="\033[45m";
		public static final String CYAN="\033[46m";
		public static final String WHITE="\033[47m";
		public static final String RESET="\033[49m";

		public static final String LIGHT_BLACK="\033[100m";
		public static final String LIGHT_RED="\033[101m";
		public static final String LIGHT_GREEN="\033[102m";
		public static final String LIGHT_YELLOW="\033[103m";
		public static final String LIGHT_BLUE="\033[104m";
		public static final String LIGHT_MAGENTA="\033[105m";
		public static final String LIGHT_CYAN="\033[106m";
		public static final String LIGHT_WHITE="\033[107m";

		public static final String CUSTOM="\033[48;2;%d;%d;%dm";
	}



	public static void setup(){
		Function _std_fh=Function.getFunction("kernel32","GetStdHandle");
		HANDLE ho=(HANDLE)_std_fh.invoke(HANDLE.class,new Object[]{new DWORD(-11)});
		DWORDByReference p_dw_m=new DWORDByReference(new DWORD(0));
		Function GetConsoleModeFunc=Function.getFunction("kernel32","GetConsoleMode");
		GetConsoleModeFunc.invoke(BOOL.class,new Object[]{ho,p_dw_m});
		DWORD dw_m=p_dw_m.getValue();
		dw_m.setValue(dw_m.intValue()|4);
		Function SetConsoleModeFunc=Function.getFunction("kernel32","SetConsoleMode");
		SetConsoleModeFunc.invoke(BOOL.class,new Object[]{ho,dw_m});
	}
}