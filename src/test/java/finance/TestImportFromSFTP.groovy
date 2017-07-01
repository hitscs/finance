package finance;

import static org.junit.Assert.*

import java.security.Timestamp

import org.junit.Test
import org.moqui.context.ExecutionContext

import com.finance.utils.SFTPParameter
import com.finance.utils.SftpUtil
//import org.moqui.context.ExecutionContext
class TestImportFromSFTP {

	@Test
	public void testUploadFileInputStream() {




		SFTPParameter para = new SFTPParameter();

		para.hostName = "127.0.0.1";
		para.userName = "sunmingjun";
		para.passWord = "sunmingjun";
		para.port = 22;

		ExecutionContext ec 
		//para.downloadPath ="/home/hezuo/"
		para.uploadPath ="/home/hezuo/"
		//List<ChannelSftp.LsEntry> fileList=SftpUtil.getFiles(para)

		String str = "String与InputStream相互转换";

		InputStream   in_nocode   =   new   ByteArrayInputStream(str.getBytes());
		InputStream   instream   =   new   ByteArrayInputStream(str.getBytes("UTF-8"));

		SftpUtil.uploadFileInputStream(para, instream,"aaaaaaaaaaaaaa.txt")
		//toDay=ec.l10n.format(new Timestamp(System.currentTimeMillis()), "yyyyMMdd")

		println "列表"
	}
	@Test
	public void testDownloadFileAsInputStream() {
		SFTPParameter para = new SFTPParameter();

		para.hostName = "127.0.0.1";
		para.userName = "sunmingjun";
		para.passWord = "sunmingjun";
		para.port = 22;
		para.downloadPath ="/home/hezuo/"
		para.uploadPath ="/home/hezuo/"
		//List<ChannelSftp.LsEntry> fileList=SftpUtil.getFiles(para)

		//String str = "String与InputStream相互转换";

		//InputStream   in_nocode   =   new   ByteArrayInputStream(str.getBytes());
		//InputStream   instream   =   new   ByteArrayInputStream(str.getBytes("UTF-8"));

		List list=SftpUtil.downloadFilesAsInputStream(para)
		for(Map map:list){
			ByteArrayOutputStream   baos   =   new   ByteArrayOutputStream();
			InputStream   instream   =map.get("fileContent")
			int   i=-1;
			while((i=instream.read())!=-1){
				baos.write(i);
			}
			baos.toString();
			//print  baos.toString();

		}

		println "列表"
	}
}
