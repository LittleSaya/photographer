package photographer;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Main {

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		
		System.out.println("===produce frame, time: " + sdf.format(new Date()));
		
		// 生成视频图片
		
		// 读取配置文件
		JSONObject conf = JSONObject.parseObject(SysHelper.readFile(args[0], "UTF-8"));
		
		// 随机选择一个番剧，再随机选择番剧中的一集
		String videoBaseDir = conf.getString("videoBaseDir");
		
		JSONArray animeList = conf.getJSONArray("animeList");
		int animeIndex = new Double(Math.floor(Math.random() * animeList.size())).intValue();
		JSONObject anime = animeList.getJSONObject(animeIndex);
		String animeDir = anime.getString("animeDir");
		String animeName = anime.getString("animeName");
		
		JSONArray episodeList = anime.getJSONArray("episodeList");
		int episodeIndex = new Double(Math.floor(Math.random() * episodeList.size())).intValue();
		JSONObject episode = episodeList.getJSONObject(episodeIndex);
		String episodeFile = episode.getString("episodeFile");
		String episodeName = episode.getString("episodeName");
		
		String videoLocation = videoBaseDir + animeDir + episodeFile;
		
		System.out.println("video location: " + videoLocation);
		
		// 使用 ffprobe 获取该视频的开始时间与长度
		String ffprobeLocation = conf.getString("ffprobeLocation");
		String ffprobeCmd = ffprobeLocation + " -print_format ini -show_format " + videoLocation;
		String ffprobeResult = SysHelper.execCmd(ffprobeCmd);
		int startTimeIndex = ffprobeResult.indexOf("start_time=") + "start_time=".length();
		String startTimeStr = ffprobeResult.substring(startTimeIndex, ffprobeResult.indexOf('\n', startTimeIndex));
		double startTime = Double.valueOf(startTimeStr);
		int durationIndex = ffprobeResult.indexOf("duration=") + "duration=".length();
		String durationStr = ffprobeResult.substring(durationIndex, ffprobeResult.indexOf('\n', durationIndex));
		double duration = Double.valueOf(durationStr);
		
		System.out.println("start time: " + startTime);
		System.out.println("duration: " + duration);
		
		// 使用 ffmpeg 截取视频中的随机帧
		double frameTime = Math.random() * duration + startTime;
		String ffmpegLocation = conf.getString("ffmpegLocation");
		String outputPicName = conf.getString("outputDir") + "out.png";
		String ffmpegCmd = ffmpegLocation + " -ss " + frameTime + " -i " + videoLocation + " -frames 1 -pix_fmt rgb24 -y " + outputPicName;
		SysHelper.execCmd(ffmpegCmd);
		
		System.out.println("anime name: " + animeName);
		System.out.println("episode name: " + episodeName);
		System.out.println("frame time: " + frameTime);
		
		System.out.println("===post to QQZone, time: " + sdf.format(new Date()));
		
		// 将图片po到QQ空间
		String albumName = conf.getJSONArray("animeList").getJSONObject(animeIndex).getString("remoteAlbumName");
		
		if (conf.getString("browser").equals("chrome")) {
			photographer.poster.chrome.QZone.postToQZone(
					animeName, episodeName, formatSec(frameTime),
					outputPicName,
					conf,
					albumName);
		} else if (conf.getString("browser").equals("firefox")) {
			photographer.poster.firefox.QZone.postToQZone(
					animeName, episodeName, formatSec(frameTime),
					outputPicName,
					conf,
					albumName);
		}
	}
	
	public static String formatSec(double sec) {
		Integer m = new Double(Math.floor(sec / 60)).intValue();
		Integer s = new Double(Math.floor(sec - 60 * m)).intValue();
		//Integer S = new Double((sec - 60 * m - s) * 1000).intValue();
		return pad(m.toString()) + "分" + pad(s.toString()) + "秒";
	}
	
	public static String pad(String s) {
		return (s.length() == 1 ? "0" + s : s);
	}
}
