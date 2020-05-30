package template.method

trait NotificationService {

  protected def isSubscribed(userId: Long): Boolean

  protected def sendNotification(userId: Long): Unit

  def notifyUser(userId: Long): Unit = {
    if (isSubscribed(userId)) sendNotification(userId)
    else println("User not subscribed")
  }
}
