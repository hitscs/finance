import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityList
import org.moqui.entity.EntityValue

import com.finance.utils.DateUtils


ExecutionContext ec = context.ec
def emailMessageId="REMIND"
def nowDate=DateUtils.getNowTime("yyyyMMdd")
Date date=new Date()
Calendar calender = Calendar.getInstance();
calender.setTime(date);
calender.add(Calendar.DATE, 1);
def trialDate=DateUtils.addDay(date, "yyyyMMdd", 1)
EntityList tomorrowDueProductList = ec.entity.find("finance.product.CompletedProduct").condition("dueDate", calender.getTime()).list()
EntityList todayDueProductList = ec.entity.find("finance.product.CompletedProduct").condition("dueDate", date).list()
if(tomorrowDueProductList.size()>0||todayDueProductList.size()>0) {
	String bodyText ="今日到期投资产品：\r\n"
	for(EntityValue todayDueProduct:todayDueProductList) {
		bodyText=bodyText+todayDueProduct.get("pseudoId")+"    "+todayDueProduct.get("productName")+"    "+todayDueProduct.get("managementChannelName")+"    "+todayDueProduct.get("assetSideName")+"    "+todayDueProduct.get("dueDate")+"，请及时处理 \r\n"
	}

	bodyText=bodyText+"明日到期投资产品：\r\n"
	for(EntityValue tomorrowDueProduct:tomorrowDueProductList) {
		bodyText=bodyText+tomorrowDueProduct.get("pseudoId")+"    "+tomorrowDueProduct.get("productName")+"    "+tomorrowDueProduct.get("managementChannelName")+"    "+tomorrowDueProduct.get("assetSideName")+"    "+tomorrowDueProduct.get("dueDate")+"，请及时处理 \r\n"
	}
	EntityValue emailMessage = ec.entity.find("moqui.basic.email.EmailMessage").condition("emailMessageId", emailMessageId).one()
	if (emailMessage == null) {
		ec.message.addError(ec.resource.expand('No EmailMessage record found for ID ${emailMessageId}','')); return
	}
	String subject=nowDate+"投资产品到期提醒"
	String bodyHtml = emailMessage.body
	//String bodyText = emailMessage.bodyText
	String fromAddress = emailMessage.fromAddress
	String toAddresses = emailMessage.toAddresses
	String ccAddresses = emailMessage.ccAddresses
	String bccAddresses = emailMessage.bccAddresses

	if (!bodyHtml && !bodyText) ec.message.addError(ec.resource.expand('Email Message ${emailMessageId} has no body',''))
	if (!fromAddress) ec.message.addError(ec.resource.expand('Email Message ${emailMessageId} has no from address',''))
	if (!toAddresses) ec.message.addError(ec.resource.expand('Email Message ${emailMessageId} has no to address',''))
	if (ec.message.hasError()) return

		EntityValue emailTemplate = (EntityValue) emailMessage.template

	EntityValue emailServer = (EntityValue) emailMessage.server
	if (emailServer == null) { ec.message.addError(ec.resource.expand('No Email Server record found for Email Message ${emailMessageId}','')); return }


	String host = emailServer.smtpHost
	int port = (emailServer.smtpPort ?: "25") as int

	HtmlEmail email = new HtmlEmail()
	email.setCharset("utf-8")
	email.setHostName(host)
	email.setSmtpPort(port)
	if (emailServer.mailUsername) {
		email.setAuthenticator(new DefaultAuthenticator((String) emailServer.mailUsername, (String) emailServer.mailPassword))
	}
	if (emailServer.smtpStartTls == "Y") {
		email.setStartTLSEnabled(true)
		// email.setStartTLSRequired(true)
	}
	if (emailServer.smtpSsl == "Y") {
		email.setSSLOnConnect(true)
		email.setSslSmtpPort(port as String)
		// email.setSSLCheckServerIdentity(true)
	}

	// set the subject
	email.setSubject(subject)

	// set from, reply to, bounce addresses
	email.setFrom(fromAddress, (String) emailMessage.fromName)
	if (emailTemplate?.replyToAddresses) {
		def rtList = ((String) emailTemplate.replyToAddresses).split(",")
		for (address in rtList) email.addReplyTo(address.trim())
	}
	if (emailTemplate?.bounceAddress) email.setBounceAddress((String) emailTemplate.bounceAddress)

	// set to, cc, bcc addresses
	def toList = ((String) toAddresses).split(",")
	for (toAddress in toList) email.addTo(toAddress.trim())
	if (ccAddresses) {
		def ccList = ((String) ccAddresses).split(",")
		for (ccAddress in ccList) email.addCc(ccAddress.trim())
	}
	if (bccAddresses) {
		def bccList = ((String) bccAddresses).split(",")
		for (def bccAddress in bccList) email.addBcc(bccAddress.trim())
	}

	// set the html message
	if (bodyHtml) email.setHtmlMsg(bodyHtml)
	// set the alternative plain text message
	if (bodyText) email.setTextMsg(bodyText)

	messageId = email.send()
}