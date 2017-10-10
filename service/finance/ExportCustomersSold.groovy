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
EntityList customersSoldList = ec.entity.find("finance.product.CustomersSold").condition("instId", instId).condition("projectCode", projectCode).condition("versionNo", versionNo).list()

if(customersSoldList.size() == 0){
	ec.message.addError("输入的项目编号或版本号不正确，找不到所需文件")
}else{
	def response = ec.web.response
	String filename = projectCode.trim()+"_客户明细销售表_"+versionNo.trim()+".csv";
	response.setContentType("application/csv;charset=UTF-8");
	response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
	response.setCharacterEncoding("UTF-8");
	def array = [(byte)0xef, (byte)0xbb, (byte)0xbf] as byte[]
	
	int len = 0;
	OutputStream out = response.getOutputStream();
	out.write(array);
	
	def header="交易编号,交易时间戳,资产份额,机构标志,证件类别,客户姓名,客户全称,证件编号,手机,性别,证件地址,电话,邮政编码,联系地址,风险承受级别,订单标识,计息年化天数,年化收益率\r\n"
	out.write(header.getBytes("UTF-8"))
	for(EntityValue customersSold:customersSoldList){
		def value=""
		value=value+customersSold.get("transactionId")+"\t"+","
		value=value+customersSold.get("transactionTime")+"\t"+","
		value=value+customersSold.get("assetShare")+","
		value=value+customersSold.get("customerType")+","
		value=value+customersSold.get("certificateType")+","
		value=value+customersSold.get("username")+","
		value=value+customersSold.get("userFullName")+","
		value=value+customersSold.get("certificateNumber")+"\t"+","
		value=value+customersSold.get("cellPhone")+"\t"+","
		value=value+customersSold.get("sex")+","
		value=value+customersSold.get("certificateAddress")+","
		value=value+customersSold.get("telephone")+","
		value=value+customersSold.get("postalcode")+","
		value=value+customersSold.get("contactAddress")+","
		value=value+customersSold.get("riskRating")+","
		value=value+customersSold.get("orderId")+","
		value=value+customersSold.get("daysOfYear")+","
		value=value+customersSold.get("yieldRate")+"\r\n"
		
		out.write(value.getBytes("UTF-8"))
	}
	
	
	out.flush()


}


para.release()

