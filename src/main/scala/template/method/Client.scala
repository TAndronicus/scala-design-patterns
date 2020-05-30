package template.method

object Client {

  def main(args: Array[String]): Unit = {
    EmailNotifier.notifyUser(1)
    WebpageNotifier.notifyUser(1)
  }

}
