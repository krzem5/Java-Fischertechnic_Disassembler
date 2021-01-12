package com.krzem.fischertechnic_disassembler;



import java.lang.ClassNotFoundException;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class FischertechnicDisassembler{
	private static final String DOUBLE_V_LINE_CHAR="║";
	private static final String DOUBLE_H_LINE_CHAR="═";
	private static final String DOUBLE_DL_CORNER_CHAR="╔";
	private static final String DOUBLE_DR_CORNER_CHAR="╗";
	private static final String DOUBLE_UL_CORNER_CHAR="╚";
	private static final String DOUBLE_UR_CORNER_CHAR="╝";
	// private static final String DOUBLE_V_LINE_RIGHT_LINE_CHAR="╟";
	// private static final String DOUBLE_V_LINE_LEFT_LINE_CHAR="╢";
	// private static final String DOUBLE_V_LINE_H_LINE_CHAR="╫";
	private static final String SINGLE_H_LINE_CHAR="─";
	// private static final String SINGLE_DOUBLE_LU_CORNER_CHAR="╜";
	// private static final String SINGLE_DOUBLE_LD_CORNER_CHAR="╖";
	// private static final String DOUBLE_U_LINE_SINGLE_H_LINE_CHAR="╨";
	// private static final String DOUBLE_D_LINE_SINGLE_H_LINE_CHAR="╥";
	private static final String DOUBLE_V_LINE_LEFT_DOUBLE_LINE_CHAR="╣";
	private static final String DOUBLE_V_LINE_RIGHT_DOUBLE_LINE_CHAR="╠";



	public static final class FischertechnicFile{
		public String name;
		public List<FischertechnicDisassembler.FischertechnicProgram> progs;



		public FischertechnicFile(String name){
			this.name=name;
			this.progs=new ArrayList<FischertechnicDisassembler.FischertechnicProgram>();
		}



		public void draw(){
			for (FischertechnicDisassembler.FischertechnicProgram p:this.progs){
				System.out.println(p.draw());
			}
		}



		@Override
		public String toString(){
			return String.format("File(name='%s', scripts=%s)",this.name,this.progs.toString());
		}
	}



	public static final class FischertechnicProgram{
		public String name;
		public List<List<FischertechnicDisassembler.FischertechnicBlock>> tl;
		private List<FischertechnicDisassembler.FischertechnicBlock> _bl;
		private List<FischertechnicDisassembler.FischertechnicWire> _wl;



		public FischertechnicProgram(String name){
			this.name=name;
			this._bl=new ArrayList<FischertechnicDisassembler.FischertechnicBlock>();
			this._wl=new ArrayList<FischertechnicDisassembler.FischertechnicWire>();
		}



		public void add(FischertechnicDisassembler.FischertechnicBlock b){
			b.p=this;
			this._bl.add(b);
		}



		public void add(FischertechnicDisassembler.FischertechnicWire w){
			this._wl.add(w);
		}



		public void resolve(){
			for (int i=this._wl.size()-1;i>=0;i--){
				this._wl.get(i).resolve(this._wl);
			}
			for (FischertechnicDisassembler.FischertechnicBlock b:this._bl){
				for (String t:new String[]{"flow_out","data_out"}){
					for (FischertechnicDisassembler.FischertechnicPin p:b.pins.get(t)){
						if (p.p_id!=-1){
							FischertechnicDisassembler.FischertechnicPin e=null;
							for (FischertechnicDisassembler.FischertechnicWire w:this._wl){
								if (w.type.equals(t.replace("_out",""))==true){
									if (w.end==p.p_id){
										if (w.start.size()>0){
											p.w=w;
											p.e.add(FischertechnicDisassembler.FischertechnicPin.get(this.name,t.replace("_out",""),w.start.get(0)));
											p.e.get(p.e.size()-1).e.add(p);
										}
										break;
									}
									if (w.start.contains(p.p_id)){
										p.w=w;
										p.e.add(FischertechnicDisassembler.FischertechnicPin.get(this.name,t.replace("_out",""),w.end));
										p.e.get(p.e.size()-1).e.add(p);
										break;
									}
								}
							}
						}
					}
				}
			}
			this.tl=new ArrayList<List<FischertechnicDisassembler.FischertechnicBlock>>();
			for (FischertechnicDisassembler.FischertechnicBlock b:this._bl){
				if (b.t==null){
					this.tl.add(new ArrayList<FischertechnicDisassembler.FischertechnicBlock>());
					this._tree(b,this.tl.size()-1);
				}
			}
		}



		public String draw(){
			Ansi.setup();
			String o="";
			int mw=Math.max(Math.max(Math.max(this.name.length()+4,Integer.toString(this._wl.size()).length()+12),Integer.toString(this._bl.size()).length()+11),Integer.toString(this.tl.size()).length()+11);
			mw=40;//////////////////////
			o=String.format("%s\n%s\n%s\n%sCode Trees%s: %s%d\n%sCode Blocks%s: %s%d\n%sCode Wires%s: %s%d\n"
				,Ansi.BRIGHT+Ansi.FOREGROUND.BLACK+this._center(FischertechnicDisassembler.DOUBLE_DL_CORNER_CHAR+this._fill(this.name.length()+2,FischertechnicDisassembler.DOUBLE_H_LINE_CHAR)+FischertechnicDisassembler.DOUBLE_DR_CORNER_CHAR,mw," "),
				this._center(FischertechnicDisassembler.DOUBLE_V_LINE_LEFT_DOUBLE_LINE_CHAR+" "+Ansi.FOREGROUND.CYAN+this.name+Ansi.FOREGROUND.BLACK+" "+FischertechnicDisassembler.DOUBLE_V_LINE_RIGHT_DOUBLE_LINE_CHAR,mw,FischertechnicDisassembler.DOUBLE_H_LINE_CHAR),
				this._center(FischertechnicDisassembler.DOUBLE_UL_CORNER_CHAR+this._fill(this.name.length()+2,FischertechnicDisassembler.DOUBLE_H_LINE_CHAR)+FischertechnicDisassembler.DOUBLE_UR_CORNER_CHAR,mw," "),
				Ansi.NORMAL+String.format(Ansi.FOREGROUND.CUSTOM,59,120,255),
				Ansi.BRIGHT+Ansi.FOREGROUND.BLACK,
				Ansi.NORMAL+String.format(Ansi.FOREGROUND.CUSTOM,255,127,0),
				this.tl.size(),
				Ansi.NORMAL+String.format(Ansi.FOREGROUND.CUSTOM,59,120,255),
				Ansi.BRIGHT+Ansi.FOREGROUND.BLACK,
				Ansi.NORMAL+String.format(Ansi.FOREGROUND.CUSTOM,255,127,0),
				this._bl.size(),
				Ansi.NORMAL+String.format(Ansi.FOREGROUND.CUSTOM,59,120,255),
				Ansi.BRIGHT+Ansi.FOREGROUND.BLACK,
				Ansi.NORMAL+String.format(Ansi.FOREGROUND.CUSTOM,255,127,0),
				this._wl.size())+o;
			return o+Ansi.RESET_ALL;
		}



		@Override
		public String toString(){
			return String.format("Program(name='%s', code_trees=%s)",this.name,this.tl.toString());
		}



		private void _tree(FischertechnicDisassembler.FischertechnicBlock b,int t_id){
			b.t=this.tl.get(t_id);
			this.tl.get(t_id).add(b);
			for (Map.Entry<String,List<FischertechnicDisassembler.FischertechnicPin>> e:b.pins.entrySet()){
				for (FischertechnicDisassembler.FischertechnicPin p:e.getValue()){
					for (FischertechnicDisassembler.FischertechnicPin pe:p.e){
						if (pe.pr.t==null){
							this._tree(pe.pr,t_id);
						}
					}
				}
			}
		}



		private String _fill(int l,String chr){
			String o="";
			for (int i=0;i<l;i++){
				o+=chr;
			}
			return o;
		}


		private String _center(String s,int l,String c){
			int a=(l-this._no_escape(s).length())/2;
			if (a==0){
				return String.format((l-s.length()==1?" ":"")+"%-"+(l-(l-s.length()==1?1:0))+"s","\0").replace(" ",c).replace("\0",s);
			}
			return String.format("%"+a+"s%-"+(l-a-this._no_escape(s).length()+1)+"s","","\0").replace(" ",c).replace("\0",s);
		}



		private String _no_escape(String s){
			return s.replaceAll("\033\\[[^m]+m","");
		}
	}



	public static abstract class FischertechnicBlock{
		public static final class Default extends FischertechnicDisassembler.FischertechnicBlock{
			private String nm;



			public Default(String nm){
				this.nm=nm;
			}



			@Override
			public String name(){
				return "{DEFAULT} "+this.nm+" {DEFAULT}";
			}



			@Override
			public void load(Element e){
				this.load_pins(e);
			}



			@Override
			public String toString(){
				return this.name();
			}
		}



		public static final class Process{
			public static final class Start extends FischertechnicDisassembler.FischertechnicBlock{
				@Override
				public String name(){
					return "Process > Start";
				}



				@Override
				public void load(Element e){
					this.load_pins(e);
				}



				@Override
				public String toString(){
					return String.format("ProcessStartCodeBlock(pins=%s)",this.pins.toString());
				}
			}



			public static final class Stop extends FischertechnicDisassembler.FischertechnicBlock{
				@Override
				public String name(){
					return "Process > Stop";
				}



				@Override
				public void load(Element e){
					this.load_pins(e);
				}



				@Override
				public String toString(){
					return String.format("ProcessEndCodeBlock(pins=%s)",this.pins.toString());
				}
			}
		}



		public static final class Subroutine{
			public static final class Data{
				public static final class In extends FischertechnicDisassembler.FischertechnicBlock{
					public String name;



					@Override
					public String name(){
						return "Subroutine > Data Input";
					}



					@Override
					public void load(Element e){
						this.load_pins(e);
						this.name=e.getAttribute("name");
					}



					@Override
					public String toString(){
						return String.format("SubroutineDataInputCodeBlock(name='%s', pins=%s)",this.name,this.pins.toString());
					}
				}



				public static final class Out extends FischertechnicDisassembler.FischertechnicBlock{
					public String name;



					@Override
					public String name(){
						return "Subroutine > Data Output";
					}



					@Override
					public void load(Element e){
						this.load_pins(e);
						this.name=e.getAttribute("name");
					}



					@Override
					public String toString(){
						return String.format("SubroutineDataOutputCodeBlock(name='%s', pins=%s)",this.name,this.pins.toString());
					}
				}
			}
		}



		public FischertechnicDisassembler.FischertechnicProgram p;
		public Map<String,List<FischertechnicDisassembler.FischertechnicPin>> pins;
		public List<FischertechnicDisassembler.FischertechnicBlock> t=null;



		public abstract String name();



		public abstract void load(Element e);



		public void load_pins(Element e){
			this.pins=new HashMap<String,List<FischertechnicDisassembler.FischertechnicPin>>(){{
				this.put("flow_in",new ArrayList<FischertechnicDisassembler.FischertechnicPin>());
				this.put("flow_out",new ArrayList<FischertechnicDisassembler.FischertechnicPin>());
				this.put("data_in",new ArrayList<FischertechnicDisassembler.FischertechnicPin>());
				this.put("data_out",new ArrayList<FischertechnicDisassembler.FischertechnicPin>());
			}};
			for (Element pe:FischertechnicDisassembler._xml_child(e,"o","classname","ftProObjectPin")){
				FischertechnicDisassembler.FischertechnicPin p=new FischertechnicDisassembler.FischertechnicPin(this.p,this,pe.getAttribute("name"),pe.getAttribute("pinclass").replace("object","").replace("wire","").replace("input","_in").replace("output","_out"),Integer.parseInt(pe.getAttribute("id")),(pe.getAttribute("resolveid")!=null&&pe.getAttribute("resolveid").length()!=0?Integer.parseInt(pe.getAttribute("resolveid")):-1));
				switch (pe.getAttribute("pinclass")){
					case "flowobjectinput":
						this.pins.get("flow_in").add(p);
						break;
					case "flowobjectoutput":
						this.pins.get("flow_out").add(p);
						break;
					case "dataobjectinput":
						this.pins.get("data_in").add(p);
						break;
					case "dataobjectoutput":
						this.pins.get("data_out").add(p);
						break;
				}
			}
		}
	}



	public static final class FischertechnicPin{
		private static Map<String,Map<String,Map<Integer,FischertechnicDisassembler.FischertechnicPin>>> _pl=new HashMap<String,Map<String,Map<Integer,FischertechnicDisassembler.FischertechnicPin>>>();



		public FischertechnicDisassembler.FischertechnicProgram p;
		public FischertechnicDisassembler.FischertechnicBlock pr;
		public String name;
		public String type;
		public int id;
		public int p_id;
		public FischertechnicDisassembler.FischertechnicWire w;
		public List<FischertechnicDisassembler.FischertechnicPin> e;



		public FischertechnicPin(FischertechnicDisassembler.FischertechnicProgram p,FischertechnicDisassembler.FischertechnicBlock pr,String name,String type,int id,int p_id){
			this.p=p;
			this.pr=pr;
			this.name=name;
			this.type=type;
			this.id=id;
			this.p_id=p_id;
			this.w=null;
			this.e=new ArrayList<FischertechnicDisassembler.FischertechnicPin>();
			if (FischertechnicDisassembler.FischertechnicPin._pl.containsKey(this.p.name)==false){
				FischertechnicDisassembler.FischertechnicPin._pl.put(this.p.name,new HashMap<String,Map<Integer,FischertechnicDisassembler.FischertechnicPin>>());
			}
			if (FischertechnicDisassembler.FischertechnicPin._pl.get(this.p.name).containsKey(this.type.split("_")[0])==false){
				FischertechnicDisassembler.FischertechnicPin._pl.get(this.p.name).put(this.type.split("_")[0],new HashMap<Integer,FischertechnicDisassembler.FischertechnicPin>());
			}
			FischertechnicDisassembler.FischertechnicPin._pl.get(this.p.name).get(this.type.split("_")[0]).put(this.p_id,this);
		}



		public static FischertechnicDisassembler.FischertechnicPin get(String p,String t,int id){
			return FischertechnicDisassembler.FischertechnicPin._pl.get(p).get(t).get(id);
		}



		@Override
		public String toString(){
			return String.format("Pin(id=%d, name='%s', type='%s')",this.id,this.name,this.type);
		}
	}



	public static final class FischertechnicWire{
		public FischertechnicDisassembler.FischertechnicProgram p;
		public List<Integer> start;
		public int end;
		public String type=null;
		private List<Integer> _d;
		private boolean _rs=false;



		public FischertechnicWire(FischertechnicDisassembler.FischertechnicProgram p,Element e){
			this.p=p;
			this.start=new ArrayList<Integer>();
			this._d=new ArrayList<Integer>();
			for (Element pe:FischertechnicDisassembler._xml_child(e,"o","classname","wxCanvasPin")){
				if (this.type==null){
					this.type=(pe.getAttribute("pinclass").contains("flow")?"flow":"data");
				}
				if (pe.getAttribute("name").equals("dynamic")){
					this._d.add(Integer.parseInt(pe.getAttribute("resolveid")));
				}
				else{
					FischertechnicDisassembler.FischertechnicPin tp=FischertechnicDisassembler.FischertechnicPin.get(this.p.name,this.type,Integer.parseInt(pe.getAttribute("id")));
					if (tp==null){
						this.end=Integer.parseInt(pe.getAttribute("id"));
					}
					else if (tp.type.contains("out")==true){
						this.start.add(Integer.parseInt(pe.getAttribute("id")));
					}
					else{
						this.end=Integer.parseInt(pe.getAttribute("id"));
					}
				}
			}
		}



		public void resolve(List<FischertechnicDisassembler.FischertechnicWire> wl){
			if (this._rs==true){
				return;
			}
			this._rs=true;
			for (int i=wl.size()-1;i>=0;i--){
				if (wl.get(i)==this||!wl.get(i).type.equals(this.type)){
					continue;
				}
				if (wl.get(i)._rs==false){
					wl.get(i).resolve(wl);
				}
				for (int id:this._d){
					if (wl.get(i).end==id){
						this.start.addAll(wl.get(i).start);
						wl.remove(i);
					}
				}
			}
			this._d=null;
		}



		@Override
		public String toString(){
			return String.format("Wire(type='%s', start=%s, end=%d)",this.type,this.start.toString(),this.end);
		}
	}



	public static FischertechnicDisassembler.FischertechnicFile load(String fp){
		try{
			Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fp);
			doc.getDocumentElement().normalize();
			Element root=doc.getDocumentElement();
			FischertechnicDisassembler.FischertechnicFile fo=new FischertechnicDisassembler.FischertechnicFile(FischertechnicDisassembler._xml_child(root,"title",null,null).get(0).getTextContent());
			Element wxc=FischertechnicDisassembler._xml_child(root,"o","classname","wxCanvasObject").get(0);
			for (Element sce:FischertechnicDisassembler._xml_child(wxc,"o","classname","ftProSubroutineFunction")){
				FischertechnicDisassembler.FischertechnicProgram p=new FischertechnicDisassembler.FischertechnicProgram(sce.getAttribute("name"));
				for (Element be:FischertechnicDisassembler._xml_child(sce,"o",null,null)){
					if (be.getAttribute("classname").equals("ftProFlowWire")||be.getAttribute("classname").equals("ftProDataWire")){
						p.add(new FischertechnicDisassembler.FischertechnicWire(p,be));
						continue;
					}
					FischertechnicDisassembler.FischertechnicBlock bc=null;
					try{
						bc=(FischertechnicDisassembler.FischertechnicBlock)Class.forName(FischertechnicDisassembler._get_class_name(be.getAttribute("classname").substring(5))).getConstructor().newInstance();
					}
					catch (ClassNotFoundException e){
						bc=new FischertechnicDisassembler.FischertechnicBlock.Default(be.getAttribute("classname").substring(5));
					}
					p.add(bc);
					bc.load(be);
				}
				p.resolve();
				fo.progs.add(p);
			}
			return fo;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



	private static String _get_class_name(String nm){
		String o="com.krzem.fischertechnic_disassembler.FischertechnicDisassembler$FischertechnicBlock";
		for (String c:nm.split("")){
			if (c.toUpperCase().equals(c)){
				o+="$";
			}
			o+=c;
		}
		return o;
	}



	private static ArrayList<Element> _xml_child(Element p,String tn,String k,String v){
		ArrayList<Element> o=new ArrayList<Element>();
		NodeList cl=p.getChildNodes();
		for (int j=0;j<cl.getLength();j++){
			if (cl.item(j).getNodeType()!=Node.ELEMENT_NODE){
				continue;
			}
			Element e=(Element)cl.item(j);
			if (e.getTagName().equals(tn)&&((k!=null&&e.getAttribute(k).equals(v))||k==null)){
				o.add(e);
			}
		}
		return o;
	}
}