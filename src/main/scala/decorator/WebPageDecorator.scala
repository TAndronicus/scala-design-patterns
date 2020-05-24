package decorator

abstract class WebPageDecorator(webPage: WebPage) extends WebPage {
  override def render(): Unit = webPage.render()
}

class AuthorizedWebPage(webPage: WebPage) extends WebPageDecorator(webPage) {
  override def render(): Unit = {
    super.render()
    println("Authorizing...")
  }
}

class AuthenticatedWebPage(webPage: WebPage) extends WebPageDecorator(webPage) {
  override def render(): Unit = {
    super.render()
    println("Authenticating...")
  }
}
