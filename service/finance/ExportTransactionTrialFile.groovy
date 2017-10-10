import com.finance.utils.SFTPParameter
import com.finance.utils.SftpUtil

import java.io.BufferedReader
import java.io.InputStreamReader

import org.moqui.context.ExecutionContext
import com.finance.utils.DateUtils
import org.moqui.entity.EntityValue
import org.moqui.entity.EntityList

SFTPParameter para = new SFTPParameter();

para.hostName = "127.0.0.1";
para.userName = "sunmingjun";
para.passWord = "sunmingjun";
para.port = 22;


ExecutionContext ec = context.ec

def instId="hezuo"
EntityList resultsFileList = ec.entity.find("finance.product.TransactionTrialFile").condition("instId", instId).condition("projectCode", projectCode).condition("versionNo", versionNo).list()

if(resultsFileList.size() == 0){
	ec.message.addError("输入的项目编号或版本号不正确，找不到所需文件")
}else{


	String filename = projectCode.trim()+"_交易试算文件_"+versionNo.trim()+".csv";
	def response = ec.web.response
	response.setContentType("application/csv;charset=UTF-8");
	response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
	response.setCharacterEncoding("UTF-8");
	def array = [(byte)0xef, (byte)0xbb, (byte)0xbf] as byte[]
	
	//int len = 0;
	OutputStream out = response.getOutputStream();
	out.write(array);
	
	
	def header="外贸信托订单号,机构订单号,产品代码,客户姓名,客户证件类别,客户证件编号,机构客户ID,还款本金,还款利息,还款总金额,到期还款日,是否赎回\r\n"
	
	out.write(header.getBytes("UTF-8"))
	for(EntityValue resultsFile:resultsFileList){
		def isRedeem=resultsFile.get("isRedeem")?resultsFile.get("isRedeem"):""
		def value=""
		value=value+resultsFile.get("kaitongOrderNumber")+"\t"+","
		value=value+resultsFile.get("orgOrderNumber")+"\t"+","
		value=value+resultsFile.get("productCode")+"\t"+","
		value=value+resultsFile.get("customerName")+","
		value=value+resultsFile.get("passportType")+","
		value=value+resultsFile.get("passportNo")+"\t"+","
		value=value+resultsFile.get("institutionId")+"\t"+","
		value=value+resultsFile.get("repaymentPrincipal")+","
		value=value+resultsFile.get("repaymentInterest")+","
		value=value+resultsFile.get("totalRepaymentAmount")+","
		value=value+resultsFile.get("paymentDueDay")+"\t"+","
		value=value+isRedeem+"\r\n"
		
		out.write(value.getBytes("UTF-8"))
	}
	
	
	out.flush()

}




para.release()
