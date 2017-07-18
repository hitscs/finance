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

//def treatmentResult="0"
Float sum=0.00
EntityValue fileMeta
if(trialResultsFileMeta.size()==1){
	fileMeta=trialResultsFileMeta.get(0)
}
String fileString="";
for(EntityValue file:fileList){
	sum=sum+file.totalRepaymentAmount
	def kaitongOrderNumber=file.kaitongOrderNumber
	if(null==file.kaitongOrderNumber||file.kaitongOrderNumber.trim().equals("")){
		kaitongOrderNumber="\"\""
	}
	def orgOrderNumber=file.orgOrderNumber
	if(null==file.orgOrderNumber.trim().equals("")){
		orgOrderNumber="\"\""
	}
	fileString=fileString+kaitongOrderNumber+"|"+orgOrderNumber+"|"+file.productCode+"|"+file.customerName+"|"+file.passportType+"|"+file.passportNo+"|"+file.institutionId+"|"+file.repaymentPrincipal+"|"+file.repaymentInterest+"|"+file.totalRepaymentAmount+"|"+file.paymentDueDay.replace("-", "")+"|"+file.treatmentResult+"\r\n"
}
lineOne="版本号:"+versionNo+"|总笔数:"+fileMeta.totalRoll+"|总金额:"+fileMeta.totalMoney+"|处理结果:"+fileMeta.treatmentResult+"\r\n"
header="外贸信托订单号|机构订单号|产品代码|客户姓名|客户证件类别|客户证件编号|机构客户ID|还款本金|还款利息|还款总金额|到期还款日|处理结果\r\n"

content=lineOne+header+fileString

String makeDir = "/home/"+instId+"/download/"+uploadDate+"/"
SftpUtil.makeDir(para, makeDir)

para.uploadPath =makeDir

InputStream instream = new ByteArrayInputStream(content.getBytes("UTF-8"));

fileName=projectCode+"_交易试算结果文件_"+versionNo+".txt"
SftpUtil.uploadFileInputStream(para, instream,fileName)
