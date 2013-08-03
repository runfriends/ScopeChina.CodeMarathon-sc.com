package cn.com.scopeinternational.codemarathon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cn.com.scopeinternational.codemarathon.common.Help;

/**
 * 从指定输入文件读取数据，完成长度单位换算，计算结果输出到指定输出文件
 */
public class LengthUnitConvertor {
	private static final Pattern WHITESPACE=Pattern.compile(" ");
	private static final Pattern DIGIT=Pattern.compile("^\\d+(\\.\\d*)*$");
	private static final String DEFAULT_INPUT_PATH="input.txt";//默认输入文件路径是程序当前路径
	private static final String DEFAULT_OUTPUT_PATH="output.txt";//默认输出文件路径是程序当前路径
	
	private boolean rulesEnd;//程序开始采用默认值
	private Map<String,BigDecimal> unitRules;//保存所有换算规则
	private int scale;
	private RoundingMode roundingMode;
	private LineNumberReader reader;
	private PrintWriter writer;
	/**
	 * 以默认输入输出路径初始化
	 * @throws IOException
	 */
	public LengthUnitConvertor() throws IOException{
		this("","",2,RoundingMode.HALF_UP);
	}
	/**
	 * @param input  输入文件路径
	 * @param output 输出文件路径
	 * @throws IOException
	 */
	public LengthUnitConvertor(String input, String output,int scale,RoundingMode roundingMode) throws IOException{
		unitRules=new HashMap<String,BigDecimal>();
		this.scale=scale;
		this.roundingMode=roundingMode;
		createReader(input);
		createWriter(output);
	}
	private void createReader(String input) throws FileNotFoundException{
		reader=new LineNumberReader(new FileReader(getInputPath(input)));
	}
	private void createWriter(String output) throws IOException{
		File out=new File(getOutputPath(output));
		if(!out.exists()){
			out.createNewFile();
		}
		writer=new PrintWriter(new FileWriter(out));
	}
	/**
	 * 如果参数是null或""，就返回DEFAULT_INPUT_PATH，否则返回参数
	 * @param in
	 * @return
	 */
	private String getInputPath(String in){
		return getPath(in,DEFAULT_INPUT_PATH);
	}
	/**
	 * 如果参数是null或""，就返回DEFAULT_OUTPUT_PATH，否则返回参数
	 * @param out
	 * @return
	 */
	private String getOutputPath(String out){
		return getPath(out,DEFAULT_OUTPUT_PATH);
	}
	/**
	 * 如果path是null或""，就返回defaultPath，否则返回path
	 * @param path
	 * @param defaultPath
	 * @return
	 */
	private String getPath(String path,String defaultPath){
		if(Help.isEmpty(path)){
			return defaultPath;
		}else{
			return path;
		}
	}
	private LengthUnitConvertor initOutput(){
		writer.println(Help.getMyRegistEmail());
		writer.println();
		return this;
	}
	/**
	 * 转换结束时调用
	 * @throws IOException 
	 */
	private void complete() throws IOException{
		rulesEnd=false;
		if(reader!=null){
			reader.close();
		}
		if(writer!=null){
			writer.close();
		}
	}
	/**
	 * 输入文件的规则部分结束，转换开始
	 */
	private void startConvert(){
		rulesEnd=true;
	}
	/**
	 * 判断规则部分是否结束
	 * @return
	 */
	private boolean rulesEnd(){
		return rulesEnd;
	}
	/**
	 * 参数作为输入文件路径，读取、转换并输出计算结果到指定文件
	 * @throws IOException
	 */
	public LengthUnitConvertor convert() throws IOException{
		String line=null;
		while((line=reader.readLine())!=null){
			convert(line);
		}
		return this;
	}
	/**
	 * 根据输入行不同，判断是规则部分还是计算部分，进而执行不同分支
	 * @param line
	 */
	private void convert(String line){
		if(Help.isEmpty(line)){
			startConvert();
		}else if(rulesEnd()){
			try{
				writer.println(calculate(line).toPlainString()+" m");
			}catch(ArrayIndexOutOfBoundsException e){
				printIllegalExpression(line);
			}catch(IllegalArgumentException e){
				printIllegalExpression(line);
			}
		}else{
			learnRule(line);
		}
	}
	private void printIllegalExpression(String line){
		System.out.println("line number:"+reader.getLineNumber()+", illegal expression line: "+line);
	}
	/**
	 * 计算表达式
	 * @param line 输入文件的计算部分的表达式，一行一个表达式
	 * @return
	 */
	private BigDecimal calculate(String line){
		String[] expression=WHITESPACE.split(line);
		/*
		 * 0.032 furlong
1 furlong + 2.5 feet
		 */
		BigDecimal result=BigDecimal.ZERO;
		char op='+';
		for(int i=0,l=expression.length;i<l;i++){
			String part=expression[i];
			if(DIGIT.matcher(part).matches()){
				result=calculate(result,op,
						new BigDecimal(part).multiply(extractRule(expression[++i])));
			}else{
				op=part.charAt(0);
			}
		}
		return result.setScale(scale,roundingMode);
	}
	/**
	 * op1与op2的和或差，由op决定
	 * @param op1
	 * @param op  '+'或'-'
	 * @param op2
	 * @return
	 */
	private BigDecimal calculate(BigDecimal op1, char op, BigDecimal op2){
		switch(op){
		case '+':
			return op1.add(op2);
		case '-':
			return op1.subtract(op2);
		default :
			throw new IllegalArgumentException("op is illegal"+op);	
		}
	}
	/**
	 * 根据单位名称得到换算规则
	 * @param unit
	 * @return
	 */
	private BigDecimal extractRule(String unit){
		return this.unitRules.get(Help.returnSingular(unit));
	}
	/**
	 * 让程序学习单位换算规则 
	 * @param line 输入文件的规则行
	 */
	private void learnRule(String line){
		String[] rule=WHITESPACE.split(line);
		this.unitRules.put(rule[1], new BigDecimal(rule[3]));
	}
	
	public static void main(String[] args) throws IOException {
		int argslen=args.length;
		String input=argslen>0?args[0]:"";
		String output=argslen>1?args[1]:"";
		int scale=argslen>2?Integer.parseInt(args[2]):2;
		RoundingMode mode=argslen>3?RoundingMode.valueOf(args[3]):RoundingMode.HALF_UP;
		new LengthUnitConvertor(input,output,scale,mode).initOutput().convert().complete();
	}
}
