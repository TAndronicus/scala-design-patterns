package template.method

object WebpageNotifier extends NotificationService {

  override protected def isSubscribed(userId: Long): Boolean = isSubscribedToSms(userId)

  override protected def sendNotification(userId: Long): Unit = sendSms(userId)

  private def sendSms(userId: Long): Unit = println(s"SMS to user ${userId}")

  private def isSubscribedToSms(userId: Long): Boolean = userId == 1
}
