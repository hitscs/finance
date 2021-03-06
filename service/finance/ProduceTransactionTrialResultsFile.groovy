import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue

import com.finance.utils.DateUtils
import com.finance.utils.SFTPParameter


//SFTPParameter para = new SFTPParameter();
//
//para.hostName = "127.0.0.1";
//para.userName = "sunmingjun";
//para.passWord = "sunmingjun";
//para.port = 22;

ExecutionContext ec = context.ec
def uploadDate=DateUtils.getNowTime("yyyyMMdd")

def instId="hezuo"

def treatmentResultAll="0"//
BigDecimal sum=0
String fileString="";

EntityValue fileMeta
if(trialFileMeta.size()==1){
	fileMeta=trialFileMeta.get(0)
}
ec.entity.find("finance.product.TransactionTrialResultsFileMeta").condition("projectCode", projectCode).condition("versionNo", versionNo).deleteAll()
ec.entity.find("finance.product.TransactionTrialResultsFile").condition("projectCode", projectCode).condition("versionNo", versionNo).deleteAll()

EntityValue releasedProduct = ec.entity.find("finance.product.ReleasedProduct").condition("projectCode", projectCode).one()
println customersSoldList.size()
for(EntityValue customersSold:customersSoldList){

	EntityValue transactionTrialResultsFile =ec.entity.makeValue("finance.product.TransactionTrialResultsFile").setSequencedIdPrimary()

    //double repaymentInterest=Double.parseDouble(customersSold.get("assetShare").toString())*Double.parseDouble(releasedProduct.get("fixedYieldRate").toString())/100/Double.parseDouble(customersSold.get("daysOfYear").toString())*Double.parseDouble(releasedProduct.get("numberOfDays").toString())
	//double totalRepaymentAmount=Double.parseDouble(customersSold.get("assetShare").toString())+Double.parseDouble(customersSold.get("assetShare").toString())*Double.parseDouble(releasedProduct.get("fixedYieldRate").toString())/100/Double.parseDouble(customersSold.get("daysOfYear").toString())*Double.parseDouble(releasedProduct.get("numberOfDays").toString())
	BigDecimal assetShare=new BigDecimal(customersSold.get("assetShare").toString())
	BigDecimal fixedYieldRate=new BigDecimal(releasedProduct.get("fixedYieldRate").toString())
	BigDecimal daysOfYear=new BigDecimal(customersSold.get("daysOfYear").toString())
	BigDecimal numberOfDays=new BigDecimal(releasedProduct.get("numberOfDays").toString())
	
	BigDecimal repaymentInterest=assetShare.multiply(fixedYieldRate).multiply(numberOfDays).divide(new BigDecimal("100")).divide(daysOfYear,2,BigDecimal.ROUND_DOWN)
	
	BigDecimal totalRepaymentAmount=assetShare.add(repaymentInterest)

		//repaymentInterest =Math.floor(repaymentInterest*100)/100;
	//totalRepaymentAmount =Math.floor(totalRepaymentAmount*100)/100;
	
	
	
	//repaymentInterest.setScale(2,BigDecimal.ROUND_DOWN)
	//totalRepaymentAmount.setScale(2,BigDecimal.ROUND_DOWN)
	transactionTrialResultsFile.put("kaitongOrderNumber", "")
	transactionTrialResultsFile.put("orgOrderNumber", customersSold.get("transactionId"))
	transactionTrialResultsFile.put("productCode", projectCode)
	transactionTrialResultsFile.put("customerName", customersSold.get("username"))
	transactionTrialResultsFile.put("passportType", customersSold.get("certificateType"))
	transactionTrialResultsFile.put("passportNo", customersSold.get("certificateNumber"))
	transactionTrialResultsFile.put("institutionId", "")
	transactionTrialResultsFile.put("repaymentPrincipal", customersSold.get("assetShare"))
	transactionTrialResultsFile.put("repaymentInterest", repaymentInterest.setScale(2,BigDecimal.ROUND_DOWN))
	transactionTrialResultsFile.put("totalRepaymentAmount", totalRepaymentAmount.setScale(2,BigDecimal.ROUND_DOWN))
	transactionTrialResultsFile.put("paymentDueDay", releasedProduct.get("paymentDueDay"))

println("----------totalRepaymentAmount:"+totalRepaymentAmount.setScale(2,BigDecimal.ROUND_DOWN)+"--------------")
	sum=sum.add(totalRepaymentAmount.setScale(2,BigDecimal.ROUND_DOWN))
	//sum=sum+totalRepaymentAmount
	println("----------sum:"+sum+"--------------")
	def treatmentResult="0"
//	for(EntityValue file:trialFileList){
//		if(file.get("orgOrderNumber").equals(customersSold.get("transactionId"))){
//			if(!transactionTrialResultsFile.get("repaymentPrincipal").equals(file.get("repaymentPrincipal"))||
//			   !transactionTrialResultsFile.get("repaymentInterest").equals(file.get("repaymentInterest"))||
//			   !transactionTrialResultsFile.get("totalRepaymentAmount").equals(file.get("totalRepaymentAmount"))||
//			   !transactionTrialResultsFile.get("paymentDueDay").equals(file.get("paymentDueDay"))||
//			   !transactionTrialResultsFile.get("passportNo").equals(file.get("passportNo"))||
//			   !transactionTrialResultsFile.get("passportType").equals(file.get("passportType"))||
//			   !transactionTrialResultsFile.get("customerName").equals(file.get("customerName"))){
//			   treatmentResult="1"
//			   treatmentResultAll="1"
//			}
//		}
//	}
	transactionTrialResultsFile.put("treatmentResult", treatmentResult)
	//transactionTrialResultsFile.put("isRedeem", 0)//不确定
	transactionTrialResultsFile.put("projectCode", projectCode)
	transactionTrialResultsFile.put("versionNo",versionNo)
	transactionTrialResultsFile.create()

}

EntityValue transactionTrialResultsFileMeta =ec.entity.makeValue("finance.product.TransactionTrialResultsFileMeta").setSequencedIdPrimary()


transactionTrialResultsFileMeta.put("productCode",projectCode)//产品代码
transactionTrialResultsFileMeta.put("totalRoll",customersSoldList.size())//总笔数
//println("--------------------------"+sum+"-----------------------------------------")
sum=sum.setScale(2,BigDecimal.ROUND_DOWN);
transactionTrialResultsFileMeta.put("totalMoney",sum)//总金额
//if(!transactionTrialResultsFileMeta.get("totalRoll").equals(fileMeta.get("totalRoll"))||
//   !transactionTrialResultsFileMeta.get("totalMoney").equals(fileMeta.get("totalMoney"))){
//	treatmentResultAll="1"
//}


transactionTrialResultsFileMeta.put("treatmentResult",treatmentResultAll)//
transactionTrialResultsFileMeta.put("instId",instId)//互联网平台Id
transactionTrialResultsFileMeta.put("projectCode",projectCode)//与互联网合作项目的编号
transactionTrialResultsFileMeta.put("versionNo",versionNo)//版本号
transactionTrialResultsFileMeta.create()

