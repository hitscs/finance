
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityFind
import org.moqui.entity.EntityValue


ExecutionContext ec = context.ec





EntityFind ef = ec.entity.find("finance.product.CompletedProduct")
if(productName)ef.condition("productName", EntityCondition.LIKE, "%${productType}%")
if(productType)ef.condition("productType", EntityCondition.LIKE, "%${productName}%")
if(pseudoId)ef.condition("pseudoId", EntityCondition.LIKE, "%${pseudoId}%")
if(managementChannelName)ef.condition("managementChannelName", EntityCondition.LIKE, "%${managementChannelName}%")
if(assetSideName)ef.condition("assetSideName", EntityCondition.LIKE, "%${assetSideName}%")
if(paymentDueDate)ef.condition("paymentDueDate", EntityCondition.LIKE, "%${paymentDueDate}%")

if(interestPeriod)ef.condition("interestPeriod", EntityCondition.LIKE, "%${interestPeriod}%")
if(isReturnMoney)ef.condition("isReturnMoney", EntityCondition.LIKE, "%${isReturnMoney}%")



def completedProductList=ef.list()

def response = ec.web.response
String filename = "产品投资信息明细表.csv";
response.setContentType("application/csv;charset=UTF-8");
//response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
response.setHeader("content-disposition", "attachment;filename=" + filename);
response.setCharacterEncoding("UTF-8");
def array = [(byte)0xef, (byte)0xbb, (byte)0xbf] as byte[]

int len = 0;
OutputStream out = response.getOutputStream();
out.write(array);


def header="产品编码,产品名称,产品类型,发行方,产品风险等级,起息日,到期日,试算日,到期还款日,计息周期,资管通道,投向资产方,投资金额,预期收益率,起息日,到期日,到期还款日,计息周期,是否回款,备注\r\n"
out.write(header.getBytes("UTF-8"))
def value=""
for(EntityValue completedProduct:completedProductList){
	
	value=value+completedProduct.get("pseudoId")+"\t"+","
	value=value+completedProduct.get("productName")+"\t"+","
	value=value+completedProduct.get("productType")+","
	value=value+completedProduct.get("publisher")+","
	value=value+completedProduct.get("riskRating")+","
	value=value+completedProduct.get("interestDate")+"\t"+","
	value=value+completedProduct.get("maturityDate")+"\t"+","
	value=value+completedProduct.get("trialDate")+"\t"+","
	value=value+completedProduct.get("paymentDueDay")+"\t"+","
	value=value+completedProduct.get("numberOfDays")+","
	/*value=value+completedProduct.get("totleAmountInvestment")+","  //募集总金额
	 */		value=value+completedProduct.get("managementChannelName")+","
	value=value+completedProduct.get("assetSideName")+","
	value=value+completedProduct.get("amountInvestment")+","
	value=value+completedProduct.get("expectedRate")+","
	value=value+completedProduct.get("interestDate")+"\t"+","
	value=value+completedProduct.get("dueDate")+"\t"+","
	value=value+completedProduct.get("paymentDueDate")+"\t"+","
	value=value+completedProduct.get("interestPeriod")+","
	value=value+completedProduct.get("isReturnMoney")+","
	value=value+completedProduct.get("remarks")+"\r\n"

	
}
out.write(value.getBytes("UTF-8"))

out.flush()
out.close()







XSSFWorkbook wb = new XSSFWorkbook();  
          
XSSFSheet sheet = wb.createSheet();  
//这个就是合并单元格  
//参数说明：1：开始行 2：结束行  3：开始列 4：结束列  
//比如我要合并 第二行到第四行的    第六列到第八列     sheet.addMergedRegion(new CellRangeAddress(1,3,5,7));  
//sheet.addMergedRegion(new CellRangeAddress(0,0,0,1));  
//  
//XSSFRow row = sheet.createRow(number);
//
//
//
//
//sheet.addMergedRegion(new CellRangeAddress(0,3,0,0));
//sheet.addMergedRegion(new CellRangeAddress(0,3,3,3));
//sheet.addMergedRegion(new CellRangeAddress(0,3,4,4));
  
//第一行数据
XSSFRow row = sheet.createRow(0);
row.createCell(0).setCellValue("产品编码");
row.createCell(1).setCellValue("产品名称");
row.createCell(2).setCellValue("产品类型");
row.createCell(3).setCellValue("发行方");
row.createCell(4).setCellValue("产品风险等级");
row.createCell(5).setCellValue("起息日");
row.createCell(6).setCellValue("到期日");
row.createCell(7).setCellValue("试算日");
row.createCell(8).setCellValue("到期还款日");
row.createCell(9).setCellValue("计息周期");
row.createCell(10).setCellValue("募集总金额");


row.createCell(11).setCellValue("资管通道");
row.createCell(12).setCellValue("投向资产方");
row.createCell(13).setCellValue("投资金额");
row.createCell(14).setCellValue("预期收益率");
row.createCell(15).setCellValue("起息日");
row.createCell(16).setCellValue("到期日");

row.createCell(17).setCellValue("到期还款日");
row.createCell(18).setCellValue("计息周期");
row.createCell(19).setCellValue("是否回款");
row.createCell(20).setCellValue("备注");



Map productMap=new HashMap()
Map managementChannelMap=new HashMap()
for(EntityValue completedProduct:completedProductList){
	productMap.put(completedProduct.get("pseudoId"), completedProduct.get("pseudoId"))
	managementChannelMap.put(completedProduct.get("pseudoId")+"_"+completedProduct.get("managementChannelId"), completedProduct.get("pseudoId")+"_"+completedProduct.get("managementChannelId"))
	
}
for(String key : managementChannelMap.keySet()) {
	
	
	
	
	for() {
		
	}
	
	
	
	
	
}







  
//第二行数据
XSSFRow row = sheet.createRow(number);
//row.createCell(0).setCellValue("工作站");//因为和上面的行合并了，所以不用再次 赋值了
row.createCell(1).setCellValue("位置");
row.createCell(2).setCellValue("序号");
//row.createCell(3).setCellValue("订单号");//因为和上面的行合并了，所以不用再次 赋值了
//row.createCell(4).setCellValue("成品号/型号");//因为和上面的行合并了，所以不用再次 赋值了











