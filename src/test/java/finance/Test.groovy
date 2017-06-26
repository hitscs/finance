package finance

import com.finance.utils.LoadProperties
import com.finance.utils.SFTPParameter
import com.finance.utils.SftpUtil

SFTPParameter para = new SFTPParameter();
	
	para.hostName = LoadProperties.getProperties().getProperty("ip");
	para.userName = LoadProperties.getProperties().getProperty("user");
	para.passWord = LoadProperties.getProperties().getProperty("pwd");
	para.port = LoadProperties.getProperties().getProperty("port");
	//para.downloadPath = LoadProperties.getProperties().getProperty("ip");
	//para.uploadPath = LoadProperties.getProperties().getProperty("ip");
	 header="产品提供方|产品名称|产品类型|发行方|发行方营业执照|发行方开户银行|所属省|所属市|发行方开户分支行|发行方银行账号|产品风险说明|产品收益说明|产品描述|是否需要风险测评|产品风险等级|产品期限（天）|年化天数|起息日|到期日|收益类型|固定年化收益率|浮动年化收益率|募集总金额|子包最低募集成功金额|起售金额|单位加价金额|上架时间|下架时间|备注|打款银行账户名称|大包产品名称|交易所代码|产品系列编号"
	 
	 //productString=product.
	// content=header+""
	 
	 
	 
	String makeDir = "/home/cfiusr/D/E/F/G/H/J/kk"
			SftpUtil.makeDir(para, makeDir)