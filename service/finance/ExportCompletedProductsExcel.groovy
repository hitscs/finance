
import org.apache.poi.ss.util.CellRangeAddress
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
if(completedProductList.size()>0) {
	def response = ec.web.response

	XSSFWorkbook wb = new XSSFWorkbook();

	XSSFSheet sheet = wb.createSheet();


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
		productMap.put(completedProduct.get("productId"), completedProduct.get("productId"))
		managementChannelMap.put(completedProduct.get("productId")+"_"+completedProduct.get("managementChannelId"), completedProduct.get("productId")+"_"+completedProduct.get("managementChannelId"))

	}
	println"++++++++++++++++++++++++++++++++++++ONE+++++++++++++++++++++++++++++++++++++++++++++++"
	List managementChannelList=new ArrayList()
	for(String key : managementChannelMap.keySet()) {
		String productId=key.split("_")[0]
		String managementChannelId=key.split("_")[1]
		List assetSideList=new ArrayList()
		for(EntityValue completedProduct:completedProductList) {
			if(productId.equals(completedProduct.get("productId"))&&managementChannelId.equals(completedProduct.get("managementChannelId"))) {
				assetSideList.add(completedProduct)
			}
		}
		managementChannelList.add(assetSideList)
	}
	println"++++++++++++++++++++++++++++++++++++TWO+++++++++++++++++++++++++++++++++++++++++++++++"
	List list=new ArrayList()
	List collectTotalAmountList=new ArrayList()
	for(String key : productMap.keySet()) {
		int collectTotalAmount=0
		List productList=new ArrayList()
		for(int i = 0; i < managementChannelList.size(); i++) {
			List assetSideList=managementChannelList.get(0)
			if(assetSideList.get(0).getAt("productId").equals(key)) {
				productList.add(managementChannelList)

			}
		}
		for(EntityValue completedProduct:completedProductList) {
			if(completedProduct.get("productId").equals(key)) {
				String amountInvestment=completedProduct.get("amountInvestment").toString()
				collectTotalAmount=collectTotalAmount+Integer.parseInt(amountInvestment)
			}

		}
		list.add(productList)
		collectTotalAmountList.add(collectTotalAmount);
	}
	println"++++++++++++++++++++++++++++++++++++THREE+++++++++++++++++++++++++++++++++++++++++++++++"
	int productFirstRow=1
	int productLastRow=0
	//开始合并单元格
	for(int i = 0; i < list.size(); i++) {
		List productList=list.get(i)
		for(int j = 0; j < productList.size(); j++) {
			List mList=productList.get(j)
			productLastRow=productFirstRow+mList.size()
			sheet.addMergedRegion(new CellRangeAddress(productFirstRow,productLastRow,11,11));
		}
		for(int m=0;m<11;m++) {
			sheet.addMergedRegion(new CellRangeAddress(productFirstRow,productLastRow,m,m));
		}
		productFirstRow=productLastRow+1
	}
	println"++++++++++++++++++++++++++++++++++++FOUR+++++++++++++++++++++++++++++++++++++++++++++++"
	//开始写入列表
	for(int i = 0; i < list.size(); i++) {
		List productList=list.get(i)
		for(int j = 0; j < productList.size(); j++) {
			List mList=productList.get(j)
			for(int k = 0; k < mList.size(); k++) {
				EntityValue completedProduct=mList.get(j)
				XSSFRow valueRow = sheet.createRow((i+1)*(j+1)*k+1);
				valueRow.createCell(0).setCellValue(completedProduct.get("pseudoId"));
				valueRow.createCell(1).setCellValue(completedProduct.get("productName"));
				valueRow.createCell(2).setCellValue(completedProduct.get("productType"));
				valueRow.createCell(3).setCellValue(completedProduct.get("publisher"));
				valueRow.createCell(4).setCellValue(completedProduct.get("riskRating"));
				valueRow.createCell(5).setCellValue(completedProduct.get("interestDate"));
				valueRow.createCell(6).setCellValue(completedProduct.get("maturityDate"));
				valueRow.createCell(7).setCellValue(completedProduct.get("trialDate"));
				valueRow.createCell(8).setCellValue(completedProduct.get("paymentDueDay"));
				valueRow.createCell(9).setCellValue(completedProduct.get("numberOfDays"));
				valueRow.createCell(10).setCellValue(collectTotalAmountList.get(i));
				valueRow.createCell(11).setCellValue(completedProduct.get("managementChannelName"));
				valueRow.createCell(12).setCellValue(completedProduct.get("assetSideName"));
				valueRow.createCell(13).setCellValue(completedProduct.get("amountInvestment"));
				valueRow.createCell(14).setCellValue(completedProduct.get("expectedRate"));
				valueRow.createCell(15).setCellValue(completedProduct.get("interestDate"));
				valueRow.createCell(16).setCellValue(completedProduct.get("dueDate"));
				valueRow.createCell(17).setCellValue(completedProduct.get("paymentDueDate"));
				valueRow.createCell(18).setCellValue(completedProduct.get("interestPeriod"));
				valueRow.createCell(19).setCellValue(completedProduct.get("isReturnMoney"));
				valueRow.createCell(20).setCellValue(completedProduct.get("remarks"));
			}
		}
	}
	String downFileName = "产品投资信息明细表.xlsx";
	// 清空response
	response.reset();
	response.setContentType("application/msexcel");//设置生成的文件类型
	response.setCharacterEncoding("UTF-8");//设置文件头编码方式和文件名
	response.setHeader("Content-Disposition", "attachment; filename=" + new String(downFileName.getBytes("utf-8"), "ISO8859-1"));
	OutputStream os=response.getOutputStream();
	wb.write(os);
	os.flush();
	os.close();
}