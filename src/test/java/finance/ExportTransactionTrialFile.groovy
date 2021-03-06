package finance

import static org.junit.Assert.*

import javax.swing.text.html.CSS.StringValue

import org.junit.Test
import org.moqui.entity.EntityValue

import com.finance.utils.DateUtils
import com.finance.utils.SFTPParameter
import com.finance.utils.SftpUtil

class ExportTransactionTrialFile {
	@Test
	public void testUploadFileInputStream() {
		                SFTPParameter para = new SFTPParameter();
		
						para.hostName = "127.0.0.1";
						para.userName = "sunmingjun";
						para.passWord = "sunmingjun";
						para.port = 22;

						def uploadDate=DateUtils.getNowTime("yyyyMMdd")
						
						def instId="hezuo"
						

						Float sum=0.00
						String fileString="";
						for(EntityValue file:fileList){
							sum=sum+file.totalRepaymentAmount
							def kaitongOrderNumber=file.kaitongOrderNumber
							if(null==file.kaitongOrderNumber){
							kaitongOrderNumber=""
							}
							def orgOrderNumber=file.orgOrderNumber
							if(null==file.orgOrderNumber){
							orgOrderNumber=""
							}
							fileString=fileString+"|"+kaitongOrderNumber+"|"+orgOrderNumber+"|"+file.productCode+"|"+file.customerName+"|"+file.passportType+"|"+file.passportNo+"|"+file.institutionId+"|"+file.repaymentPrincipal+"|"+file.repaymentInterest+"|"+file.totalRepaymentAmount+"|"+file.paymentDueDay.replace("-", "")+"|\r\n"
						}
						lineOne="版本号:"+file.versionNo+"|总笔数:"+fileList.size()+"|总金额:"+sum+"|\r\n"
						header="外贸信托订单号|机构订单号|产品代码|客户姓名|客户证件类别|客户证件编号|机构客户ID|还款本金|还款利息|还款总金额|到期还款日|\r\n"
						
						content=lineOne+header+fileString
		
						String makeDir = "/home/"+instId+"/download/"+uploadDate+"/"
						SftpUtil.makeDir(para, makeDir)
						
						para.uploadPath =makeDir
						
						InputStream instream = new ByteArrayInputStream(content.getBytes("UTF-8"));
		
						fileName=projectCode+"_交易试算文件_"+versionNo+".txt"
						SftpUtil.uploadFileInputStream(para, instream,fileName)
	}
	
	@Test
	public void test(){
	double d = 3.1495926;
	d =Math.floor(d*100)/100;
	BigDecimal sum=0
	sum=sum.add(new BigDecimal(String.valueOf(d)))
	sum=sum.setScale(2,BigDecimal.ROUND_DOWN);
	println d
	println sum
	
	}
}
