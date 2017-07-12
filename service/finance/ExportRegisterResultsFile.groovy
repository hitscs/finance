import org.moqui.entity.EntityValue

import com.finance.utils.DateUtils
import com.finance.utils.SFTPParameter
import com.finance.utils.SftpUtil


SFTPParameter para = new SFTPParameter();

para.hostName = "127.0.0.1";
para.userName = "sunmingjun";
para.passWord = "sunmingjun";
para.port = 22;

def uploadDate=DateUtils.getNowTime("yyyyMMdd")

def instId="hezuo"



String fileString="";
for(EntityValue file:fileList){

	fileString=fileString+"|"+file.get("transactionId")+"|"+file.get("productCode")+"\r\n"
}
header="交易编号|产品代码\n"

content=header+fileString

String makeDir = "/home/"+instId+"/download/"+uploadDate+"/"
SftpUtil.makeDir(para, makeDir)

para.uploadPath =makeDir

InputStream instream = new ByteArrayInputStream(content.getBytes("UTF-8"));

fileName=projectCode+"_交易登记文件_"+versionNo+".txt"
SftpUtil.uploadFileInputStream(para, instream,fileName)