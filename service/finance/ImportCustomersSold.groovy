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
String str = "客户明细销售表";

List list=SftpUtil.downloadFilesAsInputStreamByName(para,str)

LineNumberReader reader ;
for(Map map:list){
	String fileName=map.get("fileName");
	//if(fileName.contains(str)){
	EntityList customersSoldList = ec.entity.find("finance.product.CustomersSold").condition("instId", instId).condition("importDate", importDate).condition("fileName", fileName).list()
	if(customersSoldList.size() >0){
		EntityValue customers=customersSoldList.getAt(0)
		//println "-----------------------------------"+customers.projectCode+"------------------------------------"
		ec.entity.find("finance.product.CustomersSold").condition("instId", instId).condition("fileName", fileName).deleteAll()
		ec.entity.find("finance.product.RegisterResultsFile").condition("projectCode", customers.projectCode).condition("versionNo", customers.versionNo).deleteAll()
		println "---------------------------删除文件名为--$fileName--的记录------------------------------------"

	}
	//if(customersSoldList.size() == 0){
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
			//println "--------------------------------------------------------------------证件地址:"+ss[9]
			customersSold.put("telephone",ss[11])//电话
			customersSold.put("postalcode",ss[12])//邮政编码
			customersSold.put("contactAddress",ss[13])//联系地址
			customersSold.put("riskRating",ss[14])//风险承受级别
			//println "--------------------------------------------------------------------风险承受级别:"+ss[14]
			customersSold.put("orderId",ss[15])//订单标识
			customersSold.put("daysOfYear",ss[17])//计息年化天数
			customersSold.put("yieldRate",ss[16])//年化收益率
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

			//生成登记结果文件
			EntityValue registerResultsFile =ec.entity.makeValue("finance.product.RegisterResultsFile").setSequencedIdPrimary()

			registerResultsFile.put("transactionId",ss[0])//交易编号
			registerResultsFile.put("productCode", fileNameSplit[0])

			registerResultsFile.put("projectCode",fileNameSplit[0])//与互联网合作项目的编号
			//println "--------------------------------------------------------------------产品代码:"+fileNameSplit[0]
			registerResultsFile.put("versionNo",fileNameSplit[2].replace(".txt", ""))//版本号
			registerResultsFile.create()

		}
	}
	reader.close();
	//导入销售文件后，产品状态变更为已完成交易
	//println("+++++++++++++++++++++++++++++++${fileNameSplit[0]}++++++++++++++++++++++++++++++++++++++++++++++++++++")
	EntityValue releasedProduct=ec.entity.find("finance.product.ReleasedProduct").condition("pseudoId", fileNameSplit[0]).condition("versionNo", fileNameSplit[2].replace(".txt", "")).one()
	releasedProduct.set("status", 1)
	releasedProduct.update()
}
if(list.size()==0)
	ec.message.addError("导入失败,找不到所需文件")
para.release()