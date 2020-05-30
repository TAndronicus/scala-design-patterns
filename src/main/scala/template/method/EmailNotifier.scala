package template.method

object EmailNotifier extends NotificationService {

  override protected def isSubscribed(userId: Long): Boolean = isSubscribedToEmail(userId)

  override protected def sendNotification(userId: Long): Unit = sendEmail(userId)

  private def sendEmail(userId: Long): Unit = println(s"Email to user ${userId}")

  private def isSubscribedToEmail(userId: Long): Boolean = userId == 0
}
