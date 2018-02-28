import org.moqui.context.ExecutionContext
import com.finance.utils.DateUtils
import org.moqui.entity.EntityValue
import org.moqui.entity.EntityList
import org.apache.commons.mail.HtmlEmail


ExecutionContext ec = context.ec
def nowDate=DateUtils.getNowTime("yyyyMMdd")
Date date=new Date()
def trialDate=DateUtils.addDay(date, "yyyyMMdd", 1)
EntityList releasedProductList = ec.entity.find("finance.product.ReleasedProduct").condition("trialDate", trialDate).list()




	EntityValue emailMessage = ec.entity.find("moqui.basic.email.EmailMessage").condition("emailMessageId", emailMessageId).one()
	if (emailMessage == null) { ec.message.addError(ec.resource.expand('No EmailMessage record found for ID ${emailMessageId}','')); return }
	String statusId = emailMessage.statusId
	if (statusId == 'ES_DRAFT') ec.message.addError(ec.resource.expand('Email Message ${emailMessageId} is in Draft status',''))
	if (statusId == 'ES_CANCELLED') ec.message.addError(ec.resource.expand('Email Message ${emailMessageId} is Cancelled',''))

	String bodyHtml = emailMessage.body
	String bodyText = emailMessage.bodyText
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
	if (!emailServer.smtpHost) {
		logger.warn("SMTP Host is empty for EmailServer ${emailServer.emailServerId}, not sending email message ${emailMessageId}")
		// logger.warn("SMTP Host is empty for EmailServer ${emailServer.emailServerId}, not sending email:\nbodyHtml:\n${bodyHtml}\nbodyText:\n${bodyText}")
		return
	}

	String host = emailServer.smtpHost
	int port = (emailServer.smtpPort ?: "25") as int

	HtmlEmail email = new HtmlEmail()
	email.setCharset("utf-8")
	email.setHostName(host)
	email.setSmtpPort(port)
	if (emailServer.mailUsername) {
		email.setAuthenticator(new DefaultAuthenticator((String) emailServer.mailUsername, (String) emailServer.mailPassword))
		// logger.info("Set user=${emailServer.mailUsername}, password=${emailServer.mailPassword}")
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
	if (emailMessage.subject) email.setSubject((String) emailMessage.subject)

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
