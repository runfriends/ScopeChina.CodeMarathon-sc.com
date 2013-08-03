package cn.com.scopeinternational.codemarathon.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import cn.com.scopeinternational.codemarathon.exception.ConstantLoadingException;
/**
 * 帮助工具
 * @author running
 *
 */
public class Help {
	/**
	 * 常量属性文件后缀，所有在资源目录中以此结束的文件名都作为常量文件名加载。
	 * 常量key相同的以最后一次出现的为准
	 */
	public static final String CONSTANT_PROPERTIES="constant.properties";
	/**
	 * 默认字符集
	 */
	public static final String DEFAULT_CHARSET="UTF-8";
	private static Properties PROPERTIES;
	static{
		Properties properties=new Properties();
		try {
			for(File props:new File(Help.class.getResource("/").toURI()).listFiles()){
				if(props.isFile() && props.getName().endsWith(CONSTANT_PROPERTIES)){
					properties.putAll(Help.loadProperties(props, DEFAULT_CHARSET));
				}
			}
			PROPERTIES=properties;
		}catch(Exception e) {
			throw new ConstantLoadingException(e);
		}
		
	}
	/**
	 * 从file以charset指定字符集加载Properties
	 * @param file 保存着Properies数据的文件
	 * @param charset file的字符集
	 * @return 从file加载的Properties对象
	 * @throws IOException Properties
	 */
	public static Properties loadProperties(File file, String charset) throws IOException{
		InputStream in=null;
		try{
			in=new FileInputStream(file);
			return loadProperties(in,charset);
		}finally{
			if(in!=null){
				in.close();
			}
		}
	}
	/**
	 * 以charset指定的字符集从in加载Properties
	 * @param in
	 * @param charset
	 * @return
	 * @throws IOException Properties
	 * @throws 
	 * @exception
	 */
	public static Properties loadProperties(InputStream in, String charset) throws IOException{
		return loadProperties(new InputStreamReader(in, charset));
	}
	/**
	 * 从reader加载Properties数据
	 * @param reader 字符流
	 * @return
	 * @throws IOException Properties
	 * @throws 
	 * @exception
	 */
	public static Properties loadProperties(Reader reader) throws IOException{
		Properties props=new Properties();
		props.load(reader);
		return props;
	}
	/**
	 * 如果src是null或""，就返回true，否则返回false
	 * @param src
	 * @return
	 */
	public static boolean isEmpty(String src){
		return src==null || src.equals("");
	}
	/**
	 * 返回单位的单数形式
	 * @param src
	 * @return
	 */
	public static String returnSingular(String unit){
		String result=PROPERTIES.getProperty(unit);
		return !isEmpty(result)?result:unit;
	}
	/**
	 * 得到我的注册email
	 */
	public static String getMyRegistEmail(){
		return PROPERTIES.getProperty("regist.email");
	}
}
