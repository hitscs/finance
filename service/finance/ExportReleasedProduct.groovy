import com.finance.utils.SFTPParameter
import com.finance.utils.SftpUtil
import org.moqui.context.ExecutionContext
import com.finance.utils.DateUtils


SFTPParameter para = new SFTPParameter();

para.hostName = "127.0.0.1";
para.userName = "sunmingjun";
para.passWord = "sunmingjun";
para.port = 22;

ExecutionContext ec = context.ec
def interestDate=ec.l10n.format(product.interestDate, "yyyyMMdd")
def maturityDate=ec.l10n.format(product.maturityDate, "yyyyMMdd")
def startTime=ec.l10n.format(product.startTime, "yyyyMMdd HH:mm:ss")
def endTime=ec.l10n.format(product.endTime, "yyyyMMdd HH:mm:ss")
println "------------------+++++++++++++++++++-----------产品发行时间："+product.startTime
def uploadDate=DateUtils.getNowTime("yyyyMMdd")

def instId="hezuo"

def riskRating=product.riskRating
if(null==product.riskRating){
	riskRating=""
}
def remark=product.remark
if(null==product.remark){
	remark=""
}

header="产品提供方|产品名称|产品类型|发行方|发行方营业执照|发行方开户银行|所属省|所属市|发行方开户分支行|发行方银行账号|"
header=header+"产品风险说明|产品收益说明|产品描述|是否需要风险测评|产品风险等级|产品期限（天）|年化天数|起息日|到期日|收益类型|固定年化收益率|"
header=header+"浮动年化收益率|募集总金额|子包最低募集成功金额|起售金额|单位加价金额|上架时间|下架时间|备注|打款银行账户名称|大包产品名称|交易所代码|产品系列编号"
productString="\n"+product.provider+"|"+product.productName+"|"+product.productType+"|"+product.publisher+"|"+product.publisherTradingCertificate
productString=productString+"|"+product.publisherBank+"|"+provence.geoNameLocal+"|"+city.geoNameLocal+"|"+product.publisherBranchBank+"|"+product.publisherBankAccount+"|"+product.riskInstruction
productString=productString+"|"+product.earningInstruction+"|"+product.description+"|"+product.riskEvaluation+"|"+riskRating+"|"+product.numberOfDays+"|"+product.daysOfYear
productString=productString+"|"+interestDate+"|"+maturityDate+"|"+product.revenueType+"|"+product.fixedYieldRate+"|"+product.floatingYieldRate+"|"+product.totalAmountToRaise
productString=productString+"|"+product.minAmountToRaise+"|"+product.minimumMoney+"|"+product.perMoney+"|"+startTime+"|"+endTime+"|"+remark+"|"+product.payBankAccount+"|"+product.productCategoryName+"|"+product.exchangeCode+"|"+product.pseudoId
content=header+productString


String makeDir = "/home/"+instId+"/download/"+uploadDate+"/"
SftpUtil.makeDir(para, makeDir)

para.uploadPath =makeDir

InputStream instream = new ByteArrayInputStream(content.getBytes("UTF-8"));

fileName=product.projectCode.trim()+"_产品发布文件_"+product.versionNo.trim()+".txt"
SftpUtil.uploadFileInputStream(para, instream,fileName)
para.release()