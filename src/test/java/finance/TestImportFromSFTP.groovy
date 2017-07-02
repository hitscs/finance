package finance;

import static org.junit.Assert.*

import org.junit.Test
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityList
import org.moqui.entity.EntityValue

import com.finance.utils.DateUtils
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

		//SftpUtil.uploadFileInputStream(para, instream,"aaaaaaaaaaaaaa.txt")
		def toDay=DateUtils.getNowTime("yyyyMMdd")

		println toDay
	}
	@Test
	public void testDownloadFileAsInputStream() {
		SFTPParameter para = new SFTPParameter();

		para.hostName = "127.0.0.1";
		para.userName = "sunmingjun";
		para.passWord = "sunmingjun";
		para.port = 22;
		para.downloadPath ="/home/hezuo/upload/"
		
		def toDay=DateUtils.getNowTime("yyyyMMdd")
		
		def instId="hezuo"
		//para.uploadPath ="/home/hezuo/"
		//List<ChannelSftp.LsEntry> fileList=SftpUtil.getFiles(para)

		String str = "客户明细销售表";

		//InputStream   in_nocode   =   new   ByteArrayInputStream(str.getBytes());
		//InputStream   instream   =   new   ByteArrayInputStream(str.getBytes("UTF-8"));

		List list=SftpUtil.downloadFilesAsInputStream(para)

		LineNumberReader reader ;
		for(Map map:list){
			String fileName=map.get("fileName");
			
			if(fileName.contains(str)){
				//ByteArrayOutputStream baos = new ByteArrayOutputStream();
				EntityList curList = ec.entity.find("moqui.basic.Enumeration").condition("parentEnumId", parentEnumId).useCache(true).list()
				String[] fileNameSplit=fileName.split("_")
				InputStream instream =map.get("fileContent");

				reader=new LineNumberReader(new InputStreamReader(instream ,"UTF-8"));
				String s = "";

				while ((s = reader.readLine()) != null) {
					if(reader.getLineNumber()>1){
						String[] ss=s.split('\\|');
						
						EntityValue customersSold =ec.entity.makeValue("finance.product.CustomersSold")
						
						
						customersSold.put("transactionId",ss[0])//交易编号
						customersSold.put("transactionTime",ss[1])//交易时间戳
						customersSold.put("assetShare",ss[2])//资产份额
						customersSold.put("customerType",ss[3])//机构标志
						customersSold.put("certificateType",ss[4])//证件类别
						customersSold.put("username",ss[5])//客户姓名
						customersSold.put("userFullName",ss[6])//客户全称
						customersSold.put("certificateNumber",ss[7])//证件编号
						customersSold.put("cellPhone",ss[8])//手机
						customersSold.put("sex",ss[9])//性别
						customersSold.put("certificateAddress",ss[10])//证件地址
						customersSold.put("telephone",ss[11])//电话
						customersSold.put("postalcode",ss[12])//邮政编码
						customersSold.put("contactAddress",ss[13])//联系地址
						customersSold.put("riskRating",ss[14])//风险承受级别
						customersSold.put("orderId",ss[15])//订单标识
						customersSold.put("daysOfYear",ss[16])//计息年化天数
						customersSold.put("yieldRate",ss[17])//年化收益率
						customersSold.put("fileName",fileName)//导入文件名称
						customersSold.put("instId",instId)//互联网平台Id
						customersSold.put("importDate",toDay)//导入时间（文件夹名）
						customersSold.put("productCode",fileNameSplit[0])//产品代码
						customersSold.put("versionNo",fileNameSplit[2])//版本号

						customersSold.create()
						}
				}
				reader.close();
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/*		InputStream instream = new ByteArrayInputStream(content.getBytes("UTF-8"));
		
		//List list=SftpUtil.downloadFilesAsInputStream(para)
		for(Map map:list){
			ByteArrayOutputStream   baos   =   new   ByteArrayOutputStream();
			InputStream   instream   =map.get("fileContent")
			int   i=-1;
			while((i=instream.read())!=-1){
				baos.write(i);
			}
			baos.toString();
			//print  baos.toString();
		
		}*/
	}
}
