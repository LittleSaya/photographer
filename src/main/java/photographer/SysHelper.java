package photographer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SysHelper {
	
	public static String readFile(String filePath, String encoding) throws Exception {
		File file = new File(filePath);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
		String line = "", data = "";
		while (true) {
			line = bufferedReader.readLine();
			if (line == null) {
				break;
			} else {
				data += line;
			}
		}
		bufferedReader.close();
		return data;
	}
	
	public static void writeFile(String filePath, String data) throws Exception {
		File file = new File(filePath);
		file.createNewFile();
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bufferedWriter.write(data);
		bufferedWriter.close();
	}
	
	public static String execCmd(String cmd) throws Exception {
		System.out.println("exec cmd: " + cmd);
		
        StringBuilder result = new StringBuilder();

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;

        // 执行命令, 返回一个子进程对象（命令在子进程中执行）
        process = Runtime.getRuntime().exec(cmd, null);

        // 方法阻塞, 等待命令执行完成（成功会返回0）
        int retVal = process.waitFor();
        System.out.println("cmd return value: " + retVal);

        // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
        bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
        bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

        // 读取输出
        String line = null;
        while ((line = bufrIn.readLine()) != null) {
            result.append(line).append('\n');
        }
        while ((line = bufrError.readLine()) != null) {
            result.append(line).append('\n');
        }
        
        bufrIn.close();
        bufrError.close();

        // 销毁子进程
        if (process != null) {
            process.destroy();
        }

        // 返回执行结果
        return result.toString();
	}

}
