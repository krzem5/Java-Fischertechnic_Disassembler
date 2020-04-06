package com.krzem.fischertechnic_disassembler;



public class Main{
	public static void main(String[] args){
		new Main(args);
	}



	public Main(String[] args){
		FischertechnicDisassembler.FischertechnicFile f=FischertechnicDisassembler.load(args[0]);
		f.draw();
	}
}