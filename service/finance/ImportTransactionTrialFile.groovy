import com.finance.utils.SFTPParameter
import com.finance.utils.SftpUtil
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

//def importDate=DateUtils.getNowTime("yyyyMMdd")
importDate=importDate.replace("-", "")
def instId="hezuo"

para.downloadPath = "/home/"+instId+"/upload/"+importDate+"/";

String str = "交易试算文件";


List list=SftpUtil.downloadFilesAsInputStreamByName(para,str)

LineNumberReader reader ;
for(Map map:list){
	String fileName=map.get("fileName");
	//if(fileName.contains(str)){
		
		EntityList resultsFileList = ec.entity.find("finance.product.TransactionTrialFile").condition("instId", instId).condition("importDate", importDate).condition("fileName", fileName).list()
		if(resultsFileList.size() >0){
			ec.entity.find("finance.product.TransactionTrialFileMeta").condition("instId", instId).condition("fileName", fileName).deleteAll()
			ec.entity.find("finance.product.TransactionTrialFile").condition("instId", instId).condition("fileName", fileName).deleteAll()
			println "---------------------------删除文件名为--$fileName--的记录------------------------------------"
		}
		//if(resultsFileList.size() == 0){
			String[] fileNameSplit=fileName.split("_")
			InputStream instream =map.get("fileContent");

			reader=new LineNumberReader(new InputStreamReader(instream ,"UTF-8"));
			String s = "";
			EntityValue meta =ec.entity.makeValue("finance.product.TransactionTrialFileMeta").setSequencedIdPrimary()
			while ((s = reader.readLine()) != null) {

				if(reader.getLineNumber()==1){
					String[] ss=s.split('\\|');
					println ss

					meta.put("productCode",fileNameSplit[0])//产品代码
					meta.put("totalRoll",ss[1].replace("总笔数:", ""))//总笔数
					meta.put("totalMoney",ss[2].replace("总金额:", ""))//总金额



					meta.put("fileName",fileName)//导入文件名称
					meta.put("instId",instId)//互联网平台Id
					meta.put("importDate",importDate)//导入时间（文件夹名）
					meta.put("projectCode",fileNameSplit[0])//与互联网合作项目的编号
					meta.put("versionNo",fileNameSplit[2].replace(".txt", ""))//版本号
					meta.create()
				}



				//println meta.get("ttfmId")+"--------------------------------------------------------"



				if(reader.getLineNumber()>2){
					String[] ss=s.split('\\|');
					println ss
					EntityValue resultsFile =ec.entity.makeValue("finance.product.TransactionTrialFile").setSequencedIdPrimary()


					resultsFile.put("kaitongOrderNumber",ss[0])//外贸信托订单号
					resultsFile.put("orgOrderNumber",ss[1])//机构订单号
					resultsFile.put("productCode",ss[2])//产品代码
					resultsFile.put("customerName",ss[3])//客户姓名
					resultsFile.put("passportType",ss[4])//客户证件类别
					resultsFile.put("passportNo",ss[5])//客户证件编号
					resultsFile.put("institutionId",ss[6])//机构客户ID
					resultsFile.put("repaymentPrincipal",ss[7])//还款本金
					resultsFile.put("repaymentInterest",ss[8])//还款利息
					resultsFile.put("totalRepaymentAmount",ss[9])//还款总金额
					resultsFile.put("paymentDueDay",ss[10])//到期还款日
					//resultsFile.put("treatmentResult",ss[11])//处理结果


					resultsFile.put("fileName",fileName)//导入文件名称
					resultsFile.put("instId",instId)//互联网平台Id
					resultsFile.put("importDate",importDate)//导入时间（文件夹名）
					resultsFile.put("projectCode",fileNameSplit[0])//与互联网合作项目的编号
					resultsFile.put("versionNo",fileNameSplit[2].replace(".txt", ""))//版本号

					resultsFile.create()

				}
			}
			reader.close();
		//}
	//}
}
if(list.size()==0)
ec.message.addError("导入失败,找不到所需文件")
para.release()