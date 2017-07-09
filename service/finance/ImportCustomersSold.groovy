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

def importDate=DateUtils.getNowTime("yyyyMMdd")

def instId="hezuo"

para.downloadPath = "/home/"+instId+"/upload/"+importDate+"/";

String str = "客户明细销售表";


List list=SftpUtil.downloadFilesAsInputStream(para)

LineNumberReader reader ;
for(Map map:list){
	String fileName=map.get("fileName");
	if(fileName.contains(str)){
		EntityList customersSoldList = ec.entity.find("finance.product.CustomersSold").condition("instId", instId).condition("importDate", importDate).condition("fileName", fileName).list()
		if(customersSoldList.size() >0)println "---------------------------已经成功导入过，不能再次导入------------------------------------"
		if(customersSoldList.size() == 0){
			String[] fileNameSplit=fileName.split("_")
			InputStream instream =map.get("fileContent");

			reader=new LineNumberReader(new InputStreamReader(instream ,"UTF-8"));
			String s = "";

			while ((s = reader.readLine()) != null) {
				if(reader.getLineNumber()>1){
					String[] ss=s.split('\\|');
					//println ss
					EntityValue customersSold =ec.entity.makeValue("finance.product.CustomersSold").setSequencedIdPrimary()

					//customersSold.put("customerDetailId",ss[0])
					customersSold.put("transactionId",ss[0])//交易编号
					customersSold.put("transactionTime",ss[1])//交易时间戳
					customersSold.put("assetShare",ss[2])//资产份额
					customersSold.put("customerType",ss[3])//机构标志
					//println "--------------------------------------------------------------------机构标志:"+ss[3]
					customersSold.put("certificateType",ss[4])//证件类别
					customersSold.put("username",ss[5])//客户姓名
					customersSold.put("userFullName",ss[6])//客户全称
					customersSold.put("certificateNumber",ss[7])//证件编号
					customersSold.put("cellPhone",ss[8])//手机
					customersSold.put("sex",ss[9])//性别
					customersSold.put("certificateAddress",ss[10])//证件地址
					//println "--------------------------------------------------------------------证件地址:"+ss[10]
					customersSold.put("telephone",ss[11])//电话
					customersSold.put("postalcode",ss[12])//邮政编码
					customersSold.put("contactAddress",ss[13])//联系地址
					customersSold.put("riskRating",ss[14])//风险承受级别
					//println "--------------------------------------------------------------------风险承受级别:"+ss[14]
					customersSold.put("orderId",ss[15])//订单标识
					customersSold.put("daysOfYear",ss[16])//计息年化天数
					customersSold.put("yieldRate",ss[17])//年化收益率
					//println "--------------------------------------------------------------------年化收益率:"+ss[17]
					//println "--------------------------------------------------------------------导入文件名称:"+fileName
					//println "--------------------------------------------------------------------互联网平台Id:"+instId
					//println "--------------------------------------------------------------------导入时间（文件夹名）:"+importDate
					//println "--------------------------------------------------------------------产品代码:"+fileNameSplit[0]
					//println "--------------------------------------------------------------------版本号:"+fileNameSplit[2].replace(".txt", "")
					customersSold.put("fileName",fileName)//导入文件名称
					customersSold.put("instId",instId)//互联网平台Id
					customersSold.put("importDate",importDate)//导入时间（文件夹名）
					//customersSold.put("productCode",fileNameSplit[0])//产品代码
					customersSold.put("projectCode",fileNameSplit[0])//与互联网合作项目的编号
					//println "--------------------------------------------------------------------产品代码:"+fileNameSplit[0]
					customersSold.put("versionNo",fileNameSplit[2].replace(".txt", ""))//版本号

					customersSold.create()
					//同时生成交易试算文件
					EntityValue transactionTrialFile =ec.entity.makeValue("finance.product.TransactionTrialFile").setSequencedIdPrimary()
					
					EntityValue releasedProduct = ec.entity.find("finance.product.ReleasedProduct").condition("projectCode", fileNameSplit[0]).one()
					
					transactionTrialFile.put("kaitongOrderNumber", "")
					transactionTrialFile.put("orgOrderNumber", "")
					transactionTrialFile.put("productCode", fileNameSplit[0])
					transactionTrialFile.put("customerName", ss[5])
					transactionTrialFile.put("passportType", ss[4])
					transactionTrialFile.put("passportNo", ss[7])
					transactionTrialFile.put("institutionId", "")
					transactionTrialFile.put("repaymentPrincipal", ss[2])
					transactionTrialFile.put("repaymentInterest", Double.parseDouble(ss[2])*Double.parseDouble(ss[17])/365*Double.parseDouble(ss[16]))
					transactionTrialFile.put("totalRepaymentAmount", Double.parseDouble(ss[2])+Double.parseDouble(ss[2])*Double.parseDouble(ss[17])/365*Double.parseDouble(ss[16]))
					transactionTrialFile.put("paymentDueDay", releasedProduct.get("paymentDueDay"))
					transactionTrialFile.put("isRedeem", 0)//不确定
					transactionTrialFile.put("projectCode", fileNameSplit[0])
					transactionTrialFile.put("versionNo",fileNameSplit[2].replace(".txt", ""))
					transactionTrialFile.create()
				}
			}
			reader.close();
		}
	}
}