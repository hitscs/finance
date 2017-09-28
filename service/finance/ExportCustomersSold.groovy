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

	EntityValue customersSold =customersSoldList.get(0)
	para.downloadPath = "/home/"+instId+"/upload/"+customersSold.get("importDate")+"/";

	String filename = projectCode.trim()+"_客户明细销售表_"+versionNo.trim();


	List list=SftpUtil.downloadFilesAsInputStreamByName(para,filename)
	if(list.size()>0){
		//LineNumberReader reader ;

		Map map=list.get(0)
		InputStream instream =map.get("fileContent");
        BufferedReader br = new BufferedReader(new InputStreamReader(instream,"UTF-8"));
        StringBuffer resBuffer = new StringBuffer();  
        String resTemp = "";  
        while((resTemp = br.readLine()) != null){  
            resBuffer.append(resTemp+"\r\n");  
        }  
		br.close()
		instream.close()
		def response = ec.web.response

		response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));

		ec.web.sendTextResponse(resBuffer.toString(), "text/xml", "filename")


	}else{
		ec.message.addError("SFTP未启动或者文件已被删除，找不到所需文件")
	}




}




